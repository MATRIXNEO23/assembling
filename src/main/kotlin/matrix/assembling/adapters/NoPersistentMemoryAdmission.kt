package matrix.assembling.adapters

import matrix.assembling.CoherenceDecision
import matrix.assembling.DiagnosticSnapshot
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
        val reasonCode = when (result.status) {
            "REJECTED" -> "MEMORY_ADMISSION_REJECTED"
            else -> "MEMORY_PROVISIONAL_ONLY"
        }
        val trace = turn.diagnostics
            .admission(
                DiagnosticSnapshot(
                    module = "MEMORY_ADMISSION",
                    input = "coherence=$coherence; authorityAccepted=${authority.accepted}; ownerResolved=${authority.ownerResolved}",
                    output = "status=${result.status}; stableWrite=${result.stableWrite}",
                    decision = result.status,
                    status = if (result.status == "REJECTED") "HOLD" else "PASS",
                    reasonCodes = listOf(reasonCode, "MEMORY_PERSISTENCE_DISABLED"),
                    metadata = mapOf(
                        "sourceType" to authority.sourceType,
                        "backend" to "DISABLED",
                    ),
                )
            )
            .memory(
                DiagnosticSnapshot(
                    module = "MEMORY",
                    input = "admission=${result.status}",
                    output = "memoryIds=${result.memoryIds.size}; stableWrite=${result.stableWrite}",
                    decision = "NO_DURABLE_WRITE",
                    status = "PASS",
                    reasonCodes = listOf("MEMORY_NO_DURABLE_WRITE"),
                    metadata = mapOf("backend" to "DISABLED"),
                ),
                id = result.memoryIds.firstOrNull(),
            )
            .reason(reasonCode)
            .add("memory.no_persistent_adapter")
            .tag("memory", "MEMORY_PERSISTENCE_DISABLED")
        return turn.copy(
            memoryResult = result,
            diagnostics = trace,
        )
    }
}
