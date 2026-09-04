package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import matrix.assembling.adapters.AffectiveLabAdapter
import matrix.assembling.adapters.AffectiveRuntimeBridge
import matrix.assembling.adapters.AffectiveRuntimeOutput

class DiagnosticTraceTest {

    @Test
    fun firstDivergenceIsWriteOnce() {
        val trace = DiagnosticTrace()
            .withInput("test")
            .diverge("UNDERSTANDING.UNRESOLVED_SUBJECT")
            .diverge("AUTHORITY.OWNER_UNRESOLVED")

        assertEquals("UNDERSTANDING.UNRESOLVED_SUBJECT", trace.firstDivergence)
        assertTrue("UNDERSTANDING.UNRESOLVED_SUBJECT" in trace.reasoningChain)
        assertTrue("AUTHORITY.OWNER_UNRESOLVED" in trace.reasoningChain)
        assertTrue("divergence.AUTHORITY.OWNER_UNRESOLVED" in trace.events)
    }

    @Test
    fun unauthorizedAffectivePersistenceIsBlockedAndTraced() {
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: matrix.assembling.adapters.AffectiveRuntimeRequest): AffectiveRuntimeOutput =
                    AffectiveRuntimeOutput(
                        emotions = mapOf("joy" to 0.7),
                        persistentDeltaApplied = true,
                    )
            }
        )
        val turn = MatrixTurnFrame(
            turnId = "trace-affective",
            sessionId = "session-test",
            input = UserMessage("Ciao", "alberto", timestampMillis = 0L),
            semantic = SemanticFrame(
                originalText = "Ciao",
                semanticSummary = "saluto",
                dialogueAct = "ASSERT",
                predicate = "attribute.is",
                polarity = "POSITIVE",
                temporalRelation = "CURRENT",
                subject = "alberto",
                target = null,
                owner = "alberto",
                confidence = mapOf("overall" to 0.90),
            ),
            memoryResult = MemoryAdmissionResult(
                status = "PROVISIONAL_CLAIM",
                stableWrite = false,
                reason = "test",
            ),
            diagnostics = DiagnosticTrace().withInput("Ciao"),
        )

        val result = adapter.update(turn)
        val snapshot = assertNotNull(result.diagnostics.affectiveStimulus)

        assertFalse(result.requireAffective().persistentDeltaAllowed)
        assertEquals("AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION", result.diagnostics.firstDivergence)
        assertEquals("VIOLATION_BLOCKED", snapshot.status)
        assertTrue("PERSISTENCE_ATTEMPT_BLOCKED" in snapshot.reasonCodes)
    }

    @Test
    fun preResponseStableWriteCarriesBoundaryTrace() {
        val orchestrator = MatrixAssemblingOrchestrator(
            nlu = object : NluPort {
                override fun analyze(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                    nlu = NluOutput(
                        dialogueAct = "ASSERT",
                        predicate = "residence.place",
                        polarity = "POSITIVE",
                        temporalRelation = "CURRENT",
                        subjectReferent = "SPEAKER",
                        targetReferent = "NONE",
                        ownerReferent = "SPEAKER",
                        perspectiveReferent = "SPEAKER",
                        confidence = mapOf(
                            "overall" to 0.95,
                            "token.negation" to 0.99,
                            "sequence.predicate" to 0.95,
                            "sequence.subjectReferent" to 0.95,
                            "sequence.targetReferent" to 0.95,
                        ),
                        spans = emptyMap(),
                    )
                )
            },
            understanding = object : UnderstandingPort {
                override fun understand(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                    semantic = SemanticFrame(
                        originalText = turn.input.text,
                        semanticSummary = "residenza",
                        dialogueAct = "ASSERT",
                        predicate = "residence.place",
                        polarity = "POSITIVE",
                        temporalRelation = "CURRENT",
                        subject = "alberto",
                        target = null,
                        owner = "alberto",
                        confidence = turn.requireNlu().confidence,
                    ),
                    typedClaims = listOf(
                        TypedClaim(
                            claimId = "${turn.turnId}:claim:0",
                            ownerId = "alberto",
                            subject = "alberto",
                            predicate = "residence.place",
                            objectValue = "Padova",
                            target = null,
                            polarity = "POSITIVE",
                            temporalRelation = "CURRENT",
                            sourceType = "USER_ASSERTION",
                            confidence = turn.requireNlu().confidence,
                        )
                    ),
                )
            },
            coherence = object : CoherenceGuardPort {
                override fun check(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                    coherenceDecision = CoherenceDecision.SAFE_TO_ADMIT
                )
            },
            authority = object : AuthorityResolverPort {
                override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                    authorityDecision = AuthorityDecision(true, true, "USER_ASSERTION", "NONE", "test")
                )
            },
            memory = object : MemoryAdmissionPort {
                override fun admit(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                    memoryResult = MemoryAdmissionResult(
                        status = "ADMITTED",
                        memoryIds = listOf("illegal-id"),
                        stableWrite = true,
                        reason = "illegal pre-response write",
                    )
                )
            },
            affective = object : AffectivePort {
                override fun update(turn: MatrixTurnFrame): MatrixTurnFrame = turn
            },
            promptBuilder = object : SemanticFrameToPromptPort {
                override fun buildPrompt(turn: MatrixTurnFrame): MatrixTurnFrame = turn
            },
            gguf = object : GgufPort {
                override fun generate(turn: MatrixTurnFrame): MatrixTurnFrame = turn
            },
        )

        val failure = assertFailsWith<MatrixBoundaryViolationException> {
            orchestrator.handle(
                UserMessage("Vivo a Padova", "alberto", timestampMillis = 0L),
                "trace-memory",
                "session-test",
            )
        }

        assertEquals("MEMORY.PRE_RESPONSE_STABLE_WRITE", failure.diagnosticTrace.firstDivergence)
        assertEquals("VIOLATION", failure.diagnosticTrace.tags["memory.pre_response_boundary"])
        assertTrue("INPUT_ACCEPTED" in failure.diagnosticTrace.reasoningChain)
    }
}
