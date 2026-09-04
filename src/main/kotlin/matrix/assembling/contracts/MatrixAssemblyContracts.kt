package matrix.assembling.contracts

/**
 * Shared contracts for the Matrix/Luna assembly layer.
 *
 * These types are intentionally small and deterministic. They are not a policy
 * engine and they do not approve production use. Their job is to translate
 * structured semantic state into a short prompt the GGUF can follow.
 */

data class SemanticFrame(
    val originalText: String,
    val dialogueAct: DialogueAct,
    val predicate: Predicate,
    val polarity: Polarity,
    val subject: Referent,
    val target: Referent = Referent.None,
    val temporalRelation: TemporalRelation = TemporalRelation.Current,
    val confidence: Confidence = Confidence(),
    val adultIntimacy: AdultIntimacyMarker = AdultIntimacyMarker.None,
)

enum class DialogueAct {
    Assert,
    Correct,
    Question,
    Request,
    Hypothesis,
    Unknown,
}

enum class Predicate {
    IdentityName,
    IdentityAge,
    ResidencePlace,
    PresenceReported,
    PreferenceLike,
    WorkRole,
    PossessionHas,
    GoalObject,
    AttributeIs,
    ConsentGrant,
    ConsentRefuse,
    SpeechUnresolved,
}

enum class Polarity {
    Positive,
    Negative,
    Unknown,
}

enum class TemporalRelation {
    Atemporal,
    Current,
    Past,
    Future,
    Unknown,
}

sealed class Referent {
    data object Speaker : Referent()
    data object Observer : Referent()
    data class KnownEntity(val label: String) : Referent()
    data class RecentEntity(val label: String) : Referent()
    data object Self : Referent()
    data object None : Referent()
    data object Unknown : Referent()
}

data class Confidence(
    val overall: Double = 1.0,
    val negation: Double = 1.0,
    val predicate: Double = 1.0,
    val dialogueAct: Double = 1.0,
    val referents: Double = 1.0,
    val temporal: Double = 1.0,
)

enum class AdultIntimacyMarker {
    None,
    Desire,
    Request,
    Consent,
    RefusalOrBoundary,
    UnresolvedIntimateTerm,
}

data class CoherenceDecision(
    val status: CoherenceStatus,
    val reason: String,
    val stableMemoryAllowed: Boolean,
    val persistentAffectAllowed: Boolean,
)

enum class CoherenceStatus {
    SafeToUseForReply,
    TransientOnly,
    LowConfidence,
    QuestionOnly,
    ReportOnly,
    ConflictRequiresReview,
    RejectedUnsafe,
}

data class RelationshipState(
    val summary: String,
    val trust: Double? = null,
    val affection: Double? = null,
    val attraction: Double? = null,
    val caution: Double? = null,
)

data class AffectiveState(
    val summary: String,
    val persistent: Boolean = false,
)

data class FilteredMemorySummary(
    val lines: List<String> = emptyList(),
    val uncertainty: String? = null,
)

data class PromptDirective(
    val originalUserText: String,
    val systemMeaning: String,
    val relationshipSummary: String?,
    val affectiveSummary: String?,
    val memoryLines: List<String>,
    val instruction: String,
    val hardLimits: List<String>,
)
