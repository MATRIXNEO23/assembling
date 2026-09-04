# Imported Components Manifest

Status: integration snapshot for `MATRIXNEO23/assembling`.

This repository is now the assembly layer for Matrix/Luna components. Source labs remain the origin of truth for experiments, but the files below have been copied into `vendor/` so they can be adapted and connected in this repository.

## Source repositories

| Component | Source repository | Source status | Assembly strategy |
|---|---|---|---|
| Understanding / NLU | `MATRIXNEO23/matrix-understanding-lab` | Active lab, Student-4-v2.2A still experimental until evaluation/frozen authorization | Copy stable contracts/runtime decoder, then wrap with `UnderstandingLabAdapter` |
| Affective Engine / sentimenti | `MATRIXNEO23/matrix-affective-lab` | Validated prototype, not yet Android production module | Copy prototype, then wrap with `AffectiveLabAdapter` |
| Memory | Not implemented yet | Missing real backend | Use `NoPersistentMemoryAdmission` only; no fake persistence |

## Copied files

### Understanding / NLU

Copied into:

```text
vendor/matrix-understanding-lab/core/src/main/java/com/matrix/p0/Domain.java
vendor/matrix-understanding-lab/core/src/main/java/com/matrix/p0/UnderstandingEngine.java
vendor/matrix-understanding-lab/matrix_nlu/labels.py
vendor/matrix-understanding-lab/matrix_nlu/inference.py
```

Purpose:

- preserve the original Understanding domain contract;
- preserve the original engine interface;
- preserve the NLU label vocabulary;
- preserve the Python/ONNX inference decoder and invariant validation path.

### Affective Engine

Copied into:

```text
vendor/matrix-affective-lab/src/affective_engine.py
```

Purpose:

- preserve the validated affective prototype;
- preserve persistent affect fields such as trust, attachment, affection, attraction, resentment, respect, admiration and aversion;
- preserve transient emotion/mood/PAD behavior for later Kotlin or Android bridge work.

## New assembly adapters

```text
src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt
src/main/kotlin/matrix/assembling/adapters/AffectiveLabAdapter.kt
```

### UnderstandingLabAdapter

Role:

```text
Matrix-NLU runtime output
→ NluOutput
→ SemanticFrame
→ TypedClaim
→ MatrixTurnFrame
```

It converts copied NLU/Understanding outputs into the canonical `MatrixTurnFrame` format.

### AffectiveLabAdapter

Role:

```text
SemanticFrame + MemoryAdmissionResult
→ AffectiveRuntimeRequest
→ AffectiveState
→ MatrixTurnFrame
```

It connects the affective prototype without allowing persistent emotional/relationship deltas unless memory admission and semantic stability both allow it.

## Memory rule

Memory is intentionally not imported because the real memory backend does not exist yet.

Current behavior must remain:

```text
NoPersistentMemoryAdmission
status = NO_MEMORY_BACKEND
stableWrite = false
```

The GGUF prompt may receive transient context, but no stable memory should be written until `MATRIXNEO23/memoria` or an equivalent repository provides a real implementation.

## Assembly principle

Do not directly connect:

```text
NLU → Memory
GGUF → Memory
Affective → Memory database
GGUF → Affective persistent state
```

Use only:

```text
MatrixTurnFrame
+ ports
+ adapters
+ explicit admission decisions
```

## Next concrete steps

1. Add a runnable test using fake `MatrixNluRuntimeBridge` and fake `AffectiveRuntimeBridge`.
2. Validate the full route:

```text
UserMessage
→ UnderstandingLabAdapter
→ CoherenceGuard
→ AuthorityResolver
→ NoPersistentMemoryAdmission
→ AffectiveLabAdapter
→ SemanticFrameToPrompt
→ GGUF adapter/fake
```

3. After Student-4-v2.2A artifacts are available, connect the ONNX runtime path.
4. Only after memory exists, replace `NoPersistentMemoryAdmission` with the real memory repository adapter.
