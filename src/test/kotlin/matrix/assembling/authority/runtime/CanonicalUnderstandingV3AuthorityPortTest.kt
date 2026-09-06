package matrix.assembling.authority.runtime

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import matrix.assembling.MatrixBoundaryViolationException
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.UserMessage
import matrix.assembling.authority.AuthorityCandidateEvidencePort
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.DeterministicAuthorityResolver
import matrix.assembling.authority.EpistemicClass
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.ContextDomainAvailability
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipSpan
import matrix.assembling.mip.MipUnderstandingV3Alternative
import matrix.assembling.mip.MipUnderstandingV3CandidateKind
import matrix.assembling.mip.MipUnderstandingV3EntityType
import matrix.assembling.mip.MipUnderstandingV3Field
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3Mention
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.MipUnderstandingV3ReferentCandidate
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus
import matrix.assembling.mip.MipUnderstandingV3TemporalRelationValue
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class CanonicalUnderstandingV3AuthorityPortTest {

    @Test
    fun `real V3 report claim reaches canonical Authority without legacy TypedClaim`() {
        val observation = observation()
        val result = port().resolve(frame(observation, listOf(noMatch("q:c0"))))
        val resolution = result.requireCanonicalAuthorityForClaim("c0")

        assertTrue(result.typedClaims.isEmpty())
        assertEquals(MipFieldStatus.PRESENT, result.canonicalAuthorityResolutions.status)
        assertEquals(AuthorityResolutionStatus.COMPLETE, resolution.resolutionStatus)
        assertEquals(EpistemicClass.REPORT, resolution.authority.value)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, resolution.contradictedMemoryRef.status)
        assertEquals("UNDERSTANDING_V3", result.diagnostics.tags["authority.input"])
    }

    @Test
    fun `projection consumes structured V3 roles and object span without free text reparsing`() {
        val observation = observation()
        val projected = UnderstandingV3AuthorityProjection.project(observation, observation.claims.single())

        assertEquals("marco", projected.source.entityId)
        assertEquals("anna", projected.subject.entityId)
        assertEquals("anna", projected.owner.entityId)
        assertEquals("user", projected.perspective.entityId)
        assertEquals("Roma", projected.objectValue.value)
        assertEquals("REPORT", projected.semanticMarkers["CLAIM_KIND"]?.value)
        assertEquals("speech-time", projected.semanticMarkers["TEMPORAL_REFERENCE_KEY"]?.value)
    }

    @Test
    fun `multi claim turn does not guess retrieval binding by list order`() {
        val base = observation()
        val second = base.claims.single().copy(
            claimId = "c1",
            provenance = claimProvenance("c1"),
        )
        val multi = base.copy(claims = listOf(base.claims.single(), second))
        val result = port().resolve(
            frame(
                multi,
                listOf(noMatch("q:first"), noMatch("q:second")),
            )
        )

        assertEquals(listOf("c0", "c1"), result.requireCanonicalAuthorityResolutions().map { it.claimId })
        assertTrue(result.requireCanonicalAuthorityResolutions().all {
            it.resolutionStatus == AuthorityResolutionStatus.PARTIAL
        })
        assertTrue(result.requireCanonicalAuthorityResolutions().all {
            it.contradictedMemoryRef.status == MipFieldStatus.UNRESOLVED
        })
    }

    @Test
    fun `ambiguous V3 interpretation is held instead of promoted`() {
        val base = observation()
        val ambiguousSubject = MipUnderstandingV3Field(
            value = "UNKNOWN",
            confidence = 0.60,
            fieldStatus = MipUnderstandingV3FieldStatus.AMBIGUOUS,
            alternatives = listOf(
                MipUnderstandingV3Alternative("mention:m1", 0.60),
                MipUnderstandingV3Alternative("mention:m0", 0.40),
            ),
        )
        val ambiguousClaim = base.claims.single().copy(
            subjectReferent = ambiguousSubject,
            interpretationStatus = MipUnderstandingV3InterpretationStatus.AMBIGUOUS,
        )
        val ambiguous = base.copy(claims = listOf(ambiguousClaim))

        val resolution = port().resolve(frame(ambiguous, listOf(noMatch("q:c0"))))
            .requireCanonicalAuthorityForClaim("c0")

        assertEquals(AuthorityResolutionStatus.HOLD, resolution.resolutionStatus)
        assertEquals(MipFieldStatus.UNRESOLVED, resolution.contradictedMemoryRef.status)
    }

    @Test
    fun `missing canonical context fails closed with diagnostic divergence`() {
        val observation = observation()
        val turn = MatrixTurnFrame(
            turnId = TURN,
            sessionId = SESSION,
            input = input(),
            canonicalUnderstandingV3 = MipField.present(observation),
        )

        val error = assertFailsWith<MatrixBoundaryViolationException> {
            port().resolve(turn)
        }

        assertEquals(CanonicalUnderstandingV3AuthorityPort.REASON_CONTEXT_UNAVAILABLE, error.diagnosticTrace.firstDivergence)
    }

    private fun port() = CanonicalUnderstandingV3AuthorityPort(
        DeterministicAuthorityResolver(
            AuthorityCandidateEvidencePort { _, _ -> error("NO_MATCH must not read candidate evidence") }
        )
    )

    private fun frame(
        observation: MipUnderstandingV3Observation,
        retrieval: List<RetrievalResult>,
    ) = MatrixTurnFrame(
        turnId = TURN,
        sessionId = SESSION,
        input = input(),
        contextSnapshot = MipField.present(context()),
        retrievalResults = MipField.present(retrieval),
        canonicalUnderstandingV3 = MipField.present(observation),
    )

    private fun input() = UserMessage(
        text = INPUT,
        speakerId = "user",
        observerId = "luna",
        timestampMillis = 1_788_604_800_000L,
        locale = "it",
    )

    private fun observation(): MipUnderstandingV3Observation {
        val marco = spanOf("Marco")
        val anna = spanOf("Anna")
        val roma = spanOf("Roma")

        val mentions = listOf(
            mention("m0", marco, MipUnderstandingV3EntityType.PERSON, "Marco", "marco"),
            mention("m1", anna, MipUnderstandingV3EntityType.PERSON, "Anna", "anna"),
            mention("m2", roma, MipUnderstandingV3EntityType.LOCATION, "Roma", "rome"),
        )
        val candidates = listOf(
            MipUnderstandingV3ReferentCandidate(
                candidateId = "ctx:speaker",
                kind = MipUnderstandingV3CandidateKind.CONTEXT_SPEAKER,
                entityRef = resolved("user"),
            ),
            MipUnderstandingV3ReferentCandidate(
                candidateId = "ctx:observer",
                kind = MipUnderstandingV3CandidateKind.CONTEXT_OBSERVER,
                entityRef = resolved("luna"),
            ),
            mentionCandidate("m0", marco, MipUnderstandingV3EntityType.PERSON, "marco"),
            mentionCandidate("m1", anna, MipUnderstandingV3EntityType.PERSON, "anna"),
            mentionCandidate("m2", roma, MipUnderstandingV3EntityType.LOCATION, "rome"),
        )
        val claim = matrix.assembling.mip.MipUnderstandingV3Claim(
            claimId = "c0",
            provenance = claimProvenance("c0"),
            sourceSpan = MipSpan(0, INPUT.length),
            subjectSpans = listOf(anna),
            objectSpans = listOf(roma),
            negationCueSpans = emptyList(),
            temporalEvidence = emptyList(),
            entityMentionIds = listOf("m0", "m1", "m2"),
            dialogueAct = resolvedString("ASSERT"),
            predicate = resolvedString("residence.place"),
            subjectReferent = resolvedString("mention:m1"),
            targetReferent = notApplicableString(),
            ownerReferent = resolvedString("mention:m1"),
            perspectiveReferent = resolvedString("ctx:speaker"),
            sourceReferent = resolvedString("mention:m0"),
            polarity = resolvedString("POSITIVE"),
            temporalRelation = MipUnderstandingV3Field(
                value = MipUnderstandingV3TemporalRelationValue("CURRENT", "speech-time"),
                confidence = 0.96,
                fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
            ),
            claimKind = resolvedString("REPORT"),
            fieldStatusByField = emptyMap(),
            confidenceByField = mapOf(
                "dialogueAct" to 0.99,
                "predicate" to 0.98,
                "subjectReferent" to 0.98,
                "ownerReferent" to 0.98,
                "sourceReferent" to 0.98,
                "polarity" to 0.99,
                "temporalRelation" to 0.96,
                "claimKind" to 0.99,
            ),
            overallInterpretationConfidence = 0.97,
            structuralStatus = MipUnderstandingV3StructuralStatus.VALID,
            interpretationStatus = MipUnderstandingV3InterpretationStatus.RESOLVED,
        )

        return MipUnderstandingV3Observation(
            nluContractVersion = "MATRIX_NLU_CONTRACT_V3",
            nluContractFingerprintSha256 = "7b0646e44243ad897760c0fcadbe141f1b8e88e3fd8d63a1789106571b9987b0",
            input = INPUT,
            observationSourceId = "obs-1",
            speaker = resolved("user"),
            observer = resolved("luna"),
            provenance = observationProvenance(),
            mentions = mentions,
            referentCandidates = candidates,
            claims = listOf(claim),
        )
    }

    private fun context() = MatrixContextSnapshot(
        snapshotId = "ctx-1",
        turnId = TURN,
        sessionId = SESSION,
        agentId = "luna",
        createdAt = NOW,
        entries = emptyList(),
        domainAvailability = ContextDomain.entries.map {
            ContextDomainAvailability(it, DomainAvailability.AVAILABLE)
        },
    )

    private fun noMatch(queryId: String) = RetrievalResult(
        queryId = queryId,
        status = RetrievalStatus.NO_MATCH,
    )

    private fun observationProvenance() = ProvenanceRef(
        originId = "obs-1",
        originType = "NLU_OBSERVATION",
        generatedBy = ModuleId.NLU,
        observationId = MipField.present("obs-1"),
        createdAt = NOW,
    )

    private fun claimProvenance(claimId: String) = ProvenanceRef(
        originId = "claim:$claimId",
        originType = "UNDERSTANDING_CLAIM",
        generatedBy = ModuleId.UNDERSTANDING,
        derivedFromIds = listOf("obs-1"),
        observationId = MipField.present("obs-1"),
        claimId = MipField.present(claimId),
        createdAt = NOW,
    )

    private fun mention(
        id: String,
        span: MipSpan,
        type: MipUnderstandingV3EntityType,
        surface: String,
        entityId: String,
    ) = MipUnderstandingV3Mention(
        mentionId = id,
        span = span,
        entityType = type,
        surfaceForm = surface,
        entityRef = resolved(entityId),
    )

    private fun mentionCandidate(
        id: String,
        span: MipSpan,
        type: MipUnderstandingV3EntityType,
        entityId: String,
    ) = MipUnderstandingV3ReferentCandidate(
        candidateId = "mention:$id",
        kind = MipUnderstandingV3CandidateKind.MENTION,
        mentionId = id,
        span = span,
        entityType = type,
        entityRef = resolved(entityId),
    )

    private fun resolvedString(value: String) = MipUnderstandingV3Field(
        value = value,
        confidence = 0.99,
        fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
    )

    private fun notApplicableString() = MipUnderstandingV3Field(
        value = "NONE",
        confidence = 0.99,
        fieldStatus = MipUnderstandingV3FieldStatus.NOT_APPLICABLE,
    )

    private fun resolved(id: String) = MipEntityRef(
        entityId = id,
        surfaceForm = id,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    private fun spanOf(token: String): MipSpan {
        val start = INPUT.indexOf(token)
        require(start >= 0)
        return MipSpan(start, start + token.length)
    }

    companion object {
        private const val INPUT = "Marco dice che Anna vive a Roma."
        private const val TURN = "turn-1"
        private const val SESSION = "session-1"
        private val NOW = Instant.parse("2026-09-06T08:00:00Z")
    }
}
