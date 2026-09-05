package matrix.assembling

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PromptBoundaryTest {

    @Test
    fun requestPromptDoesNotBecomeHiddenDecisionEngine() {
        val turn = MatrixTurnFrame(
            turnId = "prompt-boundary",
            sessionId = "session-test",
            input = UserMessage("Vieni con me?", "alberto", timestampMillis = 0L),
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
                confidence = mapOf("overall" to 0.95),
            ),
            coherenceDecision = CoherenceDecision.SAFE_TRANSIENT_ONLY,
            authorityDecision = AuthorityDecision(
                accepted = false,
                ownerResolved = true,
                sourceType = "TURN_INTENT",
                conflictStatus = "NONE",
                reason = "request is not direct stable authority",
            ),
            memoryResult = MemoryAdmissionResult(
                status = "PROVISIONAL_CLAIM",
                stableWrite = false,
                reason = "no persistent backend",
            ),
            affectiveState = AffectiveState(
                relationshipSummary = "RelationshipState esterno: nessuna modifica applicata dall'Affective Engine.",
                affectiveSummary = "transient",
                persistentDeltaAllowed = false,
            ),
        )

        val result = SemanticFrameToPrompt().buildPrompt(turn)
        val prompt = result.requirePrompt().text

        assertContains(prompt, "ISTRUZIONE DI REALIZZAZIONE")
        assertContains(prompt, "senza trattarla come azione già decisa o avvenuta")
        assertFalse(prompt.contains("valutando relazione", ignoreCase = true))
        assertEquals("REALIZATION_ONLY", result.diagnostics.tags["prompt.role"])
        assertEquals("NON_CABLATO", result.diagnostics.tags["prompt.decision_layer"])
    }
}
