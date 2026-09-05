package matrix.assembling.understanding.v3

import java.time.Instant
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixBoundaryViolationException
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.mip.MATRIX_NLU_CONTRACT_V3
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipUnderstandingV3Alternative
import matrix.assembling.mip.MipUnderstandingV3CandidateKind
import matrix.assembling.mip.MipUnderstandingV3Claim
import matrix.assembling.mip.MipUnderstandingV3EntityType
import matrix.assembling.mip.MipUnderstandingV3Field
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3Mention
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.MipUnderstandingV3ReferentCandidate
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus
import matrix.assembling.mip.MipUnderstandingV3TemporalEvidence
import matrix.assembling.mip.MipUnderstandingV3TemporalRelationValue
import matrix.assembling.mip.MipSpan
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef

/**
 * Exact runtime boundary for Matrix-NLU V3.
 *
 * These source DTOs mirror the frozen linguistic output only. They intentionally contain no
 * worldTruth, Authority, Memory, relationship, affective, persistent consent/goal or behavior
 * ownership fields.
 */
data class MatrixNluV3Request(
    val turnId: String,
    val sessionId: String,
    val language: String,
    val text: String,
    val speakerId: String,
    val observerId: String,
)

data class MatrixNluV3Alternative<T : Any>(
    val value: T,
    val confidence: Double,
)

data class MatrixNluV3Field<T : Any>(
    val value: T,
    val confidence: Double,
    val fieldStatus: String,
    val alternatives: List<MatrixNluV3Alternative<T>> = emptyList(),
)

data class MatrixNluV3Mention(
    val mentionId: String,
    val span: List<Int>,
    val entityType: String,
    val surfaceForm: String? = null,
    val resolvedEntityRef: String? = null,
    val resolutionStatus: String,
)

data class MatrixNluV3ReferentCandidate(
    val candidateId: String,
    val kind: String,
    val mentionId: String? = null,
    val span: List<Int>? = null,
    val entityType: String? = null,
    val resolvedEntityRef: String? = null,
    val resolutionStatus: String,
)

data class MatrixNluV3TemporalEvidence(
    val temporalId: String,
    val span: List<Int>,
    val metadata: Map<String, String> = emptyMap(),
)

data class MatrixNluV3TemporalRelationValue(
    val relation: String,
    val anchorRef: String? = null,
)

data class MatrixNluV3Claim(
    val claimId: String,
    val sourceSpan: List<Int>,
    val subjectSpans: List<List<Int>>,
    val objectSpans: List<List<Int>>,
    val negationCueSpans: List<List<Int>>,
    val temporalEvidence: List<MatrixNluV3TemporalEvidence>,
    val entityMentionIds: List<String>,
    val dialogueAct: MatrixNluV3Field<String>,
    val predicate: MatrixNluV3Field<String>,
    val subjectReferent: MatrixNluV3Field<String>,
    val targetReferent: MatrixNluV3Field<String>,
    val ownerReferent: MatrixNluV3Field<String>,
    val perspectiveReferent: MatrixNluV3Field<String>,
    val sourceReferent: MatrixNluV3Field<String>,
    val polarity: MatrixNluV3Field<String>,
    val temporalRelation: MatrixNluV3Field<MatrixNluV3TemporalRelationValue>,
    val claimKind: MatrixNluV3Field<String>,
    val fieldStatusByField: Map<String, String>,
    val confidenceByField: Map<String, Double>,
    val overallInterpretationConfidence: Double,
    val structuralStatus: String,
    val interpretationStatus: String,
    val diagnostics: List<String> = emptyList(),
)

data class MatrixNluV3Output(
    val contractVersion: String,
    val contractFingerprintSha256: String,
    val input: String,
    val observationSourceId: String,
    val speakerRef: String,
    val observerRef: String,
    val mentions: List<MatrixNluV3Mention>,
    val referentCandidates: List<MatrixNluV3ReferentCandidate>,
    val claims: List<MatrixNluV3Claim>,
)

/** Implemented by the real Python/ONNX/Android V3 runtime. */
fun interface MatrixNluV3RuntimeBridge {
    fun interpret(request: MatrixNluV3Request): MatrixNluV3Output
}

data class CanonicalUnderstandingV3Config(
    val expectedContractFingerprintSha256: String,
) {
    init {
        require(SHA256.matches(expectedContractFingerprintSha256)) {
            "expectedContractFingerprintSha256 must be lowercase 64-char SHA-256 hex"
        }
    }
}

/**
 * Validated, business-logic-free adapter from frozen Matrix-NLU V3 output to the canonical
 * MIP Understanding V3 observation/TypedClaims.
 *
 * It never reparses free text and never creates truth/Authority/Memory decisions.
 */
class CanonicalUnderstandingV3Adapter(
    private val runtime: MatrixNluV3RuntimeBridge,
    private val config: CanonicalUnderstandingV3Config,
) {

    fun understand(turn: MatrixTurnFrame): MatrixTurnFrame {
        val request = MatrixNluV3Request(
            turnId = turn.turnId,
            sessionId = turn.sessionId,
            language = turn.input.locale.uppercase(),
            text = turn.input.text,
            speakerId = turn.input.speakerId,
            observerId = turn.input.observerId,
        )

        val output = try {
            runtime.interpret(request)
        } catch (error: RuntimeException) {
            throw boundaryFailure(
                turn,
                code = "UNDERSTANDING.V3.RUNTIME_ERROR",
                message = "Matrix-NLU V3 runtime failed: ${error::class.simpleName}",
            )
        }

        validateEnvelope(turn, output)

        val timestamp = Instant.ofEpochMilli(turn.input.timestampMillis)
        val observationProvenance = ProvenanceRef(
            originId = "nlu:${output.observationSourceId}",
            originType = MATRIX_NLU_CONTRACT_V3,
            generatedBy = ModuleId.NLU,
            observationId = MipField.present(output.observationSourceId),
            createdAt = timestamp,
        )

        val observation = try {
            MipUnderstandingV3Observation(
                nluContractVersion = output.contractVersion,
                nluContractFingerprintSha256 = output.contractFingerprintSha256,
                input = output.input,
                observationSourceId = output.observationSourceId,
                speaker = resolvedEntity(output.speakerRef),
                observer = resolvedEntity(output.observerRef),
                provenance = observationProvenance,
                mentions = output.mentions.map { it.toCanonicalMention() },
                referentCandidates = output.referentCandidates.map { it.toCanonicalCandidate() },
                claims = output.claims.map { claim ->
                    claim.toCanonicalClaim(output.observationSourceId, observationProvenance.originId, timestamp)
                },
            )
        } catch (error: IllegalArgumentException) {
            throw boundaryFailure(
                turn,
                code = "UNDERSTANDING.V3.CONTRACT_INVALID",
                message = error.message ?: "Matrix-NLU V3 output violated canonical Understanding contract",
            )
        } catch (error: IllegalStateException) {
            throw boundaryFailure(
                turn,
                code = "UNDERSTANDING.V3.CONTRACT_INVALID",
                message = error.message ?: "Matrix-NLU V3 output violated canonical Understanding contract",
            )
        }

        val invalid = observation.claims.count { it.structuralStatus == MipUnderstandingV3StructuralStatus.INVALID }
        val abstained = observation.claims.count { it.interpretationStatus == MipUnderstandingV3InterpretationStatus.ABSTAINED }
        val ambiguous = observation.claims.count { it.interpretationStatus == MipUnderstandingV3InterpretationStatus.AMBIGUOUS }
        val status = when {
            observation.claims.isEmpty() || invalid > 0 || abstained > 0 -> "HOLD"
            ambiguous > 0 -> "PARTIAL"
            else -> "PASS"
        }
        val reasonCodes = buildList {
            add("UNDERSTANDING_V3_OUTPUT_ACCEPTED")
            if (observation.claims.isEmpty()) add("UNDERSTANDING_V3_NO_CLAIMS")
            if (invalid > 0) add("UNDERSTANDING_V3_INVALID_CLAIM_PRESENT")
            if (abstained > 0) add("UNDERSTANDING_V3_ABSTAINED_CLAIM_PRESENT")
            if (ambiguous > 0) add("UNDERSTANDING_V3_AMBIGUOUS_CLAIM_PRESENT")
        }

        val trace = turn.diagnostics
            .understood(
                DiagnosticSnapshot(
                    module = "UNDERSTANDING_V3",
                    input = "contract=${output.contractVersion}; observation=${output.observationSourceId}",
                    output = "claims=${observation.claims.size}; mentions=${observation.mentions.size}; candidates=${observation.referentCandidates.size}",
                    decision = "CANONICAL_TYPED_CLAIMS_BUILT",
                    status = status,
                    reasonCodes = reasonCodes,
                    metadata = mapOf(
                        "profile" to observation.profileVersion,
                        "contractFingerprint" to observation.nluContractFingerprintSha256,
                        "claimCount" to observation.claims.size.toString(),
                        "invalidClaimCount" to invalid.toString(),
                        "abstainedClaimCount" to abstained.toString(),
                        "ambiguousClaimCount" to ambiguous.toString(),
                    ),
                )
            )
            .reason("UNDERSTANDING_V3_OUTPUT_ACCEPTED")
            .add("understanding_v3.canonical_claims.built")
            .tag("understanding_v3.profile", observation.profileVersion)
            .tag("understanding_v3.claim_count", observation.claims.size.toString())

        return turn.copy(
            canonicalUnderstandingV3 = MipField.present(observation),
            diagnostics = trace,
        )
    }

    private fun validateEnvelope(turn: MatrixTurnFrame, output: MatrixNluV3Output) {
        if (output.contractVersion != MATRIX_NLU_CONTRACT_V3) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.CONTRACT_VERSION_MISMATCH", "Unexpected NLU contract=${output.contractVersion}")
        }
        if (output.contractFingerprintSha256 != config.expectedContractFingerprintSha256) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.CONTRACT_FINGERPRINT_MISMATCH", "Matrix-NLU V3 contract fingerprint mismatch")
        }
        if (output.input != turn.input.text) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.INPUT_MISMATCH", "Runtime output input does not equal frame input")
        }
        if (output.speakerRef != turn.input.speakerId) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.SPEAKER_MISMATCH", "Runtime speakerRef does not equal frame speakerId")
        }
        if (output.observerRef != turn.input.observerId) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.OBSERVER_MISMATCH", "Runtime observerRef does not equal frame observerId")
        }
        if (output.observationSourceId.isBlank()) {
            throw boundaryFailure(turn, "UNDERSTANDING.V3.OBSERVATION_ID_INVALID", "Runtime observationSourceId is blank")
        }
    }

    private fun MatrixNluV3Mention.toCanonicalMention() = MipUnderstandingV3Mention(
        mentionId = mentionId,
        span = span.toMipSpan("mention.$mentionId.span"),
        entityType = enumValue(entityType, "entityType", MipUnderstandingV3EntityType::valueOf),
        surfaceForm = surfaceForm,
        entityRef = entityRef(resolvedEntityRef, resolutionStatus),
    )

    private fun MatrixNluV3ReferentCandidate.toCanonicalCandidate() = MipUnderstandingV3ReferentCandidate(
        candidateId = candidateId,
        kind = enumValue(kind, "candidate.kind", MipUnderstandingV3CandidateKind::valueOf),
        mentionId = mentionId,
        span = span?.toMipSpan("candidate.$candidateId.span"),
        entityType = entityType?.let { enumValue(it, "candidate.entityType", MipUnderstandingV3EntityType::valueOf) },
        entityRef = entityRef(resolvedEntityRef, resolutionStatus),
    )

    private fun MatrixNluV3Claim.toCanonicalClaim(
        observationSourceId: String,
        upstreamOriginId: String,
        timestamp: Instant,
    ) = MipUnderstandingV3Claim(
        claimId = claimId,
        provenance = ProvenanceRef(
            originId = "understanding:$observationSourceId:$claimId",
            originType = "UNDERSTANDING_V3_TYPED_CLAIM",
            generatedBy = ModuleId.UNDERSTANDING,
            derivedFromIds = listOf(upstreamOriginId),
            observationId = MipField.present(observationSourceId),
            claimId = MipField.present(claimId),
            createdAt = timestamp,
        ),
        sourceSpan = sourceSpan.toMipSpan("claim.$claimId.sourceSpan"),
        subjectSpans = subjectSpans.mapIndexed { index, span -> span.toMipSpan("claim.$claimId.subjectSpans[$index]") },
        objectSpans = objectSpans.mapIndexed { index, span -> span.toMipSpan("claim.$claimId.objectSpans[$index]") },
        negationCueSpans = negationCueSpans.mapIndexed { index, span -> span.toMipSpan("claim.$claimId.negationCueSpans[$index]") },
        temporalEvidence = temporalEvidence.map { item ->
            MipUnderstandingV3TemporalEvidence(
                temporalId = item.temporalId,
                span = item.span.toMipSpan("claim.$claimId.temporalEvidence.${item.temporalId}"),
                metadata = item.metadata,
            )
        },
        entityMentionIds = entityMentionIds,
        dialogueAct = dialogueAct.toCanonicalField(),
        predicate = predicate.toCanonicalField(),
        subjectReferent = subjectReferent.toCanonicalField(),
        targetReferent = targetReferent.toCanonicalField(),
        ownerReferent = ownerReferent.toCanonicalField(),
        perspectiveReferent = perspectiveReferent.toCanonicalField(),
        sourceReferent = sourceReferent.toCanonicalField(),
        polarity = polarity.toCanonicalField(),
        temporalRelation = temporalRelation.toCanonicalTemporalField(),
        claimKind = claimKind.toCanonicalField(),
        fieldStatusByField = fieldStatusByField.mapValues { (name, status) ->
            enumValue(status, "claim.$claimId.fieldStatusByField.$name", MipUnderstandingV3FieldStatus::valueOf)
        },
        confidenceByField = confidenceByField,
        overallInterpretationConfidence = overallInterpretationConfidence,
        structuralStatus = enumValue(structuralStatus, "claim.$claimId.structuralStatus", MipUnderstandingV3StructuralStatus::valueOf),
        interpretationStatus = enumValue(interpretationStatus, "claim.$claimId.interpretationStatus", MipUnderstandingV3InterpretationStatus::valueOf),
        diagnostics = diagnostics,
    )

    private fun <T : Any> MatrixNluV3Field<T>.toCanonicalField() = MipUnderstandingV3Field(
        value = value,
        confidence = confidence,
        fieldStatus = enumValue(fieldStatus, "fieldStatus", MipUnderstandingV3FieldStatus::valueOf),
        alternatives = alternatives.map { MipUnderstandingV3Alternative(it.value, it.confidence) },
    )

    private fun MatrixNluV3Field<MatrixNluV3TemporalRelationValue>.toCanonicalTemporalField() =
        MipUnderstandingV3Field(
            value = MipUnderstandingV3TemporalRelationValue(value.relation, value.anchorRef),
            confidence = confidence,
            fieldStatus = enumValue(fieldStatus, "temporalRelation.fieldStatus", MipUnderstandingV3FieldStatus::valueOf),
            alternatives = alternatives.map { alternative ->
                MipUnderstandingV3Alternative(
                    MipUnderstandingV3TemporalRelationValue(alternative.value.relation, alternative.value.anchorRef),
                    alternative.confidence,
                )
            },
        )

    private fun List<Int>.toMipSpan(name: String): MipSpan {
        require(size == 2) { "$name must contain exactly [start,end]" }
        return MipSpan(this[0], this[1])
    }

    private fun resolvedEntity(entityId: String) = MipEntityRef(
        entityId = entityId,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    private fun entityRef(entityId: String?, rawStatus: String): MipEntityRef {
        val status = enumValue(rawStatus, "entity resolutionStatus", MipEntityResolutionStatus::valueOf)
        return if (status == MipEntityResolutionStatus.RESOLVED) {
            require(!entityId.isNullOrBlank()) { "RESOLVED entity requires resolvedEntityRef" }
            MipEntityRef(entityId = entityId, resolutionStatus = status)
        } else {
            require(entityId == null) { "Only RESOLVED entity may carry resolvedEntityRef" }
            MipEntityRef(resolutionStatus = status)
        }
    }

    private fun <T> enumValue(raw: String, name: String, decode: (String) -> T): T = try {
        decode(raw)
    } catch (error: IllegalArgumentException) {
        throw IllegalArgumentException("Unsupported $name=$raw", error)
    }

    private fun boundaryFailure(turn: MatrixTurnFrame, code: String, message: String): MatrixBoundaryViolationException {
        val trace = turn.diagnostics.diverge(code).add("understanding_v3.boundary_failure")
        return MatrixBoundaryViolationException(message, trace)
    }

    companion object {
        private val SHA256 = Regex("^[0-9a-f]{64}$")
    }
}
