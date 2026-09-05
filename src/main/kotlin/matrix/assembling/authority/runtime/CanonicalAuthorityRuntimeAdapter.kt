package matrix.assembling.authority.runtime

import matrix.assembling.MatrixTurnFrame
import matrix.assembling.TypedClaim
import matrix.assembling.authority.AuthorityResolveRequest
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolver
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipBridge
import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult

/**
 * Runtime-facing canonical Authority input.
 *
 * This is deliberately separate from MatrixTurnFrame while the legacy frame cannot carry the
 * complete MIP Context/Retrieval/Authority surface without semantic loss.
 */
data class CanonicalAuthorityRuntimeInput(
    val requestId: String,
    val claim: MipClaimV1,
    val contextSnapshot: MatrixContextSnapshot,
    val retrievalResult: MipField<RetrievalResult> = MipField.notApplicable(),
    val provenance: ProvenanceRef,
) {
    init {
        require(requestId.isNotBlank()) { "requestId must not be blank" }
    }
}

/** Observable compatibility gaps when projecting a root TypedClaim into MIP. */
enum class LegacyAuthorityGap {
    SOURCE_IDENTITY_NOT_REPRESENTED,
    DIALOGUE_ACT_NOT_REPRESENTED,
    CLAIM_KIND_NOT_REPRESENTED,
    OWNER_UNRESOLVED,
    PERSPECTIVE_UNRESOLVED,
}

/**
 * The root AuthorityDecision is never emitted here because it cannot losslessly carry
 * AUTHORITY-1.0 EpistemicClass/confidence/provenance/ambiguity/candidate semantics.
 */
enum class LegacyAuthorityDecisionProjectionStatus {
    UNREPRESENTABLE_WITHOUT_SEMANTIC_LOSS,
}

sealed interface LegacyAuthorityRuntimeOutcome {
    /**
     * Legacy input was safely projected into a canonical MIP claim and resolved.
     * `resolutionStatus` may still be HOLD/PARTIAL/UNAVAILABLE; that is a valid canonical result.
     */
    data class Canonical(
        val canonicalClaim: MipClaimV1,
        val resolution: AuthorityResolution,
        val compatibilityGaps: List<LegacyAuthorityGap>,
        val legacyDecisionProjectionStatus: LegacyAuthorityDecisionProjectionStatus =
            LegacyAuthorityDecisionProjectionStatus.UNREPRESENTABLE_WITHOUT_SEMANTIC_LOSS,
    ) : LegacyAuthorityRuntimeOutcome

    /** Structural runtime mismatch prevented a safe canonical request from being built. */
    data class Blocked(
        val reasonCodes: List<String>,
        val details: List<String>,
    ) : LegacyAuthorityRuntimeOutcome {
        init {
            require(reasonCodes.isNotEmpty()) { "blocked runtime outcome requires a reason code" }
            require(reasonCodes.all { it.startsWith("AUTHORITY.RUNTIME.") }) {
                "runtime adapter reason codes must use AUTHORITY.RUNTIME.* namespace"
            }
            require(reasonCodes.distinct().size == reasonCodes.size) {
                "runtime adapter reason codes must be unique"
            }
            require(details.all { it.isNotBlank() }) { "runtime adapter details must not be blank" }
        }
    }
}

/**
 * Standalone bridge into the canonical Authority resolver.
 *
 * It does not implement the legacy AuthorityResolverPort and therefore cannot be accidentally
 * wired into MatrixAssemblingOrchestrator in this checkpoint. It performs no Memory writes,
 * admission, persistence, or free-text interpretation.
 */
class CanonicalAuthorityRuntimeAdapter(
    private val resolver: AuthorityResolver,
) {
    fun resolveCanonical(input: CanonicalAuthorityRuntimeInput): AuthorityResolution {
        requireContextMatchesClaimSession(
            contextSnapshot = input.contextSnapshot,
            expectedTurnId = null,
            expectedSessionId = null,
        )
        return resolver.resolve(
            AuthorityResolveRequest(
                requestId = input.requestId,
                claim = input.claim,
                contextSnapshot = input.contextSnapshot,
                retrievalResult = input.retrievalResult,
                provenance = input.provenance,
            )
        )
    }

    /**
     * Compatibility attempt for one explicitly selected root TypedClaim.
     *
     * Multi-claim turns are intentionally claim-explicit: this method never selects the first
     * claim implicitly. Missing legacy semantics are represented in MIP and surfaced through
     * compatibilityGaps; they are never guessed.
     */
    fun resolveLegacyClaim(
        turn: MatrixTurnFrame,
        claim: TypedClaim,
        contextSnapshot: MatrixContextSnapshot,
        retrievalResult: MipField<RetrievalResult> = MipField.notApplicable(),
        provenance: ProvenanceRef,
        requestId: String = "${turn.turnId}:${claim.claimId}:authority",
    ): LegacyAuthorityRuntimeOutcome {
        val structuralProblems = mutableListOf<Pair<String, String>>()

        if (contextSnapshot.turnId != turn.turnId) {
            structuralProblems += REASON_CONTEXT_TURN_MISMATCH to
                "context turnId=${contextSnapshot.turnId} does not match frame turnId=${turn.turnId}"
        }
        if (contextSnapshot.sessionId != turn.sessionId) {
            structuralProblems += REASON_CONTEXT_SESSION_MISMATCH to
                "context sessionId=${contextSnapshot.sessionId} does not match frame sessionId=${turn.sessionId}"
        }
        if (turn.typedClaims.none { it.claimId == claim.claimId }) {
            structuralProblems += REASON_CLAIM_NOT_IN_FRAME to
                "claimId=${claim.claimId} is not present in MatrixTurnFrame.typedClaims"
        }
        if (provenance.claimId.status == MipFieldStatus.PRESENT && provenance.claimId.value != claim.claimId) {
            structuralProblems += REASON_PROVENANCE_CLAIM_MISMATCH to
                "provenance claimId=${provenance.claimId.value} does not match claimId=${claim.claimId}"
        }

        if (structuralProblems.isNotEmpty()) {
            return LegacyAuthorityRuntimeOutcome.Blocked(
                reasonCodes = structuralProblems.map { it.first }.distinct(),
                details = structuralProblems.map { it.second },
            )
        }

        val canonicalClaim = MipBridge.fromAssemblingTypedClaim(
            native = claim,
            speakerId = turn.input.speakerId,
            observerId = turn.input.observerId,
        )

        val gaps = buildList {
            if (canonicalClaim.source.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
                add(LegacyAuthorityGap.SOURCE_IDENTITY_NOT_REPRESENTED)
            }
            if (canonicalClaim.dialogueAct.status != MipFieldStatus.PRESENT) {
                add(LegacyAuthorityGap.DIALOGUE_ACT_NOT_REPRESENTED)
            }
            if (canonicalClaim.semanticMarkers["CLAIM_KIND"]?.status != MipFieldStatus.PRESENT) {
                add(LegacyAuthorityGap.CLAIM_KIND_NOT_REPRESENTED)
            }
            if (canonicalClaim.owner.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
                add(LegacyAuthorityGap.OWNER_UNRESOLVED)
            }
            if (canonicalClaim.perspective.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
                add(LegacyAuthorityGap.PERSPECTIVE_UNRESOLVED)
            }
        }.distinct()

        val resolution = resolver.resolve(
            AuthorityResolveRequest(
                requestId = requestId,
                claim = canonicalClaim,
                contextSnapshot = contextSnapshot,
                retrievalResult = retrievalResult,
                provenance = provenance,
            )
        )

        return LegacyAuthorityRuntimeOutcome.Canonical(
            canonicalClaim = canonicalClaim,
            resolution = resolution,
            compatibilityGaps = gaps,
        )
    }

    private fun requireContextMatchesClaimSession(
        contextSnapshot: MatrixContextSnapshot,
        expectedTurnId: String?,
        expectedSessionId: String?,
    ) {
        expectedTurnId?.let {
            require(contextSnapshot.turnId == it) { "context turnId mismatch" }
        }
        expectedSessionId?.let {
            require(contextSnapshot.sessionId == it) { "context sessionId mismatch" }
        }
    }

    companion object {
        const val REASON_CONTEXT_TURN_MISMATCH = "AUTHORITY.RUNTIME.CONTEXT_TURN_MISMATCH"
        const val REASON_CONTEXT_SESSION_MISMATCH = "AUTHORITY.RUNTIME.CONTEXT_SESSION_MISMATCH"
        const val REASON_CLAIM_NOT_IN_FRAME = "AUTHORITY.RUNTIME.CLAIM_NOT_IN_FRAME"
        const val REASON_PROVENANCE_CLAIM_MISMATCH = "AUTHORITY.RUNTIME.PROVENANCE_CLAIM_MISMATCH"
    }
}
