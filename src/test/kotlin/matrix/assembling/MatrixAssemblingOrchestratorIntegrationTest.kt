package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.adapters.BasicAffectiveAdapter
import matrix.assembling.adapters.BasicAuthorityResolver
import matrix.assembling.adapters.BasicCoherenceGuard
import matrix.assembling.adapters.EchoGgufAdapter
import matrix.assembling.adapters.MatrixNluClaim
import matrix.assembling.adapters.MatrixNluInterpretation
import matrix.assembling.adapters.MatrixNluRequest
import matrix.assembling.adapters.MatrixNluRuntimeBridge
import matrix.assembling.adapters.NoPersistentMemoryAdmission
import matrix.assembling.adapters.UnderstandingLabAdapter

class MatrixAssemblingOrchestratorIntegrationTest {

    @Test
    fun negationRefusalCrossesCanonicalBoundariesWithoutStableMemory() {
        val result = runTurn(
            text = "Non voglio uscire con Marco",
            claim = claim(
                predicate = "consent.refuse",
                polarity = "NEGATIVE",
                subject = "alberto",
                target = "Marco",
                objectValue = "uscire con Marco",
            ),
        )

        assertEquals("NEGATIVE", result.requireSemantic().polarity)
        assertFalse(result.requireMemory().stableWrite)
        assertContains(result.requirePrompt().text, "Non invertire negazioni")
        assertTraceCompleteThroughAffective(result)
    }

    @Test
    fun thirdPartyReportRemainsIndirectAndNonPersistent() {
        val result = runTurn(
            text = "Marco dice che Sara mi odia",
            claim = claim(
                predicate = "preference.like",
                polarity = "NEGATIVE",
                subject = "Sara",
                target = "alberto",
                owner = "Marco",
                perspective = "Marco",
                objectValue = "alberto",
                sourceType = "THIRD_PARTY_REPORT",
            ),
        )

        assertEquals(CoherenceDecision.REPORT_ONLY, result.requireCoherence())
        assertFalse(result.requireAuthority().accepted)
        assertEquals("THIRD_PARTY_REPORT", result.requireAuthority().sourceType)
        assertFalse(result.requireMemory().stableWrite)
        assertEquals("THIRD_PARTY_REPORT", result.diagnostics.authorityResolution?.metadata?.get("sourceType"))
        assertTraceCompleteThroughAffective(result)
    }

    @Test
    fun requestStaysTransientAndReachesResponse() {
        val result = runTurn(
            text = "Vieni con me al bar?",
            claim = claim(
                dialogueAct = "REQUEST",
                predicate = "goal.object",
                subject = "alberto",
                target = "luna",
                objectValue = "bar",
                sourceType = "TURN_INTENT",
            ),
        )

        assertEquals(CoherenceDecision.SAFE_TRANSIENT_ONLY, result.requireCoherence())
        assertFalse(result.requireMemory().stableWrite)
        assertNotNull(result.reply)
        assertTraceCompleteThroughAffective(result)
    }

    @Test
    fun directAssertionIsSemanticallyAdmissibleButBackendRemainsDisabled() {
        val result = runTurn(
            text = "Vivo a Padova",
            claim = claim(
                predicate = "residence.place",
                subject = "alberto",
                objectValue = "Padova",
            ),
        )

        assertEquals(CoherenceDecision.SAFE_TO_ADMIT, result.requireCoherence())
        assertTrue(result.requireAuthority().accepted)
        assertEquals("PROVISIONAL_CLAIM", result.requireMemory().status)
        assertFalse(result.requireMemory().stableWrite)
        assertTrue(result.requireMemory().memoryIds.isEmpty())
        assertEquals("DISABLED", result.diagnostics.memoryResult?.metadata?.get("backend"))
        assertTraceCompleteThroughAffective(result)
    }

    @Test
    fun adultIntimacySemanticSignalIsNotAutomaticBlock() {
        val result = runTurn(
            text = "Ti do il mio consenso",
            claim = claim(
                predicate = "consent.grant",
                subject = "alberto",
                target = "luna",
                objectValue = "consenso",
                adultOrIntimacy = true,
            ),
        )

        assertTrue(result.requireSemantic().adultOrIntimacy)
        assertTrue(result.requireCoherence() != CoherenceDecision.REJECTED_UNSAFE)
        assertFalse(result.requireMemory().stableWrite)
        assertNotNull(result.reply)
        assertEquals("NLU_EXPLICIT", result.diagnostics.tags["understanding_lab.adult_intimacy_source"])
        assertTraceCompleteThroughAffective(result)
    }

    @Test
    fun outputValidatorRunsAfterGgufWhenProvided() {
        var observedReply: String? = null
        val validator = object : OutputValidatorPort {
            override fun validate(turn: MatrixTurnFrame): MatrixTurnFrame {
                observedReply = turn.reply?.text
                return turn.copy(
                    diagnostics = turn.diagnostics.tag("test.output_validator", "PASS"),
                )
            }
        }

        val result = runTurn(
            text = "Vivo a Padova",
            claim = claim(
                predicate = "residence.place",
                subject = "alberto",
                objectValue = "Padova",
            ),
            outputValidator = validator,
        )

        assertNotNull(observedReply)
        assertEquals("PASS", result.diagnostics.tags["test.output_validator"])
        assertEquals("EXECUTED", result.diagnostics.tags["output.validation"])
        assertTrue("OUTPUT_VALIDATOR_EXECUTED" in result.diagnostics.reasoningChain)
    }

    private fun runTurn(
        text: String,
        claim: MatrixNluClaim,
        outputValidator: OutputValidatorPort? = null,
    ): MatrixTurnFrame {
        val understanding = UnderstandingLabAdapter(
            object : MatrixNluRuntimeBridge {
                override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                    engine = "integration-static-nlu",
                    status = "OK",
                    claims = listOf(claim),
                )
            }
        )
        val orchestrator = MatrixAssemblingOrchestrator(
            nlu = understanding,
            understanding = understanding,
            coherence = BasicCoherenceGuard(),
            authority = BasicAuthorityResolver(),
            memory = NoPersistentMemoryAdmission(),
            affective = BasicAffectiveAdapter(),
            promptBuilder = SemanticFrameToPrompt(),
            gguf = EchoGgufAdapter(),
            outputValidator = outputValidator,
        )
        return orchestrator.handle(
            MatrixTurnFrame(
                turnId = "turn-${text.hashCode()}",
                sessionId = "session-integration",
                input = UserMessage(text, "alberto", "luna", 0L, "it"),
            )
        )
    }

    private fun assertTraceCompleteThroughAffective(result: MatrixTurnFrame) {
        assertEquals(result.input.text, result.diagnostics.inputOriginale)
        assertNotNull(result.diagnostics.observation)
        assertNotNull(result.diagnostics.understandingResult)
        assertNotNull(result.diagnostics.authorityResolution)
        assertNotNull(result.diagnostics.admissionDecision)
        assertNotNull(result.diagnostics.memoryResult)
        assertNotNull(result.diagnostics.affectiveStimulus)
        assertTrue(result.diagnostics.reasoningChain.isNotEmpty())
        assertTrue("turn.completed" in result.diagnostics.events)
        assertNull(result.diagnostics.memoryId)
        assertEquals("NON_CABLATO", result.diagnostics.tags["output.validation"])
    }

    private fun claim(
        dialogueAct: String = "ASSERT",
        predicate: String,
        polarity: String = "POSITIVE",
        subject: String,
        target: String? = null,
        owner: String = "alberto",
        perspective: String = owner,
        objectValue: String? = null,
        sourceType: String = "USER_ASSERTION",
        adultOrIntimacy: Boolean? = null,
    ): MatrixNluClaim = MatrixNluClaim(
        dialogueAct = dialogueAct,
        predicate = predicate,
        polarity = polarity,
        temporalRelation = "CURRENT",
        subjectReferent = if (subject == "alberto") "SPEAKER" else "KNOWN_ENTITY",
        targetReferent = when (target) {
            null -> "NONE"
            "alberto" -> "SPEAKER"
            "luna" -> "OBSERVER"
            else -> "KNOWN_ENTITY"
        },
        ownerReferent = if (owner == "alberto") "SPEAKER" else "KNOWN_ENTITY",
        perspectiveReferent = if (perspective == "alberto") "SPEAKER" else "KNOWN_ENTITY",
        confidence = 0.95,
        confidenceByHead = mapOf(
            "token.negation" to 0.97,
            "sequence.predicate" to 0.95,
            "sequence.subjectReferent" to 0.95,
            "sequence.targetReferent" to 0.95,
        ),
        subject = subject,
        target = target,
        owner = owner,
        perspective = perspective,
        objectValue = objectValue,
        sourceType = sourceType,
        worldTruth = false,
        adultOrIntimacy = adultOrIntimacy,
    )
}
