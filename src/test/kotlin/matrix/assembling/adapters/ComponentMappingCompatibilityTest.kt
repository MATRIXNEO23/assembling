package matrix.assembling.adapters

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.AffectiveState
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryAdmissionResult
import matrix.assembling.SemanticFrame
import matrix.assembling.UserMessage

class ComponentMappingCompatibilityTest {

    @Test
    fun understandingAdapterPreservesResolvedClaimFields() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                MatrixNluClaim(
                    dialogueAct = "ASSERT",
                    predicate = "residence.place",
                    polarity = "POSITIVE",
                    temporalRelation = "CURRENT",
                    subjectReferent = "SPEAKER",
                    targetReferent = "UNKNOWN",
                    ownerReferent = "SPEAKER",
                    perspectiveReferent = "SPEAKER",
                    confidence = 0.93,
                    confidenceByHead = mapOf(
                        "sequence.predicate" to 0.91,
                        "sequence.subjectReferent" to 0.94,
                        "sequence.targetReferent" to 0.90,
                    ),
                    subject = "alberto",
                    target = null,
                    owner = "alberto",
                    perspective = "alberto",
                    objectValue = "Padova",
                    sourceType = "USER_ASSERTION",
                    worldTruth = true,
                )
            )
        )

        val turn = adapter.understand(adapter.analyze(baseTurn("Vivo a Padova")))
        val nlu = turn.requireNlu()
        val semantic = turn.requireSemantic()
        val claim = turn.typedClaims.single()

        assertEquals("alberto", nlu.resolvedSubject)
        assertEquals("alberto", nlu.resolvedOwner)
        assertEquals("alberto", nlu.resolvedPerspective)
        assertEquals("Padova", nlu.objectValue)
        assertEquals("USER_ASSERTION", nlu.sourceType)
        assertTrue(nlu.worldTruth)

        assertEquals("alberto", semantic.subject)
        assertEquals("alberto", semantic.owner)
        assertEquals("Padova", claim.objectValue)
        assertEquals("alberto", claim.ownerId)
        assertEquals("alberto", claim.perspective)
        assertEquals("USER_ASSERTION", claim.sourceType)
        assertTrue(claim.worldTruth)
        assertTrue(semantic.stableMemoryAllowed)
    }

    @Test
    fun thirdPartyReportCannotBecomeStableMemoryByDefault() {
        val adapter = UnderstandingLabAdapter(
            StaticNluRuntime(
                MatrixNluClaim(
                    dialogueAct = "ASSERT",
                    predicate = "preference.like",
                    polarity = "NEGATIVE",
                    temporalRelation = "CURRENT",
                    subjectReferent = "KNOWN_ENTITY",
                    targetReferent = "SPEAKER",
                    ownerReferent = "KNOWN_ENTITY",
                    perspectiveReferent = "KNOWN_ENTITY",
                    confidence = 0.96,
                    confidenceByHead = mapOf(
                        "sequence.predicate" to 0.95,
                        "sequence.subjectReferent" to 0.95,
                        "sequence.targetReferent" to 0.95,
                    ),
                    subject = "Marco",
                    target = "alberto",
                    owner = "Marco",
                    perspective = "Marco",
                    objectValue = "alberto",
                    sourceType = "THIRD_PARTY_REPORT",
                    worldTruth = false,
                )
            )
        )

        val turn = adapter.understand(adapter.analyze(baseTurn("Marco dice che non gli piaccio")))
        val semantic = turn.requireSemantic()
        val claim = turn.typedClaims.single()

        assertEquals("Marco", semantic.subject)
        assertEquals("alberto", semantic.target)
        assertEquals("Marco", semantic.owner)
        assertEquals("THIRD_PARTY_REPORT", claim.sourceType)
        assertFalse(claim.worldTruth)
        assertFalse(semantic.stableMemoryAllowed)
    }

    @Test
    fun affectiveAdapterDoesNotApplyPersistentDeltaWithoutStableMemory() {
        var captured: AffectiveRuntimeRequest? = null
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput {
                    captured = request
                    return AffectiveRuntimeOutput(
                        emotions = mapOf("hope" to 0.7),
                        persistentDeltaApplied = request.persistentAllowed,
                    )
                }
            }
        )

        val turn = adapter.update(
            baseTurn("Vieni con me?").copy(
                semantic = SemanticFrame(
                    originalText = "Vieni con me?",
                    semanticSummary = "L'utente sta facendo una richiesta.",
                    dialogueAct = "REQUEST",
                    predicate = "goal.object",
                    polarity = "POSITIVE",
                    temporalRelation = "CURRENT",
                    subject = "alberto",
                    target = "luna",
                    owner = "alberto",
                    confidence = mapOf("overall" to 0.88),
                    stableMemoryAllowed = false,
                ),
                memoryResult = MemoryAdmissionResult(
                    status = "PROVISIONAL_CLAIM",
                    stableWrite = false,
                    reason = "no memory backend",
                ),
            )
        )

        val request = assertNotNull(captured)
        assertFalse(request.persistentAllowed)
        assertEquals("hope", request.impulse?.emotionType)
        assertNull(request.impulse?.targetId)
        assertFalse(turn.requireAffective().persistentDeltaAllowed)
    }

    @Test
    fun affectiveAdapterMapsStableSemanticMemoryToPersistentTarget() {
        var captured: AffectiveRuntimeRequest? = null
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput {
                    captured = request
                    return AffectiveRuntimeOutput(
                        persistentAffect = mapOf(
                            "alberto" to PersistentAffectSnapshot(trust = 0.55, affection = 0.1)
                        ),
                        persistentDeltaApplied = request.persistentAllowed,
                    )
                }
            }
        )

        val turn = adapter.update(
            baseTurn("Mi piace Luna").copy(
                semantic = SemanticFrame(
                    originalText = "Mi piace Luna",
                    semanticSummary = "L'utente esprime preferenza positiva verso Luna.",
                    dialogueAct = "ASSERT",
                    predicate = "preference.like",
                    polarity = "POSITIVE",
                    temporalRelation = "CURRENT",
                    subject = "alberto",
                    target = "luna",
                    owner = "alberto",
                    confidence = mapOf("overall" to 0.92),
                    stableMemoryAllowed = true,
                ),
                memoryResult = MemoryAdmissionResult(
                    status = "ADMITTED",
                    stableWrite = true,
                    reason = "stable test memory",
                ),
            )
        )

        val request = assertNotNull(captured)
        assertTrue(request.persistentAllowed)
        assertEquals("liking", request.impulse?.emotionType)
        assertEquals("alberto", request.impulse?.targetId)
        assertTrue(turn.requireAffective().persistentDeltaAllowed)
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

    private class StaticNluRuntime(
        private val claim: MatrixNluClaim,
    ) : MatrixNluRuntimeBridge {
        override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
            engine = "static-test-nlu",
            status = "OK",
            claims = listOf(claim),
        )
    }
}
