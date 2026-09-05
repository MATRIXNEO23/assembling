# Matrix Assembly Plan

Status: CANONICAL ASSEMBLING PLAN  
Date: 2026-09-05  
Global architecture source: `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`

## Goal

Preserve the modules already built while converging on one authoritative runtime direction.

```text
User / World observation
→ NLU / Understanding
→ Working Memory of the current turn
→ context retrieval
→ Coherence / Authority / Belief resolution
→ MemoryPreflightPort
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ OutputValidatorPort
→ PersistentConsolidationPort
```

The older `contracts/pipeline/prompt` path remains a deprecated compatibility facade only.

## Fundamental separation

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

- Understanding extracts semantic evidence and preserves every claim.
- Belief/Authority decide how evidence is interpreted.
- `MemoryPreflightPort` is non-persistent current-turn evaluation.
- durable Memory Admission belongs only to future `PersistentConsolidationPort`.
- Affective evaluates emotional consequences but does not own RelationshipState.
- Matrix decision logic owns behavioral choice.
- GGUF realizes language; it does not own truth, memory or decisions.

## Phase 1 — Ingress / Understanding

Status: **WIRED / REGRESSION-TESTED**.

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
- unresolved subject remains `UNKNOWN`;
- explicit adult/intimacy marker is preferred over the compatibility fallback.

## Phase 2 — Working Memory and context read

Status: **TURN FRAME WIRED / EXTERNAL READ PORTS NON_CABLATO**.

Working Memory is temporary operational state for the current turn. It is not durable storage.

Future bounded reads:
- relevant Long-Term memories;
- RelationshipState snapshot;
- AffectiveState snapshot;
- World/perceived-state snapshot;
- recent active referents/context.

## Phase 3 — Contextual resolution

Status: **BASIC COHERENCE/AUTHORITY WIRED**.

Current behavior:
- critical confidence keys are checked on every `TypedClaim`;
- a missing/low key in a secondary claim also fails closed;
- third-party reports remain indirect;
- multi-claim turns preserve every claim, remain transient, and expose `sourceType=MULTI_CLAIM`;
- unresolved subjects/owners remain held.

Still `NON_CABLATO`:
- memory-backed contradiction checks;
- per-claim final AuthorityResolution;
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

## Phase 5 — Affective appraisal

Status: **WIRED / GUARDED**.

- RelationshipState remains external;
- runtime relationship projections are ignored;
- unauthorized persistent affect is clamped and diagnosed;
- adult/intimacy is not a blanket persistence penalty.

## Phase 6 — Matrix decision

Status: **NON_CABLATO**.

The future Matrix Decision layer owns behavioral choice. Prompt Builder remains realization-only.

## Phase 7 — Response realization and validation

Prompt/GGUF status: **WIRED WITH PLACEHOLDER GGUF**.

`OutputValidatorPort` is now an explicit optional boundary after GGUF:
- supplied validator executes after generation;
- absence is recorded as `output.validation=NON_CABLATO`;
- real semantic validation remains future work.

## Phase 8 — Persistent consolidation

Status: **PORT DEFINED / IMPLEMENTATION NON_CABLATO**.

Only after accepted output may `PersistentConsolidationPort` later coordinate:
- Memory Admission / Long-Term write;
- persistent affect commit;
- RelationshipState update through its owner/controller;
- causal trace / lifecycle records.

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
- `revisionOf` always points to the lineage root;
- `supersededBy` preserves the sequential revision chain;
- semantic changes use `supersede()`, never metadata update;
- contradiction identity is explicit and comes from Authority resolution, not text-difference inference;
- Authority identifies `contradictsMemoryId`, Memory Admission consumes that decision and owns SAVE/SUPERSEDE/REJECT/IGNORE;
- atomic rollback and lineage protection must be demonstrated with fault-injection tests;
- no durable Memory Foundation API may bypass `PersistentConsolidationPort`.

Production implementation policy:
- Kotlin/Room may start directly from the current canonical schema if no real legacy database requires migration;
- do not recreate historical migrations solely to imitate the Python reference history;
- do not use destructive migration as a normal fallback for persistent Luna memory;
- Kotlin/Room must pass contract-parity tests against the frozen Python oracle before runtime integration;
- keep a fast core gate plus the full extended regression suite; do not delete validated edge-case tests merely to reduce the core gate.

Authority direction:
- prefer structured Matrix-NLU evidence (`claimKind`, dialogue act, owner, perspective, source/provenance) over regex/text parsing when those fields are available;
- Authority Resolver remains a resolver of epistemic/source authority, not a text parser and not a persistence writer.

Not canonical yet and must be audited before implementation:
- exact Room entities/DAO signatures;
- exact `MemoryCategory` enum;
- FTS design;
- JSON versus normalized actor/entity persistence;
- transaction-manager shape;
- concrete FK/delete policy;
- timestamp storage units.

## DiagnosticTrace

Status: **WIRED THROUGH IMPLEMENTED COGNITIVE MODULES**.

It records observable input/output/decision/reason-code evidence and preserves the first divergence. No private chain-of-thought is stored.

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

## Next work after this gate

1. keep the full CI suite green;
2. introduce explicit Working Context/read ports without persistence;
3. implement per-claim contextual resolution;
4. implement a real semantic `OutputValidatorPort`;
5. connect Memory Foundation only through `PersistentConsolidationPort`;
6. preserve current regression and DiagnosticTrace invariants.

## Change-control rule

Any approved component change must update, in the same workstream:
- module wiring;
- code/adapter contracts;
- tests;
- continuity;
- conflicting active documentation.
