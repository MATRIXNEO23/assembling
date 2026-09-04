package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import matrix.assembling.adapters.MatrixNluClaim
import matrix.assembling.adapters.MatrixNluInterpretation
import matrix.assembling.adapters.MatrixNluRequest
import matrix.assembling.adapters.MatrixNluRuntimeBridge
import matrix.assembling.adapters.UnderstandingLabAdapter

class ArchitectureBoundaryTest {

    @Test
    fun understandingPreservesAllClaimsInsteadOfDroppingAfterFirst() {
        val runtime = object : MatrixNluRuntimeBridge {
            override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                engine = "boundary-test",
                status = "OK",
                claims = listOf(
                    claim(subject = "Marco", polarity = "POSITIVE", objectValue = "domani"),
                    claim(subject = "Sara", polarity = "NEGATIVE", objectValue = "domani"),
                ),
            )
        }
        val adapter = UnderstandingLabAdapter(runtime)
        val input = MatrixTurnFrame(
            turnId = "turn-multi",
            sessionId = "session-test",
            input = UserMessage(
                text = "Marco viene domani ma Sara non viene",
                speakerId = "alberto",
                observerId = "luna",
                timestampMillis = 0L,
            ),
        )

        val result = adapter.understand(adapter.analyze(input))

        assertEquals(2, result.typedClaims.size)
        assertEquals("Marco", result.typedClaims[0].subject)
        assertEquals("Sara", result.typedClaims[1].subject)
        assertEquals("NEGATIVE", result.typedClaims[1].polarity)
    }

    @Test
    fun missingCriticalConfidenceFailsClosed() {
        val turn = MatrixTurnFrame(
            turnId = "turn-confidence",
            sessionId = "session-test",
            input = UserMessage("Vivo a Padova", "alberto", timestampMillis = 0L),
            semantic = SemanticFrame(
                originalText = "Vivo a Padova",
                semanticSummary = "residenza",
                dialogueAct = "ASSERT",
                predicate = "residence.place",
                polarity = "POSITIVE",
                temporalRelation = "CURRENT",
                subject = "alberto",
                target = null,
                owner = "alberto",
                confidence = mapOf("overall" to 0.95),
            ),
            typedClaims = listOf(
                TypedClaim(
                    claimId = "turn-confidence:claim:0",
                    ownerId = "alberto",
                    subject = "alberto",
                    predicate = "residence.place",
                    objectValue = "Padova",
                    target = null,
                    polarity = "POSITIVE",
                    temporalRelation = "CURRENT",
                    sourceType = "USER_ASSERTION",
                    confidence = mapOf("overall" to 0.95),
                )
            ),
        )

        val checked = matrix.assembling.adapters.BasicCoherenceGuard().check(turn)

        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, checked.requireCoherence())
    }

    @Test
    fun orchestratorRejectsStableWriteBeforeResponsePhase() {
        val nlu = object : NluPort {
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
        }
        val understanding = object : UnderstandingPort {
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
        }
        val coherence = object : CoherenceGuardPort {
            override fun check(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                coherenceDecision = CoherenceDecision.SAFE_TO_ADMIT
            )
        }
        val authority = object : AuthorityResolverPort {
            override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                authorityDecision = AuthorityDecision(
                    accepted = true,
                    ownerResolved = true,
                    sourceType = "USER_ASSERTION",
                    conflictStatus = "NONE",
                    reason = "test",
                )
            )
        }
        val memory = object : MemoryAdmissionPort {
            override fun admit(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                memoryResult = MemoryAdmissionResult(
                    status = "ADMITTED",
                    memoryIds = listOf("illegal-pre-response-id"),
                    stableWrite = true,
                    reason = "illegal pre-response stable write",
                )
            )
        }
        val affective = object : AffectivePort {
            override fun update(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
                affectiveState = AffectiveState("external", "neutral", false)
            )
        }
        val prompt = object : SemanticFrameToPromptPort {
            override fun buildPrompt(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(prompt = GgufPrompt("test"))
        }
        val gguf = object : GgufPort {
            override fun generate(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(reply = AssistantReply("test"))
        }
        val orchestrator = MatrixAssemblingOrchestrator(
            nlu,
            understanding,
            coherence,
            authority,
            memory,
            affective,
            prompt,
            gguf,
        )

        assertFailsWith<IllegalStateException> {
            orchestrator.handle(
                UserMessage("Vivo a Padova", "alberto", timestampMillis = 0L),
                "turn-stable-write",
                "session-test",
            )
        }
    }

    private fun claim(subject: String, polarity: String, objectValue: String): MatrixNluClaim = MatrixNluClaim(
        dialogueAct = "ASSERT",
        predicate = "presence.reported",
        polarity = polarity,
        temporalRelation = "FUTURE",
        subjectReferent = "KNOWN_ENTITY",
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
        subject = subject,
        owner = "alberto",
        perspective = "alberto",
        objectValue = objectValue,
        sourceType = "USER_ASSERTION",
    )
}
