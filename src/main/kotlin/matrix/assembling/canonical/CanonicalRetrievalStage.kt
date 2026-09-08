package matrix.assembling.canonical

import matrix.assembling.CanonicalRetrievalPort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixBoundaryViolationException
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipUnderstandingV3Claim
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.RetrievalPurpose
import matrix.assembling.mip.RetrievalQuery
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

fun interface CanonicalRetrievalProvider {
    fun retrieve(query: RetrievalQuery, context: MatrixContextSnapshot): RetrievalResult
}

/**
 * Builds one structured retrieval query per canonical claim and enforces exact claim/context binding.
 * The stage is read-only and never fabricates Memory records.
 */
class CanonicalClaimRetrievalStage(
    private val provider: CanonicalRetrievalProvider,
    private val maxCandidates: Int = 8,
    private val maxSelected: Int = 4,
) : CanonicalRetrievalPort {

    init {
        require(maxCandidates > 0) { "maxCandidates must be > 0" }
        require(maxSelected in 1..maxCandidates) { "maxSelected must be in 1..maxCandidates" }
    }

    override fun retrieve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val observation = turn.requireCanonicalUnderstandingV3()
        val context = turn.requireCanonicalContextSnapshot()

        if (observation.claims.isEmpty()) {
            return turn.copy(
                retrievalResults = MipField.notApplicable(),
                diagnostics = turn.diagnostics
                    .retrieval(
                        DiagnosticSnapshot(
                            module = "RETRIEVAL",
                            input = "claims=0; snapshotId=${context.snapshotId}",
                            output = "NOT_APPLICABLE",
                            decision = "SKIP_NO_CLAIMS",
                            status = "PASS",
                            reasonCodes = listOf("RETRIEVAL.NO_CLAIMS"),
                        )
                    )
                    .reason("RETRIEVAL.NO_CLAIMS")
                    .add("retrieval.canonical.not_applicable"),
            )
        }

        val memoryAvailability = context.availabilityOf(ContextDomain.MEMORY)
        val results = observation.claims.map { claim ->
            val query = buildQuery(observation, claim, context)
            val result = when (memoryAvailability) {
                DomainAvailability.AVAILABLE -> callProvider(turn, query, context)
                DomainAvailability.ERROR -> RetrievalResult(
                    queryId = query.queryId,
                    status = RetrievalStatus.ERROR,
                    claimId = MipField.present(claim.claimId),
                    contextSnapshotId = MipField.present(context.snapshotId),
                    reasonCodes = listOf("RETRIEVAL.CONTEXT_MEMORY_ERROR"),
                )
                DomainAvailability.NOT_WIRED,
                DomainAvailability.UNAVAILABLE -> RetrievalResult(
                    queryId = query.queryId,
                    status = RetrievalStatus.INDEX_UNAVAILABLE,
                    claimId = MipField.present(claim.claimId),
                    contextSnapshotId = MipField.present(context.snapshotId),
                    reasonCodes = listOf(
                        if (memoryAvailability == DomainAvailability.NOT_WIRED) {
                            "RETRIEVAL.MEMORY_NOT_WIRED"
                        } else {
                            "RETRIEVAL.INDEX_UNAVAILABLE"
                        }
                    ),
                )
            }
            validateBinding(turn, query, result, context)
        }

        val statusCounts = results.groupingBy { it.status }.eachCount()
        val diagnosticStatus = when {
            results.any { it.status == RetrievalStatus.ERROR } -> "ERROR"
            results.any { it.status == RetrievalStatus.INDEX_UNAVAILABLE } -> "UNAVAILABLE"
            results.any { it.status == RetrievalStatus.AMBIGUOUS } -> "PARTIAL"
            else -> "PASS"
        }
        val reasonCodes = results.flatMap { it.reasonCodes }.distinct().ifEmpty {
            listOf("RETRIEVAL.RESULTS_BOUND")
        }
        var diagnostics = turn.diagnostics
            .retrieval(
                DiagnosticSnapshot(
                    module = "RETRIEVAL",
                    input = "claims=${observation.claims.size}; snapshotId=${context.snapshotId}",
                    output = "results=${results.size}; statuses=$statusCounts",
                    decision = "CLAIM_BOUND_RETRIEVAL",
                    status = diagnosticStatus,
                    reasonCodes = reasonCodes,
                    metadata = mapOf(
                        "contextSnapshotId" to context.snapshotId,
                        "queryIds" to results.joinToString(",") { it.queryId },
                        "claimIds" to results.joinToString(",") { it.claimId.value ?: "-" },
                    ),
                )
            )
            .reason("RETRIEVAL.RESULTS_BOUND")
            .add("retrieval.canonical.completed")
            .tag("retrieval.context_snapshot_id", context.snapshotId)
            .tag("retrieval.result_count", results.size.toString())

        results.firstOrNull { it.status == RetrievalStatus.ERROR }?.let { errorResult ->
            diagnostics = diagnostics.diverge(
                "RETRIEVAL.RESULT_ERROR.${errorResult.claimId.value ?: "UNBOUND"}"
            )
        }

        return turn.copy(
            retrievalResults = MipField.present(results),
            diagnostics = diagnostics,
        )
    }

    private fun buildQuery(
        observation: MipUnderstandingV3Observation,
        claim: MipUnderstandingV3Claim,
        context: MatrixContextSnapshot,
    ): RetrievalQuery {
        val candidates = observation.referentCandidates.associateBy { it.candidateId }
        val mentions = observation.mentions.associateBy { it.mentionId }
        val subjectRefs = claim.subjectReferent
            .takeIf { it.fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED }
            ?.value
            ?.let(candidates::get)
            ?.entityRef
            ?.let(::listOf)
            .orEmpty()
        val entityRefs = claim.entityMentionIds
            .mapNotNull(mentions::get)
            .map { it.entityRef }
            .distinct()
        val predicates = claim.predicate
            .takeIf {
                it.fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED && it.value != "UNKNOWN"
            }
            ?.value
            ?.let(::listOf)
            .orEmpty()
        val temporal = claim.temporalRelation
            .takeIf { it.fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED }
            ?.value
            ?.let { value ->
                buildString {
                    append(value.relation)
                    value.anchorRef?.let { append('@').append(it) }
                }
            }
            ?.let { MipField.present(it) }
            ?: MipField.notApplicable()

        return RetrievalQuery(
            queryId = "retrieval:${turnSafeId(context.turnId)}:${claim.claimId}",
            purpose = RetrievalPurpose.CHECK_CONTRADICTION,
            agentId = context.agentId,
            claimId = MipField.present(claim.claimId),
            subjectRefs = subjectRefs,
            entityRefs = entityRefs,
            predicates = predicates,
            temporalConstraint = temporal,
            maxCandidates = maxCandidates,
            maxSelected = maxSelected,
            contextSnapshotId = context.snapshotId,
        )
    }

    private fun callProvider(
        turn: MatrixTurnFrame,
        query: RetrievalQuery,
        context: MatrixContextSnapshot,
    ): RetrievalResult = try {
        provider.retrieve(query, context)
    } catch (error: RuntimeException) {
        throw boundaryFailure(
            turn,
            "RETRIEVAL.PROVIDER_EXCEPTION",
            "Canonical retrieval provider failed: ${error::class.simpleName}",
        )
    }

    private fun validateBinding(
        turn: MatrixTurnFrame,
        query: RetrievalQuery,
        result: RetrievalResult,
        context: MatrixContextSnapshot,
    ): RetrievalResult {
        if (result.queryId != query.queryId) {
            throw boundaryFailure(
                turn,
                "RETRIEVAL.QUERY_ID_MISMATCH",
                "Retrieval result queryId=${result.queryId} does not match ${query.queryId}",
            )
        }
        return try {
            result.requireBinding(query.requireClaimBinding(), context.snapshotId)
        } catch (error: IllegalArgumentException) {
            throw boundaryFailure(
                turn,
                "RETRIEVAL.BINDING_MISMATCH",
                error.message ?: "Retrieval result binding mismatch",
            )
        }
    }

    private fun boundaryFailure(
        turn: MatrixTurnFrame,
        code: String,
        message: String,
    ): MatrixBoundaryViolationException = MatrixBoundaryViolationException(
        message,
        turn.diagnostics
            .diverge(code)
            .reason(code)
            .add("retrieval.canonical.boundary_failure"),
    )

    private fun turnSafeId(value: String): String = value.replace(':', '_')
}
