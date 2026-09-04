package matrix.assembling

/**
 * Deterministic translator from internal semantic state to GGUF-readable prompt.
 *
 * The GGUF should not infer truth, memory policy or consent from raw numbers.
 * This builder turns module decisions into short natural-language instructions.
 */
class SemanticFrameToPrompt : SemanticFrameToPromptPort {
    override fun buildPrompt(
        input: UserMessage,
        frame: SemanticFrame,
        coherenceDecision: CoherenceDecision,
        authorityDecision: AuthorityDecision,
        memoryResult: MemoryAdmissionResult,
        affectiveState: AffectiveState,
    ): GgufPrompt {
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
            appendLine("ISTRUZIONE:")
            appendLine(buildResponseInstruction(frame, coherenceDecision, memoryResult, affectiveState))
            appendLine("Non invertire negazioni, rifiuti, consenso, tempo o referenti già decisi dal sistema.")
            appendLine("Non inventare nuove memorie o fatti stabili.")
            appendLine()
            appendLine("RISPOSTA DI LUNA:")
        }
        return GgufPrompt(prompt.trimEnd())
    }

    private fun buildMeaningLine(frame: SemanticFrame, coherence: CoherenceDecision): String {
        val certainty = when (coherence) {
            CoherenceDecision.SAFE_TO_ADMIT -> "Il significato è considerato stabile."
            CoherenceDecision.SAFE_TRANSIENT_ONLY -> "Il significato è utilizzabile solo nel turno corrente."
            CoherenceDecision.LOW_CONFIDENCE_HOLD -> "Il significato è incerto: rispondi con cautela."
            CoherenceDecision.REPORT_ONLY -> "La frase è un report o riferimento indiretto, non una verità diretta."
            CoherenceDecision.QUESTION_ONLY -> "La frase è una domanda o richiesta, non un fatto stabile."
            CoherenceDecision.CONFLICT_REQUIRES_REVIEW -> "La frase può contraddire informazioni precedenti: non decidere tu la verità."
            CoherenceDecision.REJECTED_UNSAFE -> "Il sistema non considera affidabile l'interpretazione: rispondi senza fissare fatti."
        }
        return "$certainty ${frame.semanticSummary}".trim()
    }

    private fun buildMemoryLine(memory: MemoryAdmissionResult): String = when {
        memory.stableWrite -> "Il sistema ha autorizzato una memoria stabile: ${memory.reason}"
        memory.status == "PROVISIONAL_CLAIM" -> "Solo memoria provvisoria: ${memory.reason}"
        memory.status == "REJECTED" -> "Non salvare memoria da questa frase: ${memory.reason}"
        else -> "Non creare nuove memorie se non già autorizzate dal sistema: ${memory.reason}"
    }

    private fun buildAuthorityLine(authority: AuthorityDecision): String = when {
        !authority.ownerResolved -> "Owner/fonte non risolti: non trattare il contenuto come definitivo."
        authority.conflictStatus != "NONE" -> "Possibile conflitto (${authority.conflictStatus}): non risolverlo nel testo come fatto certo."
        authority.sourceType == "THIRD_PARTY_REPORT" -> "Fonte indiretta: presenta il contenuto come riportato, non confermato."
        else -> "Fonte e owner risultano coerenti secondo il sistema."
    }

    private fun buildAdultLine(frame: SemanticFrame): String? {
        if (!frame.adultOrIntimacy) return null
        return "La frase contiene contenuto intimo/adulto: non trattarlo come errore automatico; usa consenso, limite e contesto relazione."
    }

    private fun buildResponseInstruction(
        frame: SemanticFrame,
        coherence: CoherenceDecision,
        memory: MemoryAdmissionResult,
        affective: AffectiveState,
    ): String {
        if (frame.predicate == "consent.refuse" || frame.polarity == "NEGATIVE") {
            return "Rispondi rispettando il rifiuto o la negazione dell'utente."
        }
        if (frame.predicate == "consent.grant") {
            return "Rispondi tenendo conto che l'utente sta dando consenso o disponibilità, senza forzare oltre il contesto."
        }
        if (frame.dialogueAct == "QUESTION") {
            return "Rispondi alla domanda senza trasformarla in memoria stabile."
        }
        if (frame.dialogueAct == "REQUEST") {
            return "Rispondi alla richiesta in modo coerente con relazione, stato emotivo e limiti del contesto."
        }
        if (coherence == CoherenceDecision.LOW_CONFIDENCE_HOLD) {
            return "Rispondi in modo naturale ma prudente: il significato non è abbastanza sicuro."
        }
        if (!memory.stableWrite && affective.persistentDeltaAllowed.not()) {
            return "Rispondi naturalmente senza modificare fatti stabili o relazione persistente."
        }
        return "Rispondi come Luna in modo coerente con significato, memoria e stato emotivo."
    }
}
