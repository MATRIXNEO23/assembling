package matrix.assembling

/**
 * Deterministic translator from resolved Matrix state to GGUF-readable prompt.
 *
 * This component is a realization boundary, not a behavior/decision engine.
 * Until DecisionSnapshot is wired, it may preserve semantic invariants but must
 * not silently choose relationship, memory, affective or behavioral policy.
 */
class SemanticFrameToPrompt : SemanticFrameToPromptPort {
    override fun buildPrompt(turn: MatrixTurnFrame): MatrixTurnFrame {
        val input = turn.input
        val frame = turn.requireSemantic()
        val coherenceDecision = turn.requireCoherence()
        val authorityDecision = turn.requireAuthority()
        val memoryResult = turn.requireMemory()
        val affectiveState = turn.requireAffective()

        val meaning = buildMeaningLine(frame, coherenceDecision)
        val memoryLine = buildMemoryLine(memoryResult)
        val authorityLine = buildAuthorityLine(authorityDecision)
        val adultLine = buildAdultLine(frame)

        val prompt = buildString {
            appendLine("Sei Luna.")
            appendLine()
            appendLine("UTENTE:")
            appendLine('"' + input.text + '"')
            appendLine()
            appendLine("SIGNIFICATO DECISO DAL SISTEMA:")
            appendLine(meaning)
            if (adultLine != null) appendLine(adultLine)
            appendLine()
            appendLine("MEMORIA:")
            appendLine(memoryLine)
            appendLine(authorityLine)
            appendLine()
            appendLine("STATO RELAZIONE:")
            appendLine(affectiveState.relationshipSummary)
            appendLine()
            appendLine("STATO EMOTIVO:")
            appendLine(affectiveState.affectiveSummary)
            appendLine()
            appendLine("ISTRUZIONE DI REALIZZAZIONE:")
            appendLine(buildRealizationInstruction(frame, coherenceDecision))
            appendLine("Non invertire negazioni, rifiuti, consenso, tempo o referenti già decisi dal sistema.")
            appendLine("Non inventare nuove memorie, fatti stabili, stati relazionali o decisioni persistenti.")
            appendLine()
            appendLine("RISPOSTA DI LUNA:")
        }
        return turn.copy(
            prompt = GgufPrompt(prompt.trimEnd()),
            diagnostics = turn.diagnostics
                .add("prompt.built")
                .tag("prompt.role", "REALIZATION_ONLY")
                .tag("prompt.decision_layer", "NON_CABLATO"),
        )
    }

    private fun buildMeaningLine(frame: SemanticFrame, coherence: CoherenceDecision): String {
        val certainty = when (coherence) {
            CoherenceDecision.SAFE_TO_ADMIT -> "Il significato è semanticamente stabile per il turno; la persistenza resta separata."
            CoherenceDecision.SAFE_TRANSIENT_ONLY -> "Il significato è utilizzabile solo nel turno corrente."
            CoherenceDecision.LOW_CONFIDENCE_HOLD -> "Il significato è incerto: non inventare ciò che non è stato risolto."
            CoherenceDecision.REPORT_ONLY -> "La frase è un report o riferimento indiretto, non una verità diretta."
            CoherenceDecision.QUESTION_ONLY -> "La frase è una domanda, non un fatto già vero."
            CoherenceDecision.CONFLICT_REQUIRES_REVIEW -> "La frase può contraddire informazioni precedenti: non decidere tu la verità."
            CoherenceDecision.REJECTED_UNSAFE -> "L'interpretazione non è sufficientemente affidabile: non fissare fatti."
        }
        return "$certainty ${frame.semanticSummary}".trim()
    }

    private fun buildMemoryLine(memory: MemoryAdmissionResult): String = when {
        memory.stableWrite -> "Il sistema segnala un risultato persistente: ${memory.reason}"
        memory.status == "PROVISIONAL_CLAIM" -> "Solo stato provvisorio del turno: ${memory.reason}"
        memory.status == "REJECTED" -> "Nessuna memoria stabile da questa frase: ${memory.reason}"
        else -> "Nessuna nuova memoria stabile disponibile: ${memory.reason}"
    }

    private fun buildAuthorityLine(authority: AuthorityDecision): String = when {
        !authority.ownerResolved -> "Owner/fonte non risolti: non trattare il contenuto come definitivo."
        authority.conflictStatus != "NONE" -> "Possibile conflitto (${authority.conflictStatus}): non risolverlo nel testo come fatto certo."
        authority.sourceType == "THIRD_PARTY_REPORT" -> "Fonte indiretta: presenta il contenuto come riportato, non confermato."
        else -> "Fonte e owner risultano coerenti secondo il sistema."
    }

    private fun buildAdultLine(frame: SemanticFrame): String? {
        if (!frame.adultOrIntimacy) return null
        return "La frase contiene semantica intima/adulta: non trattarla come errore automatico e non estendere consenso o limiti oltre ciò che è stato risolto."
    }

    /**
     * Only semantic realization constraints live here. Behavioral choice belongs
     * to the future Matrix DecisionSnapshot and must not be reconstructed here.
     */
    private fun buildRealizationInstruction(
        frame: SemanticFrame,
        coherence: CoherenceDecision,
    ): String = when {
        coherence == CoherenceDecision.LOW_CONFIDENCE_HOLD ->
            "Formula una risposta naturale che mantenga esplicita l'incertezza senza inventare significati."
        coherence == CoherenceDecision.REPORT_ONLY ->
            "Formula la risposta mantenendo il contenuto come informazione riportata, non confermata."
        frame.dialogueAct == "QUESTION" ->
            "Rispondi alla domanda senza trasformarla in un fatto già accaduto o confermato."
        frame.dialogueAct == "REQUEST" ->
            "Riconosci linguisticamente la richiesta senza trattarla come azione già decisa o avvenuta."
        frame.predicate == "consent.refuse" ->
            "Preserva esattamente il rifiuto o limite risolto dal sistema; non invertirlo o attenuarlo arbitrariamente."
        frame.predicate == "consent.grant" ->
            "Preserva esattamente il consenso espresso senza estenderlo oltre il contenuto risolto."
        frame.polarity == "NEGATIVE" ->
            "Preserva la negazione nel testo generato."
        else ->
            "Realizza linguisticamente il significato risolto senza aggiungere decisioni o fatti persistenti."
    }
}
