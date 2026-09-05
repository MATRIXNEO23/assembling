package matrix.assembling.mip

/**
 * Claim wire support for the existing MipEvidenceWire codec.
 *
 * These are extension functions on the existing codec object so Authority requests can carry
 * the canonical MipClaimV1 without introducing a second claim model or a second bridge.
 */
fun MipEvidenceWire.claimToWire(value: MipClaimV1): Map<String, Any?> = mapOf(
    "schemaVersion" to value.schemaVersion,
    "claimId" to value.claimId,
    "speaker" to claimEntityToWire(value.speaker),
    "observer" to claimEntityToWire(value.observer),
    "source" to claimEntityToWire(value.source),
    "subject" to claimEntityToWire(value.subject),
    "target" to claimEntityToWire(value.target),
    "owner" to claimEntityToWire(value.owner),
    "perspective" to claimEntityToWire(value.perspective),
    "predicate" to value.predicate,
    "objectValue" to claimFieldToWire(value.objectValue) { it },
    "dialogueAct" to claimFieldToWire(value.dialogueAct) { it },
    "polarity" to value.polarity,
    "temporalRelation" to value.temporalRelation,
    "sourceType" to claimFieldToWire(value.sourceType) { it },
    "interpretationConfidence" to claimFieldToWire(value.interpretationConfidence) { it },
    "confidenceByField" to value.confidenceByField,
    "sourceSpans" to value.sourceSpans.mapValues { (_, span) ->
        span?.let { mapOf("start" to it.start, "end" to it.end) }
    },
    "epistemicClass" to claimFieldToWire(value.epistemicClass) { it },
    "semanticMarkers" to value.semanticMarkers.mapValues { (_, field) ->
        claimFieldToWire(field) { it }
    },
)

fun MipEvidenceWire.claimFromWire(wire: Map<String, Any?>): MipClaimV1 = MipClaimV1(
    schemaVersion = wire.claimRequireString("schemaVersion"),
    claimId = wire.claimRequireString("claimId"),
    speaker = claimEntityFromWire(wire.claimRequireObject("speaker")),
    observer = claimEntityFromWire(wire.claimRequireObject("observer")),
    source = claimEntityFromWire(wire.claimRequireObject("source")),
    subject = claimEntityFromWire(wire.claimRequireObject("subject")),
    target = claimEntityFromWire(wire.claimRequireObject("target")),
    owner = claimEntityFromWire(wire.claimRequireObject("owner")),
    perspective = claimEntityFromWire(wire.claimRequireObject("perspective")),
    predicate = wire.claimRequireString("predicate"),
    objectValue = wire.claimRequireField("objectValue") { it as? String },
    dialogueAct = wire.claimRequireField("dialogueAct") { it as? String },
    polarity = wire.claimRequireString("polarity"),
    temporalRelation = wire.claimRequireString("temporalRelation"),
    sourceType = wire.claimRequireField("sourceType") { it as? String },
    interpretationConfidence = wire.claimRequireField("interpretationConfidence") { (it as? Number)?.toDouble() },
    confidenceByField = wire.claimRequireDoubleMap("confidenceByField"),
    sourceSpans = wire.claimRequireSpanMap("sourceSpans"),
    epistemicClass = wire.claimRequireField("epistemicClass") { it as? String },
    semanticMarkers = wire.claimRequireFieldMap("semanticMarkers"),
)

private fun claimEntityToWire(value: MipEntityRef): Map<String, Any?> = mapOf(
    "entityId" to value.entityId,
    "surfaceForm" to value.surfaceForm,
    "resolutionStatus" to value.resolutionStatus.name,
)

private fun claimEntityFromWire(wire: Map<String, Any?>): MipEntityRef = MipEntityRef(
    entityId = wire.claimOptionalString("entityId"),
    surfaceForm = wire.claimOptionalString("surfaceForm"),
    resolutionStatus = wire.claimRequireEnum("resolutionStatus", MipEntityResolutionStatus::valueOf),
)

private fun <T> claimFieldToWire(field: MipField<T>, encode: (T) -> Any?): Map<String, Any?> = mapOf(
    "status" to field.status.name,
    "value" to field.value?.let(encode),
)

private fun Map<String, Any?>.claimRequireObject(key: String): Map<String, Any?> =
    this[key].claimAsStringAnyMapOrNull()
        ?: throw MipContractException("Missing or non-object claim field: $key")

private fun Map<String, Any?>.claimRequireString(key: String): String =
    (this[key] as? String)?.takeIf { it.isNotBlank() }
        ?: throw MipContractException("Missing/blank/non-string claim field: $key")

private fun Map<String, Any?>.claimOptionalString(key: String): String? = when (val value = this[key]) {
    null -> null
    is String -> value
    else -> throw MipContractException("claim.$key must be string or null")
}

private fun Map<String, Any?>.claimRequireInt(key: String): Int {
    val number = this[key] as? Number ?: throw MipContractException("claim.$key must be a number")
    val long = number.toLong()
    if (number.toDouble() != long.toDouble() || long !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw MipContractException("claim.$key must be an exact Int")
    }
    return long.toInt()
}

private fun <T> Map<String, Any?>.claimRequireEnum(key: String, decode: (String) -> T): T {
    val raw = claimRequireString(key)
    return try {
        decode(raw)
    } catch (error: IllegalArgumentException) {
        throw MipContractException("Unsupported claim.$key value: $raw")
    }
}

private fun <T> Map<String, Any?>.claimRequireField(
    key: String,
    decode: (Any?) -> T?,
): MipField<T> {
    val raw = claimRequireObject(key)
    return raw.claimRequireFieldSelf(decode)
}

private fun Map<String, Any?>.claimRequireDoubleMap(key: String): Map<String, Double> {
    val raw = this[key].claimAsStringAnyMapOrNull()
        ?: throw MipContractException("claim.$key must be an object")
    return raw.mapValues { (name, value) ->
        val number = value as? Number ?: throw MipContractException("claim.$key.$name must be numeric")
        val decoded = number.toDouble()
        if (!decoded.isFinite()) throw MipContractException("claim.$key.$name must be finite")
        decoded
    }
}

private fun Map<String, Any?>.claimRequireSpanMap(key: String): Map<String, MipSpan?> {
    val raw = this[key].claimAsStringAnyMapOrNull()
        ?: throw MipContractException("claim.$key must be an object")
    return raw.mapValues { (name, value) ->
        if (value == null) {
            null
        } else {
            val span = value.claimAsStringAnyMapOrNull()
                ?: throw MipContractException("claim.$key.$name must be object or null")
            MipSpan(
                start = span.claimRequireInt("start"),
                end = span.claimRequireInt("end"),
            )
        }
    }
}

private fun Map<String, Any?>.claimRequireFieldMap(key: String): Map<String, MipField<String>> {
    val raw = this[key].claimAsStringAnyMapOrNull()
        ?: throw MipContractException("claim.$key must be an object")
    return raw.mapValues { (name, value) ->
        val encoded = value.claimAsStringAnyMapOrNull()
            ?: throw MipContractException("claim.$key.$name must be an object")
        encoded.claimRequireFieldSelf { it as? String }
    }
}

/** Decode a field map when the field object itself is the current map. */
private fun <T> Map<String, Any?>.claimRequireFieldSelf(decode: (Any?) -> T?): MipField<T> {
    val status = claimRequireEnum("status", MipFieldStatus::valueOf)
    val rawValue = this["value"]
    if (status == MipFieldStatus.PRESENT) {
        val value = decode(rawValue) ?: throw MipContractException("claim field value missing or wrong type")
        return MipField.present(value)
    }
    if (rawValue != null) throw MipContractException("claim field has value while status=$status")
    return MipField(status)
}

@Suppress("UNCHECKED_CAST")
private fun Any?.claimAsStringAnyMapOrNull(): Map<String, Any?>? {
    val raw = this as? Map<*, *> ?: return null
    if (raw.keys.any { it !is String }) return null
    return raw as Map<String, Any?>
}
