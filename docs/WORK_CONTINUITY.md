# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T14:43+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v2`

## Current objective

Combinare i componenti Matrix/Luna in un repo di integrazione separato, usando `MatrixTurnFrame` come formato canonico centrale e adapter espliciti per collegare Understanding/NLU, Coherence, Authority, Memory placeholder, Affective Engine, Prompt Builder e GGUF.

## Current status

- Repo inizializzato come integration/assembling lab.
- `MatrixTurnFrame` presente come envelope centrale per turno.
- Porte/interfacce presenti per NLU, Understanding, Coherence, Authority, MemoryAdmission, Affective, PromptBuilder e GGUF.
- Componenti Understanding/NLU copiati da `MATRIXNEO23/matrix-understanding-lab` in `vendor/`.
- Componente Affective Engine copiato da `MATRIXNEO23/matrix-affective-lab` in `vendor/`.
- Adapter Kotlin creati per collegare output Understanding/NLU e Affective al formato centrale.
- Memoria reale non presente: usare solo `NoPersistentMemoryAdmission`, nessuna persistenza finta.
- Nessun frozen letto.
- Nessuna promotion production autorizzata.

## Latest significant commits

- `562654580cda170cf4fa7b8984c4b663c8f54d80` — initial README.
- `1440b6b32ba624ee20263cbae57a79b72c5358ad` — continuity file.
- `a65a70de257975c37c4376066c8bd18aa71a5820` — assembly plan.
- `defca28fdf4f590a779774432d81187d9ceccd17` — shared contracts.
- `882dbf83e5919fead37f960282f178fe8df29a6c` — canonical `MatrixTurnFrame`.
- `167b1c35fe09cd6183dc68bdd111f5936ff011b0` — frame-based integration ports.
- `c4daee93126f9984567159cbebda731b433ebc82` — orchestrator updated to frame pipeline.
- `d2631e45da1a3b01db330088782f5c7f984a7493` — prompt builder updated to frame pipeline.
- `aad7c694505543e7457902ebe358d244e7b11241` — memory integration policy / no real memory backend.
- `03b5b8817eb00bedc646ed916fb5970c81e6b119` — copied Understanding `Domain.java`.
- `c8ba9ddfd4d3f3da9795b0659b59a3981f693c6d` — copied `UnderstandingEngine.java`.
- `09df9f03e67478a988f6f7563eb6a3c54285bad6` — copied Matrix-NLU `labels.py`.
- `8e54c94d17e44f0b2dcea4ea17e8de3ed26b00d1` — copied Affective Engine prototype.
- `1f5a4d9a58b24c176965b4ac699eaba3557ae2ce` — added `UnderstandingLabAdapter.kt`.
- `eb7ab4d649d13931a4ce822b9675b3f248848dea` — added `AffectiveLabAdapter.kt`.
- `0bcf497f6c1010f21e29aef9d02de2da46ff123f` — copied Matrix-NLU `inference.py` runtime.
- `4d320fa78d8c87c69f648390aaaf69843e99d467` — imported components manifest.

## Imported component state

| Component | Source repository | Imported into assembling | Assembly status |
|---|---|---|---|
| Understanding domain contract | `MATRIXNEO23/matrix-understanding-lab` | `vendor/matrix-understanding-lab/core/.../Domain.java` | Copied snapshot |
| Understanding engine interface | `MATRIXNEO23/matrix-understanding-lab` | `vendor/matrix-understanding-lab/core/.../UnderstandingEngine.java` | Copied snapshot |
| Matrix-NLU labels | `MATRIXNEO23/matrix-understanding-lab` | `vendor/matrix-understanding-lab/matrix_nlu/labels.py` | Copied snapshot |
| Matrix-NLU inference runtime | `MATRIXNEO23/matrix-understanding-lab` | `vendor/matrix-understanding-lab/matrix_nlu/inference.py` | Copied snapshot |
| Affective Engine prototype | `MATRIXNEO23/matrix-affective-lab` | `vendor/matrix-affective-lab/src/affective_engine.py` | Copied snapshot |
| Memory Foundation | Not implemented yet | Not imported | Placeholder only |

## New adapter state

| Adapter | Role | Status |
|---|---|---|
| `UnderstandingLabAdapter.kt` | Matrix-NLU/lab output → `NluOutput` → `SemanticFrame` → `TypedClaim` → `MatrixTurnFrame` | Created |
| `AffectiveLabAdapter.kt` | `SemanticFrame` + memory decision → affective impulse/request → `AffectiveState` → `MatrixTurnFrame` | Created |
| `NoPersistentMemoryAdmission.kt` | Temporary memory admission adapter while real memory is absent | Present |
| `SemanticFrameToPrompt.kt` | Internal semantic/affective/memory state → GGUF-readable prompt | Present |

## Current assembly path

```text
UserMessage
→ UnderstandingLabAdapter.analyze
→ UnderstandingLabAdapter.understand
→ CoherenceGuardPort.check
→ AuthorityResolverPort.resolve
→ NoPersistentMemoryAdmission.admit
→ AffectiveLabAdapter.update
→ SemanticFrameToPromptPort.buildPrompt
→ GgufPort.generate
→ AssistantReply
```

## Memory status

The real memory backend does not exist yet.

Required temporary behavior:

```text
NoPersistentMemoryAdmission
status = NO_MEMORY_BACKEND
stableWrite = false
reason = real memory backend missing
```

Do not create fake persistence, fake memories, or hidden state in `assembling`.

## Active design rule

The GGUF receives short natural-language instructions, not raw NLU numbers.

Internal:

```text
dialogueAct = REQUEST
predicate = goal.object
polarity = NEGATIVE
confidence = 0.91
```

Prompt-facing:

```text
L'utente sta facendo una richiesta con negazione/rifiuto.
Rispondi rispettando significato, relazione e limiti del contesto.
Non inventare memoria stabile.
```

## Next exact activity

1. Add runnable tests with fake `MatrixNluRuntimeBridge`, fake `AffectiveRuntimeBridge`, `NoPersistentMemoryAdmission`, prompt builder and fake GGUF.
2. Ensure the full path produces one `MatrixTurnFrame` with semantic, coherence, authority, no-memory result, affective summary, GGUF prompt and reply.
3. Add compile guard to avoid duplicate/conflicting prompt builder contracts.
4. When Student-4-v2.2A artifact is available, wire the ONNX runtime output into `MatrixNluRuntimeBridge`.
5. Only after the memory repo becomes real, replace `NoPersistentMemoryAdmission` with a real `MemoryRepository` adapter.

## Safety/project constraints

- No automatic production approval.
- Experimental candidates are allowed for user practical testing.
- No frozen access from this repo.
- No NLU censorship.
- Adult/intimacy handling is semantic robustness only.
- Uncertain semantic frames must not create stable memories.
- Persistent affect only from guarded, safe events.
- Memory remains absent until explicitly implemented in a real backend.
