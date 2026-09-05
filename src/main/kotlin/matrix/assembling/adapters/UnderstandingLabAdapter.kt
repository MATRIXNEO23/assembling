package matrix.assembling.adapters

import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.DiagnosticStage
import matrix.assembling.DiagnosticStatus
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
 * Understanding preserves semantic evidence and provenance but does not
 * authorize durable memory. Stable persistence belongs downstream.
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
            ),
        )
        val allClaims = interpretation.claims.map { it.toNluOutput() }
        val primary = allClaims.firstOrNull() ?: unresolvedNlu()
        val reasonCodes = if (allClaims.isEmpty()) {
            listOf("NLU_NO_CLAIMS")
        } else {
            buildList {
                add("NLU_OUTPUT_ACCEPTED")
                if (allClaims.size > 1) add("MULTI_CLAIM_OUTPUT_PRESERVED")
            }
        }
        var diagnostics = turn.diagnostics
            .record(
                DiagnosticStage.OBSERVATION,
                DiagnosticSnapshot(
                    module = "MATRIX_NLU",
                    status = if (allClaims.isEmpty()) DiagnosticStatus.HOLD else DiagnosticStatus.PASS,
                    input = mapOf(
                        "locale" to turn.input.locale,
                        "textLength" to turn.input.text.length.toString(),
                    ),
                    output = mapOf(
                        "engine" to interpretation.engine,
                        "runtimeStatus" to interpretation.status,
                        "claimCount" to allClaims.size.toString(),
                    ),
                    decision = if (allClaims.isEmpty()) "UNRESOLVED" else "ACCEPT_OUTPUT",
                    reasonCodes = reasonCodes,
                ),
            )
            .add("understanding_lab.nlu.analyzed")
            .tag("understanding_lab.status", interpretation.status)
            .tag("understanding_lab.engine", interpretation.engine)
            .tag("understanding_lab.claim_count", allClaims.size.toString())
        if (allClaims.isEmpty()) diagnostics = diagnostics.diverge("UNDERSTANDING.NO_CLAIMS")

        return turn.copy(
            nlu = primary,
            nluClaims = allClaims,
            diagnostics = diagnostics,
        )
    }

    override fun understand(turn: MatrixTurnFrame): MatrixTurnFrame {
        val claimsToMap = turn.nluClaims.ifEmpty { listOf(turn.requireNlu()) }
        val typedClaims = claimsToMap.mapIndexed { index, claim -> claim.toTypedClaim(turn, index) }
        val primaryNlu = claimsToMap.first()
        val primaryClaim = typedClaims.first()
        val semantic = SemanticFrame(
            originalText = turn.input.text,
            semanticSummary = semanticSummary(primaryNlu, primaryClaim.objectValue),
            dialogueAct = primaryNlu.dialogueAct,
            predicate = primaryNlu.predicate,
            polarity = primaryNlu.polarity,
            temporalRelation = primaryNlu.temporalRelation,
            subject = primaryClaim.subject,
            target = primaryClaim.target,
            owner = primaryClaim.ownerId,
            confidence = primaryNlu.confidence,
            adultOrIntimacy = isAdultOrIntimacy(primaryNlu, primaryClaim.objectValue),
            // Compatibility field only. Understanding never authorizes durable memory.
            stableMemoryAllowed = false,
        )
        val unresolvedSubject = typedClaims.any { it.subject == UNKNOWN_REFERENT }
        val unresolvedOwner = typedClaims.any { it.ownerId == null }
        val reasonCodes = buildList {
            add("SEMANTIC_EVIDENCE_PRESERVED")
            add("MEMORY_AUTHORITY_DEFERRED")
            if (typedClaims.size > 1) add("MULTI_CLAIM_PRESERVED_TRANSIENT")
            if (unresolvedSubject) add("SUBJECT_UNRESOLVED")
            if (unresolvedOwner) add("OWNER_UNRESOLVED")
        }
        var diagnostics = turn.diagnostics
            .record(
                DiagnosticStage.UNDERSTANDING,
                DiagnosticSnapshot(
                    module = "UNDERSTANDING",
                    status = if (unresolvedSubject || unresolvedOwner || typedClaims.size > 1) {
                        DiagnosticStatus.HOLD
                    } else {
                        DiagnosticStatus.PASS
                    },
                    input = mapOf("nluClaimCount" to claimsToMap.size.toString()),
                    output = mapOf(
                        "typedClaimCount" to typedClaims.size.toString(),
                        "primarySubject" to primaryClaim.subject,
                        "primaryOwner" to (primaryClaim.ownerId ?: "UNRESOLVED"),
                        "primaryPredicate" to primaryClaim.predicate,
                        "primaryPolarity" to primaryClaim.polarity,
                    ),
                    decision = if (typedClaims.size > 1) "PRESERVE_ALL_PRIMARY_VIEW_ONLY" else "BUILD_SEMANTIC_FRAME",
                    reasonCodes = reasonCodes,
                ),
            )
            .add("understanding_lab.semantic_frame.built")
            .tag("understanding_lab.source_type", primaryClaim.sourceType)
            .tag("understanding_lab.world_truth_observed", primaryClaim.worldTruth.toString())
            .tag("understanding_lab.memory_authority", "DEFERRED")
            .tag("understanding_lab.multi_claim_mode", if (typedClaims.size > 1) "ALL_PRESERVED_PRIMARY_SEMANTIC" else "SINGLE")
        if (unresolvedSubject) diagnostics = diagnostics.diverge("UNDERSTANDING.UNRESOLVED_SUBJECT")

        return turn.copy(
            semantic = semantic,
            typedClaims = typedClaims,
            diagnostics = diagnostics,
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
        adultOrIntimacy = adultOrIntimacy,
    )

    private fun NluOutput.toTypedClaim(turn: MatrixTurnFrame, index: Int): TypedClaim {
        val objectValue = objectValue ?: turn.input.text.sliceOrNull(spans["object"])
        val subject = resolvedSubject
            ?: resolveOptionalReferent(subjectReferent, turn.input.speakerId, turn.input.observerId, null)
            ?: UNKNOWN_REFERENT
        val target = resolvedTarget
            ?: resolveOptionalReferent(targetReferent, turn.input.speakerId, turn.input.observerId, subject)
        val owner = resolvedOwner
            ?: resolveOptionalReferent(ownerReferent, turn.input.speakerId, turn.input.observerId, subject)
        val perspective = resolvedPerspective
            ?: resolveOptionalReferent(perspectiveReferent, turn.input.speakerId, turn.input.observerId, subject)
        return TypedClaim(
            claimId = "${turn.turnId}:claim:$index",
            ownerId = owner,
            subject = subject,
            predicate = predicate,
            objectValue = objectValue,
            target = target,
            polarity = polarity,
            temporalRelation = temporalRelation,
            sourceType = sourceType ?: inferSourceType(this),
            confidence = confidence,
            spans = spans,
            perspective = perspective,
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

    private fun inferSourceType(nlu: NluOutput): String = when (nlu.dialogueAct) {
        "QUESTION", "REQUEST" -> "TURN_INTENT"
        "HYPOTHESIS" -> "HYPOTHESIS"
        else -> "USER_ASSERTION"
    }

    /** Compatibility fallback until the complete NLU marker is always emitted. */
    private fun isAdultOrIntimacy(nlu: NluOutput, objectValue: String?): Boolean {
        nlu.adultOrIntimacy?.let { return it }
        if (nlu.predicate in setOf("consent.grant", "consent.refuse")) return true
        val text = objectValue?.lowercase().orEmpty()
        return listOf("intimo", "intimacy", "consenso", "limite").any { it in text }
    }

    private fun resolveOptionalReferent(
        referent: String,
        speaker: String,
        observer: String,
        subject: String?,
    ): String? = when (referent) {
        "SPEAKER" -> speaker
        "OBSERVER" -> observer
        "SELF" -> "SELF"
        "SUBJECT" -> subject?.takeUnless { it == UNKNOWN_REFERENT }
        "NONE", "UNKNOWN" -> null
        else -> referent
    }

    private fun List<Int>?.toTextSpan(): TextSpan? =
        if (this != null && size == 2 && this[0] >= 0 && this[1] >= this[0]) TextSpan(this[0], this[1]) else null

    private fun String.sliceOrNull(span: TextSpan?): String? =
        span?.takeIf { it.start >= 0 && it.end <= length && it.end >= it.start }
            ?.let { substring(it.start, it.end).trim() }
            ?.takeIf { it.isNotBlank() }

    private companion object {
        const val UNKNOWN_REFERENT = "UNKNOWN"
    }
}

/** Bridge implemented by the real Python/ONNX runtime or Android wrapper. */
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
    val adultOrIntimacy: Boolean? = null,
)
