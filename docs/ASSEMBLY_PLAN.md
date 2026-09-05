# Matrix Assembly Plan

Status: CANONICAL ASSEMBLING PLAN  
Date: 2026-09-05

## Goal

Preserve existing modules while converging on one authoritative, diagnosable runtime path.

```text
User / World observation
→ NLU / Understanding
→ Working Memory / MatrixTurnFrame
→ context retrieval
→ Coherence / Authority / Belief resolution
→ non-persistent memory preflight
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ output validation
→ persistent consolidation
```

The older `contracts/pipeline/prompt` path remains compatibility-only and is deprecated for new callers.

## Fundamental separation

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

## Phase 1 — Ingress / Understanding

Current state: **WIRED / HARDENED**.

Input:
- raw text;
- locale/session/speaker/observer metadata.

Output:
- primary compatibility `NluOutput`;
- complete `nluClaims` list;
- primary `SemanticFrame`;
- complete `TypedClaim` list;
- confidence and provenance evidence.

Rules:
- no stable-memory authority;
- no invented subject when unresolved;
- no silent loss of secondary claims;
- explicit adult/intimacy marker preferred over local fallback.

## Phase 2 — Working Memory and context read

Current state: **MatrixTurnFrame WIRED; external retrieval NON_CABLATO**.

Working Memory is temporary and not durable storage. Future reads may include Long-Term memory, RelationshipState, Affective snapshot, World/perceived state and recent referents.

## Phase 3 — Contextual resolution

Current state: **BASIC COHERENCE/AUTHORITY WIRED**.

Implemented safeguards:
- missing critical confidence fails closed;
- unresolved owner/subject remains held;
- third-party reports cannot become direct authority;
- multi-claim inputs remain transient until per-claim resolution exists.

Still future:
- real memory-backed contradiction checks;
- per-claim AuthorityResolution;
- full consent/intimacy contextual resolver.

## Phase 4 — Memory preflight

Current state: **WIRED / NON-PERSISTENT**.

```text
MemoryPreflightPort.evaluate
→ PROVISIONAL_CLAIM / NO_MEMORY_BACKEND / REJECTED
→ stableWrite=false
→ memoryIds=[]
```

The orchestrator fails closed on attempted pre-response persistence.

`MemoryAdmissionPort` is compatibility-only and deprecated. Real durable Memory Admission belongs to Phase 8.

## Phase 5 — Affective appraisal

Current state: **WIRED / GUARDED**.

Implemented safeguards:
- RelationshipState remains external;
- runtime relationship projections are ignored;
- unauthorized persistent affect is clamped and diagnosed;
- adult/intimacy is not a blanket persistence penalty.

## Phase 6 — Matrix decision

Current state: **NON_CABLATO**.

The future Matrix Decision layer owns behavioral choice. Until then, Prompt Builder may only preserve semantic invariants and must not become a hidden Behavior Engine.

## Phase 7 — Response realization and validation

Prompt/GGUF state: **WIRED WITH PLACEHOLDER GGUF**.  
Output validator boundary: **DEFINED; REAL SEMANTIC VALIDATOR NON_CABLATO**.

Prompt Builder preserves negation, referents, time, source and consent boundaries without inventing facts or behavioral goals.

## Phase 8 — Persistent consolidation

Current state: **CONTRACT DEFINED / NON_CABLATO**.

Only after accepted output may this stage later perform:
- Memory Admission / MemoryRepository write;
- persistent affect commit;
- RelationshipState update through its owner;
- causal/lifecycle trace.

## DiagnosticTrace

Current state: **WIRED END-TO-END FOR IMPLEMENTED PHASES**.

Each phase records observable input, output, decision, reason codes and status. `firstDivergence` identifies the first broken contract and is never overwritten.

## Completed hardening block

- preserve all NLU claims;
- fail closed on missing critical confidence;
- separate memory preflight from durable consolidation;
- stop unresolved-subject → speaker fallback;
- clamp Affective persistence;
- ignore Affective RelationshipState projection;
- restrict Prompt Builder to semantic realization;
- mark compatibility pipeline deprecated;
- add canonical orchestrator integration tests.

## Next work after this gate

1. keep CI green;
2. add explicit Working Context/read ports;
3. implement per-claim contextual resolution;
4. connect real output semantic validation;
5. connect real Memory Foundation only at `PersistentConsolidationPort`;
6. keep all missing phases visibly `NON_CABLATO`.
