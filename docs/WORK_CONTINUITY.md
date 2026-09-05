# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:26+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v62`

## Mandatory continuity policy

Single canonical restart file. Update at every meaningful checkpoint/CI/architecture decision and before STOP.

Canonical work method:

`docs/MATRIX_ENGINE_WORK_METHOD.md`

Hard rules now explicitly include:

```text
CONTRACT BEFORE CODE
ONE OWNER PER STATE
ADAPTER BEFORE DIRECT COUPLING
FAIL CLOSED BEFORE GUESSING
UNIT + CROSS-MODULE + E2E
DIAGNOSTIC TRACE EVERYWHERE
FIX CAUSE, NOT SYMPTOM
NEVER LOWER A GATE
ONE WRITE REPO AT A TIME
NO NEXT MODULE UNTIL CURRENT SUITE IS 100% GREEN
```

## Completed Authority / MIP baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = MERGED/GREEN
shared MIP evidence PR #12 = MERGED/GREEN
Authority runtime DTO PR #13 = MERGED/GREEN
real Authority resolver PR #14 = MERGED/GREEN
Authority compatibility PR #15 = MERGED/GREEN
canonical Authority runtime adapter PR #16 = MERGED/GREEN
runtime frame canonical MIP slots PR #17 merge = 566751798d5ea2dc93db5a01039715f785b04d00
post-merge frame-slot CI = 33962117105 SUCCESS
frame-slot continuity commit = 4efcd9a1d275fa6cf2d5546ea0b0e76f51800897
frame-slot continuity CI = 33962208689 SUCCESS
```

Other repos = READ-ONLY. Memory writes/admission = NOT TOUCHED.

## Canonical work-method checkpoint

Document created:

`docs/MATRIX_ENGINE_WORK_METHOD.md`

Commit:

`c0e49e91a6d89563a080e1aea28df1bc3d872dea`

The method is now binding for future Matrix Engine work. In particular, a downstream integration checkpoint must not start while an upstream canonical semantic boundary is still incomplete.

## Current architecture state

```text
Canonical Context slot in MatrixTurnFrame = READY
Canonical Retrieval results slot = READY / MULTI-RESULT
Canonical AuthorityResolution slot = READY / MULTI-CLAIM
CanonicalAuthorityRuntimeAdapter = READY
DeterministicAuthorityResolver = READY
legacy AuthorityDecision = COMPATIBILITY-ONLY
orchestrator uses canonical resolver = false
Memory Admission = NOT IMPLEMENTED HERE
MemoryRepository = NOT TOUCHED
```

## PRIORITY CORRECTION — UNDERSTANDING BEFORE ORCHESTRATOR REWIRE

Previous continuity proposed the canonical Authority orchestrator rewire as the next task.

Owner review clarified that Understanding V3 still needs canonical implementation/integration before the first Memory/APK path.

Under the canonical work method, the next task is therefore corrected to:

```text
CANONICAL UNDERSTANDING V3 BOUNDARY / LOSSLESS CONTRACT AUDIT
```

Reason:

- `matrix-understanding-lab` now has frozen/implemented `MATRIX_NLU_CONTRACT_V3` structure;
- current Assembling `UnderstandingLabAdapter` is still a compatibility adapter using older `MatrixNluClaim`, legacy `NluOutput`, `SemanticFrame`, root `TypedClaim` and partial inference/fallback behavior;
- current legacy mapping can lose or fail to represent V3 information such as independent `sourceReferent`, claimKind, field status/alternatives, plural evidence spans and temporal anchor semantics;
- canonical Authority must not be rewired on top of a lossy Understanding boundary.

## NEXT BOUNDED CHECKPOINT — UNDERSTANDING V3 LOSSLESS AUDIT FIRST

Before writing implementation code:

1. read `MATRIX_NLU_CONTRACT_V3` and V3 output schema read-only from `MATRIXNEO23/matrix-understanding-lab`;
2. compare every V3 field against current `MipClaimV1` and Assembling runtime types;
3. classify each field as:

```text
LOSSLESS
REPRESENTABLE_WITH_EXISTING_MIP_FIELD
MISSING_FROM_MIP_RUNTIME
LEGACY_ONLY_LOSS
NOT_APPLICABLE_DOWNSTREAM
```

4. STOP implementation if `MipClaimV1` cannot carry frozen V3 semantics without loss;
5. if contract extension is required, make that a separate explicit contract checkpoint before adapter implementation;
6. do not modify `matrix-understanding-lab` from this chat;
7. do not rewire orchestrator or Memory in the same checkpoint.

## Explicitly NOT authorized in the next audit

```text
no matrix-understanding-lab writes
no Student-5 training changes
no orchestrator Authority rewire
no BasicAuthorityResolver replacement
no Memory Admission
no MemoryRepository write/dependency
no PersistentConsolidation
no Reflection
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
work method = docs/MATRIX_ENGINE_WORK_METHOD.md
work-method commit = c0e49e91a6d89563a080e1aea28df1bc3d872dea
frame-slot continuity CI = 33962208689 SUCCESS
AUTHORITY-1.0 = FROZEN
shared MIP evidence = COMPLETE / TESTED
DeterministicAuthorityResolver = COMPLETE / TESTED
CanonicalAuthorityRuntimeAdapter = COMPLETE / TESTED
MatrixTurnFrame canonical MIP slots = COMPLETE / TESTED
Understanding V3 canonical boundary = NOT STARTED
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = UNDERSTANDING V3 LOSSLESS CONTRACT AUDIT ONLY
```
