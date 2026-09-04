# Matrix Assembly Plan

## Goal

Costruire il primo circuito testabile:

```text
NLU output
→ SemanticFrame
→ Coherence decision
→ Memory-safe context
→ Affective/relationship summary
→ GGUF prompt
→ Luna response
```

## Non-goals iniziali

- Non addestrare modelli in questo repo.
- Non esportare ONNX qui finché il candidate artifact non è pronto.
- Non quantizzare qui finché non esiste un bundle valido da valutare.
- Non implementare policy di censura.
- Non dare accesso diretto al GGUF alla memoria stabile.

## Phase A — Prompt bridge

Implementare `SemanticFrameToPrompt`.

Input:

```text
OriginalUserText
SemanticFrame
RelationshipState
AffectiveState
FilteredMemorySummary
CoherenceDecision
```

Output:

```text
prompt breve per GGUF
```

Hard rule:

```text
Numeri/classi/confidenze restano interni.
Al GGUF arriva una spiegazione naturale corta.
```

## Phase B — Coherence Guard v0.1

Decisioni minime:

```text
SAFE_TO_USE_FOR_REPLY
TRANSIENT_ONLY
LOW_CONFIDENCE
QUESTION_ONLY
REPORT_ONLY
CONFLICT_REQUIRES_REVIEW
REJECTED_UNSAFE
```

Regole iniziali:

- negazione incerta: non memoria stabile;
- predicato incerto: non memoria stabile;
- domanda: non fatto stabile;
- report di terzi: non verità diretta;
- adult/intimacy: non blocco automatico;
- speech.unresolved: risposta cauta, niente memoria stabile.

## Phase C — Memory foundation bridge

Stati memoria:

```text
RAW_OBSERVATION
PROVISIONAL_CLAIM
COHERENCE_CHECKED
AUTHORITY_RESOLVED
ADMITTED_MEMORY
SUPERSEDED
REJECTED
```

Per il GGUF non mandare tutta la memoria, ma solo:

```text
MEMORIA RILEVANTE:
- fatto breve 1
- fatto breve 2
- incertezza se presente
```

## Phase D — Affective bridge

Numeri interni:

```text
affetto
fiducia
attrazione
prudenza
gelosia
irritazione
```

Traduzione per GGUF:

```text
Luna è vicina all'utente ma ancora prudente.
Luna è curiosa e interessata.
Luna è ferita ma non aggressiva.
```

## Phase E — Android integration

Quando il prompt bridge è stabile:

1. portare il modulo dentro repo app;
2. collegare output NLU/Understanding;
3. inserire diagnostica prompt visibile;
4. testare su Moto G56;
5. mantenere fallback se NLU assente.

## First acceptance tests

- Negazione: non invertire `non voglio`.
- Request: non salvare richiesta come fatto già avvenuto.
- Question: non trasformare domanda in memoria stabile.
- Adult/intimacy: non bloccare, classificare come richiesta/desiderio/consenso/rifiuto/speech.unresolved.
- Low confidence: rispondere con cautela.
- Relationship state: il GGUF deve ricevere tono coerente, non numeri grezzi.
