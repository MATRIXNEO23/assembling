package matrix.assembling.adapters

import matrix.assembling.DiagnosticSnapshot
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
            primary.toNluOutput()
        }
        val claims = interpretation.claims.mapIndexed { index, claim ->
            claim.toTypedClaim(turn, index)
        }
        var trace = turn.diagnostics
            .observe(
                DiagnosticSnapshot(
                    module = "NLU",
                    input = turn.input.text,
                    output = "engine=${interpretation.engine}; claims=${interpretation.claims.size}",
                    decision = interpretation.status,
                    status = if (primary == null) "HOLD" else "PASS",
                    reasonCodes = listOf(if (primary == null) "NLU_NO_CLAIM" else "NLU_OUTPUT_ACCEPTED"),
                    confidence = primary?.confidenceByHead?.plus("overall" to primary.confidence).orEmpty(),
                    metadata = mapOf(
                        "engine" to interpretation.engine,
                        "claimCount" to interpretation.claims.size.toString(),
                        "multiClaim" to (interpretation.claims.size > 1).toString(),
                    ),
                )
            )
            .reason(if (primary == null) "NLU_NO_CLAIM" else "NLU_OUTPUT_ACCEPTED")
            .add("understanding_lab.nlu.analyzed")
            .tag("understanding_lab.status", interpretation.status)
            .tag("understanding_lab.engine", interpretation.engine)
            .tag("understanding_lab.claim_count", interpretation.claims.size.toString())
            .tag("understanding_lab.multi_claim", (interpretation.claims.size > 1).toString())
        if (primary == null) {
            trace = trace.diverge("NLU.NO_CLAIM")
        }
        return turn.copy(
            nlu = nlu,
            typedClaims = claims,
            diagnostics = trace,
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
        val sourceType = nlu.sourceType ?: sourceType(nlu.dialogueAct)
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
        val primaryClaim = TypedClaim(
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
        val claims = if (turn.typedClaims.isNotEmpty()) turn.typedClaims else listOf(primaryClaim)
        val subjectUnresolved = subject == "UNKNOWN"
        var trace = turn.diagnostics
            .understood(
                DiagnosticSnapshot(
                    module = "UNDERSTANDING",
                    input = "dialogueAct=${nlu.dialogueAct}; predicate=${nlu.predicate}; polarity=${nlu.polarity}",
                    output = semantic.semanticSummary,
                    decision = "SEMANTIC_FRAME_BUILT",
                    status = if (subjectUnresolved) "HOLD" else "PASS",
                    reasonCodes = listOf(
                        "SEMANTIC_FRAME_BUILT",
                        if (subjectUnresolved) "SUBJECT_UNRESOLVED" else "SUBJECT_RESOLVED",
                        "MEMORY_AUTHORITY_DEFERRED",
                    ),
                    confidence = nlu.confidence,
                    metadata = mapOf(
                        "subject" to subject,
                        "target" to (target ?: "NONE"),
                        "owner" to (owner ?: "UNRESOLVED"),
                        "perspective" to (perspective ?: "UNRESOLVED"),
                        "sourceType" to sourceType,
                        "claimCount" to claims.size.toString(),
                    ),
                )
            )
            .reason("UNDERSTANDING_FRAME_BUILT")
            .add("understanding_lab.semantic_frame.built")
            .tag("understanding_lab.source_type", sourceType)
            .tag("understanding_lab.world_truth_observed", nlu.worldTruth.toString())
            .tag("understanding_lab.subject_resolution", if (subjectUnresolved) "UNRESOLVED" else "RESOLVED")
            .tag("understanding_lab.memory_authority", "DEFERRED")
        if (subjectUnresolved) {
            trace = trace.diverge("UNDERSTANDING.UNRESOLVED_SUBJECT")
        }
        return turn.copy(
            semantic = semantic,
            typedClaims = claims,
            diagnostics = trace,
        )
    }

    private fun MatrixNluClaim.toNluOutput(): NluOutput = NluOutput(
        dialogueAct = dialogueAct,
        predicate = predicate,
        polarity = polarity,
        temporalRelation = temporalRelation,
        subjectReferent = subjectReferent,
        targetReferent = targetReferent,
        ownerReferent = ownerReferent,
        perspectiveReferent = perspectiveReferent,
        confidence = confidenceByHead + ("overall" to confidence),
        spans = mapOf(
            "source" to sourceSpan.toTextSpan(),
            "subject" to subjectSpan.toTextSpan(),
            "object" to objectSpan.toTextSpan(),
            "negation" to negationSpan.toTextSpan(),
            "temporal" to temporalSpan.toTextSpan(),
        ),
        resolvedSubject = subject,
        resolvedTarget = target,
        resolvedOwner = owner,
        resolvedPerspective = perspective,
        objectValue = objectValue,
        sourceType = sourceType,
        worldTruth = worldTruth,
    )

    private fun MatrixNluClaim.toTypedClaim(turn: MatrixTurnFrame, index: Int): TypedClaim {
        val spans = mapOf(
            "source" to sourceSpan.toTextSpan(),
            "subject" to subjectSpan.toTextSpan(),
            "object" to objectSpan.toTextSpan(),
            "negation" to negationSpan.toTextSpan(),
            "temporal" to temporalSpan.toTextSpan(),
        )
        val resolvedSubject = subject
            ?: resolveReferent(subjectReferent, turn.input.speakerId, turn.input.observerId)
        val resolvedTarget = target
            ?: resolveOptionalReferent(targetReferent, turn.input.speakerId, turn.input.observerId)
        val resolvedOwner = owner
            ?: resolveOptionalReferent(ownerReferent, turn.input.speakerId, turn.input.observerId)
        val resolvedPerspective = perspective
            ?: resolveOptionalReferent(perspectiveReferent, turn.input.speakerId, turn.input.observerId)
        val resolvedObject = objectValue ?: turn.input.text.sliceOrNull(spans["object"])
        return TypedClaim(
            claimId = "${turn.turnId}:claim:$index",
            ownerId = resolvedOwner,
            subject = resolvedSubject,
            predicate = predicate,
            objectValue = resolvedObject,
            target = resolvedTarget,
            polarity = polarity,
            temporalRelation = temporalRelation,
            sourceType = sourceType ?: sourceType(dialogueAct),
            confidence = confidenceByHead + ("overall" to confidence),
            spans = spans,
            perspective = resolvedPerspective,
            worldTruth = worldTruth,
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

    private fun sourceType(dialogueAct: String): String = when (dialogueAct) {
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
        resolveOptionalReferent(referent, speaker, observer) ?: "UNKNOWN"

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
