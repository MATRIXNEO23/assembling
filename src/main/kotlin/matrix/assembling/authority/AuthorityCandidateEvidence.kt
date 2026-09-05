package matrix.assembling.authority

import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ProvenanceRef

/**
 * Read-only normalized evidence projection used by AUTHORITY-1.0 contradiction comparison.
 *
 * This is deliberately NOT a MemoryRecord, persistence DTO, repository API, or admission result.
 * A Memory/context adapter may project durable data into this view, but Authority never owns or
 * mutates the underlying Memory state.
 */
data class AuthorityCandidateEvidence(
    val memoryRef: MemoryRef,
    /** Memory lifecycle/validity as observed in the supplied snapshot. Only PRESENT("VALID") may contradict. */
    val validity: MipField<String>,
    val subject: MipEntityRef,
    val predicate: String,
    val objectValue: MipField<String>,
    val target: MipEntityRef,
    val owner: MipEntityRef,
    val perspective: MipEntityRef,
    val source: MipEntityRef,
    val polarity: MipField<String>,
    val temporalRelation: MipField<String>,
    /** Optional stable time/event identity used when broad PAST/FUTURE/reference labels are insufficient. */
    val temporalReferenceKey: MipField<String> = MipField.notApplicable(),
    val provenance: ProvenanceRef,
) {
    init {
        require(predicate.isNotBlank()) { "candidate predicate must not be blank" }
        validity.requirePresentNonBlankIfPresent("candidate validity")
        objectValue.requirePresentNonBlankIfPresent("candidate objectValue")
        polarity.requirePresentNonBlankIfPresent("candidate polarity")
        temporalRelation.requirePresentNonBlankIfPresent("candidate temporalRelation")
        temporalReferenceKey.requirePresentNonBlankIfPresent("candidate temporalReferenceKey")

        require(subject.resolutionStatus != MipEntityResolutionStatus.NOT_APPLICABLE) {
            "candidate subject cannot be NOT_APPLICABLE"
        }
    }
}

/**
 * Explicitly read-only candidate evidence boundary permitted by AUTHORITY-1.0 section 13.
 *
 * The interface intentionally exposes no save/update/delete/supersede/admission operation.
 */
fun interface AuthorityCandidateEvidencePort {
    fun read(memoryRef: MemoryRef, contextSnapshot: MatrixContextSnapshot): MipField<AuthorityCandidateEvidence>
}

private fun MipField<String>.requirePresentNonBlankIfPresent(name: String) {
    if (status == MipFieldStatus.PRESENT) {
        require(!value.isNullOrBlank()) { "$name PRESENT value must not be blank" }
    }
}
