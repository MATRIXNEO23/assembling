# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T13:36+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v1`

## Current objective

Combinare i componenti Matrix/Luna in un repo di integrazione separato, partendo dal ponte tra output semantico NLU e prompt leggibile dal GGUF.

## Current status

- Repo inizializzato come integration lab.
- Nessun modulo production importato.
- Nessun artifact ONNX/INT8 importato.
- Nessun frozen letto.
- Nessuna promotion production autorizzata.

## Component state

| Component | Source/status | Assembly status |
|---|---|---|
| Student-4-v2.2A NLU | Training in corso in `MATRIXNEO23/matrix-understanding-lab`, run `33860928806` | Await candidate artifact |
| Memory Foundation | Discussione salvata in `MATRIXNEO23/memoria` | Contract first |
| Affective Engine | Validato isolato in `MATRIXNEO23/matrix-affective-lab`, not production | Await guarded input |
| Coherence Guard | Progettato | Not implemented |
| Authority Resolver | P0 not fully resolved | Must be guarded |
| SemanticFrameToPrompt | Progettato | First implementation target |
| GGUF integration | Existing app path outside this repo | Await prompt contract |

## Assembly order

1. Define shared contracts: `SemanticFrame`, `RelationshipState`, `AffectiveState`, `FilteredMemory`, `PromptDirective`.
2. Implement deterministic `SemanticFrameToPrompt` templates.
3. Add tests for negation, request, refusal, correction, adult/intimacy robustness, low confidence and memory-safe behavior.
4. Import or reference v2.2A NLU candidate only after artifact exists.
5. Add Coherence Guard before stable memory.
6. Add Memory Foundation raw/provisional/admitted lifecycle.
7. Add Affective Engine only after guarded events.
8. Prepare Android/GGUF integration patch.

## Active design rule

The GGUF receives short natural-language instructions, not raw NLU numbers. Example:

```text
USER_TEXT:
"Non voglio uscire con Marco"

SYSTEM_MEANING:
L'utente sta rifiutando di uscire con Marco. La negazione è importante.

RESPONSE_INSTRUCTION:
Rispondi come Luna rispettando il rifiuto. Non interpretarlo come desiderio di uscire.
```

## Next exact activity

Create the first contract and implementation skeleton for `SemanticFrameToPrompt` with deterministic template output and tests.

## Safety/project constraints

- No automatic production approval.
- Experimental candidates are allowed for user practical testing.
- No frozen access from this repo.
- No NLU censorship.
- Adult/intimacy handling is semantic robustness only.
- Uncertain semantic frames must not create stable memories.
