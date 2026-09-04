package matrix.assembling

/**
 * First integration orchestrator for Matrix/Luna components.
 *
 * This file intentionally defines ports and flow only. Concrete modules can live in
 * separate repositories and be connected through adapters.
 */
class MatrixAssemblingOrchestrator(
    private val nlu: NluPort,
    private val understanding: UnderstandingPort,
    private val coherence: CoherenceGuardPort,
    private val authority: AuthorityResolverPort,
    private val memory: MemoryAdmissionPort,
    private val affective: AffectivePort,
    private val promptBuilder: SemanticFrameToPromptPort,
    private val gguf: GgufPort,
) {
    fun handle(input: UserMessage): AssistantReply {
        val nluOutput = nlu.analyze(input)
        val frame = understanding.buildFrame(input, nluOutput)
        val coherenceDecision = coherence.check(frame)
        val authorityDecision = authority.resolve(frame, coherenceDecision)
        val memoryResult = memory.admit(input, frame, coherenceDecision, authorityDecision)
        val affectiveState = affective.update(input, frame, memoryResult)
        val prompt = promptBuilder.buildPrompt(
            input = input,
            frame = frame,
            coherenceDecision = coherenceDecision,
            authorityDecision = authorityDecision,
            memoryResult = memoryResult,
            affectiveState = affectiveState,
        )
        return gguf.generate(prompt)
    }
}

data class UserMessage(
    val text: String,
    val speakerId: String,
    val observerId: String = "luna",
    val timestampMillis: Long,
    val locale: String = "it",
)

data class NluOutput(
    val dialogueAct: String,
    val predicate: String,
    val polarity: String,
    val temporalRelation: String,
    val subjectReferent: String,
    val targetReferent: String,
    val ownerReferent: String,
    val perspectiveReferent: String,
    val confidence: Map<String, Double>,
    val spans: Map<String, TextSpan?>,
)

data class TextSpan(val start: Int, val end: Int)

data class SemanticFrame(
    val originalText: String,
    val semanticSummary: String,
    val dialogueAct: String,
    val predicate: String,
    val polarity: String,
    val temporalRelation: String,
    val subject: String,
    val target: String?,
    val owner: String?,
    val confidence: Map<String, Double>,
    val adultOrIntimacy: Boolean = false,
    val stableMemoryAllowed: Boolean = false,
)

enum class CoherenceDecision {
    SAFE_TO_ADMIT,
    SAFE_TRANSIENT_ONLY,
    LOW_CONFIDENCE_HOLD,
    REPORT_ONLY,
    QUESTION_ONLY,
    CONFLICT_REQUIRES_REVIEW,
    REJECTED_UNSAFE,
}

data class AuthorityDecision(
    val accepted: Boolean,
    val ownerResolved: Boolean,
    val sourceType: String,
    val conflictStatus: String,
    val reason: String,
)

data class MemoryAdmissionResult(
    val status: String,
    val memoryIds: List<String> = emptyList(),
    val stableWrite: Boolean = false,
    val reason: String,
)

data class AffectiveState(
    val relationshipSummary: String,
    val affectiveSummary: String,
    val persistentDeltaAllowed: Boolean,
)

data class GgufPrompt(
    val text: String,
)

data class AssistantReply(
    val text: String,
    val diagnosticTrace: Map<String, String> = emptyMap(),
)

interface NluPort {
    fun analyze(input: UserMessage): NluOutput
}

interface UnderstandingPort {
    fun buildFrame(input: UserMessage, nlu: NluOutput): SemanticFrame
}

interface CoherenceGuardPort {
    fun check(frame: SemanticFrame): CoherenceDecision
}

interface AuthorityResolverPort {
    fun resolve(frame: SemanticFrame, coherence: CoherenceDecision): AuthorityDecision
}

interface MemoryAdmissionPort {
    fun admit(
        input: UserMessage,
        frame: SemanticFrame,
        coherence: CoherenceDecision,
        authority: AuthorityDecision,
    ): MemoryAdmissionResult
}

interface AffectivePort {
    fun update(
        input: UserMessage,
        frame: SemanticFrame,
        memory: MemoryAdmissionResult,
    ): AffectiveState
}

interface SemanticFrameToPromptPort {
    fun buildPrompt(
        input: UserMessage,
        frame: SemanticFrame,
        coherenceDecision: CoherenceDecision,
        authorityDecision: AuthorityDecision,
        memoryResult: MemoryAdmissionResult,
        affectiveState: AffectiveState,
    ): GgufPrompt
}

interface GgufPort {
    fun generate(prompt: GgufPrompt): AssistantReply
}
