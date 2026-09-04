package matrix.assembling.coherence

import matrix.assembling.contracts.AdultIntimacyMarker
import matrix.assembling.contracts.CoherenceDecision
import matrix.assembling.contracts.CoherenceStatus
import matrix.assembling.contracts.DialogueAct
import matrix.assembling.contracts.Predicate
import matrix.assembling.contracts.Referent
import matrix.assembling.contracts.SemanticFrame
import matrix.assembling.contracts.TemporalRelation

/**
 * Deterministic safety guard between Understanding and downstream modules.
 *
 * It does not censor. It only decides whether a semantic frame is safe for a
 * reply, stable memory, or persistent affect.
 */
class CoherenceGuard(
    private val thresholds: CoherenceThresholds = CoherenceThresholds(),
) {
    fun evaluate(frame: SemanticFrame): CoherenceDecision {
        if (frame.originalText.isBlank()) {
            return decision(
                CoherenceStatus.RejectedUnsafe,
                "empty user text",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.subject == Referent.Unknown || frame.target == Referent.Unknown) {
            return decision(
                CoherenceStatus.LowConfidence,
                "unknown subject or target referent",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.dialogueAct == DialogueAct.Question) {
            return decision(
                CoherenceStatus.QuestionOnly,
                "question is not a stable fact",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.dialogueAct == DialogueAct.Hypothesis) {
            return decision(
                CoherenceStatus.TransientOnly,
                "hypothesis is transient until confirmed",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.dialogueAct == DialogueAct.Unknown || frame.predicate == Predicate.SpeechUnresolved) {
            return decision(
                CoherenceStatus.TransientOnly,
                "semantic meaning is unresolved",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.confidence.negation < thresholds.negation) {
            return decision(
                CoherenceStatus.LowConfidence,
                "negation confidence below threshold",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.confidence.predicate < thresholds.predicate) {
            return decision(
                CoherenceStatus.LowConfidence,
                "predicate confidence below threshold",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.confidence.dialogueAct < thresholds.dialogueAct) {
            return decision(
                CoherenceStatus.LowConfidence,
                "dialogue-act confidence below threshold",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.confidence.referents < thresholds.referents) {
            return decision(
                CoherenceStatus.LowConfidence,
                "referent confidence below threshold",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.confidence.temporal < thresholds.temporal ||
            frame.temporalRelation == TemporalRelation.Unknown
        ) {
            return decision(
                CoherenceStatus.TransientOnly,
                "temporal relation is uncertain",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        if (frame.adultIntimacy == AdultIntimacyMarker.UnresolvedIntimateTerm) {
            return decision(
                CoherenceStatus.TransientOnly,
                "intimate term is unresolved; do not treat as stable memory",
                stableMemoryAllowed = false,
                persistentAffectAllowed = false,
            )
        }

        val stableMemoryAllowed = when (frame.predicate) {
            Predicate.ConsentGrant,
            Predicate.ConsentRefuse,
            Predicate.GoalObject,
            Predicate.SpeechUnresolved -> false
            else -> true
        }

        val persistentAffectAllowed = stableMemoryAllowed && frame.adultIntimacy == AdultIntimacyMarker.None

        return decision(
            CoherenceStatus.SafeToUseForReply,
            "semantic frame is safe for reply",
            stableMemoryAllowed = stableMemoryAllowed,
            persistentAffectAllowed = persistentAffectAllowed,
        )
    }

    private fun decision(
        status: CoherenceStatus,
        reason: String,
        stableMemoryAllowed: Boolean,
        persistentAffectAllowed: Boolean,
    ): CoherenceDecision = CoherenceDecision(
        status = status,
        reason = reason,
        stableMemoryAllowed = stableMemoryAllowed,
        persistentAffectAllowed = persistentAffectAllowed,
    )
}

data class CoherenceThresholds(
    val negation: Double = 0.90,
    val predicate: Double = 0.88,
    val dialogueAct: Double = 0.86,
    val referents: Double = 0.88,
    val temporal: Double = 0.82,
)
