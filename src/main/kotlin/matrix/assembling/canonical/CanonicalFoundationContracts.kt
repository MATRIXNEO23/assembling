package matrix.assembling.canonical

/** Claim-wise semantic stability result produced before epistemic Authority resolution. */
enum class CanonicalCoherenceStatus {
    PASS,
    HOLD,
    ERROR,
}

data class CanonicalClaimCoherenceResult(
    val claimId: String,
    val contextSnapshotId: String,
    val status: CanonicalCoherenceStatus,
    val reasonCodes: List<String>,
) {
    init {
        require(claimId.isNotBlank()) { "claimId must not be blank" }
        require(contextSnapshotId.isNotBlank()) { "contextSnapshotId must not be blank" }
        require(reasonCodes.isNotEmpty()) { "reasonCodes must not be empty" }
        require(reasonCodes.all { it.isNotBlank() }) { "reasonCodes must not contain blanks" }
        require(reasonCodes.distinct().size == reasonCodes.size) { "reasonCodes must not contain duplicates" }
    }
}
