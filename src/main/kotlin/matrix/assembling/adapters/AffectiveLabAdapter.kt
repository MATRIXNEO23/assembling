package matrix.assembling.adapters

import matrix.assembling.AffectivePort
import matrix.assembling.AffectiveState
import matrix.assembling.MatrixTurnFrame

/**
 * Adapter over the Matrix Affective lab prototype.
 *
 * The prototype currently lives as Python code in vendor/matrix-affective-lab.
 * This adapter defines the stable Kotlin bridge that the Android app can later
 * implement with a local port, JNI, generated Kotlin, or a direct rewritten
 * Kotlin engine.
 */
class AffectiveLabAdapter(
    private val runtime: AffectiveRuntimeBridge,
) : AffectivePort {

    override fun update(turn: MatrixTurnFrame): MatrixTurnFrame {
        val semantic = turn.requireSemantic()
        val memory = turn.memoryResult
        val persistentAllowed = memory?.stableWrite == true && semantic.stableMemoryAllowed
        val impulse = mapImpulse(turn, persistentAllowed)
        val output = runtime.update(
            AffectiveRuntimeRequest(
                turnId = turn.turnId,
                speakerId = turn.input.speakerId,
                observerId = turn.input.observerId,
                predicate = semantic.predicate,
                polarity = semantic.polarity,
                dialogueAct = semantic.dialogueAct,
                confidence = semantic.confidence.getOrDefault("overall", 0.0),
                persistentAllowed = persistentAllowed,
                impulse = impulse,
            )
        )
        return turn.copy(
            affectiveState = AffectiveState(
                relationshipSummary = output.relationshipSummary ?: relationshipSummary(output, turn.input.speakerId),
                affectiveSummary = output.affectiveSummary ?: affectiveSummary(output),
                persistentDeltaAllowed = output.persistentDeltaApplied,
            ),
            diagnostics = turn.diagnostics
                .add("affective_lab.updated")
                .tag("affective_lab.impulse", impulse?.emotionType ?: "none")
                .tag("affective_lab.persistent_delta", output.persistentDeltaApplied.toString()),
        )
    }

    private fun mapImpulse(turn: MatrixTurnFrame, persistentAllowed: Boolean): AffectiveImpulse? {
        val semantic = turn.requireSemantic()
        val confidence = semantic.confidence.getOrDefault("overall", 0.0)
        if (confidence < 0.55) return null
        val emotionType = when {
            semantic.predicate == "preference.like" && semantic.polarity == "POSITIVE" -> "liking"
            semantic.predicate == "preference.like" && semantic.polarity == "NEGATIVE" -> "disliking"
            semantic.predicate == "consent.grant" -> "gratitude"
            semantic.predicate == "consent.refuse" -> null
            semantic.dialogueAct == "REQUEST" -> "hope"
            semantic.dialogueAct == "QUESTION" -> null
            semantic.polarity == "NEGATIVE" -> null
            else -> "satisfaction"
        } ?: return null
        return AffectiveImpulse(
            emotionType = emotionType,
            intensity = confidence.coerceIn(0.0, 1.0),
            causeId = turn.turnId,
            targetId = if (persistentAllowed) turn.input.speakerId else null,
            appraisalChannel = "matrix_understanding",
            persistentAllowed = persistentAllowed,
        )
    }

    private fun relationshipSummary(output: AffectiveRuntimeOutput, speakerId: String): String {
        val relation = output.persistentAffect[speakerId]
        if (relation == null) return "Relazione non aggiornata in modo persistente."
        return buildString {
            append("Fiducia ${relation.trust.format2()}, affetto ${relation.affection.format2()}, ")
            append("attaccamento ${relation.attachment.format2()}, rispetto ${relation.respect.format2()}.")
            if (relation.resentment > 0.15) append(" Presente risentimento da considerare.")
            if (relation.attraction > 0.15) append(" Presente attrazione da considerare.")
        }
    }

    private fun affectiveSummary(output: AffectiveRuntimeOutput): String {
        val active = output.emotions.entries
            .filter { it.value > 0.05 }
            .sortedByDescending { it.value }
            .joinToString { "${it.key} ${it.value.format2()}" }
        val emotionPart = if (active.isBlank()) "Nessuna emozione forte attiva." else "Emozioni attive: $active."
        return "$emotionPart Valenza ${output.valence.format2()}, arousal ${output.arousal.format2()}, mood ${output.moodValence.format2()}."
    }

    private fun Double.format2(): String = "%.2f".format(this)
}

/** Bridge implemented by the copied Python prototype, a local process, JNI, or a future Kotlin port. */
interface AffectiveRuntimeBridge {
    fun update(request: AffectiveRuntimeRequest): AffectiveRuntimeOutput
}

data class AffectiveRuntimeRequest(
    val turnId: String,
    val speakerId: String,
    val observerId: String,
    val predicate: String,
    val polarity: String,
    val dialogueAct: String,
    val confidence: Double,
    val persistentAllowed: Boolean,
    val impulse: AffectiveImpulse?,
)

data class AffectiveImpulse(
    val emotionType: String,
    val intensity: Double,
    val causeId: String,
    val targetId: String?,
    val appraisalChannel: String,
    val persistentAllowed: Boolean,
)

data class AffectiveRuntimeOutput(
    val emotions: Map<String, Double> = emptyMap(),
    val valence: Double = 0.0,
    val arousal: Double = 0.0,
    val dominance: Double = 0.0,
    val moodValence: Double = 0.0,
    val persistentAffect: Map<String, PersistentAffectSnapshot> = emptyMap(),
    val persistentDeltaApplied: Boolean = false,
    val relationshipSummary: String? = null,
    val affectiveSummary: String? = null,
)

data class PersistentAffectSnapshot(
    val trust: Double = 0.5,
    val attachment: Double = 0.0,
    val affection: Double = 0.0,
    val attraction: Double = 0.0,
    val resentment: Double = 0.0,
    val respect: Double = 0.5,
    val admiration: Double = 0.0,
    val aversion: Double = 0.0,
)
