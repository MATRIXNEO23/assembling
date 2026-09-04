package matrix.assembling

/**
 * Ports used by the assembling layer.
 *
 * Each module receives a MatrixTurnFrame and returns a copied/updated frame.
 * This is the single internal contract between NLU, understanding, coherence,
 * authority, memory, affective state, prompt construction and GGUF generation.
 */
interface NluPort {
    fun analyze(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface UnderstandingPort {
    fun understand(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface CoherenceGuardPort {
    fun check(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface AuthorityResolverPort {
    fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface MemoryAdmissionPort {
    fun admit(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface AffectivePort {
    fun update(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface SemanticFrameToPromptPort {
    fun buildPrompt(turn: MatrixTurnFrame): MatrixTurnFrame
}

interface GgufPort {
    fun generate(turn: MatrixTurnFrame): MatrixTurnFrame
}
