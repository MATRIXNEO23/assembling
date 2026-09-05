package matrix.assembling

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import matrix.assembling.authority.AuthorityReasonCode
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolutionConfidence
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.EpistemicClass
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.ContextDomainAvailability
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class MatrixTurnFrameCanonicalSlotsTest {

    @Test
    fun `legacy minimal constructor keeps canonical slots explicitly unavailable`() {
        val frame = minimalFrame()

        assertEquals(MipFieldStatus.UNAVAILABLE, frame.contextSnapshot.status)
        assertEquals(MipFieldStatus.UNAVAILABLE, frame.retrievalResults.status)
        assertEquals(MipFieldStatus.UNAVAILABLE, frame.canonicalAuthorityResolutions.status)
        assertNull(frame.authorityDecision)
    }

    @Test
    fun `present context must match frame turn and session`() {
        assertFailsWith<IllegalArgumentException> {
            minimalFrame().copy(
                contextSnapshot = MipField.present(context(turnId = "other-turn")),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            minimalFrame().copy(
                contextSnapshot = MipField.present(context(sessionId = "other-session")),
            )
        }
    }

    @Test
    fun `retrieval outer NO_MATCH is rejected while inner NO_MATCH is preserved`() {
        val context = context()
        assertFailsWith<IllegalArgumentException> {
            minimalFrame().copy(
                contextSnapshot = MipField.present(context),
                retrievalResults = MipField.noMatch(),
            )
        }

        val noMatch = RetrievalResult(
            queryId = "query-1",
            status = RetrievalStatus.NO_MATCH,
        )
        val frame = minimalFrame().copy(
            contextSnapshot = MipField.present(context),
            retrievalResults = MipField.present(listOf(noMatch)),
        )

        assertEquals(MipFieldStatus.PRESENT, frame.retrievalResults.status)
        assertEquals(RetrievalStatus.NO_MATCH, frame.requireCanonicalRetrievalResults().single().status)
    }

    @Test
    fun `present retrieval requires present context and explicit result`() {
        val noMatch = RetrievalResult(queryId = "query-1", status = RetrievalStatus.NO_MATCH)

        assertFailsWith<IllegalArgumentException> {
            minimalFrame().copy(
                retrievalResults = MipField.present(listOf(noMatch)),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            minimalFrame().copy(
                contextSnapshot = MipField.present(context()),
                retrievalResults = MipField.present(emptyList()),
            )
        }
    }

    @Test
    fun `multi claim canonical Authority preserves one current resolution per claim`() {
        val claim0 = claim("claim-0", "alice")
        val claim1 = claim("claim-1", "bob")
        val snapshot = context()
        val resolution0 = resolution("resolution-0", claim0.claimId, snapshot.snapshotId)
        val resolution1 = resolution("resolution-1", claim1.claimId, snapshot.snapshotId)

        val frame = minimalFrame(claim0, claim1).copy(
            contextSnapshot = MipField.present(snapshot),
            canonicalAuthorityResolutions = MipField.present(listOf(resolution0, resolution1)),
        )

        assertEquals(2, frame.requireCanonicalAuthorityResolutions().size)
        assertEquals(resolution0, frame.requireCanonicalAuthorityForClaim("claim-0"))
        assertEquals(resolution1, frame.requireCanonicalAuthorityForClaim("claim-1"))
        assertNull(frame.authorityDecision)
    }

    @Test
    fun `canonical Authority must cover exactly current typed claims`() {
        val claim0 = claim("claim-0", "alice")
        val claim1 = claim("claim-1", "bob")
        val snapshot = context()

        assertFailsWith<IllegalArgumentException> {
            minimalFrame(claim0, claim1).copy(
                contextSnapshot = MipField.present(snapshot),
                canonicalAuthorityResolutions = MipField.present(
                    listOf(resolution("resolution-0", claim0.claimId, snapshot.snapshotId))
                ),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            minimalFrame(claim0).copy(
                contextSnapshot = MipField.present(snapshot),
                canonicalAuthorityResolutions = MipField.present(
                    listOf(resolution("resolution-other", "unknown-claim", snapshot.snapshotId))
                ),
            )
        }
    }

    @Test
    fun `canonical Authority resolution must reference current snapshot`() {
        val claim = claim("claim-0", "alice")
        val snapshot = context()

        assertFailsWith<IllegalArgumentException> {
            minimalFrame(claim).copy(
                contextSnapshot = MipField.present(snapshot),
                canonicalAuthorityResolutions = MipField.present(
                    listOf(resolution("resolution-0", claim.claimId, "old-snapshot"))
                ),
            )
        }
    }

    @Test
    fun `legacy Authority and canonical Authority remain independent`() {
        val claim = claim("claim-0", "alice")
        val snapshot = context()
        val canonical = resolution("resolution-0", claim.claimId, snapshot.snapshotId)
        val legacy = AuthorityDecision(
            accepted = true,
            ownerResolved = true,
            sourceType = "USER_ASSERTION",
            conflictStatus = "NONE",
            reason = "legacy compatibility",
        )

        val legacyOnly = minimalFrame(claim).copy(authorityDecision = legacy)
        assertEquals(legacy, legacyOnly.authorityDecision)
        assertEquals(MipFieldStatus.UNAVAILABLE, legacyOnly.canonicalAuthorityResolutions.status)

        val canonicalOnly = minimalFrame(claim).copy(
            contextSnapshot = MipField.present(snapshot),
            canonicalAuthorityResolutions = MipField.present(listOf(canonical)),
        )
        assertNull(canonicalOnly.authorityDecision)
        assertEquals(canonical, canonicalOnly.requireCanonicalAuthorityForClaim(claim.claimId))
    }

    @Test
    fun `copy preserves canonical MIP slots and statuses`() {
        val claim = claim("claim-0", "alice")
        val snapshot = context()
        val retrieval = RetrievalResult(queryId = "query-1", status = RetrievalStatus.NO_MATCH)
        val authority = resolution("resolution-0", claim.claimId, snapshot.snapshotId)
        val frame = minimalFrame(claim).copy(
            contextSnapshot = MipField.present(snapshot),
            retrievalResults = MipField.present(listOf(retrieval)),
            canonicalAuthorityResolutions = MipField.present(listOf(authority)),
        )

        val copied = frame.copy(diagnostics = frame.diagnostics.add("copy-test"))

        assertEquals(frame.contextSnapshot, copied.contextSnapshot)
        assertEquals(frame.retrievalResults, copied.retrievalResults)
        assertEquals(frame.canonicalAuthorityResolutions, copied.canonicalAuthorityResolutions)
        assertEquals(authority, copied.requireCanonicalAuthorityForClaim(claim.claimId))
    }

    private fun minimalFrame(vararg claims: TypedClaim) = MatrixTurnFrame(
        turnId = "turn-1",
        sessionId = "session-1",
        input = UserMessage(
            text = "fixture",
            speakerId = "user",
            observerId = "luna",
            timestampMillis = 0L,
        ),
        typedClaims = claims.toList(),
    )

    private fun claim(claimId: String, subject: String) = TypedClaim(
        claimId = claimId,
        ownerId = subject,
        subject = subject,
        predicate = "residence.place",
        objectValue = "Rome",
        target = null,
        polarity = "POSITIVE",
        temporalRelation = "CURRENT",
        sourceType = "USER_ASSERTION",
        confidence = mapOf("overall" to 0.95),
        perspective = subject,
    )

    private fun context(
        turnId: String = "turn-1",
        sessionId: String = "session-1",
    ) = MatrixContextSnapshot(
        snapshotId = "snapshot-1",
        turnId = turnId,
        sessionId = sessionId,
        agentId = "luna",
        createdAt = TEST_TIME,
        entries = emptyList(),
        domainAvailability = ContextDomain.entries.map { domain ->
            ContextDomainAvailability(
                domain = domain,
                availability = if (domain == ContextDomain.LINGUISTIC) {
                    DomainAvailability.AVAILABLE
                } else {
                    DomainAvailability.NOT_WIRED
                },
            )
        },
    )

    private fun resolution(
        resolutionId: String,
        claimId: String,
        snapshotId: String,
    ) = AuthorityResolution(
        resolutionId = resolutionId,
        claimId = claimId,
        contextSnapshotId = snapshotId,
        resolutionStatus = AuthorityResolutionStatus.COMPLETE,
        authority = MipField.present(EpistemicClass.REPORT),
        authorityResolutionConfidence = MipField.present(AuthorityResolutionConfidence(1.0)),
        sourceReliability = MipField.unavailable(),
        contradictedMemoryRef = MipField.notApplicable(),
        reasonCodes = listOf(
            AuthorityReasonCode.RESOLVED_REPORT,
            AuthorityReasonCode.CONTRADICTION_NONE,
        ),
        provenance = ProvenanceRef(
            originId = "authority-$resolutionId",
            originType = "AUTHORITY_RESOLUTION",
            generatedBy = ModuleId.BELIEF_AUTHORITY,
            claimId = MipField.present(claimId),
            createdAt = TEST_TIME,
        ),
    )

    private companion object {
        val TEST_TIME: Instant = Instant.parse("2026-09-05T10:00:00Z")
    }
}
