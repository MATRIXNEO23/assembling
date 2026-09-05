package matrix.assembling

/** Integration orchestrator for Matrix/Luna components. */
class MatrixAssemblingOrchestrator(
    private val nlu: NluPort,
    private val understanding: UnderstandingPort,
    private val coherence: CoherenceGuardPort,
    private val authority: AuthorityResolverPort,
    private val memory: MemoryPreflightPort,
    private val affective: AffectivePort,
    private val promptBuilder: SemanticFrameToPromptPort,
    private val gguf: GgufPort,
    private val outputValidator: OutputValidatorPort? = null,
) {
    fun handle(input: UserMessage, turnId: String, sessionId: String): AssistantReply {
        val diagnostics = DiagnosticTrace(inputOriginale = input.text)
            .record(
                DiagnosticStage.INPUT,
                DiagnosticSnapshot(
                    module = "INPUT",
                    status = DiagnosticStatus.PASS,
                    input = mapOf(
                        "speakerId" to input.speakerId,
                        "observerId" to input.observerId,
                        "locale" to input.locale,
                    ),
                    output = mapOf("textLength" to input.text.length.toString()),
                    decision = "ACCEPT",
                    reasonCodes = listOf("INPUT_ACCEPTED"),
                ),
            )
            .add("turn.created")
        val completed = handle(
            MatrixTurnFrame(
                turnId = turnId,
                sessionId = sessionId,
                input = input,
                diagnostics = diagnostics,
            ),
        )
        return completed.reply ?: error("MatrixTurnFrame completed without assistant reply")
    }

    fun handle(turn: MatrixTurnFrame): MatrixTurnFrame {
        val initialized = if (turn.diagnostics.inputOriginale == null) {
            turn.copy(diagnostics = turn.diagnostics.copy(inputOriginale = turn.input.text))
        } else {
            turn
        }

        val analyzed = runStage("NLU", DiagnosticStage.OBSERVATION, initialized, nlu::analyze)
        val understood = runStage("UNDERSTANDING", DiagnosticStage.UNDERSTANDING, analyzed, understanding::understand)
        val coherent = runStage("COHERENCE", DiagnosticStage.COHERENCE, understood, coherence::check)
        val resolved = runStage("AUTHORITY", DiagnosticStage.AUTHORITY, coherent, authority::resolve)
        val preflight = runStage("MEMORY_PREFLIGHT", DiagnosticStage.MEMORY_ADMISSION, resolved, memory::evaluate)
        val safePreflight = requireNonPersistentPreflight(preflight)
        val appraised = runStage("AFFECTIVE", DiagnosticStage.AFFECTIVE, safePreflight, affective::update)
        val prompted = runStage("PROMPT", DiagnosticStage.PROMPT, appraised, promptBuilder::buildPrompt)
        val generated = runStage("GGUF", DiagnosticStage.GGUF, prompted, gguf::generate)
        val withGeneratedTrace = if (generated.diagnostics.ggufResult == null) {
            generated.copy(
                diagnostics = generated.diagnostics.record(
                    DiagnosticStage.GGUF,
                    DiagnosticSnapshot(
                        module = "GGUF",
                        status = if (generated.reply == null) DiagnosticStatus.ERROR else DiagnosticStatus.PASS,
                        input = mapOf("promptLength" to generated.prompt?.text?.length.toString()),
                        output = mapOf("replyLength" to generated.reply?.text?.length.toString()),
                        decision = if (generated.reply == null) "MISSING_REPLY" else "GENERATED",
                        reasonCodes = listOf(if (generated.reply == null) "GGUF_REPLY_MISSING" else "GGUF_OUTPUT_AVAILABLE"),
                    ),
                ),
            )
        } else {
            generated
        }
        if (withGeneratedTrace.reply == null) {
            throw failure("GGUF", DiagnosticStage.GGUF, withGeneratedTrace, "GGUF.REPLY_MISSING")
        }

        val validated = if (outputValidator == null) {
            withGeneratedTrace.copy(
                diagnostics = withGeneratedTrace.diagnostics
                    .record(
                        DiagnosticStage.OUTPUT_VALIDATION,
                        DiagnosticSnapshot(
                            module = "OUTPUT_VALIDATOR",
                            status = DiagnosticStatus.NON_CABLATO,
                            decision = "NOT_EXECUTED",
                            reasonCodes = listOf("OUTPUT_VALIDATOR_NON_CABLATO"),
                        ),
                    )
                    .add("output_validation.non_cablato"),
            )
        } else {
            runStage("OUTPUT_VALIDATION", DiagnosticStage.OUTPUT_VALIDATION, withGeneratedTrace, outputValidator::validate)
        }

        val finalTrace = validated.diagnostics.add("turn.completed")
        val finalReply = validated.reply!!.copy(
            diagnosticTrace = finalTrace.tags,
            diagnostics = finalTrace,
        )
        return validated.copy(reply = finalReply, diagnostics = finalTrace)
    }

    private fun requireNonPersistentPreflight(turn: MatrixTurnFrame): MatrixTurnFrame {
        val result = turn.requireMemory()
        if (!result.stableWrite && result.memoryIds.isEmpty()) return turn

        val rejected = result.copy(
            status = "REJECTED_UNAUTHORIZED_PREFLIGHT_WRITE",
            memoryIds = emptyList(),
            stableWrite = false,
            reason = "pre-response memory preflight attempted a durable write",
        )
        val failed = turn.copy(
            memoryResult = rejected,
            diagnostics = turn.diagnostics
                .record(
                    DiagnosticStage.MEMORY,
                    DiagnosticSnapshot(
                        module = "MEMORY_PREFLIGHT",
                        status = DiagnosticStatus.ERROR,
                        input = mapOf("status" to result.status),
                        output = mapOf(
                            "stableWrite" to result.stableWrite.toString(),
                            "memoryIds" to result.memoryIds.joinToString(","),
                        ),
                        decision = "REJECT",
                        reasonCodes = listOf("UNAUTHORIZED_PREFLIGHT_WRITE"),
                    ),
                )
                .diverge("MEMORY_PREFLIGHT.UNAUTHORIZED_STABLE_WRITE"),
        )
        throw MatrixPipelineException("MEMORY_PREFLIGHT", failed, IllegalStateException(rejected.reason))
    }

    private fun runStage(
        stageName: String,
        diagnosticStage: DiagnosticStage,
        turn: MatrixTurnFrame,
        operation: (MatrixTurnFrame) -> MatrixTurnFrame,
    ): MatrixTurnFrame = try {
        operation(turn)
    } catch (error: MatrixPipelineException) {
        throw error
    } catch (error: Throwable) {
        throw failure(stageName, diagnosticStage, turn, "$stageName.EXCEPTION", error)
    }

    private fun failure(
        stageName: String,
        diagnosticStage: DiagnosticStage,
        turn: MatrixTurnFrame,
        divergence: String,
        cause: Throwable = IllegalStateException(divergence),
    ): MatrixPipelineException {
        val failed = turn.copy(
            diagnostics = turn.diagnostics
                .record(
                    diagnosticStage,
                    DiagnosticSnapshot(
                        module = stageName,
                        status = DiagnosticStatus.ERROR,
                        decision = "ERROR",
                        reasonCodes = listOf("${stageName}_EXCEPTION"),
                    ),
                )
                .diverge(divergence),
        )
        return MatrixPipelineException(stageName, failed, cause)
    }
}

class MatrixPipelineException(
    val stage: String,
    val failedFrame: MatrixTurnFrame,
    cause: Throwable,
) : IllegalStateException("Matrix pipeline failed at $stage", cause)
