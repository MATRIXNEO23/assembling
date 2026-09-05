# Matrix Assembly Plan

Status: CANONICAL ASSEMBLING PLAN  
Date: 2026-09-05  
Global architecture source: `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`  
Canonical intermodule protocol: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## Goal

Preserve the modules already built while converging on one authoritative runtime direction and one universal intermodule semantic language.

```text
User / World observation
→ NLU / Understanding
→ TurnWorkspace / MatrixContextSnapshot
→ context ENRICH
   ├─ Memory index probe — ALWAYS
   ├─ Relationship snapshot when wired
   ├─ Affective snapshot when wired
   ├─ World/perceived-state snapshot when wired
   └─ other registered domains when wired
→ Coherence / Authority / Belief resolution
→ MemoryPreflightPort
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ OutputValidatorPort
→ PersistentConsolidationPort
```

Logical stages do not require strictly serial execution; independent context reads may run in parallel.

The older `contracts/pipeline/prompt` path remains a deprecated compatibility facade only.

## Canonical intermodule protocol

`docs/MATRIX_INTERMODULE_PROTOCOL.md` is the Assembling-owned cross-module semantic contract.

Hard rule:

```text
ONE SEMANTIC LANGUAGE
ONE CONTEXT FORMAT
ONE ENTITY REFERENCE MODEL
ONE TEMPORAL MODEL
ONE PROVENANCE MODEL
ONE CONFIDENCE TAXONOMY
ONE TRACE/REASON-CODE LANGUAGE
```

Shared terms such as `subject`, `target`, `owner`, `perspective`, `source`, `authority`, `context`, `unknown`, `contradiction` and `supersede` may not be redefined independently by NLU, Memory, Affective, Relationship or future modules.

Current Kotlin types remain implementation predecessors/compatibility surfaces and will migrate incrementally. This checkpoint does not authorize a large rewrite.

## Fundamental separation

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS
```

- Understanding extracts semantic evidence and preserves every claim.
- Belief/Authority decide how evidence is interpreted.
- Memory records history/knowledge; it is not BeliefState, RelationshipState or AffectiveState.
- `MemoryPreflightPort` is non-persistent current-turn evaluation.
- durable Memory Admission belongs only to future `PersistentConsolidationPort`.
- Affective owns appraisal/current emotion, not Relationship or consent.
- stable sexual/romantic relationship dimensions are distinct from current desire/arousal and from current consent.
- Matrix decision logic owns behavioral choice.
- GGUF realizes language; it does not own truth, memory, consent, relationship or decisions.

## Phase 1 — Ingress / Understanding

Status: **WIRED / REGRESSION-TESTED / MIP MIGRATION PARTIAL**.

Input:
- raw user/world observation;
- language/session/speaker metadata.

Output:
- primary compatibility `NluOutput`;
- all `TypedClaim` candidates;
- primary `SemanticFrame`;
- provenance/confidence/source spans;
- optional explicit adult/intimacy semantic marker.

Hard rules:
- Understanding does not authorize stable memory or create World Truth;
- no secondary claim is discarded;
- unresolved subject remains unresolved;
- speaker/source/subject/target/owner/perspective remain distinct;
- explicit adult/intimacy marker is preferred over the compatibility fallback;
- downstream modules do not re-parse free text to reinvent linguistic semantics already represented structurally.

## Phase 2 — Turn workspace and universal context read

Status: **TURN FRAME WIRED / MIP CONTEXT TYPES NON_CABLATO**.

Current `MatrixTurnFrame` is the implementation precursor to the MIP `TurnWorkspace` concept. It remains temporary operational state for the current turn and is not durable storage.

Target context model:

```text
TypedClaims
→ MatrixContextSnapshot v1
→ parallel/bounded domain reads
   ├─ Memory index probe — ALWAYS
   ├─ RelationshipState snapshot when wired
   ├─ AffectiveState snapshot when wired
   ├─ World/perceived-state snapshot when wired
   └─ Goal/other registered domain snapshots when wired
→ MatrixContextSnapshot v2
```

Universal context rules:
- context is immutable/read-only;
- canonical state remains owned by exactly one module;
- a non-wired domain is `NOT_WIRED`, never represented by fake zero/default values;
- `NO_MATCH` is distinct from backend/module `UNAVAILABLE`;
- snapshots are versioned and traceable.

### Memory retrieval rule

Every normal turn performs a lightweight Memory index probe once structured semantics/current context are available:

```text
EVERY TURN → MEMORY INDEX PROBE
```

This does not mean loading the entire Memory store.

Target levels:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — relevant hits only
LEVEL 3 DEEP_OR_MULTI_HOP — explicit complex retrieval purpose only
```

Retrieval is a universal service contract, not a future Reflection-specific subsystem.

## Phase 3 — Contextual resolution

Status: **BASIC COHERENCE/AUTHORITY WIRED**.

Current behavior:
- critical confidence keys are checked on every `TypedClaim`;
- a missing/low key in a secondary claim also fails closed;
- third-party reports remain indirect;
- multi-claim turns preserve every claim, remain transient, and expose `sourceType=MULTI_CLAIM`;
- unresolved subjects/owners remain held.

MIP clarifications:
- interpretation confidence, source reliability, authority, belief confidence and retrieval relevance are separate concepts;
- contradiction is not supersession;
- temporal change is not contradiction by default;
- Authority/Belief resolution identifies contradiction identity; Memory Admission does not invent semantic conflict from text difference.

Still `NON_CABLATO`:
- memory-backed contradiction checks;
- per-claim final AuthorityResolution;
- typed BeliefState;
- complete contextual consent/intimacy resolver.

## Phase 4 — Memory preflight

Status: **WIRED / NON-PERSISTENT**.

```text
MemoryPreflightPort.evaluate
→ PROVISIONAL_CLAIM / NO_MEMORY_BACKEND / REJECTED
→ stableWrite=false
→ memoryIds=[]
```

`MemoryAdmissionPort` remains deprecated compatibility only. The orchestrator rejects attempted pre-response persistence.

Under MIP:

```text
pre-response MemoryCandidate != durable MemoryRecord
```

## Phase 5 — Affective appraisal

Status: **WIRED / GUARDED**.

- RelationshipState remains external;
- runtime relationship projections are ignored;
- unauthorized persistent affect is clamped and diagnosed;
- adult/intimacy is not a blanket persistence penalty;
- current desire/arousal is not stable sexual interest and is not consent.

## Phase 6 — Relationship / intimacy contextual domains

Status: **NON_CABLATO / PROTOCOL RESERVED**.

MIP reserves distinct domains:

```text
RELATIONSHIP
= relatively stable directional relationship state

AFFECTIVE
= current/short-lived emotional state

INTIMACY
= current contextual consent/boundary/coercion/roleplay state
```

Hard invariant:

```text
sexualInterest HIGH + currentDesire HIGH != consent GRANTED
```

No currently wired module may fake these missing canonical domains.

## Phase 7 — Matrix decision

Status: **NON_CABLATO**.

The future Matrix Decision layer owns behavioral choice. Prompt Builder remains realization-only.

Cross-owner state changes must eventually use typed proposals/events rather than direct mutation.

## Phase 8 — Response realization and validation

Prompt/GGUF status: **WIRED WITH PLACEHOLDER GGUF**.

`OutputValidatorPort` is an explicit optional boundary after GGUF:
- supplied validator executes after generation;
- absence is recorded as `output.validation=NON_CABLATO`;
- real semantic validation remains future work.

## Phase 9 — Persistent consolidation

Status: **PORT DEFINED / IMPLEMENTATION NON_CABLATO**.

Only after accepted output/action may `PersistentConsolidationPort` later coordinate:
- Memory Admission / Long-Term write through Memory owner;
- persistent affect commit through Affective owner;
- RelationshipState update through Relationship owner/controller;
- other owner-specific durable changes;
- causal trace / lifecycle records.

`PersistentConsolidationPort` coordinates commit; it does not become owner of all state.

## Memory Foundation production migration policy

Status: **CANONICAL DIRECTION / IMPLEMENTATION DEFERRED**.

The validated Python Memory Foundation v3 must be preserved as a frozen **reference oracle**, not deleted and not used as the Android production runtime.

Target production implementation:

```text
Python Memory Foundation v3
= frozen reference/oracle
        ↓ contract parity
Kotlin / Room Memory Foundation
= future production implementation
        ↓ fault-injection + regression gates
PersistentConsolidationPort
= only allowed runtime integration point for durable writes
```

Hard invariants to preserve when Kotlin/Room is implemented:
- semantic Memory kinds are `EPISODIC`, `SEMANTIC`, `REFLECTION`;
- Relationship/Affective/Goal are separate states, not Memory kinds;
- `revisionOf` always points to the lineage root;
- `supersededBy` preserves the sequential revision chain;
- semantic changes use `supersede()`, never metadata update;
- contradiction identity is explicit and comes from Authority/Belief resolution, not text-difference inference;
- Authority identifies `contradictsMemoryId`, Memory Admission consumes that decision and owns SAVE/SUPERSEDE/REJECT/IGNORE;
- contradiction does not automatically mean supersession;
- atomic rollback and lineage protection must be demonstrated with fault-injection tests;
- no durable Memory Foundation API may bypass `PersistentConsolidationPort`.

Production implementation policy:
- Kotlin/Room may start directly from the current canonical schema if no real legacy database requires migration;
- do not recreate historical migrations solely to imitate the Python reference history;
- do not use destructive migration as a normal fallback for persistent Luna memory;
- Kotlin/Room must pass contract-parity tests against the frozen Python oracle before runtime integration;
- keep a fast core gate plus the full extended regression suite.

Authority direction:
- prefer structured Matrix-NLU evidence over regex/text parsing when those fields are available;
- Authority/Belief Resolver remains a resolver of epistemic/source authority, not a text parser and not a persistence writer.

Not canonical yet and must be audited before implementation:
- exact Room entities/DAO signatures;
- exact physical index design;
- JSON versus normalized actors/entities representation;
- transaction-manager/API shape;
- concrete self-FK/delete policy;
- timestamp storage units;
- exact retention/access tier implementation.

## DiagnosticTrace

Status: **WIRED THROUGH IMPLEMENTED COGNITIVE MODULES**.

It records observable input/output/decision/reason-code evidence and preserves the first divergence. No private chain-of-thought is stored.

MIP adds correlation/causation, context snapshot identity, domain availability and reason-code namespace semantics to adopt incrementally.

## Completed hardening

1. Understanding no longer owns durable memory admission.
2. All NLU claims are preserved.
3. Critical confidence is fail-closed on every claim.
4. Third-party and multi-claim authority are explicit.
5. Pre-response memory is a named non-persistent preflight contract.
6. Durable memory output before validation is rejected.
7. Affective cannot own RelationshipState or exceed persistence authorization.
8. Prompt Builder is realization-only.
9. Legacy pipeline is deprecated for new callers.
10. Optional output-validation and future consolidation boundaries are named explicitly.
11. Explicit adult/intimacy NLU marker is supported without removing the compatibility fallback.
12. MIP-1.0 now defines the universal cross-module semantic/context contract.

## Next work after the MIP design checkpoint

Do not immediately rewrite the runtime.

When Assembling implementation resumes explicitly:

```text
1. introduce minimal typed MIP core primitives required by current boundaries;
2. add MatrixContextSnapshot/read-only context contract;
3. add always-on Memory index-probe read contract without persistence;
4. migrate current compatibility strings/nulls behind adapters;
5. preserve all existing P0/P1/P2 regressions;
6. implement additional domains only when their real module is ready.
```

Memory schema/design can now be specified separately against MIP before implementation.

## Change-control rule

Any approved component change must update, in the same workstream:
- module wiring;
- code/adapter contracts when implementation is authorized;
- tests when code changes;
- continuity;
- conflicting active documentation.

Global shared semantics are changed in MIP under explicit Assembling work. Other repositories conform to a MIP version; they do not create competing definitions.