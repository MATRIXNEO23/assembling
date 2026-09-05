# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:05+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `runtime-frame-mip-slots-v1`  
Continuity schema: `matrix.assembling.continuity.v58`

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

### Cardinality correction before implementation

The previous provisional plan named singular retrieval/Authority slots. That would be unsafe because:

```text
one observation may contain multiple TypedClaims
Authority resolves claim-wise
MIP TurnWorkspace describes retrieval queries/results and coherence/authority resolutions as plural operational evidence
```

Therefore the safe additive frame contract for this checkpoint is:

```text
contextSnapshot: MipField<MatrixContextSnapshot>
retrievalResults: MipField<List<RetrievalResult>>
canonicalAuthorityResolutions: MipField<List<AuthorityResolution>>
```

This is a correction of the implementation plan, not a change to MIP-1.0 or AUTHORITY-1.0.

### Planned default semantics

The current legacy orchestrator has no canonical Context/Retrieval/Authority provider wired, so default state for all three new slots is:

```text
UNAVAILABLE
```

Hard:

```text
UNAVAILABLE != NO_MATCH
```

A successful retrieval that found nothing must later be represented as:

```text
retrievalResults = PRESENT([
  RetrievalResult(status = NO_MATCH, ...)
])
```

not as an unavailable/empty placeholder.

### Planned frame invariants

- legacy constructor calls remain valid because fields are additive with defaults;
- PRESENT context snapshot must match frame turnId/sessionId;
- PRESENT retrievalResults requires PRESENT contextSnapshot;
- retrieval query IDs must be unique within the current frame slot;
- PRESENT canonicalAuthorityResolutions requires PRESENT contextSnapshot;
- authority resolution IDs must be unique;
- current authority resolutions must have unique claimId values;
- every authority resolution claimId must exist in `typedClaims`;
- every authority resolution contextSnapshotId must equal current snapshotId;
- legacy `authorityDecision` and canonical Authority resolutions remain independent; no auto-sync/collapse;
- no fake context, fake retrieval success, or fake no-contradiction state is created.

### Hard boundaries

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

### Planned tests

```text
old/minimal MatrixTurnFrame constructor -> all canonical slots UNAVAILABLE
PRESENT context mismatch turn/session -> rejected
PRESENT retrieval list without context -> rejected
PRESENT RetrievalResult(NO_MATCH) preserves outer PRESENT + inner NO_MATCH
multi-claim frame preserves multiple canonical AuthorityResolution values
unknown authority claimId -> rejected
authority resolution snapshot mismatch -> rejected
legacy authorityDecision does not populate canonical resolutions
canonical resolutions do not populate legacy authorityDecision
copy() preserves canonical slots/statuses
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = runtime-frame-mip-slots-v1
base = 65d3b9b407d0b861454bc66ba2d010da28d14a4f
cardinality audit = COMPLETE
frame slot code = NOT STARTED
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
NEXT = ADD MULTI-CLAIM-SAFE CANONICAL MIP SLOTS TO MatrixTurnFrame + TESTS
```
