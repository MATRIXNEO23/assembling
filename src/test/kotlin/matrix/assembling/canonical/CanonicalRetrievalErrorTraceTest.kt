package matrix.assembling.canonical

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.UserMessage
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipSpan
import matrix.assembling.mip.MipUnderstandingV3CandidateKind
import matrix.assembling.mip.MipUnderstandingV3Field
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.MipUnderstandingV3InterpretationStatus
import matrix.assembling.mip.MipUnderstandingV3Observation
import matrix.assembling.mip.MipUnderstandingV3ReferentCandidate
import matrix.assembling.mip.MipUnderstandingV3StructuralStatus
import matrix.assembling.mip.MipUnderstandingV3TemporalRelationValue
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.ProvenanceRef
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.RetrievalStatus

class CanonicalRetrievalErrorTraceTest {

    @Test
    fun `retrieval error records first divergence on exact claim`() {
        val withContext = CanonicalContextAssembler(DomainAvailability.AVAILABLE)
            .assemble(frame())
        val result = CanonicalClaimRetrievalStage(
            CanonicalRetrievalProvider { query, context ->
                RetrievalResult(
                    queryId = query.queryId,
                    status = RetrievalStatus.ERROR,
                    claimId = MipField.present(query.requireClaimBinding()),
                    contextSnapshotId = MipField.present(context.snapshotId),
                    reasonCodes = listOf("RETRIEVAL.TEST_ERROR"),
                )
            }
        ).retrieve(withContext)

        assertEquals(RetrievalStatus.ERROR, result.requireCanonicalRetrievalResults().single().status)
        assertEquals("RETRIEVAL.RESULT_ERROR.c0", result.diagnostics.firstDivergence)
    }

    private fun frame(): MatrixTurnFrame {
        val now = Instant.parse("2026-09-06T08:00:00Z")
        val speaker = resolved("user")
        val observer = resolved("luna")
        val observation = MipUnderstandingV3Observation(
            nluContractVersion = "MATRIX_NLU_CONTRACT_V3",
            nluContractFingerprintSha256 = "7b0646e44243ad897760c0fcadbe141f1b8e88e3fd8d63a1789106571b9987b0",
            input = INPUT,
            observationSourceId = "obs-error",
            speaker = speaker,
            observer = observer,
            provenance = ProvenanceRef(
                originId = "obs-error",
                originType = "NLU_OBSERVATION",
                generatedBy = ModuleId.NLU,
                observationId = MipField.present("obs-error"),
                createdAt = now,
            ),
            mentions = emptyList(),
            referentCandidates = listOf(
                MipUnderstandingV3ReferentCandidate(
                    candidateId = "ctx:speaker",
                    kind = MipUnderstandingV3CandidateKind.CONTEXT_SPEAKER,
                    entityRef = speaker,
                ),
                MipUnderstandingV3ReferentCandidate(
                    candidateId = "ctx:observer",
                    kind = MipUnderstandingV3CandidateKind.CONTEXT_OBSERVER,
                    entityRef = observer,
                ),
            ),
            claims = listOf(
                matrix.assembling.mip.MipUnderstandingV3Claim(
                    claimId = "c0",
                    provenance = ProvenanceRef(
                        originId = "claim:c0",
                        originType = "UNDERSTANDING_CLAIM",
                        generatedBy = ModuleId.UNDERSTANDING,
                        derivedFromIds = listOf("obs-error"),
                        observationId = MipField.present("obs-error"),
                        claimId = MipField.present("c0"),
                        createdAt = now,
                    ),
                    sourceSpan = MipSpan(0, INPUT.length),
                    subjectSpans = emptyList(),
                    objectSpans = emptyList(),
                    negationCueSpans = emptyList(),
                    temporalEvidence = emptyList(),
                    entityMentionIds = emptyList(),
                    dialogueAct = resolvedField("ASSERT"),
                    predicate = resolvedField("residence.place"),
                    subjectReferent = resolvedField("ctx:speaker"),
                    targetReferent = notApplicableField(),
                    ownerReferent = resolvedField("ctx:speaker"),
                    perspectiveReferent = resolvedField("ctx:speaker"),
                    sourceReferent = resolvedField("ctx:speaker"),
                    polarity = resolvedField("POSITIVE"),
                    temporalRelation = MipUnderstandingV3Field(
                        value = MipUnderstandingV3TemporalRelationValue("CURRENT", "speech-time"),
                        confidence = 0.99,
                        fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
                    ),
                    claimKind = resolvedField("DIRECT"),
                    fieldStatusByField = emptyMap(),
                    confidenceByField = emptyMap(),
                    overallInterpretationConfidence = 0.99,
                    structuralStatus = MipUnderstandingV3StructuralStatus.VALID,
                    interpretationStatus = MipUnderstandingV3InterpretationStatus.RESOLVED,
                )
            ),
        )
        return MatrixTurnFrame(
            turnId = "turn-error",
            sessionId = "session-error",
            input = UserMessage(INPUT, "user", "luna", now.toEpochMilli(), "it"),
            canonicalUnderstandingV3 = MipField.present(observation),
        )
    }

    private fun resolvedField(value: String) = MipUnderstandingV3Field(
        value = value,
        confidence = 0.99,
        fieldStatus = MipUnderstandingV3FieldStatus.RESOLVED,
    )

    private fun notApplicableField() = MipUnderstandingV3Field(
        value = "NONE",
        confidence = 0.99,
        fieldStatus = MipUnderstandingV3FieldStatus.NOT_APPLICABLE,
    )

    private fun resolved(id: String) = MipEntityRef(
        entityId = id,
        surfaceForm = id,
        resolutionStatus = MipEntityResolutionStatus.RESOLVED,
    )

    companion object {
        private const val INPUT = "Vivo qui."
    }
}
