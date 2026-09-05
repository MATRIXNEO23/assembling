package matrix.assembling.adapters

import matrix.assembling.AffectivePort
import matrix.assembling.AffectiveState
import matrix.assembling.AssistantReply
import matrix.assembling.AuthorityDecision
import matrix.assembling.AuthorityResolverPort
import matrix.assembling.CoherenceDecision
import matrix.assembling.CoherenceGuardPort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.DiagnosticStage
import matrix.assembling.DiagnosticStatus
import matrix.assembling.GgufPort
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.MemoryPreflightPort
import matrix.assembling.MemoryAdmissionResult

/** Minimal deterministic guard until the production Coherence Buffer is ported. */
class BasicCoherenceGuard(
    private val minNegation: Double = 0.90,
    private val minPredicate: Double = 0.85,
    private val minReferent: Double = 0.85,
) : CoherenceGuardPort {
    override fun check(turn: MatrixTurnFrame): MatrixTurnFrame {
        val frame = turn.requireSemantic()
        val claims = turn.typedClaims
        val confidenceMaps = claims.map { it.confidence }.ifEmpty { listOf(frame.confidence) }
        val missingKeys = confidenceMaps.flatMapIndexed { index, confidence ->
            CRITICAL_CONFIDENCE_KEYS.filterNot(confidence::containsKey).map { "claim[$index].$it" }
        }
        val lowKeys = confidenceMaps.flatMapIndexed { index, confidence ->
            buildList {
                if ((confidence["token.negation"] ?: 0.0) < minNegation) add("claim[$index].token.negation")
                if ((confidence["sequence.predicate"] ?: 0.0) < minPredicate) add("claim[$index].sequence.predicate")
                if ((confidence["sequence.subjectReferent"] ?: 0.0) < minReferent) add("claim[$index].sequence.subjectReferent")
                if ((confidence["sequence.targetReferent"] ?: 0.0) < minReferent) add("claim[$index].sequence.targetReferent")
            }
        }
        val unresolvedSubject = claims.any { it.subject == "UNKNOWN" } || frame.subject == "UNKNOWN"
        val unresolvedOwner = claims.any { it.ownerId == null } || frame.owner == null
        val anyThirdParty = claims.any { it.sourceType == "THIRD_PARTY_REPORT" }

        val decision = when {
            claims.isEmpty() -> CoherenceDecision.REJECTED_UNSAFE
            unresolvedSubject || unresolvedOwner -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            missingKeys.isNotEmpty() -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            lowKeys.isNotEmpty() -> CoherenceDecision.LOW_CONFIDENCE_HOLD
            claims.size > 1 -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            frame.dialogueAct == "QUESTION" -> CoherenceDecision.QUESTION_ONLY
            anyThirdParty -> CoherenceDecision.REPORT_ONLY
            frame.dialogueAct == "REQUEST" || frame.dialogueAct == "HYPOTHESIS" -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            frame.predicate == "speech.unresolved" -> CoherenceDecision.SAFE_TRANSIENT_ONLY
            else -> CoherenceDecision.SAFE_TO_ADMIT
        }
        val reasons = buildList {
            add("COHERENCE_$decision")
            if (claims.size > 1) add("MULTI_CLAIM_TRANSIENT_ONLY")
            if (missingKeys.isNotEmpty()) add("MISSING_CRITICAL_CONFIDENCE")
            if (lowKeys.isNotEmpty()) add("CRITICAL_CONFIDENCE_BELOW_GATE")
            if (unresolvedSubject) add("SUBJECT_UNRESOLVED")
            if (unresolvedOwner) add("OWNER_UNRESOLVED")
            if (anyThirdParty) add("THIRD_PARTY_SOURCE_PRESERVED")
        }
        var diagnostics = turn.diagnostics
            .record(
                DiagnosticStage.COHERENCE,
                DiagnosticSnapshot(
                    module = "COHERENCE",
                    status = when (decision) {
                        CoherenceDecision.SAFE_TO_ADMIT -> DiagnosticStatus.PASS
                        CoherenceDecision.REJECTED_UNSAFE -> DiagnosticStatus.REJECT
                        else -> DiagnosticStatus.HOLD
                    },
                    input = mapOf("claimCount" to claims.size.toString()),
                    output = mapOf(
                        "missingCriticalConfidence" to missingKeys.joinToString(","),
                        "lowCriticalConfidence" to lowKeys.joinToString(","),
                    ),
                    decision = decision.name,
                    reasonCodes = reasons,
                ),
            )
            .add("coherence.$decision")
        if (missingKeys.isNotEmpty()) diagnostics = diagnostics.diverge("COHERENCE.MISSING_CRITICAL_CONFIDENCE")
        if (unresolvedSubject && diagnostics.firstDivergence == null) {
            diagnostics = diagnostics.diverge("UNDERSTANDING.UNRESOLVED_SUBJECT")
        }

        return turn.copy(coherenceDecision = decision, diagnostics = diagnostics)
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

/** Conservative authority adapter; it never writes memory. */
class BasicAuthorityResolver : AuthorityResolverPort {
    override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val coherence = turn.requireCoherence()
        val frame = turn.requireSemantic()
        val claims = turn.typedClaims
        val multiClaim = claims.size > 1
        val sourceType = when {
            multiClaim -> "MULTI_CLAIM"
            claims.isNotEmpty() -> claims.single().sourceType
            else -> "UNRESOLVED"
        }
        val ownerResolved = claims.isNotEmpty() && claims.all { it.ownerId != null } && frame.owner != null
        val accepted = coherence == CoherenceDecision.SAFE_TO_ADMIT &&
            ownerResolved &&
            !multiClaim &&
            sourceType != "THIRD_PARTY_REPORT"
        val decision = AuthorityDecision(
            accepted = accepted,
            ownerResolved = ownerResolved,
            sourceType = sourceType,
            conflictStatus = if (coherence == CoherenceDecision.CONFLICT_REQUIRES_REVIEW) "PENDING_REVIEW" else "NONE",
            reason = when {
                multiClaim -> "multiple claims preserved; per-claim authority resolution not wired"
                sourceType == "THIRD_PARTY_REPORT" -> "third-party report preserved as indirect source"
                !ownerResolved -> "owner unresolved"
                accepted -> "coherence and direct-source authority accepted"
                else -> "not stable enough for authority admission"
            },
        )
        return turn.copy(
            authorityDecision = decision,
            diagnostics = turn.diagnostics
                .record(
                    DiagnosticStage.AUTHORITY,
                    DiagnosticSnapshot(
                        module = "AUTHORITY",
                        status = if (accepted) DiagnosticStatus.PASS else DiagnosticStatus.HOLD,
                        input = mapOf(
                            "claimCount" to claims.size.toString(),
                            "coherence" to coherence.name,
                        ),
                        output = mapOf(
                            "sourceType" to sourceType,
                            "ownerResolved" to ownerResolved.toString(),
                        ),
                        decision = if (accepted) "DIRECT_AUTHORITY_ACCEPTED" else "DIRECT_AUTHORITY_NOT_ACCEPTED",
                        reasonCodes = listOf(
                            when {
                                multiClaim -> "MULTI_CLAIM_AUTHORITY_DEFERRED"
                                sourceType == "THIRD_PARTY_REPORT" -> "AUTHORITY_DIRECT_REJECTED_THIRD_PARTY"
                                !ownerResolved -> "AUTHORITY_OWNER_UNRESOLVED"
                                accepted -> "AUTHORITY_DIRECT_ACCEPTED"
                                else -> "AUTHORITY_HELD"
                            },
                        ),
                    ),
                )
                .add("authority.accepted=$accepted")
                .tag("authority.source_type", sourceType),
        )
    }
}

/** Compatibility preflight placeholder; never writes durable memory. */
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
                reason = "no memory backend; preflight only, durable persistence disabled",
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
                .add("memory.${result.status}")
                .tag("memory", "MEMORY_PERSISTENCE_DISABLED"),
        )
    }

    @Deprecated("Use evaluate(); this stage is preflight-only")
    fun admit(turn: MatrixTurnFrame): MatrixTurnFrame = evaluate(turn)
}

/** Minimal affective adapter; RelationshipState remains externally owned. */
class BasicAffectiveAdapter : AffectivePort {
    override fun update(turn: MatrixTurnFrame): MatrixTurnFrame {
        val memory = turn.requireMemory()
        val state = AffectiveState(
            relationshipSummary = "RelationshipState NON_CABLATO: nessuna modifica applicata dall'Affective Engine.",
            affectiveSummary = "Luna può reagire emotivamente nel turno, senza creare automaticamente persistenza.",
            persistentDeltaAllowed = memory.stableWrite,
        )
        return turn.copy(
            affectiveState = state,
            diagnostics = turn.diagnostics
                .record(
                    DiagnosticStage.AFFECTIVE,
                    DiagnosticSnapshot(
                        module = "AFFECTIVE",
                        status = DiagnosticStatus.PASS,
                        input = mapOf("memoryStableWrite" to memory.stableWrite.toString()),
                        output = mapOf("persistentDeltaApplied" to state.persistentDeltaAllowed.toString()),
                        decision = if (state.persistentDeltaAllowed) "PERSISTENT_ALLOWED" else "TRANSIENT_ONLY",
                        reasonCodes = listOf(if (state.persistentDeltaAllowed) "AFFECTIVE_PERSISTENT_ALLOWED" else "AFFECTIVE_TRANSIENT_ONLY"),
                    ),
                )
                .add("affective.persistentDelta=${state.persistentDeltaAllowed}"),
        )
    }
}

/** Fake GGUF adapter for smoke tests. */
class EchoGgufAdapter : GgufPort {
    override fun generate(turn: MatrixTurnFrame): MatrixTurnFrame {
        val prompt = turn.requirePrompt()
        val text = "[GGUF_PLACEHOLDER] Prompt ricevuto (${prompt.text.length} caratteri)."
        val diagnostics = turn.diagnostics
            .record(
                DiagnosticStage.GGUF,
                DiagnosticSnapshot(
                    module = "GGUF_PLACEHOLDER",
                    status = DiagnosticStatus.PASS,
                    input = mapOf("promptLength" to prompt.text.length.toString()),
                    output = mapOf("replyLength" to text.length.toString()),
                    decision = "GENERATED",
                    reasonCodes = listOf("GGUF_PLACEHOLDER_OUTPUT"),
                ),
            )
            .add("gguf.echo")
        return turn.copy(
            reply = AssistantReply(
                text = text,
                diagnosticTrace = diagnostics.tags,
                diagnostics = diagnostics,
            ),
            diagnostics = diagnostics,
        )
    }
}
