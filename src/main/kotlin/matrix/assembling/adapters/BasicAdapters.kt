package matrix.assembling.adapters

import matrix.assembling.AffectivePort
import matrix.assembling.AffectiveState
import matrix.assembling.AssistantReply
import matrix.assembling.AuthorityDecision
import matrix.assembling.AuthorityResolverPort
import matrix.assembling.CoherenceDecision
import matrix.assembling.CoherenceGuardPort
import matrix.assembling.GgufPort
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryAdmissionPort
import matrix.assembling.MemoryAdmissionResult

/**
 * Minimal deterministic guard used until the production Coherence Buffer is ported.
 * It blocks stable memory writes when critical heads are below safety thresholds.
 */
class BasicCoherenceGuard(
    private val minNegation: Double = 0.90,
    private val minPredicate: Double = 0.85,
    private val minReferent: Double = 0.85,
) : CoherenceGuardPort {
    override fun check(turn: MatrixTurnFrame): MatrixTurnFrame {
        val frame = turn.requireSemantic()
        val confidence = frame.confidence
        val decision = when {
            frame.owner == null -> CoherenceDecision.REJECTED_UNSAFE
            frame.dialogueAct == "QUESTION" -> CoherenceDecision.QUESTION_ONLY
            confidence.getOrDefault("tokens.negation", 1.0) < minNegation -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getOrDefault("sequence.predicate", 1.0) < minPredicate -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getOrDefault("sequence.subjectReferent", 1.0) < minReferent -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getOrDefault("sequence.targetReferent", 1.0) < minReferent -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            !frame.stableMemoryAllowed -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            else -> CoherenceDecision.SAFE_TO_ADMIT
        }
        return turn.copy(
            coherenceDecision = decision,
            diagnostics = turn.diagnostics.add("coherence.$decision"),
        )
    }
}

/**
 * Placeholder authority adapter. It is intentionally conservative: only a frame
 * already considered safe by coherence can be accepted as direct authority.
 */
class BasicAuthorityResolver : AuthorityResolverPort {
    override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val frame = turn.requireSemantic()
        val accepted = coherence == CoherenceDecision.SAFE_TO_ADMIT
        val decision = AuthorityDecision(
            accepted = accepted,
            ownerResolved = frame.owner != null,
            sourceType = if (coherence == CoherenceDecision.REPORT_ONLY) "THIRD_PARTY_REPORT" else "DIRECT",
            conflictStatus = if (coherence == CoherenceDecision.CONFLICT_REQUIRES_REVIEW) "PENDING_REVIEW" else "NONE",
            reason = if (accepted) "coherence accepted stable frame" else "not stable enough for authority admission",
        )
        return turn.copy(
            authorityDecision = decision,
            diagnostics = turn.diagnostics.add("authority.accepted=$accepted"),
        )
    }
}

/**
 * Temporary memory placeholder.
 *
 * The real memory backend is not implemented yet, therefore this adapter must
 * never return stableWrite=true or real memory IDs. It keeps the pipeline
 * runnable without pretending that persistence exists.
 */
class BasicMemoryAdmission : MemoryAdmissionPort {
    override fun admit(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val authority = turn.requireAuthority()
        val result = when {
            coherence == CoherenceDecision.REJECTED_UNSAFE || !authority.ownerResolved -> MemoryAdmissionResult(
                status = "REJECTED",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "no memory backend; unsafe or unresolved claim rejected before storage",
            )
            else -> MemoryAdmissionResult(
                status = "NO_MEMORY_BACKEND",
                memoryIds = emptyList(),
                stableWrite = false,
                reason = "no memory backend; stable persistence disabled in assembling placeholder",
            )
        }
        return turn.copy(
            memoryResult = result,
            diagnostics = turn.diagnostics
                .add("memory.${result.status}")
                .tag("memory", "MEMORY_PERSISTENCE_DISABLED"),
        )
    }
}

/**
 * Minimal affective adapter. Persistent emotional deltas are allowed only after
 * stable memory admission; otherwise state is translated as transient attitude.
 */
class BasicAffectiveAdapter : AffectivePort {
    override fun update(turn: MatrixTurnFrame): MatrixTurnFrame {
        val memory = turn.requireMemory()
        val state = AffectiveState(
            relationshipSummary = "Luna considera il rapporto con l'utente attivo e tiene conto del contesto recente.",
            affectiveSummary = if (memory.stableWrite) {
                "Luna può reagire emotivamente al significato confermato dal sistema."
            } else {
                "Luna reagisce con cautela: il contenuto non è stato stabilizzato in memoria."
            },
            persistentDeltaAllowed = memory.stableWrite,
        )
        return turn.copy(
            affectiveState = state,
            diagnostics = turn.diagnostics.add("affective.persistentDelta=${state.persistentDeltaAllowed}"),
        )
    }
}

/**
 * Fake GGUF adapter for smoke tests. Real llama.cpp/MLC integration must replace
 * this port without changing upstream module contracts.
 */
class EchoGgufAdapter : GgufPort {
    override fun generate(turn: MatrixTurnFrame): MatrixTurnFrame {
        val prompt = turn.requirePrompt()
        return turn.copy(
            reply = AssistantReply(
                text = "[GGUF_PLACEHOLDER] Prompt ricevuto (${prompt.text.length} caratteri).",
                diagnosticTrace = turn.diagnostics.tags,
            ),
            diagnostics = turn.diagnostics.add("gguf.echo"),
        )
    }
}
