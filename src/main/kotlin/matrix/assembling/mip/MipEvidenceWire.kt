package matrix.assembling.mip

import java.time.Instant

/**
 * Reflection-free primitive wire codec for the shared MIP evidence contracts.
 * It is part of the MIP contract package, not a second intermodule bridge.
 */
object MipEvidenceWire {

    fun provenanceToWire(value: ProvenanceRef): Map<String, Any?> = mapOf(
        "originId" to value.originId,
        "originType" to value.originType,
        "originAgent" to fieldToWire(value.originAgent) { it },
        "generatedBy" to value.generatedBy.name,
        "derivedFromIds" to value.derivedFromIds,
        "quotedFromId" to fieldToWire(value.quotedFromId) { it },
        "revisionOfId" to fieldToWire(value.revisionOfId) { it },
        "observationId" to fieldToWire(value.observationId) { it },
        "eventId" to fieldToWire(value.eventId) { it },
        "claimId" to fieldToWire(value.claimId) { it },
        "createdAt" to value.createdAt.toString(),
    )

    fun provenanceFromWire(wire: Map<String, Any?>): ProvenanceRef = ProvenanceRef(
        originId = wire.requireString("originId"),
        originType = wire.requireString("originType"),
        originAgent = wire.requireField("originAgent") { it as? String },
        generatedBy = wire.requireEnum("generatedBy", ModuleId::valueOf),
        derivedFromIds = wire.requireStringList("derivedFromIds"),
        quotedFromId = wire.requireField("quotedFromId") { it as? String },
        revisionOfId = wire.requireField("revisionOfId") { it as? String },
        observationId = wire.requireField("observationId") { it as? String },
        eventId = wire.requireField("eventId") { it as? String },
        claimId = wire.requireField("claimId") { it as? String },
        createdAt = wire.requireInstant("createdAt"),
    )

    fun snapshotToWire(value: MatrixContextSnapshot): Map<String, Any?> = mapOf(
        "snapshotId" to value.snapshotId,
        "parentSnapshotId" to fieldToWire(value.parentSnapshotId) { it },
        "turnId" to value.turnId,
        "sessionId" to value.sessionId,
        "agentId" to value.agentId,
        "createdAt" to value.createdAt.toString(),
        "entries" to value.entries.map(::contextEntryToWire),
        "domainAvailability" to value.domainAvailability.map(::domainAvailabilityToWire),
    )

    fun snapshotFromWire(wire: Map<String, Any?>): MatrixContextSnapshot = MatrixContextSnapshot(
        snapshotId = wire.requireString("snapshotId"),
        parentSnapshotId = wire.requireField("parentSnapshotId") { it as? String },
        turnId = wire.requireString("turnId"),
        sessionId = wire.requireString("sessionId"),
        agentId = wire.requireString("agentId"),
        createdAt = wire.requireInstant("createdAt"),
        entries = wire.requireObjectList("entries").map(::contextEntryFromWire),
        domainAvailability = wire.requireObjectList("domainAvailability").map(::domainAvailabilityFromWire),
    )

    fun retrievalQueryToWire(value: RetrievalQuery): Map<String, Any?> = mapOf(
        "queryId" to value.queryId,
        "purpose" to value.purpose.name,
        "agentId" to value.agentId,
        "subjectRefs" to value.subjectRefs.map(::entityToWire),
        "entityRefs" to value.entityRefs.map(::entityToWire),
        "predicates" to value.predicates,
        "temporalConstraint" to fieldToWire(value.temporalConstraint) { it },
        "relationshipTarget" to fieldToWire(value.relationshipTarget, ::entityToWire),
        "goalRefs" to value.goalRefs,
        "includeHistorical" to value.includeHistorical,
        "includeSuperseded" to value.includeSuperseded,
        "maxCandidates" to value.maxCandidates,
        "maxSelected" to value.maxSelected,
        "contextSnapshotId" to value.contextSnapshotId,
    )

    fun retrievalQueryFromWire(wire: Map<String, Any?>): RetrievalQuery = RetrievalQuery(
        queryId = wire.requireString("queryId"),
        purpose = wire.requireEnum("purpose", RetrievalPurpose::valueOf),
        agentId = wire.requireString("agentId"),
        subjectRefs = wire.requireObjectList("subjectRefs").map(::entityFromWire),
        entityRefs = wire.requireObjectList("entityRefs").map(::entityFromWire),
        predicates = wire.requireStringList("predicates"),
        temporalConstraint = wire.requireField("temporalConstraint") { it as? String },
        relationshipTarget = wire.requireField("relationshipTarget") { raw ->
            raw.asStringAnyMapOrNull()?.let(::entityFromWire)
        },
        goalRefs = wire.requireStringList("goalRefs"),
        includeHistorical = wire.requireBoolean("includeHistorical"),
        includeSuperseded = wire.requireBoolean("includeSuperseded"),
        maxCandidates = wire.requireInt("maxCandidates"),
        maxSelected = wire.requireInt("maxSelected"),
        contextSnapshotId = wire.requireString("contextSnapshotId"),
    )

    fun retrievalResultToWire(value: RetrievalResult): Map<String, Any?> = mapOf(
        "queryId" to value.queryId,
        "status" to value.status.name,
        "candidateRefs" to value.candidateRefs,
        "selectedRefs" to value.selectedRefs,
        "scores" to value.scores.map {
            mapOf(
                "ref" to it.ref,
                "retrievalRelevance" to it.retrievalRelevance,
            )
        },
        "reasonCodes" to value.reasonCodes,
        "indexVersion" to fieldToWire(value.indexVersion) { it },
    )

    fun retrievalResultFromWire(wire: Map<String, Any?>): RetrievalResult = RetrievalResult(
        queryId = wire.requireString("queryId"),
        status = wire.requireEnum("status", RetrievalStatus::valueOf),
        candidateRefs = wire.requireStringList("candidateRefs"),
        selectedRefs = wire.requireStringList("selectedRefs"),
        scores = wire.requireObjectList("scores").map {
            RetrievalScore(
                ref = it.requireString("ref"),
                retrievalRelevance = it.requireDouble("retrievalRelevance"),
            )
        },
        reasonCodes = wire.requireStringList("reasonCodes"),
        indexVersion = wire.requireField("indexVersion") { it as? String },
    )

    private fun contextEntryToWire(value: ContextEntry): Map<String, Any?> = mapOf(
        "entryId" to value.entryId,
        "domain" to value.domain.name,
        "scope" to value.scope.name,
        "key" to value.key,
        "typedValue" to mapOf(
            "typeId" to value.typedValue.typeId,
            "payload" to value.typedValue.payload,
        ),
        "subjectRefs" to value.subjectRefs.map(::entityToWire),
        "entityRefs" to value.entityRefs.map(::entityToWire),
        "authority" to fieldToWire(value.authority) { it },
        "confidence" to fieldToWire(value.confidence) { it },
        "provenance" to provenanceToWire(value.provenance),
        "validity" to fieldToWire(value.validity) { it },
        "ownerModule" to value.ownerModule.name,
        "stateVersion" to fieldToWire(value.stateVersion) { it },
    )

    private fun contextEntryFromWire(wire: Map<String, Any?>): ContextEntry {
        val typed = wire.requireObject("typedValue")
        return ContextEntry(
            entryId = wire.requireString("entryId"),
            domain = wire.requireEnum("domain", ContextDomain::valueOf),
            scope = wire.requireEnum("scope", ContextScope::valueOf),
            key = wire.requireString("key"),
            typedValue = TypedContextValue(
                typeId = typed.requireString("typeId"),
                payload = typed.requireStringAllowEmpty("payload"),
            ),
            subjectRefs = wire.requireObjectList("subjectRefs").map(::entityFromWire),
            entityRefs = wire.requireObjectList("entityRefs").map(::entityFromWire),
            authority = wire.requireField("authority") { it as? String },
            confidence = wire.requireField("confidence") { (it as? Number)?.toDouble() },
            provenance = provenanceFromWire(wire.requireObject("provenance")),
            validity = wire.requireField("validity") { it as? String },
            ownerModule = wire.requireEnum("ownerModule", ModuleId::valueOf),
            stateVersion = wire.requireField("stateVersion") { it as? String },
        )
    }

    private fun domainAvailabilityToWire(value: ContextDomainAvailability): Map<String, Any?> = mapOf(
        "domain" to value.domain.name,
        "availability" to value.availability.name,
        "reasonCodes" to value.reasonCodes,
    )

    private fun domainAvailabilityFromWire(wire: Map<String, Any?>): ContextDomainAvailability =
        ContextDomainAvailability(
            domain = wire.requireEnum("domain", ContextDomain::valueOf),
            availability = wire.requireEnum("availability", DomainAvailability::valueOf),
            reasonCodes = wire.requireStringList("reasonCodes"),
        )

    private fun entityToWire(value: MipEntityRef): Map<String, Any?> = mapOf(
        "entityId" to value.entityId,
        "surfaceForm" to value.surfaceForm,
        "resolutionStatus" to value.resolutionStatus.name,
    )

    private fun entityFromWire(wire: Map<String, Any?>): MipEntityRef = MipEntityRef(
        entityId = wire.optionalString("entityId"),
        surfaceForm = wire.optionalString("surfaceForm"),
        resolutionStatus = wire.requireEnum("resolutionStatus", MipEntityResolutionStatus::valueOf),
    )

    private fun <T> fieldToWire(field: MipField<T>, encode: (T) -> Any?): Map<String, Any?> = mapOf(
        "status" to field.status.name,
        "value" to field.value?.let(encode),
    )
}

private fun Map<String, Any?>.requireObject(key: String): Map<String, Any?> =
    this[key].asStringAnyMapOrNull() ?: throw MipContractException("Missing or non-object field: $key")

private fun Map<String, Any?>.requireObjectList(key: String): List<Map<String, Any?>> {
    val raw = this[key] as? List<*> ?: throw MipContractException("Missing or non-list field: $key")
    return raw.mapIndexed { index, value ->
        value.asStringAnyMapOrNull()
            ?: throw MipContractException("$key[$index] must be an object")
    }
}

private fun Map<String, Any?>.requireString(key: String): String =
    (this[key] as? String)?.takeIf { it.isNotBlank() }
        ?: throw MipContractException("Missing/blank/non-string field: $key")

private fun Map<String, Any?>.requireStringAllowEmpty(key: String): String =
    this[key] as? String ?: throw MipContractException("Missing/non-string field: $key")

private fun Map<String, Any?>.optionalString(key: String): String? = when (val value = this[key]) {
    null -> null
    is String -> value
    else -> throw MipContractException("$key must be string or null")
}

private fun Map<String, Any?>.requireBoolean(key: String): Boolean =
    this[key] as? Boolean ?: throw MipContractException("Missing/non-boolean field: $key")

private fun Map<String, Any?>.requireInt(key: String): Int {
    val number = this[key] as? Number ?: throw MipContractException("Missing/non-number field: $key")
    val long = number.toLong()
    if (number.toDouble() != long.toDouble() || long !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw MipContractException("$key must be an exact Int")
    }
    return long.toInt()
}

private fun Map<String, Any?>.requireDouble(key: String): Double =
    (this[key] as? Number)?.toDouble() ?: throw MipContractException("Missing/non-number field: $key")

private fun Map<String, Any?>.requireInstant(key: String): Instant {
    val raw = requireString(key)
    return try {
        Instant.parse(raw)
    } catch (error: RuntimeException) {
        throw MipContractException("$key is not an ISO-8601 Instant: $raw")
    }
}

private fun Map<String, Any?>.requireStringList(key: String): List<String> {
    val raw = this[key] as? List<*> ?: throw MipContractException("Missing/non-list field: $key")
    return raw.mapIndexed { index, value ->
        value as? String ?: throw MipContractException("$key[$index] must be a string")
    }
}

private fun <T> Map<String, Any?>.requireEnum(key: String, decode: (String) -> T): T {
    val raw = requireString(key)
    return try {
        decode(raw)
    } catch (error: IllegalArgumentException) {
        throw MipContractException("Unsupported $key value: $raw")
    }
}

private fun <T> Map<String, Any?>.requireField(
    key: String,
    decode: (Any?) -> T?,
): MipField<T> {
    val raw = requireObject(key)
    val status = raw.requireEnum("status", MipFieldStatus::valueOf)
    val rawValue = raw["value"]
    if (status == MipFieldStatus.PRESENT) {
        val value = decode(rawValue) ?: throw MipContractException("$key.value missing or wrong type")
        return MipField.present(value)
    }
    if (rawValue != null) throw MipContractException("$key has value while status=$status")
    return MipField(status)
}

@Suppress("UNCHECKED_CAST")
private fun Any?.asStringAnyMapOrNull(): Map<String, Any?>? {
    val raw = this as? Map<*, *> ?: return null
    if (raw.keys.any { it !is String }) return null
    return raw as Map<String, Any?>
}
