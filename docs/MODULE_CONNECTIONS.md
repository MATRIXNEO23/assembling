# Matrix Assembling — Module Connections

Status: CANONICAL MODULE WIRING  
Date: 2026-09-05  
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
MemoryPreflightPort.evaluate
  ↓
Affective appraisal
  ↓
Matrix decision layer
  ↓
SemanticFrameToPrompt
  ↓
GgufPort
  ↓
OutputValidatorPort
  ↓
AssistantReply / ActionIntent
  ↓
PersistentConsolidationPort
```

Only the currently implemented phases run. Missing stages must be reported as `NON_CABLATO`, never simulated.

## Current authoritative implementation path

```text
MatrixTurnFrame
+ IntegrationPorts.kt
+ MatrixAssemblingOrchestrator.kt
+ root SemanticFrameToPrompt.kt
```

The older `contracts/*`, `pipeline/*`, `prompt/*` path is compatibility/testing only and is deprecated for new callers.

## Cross-cutting DiagnosticTrace

`MatrixTurnFrame.diagnostics` is the single diagnostic path. Do not create a parallel trace system.

It records observable boundary facts such as original input, module snapshots, decisions, confidence, metadata, deterministic reason codes and `firstDivergence`.

`reasoningChain` contains reason codes only, never private chain-of-thought. `firstDivergence` is write-once.

## Module responsibilities

### NLU

Produces learned/structured semantic evidence and confidence.

Allowed:
- multiple claims;
- dialogue act, predicate, polarity, temporal relation;
- subject/target/owner/perspective evidence;
- confidence by head;
- explicit adult/intimacy semantic marker when available.

Forbidden:
- truth decisions;
- memory persistence;
- affective persistence;
- censorship policy.

### Understanding

Produces a primary compatibility `SemanticFrame` and preserves **all** NLU claims as `TypedClaim` drafts.

Hard rules:
- never silently drop later claims;
- unresolved subject remains `UNKNOWN`, never defaults to speaker;
- does not authorize durable memory;
- does not turn a report/user claim into World Truth;
- explicit NLU adult/intimacy marker takes precedence over the temporary keyword fallback.

Until claim-wise contextual resolution is implemented:

```text
multi-claim turn
→ all TypedClaim values preserved
→ Coherence SAFE_TRANSIENT_ONLY
→ Authority sourceType MULTI_CLAIM
→ direct authority rejected
```

### Working Memory / Context

Temporary current-turn state only; not durable storage.

### Coherence

Validates semantic invariants for **every TypedClaim**, not only the primary `SemanticFrame`.

Critical confidence keys:
- `token.negation`;
- `sequence.predicate`;
- `sequence.subjectReferent`;
- `sequence.targetReferent`.

Missing or sub-threshold confidence in any claim fails closed. Diagnostics identify the exact claim/key, for example:

```text
claim[1].token.negation
```

Coherence does not own durable persistence.

### Authority Resolver

Resolves source/owner/perspective and direct-vs-indirect authority. It does not write memory.

For multiple claims, the current basic adapter exposes `sourceType=MULTI_CLAIM` and holds direct authority until claim-wise resolution exists.

### Memory preflight

Authoritative pre-response API:

```text
MemoryPreflightPort.evaluate
```

Purpose:
- evaluate/provisionally classify current-turn candidates;
- expose non-persistent status to downstream modules;
- never write durable memory.

Hard invariant:

```text
stableWrite == false
memoryIds == []
```

Any violation is rejected by `MatrixAssemblingOrchestrator` and traced as `MEMORY.PRE_RESPONSE_STABLE_WRITE`.

`MemoryAdmissionPort` remains deprecated compatibility only.

### Affective Engine

Owns appraisal/emotional state, not RelationshipState.

Hard rules:
- runtime-provided relationship summaries cannot become canonical RelationshipState;
- persistent affect is clamped to upstream persistence authorization;
- unauthorized persistent output is blocked and traced as `AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION`;
- does not write memory directly.

### RelationshipState owner/controller

Separate authority. Currently `NON_CABLATO` in Assembling.

### Matrix Decision Layer

Canonical behavior owner. Full BDI-lite + Utility remains `NON_CABLATO`; Prompt/Affective/GGUF must not silently become the decision owner.

### Prompt Builder

Realization-only translator. It may preserve resolved semantic invariants but cannot select behavioral policy or acquire truth, memory or relationship authority.

### GGUF

Natural-language realization only. It cannot write memory, mutate relationship state or override resolved semantic constraints.

### Output Validator

`OutputValidatorPort` is now an explicit post-GGUF boundary.

- when supplied, it executes after generation;
- when absent, `DiagnosticTrace.tags["output.validation"] = "NON_CABLATO"`;
- the real semantic validator implementation remains `NON_CABLATO`.

### Persistent Consolidation

`PersistentConsolidationPort` names the future final durable stage after accepted output/action result:
- Memory Admission/write;
- persistent affect commit;
- Relationship update;
- lifecycle/causal trace.

No implementation is currently connected.

## Adult/intimacy rule

Adult/intimacy is a first-class semantic domain, not an automatic block or persistence penalty. The explicit NLU marker is preferred; the local keyword fallback exists only for compatibility with older runtimes.

## Change-control

A component change is complete only when the same workstream updates affected wiring, adapters/contracts, tests, active documentation and `WORK_CONTINUITY.md`. Global architecture is changed only when explicitly authorized for that repository/workstream.
