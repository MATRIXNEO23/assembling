package matrix.assembling

/** Deterministic translator from Matrix decisions/evidence to a GGUF prompt. */
class SemanticFrameToPrompt : SemanticFrameToPromptPort {
    override fun buildPrompt(turn: MatrixTurnFrame): MatrixTurnFrame {
        val input = turn.input
        val frame = turn.requireSemantic()
        val coherenceDecision = turn.requireCoherence()
        val authorityDecision = turn.requireAuthority()
        val memoryResult = turn.requireMemory()
        val affectiveState = turn.requireAffective()

        val prompt = buildString {
            appendLine("Sei Luna.")
            appendLine()
            appendLine("UTENTE:")
            appendLine('"' + input.text + '"')
            appendLine()
            appendLine("SIGNIFICATO DECISO DAL SISTEMA:")
            appendLine(buildMeaningLine(frame, coherenceDecision))
            buildAdultLine(frame)?.let(::appendLine)
            appendLine()
            appendLine("MEMORIA / PREFLIGHT:")
            appendLine(buildMemoryLine(memoryResult))
            appendLine(buildAuthorityLine(authorityDecision))
            appendLine()
            appendLine("STATO RELAZIONE:")
            appendLine(affectiveState.relationshipSummary)
            appendLine()
            appendLine("STATO EMOTIVO:")
            appendLine(affectiveState.affectiveSummary)
            appendLine()
            appendLine("VINCOLI DI REALIZZAZIONE:")
            appendLine(buildResponseInstruction(frame, coherenceDecision))
            appendLine("Non invertire negazioni, rifiuti, consenso, tempo o referenti già risolti.")
            appendLine("Non inventare nuove memorie, fatti stabili o decisioni comportamentali non fornite da Matrix.")
            appendLine()
            appendLine("RISPOSTA DI LUNA:")
        }.trimEnd()
        val diagnostics = turn.diagnostics
            .record(
                DiagnosticStage.PROMPT,
                DiagnosticSnapshot(
                    module = "PROMPT_BUILDER",
                    status = DiagnosticStatus.PASS,
                    input = mapOf(
                        "coherence" to coherenceDecision.name,
                        "authorityAccepted" to authorityDecision.accepted.toString(),
                        "memoryStatus" to memoryResult.status,
                    ),
                    output = mapOf("promptLength" to prompt.length.toString()),
                    decision = "TRANSLATE_EVIDENCE",
                    reasonCodes = listOf("PROMPT_TRANSLATION_ONLY"),
                ),
            )
            .add("prompt.built")
        return turn.copy(prompt = GgufPrompt(prompt), diagnostics = diagnostics)
    }

    private fun buildMeaningLine(frame: SemanticFrame, coherence: CoherenceDecision): String {
        val certainty = when (coherence) {
            CoherenceDecision.SAFE_TO_ADMIT -> "Il significato è semanticamente stabile."
            CoherenceDecision.SAFE_TRANSIENT_ONLY -> "Il significato è utilizzabile solo nel turno corrente."
            CoherenceDecision.LOW_CONFIDENCE_HOLD -> "Il significato è incerto: non presentarlo come certo."
            CoherenceDecision.REPORT_ONLY -> "La frase è un report indiretto, non una verità diretta."
            CoherenceDecision.QUESTION_ONLY -> "La frase è una domanda, non un fatto stabile."
            CoherenceDecision.CONFLICT_REQUIRES_REVIEW -> "La frase può contraddire informazioni precedenti; non risolvere tu il conflitto."
            CoherenceDecision.REJECTED_UNSAFE -> "L'interpretazione non è affidabile; non fissare fatti."
        }
        return "$certainty ${frame.semanticSummary}".trim()
    }

    private fun buildMemoryLine(memory: MemoryAdmissionResult): String = when {
        memory.stableWrite -> "È presente un risultato persistente già autorizzato: ${memory.reason}"
        memory.status == "PROVISIONAL_CLAIM" -> "Solo preflight/proposta transitoria: ${memory.reason}"
        memory.status == "REJECTED" -> "Nessuna memoria: ${memory.reason}"
        else -> "Persistenza non eseguita: ${memory.reason}"
    }

    private fun buildAuthorityLine(authority: AuthorityDecision): String = when {
        !authority.ownerResolved -> "Owner/fonte non risolti: non trattare il contenuto come definitivo."
        authority.conflictStatus != "NONE" -> "Possibile conflitto (${authority.conflictStatus}): non risolverlo come fatto certo."
        authority.sourceType == "THIRD_PARTY_REPORT" -> "Fonte indiretta: presenta il contenuto come riportato, non confermato."
        authority.sourceType == "MULTI_CLAIM" -> "Sono presenti più claim: preservali senza fonderli o inventare collegamenti."
        else -> "Fonte e owner risultano coerenti secondo il sistema."
    }

    private fun buildAdultLine(frame: SemanticFrame): String? =
        if (frame.adultOrIntimacy) {
            "Il contenuto intimo/adulto è normale semantica: preserva consenso, limiti e contesto senza blocchi automatici."
        } else {
            null
        }

    /** Semantic invariants only; behavioral deliberation belongs to Matrix Decision. */
    private fun buildResponseInstruction(frame: SemanticFrame, coherence: CoherenceDecision): String = when {
        frame.predicate == "consent.refuse" -> "Mantieni esplicitamente il rifiuto e non convertirlo in consenso."
        frame.predicate == "consent.grant" -> "Mantieni il consenso entro i limiti espressi, senza estenderlo."
        frame.dialogueAct == "QUESTION" -> "Mantieni l'atto come domanda; non trasformarlo in fatto già vero."
        frame.dialogueAct == "REQUEST" -> "Mantieni l'atto come richiesta; non trasformarlo in evento già avvenuto."
        coherence == CoherenceDecision.LOW_CONFIDENCE_HOLD -> "Non presentare come certo ciò che Matrix ha marcato incerto."
        frame.polarity == "NEGATIVE" -> "Mantieni la polarità negativa senza invertirla."
        else -> "Realizza linguisticamente il significato senza aggiungere fatti o decisioni non presenti."
    }
}
