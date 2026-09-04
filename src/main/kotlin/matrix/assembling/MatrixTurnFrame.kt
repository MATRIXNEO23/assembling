package matrix.assembling

/**
 * Canonical per-turn data envelope for Matrix Engine assembly.
 *
 * Every module reads the same frame and returns an updated copy. This avoids
 * direct coupling such as NLU -> Memory or Affective -> GGUF and keeps all
 * cross-module data visible for diagnostics.
 */
data class MatrixTurnFrame(
    val turnId: String,
    val sessionId: String,
    val input: UserMessage,
    val nlu: NluOutput? = null,
    val semantic: SemanticFrame? = null,
    val typedClaims: List<TypedClaim> = emptyList(),
    val coherenceDecision: CoherenceDecision? = null,
    val authorityDecision: AuthorityDecision? = null,
    val memoryResult: MemoryAdmissionResult? = null,
    val affectiveState: AffectiveState? = null,
    val prompt: GgufPrompt? = null,
    val reply: AssistantReply? = null,
    val diagnostics: DiagnosticTrace = DiagnosticTrace(),
) {
    fun requireNlu(): NluOutput = nlu ?: error("MatrixTurnFrame missing NLU output")
    fun requireSemantic(): SemanticFrame = semantic ?: error("MatrixTurnFrame missing semantic frame")
    fun requireCoherence(): CoherenceDecision = coherenceDecision ?: error("MatrixTurnFrame missing coherence decision")
    fun requireAuthority(): AuthorityDecision = authorityDecision ?: error("MatrixTurnFrame missing authority decision")
    fun requireMemory(): MemoryAdmissionResult = memoryResult ?: error("MatrixTurnFrame missing memory result")
    fun requireAffective(): AffectiveState = affectiveState ?: error("MatrixTurnFrame missing affective state")
    fun requirePrompt(): GgufPrompt = prompt ?: error("MatrixTurnFrame missing GGUF prompt")
}

data class UserMessage(
    val text: String,
    val speakerId: String,
    val observerId: String = "luna",
    val timestampMillis: Long,
    val locale: String = "it",
)

data class NluOutput(
    val dialogueAct: String,
    val predicate: String,
    val polarity: String,
    val temporalRelation: String,
    val subjectReferent: String,
    val targetReferent: String,
    val ownerReferent: String,
    val perspectiveReferent: String,
    val confidence: Map<String, Double>,
    val spans: Map<String, TextSpan?>,
    /**
     * Optional resolved fields produced by the Understanding lab contract.
     * They preserve already-resolved semantic evidence without granting
     * downstream truth or persistence authority.
     */
    val resolvedSubject: String? = null,
    val resolvedTarget: String? = null,
    val resolvedOwner: String? = null,
    val resolvedPerspective: String? = null,
    val objectValue: String? = null,
    val sourceType: String? = null,
    /**
     * Observation/provenance flag copied from the source runtime when present.
     * It is never, by itself, authorization for Belief or Memory persistence.
     */
    val worldTruth: Boolean = false,
)

data class TextSpan(val start: Int, val end: Int)

data class SemanticFrame(
    val originalText: String,
    val semanticSummary: String,
    val dialogueAct: String,
    val predicate: String,
    val polarity: String,
    val temporalRelation: String,
    val subject: String,
    val target: String?,
    val owner: String?,
    val confidence: Map<String, Double>,
    val adultOrIntimacy: Boolean = false,
    /**
     * LEGACY COMPATIBILITY FIELD.
     * New Understanding code must not set this as durable-memory authority.
     * Coherence + Authority + Memory Admission own persistence decisions.
     */
    val stableMemoryAllowed: Boolean = false,
)

data class TypedClaim(
    val claimId: String,
    val ownerId: String?,
    val subject: String,
    val predicate: String,
    val objectValue: String?,
    val target: String?,
    val polarity: String,
    val temporalRelation: String,
    val sourceType: String,
    val confidence: Map<String, Double>,
    val spans: Map<String, TextSpan?> = emptyMap(),
    val perspective: String? = null,
    val worldTruth: Boolean = false,
)

enum class CoherenceDecision {
    SAFE_TO_ADMIT,
    SAFE_TRANSIENT_ONLY,
    LOW_CONFIDENCE_HOLD,
    REPORT_ONLY,
    QUESTION_ONLY,
    CONFLICT_REQUIRES_REVIEW,
    REJECTED_UNSAFE,
}

data class AuthorityDecision(
    val accepted: Boolean,
    val ownerResolved: Boolean,
    val sourceType: String,
    val conflictStatus: String,
    val reason: String,
)

data class MemoryAdmissionResult(
    val status: String,
    val memoryIds: List<String> = emptyList(),
    val stableWrite: Boolean = false,
    val reason: String,
)

data class AffectiveState(
    /**
     * Compatibility projection for prompt construction only. Canonical
     * RelationshipState is externally owned and must not be mutated here.
     */
    val relationshipSummary: String,
    val affectiveSummary: String,
    val persistentDeltaAllowed: Boolean,
)

data class GgufPrompt(
    val text: String,
)

data class AssistantReply(
    val text: String,
    val diagnosticTrace: Map<String, String> = emptyMap(),
)

data class DiagnosticTrace(
    val events: List<String> = emptyList(),
    val tags: Map<String, String> = emptyMap(),
) {
    fun add(event: String): DiagnosticTrace = copy(events = events + event)
    fun tag(key: String, value: String): DiagnosticTrace = copy(tags = tags + (key to value))
}
