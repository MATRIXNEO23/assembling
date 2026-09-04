package matrix.assembling.adapters

import matrix.assembling.DiagnosticTrace
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.NluOutput
import matrix.assembling.NluPort
import matrix.assembling.SemanticFrame
import matrix.assembling.TextSpan
import matrix.assembling.TypedClaim
import matrix.assembling.UnderstandingPort

/**
 * Adapter over the Matrix Understanding / Matrix-NLU lab output.
 *
 * The copied lab component produces labels, spans and claim dictionaries.
 * This adapter normalizes that output into the canonical MatrixTurnFrame used
 * by the assembling layer.
 *
 * IMPORTANT: Understanding preserves semantic evidence and provenance but does
 * not authorize durable memory. Stable persistence belongs downstream to
 * Coherence/Authority/Memory Admission.
 */
class UnderstandingLabAdapter(
    private val runtime: MatrixNluRuntimeBridge,
) : NluPort, UnderstandingPort {

    override fun analyze(turn: MatrixTurnFrame): MatrixTurnFrame {
        val interpretation = runtime.interpret(
            MatrixNluRequest(
                turnId = turn.turnId,
                language = turn.input.locale.uppercase(),
                text = turn.input.text,
                speakerId = turn.input.speakerId,
                observerId = turn.input.observerId,
            )
        )
        val primary = interpretation.claims.firstOrNull()
        val nlu = if (primary == null) {
            unresolvedNlu()
        } else {
            NluOutput(
                dialogueAct = primary.dialogueAct,
                predicate = primary.predicate,
                polarity = primary.polarity,
                temporalRelation = primary.temporalRelation,
                subjectReferent = primary.subjectReferent,
                targetReferent = primary.targetReferent,
                ownerReferent = primary.ownerReferent,
                perspectiveReferent = primary.perspectiveReferent,
                confidence = primary.confidenceByHead + ("overall" to primary.confidence),
                spans = mapOf(
                    "source" to primary.sourceSpan.toTextSpan(),
                    "subject" to primary.subjectSpan.toTextSpan(),
                    "object" to primary.objectSpan.toTextSpan(),
                    "negation" to primary.negationSpan.toTextSpan(),
                    "temporal" to primary.temporalSpan.toTextSpan(),
                ),
                resolvedSubject = primary.subject,
                resolvedTarget = primary.target,
                resolvedOwner = primary.owner,
                resolvedPerspective = primary.perspective,
                objectValue = primary.objectValue,
                sourceType = primary.sourceType,
                worldTruth = primary.worldTruth,
            )
        }
        return turn.copy(
            nlu = nlu,
            diagnostics = turn.diagnostics
                .add("understanding_lab.nlu.analyzed")
                .tag("understanding_lab.status", interpretation.status)
                .tag("understanding_lab.engine", interpretation.engine)
                .tag("understanding_lab.claim_count", interpretation.claims.size.toString()),
        )
    }

    override fun understand(turn: MatrixTurnFrame): MatrixTurnFrame {
        val nlu = turn.requireNlu()
        val objectValue = nlu.objectValue ?: turn.input.text.sliceOrNull(nlu.spans["object"])
        val subject = nlu.resolvedSubject
            ?: resolveReferent(nlu.subjectReferent, turn.input.speakerId, turn.input.observerId)
        val target = nlu.resolvedTarget
            ?: resolveOptionalReferent(nlu.targetReferent, turn.input.speakerId, turn.input.observerId)
        val owner = nlu.resolvedOwner
            ?: resolveOptionalReferent(nlu.ownerReferent, turn.input.speakerId, turn.input.observerId)
        val perspective = nlu.resolvedPerspective
            ?: resolveOptionalReferent(nlu.perspectiveReferent, turn.input.speakerId, turn.input.observerId)
        val sourceType = nlu.sourceType ?: sourceType(nlu)
        val semantic = SemanticFrame(
            originalText = turn.input.text,
            semanticSummary = semanticSummary(nlu, objectValue),
            dialogueAct = nlu.dialogueAct,
            predicate = nlu.predicate,
            polarity = nlu.polarity,
            temporalRelation = nlu.temporalRelation,
            subject = subject,
            target = target,
            owner = owner,
            confidence = nlu.confidence,
            adultOrIntimacy = isAdultOrIntimacy(nlu, objectValue),
            // Compatibility field only. Understanding must never authorize
            // durable memory; downstream admission owns this decision.
            stableMemoryAllowed = false,
        )
        val claim = TypedClaim(
            claimId = "${turn.turnId}:claim:0",
            ownerId = owner,
            subject = subject,
            predicate = nlu.predicate,
            objectValue = objectValue,
            target = target,
            polarity = nlu.polarity,
            temporalRelation = nlu.temporalRelation,
            sourceType = sourceType,
            confidence = nlu.confidence,
            spans = nlu.spans,
            perspective = perspective,
            worldTruth = nlu.worldTruth,
        )
        return turn.copy(
            semantic = semantic,
            typedClaims = listOf(claim),
            diagnostics = turn.diagnostics
                .add("understanding_lab.semantic_frame.built")
                .tag("understanding_lab.source_type", sourceType)
                .tag("understanding_lab.world_truth_observed", nlu.worldTruth.toString())
                .tag("understanding_lab.memory_authority", "DEFERRED"),
        )
    }

    private fun unresolvedNlu(): NluOutput = NluOutput(
        dialogueAct = "UNKNOWN",
        predicate = "speech.unresolved",
        polarity = "UNKNOWN",
        temporalRelation = "UNKNOWN",
        subjectReferent = "UNKNOWN",
        targetReferent = "UNKNOWN",
        ownerReferent = "UNKNOWN",
        perspectiveReferent = "UNKNOWN",
        confidence = mapOf("overall" to 0.0),
        spans = emptyMap(),
        sourceType = "UNRESOLVED",
        worldTruth = false,
    )

    private fun semanticSummary(nlu: NluOutput, objectValue: String?): String {
        val objectPart = objectValue?.takeIf { it.isNotBlank() }?.let { ": $it" }.orEmpty()
        val polarityPart = when (nlu.polarity) {
            "NEGATIVE" -> " con negazione/rifiuto"
            "POSITIVE" -> " in forma positiva"
            else -> " con polarità incerta"
        }
        return when (nlu.dialogueAct) {
            "QUESTION" -> "L'utente sta facendo una domanda; non trattarla come fatto stabile."
            "REQUEST" -> "L'utente sta facendo una richiesta$objectPart."
            "CORRECT" -> "L'utente sta correggendo un'informazione$objectPart."
            "HYPOTHESIS" -> "L'utente sta formulando un'ipotesi$objectPart."
            else -> "L'utente esprime ${nlu.predicate}$objectPart$polarityPart."
        }
    }

    private fun sourceType(nlu: NluOutput): String = when (nlu.dialogueAct) {
        "QUESTION", "REQUEST" -> "TURN_INTENT"
        "HYPOTHESIS" -> "HYPOTHESIS"
        else -> "USER_ASSERTION"
    }

    /**
     * Compatibility fallback only until the NLU contract exposes the complete
     * adult/intimacy semantic marker directly. This marker never censors and is
     * not a persistence gate by itself.
     */
    private fun isAdultOrIntimacy(nlu: NluOutput, objectValue: String?): Boolean {
        if (nlu.predicate in setOf("consent.grant", "consent.refuse")) return true
        val text = objectValue?.lowercase().orEmpty()
        return listOf("intimo", "intimacy", "consenso", "limite").any { it in text }
    }

    private fun resolveReferent(referent: String, speaker: String, observer: String): String =
        resolveOptionalReferent(referent, speaker, observer) ?: speaker

    private fun resolveOptionalReferent(referent: String, speaker: String, observer: String): String? = when (referent) {
        "SPEAKER" -> speaker
        "OBSERVER" -> observer
        "SELF" -> "SELF"
        "SUBJECT" -> null
        "NONE" -> null
        "UNKNOWN" -> null
        else -> referent
    }

    private fun List<Int>?.toTextSpan(): TextSpan? =
        if (this != null && size == 2 && this[0] >= 0 && this[1] >= this[0]) TextSpan(this[0], this[1]) else null

    private fun String.sliceOrNull(span: TextSpan?): String? =
        span?.takeIf { it.start >= 0 && it.end <= length && it.end >= it.start }
            ?.let { substring(it.start, it.end).trim() }
            ?.takeIf { it.isNotBlank() }
}

/** Bridge implemented by the real Python/ONNX runtime or by an Android ONNX wrapper. */
interface MatrixNluRuntimeBridge {
    fun interpret(request: MatrixNluRequest): MatrixNluInterpretation
}

data class MatrixNluRequest(
    val turnId: String,
    val language: String,
    val text: String,
    val speakerId: String,
    val observerId: String,
    val knownEntities: Map<String, String> = emptyMap(),
    val recentEntityRefs: List<String> = emptyList(),
)

data class MatrixNluInterpretation(
    val engine: String,
    val status: String,
    val claims: List<MatrixNluClaim>,
    val diagnostics: List<String> = emptyList(),
)

data class MatrixNluClaim(
    val dialogueAct: String,
    val predicate: String,
    val polarity: String,
    val temporalRelation: String,
    val subjectReferent: String,
    val targetReferent: String,
    val ownerReferent: String,
    val perspectiveReferent: String,
    val confidence: Double,
    val confidenceByHead: Map<String, Double> = emptyMap(),
    val sourceSpan: List<Int>? = null,
    val subjectSpan: List<Int>? = null,
    val objectSpan: List<Int>? = null,
    val negationSpan: List<Int>? = null,
    val temporalSpan: List<Int>? = null,
    val subject: String? = null,
    val target: String? = null,
    val owner: String? = null,
    val perspective: String? = null,
    val objectValue: String? = null,
    val sourceType: String? = null,
    val worldTruth: Boolean = false,
)
