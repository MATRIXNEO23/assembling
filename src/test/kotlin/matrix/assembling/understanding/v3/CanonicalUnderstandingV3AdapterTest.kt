package matrix.assembling.understanding.v3

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.MatrixBoundaryViolationException
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.UserMessage
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus

class CanonicalUnderstandingV3AdapterTest {

    @Test
    fun `adapter produces real canonical V3 TypedClaims without legacy collapse`() {
        val output = validOutput()
        val adapter = adapterReturning(output)

        val result = adapter.understand(frame())
        val observation = result.requireCanonicalUnderstandingV3()
        val claim = result.requireCanonicalTypedClaimsV3().single()

        assertEquals(MipFieldStatus.PRESENT, result.canonicalUnderstandingV3.status)
        assertEquals("obs-1", observation.observationSourceId)
        assertEquals("c0", claim.claimId)
        assertEquals("mention:m0", claim.sourceReferent.value)
        assertEquals("ctx:speaker", claim.perspectiveReferent.value)
        assertEquals("mention:m1", claim.subjectReferent.value)
        assertEquals("mention:m1", claim.ownerReferent.value)
        assertEquals("REPORT", claim.claimKind.value)
        assertEquals(2, claim.negationCueSpans.size)
        assertEquals("temporal:t0", claim.temporalRelation.value.anchorRef)
        assertEquals(MipUnderstandingV3StructuralStatus.VALID, claim.structuralStatus)
        assertEquals(MipUnderstandingV3InterpretationStatus.RESOLVED, claim.interpretationStatus)

        // Lossy compatibility paths are deliberately not auto-populated.
        assertTrue(result.typedClaims.isEmpty())
        assertNull(result.nlu)
        assertNull(result.semantic)
        assertEquals("CANONICAL_TYPED_CLAIMS_BUILT", result.diagnostics.understandingResult?.decision)
        assertEquals("PASS", result.diagnostics.understandingResult?.status)
    }

    @Test
    fun `multi claim output preserves original V3 claim identities and cross claim temporal anchor`() {
        val base = validOutput()
        val claim1 = base.claims.single().copy(
            claimId = "c1",
            temporalEvidence = emptyList(),
            temporalRelation = temporalField("AFTER", "claim:c0"),
        )
        val output = base.copy(claims = listOf(base.claims.single(), claim1))

        val result = adapterReturning(output).understand(frame())
        val claims = result.requireCanonicalTypedClaimsV3()

        assertEquals(listOf("c0", "c1"), claims.map { it.claimId })
        assertEquals("claim:c0", claims[1].temporalRelation.value.anchorRef)
        assertEquals(listOf("c0", "c1"), claims.map { it.provenance.claimId.value })
        assertTrue(result.typedClaims.isEmpty())
    }

    @Test
    fun `ambiguous and abstained claim states survive adapter and drive diagnostics`() {
        val base = validOutput()
        val ambiguousSubject = MatrixNluV3Field(
            value = "UNKNOWN",
            confidence = 0.61,
            fieldStatus = "AMBIGUOUS",
            alternatives = listOf(
                MatrixNluV3Alternative("mention:m1", 0.61),
                MatrixNluV3Alternative("mention:m0", 0.59),
            ),
        )
        val ambiguousOutput = base.copy(
            claims = listOf(
                base.claims.single().copy(
                    subjectReferent = ambiguousSubject,
                    interpretationStatus = "AMBIGUOUS",
                )
            )
        )

        val ambiguousFrame = adapterReturning(ambiguousOutput).understand(frame())
        val ambiguousClaim = ambiguousFrame.requireCanonicalTypedClaimsV3().single()
        assertEquals(MipUnderstandingV3FieldStatus.AMBIGUOUS, ambiguousClaim.subjectReferent.fieldStatus)
        assertEquals("UNKNOWN", ambiguousClaim.subjectReferent.value)
        assertEquals(listOf("mention:m1", "mention:m0"), ambiguousClaim.subjectReferent.alternatives.map { it.value })
        assertEquals("PARTIAL", ambiguousFrame.diagnostics.understandingResult?.status)

        val abstainedOutput = base.copy(
            claims = listOf(
                base.claims.single().copy(
                    structuralStatus = "INVALID",
                    interpretationStatus = "ABSTAINED",
                    diagnostics = listOf("invalid source evidence"),
                )
            )
        )
        val abstainedFrame = adapterReturning(abstainedOutput).understand(frame())
        val abstainedClaim = abstainedFrame.requireCanonicalTypedClaimsV3().single()
        assertEquals(MipUnderstandingV3StructuralStatus.INVALID, abstainedClaim.structuralStatus)
        assertEquals(MipUnderstandingV3InterpretationStatus.ABSTAINED, abstainedClaim.interpretationStatus)
        assertEquals("HOLD", abstainedFrame.diagnostics.understandingResult?.status)
    }

    @Test
    fun `empty valid observation remains explicit PRESENT with zero claims`() {
        val output = validOutput().copy(claims = emptyList())
        val result = adapterReturning(output).understand(frame())

        assertEquals(MipFieldStatus.PRESENT, result.canonicalUnderstandingV3.status)
        assertTrue(result.requireCanonicalTypedClaimsV3().isEmpty())
        assertEquals("HOLD", result.diagnostics.understandingResult?.status)
        assertTrue(result.diagnostics.understandingResult?.reasonCodes?.contains("UNDERSTANDING_V3_NO_CLAIMS") == true)
    }

    @Test
    fun `contract fingerprint mismatch fails closed with first divergence`() {
        val output = validOutput().copy(contractFingerprintSha256 = "a".repeat(64))
        val error = assertFailsWith<MatrixBoundaryViolationException> {
            adapterReturning(output).understand(frame())
        }

        assertEquals("UNDERSTANDING.V3.CONTRACT_FINGERPRINT_MISMATCH", error.diagnosticTrace.firstDivergence)
        assertTrue(error.diagnosticTrace.events.contains("understanding_v3.boundary_failure"))
    }

    @Test
    fun `input speaker and observer mismatches fail closed before canonical claim creation`() {
        listOf(
            validOutput().copy(input = "different"),
            validOutput().copy(speakerRef = "other-user"),
            validOutput().copy(observerRef = "other-observer"),
        ).forEach { output ->
            assertFailsWith<MatrixBoundaryViolationException> {
                adapterReturning(output).understand(frame())
            }
        }
    }

    @Test
    fun `runtime exception is surfaced as deterministic Understanding boundary failure`() {
        val adapter = CanonicalUnderstandingV3Adapter(
            runtime = MatrixNluV3RuntimeBridge { throw IllegalStateException("runtime failed") },
            config = CanonicalUnderstandingV3Config(FINGERPRINT),
        )
        val error = assertFailsWith<MatrixBoundaryViolationException> {
            adapter.understand(frame())
        }
        assertEquals("UNDERSTANDING.V3.RUNTIME_ERROR", error.diagnosticTrace.firstDivergence)
    }

    @Test
    fun `runtime DTOs expose no forbidden truth Authority Memory or state ownership fields`() {
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
        val outputFields = MatrixNluV3Output::class.java.declaredFields.map { it.name }.toSet()
        val claimFields = MatrixNluV3Claim::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(forbidden.intersect(outputFields).isEmpty())
        assertTrue(forbidden.intersect(claimFields).isEmpty())
    }

    @Test
    fun `legacy minimal MatrixTurnFrame remains source compatible and canonical Understanding defaults unavailable`() {
        val legacy = frame()
        assertEquals(MipFieldStatus.UNAVAILABLE, legacy.canonicalUnderstandingV3.status)
        assertTrue(legacy.typedClaims.isEmpty())
    }

    private fun adapterReturning(output: MatrixNluV3Output) = CanonicalUnderstandingV3Adapter(
        runtime = MatrixNluV3RuntimeBridge { request ->
            assertEquals("turn-1", request.turnId)
            assertEquals("session-1", request.sessionId)
            assertEquals("IT", request.language)
            assertEquals(INPUT, request.text)
            assertEquals("user", request.speakerId)
            assertEquals("luna", request.observerId)
            output
        },
        config = CanonicalUnderstandingV3Config(FINGERPRINT),
    )

    private fun frame() = MatrixTurnFrame(
        turnId = "turn-1",
        sessionId = "session-1",
        input = UserMessage(
            text = INPUT,
            speakerId = "user",
            observerId = "luna",
            timestampMillis = 1_788_604_800_000L,
            locale = "it",
        ),
    )

    private fun validOutput(): MatrixNluV3Output {
        val marco = spanOf(INPUT, "Marco")
        val anna = spanOf(INPUT, "Anna")
        val roma = spanOf(INPUT, "Roma")
        val temporal = spanOf(INPUT, "2024")
        val negations = spansOf(INPUT, "non")

        val mentions = listOf(
            MatrixNluV3Mention("m0", marco, "PERSON", "Marco", "marco", "RESOLVED"),
            MatrixNluV3Mention("m1", anna, "PERSON", "Anna", "anna", "RESOLVED"),
            MatrixNluV3Mention("m2", roma, "LOCATION", "Roma", "rome", "RESOLVED"),
        )
        val candidates = listOf(
            MatrixNluV3ReferentCandidate("ctx:speaker", "CONTEXT_SPEAKER", resolvedEntityRef = "user", resolutionStatus = "RESOLVED"),
            MatrixNluV3ReferentCandidate("ctx:observer", "CONTEXT_OBSERVER", resolvedEntityRef = "luna", resolutionStatus = "RESOLVED"),
            MatrixNluV3ReferentCandidate("mention:m0", "MENTION", "m0", marco, "PERSON", "marco", "RESOLVED"),
            MatrixNluV3ReferentCandidate("mention:m1", "MENTION", "m1", anna, "PERSON", "anna", "RESOLVED"),
            MatrixNluV3ReferentCandidate("mention:m2", "MENTION", "m2", roma, "LOCATION", "rome", "RESOLVED"),
        )
        val claim = MatrixNluV3Claim(
            claimId = "c0",
            sourceSpan = listOf(0, INPUT.length),
            subjectSpans = listOf(anna),
            objectSpans = listOf(roma),
            negationCueSpans = negations,
            temporalEvidence = listOf(MatrixNluV3TemporalEvidence("t0", temporal)),
            entityMentionIds = listOf("m0", "m1", "m2"),
            dialogueAct = stringField("ASSERT"),
            predicate = stringField("residence.place"),
            subjectReferent = stringField("mention:m1"),
            targetReferent = MatrixNluV3Field("NONE", 0.98, "NOT_APPLICABLE"),
            ownerReferent = stringField("mention:m1"),
            // Deliberately different from source to prove the roles stay independent.
            perspectiveReferent = stringField("ctx:speaker"),
            sourceReferent = stringField("mention:m0"),
            polarity = stringField("NEGATIVE"),
            temporalRelation = temporalField("AFTER", "temporal:t0"),
            claimKind = stringField("REPORT"),
            fieldStatusByField = mapOf(
                "boundary" to "RESOLVED",
                "subjectSpans" to "RESOLVED",
                "objectSpans" to "RESOLVED",
                "negationCueSpans" to "RESOLVED",
                "temporalEvidence" to "RESOLVED",
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
            structuralStatus = "VALID",
            interpretationStatus = "RESOLVED",
        )

        return MatrixNluV3Output(
            contractVersion = "MATRIX_NLU_CONTRACT_V3",
            contractFingerprintSha256 = FINGERPRINT,
            input = INPUT,
            observationSourceId = "obs-1",
            speakerRef = "user",
            observerRef = "luna",
            mentions = mentions,
            referentCandidates = candidates,
            claims = listOf(claim),
        )
    }

    private fun stringField(value: String) = MatrixNluV3Field(
        value = value,
        confidence = 0.95,
        fieldStatus = "RESOLVED",
    )

    private fun temporalField(relation: String, anchor: String? = null) = MatrixNluV3Field(
        value = MatrixNluV3TemporalRelationValue(relation, anchor),
        confidence = 0.94,
        fieldStatus = "RESOLVED",
    )

    private fun spanOf(text: String, token: String): List<Int> {
        val start = text.indexOf(token)
        require(start >= 0)
        return listOf(start, start + token.length)
    }

    private fun spansOf(text: String, token: String): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var from = 0
        while (true) {
            val start = text.indexOf(token, from)
            if (start < 0) break
            result += listOf(start, start + token.length)
            from = start + token.length
        }
        return result
    }

    companion object {
        private const val INPUT = "Marco dice che Anna non non vive a Roma dopo il 2024."
        private const val FINGERPRINT = "7b0646e44243ad897760c0fcadbe141f1b8e88e3fd8d63a1789106571b9987b0"
    }
}
