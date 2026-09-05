package matrix.assembling.mip

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MipUnderstandingV3ContractsTest {

    @Test
    fun `valid profile preserves independent source perspective plural evidence and original claim ids`() {
        val observation = validObservation()
        val claim = observation.claims.single()

        assertEquals(MIP_UNDERSTANDING_V3_PROFILE_VERSION, observation.profileVersion)
        assertEquals(MATRIX_NLU_CONTRACT_V3, observation.nluContractVersion)
        assertEquals("c0", claim.claimId)
        assertEquals("mention:m0", claim.sourceReferent.value)
        assertEquals("mention:m0", claim.perspectiveReferent.value)
        assertEquals("mention:m1", claim.subjectReferent.value)
        assertEquals("mention:m1", claim.ownerReferent.value)
        assertEquals(2, claim.negationCueSpans.size)
        assertEquals("temporal:t0", claim.temporalRelation.value.anchorRef)
        assertEquals(MipUnderstandingV3InterpretationStatus.RESOLVED, claim.interpretationStatus)
    }

    @Test
    fun `source and perspective remain independently assignable`() {
        val base = validObservation()
        val candidate = MipUnderstandingV3ReferentCandidate(
            candidateId = "ctx:other",
            kind = MipUnderstandingV3CandidateKind.CONTEXT_ENTITY,
            entityRef = resolved("other"),
        )
        val claim = base.claims.single().copy(
            perspectiveReferent = stringField("ctx:other"),
        )

        val observation = base.copy(
            referentCandidates = base.referentCandidates + candidate,
            claims = listOf(claim),
        )

        assertEquals("mention:m0", observation.claims.single().sourceReferent.value)
        assertEquals("ctx:other", observation.claims.single().perspectiveReferent.value)
    }

    @Test
    fun `ambiguous role preserves UNKNOWN primary and ranked candidate alternatives`() {
        val base = validObservation()
        val ambiguous = MipUnderstandingV3Field(
            value = "UNKNOWN",
            confidence = 0.61,
            fieldStatus = MipUnderstandingV3FieldStatus.AMBIGUOUS,
            alternatives = listOf(
                MipUnderstandingV3Alternative("mention:m1", 0.61),
                MipUnderstandingV3Alternative("mention:m0", 0.59),
            ),
        )

        val observation = base.copy(
            claims = listOf(base.claims.single().copy(subjectReferent = ambiguous)),
        )

        val field = observation.claims.single().subjectReferent
        assertEquals(MipUnderstandingV3FieldStatus.AMBIGUOUS, field.fieldStatus)
        assertEquals("UNKNOWN", field.value)
        assertEquals(listOf("mention:m1", "mention:m0"), field.alternatives.map { it.value })
    }

    @Test
    fun `ambiguous field requires at least two descending alternatives`() {
        assertFailsWith<IllegalArgumentException> {
            MipUnderstandingV3Field(
                value = "UNKNOWN",
                confidence = 0.6,
                fieldStatus = MipUnderstandingV3FieldStatus.AMBIGUOUS,
                alternatives = listOf(MipUnderstandingV3Alternative("mention:m0", 0.6)),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            MipUnderstandingV3Field(
                value = "UNKNOWN",
                confidence = 0.6,
                fieldStatus = MipUnderstandingV3FieldStatus.AMBIGUOUS,
                alternatives = listOf(
                    MipUnderstandingV3Alternative("mention:m0", 0.5),
                    MipUnderstandingV3Alternative("mention:m1", 0.7),
                ),
            )
        }
    }

    @Test
    fun `role field cannot resolve to unknown candidate or misuse special value`() {
        val base = validObservation()
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                claims = listOf(
                    base.claims.single().copy(subjectReferent = stringField("missing:candidate"))
                )
            )
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                claims = listOf(
                    base.claims.single().copy(
                        targetReferent = MipUnderstandingV3Field(
                            value = "UNKNOWN",
                            confidence = 0.9,
                            fieldStatus = MipUnderstandingV3FieldStatus.NOT_APPLICABLE,
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `required temporal relation cannot omit anchor`() {
        assertFailsWith<IllegalArgumentException> {
            MipUnderstandingV3TemporalRelationValue(relation = "AFTER", anchorRef = null)
        }
    }

    @Test
    fun `temporal anchor must resolve inside claim or observation`() {
        val base = validObservation()
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                claims = listOf(
                    base.claims.single().copy(
                        temporalRelation = temporalField("AFTER", "temporal:missing")
                    )
                )
            )
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                claims = listOf(
                    base.claims.single().copy(
                        temporalRelation = temporalField("AFTER", "claim:c0")
                    )
                )
            )
        }
    }

    @Test
    fun `multi claim observation preserves original identities and claim anchor`() {
        val base = validObservation()
        val claim0 = base.claims.single()
        val claim1 = claim0.copy(
            claimId = "c1",
            provenance = claimProvenance("c1"),
            temporalEvidence = emptyList(),
            temporalRelation = temporalField("AFTER", "claim:c0"),
        )
        val observation = base.copy(claims = listOf(claim0, claim1))

        assertEquals(listOf("c0", "c1"), observation.claims.map { it.claimId })
        assertEquals("claim:c0", observation.claims[1].temporalRelation.value.anchorRef)
    }

    @Test
    fun `invalid structural claim must remain abstained`() {
        val claim = validObservation().claims.single()
        assertFailsWith<IllegalArgumentException> {
            claim.copy(
                structuralStatus = MipUnderstandingV3StructuralStatus.INVALID,
                interpretationStatus = MipUnderstandingV3InterpretationStatus.RESOLVED,
            )
        }
        val invalid = claim.copy(
            structuralStatus = MipUnderstandingV3StructuralStatus.INVALID,
            interpretationStatus = MipUnderstandingV3InterpretationStatus.ABSTAINED,
        )
        assertEquals(MipUnderstandingV3InterpretationStatus.ABSTAINED, invalid.interpretationStatus)
    }

    @Test
    fun `observation and claim provenance must bind to NLU observation and claim identity`() {
        val base = validObservation()
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                provenance = base.provenance.copy(observationId = MipField.present("other-observation"))
            )
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(
                claims = listOf(
                    base.claims.single().copy(
                        provenance = base.claims.single().provenance.copy(claimId = MipField.present("other-claim"))
                    )
                )
            )
        }
    }

    @Test
    fun `mention candidate must match referenced mention evidence`() {
        val base = validObservation()
        val bad = base.referentCandidates.map { candidate ->
            if (candidate.candidateId == "mention:m1") {
                candidate.copy(span = MipSpan(candidate.span!!.start, candidate.span.end + 1))
            } else candidate
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(referentCandidates = bad)
        }
    }

    @Test
    fun `plural span groups cannot overlap or escape source span`() {
        val base = validObservation()
        val claim = base.claims.single()
        assertFailsWith<IllegalArgumentException> {
            claim.copy(
                negationCueSpans = listOf(MipSpan(20, 24), MipSpan(23, 27)),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            claim.copy(
                objectSpans = listOf(MipSpan(claim.sourceSpan.end - 1, claim.sourceSpan.end + 1)),
            )
        }
    }

    @Test
    fun `profile validates exact upstream contract identity and fingerprint shape`() {
        val base = validObservation()
        assertFailsWith<IllegalArgumentException> {
            base.copy(nluContractVersion = "MATRIX_NLU_CONTRACT_V2")
        }
        assertFailsWith<IllegalArgumentException> {
            base.copy(nluContractFingerprintSha256 = "ABCDEF")
        }
    }

    @Test
    fun `canonical V3 profile exposes no forbidden Authority Memory or worldTruth ownership fields`() {
        val forbidden = setOf(
            "worldTruth",
            "memoryAdmission",
            "authority",
            "beliefConfidence",
            "persistentConsent",
            "persistentGoal",
            "relationshipState",
            "affectiveState",
            "behaviorDecision",
        )
        val observationFields = MipUnderstandingV3Observation::class.java.declaredFields.map { it.name }.toSet()
        val claimFields = MipUnderstandingV3Claim::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(forbidden.intersect(observationFields).isEmpty())
        assertTrue(forbidden.intersect(claimFields).isEmpty())
        assertFalse("worldTruth" in claimFields)
    }

    @Test
    fun `legacy MipClaimV1 remains independently constructible`() {
        val legacy = MipClaimV1(
            claimId = "legacy-c0",
            speaker = resolved("user"),
            observer = resolved("luna"),
            source = resolved("user"),
            subject = resolved("user"),
            target = MipEntityRef(resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE),
            owner = resolved("user"),
            perspective = resolved("user"),
            predicate = "residence.place",
            objectValue = MipField.present("Rome"),
            dialogueAct = MipField.present("ASSERT"),
            polarity = "POSITIVE",
            temporalRelation = "CURRENT",
            sourceType = MipField.present("USER_ASSERTION"),
            interpretationConfidence = MipField.present(0.9),
            confidenceByField = mapOf("predicate" to 0.9),
            sourceSpans = emptyMap(),
            epistemicClass = MipField.unknown(),
        )

        assertEquals("legacy-c0", legacy.claimId)
        assertEquals(MIP_SCHEMA_VERSION, legacy.schemaVersion)
    }

    private fun validObservation(): MipUnderstandingV3Observation {
        val input = "Marco dice che Anna non non vive a Roma dopo il 2024."
        val marco = spanOf(input, "Marco")
        val anna = spanOf(input, "Anna")
        val roma = spanOf(input, "Roma")
        val negations = spansOf(input, "non")
        val temporal = spanOf(input, "2024")

        val mentions = listOf(
            MipUnderstandingV3Mention("m0", marco, MipUnderstandingV3EntityType.PERSON, "Marco", resolved("marco")),
            MipUnderstandingV3Mention("m1", anna, MipUnderstandingV3EntityType.PERSON, "Anna", resolved("anna")),
            MipUnderstandingV3Mention("m2", roma, MipUnderstandingV3EntityType.LOCATION, "Roma", resolved("rome")),
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
            MipUnderstandingV3ReferentCandidate(
                candidateId = "mention:m0",
                kind = MipUnderstandingV3CandidateKind.MENTION,
                mentionId = "m0",
                span = marco,
                entityType = MipUnderstandingV3EntityType.PERSON,
                entityRef = resolved("marco"),
            ),
            MipUnderstandingV3ReferentCandidate(
                candidateId = "mention:m1",
                kind = MipUnderstandingV3CandidateKind.MENTION,
                mentionId = "m1",
                span = anna,
                entityType = MipUnderstandingV3EntityType.PERSON,
                entityRef = resolved("anna"),
            ),
            MipUnderstandingV3ReferentCandidate(
                candidateId = "mention:m2",
                kind = MipUnderstandingV3CandidateKind.MENTION,
                mentionId = "m2",
                span = roma,
                entityType = MipUnderstandingV3EntityType.LOCATION,
                entityRef = resolved("rome"),
            ),
        )
        val source = MipSpan(0, input.length)
        val claim = MipUnderstandingV3Claim(
            claimId = "c0",
            provenance = claimProvenance("c0"),
            sourceSpan = source,
            subjectSpans = listOf(anna),
            objectSpans = listOf(roma),
            negationCueSpans = negations,
            temporalEvidence = listOf(MipUnderstandingV3TemporalEvidence("t0", temporal)),
            entityMentionIds = listOf("m0", "m1", "m2"),
            dialogueAct = stringField("ASSERT"),
            predicate = stringField("residence.place"),
            subjectReferent = stringField("mention:m1"),
            targetReferent = MipUnderstandingV3Field(
                value = "NONE",
                confidence = 0.98,
                fieldStatus = MipUnderstandingV3FieldStatus.NOT_APPLICABLE,
            ),
            ownerReferent = stringField("mention:m1"),
            perspectiveReferent = stringField("mention:m0"),
            sourceReferent = stringField("mention:m0"),
            polarity = stringField("NEGATIVE"),
            temporalRelation = temporalField("AFTER", "temporal:t0"),
            claimKind = stringField("REPORT"),
            fieldStatusByField = mapOf(
                "boundary" to MipUnderstandingV3FieldStatus.RESOLVED,
                "subjectSpans" to MipUnderstandingV3FieldStatus.RESOLVED,
                "objectSpans" to MipUnderstandingV3FieldStatus.RESOLVED,
                "negationCueSpans" to MipUnderstandingV3FieldStatus.RESOLVED,
                "temporalEvidence" to MipUnderstandingV3FieldStatus.RESOLVED,
            ),
            confidenceByField = mapOf(
                "boundary" to 0.99,
                "dialogueAct" to 0.98,
                "predicate" to 0.97,
                "subjectReferent" to 0.96,
                "ownerReferent" to 0.96,
                "perspectiveReferent" to 0.95,
                "sourceReferent" to 0.95,
                "polarity" to 0.97,
                "temporalRelation" to 0.94,
                "claimKind" to 0.96,
            ),
            overallInterpretationConfidence = 0.94,
            structuralStatus = MipUnderstandingV3StructuralStatus.VALID,
            interpretationStatus = MipUnderstandingV3InterpretationStatus.RESOLVED,
            diagnostics = emptyList(),
        )

        return MipUnderstandingV3Observation(
            nluContractVersion = MATRIX_NLU_CONTRACT_V3,
            nluContractFingerprintSha256 = "7b0646e44243ad897760c0fcadbe141f1b8e88e3fd8d63a1789106571b9987b0",
            input = input,
            observationSourceId = "obs-1",
            speaker = resolved("user"),
            observer = resolved("luna"),
            provenance = observationProvenance(),
            mentions = mentions,
            referentCandidates = candidates,
            claims = listOf(claim),
        )
    }

    private fun resolved(id: String) = MipEntityRef(
        entityId = id,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    private fun observationProvenance() = ProvenanceRef(
        originId = "nlu-observation-1",
        originType = "MATRIX_NLU_V3",
        generatedBy = ModuleId.NLU,
        observationId = MipField.present("obs-1"),
        createdAt = Instant.parse("2026-09-05T12:00:00Z"),
    )

    private fun claimProvenance(claimId: String) = ProvenanceRef(
        originId = "understanding-$claimId",
        originType = "UNDERSTANDING_V3",
        generatedBy = ModuleId.UNDERSTANDING,
        derivedFromIds = listOf("obs-1"),
        observationId = MipField.present("obs-1"),
        claimId = MipField.present(claimId),
        createdAt = Instant.parse("2026-09-05T12:00:01Z"),
    )

    private fun stringField(value: String) = MipUnderstandingV3Field(
        value = value,
        confidence = 0.95,
        fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
    )

    private fun temporalField(relation: String, anchor: String? = null) = MipUnderstandingV3Field(
        value = MipUnderstandingV3TemporalRelationValue(relation, anchor),
        confidence = 0.94,
        fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
    )

    private fun spanOf(text: String, token: String): MipSpan {
        val start = text.indexOf(token)
        require(start >= 0)
        return MipSpan(start, start + token.length)
    }

    private fun spansOf(text: String, token: String): List<MipSpan> {
        val result = mutableListOf<MipSpan>()
        var from = 0
        while (true) {
            val start = text.indexOf(token, from)
            if (start < 0) break
            result += MipSpan(start, start + token.length)
            from = start + token.length
        }
        return result
    }
}
