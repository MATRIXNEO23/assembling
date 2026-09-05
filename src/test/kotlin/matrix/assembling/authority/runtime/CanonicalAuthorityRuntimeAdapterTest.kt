package matrix.assembling.authority.runtime

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.AuthorityResolverPort
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.TypedClaim
import matrix.assembling.UserMessage
import matrix.assembling.authority.AuthorityCandidateEvidencePort
import matrix.assembling.authority.AuthorityReasonCode
import matrix.assembling.authority.AuthorityResolveRequest
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.DeterministicAuthorityResolver
import matrix.assembling.authority.EpistemicClass
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.ContextDomainAvailability
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef

class CanonicalAuthorityRuntimeAdapterTest {

    @Test
    fun `canonical adapter preserves direct resolver output exactly`() {
        val resolver = resolverWithoutCandidateReads()
        val adapter = CanonicalAuthorityRuntimeAdapter(resolver)
        val claim = canonicalReportClaim()
        val context = context()
        val provenance = provenance(claim.claimId)
        val input = CanonicalAuthorityRuntimeInput(
            requestId = "req-1",
            claim = claim,
            contextSnapshot = context,
            retrievalResult = MipField.notApplicable(),
            provenance = provenance,
        )

        val direct = resolver.resolve(
            AuthorityResolveRequest(
                requestId = input.requestId,
                claim = input.claim,
                contextSnapshot = input.contextSnapshot,
                retrievalResult = input.retrievalResult,
                provenance = input.provenance,
            )
        )
        val throughAdapter = adapter.resolveCanonical(input)

        assertEquals(direct, throughAdapter)
        assertEquals(AuthorityResolutionStatus.COMPLETE, throughAdapter.resolutionStatus)
        assertEquals(EpistemicClass.REPORT, throughAdapter.authority.value)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, throughAdapter.contradictedMemoryRef.status)
        assertEquals(ModuleId.BELIEF_AUTHORITY, throughAdapter.provenance.generatedBy)
    }

    @Test
    fun `legacy user assertion preserves missing source and returns canonical HOLD instead of guessing`() {
        val adapter = CanonicalAuthorityRuntimeAdapter(resolverWithoutCandidateReads())
        val claim = legacyClaim(claimId = "claim-legacy", sourceType = "USER_ASSERTION")
        val turn = frame(claim)

        val outcome = adapter.resolveLegacyClaim(
            turn = turn,
            claim = claim,
            contextSnapshot = context(turn.turnId, turn.sessionId),
            provenance = provenance(claim.claimId),
        )

        val canonical = assertIs<LegacyAuthorityRuntimeOutcome.Canonical>(outcome)
        assertEquals(MipEntityResolutionStatus.UNKNOWN, canonical.canonicalClaim.source.resolutionStatus)
        assertTrue(LegacyAuthorityGap.SOURCE_IDENTITY_NOT_REPRESENTED in canonical.compatibilityGaps)
        assertTrue(LegacyAuthorityGap.DIALOGUE_ACT_NOT_REPRESENTED in canonical.compatibilityGaps)
        assertTrue(LegacyAuthorityGap.CLAIM_KIND_NOT_REPRESENTED in canonical.compatibilityGaps)
        assertEquals(AuthorityResolutionStatus.HOLD, canonical.resolution.resolutionStatus)
        assertTrue(AuthorityReasonCode.SOURCE_UNRESOLVED in canonical.resolution.reasonCodes)
        assertEquals(
            LegacyAuthorityDecisionProjectionStatus.UNREPRESENTABLE_WITHOUT_SEMANTIC_LOSS,
            canonical.legacyDecisionProjectionStatus,
        )
        assertNull(turn.authorityDecision)
    }

    @Test
    fun `trusted WORLD legacy evidence resolves only from independent WORLD provenance`() {
        val adapter = CanonicalAuthorityRuntimeAdapter(resolverWithoutCandidateReads())
        val claim = legacyClaim(
            claimId = "claim-world",
            sourceType = "WORLD_STATE",
            worldTruth = true,
        )
        val turn = frame(claim)

        val outcome = adapter.resolveLegacyClaim(
            turn = turn,
            claim = claim,
            contextSnapshot = context(turn.turnId, turn.sessionId),
            provenance = provenance(
                claimId = claim.claimId,
                generatedBy = ModuleId.WORLD,
                originType = "WORLD_STATE",
            ),
        )

        val canonical = assertIs<LegacyAuthorityRuntimeOutcome.Canonical>(outcome)
        assertEquals(AuthorityResolutionStatus.COMPLETE, canonical.resolution.resolutionStatus)
        assertEquals(EpistemicClass.WORLD_TRUTH, canonical.resolution.authority.value)
        assertTrue(LegacyAuthorityGap.SOURCE_IDENTITY_NOT_REPRESENTED in canonical.compatibilityGaps)
        assertNull(turn.authorityDecision)
    }

    @Test
    fun `legacy adapter blocks context turn mismatch before resolver`() {
        val adapter = CanonicalAuthorityRuntimeAdapter(resolverWithoutCandidateReads())
        val claim = legacyClaim("claim-context")
        val turn = frame(claim)

        val outcome = adapter.resolveLegacyClaim(
            turn = turn,
            claim = claim,
            contextSnapshot = context(turnId = "different-turn", sessionId = turn.sessionId),
            provenance = provenance(claim.claimId),
        )

        val blocked = assertIs<LegacyAuthorityRuntimeOutcome.Blocked>(outcome)
        assertEquals(
            listOf(CanonicalAuthorityRuntimeAdapter.REASON_CONTEXT_TURN_MISMATCH),
            blocked.reasonCodes,
        )
        assertNull(turn.authorityDecision)
    }

    @Test
    fun `legacy adapter blocks claim that is not explicitly present in frame`() {
        val adapter = CanonicalAuthorityRuntimeAdapter(resolverWithoutCandidateReads())
        val frameClaim = legacyClaim("claim-frame")
        val externalClaim = legacyClaim("claim-external")
        val turn = frame(frameClaim)

        val outcome = adapter.resolveLegacyClaim(
            turn = turn,
            claim = externalClaim,
            contextSnapshot = context(turn.turnId, turn.sessionId),
            provenance = provenance(externalClaim.claimId),
        )

        val blocked = assertIs<LegacyAuthorityRuntimeOutcome.Blocked>(outcome)
        assertTrue(CanonicalAuthorityRuntimeAdapter.REASON_CLAIM_NOT_IN_FRAME in blocked.reasonCodes)
        assertNull(turn.authorityDecision)
    }

    @Test
    fun `multi claim runtime requires explicit claim and never selects first implicitly`() {
        val adapter = CanonicalAuthorityRuntimeAdapter(resolverWithoutCandidateReads())
        val first = legacyClaim("claim-0", subject = "alice", owner = "alice", perspective = "alice")
        val second = legacyClaim("claim-1", subject = "bob", owner = "bob", perspective = "bob")
        val turn = frame(first, second)

        val outcome = adapter.resolveLegacyClaim(
            turn = turn,
            claim = second,
            contextSnapshot = context(turn.turnId, turn.sessionId),
            provenance = provenance(second.claimId),
        )

        val canonical = assertIs<LegacyAuthorityRuntimeOutcome.Canonical>(outcome)
        assertEquals("claim-1", canonical.canonicalClaim.claimId)
        assertEquals("bob", canonical.canonicalClaim.subject.entityId)
        assertFalse(canonical.canonicalClaim.claimId == first.claimId)
        assertNull(turn.authorityDecision)
    }

    @Test
    fun `adapter is not legacy orchestrator port and exposes no persistence mutation API`() {
        assertFalse(
            AuthorityResolverPort::class.java.isAssignableFrom(CanonicalAuthorityRuntimeAdapter::class.java)
        )

        val methodNames = CanonicalAuthorityRuntimeAdapter::class.java.methods.map { it.name.lowercase() }.toSet()
        listOf("save", "admit", "supersede", "delete", "update", "consolidate", "persist").forEach { forbidden ->
            assertTrue(methodNames.none { it == forbidden || it.startsWith("$forbidden") })
        }
        assertTrue("resolvecanonical" in methodNames)
        assertTrue("resolvelegacyclaim" in methodNames)
    }

    private fun resolverWithoutCandidateReads() = DeterministicAuthorityResolver(
        AuthorityCandidateEvidencePort { _, _ -> error("candidate evidence must not be read in this fixture") }
    )

    private fun canonicalReportClaim() = MipClaimV1(
        claimId = "claim-report",
        speaker = resolved("user"),
        observer = resolved("luna"),
        source = resolved("alice"),
        subject = resolved("bob"),
        target = notApplicableEntity(),
        owner = resolved("bob"),
        perspective = resolved("alice"),
        predicate = "residence.place",
        objectValue = MipField.present("Rome"),
        dialogueAct = MipField.present("ASSERT"),
        polarity = "POSITIVE",
        temporalRelation = "CURRENT",
        sourceType = MipField.present("THIRD_PARTY_REPORT"),
        interpretationConfidence = MipField.present(0.99),
        confidenceByField = mapOf("overall" to 0.99),
        sourceSpans = emptyMap(),
        epistemicClass = MipField.unknown(),
        semanticMarkers = mapOf("CLAIM_KIND" to MipField.present("REPORT")),
    )

    private fun legacyClaim(
        claimId: String,
        sourceType: String = "USER_ASSERTION",
        subject: String = "bob",
        owner: String = "bob",
        perspective: String? = "bob",
        worldTruth: Boolean = false,
    ) = TypedClaim(
        claimId = claimId,
        ownerId = owner,
        subject = subject,
        predicate = "residence.place",
        objectValue = "Rome",
        target = null,
        polarity = "POSITIVE",
        temporalRelation = "CURRENT",
        sourceType = sourceType,
        confidence = mapOf("overall" to 0.95),
        perspective = perspective,
        worldTruth = worldTruth,
    )

    private fun frame(vararg claims: TypedClaim) = MatrixTurnFrame(
        turnId = "turn-1",
        sessionId = "session-1",
        input = UserMessage(
            text = "runtime fixture",
            speakerId = "user",
            observerId = "luna",
            timestampMillis = 0L,
        ),
        typedClaims = claims.toList(),
    )

    private fun context(
        turnId: String = "turn-1",
        sessionId: String = "session-1",
    ) = MatrixContextSnapshot(
        snapshotId = "snapshot-$turnId",
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

    private fun provenance(
        claimId: String,
        generatedBy: ModuleId = ModuleId.UNDERSTANDING,
        originType: String = "USER_MESSAGE",
    ) = ProvenanceRef(
        originId = "origin-$claimId",
        originType = originType,
        generatedBy = generatedBy,
        claimId = MipField.present(claimId),
        createdAt = TEST_TIME,
    )

    private fun resolved(id: String) = MipEntityRef(
        entityId = id,
        surfaceForm = id,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    private fun notApplicableEntity() = MipEntityRef(
        resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE,
    )

    private companion object {
        val TEST_TIME: Instant = Instant.parse("2026-09-05T10:00:00Z")
    }
}
