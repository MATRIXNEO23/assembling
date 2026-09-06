#!/usr/bin/env bash
set -euo pipefail

TEST_FILE="src/test/kotlin/matrix/assembling/mip/TemporaryMipEvidenceWireBindingTest.kt"

cleanup() {
  rm -f "$TEST_FILE"
}
trap cleanup EXIT

mkdir -p "$(dirname "$TEST_FILE")"

cat > "$TEST_FILE" <<'KOTLIN'
package matrix.assembling.mip

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TemporaryMipEvidenceWireBindingTest {

    @Test
    fun retrievalQueryRoundTripPreservesBindingsExactly() {
        val query = RetrievalQuery(
            queryId = "rq-c0",
            purpose = RetrievalPurpose.CHECK_CONTRADICTION,
            agentId = "luna",
            claimId = MipField.present("c0"),
            subjectRefs = emptyList(),
            entityRefs = emptyList(),
            predicates = listOf("matrix.location.live_at"),
            temporalConstraint = MipField.notApplicable(),
            relationshipTarget = MipField.notApplicable(),
            goalRefs = emptyList(),
            includeHistorical = true,
            includeSuperseded = false,
            maxCandidates = 20,
            maxSelected = 5,
            contextSnapshotId = "ctx-1",
        )

        val encoded =
            MipEvidenceWire.retrievalQueryToWire(query)

        val decoded =
            MipEvidenceWire.retrievalQueryFromWire(encoded)

        assertEquals(query, decoded)
        assertEquals(MipFieldStatus.PRESENT, decoded.claimId.status)
        assertEquals("c0", decoded.claimId.value)
        assertEquals("ctx-1", decoded.contextSnapshotId)
    }

    @Test
    fun legacyRetrievalQueryWithoutClaimIdBecomesUnresolved() {
        val current = RetrievalQuery(
            queryId = "rq-legacy",
            purpose = RetrievalPurpose.ENRICH_TURN,
            agentId = "luna",
            claimId = MipField.present("c0"),
            maxCandidates = 10,
            maxSelected = 5,
            contextSnapshotId = "ctx-1",
        )

        val legacyPayload =
            MipEvidenceWire.retrievalQueryToWire(current)
                .toMutableMap()
                .apply {
                    remove("claimId")
                }

        val decoded =
            MipEvidenceWire.retrievalQueryFromWire(
                legacyPayload
            )

        assertEquals(
            MipFieldStatus.UNRESOLVED,
            decoded.claimId.status,
        )
        assertEquals(null, decoded.claimId.value)
        assertEquals("ctx-1", decoded.contextSnapshotId)
    }

    @Test
    fun retrievalResultRoundTripPreservesBindingsExactly() {
        val result = RetrievalResult(
            queryId = "rq-c0",
            status = RetrievalStatus.MATCHED,
            claimId = MipField.present("c0"),
            contextSnapshotId = MipField.present("ctx-1"),
            candidateRefs = listOf(
                "memory-1",
                "memory-2",
            ),
            selectedRefs = listOf(
                "memory-1",
            ),
            scores = listOf(
                RetrievalScore(
                    ref = "memory-1",
                    retrievalRelevance = 0.93,
                ),
                RetrievalScore(
                    ref = "memory-2",
                    retrievalRelevance = 0.61,
                ),
            ),
            reasonCodes = listOf(
                "RETRIEVAL.MATCHED",
            ),
            indexVersion = MipField.present(
                "memory-index-v8"
            ),
        )

        val encoded =
            MipEvidenceWire.retrievalResultToWire(result)

        val decoded =
            MipEvidenceWire.retrievalResultFromWire(encoded)

        assertEquals(result, decoded)
        assertEquals(MipFieldStatus.PRESENT, decoded.claimId.status)
        assertEquals("c0", decoded.claimId.value)
        assertEquals(
            MipFieldStatus.PRESENT,
            decoded.contextSnapshotId.status,
        )
        assertEquals(
            "ctx-1",
            decoded.contextSnapshotId.value,
        )
    }

    @Test
    fun legacyRetrievalResultWithoutBindingsBecomesUnresolved() {
        val current = RetrievalResult(
            queryId = "rq-legacy",
            status = RetrievalStatus.NO_MATCH,
            claimId = MipField.present("c0"),
            contextSnapshotId = MipField.present("ctx-1"),
        )

        val legacyPayload =
            MipEvidenceWire.retrievalResultToWire(current)
                .toMutableMap()
                .apply {
                    remove("claimId")
                    remove("contextSnapshotId")
                }

        val decoded =
            MipEvidenceWire.retrievalResultFromWire(
                legacyPayload
            )

        assertEquals(
            MipFieldStatus.UNRESOLVED,
            decoded.claimId.status,
        )
        assertEquals(null, decoded.claimId.value)

        assertEquals(
            MipFieldStatus.UNRESOLVED,
            decoded.contextSnapshotId.status,
        )
        assertEquals(
            null,
            decoded.contextSnapshotId.value,
        )
    }

    @Test
    fun malformedPresentBindingFailsClosed() {
        val result = RetrievalResult(
            queryId = "rq-c0",
            status = RetrievalStatus.NO_MATCH,
            claimId = MipField.present("c0"),
            contextSnapshotId = MipField.present("ctx-1"),
        )

        val malformedPayload =
            MipEvidenceWire.retrievalResultToWire(result)
                .toMutableMap()
                .apply {
                    this["claimId"] = mapOf(
                        "status" to "PRESENT",
                        "value" to null,
                    )
                }

        assertFailsWith<MipContractException> {
            MipEvidenceWire.retrievalResultFromWire(
                malformedPayload
            )
        }
    }
}
KOTLIN

gradle test \
  --tests "matrix.assembling.mip.TemporaryMipEvidenceWireBindingTest"
