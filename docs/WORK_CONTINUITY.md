# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T13:41+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v1`

## Current objective

Combinare i componenti Matrix/Luna in un repo di integrazione separato, partendo dal ponte tra output semantico NLU e prompt leggibile dal GGUF.

## Current status

- Repo inizializzato come integration lab.
- Prima pipeline di collegamento implementata:
  `SemanticFrame -> CoherenceGuard -> PromptDirective -> GGUF prompt`.
- Nessun modulo production importato.
- Nessun artifact ONNX/INT8 importato.
- Nessun frozen letto.
- Nessuna promotion production autorizzata.

## Latest commits

- `562654580cda170cf4fa7b8984c4b663c8f54d80` — initial README.
- `1440b6b32ba624ee20263cbae57a79b72c5358ad` — continuity file.
- `a65a70de257975c37c4376066c8bd18aa71a5820` — assembly plan.
- `defca28fdf4f590a779774432d81187d9ceccd17` — shared contracts.
- `e0c7ac6901bbae4bc394f3db4ebd6a70b4c7bf68` — Gradle settings.
- `703fffb50df08809c6a086b14aa871cf0449a13c` — Kotlin JVM build.
- `0760edf45eb625a379313e328b67d9db3514b889` — deterministic CoherenceGuard.
- `39b233e572b54f0ad0405187449624509bbc934b` — SemanticFrameToPrompt translator.
- `edb838472d80b36535fa7e0d09d3796b94e99c58` — MatrixAssemblyPipeline connector.
- `28655608161e70d3df466937d0a2b39b728e0a5b` — bridge behavior tests.
- `0169cd4588c621b815274d6ff056117befe85ea7` — CI workflow.

## Component state

| Component | Source/status | Assembly status |
|---|---|---|
| Student-4-v2.2A NLU | Training in corso in `MATRIXNEO23/matrix-understanding-lab`, run `33860928806` | Await candidate artifact |
| SemanticFrame contract | Local repo | Implemented skeleton |
| Coherence Guard | Local repo | Implemented v0.1 deterministic |
| SemanticFrameToPrompt | Local repo | Implemented v0.1 deterministic |
| MatrixAssemblyPipeline | Local repo | Implemented v0.1 connector |
| Memory Foundation | Discussione salvata in `MATRIXNEO23/memoria` | Contract bridge pending |
| Affective Engine | Validato isolato in `MATRIXNEO23/matrix-affective-lab`, not production | Await guarded input |
| Authority Resolver | P0 not fully resolved | Must be guarded |
| GGUF integration | Existing app path outside this repo | Await Android integration patch |

## Implemented assembly path

```text
SemanticFrame
  contains original text, dialogueAct, predicate, polarity, referents,
  temporalRelation, confidence and adult/intimacy marker

CoherenceGuard
  decides reply safety, memory stability and persistent affect permission

SemanticFrameToPrompt
  converts internal classes into short natural-language instructions

MatrixAssemblyPipeline
  exposes buildDirective(...) and buildPrompt(...)
```

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

## First behavior tests

- Negative refusal: must not invert negation; no stable memory.
- Question: must not become a stable fact.
- Adult/intimacy: semantic handling, no automatic block/error.
- Low confidence: transient/cautious response; no stable memory.

## Next exact activity

1. Check CI result for `Matrix Assembling CI`.
2. Add Memory/Affective bridge contracts.
3. When v2.2A artifact exists, define import contract for NLU output -> `SemanticFrame`.
4. Prepare Android/GGUF integration patch with prompt visibility diagnostics.

## Safety/project constraints

- No automatic production approval.
- Experimental candidates are allowed for user practical testing.
- No frozen access from this repo.
- No NLU censorship.
- Adult/intimacy handling is semantic robustness only.
- Uncertain semantic frames must not create stable memories.
- Persistent affect only from guarded, safe events.
