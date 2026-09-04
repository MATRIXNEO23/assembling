package matrix.assembling.prompt

import matrix.assembling.contracts.AdultIntimacyMarker
import matrix.assembling.contracts.AffectiveState
import matrix.assembling.contracts.CoherenceDecision
import matrix.assembling.contracts.CoherenceStatus
import matrix.assembling.contracts.DialogueAct
import matrix.assembling.contracts.FilteredMemorySummary
import matrix.assembling.contracts.Polarity
import matrix.assembling.contracts.Predicate
import matrix.assembling.contracts.PromptDirective
import matrix.assembling.contracts.Referent
import matrix.assembling.contracts.RelationshipState
import matrix.assembling.contracts.SemanticFrame
import matrix.assembling.contracts.TemporalRelation

/**
 * Converts internal semantic classes into short natural-language instructions
 * that a small/medium GGUF can follow more reliably than raw numeric fields.
 */
class SemanticFrameToPrompt {
    fun toDirective(
        frame: SemanticFrame,
        coherence: CoherenceDecision,
        relationship: RelationshipState? = null,
        affective: AffectiveState? = null,
        memory: FilteredMemorySummary = FilteredMemorySummary(),
    ): PromptDirective {
        val meaning = buildMeaning(frame, coherence)
        val instruction = buildInstruction(frame, coherence)
        val limits = buildHardLimits(frame, coherence, memory)
        return PromptDirective(
            originalUserText = frame.originalText,
            systemMeaning = meaning,
            relationshipSummary = relationship?.summary,
            affectiveSummary = affective?.summary,
            memoryLines = memory.lines,
            instruction = instruction,
            hardLimits = limits,
        )
    }

    fun toPrompt(directive: PromptDirective): String = buildString {
        appendLine("SEI LUNA.")
        appendLine()
        appendLine("UTENTE:")
        appendLine(quote(directive.originalUserText))
        appendLine()
        appendLine("SIGNIFICATO DECISO DAL SISTEMA:")
        appendLine(directive.systemMeaning)
        if (!directive.relationshipSummary.isNullOrBlank()) {
            appendLine()
            appendLine("STATO RELAZIONE:")
            appendLine(directive.relationshipSummary)
        }
        if (!directive.affectiveSummary.isNullOrBlank()) {
            appendLine()
            appendLine("STATO EMOTIVO DI LUNA:")
            appendLine(directive.affectiveSummary)
        }
        if (directive.memoryLines.isNotEmpty()) {
            appendLine()
            appendLine("MEMORIA RILEVANTE FILTRATA:")
            directive.memoryLines.forEach { appendLine("- $it") }
        }
        appendLine()
        appendLine("ISTRUZIONE:")
        appendLine(directive.instruction)
        if (directive.hardLimits.isNotEmpty()) {
            appendLine()
            appendLine("NON FARE:")
            directive.hardLimits.forEach { appendLine("- $it") }
        }
        appendLine()
        append("RISPOSTA DI LUNA:")
    }

    fun toPrompt(
        frame: SemanticFrame,
        coherence: CoherenceDecision,
        relationship: RelationshipState? = null,
        affective: AffectiveState? = null,
        memory: FilteredMemorySummary = FilteredMemorySummary(),
    ): String = toPrompt(toDirective(frame, coherence, relationship, affective, memory))

    private fun buildMeaning(frame: SemanticFrame, coherence: CoherenceDecision): String {
        val parts = mutableListOf<String>()
        parts += dialogueActText(frame)
        parts += predicateText(frame)
        parts += polarityText(frame)
        parts += temporalText(frame)
        parts += referentText(frame)
        adultText(frame)?.let { parts += it }
        if (coherence.status != CoherenceStatus.SafeToUseForReply) {
            parts += "Il significato non è abbastanza sicuro per creare memoria stabile: ${coherence.reason}."
        }
        return parts.filter { it.isNotBlank() }.joinToString(" ")
    }

    private fun buildInstruction(frame: SemanticFrame, coherence: CoherenceDecision): String {
        if (coherence.status == CoherenceStatus.LowConfidence ||
            coherence.status == CoherenceStatus.TransientOnly ||
            coherence.status == CoherenceStatus.QuestionOnly
        ) {
            return "Rispondi come Luna usando cautela. Non trattare questa frase come fatto stabile."
        }
        return when (frame.predicate) {
            Predicate.ConsentRefuse -> "Rispondi come Luna rispettando il rifiuto o il limite dell'utente."
            Predicate.ConsentGrant -> "Rispondi come Luna tenendo conto del consenso espresso, senza dare per scontato altro."
            Predicate.GoalObject -> if (frame.dialogueAct == DialogueAct.Request) {
                "Rispondi come Luna alla richiesta dell'utente, valutando relazione, contesto e disponibilità."
            } else {
                "Rispondi come Luna riconoscendo il desiderio o obiettivo espresso dall'utente."
            }
            Predicate.PreferenceLike -> "Rispondi come Luna tenendo conto della preferenza espressa."
            Predicate.SpeechUnresolved -> "Rispondi come Luna chiedendo chiarimento o restando generica, senza inventare significati."
            else -> "Rispondi come Luna in modo coerente con il significato, la relazione e il contesto."
        }
    }

    private fun buildHardLimits(
        frame: SemanticFrame,
        coherence: CoherenceDecision,
        memory: FilteredMemorySummary,
    ): List<String> {
        val limits = mutableListOf<String>()
        if (frame.polarity == Polarity.Negative) {
            limits += "Non invertire la negazione."
        }
        if (!coherence.stableMemoryAllowed) {
            limits += "Non creare memoria stabile da questa frase."
        }
        if (!coherence.persistentAffectAllowed) {
            limits += "Non produrre cambi emotivi persistenti basati solo su questa frase."
        }
        if (frame.dialogueAct == DialogueAct.Question) {
            limits += "Non trasformare una domanda in un fatto già vero."
        }
        if (frame.adultIntimacy != AdultIntimacyMarker.None) {
            limits += "Non trattare automaticamente il contenuto intimo come blocco o errore."
        }
        if (!memory.uncertainty.isNullOrBlank()) {
            limits += "Non ignorare l'incertezza della memoria: ${memory.uncertainty}."
        }
        return limits
    }

    private fun dialogueActText(frame: SemanticFrame): String = when (frame.dialogueAct) {
        DialogueAct.Assert -> "L'utente sta affermando qualcosa."
        DialogueAct.Correct -> "L'utente sta correggendo qualcosa."
        DialogueAct.Question -> "L'utente sta facendo una domanda."
        DialogueAct.Request -> "L'utente sta facendo una richiesta."
        DialogueAct.Hypothesis -> "L'utente sta esprimendo un'ipotesi."
        DialogueAct.Unknown -> "Il tipo di frase è incerto."
    }

    private fun predicateText(frame: SemanticFrame): String = when (frame.predicate) {
        Predicate.IdentityName -> "La frase riguarda un nome o identità."
        Predicate.IdentityAge -> "La frase riguarda un'età."
        Predicate.ResidencePlace -> "La frase riguarda un luogo in cui qualcuno vive."
        Predicate.PresenceReported -> "La frase riguarda una presenza riferita."
        Predicate.PreferenceLike -> "La frase riguarda una preferenza o gradimento."
        Predicate.WorkRole -> "La frase riguarda un lavoro o ruolo."
        Predicate.PossessionHas -> "La frase riguarda possesso o appartenenza."
        Predicate.GoalObject -> "La frase riguarda un desiderio, obiettivo o azione richiesta."
        Predicate.AttributeIs -> "La frase riguarda un attributo o stato personale."
        Predicate.ConsentGrant -> "La frase esprime consenso."
        Predicate.ConsentRefuse -> "La frase esprime rifiuto o limite."
        Predicate.SpeechUnresolved -> "Il contenuto preciso non è risolto."
    }

    private fun polarityText(frame: SemanticFrame): String = when (frame.polarity) {
        Polarity.Positive -> "La polarità è positiva o affermativa."
        Polarity.Negative -> "La polarità è negativa: la negazione è importante."
        Polarity.Unknown -> "La polarità è incerta."
    }

    private fun temporalText(frame: SemanticFrame): String = when (frame.temporalRelation) {
        TemporalRelation.Atemporal -> "Non emerge un tempo specifico."
        TemporalRelation.Current -> "Il riferimento principale è al presente."
        TemporalRelation.Past -> "Il riferimento principale è al passato."
        TemporalRelation.Future -> "Il riferimento principale è al futuro."
        TemporalRelation.Unknown -> "Il riferimento temporale è incerto."
    }

    private fun referentText(frame: SemanticFrame): String {
        val subject = referentName(frame.subject)
        val target = referentName(frame.target)
        return if (target == "nessun target") {
            "Il soggetto principale è $subject."
        } else {
            "Il soggetto principale è $subject; il target o oggetto rilevante è $target."
        }
    }

    private fun adultText(frame: SemanticFrame): String? = when (frame.adultIntimacy) {
        AdultIntimacyMarker.None -> null
        AdultIntimacyMarker.Desire -> "La frase contiene desiderio intimo; va trattata come semantica relazionale, non come errore automatico."
        AdultIntimacyMarker.Request -> "La frase contiene una richiesta intima; va interpretata tramite contesto e consenso, non censurata automaticamente."
        AdultIntimacyMarker.Consent -> "La frase contiene consenso intimo; non estenderlo oltre ciò che è detto."
        AdultIntimacyMarker.RefusalOrBoundary -> "La frase contiene rifiuto o limite intimo; il limite va rispettato."
        AdultIntimacyMarker.UnresolvedIntimateTerm -> "La frase contiene termini intimi non risolti; non inventare significati."
    }

    private fun referentName(referent: Referent): String = when (referent) {
        Referent.Speaker -> "l'utente"
        Referent.Observer -> "Luna"
        is Referent.KnownEntity -> referent.label
        is Referent.RecentEntity -> referent.label
        Referent.Self -> "sé stesso"
        Referent.None -> "nessun target"
        Referent.Unknown -> "un referente incerto"
    }

    private fun quote(value: String): String = "\"" + value.replace("\"", "\\\"") + "\""
}
