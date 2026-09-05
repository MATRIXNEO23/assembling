# Matrix Assembling — Module Connections

Status: CANONICAL MODULE WIRING  
Date: 2026-09-05  
Global source: `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`  
Universal semantic contract: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## Core rule

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS
```

No module may silently absorb the authority or semantics of another.

Shared cross-module meanings are owned by MIP, not by individual module implementations.

## Canonical runtime direction

```text
UserMessage / Observation
  ↓
NluPort
  ↓
UnderstandingPort
  ↓
TurnWorkspace / MatrixContextSnapshot
  ↓
Context ENRICH
  ├─ Memory index probe — ALWAYS
  ├─ RelationshipState snapshot when wired
  ├─ AffectiveState snapshot when wired
  ├─ World/perceived-state snapshot when wired
  └─ other registered domains when wired
  ↓
Coherence / Authority / Belief resolution
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

Logical stages may contain parallel independent reads; this diagram does not require a strictly serial implementation.

Only currently implemented phases run. Missing stages/domains must be reported explicitly as `NON_CABLATO` / `NOT_WIRED`, never simulated with fake defaults.

## Current authoritative implementation path

```text
MatrixTurnFrame
+ IntegrationPorts.kt
+ MatrixAssemblingOrchestrator.kt
+ root SemanticFrameToPrompt.kt
```

These are current implementation surfaces. MIP is the semantic authority for future migration.

The older `contracts/*`, `pipeline/*`, `prompt/*` path is compatibility/testing only and is deprecated for new callers.

## Cross-cutting DiagnosticTrace

`MatrixTurnFrame.diagnostics` remains the single diagnostic path. Do not create a parallel trace system.

It records observable boundary facts such as original input, module snapshots, decisions, confidence, metadata, deterministic reason codes and `firstDivergence`.

MIP adds canonical meanings for correlation/causation, context snapshot IDs, domain availability and reason-code namespaces; these should be adopted incrementally without discarding current regression-tested diagnostics.

`reasoningChain` contains reason codes only, never private chain-of-thought. `firstDivergence` is write-once.

## Shared MIP boundary semantics

The following terms have one meaning across all modules and may not be redefined locally:

```text
speaker
observer
source
subject
target
owner
perspective
```

Likewise:

```text
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
NO_MATCH
```

are distinct states.

Confidence-like values are distinct:

```text
interpretationConfidence
sourceReliability
authorityLevel
beliefConfidence
retrievalRelevance
```

`TypedClaim` is interpreted semantic evidence, not truth, Belief or Memory.

## Module responsibilities

### NLU

Produces learned/structured semantic evidence and confidence.

Allowed:
- multiple claims;
- dialogue act, predicate, polarity, temporal relation;
- subject/target/owner/perspective evidence;
- confidence by head;
- source spans/provenance evidence;
- explicit adult/intimacy semantic marker when available.

Forbidden:
- truth decisions;
- memory persistence;
- affective persistence;
- consent decisions;
- censorship policy.

NLU output must progressively conform to MIP entity/temporal/predicate/confidence semantics. Compatibility strings remain temporary implementation details.

### Understanding

Produces a primary compatibility `SemanticFrame` and preserves **all** NLU claims as `TypedClaim` drafts.

Hard rules:
- never silently drop later claims;
- unresolved subject remains unresolved, never defaults to speaker;
- speaker/source/subject/target/owner/perspective remain distinct;
- does not authorize durable memory;
- does not turn a report/user claim into World Truth;
- explicit NLU adult/intimacy marker takes precedence over the temporary keyword fallback;
- no downstream module should re-parse free text to reconstruct linguistics already represented here.

Until claim-wise contextual resolution is implemented:

```text
multi-claim turn
→ all TypedClaim values preserved
→ Coherence SAFE_TRANSIENT_ONLY
→ Authority sourceType MULTI_CLAIM
→ direct authority rejected
```

### TurnWorkspace / MatrixContextSnapshot

Current `MatrixTurnFrame` is the implementation precursor to MIP `TurnWorkspace`.

Target semantics:
- ephemeral current-turn operational state;
- immutable/versioned context snapshots;
- context is read-only;
- canonical mutable state remains owned by its module;
- a non-wired domain is explicitly unavailable/not-wired, never fake zero/default state.

Reserved context domains:

```text
LINGUISTIC
WORLD
MEMORY
BELIEF
RELATIONSHIP
AFFECTIVE
INTIMACY
GOAL
SYSTEM
```

### Memory retrieval

Memory retrieval is a universal service contract, not Reflection-specific.

Hard rule:

```text
EVERY NORMAL TURN → LIGHTWEIGHT MEMORY INDEX PROBE
```

This means querying the index to discover relevance, not loading the entire store.

Target levels:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — relevant hits only
LEVEL 3 DEEP_OR_MULTI_HOP — explicit complex purpose only
```

`NO_MATCH` means the index worked and found nothing relevant. `INDEX_UNAVAILABLE` means retrieval could not be performed. They must never be represented identically.

### Coherence

Validates semantic invariants for **every TypedClaim**, not only the primary `SemanticFrame`.

Critical confidence keys currently include:
- `token.negation`;
- `sequence.predicate`;
- `sequence.subjectReferent`;
- `sequence.targetReferent`.

Missing or sub-threshold confidence in any claim fails closed. Diagnostics identify the exact claim/key.

Coherence does not own durable persistence and must not reinterpret free text.

### Authority / Belief resolution

Resolves source/owner/perspective and direct-vs-indirect authority. It does not write Memory.

MIP rules:
- interpretation confidence is not authority;
- source reliability is not belief confidence;
- only authorized World input may originate World Truth;
- contradiction is distinct from supersession;
- temporal change is not contradiction by default;
- explicit contradiction identity is produced by Authority/Belief resolution, not invented later by Memory Admission.

For multiple claims, the current basic adapter exposes `sourceType=MULTI_CLAIM` and holds direct authority until claim-wise resolution exists.

### Memory preflight

Authoritative pre-response API:

```text
MemoryPreflightPort.evaluate
```

Purpose:
- evaluate/provisionally classify current-turn candidates;
- expose non-persistent status to downstream modules;
- never write durable Memory.

Hard invariant:

```text
stableWrite == false
memoryIds == []
```

Under MIP:

```text
pre-response MemoryCandidate != durable MemoryRecord
```

Any attempted durable pre-response write is rejected and traced as `MEMORY.PRE_RESPONSE_STABLE_WRITE`.

`MemoryAdmissionPort` remains deprecated compatibility only.

### Memory Foundation owner

Future Memory Foundation owns durable Long-Term Memory only.

Canonical semantic memory kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

`RELATIONSHIP`, `AFFECTIVE` and `GOAL` are separate canonical states, not Memory kinds.

If terms such as Core/Recall/Archival are retained, they represent access/retention tiers, not semantic memory kinds.

Semantic changes use `supersede()`; metadata-only changes do not rewrite semantic history.

### Affective Engine

Owns appraisal/current emotional state, not RelationshipState and not consent.

Hard rules:
- runtime-provided relationship summaries cannot become canonical RelationshipState;
- persistent affect is clamped to upstream persistence authorization;
- unauthorized persistent output is blocked and traced as `AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION`;
- does not write Memory directly;
- current desire/arousal is not stable sexual interest and is not consent.

### RelationshipState owner/controller

Separate authority. Currently `NON_CABLATO` in Assembling.

Stable directional dimensions may eventually include trust, affinity, attraction, sexual interest, intimacy and comfort.

These do not establish current consent.

### Intimacy / Consent resolver

Separate reserved MIP domain. Currently `NON_CABLATO`.

Owns contextual interpretation of current consent/boundary/coercion/roleplay state when implemented.

Hard invariant:

```text
sexualInterest HIGH + currentDesire HIGH != consent GRANTED
```

Dominance or consensual role language alone does not imply coercion.

### Matrix Decision Layer

Canonical behavior owner. Full BDI-lite + Utility remains `NON_CABLATO`; Prompt/Affective/GGUF must not silently become the decision owner.

Cross-owner persistent changes must eventually be represented as typed proposals/events rather than direct mutation.

### Prompt Builder

Realization-only translator. It may preserve resolved semantic invariants but cannot select behavioral policy or acquire truth, memory, relationship, affective or consent authority.

### GGUF

Natural-language realization only. It cannot write Memory, mutate canonical state or override resolved semantic constraints.

### Output Validator

`OutputValidatorPort` is an explicit post-GGUF boundary.

- when supplied, it executes after generation;
- when absent, `DiagnosticTrace.tags["output.validation"] = "NON_CABLATO"`;
- the real semantic validator implementation remains `NON_CABLATO`.

### Persistent Consolidation

`PersistentConsolidationPort` names the future final durable stage after accepted output/action result:
- Memory Admission/write through Memory owner;
- persistent affect commit through Affective owner;
- Relationship update through Relationship owner/controller;
- other owner-specific durable changes;
- lifecycle/causal trace.

It coordinates commit; it does not become owner of all state.

No implementation is currently connected.

## Adult/intimacy rule

Adult/intimacy is a first-class semantic domain, not an automatic block or persistence penalty.

MIP explicitly separates:

```text
stable sexual/romantic relationship dimension
!= current desire/arousal
!= current consent/boundary state
!= historical Memory
```

Adult/intimacy semantic presence alone must not trigger censorship, automatic confidence downgrade, automatic Memory penalty or automatic Affective penalty.

## Change-control

A component change is complete only when the same workstream updates affected wiring, adapters/contracts, tests, active documentation and `WORK_CONTINUITY.md`.

MIP semantic changes are made only in `docs/MATRIX_INTERMODULE_PROTOCOL.md` under explicit Assembling work. Module repositories conform to a MIP version; they do not create competing definitions.

Current checkpoint is documentation/architecture only. No new runtime module is authorized by this alignment.