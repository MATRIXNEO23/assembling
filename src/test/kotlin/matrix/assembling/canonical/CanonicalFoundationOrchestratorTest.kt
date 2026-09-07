package matrix.assembling.canonical

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
import matrix.assembling.authority.runtime.CanonicalUnderstandingV3AuthorityPort
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.DomainAvailability
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
import matrix.assembling.mip.RetrievalQuery
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class CanonicalFoundationOrchestratorTest {

    @Test
    fun `single claim crosses context retrieval coherence and authority with exact bindings`() {
        val result = foundation(
            memoryAvailability = DomainAvailability.AVAILABLE,
            provider = CanonicalRetrievalProvider(::noMatch),
        ).handle(frame(observation()))

        val context = result.requireCanonicalContextSnapshot()
        val retrieval = result.requireCanonicalRetrievalResults().single()
        val coherence = result.requireCanonicalCoherenceForClaim("c0")
        val authority = result.requireCanonicalAuthorityForClaim("c0")

        assertEquals("ctx:$SESSION:$TURN:v1", context.snapshotId)
        assertEquals(listOf("c0"), context.entriesFor(ContextDomain.LINGUISTIC).map { it.typedValue.payload })
        assertEquals(RetrievalStatus.NO_MATCH, retrieval.status)
        assertEquals("c0", retrieval.claimId.value)
        assertEquals(context.snapshotId, retrieval.contextSnapshotId.value)
        assertEquals(CanonicalCoherenceStatus.PASS, coherence.status)
        assertEquals(context.snapshotId, coherence.contextSnapshotId)
        assertEquals(AuthorityResolutionStatus.COMPLETE, authority.resolutionStatus)
        assertEquals(EpistemicClass.REPORT, authority.authority.value)
        assertEquals(context.snapshotId, authority.contextSnapshotId)
        assertEquals("CANONICAL_V3_CONTEXT_RETRIEVAL_COHERENCE_AUTHORITY", result.diagnostics.tags["foundation.path"])
    }

    @Test
    fun `multi claim stays separated through retrieval coherence and authority`() {
        val base = observation()
        val second = base.claims.single().copy(
            claimId = "c1",
            provenance = claimProvenance("c1"),
        )
        val multi = base.copy(claims = listOf(base.claims.single(), second))

        val result = foundation(
            memoryAvailability = DomainAvailability.AVAILABLE,
            provider = CanonicalRetrievalProvider(::noMatch),
        ).handle(frame(multi))

        val context = result.requireCanonicalContextSnapshot()
        assertEquals(setOf("c0", "c1"), context.entriesFor(ContextDomain.LINGUISTIC).map { it.typedValue.payload }.toSet())
        assertEquals(setOf("c0", "c1"), result.requireCanonicalRetrievalResults().mapNotNull { it.claimId.value }.toSet())
        assertEquals(setOf("c0", "c1"), result.requireCanonicalCoherenceResults().map { it.claimId }.toSet())
        assertTrue(result.requireCanonicalCoherenceResults().all { it.status == CanonicalCoherenceStatus.PASS })
        assertEquals(setOf("c0", "c1"), result.requireCanonicalAuthorityResolutions().map { it.claimId }.toSet())
        assertTrue(result.requireCanonicalAuthorityResolutions().all { it.contextSnapshotId == context.snapshotId })
        assertTrue(result.requireCanonicalAuthorityResolutions().all { it.resolutionStatus == AuthorityResolutionStatus.COMPLETE })
    }

    @Test
    fun `ambiguous semantic claim is held before authority without guessed identity`() {
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

        val result = foundation(
            memoryAvailability = DomainAvailability.AVAILABLE,
            provider = CanonicalRetrievalProvider(::noMatch),
        ).handle(frame(base.copy(claims = listOf(ambiguousClaim))))

        val coherence = result.requireCanonicalCoherenceForClaim("c0")
        assertEquals(CanonicalCoherenceStatus.HOLD, coherence.status)
        assertTrue("COHERENCE.INTERPRETATION_AMBIGUOUS" in coherence.reasonCodes)
        assertTrue("COHERENCE.FIELD_AMBIGUOUS.subjectReferent" in coherence.reasonCodes)
        assertEquals(MipFieldStatus.UNAVAILABLE, result.canonicalAuthorityResolutions.status)
        assertEquals("SKIPPED_COHERENCE_HOLD", result.diagnostics.tags["authority.canonical"])
        assertEquals("COHERENCE.CLAIM_HOLD.c0", result.diagnostics.firstDivergence)
    }

    @Test
    fun `retrieval unavailable remains distinct and reaches authority as unavailable`() {
        val result = foundation(
            memoryAvailability = DomainAvailability.UNAVAILABLE,
            provider = CanonicalRetrievalProvider { _, _ -> error("provider must not run when Memory domain is unavailable") },
        ).handle(frame(observation()))

        val retrieval = result.requireCanonicalRetrievalResults().single()
        val authority = result.requireCanonicalAuthorityForClaim("c0")
        assertEquals(RetrievalStatus.INDEX_UNAVAILABLE, retrieval.status)
        assertEquals(AuthorityResolutionStatus.UNAVAILABLE, authority.resolutionStatus)
        assertTrue("RETRIEVAL.INDEX_UNAVAILABLE" in retrieval.reasonCodes)
    }

    @Test
    fun `retrieval error remains error and authority does not collapse it to unavailable`() {
        val result = foundation(
            memoryAvailability = DomainAvailability.AVAILABLE,
            provider = CanonicalRetrievalProvider { query, context ->
                RetrievalResult(
                    queryId = query.queryId,
                    status = RetrievalStatus.ERROR,
                    claimId = MipField.present(query.requireClaimBinding()),
                    contextSnapshotId = MipField.present(context.snapshotId),
                    reasonCodes = listOf("RETRIEVAL.TEST_ERROR"),
                )
            },
        ).handle(frame(observation()))

        assertEquals(RetrievalStatus.ERROR, result.requireCanonicalRetrievalResults().single().status)
        assertEquals(AuthorityResolutionStatus.ERROR, result.requireCanonicalAuthorityForClaim("c0").resolutionStatus)
    }

    @Test
    fun `context binding mismatch fails closed before coherence and authority`() {
        val orchestrator = foundation(
            memoryAvailability = DomainAvailability.AVAILABLE,
            provider = CanonicalRetrievalProvider { query, _ ->
                RetrievalResult(
                    queryId = query.queryId,
                    status = RetrievalStatus.NO_MATCH,
                    claimId = MipField.present(query.requireClaimBinding()),
                    contextSnapshotId = MipField.present("ctx:stale"),
                    reasonCodes = listOf("RETRIEVAL.TEST_STALE_CONTEXT"),
                )
            },
        )

        val error = assertFailsWith<MatrixBoundaryViolationException> {
            orchestrator.handle(frame(observation()))
        }

        assertEquals("RETRIEVAL.BINDING_MISMATCH", error.diagnosticTrace.firstDivergence)
    }

    private fun foundation(
        memoryAvailability: DomainAvailability,
        provider: CanonicalRetrievalProvider,
    ) = CanonicalFoundationOrchestrator(
        context = CanonicalContextAssembler(memoryAvailability),
        retrieval = CanonicalClaimRetrievalStage(provider),
        coherence = CanonicalCoherenceValidator(),
        authority = CanonicalUnderstandingV3AuthorityPort(
            DeterministicAuthorityResolver(
                AuthorityCandidateEvidencePort { _, _ -> error("NO_MATCH/UNAVAILABLE/ERROR must not read candidate evidence") }
            )
        ),
    )

    private fun noMatch(query: RetrievalQuery, context: matrix.assembling.mip.MatrixContextSnapshot) = RetrievalResult(
        queryId = query.queryId,
        status = RetrievalStatus.NO_MATCH,
        claimId = MipField.present(query.requireClaimBinding()),
        contextSnapshotId = MipField.present(context.snapshotId),
        reasonCodes = listOf("RETRIEVAL.NO_MATCH"),
    )

    private fun frame(observation: MipUnderstandingV3Observation) = MatrixTurnFrame(
        turnId = TURN,
        sessionId = SESSION,
        input = input(),
        canonicalUnderstandingV3 = MipField.present(observation),
    )

    private fun input() = UserMessage(
        text = INPUT,
        speakerId = "user",
        observerId = "luna",
        timestampMillis = NOW.toEpochMilli(),
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
