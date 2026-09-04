package matrix.assembling.adapters

import matrix.assembling.AffectivePort
import matrix.assembling.AffectiveState
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixTurnFrame

/**
 * Adapter over the Matrix Affective lab prototype.
 *
 * RelationshipState remains canonically external to the affective system.
 * Persistent affect is not a replacement for relationship state and this
 * adapter must not create a competing relationship authority.
 */
class AffectiveLabAdapter(
    private val runtime: AffectiveRuntimeBridge,
) : AffectivePort {

    override fun update(turn: MatrixTurnFrame): MatrixTurnFrame {
        val semantic = turn.requireSemantic()
        val memory = turn.memoryResult
        // Durable-memory admission is the persistence authority. Understanding
        // metadata is not an independent second gate for persistent affect.
        val persistentAllowed = memory?.stableWrite == true
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
        val persistenceViolation = output.persistentDeltaApplied && !persistentAllowed
        val persistentApplied = output.persistentDeltaApplied && persistentAllowed
        val state = AffectiveState(
            // RelationshipState is externally owned. Runtime-provided
            // relationship summaries are compatibility data and are never
            // accepted as canonical relationship authority here.
            relationshipSummary = "RelationshipState esterno: nessuna modifica applicata dall'Affective Engine.",
            affectiveSummary = output.affectiveSummary ?: affectiveSummary(output),
            persistentDeltaAllowed = persistentApplied,
        )
        var trace = turn.diagnostics
            .affective(
                DiagnosticSnapshot(
                    module = "AFFECTIVE",
                    input = "predicate=${semantic.predicate}; polarity=${semantic.polarity}; persistentAllowed=$persistentAllowed",
                    output = "impulse=${impulse?.emotionType ?: "none"}; persistentApplied=$persistentApplied",
                    decision = if (persistentApplied) "PERSISTENT_AND_TRANSIENT" else "TRANSIENT_ONLY",
                    status = if (persistenceViolation) "VIOLATION_BLOCKED" else "PASS",
                    reasonCodes = listOf(
                        if (persistentAllowed) "PERSISTENCE_AUTHORIZED" else "PERSISTENCE_NOT_AUTHORIZED",
                        if (persistenceViolation) "PERSISTENCE_ATTEMPT_BLOCKED" else "AFFECTIVE_OUTPUT_ACCEPTED",
                        "RELATIONSHIP_OWNER_EXTERNAL",
                    ),
                    confidence = mapOf("overall" to semantic.confidence.getOrDefault("overall", 0.0)),
                    metadata = mapOf(
                        "relationshipOwner" to "EXTERNAL",
                        "persistentAllowed" to persistentAllowed.toString(),
                        "persistentApplied" to persistentApplied.toString(),
                    ),
                )
            )
            .reason(if (persistentApplied) "AFFECTIVE_PERSISTENCE_APPLIED" else "AFFECTIVE_TRANSIENT_ONLY")
            .add("affective_lab.updated")
            .tag("affective_lab.impulse", impulse?.emotionType ?: "none")
            .tag("affective_lab.persistent_delta", persistentApplied.toString())
            .tag("affective_lab.persistence_violation", if (persistenceViolation) "BLOCKED" else "NONE")
            .tag("affective_lab.relationship_owner", "EXTERNAL")
        if (persistenceViolation) {
            trace = trace.diverge("AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION")
        }
        return turn.copy(
            affectiveState = state,
            diagnostics = trace,
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
    /** Compatibility-only field. AffectiveLabAdapter never treats it as canonical RelationshipState. */
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
