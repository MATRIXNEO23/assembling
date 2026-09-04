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
 * It validates semantic stability but does not own durable-memory authority.
 */
class BasicCoherenceGuard(
    private val minNegation: Double = 0.90,
    private val minPredicate: Double = 0.85,
    private val minReferent: Double = 0.85,
) : CoherenceGuardPort {
    override fun check(turn: MatrixTurnFrame): MatrixTurnFrame {
        val frame = turn.requireSemantic()
        val confidence = frame.confidence
        val claim = turn.typedClaims.firstOrNull()
        val missingCritical = listOf(
            "token.negation",
            "sequence.predicate",
            "sequence.subjectReferent",
            "sequence.targetReferent",
        ).firstOrNull { it !in confidence }
        val unresolvedSubject = frame.subject == "UNKNOWN" || claim?.subject == "UNKNOWN"
        val decision = when {
            frame.owner == null -> CoherenceDecision.REJECTED_UNSAFE
            unresolvedSubject -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            missingCritical != null -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            frame.dialogueAct == "QUESTION" -> CoherenceDecision.QUESTION_ONLY
            claim?.sourceType == "THIRD_PARTY_REPORT" -> CoherenceDecision.REPORT_ONLY
            turn.typedClaims.size > 1 -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            confidence.getValue("token.negation") < minNegation -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getValue("sequence.predicate") < minPredicate -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getValue("sequence.subjectReferent") < minReferent -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            confidence.getValue("sequence.targetReferent") < minReferent -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            frame.dialogueAct == "REQUEST" || frame.dialogueAct == "HYPOTHESIS" -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            frame.predicate == "speech.unresolved" -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            else -> CoherenceDecision.SAFE_TO_ADMIT
        }
        val diagnostics = turn.diagnostics
            .add("coherence.$decision")
            .let { trace ->
                if (unresolvedSubject) trace.tag("coherence.subject", "UNRESOLVED") else trace
            }
            .let { trace ->
                if (missingCritical != null) trace.tag("coherence.missing_critical_confidence", missingCritical) else trace
            }
            .let { trace ->
                if (turn.typedClaims.size > 1) trace.tag("coherence.multi_claim", "TRANSIENT_ONLY") else trace
            }
        return turn.copy(
            coherenceDecision = decision,
            diagnostics = diagnostics,
        )
    }
}

/**
 * Conservative authority adapter.
 * Source/owner decisions come from the TypedClaim itself, not from guessed text
 * differences or from the Coherence enum alone.
 */
class BasicAuthorityResolver : AuthorityResolverPort {
    override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val frame = turn.requireSemantic()
        val claim = turn.typedClaims.firstOrNull()
        val sourceType = claim?.sourceType ?: "UNRESOLVED"
        val ownerResolved = claim?.ownerId != null || frame.owner != null
        val accepted = coherence == CoherenceDecision.SAFE_TO_ADMIT &&
            ownerResolved &&
            sourceType != "THIRD_PARTY_REPORT"
        val decision = AuthorityDecision(
            accepted = accepted,
            ownerResolved = ownerResolved,
            sourceType = sourceType,
            conflictStatus = if (coherence == CoherenceDecision.CONFLICT_REQUIRES_REVIEW) "PENDING_REVIEW" else "NONE",
            reason = when {
                sourceType == "THIRD_PARTY_REPORT" -> "third-party report preserved as indirect source"
                !ownerResolved -> "owner unresolved"
                frame.subject == "UNKNOWN" -> "subject unresolved"
                turn.typedClaims.size > 1 -> "multi-claim turn remains transient until claim-wise authority resolution is wired"
                accepted -> "coherence and direct-source authority accepted"
                else -> "not stable enough for authority admission"
            },
        )
        return turn.copy(
            authorityDecision = decision,
            diagnostics = turn.diagnostics
                .add("authority.accepted=$accepted")
                .tag("authority.source_type", sourceType),
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
                reason = "no memory backend; durable persistence disabled in assembling placeholder",
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
 * Minimal affective adapter.
 * RelationshipState remains externally owned; this adapter exposes affective
 * state only and cannot create a competing relationship authority.
 */
class BasicAffectiveAdapter : AffectivePort {
    override fun update(turn: MatrixTurnFrame): MatrixTurnFrame {
        val memory = turn.requireMemory()
        val state = AffectiveState(
            relationshipSummary = "RelationshipState esterno: nessuna modifica applicata dall'Affective Engine.",
            affectiveSummary = if (memory.stableWrite) {
                "Luna può applicare un effetto affettivo persistente solo perché un evento è stato realmente ammesso."
            } else {
                "Luna può reagire emotivamente nel turno, senza creare automaticamente persistenza."
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
