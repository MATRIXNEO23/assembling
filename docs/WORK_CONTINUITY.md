# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T20:45+02:00
Repository: `MATRIXNEO23/assembling`
Working branch: `p0-boundary-fixes-20260904`
Continuity schema: `matrix.assembling.continuity.v6`
Baseline main before this workstream: `863c21628198a9fe66334c5f06c384c8a9b9bb31`
PR: `#2` — P0 module-boundary hardening
Pre-fix CI: `33906637932` — FAILURE as expected (3 P0 regressions reproduced)
Post-fix CI: `33906844505` — SUCCESS

## Canonical work rules

Repository-local canonical workflow file: `PROJECT_WORK_RULES.md`.

Hard rules:
- one repository at a time unless the owner explicitly says otherwise;
- current active repository is `MATRIXNEO23/assembling`;
- other repositories may be read for dependency/context checks but must not be written without explicit authorization;
- historical/older repositories are backup/checkpoint sources, not normal development targets;
- do not delete or rewrite backup repositories;
- when a component changes in the active repository, update affected code, tests, local canonical docs and continuity in the same workstream.

## Canonical document order inside Assembling

1. `README.md` — repository purpose and authoritative path;
2. `docs/README.md` — document index and status;
3. `docs/MODULE_CONNECTIONS.md` — canonical module wiring;
4. `docs/ASSEMBLY_PLAN.md` — current implementation plan;
5. `docs/WORK_CONTINUITY.md` — exact operational restart point;
6. audit/evidence documents only for supporting history.

The old `contracts/pipeline/prompt` path remains compatibility/testing code. It is not a second architectural authority.

## Canonical principle

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

## Current canonical direction in Assembling

```text
User / World observation
→ NLU / Understanding
→ Working Memory / Semantic Draft
→ context read
→ Coherence / Authority / Belief resolution
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ output validation
→ persistent consolidation
```

Not every target phase is implemented yet. Missing stages must be marked `NON_CABLATO`; do not simulate them as real authority.

## Architecture alignment already merged

PR `#1` merged the previous architecture cleanup into `main`.

Key results preserved:
- frame path (`MatrixTurnFrame` + `IntegrationPorts` + `MatrixAssemblingOrchestrator`) is authoritative;
- `contracts/*`, `pipeline/*`, `prompt/*` are compatibility/testing only;
- Understanding does not own durable memory admission;
- canonical negation confidence key is `token.negation`;
- third-party reports reach Authority as indirect source;
- Affective does not own RelationshipState;
- adult/intimacy is semantic context, not an automatic persistence penalty.

## P0 boundary hardening — current workstream

### P0-01 — Multi-claim loss

Before:
`UnderstandingLabAdapter` used only `interpretation.claims.firstOrNull()` and later replaced `typedClaims` with a single claim.

Risk:
A turn such as `Marco viene domani ma Sara non viene` could lose the second claim, including negation/subject/target data.

Fix:
- all NLU claims are now mapped into `MatrixTurnFrame.typedClaims`;
- the first claim remains the compatibility `NluOutput`/`SemanticFrame` view;
- no claim after the first is silently discarded;
- while claim-wise Coherence/Authority is not fully wired, multi-claim turns are `SAFE_TRANSIENT_ONLY`.

Files:
- `src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt`
- `src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt`

Regression test:
`ArchitectureBoundaryTest.understandingPreservesAllClaimsInsteadOfDroppingAfterFirst`

### P0-02 — Critical confidence fail-open

Before:
`BasicCoherenceGuard` used `getOrDefault(..., 1.0)` for critical confidence heads.

Risk:
A missing confidence field could be treated as perfect confidence.

Fix:
- critical confidence keys are mandatory;
- missing key => `LOW_CONFIDENCE_HOLD`;
- diagnostic tag records `coherence.missing_critical_confidence`;
- threshold checks use `getValue()` only after presence validation.

Critical keys currently enforced:
- `token.negation`;
- `sequence.predicate`;
- `sequence.subjectReferent`;
- `sequence.targetReferent`.

Regression test:
`ArchitectureBoundaryTest.missingCriticalConfidenceFailsClosed`

### P0-03 — Durable memory result before response

Before:
`MemoryAdmissionPort.admit()` is still called on the current pre-response compatibility path.

Current architectural rule:
real durable write belongs only after output validation in Persistent Consolidation.

Minimal fix without changing public interfaces:
- `MatrixAssemblingOrchestrator` now enforces a hard pre-response boundary;
- before Affective/Prompt/GGUF, memory result must have:

```text
stableWrite == false
memoryIds == []
```

Any violation raises immediately and prevents the turn from proceeding as if persistence were valid.

This does not create the future real consolidation port; that stage remains `NON_CABLATO`.

Regression test:
`ArchitectureBoundaryTest.orchestratorRejectsStableWriteBeforeResponsePhase`

## P0 verification evidence

Pre-fix test-only run:
- CI `33906637932`;
- 12 tests completed;
- 3 failed;
- failures exactly matched P0-01/P0-02/P0-03.

Post-fix run:
- CI `33906844505`;
- `kotlin-tests` completed;
- conclusion `success`.

No gate/threshold was lowered and no failing test was weakened.

## Current memory state

Assembling still has no real persistent Memory Foundation wired.

Hard runtime rule remains:

```text
stableWrite = false
memoryIds = []
status = NO_MEMORY_BACKEND / PROVISIONAL_CLAIM / REJECTED
```

Working Memory and Long-Term remain separate concepts:

```text
WORKING MEMORY
= temporary current-turn/context state used to understand/respond

LONG-TERM MEMORY
= persistent catalogued information
```

A future real persistent adapter must be connected to post-validation consolidation, not substituted directly into the current pre-response placeholder slot.

## Model state

Student-4-v2.2A:
- controlled runtime candidate;
- not production approved;
- mixed-head-protected artifact is present through Git LFS;
- full artifact SHA-256: `4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5`.

Student-5 remains separate experimental work and does not block Assembling.

No Frozen access or gate reduction is authorized here.

## Still NON_CABLATO / not production-real

- real ONNX Student-4-v2.2A runtime bridge;
- explicit Working Memory/context read layer;
- real Long-Term MemoryRepository adapter;
- canonical RelationshipState controller/port;
- BDI-lite + Utility decision layer;
- Output Semantic Validator;
- explicit Persistent Consolidation / atomic commit port;
- real llama.cpp/MLC GGUF adapter in this repo;
- Android app integration.

## Next exact work target after P0 merge

Continue only in `MATRIXNEO23/assembling`.

Order:
1. merge PR #2 only after final CI remains green;
2. P1 hardening:
   - Affective must ignore any runtime-provided Relationship authority;
   - clamp persistent affect so runtime cannot report persistence when not authorized;
   - unresolved subject must not silently become speaker;
   - update stale memory integration policy;
3. implement structured end-to-end `DiagnosticTrace` without creating a parallel pipeline;
4. add canonical orchestrator integration/smoke tests;
5. only then consider OutputValidator/PersistentConsolidation interface additions.

Before ending every workstream:
- run CI;
- fix only actual failures;
- update local canonical docs and this continuity file.
