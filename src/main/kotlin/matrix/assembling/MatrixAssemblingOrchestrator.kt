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
            diagnostics = DiagnosticTrace().add("turn.created"),
        )
        val completed = handle(initial)
        return completed.reply ?: error("MatrixTurnFrame completed without assistant reply")
    }

    fun handle(turn: MatrixTurnFrame): MatrixTurnFrame {
        return turn
            .let(nlu::analyze)
            .let(understanding::understand)
            .let(coherence::check)
            .let(authority::resolve)
            .let(memory::admit)
            .let(affective::update)
            .let(promptBuilder::buildPrompt)
            .let(gguf::generate)
            .let { completed -> completed.copy(diagnostics = completed.diagnostics.add("turn.completed")) }
    }
}
