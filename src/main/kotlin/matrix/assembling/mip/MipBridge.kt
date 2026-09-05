package matrix.assembling.mip

import java.math.BigInteger
import matrix.assembling.AffectiveState
import matrix.assembling.AuthorityDecision
import matrix.assembling.CoherenceDecision
import matrix.assembling.MemoryAdmissionResult
import matrix.assembling.TextSpan
import matrix.assembling.TypedClaim
import matrix.assembling.adapters.MatrixNluClaim

/**
 * Explicit, business-logic-free bridge between native Matrix module DTOs and MIP-1.0 wire DTOs.
 *
 * This package does not replace module-owned types and is intentionally not wired into the
 * orchestrator yet. It exists to make cross-language/cross-version translation explicit and
 * fail-closed when a destination format cannot represent a canonical field.
 */
const val MIP_SCHEMA_VERSION: String = "MIP-1.0"

enum class MipFieldStatus {
    PRESENT,
    NOT_APPLICABLE,
    UNKNOWN,
    UNRESOLVED,
    AMBIGUOUS,
    CONFLICTED,
    UNAVAILABLE,
}

data class MipField<T>(
    val status: MipFieldStatus,
    val value: T? = null,
) {
    init {
        require((status == MipFieldStatus.PRESENT) == (value != null)) {
            "MIP field with status=$status must ${if (status == MipFieldStatus.PRESENT) "contain" else "not contain"} a value"
        }
    }

    companion object {
        fun <T> present(value: T): MipField<T> = MipField(MipFieldStatus.PRESENT, value)
        fun <T> notApplicable(): MipField<T> = MipField(MipFieldStatus.NOT_APPLICABLE)
        fun <T> unknown(): MipField<T> = MipField(MipFieldStatus.UNKNOWN)
        fun <T> unresolved(): MipField<T> = MipField(MipFieldStatus.UNRESOLVED)
        fun <T> unavailable(): MipField<T> = MipField(MipFieldStatus.UNAVAILABLE)
    }
}

data class MipEntityRef(
    val entityId: String? = null,
    val surfaceForm: String? = null,
    val resolutionStatus: MipFieldStatus,
) {
    init {
        require(resolutionStatus != MipFieldStatus.PRESENT || entityId != null) {
            "Resolved/PRESENT EntityRef requires entityId"
        }
        require(resolutionStatus != MipFieldStatus.NOT_APPLICABLE || entityId == null) {
            "NOT_APPLICABLE EntityRef cannot contain entityId"
        }
    }
}

data class MipSpan(
    val start: Int,
    val end: Int,
) {
    init {
        require(start >= 0) { "span.start must be >= 0" }
        require(end >= start) { "span.end must be >= span.start" }
    }
}

data class MipClaimV1(
    val schemaVersion: String = MIP_SCHEMA_VERSION,
    val claimId: String,
    val speaker: MipEntityRef,
    val observer: MipEntityRef,
    val source: MipEntityRef,
    val subject: MipEntityRef,
    val target: MipEntityRef,
    val owner: MipEntityRef,
    val perspective: MipEntityRef,
    val predicate: String,
    val objectValue: MipField<String>,
    val dialogueAct: MipField<String>,
    val polarity: String,
    val temporalRelation: String,
    val sourceType: MipField<String>,
    val interpretationConfidence: MipField<Double>,
    val confidenceByField: Map<String, Double>,
    val sourceSpans: Map<String, MipSpan?>,
    val epistemicClass: MipField<String>,
    val semanticMarkers: Map<String, MipField<String>> = emptyMap(),
) {
    init {
        require(schemaVersion == MIP_SCHEMA_VERSION) { "Unsupported MIP schema: $schemaVersion" }
        require(claimId.isNotBlank()) { "claimId must not be blank" }
        require(predicate.isNotBlank()) { "predicate must not be blank" }
        confidenceByField.values.forEach { require(it in 0.0..1.0) { "confidence must be in [0,1]" } }
        interpretationConfidence.value?.let { require(it in 0.0..1.0) { "interpretationConfidence must be in [0,1]" } }
    }
}

data class MipAuthorityResolutionV1(
    val schemaVersion: String = MIP_SCHEMA_VERSION,
    val accepted: MipField<Boolean>,
    val ownerResolved: MipField<Boolean>,
    val sourceType: MipField<String>,
    val conflictStatus: MipField<String>,
    /** Opaque MIP identifier. Decimal strings bridge Python int and Kotlin Long without renaming semantics. */
    val contradictedMemoryId: MipField<String>,
    val reason: MipField<String>,
) {
    init {
        require(schemaVersion == MIP_SCHEMA_VERSION) { "Unsupported MIP schema: $schemaVersion" }
    }
}

data class MipMemoryResultV1(
    val schemaVersion: String = MIP_SCHEMA_VERSION,
    val status: String,
    val memoryIds: List<String>,
    val stableWrite: Boolean,
    val reason: String,
)

data class MipAffectiveSnapshotV1(
    val schemaVersion: String = MIP_SCHEMA_VERSION,
    /** Compatibility projection only; never canonical RelationshipState authority. */
    val relationshipSummary: String,
    val affectiveSummary: String,
    val persistentDeltaAllowed: Boolean,
)

data class MipCoherenceDecisionV1(
    val schemaVersion: String = MIP_SCHEMA_VERSION,
    val status: String,
)

/** Known source-side field from the Python Memory Foundation AuthorityResolution contract. */
data class PythonAuthorityResolutionWire(
    val contradicts_memory_id: BigInteger?,
)

/** Known destination-side field expected by the Kotlin Memory Admission boundary. */
data class KotlinMemoryAuthorityDecisionWire(
    val contradictedMemoryId: Long?,
)

class MipContractException(message: String) : IllegalArgumentException(message)

object MipBridge {

    fun fromMatrixNluClaim(
        native: MatrixNluClaim,
        claimId: String,
        speakerId: String? = null,
        observerId: String? = null,
    ): MipClaimV1 = MipClaimV1(
        claimId = claimId,
        speaker = entityFromResolved(speakerId, null, missing = MipFieldStatus.UNKNOWN),
        observer = entityFromResolved(observerId, null, missing = MipFieldStatus.UNKNOWN),
        source = MipEntityRef(resolutionStatus = MipFieldStatus.UNKNOWN),
        subject = entityFromResolved(native.subject, native.subjectReferent, missing = referentStatus(native.subjectReferent)),
        target = entityFromResolved(native.target, native.targetReferent, missing = optionalReferentStatus(native.targetReferent)),
        owner = entityFromResolved(native.owner, native.ownerReferent, missing = referentStatus(native.ownerReferent)),
        perspective = entityFromResolved(native.perspective, native.perspectiveReferent, missing = referentStatus(native.perspectiveReferent)),
        predicate = native.predicate,
        objectValue = native.objectValue?.let(MipField<String>::present) ?: MipField.notApplicable(),
        dialogueAct = MipField.present(native.dialogueAct),
        polarity = native.polarity,
        temporalRelation = native.temporalRelation,
        sourceType = native.sourceType?.let(MipField<String>::present) ?: MipField.unresolved(),
        interpretationConfidence = MipField.present(native.confidence),
        confidenceByField = native.confidenceByHead,
        sourceSpans = mapOf(
            "source" to native.sourceSpan.toMipSpan(),
            "subject" to native.subjectSpan.toMipSpan(),
            "object" to native.objectSpan.toMipSpan(),
            "negation" to native.negationSpan.toMipSpan(),
            "temporal" to native.temporalSpan.toMipSpan(),
        ),
        epistemicClass = if (native.worldTruth) MipField.present("WORLD_TRUTH") else MipField.unknown(),
        semanticMarkers = mapOf(
            "ADULT_INTIMACY" to when (native.adultOrIntimacy) {
                true -> MipField.present("PRESENT")
                false -> MipField.present("ABSENT")
                null -> MipField.unknown()
            }
        ),
    )

    fun toMatrixNluClaim(canonical: MipClaimV1): MatrixNluClaim = MatrixNluClaim(
        dialogueAct = canonical.dialogueAct.requirePresent("dialogueAct"),
        predicate = canonical.predicate,
        polarity = canonical.polarity,
        temporalRelation = canonical.temporalRelation,
        subjectReferent = canonical.subject.nativeReferent("subject"),
        targetReferent = canonical.target.nativeOptionalReferent(),
        ownerReferent = canonical.owner.nativeReferent("owner"),
        perspectiveReferent = canonical.perspective.nativeReferent("perspective"),
        confidence = canonical.interpretationConfidence.requirePresent("interpretationConfidence"),
        confidenceByHead = canonical.confidenceByField,
        sourceSpan = canonical.sourceSpans["source"].toNativeSpan(),
        subjectSpan = canonical.sourceSpans["subject"].toNativeSpan(),
        objectSpan = canonical.sourceSpans["object"].toNativeSpan(),
        negationSpan = canonical.sourceSpans["negation"].toNativeSpan(),
        temporalSpan = canonical.sourceSpans["temporal"].toNativeSpan(),
        subject = canonical.subject.entityId,
        target = canonical.target.entityId,
        owner = canonical.owner.entityId,
        perspective = canonical.perspective.entityId,
        objectValue = canonical.objectValue.presentOrNull("objectValue"),
        sourceType = canonical.sourceType.presentOrNull("sourceType"),
        worldTruth = canonical.epistemicClass.value == "WORLD_TRUTH",
        adultOrIntimacy = canonical.semanticMarkers["ADULT_INTIMACY"].toAdultBoolean(),
    )

    fun fromAssemblingTypedClaim(
        native: TypedClaim,
        speakerId: String? = null,
        observerId: String? = null,
    ): MipClaimV1 = MipClaimV1(
        claimId = native.claimId,
        speaker = entityFromResolved(speakerId, null, missing = MipFieldStatus.UNKNOWN),
        observer = entityFromResolved(observerId, null, missing = MipFieldStatus.UNKNOWN),
        source = MipEntityRef(resolutionStatus = MipFieldStatus.UNKNOWN),
        subject = entityFromNativeValue(native.subject, required = true),
        target = entityFromNativeValue(native.target, required = false),
        owner = entityFromNativeValue(native.ownerId, required = true),
        perspective = entityFromNativeValue(native.perspective, required = true),
        predicate = native.predicate,
        objectValue = native.objectValue?.let(MipField<String>::present) ?: MipField.notApplicable(),
        dialogueAct = MipField.unavailable(),
        polarity = native.polarity,
        temporalRelation = native.temporalRelation,
        sourceType = MipField.present(native.sourceType),
        interpretationConfidence = native.confidence["overall"]?.let(MipField<Double>::present) ?: MipField.unavailable(),
        confidenceByField = native.confidence,
        sourceSpans = native.spans.mapValues { (_, span) -> span?.let { MipSpan(it.start, it.end) } },
        epistemicClass = if (native.worldTruth) MipField.present("WORLD_TRUTH") else MipField.unknown(),
    )

    fun toAssemblingTypedClaim(canonical: MipClaimV1): TypedClaim = TypedClaim(
        claimId = canonical.claimId,
        ownerId = canonical.owner.nativeRequiredOrNull("owner"),
        subject = canonical.subject.nativeSubject(),
        predicate = canonical.predicate,
        objectValue = canonical.objectValue.presentOrNull("objectValue"),
        target = canonical.target.nativeOptionalId(),
        polarity = canonical.polarity,
        temporalRelation = canonical.temporalRelation,
        sourceType = canonical.sourceType.requirePresent("sourceType"),
        confidence = canonical.confidenceByField + canonical.interpretationConfidence.value?.let { mapOf("overall" to it) }.orEmpty(),
        spans = canonical.sourceSpans.mapValues { (_, span) -> span?.let { TextSpan(it.start, it.end) } },
        perspective = canonical.perspective.nativeRequiredOrNull("perspective"),
        worldTruth = canonical.epistemicClass.value == "WORLD_TRUTH",
    )

    fun fromAssemblingCoherenceDecision(native: CoherenceDecision): MipCoherenceDecisionV1 =
        MipCoherenceDecisionV1(status = native.name)

    fun toAssemblingCoherenceDecision(canonical: MipCoherenceDecisionV1): CoherenceDecision =
        try {
            CoherenceDecision.valueOf(canonical.status)
        } catch (error: IllegalArgumentException) {
            throw MipContractException("Unsupported coherence status for Assembling: ${canonical.status}")
        }

    fun fromAssemblingAuthorityDecision(native: AuthorityDecision): MipAuthorityResolutionV1 =
        MipAuthorityResolutionV1(
            accepted = MipField.present(native.accepted),
            ownerResolved = MipField.present(native.ownerResolved),
            sourceType = MipField.present(native.sourceType),
            conflictStatus = MipField.present(native.conflictStatus),
            contradictedMemoryId = MipField.unavailable(),
            reason = MipField.present(native.reason),
        )

    fun toAssemblingAuthorityDecision(canonical: MipAuthorityResolutionV1): AuthorityDecision {
        if (canonical.contradictedMemoryId.status == MipFieldStatus.PRESENT) {
            throw MipContractException(
                "Current Assembling AuthorityDecision cannot represent contradictedMemoryId; use a Memory-boundary adapter instead"
            )
        }
        return AuthorityDecision(
            accepted = canonical.accepted.requirePresent("accepted"),
            ownerResolved = canonical.ownerResolved.requirePresent("ownerResolved"),
            sourceType = canonical.sourceType.requirePresent("sourceType"),
            conflictStatus = canonical.conflictStatus.requirePresent("conflictStatus"),
            reason = canonical.reason.requirePresent("reason"),
        )
    }

    fun fromPythonAuthorityResolution(native: PythonAuthorityResolutionWire): MipAuthorityResolutionV1 =
        MipAuthorityResolutionV1(
            accepted = MipField.unavailable(),
            ownerResolved = MipField.unavailable(),
            sourceType = MipField.unavailable(),
            conflictStatus = MipField.unavailable(),
            contradictedMemoryId = native.contradicts_memory_id
                ?.toString()
                ?.let(MipField<String>::present)
                ?: MipField.notApplicable(),
            reason = MipField.unavailable(),
        )

    fun toPythonAuthorityResolution(canonical: MipAuthorityResolutionV1): PythonAuthorityResolutionWire =
        PythonAuthorityResolutionWire(
            contradicts_memory_id = canonical.contradictedMemoryId.presentOrNull("contradictedMemoryId")?.let {
                try {
                    BigInteger(it)
                } catch (error: NumberFormatException) {
                    throw MipContractException("contradictedMemoryId is not a decimal integer: $it")
                }
            }
        )

    fun toKotlinMemoryAuthorityDecision(canonical: MipAuthorityResolutionV1): KotlinMemoryAuthorityDecisionWire =
        KotlinMemoryAuthorityDecisionWire(
            contradictedMemoryId = canonical.contradictedMemoryId.presentOrNull("contradictedMemoryId")?.let {
                it.toLongOrNull() ?: throw MipContractException(
                    "contradictedMemoryId=$it cannot be represented as Kotlin Long"
                )
            }
        )

    fun fromAssemblingMemoryResult(native: MemoryAdmissionResult): MipMemoryResultV1 =
        MipMemoryResultV1(
            status = native.status,
            memoryIds = native.memoryIds,
            stableWrite = native.stableWrite,
            reason = native.reason,
        )

    fun toAssemblingMemoryResult(canonical: MipMemoryResultV1): MemoryAdmissionResult =
        MemoryAdmissionResult(
            status = canonical.status,
            memoryIds = canonical.memoryIds,
            stableWrite = canonical.stableWrite,
            reason = canonical.reason,
        )

    fun fromAssemblingAffectiveState(native: AffectiveState): MipAffectiveSnapshotV1 =
        MipAffectiveSnapshotV1(
            relationshipSummary = native.relationshipSummary,
            affectiveSummary = native.affectiveSummary,
            persistentDeltaAllowed = native.persistentDeltaAllowed,
        )

    fun toAssemblingAffectiveState(canonical: MipAffectiveSnapshotV1): AffectiveState =
        AffectiveState(
            relationshipSummary = canonical.relationshipSummary,
            affectiveSummary = canonical.affectiveSummary,
            persistentDeltaAllowed = canonical.persistentDeltaAllowed,
        )

    /** Primitive-only representation suitable for JSON/dict encoders without reflection. */
    fun authorityToWireMap(canonical: MipAuthorityResolutionV1): Map<String, Any?> = mapOf(
        "schemaVersion" to canonical.schemaVersion,
        "accepted" to canonical.accepted.toWireMap(),
        "ownerResolved" to canonical.ownerResolved.toWireMap(),
        "sourceType" to canonical.sourceType.toWireMap(),
        "conflictStatus" to canonical.conflictStatus.toWireMap(),
        "contradictedMemoryId" to canonical.contradictedMemoryId.toWireMap(),
        "reason" to canonical.reason.toWireMap(),
    )

    fun authorityFromWireMap(wire: Map<String, Any?>): MipAuthorityResolutionV1 {
        val version = wire.requireString("schemaVersion")
        if (version != MIP_SCHEMA_VERSION) throw MipContractException("Unsupported MIP schema: $version")
        return MipAuthorityResolutionV1(
            schemaVersion = version,
            accepted = wire.requireField("accepted") { it as? Boolean },
            ownerResolved = wire.requireField("ownerResolved") { it as? Boolean },
            sourceType = wire.requireField("sourceType") { it as? String },
            conflictStatus = wire.requireField("conflictStatus") { it as? String },
            contradictedMemoryId = wire.requireField("contradictedMemoryId") { it as? String },
            reason = wire.requireField("reason") { it as? String },
        )
    }

    private fun entityFromResolved(value: String?, surface: String?, missing: MipFieldStatus): MipEntityRef =
        if (value != null && value != "UNKNOWN") {
            MipEntityRef(entityId = value, surfaceForm = surface, resolutionStatus = MipFieldStatus.PRESENT)
        } else {
            MipEntityRef(surfaceForm = surface, resolutionStatus = missing)
        }

    private fun entityFromNativeValue(value: String?, required: Boolean): MipEntityRef = when {
        value == null -> MipEntityRef(resolutionStatus = if (required) MipFieldStatus.UNRESOLVED else MipFieldStatus.NOT_APPLICABLE)
        value == "UNKNOWN" -> MipEntityRef(surfaceForm = value, resolutionStatus = MipFieldStatus.UNKNOWN)
        else -> MipEntityRef(entityId = value, surfaceForm = value, resolutionStatus = MipFieldStatus.PRESENT)
    }

    private fun referentStatus(referent: String): MipFieldStatus = when (referent) {
        "UNKNOWN" -> MipFieldStatus.UNKNOWN
        "NONE" -> MipFieldStatus.NOT_APPLICABLE
        else -> MipFieldStatus.UNRESOLVED
    }

    private fun optionalReferentStatus(referent: String): MipFieldStatus = when (referent) {
        "NONE" -> MipFieldStatus.NOT_APPLICABLE
        "UNKNOWN" -> MipFieldStatus.UNKNOWN
        else -> MipFieldStatus.UNRESOLVED
    }
}

private fun List<Int>?.toMipSpan(): MipSpan? =
    if (this != null && size == 2 && this[0] >= 0 && this[1] >= this[0]) MipSpan(this[0], this[1]) else null

private fun MipSpan?.toNativeSpan(): List<Int>? = this?.let { listOf(it.start, it.end) }

private fun <T> MipField<T>.requirePresent(name: String): T =
    if (status == MipFieldStatus.PRESENT && value != null) value
    else throw MipContractException("$name must be PRESENT, found $status")

private fun <T> MipField<T>.presentOrNull(name: String): T? = when (status) {
    MipFieldStatus.PRESENT -> value ?: throw MipContractException("$name PRESENT without value")
    MipFieldStatus.NOT_APPLICABLE,
    MipFieldStatus.UNKNOWN,
    MipFieldStatus.UNRESOLVED,
    MipFieldStatus.UNAVAILABLE -> null
    MipFieldStatus.AMBIGUOUS,
    MipFieldStatus.CONFLICTED -> throw MipContractException("$name=$status cannot be collapsed to nullable native field")
}

private fun MipEntityRef.nativeReferent(name: String): String =
    surfaceForm ?: entityId ?: when (resolutionStatus) {
        MipFieldStatus.UNKNOWN -> "UNKNOWN"
        MipFieldStatus.NOT_APPLICABLE -> "NONE"
        MipFieldStatus.UNRESOLVED -> "UNKNOWN"
        MipFieldStatus.PRESENT -> entityId ?: throw MipContractException("$name PRESENT without entityId")
        else -> throw MipContractException("$name=$resolutionStatus cannot be represented by MatrixNluClaim referent")
    }

private fun MipEntityRef.nativeOptionalReferent(): String =
    surfaceForm ?: entityId ?: when (resolutionStatus) {
        MipFieldStatus.NOT_APPLICABLE -> "NONE"
        MipFieldStatus.UNKNOWN,
        MipFieldStatus.UNRESOLVED -> "UNKNOWN"
        MipFieldStatus.PRESENT -> entityId ?: throw MipContractException("PRESENT target without entityId")
        else -> throw MipContractException("target=$resolutionStatus cannot be represented by MatrixNluClaim referent")
    }

private fun MipEntityRef.nativeSubject(): String = when (resolutionStatus) {
    MipFieldStatus.PRESENT -> entityId ?: throw MipContractException("subject PRESENT without entityId")
    MipFieldStatus.UNKNOWN,
    MipFieldStatus.UNRESOLVED -> "UNKNOWN"
    else -> throw MipContractException("subject=$resolutionStatus cannot be represented by TypedClaim")
}

private fun MipEntityRef.nativeRequiredOrNull(name: String): String? = when (resolutionStatus) {
    MipFieldStatus.PRESENT -> entityId ?: throw MipContractException("$name PRESENT without entityId")
    MipFieldStatus.UNKNOWN,
    MipFieldStatus.UNRESOLVED,
    MipFieldStatus.NOT_APPLICABLE -> null
    else -> throw MipContractException("$name=$resolutionStatus cannot be collapsed to nullable TypedClaim field")
}

private fun MipEntityRef.nativeOptionalId(): String? = when (resolutionStatus) {
    MipFieldStatus.PRESENT -> entityId ?: throw MipContractException("target PRESENT without entityId")
    MipFieldStatus.NOT_APPLICABLE,
    MipFieldStatus.UNKNOWN,
    MipFieldStatus.UNRESOLVED -> null
    else -> throw MipContractException("target=$resolutionStatus cannot be collapsed to nullable TypedClaim field")
}

private fun MipField<String>?.toAdultBoolean(): Boolean? = when {
    this == null -> null
    status == MipFieldStatus.UNKNOWN || status == MipFieldStatus.UNAVAILABLE -> null
    status == MipFieldStatus.PRESENT && value == "PRESENT" -> true
    status == MipFieldStatus.PRESENT && value == "ABSENT" -> false
    status == MipFieldStatus.PRESENT -> true
    status == MipFieldStatus.NOT_APPLICABLE -> false
    else -> throw MipContractException("ADULT_INTIMACY marker $status cannot be represented as Boolean?")
}

private fun <T> MipField<T>.toWireMap(): Map<String, Any?> = mapOf(
    "status" to status.name,
    "value" to value,
)

private fun Map<String, Any?>.requireString(key: String): String =
    this[key] as? String ?: throw MipContractException("Missing or non-string field: $key")

@Suppress("UNCHECKED_CAST")
private fun <T> Map<String, Any?>.requireField(
    key: String,
    decode: (Any?) -> T?,
): MipField<T> {
    val raw = this[key] as? Map<*, *> ?: throw MipContractException("Missing or non-object MIP field: $key")
    val statusName = raw["status"] as? String ?: throw MipContractException("$key.status missing")
    val status = try {
        MipFieldStatus.valueOf(statusName)
    } catch (error: IllegalArgumentException) {
        throw MipContractException("$key.status unsupported: $statusName")
    }
    val rawValue = raw["value"]
    if (status == MipFieldStatus.PRESENT) {
        val value = decode(rawValue) ?: throw MipContractException("$key.value missing or wrong type")
        return MipField.present(value)
    }
    if (rawValue != null) throw MipContractException("$key has value while status=$status")
    return MipField(status)
}
