package matrix.assembling.mip

const val MIP_UNDERSTANDING_V3_PROFILE_VERSION: String = "MIP-1.0/UNDERSTANDING-V3-1.0"
const val MATRIX_NLU_CONTRACT_V3: String = "MATRIX_NLU_CONTRACT_V3"

/** Exact frozen Matrix-NLU V3 field-status vocabulary. */
enum class MipUnderstandingV3FieldStatus {
    RESOLVED,
    UNKNOWN,
    AMBIGUOUS,
    NOT_APPLICABLE,
}

enum class MipUnderstandingV3StructuralStatus {
    VALID,
    INVALID,
}

enum class MipUnderstandingV3InterpretationStatus {
    RESOLVED,
    AMBIGUOUS,
    ABSTAINED,
}

enum class MipUnderstandingV3CandidateKind {
    CONTEXT_SPEAKER,
    CONTEXT_OBSERVER,
    MENTION,
    CONTEXT_ENTITY,
}

enum class MipUnderstandingV3EntityType {
    PERSON,
    LOCATION,
}

data class MipUnderstandingV3Alternative<T : Any>(
    val value: T,
    val confidence: Double,
) {
    init {
        requireUnitConfidence(confidence, "alternative confidence")
    }
}

/**
 * Lossless carrier for one frozen V3 learned/decoded field.
 *
 * Unlike ordinary MipField, the primary value remains present even when V3 marks the field
 * UNKNOWN or AMBIGUOUS. This is required because V3 uses e.g. value=UNKNOWN +
 * fieldStatus=AMBIGUOUS + ranked alternatives.
 */
data class MipUnderstandingV3Field<T : Any>(
    val value: T,
    val confidence: Double,
    val fieldStatus: MipUnderstandingV3FieldStatus,
    val alternatives: List<MipUnderstandingV3Alternative<T>> = emptyList(),
) {
    init {
        requireUnitConfidence(confidence, "field confidence")
        require(alternatives.map { it.value }.distinct().size == alternatives.size) {
            "V3 field alternatives must contain unique values"
        }
        require(alternatives.zipWithNext().all { (left, right) -> left.confidence >= right.confidence }) {
            "V3 field alternatives must preserve descending confidence rank"
        }
        if (fieldStatus == MipUnderstandingV3FieldStatus.AMBIGUOUS) {
            require(alternatives.size >= 2) { "AMBIGUOUS V3 field requires at least two ranked alternatives" }
        } else {
            require(alternatives.isEmpty()) { "Only AMBIGUOUS V3 fields may carry alternatives" }
        }
    }
}

data class MipUnderstandingV3Mention(
    val mentionId: String,
    val span: MipSpan,
    val entityType: MipUnderstandingV3EntityType,
    val surfaceForm: String? = null,
    val entityRef: MipEntityRef,
) {
    init {
        requireOpaqueId(mentionId, "mentionId")
        requireNonEmptySpan(span, "mention span")
        surfaceForm?.let { require(it.isNotBlank()) { "mention surfaceForm must not be blank" } }
    }
}

data class MipUnderstandingV3ReferentCandidate(
    val candidateId: String,
    val kind: MipUnderstandingV3CandidateKind,
    val mentionId: String? = null,
    val span: MipSpan? = null,
    val entityType: MipUnderstandingV3EntityType? = null,
    val entityRef: MipEntityRef,
) {
    init {
        requireOpaqueId(candidateId, "candidateId")
        mentionId?.let { requireOpaqueId(it, "candidate mentionId") }
        span?.let { requireNonEmptySpan(it, "candidate span") }

        when (kind) {
            MipUnderstandingV3CandidateKind.CONTEXT_SPEAKER -> {
                require(candidateId == "ctx:speaker") { "CONTEXT_SPEAKER must use candidateId=ctx:speaker" }
                require(mentionId == null && span == null && entityType == null) {
                    "CONTEXT_SPEAKER must not fabricate mention evidence"
                }
            }
            MipUnderstandingV3CandidateKind.CONTEXT_OBSERVER -> {
                require(candidateId == "ctx:observer") { "CONTEXT_OBSERVER must use candidateId=ctx:observer" }
                require(mentionId == null && span == null && entityType == null) {
                    "CONTEXT_OBSERVER must not fabricate mention evidence"
                }
            }
            MipUnderstandingV3CandidateKind.MENTION -> {
                require(mentionId != null && span != null && entityType != null) {
                    "MENTION candidate requires mentionId/span/entityType"
                }
            }
            MipUnderstandingV3CandidateKind.CONTEXT_ENTITY -> {
                require(mentionId == null && span == null && entityType == null) {
                    "CONTEXT_ENTITY must not fabricate mention evidence"
                }
            }
        }
    }
}

data class MipUnderstandingV3TemporalEvidence(
    val temporalId: String,
    val span: MipSpan,
    /** Optional normalized linguistic metadata; no wall-clock value is fabricated here. */
    val metadata: Map<String, String> = emptyMap(),
) {
    init {
        requireOpaqueId(temporalId, "temporalId")
        requireNonEmptySpan(span, "temporal evidence span")
        metadata.forEach { (key, _) -> require(key.isNotBlank()) { "temporal metadata key must not be blank" } }
    }
}

data class MipUnderstandingV3TemporalRelationValue(
    val relation: String,
    /** Exact upstream anchor identity: speech-time/context-reference/temporal:<id>/claim:<id>. */
    val anchorRef: String? = null,
) {
    init {
        require(relation in V3_TEMPORAL_RELATIONS) { "Unsupported V3 temporal relation: $relation" }
        anchorRef?.let { require(it.isNotBlank()) { "temporal anchorRef must not be blank" } }

        if (relation in V3_ANCHOR_REQUIRED_RELATIONS) {
            require(anchorRef != null) { "$relation requires temporal anchorRef" }
            require(isRecognizedAnchorShape(anchorRef)) { "Invalid temporal anchorRef shape: $anchorRef" }
        } else if (anchorRef != null) {
            require(anchorRef == "speech-time" || anchorRef == "context-reference") {
                "$relation may only use speech-time/context-reference when an optional anchor is supplied"
            }
        }
    }
}

data class MipUnderstandingV3Claim(
    /** Exact upstream Matrix-NLU V3 claim identity; it must not be rewritten to turn:index. */
    val claimId: String,
    val provenance: ProvenanceRef,
    val sourceSpan: MipSpan,
    val subjectSpans: List<MipSpan>,
    val objectSpans: List<MipSpan>,
    val negationCueSpans: List<MipSpan>,
    val temporalEvidence: List<MipUnderstandingV3TemporalEvidence>,
    val entityMentionIds: List<String>,
    val dialogueAct: MipUnderstandingV3Field<String>,
    val predicate: MipUnderstandingV3Field<String>,
    val subjectReferent: MipUnderstandingV3Field<String>,
    val targetReferent: MipUnderstandingV3Field<String>,
    val ownerReferent: MipUnderstandingV3Field<String>,
    val perspectiveReferent: MipUnderstandingV3Field<String>,
    val sourceReferent: MipUnderstandingV3Field<String>,
    val polarity: MipUnderstandingV3Field<String>,
    val temporalRelation: MipUnderstandingV3Field<MipUnderstandingV3TemporalRelationValue>,
    val claimKind: MipUnderstandingV3Field<String>,
    /** Includes token/span field statuses that do not have a dedicated field wrapper above. */
    val fieldStatusByField: Map<String, MipUnderstandingV3FieldStatus>,
    val confidenceByField: Map<String, Double>,
    val overallInterpretationConfidence: Double,
    val structuralStatus: MipUnderstandingV3StructuralStatus,
    val interpretationStatus: MipUnderstandingV3InterpretationStatus,
    val diagnostics: List<String> = emptyList(),
) {
    init {
        requireOpaqueId(claimId, "claimId")
        requireNonEmptySpan(sourceSpan, "claim sourceSpan")
        require(provenance.generatedBy == ModuleId.UNDERSTANDING) {
            "canonical Understanding V3 claim provenance must be generatedBy=UNDERSTANDING"
        }
        require(provenance.claimId.status == MipFieldStatus.PRESENT && provenance.claimId.value == claimId) {
            "claim provenance.claimId must be PRESENT and equal claimId"
        }

        requireSpanListWithinSource(subjectSpans, sourceSpan, "subjectSpans")
        requireSpanListWithinSource(objectSpans, sourceSpan, "objectSpans")
        requireSpanListWithinSource(negationCueSpans, sourceSpan, "negationCueSpans")
        require(temporalEvidence.map { it.temporalId }.distinct().size == temporalEvidence.size) {
            "temporalEvidence temporalIds must be unique per claim"
        }
        temporalEvidence.forEach { requireSpanWithin(it.span, sourceSpan, "temporalEvidence.${it.temporalId}") }
        require(entityMentionIds.all { it.isNotBlank() }) { "entityMentionIds must not contain blank IDs" }
        require(entityMentionIds.distinct().size == entityMentionIds.size) {
            "entityMentionIds must be unique per claim"
        }

        validateCategoricalField(dialogueAct, V3_DIALOGUE_ACTS, "dialogueAct")
        validateCategoricalField(predicate, V3_PREDICATES, "predicate")
        validateCategoricalField(polarity, V3_POLARITIES, "polarity")
        validateCategoricalField(claimKind, V3_CLAIM_KINDS, "claimKind")

        fieldStatusByField.keys.forEach { require(it.isNotBlank()) { "fieldStatusByField key must not be blank" } }
        confidenceByField.forEach { (name, value) ->
            require(name.isNotBlank()) { "confidenceByField key must not be blank" }
            requireUnitConfidence(value, "confidenceByField.$name")
        }
        requireUnitConfidence(overallInterpretationConfidence, "overallInterpretationConfidence")
        diagnostics.forEach { require(it.isNotBlank()) { "claim diagnostic must not be blank" } }

        if (structuralStatus == MipUnderstandingV3StructuralStatus.INVALID) {
            require(interpretationStatus == MipUnderstandingV3InterpretationStatus.ABSTAINED) {
                "INVALID V3 claim must be ABSTAINED"
            }
        }
    }
}

data class MipUnderstandingV3Observation(
    val profileVersion: String = MIP_UNDERSTANDING_V3_PROFILE_VERSION,
    val nluContractVersion: String,
    val nluContractFingerprintSha256: String,
    val input: String,
    val observationSourceId: String,
    val speaker: MipEntityRef,
    val observer: MipEntityRef,
    /** Provenance of the validated upstream NLU observation. */
    val provenance: ProvenanceRef,
    val mentions: List<MipUnderstandingV3Mention>,
    val referentCandidates: List<MipUnderstandingV3ReferentCandidate>,
    val claims: List<MipUnderstandingV3Claim>,
) {
    init {
        require(profileVersion == MIP_UNDERSTANDING_V3_PROFILE_VERSION) {
            "Unsupported Understanding V3 profileVersion=$profileVersion"
        }
        require(nluContractVersion == MATRIX_NLU_CONTRACT_V3) {
            "Unsupported upstream NLU contract=$nluContractVersion"
        }
        require(V3_SHA256_REGEX.matches(nluContractFingerprintSha256)) {
            "nluContractFingerprintSha256 must be lowercase 64-char SHA-256 hex"
        }
        require(input.isNotEmpty()) { "V3 observation input must not be empty" }
        requireOpaqueId(observationSourceId, "observationSourceId")
        require(provenance.generatedBy == ModuleId.NLU) {
            "observation provenance must be generatedBy=NLU"
        }
        require(provenance.observationId.status == MipFieldStatus.PRESENT &&
            provenance.observationId.value == observationSourceId
        ) {
            "observation provenance.observationId must be PRESENT and equal observationSourceId"
        }

        require(mentions.map { it.mentionId }.distinct().size == mentions.size) {
            "mention IDs must be unique"
        }
        require(referentCandidates.map { it.candidateId }.distinct().size == referentCandidates.size) {
            "referent candidate IDs must be unique"
        }
        require(claims.map { it.claimId }.distinct().size == claims.size) {
            "V3 claim IDs must be unique"
        }

        val mentionById = mentions.associateBy { it.mentionId }
        val candidateById = referentCandidates.associateBy { it.candidateId }
        val claimIds = claims.map { it.claimId }.toSet()

        mentions.forEach { requireSpanInsideObservation(it.span, input.length, "mention.${it.mentionId}") }

        val speakerCandidate = candidateById["ctx:speaker"]
            ?: error("V3 candidate table missing ctx:speaker")
        require(speakerCandidate.kind == MipUnderstandingV3CandidateKind.CONTEXT_SPEAKER) {
            "ctx:speaker candidate has wrong kind"
        }
        require(speakerCandidate.entityRef == speaker) {
            "ctx:speaker candidate entityRef must equal observation speaker"
        }

        val observerCandidate = candidateById["ctx:observer"]
            ?: error("V3 candidate table missing ctx:observer")
        require(observerCandidate.kind == MipUnderstandingV3CandidateKind.CONTEXT_OBSERVER) {
            "ctx:observer candidate has wrong kind"
        }
        require(observerCandidate.entityRef == observer) {
            "ctx:observer candidate entityRef must equal observation observer"
        }

        referentCandidates.forEach { candidate ->
            if (candidate.kind == MipUnderstandingV3CandidateKind.MENTION) {
                val mention = mentionById[candidate.mentionId]
                    ?: error("candidate ${candidate.candidateId} references unknown mentionId=${candidate.mentionId}")
                require(candidate.span == mention.span) {
                    "candidate ${candidate.candidateId} span must equal referenced mention span"
                }
                require(candidate.entityType == mention.entityType) {
                    "candidate ${candidate.candidateId} entityType must equal referenced mention entityType"
                }
            }
        }

        claims.forEach { claim ->
            requireSpanInsideObservation(claim.sourceSpan, input.length, "claim.${claim.claimId}.sourceSpan")
            require(claim.provenance.observationId.status == MipFieldStatus.PRESENT &&
                claim.provenance.observationId.value == observationSourceId
            ) {
                "claim ${claim.claimId} provenance must reference current observationSourceId"
            }
            require(claim.entityMentionIds.all { it in mentionById }) {
                "claim ${claim.claimId} references unknown entityMentionId"
            }

            validateRoleField("subjectReferent", claim.subjectReferent, candidateById.keys)
            validateRoleField("targetReferent", claim.targetReferent, candidateById.keys)
            validateRoleField("ownerReferent", claim.ownerReferent, candidateById.keys)
            validateRoleField("perspectiveReferent", claim.perspectiveReferent, candidateById.keys)
            validateRoleField("sourceReferent", claim.sourceReferent, candidateById.keys)
            validateTemporalAnchorIdentity(claim, claimIds)
        }
    }
}

private val V3_DIALOGUE_ACTS = setOf("ASSERT", "QUESTION", "REQUEST", "COMMAND", "CORRECT", "UNKNOWN")
private val V3_CLAIM_KINDS = setOf("DIRECT", "REPORT", "BELIEF", "HYPOTHESIS", "UNKNOWN")
private val V3_POLARITIES = setOf("POSITIVE", "NEGATIVE", "UNKNOWN")
private val V3_TEMPORAL_RELATIONS = setOf(
    "ATEMPORAL", "CURRENT", "PAST", "FUTURE", "BEFORE", "AFTER", "DURING",
    "RECURRENT", "AT_REFERENCE", "UNKNOWN",
)
private val V3_ANCHOR_REQUIRED_RELATIONS = setOf("BEFORE", "AFTER", "DURING", "AT_REFERENCE")
private val V3_PREDICATES = setOf(
    "identity.name", "identity.age", "residence.place", "presence.reported", "preference.like",
    "work.role", "possession.has", "goal.object", "attribute.is", "consent.grant",
    "consent.refuse", "speech.unresolved",
)
private val V3_SHA256_REGEX = Regex("^[0-9a-f]{64}$")

private fun requireUnitConfidence(value: Double, name: String) {
    require(value.isFinite() && value in 0.0..1.0) { "$name must be finite in [0,1]" }
}

private fun requireOpaqueId(value: String, name: String) {
    require(value.isNotBlank()) { "$name must not be blank" }
}

private fun requireNonEmptySpan(span: MipSpan, name: String) {
    require(span.end > span.start) { "$name must satisfy start < end" }
}

private fun requireSpanInsideObservation(span: MipSpan, inputLength: Int, name: String) {
    requireNonEmptySpan(span, name)
    require(span.start >= 0 && span.end <= inputLength) {
        "$name must be inside observation input length=$inputLength"
    }
}

private fun requireSpanWithin(span: MipSpan, source: MipSpan, name: String) {
    requireNonEmptySpan(span, name)
    require(span.start >= source.start && span.end <= source.end) {
        "$name must be inside claim sourceSpan"
    }
}

private fun requireSpanListWithinSource(spans: List<MipSpan>, source: MipSpan, name: String) {
    spans.forEachIndexed { index, span -> requireSpanWithin(span, source, "$name[$index]") }
    val ordered = spans.sortedWith(compareBy<MipSpan> { it.start }.thenBy { it.end })
    require(ordered.zipWithNext().all { (left, right) -> left.end <= right.start }) {
        "$name must contain non-overlapping spans"
    }
}

private fun validateCategoricalField(
    field: MipUnderstandingV3Field<String>,
    registry: Set<String>,
    name: String,
) {
    require(field.value in registry) { "Unsupported V3 $name value=${field.value}" }
    field.alternatives.forEach { alternative ->
        require(alternative.value in registry) { "Unsupported V3 $name alternative=${alternative.value}" }
    }
}

private fun validateRoleField(
    name: String,
    field: MipUnderstandingV3Field<String>,
    candidateIds: Set<String>,
) {
    when (field.fieldStatus) {
        MipUnderstandingV3FieldStatus.RESOLVED -> {
            require(field.value in candidateIds) { "$name RESOLVED must point to a concrete candidate ID" }
        }
        MipUnderstandingV3FieldStatus.UNKNOWN -> {
            require(field.value == "UNKNOWN") { "$name UNKNOWN must carry primary value UNKNOWN" }
        }
        MipUnderstandingV3FieldStatus.AMBIGUOUS -> {
            require(field.value == "UNKNOWN") { "$name AMBIGUOUS must carry primary value UNKNOWN" }
            require(field.alternatives.all { it.value in candidateIds }) {
                "$name AMBIGUOUS alternatives must be concrete candidate IDs"
            }
        }
        MipUnderstandingV3FieldStatus.NOT_APPLICABLE -> {
            require(field.value == "NONE") { "$name NOT_APPLICABLE must carry primary value NONE" }
        }
    }
}

private fun validateTemporalAnchorIdentity(
    claim: MipUnderstandingV3Claim,
    claimIds: Set<String>,
) {
    val value = claim.temporalRelation.value
    val anchor = value.anchorRef ?: return
    when {
        anchor == "speech-time" || anchor == "context-reference" -> Unit
        anchor.startsWith("temporal:") -> {
            val id = anchor.substringAfter("temporal:")
            require(id.isNotBlank() && claim.temporalEvidence.any { it.temporalId == id }) {
                "claim ${claim.claimId} temporal anchor references unknown temporalId=$id"
            }
        }
        anchor.startsWith("claim:") -> {
            val id = anchor.substringAfter("claim:")
            require(id.isNotBlank() && id in claimIds && id != claim.claimId) {
                "claim ${claim.claimId} temporal anchor references invalid claimId=$id"
            }
        }
        else -> error("claim ${claim.claimId} has invalid temporal anchorRef=$anchor")
    }
}

private fun isRecognizedAnchorShape(anchor: String): Boolean =
    anchor == "speech-time" ||
        anchor == "context-reference" ||
        (anchor.startsWith("temporal:") && anchor.length > "temporal:".length) ||
        (anchor.startsWith("claim:") && anchor.length > "claim:".length)
