# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:48+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-compatibility-v1`  
Continuity schema: `matrix.assembling.continuity.v52`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update after every meaningful checkpoint, CI result, architecture decision, before risky operations and before STOP/session end.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
real Authority resolver PR #14 = b7237542259d86c26632b2185d7e90691e82141f
resolver post-merge CI = 33957882144 SUCCESS
resolver continuity = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
resolver continuity CI = 33957996637 SUCCESS
```

Canonical `DeterministicAuthorityResolver` is implemented/P0-tested but deliberately not orchestrator-wired. Memory writes/admission remain untouched.

## ACTIVE TASK — AUTHORITY COMPATIBILITY / MIP BRIDGE COMPLETION ONLY

```text
branch = authority-compatibility-v1
base = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
PR = #15
other repos modified = false
```

### Compatibility audit decision

Legacy `MipAuthorityResolutionV1` and root `AuthorityDecision` cannot losslessly represent canonical `AuthorityResolution`. They remain unchanged compatibility/quarantine surfaces. No forced canonical conversion is permitted.

### Checkpoint 1 — canonical contradiction-only projections

```text
commit = 56e94328da2f57f895365911ad2ada711d7be462
file = src/main/kotlin/matrix/assembling/mip/MipAuthorityCompatibility.kt
```

Added:

```text
AuthorityResolution.toKotlinMemoryContradictionProjection()
AuthorityResolution.toPythonContradictionProjection()
```

Only COMPLETE resolutions may project. PRESENT IDs must retain exact canonical decimal identity. Kotlin additionally requires in-range Long. NOT_APPLICABLE becomes native null/None. Opaque IDs, noncanonical decimals and incomplete statuses fail closed.

The Python function is explicitly a contradiction-only projection, not a claim of full historical Python AuthorityResolution compatibility.

### Checkpoint 2 — compatibility gates

```text
commit = 95d2720c6cd800749071e263a3b46ebb48dd31e9
file = src/test/kotlin/matrix/assembling/mip/MipAuthorityCompatibilityTest.kt
```

Tests cover native absence, exact numeric identity, opaque-ID rejection, Kotlin overflow vs Python arbitrary integer, noncanonical decimal rejection, and PARTIAL/HOLD projection rejection.

### Diff / PR checkpoint

Verified diff from base contains exactly:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/mip/MipAuthorityCompatibility.kt
src/test/kotlin/matrix/assembling/mip/MipAuthorityCompatibilityTest.kt
```

PR #15:

`Add fail-closed canonical Authority compatibility projections`

PR-open head before this documentation update:

`0d704c9d49bddc559d0b1897833aa0a11a01913c`

Full repository CI is the merge gate. Merge is forbidden until the exact final PR head is green.

### Hard boundaries

```text
no orchestrator rewiring
no BasicAuthorityResolver replacement
no root AuthorityDecision redesign
no Memory Admission implementation
no MemoryRepository dependency/write
no Python schema invention beyond known contradiction-only wire
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-compatibility-v1
PR = #15
base = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
PR-open head = 0d704c9d49bddc559d0b1897833aa0a11a01913c
compatibility projections = CODE + TESTS ADDED / PR OPEN / CI PENDING
legacy MipAuthorityResolutionV1 = QUARANTINED / UNCHANGED
orchestrator rewiring = NOT STARTED
other repos = READ-ONLY
NEXT = INSPECT PR #15 FINAL-HEAD CI; FIX ONLY TASK-INTRODUCED FAILURES; MERGE ONLY IF GREEN
```
