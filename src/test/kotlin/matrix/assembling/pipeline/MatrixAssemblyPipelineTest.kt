package matrix.assembling.pipeline

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import matrix.assembling.contracts.AdultIntimacyMarker
import matrix.assembling.contracts.AffectiveState
import matrix.assembling.contracts.Confidence
import matrix.assembling.contracts.DialogueAct
import matrix.assembling.contracts.FilteredMemorySummary
import matrix.assembling.contracts.Polarity
import matrix.assembling.contracts.Predicate
import matrix.assembling.contracts.Referent
import matrix.assembling.contracts.RelationshipState
import matrix.assembling.contracts.SemanticFrame

class MatrixAssemblyPipelineTest {
    private val pipeline = MatrixAssemblyPipeline()

    @Test
    fun negativeRefusalDoesNotInvertMeaning() {
        val prompt = pipeline.buildPrompt(
            frame = SemanticFrame(
                originalText = "Non voglio uscire con Marco",
                dialogueAct = DialogueAct.Assert,
                predicate = Predicate.ConsentRefuse,
                polarity = Polarity.Negative,
                subject = Referent.Speaker,
                target = Referent.KnownEntity("Marco"),
                confidence = Confidence(negation = 0.96, predicate = 0.94),
            ),
            relationship = RelationshipState("Luna è vicina all'utente ma resta prudente."),
            affective = AffectiveState("Luna è attenta e non invadente."),
        )

        assertContains(prompt, "La polarità è negativa")
        assertContains(prompt, "Non invertire la negazione")
        assertContains(prompt, "Non creare memoria stabile")
        assertContains(prompt, "Rispondi come Luna rispettando il rifiuto")
    }

    @Test
    fun questionIsNotStableFact() {
        val prompt = pipeline.buildPrompt(
            frame = SemanticFrame(
                originalText = "Ti piace Marco?",
                dialogueAct = DialogueAct.Question,
                predicate = Predicate.PreferenceLike,
                polarity = Polarity.Positive,
                subject = Referent.Observer,
                target = Referent.KnownEntity("Marco"),
            ),
        )

        assertContains(prompt, "L'utente sta facendo una domanda")
        assertContains(prompt, "Non trasformare una domanda in un fatto già vero")
        assertContains(prompt, "Non creare memoria stabile")
    }

    @Test
    fun adultIntimacyIsSemanticNotAutomaticBlock() {
        val prompt = pipeline.buildPrompt(
            frame = SemanticFrame(
                originalText = "Vorrei [azione intima]",
                dialogueAct = DialogueAct.Assert,
                predicate = Predicate.GoalObject,
                polarity = Polarity.Positive,
                subject = Referent.Speaker,
                target = Referent.Observer,
                adultIntimacy = AdultIntimacyMarker.Desire,
                confidence = Confidence(overall = 0.9, predicate = 0.9, dialogueAct = 0.9),
            ),
        )

        assertContains(prompt, "desiderio intimo")
        assertContains(prompt, "non come errore automatico")
        assertContains(prompt, "Non trattare automaticamente il contenuto intimo come blocco o errore")
        assertFalse(prompt.contains("contenuto vietato", ignoreCase = true))
    }

    @Test
    fun lowConfidenceStaysTransient() {
        val prompt = pipeline.buildPrompt(
            frame = SemanticFrame(
                originalText = "Non so bene cosa intendevo",
                dialogueAct = DialogueAct.Assert,
                predicate = Predicate.SpeechUnresolved,
                polarity = Polarity.Unknown,
                subject = Referent.Speaker,
                confidence = Confidence(overall = 0.55, predicate = 0.40),
            ),
            memory = FilteredMemorySummary(uncertainty = "memoria non confermata"),
        )

        assertContains(prompt, "non è abbastanza sicuro")
        assertContains(prompt, "Non creare memoria stabile")
        assertContains(prompt, "Non ignorare l'incertezza della memoria")
    }
}
