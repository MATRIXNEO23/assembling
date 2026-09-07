package matrix.assembling.mip

import java.time.Instant

enum class MemoryKind {
    EPISODIC,
    SEMANTIC,
    REFLECTION,
}

enum class MemoryRetentionTier {
    PINNED,
    NORMAL,
    ARCHIVAL,
}

enum class MemoryCandidateStatus {
    PROPOSED,
    HOLD,
    REJECTED,
}

data class MemoryCandidate(
    val candidateId: String,
    val claim: MipClaimV1,
    val contextSnapshotId: String,
    val authorityResolutionId: String,
    val authorityClass: MipField<String>,
    val authorityResolutionConfidence: MipField<Double>,
    val sourceReliability: MipField<Double>,
    val contradictedMemoryRef: MipField<String>,
    val candidateMemoryRefs: List<String> = emptyList(),
    val memoryKind: MipField<MemoryKind> = MipField.unknown(),
    val retentionTier: MipField<MemoryRetentionTier> = MipField.notApplicable(),
    val status: MemoryCandidateStatus,
    val reasonCodes: List<String>,
    val provenance: ProvenanceRef,
    val createdAt: Instant,
) {
    init {
        require(candidateId.isNotBlank()) { "candidateId must not be blank" }
        require(contextSnapshotId.isNotBlank()) { "contextSnapshotId must not be blank" }
        require(authorityResolutionId.isNotBlank()) { "authorityResolutionId must not be blank" }

        require(
            provenance.claimId.status == MipFieldStatus.PRESENT &&
                provenance.claimId.value == claim.claimId
        ) {
            "MemoryCandidate provenance.claimId must be PRESENT and match claim.claimId"
        }

        authorityClass.requirePresentNonBlankIfPresent("authorityClass")
        contradictedMemoryRef.requirePresentNonBlankIfPresent("contradictedMemoryRef")
        authorityResolutionConfidence.requireNormalizedFiniteIfPresent("authorityResolutionConfidence")
        sourceReliability.requireNormalizedFiniteIfPresent("sourceReliability")

        requireOpaqueMemoryRefs("candidateMemoryRefs", candidateMemoryRefs)

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

        requireReasonCodes("reasonCodes", reasonCodes)

        when (status) {
            MemoryCandidateStatus.PROPOSED -> {
                require(authorityClass.status == MipFieldStatus.PRESENT) {
                    "PROPOSED MemoryCandidate requires authorityClass=PRESENT"
                }
                require(authorityResolutionConfidence.status == MipFieldStatus.PRESENT) {
                    "PROPOSED MemoryCandidate requires authorityResolutionConfidence=PRESENT"
                }
                require(
                    contradictedMemoryRef.status in setOf(
                        MipFieldStatus.PRESENT,
                        MipFieldStatus.NOT_APPLICABLE,
                    )
                ) {
                    "PROPOSED MemoryCandidate requires contradiction assessment PRESENT or NOT_APPLICABLE"
                }
                require(memoryKind.status == MipFieldStatus.PRESENT) {
                    "PROPOSED MemoryCandidate requires memoryKind=PRESENT"
                }
            }
            MemoryCandidateStatus.HOLD,
            MemoryCandidateStatus.REJECTED -> require(reasonCodes.isNotEmpty()) {
                "$status MemoryCandidate requires at least one reasonCode"
            }
        }
    }

    fun requireBinding(
        expectedClaimId: String,
        expectedContextSnapshotId: String,
        expectedAuthorityResolutionId: String,
    ): MemoryCandidate {
        require(expectedClaimId.isNotBlank()) { "expectedClaimId must not be blank" }
        require(expectedContextSnapshotId.isNotBlank()) { "expectedContextSnapshotId must not be blank" }
        require(expectedAuthorityResolutionId.isNotBlank()) { "expectedAuthorityResolutionId must not be blank" }

        require(claim.claimId == expectedClaimId) {
            "MemoryCandidate claim binding mismatch: expected=$expectedClaimId, actual=${claim.claimId}"
        }
        require(contextSnapshotId == expectedContextSnapshotId) {
            "MemoryCandidate context binding mismatch: expected=$expectedContextSnapshotId, actual=$contextSnapshotId"
        }
        require(authorityResolutionId == expectedAuthorityResolutionId) {
            "MemoryCandidate Authority binding mismatch: expected=$expectedAuthorityResolutionId, actual=$authorityResolutionId"
        }

        return this
    }
}

private fun MipField<String>.requirePresentNonBlankIfPresent(name: String) {
    if (status == MipFieldStatus.PRESENT) {
        require(!value.isNullOrBlank()) { "$name PRESENT value must not be blank" }
    }
}

private fun MipField<Double>.requireNormalizedFiniteIfPresent(name: String) {
    if (status == MipFieldStatus.PRESENT) {
        val number = requireNotNull(value)
        require(number.isFinite() && number in 0.0..1.0) {
            "$name PRESENT value must be finite in [0,1]"
        }
    }
}

private fun requireOpaqueMemoryRefs(name: String, values: List<String>) {
    require(values.all { it.isNotBlank() }) { "$name must not contain blank references" }
    require(values.distinct().size == values.size) { "$name must not contain duplicate references" }
}

private fun requireReasonCodes(name: String, values: List<String>) {
    require(values.all { it.isNotBlank() }) { "$name must not contain blank values" }
    require(values.distinct().size == values.size) { "$name must not contain duplicates" }
}
