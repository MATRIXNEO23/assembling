package matrix.assembling.authority

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.ContextDomainAvailability
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipContractException
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipEvidenceWire
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipSpan
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus
import matrix.assembling.mip.claimFromWire
import matrix.assembling.mip.claimToWire

class AuthorityContractsTest {

    private val now = Instant.parse("2026-09-05T08:00:00Z")

    @Test
    fun mipClaimWireRoundTripPreservesRolesSpansAndSemanticMarkers() {
        val claim = canonicalClaim()

        val roundTrip = MipEvidenceWire.claimFromWire(MipEvidenceWire.claimToWire(claim))

        assertEquals(claim, roundTrip)
        assertEquals("alberto", roundTrip.subject.entityId)
        assertEquals(MipFieldStatus.PRESENT, roundTrip.semanticMarkers.getValue("ADULT_INTIMACY").status)
        assertEquals("PRESENT", roundTrip.semanticMarkers.getValue("ADULT_INTIMACY").value)
        assertEquals(MipSpan(18, 22), roundTrip.sourceSpans.getValue("object"))
    }

    @Test
    fun authorityRequestWireRoundTripPreservesSuccessfulNoMatchRetrieval() {
        val request = canonicalRequest(
            retrievalResult = MipField.present(
                RetrievalResult(
                    queryId = "rq-1",
                    status = RetrievalStatus.NO_MATCH,
                    reasonCodes = listOf("RETRIEVAL.NO_MATCH"),
                )
            )
        )

        val roundTrip = AuthorityContractWire.requestFromWire(AuthorityContractWire.requestToWire(request))

        assertEquals(request, roundTrip)
        assertEquals(MipFieldStatus.PRESENT, roundTrip.retrievalResult.status)
        assertEquals(RetrievalStatus.NO_MATCH, roundTrip.retrievalResult.value?.status)
    }

    @Test
    fun requestRejectsFieldLevelNoMatchBecauseNoMatchBelongsToRetrievalResultStatus() {
        assertFailsWith<IllegalArgumentException> {
            canonicalRequest(retrievalResult = MipField.noMatch())
        }
    }

    @Test
    fun requestRejectsExplicitProvenanceClaimMismatch() {
        assertFailsWith<IllegalArgumentException> {
            AuthorityResolveRequest(
                requestId = "request-1",
                claim = canonicalClaim(),
                contextSnapshot = snapshot(),
                retrievalResult = MipField.notApplicable(),
                provenance = provenance(ModuleId.BELIEF_AUTHORITY, claimId = "other-claim"),
            )
        }
    }

    @Test
    fun completeResolutionWithoutContradictionRoundTripsLosslessly() {
        val resolution = completeNoContradictionResolution()

        val wire = AuthorityContractWire.resolutionToWire(resolution)
        val roundTrip = AuthorityContractWire.resolutionFromWire(wire)

        assertEquals(resolution, roundTrip)
        assertFalse("accepted" in wire)
        assertFalse("memoryAdmission" in wire)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, roundTrip.contradictedMemoryRef.status)
    }

    @Test
    fun concreteContradictionMustBeAmongCandidateMemoryRefs() {
        assertFailsWith<IllegalArgumentException> {
            completeNoContradictionResolution().copy(
                contradictedMemoryRef = MipField.present(MemoryRef("memory-7")),
                candidateMemoryRefs = listOf(MemoryRef("memory-8")),
                reasonCodes = listOf(AuthorityReasonCode.CONTRADICTION_IDENTIFIED),
            )
        }
    }

    @Test
    fun ambiguousContradictionRequiresAtLeastTwoCandidates() {
        val base = completeNoContradictionResolution()
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                contradictedMemoryRef = MipField.ambiguous(),
                candidateMemoryRefs = listOf(MemoryRef("memory-1")),
                ambiguityReasons = listOf("two plausible temporal targets"),
                reasonCodes = listOf(AuthorityReasonCode.CONTRADICTION_AMBIGUOUS),
            )
        }

        val valid = base.copy(
            resolutionStatus = AuthorityResolutionStatus.HOLD,
            contradictedMemoryRef = MipField.ambiguous(),
            candidateMemoryRefs = listOf(MemoryRef("memory-1"), MemoryRef("memory-2")),
            ambiguityReasons = listOf("two plausible temporal targets"),
            reasonCodes = listOf(AuthorityReasonCode.CONTRADICTION_AMBIGUOUS),
        )
        assertEquals(AuthorityResolutionStatus.HOLD, valid.resolutionStatus)
    }

    @Test
    fun completeResolutionFailsClosedWhenAuthorityOrAssessmentIsUnresolved() {
        val base = completeNoContradictionResolution()

        assertFailsWith<IllegalArgumentException> {
            base.copy(authority = MipField.unknown())
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(authorityResolutionConfidence = MipField.unresolved())
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(contradictedMemoryRef = MipField.noMatch())
        }
    }

    @Test
    fun authorityReasonCodesMustBeNamespacedUniqueAndNonBlank() {
        val base = completeNoContradictionResolution()

        assertFailsWith<IllegalArgumentException> {
            base.copy(reasonCodes = listOf("MEMORY.CONTRADICTION_IDENTIFIED"))
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                reasonCodes = listOf(
                    AuthorityReasonCode.CONTRADICTION_NONE,
                    AuthorityReasonCode.CONTRADICTION_NONE,
                )
            )
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(reasonCodes = listOf(""))
        }
    }

    @Test
    fun resolutionProvenanceClaimMustMatchResolutionClaimId() {
        assertFailsWith<IllegalArgumentException> {
            completeNoContradictionResolution().copy(
                provenance = provenance(ModuleId.BELIEF_AUTHORITY, claimId = "wrong-claim"),
            )
        }
    }

    @Test
    fun resolutionWireRejectsInvalidConfidenceAndUnknownStatus() {
        val validWire = AuthorityContractWire.resolutionToWire(completeNoContradictionResolution())

        val invalidConfidence = validWire + (
            "authorityResolutionConfidence" to mapOf(
                "status" to "PRESENT",
                "value" to 1.5,
            )
        )
        assertFailsWith<MipContractException> {
            AuthorityContractWire.resolutionFromWire(invalidConfidence)
        }

        val unknownStatus = validWire + ("resolutionStatus" to "PERSIST_THIS")
        assertFailsWith<MipContractException> {
            AuthorityContractWire.resolutionFromWire(unknownStatus)
        }
    }

    @Test
    fun requestWireRejectsMalformedRetrievalFieldInsteadOfCollapsingIt() {
        val validWire = AuthorityContractWire.requestToWire(canonicalRequest())
        val malformed = validWire + (
            "retrievalResult" to mapOf(
                "status" to "UNAVAILABLE",
                "value" to mapOf("queryId" to "rq-ghost"),
            )
        )

        assertFailsWith<MipContractException> {
            AuthorityContractWire.requestFromWire(malformed)
        }
    }

    @Test
    fun semanticMarkerWireRejectsPresentWithoutValue() {
        val claimWire = MipEvidenceWire.claimToWire(canonicalClaim()).toMutableMap()
        val markers = (claimWire.getValue("semanticMarkers") as Map<*, *>).toMutableMap()
        markers["ADULT_INTIMACY"] = mapOf("status" to "PRESENT", "value" to null)
        claimWire["semanticMarkers"] = markers

        assertFailsWith<MipContractException> {
            MipEvidenceWire.claimFromWire(claimWire)
        }
    }

    @Test
    fun completeResolutionWithConcreteContradictionRoundTripsIdentity() {
        val memory = MemoryRef("42")
        val resolution = completeNoContradictionResolution().copy(
            contradictedMemoryRef = MipField.present(memory),
            candidateMemoryRefs = listOf(memory, MemoryRef("77")),
            reasonCodes = listOf(
                AuthorityReasonCode.RESOLVED_OBSERVATION,
                AuthorityReasonCode.CONTRADICTION_IDENTIFIED,
            ),
        )

        val roundTrip = AuthorityContractWire.resolutionFromWire(
            AuthorityContractWire.resolutionToWire(resolution)
        )

        assertEquals(memory, roundTrip.contradictedMemoryRef.value)
        assertTrue(memory in roundTrip.candidateMemoryRefs)
    }

    private fun canonicalRequest(
        retrievalResult: MipField<RetrievalResult> = MipField.notApplicable(),
    ): AuthorityResolveRequest = AuthorityResolveRequest(
        requestId = "request-1",
        claim = canonicalClaim(),
        contextSnapshot = snapshot(),
        retrievalResult = retrievalResult,
        provenance = provenance(ModuleId.BELIEF_AUTHORITY, claimId = "claim-1"),
    )

    private fun completeNoContradictionResolution(): AuthorityResolution = AuthorityResolution(
        resolutionId = "authority-1",
        claimId = "claim-1",
        contextSnapshotId = "snapshot-1",
        retrievalQueryId = MipField.notApplicable(),
        resolutionStatus = AuthorityResolutionStatus.COMPLETE,
        authority = MipField.present(EpistemicClass.REPORT),
        authorityResolutionConfidence = MipField.present(AuthorityResolutionConfidence(0.93)),
        sourceReliability = MipField.unknown(),
        contradictedMemoryRef = MipField.notApplicable(),
        candidateMemoryRefs = emptyList(),
        ambiguityReasons = emptyList(),
        reasonCodes = listOf(
            AuthorityReasonCode.RESOLVED_REPORT,
            AuthorityReasonCode.CONTRADICTION_NONE,
        ),
        provenance = provenance(ModuleId.BELIEF_AUTHORITY, claimId = "claim-1"),
    )

    private fun canonicalClaim(): MipClaimV1 = MipClaimV1(
        claimId = "claim-1",
        speaker = resolvedEntity("alberto", "Alberto"),
        observer = resolvedEntity("luna", "Luna"),
        source = MipEntityRef(surfaceForm = "Marco", resolutionStatus = MipEntityResolutionStatus.UNRESOLVED),
        subject = resolvedEntity("alberto", "Alberto"),
        target = MipEntityRef(resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE),
        owner = resolvedEntity("alberto", "Alberto"),
        perspective = resolvedEntity("alberto", "Alberto"),
        predicate = "matrix.location.live_at",
        objectValue = MipField.present("Rome"),
        dialogueAct = MipField.present("ASSERT"),
        polarity = "POSITIVE",
        temporalRelation = "PRESENT",
        sourceType = MipField.present("USER_ASSERTION"),
        interpretationConfidence = MipField.present(0.96),
        confidenceByField = mapOf("predicate" to 0.97, "subject" to 0.95),
        sourceSpans = mapOf(
            "source" to MipSpan(0, 22),
            "subject" to MipSpan(0, 7),
            "object" to MipSpan(18, 22),
            "negation" to null,
            "temporal" to null,
        ),
        epistemicClass = MipField.unknown(),
        semanticMarkers = mapOf("ADULT_INTIMACY" to MipField.present("PRESENT")),
    )

    private fun snapshot(): MatrixContextSnapshot = MatrixContextSnapshot(
        snapshotId = "snapshot-1",
        turnId = "turn-1",
        sessionId = "session-1",
        agentId = "luna",
        createdAt = now,
        entries = emptyList(),
        domainAvailability = ContextDomain.entries.map { domain ->
            ContextDomainAvailability(
                domain = domain,
                availability = when (domain) {
                    ContextDomain.LINGUISTIC,
                    ContextDomain.MEMORY -> DomainAvailability.AVAILABLE
                    else -> DomainAvailability.NOT_WIRED
                },
            )
        },
    )

    private fun provenance(module: ModuleId, claimId: String): ProvenanceRef = ProvenanceRef(
        originId = "origin-${module.name.lowercase()}",
        originType = "AUTHORITY_TEST",
        generatedBy = module,
        claimId = MipField.present(claimId),
        createdAt = now,
    )

    private fun resolvedEntity(id: String, surface: String): MipEntityRef = MipEntityRef(
        entityId = id,
        surfaceForm = surface,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )
}
