# Matrix Assembling Lab

Repository di integrazione per collegare i moduli Matrix/Luna senza creare una seconda architettura concorrente.

## Fonte canonica globale

L'architettura generale di Matrix resta definita in:

`MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`

Questo repository implementa e testa il cablaggio tra i componenti.

## Percorso autorevole in Assembling

```text
MatrixTurnFrame
→ NLU / Understanding
→ Working Memory / context
→ Coherence / Authority
→ Affective appraisal
→ Matrix decision layer
→ Prompt Builder
→ GGUF
→ output validation
→ persistent consolidation
```

Non tutte le fasi future sono ancora cablate. Una fase mancante deve risultare `NON_CABLATO`, non simulata.

## Regola fondamentale

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

- NLU/Understanding comprende e conserva evidenza/provenienza.
- Coherence/Authority risolvono affidabilità, fonte, owner e conflitti.
- Memory Admission possiede la persistenza Long-Term.
- Affective possiede appraisal/emozioni/persistent affect, non RelationshipState.
- Matrix possiede la decisione comportamentale.
- GGUF realizza il linguaggio e non possiede verità, memoria o relazione.

## Memoria

```text
WORKING MEMORY
= contesto operativo temporaneo del turno

LONG-TERM MEMORY
= EPISODIC + SEMANTIC + REFLECTION
  con eventuale CORE priority subset
```

La Working Memory non è persistenza.

Il backend Long-Term reale non è ancora collegato in Assembling: gli adapter memoria attuali devono rimanere non persistenti.

## Adult / intimacy

Adult/intimacy è un dominio semantico di prima classe, non una categoria di censura.

Essere contenuto adulto/intimo non è, da solo, motivo per:
- bloccare NLU;
- degradare confidence;
- impedire affective persistence;
- impedire Memory Admission.

Le decisioni dipendono da significato, contesto, fonte, confidence, rilevanza e normali regole Matrix.

## Compatibilità

Il vecchio percorso:

```text
contracts/*
pipeline/*
prompt/*
```

resta presente per compatibilità e test, ma non è più una seconda fonte architetturale. Nuove decisioni di ownership/ordine vanno implementate sul percorso `MatrixTurnFrame` e migrate progressivamente.

## Documentazione

Leggere nell'ordine:

1. `docs/README.md` — indice e stato dei documenti;
2. `docs/MODULE_CONNECTIONS.md` — cablaggio canonico;
3. `docs/ASSEMBLY_PLAN.md` — piano operativo;
4. `docs/WORK_CONTINUITY.md` — stato corrente e punto di ripresa.

## Stato modello NLU

Student-4-v2.2A resta un candidato controllato, non una production approval. Il bundle mixed-head-protected è presente via Git LFS per integrazione/runtime testing; Student-5 resta esperimento separato e non blocca Assembling.

## Change control

Se cambia un componente, il cambiamento è completo solo quando vengono aggiornati nello stesso workstream:
- architettura/spec canonica;
- wiring Assembling;
- codice/adapter;
- test;
- continuità;
- documenti che altrimenti diventerebbero contraddittori.
