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

The older path:

```text
contracts/*
pipeline/*
prompt/*
```

is retained as a compatibility/testing facade. It is not a second architecture and must not receive new independent authority.

## Module responsibilities

### NLU
Input: language signal.
Output: learned/structured semantic evidence and confidence.

Allowed:
- dialogue act;
- predicate;
- polarity;
- spans;
- referent classes;
- temporal relation;
- confidence.

Forbidden:
- stable memory writes;
- truth decisions;
- affective persistence;
- censorship policy.

### Understanding
Input: NLU evidence + supplied turn context.
Output: `SemanticFrame` / `TypedClaim` draft.

Allowed:
- preserve subject/target/owner/perspective;
- normalize object values;
- preserve source/provenance/world-observation flags;
- preserve every claim emitted by the NLU runtime;
- mark uncertainty.

Forbidden:
- silently dropping claims after the first one;
- deciding that a claim is durable memory;
- turning a user/report claim into World Truth;
- bypassing Authority/Memory Admission.

Compatibility note: `SemanticFrame.stableMemoryAllowed` remains in the current data class only for ABI/source compatibility and is not authoritative. New code must rely on Coherence/Authority/Memory Admission.

Current multi-claim rule: all claims must remain in `MatrixTurnFrame.typedClaims`. Until claim-wise Coherence/Authority is fully wired, a turn containing more than one claim remains `SAFE_TRANSIENT_ONLY` and cannot become durable memory through the pre-response path.

### Working Memory / Context
Purpose: temporary state used to answer the current turn.

Contains bounded current-turn semantics, active referents, retrieved memories and decision context. It is not durable storage.

### Coherence
Purpose: validate semantic stability/invariants.

Must inspect canonical confidence keys such as:
- `token.negation`;
- `sequence.predicate`;
- `sequence.subjectReferent`;
- `sequence.targetReferent`.

Critical confidence is fail-closed: a missing critical confidence key is treated as `LOW_CONFIDENCE_HOLD`, never as confidence `1.0`.

It may mark low-confidence/transient/report/question states but does not own final persistence.

### Authority Resolver
Purpose:
- resolve source/owner/perspective;
- distinguish direct assertion from third-party report;
- detect same-property conflict when relevant memory exists;
- propose correction/supersede semantics.

It must consume actual claim source metadata, not infer report status only from Coherence enum values.

### Memory
Two different roles are mandatory:

```text
READ: retrieve relevant Long-Term context before decision.
WRITE: admit/persist only during controlled consolidation.
```

Current Assembling memory adapters are placeholders only and must return no durable write.

Hard pre-response invariant enforced by `MatrixAssemblingOrchestrator`:

```text
before Output Validation / Persistent Consolidation:
stableWrite == false
memoryIds == []
```

Any memory adapter returning a durable write/result before the response phase is rejected immediately. A future real persistent adapter must be connected to the post-validation consolidation phase rather than replacing the current placeholder in place.

Long-Term logical layers:
- EPISODIC;
- SEMANTIC;
- REFLECTION;
- optional CORE priority subset.

Working Memory is separate and temporary.

### Affective Engine
Purpose:
- appraisal;
- transient emotions;
- mood;
- persistent affect proposals/state.

Hard boundaries:
- does not own `RelationshipState`;
- does not create World Truth;
- does not write memory directly;
- adult/intimacy is not an automatic reason to suppress persistent affect.

### RelationshipState owner/controller
Relationship is canonical cognitive/app state separate from affective state. Affective signals may contribute evidence, but relationship changes require the relationship owner/controller and normal decision/commit rules.

### Matrix Decision Layer
Canonical owner of behavior choice.

Target architecture uses BDI-lite + bounded Utility and emits a `DecisionSnapshot`. The full layer is not yet wired in Assembling; until then it is `NON_CABLATO`, not delegated silently to Affective or GGUF.

### Prompt Builder
Input:
- original text;
- resolved semantic meaning;
- filtered memory/context;
- relationship snapshot;
- affective/appraisal state;
- decision constraints.

Output: short natural-language instructions for GGUF.

### GGUF
Role: natural-language realization only.

Forbidden:
- final truth resolution;
- memory writes;
- relationship mutation;
- overriding resolved negation/referents/consent/context.

### Output Validator
Target role: verify the generated response against the resolved semantic/decision state. This is planned but not yet wired.

### Persistent Consolidation
Target final stage after accepted output:
- Memory Admission/write;
- persistent affect;
- Relationship update;
- causal/lifecycle trace.

## Adult/intimacy rule

Adult/intimacy is a first-class semantic domain. It must not be treated as an automatic block, low-value event or reason to suppress memory/affect merely because it is intimate. Persistence depends on meaning, context, confidence, source, relevance and admission rules.

## Change-control

A component change is complete only when the same workstream updates:
- global architecture if ownership/order changed;
- this module wiring document;
- code adapters/contracts;
- affected tests;
- `WORK_CONTINUITY.md`;
- any now-obsolete document status.
