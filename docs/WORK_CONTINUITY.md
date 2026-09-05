# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:10+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `runtime-frame-mip-slots-v1`  
Continuity schema: `matrix.assembling.continuity.v59`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update after every meaningful checkpoint, CI result, architecture decision, before risky operations and before STOP/session end.

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
runtime adapter continuity commit = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
runtime adapter continuity CI = 33961726792 SUCCESS
```

Other repositories remain read-only. Memory writes/admission remain untouched.

## ACTIVE TASK — RUNTIME FRAME MIP EVIDENCE SLOTS ONLY

```text
branch = runtime-frame-mip-slots-v1
base = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
orchestrator rewiring = NOT AUTHORIZED IN THIS TASK
```

### Checkpoint 1 — cardinality correction

Continuity commit:

`0b5e1643604a87066006715e9e2da65df1d37965`

The provisional singular retrieval/Authority slots were rejected because MIP/runtime is multi-claim. Final additive contract:

```text
contextSnapshot: MipField<MatrixContextSnapshot>
retrievalResults: MipField<List<RetrievalResult>>
canonicalAuthorityResolutions: MipField<List<AuthorityResolution>>
```

Defaults are `UNAVAILABLE`; successful no-match must remain inner `RetrievalResult(status=NO_MATCH)` under outer `PRESENT`.

### Checkpoint 2 — MatrixTurnFrame canonical slots

Initial functional commit:

`c6c7f8b734dfe9fb94124a6149e016ee92a27abb`

File modified:

`src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt`

Implemented additive fields plus helpers:

```text
requireCanonicalContextSnapshot()
requireCanonicalRetrievalResults()
requireCanonicalAuthorityResolutions()
requireCanonicalAuthorityForClaim(claimId)
```

Runtime invariants:

- context outer `NO_MATCH` is forbidden;
- retrieval outer `NO_MATCH/AMBIGUOUS/CONFLICTED` is forbidden because those are result-level states;
- PRESENT context must match frame turnId/sessionId;
- PRESENT retrieval requires PRESENT context, non-empty explicit results, unique query IDs;
- PRESENT Authority requires PRESENT context;
- typedClaims claim IDs must be unique when canonical Authority is PRESENT;
- resolution IDs and resolution claim IDs must be unique;
- canonical resolutions must cover exactly the current typedClaims;
- every resolution must reference the current context snapshot ID;
- legacy `authorityDecision` and canonical Authority remain independent.

### Checkpoint 3 — canonical slot regression tests

Commit:

`77402674b7ba4851b8334687f895b441b2edbc01`

File added:

`src/test/kotlin/matrix/assembling/MatrixTurnFrameCanonicalSlotsTest.kt`

Coverage:

```text
legacy minimal constructor -> all canonical slots UNAVAILABLE
context turn/session mismatch rejected
outer retrieval NO_MATCH rejected, inner NO_MATCH preserved
PRESENT retrieval requires context + non-empty explicit result list
multi-claim frame preserves two canonical AuthorityResolution values
canonical resolutions must cover exactly current typedClaims
unknown/missing claim identity rejected
resolution snapshot mismatch rejected
legacy AuthorityDecision and canonical Authority remain independent
copy() preserves canonical slots/statuses
```

### Checkpoint 4 — positional source compatibility self-review fix

Self-review found a source-compatibility risk before CI: the initial implementation inserted the new fields in the middle of the data-class constructor. Existing positional constructor calls could therefore silently bind later legacy arguments to wrong parameter positions or fail compilation.

Fix commit:

`1a670a1dc5c818f21cda9143cdf0f44581b18e36`

Correction:

- every historical `MatrixTurnFrame` constructor parameter remains in its exact original order;
- all three new canonical fields are appended after the historical `diagnostics` parameter;
- defaults preserve all old constructor call forms;
- semantic slot invariants are unchanged.

This is a compatibility fix only, not an architectural change.

### Current validation state

```text
frame slot code = IMPLEMENTED
slot tests = ADDED
positional compatibility fix = APPLIED BEFORE CI
full repository CI = NOT YET RUN
orchestrator = UNCHANGED
BasicAuthorityResolver = UNCHANGED
Memory = UNCHANGED
other repos = READ-ONLY
```

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
base = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
last frame code commit = c6c7f8b734dfe9fb94124a6149e016ee92a27abb
last test commit = 77402674b7ba4851b8334687f895b441b2edbc01
last compatibility fix = 1a670a1dc5c818f21cda9143cdf0f44581b18e36
runtime MIP slots = CODE + TESTS + POSITIONAL FIX / CI PENDING
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
NEXT = VERIFY DIFF; OPEN PR; RUN FULL CI; FIX ONLY TASK-INTRODUCED FAILURES
```
