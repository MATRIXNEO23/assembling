package matrix.assembling.adapters

import matrix.assembling.CoherenceDecision
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryAdmissionPort
import matrix.assembling.MemoryAdmissionResult

/**
 * Temporary memory adapter used while the real Memory Foundation is not yet
 * integrated.
 *
 * This adapter never writes durable memory and never returns real memory IDs.
 * It exists only so NLU -> Understanding -> Coherence -> Authority -> Affective
 * -> Prompt -> GGUF can be connected safely before MemoryRepository exists.
 */
class NoPersistentMemoryAdmission : MemoryAdmissionPort {
    override fun admit(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val authority = turn.requireAuthority()
        val result = when {
            coherence == CoherenceDecision.REJECTED_UNSAFE -> MemoryAdmissionResult(
                status = "REJECTED",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "memory persistence disabled; unsafe claim rejected before storage",
            )
            !authority.ownerResolved -> MemoryAdmissionResult(
                status = "REJECTED",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "memory persistence disabled; owner/source unresolved",
            )
            else -> MemoryAdmissionResult(
                status = "PROVISIONAL_CLAIM",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "memory persistence disabled; claim kept only inside current MatrixTurnFrame",
            )
        }
        return turn.copy(
            memoryResult = result,
            diagnostics = turn.diagnostics
                .add("memory.no_persistent_adapter")
                .tag("memory", "MEMORY_PERSISTENCE_DISABLED"),
        )
    }
}
