# Component Mapping Audit — Integration Hardening

Date: 2026-09-05  
Repository: `MATRIXNEO23/assembling`  
Scope: authoritative `MatrixTurnFrame` path only.

## Contracts audited

```text
Matrix-NLU
→ UnderstandingLabAdapter
→ MatrixTurnFrame / TypedClaim
→ BasicCoherenceGuard
→ BasicAuthorityResolver
→ MemoryPreflightPort
→ AffectiveLabAdapter
→ SemanticFrameToPrompt
→ GgufPort
```

## P0 findings and minimal fixes

### P0-01 — Secondary claims were discarded

**Files:** `MatrixTurnFrame.kt`, `UnderstandingLabAdapter.kt`, `BasicAdapters.kt`  
**Cause:** the adapter used only `claims.firstOrNull()` and emitted one `TypedClaim`.

**Minimal justified multi-file fix:**
- add backward-compatible `nluClaims` to the turn envelope;
- retain `nlu` as the primary compatibility view;
- map every runtime claim to `TypedClaim`;
- hold multi-claim input transiently until per-claim Authority is wired.

**Result:** no claim is silently discarded; stable admission remains blocked for partially resolved multi-claim turns.

### P0-02 — Missing critical confidence failed open

**File:** `BasicAdapters.kt`  
**Cause:** missing confidence keys defaulted to `1.0`.

**Fix:** require all critical keys:

```text
token.negation
sequence.predicate
sequence.subjectReferent
sequence.targetReferent
```

Missing keys now produce:

```text
LOW_CONFIDENCE_HOLD
firstDivergence = COHERENCE.MISSING_CRITICAL_CONFIDENCE
```

### P0-03 — Memory Admission contract was placed before response

**Files:** `IntegrationPorts.kt`, `MatrixAssemblingOrchestrator.kt`, non-persistent memory adapters, memory docs  
**Reason multi-file was necessary:** the bug was in the public boundary and orchestration order, not in one adapter.

**Fix:**
- introduce `MemoryPreflightPort` for pre-response evaluation;
- deprecate `MemoryAdmissionPort` as compatibility only;
- reserve `PersistentConsolidationPort` for future post-validation durable writes;
- orchestrator rejects `stableWrite=true` or non-empty memory IDs during preflight.

**Result:** a future durable backend cannot be inserted legitimately into the pre-response slot.

## P1 findings and minimal fixes

### P1-01 — Affective could impersonate RelationshipState

**File:** `AffectiveLabAdapter.kt`  
**Fix:** ignore runtime `relationshipSummary`; expose `RelationshipState NON_CABLATO` and retain external ownership.

### P1-02 — Affective trusted an unauthorized persistent result

**File:** `AffectiveLabAdapter.kt`  
**Fix:** accept persistence only when both upstream authorization and runtime result are true. A violating runtime is clamped and traced as:

```text
AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION
```

### P1-03 — Unknown subject became the speaker

**File:** `UnderstandingLabAdapter.kt`  
**Fix:** preserve `UNKNOWN`; Coherence holds it rather than inventing identity.

### P1-04 — Memory documentation contradicted the new commit boundary

**Files:** `MEMORY_INTEGRATION_POLICY.md`, `MEMORY_INTEGRATION_STATUS.md`  
**Fix:** document preflight vs final consolidation and canonical placeholder statuses.

### P1-05 — Prompt Builder contained hidden decision logic

**File:** `SemanticFrameToPrompt.kt`  
**Fix:** retain only semantic realization invariants. Behavioral deliberation remains owned by the future Matrix Decision layer.

### P1-06 — Output validator boundary absent

**Files:** `IntegrationPorts.kt`, `MatrixAssemblingOrchestrator.kt`  
**Fix:** add optional `OutputValidatorPort`; when absent, trace `NON_CABLATO` explicitly. Full semantic validation remains a residual risk.

## P2 findings and minimal fixes

### P2-01 — Compatibility pipeline appeared authoritative

**File:** `pipeline/MatrixAssemblyPipeline.kt`  
**Fix:** mark class deprecated for new callers; preserve existing tests and behavior.

### P2-02 — Adult/intimacy marker depended only on local keywords

**Files:** `MatrixTurnFrame.kt`, `UnderstandingLabAdapter.kt`  
**Fix:** add an optional explicit NLU marker and prefer it. Keep the legacy fallback until the runtime always emits the field.

## DiagnosticTrace implementation

`DiagnosticTrace` now records typed snapshots for:

```text
INPUT
OBSERVATION / NLU
UNDERSTANDING
COHERENCE
AUTHORITY
MEMORY PREFLIGHT
MEMORY
AFFECTIVE
PROMPT
GGUF
OUTPUT VALIDATION
```

Each snapshot contains observable input/output metadata, decision, status and deterministic reason codes. `reasoningChain` is not private chain-of-thought. `firstDivergence` is write-once.

Pipeline exceptions carry the failed `MatrixTurnFrame`, allowing tests to inspect the exact stage and first broken contract.

## Tests added/updated

`ComponentMappingCompatibilityTest` covers:
- resolved field preservation;
- third-party report handling;
- multi-claim preservation;
- missing confidence fail-closed;
- canonical negation confidence;
- unresolved subject preservation;
- affective persistence gates;
- relationship projection rejection.

`MatrixAssemblingOrchestratorTest` covers:
- complete implemented-stage trace;
- pre-response write rejection;
- module-exception first divergence;
- first-divergence immutability.

Local compile and direct execution result before CI:

```text
13 targeted tests PASS
main Kotlin sources compile PASS
```

## Architectural boundary verdict

- NLU/Understanding direct memory write: **not introduced**.
- Authority direct repository write: **not introduced**.
- Affective Relationship ownership: **blocked**.
- Pre-response durable memory: **blocked/fail-closed**.
- Legacy pipeline as new authority: **deprecated**.

## Residual risks

1. Real per-claim Authority/Belief resolution is not yet wired; multi-claim turns are preserved but transient.
2. Real semantic Output Validator remains `NON_CABLATO`.
3. Real Persistent Consolidation and Memory Foundation remain `NON_CABLATO`.
4. Adult/intimacy explicit marker depends on the future runtime contract; keyword fallback remains temporary.

CI status: pending final branch run.
