package matrix.assembling

import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipUnderstandingV3Claim
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.RetrievalResult

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
    /** Legacy compatibility-only Authority surface; never auto-synchronized from canonical Authority. */
    val authorityDecision: AuthorityDecision? = null,
    val memoryResult: MemoryAdmissionResult? = null,
    val affectiveState: AffectiveState? = null,
    val prompt: GgufPrompt? = null,
    val reply: AssistantReply? = null,
    val diagnostics: DiagnosticTrace = DiagnosticTrace(),
    /** Current immutable canonical MIP context snapshot. Legacy runtime defaults to UNAVAILABLE. */
    val contextSnapshot: MipField<MatrixContextSnapshot> = MipField.unavailable(),
    /**
     * Current canonical retrieval results. Outer field status describes provider/stage availability;
     * successful no-match is represented by PRESENT list entries with RetrievalStatus.NO_MATCH.
     */
    val retrievalResults: MipField<List<RetrievalResult>> = MipField.unavailable(),
    /** Current claim-wise canonical AUTHORITY-1.0 resolutions. */
    val canonicalAuthorityResolutions: MipField<List<AuthorityResolution>> = MipField.unavailable(),
    /**
     * Complete lossless Understanding V3 observation. Its claims are the real canonical V3
     * TypedClaims; legacy `typedClaims` remains compatibility-only and is never auto-populated.
     */
    val canonicalUnderstandingV3: MipField<MipUnderstandingV3Observation> = MipField.unavailable(),
) {
    init {
        validateCanonicalRuntimeSlots()
    }

    fun requireNlu(): NluOutput = nlu ?: error("MatrixTurnFrame missing NLU output")
    fun requireSemantic(): SemanticFrame = semantic ?: error("MatrixTurnFrame missing semantic frame")
    fun requireCoherence(): CoherenceDecision = coherenceDecision ?: error("MatrixTurnFrame missing coherence decision")
    fun requireAuthority(): AuthorityDecision = authorityDecision ?: error("MatrixTurnFrame missing authority decision")
    fun requireMemory(): MemoryAdmissionResult = memoryResult ?: error("MatrixTurnFrame missing memory result")
    fun requireAffective(): AffectiveState = affectiveState ?: error("MatrixTurnFrame missing affective state")
    fun requirePrompt(): GgufPrompt = prompt ?: error("MatrixTurnFrame missing GGUF prompt")

    fun requireCanonicalUnderstandingV3(): MipUnderstandingV3Observation =
        canonicalUnderstandingV3.requirePresentSlot("canonicalUnderstandingV3")

    fun requireCanonicalTypedClaimsV3(): List<MipUnderstandingV3Claim> =
        requireCanonicalUnderstandingV3().claims

    fun requireCanonicalContextSnapshot(): MatrixContextSnapshot =
        contextSnapshot.requirePresentSlot("contextSnapshot")

    fun requireCanonicalRetrievalResults(): List<RetrievalResult> =
        retrievalResults.requirePresentSlot("retrievalResults")

    fun requireCanonicalAuthorityResolutions(): List<AuthorityResolution> =
        canonicalAuthorityResolutions.requirePresentSlot("canonicalAuthorityResolutions")

    fun requireCanonicalAuthorityForClaim(claimId: String): AuthorityResolution {
        require(claimId.isNotBlank()) { "claimId must not be blank" }
        return requireCanonicalAuthorityResolutions().singleOrNull { it.claimId == claimId }
            ?: error("MatrixTurnFrame missing unique canonical AuthorityResolution for claimId=$claimId")
    }

    private fun validateCanonicalRuntimeSlots() {
        require(canonicalUnderstandingV3.status in setOf(
            MipFieldStatus.PRESENT,
            MipFieldStatus.UNAVAILABLE,
            MipFieldStatus.ERROR,
        )) {
            "canonicalUnderstandingV3 outer status=${canonicalUnderstandingV3.status} is invalid; linguistic ambiguity belongs inside V3 claims"
        }
        if (canonicalUnderstandingV3.status == MipFieldStatus.PRESENT) {
            val understanding = requireNotNull(canonicalUnderstandingV3.value)
            require(understanding.input == input.text) {
                "canonical Understanding input does not match frame input"
            }
            require(understanding.speaker.resolutionStatus == matrix.assembling.mip.MipEntityResolutionStatus.RESOLVED &&
                understanding.speaker.entityId == input.speakerId
            ) {
                "canonical Understanding speaker does not match frame speakerId"
            }
            require(understanding.observer.resolutionStatus == matrix.assembling.mip.MipEntityResolutionStatus.RESOLVED &&
                understanding.observer.entityId == input.observerId
            ) {
                "canonical Understanding observer does not match frame observerId"
            }
        }

        require(contextSnapshot.status != MipFieldStatus.NO_MATCH) {
            "contextSnapshot cannot use NO_MATCH; context provider availability must remain explicit"
        }
        require(retrievalResults.status !in setOf(
            MipFieldStatus.NO_MATCH,
            MipFieldStatus.AMBIGUOUS,
            MipFieldStatus.CONFLICTED,
        )) {
            "retrievalResults outer status=${retrievalResults.status} is invalid; result-level retrieval status belongs inside RetrievalResult"
        }

        val context = contextSnapshot.value.takeIf { contextSnapshot.status == MipFieldStatus.PRESENT }
        if (context != null) {
            require(context.turnId == turnId) {
                "canonical context turnId=${context.turnId} does not match frame turnId=$turnId"
            }
            require(context.sessionId == sessionId) {
                "canonical context sessionId=${context.sessionId} does not match frame sessionId=$sessionId"
            }
        }

        if (retrievalResults.status == MipFieldStatus.PRESENT) {
            require(context != null) {
                "PRESENT retrievalResults requires PRESENT contextSnapshot"
            }
            val results = requireNotNull(retrievalResults.value)
            require(results.isNotEmpty()) {
                "PRESENT retrievalResults must contain at least one explicit RetrievalResult; use NO_MATCH inside a result"
            }
            require(results.map { it.queryId }.distinct().size == results.size) {
                "retrievalResults queryIds must be unique"
            }
        }

        if (canonicalAuthorityResolutions.status == MipFieldStatus.PRESENT) {
            require(context != null) {
                "PRESENT canonicalAuthorityResolutions requires PRESENT contextSnapshot"
            }
            val resolutions = requireNotNull(canonicalAuthorityResolutions.value)
            val canonicalClaimIds = if (canonicalUnderstandingV3.status == MipFieldStatus.PRESENT) {
                requireNotNull(canonicalUnderstandingV3.value).claims.map { it.claimId }
            } else {
                typedClaims.map { it.claimId }
            }
            require(canonicalClaimIds.distinct().size == canonicalClaimIds.size) {
                "canonical Authority source claimIds must be unique"
            }
            require(resolutions.map { it.resolutionId }.distinct().size == resolutions.size) {
                "canonical Authority resolutionIds must be unique"
            }
            require(resolutions.map { it.claimId }.distinct().size == resolutions.size) {
                "canonical Authority resolutions must contain at most one current resolution per claimId"
            }
            require(resolutions.map { it.claimId }.toSet() == canonicalClaimIds.toSet()) {
                "canonical Authority resolutions must cover exactly the current canonical claim set"
            }
            require(resolutions.all { it.contextSnapshotId == context.snapshotId }) {
                "canonical Authority resolutions must reference the current contextSnapshotId=${context.snapshotId}"
            }
        }
    }
}

private fun <T> MipField<T>.requirePresentSlot(name: String): T =
    if (status == MipFieldStatus.PRESENT && value != null) {
        value
    } else {
        error("MatrixTurnFrame canonical slot $name must be PRESENT, found $status")
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
    val resolvedSubject: String? = null,
    val resolvedTarget: String? = null,
    val resolvedOwner: String? = null,
    val resolvedPerspective: String? = null,
    val objectValue: String? = null,
    val sourceType: String? = null,
    val worldTruth: Boolean = false,
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

data class DiagnosticSnapshot(
    val module: String,
    val input: String? = null,
    val output: String? = null,
    val decision: String? = null,
    val status: String = "PASS",
    val reasonCodes: List<String> = emptyList(),
    val confidence: Map<String, Double> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
)

data class DiagnosticTrace(
    val inputOriginale: String? = null,
    val observation: DiagnosticSnapshot? = null,
    val understandingResult: DiagnosticSnapshot? = null,
    val authorityResolution: DiagnosticSnapshot? = null,
    val admissionDecision: DiagnosticSnapshot? = null,
    val memoryResult: DiagnosticSnapshot? = null,
    val memoryId: String? = null,
    val affectiveStimulus: DiagnosticSnapshot? = null,
    val firstDivergence: String? = null,
    val reasoningChain: List<String> = emptyList(),
    val events: List<String> = emptyList(),
    val tags: Map<String, String> = emptyMap(),
) {
    fun add(event: String): DiagnosticTrace = copy(events = events + event)
    fun tag(key: String, value: String): DiagnosticTrace = copy(tags = tags + (key to value))
    fun withInput(value: String): DiagnosticTrace = copy(inputOriginale = value)
    fun reason(code: String): DiagnosticTrace = copy(reasoningChain = reasoningChain + code)
    fun observe(snapshot: DiagnosticSnapshot): DiagnosticTrace = copy(observation = snapshot)
    fun understood(snapshot: DiagnosticSnapshot): DiagnosticTrace = copy(understandingResult = snapshot)
    fun authority(snapshot: DiagnosticSnapshot): DiagnosticTrace = copy(authorityResolution = snapshot)
    fun admission(snapshot: DiagnosticSnapshot): DiagnosticTrace = copy(admissionDecision = snapshot)
    fun memory(snapshot: DiagnosticSnapshot, id: String? = memoryId): DiagnosticTrace = copy(
        memoryResult = snapshot,
        memoryId = id,
    )
    fun affective(snapshot: DiagnosticSnapshot): DiagnosticTrace = copy(affectiveStimulus = snapshot)
    fun diverge(code: String): DiagnosticTrace = copy(
        firstDivergence = firstDivergence ?: code,
        reasoningChain = reasoningChain + code,
        events = events + "divergence.$code",
    )
}

class MatrixBoundaryViolationException(
    message: String,
    val diagnosticTrace: DiagnosticTrace,
) : IllegalStateException(message)
