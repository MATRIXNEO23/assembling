package matrix.assembling.authority

import matrix.assembling.mip.MipClaimV1
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

/** Canonical AUTHORITY-1.0 resolver boundary. */
fun interface AuthorityResolver {
    fun resolve(request: AuthorityResolveRequest): AuthorityResolution
}

/**
 * Deterministic AUTHORITY-1.0 implementation.
 *
 * The resolver consumes structured semantics only. It never reparses natural-language text,
 * never mutates Memory, never decides Memory Admission, and never depends on retrieval score as
 * truth/authority evidence.
 */
class DeterministicAuthorityResolver(
    private val candidateEvidencePort: AuthorityCandidateEvidencePort,
    private val singleValuePredicates: Set<String> = DEFAULT_SINGLE_VALUE_PREDICATES,
) : AuthorityResolver {

    override fun resolve(request: AuthorityResolveRequest): AuthorityResolution {
        val classification = classify(request)
        val baseReasons = classification.reasonCodes.toMutableList()
        val ambiguityReasons = classification.ambiguityReasons.toMutableList()

        if (classification.fatalStatus == AuthorityResolutionStatus.ERROR) {
            return buildResolution(
                request = request,
                resolutionStatus = AuthorityResolutionStatus.ERROR,
                authority = classification.authority,
                authorityConfidence = classification.confidence,
                contradictedMemoryRef = MipField.error(),
                candidateMemoryRefs = emptyList(),
                ambiguityReasons = ambiguityReasons,
                reasonCodes = baseReasons + AuthorityReasonCode.ERROR,
            )
        }

        val semanticHold = semanticHold(request.claim, classification.authority.value)
        if (semanticHold != null) {
            baseReasons += semanticHold.reasonCode
            ambiguityReasons += semanticHold.detail
            return buildResolution(
                request = request,
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                authority = classification.authority,
                authorityConfidence = classification.confidence,
                contradictedMemoryRef = semanticHold.contradictionState,
                candidateMemoryRefs = retrievalCandidateRefs(request),
                ambiguityReasons = ambiguityReasons,
                reasonCodes = baseReasons,
            )
        }

        if (classification.fatalStatus == AuthorityResolutionStatus.HOLD ||
            classification.authority.status != MipFieldStatus.PRESENT
        ) {
            if (baseReasons.none { it == REASON_CLASSIFICATION_UNRESOLVED }) {
                baseReasons += REASON_CLASSIFICATION_UNRESOLVED
            }
            return buildResolution(
                request = request,
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                authority = classification.authority,
                authorityConfidence = classification.confidence,
                contradictedMemoryRef = MipField.unresolved(),
                candidateMemoryRefs = retrievalCandidateRefs(request),
                ambiguityReasons = ambiguityReasons.ifEmpty { listOf("authority classification unresolved") },
                reasonCodes = baseReasons,
            )
        }

        val act = request.claim.dialogueAct.value?.uppercase()
        if (act in NON_ASSERTIVE_DIALOGUE_ACTS) {
            return buildResolution(
                request = request,
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                authority = classification.authority,
                authorityConfidence = classification.confidence,
                contradictedMemoryRef = MipField.notApplicable(),
                candidateMemoryRefs = emptyList(),
                ambiguityReasons = listOf("non-assertive dialogue act does not authorize contradiction targeting"),
                reasonCodes = baseReasons + REASON_HOLD_NON_ASSERTIVE,
            )
        }

        val assessment = assessContradiction(request, classification.authority.value!!)
        return buildResolution(
            request = request,
            resolutionStatus = assessment.resolutionStatus,
            authority = classification.authority,
            authorityConfidence = classification.confidence,
            contradictedMemoryRef = assessment.contradictedMemoryRef,
            candidateMemoryRefs = assessment.candidateMemoryRefs,
            ambiguityReasons = assessment.ambiguityReasons,
            reasonCodes = baseReasons + assessment.reasonCodes,
        )
    }

    private fun classify(request: AuthorityResolveRequest): ClassificationOutcome {
        val claim = request.claim
        val reasons = mutableListOf<String>()

        val explicitAuthority = claim.epistemicClass.value?.let { raw ->
            try {
                EpistemicClass.valueOf(raw.uppercase())
            } catch (_: IllegalArgumentException) {
                return ClassificationOutcome(
                    authority = MipField.error(),
                    confidence = MipField.error(),
                    reasonCodes = listOf(REASON_CLASSIFICATION_UNSUPPORTED),
                    fatalStatus = AuthorityResolutionStatus.ERROR,
                )
            }
        }

        if (explicitAuthority == EpistemicClass.WORLD_TRUTH) {
            if (isTrustedWorldProvenance(request)) {
                return resolvedClassification(EpistemicClass.WORLD_TRUTH, AuthorityReasonCode.RESOLVED_WORLD_TRUTH)
            }
            reasons += REASON_WORLD_TRUTH_PROVENANCE_REJECTED
        }

        if (explicitAuthority == EpistemicClass.OBSERVATION) {
            if (isTrustedObservationProvenance(request)) {
                return resolvedClassification(EpistemicClass.OBSERVATION, AuthorityReasonCode.RESOLVED_OBSERVATION)
            }
            reasons += REASON_OBSERVATION_PROVENANCE_REJECTED
        }

        if (explicitAuthority == EpistemicClass.INFERENCE) {
            if (isTrustedInferenceProvenance(request)) {
                return resolvedClassification(EpistemicClass.INFERENCE, AuthorityReasonCode.RESOLVED_INFERENCE)
            }
            return ClassificationOutcome(
                authority = MipField.unresolved(),
                confidence = MipField.unresolved(),
                reasonCodes = reasons + REASON_INFERENCE_DERIVATION_MISSING,
                fatalStatus = AuthorityResolutionStatus.HOLD,
                ambiguityReasons = listOf("INFERENCE requested without explicit derived-from provenance"),
            )
        }

        if (explicitAuthority == EpistemicClass.REPORT) {
            return resolvedClassification(EpistemicClass.REPORT, AuthorityReasonCode.RESOLVED_REPORT, reasons)
        }
        if (explicitAuthority == EpistemicClass.BELIEF) {
            return resolvedClassification(EpistemicClass.BELIEF, AuthorityReasonCode.RESOLVED_BELIEF, reasons)
        }

        if (isTrustedObservationProvenance(request)) {
            return resolvedClassification(EpistemicClass.OBSERVATION, AuthorityReasonCode.RESOLVED_OBSERVATION, reasons)
        }
        if (isTrustedInferenceProvenance(request)) {
            return resolvedClassification(EpistemicClass.INFERENCE, AuthorityReasonCode.RESOLVED_INFERENCE, reasons)
        }

        val claimKind = claim.semanticMarkers[MARKER_CLAIM_KIND]
        if (claimKind?.status == MipFieldStatus.PRESENT) {
            when (claimKind.value?.uppercase()) {
                "REPORT" -> return resolvedClassification(EpistemicClass.REPORT, AuthorityReasonCode.RESOLVED_REPORT, reasons)
                "BELIEF", "HYPOTHESIS" -> return resolvedClassification(EpistemicClass.BELIEF, AuthorityReasonCode.RESOLVED_BELIEF, reasons)
            }
        } else if (claimKind != null && claimKind.status in UNCERTAIN_FIELD_STATES) {
            reasons += REASON_CLAIM_KIND_UNRESOLVED
        }

        if (claim.sourceType.status == MipFieldStatus.PRESENT) {
            when (claim.sourceType.value?.uppercase()) {
                "THIRD_PARTY_REPORT", "USER_ASSERTION", "SELF_REPORT", "REPORT", "NPC_ASSERTION" ->
                    return resolvedClassification(EpistemicClass.REPORT, AuthorityReasonCode.RESOLVED_REPORT, reasons)
                "BELIEF", "OPINION", "HYPOTHESIS" ->
                    return resolvedClassification(EpistemicClass.BELIEF, AuthorityReasonCode.RESOLVED_BELIEF, reasons)
                "INFERENCE" -> {
                    if (isTrustedInferenceProvenance(request)) {
                        return resolvedClassification(EpistemicClass.INFERENCE, AuthorityReasonCode.RESOLVED_INFERENCE, reasons)
                    }
                    return ClassificationOutcome(
                        authority = MipField.unresolved(),
                        confidence = MipField.unresolved(),
                        reasonCodes = reasons + REASON_INFERENCE_DERIVATION_MISSING,
                        fatalStatus = AuthorityResolutionStatus.HOLD,
                        ambiguityReasons = listOf("sourceType=INFERENCE without explicit derived-from provenance"),
                    )
                }
            }
        }

        if (claim.dialogueAct.status == MipFieldStatus.PRESENT && claim.dialogueAct.value?.uppercase() == "HYPOTHESIS") {
            return resolvedClassification(EpistemicClass.BELIEF, AuthorityReasonCode.RESOLVED_BELIEF, reasons)
        }

        return ClassificationOutcome(
            authority = MipField.unresolved(),
            confidence = MipField.unresolved(),
            reasonCodes = (reasons + REASON_CLASSIFICATION_UNRESOLVED).distinct(),
            fatalStatus = AuthorityResolutionStatus.HOLD,
            ambiguityReasons = listOf("no trusted structured evidence resolves EpistemicClass"),
        )
    }

    private fun semanticHold(claim: MipClaimV1, authority: EpistemicClass?): SemanticHold? {
        if (claim.subject.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
            return SemanticHold(
                reasonCode = AuthorityReasonCode.SUBJECT_UNRESOLVED,
                detail = "subject identity unresolved",
                contradictionState = MipField.unresolved(),
            )
        }
        if (claim.owner.resolutionStatus in UNSAFE_ENTITY_STATES) {
            return SemanticHold(
                reasonCode = AuthorityReasonCode.OWNER_UNRESOLVED,
                detail = "owner identity unresolved",
                contradictionState = MipField.unresolved(),
            )
        }
        if (authority == EpistemicClass.REPORT && claim.source.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
            return SemanticHold(
                reasonCode = AuthorityReasonCode.SOURCE_UNRESOLVED,
                detail = "REPORT source identity unresolved",
                contradictionState = MipField.unresolved(),
            )
        }
        if (authority == EpistemicClass.BELIEF && claim.perspective.resolutionStatus != MipEntityResolutionStatus.RESOLVED) {
            return SemanticHold(
                reasonCode = REASON_PERSPECTIVE_UNRESOLVED,
                detail = "BELIEF perspective identity unresolved",
                contradictionState = MipField.unresolved(),
            )
        }
        return null
    }

    private fun assessContradiction(
        request: AuthorityResolveRequest,
        authority: EpistemicClass,
    ): ContradictionAssessment {
        return when (request.retrievalResult.status) {
            MipFieldStatus.NOT_APPLICABLE -> ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.COMPLETE,
                contradictedMemoryRef = MipField.notApplicable(),
                reasonCodes = listOf(AuthorityReasonCode.CONTRADICTION_NONE),
            )
            MipFieldStatus.UNKNOWN -> unresolvedAssessment(AuthorityReasonCode.TEMPORAL_UNRESOLVED, "retrieval evidence unknown")
            MipFieldStatus.UNRESOLVED -> unresolvedAssessment(REASON_RETRIEVAL_UNRESOLVED, "retrieval evidence unresolved")
            MipFieldStatus.UNAVAILABLE -> ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.UNAVAILABLE,
                contradictedMemoryRef = MipField.unavailable(),
                reasonCodes = listOf(AuthorityReasonCode.RETRIEVAL_UNAVAILABLE),
                ambiguityReasons = listOf("retrieval provider unavailable"),
            )
            MipFieldStatus.ERROR -> ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.ERROR,
                contradictedMemoryRef = MipField.error(),
                reasonCodes = listOf(REASON_RETRIEVAL_ERROR, AuthorityReasonCode.ERROR),
                ambiguityReasons = listOf("retrieval failed"),
            )
            MipFieldStatus.PRESENT -> assessPresentRetrieval(request, authority, request.retrievalResult.value!!)
            else -> ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.ERROR,
                contradictedMemoryRef = MipField.error(),
                reasonCodes = listOf(REASON_RETRIEVAL_ILLEGAL_STATE, AuthorityReasonCode.ERROR),
                ambiguityReasons = listOf("illegal retrieval field state ${request.retrievalResult.status}"),
            )
        }
    }

    private fun assessPresentRetrieval(
        request: AuthorityResolveRequest,
        authority: EpistemicClass,
        retrieval: RetrievalResult,
    ): ContradictionAssessment {
        when (retrieval.status) {
            RetrievalStatus.NO_MATCH -> return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.COMPLETE,
                contradictedMemoryRef = MipField.notApplicable(),
                reasonCodes = listOf(AuthorityReasonCode.RETRIEVAL_NO_MATCH, AuthorityReasonCode.CONTRADICTION_NONE),
            )
            RetrievalStatus.INDEX_UNAVAILABLE -> return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.UNAVAILABLE,
                contradictedMemoryRef = MipField.unavailable(),
                reasonCodes = listOf(AuthorityReasonCode.RETRIEVAL_UNAVAILABLE),
                ambiguityReasons = listOf("retrieval index unavailable"),
            )
            RetrievalStatus.ERROR -> return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.ERROR,
                contradictedMemoryRef = MipField.error(),
                reasonCodes = listOf(REASON_RETRIEVAL_ERROR, AuthorityReasonCode.ERROR),
                ambiguityReasons = listOf("retrieval result reports ERROR"),
            )
            RetrievalStatus.MATCHED,
            RetrievalStatus.AMBIGUOUS -> Unit
        }

        val refs = (if (retrieval.selectedRefs.isNotEmpty()) retrieval.selectedRefs else retrieval.candidateRefs)
            .map(::MemoryRef)
        val reasons = mutableListOf<String>()
        val ambiguity = mutableListOf<String>()
        if (request.claim.dialogueAct.value?.uppercase() == "CORRECT") {
            reasons += AuthorityReasonCode.CORRECTION_CANDIDATE
        }

        val contradictions = mutableListOf<MemoryRef>()
        var unresolvedEvidence = false
        var unavailableEvidence = false
        var ambiguousEvidence = false
        var errorEvidence = false

        refs.forEach { ref ->
            val evidenceField = candidateEvidencePort.read(ref, request.contextSnapshot)
            when (evidenceField.status) {
                MipFieldStatus.PRESENT -> {
                    val evidence = evidenceField.value!!
                    if (evidence.memoryRef != ref) {
                        errorEvidence = true
                        reasons += REASON_CANDIDATE_ID_MISMATCH
                    } else {
                        when (val comparison = compareCandidate(request.claim, authority, evidence)) {
                            is CandidateComparison.Contradiction -> {
                                contradictions += ref
                                reasons += comparison.reasonCode
                            }
                            is CandidateComparison.NoContradiction -> reasons += comparison.reasonCode
                            is CandidateComparison.Unresolved -> {
                                unresolvedEvidence = true
                                reasons += comparison.reasonCode
                                ambiguity += comparison.detail
                            }
                        }
                    }
                }
                MipFieldStatus.UNAVAILABLE -> {
                    unavailableEvidence = true
                    reasons += REASON_CANDIDATE_EVIDENCE_UNAVAILABLE
                }
                MipFieldStatus.ERROR -> {
                    errorEvidence = true
                    reasons += REASON_CANDIDATE_EVIDENCE_ERROR
                }
                MipFieldStatus.AMBIGUOUS,
                MipFieldStatus.CONFLICTED -> {
                    ambiguousEvidence = true
                    reasons += AuthorityReasonCode.CONTRADICTION_AMBIGUOUS
                    ambiguity += "candidate evidence ${ref.value} is ${evidenceField.status}"
                }
                else -> {
                    unresolvedEvidence = true
                    reasons += REASON_CANDIDATE_EVIDENCE_UNRESOLVED
                    ambiguity += "candidate evidence ${ref.value} is ${evidenceField.status}"
                }
            }
        }

        if (errorEvidence) {
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.ERROR,
                contradictedMemoryRef = MipField.error(),
                candidateMemoryRefs = refs,
                ambiguityReasons = ambiguity.ifEmpty { listOf("candidate evidence error") },
                reasonCodes = reasons + AuthorityReasonCode.ERROR,
            ).deduplicated()
        }
        if (unavailableEvidence) {
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.UNAVAILABLE,
                contradictedMemoryRef = MipField.unavailable(),
                candidateMemoryRefs = refs,
                ambiguityReasons = ambiguity.ifEmpty { listOf("candidate evidence unavailable") },
                reasonCodes = reasons + AuthorityReasonCode.RETRIEVAL_UNAVAILABLE,
            ).deduplicated()
        }
        if (contradictions.size > 1) {
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                contradictedMemoryRef = MipField.ambiguous(),
                candidateMemoryRefs = refs,
                ambiguityReasons = ambiguity + "multiple valid contradiction targets: ${contradictions.joinToString { it.value }}",
                reasonCodes = reasons + AuthorityReasonCode.CONTRADICTION_AMBIGUOUS + AuthorityReasonCode.HOLD_AMBIGUOUS,
            ).deduplicated()
        }
        if (ambiguousEvidence) {
            val state: MipField<MemoryRef> = if (refs.size >= 2) MipField.ambiguous() else MipField.unresolved()
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.HOLD,
                contradictedMemoryRef = state,
                candidateMemoryRefs = refs,
                ambiguityReasons = ambiguity.ifEmpty { listOf("candidate evidence ambiguous") },
                reasonCodes = reasons + AuthorityReasonCode.HOLD_AMBIGUOUS,
            ).deduplicated()
        }
        if (unresolvedEvidence) {
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.PARTIAL,
                contradictedMemoryRef = MipField.unresolved(),
                candidateMemoryRefs = refs,
                ambiguityReasons = ambiguity.ifEmpty { listOf("candidate evidence incomplete") },
                reasonCodes = reasons + REASON_CONTRADICTION_INCOMPLETE,
            ).deduplicated()
        }
        if (contradictions.size == 1) {
            return ContradictionAssessment(
                resolutionStatus = AuthorityResolutionStatus.COMPLETE,
                contradictedMemoryRef = MipField.present(contradictions.single()),
                candidateMemoryRefs = refs,
                reasonCodes = reasons + AuthorityReasonCode.CONTRADICTION_IDENTIFIED,
            ).deduplicated()
        }

        return ContradictionAssessment(
            resolutionStatus = AuthorityResolutionStatus.COMPLETE,
            contradictedMemoryRef = MipField.notApplicable(),
            candidateMemoryRefs = refs,
            reasonCodes = reasons + AuthorityReasonCode.CONTRADICTION_NONE,
        ).deduplicated()
    }

    private fun compareCandidate(
        claim: MipClaimV1,
        authority: EpistemicClass,
        candidate: AuthorityCandidateEvidence,
    ): CandidateComparison {
        if (candidate.validity.status != MipFieldStatus.PRESENT) {
            return CandidateComparison.Unresolved(REASON_CANDIDATE_VALIDITY_UNRESOLVED, "candidate validity unresolved")
        }
        if (candidate.validity.value != "VALID") {
            return CandidateComparison.NoContradiction(REASON_CANDIDATE_NOT_VALID)
        }

        val subject = compareEntityRole(claim.subject, candidate.subject)
        if (subject == RoleComparison.UNRESOLVED) {
            return CandidateComparison.Unresolved(AuthorityReasonCode.SUBJECT_UNRESOLVED, "candidate subject identity unresolved")
        }
        if (subject == RoleComparison.MISMATCH) {
            return CandidateComparison.NoContradiction(REASON_DIFFERENT_SEMANTIC_SLOT)
        }

        val normalizedClaimPredicate = normalizePredicate(claim.predicate)
        val normalizedCandidatePredicate = normalizePredicate(candidate.predicate)
        if (normalizedClaimPredicate != normalizedCandidatePredicate) {
            return CandidateComparison.NoContradiction(AuthorityReasonCode.CONTRADICTION_UNRELATED_PREDICATE)
        }

        when (compareEntityRole(claim.owner, candidate.owner)) {
            RoleComparison.UNRESOLVED -> return CandidateComparison.Unresolved(AuthorityReasonCode.OWNER_UNRESOLVED, "candidate owner identity unresolved")
            RoleComparison.MISMATCH -> return CandidateComparison.NoContradiction(REASON_DIFFERENT_SEMANTIC_SLOT)
            RoleComparison.MATCH -> Unit
        }

        when (compareEntityRole(claim.target, candidate.target)) {
            RoleComparison.UNRESOLVED -> return CandidateComparison.Unresolved(REASON_TARGET_UNRESOLVED, "candidate target identity unresolved")
            RoleComparison.MISMATCH -> return CandidateComparison.NoContradiction(REASON_DIFFERENT_SEMANTIC_SLOT)
            RoleComparison.MATCH -> Unit
        }

        if (authority == EpistemicClass.REPORT) {
            when (compareEntityRole(claim.source, candidate.source)) {
                RoleComparison.UNRESOLVED -> return CandidateComparison.Unresolved(AuthorityReasonCode.SOURCE_UNRESOLVED, "candidate report source unresolved")
                RoleComparison.MISMATCH -> return CandidateComparison.NoContradiction(REASON_DIFFERENT_SOURCE_SCOPE)
                RoleComparison.MATCH -> Unit
            }
        }
        if (authority == EpistemicClass.BELIEF) {
            when (compareEntityRole(claim.perspective, candidate.perspective)) {
                RoleComparison.UNRESOLVED -> return CandidateComparison.Unresolved(REASON_PERSPECTIVE_UNRESOLVED, "candidate belief perspective unresolved")
                RoleComparison.MISMATCH -> return CandidateComparison.NoContradiction(REASON_DIFFERENT_PERSPECTIVE_SCOPE)
                RoleComparison.MATCH -> Unit
            }
        }

        when (val temporal = compareTemporal(claim, candidate)) {
            TemporalComparison.MISMATCH -> return CandidateComparison.NoContradiction(AuthorityReasonCode.CONTRADICTION_TEMPORAL_MISMATCH)
            TemporalComparison.UNRESOLVED -> return CandidateComparison.Unresolved(AuthorityReasonCode.TEMPORAL_UNRESOLVED, "temporal identity insufficient for contradiction")
            TemporalComparison.MATCH -> Unit
        }

        val candidatePolarity = candidate.polarity.value?.uppercase()
        val claimPolarity = claim.polarity.uppercase()
        if (candidate.polarity.status != MipFieldStatus.PRESENT ||
            candidatePolarity !in RESOLVED_POLARITIES || claimPolarity !in RESOLVED_POLARITIES
        ) {
            return CandidateComparison.Unresolved(REASON_POLARITY_UNRESOLVED, "polarity unresolved")
        }

        val objectComparison = compareScalarField(claim.objectValue, candidate.objectValue)
        if (objectComparison == ScalarComparison.UNRESOLVED) {
            return CandidateComparison.Unresolved(REASON_OBJECT_UNRESOLVED, "object/value identity unresolved")
        }

        if (claimPolarity != candidatePolarity) {
            return if (objectComparison == ScalarComparison.MATCH) {
                CandidateComparison.Contradiction(REASON_OPPOSITE_POLARITY)
            } else {
                CandidateComparison.NoContradiction(REASON_DIFFERENT_SEMANTIC_SLOT)
            }
        }

        if (normalizedClaimPredicate in singleValuePredicates && objectComparison == ScalarComparison.MISMATCH) {
            return CandidateComparison.Contradiction(REASON_SINGLE_VALUE_CONFLICT)
        }

        return CandidateComparison.NoContradiction(AuthorityReasonCode.CONTRADICTION_NONE)
    }

    private fun compareTemporal(claim: MipClaimV1, candidate: AuthorityCandidateEvidence): TemporalComparison {
        val claimRelation = normalizeTemporalRelation(claim.temporalRelation)
        val candidateRelation = candidate.temporalRelation.value?.let(::normalizeTemporalRelation)

        if (claimRelation in UNRESOLVED_TEMPORAL_RELATIONS ||
            candidate.temporalRelation.status != MipFieldStatus.PRESENT ||
            candidateRelation == null || candidateRelation in UNRESOLVED_TEMPORAL_RELATIONS
        ) {
            return TemporalComparison.UNRESOLVED
        }
        if (claimRelation != candidateRelation) return TemporalComparison.MISMATCH
        if (claimRelation in DIRECTLY_COMPARABLE_TEMPORAL_RELATIONS) return TemporalComparison.MATCH

        val claimKey = claim.semanticMarkers[MARKER_TEMPORAL_REFERENCE_KEY]
        val candidateKey = candidate.temporalReferenceKey
        if (claimKey?.status != MipFieldStatus.PRESENT || candidateKey.status != MipFieldStatus.PRESENT) {
            return TemporalComparison.UNRESOLVED
        }
        return if (claimKey.value == candidateKey.value) TemporalComparison.MATCH else TemporalComparison.MISMATCH
    }

    private fun buildResolution(
        request: AuthorityResolveRequest,
        resolutionStatus: AuthorityResolutionStatus,
        authority: MipField<EpistemicClass>,
        authorityConfidence: MipField<AuthorityResolutionConfidence>,
        contradictedMemoryRef: MipField<MemoryRef>,
        candidateMemoryRefs: List<MemoryRef>,
        ambiguityReasons: List<String>,
        reasonCodes: List<String>,
    ): AuthorityResolution {
        val resolutionId = "${request.requestId}:authority"
        return AuthorityResolution(
            resolutionId = resolutionId,
            claimId = request.claim.claimId,
            contextSnapshotId = request.contextSnapshot.snapshotId,
            retrievalQueryId = retrievalQueryId(request),
            resolutionStatus = resolutionStatus,
            authority = authority,
            authorityResolutionConfidence = authorityConfidence,
            sourceReliability = MipField.unavailable(),
            contradictedMemoryRef = contradictedMemoryRef,
            candidateMemoryRefs = candidateMemoryRefs.distinctBy { it.value },
            ambiguityReasons = ambiguityReasons.filter { it.isNotBlank() }.distinct(),
            reasonCodes = reasonCodes.filter { it.isNotBlank() }.distinct(),
            provenance = resolutionProvenance(request, resolutionId),
        )
    }

    private fun resolutionProvenance(request: AuthorityResolveRequest, resolutionId: String): ProvenanceRef {
        val derived = buildList {
            add(request.provenance.originId)
            add(request.contextSnapshot.snapshotId)
            request.retrievalResult.value?.queryId?.let(::add)
        }.distinct()
        return ProvenanceRef(
            originId = resolutionId,
            originType = "AUTHORITY_RESOLUTION",
            originAgent = request.provenance.originAgent,
            generatedBy = ModuleId.BELIEF_AUTHORITY,
            derivedFromIds = derived,
            quotedFromId = request.provenance.quotedFromId,
            revisionOfId = MipField.notApplicable(),
            observationId = request.provenance.observationId,
            eventId = request.provenance.eventId,
            claimId = MipField.present(request.claim.claimId),
            createdAt = request.contextSnapshot.createdAt,
        )
    }

    private fun retrievalQueryId(request: AuthorityResolveRequest): MipField<String> = when (request.retrievalResult.status) {
        MipFieldStatus.PRESENT -> MipField.present(request.retrievalResult.value!!.queryId)
        MipFieldStatus.NOT_APPLICABLE -> MipField.notApplicable()
        MipFieldStatus.UNKNOWN -> MipField.unknown()
        MipFieldStatus.UNRESOLVED -> MipField.unresolved()
        MipFieldStatus.UNAVAILABLE -> MipField.unavailable()
        MipFieldStatus.ERROR -> MipField.error()
        else -> MipField.error()
    }

    private fun retrievalCandidateRefs(request: AuthorityResolveRequest): List<MemoryRef> {
        val result = request.retrievalResult.value ?: return emptyList()
        return (if (result.selectedRefs.isNotEmpty()) result.selectedRefs else result.candidateRefs).map(::MemoryRef)
    }

    private fun resolvedClassification(
        authority: EpistemicClass,
        reason: String,
        previousReasons: List<String> = emptyList(),
    ): ClassificationOutcome = ClassificationOutcome(
        authority = MipField.present(authority),
        authorityConfidence = MipField.present(AuthorityResolutionConfidence(1.0)),
        reasonCodes = (previousReasons + reason).distinct(),
    )

    private fun isTrustedWorldProvenance(request: AuthorityResolveRequest): Boolean =
        request.provenance.generatedBy == ModuleId.WORLD &&
            request.provenance.originType.uppercase() in TRUSTED_WORLD_ORIGIN_TYPES

    private fun isTrustedObservationProvenance(request: AuthorityResolveRequest): Boolean =
        request.provenance.generatedBy == ModuleId.PERCEPTION &&
            request.provenance.originType.uppercase() in TRUSTED_OBSERVATION_ORIGIN_TYPES

    private fun isTrustedInferenceProvenance(request: AuthorityResolveRequest): Boolean =
        request.provenance.originType.uppercase() == "INFERENCE" &&
            request.provenance.derivedFromIds.isNotEmpty()

    private fun normalizePredicate(value: String): String = PREDICATE_ALIASES[value] ?: value

    private fun normalizeTemporalRelation(value: String): String = when (value.uppercase()) {
        "PRESENT" -> "CURRENT"
        else -> value.uppercase()
    }

    private fun compareEntityRole(left: MipEntityRef, right: MipEntityRef): RoleComparison {
        if (left.resolutionStatus == MipEntityResolutionStatus.NOT_APPLICABLE &&
            right.resolutionStatus == MipEntityResolutionStatus.NOT_APPLICABLE
        ) return RoleComparison.MATCH
        if (left.resolutionStatus == MipEntityResolutionStatus.NOT_APPLICABLE ||
            right.resolutionStatus == MipEntityResolutionStatus.NOT_APPLICABLE
        ) return RoleComparison.MISMATCH
        if (left.resolutionStatus != MipEntityResolutionStatus.RESOLVED ||
            right.resolutionStatus != MipEntityResolutionStatus.RESOLVED
        ) return RoleComparison.UNRESOLVED
        return if (left.entityId == right.entityId) RoleComparison.MATCH else RoleComparison.MISMATCH
    }

    private fun compareScalarField(left: MipField<String>, right: MipField<String>): ScalarComparison {
        if (left.status == MipFieldStatus.NOT_APPLICABLE && right.status == MipFieldStatus.NOT_APPLICABLE) {
            return ScalarComparison.MATCH
        }
        if (left.status == MipFieldStatus.NOT_APPLICABLE || right.status == MipFieldStatus.NOT_APPLICABLE) {
            return ScalarComparison.MISMATCH
        }
        if (left.status != MipFieldStatus.PRESENT || right.status != MipFieldStatus.PRESENT) {
            return ScalarComparison.UNRESOLVED
        }
        return if (left.value == right.value) ScalarComparison.MATCH else ScalarComparison.MISMATCH
    }

    private fun unresolvedAssessment(reason: String, detail: String): ContradictionAssessment =
        ContradictionAssessment(
            resolutionStatus = AuthorityResolutionStatus.PARTIAL,
            contradictedMemoryRef = MipField.unresolved(),
            reasonCodes = listOf(reason),
            ambiguityReasons = listOf(detail),
        )

    private data class ClassificationOutcome(
        val authority: MipField<EpistemicClass>,
        val confidence: MipField<AuthorityResolutionConfidence>,
        val reasonCodes: List<String>,
        val fatalStatus: AuthorityResolutionStatus? = null,
        val ambiguityReasons: List<String> = emptyList(),
    )

    private data class SemanticHold(
        val reasonCode: String,
        val detail: String,
        val contradictionState: MipField<MemoryRef>,
    )

    private data class ContradictionAssessment(
        val resolutionStatus: AuthorityResolutionStatus,
        val contradictedMemoryRef: MipField<MemoryRef>,
        val candidateMemoryRefs: List<MemoryRef> = emptyList(),
        val ambiguityReasons: List<String> = emptyList(),
        val reasonCodes: List<String>,
    ) {
        fun deduplicated(): ContradictionAssessment = copy(
            candidateMemoryRefs = candidateMemoryRefs.distinctBy { it.value },
            ambiguityReasons = ambiguityReasons.filter { it.isNotBlank() }.distinct(),
            reasonCodes = reasonCodes.filter { it.isNotBlank() }.distinct(),
        )
    }

    private sealed interface CandidateComparison {
        data class Contradiction(val reasonCode: String) : CandidateComparison
        data class NoContradiction(val reasonCode: String) : CandidateComparison
        data class Unresolved(val reasonCode: String, val detail: String) : CandidateComparison
    }

    private enum class RoleComparison { MATCH, MISMATCH, UNRESOLVED }
    private enum class ScalarComparison { MATCH, MISMATCH, UNRESOLVED }
    private enum class TemporalComparison { MATCH, MISMATCH, UNRESOLVED }

    private companion object {
        const val MARKER_CLAIM_KIND = "CLAIM_KIND"
        const val MARKER_TEMPORAL_REFERENCE_KEY = "TEMPORAL_REFERENCE_KEY"

        val NON_ASSERTIVE_DIALOGUE_ACTS = setOf("QUESTION", "REQUEST", "COMMAND")
        val RESOLVED_POLARITIES = setOf("POSITIVE", "NEGATIVE")
        val UNSAFE_ENTITY_STATES = setOf(
            MipEntityResolutionStatus.UNKNOWN,
            MipEntityResolutionStatus.UNRESOLVED,
            MipEntityResolutionStatus.AMBIGUOUS,
            MipEntityResolutionStatus.CONFLICTED,
        )
        val UNCERTAIN_FIELD_STATES = setOf(
            MipFieldStatus.UNKNOWN,
            MipFieldStatus.UNRESOLVED,
            MipFieldStatus.AMBIGUOUS,
            MipFieldStatus.CONFLICTED,
            MipFieldStatus.UNAVAILABLE,
        )
        val TRUSTED_WORLD_ORIGIN_TYPES = setOf("WORLD_STATE", "GAME_STATE", "WORLD_TRUTH")
        val TRUSTED_OBSERVATION_ORIGIN_TYPES = setOf("OBSERVATION", "PERCEPTION", "SENSOR_OBSERVATION")
        val DIRECTLY_COMPARABLE_TEMPORAL_RELATIONS = setOf("CURRENT", "ATEMPORAL")
        val UNRESOLVED_TEMPORAL_RELATIONS = setOf("UNKNOWN", "UNRESOLVED")

        val PREDICATE_ALIASES = mapOf(
            "identity.name" to "matrix.identity.name",
            "identity.age" to "matrix.identity.age",
            "residence.place" to "matrix.location.live_at",
            "presence.reported" to "matrix.presence.reported",
            "preference.like" to "matrix.preference.like",
            "work.role" to "matrix.work.role",
            "possession.has" to "matrix.possession.has",
            "goal.object" to "matrix.goal.want",
            "attribute.is" to "matrix.attribute.is",
            "consent.grant" to "matrix.consent.grant",
            "consent.refuse" to "matrix.consent.refuse",
            "speech.unresolved" to "matrix.speech.unresolved",
        )

        val DEFAULT_SINGLE_VALUE_PREDICATES = setOf(
            "matrix.location.live_at",
            "matrix.identity.age",
        )

        const val REASON_CLASSIFICATION_UNSUPPORTED = "AUTHORITY.CLASSIFICATION.UNSUPPORTED"
        const val REASON_CLASSIFICATION_UNRESOLVED = "AUTHORITY.CLASSIFICATION.UNRESOLVED"
        const val REASON_WORLD_TRUTH_PROVENANCE_REJECTED = "AUTHORITY.WORLD_TRUTH.PROVENANCE_REJECTED"
        const val REASON_OBSERVATION_PROVENANCE_REJECTED = "AUTHORITY.OBSERVATION.PROVENANCE_REJECTED"
        const val REASON_INFERENCE_DERIVATION_MISSING = "AUTHORITY.INFERENCE.DERIVATION_MISSING"
        const val REASON_CLAIM_KIND_UNRESOLVED = "AUTHORITY.CLAIM_KIND.UNRESOLVED"
        const val REASON_HOLD_NON_ASSERTIVE = "AUTHORITY.HOLD.NON_ASSERTIVE"
        const val REASON_PERSPECTIVE_UNRESOLVED = "AUTHORITY.PERSPECTIVE_UNRESOLVED"
        const val REASON_RETRIEVAL_UNRESOLVED = "AUTHORITY.RETRIEVAL.UNRESOLVED"
        const val REASON_RETRIEVAL_ERROR = "AUTHORITY.RETRIEVAL.ERROR"
        const val REASON_RETRIEVAL_ILLEGAL_STATE = "AUTHORITY.RETRIEVAL.ILLEGAL_STATE"
        const val REASON_CANDIDATE_ID_MISMATCH = "AUTHORITY.CONTRADICTION.CANDIDATE_ID_MISMATCH"
        const val REASON_CANDIDATE_EVIDENCE_UNAVAILABLE = "AUTHORITY.CONTRADICTION.EVIDENCE_UNAVAILABLE"
        const val REASON_CANDIDATE_EVIDENCE_ERROR = "AUTHORITY.CONTRADICTION.EVIDENCE_ERROR"
        const val REASON_CANDIDATE_EVIDENCE_UNRESOLVED = "AUTHORITY.CONTRADICTION.EVIDENCE_UNRESOLVED"
        const val REASON_CONTRADICTION_INCOMPLETE = "AUTHORITY.CONTRADICTION.INCOMPLETE"
        const val REASON_CANDIDATE_VALIDITY_UNRESOLVED = "AUTHORITY.CONTRADICTION.CANDIDATE_VALIDITY_UNRESOLVED"
        const val REASON_CANDIDATE_NOT_VALID = "AUTHORITY.CONTRADICTION.CANDIDATE_NOT_VALID"
        const val REASON_DIFFERENT_SEMANTIC_SLOT = "AUTHORITY.CONTRADICTION.DIFFERENT_SEMANTIC_SLOT"
        const val REASON_DIFFERENT_SOURCE_SCOPE = "AUTHORITY.CONTRADICTION.DIFFERENT_SOURCE_SCOPE"
        const val REASON_DIFFERENT_PERSPECTIVE_SCOPE = "AUTHORITY.CONTRADICTION.DIFFERENT_PERSPECTIVE_SCOPE"
        const val REASON_TARGET_UNRESOLVED = "AUTHORITY.TARGET_UNRESOLVED"
        const val REASON_POLARITY_UNRESOLVED = "AUTHORITY.POLARITY_UNRESOLVED"
        const val REASON_OBJECT_UNRESOLVED = "AUTHORITY.OBJECT_UNRESOLVED"
        const val REASON_OPPOSITE_POLARITY = "AUTHORITY.CONTRADICTION.OPPOSITE_POLARITY"
        const val REASON_SINGLE_VALUE_CONFLICT = "AUTHORITY.CONTRADICTION.SINGLE_VALUE_CONFLICT"
    }
}
