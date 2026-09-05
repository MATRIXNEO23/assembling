package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import matrix.assembling.adapters.AffectiveLabAdapter
import matrix.assembling.adapters.AffectiveRuntimeBridge
import matrix.assembling.adapters.AffectiveRuntimeOutput
import matrix.assembling.adapters.BasicAuthorityResolver
import matrix.assembling.adapters.BasicCoherenceGuard
import matrix.assembling.adapters.EchoGgufAdapter
import matrix.assembling.adapters.MatrixNluClaim
import matrix.assembling.adapters.MatrixNluInterpretation
import matrix.assembling.adapters.MatrixNluRequest
import matrix.assembling.adapters.MatrixNluRuntimeBridge
import matrix.assembling.adapters.NoPersistentMemoryAdmission
import matrix.assembling.adapters.UnderstandingLabAdapter

class MatrixAssemblingOrchestratorTest {

    @Test
    fun canonicalFlowProducesCompleteTraceWithoutDurableMemory() {
        val orchestrator = orchestrator()

        val completed = orchestrator.handle(baseTurn("Vivo a Padova"))

        val trace = completed.diagnostics
        assertNotNull(trace.observation)
        assertNotNull(trace.understandingResult)
        assertNotNull(trace.coherenceResult)
        assertNotNull(trace.authorityResolution)
        assertNotNull(trace.admissionDecision)
        assertNotNull(trace.memory)
        assertNotNull(trace.affectiveStimulus)
        assertNotNull(trace.promptResult)
        assertNotNull(trace.ggufResult)
        assertEquals(DiagnosticStatus.NON_CABLATO, trace.outputValidation?.status)
        assertFalse(completed.requireMemory().stableWrite)
        assertTrue(completed.requireMemory().memoryIds.isEmpty())
        assertNotNull(completed.reply?.diagnostics)
        assertTrue("turn.completed" in trace.events)
    }

    @Test
    fun preResponseStableWriteIsRejectedAtExactBoundary() {
        val badPreflight = object : MemoryPreflightPort {
            override fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                memoryResult = MemoryAdmissionResult(
                    status = "ADMITTED",
                    memoryIds = listOf("illegal-memory-id"),
                    stableWrite = true,
                    reason = "malformed preflight",
                ),
            )
        }
        val orchestrator = orchestrator(memory = badPreflight)

        val failure = assertFailsWith<MatrixPipelineException> {
            orchestrator.handle(baseTurn("Vivo a Padova"))
        }

        assertEquals("MEMORY_PREFLIGHT", failure.stage)
        assertEquals("MEMORY_PREFLIGHT.UNAUTHORIZED_STABLE_WRITE", failure.failedFrame.diagnostics.firstDivergence)
        assertFalse(failure.failedFrame.requireMemory().stableWrite)
        assertTrue(failure.failedFrame.requireMemory().memoryIds.isEmpty())
    }

    @Test
    fun moduleExceptionRetainsFirstFailureStage() {
        val failingAuthority = object : AuthorityResolverPort {
            override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame = error("authority exploded")
        }
        val orchestrator = orchestrator(authority = failingAuthority)

        val failure = assertFailsWith<MatrixPipelineException> {
            orchestrator.handle(baseTurn("Vivo a Padova"))
        }

        assertEquals("AUTHORITY", failure.stage)
        assertEquals("AUTHORITY.EXCEPTION", failure.failedFrame.diagnostics.firstDivergence)
        assertEquals(DiagnosticStatus.ERROR, failure.failedFrame.diagnostics.authorityResolution?.status)
        assertTrue("SEMANTIC_EVIDENCE_PRESERVED" in failure.failedFrame.diagnostics.reasoningChain)
    }

    @Test
    fun firstDivergenceIsNeverOverwritten() {
        val trace = DiagnosticTrace()
            .diverge("UNDERSTANDING.UNRESOLVED_SUBJECT")
            .diverge("AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION")

        assertEquals("UNDERSTANDING.UNRESOLVED_SUBJECT", trace.firstDivergence)
        assertTrue("AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION" in trace.reasoningChain)
    }

    private fun orchestrator(
        memory: MemoryPreflightPort = NoPersistentMemoryAdmission(),
        authority: AuthorityResolverPort = BasicAuthorityResolver(),
    ): MatrixAssemblingOrchestrator {
        val bridge = object : MatrixNluRuntimeBridge {
            override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                engine = "static-test-nlu",
                status = "OK",
                claims = listOf(
                    MatrixNluClaim(
                        dialogueAct = "ASSERT",
                        predicate = "residence.place",
                        polarity = "POSITIVE",
                        temporalRelation = "CURRENT",
                        subjectReferent = "SPEAKER",
                        targetReferent = "NONE",
                        ownerReferent = "SPEAKER",
                        perspectiveReferent = "SPEAKER",
                        confidence = 0.95,
                        confidenceByHead = mapOf(
                            "token.negation" to 0.95,
                            "sequence.predicate" to 0.95,
                            "sequence.subjectReferent" to 0.95,
                            "sequence.targetReferent" to 0.95,
                        ),
                        subject = "alberto",
                        owner = "alberto",
                        perspective = "alberto",
                        objectValue = "Padova",
                        sourceType = "USER_ASSERTION",
                    ),
                ),
            )
        }
        val affectiveBridge = object : AffectiveRuntimeBridge {
            override fun update(request: matrix.assembling.adapters.AffectiveRuntimeRequest): AffectiveRuntimeOutput =
                AffectiveRuntimeOutput(emotions = mapOf("interest" to 0.4))
        }
        val understandingAdapter = UnderstandingLabAdapter(bridge)
        return MatrixAssemblingOrchestrator(
            nlu = understandingAdapter,
            understanding = understandingAdapter,
            coherence = BasicCoherenceGuard(),
            authority = authority,
            memory = memory,
            affective = AffectiveLabAdapter(affectiveBridge),
            promptBuilder = SemanticFrameToPrompt(),
            gguf = EchoGgufAdapter(),
        )
    }

    private fun baseTurn(text: String): MatrixTurnFrame = MatrixTurnFrame(
        turnId = "turn-test",
        sessionId = "session-test",
        input = UserMessage(
            text = text,
            speakerId = "alberto",
            observerId = "luna",
            timestampMillis = 0L,
            locale = "it",
        ),
    )
}
