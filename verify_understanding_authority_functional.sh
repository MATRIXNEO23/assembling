#!/usr/bin/env bash
set -u

BASE_TEST="matrix.assembling.authority.runtime.CanonicalUnderstandingV3AuthorityPortTest"
TEMP_FILE="src/test/kotlin/matrix/assembling/authority/runtime/TemporaryRetrievalBindingFunctionalTest.kt"
LOG_FILE="build/reports/functional/understanding_authority_functional.log"

cleanup() {
  rm -f "$TEMP_FILE"
}
trap cleanup EXIT

mkdir -p "$(dirname "$TEMP_FILE")" "$(dirname "$LOG_FILE")"

echo "============================================================"
echo "MATRIX FUNCTIONAL AUDIT - UNDERSTANDING V3 -> AUTHORITY"
echo "============================================================"
echo "NOTE: this verifies the real structured V3 -> Authority path."
echo "NOTE: it does NOT prove raw-text NLU comprehension."
echo

echo "[1/2] Existing real structured-path checks"
gradle test \
  --tests "$BASE_TEST.real V3 report claim reaches canonical Authority without legacy TypedClaim" \
  --tests "$BASE_TEST.projection consumes structured V3 roles and object span without free text reparsing" \
  --tests "$BASE_TEST.ambiguous V3 interpretation is held instead of promoted"
BASE_STATUS=$?

if [ "$BASE_STATUS" -ne 0 ]; then
  echo
  echo "FUNCTIONAL VERDICT: BASE STRUCTURED PATH NOT VERIFIED"
  echo "The existing single-claim/projection/ambiguity checks failed."
  exit "$BASE_STATUS"
fi

cat > "$TEMP_FILE" <<'KOTLIN'
package matrix.assembling.authority.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.authority.AuthorityCandidateEvidencePort
import matrix.assembling.authority.AuthorityResolveRequest
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolver
import matrix.assembling.authority.DeterministicAuthorityResolver
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class TemporaryRetrievalBindingFunctionalTest {

    @Test
    fun `multi claim bound retrieval reaches matching Authority request`() {
        val fixture = CanonicalUnderstandingV3AuthorityPortTest()

        val observationMethod = fixture.javaClass.getDeclaredMethod("observation")
        observationMethod.isAccessible = true
        val base = observationMethod.invoke(fixture) as MipUnderstandingV3Observation

        val provenanceMethod = fixture.javaClass.getDeclaredMethod("claimProvenance", String::class.java)
        provenanceMethod.isAccessible = true
        val c1Provenance = provenanceMethod.invoke(fixture, "c1") as ProvenanceRef

        val first = base.claims.single()
        val second = first.copy(
            claimId = "c1",
            provenance = c1Provenance,
        )
        val multi = base.copy(claims = listOf(first, second))

        val retrieval = listOf(
            RetrievalResult(
                queryId = "q:c0",
                status = RetrievalStatus.NO_MATCH,
                claimId = MipField.present("c0"),
                contextSnapshotId = MipField.present("ctx-1"),
            ),
            RetrievalResult(
                queryId = "q:c1",
                status = RetrievalStatus.NO_MATCH,
                claimId = MipField.present("c1"),
                contextSnapshotId = MipField.present("ctx-1"),
            ),
        )

        val frameMethod = fixture.javaClass.getDeclaredMethod(
            "frame",
            MipUnderstandingV3Observation::class.java,
            List::class.java,
        )
        frameMethod.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val frame = frameMethod.invoke(fixture, multi, retrieval) as MatrixTurnFrame

        val recording = RecordingResolver(
            DeterministicAuthorityResolver(
                AuthorityCandidateEvidencePort { _, _ ->
                    error("NO_MATCH must not read candidate evidence")
                }
            )
        )
        val port = CanonicalUnderstandingV3AuthorityPort(recording)
        port.resolve(frame)

        assertEquals(2, recording.requests.size)

        recording.requests.forEach { request ->
            assertEquals(
                MipFieldStatus.PRESENT,
                request.retrievalResult.status,
                "claim=${request.claim.claimId}: bound retrieval was not delivered to Authority; actual=${request.retrievalResult}",
            )
            assertEquals(
                request.claim.claimId,
                request.retrievalResult.value?.claimId?.value,
                "claim=${request.claim.claimId}: Authority received a retrieval result bound to another claim",
            )
            assertEquals(
                "ctx-1",
                request.retrievalResult.value?.contextSnapshotId?.value,
                "claim=${request.claim.claimId}: retrieval context binding changed before Authority",
            )
        }
    }

    private class RecordingResolver(
        private val delegate: AuthorityResolver,
    ) : AuthorityResolver {
        val requests = mutableListOf<AuthorityResolveRequest>()

        override fun resolve(request: AuthorityResolveRequest): AuthorityResolution {
            requests += request
            return delegate.resolve(request)
        }
    }
}
KOTLIN

echo
echo "[2/2] Multi-claim explicit retrieval-binding check"
set +e
gradle test \
  --tests "matrix.assembling.authority.runtime.TemporaryRetrievalBindingFunctionalTest" \
  --rerun-tasks 2>&1 | tee "$LOG_FILE"
BINDING_STATUS=${PIPESTATUS[0]}
set -e

echo
if [ "$BINDING_STATUS" -eq 0 ]; then
  echo "============================================================"
  echo "FUNCTIONAL VERDICT"
  echo "SINGLE-CLAIM STRUCTURED PATH: VERIFIED BY EXISTING REAL TESTS"
  echo "MULTI-CLAIM RETRIEVAL BINDING: WORKS"
  echo "============================================================"
  exit 0
fi

echo "============================================================"
echo "FUNCTIONAL VERDICT"
echo "SINGLE-CLAIM STRUCTURED PATH: VERIFIED BY EXISTING REAL TESTS"
echo "MULTI-CLAIM RETRIEVAL BINDING: DEFECT CONFIRMED"
echo "Expected: each claim receives its explicitly bound RetrievalResult."
echo "Actual failure details are above and in: $LOG_FILE"
echo "No production code was changed by this audit."
echo "============================================================"
exit 0
