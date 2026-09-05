package matrix.assembling.authority

import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult

/**
 * Canonical runtime input for AUTHORITY-1.0.
 *
 * This is a contract DTO only. It performs no Authority classification, contradiction
 * detection, retrieval, Memory access, persistence, or natural-language parsing.
 */
data class AuthorityResolveRequest(
    val requestId: String,
    val claim: MipClaimV1,
    val contextSnapshot: MatrixContextSnapshot,
    val retrievalResult: MipField<RetrievalResult> = MipField.notApplicable(),
    val provenance: ProvenanceRef,
) {
    init {
        require(requestId.isNotBlank()) { "requestId must not be blank" }

        provenance.claimId.value?.let { provenanceClaimId ->
            require(provenanceClaimId == claim.claimId) {
                "request provenance claimId must match claim.claimId"
            }
        }

        require(
            retrievalResult.status in setOf(
                MipFieldStatus.PRESENT,
                MipFieldStatus.NOT_APPLICABLE,
                MipFieldStatus.UNKNOWN,
                MipFieldStatus.UNRESOLVED,
                MipFieldStatus.UNAVAILABLE,
                MipFieldStatus.ERROR,
            )
        ) {
            "retrievalResult=${retrievalResult.status} is not a valid request evidence field state; " +
                "NO_MATCH/AMBIGUOUS belong to PRESENT RetrievalResult.status"
        }
    }
}

/**
 * Canonical runtime output for the frozen MIP-1.0 / AUTHORITY-1.0 profile.
 *
 * COMPLETE means Authority assessment completed. It never means persist/admit this claim.
 */
data class AuthorityResolution(
    val resolutionId: String,
    val claimId: String,
    val contextSnapshotId: String,
    val retrievalQueryId: MipField<String> = MipField.notApplicable(),
    val resolutionStatus: AuthorityResolutionStatus,
    val authority: MipField<EpistemicClass>,
    val authorityResolutionConfidence: MipField<AuthorityResolutionConfidence>,
    val sourceReliability: MipField<SourceReliability>,
    val contradictedMemoryRef: MipField<MemoryRef>,
    val candidateMemoryRefs: List<MemoryRef> = emptyList(),
    val ambiguityReasons: List<String> = emptyList(),
    val reasonCodes: List<String>,
    val provenance: ProvenanceRef,
) {
    init {
        require(resolutionId.isNotBlank()) { "resolutionId must not be blank" }
        require(claimId.isNotBlank()) { "claimId must not be blank" }
        require(contextSnapshotId.isNotBlank()) { "contextSnapshotId must not be blank" }
        retrievalQueryId.requirePresentNonBlankString("retrievalQueryId")

        require(candidateMemoryRefs.map { it.value }.distinct().size == candidateMemoryRefs.size) {
            "candidateMemoryRefs must not contain duplicates"
        }
        requireNonBlankDistinct("ambiguityReasons", ambiguityReasons)
        requireNonBlankDistinct("reasonCodes", reasonCodes)
        require(reasonCodes.all(AuthorityReasonCode::isAuthorityCode)) {
            "all AuthorityResolution reasonCodes must use AUTHORITY.* namespace"
        }

        provenance.claimId.value?.let { provenanceClaimId ->
            require(provenanceClaimId == claimId) {
                "resolution provenance claimId must match claimId"
            }
        }

        when (contradictedMemoryRef.status) {
            MipFieldStatus.PRESENT -> {
                val contradicted = requireNotNull(contradictedMemoryRef.value)
                require(contradicted in candidateMemoryRefs) {
                    "PRESENT contradictedMemoryRef must be included in candidateMemoryRefs"
                }
            }
            MipFieldStatus.AMBIGUOUS -> require(candidateMemoryRefs.size >= 2) {
                "AMBIGUOUS contradictedMemoryRef requires at least two candidateMemoryRefs"
            }
            MipFieldStatus.NO_MATCH -> require(candidateMemoryRefs.isEmpty()) {
                "NO_MATCH contradictedMemoryRef must not carry candidateMemoryRefs"
            }
            else -> Unit
        }

        if (resolutionStatus == AuthorityResolutionStatus.COMPLETE) {
            require(authority.status == MipFieldStatus.PRESENT) {
                "COMPLETE AuthorityResolution requires authority=PRESENT"
            }
            require(authorityResolutionConfidence.status == MipFieldStatus.PRESENT) {
                "COMPLETE AuthorityResolution requires authorityResolutionConfidence=PRESENT"
            }
            require(
                contradictedMemoryRef.status == MipFieldStatus.PRESENT ||
                    contradictedMemoryRef.status == MipFieldStatus.NOT_APPLICABLE
            ) {
                "COMPLETE AuthorityResolution requires contradiction assessment PRESENT or NOT_APPLICABLE"
            }
        }
    }
}

private fun MipField<String>.requirePresentNonBlankString(name: String) {
    if (status == MipFieldStatus.PRESENT) {
        require(!value.isNullOrBlank()) { "$name PRESENT value must not be blank" }
    }
}

private fun requireNonBlankDistinct(name: String, values: List<String>) {
    require(values.all { it.isNotBlank() }) { "$name must not contain blank values" }
    require(values.distinct().size == values.size) { "$name must not contain duplicates" }
}
