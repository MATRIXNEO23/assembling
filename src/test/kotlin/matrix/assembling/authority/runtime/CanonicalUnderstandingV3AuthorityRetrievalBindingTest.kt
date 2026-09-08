package matrix.assembling.authority.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.authority.AuthorityCandidateEvidencePort
import matrix.assembling.authority.AuthorityResolveRequest
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.AuthorityResolver
import matrix.assembling.authority.DeterministicAuthorityResolver
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class CanonicalUnderstandingV3AuthorityRetrievalBindingTest {

    @Test
    fun `multi claim retrieval is delivered by explicit claim and context binding`() {
        val fixture = CanonicalUnderstandingV3AuthorityPortTest()
        val base = fixtureObservation(fixture)
        val first = base.claims.single()
        val second = first.copy(
            claimId = "c1",
            provenance = fixtureClaimProvenance(fixture, "c1"),
        )
        val multi = base.copy(claims = listOf(first, second))

        val retrieval = listOf(
            boundNoMatch("q:c0", "c0"),
            boundNoMatch("q:c1", "c1"),
        )
        val frame = fixtureFrame(fixture, multi, retrieval)

        val recording = RecordingResolver(
            DeterministicAuthorityResolver(
                AuthorityCandidateEvidencePort { _, _ ->
                    error("NO_MATCH must not read candidate evidence")
                }
            )
        )

        val result = CanonicalUnderstandingV3AuthorityPort(recording).resolve(frame)

        assertEquals(2, recording.requests.size)
        recording.requests.forEach { request ->
            assertEquals(MipFieldStatus.PRESENT, request.retrievalResult.status)
            assertEquals(request.claim.claimId, request.retrievalResult.value?.claimId?.value)
            assertEquals("ctx-1", request.retrievalResult.value?.contextSnapshotId?.value)
        }

        result.requireCanonicalAuthorityResolutions().forEach { resolution ->
            assertEquals(AuthorityResolutionStatus.COMPLETE, resolution.resolutionStatus)
            assertEquals(MipFieldStatus.NOT_APPLICABLE, resolution.contradictedMemoryRef.status)
        }
    }

    @Test
    fun `multi claim retrieval with wrong context fails closed`() {
        val fixture = CanonicalUnderstandingV3AuthorityPortTest()
        val base = fixtureObservation(fixture)
        val first = base.claims.single()
        val second = first.copy(
            claimId = "c1",
            provenance = fixtureClaimProvenance(fixture, "c1"),
        )
        val multi = base.copy(claims = listOf(first, second))

        val retrieval = listOf(
            boundNoMatch("q:c0", "c0"),
            RetrievalResult(
                queryId = "q:c1",
                status = RetrievalStatus.NO_MATCH,
                claimId = MipField.present("c1"),
                contextSnapshotId = MipField.present("ctx-wrong"),
            ),
        )
        val frame = fixtureFrame(fixture, multi, retrieval)

        val recording = RecordingResolver(
            DeterministicAuthorityResolver(
                AuthorityCandidateEvidencePort { _, _ ->
                    error("NO_MATCH must not read candidate evidence")
                }
            )
        )

        val result = CanonicalUnderstandingV3AuthorityPort(recording).resolve(frame)

        val c0Request = recording.requests.single { it.claim.claimId == "c0" }
        val c1Request = recording.requests.single { it.claim.claimId == "c1" }
        assertEquals(MipFieldStatus.PRESENT, c0Request.retrievalResult.status)
        assertEquals(MipFieldStatus.ERROR, c1Request.retrievalResult.status)

        val c1Resolution = result.requireCanonicalAuthorityForClaim("c1")
        assertEquals(AuthorityResolutionStatus.ERROR, c1Resolution.resolutionStatus)
        assertEquals(MipFieldStatus.ERROR, c1Resolution.contradictedMemoryRef.status)
    }

    private fun boundNoMatch(queryId: String, claimId: String) =
        RetrievalResult(
            queryId = queryId,
            status = RetrievalStatus.NO_MATCH,
            claimId = MipField.present(claimId),
            contextSnapshotId = MipField.present("ctx-1"),
        )

    private fun fixtureObservation(
        fixture: CanonicalUnderstandingV3AuthorityPortTest,
    ): MipUnderstandingV3Observation {
        val method = fixture.javaClass.getDeclaredMethod("observation")
        method.isAccessible = true
        return method.invoke(fixture) as MipUnderstandingV3Observation
    }

    private fun fixtureClaimProvenance(
        fixture: CanonicalUnderstandingV3AuthorityPortTest,
        claimId: String,
    ): ProvenanceRef {
        val method = fixture.javaClass.getDeclaredMethod("claimProvenance", String::class.java)
        method.isAccessible = true
        return method.invoke(fixture, claimId) as ProvenanceRef
    }

    private fun fixtureFrame(
        fixture: CanonicalUnderstandingV3AuthorityPortTest,
        observation: MipUnderstandingV3Observation,
        retrieval: List<RetrievalResult>,
    ): MatrixTurnFrame {
        val method = fixture.javaClass.getDeclaredMethod(
            "frame",
            MipUnderstandingV3Observation::class.java,
            List::class.java,
        )
        method.isAccessible = true
        return method.invoke(fixture, observation, retrieval) as MatrixTurnFrame
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
