#!/usr/bin/env bash
set -euo pipefail

TEST_FILE="src/test/kotlin/matrix/assembling/mip/TemporaryMipMemoryCandidateTest.kt"

cleanup() {
  rm -f "$TEST_FILE"
}
trap cleanup EXIT

mkdir -p "$(dirname "$TEST_FILE")"

cat > "$TEST_FILE" <<'KOTLIN'
package matrix.assembling.mip

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TemporaryMipMemoryCandidateTest {

    private val now = Instant.parse("2026-09-07T00:00:00Z")

    @Test
    fun proposedCandidatePreservesBindingsAndHasNoDurableWriteFields() {
        val candidate = proposedCandidate()

        assertEquals("candidate-c0", candidate.candidateId)
        assertEquals("c0", candidate.claim.claimId)
        assertEquals("ctx-1", candidate.contextSnapshotId)
        assertEquals("authority-c0", candidate.authorityResolutionId)
        assertEquals(MemoryKind.SEMANTIC, candidate.memoryKind.value)

        assertEquals(
            candidate,
            candidate.requireBinding("c0", "ctx-1", "authority-c0"),
        )

        val propertyNames = MemoryCandidate::class.java.declaredFields.map { it.name }.toSet()
        check("memoryId" !in propertyNames)
        check("memoryIds" !in propertyNames)
        check("stableWrite" !in propertyNames)
    }

    @Test
    fun mismatchedBindingFailsClosed() {
        val candidate = proposedCandidate()

        assertFailsWith<IllegalArgumentException> {
            candidate.requireBinding("c1", "ctx-1", "authority-c0")
        }
        assertFailsWith<IllegalArgumentException> {
            candidate.requireBinding("c0", "ctx-2", "authority-c0")
        }
        assertFailsWith<IllegalArgumentException> {
            candidate.requireBinding("c0", "ctx-1", "authority-c1")
        }
    }

    @Test
    fun proposedCandidateRequiresResolvedAuthorityAndMemoryKind() {
        assertFailsWith<IllegalArgumentException> {
            proposedCandidate(authorityClass = MipField.unresolved())
        }
        assertFailsWith<IllegalArgumentException> {
            proposedCandidate(memoryKind = MipField.unknown())
        }
        assertFailsWith<IllegalArgumentException> {
            proposedCandidate(contradictedMemoryRef = MipField.unresolved())
        }
    }

    @Test
    fun contradictionReferenceMustBelongToCandidateRefs() {
        assertFailsWith<IllegalArgumentException> {
            proposedCandidate(
                contradictedMemoryRef = MipField.present("memory-9"),
                candidateMemoryRefs = listOf("memory-1"),
            )
        }

        val valid = proposedCandidate(
            contradictedMemoryRef = MipField.present("memory-1"),
            candidateMemoryRefs = listOf("memory-1"),
        )

        assertEquals("memory-1", valid.contradictedMemoryRef.value)
    }

    @Test
    fun holdCandidateMayRemainUnresolvedButNeedsReason() {
        val hold = MemoryCandidate(
            candidateId = "candidate-hold",
            claim = claim(),
            contextSnapshotId = "ctx-1",
            authorityResolutionId = "authority-hold",
            authorityClass = MipField.unresolved(),
            authorityResolutionConfidence = MipField.unresolved(),
            sourceReliability = MipField.unknown(),
            contradictedMemoryRef = MipField.unresolved(),
            memoryKind = MipField.unknown(),
            status = MemoryCandidateStatus.HOLD,
            reasonCodes = listOf("MEMORY_CANDIDATE.AUTHORITY_UNRESOLVED"),
            provenance = provenance(),
            createdAt = now,
        )

        assertEquals(MemoryCandidateStatus.HOLD, hold.status)
        assertEquals(MipFieldStatus.UNRESOLVED, hold.authorityClass.status)
    }

    private fun proposedCandidate(
        authorityClass: MipField<String> = MipField.present("OBSERVATION"),
        memoryKind: MipField<MemoryKind> = MipField.present(MemoryKind.SEMANTIC),
        contradictedMemoryRef: MipField<String> = MipField.notApplicable(),
        candidateMemoryRefs: List<String> = emptyList(),
    ): MemoryCandidate = MemoryCandidate(
        candidateId = "candidate-c0",
        claim = claim(),
        contextSnapshotId = "ctx-1",
        authorityResolutionId = "authority-c0",
        authorityClass = authorityClass,
        authorityResolutionConfidence = MipField.present(0.95),
        sourceReliability = MipField.unknown(),
        contradictedMemoryRef = contradictedMemoryRef,
        candidateMemoryRefs = candidateMemoryRefs,
        memoryKind = memoryKind,
        retentionTier = MipField.present(MemoryRetentionTier.NORMAL),
        status = MemoryCandidateStatus.PROPOSED,
        reasonCodes = listOf("MEMORY_CANDIDATE.PROPOSED"),
        provenance = provenance(),
        createdAt = now,
    )

    private fun claim(): MipClaimV1 = MipClaimV1(
        claimId = "c0",
        speaker = resolved("alberto"),
        observer = resolved("luna"),
        source = resolved("alberto"),
        subject = resolved("alberto"),
        target = MipEntityRef(resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE),
        owner = resolved("alberto"),
        perspective = resolved("alberto"),
        predicate = "matrix.location.live_at",
        objectValue = MipField.present("Padova"),
        dialogueAct = MipField.present("ASSERT"),
        polarity = "POSITIVE",
        temporalRelation = "PRESENT",
        sourceType = MipField.present("USER_ASSERTION"),
        interpretationConfidence = MipField.present(0.97),
        confidenceByField = emptyMap(),
        sourceSpans = emptyMap(),
        epistemicClass = MipField.unknown(),
    )

    private fun resolved(id: String): MipEntityRef =
        MipEntityRef(
            entityId = id,
            resolutionStatus = MipEntityResolutionStatus.RESOLVED,
        )

    private fun provenance(): ProvenanceRef =
        ProvenanceRef(
            originId = "obs-1",
            originType = "OBSERVATION",
            originAgent = MipField.present("luna"),
            generatedBy = ModuleId.BELIEF_AUTHORITY,
            derivedFromIds = listOf("c0", "authority-c0"),
            claimId = MipField.present("c0"),
            createdAt = now,
        )
}
KOTLIN

gradle test --tests "matrix.assembling.mip.TemporaryMipMemoryCandidateTest"
