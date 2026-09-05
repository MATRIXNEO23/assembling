package matrix.assembling.authority

/**
 * Kotlin realization of the frozen MIP-1.0 / AUTHORITY-1.0 value vocabulary.
 *
 * This package contains contract types only. It must not perform Authority resolution,
 * Memory access, contradiction detection, persistence, or natural-language interpretation.
 */
enum class EpistemicClass {
    WORLD_TRUTH,
    OBSERVATION,
    REPORT,
    INFERENCE,
    BELIEF,
}

/** Overall status of one Authority resolution attempt. */
enum class AuthorityResolutionStatus {
    COMPLETE,
    PARTIAL,
    HOLD,
    UNAVAILABLE,
    ERROR,
}

/**
 * Confidence that an Authority classification/resolution is correct.
 *
 * This is deliberately distinct from EpistemicClass, interpretation confidence,
 * source reliability, belief confidence, and retrieval relevance.
 */
data class AuthorityResolutionConfidence(
    val value: Double,
) {
    init {
        require(value in 0.0..1.0) {
            "AuthorityResolutionConfidence must be in [0,1]"
        }
    }
}

/**
 * Reliability evidence about an epistemic source when such evidence actually exists.
 * Absence/unknown/unavailable remains represented by the surrounding MIP field status.
 */
data class SourceReliability(
    val value: Double,
) {
    init {
        require(value in 0.0..1.0) {
            "SourceReliability must be in [0,1]"
        }
    }
}

/**
 * Language-neutral opaque Memory identity used by Authority contradiction output.
 *
 * It is intentionally not numeric: adapters may bridge Python integer identity and
 * Kotlin Long identity without making numeric width part of MIP semantics.
 */
data class MemoryRef(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "MemoryRef must not be blank" }
    }
}

/**
 * Frozen AUTHORITY reason-code vocabulary.
 *
 * Codes are observable diagnostic facts only. They are not free-form reasoning or
 * hidden chain-of-thought. The namespace remains additive/versioned by MIP.
 */
object AuthorityReasonCode {
    const val RESOLVED_WORLD_TRUTH = "AUTHORITY.RESOLVED.WORLD_TRUTH"
    const val RESOLVED_OBSERVATION = "AUTHORITY.RESOLVED.OBSERVATION"
    const val RESOLVED_REPORT = "AUTHORITY.RESOLVED.REPORT"
    const val RESOLVED_INFERENCE = "AUTHORITY.RESOLVED.INFERENCE"
    const val RESOLVED_BELIEF = "AUTHORITY.RESOLVED.BELIEF"

    const val OWNER_UNRESOLVED = "AUTHORITY.OWNER_UNRESOLVED"
    const val SUBJECT_UNRESOLVED = "AUTHORITY.SUBJECT_UNRESOLVED"
    const val SOURCE_UNRESOLVED = "AUTHORITY.SOURCE_UNRESOLVED"
    const val TEMPORAL_UNRESOLVED = "AUTHORITY.TEMPORAL_UNRESOLVED"

    const val RETRIEVAL_NO_MATCH = "AUTHORITY.RETRIEVAL.NO_MATCH"
    const val RETRIEVAL_UNAVAILABLE = "AUTHORITY.RETRIEVAL.UNAVAILABLE"

    const val CONTRADICTION_IDENTIFIED = "AUTHORITY.CONTRADICTION.IDENTIFIED"
    const val CONTRADICTION_NONE = "AUTHORITY.CONTRADICTION.NONE"
    const val CONTRADICTION_AMBIGUOUS = "AUTHORITY.CONTRADICTION.AMBIGUOUS"
    const val CONTRADICTION_TEMPORAL_MISMATCH = "AUTHORITY.CONTRADICTION.TEMPORAL_MISMATCH"
    const val CONTRADICTION_UNRELATED_PREDICATE = "AUTHORITY.CONTRADICTION.UNRELATED_PREDICATE"

    const val CORRECTION_CANDIDATE = "AUTHORITY.CORRECTION.CANDIDATE"
    const val HOLD_AMBIGUOUS = "AUTHORITY.HOLD.AMBIGUOUS"
    const val ERROR = "AUTHORITY.ERROR"

    val frozenV1: Set<String> = setOf(
        RESOLVED_WORLD_TRUTH,
        RESOLVED_OBSERVATION,
        RESOLVED_REPORT,
        RESOLVED_INFERENCE,
        RESOLVED_BELIEF,
        OWNER_UNRESOLVED,
        SUBJECT_UNRESOLVED,
        SOURCE_UNRESOLVED,
        TEMPORAL_UNRESOLVED,
        RETRIEVAL_NO_MATCH,
        RETRIEVAL_UNAVAILABLE,
        CONTRADICTION_IDENTIFIED,
        CONTRADICTION_NONE,
        CONTRADICTION_AMBIGUOUS,
        CONTRADICTION_TEMPORAL_MISMATCH,
        CONTRADICTION_UNRELATED_PREDICATE,
        CORRECTION_CANDIDATE,
        HOLD_AMBIGUOUS,
        ERROR,
    )

    fun isAuthorityCode(value: String): Boolean = value.startsWith("AUTHORITY.")
}
