package matrix.assembling.mip

import java.math.BigInteger
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolutionConfidence
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.EpistemicClass
import matrix.assembling.authority.MemoryRef

class MipAuthorityCompatibilityTest {

    @Test
    fun completeKnownAbsenceProjectsToNativeNullOnly() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.COMPLETE,
            contradiction = MipField.notApplicable(),
        )

        assertNull(resolution.toKotlinMemoryContradictionProjection().contradictedMemoryId)
        assertNull(resolution.toPythonContradictionProjection().contradicts_memory_id)
    }

    @Test
    fun completeCanonicalDecimalIdentityProjectsExactlyToKotlinAndPython() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.COMPLETE,
            contradiction = MipField.present(MemoryRef("42")),
            candidates = listOf(MemoryRef("42")),
        )

        assertEquals(42L, resolution.toKotlinMemoryContradictionProjection().contradictedMemoryId)
        assertEquals(BigInteger("42"), resolution.toPythonContradictionProjection().contradicts_memory_id)
    }

    @Test
    fun opaqueNonNumericMemoryRefFailsClosedForNumericLegacySeams() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.COMPLETE,
            contradiction = MipField.present(MemoryRef("memory:alpha:v3")),
            candidates = listOf(MemoryRef("memory:alpha:v3")),
        )

        assertFailsWith<MipContractException> {
            resolution.toKotlinMemoryContradictionProjection()
        }
        assertFailsWith<MipContractException> {
            resolution.toPythonContradictionProjection()
        }
    }

    @Test
    fun arbitraryPythonIntegerMayProjectWhileKotlinLongOverflowFailsClosed() {
        val raw = "9223372036854775808"
        val resolution = resolution(
            status = AuthorityResolutionStatus.COMPLETE,
            contradiction = MipField.present(MemoryRef(raw)),
            candidates = listOf(MemoryRef(raw)),
        )

        assertFailsWith<MipContractException> {
            resolution.toKotlinMemoryContradictionProjection()
        }
        assertEquals(BigInteger(raw), resolution.toPythonContradictionProjection().contradicts_memory_id)
    }

    @Test
    fun nonCanonicalDecimalIdentityCannotBeNormalizedSilently() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.COMPLETE,
            contradiction = MipField.present(MemoryRef("001")),
            candidates = listOf(MemoryRef("001")),
        )

        assertFailsWith<MipContractException> {
            resolution.toKotlinMemoryContradictionProjection()
        }
        assertFailsWith<MipContractException> {
            resolution.toPythonContradictionProjection()
        }
    }

    @Test
    fun partialResolutionCannotMasqueradeAsMemoryAdmissionInput() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.PARTIAL,
            contradiction = MipField.unresolved(),
        )

        assertFailsWith<MipContractException> {
            resolution.toKotlinMemoryContradictionProjection()
        }
        assertFailsWith<MipContractException> {
            resolution.toPythonContradictionProjection()
        }
    }

    @Test
    fun holdResolutionCannotMasqueradeAsMemoryAdmissionInput() {
        val resolution = resolution(
            status = AuthorityResolutionStatus.HOLD,
            contradiction = MipField.ambiguous(),
            candidates = listOf(MemoryRef("1"), MemoryRef("2")),
            ambiguityReasons = listOf("multiple contradiction targets"),
        )

        assertFailsWith<MipContractException> {
            resolution.toKotlinMemoryContradictionProjection()
        }
    }

    private fun resolution(
        status: AuthorityResolutionStatus,
        contradiction: MipField<MemoryRef>,
        candidates: List<MemoryRef> = emptyList(),
        ambiguityReasons: List<String> = emptyList(),
    ): AuthorityResolution = AuthorityResolution(
        resolutionId = "resolution:1",
        claimId = "claim:1",
        contextSnapshotId = "ctx:1",
        retrievalQueryId = MipField.present("query:1"),
        resolutionStatus = status,
        authority = MipField.present(EpistemicClass.REPORT),
        authorityResolutionConfidence = MipField.present(AuthorityResolutionConfidence(0.95)),
        sourceReliability = MipField.unavailable(),
        contradictedMemoryRef = contradiction,
        candidateMemoryRefs = candidates,
        ambiguityReasons = ambiguityReasons,
        reasonCodes = listOf(
            if (contradiction.status == MipFieldStatus.PRESENT) {
                "AUTHORITY.CONTRADICTION.IDENTIFIED"
            } else {
                "AUTHORITY.CONTRADICTION.NONE"
            }
        ),
        provenance = ProvenanceRef(
            originId = "authority:1",
            originType = "AUTHORITY_RESOLUTION",
            generatedBy = ModuleId.BELIEF_AUTHORITY,
            claimId = MipField.present("claim:1"),
            createdAt = Instant.parse("2026-09-05T09:00:00Z"),
        ),
    )
}
