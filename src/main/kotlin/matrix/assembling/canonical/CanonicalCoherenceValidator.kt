package matrix.assembling.canonical

import matrix.assembling.CanonicalCoherencePort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipUnderstandingV3Claim
import matrix.assembling.mip.MipUnderstandingV3Field
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus

/**
 * Narrow semantic/structural validator.
 * It does not classify Authority, identify Memory contradiction, decide persistence, or choose behavior.
 */
class CanonicalCoherenceValidator : CanonicalCoherencePort {

    override fun validate(turn: MatrixTurnFrame): MatrixTurnFrame {
        val observation = turn.requireCanonicalUnderstandingV3()
        val context = turn.requireCanonicalContextSnapshot()

        if (observation.claims.isEmpty()) {
            return turn.copy(
                canonicalCoherenceResults = MipField.notApplicable(),
                diagnostics = turn.diagnostics
                    .coherence(
                        DiagnosticSnapshot(
                            module = "COHERENCE",
                            input = "claims=0; snapshotId=${context.snapshotId}",
                            output = "NOT_APPLICABLE",
                            decision = "SKIP_NO_CLAIMS",
                            status = "PASS",
                            reasonCodes = listOf("COHERENCE.NO_CLAIMS"),
                        )
                    )
                    .reason("COHERENCE.NO_CLAIMS")
                    .add("coherence.canonical.not_applicable"),
            )
        }

        val results = observation.claims.map { claim -> evaluate(claim, context.snapshotId) }
        val holds = results.filter { it.status == CanonicalCoherenceStatus.HOLD }
        val errors = results.filter { it.status == CanonicalCoherenceStatus.ERROR }
        val status = when {
            errors.isNotEmpty() -> "ERROR"
            holds.isNotEmpty() -> "HOLD"
            else -> "PASS"
        }
        val reasonCodes = results.flatMap { it.reasonCodes }.distinct()

        var diagnostics = turn.diagnostics
            .coherence(
                DiagnosticSnapshot(
                    module = "COHERENCE",
                    input = "claims=${observation.claims.size}; snapshotId=${context.snapshotId}",
                    output = "pass=${results.count { it.status == CanonicalCoherenceStatus.PASS }}; hold=${holds.size}; error=${errors.size}",
                    decision = "CLAIM_WISE_SEMANTIC_STABILITY",
                    status = status,
                    reasonCodes = reasonCodes,
                    metadata = mapOf(
                        "contextSnapshotId" to context.snapshotId,
                        "claimStatuses" to results.joinToString(",") { "${it.claimId}:${it.status}" },
                    ),
                )
            )
            .reason("COHERENCE.CLAIM_WISE_VALIDATED")
            .add("coherence.canonical.completed")
            .tag("coherence.context_snapshot_id", context.snapshotId)
            .tag("coherence.claim_statuses", results.joinToString(",") { "${it.claimId}:${it.status}" })

        if (holds.isNotEmpty()) {
            diagnostics = diagnostics.diverge("COHERENCE.CLAIM_HOLD.${holds.first().claimId}")
        } else if (errors.isNotEmpty()) {
            diagnostics = diagnostics.diverge("COHERENCE.CLAIM_ERROR.${errors.first().claimId}")
        }

        return turn.copy(
            canonicalCoherenceResults = MipField.present(results),
            diagnostics = diagnostics,
        )
    }

    private fun evaluate(
        claim: MipUnderstandingV3Claim,
        contextSnapshotId: String,
    ): CanonicalClaimCoherenceResult {
        val reasons = mutableListOf<String>()

        when (claim.structuralStatus) {
            MipUnderstandingV3StructuralStatus.INVALID -> reasons += "COHERENCE.STRUCTURAL_INVALID"
            MipUnderstandingV3StructuralStatus.VALID -> Unit
        }
        when (claim.interpretationStatus) {
            MipUnderstandingV3InterpretationStatus.ABSTAINED -> reasons += "COHERENCE.INTERPRETATION_ABSTAINED"
            MipUnderstandingV3InterpretationStatus.AMBIGUOUS -> reasons += "COHERENCE.INTERPRETATION_AMBIGUOUS"
            MipUnderstandingV3InterpretationStatus.RESOLVED -> Unit
        }

        checkField("dialogueAct", claim.dialogueAct, reasons)
        checkField("predicate", claim.predicate, reasons)
        checkField("subjectReferent", claim.subjectReferent, reasons)
        checkField("polarity", claim.polarity, reasons)
        checkField("temporalRelation", claim.temporalRelation, reasons)

        val status = if (reasons.isEmpty()) CanonicalCoherenceStatus.PASS else CanonicalCoherenceStatus.HOLD
        return CanonicalClaimCoherenceResult(
            claimId = claim.claimId,
            contextSnapshotId = contextSnapshotId,
            status = status,
            reasonCodes = if (reasons.isEmpty()) {
                listOf("COHERENCE.SEMANTIC_STABLE")
            } else {
                reasons.distinct()
            },
        )
    }

    private fun <T : Any> checkField(
        name: String,
        field: MipUnderstandingV3Field<T>,
        reasons: MutableList<String>,
    ) {
        when (field.fieldStatus) {
            MipUnderstandingV3FieldStatus.UNKNOWN -> reasons += "COHERENCE.FIELD_UNKNOWN.$name"
            MipUnderstandingV3FieldStatus.AMBIGUOUS -> reasons += "COHERENCE.FIELD_AMBIGUOUS.$name"
            MipUnderstandingV3FieldStatus.RESOLVED,
            MipUnderstandingV3FieldStatus.NOT_APPLICABLE -> Unit
        }
    }
}
