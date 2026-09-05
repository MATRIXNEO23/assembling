package matrix.assembling.pipeline

import matrix.assembling.coherence.CoherenceGuard
import matrix.assembling.contracts.AffectiveState
import matrix.assembling.contracts.FilteredMemorySummary
import matrix.assembling.contracts.PromptDirective
import matrix.assembling.contracts.RelationshipState
import matrix.assembling.contracts.SemanticFrame
import matrix.assembling.prompt.SemanticFrameToPrompt

/**
 * Legacy compatibility/testing prompt pipeline.
 *
 * The authoritative runtime path is MatrixTurnFrame +
 * MatrixAssemblingOrchestrator. This class remains only to preserve existing
 * tests/callers during progressive migration and must not receive new
 * architectural authority.
 */
@Deprecated(
    message = "Compatibility-only. Use MatrixTurnFrame + MatrixAssemblingOrchestrator for new integration work.",
    level = DeprecationLevel.WARNING,
)
class MatrixAssemblyPipeline(
    private val coherenceGuard: CoherenceGuard = CoherenceGuard(),
    private val promptTranslator: SemanticFrameToPrompt = SemanticFrameToPrompt(),
) {
    fun buildDirective(
        frame: SemanticFrame,
        relationship: RelationshipState? = null,
        affective: AffectiveState? = null,
        memory: FilteredMemorySummary = FilteredMemorySummary(),
    ): PromptDirective {
        val coherence = coherenceGuard.evaluate(frame)
        return promptTranslator.toDirective(
            frame = frame,
            coherence = coherence,
            relationship = relationship,
            affective = affective,
            memory = memory,
        )
    }

    fun buildPrompt(
        frame: SemanticFrame,
        relationship: RelationshipState? = null,
        affective: AffectiveState? = null,
        memory: FilteredMemorySummary = FilteredMemorySummary(),
    ): String {
        val coherence = coherenceGuard.evaluate(frame)
        return promptTranslator.toPrompt(
            frame = frame,
            coherence = coherence,
            relationship = relationship,
            affective = affective,
            memory = memory,
        )
    }
}
