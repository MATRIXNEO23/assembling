# Component Mapping Audit — 2026-09-04

Status: CURRENT EVIDENCE / UPDATED AFTER P0+P1 HARDENING
Scope: `MATRIXNEO23/assembling` only.

This audit records the verified integration state. Canonical wiring remains in `docs/MODULE_CONNECTIONS.md`.

## Audited boundaries

```text
Matrix-NLU runtime
→ UnderstandingLabAdapter
→ MatrixTurnFrame / TypedClaim
→ Coherence
→ Authority
→ non-persistent Memory boundary
→ Affective
→ Prompt / GGUF placeholder
```

## Current findings

### F1 — Resolved semantic fields preservation
Status: FIXED / TESTED.

`NluOutput` preserves resolved subject/target/owner/perspective/object/source metadata and `UnderstandingLabAdapter` maps them into `TypedClaim`.

### F2 — Multi-claim preservation
Status: FIXED / TESTED.

All claims emitted by the NLU runtime are retained in `MatrixTurnFrame.typedClaims`. Later claims are not silently discarded.

Until claim-wise Coherence/Authority is fully implemented:

```text
multi-claim turn → SAFE_TRANSIENT_ONLY
```

### F3 — Understanding memory authority
Status: FIXED.

`UnderstandingLabAdapter` does not authorize durable memory. `SemanticFrame.stableMemoryAllowed` is compatibility-only and the real adapter sets it false.

### F4 — Third-party report authority
Status: FIXED / TESTED.

`TypedClaim.sourceType=THIRD_PARTY_REPORT` reaches Coherence/Authority and cannot become direct authority merely because confidence is high.

### F5 — Critical confidence missing
Status: FIXED / TESTED.

Critical confidence is fail-closed. Missing canonical head confidence produces `LOW_CONFIDENCE_HOLD` rather than default confidence 1.0.

### F6 — Unresolved subject
Status: FIXED / TESTED.

An unresolved subject remains `UNKNOWN`; it is not silently converted to the speaker. Coherence holds the turn.

### F7 — Pre-response persistent memory
Status: FIXED / TESTED.

The current compatibility memory call may only return non-persistent state.

Before response/output validation:

```text
stableWrite == false
memoryIds == []
```

`MatrixAssemblingOrchestrator` rejects a violation as `MEMORY.PRE_RESPONSE_STABLE_WRITE`.

### F8 — Affective vs Relationship ownership
Status: FIXED / TESTED.

Affective runtime output cannot override canonical RelationshipState. Relationship ownership remains external/`NON_CABLATO`.

### F9 — Unauthorized persistent affect
Status: FIXED / TESTED.

If the Affective runtime reports a persistent delta when upstream persistence is not authorized, the adapter clamps it to false and records:

```text
AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION
```

### F10 — DiagnosticTrace
Status: IMPLEMENTED / INTEGRATION TESTED THROUGH AFFECTIVE.

The existing `MatrixTurnFrame.diagnostics` now records structured snapshots for:
- input;
- NLU observation;
- Understanding;
- Authority;
- Memory Admission;
- Memory result;
- Affective;
- deterministic reason codes;
- first divergence.

`reasoningChain` contains diagnostic reason codes only, not private chain-of-thought.

`firstDivergence` is write-once.

## Test evidence

P0 regression file:
`src/test/kotlin/matrix/assembling/ArchitectureBoundaryTest.kt`

P1 regression file:
`src/test/kotlin/matrix/assembling/P1BoundaryTest.kt`

Diagnostic contract tests:
`src/test/kotlin/matrix/assembling/DiagnosticTraceTest.kt`

Canonical end-to-end smoke tests:
`src/test/kotlin/matrix/assembling/MatrixAssemblingOrchestratorIntegrationTest.kt`

Smoke coverage includes:
- negation/refusal;
- third-party report;
- request;
- direct assertion with persistence disabled;
- adult/intimacy semantic handling.

## Verified CI evidence

P0 pre-fix:
- run `33906637932`;
- 12 tests, exactly 3 expected failures.

P0 post-fix:
- run `33906844505` — SUCCESS;
- final P0 run `33907038217` — SUCCESS.

P1 pre-fix:
- run `33907204697`;
- 15 tests, exactly 3 expected P1 failures.

P1 + DiagnosticTrace + canonical smoke code:
- run `33907887621` — SUCCESS.

## Remaining architectural gaps

These are `NON_CABLATO`, not hidden authorities:
- explicit Working Context/read layer;
- real Long-Term retrieval;
- real Persistent Consolidation;
- canonical RelationshipState port/controller;
- BDI-lite + Utility Decision layer;
- Output Semantic Validator;
- real GGUF bridge;
- Android runtime integration.

## Verdict

Current connected boundaries are substantially hardened and regression-tested. No real persistent memory backend is present, and no component is authorized to bypass the declared boundaries.
