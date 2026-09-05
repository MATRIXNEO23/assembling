package matrix.assembling.mip

import java.time.Instant

/** Reserved MIP producer/owner identifiers. */
enum class ModuleId {
    PERCEPTION,
    NLU,
    UNDERSTANDING,
    CONTEXT_ASSEMBLER,
    WORLD,
    BELIEF_AUTHORITY,
    MEMORY,
    AFFECTIVE,
    RELATIONSHIP,
    INTIMACY,
    GOAL,
    DECISION,
    PROMPT_BUILDER,
    GGUF,
    OUTPUT_VALIDATOR,
    PERSISTENT_CONSOLIDATION,
    SYSTEM,
}

/**
 * Immutable universal provenance reference from MIP-1.0 section 13.
 * Optional semantic fields use MipField so unknown/unresolved/unavailable cannot collapse to null.
 */
data class ProvenanceRef(
    val originId: String,
    val originType: String,
    val originAgent: MipField<String> = MipField.notApplicable(),
    val generatedBy: ModuleId,
    val derivedFromIds: List<String> = emptyList(),
    val quotedFromId: MipField<String> = MipField.notApplicable(),
    val revisionOfId: MipField<String> = MipField.notApplicable(),
    val observationId: MipField<String> = MipField.notApplicable(),
    val eventId: MipField<String> = MipField.notApplicable(),
    val claimId: MipField<String> = MipField.notApplicable(),
    val createdAt: Instant,
) {
    init {
        require(originId.isNotBlank()) { "originId must not be blank" }
        require(originType.isNotBlank()) { "originType must not be blank" }
        requireOpaqueIds("derivedFromIds", derivedFromIds)
        originAgent.requirePresentStringIfPresent("originAgent")
        quotedFromId.requirePresentStringIfPresent("quotedFromId")
        revisionOfId.requirePresentStringIfPresent("revisionOfId")
        observationId.requirePresentStringIfPresent("observationId")
        eventId.requirePresentStringIfPresent("eventId")
        claimId.requirePresentStringIfPresent("claimId")
    }
}

enum class ContextDomain {
    LINGUISTIC,
    WORLD,
    MEMORY,
    BELIEF,
    RELATIONSHIP,
    AFFECTIVE,
    INTIMACY,
    GOAL,
    SYSTEM,
}

enum class ContextScope {
    TURN,
    CONVERSATION,
    EPISODE,
    SESSION,
    PERSISTENT,
    WORLD,
}

enum class DomainAvailability {
    AVAILABLE,
    NOT_WIRED,
    UNAVAILABLE,
    ERROR,
}

/** Explicit provider availability, separate from domain content. */
data class ContextDomainAvailability(
    val domain: ContextDomain,
    val availability: DomainAvailability,
    val reasonCodes: List<String> = emptyList(),
) {
    init {
        requireReasonCodes(reasonCodes)
    }
}

/**
 * Minimal heterogeneous context value carrier.
 * typeId identifies the registered semantic/runtime type; payload is its canonical encoded value.
 * This avoids java Any/reflection while keeping the shared context independent of module-owned classes.
 */
data class TypedContextValue(
    val typeId: String,
    val payload: String,
) {
    init {
        require(typeId.isNotBlank()) { "typedValue.typeId must not be blank" }
    }
}

/** Immutable MIP context entry. */
data class ContextEntry(
    val entryId: String,
    val domain: ContextDomain,
    val scope: ContextScope,
    val key: String,
    val typedValue: TypedContextValue,
    val subjectRefs: List<MipEntityRef> = emptyList(),
    val entityRefs: List<MipEntityRef> = emptyList(),
    val authority: MipField<String> = MipField.notApplicable(),
    val confidence: MipField<Double> = MipField.notApplicable(),
    val provenance: ProvenanceRef,
    val validity: MipField<String>,
    val ownerModule: ModuleId,
    val stateVersion: MipField<String> = MipField.notApplicable(),
) {
    init {
        require(entryId.isNotBlank()) { "entryId must not be blank" }
        require(key.isNotBlank()) { "context key must not be blank" }
        authority.requirePresentStringIfPresent("authority")
        validity.requirePresentStringIfPresent("validity")
        stateVersion.requirePresentStringIfPresent("stateVersion")
        confidence.value?.let {
            require(it.isFinite() && it in 0.0..1.0) { "context confidence must be finite in [0,1]" }
        }
    }
}

/**
 * One immutable universal context snapshot. Every reserved domain must declare availability exactly once.
 */
data class MatrixContextSnapshot(
    val snapshotId: String,
    val parentSnapshotId: MipField<String> = MipField.notApplicable(),
    val turnId: String,
    val sessionId: String,
    val agentId: String,
    val createdAt: Instant,
    val entries: List<ContextEntry>,
    val domainAvailability: List<ContextDomainAvailability>,
) {
    init {
        require(snapshotId.isNotBlank()) { "snapshotId must not be blank" }
        require(turnId.isNotBlank()) { "turnId must not be blank" }
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }
        require(agentId.isNotBlank()) { "agentId must not be blank" }
        parentSnapshotId.requirePresentStringIfPresent("parentSnapshotId")
        require(parentSnapshotId.value != snapshotId) { "parentSnapshotId must not equal snapshotId" }

        require(entries.map { it.entryId }.distinct().size == entries.size) {
            "context entry IDs must be unique"
        }
        require(domainAvailability.map { it.domain }.distinct().size == domainAvailability.size) {
            "domain availability entries must be unique per domain"
        }
        require(domainAvailability.map { it.domain }.toSet() == ContextDomain.entries.toSet()) {
            "every reserved ContextDomain must declare availability exactly once"
        }

        val availabilityByDomain = domainAvailability.associate { it.domain to it.availability }
        entries.forEach { entry ->
            require(availabilityByDomain[entry.domain] == DomainAvailability.AVAILABLE) {
                "context domain ${entry.domain} contains entries while availability=${availabilityByDomain[entry.domain]}"
            }
        }
    }

    fun availabilityOf(domain: ContextDomain): DomainAvailability =
        domainAvailability.first { it.domain == domain }.availability

    fun entriesFor(domain: ContextDomain): List<ContextEntry> =
        entries.filter { it.domain == domain }
}

enum class RetrievalPurpose {
    ENRICH_TURN,
    VERIFY_CLAIM,
    CHECK_CONTRADICTION,
    FIND_HISTORY,
    EXPLICIT_RECALL,
    ANALYZE_PATTERN,
    EXPLAIN_STATE,
    SUPPORT_DECISION,
}

enum class RetrievalStatus {
    MATCHED,
    NO_MATCH,
    AMBIGUOUS,
    INDEX_UNAVAILABLE,
    ERROR,
}

/** Universal MIP retrieval query; no engine behavior is implemented here. */
data class RetrievalQuery(
    val queryId: String,
    val purpose: RetrievalPurpose,
    val agentId: String,
    val subjectRefs: List<MipEntityRef> = emptyList(),
    val entityRefs: List<MipEntityRef> = emptyList(),
    val predicates: List<String> = emptyList(),
    val temporalConstraint: MipField<String> = MipField.notApplicable(),
    val relationshipTarget: MipField<MipEntityRef> = MipField.notApplicable(),
    val goalRefs: List<String> = emptyList(),
    val includeHistorical: Boolean = false,
    val includeSuperseded: Boolean = false,
    val maxCandidates: Int,
    val maxSelected: Int,
    val contextSnapshotId: String,
) {
    init {
        require(queryId.isNotBlank()) { "queryId must not be blank" }
        require(agentId.isNotBlank()) { "agentId must not be blank" }
        require(contextSnapshotId.isNotBlank()) { "contextSnapshotId must not be blank" }
        requireOpaqueIds("predicates", predicates)
        requireOpaqueIds("goalRefs", goalRefs)
        temporalConstraint.requirePresentStringIfPresent("temporalConstraint")
        require(maxCandidates > 0) { "maxCandidates must be > 0" }
        require(maxSelected > 0) { "maxSelected must be > 0" }
        require(maxSelected <= maxCandidates) { "maxSelected must be <= maxCandidates" }
        require(!includeSuperseded || includeHistorical) {
            "includeSuperseded requires includeHistorical=true"
        }
    }
}

/** Score identity is explicit to avoid positional ambiguity between refs and scores. */
data class RetrievalScore(
    val ref: String,
    val retrievalRelevance: Double,
) {
    init {
        require(ref.isNotBlank()) { "retrieval score ref must not be blank" }
        require(retrievalRelevance.isFinite() && retrievalRelevance in 0.0..1.0) {
            "retrievalRelevance must be finite in [0,1]"
        }
    }
}

/** Universal retrieval result with fail-closed status/list invariants. */
data class RetrievalResult(
    val queryId: String,
    val status: RetrievalStatus,
    val candidateRefs: List<String> = emptyList(),
    val selectedRefs: List<String> = emptyList(),
    val scores: List<RetrievalScore> = emptyList(),
    val reasonCodes: List<String> = emptyList(),
    val indexVersion: MipField<String> = MipField.notApplicable(),
) {
    init {
        require(queryId.isNotBlank()) { "queryId must not be blank" }
        requireOpaqueIds("candidateRefs", candidateRefs)
        requireOpaqueIds("selectedRefs", selectedRefs)
        requireReasonCodes(reasonCodes)
        indexVersion.requirePresentStringIfPresent("indexVersion")

        val candidates = candidateRefs.toSet()
        require(selectedRefs.all { it in candidates }) { "selectedRefs must be a subset of candidateRefs" }
        require(scores.map { it.ref }.distinct().size == scores.size) { "retrieval score refs must be unique" }
        require(scores.all { it.ref in candidates }) { "retrieval scores may reference candidateRefs only" }

        when (status) {
            RetrievalStatus.MATCHED -> require(candidateRefs.isNotEmpty()) {
                "MATCHED requires at least one candidateRef"
            }
            RetrievalStatus.NO_MATCH -> require(candidateRefs.isEmpty() && selectedRefs.isEmpty() && scores.isEmpty()) {
                "NO_MATCH must not carry candidate/selected refs or scores"
            }
            RetrievalStatus.AMBIGUOUS -> require(candidateRefs.size >= 2) {
                "AMBIGUOUS requires at least two candidateRefs"
            }
            RetrievalStatus.INDEX_UNAVAILABLE,
            RetrievalStatus.ERROR -> require(candidateRefs.isEmpty() && selectedRefs.isEmpty() && scores.isEmpty()) {
                "$status must not carry candidate/selected refs or scores"
            }
        }
    }
}

private fun requireOpaqueIds(name: String, values: List<String>) {
    require(values.all { it.isNotBlank() }) { "$name must not contain blank IDs" }
    require(values.distinct().size == values.size) { "$name must not contain duplicate IDs" }
}

private fun requireReasonCodes(values: List<String>) {
    require(values.all { it.isNotBlank() }) { "reasonCodes must not contain blanks" }
    require(values.distinct().size == values.size) { "reasonCodes must not contain duplicates" }
}

private fun MipField<String>.requirePresentStringIfPresent(name: String) {
    if (status == MipFieldStatus.PRESENT) {
        require(!value.isNullOrBlank()) { "$name PRESENT value must not be blank" }
    }
}
