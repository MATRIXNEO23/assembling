package matrix.assembling.canonical

import matrix.assembling.AuthorityResolverPort
import matrix.assembling.CanonicalCoherencePort
import matrix.assembling.CanonicalContextPort
import matrix.assembling.CanonicalRetrievalPort
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.mip.MipFieldStatus

/**
 * Authoritative F1/F2 foundation path after canonical Understanding V3 has been produced.
 * Future cognitive owners attach after this foundation; this class performs no durable writes.
 */
class CanonicalFoundationOrchestrator(
    private val context: CanonicalContextPort,
    private val retrieval: CanonicalRetrievalPort,
    private val coherence: CanonicalCoherencePort,
    private val authority: AuthorityResolverPort,
) {

    fun handle(turn: MatrixTurnFrame): MatrixTurnFrame {
        val prepared = initializeTrace(turn)
            .let(context::assemble)
            .let(retrieval::retrieve)
            .let(coherence::validate)

        if (!canResolveAuthority(prepared)) {
            return prepared.copy(
                diagnostics = prepared.diagnostics
                    .reason("AUTHORITY.SKIPPED.COHERENCE_HOLD")
                    .add("authority.canonical.skipped")
                    .tag("authority.canonical", "SKIPPED_COHERENCE_HOLD")
                    .add("foundation.f1f2.completed"),
            )
        }

        val resolved = authority.resolve(prepared)
        return resolved.copy(
            diagnostics = resolved.diagnostics
                .reason("FOUNDATION.F1F2.COMPLETE")
                .add("foundation.f1f2.completed")
                .tag("foundation.path", "CANONICAL_V3_CONTEXT_RETRIEVAL_COHERENCE_AUTHORITY"),
        )
    }

    private fun canResolveAuthority(turn: MatrixTurnFrame): Boolean {
        if (turn.canonicalCoherenceResults.status == MipFieldStatus.NOT_APPLICABLE) return false
        if (turn.canonicalCoherenceResults.status != MipFieldStatus.PRESENT) return false
        return turn.requireCanonicalCoherenceResults().all { it.status == CanonicalCoherenceStatus.PASS }
    }

    private fun initializeTrace(turn: MatrixTurnFrame): MatrixTurnFrame {
        if (turn.diagnostics.inputOriginale != null) return turn
        return turn.copy(
            diagnostics = turn.diagnostics
                .withInput(turn.input.text)
                .reason("INPUT_ACCEPTED")
                .add("foundation.turn.trace.initialized"),
        )
    }
}
