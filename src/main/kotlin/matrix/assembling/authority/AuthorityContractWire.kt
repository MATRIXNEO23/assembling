package matrix.assembling.authority

import matrix.assembling.mip.MipContractException
import matrix.assembling.mip.MipEvidenceWire
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipFieldStatus
import matrix.assembling.mip.RetrievalResult
import matrix.assembling.mip.claimFromWire
import matrix.assembling.mip.claimToWire

/**
 * Primitive wire codec for the frozen AUTHORITY-1.0 runtime DTOs.
 *
 * This is serialization for Authority-owned DTOs. It delegates all shared MIP payloads to the
 * existing MipEvidenceWire codec and does not replace MipBridge or perform business logic.
 */
object AuthorityContractWire {

    fun requestToWire(value: AuthorityResolveRequest): Map<String, Any?> = mapOf(
        "requestId" to value.requestId,
        "claim" to MipEvidenceWire.claimToWire(value.claim),
        "contextSnapshot" to MipEvidenceWire.snapshotToWire(value.contextSnapshot),
        "retrievalResult" to authorityFieldToWire(value.retrievalResult, MipEvidenceWire::retrievalResultToWire),
        "provenance" to MipEvidenceWire.provenanceToWire(value.provenance),
    )

    fun requestFromWire(wire: Map<String, Any?>): AuthorityResolveRequest = AuthorityResolveRequest(
        requestId = wire.authorityRequireString("requestId"),
        claim = MipEvidenceWire.claimFromWire(wire.authorityRequireObject("claim")),
        contextSnapshot = MipEvidenceWire.snapshotFromWire(wire.authorityRequireObject("contextSnapshot")),
        retrievalResult = wire.authorityRequireField("retrievalResult") { raw ->
            raw.authorityAsStringAnyMapOrNull()?.let(MipEvidenceWire::retrievalResultFromWire)
        },
        provenance = MipEvidenceWire.provenanceFromWire(wire.authorityRequireObject("provenance")),
    )

    fun resolutionToWire(value: AuthorityResolution): Map<String, Any?> = mapOf(
        "resolutionId" to value.resolutionId,
        "claimId" to value.claimId,
        "contextSnapshotId" to value.contextSnapshotId,
        "retrievalQueryId" to authorityFieldToWire(value.retrievalQueryId) { it },
        "resolutionStatus" to value.resolutionStatus.name,
        "authority" to authorityFieldToWire(value.authority) { it.name },
        "authorityResolutionConfidence" to authorityFieldToWire(value.authorityResolutionConfidence) { it.value },
        "sourceReliability" to authorityFieldToWire(value.sourceReliability) { it.value },
        "contradictedMemoryRef" to authorityFieldToWire(value.contradictedMemoryRef) { it.value },
        "candidateMemoryRefs" to value.candidateMemoryRefs.map { it.value },
        "ambiguityReasons" to value.ambiguityReasons,
        "reasonCodes" to value.reasonCodes,
        "provenance" to MipEvidenceWire.provenanceToWire(value.provenance),
    )

    fun resolutionFromWire(wire: Map<String, Any?>): AuthorityResolution = AuthorityResolution(
        resolutionId = wire.authorityRequireString("resolutionId"),
        claimId = wire.authorityRequireString("claimId"),
        contextSnapshotId = wire.authorityRequireString("contextSnapshotId"),
        retrievalQueryId = wire.authorityRequireField("retrievalQueryId") { it as? String },
        resolutionStatus = wire.authorityRequireEnum("resolutionStatus", AuthorityResolutionStatus::valueOf),
        authority = wire.authorityRequireField("authority") { raw ->
            (raw as? String)?.let { value ->
                try {
                    EpistemicClass.valueOf(value)
                } catch (error: IllegalArgumentException) {
                    null
                }
            }
        },
        authorityResolutionConfidence = wire.authorityRequireField("authorityResolutionConfidence") { raw ->
            (raw as? Number)?.toDouble()?.let(::AuthorityResolutionConfidence)
        },
        sourceReliability = wire.authorityRequireField("sourceReliability") { raw ->
            (raw as? Number)?.toDouble()?.let(::SourceReliability)
        },
        contradictedMemoryRef = wire.authorityRequireField("contradictedMemoryRef") { raw ->
            (raw as? String)?.let(::MemoryRef)
        },
        candidateMemoryRefs = wire.authorityRequireStringList("candidateMemoryRefs").map(::MemoryRef),
        ambiguityReasons = wire.authorityRequireStringList("ambiguityReasons"),
        reasonCodes = wire.authorityRequireStringList("reasonCodes"),
        provenance = MipEvidenceWire.provenanceFromWire(wire.authorityRequireObject("provenance")),
    )
}

private fun <T> authorityFieldToWire(field: MipField<T>, encode: (T) -> Any?): Map<String, Any?> = mapOf(
    "status" to field.status.name,
    "value" to field.value?.let(encode),
)

private fun Map<String, Any?>.authorityRequireObject(key: String): Map<String, Any?> =
    this[key].authorityAsStringAnyMapOrNull()
        ?: throw MipContractException("Missing or non-object Authority field: $key")

private fun Map<String, Any?>.authorityRequireString(key: String): String =
    (this[key] as? String)?.takeIf { it.isNotBlank() }
        ?: throw MipContractException("Missing/blank/non-string Authority field: $key")

private fun Map<String, Any?>.authorityRequireStringList(key: String): List<String> {
    val raw = this[key] as? List<*> ?: throw MipContractException("Missing/non-list Authority field: $key")
    return raw.mapIndexed { index, value ->
        value as? String ?: throw MipContractException("Authority.$key[$index] must be a string")
    }
}

private fun <T> Map<String, Any?>.authorityRequireEnum(key: String, decode: (String) -> T): T {
    val raw = authorityRequireString(key)
    return try {
        decode(raw)
    } catch (error: IllegalArgumentException) {
        throw MipContractException("Unsupported Authority.$key value: $raw")
    }
}

private fun <T> Map<String, Any?>.authorityRequireField(
    key: String,
    decode: (Any?) -> T?,
): MipField<T> {
    val raw = authorityRequireObject(key)
    val status = raw.authorityRequireEnum("status", MipFieldStatus::valueOf)
    val rawValue = raw["value"]
    if (status == MipFieldStatus.PRESENT) {
        val value = try {
            decode(rawValue)
        } catch (error: IllegalArgumentException) {
            null
        } ?: throw MipContractException("Authority.$key.value missing, invalid, or wrong type")
        return MipField.present(value)
    }
    if (rawValue != null) throw MipContractException("Authority.$key has value while status=$status")
    return MipField(status)
}

@Suppress("UNCHECKED_CAST")
private fun Any?.authorityAsStringAnyMapOrNull(): Map<String, Any?>? {
    val raw = this as? Map<*, *> ?: return null
    if (raw.keys.any { it !is String }) return null
    return raw as Map<String, Any?>
}
