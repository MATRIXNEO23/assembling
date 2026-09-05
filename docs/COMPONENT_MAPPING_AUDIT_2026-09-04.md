# Component Mapping Audit — 2026-09-05

Status: CURRENT EVIDENCE / P0+P1+P2 LOCAL FIXES VERIFIED  
Scope: `MATRIXNEO23/assembling` only.

Canonical wiring remains in `docs/MODULE_CONNECTIONS.md`.

## Audited boundaries

```text
Matrix-NLU runtime
→ UnderstandingLabAdapter
→ MatrixTurnFrame / all TypedClaim values
→ Coherence
→ Authority
→ MemoryPreflightPort
→ Affective
→ Prompt / GGUF placeholder
→ optional OutputValidatorPort
→ future PersistentConsolidationPort
```

## Findings and current status

### F1 — Resolved semantic fields preservation
Status: FIXED / TESTED.

Resolved subject/target/owner/perspective/object/source metadata is preserved in `TypedClaim`.

### F2 — Multi-claim preservation and authority
Status: FIXED / TESTED.

All NLU claims remain in `MatrixTurnFrame.typedClaims`.

Current bounded behavior:

```text
multiple claims
→ all claims preserved
→ SAFE_TRANSIENT_ONLY
→ Authority sourceType=MULTI_CLAIM
→ direct authority rejected
```

This avoids both silent claim loss and misleading first-claim authority while per-claim final resolution remains `NON_CABLATO`.

### F3 — Understanding memory authority
Status: FIXED.

Understanding preserves evidence but never authorizes durable memory. `SemanticFrame.stableMemoryAllowed` remains compatibility-only and is false in the real adapter.

### F4 — Third-party report authority
Status: FIXED / TESTED.

`THIRD_PARTY_REPORT` reaches Coherence/Authority and cannot become direct authority merely because confidence is high.

### F5 — Critical confidence missing or low
Status: FIXED / TESTED FOR PRIMARY AND SECONDARY CLAIMS.

The guard validates these keys on every `TypedClaim`:

```text
token.negation
sequence.predicate
sequence.subjectReferent
sequence.targetReferent
```

A missing or low key in any claim produces a hold and identifies the exact source, for example:

```text
claim[1].token.negation
```

No missing critical value defaults to certainty.

### F6 — Unresolved subject
Status: FIXED / TESTED.

An unresolved subject remains `UNKNOWN`; it is not converted to the speaker.

### F7 — Pre-response persistent memory boundary
Status: FIXED / TESTED / CONTRACT NAMED EXPLICITLY.

Authoritative pre-response contract:

```text
MemoryPreflightPort.evaluate
```

Invariant:

```text
stableWrite == false
memoryIds == []
```

The orchestrator rejects violations as `MEMORY.PRE_RESPONSE_STABLE_WRITE`.

`MemoryAdmissionPort` is deprecated compatibility only. `PersistentConsolidationPort` names the future post-validation durable boundary and has no current implementation.

### F8 — Affective vs Relationship ownership
Status: FIXED / TESTED.

Affective runtime output cannot override canonical RelationshipState.

### F9 — Unauthorized persistent affect
Status: FIXED / TESTED.

Persistent affect is clamped to upstream authorization. Violations are traced as `AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION`.

### F10 — DiagnosticTrace
Status: IMPLEMENTED / INTEGRATION TESTED THROUGH AFFECTIVE.

The existing trace records structured observable snapshots, reason codes and a write-once `firstDivergence`. No parallel trace or hidden chain-of-thought storage was introduced.

### F11 — Output validation boundary
Status: PORT WIRED / REAL VALIDATOR NON_CABLATO.

`OutputValidatorPort` executes after GGUF when supplied. Without an implementation, the turn records:

```text
output.validation=NON_CABLATO
```

This makes the missing validator explicit without simulating one.

### F12 — Adult/intimacy signal source
Status: IMPROVED / TESTED.

The NLU contract may now emit an explicit `adultOrIntimacy` semantic marker. Understanding prefers that marker and retains the local keyword logic only as backward-compatible fallback.

Adult/intimacy remains semantic context, not a censorship or persistence gate.

### F13 — Prompt and legacy pipeline authority
Status: FIXED / TESTED.

`SemanticFrameToPrompt` is realization-only. The old `MatrixAssemblyPipeline` is deprecated for new callers and remains compatibility/testing material.

## Test evidence

Files:
- `ArchitectureBoundaryTest.kt`;
- `P1BoundaryTest.kt`;
- `DiagnosticTraceTest.kt`;
- `MatrixAssemblingOrchestratorIntegrationTest.kt`;
- `PromptBoundaryTest.kt`;
- existing adapter and compatibility tests.

New/extended coverage:
- secondary-claim missing confidence fails closed;
- multi-claim Authority exposes `MULTI_CLAIM` and rejects direct authority;
- authoritative `MemoryPreflightPort` boundary rejects durable output;
- explicit NLU adult/intimacy marker is preserved;
- output validator executes only after a GGUF reply exists;
- absent validator is explicitly `NON_CABLATO`.

## CI evidence

Relevant earlier green runs:
- P0: `33906844505`, `33907038217`;
- P1 + DiagnosticTrace: `33907887621`, `33908146792`;
- realization-only prompt cleanup: `33908498023`.

Current remaining-boundary code/test run:
- `33942024450` — `Matrix Assembling CI` — SUCCESS.

No threshold or test assertion was weakened to obtain PASS.

## Dependency/coupling result

- no direct NLU/Understanding → MemoryRepository dependency;
- no Authority → repository write;
- no Affective → Relationship authority;
- no GGUF → persistence dependency;
- no durable writer accepted by the pre-response port;
- compatibility interfaces are preserved but explicitly deprecated;
- no new external library or runtime dependency introduced.

## Residual risks / NON_CABLATO

- explicit Working Context/read layer;
- real Long-Term retrieval;
- per-claim final contextual Authority/Belief resolution;
- real semantic OutputValidator implementation;
- real PersistentConsolidation implementation;
- real Memory Foundation adapter and atomic commit;
- canonical RelationshipState controller;
- BDI-lite + Utility Decision layer;
- real GGUF bridge;
- Android runtime integration.

## Verdict

All locally actionable P0/P1/P2 findings identified in this audit block are fixed with bounded changes and covered by tests. Missing large modules remain explicit `NON_CABLATO` boundaries rather than hidden placeholders or competing authorities.
