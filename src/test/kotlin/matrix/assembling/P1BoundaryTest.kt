package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import matrix.assembling.adapters.AffectiveLabAdapter
import matrix.assembling.adapters.AffectiveRuntimeBridge
import matrix.assembling.adapters.AffectiveRuntimeOutput
import matrix.assembling.adapters.BasicCoherenceGuard
import matrix.assembling.adapters.MatrixNluClaim
import matrix.assembling.adapters.MatrixNluInterpretation
import matrix.assembling.adapters.MatrixNluRequest
import matrix.assembling.adapters.MatrixNluRuntimeBridge
import matrix.assembling.adapters.UnderstandingLabAdapter

class P1BoundaryTest {

    @Test
    fun affectiveRuntimeCannotProvideRelationshipAuthority() {
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: matrix.assembling.adapters.AffectiveRuntimeRequest): AffectiveRuntimeOutput =
                    AffectiveRuntimeOutput(
                        emotions = mapOf("joy" to 0.4),
                        relationshipSummary = "MALICIOUS_RELATIONSHIP_OVERRIDE",
                        affectiveSummary = "joy 0.40",
                    )
            }
        )

        val result = adapter.update(baseAffectiveTurn(stableWrite = false))

        assertEquals(
            "RelationshipState esterno: nessuna modifica applicata dall'Affective Engine.",
            result.requireAffective().relationshipSummary,
        )
    }

    @Test
    fun affectivePersistenceIsClampedWhenAdmissionDidNotAuthorizeIt() {
        val adapter = AffectiveLabAdapter(
            object : AffectiveRuntimeBridge {
                override fun update(request: matrix.assembling.adapters.AffectiveRuntimeRequest): AffectiveRuntimeOutput =
                    AffectiveRuntimeOutput(
                        emotions = mapOf("joy" to 0.4),
                        persistentDeltaApplied = true,
                    )
            }
        )

        val result = adapter.update(baseAffectiveTurn(stableWrite = false))

        assertFalse(result.requireAffective().persistentDeltaAllowed)
        assertEquals("BLOCKED", result.diagnostics.tags["affective_lab.persistence_violation"])
    }

    @Test
    fun unresolvedSubjectDoesNotSilentlyBecomeSpeaker() {
        val runtime = object : MatrixNluRuntimeBridge {
            override fun interpret(request: MatrixNluRequest): MatrixNluInterpretation = MatrixNluInterpretation(
                engine = "boundary-test",
                status = "OK",
                claims = listOf(
                    MatrixNluClaim(
                        dialogueAct = "ASSERT",
                        predicate = "attribute.is",
                        polarity = "POSITIVE",
                        temporalRelation = "CURRENT",
                        subjectReferent = "UNKNOWN",
                        targetReferent = "NONE",
                        ownerReferent = "SPEAKER",
                        perspectiveReferent = "SPEAKER",
                        confidence = 0.95,
                        confidenceByHead = mapOf(
                            "token.negation" to 0.99,
                            "sequence.predicate" to 0.95,
                            "sequence.subjectReferent" to 0.95,
                            "sequence.targetReferent" to 0.95,
                        ),
                        subject = null,
                        owner = "alberto",
                        perspective = "alberto",
                        objectValue = "stanco",
                        sourceType = "USER_ASSERTION",
                    )
                ),
            )
        }
        val adapter = UnderstandingLabAdapter(runtime)
        val turn = MatrixTurnFrame(
            turnId = "turn-unknown-subject",
            sessionId = "session-test",
            input = UserMessage("È stanco", "alberto", timestampMillis = 0L),
        )

        val result = adapter.understand(adapter.analyze(turn))
        val checked = BasicCoherenceGuard().check(result)

        assertEquals("UNKNOWN", result.requireSemantic().subject)
        assertEquals("UNKNOWN", result.typedClaims.single().subject)
        assertEquals(CoherenceDecision.LOW_CONFIDENCE_HOLD, checked.requireCoherence())
        assertEquals("UNRESOLVED", checked.diagnostics.tags["coherence.subject"])
    }

    private fun baseAffectiveTurn(stableWrite: Boolean): MatrixTurnFrame = MatrixTurnFrame(
        turnId = "turn-affective",
        sessionId = "session-test",
        input = UserMessage("Mi piace Luna", "alberto", timestampMillis = 0L),
        semantic = SemanticFrame(
            originalText = "Mi piace Luna",
            semanticSummary = "preferenza",
            dialogueAct = "ASSERT",
            predicate = "preference.like",
            polarity = "POSITIVE",
            temporalRelation = "CURRENT",
            subject = "alberto",
            target = "luna",
            owner = "alberto",
            confidence = mapOf("overall" to 0.92),
        ),
        memoryResult = MemoryAdmissionResult(
            status = if (stableWrite) "ADMITTED" else "PROVISIONAL_CLAIM",
            stableWrite = stableWrite,
            reason = "test",
        ),
    )
}
