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
    /** All NLU claims. `nlu` remains the primary-claim compatibility view. */
    val nluClaims: List<NluOutput> = emptyList(),
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
    /** Resolved semantic evidence; it carries no truth or persistence authority. */
    val resolvedSubject: String? = null,
    val resolvedTarget: String? = null,
    val resolvedOwner: String? = null,
    val resolvedPerspective: String? = null,
    val objectValue: String? = null,
    val sourceType: String? = null,
    /** Observation/provenance flag only; never a persistence shortcut. */
    val worldTruth: Boolean = false,
    /** Explicit semantic marker when supplied by the NLU contract. */
    val adultOrIntimacy: Boolean? = null,
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
    /** Prompt projection only. Canonical RelationshipState is externally owned. */
    val relationshipSummary: String,
    val affectiveSummary: String,
    val persistentDeltaAllowed: Boolean,
)

data class GgufPrompt(val text: String)

data class AssistantReply(
    val text: String,
    val diagnosticTrace: Map<String, String> = emptyMap(),
    val diagnostics: DiagnosticTrace? = null,
)

enum class DiagnosticStatus {
    PASS,
    HOLD,
    REJECT,
    ERROR,
    NOT_EXECUTED,
    NON_CABLATO,
}

enum class DiagnosticStage {
    INPUT,
    OBSERVATION,
    UNDERSTANDING,
    COHERENCE,
    AUTHORITY,
    MEMORY_ADMISSION,
    MEMORY,
    AFFECTIVE,
    PROMPT,
    GGUF,
    OUTPUT_VALIDATION,
}

/** Observable module evidence only; never private chain-of-thought. */
data class DiagnosticSnapshot(
    val module: String,
    val status: DiagnosticStatus,
    val input: Map<String, String> = emptyMap(),
    val output: Map<String, String> = emptyMap(),
    val decision: String? = null,
    val reasonCodes: List<String> = emptyList(),
)

data class DiagnosticTrace(
    val inputOriginale: String? = null,
    val input: DiagnosticSnapshot? = null,
    val observation: DiagnosticSnapshot? = null,
    val understandingResult: DiagnosticSnapshot? = null,
    val coherenceResult: DiagnosticSnapshot? = null,
    val authorityResolution: DiagnosticSnapshot? = null,
    val admissionDecision: DiagnosticSnapshot? = null,
    val memory: DiagnosticSnapshot? = null,
    val memoryId: String? = null,
    val affectiveStimulus: DiagnosticSnapshot? = null,
    val promptResult: DiagnosticSnapshot? = null,
    val ggufResult: DiagnosticSnapshot? = null,
    val outputValidation: DiagnosticSnapshot? = null,
    val firstDivergence: String? = null,
    /** Ordered deterministic reason codes, not hidden reasoning. */
    val reasoningChain: List<String> = emptyList(),
    val events: List<String> = emptyList(),
    val tags: Map<String, String> = emptyMap(),
) {
    fun add(event: String): DiagnosticTrace = copy(events = events + event)

    fun tag(key: String, value: String): DiagnosticTrace = copy(tags = tags + (key to value))

    fun reason(code: String): DiagnosticTrace =
        if (code.isBlank() || code in reasoningChain) this else copy(reasoningChain = reasoningChain + code)

    fun diverge(code: String): DiagnosticTrace {
        val withReason = reason(code)
        return if (withReason.firstDivergence == null) withReason.copy(firstDivergence = code) else withReason
    }

    fun record(stage: DiagnosticStage, snapshot: DiagnosticSnapshot): DiagnosticTrace {
        val withReasons = snapshot.reasonCodes.fold(this) { trace, code -> trace.reason(code) }
        return when (stage) {
            DiagnosticStage.INPUT -> withReasons.copy(input = snapshot)
            DiagnosticStage.OBSERVATION -> withReasons.copy(observation = snapshot)
            DiagnosticStage.UNDERSTANDING -> withReasons.copy(understandingResult = snapshot)
            DiagnosticStage.COHERENCE -> withReasons.copy(coherenceResult = snapshot)
            DiagnosticStage.AUTHORITY -> withReasons.copy(authorityResolution = snapshot)
            DiagnosticStage.MEMORY_ADMISSION -> withReasons.copy(admissionDecision = snapshot)
            DiagnosticStage.MEMORY -> withReasons.copy(memory = snapshot)
            DiagnosticStage.AFFECTIVE -> withReasons.copy(affectiveStimulus = snapshot)
            DiagnosticStage.PROMPT -> withReasons.copy(promptResult = snapshot)
            DiagnosticStage.GGUF -> withReasons.copy(ggufResult = snapshot)
            DiagnosticStage.OUTPUT_VALIDATION -> withReasons.copy(outputValidation = snapshot)
        }
    }
}
