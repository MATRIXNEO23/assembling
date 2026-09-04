# Matrix Assembling Lab

Repository di integrazione per combinare i moduli Matrix/Luna senza sporcare i laboratori separati.

## Scopo

Questo repo non sostituisce i repo di ricerca. Serve a definire e testare il punto in cui i componenti si parlano:

```text
NLU / Understanding
→ SemanticFrame
→ Coherence Guard
→ Authority Resolver
→ Memory Admission
→ Memory Foundation
→ Affective Engine
→ SemanticFrameToPrompt
→ GGUF Prompt
```

## Regola principale

Il GGUF non deve ricevere numeri grezzi o contratti tecnici lunghi. Deve ricevere una traduzione semplice:

```text
Frase originale = tono.
SemanticFrame = significato deciso dal sistema.
Memoria filtrata = contesto.
Stato relazione/affettivo = comportamento.
GGUF = risposta naturale.
```

## Componenti da combinare

| Componente | Fonte/ruolo | Stato integrazione |
|---|---|---|
| Matrix-NLU Student-4-v2.2A | produce classi, span, referenti, negazione, predicato | candidato in training |
| Understanding Adapter | trasforma output NLU in `SemanticFrame` | da implementare |
| Coherence Guard | blocca claim instabili prima della memoria | da implementare |
| Authority Resolver | fonte, owner, conflitto, report/correzione | da collegare dopo P0 fix |
| Memory Foundation | raw/provisional/admitted/superseded memory | da integrare |
| Affective Engine | stato emotivo persistente/transient | prototipo validato isolato |
| SemanticFrameToPrompt | traduce numeri/classi in istruzioni semplici per GGUF | primo target di questo repo |
| GGUF Prompt Builder | compone prompt finale Luna | da implementare |

## Stati possibili

- `ASSEMBLY_PROTOTYPE`: codice/contratti in costruzione.
- `EXPERIMENTAL_TEST_CANDIDATE`: provabile dall'utente, non production.
- `NOT_PRODUCTION_APPROVED`: non approvato per produzione.
- `PRODUCTION_APPROVED`: solo dopo gate espliciti e decisione dell'utente.

## Vincoli

- Nessuna promotion automatica.
- Nessun frozen toccato qui.
- Nessuna censura NLU.
- Adult/intimacy deve essere robustezza semantica, non safety/moderation.
- Memoria stabile solo dopo Coherence/Authority/Admission.
- Affective Engine persistente solo da eventi sicuri.

## Primo obiettivo

Creare una prima versione funzionale del ponte:

```text
SemanticFrame + RelationshipState + AffectiveState + FilteredMemory
→ SemanticFrameToPrompt
→ prompt breve e leggibile dal GGUF
```

Questo è il primo pezzo che rende utilizzabile il nuovo NLU dentro Luna.
