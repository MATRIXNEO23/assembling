package matrix.assembling.mip

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MipEvidenceContractsTest {

    private val now: Instant = Instant.parse("2026-09-05T08:00:00Z")

    @Test
    fun reservedModuleAndContextVocabulariesMatchMip10() {
        assertEquals(
            listOf(
                "PERCEPTION", "NLU", "UNDERSTANDING", "CONTEXT_ASSEMBLER", "WORLD",
                "BELIEF_AUTHORITY", "MEMORY", "AFFECTIVE", "RELATIONSHIP", "INTIMACY",
                "GOAL", "DECISION", "PROMPT_BUILDER", "GGUF", "OUTPUT_VALIDATOR",
                "PERSISTENT_CONSOLIDATION", "SYSTEM",
            ),
            ModuleId.entries.map { it.name },
        )
        assertEquals(
            listOf("LINGUISTIC", "WORLD", "MEMORY", "BELIEF", "RELATIONSHIP", "AFFECTIVE", "INTIMACY", "GOAL", "SYSTEM"),
            ContextDomain.entries.map { it.name },
        )
        assertEquals(
            listOf("TURN", "CONVERSATION", "EPISODE", "SESSION", "PERSISTENT", "WORLD"),
            ContextScope.entries.map { it.name },
        )
        assertEquals(
            listOf("AVAILABLE", "NOT_WIRED", "UNAVAILABLE", "ERROR"),
            DomainAvailability.entries.map { it.name },
        )
    }

    @Test
    fun retrievalVocabularyExactlyMatchesMip10() {
        assertEquals(
            listOf(
                "ENRICH_TURN", "VERIFY_CLAIM", "CHECK_CONTRADICTION", "FIND_HISTORY",
                "EXPLICIT_RECALL", "ANALYZE_PATTERN", "EXPLAIN_STATE", "SUPPORT_DECISION",
            ),
            RetrievalPurpose.entries.map { it.name },
        )
        assertEquals(
            listOf("MATCHED", "NO_MATCH", "AMBIGUOUS", "INDEX_UNAVAILABLE", "ERROR"),
            RetrievalStatus.entries.map { it.name },
        )
    }

    @Test
    fun provenanceWireRoundTripPreservesExplicitFieldStates() {
        val provenance = ProvenanceRef(
            originId = "obs-1",
            originType = "OBSERVATION",
            originAgent = MipField.present("luna"),
            generatedBy = ModuleId.UNDERSTANDING,
            derivedFromIds = listOf("input-1", "nlu-1"),
            quotedFromId = MipField.unknown(),
            revisionOfId = MipField.notApplicable(),
            observationId = MipField.present("obs-1"),
            eventId = MipField.unresolved(),
            claimId = MipField.present("claim-1"),
            createdAt = now,
        )

        val roundTrip = MipEvidenceWire.provenanceFromWire(MipEvidenceWire.provenanceToWire(provenance))

        assertEquals(provenance, roundTrip)
        assertEquals(MipFieldStatus.UNKNOWN, roundTrip.quotedFromId.status)
        assertEquals(MipFieldStatus.UNRESOLVED, roundTrip.eventId.status)
        assertEquals(MipFieldStatus.NOT_APPLICABLE, roundTrip.revisionOfId.status)
    }

    @Test
    fun snapshotRequiresEveryDomainAvailabilityExactlyOnce() {
        val missingSystem = allAvailability().filterNot { it.domain == ContextDomain.SYSTEM }

        assertFailsWith<IllegalArgumentException> {
            MatrixContextSnapshot(
                snapshotId = "snapshot-1",
                turnId = "turn-1",
                sessionId = "session-1",
                agentId = "luna",
                createdAt = now,
                entries = emptyList(),
                domainAvailability = missingSystem,
            )
        }
    }

    @Test
    fun unavailableDomainCannotCarryFakeContextEntry() {
        val entry = ContextEntry(
            entryId = "memory-entry",
            domain = ContextDomain.MEMORY,
            scope = ContextScope.TURN,
            key = "memory.retrieval",
            typedValue = TypedContextValue("matrix.memory.summary", "fake"),
            provenance = provenance(ModuleId.MEMORY),
            validity = MipField.present("VALID"),
            ownerModule = ModuleId.MEMORY,
        )
        val availability = allAvailability(
            overrides = mapOf(ContextDomain.MEMORY to DomainAvailability.UNAVAILABLE),
        )

        assertFailsWith<IllegalArgumentException> {
            MatrixContextSnapshot(
                snapshotId = "snapshot-1",
                turnId = "turn-1",
                sessionId = "session-1",
                agentId = "luna",
                createdAt = now,
                entries = listOf(entry),
                domainAvailability = availability,
            )
        }
    }

    @Test
    fun snapshotWireRoundTripPreservesAvailabilityAndTypedEntry() {
        val subject = MipEntityRef(
            entityId = "alberto",
            surfaceForm = "Alberto",
            resolutionStatus = MipEntityResolutionStatus.RESOLVED,
        )
        val entry = ContextEntry(
            entryId = "ling-1",
            domain = ContextDomain.LINGUISTIC,
            scope = ContextScope.TURN,
            key = "matrix.claim.summary",
            typedValue = TypedContextValue("matrix.text", "Alberto lives in Rome"),
            subjectRefs = listOf(subject),
            authority = MipField.unknown(),
            confidence = MipField.present(0.94),
            provenance = provenance(ModuleId.UNDERSTANDING),
            validity = MipField.present("CURRENT"),
            ownerModule = ModuleId.UNDERSTANDING,
            stateVersion = MipField.present("v1"),
        )
        val snapshot = MatrixContextSnapshot(
            snapshotId = "snapshot-2",
            parentSnapshotId = MipField.present("snapshot-1"),
            turnId = "turn-1",
            sessionId = "session-1",
            agentId = "luna",
            createdAt = now,
            entries = listOf(entry),
            domainAvailability = allAvailability(),
        )

        val roundTrip = MipEvidenceWire.snapshotFromWire(MipEvidenceWire.snapshotToWire(snapshot))

        assertEquals(snapshot, roundTrip)
        assertEquals(DomainAvailability.AVAILABLE, roundTrip.availabilityOf(ContextDomain.LINGUISTIC))
        assertEquals(1, roundTrip.entriesFor(ContextDomain.LINGUISTIC).size)
    }

    @Test
    fun contextConfidenceFailsClosedOutsideNormalizedFiniteRange() {
        assertFailsWith<IllegalArgumentException> {
            ContextEntry(
                entryId = "entry-1",
                domain = ContextDomain.LINGUISTIC,
                scope = ContextScope.TURN,
                key = "k",
                typedValue = TypedContextValue("matrix.text", "x"),
                confidence = MipField.present(Double.NaN),
                provenance = provenance(ModuleId.UNDERSTANDING),
                validity = MipField.present("CURRENT"),
                ownerModule = ModuleId.UNDERSTANDING,
            )
        }
    }

    @Test
    fun retrievalQueryWireRoundTripPreservesStructuredConstraints() {
        val subject = MipEntityRef(
            entityId = "alberto",
            resolutionStatus = MipEntityResolutionStatus.RESOLVED,
        )
        val query = RetrievalQuery(
            queryId = "rq-1",
            purpose = RetrievalPurpose.CHECK_CONTRADICTION,
            agentId = "luna",
            subjectRefs = listOf(subject),
            entityRefs = listOf(subject),
            predicates = listOf("matrix.location.live_at"),
            temporalConstraint = MipField.present("PRESENT"),
            relationshipTarget = MipField.notApplicable(),
            goalRefs = emptyList(),
            includeHistorical = true,
            includeSuperseded = true,
            maxCandidates = 20,
            maxSelected = 5,
            contextSnapshotId = "snapshot-2",
        )

        val roundTrip = MipEvidenceWire.retrievalQueryFromWire(MipEvidenceWire.retrievalQueryToWire(query))

        assertEquals(query, roundTrip)
    }

    @Test
    fun includeSupersededWithoutHistoricalFailsClosed() {
        assertFailsWith<IllegalArgumentException> {
            RetrievalQuery(
                queryId = "rq-1",
                purpose = RetrievalPurpose.FIND_HISTORY,
                agentId = "luna",
                includeHistorical = false,
                includeSuperseded = true,
                maxCandidates = 10,
                maxSelected = 5,
                contextSnapshotId = "snapshot-1",
            )
        }
    }

    @Test
    fun retrievalStatusInvariantsKeepNoMatchUnavailableAndErrorDistinct() {
        val noMatch = RetrievalResult(queryId = "rq", status = RetrievalStatus.NO_MATCH)
        val unavailable = RetrievalResult(queryId = "rq", status = RetrievalStatus.INDEX_UNAVAILABLE)
        val error = RetrievalResult(queryId = "rq", status = RetrievalStatus.ERROR)

        assertEquals(RetrievalStatus.NO_MATCH, MipEvidenceWire.retrievalResultFromWire(MipEvidenceWire.retrievalResultToWire(noMatch)).status)
        assertEquals(RetrievalStatus.INDEX_UNAVAILABLE, MipEvidenceWire.retrievalResultFromWire(MipEvidenceWire.retrievalResultToWire(unavailable)).status)
        assertEquals(RetrievalStatus.ERROR, MipEvidenceWire.retrievalResultFromWire(MipEvidenceWire.retrievalResultToWire(error)).status)

        assertFailsWith<IllegalArgumentException> {
            RetrievalResult(queryId = "rq", status = RetrievalStatus.NO_MATCH, candidateRefs = listOf("memory-1"))
        }
        assertFailsWith<IllegalArgumentException> {
            RetrievalResult(queryId = "rq", status = RetrievalStatus.INDEX_UNAVAILABLE, candidateRefs = listOf("memory-1"))
        }
        assertFailsWith<IllegalArgumentException> {
            RetrievalResult(queryId = "rq", status = RetrievalStatus.MATCHED)
        }
        assertFailsWith<IllegalArgumentException> {
            RetrievalResult(queryId = "rq", status = RetrievalStatus.AMBIGUOUS, candidateRefs = listOf("memory-1"))
        }
    }

    @Test
    fun matchedRetrievalWireRoundTripPreservesIdentityBoundScores() {
        val result = RetrievalResult(
            queryId = "rq-2",
            status = RetrievalStatus.MATCHED,
            candidateRefs = listOf("memory-1", "memory-2"),
            selectedRefs = listOf("memory-1"),
            scores = listOf(
                RetrievalScore("memory-1", 0.92),
                RetrievalScore("memory-2", 0.61),
            ),
            reasonCodes = listOf("RETRIEVAL.MATCHED"),
            indexVersion = MipField.present("memory-index-v7"),
        )

        val roundTrip = MipEvidenceWire.retrievalResultFromWire(MipEvidenceWire.retrievalResultToWire(result))

        assertEquals(result, roundTrip)
        assertEquals("memory-1", roundTrip.scores.first().ref)
        assertEquals(0.92, roundTrip.scores.first().retrievalRelevance)
    }

    @Test
    fun malformedWireFieldAndUnknownEnumFailClosed() {
        val malformedNoValue = mapOf<String, Any?>(
            "queryId" to "rq",
            "status" to "NO_MATCH",
            "candidateRefs" to emptyList<String>(),
            "selectedRefs" to emptyList<String>(),
            "scores" to emptyList<Map<String, Any?>>(),
            "reasonCodes" to emptyList<String>(),
            "indexVersion" to mapOf("status" to "PRESENT", "value" to null),
        )
        assertFailsWith<MipContractException> {
            MipEvidenceWire.retrievalResultFromWire(malformedNoValue)
        }

        val unknownStatus = malformedNoValue + (
            "indexVersion" to mapOf("status" to "NOT_APPLICABLE", "value" to null)
        ) + ("status" to "NOT_A_REAL_STATUS")
        assertFailsWith<MipContractException> {
            MipEvidenceWire.retrievalResultFromWire(unknownStatus)
        }
    }

    @Test
    fun retrievalScoreRejectsInvalidRelevanceAndUnknownSelectedRefs() {
        assertFailsWith<IllegalArgumentException> { RetrievalScore("memory-1", Double.NaN) }
        assertFailsWith<IllegalArgumentException> { RetrievalScore("memory-1", -0.01) }
        assertFailsWith<IllegalArgumentException> { RetrievalScore("memory-1", 1.01) }

        assertFailsWith<IllegalArgumentException> {
            RetrievalResult(
                queryId = "rq",
                status = RetrievalStatus.MATCHED,
                candidateRefs = listOf("memory-1"),
                selectedRefs = listOf("memory-2"),
            )
        }
    }

    private fun provenance(module: ModuleId): ProvenanceRef = ProvenanceRef(
        originId = "origin-${module.name.lowercase()}",
        originType = "TEST",
        generatedBy = module,
        createdAt = now,
    )

    private fun allAvailability(
        overrides: Map<ContextDomain, DomainAvailability> = emptyMap(),
    ): List<ContextDomainAvailability> = ContextDomain.entries.map { domain ->
        ContextDomainAvailability(
            domain = domain,
            availability = overrides[domain] ?: DomainAvailability.AVAILABLE,
        )
    }
}
