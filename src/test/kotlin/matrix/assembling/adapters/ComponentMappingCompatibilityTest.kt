package matrix.assembling.adapters

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.CoherenceDecision
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryAdmissionResult
import matrix.assembling.SemanticFrame
import matrix.assembling.UserMessage

class ComponentMappingCompatibilityTest {

    @Test
    fun understandingPreservesResolvedFieldsWithoutOwningMemoryAdmission() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                claim(
                    predicate = "residence.place",
                    subject = "alberto",
                    owner = "alberto",
                    perspective = "alberto",
                    objectValue = "Padova",
                    worldTruth = true,
                ),
            ),
        )

        val turn = adapter.understand(adapter.analyze(baseTurn("Vivo a Padova")))
        val nlu = turn.requireNlu()
        val semantic = turn.requireSemantic()
        val typed = turn.typedClaims.single()

        assertEquals(1, turn.nluClaims.size)
        assertEquals("alberto", nlu.resolvedSubject)
        assertEquals("Padova", nlu.objectValue)
        assertEquals("alberto", semantic.subject)
        assertEquals("Padova", typed.objectValue)
        assertEquals("alberto", typed.ownerId)
        assertTrue(typed.worldTruth)
        assertFalse(semantic.stableMemoryAllowed)
        assertEquals("DEFERRED", turn.diagnostics.tags["understanding_lab.memory_authority"])
    }

    @Test
    fun thirdPartyReportCannotBecomeDirectAuthority() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                claim(
                    predicate = "preference.like",
                    polarity = "NEGATIVE",
                    subjectReferent = "KNOWN_ENTITY",
                    targetReferent = "SPEAKER",
                    ownerReferent = "KNOWN_ENTITY",
                    perspectiveReferent = "KNOWN_ENTITY",
                    subject = "Marco",
                    target = "alberto",
                    owner = "Marco",
                    perspective = "Marco",
                    objectValue = "alberto",
                    sourceType = "THIRD_PARTY_REPORT",
                ),
            ),
        )

        var turn = adapter.understand(adapter.analyze(baseTurn("Marco dice che non gli piaccio")))
        turn = BasicCoherenceGuard().check(turn)
        turn = BasicAuthorityResolver().resolve(turn)

        assertEquals(CoherenceDecision.REPORT_ONLY, turn.requireCoherence())
        assertFalse(turn.requireAuthority().accepted)
        assertEquals("THIRD_PARTY_REPORT", turn.requireAuthority().sourceType)
        assertTrue("AUTHORITY_DIRECT_REJECTED_THIRD_PARTY" in turn.diagnostics.reasoningChain)
    }

    @Test
    fun multipleClaimsArePreservedAndHeldTransiently() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                claim(
                    predicate = "presence.reported",
                    subject = "Marco",
                    owner = "alberto",
                    perspective = "alberto",
                    objectValue = "domani",
                ),
                claim(
                    predicate = "presence.reported",
                    polarity = "NEGATIVE",
                    subject = "Sara",
                    owner = "alberto",
                    perspective = "alberto",
                    objectValue = "domani",
                ),
            ),
        )

        var turn = adapter.understand(adapter.analyze(baseTurn("Marco viene domani, ma Sara non viene")))
        turn = BasicCoherenceGuard().check(turn)
        turn = BasicAuthorityResolver().resolve(turn)

        assertEquals(2, turn.nluClaims.size)
        assertEquals(2, turn.typedClaims.size)
        assertEquals(listOf("Marco", "Sara"), turn.typedClaims.map { it.subject })
        assertEquals(listOf("POSITIVE", "NEGATIVE"), turn.typedClaims.map { it.polarity })
        assertEquals(CoherenceDecision.SAFE_TRANSIENT_ONLY, turn.requireCoherence())
        assertEquals("MULTI_CLAIM", turn.requireAuthority().sourceType)
        assertFalse(turn.requireAuthority().accepted)
    }

    @Test
    fun missingCriticalConfidenceFailsClosed() {
        val turn = baseTurn("Vivo a Padova").copy(
            semantic = semantic(confidence = mapOf("overall" to 0.95)),
            typedClaims = listOf(
                matrix.assembling.TypedClaim(
                    claimId = "c1",
                    ownerId = "alberto",
                    subject = "alberto",
                    predicate = "residence.place",
                    objectValue = "Padova",
                    target = null,
                    polarity = "POSITIVE",
                    temporalRelation = "CURRENT",
                    sourceType = "USER_ASSERTION",
                    confidence = mapOf("overall" to 0.95),
                ),
            ),
        )

        val checked = BasicCoherenceGuard().check(turn)

        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, checked.requireCoherence())
        assertEquals("COHERENCE.MISSING_CRITICAL_CONFIDENCE", checked.diagnostics.firstDivergence)
        assertTrue("MISSING_CRITICAL_CONFIDENCE" in checked.diagnostics.reasoningChain)
    }

    @Test
    fun canonicalTokenNegationConfidenceIsEnforced() {
        val confidence = criticalConfidence() + ("token.negation" to 0.40)
        val turn = baseTurn("Non voglio uscire").copy(
            semantic = semantic(
                predicate = "goal.object",
                polarity = "NEGATIVE",
                confidence = confidence,
            ),
            typedClaims = listOf(
                matrix.assembling.TypedClaim(
                    claimId = "c1",
                    ownerId = "alberto",
                    subject = "alberto",
                    predicate = "goal.object",
                    objectValue = null,
                    target = null,
                    polarity = "NEGATIVE",
                    temporalRelation = "CURRENT",
                    sourceType = "USER_ASSERTION",
                    confidence = confidence,
                ),
            ),
        )

        val checked = BasicCoherenceGuard().check(turn)
        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, checked.requireCoherence())
    }

    @Test
    fun unresolvedSubjectIsNotSilentlyReplacedWithSpeaker() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                claim(
                    subjectReferent = "UNKNOWN",
                    ownerReferent = "SPEAKER",
                    perspectiveReferent = "SPEAKER",
                    subject = null,
                    owner = "alberto",
                    perspective = "alberto",
                ),
            ),
        )

        var turn = adapter.understand(adapter.analyze(baseTurn("È arrivato")))
        assertEquals("UNKNOWN", turn.typedClaims.single().subject)
        assertEquals("UNDERSTANDING.UNRESOLVED_SUBJECT", turn.diagnostics.firstDivergence)

        turn = BasicCoherenceGuard().check(turn)
        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, turn.requireCoherence())
    }

    @Test
    fun affectiveDoesNotApplyPersistentDeltaWithoutAdmission() {
        var captured: AffectiveRuntimeRequest? = null
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput {
                    captured = request
                    return AffectiveRuntimeOutput(emotions = mapOf("hope" to 0.7))
                }
            },
        )

        val turn = adapter.update(
            baseTurn("Vieni con me?").copy(
                semantic = semantic(dialogueAct = "REQUEST", predicate = "goal.object"),
                memoryResult = MemoryAdmissionResult(
                    status = "PROVISIONAL_CLAIM",
                    stableWrite = false,
                    reason = "preflight only",
                ),
            ),
        )

        val request = assertNotNull(captured)
        assertFalse(request.persistentAllowed)
        assertEquals("hope", request.impulse?.emotionType)
        assertNull(request.impulse?.targetId)
        assertFalse(turn.requireAffective().persistentDeltaAllowed)
        assertEquals("EXTERNAL", turn.diagnostics.tags["affective_lab.relationship_owner"])
    }

    @Test
    fun affectivePersistenceDependsOnAdmittedEvent() {
        var captured: AffectiveRuntimeRequest? = null
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput {
                    captured = request
                    return AffectiveRuntimeOutput(persistentDeltaApplied = request.persistentAllowed)
                }
            },
        )

        val turn = adapter.update(
            baseTurn("Mi piace Luna").copy(
                semantic = semantic(predicate = "preference.like"),
                memoryResult = MemoryAdmissionResult(
                    status = "ADMITTED",
                    stableWrite = true,
                    reason = "post-validation admitted event simulation",
                ),
            ),
        )

        val request = assertNotNull(captured)
        assertTrue(request.persistentAllowed)
        assertEquals("alberto", request.impulse?.targetId)
        assertTrue(turn.requireAffective().persistentDeltaAllowed)
    }

    @Test
    fun affectiveBlocksUnauthorizedRuntimePersistenceAndIgnoresRelationshipProjection() {
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput = AffectiveRuntimeOutput(
                    persistentDeltaApplied = true,
                    relationshipSummary = "Falso RelationshipState dal runtime affettivo",
                )
            },
        )

        val turn = adapter.update(
            baseTurn("Ciao").copy(
                semantic = semantic(),
                memoryResult = MemoryAdmissionResult(
                    status = "PROVISIONAL_CLAIM",
                    stableWrite = false,
                    reason = "preflight only",
                ),
            ),
        )

        assertFalse(turn.requireAffective().persistentDeltaAllowed)
        assertTrue(turn.requireAffective().relationshipSummary.contains("NON_CABLATO"))
        assertEquals("AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION", turn.diagnostics.firstDivergence)
        assertTrue("AFFECTIVE_RELATIONSHIP_PROJECTION_IGNORED" in turn.diagnostics.reasoningChain)
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

    private fun semantic(
        dialogueAct: String = "ASSERT",
        predicate: String = "residence.place",
        polarity: String = "POSITIVE",
        confidence: Map<String, Double> = criticalConfidence(),
    ): SemanticFrame = SemanticFrame(
        originalText = "test",
        semanticSummary = "test",
        dialogueAct = dialogueAct,
        predicate = predicate,
        polarity = polarity,
        temporalRelation = "CURRENT",
        subject = "alberto",
        target = null,
        owner = "alberto",
        confidence = confidence,
    )

    private fun claim(
        dialogueAct: String = "ASSERT",
        predicate: String = "residence.place",
        polarity: String = "POSITIVE",
        temporalRelation: String = "CURRENT",
        subjectReferent: String = "SPEAKER",
        targetReferent: String = "NONE",
        ownerReferent: String = "SPEAKER",
        perspectiveReferent: String = "SPEAKER",
        subject: String? = "alberto",
        target: String? = null,
        owner: String? = "alberto",
        perspective: String? = "alberto",
        objectValue: String? = null,
        sourceType: String = "USER_ASSERTION",
        worldTruth: Boolean = false,
    ): MatrixNluClaim = MatrixNluClaim(
        dialogueAct = dialogueAct,
        predicate = predicate,
        polarity = polarity,
        temporalRelation = temporalRelation,
        subjectReferent = subjectReferent,
        targetReferent = targetReferent,
        ownerReferent = ownerReferent,
        perspectiveReferent = perspectiveReferent,
        confidence = 0.95,
        confidenceByHead = criticalConfidence(),
        subject = subject,
        target = target,
        owner = owner,
        perspective = perspective,
        objectValue = objectValue,
        sourceType = sourceType,
        worldTruth = worldTruth,
    )

    private fun criticalConfidence(): Map<String, Double> = mapOf(
        "token.negation" to 0.95,
        "sequence.predicate" to 0.95,
        "sequence.subjectReferent" to 0.95,
        "sequence.targetReferent" to 0.95,
        "overall" to 0.95,
    )

    private class StaticNluRuntime(
        vararg claims: MatrixNluClaim,
    ) : MatrixNluRuntimeBridge {
        private val claims = claims.toList()

        override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
            engine = "static-test-nlu",
            status = "OK",
            claims = claims,
        )
    }
}
