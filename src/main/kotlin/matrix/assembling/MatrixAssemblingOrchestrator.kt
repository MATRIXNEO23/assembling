package matrix.assembling

/**
 * Integration orchestrator for Matrix/Luna components.
 *
 * All modules communicate through MatrixTurnFrame. Concrete implementations may
 * live in separate repositories and are connected through adapters implementing
 * IntegrationPorts.kt.
 */
class MatrixAssemblingOrchestrator(
    private val nlu: NluPort,
    private val understanding: UnderstandingPort,
    private val coherence: CoherenceGuardPort,
    private val authority: AuthorityResolverPort,
    private val memory: MemoryAdmissionPort,
    private val affective: AffectivePort,
    private val promptBuilder: SemanticFrameToPromptPort,
    private val gguf: GgufPort,
) {
    fun handle(input: UserMessage, turnId: String, sessionId: String): AssistantReply {
        val initial = MatrixTurnFrame(
            turnId = turnId,
            sessionId = sessionId,
            input = input,
            diagnostics = DiagnosticTrace()
                .withInput(input.text)
                .reason("INPUT_ACCEPTED")
                .add("turn.created"),
        )
        val completed = handle(initial)
        return completed.reply ?: error("MatrixTurnFrame completed without assistant reply")
    }

    fun handle(turn: MatrixTurnFrame): MatrixTurnFrame {
        return turn
            .let(::initializeTrace)
            .let(nlu::analyze)
            .let(understanding::understand)
            .let(coherence::check)
            .let(authority::resolve)
            .let(memory::admit)
            .let(::enforcePreResponseMemoryBoundary)
            .let(affective::update)
            .let(promptBuilder::buildPrompt)
            .let(gguf::generate)
            .let { completed -> completed.copy(diagnostics = completed.diagnostics.add("turn.completed")) }
    }

    private fun initializeTrace(turn: MatrixTurnFrame): MatrixTurnFrame {
        if (turn.diagnostics.inputOriginale != null) return turn
        return turn.copy(
            diagnostics = turn.diagnostics
                .withInput(turn.input.text)
                .reason("INPUT_ACCEPTED")
                .add("turn.trace.initialized"),
        )
    }

    private fun enforcePreResponseMemoryBoundary(turn: MatrixTurnFrame): MatrixTurnFrame {
        val memoryResult = turn.requireMemory()
        if (memoryResult.stableWrite || memoryResult.memoryIds.isNotEmpty()) {
            val trace = turn.diagnostics
                .diverge("MEMORY.PRE_RESPONSE_STABLE_WRITE")
                .tag("memory.pre_response_boundary", "VIOLATION")
            throw MatrixBoundaryViolationException(
                "Durable memory write/result is forbidden before output validation and persistent consolidation",
                trace,
            )
        }
        return turn.copy(
            diagnostics = turn.diagnostics
                .reason("MEMORY_PRE_RESPONSE_BOUNDARY_OK")
                .add("memory.pre_response_boundary.ok"),
        )
    }
}
