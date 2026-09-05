package matrix.assembling.mip

import java.math.BigInteger
import matrix.assembling.authority.AuthorityResolution
import matrix.assembling.authority.AuthorityResolutionStatus
import matrix.assembling.authority.MemoryRef

/**
 * Canonical AUTHORITY-1.0 compatibility projections for legacy/native contradiction-ID seams.
 *
 * This file does not define a second bridge or Authority model. It projects only the one field
 * that the existing destination wire can represent. All other canonical Authority semantics stay
 * in AuthorityResolution and are never silently claimed to have been transported.
 */

/**
 * Project a COMPLETE canonical AuthorityResolution to the currently known Kotlin Memory seam.
 *
 * The destination only represents a nullable Long contradiction ID. Therefore:
 * - PRESENT -> exact canonical decimal Long;
 * - NOT_APPLICABLE -> null;
 * - any incomplete Authority resolution -> fail closed;
 * - any opaque/non-decimal/out-of-range MemoryRef -> fail closed.
 */
fun AuthorityResolution.toKotlinMemoryContradictionProjection(): KotlinMemoryAuthorityDecisionWire {
    requireCompleteCompatibilityProjection("Kotlin Memory contradiction projection")
    return KotlinMemoryAuthorityDecisionWire(
        contradictedMemoryId = contradictedMemoryRef.toExactLongOrKnownAbsent("contradictedMemoryRef")
    )
}

/**
 * Project a COMPLETE canonical AuthorityResolution to the historically known Python integer seam.
 *
 * This is intentionally named a contradiction projection. It is NOT a full mapping to the
 * historical Python AuthorityResolution, whose exact canonical source artifact is not frozen.
 */
fun AuthorityResolution.toPythonContradictionProjection(): PythonAuthorityResolutionWire {
    requireCompleteCompatibilityProjection("Python contradiction projection")
    return PythonAuthorityResolutionWire(
        contradicts_memory_id = contradictedMemoryRef.toExactBigIntegerOrKnownAbsent("contradictedMemoryRef")
    )
}

private fun AuthorityResolution.requireCompleteCompatibilityProjection(destination: String) {
    if (resolutionStatus != AuthorityResolutionStatus.COMPLETE) {
        throw MipContractException(
            "$destination requires AuthorityResolutionStatus.COMPLETE; found $resolutionStatus"
        )
    }
}

private fun MipField<MemoryRef>.toExactLongOrKnownAbsent(name: String): Long? = when (status) {
    MipFieldStatus.NOT_APPLICABLE -> null
    MipFieldStatus.PRESENT -> {
        val raw = value?.value ?: throw MipContractException("$name PRESENT without MemoryRef")
        val parsed = raw.toLongOrNull()
            ?: throw MipContractException("$name=$raw cannot be represented as Kotlin Long")
        if (parsed.toString() != raw) {
            throw MipContractException(
                "$name=$raw is not the canonical decimal representation of Kotlin Long $parsed"
            )
        }
        parsed
    }
    else -> throw MipContractException(
        "$name=$status cannot be represented by nullable Kotlin contradiction ID"
    )
}

private fun MipField<MemoryRef>.toExactBigIntegerOrKnownAbsent(name: String): BigInteger? = when (status) {
    MipFieldStatus.NOT_APPLICABLE -> null
    MipFieldStatus.PRESENT -> {
        val raw = value?.value ?: throw MipContractException("$name PRESENT without MemoryRef")
        val parsed = try {
            BigInteger(raw)
        } catch (error: NumberFormatException) {
            throw MipContractException("$name=$raw cannot be represented as Python integer")
        }
        if (parsed.toString() != raw) {
            throw MipContractException(
                "$name=$raw is not the canonical decimal representation of Python integer $parsed"
            )
        }
        parsed
    }
    else -> throw MipContractException(
        "$name=$status cannot be represented by nullable Python contradiction ID"
    )
}
