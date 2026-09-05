package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import matrix.assembling.adapters.BasicAuthorityResolver
import matrix.assembling.adapters.BasicCoherenceGuard
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

        var result = adapter.understand(adapter.analyze(input))
        result = BasicCoherenceGuard().check(result)
        result = BasicAuthorityResolver().resolve(result)

        assertEquals(2, result.typedClaims.size)
        assertEquals("Marco", result.typedClaims[0].subject)
        assertEquals("Sara", result.typedClaims[1].subject)
        assertEquals("NEGATIVE", result.typedClaims[1].polarity)
        assertEquals(CoherenceDecision.SAFE_TRANSIENT_ONLY, result.requireCoherence())
        assertEquals("MULTI_CLAIM", result.requireAuthority().sourceType)
        assertFalse(result.requireAuthority().accepted)
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

        val checked = BasicCoherenceGuard().check(turn)

        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, checked.requireCoherence())
        assertEquals("COHERENCE.MISSING_CRITICAL_CONFIDENCE", checked.diagnostics.firstDivergence)
    }

    @Test
    fun secondaryClaimMissingCriticalConfidenceAlsoFailsClosed() {
        val complete = criticalConfidence()
        val incomplete = complete - "token.negation"
        val runtime = object : MatrixNluRuntimeBridge {
            override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                engine = "boundary-test",
                status = "OK",
                claims = listOf(
                    claim(subject = "Marco", polarity = "POSITIVE", objectValue = "domani", confidenceByHead = complete),
                    claim(subject = "Sara", polarity = "NEGATIVE", objectValue = "domani", confidenceByHead = incomplete),
                ),
            )
        }
        val adapter = UnderstandingLabAdapter(runtime)
        var turn = adapter.understand(
            adapter.analyze(
                MatrixTurnFrame(
                    turnId = "turn-secondary-confidence",
                    sessionId = "session-test",
                    input = UserMessage("Marco viene, Sara non viene", "alberto", timestampMillis = 0L),
                ),
            ),
        )

        turn = BasicCoherenceGuard().check(turn)

        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, turn.requireCoherence())
        assertEquals("COHERENCE.MISSING_CRITICAL_CONFIDENCE", turn.diagnostics.firstDivergence)
        assertTrue(
            turn.diagnostics.tags["coherence.missing_critical_confidence"]
                ?.contains("claim[1].token.negation") == true,
        )
    }

    @Test
    fun explicitAdultIntimacyMarkerIsPreferredOverKeywordFallback() {
        val runtime = object : MatrixNluRuntimeBridge {
            override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                engine = "boundary-test",
                status = "OK",
                claims = listOf(
                    claim(
                        subject = "alberto",
                        polarity = "POSITIVE",
                        objectValue = "messaggio neutro",
                        adultOrIntimacy = true,
                    ),
                ),
            )
        }
        val adapter = UnderstandingLabAdapter(runtime)
        val turn = adapter.understand(
            adapter.analyze(
                MatrixTurnFrame(
                    turnId = "turn-adult-marker",
                    sessionId = "session-test",
                    input = UserMessage("messaggio neutro", "alberto", timestampMillis = 0L),
                ),
            ),
        )

        assertTrue(turn.requireSemantic().adultOrIntimacy)
        assertEquals("NLU_EXPLICIT", turn.diagnostics.tags["understanding_lab.adult_intimacy_source"])
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
                    confidence = criticalConfidence(),
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
        val memory = object : MemoryPreflightPort {
            override fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame = turn.copy(
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

        assertFailsWith<MatrixBoundaryViolationException> {
            orchestrator.handle(
                UserMessage("Vivo a Padova", "alberto", timestampMillis = 0L),
                "turn-stable-write",
                "session-test",
            )
        }
    }

    private fun claim(
        subject: String,
        polarity: String,
        objectValue: String,
        confidenceByHead: Map<String, Double> = criticalConfidence(),
        adultOrIntimacy: Boolean? = null,
    ): MatrixNluClaim = MatrixNluClaim(
        dialogueAct = "ASSERT",
        predicate = "presence.reported",
        polarity = polarity,
        temporalRelation = "FUTURE",
        subjectReferent = if (subject == "alberto") "SPEAKER" else "KNOWN_ENTITY",
        targetReferent = "NONE",
        ownerReferent = "SPEAKER",
        perspectiveReferent = "SPEAKER",
        confidence = 0.95,
        confidenceByHead = confidenceByHead,
        subject = subject,
        owner = "alberto",
        perspective = "alberto",
        objectValue = objectValue,
        sourceType = "USER_ASSERTION",
        adultOrIntimacy = adultOrIntimacy,
    )

    private fun criticalConfidence(): Map<String, Double> = mapOf(
        "overall" to 0.95,
        "token.negation" to 0.99,
        "sequence.predicate" to 0.95,
        "sequence.subjectReferent" to 0.95,
        "sequence.targetReferent" to 0.95,
    )
}
