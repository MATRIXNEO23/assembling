# Matrix Assembling — Module Connections

Status: CANONICAL MODULE WIRING  
Date: 2026-09-05

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
Working Memory / MatrixTurnFrame
  ↓
Context read (future)
  ├─ Long-Term retrieval
  ├─ RelationshipState snapshot
  ├─ Affective snapshot
  └─ World/perceived-state snapshot
  ↓
Coherence / Authority / belief resolution
  ↓
MemoryPreflightPort (non-persistent only)
  ↓
Affective appraisal
  ↓
Matrix decision layer (future)
  ↓
SemanticFrameToPrompt
  ↓
GgufPort
  ↓
OutputValidatorPort (optional boundary; semantic validator not yet wired)
  ↓
AssistantReply / ActionIntent
  ↓
PersistentConsolidationPort (future durable-write boundary)
```

Missing stages must be reported as `NON_CABLATO`, never simulated.

## Authoritative implementation path

```text
MatrixTurnFrame
+ IntegrationPorts.kt
+ MatrixAssemblingOrchestrator.kt
+ root SemanticFrameToPrompt.kt
```

The older `contracts/*`, `pipeline/*`, and `prompt/*` path remains compatibility/testing only and is explicitly deprecated for new callers.

## NLU

Input: text, locale, speaker/observer context.  
Output: one or more structured NLU claims with confidence.

Required behavior:
- preserve every emitted claim;
- expose the first claim only as the legacy `nlu` view;
- store all claims in `nluClaims`;
- preserve critical confidence keys;
- expose explicit adult/intimacy marker when available.

Forbidden:
- truth decisions;
- stable memory writes;
- affective persistence;
- censorship policy.

## Understanding

Input: all NLU claims plus current turn metadata.  
Output: all `TypedClaim` instances plus a primary compatibility `SemanticFrame`.

Required behavior:
- preserve subject, target, owner, perspective, object, source and provenance;
- never discard secondary claims silently;
- leave unresolved subjects as `UNKNOWN`, not replace them with the speaker;
- keep `SemanticFrame.stableMemoryAllowed=false` because that field is legacy only.

Multi-claim limitation:

```text
all claims preserved
+ primary SemanticFrame compatibility view
+ Coherence = SAFE_TRANSIENT_ONLY
+ Authority = MULTI_CLAIM / deferred
```

until per-claim contextual resolution is implemented.

## Coherence

Purpose: validate semantic invariants and confidence gates.

Critical keys:

```text
token.negation
sequence.predicate
sequence.subjectReferent
sequence.targetReferent
```

A missing critical key is fail-closed:

```text
LOW_CONFIDENCE_HOLD
firstDivergence = COHERENCE.MISSING_CRITICAL_CONFIDENCE
```

Coherence does not own durable persistence.

## Authority Resolver

Purpose:
- resolve source/owner/perspective;
- distinguish direct assertions from third-party reports;
- reject direct authority for unresolved or multi-claim compatibility states;
- expose reason codes.

It never writes memory.

## Memory boundaries

### Pre-response

```text
MemoryPreflightPort.evaluate
```

This stage is evaluation only. It must always return:

```text
stableWrite=false
memoryIds=[]
```

The orchestrator rejects violations before Affective/Prompt/GGUF continue.

`MemoryAdmissionPort` remains a deprecated compatibility facade only.

### Post-output validation

```text
PersistentConsolidationPort
```

This is the only intended boundary for future durable Memory Admission and MemoryRepository writes. It is currently `NON_CABLATO`.

## Affective Engine

Purpose:
- appraisal;
- transient emotions;
- mood;
- persistent-affect result only when an admitted event permits it.

Hard boundaries:
- does not own `RelationshipState`;
- ignores any relationship summary emitted by the affective runtime;
- clamps `persistentDeltaApplied` to the upstream persistence authorization;
- records `AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION` on a runtime violation;
- does not write memory.

Adult/intimacy is not an automatic persistence penalty.

## RelationshipState

Relationship is externally owned and currently `NON_CABLATO` in the authoritative frame path. Affective evidence may contribute later, but it cannot become a competing relationship authority.

## Matrix Decision Layer

Canonical owner of behavior choice. Full BDI-lite/Utility wiring is still `NON_CABLATO`; Prompt Builder must not silently replace it.

## Prompt Builder

Input: semantic evidence, Coherence, Authority, memory preflight status and affective summary.  
Output: bounded natural-language realization constraints.

It may preserve semantic invariants such as negation, referents, time and consent boundaries. It must not invent behavioral goals or durable facts.

## GGUF

Natural-language realization only. It cannot decide truth, memory, relationship state or persistence.

## Output Validator

`OutputValidatorPort` now exists as an explicit optional boundary. The real semantic validator remains `NON_CABLATO`; absence is recorded in `DiagnosticTrace` rather than hidden.

## DiagnosticTrace

Each phase records:
- input received;
- output produced;
- decision;
- deterministic reason codes;
- status;
- first divergence.

`reasoningChain` contains observable reason codes only, never private chain-of-thought. Once set, `firstDivergence` cannot be overwritten by later failures.

## Adult/intimacy

Adult/intimacy is a first-class semantic domain. It must not be blocked or degraded merely because it is intimate. Normal source, confidence, context and admission rules apply.

## Change control

A component change is complete only when the same workstream updates code, tests, this wiring document, memory policy/status when relevant, and `WORK_CONTINUITY.md`.
