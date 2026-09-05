package matrix.assembling

/** Ports used by the authoritative MatrixTurnFrame assembly path. */
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

/**
 * Pre-response memory evaluation only. Implementations must not write durable
 * state and must always return stableWrite=false.
 */
interface MemoryPreflightPort {
    fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame
}

/**
 * Compatibility facade for existing non-persistent adapters. New durable memory
 * implementations must use a final consolidation boundary instead.
 */
@Deprecated("Pre-response stage is MemoryPreflightPort; durable writes belong to PersistentConsolidationPort")
interface MemoryAdmissionPort : MemoryPreflightPort {
    fun admit(turn: MatrixTurnFrame): MatrixTurnFrame
    override fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame = admit(turn)
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

/** Optional boundary until semantic output validation is implemented. */
interface OutputValidatorPort {
    fun validate(turn: MatrixTurnFrame): MatrixTurnFrame
}

/** Durable writes are allowed only here, after accepted output. */
interface PersistentConsolidationPort {
    fun consolidate(turn: MatrixTurnFrame): MatrixTurnFrame
}
