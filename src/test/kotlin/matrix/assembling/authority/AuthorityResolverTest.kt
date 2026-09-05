package matrix.assembling.authority

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
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
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class AuthorityResolverTest {

    @Test
    fun trustedWorldProvenanceCanResolveWorldTruth() {
        val claim = claim(
            epistemicClass = MipField.present("WORLD_TRUTH"),
            sourceType = MipField.notApplicable(),
        )
        val result = resolver().resolve(
            request(
                claim = claim,
                provenance = provenance(
                    claimId = claim.claimId,
                    generatedBy = ModuleId.WORLD,
                    originType = "WORLD_STATE",
                ),
            )
        )

        assertEquals(AuthorityResolutionStatus.COMPLETE, result.resolutionStatus)
        assertEquals(EpistemicClass.WORLD_TRUTH, result.authority.value)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, result.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.RESOLVED_WORLD_TRUTH in result.reasonCodes)
    }

    @Test
    fun compatibilityWorldTruthCannotSelfGrantWithoutWorldProvenance() {
        val claim = claim(epistemicClass = MipField.present("WORLD_TRUTH"))
        val result = resolver().resolve(request(claim = claim))

        assertEquals(EpistemicClass.REPORT, result.authority.value)
        assertTrue("AUTHORITY.WORLD_TRUTH.PROVENANCE_REJECTED" in result.reasonCodes)
        assertFalse(AuthorityReasonCode.RESOLVED_WORLD_TRUTH in result.reasonCodes)
    }

    @Test
    fun directPerceptionProvenanceResolvesObservation() {
        val claim = claim(sourceType = MipField.notApplicable())
        val result = resolver().resolve(
            request(
                claim = claim,
                provenance = provenance(
                    claimId = claim.claimId,
                    generatedBy = ModuleId.PERCEPTION,
                    originType = "OBSERVATION",
                ),
            )
        )

        assertEquals(EpistemicClass.OBSERVATION, result.authority.value)
        assertTrue(AuthorityReasonCode.RESOLVED_OBSERVATION in result.reasonCodes)
    }

    @Test
    fun attributedThirdPartyStatementResolvesReport() {
        val claim = claim(
            source = resolved("alice"),
            sourceType = MipField.present("THIRD_PARTY_REPORT"),
        )
        val result = resolver().resolve(request(claim = claim))

        assertEquals(EpistemicClass.REPORT, result.authority.value)
        assertTrue(AuthorityReasonCode.RESOLVED_REPORT in result.reasonCodes)
    }

    @Test
    fun explicitDerivedEvidenceResolvesInference() {
        val claim = claim(sourceType = MipField.notApplicable())
        val result = resolver().resolve(
            request(
                claim = claim,
                provenance = provenance(
                    claimId = claim.claimId,
                    generatedBy = ModuleId.UNDERSTANDING,
                    originType = "INFERENCE",
                    derivedFromIds = listOf("claim:a", "claim:b"),
                ),
            )
        )

        assertEquals(EpistemicClass.INFERENCE, result.authority.value)
        assertTrue(AuthorityReasonCode.RESOLVED_INFERENCE in result.reasonCodes)
    }

    @Test
    fun structuredBeliefKindResolvesBeliefWithoutCreatingBeliefState() {
        val claim = claim(
            perspective = resolved("speaker"),
            semanticMarkers = mapOf("CLAIM_KIND" to MipField.present("BELIEF")),
            sourceType = MipField.notApplicable(),
        )
        val result = resolver().resolve(request(claim = claim))

        assertEquals(EpistemicClass.BELIEF, result.authority.value)
        assertEquals(1.0, result.authorityResolutionConfidence.value?.value)
        assertEquals(MipFieldStatus.UNAVAILABLE, result.sourceReliability.status)
        assertTrue(AuthorityReasonCode.RESOLVED_BELIEF in result.reasonCodes)
    }

    @Test
    fun sameActorDifferentPredicateIsNotContradiction() {
        val memory = evidence(
            ref = "m1",
            predicate = "preference.like",
            objectValue = "coffee",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(predicate = "residence.place", objectValue = "Milan"),
                retrieval = matched("m1"),
            )
        )

        assertEquals(AuthorityResolutionStatus.COMPLETE, result.resolutionStatus)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, result.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.CONTRADICTION_UNRELATED_PREDICATE in result.reasonCodes)
    }

    @Test
    fun sameCurrentSingleValueSlotWithDifferentValueProducesConcreteContradiction() {
        val memory = evidence(ref = "m1", predicate = "residence.place", objectValue = "Venice")
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(predicate = "residence.place", objectValue = "Milan"),
                retrieval = matched("m1"),
            )
        )

        assertEquals(AuthorityResolutionStatus.COMPLETE, result.resolutionStatus)
        assertEquals(MipFieldStatus.PRESENT, result.contradictedMemoryRef.status)
        assertEquals("m1", result.contradictedMemoryRef.value?.value)
        assertTrue(AuthorityReasonCode.CONTRADICTION_IDENTIFIED in result.reasonCodes)
    }

    @Test
    fun oppositePolarityOnSameSemanticValueProducesContradiction() {
        val memory = evidence(
            ref = "m1",
            predicate = "preference.like",
            objectValue = "coffee",
            polarity = "POSITIVE",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(
                    predicate = "preference.like",
                    objectValue = "coffee",
                    polarity = "NEGATIVE",
                ),
                retrieval = matched("m1"),
            )
        )

        assertEquals("m1", result.contradictedMemoryRef.value?.value)
    }

    @Test
    fun historicalChangeDoesNotBecomeCurrentContradiction() {
        val memory = evidence(
            ref = "m1",
            predicate = "residence.place",
            objectValue = "Venice",
            temporalRelation = "PAST",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(
                    predicate = "residence.place",
                    objectValue = "Milan",
                    temporalRelation = "CURRENT",
                ),
                retrieval = matched("m1"),
            )
        )

        assertEquals(MipFieldStatus.NOT_APPLICABLE, result.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.CONTRADICTION_TEMPORAL_MISMATCH in result.reasonCodes)
    }

    @Test
    fun broadHistoricalScopesWithoutReferenceIdentityRemainUnresolved() {
        val memory = evidence(
            ref = "m1",
            predicate = "residence.place",
            objectValue = "Venice",
            temporalRelation = "PAST",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(
                    predicate = "residence.place",
                    objectValue = "Milan",
                    temporalRelation = "PAST",
                ),
                retrieval = matched("m1"),
            )
        )

        assertEquals(AuthorityResolutionStatus.PARTIAL, result.resolutionStatus)
        assertEquals(MipFieldStatus.UNRESOLVED, result.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.TEMPORAL_UNRESOLVED in result.reasonCodes)
    }

    @Test
    fun correctionPrioritizesDiagnosticsButDoesNotBypassPredicateVerification() {
        val memory = evidence(
            ref = "m1",
            predicate = "preference.like",
            objectValue = "coffee",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(
                    dialogueAct = "CORRECT",
                    predicate = "residence.place",
                    objectValue = "Milan",
                ),
                retrieval = matched("m1"),
            )
        )

        assertEquals(MipFieldStatus.NOT_APPLICABLE, result.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.CORRECTION_CANDIDATE in result.reasonCodes)
        assertTrue(AuthorityReasonCode.CONTRADICTION_UNRELATED_PREDICATE in result.reasonCodes)
    }

    @Test
    fun supersededCandidateCannotBecomeActiveContradictionTarget() {
        val memory = evidence(
            ref = "m1",
            predicate = "residence.place",
            objectValue = "Venice",
            validity = "SUPERSEDED",
        )
        val result = resolver(mapOf("m1" to MipField.present(memory))).resolve(
            request(
                claim = claim(predicate = "residence.place", objectValue = "Milan"),
                retrieval = matched("m1"),
            )
        )

        assertEquals(MipFieldStatus.NOT_APPLICABLE, result.contradictedMemoryRef.status)
        assertTrue("AUTHORITY.CONTRADICTION.CANDIDATE_NOT_VALID" in result.reasonCodes)
    }

    @Test
    fun multipleConcreteContradictionsProduceAmbiguousHold() {
        val memories = mapOf(
            "m1" to MipField.present(evidence("m1", "residence.place", "Venice")),
            "m2" to MipField.present(evidence("m2", "residence.place", "Rome")),
        )
        val result = resolver(memories).resolve(
            request(
                claim = claim(predicate = "residence.place", objectValue = "Milan"),
                retrieval = matched("m1", "m2"),
            )
        )

        assertEquals(AuthorityResolutionStatus.HOLD, result.resolutionStatus)
        assertEquals(MipFieldStatus.AMBIGUOUS, result.contradictedMemoryRef.status)
        assertEquals(setOf("m1", "m2"), result.candidateMemoryRefs.map { it.value }.toSet())
        assertTrue(AuthorityReasonCode.CONTRADICTION_AMBIGUOUS in result.reasonCodes)
    }

    @Test
    fun unresolvedCandidateEvidencePreventsSelectingOtherwiseConcreteTarget() {
        val memories = mapOf(
            "m1" to MipField.present(evidence("m1", "residence.place", "Venice")),
            "m2" to MipField.unresolved<AuthorityCandidateEvidence>(),
        )
        val result = resolver(memories).resolve(
            request(
                claim = claim(predicate = "residence.place", objectValue = "Milan"),
                retrieval = matched("m1", "m2"),
            )
        )

        assertEquals(AuthorityResolutionStatus.PARTIAL, result.resolutionStatus)
        assertEquals(MipFieldStatus.UNRESOLVED, result.contradictedMemoryRef.status)
    }

    @Test
    fun retrievalNoMatchAndProviderUnavailableRemainDistinct() {
        val noMatch = resolver().resolve(
            request(
                claim = claim(),
                retrieval = RetrievalResult(queryId = "q1", status = RetrievalStatus.NO_MATCH),
            )
        )
        val unavailable = resolver().resolve(
            request(
                claim = claim(),
                retrievalField = MipField.unavailable(),
            )
        )

        assertEquals(AuthorityResolutionStatus.COMPLETE, noMatch.resolutionStatus)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, noMatch.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.RETRIEVAL_NO_MATCH in noMatch.reasonCodes)

        assertEquals(AuthorityResolutionStatus.UNAVAILABLE, unavailable.resolutionStatus)
        assertEquals(MipFieldStatus.UNAVAILABLE, unavailable.contradictedMemoryRef.status)
        assertTrue(AuthorityReasonCode.RETRIEVAL_UNAVAILABLE in unavailable.reasonCodes)
    }

    @Test
    fun unresolvedReportSourceHoldsBeforeCandidateEvidenceIsRead() {
        val fake = FakeEvidencePort(emptyMap())
        val resolver = DeterministicAuthorityResolver(fake)
        val result = resolver.resolve(
            request(
                claim = claim(source = unresolved(), sourceType = MipField.present("THIRD_PARTY_REPORT")),
                retrieval = matched("m1"),
            )
        )

        assertEquals(AuthorityResolutionStatus.HOLD, result.resolutionStatus)
        assertEquals(MipFieldStatus.UNRESOLVED, result.contradictedMemoryRef.status)
        assertEquals(0, fake.readCalls)
        assertTrue(AuthorityReasonCode.SOURCE_UNRESOLVED in result.reasonCodes)
    }

    @Test
    fun candidateEvidencePortExposesReadOnlyApi() {
        val methods = AuthorityCandidateEvidencePort::class.java.declaredMethods.map { it.name }.toSet()
        assertEquals(setOf("read"), methods)
    }

    private fun resolver(
        evidence: Map<String, MipField<AuthorityCandidateEvidence>> = emptyMap(),
    ): DeterministicAuthorityResolver = DeterministicAuthorityResolver(FakeEvidencePort(evidence))

    private class FakeEvidencePort(
        private val evidence: Map<String, MipField<AuthorityCandidateEvidence>>,
    ) : AuthorityCandidateEvidencePort {
        var readCalls: Int = 0
            private set

        override fun read(
            memoryRef: MemoryRef,
            contextSnapshot: MatrixContextSnapshot,
        ): MipField<AuthorityCandidateEvidence> {
            readCalls += 1
            return evidence[memoryRef.value] ?: MipField.unresolved()
        }
    }

    private fun request(
        claim: MipClaimV1,
        provenance: ProvenanceRef = provenance(claimId = claim.claimId),
        retrieval: RetrievalResult? = null,
        retrievalField: MipField<RetrievalResult>? = null,
    ): AuthorityResolveRequest = AuthorityResolveRequest(
        requestId = "request:${claim.claimId}",
        claim = claim,
        contextSnapshot = context(),
        retrievalResult = retrievalField ?: retrieval?.let(MipField.Companion::present) ?: MipField.notApplicable(),
        provenance = provenance,
    )

    private fun claim(
        claimId: String = "claim:1",
        source: MipEntityRef = resolved("speaker"),
        subject: MipEntityRef = resolved("alberto"),
        target: MipEntityRef = notApplicable(),
        owner: MipEntityRef = resolved("alberto"),
        perspective: MipEntityRef = resolved("speaker"),
        predicate: String = "residence.place",
        objectValue: String = "Milan",
        dialogueAct: String = "ASSERT",
        polarity: String = "POSITIVE",
        temporalRelation: String = "CURRENT",
        sourceType: MipField<String> = MipField.present("USER_ASSERTION"),
        epistemicClass: MipField<String> = MipField.unknown(),
        semanticMarkers: Map<String, MipField<String>> = emptyMap(),
    ): MipClaimV1 = MipClaimV1(
        claimId = claimId,
        speaker = resolved("speaker"),
        observer = resolved("luna"),
        source = source,
        subject = subject,
        target = target,
        owner = owner,
        perspective = perspective,
        predicate = predicate,
        objectValue = MipField.present(objectValue),
        dialogueAct = MipField.present(dialogueAct),
        polarity = polarity,
        temporalRelation = temporalRelation,
        sourceType = sourceType,
        interpretationConfidence = MipField.present(0.99),
        confidenceByField = mapOf("overall" to 0.99),
        sourceSpans = emptyMap(),
        epistemicClass = epistemicClass,
        semanticMarkers = semanticMarkers,
    )

    private fun evidence(
        ref: String,
        predicate: String,
        objectValue: String,
        polarity: String = "POSITIVE",
        temporalRelation: String = "CURRENT",
        validity: String = "VALID",
        source: MipEntityRef = resolved("speaker"),
        perspective: MipEntityRef = resolved("speaker"),
    ): AuthorityCandidateEvidence = AuthorityCandidateEvidence(
        memoryRef = MemoryRef(ref),
        validity = MipField.present(validity),
        subject = resolved("alberto"),
        predicate = predicate,
        objectValue = MipField.present(objectValue),
        target = notApplicable(),
        owner = resolved("alberto"),
        perspective = perspective,
        source = source,
        polarity = MipField.present(polarity),
        temporalRelation = MipField.present(temporalRelation),
        provenance = provenance(
            claimId = "memory-claim:$ref",
            generatedBy = ModuleId.MEMORY,
            originType = "MEMORY_RECORD",
        ),
    )

    private fun matched(vararg refs: String): RetrievalResult = RetrievalResult(
        queryId = "q1",
        status = RetrievalStatus.MATCHED,
        candidateRefs = refs.toList(),
        selectedRefs = refs.toList(),
    )

    private fun context(): MatrixContextSnapshot = MatrixContextSnapshot(
        snapshotId = "ctx:1",
        turnId = "turn:1",
        sessionId = "session:1",
        agentId = "luna",
        createdAt = Instant.parse("2026-09-05T09:00:00Z"),
        entries = emptyList(),
        domainAvailability = ContextDomain.entries.map {
            ContextDomainAvailability(it, DomainAvailability.AVAILABLE)
        },
    )

    private fun provenance(
        claimId: String,
        generatedBy: ModuleId = ModuleId.UNDERSTANDING,
        originType: String = "USER_UTTERANCE",
        derivedFromIds: List<String> = emptyList(),
    ): ProvenanceRef = ProvenanceRef(
        originId = "origin:$claimId:$originType",
        originType = originType,
        generatedBy = generatedBy,
        derivedFromIds = derivedFromIds,
        claimId = MipField.present(claimId),
        createdAt = Instant.parse("2026-09-05T09:00:00Z"),
    )

    private fun resolved(id: String) = MipEntityRef(
        entityId = id,
        surfaceForm = id,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    private fun unresolved() = MipEntityRef(
        resolutionStatus = MipEntityResolutionStatus.UNRESOLVED,
    )

    private fun notApplicable() = MipEntityRef(
        resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE,
    )
}
