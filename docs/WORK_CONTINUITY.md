# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:20+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v61`

## Mandatory continuity policy

Single canonical restart file. Update at every meaningful checkpoint/CI/architecture decision and before STOP.

## Completed Authority / MIP baseline — DO NOT REDO

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

## RUNTIME FRAME MIP EVIDENCE SLOTS — COMPLETE / INTEGRATED / TESTED

PR:

`#17 — Add multi-claim-safe canonical MIP slots to MatrixTurnFrame`

Final PR head:

`998ebd8ca86ed6bc03091dba126747d47589dd36`

PR final-head CI:

```text
33962069458 = SUCCESS
```

Merge SHA:

`566751798d5ea2dc93db5a01039715f785b04d00`

Post-merge main CI:

```text
33962117105 = SUCCESS
job = kotlin-tests SUCCESS
Run tests = SUCCESS
```

Functional commits:

```text
0b5e1643604a87066006715e9e2da65df1d37965
= cardinality correction / task start

c6c7f8b734dfe9fb94124a6149e016ee92a27abb
= add canonical MatrixTurnFrame slots + invariants/helpers

77402674b7ba4851b8334687f895b441b2edbc01
= add MatrixTurnFrameCanonicalSlotsTest

1a670a1dc5c818f21cda9143cdf0f44581b18e36
= self-review fix preserving historical positional constructor parameter order
```

Integrated additive canonical runtime slots:

```text
contextSnapshot: MipField<MatrixContextSnapshot>
retrievalResults: MipField<List<RetrievalResult>>
canonicalAuthorityResolutions: MipField<List<AuthorityResolution>>
```

All three new fields are appended after the historical `diagnostics` constructor parameter and have defaults, preserving legacy constructor source compatibility.

Canonical defaults:

```text
contextSnapshot = UNAVAILABLE
retrievalResults = UNAVAILABLE
canonicalAuthorityResolutions = UNAVAILABLE
```

Successful no-match is represented only as:

```text
retrievalResults = PRESENT([
  RetrievalResult(status = NO_MATCH, ...)
])
```

not as outer NO_MATCH/UNAVAILABLE collapse.

Integrated invariants:

- PRESENT context must match frame turnId/sessionId;
- context outer NO_MATCH forbidden;
- retrieval outer NO_MATCH/AMBIGUOUS/CONFLICTED forbidden;
- PRESENT retrieval requires PRESENT context, non-empty explicit results, unique query IDs;
- PRESENT canonical Authority requires PRESENT context;
- claim IDs unique while canonical Authority is present;
- resolution IDs unique;
- at most one current Authority resolution per claim;
- canonical Authority resolutions cover exactly current typedClaims;
- all Authority resolutions reference current contextSnapshotId;
- legacy `authorityDecision` and canonical Authority are independent; no synchronization/collapse.

Integrated helpers:

```text
requireCanonicalContextSnapshot()
requireCanonicalRetrievalResults()
requireCanonicalAuthorityResolutions()
requireCanonicalAuthorityForClaim(claimId)
```

Regression coverage proves:

```text
old/minimal constructors still compile and default canonical slots to UNAVAILABLE
context mismatch rejected
outer vs inner NO_MATCH distinction preserved
retrieval requires context and explicit result
multi-claim frame preserves claim-wise AuthorityResolution values
missing/unknown Authority claim coverage rejected
snapshot mismatch rejected
legacy and canonical Authority remain independent
copy() preserves canonical slots/statuses
```

No orchestrator, BasicAuthorityResolver, AuthorityResolverPort, Memory component or other repository was modified.

## Current architecture state

```text
Canonical Context slot in MatrixTurnFrame = READY
Canonical Retrieval results slot = READY / MULTI-RESULT
Canonical AuthorityResolution slot = READY / MULTI-CLAIM
CanonicalAuthorityRuntimeAdapter = READY
DeterministicAuthorityResolver = READY
legacy AuthorityDecision = STILL COMPATIBILITY-ONLY
orchestrator uses canonical resolver = false
Memory Admission = NOT IMPLEMENTED HERE
MemoryRepository = NOT TOUCHED
```

## NEXT BOUNDED CHECKPOINT — ORCHESTRATOR CANONICAL AUTHORITY REWIRE DESIGN/IMPLEMENTATION

This is the next controlled task, but it has NOT started.

Required approach:

- preserve compatibility path until canonical prerequisites are present;
- add a canonical Authority stage that consumes the new frame Context/Retrieval slots and resolves every current claim explicitly;
- no implicit first-claim selection;
- write only `canonicalAuthorityResolutions`, not legacy `AuthorityDecision` as a lossy projection;
- legacy `BasicAuthorityResolver` may remain as fallback/compatibility during migration until tests prove safe retirement;
- no Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE in same checkpoint;
- no MemoryRepository writes;
- add integration tests proving `NLU/Understanding -> frame context/retrieval -> canonical Authority` while pre-response Memory boundary remains unchanged.

Before starting that task, review whether the canonical stage belongs behind a new dedicated runtime port rather than mutating legacy `AuthorityResolverPort` semantics in place.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
runtime frame MIP slots merge = 566751798d5ea2dc93db5a01039715f785b04d00
post-merge frame-slot CI = 33962117105 SUCCESS
AUTHORITY-1.0 = FROZEN
shared MIP evidence = COMPLETE / INTEGRATED / TESTED
DeterministicAuthorityResolver = COMPLETE / INTEGRATED / P0 TESTED
Authority compatibility projections = COMPLETE / TESTED
CanonicalAuthorityRuntimeAdapter = COMPLETE / TESTED
MatrixTurnFrame canonical MIP slots = COMPLETE / INTEGRATED / TESTED
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = OWNER REVIEW / ORCHESTRATOR CANONICAL AUTHORITY REWIRE AS SEPARATE CHECKPOINT
```
