package matrix.assembling.authority.runtime

import matrix.assembling.AuthorityResolverPort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixBoundaryViolationException
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.authority.AuthorityResolveRequest
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.AuthorityResolver
import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.MipSpan
import matrix.assembling.mip.MipUnderstandingV3Claim
import matrix.assembling.mip.MipUnderstandingV3Field
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus
import matrix.assembling.mip.RetrievalResult

/**
 * Canonical orchestrator-facing Authority port for real Understanding V3 claims.
 *
 * The projection is structural only: it consumes V3 fields, candidate identities and evidence
 * spans. It never reparses natural-language text, never invents referents, and never writes Memory.
 */
class CanonicalUnderstandingV3AuthorityPort(
    private val resolver: AuthorityResolver,
) : AuthorityResolverPort {

    override fun resolve(turn: MatrixTurnFrame): MatrixTurnFrame {
        val observation = turn.canonicalUnderstandingV3.value
            ?.takeIf { turn.canonicalUnderstandingV3.status == MipFieldStatus.PRESENT }
            ?: throw boundaryFailure(
                turn,
                REASON_UNDERSTANDING_UNAVAILABLE,
                "Canonical Authority requires PRESENT canonicalUnderstandingV3",
            )
        val context = turn.contextSnapshot.value
            ?.takeIf { turn.contextSnapshot.status == MipFieldStatus.PRESENT }
            ?: throw boundaryFailure(
                turn,
                REASON_CONTEXT_UNAVAILABLE,
                "Canonical Authority requires PRESENT contextSnapshot",
            )

        val resolutions = observation.claims.map { claim ->
            val projected = UnderstandingV3AuthorityProjection.project(observation, claim)
            resolver.resolve(
                AuthorityResolveRequest(
                    requestId = "${turn.turnId}:${claim.claimId}:authority",
                    claim = projected,
                    contextSnapshot = context,
                    retrievalResult = retrievalForClaim(turn, observation.claims.size),
                    provenance = claim.provenance,
                )
            )
        }

        val status = when {
            resolutions.any { it.resolutionStatus == AuthorityResolutionStatus.ERROR } -> "ERROR"
            resolutions.any { it.resolutionStatus == AuthorityResolutionStatus.UNAVAILABLE } -> "UNAVAILABLE"
            resolutions.any { it.resolutionStatus == AuthorityResolutionStatus.HOLD } -> "HOLD"
            resolutions.any { it.resolutionStatus == AuthorityResolutionStatus.PARTIAL } -> "PARTIAL"
            else -> "PASS"
        }
        val reasonCodes = resolutions.flatMap { it.reasonCodes }.distinct()
        val decision = when {
            resolutions.isEmpty() -> "NO_TYPED_CLAIMS"
            status == "PASS" -> "CANONICAL_AUTHORITY_RESOLVED"
            else -> "CANONICAL_AUTHORITY_$status"
        }

        return turn.copy(
            canonicalAuthorityResolutions = MipField.present(resolutions),
            diagnostics = turn.diagnostics
                .authority(
                    DiagnosticSnapshot(
                        module = "AUTHORITY",
                        decision = decision,
                        status = status,
                        reasonCodes = reasonCodes,
                        metadata = mapOf(
                            "claimCount" to observation.claims.size.toString(),
                            "resolutionCount" to resolutions.size.toString(),
                            "source" to "UNDERSTANDING_V3",
                        ),
                    )
                )
                .reason("AUTHORITY_V3_TYPED_CLAIMS_CONSUMED")
                .add("authority.v3.resolved")
                .tag("authority.input", "UNDERSTANDING_V3"),
        )
    }

    /**
     * RetrievalResult currently has no claimId binding in MIP-1.0. Therefore only the
     * unambiguous 1-claim/1-result case is bound here; multi-claim turns stay UNRESOLVED rather
     * than guessing by list order or parsing query IDs.
     */
    private fun retrievalForClaim(turn: MatrixTurnFrame, claimCount: Int): MipField<RetrievalResult> =
        when (turn.retrievalResults.status) {
            MipFieldStatus.PRESENT -> {
                val results = requireNotNull(turn.retrievalResults.value)
                if (claimCount == 1 && results.size == 1) MipField.present(results.single())
                else MipField.unresolved()
            }
            MipFieldStatus.NOT_APPLICABLE -> MipField.notApplicable()
            MipFieldStatus.UNKNOWN -> MipField.unknown()
            MipFieldStatus.UNRESOLVED -> MipField.unresolved()
            MipFieldStatus.UNAVAILABLE -> MipField.unavailable()
            MipFieldStatus.ERROR -> MipField.error()
            else -> MipField.error()
        }

    private fun boundaryFailure(turn: MatrixTurnFrame, code: String, message: String): MatrixBoundaryViolationException =
        MatrixBoundaryViolationException(
            message,
            turn.diagnostics
                .diverge(code)
                .add("authority.v3.boundary_failure")
                .tag("authority.input", "UNDERSTANDING_V3"),
        )

    companion object {
        const val REASON_UNDERSTANDING_UNAVAILABLE = "AUTHORITY.RUNTIME.UNDERSTANDING_V3_UNAVAILABLE"
        const val REASON_CONTEXT_UNAVAILABLE = "AUTHORITY.RUNTIME.CONTEXT_UNAVAILABLE"
    }
}

/** Authority-specific, fail-closed projection from the lossless V3 carrier into AUTHORITY-1.0. */
internal object UnderstandingV3AuthorityProjection {
    fun project(
        observation: MipUnderstandingV3Observation,
        claim: MipUnderstandingV3Claim,
    ): MipClaimV1 {
        val candidates = observation.referentCandidates.associateBy { it.candidateId }
        val candidateRefs = candidates.mapValues { it.value.entityRef }
        val interpretationResolved =
            claim.structuralStatus == MipUnderstandingV3StructuralStatus.VALID &&
                claim.interpretationStatus == MipUnderstandingV3InterpretationStatus.RESOLVED

        val subject = if (interpretationResolved) {
            role(claim.subjectReferent, candidateRefs)
        } else {
            MipEntityRef(
                resolutionStatus = if (claim.interpretationStatus == MipUnderstandingV3InterpretationStatus.AMBIGUOUS) {
                    MipEntityResolutionStatus.AMBIGUOUS
                } else {
                    MipEntityResolutionStatus.UNRESOLVED
                }
            )
        }

        val temporal = claim.temporalRelation.value
        val semanticMarkers = buildMap<String, MipField<String>> {
            put(MARKER_CLAIM_KIND, claim.claimKind.toMipField())
            put(MARKER_V3_STRUCTURAL_STATUS, MipField.present(claim.structuralStatus.name))
            put(MARKER_V3_INTERPRETATION_STATUS, MipField.present(claim.interpretationStatus.name))
            temporal.anchorRef?.let { anchor ->
                if (claim.temporalRelation.fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED) {
                    put(MARKER_TEMPORAL_REFERENCE_KEY, MipField.present(anchor))
                } else {
                    put(MARKER_TEMPORAL_REFERENCE_KEY, MipField.unresolved())
                }
            }
        }

        return MipClaimV1(
            claimId = claim.claimId,
            speaker = observation.speaker,
            observer = observation.observer,
            source = role(claim.sourceReferent, candidateRefs),
            subject = subject,
            target = role(claim.targetReferent, candidateRefs),
            owner = role(claim.ownerReferent, candidateRefs),
            perspective = role(claim.perspectiveReferent, candidateRefs),
            predicate = claim.predicate.resolvedValueOrNull()?.takeUnless { it == "UNKNOWN" } ?: "speech.unresolved",
            objectValue = objectValue(observation.input, claim.objectSpans, interpretationResolved),
            dialogueAct = claim.dialogueAct.toMipField(),
            polarity = claim.polarity.resolvedValueOrNull()?.takeUnless { it == "UNKNOWN" } ?: "UNKNOWN",
            temporalRelation = claim.temporalRelation.resolvedValueOrNull()?.relation ?: "UNKNOWN",
            sourceType = sourceType(claim.claimKind),
            interpretationConfidence = MipField.present(claim.overallInterpretationConfidence),
            confidenceByField = claim.confidenceByField,
            sourceSpans = mapOf(
                "source" to claim.sourceSpan,
                "subject" to claim.subjectSpans.singleOrNull(),
                "object" to claim.objectSpans.singleOrNull(),
                "negation" to claim.negationCueSpans.singleOrNull(),
                "temporal" to claim.temporalEvidence.singleOrNull()?.span,
            ),
            epistemicClass = MipField.unknown(),
            semanticMarkers = semanticMarkers,
        )
    }

    private fun role(
        field: MipUnderstandingV3Field<String>,
        candidates: Map<String, MipEntityRef>,
    ): MipEntityRef = when (field.fieldStatus) {
        MipUnderstandingV3FieldStatus.RESOLVED ->
            candidates[field.value]
                ?: MipEntityRef(resolutionStatus = MipEntityResolutionStatus.UNRESOLVED)
        MipUnderstandingV3FieldStatus.UNKNOWN ->
            MipEntityRef(resolutionStatus = MipEntityResolutionStatus.UNKNOWN)
        MipUnderstandingV3FieldStatus.AMBIGUOUS ->
            MipEntityRef(resolutionStatus = MipEntityResolutionStatus.AMBIGUOUS)
        MipUnderstandingV3FieldStatus.NOT_APPLICABLE ->
            MipEntityRef(resolutionStatus = MipEntityResolutionStatus.NOT_APPLICABLE)
    }

    private fun objectValue(input: String, spans: List<MipSpan>, interpretationResolved: Boolean): MipField<String> {
        if (!interpretationResolved) return MipField.unresolved()
        if (spans.isEmpty()) return MipField.notApplicable()
        if (spans.size != 1) return MipField.ambiguous()
        val span = spans.single()
        val value = input.substring(span.start, span.end).trim()
        return if (value.isBlank()) MipField.unresolved() else MipField.present(value)
    }

    private fun sourceType(claimKind: MipUnderstandingV3Field<String>): MipField<String> =
        when (claimKind.fieldStatus) {
            MipUnderstandingV3FieldStatus.RESOLVED -> when (claimKind.value) {
                "DIRECT" -> MipField.present("USER_ASSERTION")
                "REPORT" -> MipField.present("REPORT")
                "BELIEF" -> MipField.present("BELIEF")
                "HYPOTHESIS" -> MipField.present("HYPOTHESIS")
                else -> MipField.unknown()
            }
            MipUnderstandingV3FieldStatus.UNKNOWN -> MipField.unknown()
            MipUnderstandingV3FieldStatus.AMBIGUOUS -> MipField.ambiguous()
            MipUnderstandingV3FieldStatus.NOT_APPLICABLE -> MipField.notApplicable()
        }

    private fun MipUnderstandingV3Field<String>.toMipField(): MipField<String> =
        when (fieldStatus) {
            MipUnderstandingV3FieldStatus.RESOLVED ->
                if (value == "UNKNOWN") MipField.unknown() else MipField.present(value)
            MipUnderstandingV3FieldStatus.UNKNOWN -> MipField.unknown()
            MipUnderstandingV3FieldStatus.AMBIGUOUS -> MipField.ambiguous()
            MipUnderstandingV3FieldStatus.NOT_APPLICABLE -> MipField.notApplicable()
        }

    private fun <T : Any> MipUnderstandingV3Field<T>.resolvedValueOrNull(): T? =
        value.takeIf { fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED }

    private const val MARKER_CLAIM_KIND = "CLAIM_KIND"
    private const val MARKER_TEMPORAL_REFERENCE_KEY = "TEMPORAL_REFERENCE_KEY"
    private const val MARKER_V3_STRUCTURAL_STATUS = "V3_STRUCTURAL_STATUS"
    private const val MARKER_V3_INTERPRETATION_STATUS = "V3_INTERPRETATION_STATUS"
}
