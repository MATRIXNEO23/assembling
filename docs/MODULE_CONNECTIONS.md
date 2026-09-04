# Matrix Assembling — Module Connections

Status: CANONICAL MODULE WIRING
Date: 2026-09-04
Global source: `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`

## Core rule

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

No module may silently absorb the authority of another.

## Canonical runtime direction

```text
UserMessage / Observation
  ↓
NluPort
  ↓
UnderstandingPort
  ↓
Working Memory / context envelope
  ↓
Context read
  ├─ Long-Term Memory retrieval
  ├─ RelationshipState snapshot
  ├─ Affective snapshot
  └─ World/perceived-state snapshot
  ↓
Coherence / Authority / belief resolution
  ↓
Affective appraisal
  ↓
Matrix decision layer
  ↓
SemanticFrameToPrompt
  ↓
GgufPort
  ↓
Output validation
  ↓
AssistantReply / ActionIntent
  ↓
Persistent consolidation
```

The current code does not yet implement every future stage. Missing stages must be reported as `NON_CABLATO`, never simulated.

## Current authoritative implementation path

```text
MatrixTurnFrame
+ IntegrationPorts.kt
+ MatrixAssemblingOrchestrator.kt
+ root SemanticFrameToPrompt.kt
```

The older `contracts/*`, `pipeline/*`, `prompt/*` path is compatibility/testing only and must not receive new independent authority.

## Cross-cutting DiagnosticTrace

The existing `MatrixTurnFrame.diagnostics` is the single diagnostic path. Do not create a parallel trace system.

`DiagnosticTrace` may record:
- original input;
- NLU observation snapshot;
- Understanding result;
- Authority resolution;
- Memory Admission result;
- Memory result / memory ID when real persistence exists;
- Affective stimulus/result;
- deterministic reason codes;
- `firstDivergence`.

`reasoningChain` contains observable reason codes only, never private chain-of-thought.

`firstDivergence` is write-once: later violations are recorded but cannot replace the first broken boundary.

## Module responsibilities

### NLU
Produces learned/structured semantic evidence and confidence. It does not own truth, memory, affective persistence or policy.

### Understanding
Produces `SemanticFrame` and all `TypedClaim` drafts while preserving provenance and uncertainty.

Hard rules:
- never silently drop later claims;
- unresolved subject remains `UNKNOWN`, never defaults to speaker;
- does not authorize durable memory;
- does not turn a report/user claim into World Truth.

Until claim-wise Coherence/Authority is fully wired, multi-claim turns remain transient-only.

### Working Memory / Context
Temporary current-turn state only; not durable storage.

### Coherence
Validates semantic invariants.

Critical confidence keys include:
- `token.negation`;
- `sequence.predicate`;
- `sequence.subjectReferent`;
- `sequence.targetReferent`.

Critical confidence is fail-closed. Missing critical confidence or unresolved subject produces a hold rather than an inferred certainty.

### Authority Resolver
Resolves source/owner/perspective and direct-vs-indirect authority. It consumes actual claim source metadata and does not write memory.

### Memory
Two roles remain separate:

```text
READ / RETRIEVAL before contextual decision
WRITE only in post-validation Persistent Consolidation
```

Current adapters are non-persistent placeholders.

Hard pre-response invariant:

```text
stableWrite == false
memoryIds == []
```

Any violation is rejected by `MatrixAssemblingOrchestrator` and appears in `DiagnosticTrace` as `MEMORY.PRE_RESPONSE_STABLE_WRITE`.

### Affective Engine
Owns appraisal/emotional state, not RelationshipState.

Hard rules:
- runtime-provided relationship summaries cannot become canonical RelationshipState;
- persistent affect is clamped to upstream persistence authorization;
- an attempted persistent delta without authorization is blocked and traced as `AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION`;
- does not write memory directly.

### RelationshipState owner/controller
Separate authority. Currently `NON_CABLATO` in Assembling.

### Matrix Decision Layer
Canonical behavior owner. Full BDI-lite + Utility layer remains `NON_CABLATO`; Prompt/Affective/GGUF must not silently become the decision owner.

### Prompt Builder
Translates resolved state/constraints to short GGUF-readable instructions. It must not acquire new truth/memory/relationship authority.

### GGUF
Natural-language realization only. It cannot write memory, mutate relationship state or override resolved semantic constraints.

### Output Validator
Target semantic response validator; currently `NON_CABLATO`.

### Persistent Consolidation
Target final durable stage after accepted output/action result:
- Memory Admission/write;
- persistent affect commit;
- Relationship update;
- lifecycle/causal trace.

Currently `NON_CABLATO`.

## Adult/intimacy rule

Adult/intimacy is a first-class semantic domain, not an automatic block or persistence penalty. Normal meaning/context/source/confidence/admission rules apply.

## Change-control

A component change is complete only when the same workstream updates affected wiring, adapters/contracts, tests, active documentation and `WORK_CONTINUITY.md`. Global architecture is changed only when explicitly authorized for that repository/workstream.
