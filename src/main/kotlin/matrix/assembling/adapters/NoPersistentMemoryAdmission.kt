package matrix.assembling.adapters

import matrix.assembling.CoherenceDecision
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.DiagnosticStage
import matrix.assembling.DiagnosticStatus
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryPreflightPort
import matrix.assembling.MemoryAdmissionResult

/**
 * Compatibility preflight adapter while the real Memory Foundation is absent.
 * It never writes durable memory and never returns real memory IDs.
 */
class NoPersistentMemoryAdmission : MemoryPreflightPort {
    override fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val authority = turn.requireAuthority()
        val result = when {
            coherence == CoherenceDecision.REJECTED_UNSAFE -> MemoryAdmissionResult(
                status = "REJECTED",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "memory persistence disabled; unsafe claim rejected in preflight",
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
                reason = "preflight only; claim remains inside the current MatrixTurnFrame",
            )
        }
        return turn.copy(
            memoryResult = result,
            diagnostics = turn.diagnostics
                .record(
                    DiagnosticStage.MEMORY_ADMISSION,
                    DiagnosticSnapshot(
                        module = "MEMORY_PREFLIGHT",
                        status = if (result.status == "REJECTED") DiagnosticStatus.REJECT else DiagnosticStatus.HOLD,
                        input = mapOf(
                            "coherence" to coherence.name,
                            "authorityAccepted" to authority.accepted.toString(),
                        ),
                        output = mapOf(
                            "status" to result.status,
                            "stableWrite" to result.stableWrite.toString(),
                            "memoryIds" to result.memoryIds.joinToString(","),
                        ),
                        decision = result.status,
                        reasonCodes = listOf("MEMORY_PREFLIGHT_NON_PERSISTENT"),
                    ),
                )
                .record(
                    DiagnosticStage.MEMORY,
                    DiagnosticSnapshot(
                        module = "MEMORY",
                        status = DiagnosticStatus.NOT_EXECUTED,
                        decision = "NO_DURABLE_WRITE",
                        reasonCodes = listOf("MEMORY_PERSISTENCE_DISABLED"),
                    ),
                )
                .add("memory.no_persistent_adapter")
                .tag("memory", "MEMORY_PERSISTENCE_DISABLED"),
        )
    }

    @Deprecated("Use evaluate(); this stage is preflight-only")
    fun admit(turn: MatrixTurnFrame): MatrixTurnFrame = evaluate(turn)
}
