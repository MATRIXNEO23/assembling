package matrix.assembling.adapters

import matrix.assembling.AffectivePort
import matrix.assembling.AffectiveState
import matrix.assembling.AssistantReply
import matrix.assembling.AuthorityDecision
import matrix.assembling.AuthorityResolverPort
import matrix.assembling.CoherenceDecision
import matrix.assembling.CoherenceGuardPort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.GgufPort
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryAdmissionResult
import matrix.assembling.MemoryPreflightPort

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
        val claims = turn.typedClaims
        val confidenceSources = if (claims.isEmpty()) {
            listOf("semantic" to frame.confidence)
        } else {
            claims.mapIndexed { index, claim -> "claim[$index]" to claim.confidence }
        }
        val missingCritical = confidenceSources.flatMap { (source, confidence) ->
            CRITICAL_CONFIDENCE_KEYS
                .filterNot(confidence::containsKey)
                .map { "$source.$it" }
        }
        val lowCritical = confidenceSources.flatMap { (source, confidence) ->
            buildList {
                if ((confidence["token.negation"] ?: 0.0) < minNegation) add("$source.token.negation")
                if ((confidence["sequence.predicate"] ?: 0.0) < minPredicate) add("$source.sequence.predicate")
                if ((confidence["sequence.subjectReferent"] ?: 0.0) < minReferent) add("$source.sequence.subjectReferent")
                if ((confidence["sequence.targetReferent"] ?: 0.0) < minReferent) add("$source.sequence.targetReferent")
            }
        }
        val ownerMissing = frame.owner == null || claims.any { it.ownerId == null }
        val unresolvedSubject = frame.subject == "UNKNOWN" || claims.any { it.subject == "UNKNOWN" }
        val multiClaim = claims.size > 1
        val singleSourceType = claims.singleOrNull()?.sourceType

        val (decision, reasonCode) = when {
            ownerMissing -> CoherenceDecision.REJECTED_UNSAFE to "COHERENCE_OWNER_MISSING"
            unresolvedSubject -> CoherenceDecision.LOW_CONFIDENCE_HOLD to "COHERENCE_SUBJECT_UNRESOLVED"
            missingCritical.isNotEmpty() -> CoherenceDecision.LOW_CONFIDENCE_HOLD to "COHERENCE_MISSING_CRITICAL_CONFIDENCE"
            lowCritical.isNotEmpty() -> CoherenceDecision.LOW_CONFIDENCE_HOLD to "COHERENCE_CRITICAL_CONFIDENCE_LOW"
            multiClaim -> CoherenceDecision.SAFE_TRANSIENT_ONLY to "COHERENCE_MULTI_CLAIM_TRANSIENT"
            frame.dialogueAct == "QUESTION" -> CoherenceDecision.QUESTION_ONLY to "COHERENCE_QUESTION_ONLY"
            singleSourceType == "THIRD_PARTY_REPORT" -> CoherenceDecision.REPORT_ONLY to "COHERENCE_REPORT_ONLY"
            frame.dialogueAct == "REQUEST" || frame.dialogueAct == "HYPOTHESIS" -> CoherenceDecision.SAFE_TRANSIENT_ONLY to "COHERENCE_TRANSIENT_DIALOGUE_ACT"
            frame.predicate == "speech.unresolved" -> CoherenceDecision.SAFE_TRANSIENT_ONLY to "COHERENCE_SPEECH_UNRESOLVED"
            else -> CoherenceDecision.SAFE_TO_ADMIT to "COHERENCE_SAFE_TO_ADMIT"
        }
        var diagnostics = turn.diagnostics
            .reason(reasonCode)
            .add("coherence.$decision")
            .tag("coherence.reason_code", reasonCode)
            .tag("coherence.claim_count", claims.size.toString())
            .let { trace ->
                if (unresolvedSubject) trace.tag("coherence.subject", "UNRESOLVED") else trace
            }
            .let { trace ->
                if (missingCritical.isNotEmpty()) {
                    trace
                        .tag("coherence.missing_critical_confidence", missingCritical.joinToString(","))
                        .tag("coherence.missing_critical_confidence_count", missingCritical.size.toString())
                } else {
                    trace
                }
            }
            .let { trace ->
                if (lowCritical.isNotEmpty()) {
                    trace
                        .tag("coherence.low_critical_confidence", lowCritical.joinToString(","))
                        .tag("coherence.low_critical_confidence_count", lowCritical.size.toString())
                } else {
                    trace
                }
            }
            .let { trace ->
                if (multiClaim) trace.tag("coherence.multi_claim", "TRANSIENT_ONLY") else trace
            }
        diagnostics = when {
            ownerMissing -> diagnostics.diverge("COHERENCE.OWNER_MISSING")
            unresolvedSubject -> diagnostics.diverge("COHERENCE.UNRESOLVED_SUBJECT")
            missingCritical.isNotEmpty() -> diagnostics.diverge("COHERENCE.MISSING_CRITICAL_CONFIDENCE")
            lowCritical.isNotEmpty() -> diagnostics.diverge("COHERENCE.CRITICAL_CONFIDENCE_LOW")
            else -> diagnostics
        }
        return turn.copy(
            coherenceDecision = decision,
            diagnostics = diagnostics,
        )
    }

    private companion object {
        val CRITICAL_CONFIDENCE_KEYS = listOf(
            "token.negation",
            "sequence.predicate",
            "sequence.subjectReferent",
            "sequence.targetReferent",
        )
    }
}

/**
 * Conservative authority adapter.
 * Source/owner decisions come from TypedClaim evidence and never write memory.
 */
class BasicAuthorityResolver : AuthorityResolverPort {
    override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val frame = turn.requireSemantic()
        val claims = turn.typedClaims
        val multiClaim = claims.size > 1
        val sourceType = when {
            multiClaim -> "MULTI_CLAIM"
            claims.size == 1 -> claims.single().sourceType
            else -> "UNRESOLVED"
        }
        val ownerResolved = claims.isNotEmpty() && claims.all { it.ownerId != null } && frame.owner != null
        val accepted = coherence == CoherenceDecision.SAFE_TO_ADMIT &&
            ownerResolved &&
            !multiClaim &&
            sourceType != "THIRD_PARTY_REPORT"
        val reasonCode = when {
            multiClaim -> "AUTHORITY_MULTI_CLAIM_TRANSIENT"
            sourceType == "THIRD_PARTY_REPORT" -> "AUTHORITY_THIRD_PARTY_REPORT"
            !ownerResolved -> "AUTHORITY_OWNER_UNRESOLVED"
            frame.subject == "UNKNOWN" -> "AUTHORITY_SUBJECT_UNRESOLVED"
            accepted -> "AUTHORITY_DIRECT_ACCEPTED"
            else -> "AUTHORITY_NOT_STABLE"
        }
        val decision = AuthorityDecision(
            accepted = accepted,
            ownerResolved = ownerResolved,
            sourceType = sourceType,
            conflictStatus = if (coherence == CoherenceDecision.CONFLICT_REQUIRES_REVIEW) "PENDING_REVIEW" else "NONE",
            reason = when (reasonCode) {
                "AUTHORITY_MULTI_CLAIM_TRANSIENT" -> "multi-claim turn remains transient until claim-wise authority resolution is wired"
                "AUTHORITY_THIRD_PARTY_REPORT" -> "third-party report preserved as indirect source"
                "AUTHORITY_OWNER_UNRESOLVED" -> "owner unresolved"
                "AUTHORITY_SUBJECT_UNRESOLVED" -> "subject unresolved"
                "AUTHORITY_DIRECT_ACCEPTED" -> "coherence and direct-source authority accepted"
                else -> "not stable enough for authority admission"
            },
        )
        var trace = turn.diagnostics
            .authority(
                DiagnosticSnapshot(
                    module = "AUTHORITY",
                    input = "coherence=$coherence; sourceType=$sourceType; claimCount=${claims.size}",
                    output = "accepted=$accepted; conflict=${decision.conflictStatus}",
                    decision = if (accepted) "DIRECT_AUTHORITY_ACCEPTED" else "DIRECT_AUTHORITY_NOT_ACCEPTED",
                    status = if (ownerResolved) "PASS" else "HOLD",
                    reasonCodes = listOf(reasonCode),
                    confidence = claims.singleOrNull()?.confidence.orEmpty(),
                    metadata = mapOf(
                        "sourceType" to sourceType,
                        "ownerResolved" to ownerResolved.toString(),
                        "subject" to frame.subject,
                        "claimCount" to claims.size.toString(),
                    ),
                )
            )
            .reason(reasonCode)
            .add("authority.accepted=$accepted")
            .tag("authority.source_type", sourceType)
            .tag("authority.reason_code", reasonCode)
        if (!ownerResolved) {
            trace = trace.diverge("AUTHORITY.OWNER_UNRESOLVED")
        }
        return turn.copy(
            authorityDecision = decision,
            diagnostics = trace,
        )
    }
}

/** Compatibility-named preflight placeholder; it never writes durable memory. */
class BasicMemoryAdmission : MemoryPreflightPort {
    override fun evaluate(turn: MatrixTurnFrame): MatrixTurnFrame {
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
                reason = "no memory backend; durable persistence disabled in assembling preflight",
            )
        }
        val reasonCode = if (result.status == "REJECTED") "MEMORY_ADMISSION_REJECTED" else "MEMORY_BACKEND_DISABLED"
        val trace = turn.diagnostics
            .admission(
                DiagnosticSnapshot(
                    module = "MEMORY_PREFLIGHT",
                    input = "coherence=$coherence; authorityAccepted=${authority.accepted}",
                    output = "status=${result.status}; stableWrite=${result.stableWrite}",
                    decision = result.status,
                    status = if (result.status == "REJECTED") "HOLD" else "PASS",
                    reasonCodes = listOf(reasonCode),
                    metadata = mapOf("memoryPersistence" to "DISABLED"),
                )
            )
            .memory(
                DiagnosticSnapshot(
                    module = "MEMORY",
                    input = "preflight=${result.status}",
                    output = "memoryIds=${result.memoryIds.size}; stableWrite=${result.stableWrite}",
                    decision = "NO_DURABLE_WRITE",
                    status = "PASS",
                    reasonCodes = listOf("MEMORY_NO_DURABLE_WRITE"),
                    metadata = mapOf("backend" to "DISABLED"),
                ),
                id = result.memoryIds.firstOrNull(),
            )
            .reason(reasonCode)
            .add("memory.${result.status}")
            .tag("memory", "MEMORY_PERSISTENCE_DISABLED")
        return turn.copy(
            memoryResult = result,
            diagnostics = trace,
        )
    }

    @Deprecated("Compatibility helper; the pre-response contract is evaluate().")
    fun admit(turn: MatrixTurnFrame): MatrixTurnFrame = evaluate(turn)
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
        val trace = turn.diagnostics
            .affective(
                DiagnosticSnapshot(
                    module = "AFFECTIVE",
                    input = "memoryStableWrite=${memory.stableWrite}",
                    output = "persistentDeltaAllowed=${state.persistentDeltaAllowed}",
                    decision = if (state.persistentDeltaAllowed) "PERSISTENT_AND_TRANSIENT" else "TRANSIENT_ONLY",
                    status = "PASS",
                    reasonCodes = listOf(if (state.persistentDeltaAllowed) "AFFECTIVE_PERSISTENCE_AUTHORIZED" else "AFFECTIVE_TRANSIENT_ONLY"),
                    metadata = mapOf("relationshipOwner" to "EXTERNAL"),
                )
            )
            .reason(if (state.persistentDeltaAllowed) "AFFECTIVE_PERSISTENCE_AUTHORIZED" else "AFFECTIVE_TRANSIENT_ONLY")
            .add("affective.persistentDelta=${state.persistentDeltaAllowed}")
        return turn.copy(
            affectiveState = state,
            diagnostics = trace,
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
