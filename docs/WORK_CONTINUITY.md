# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:15+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `runtime-frame-mip-slots-v1`  
Continuity schema: `matrix.assembling.continuity.v60`

## Mandatory continuity policy

Single canonical restart file. Update at every meaningful checkpoint/CI/architecture decision and before STOP.

## Completed Authority baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = MERGED/GREEN
shared MIP evidence PR #12 = MERGED/GREEN
Authority runtime DTO PR #13 = MERGED/GREEN
real Authority resolver PR #14 = MERGED/GREEN
Authority compatibility PR #15 = MERGED/GREEN
canonical Authority runtime adapter PR #16 merge = 7a772412570237260130bd4062555c6449feaf46
runtime adapter post-merge CI = 33961639272 SUCCESS
runtime adapter continuity = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
runtime adapter continuity CI = 33961726792 SUCCESS
```

Other repos = READ-ONLY. Memory writes/admission = NOT TOUCHED.

## ACTIVE TASK — RUNTIME FRAME MIP EVIDENCE SLOTS ONLY

```text
branch = runtime-frame-mip-slots-v1
base = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
PR = #17
orchestrator rewiring = NOT AUTHORIZED
```

### Canonical additive slot contract

Multi-claim-safe cardinality:

```text
contextSnapshot: MipField<MatrixContextSnapshot>
retrievalResults: MipField<List<RetrievalResult>>
canonicalAuthorityResolutions: MipField<List<AuthorityResolution>>
```

Defaults = `UNAVAILABLE`. Successful retrieval with no match = outer PRESENT containing `RetrievalResult(status=NO_MATCH)`, never outer NO_MATCH/UNAVAILABLE collapse.

### Functional checkpoints

```text
0b5e1643604a87066006715e9e2da65df1d37965
= cardinality correction / task start

c6c7f8b734dfe9fb94124a6149e016ee92a27abb
= add canonical MatrixTurnFrame slots + invariants/helpers

77402674b7ba4851b8334687f895b441b2edbc01
= add MatrixTurnFrameCanonicalSlotsTest

1a670a1dc5c818f21cda9143cdf0f44581b18e36
= self-review fix: append new fields after historical constructor parameters to preserve positional source compatibility
```

Changed functional files:

```text
src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt
src/test/kotlin/matrix/assembling/MatrixTurnFrameCanonicalSlotsTest.kt
```

Frame invariants:

- historical parameter order preserved exactly; new fields appended with defaults;
- context PRESENT must match frame turn/session;
- context outer NO_MATCH forbidden;
- retrieval outer NO_MATCH/AMBIGUOUS/CONFLICTED forbidden;
- retrieval PRESENT requires PRESENT context, non-empty explicit results, unique query IDs;
- canonical Authority PRESENT requires PRESENT context;
- typed claim IDs unique when canonical Authority present;
- Authority resolution IDs unique and at most one current resolution per claim;
- Authority resolutions cover exactly current typedClaims;
- Authority resolution contextSnapshotId must equal current snapshot;
- legacy `authorityDecision` and canonical Authority remain independent/no auto-sync.

Helpers:

```text
requireCanonicalContextSnapshot()
requireCanonicalRetrievalResults()
requireCanonicalAuthorityResolutions()
requireCanonicalAuthorityForClaim(claimId)
```

Tests cover legacy default UNAVAILABLE, context mismatch, inner/outer NO_MATCH semantics, retrieval context/nonempty requirements, multi-claim Authority preservation, exact claim coverage, snapshot identity, legacy/canonical independence and copy preservation.

### PR / CI evidence

Verified diff contains exactly:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt
src/test/kotlin/matrix/assembling/MatrixTurnFrameCanonicalSlotsTest.kt
```

PR #17:

`Add multi-claim-safe canonical MIP slots to MatrixTurnFrame`

Green tested PR head before this doc update:

`6888be62a25337eca8c528c5ee768b66827d3958`

Full regression:

```text
CI = 33961999192 SUCCESS
job = kotlin-tests SUCCESS
Run tests = SUCCESS
```

The green full suite confirms existing legacy call sites still compile after the positional-order compatibility fix.

This documentation update creates a new final PR head. Merge only after CI for that exact head is green.

## Hard boundaries

```text
no MatrixAssemblingOrchestrator behavior change
no BasicAuthorityResolver replacement/removal
no AuthorityResolverPort replacement/removal
no root AuthorityDecision redesign/removal
no Memory Admission
no MemoryRepository dependency/write
no PersistentConsolidation
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = runtime-frame-mip-slots-v1
PR = #17
base = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
last green tested head before doc update = 6888be62a25337eca8c528c5ee768b66827d3958
CI = 33961999192 SUCCESS
runtime MIP slots = IMPLEMENTED / TESTED GREEN / FINAL DOC HEAD CI PENDING
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
NEXT = VERIFY FINAL DOC HEAD CI; MERGE #17 ONLY IF GREEN; POST-MERGE MAIN CI; FINALIZE CONTINUITY; STOP BEFORE ORCHESTRATOR REWIRE
```
