# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:27+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-resolver-v1`  
Continuity schema: `matrix.assembling.continuity.v47`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update it after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
post-merge DTO CI = 33956522738 SUCCESS
pre-resolver continuity = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
pre-resolver continuity CI = 33957143955 SUCCESS
```

Hard rules remain: one writable repo (`assembling`); MIP is the single semantic protocol; no gate weakening; no Memory writes/admission/orchestrator rewiring in this task.

## ACTIVE TASK — REAL AUTHORITY RESOLVER ONLY

```text
branch = authority-resolver-v1
base = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
PR = #14
other repos modified = false
```

### Checkpoint 1 — read-only candidate evidence projection

```text
commit = 389872dd24bd485a4873d0d8de6ccea63171248a
file = src/main/kotlin/matrix/assembling/authority/AuthorityCandidateEvidence.kt
```

`AuthorityCandidateEvidencePort` exposes `read(memoryRef, contextSnapshot)` only; there is no persistence mutation API. `AuthorityCandidateEvidence` is a normalized read projection, not a MemoryRecord/admission DTO.

### Checkpoint 2 — deterministic AUTHORITY-1.0 resolver

```text
initial = 0015c36020f196258d2936a5731bbf7bb2cf5022
pre-CI self-review fix = 88f5f4945fc265318abdcf96121d1340c45c5894
file = src/main/kotlin/matrix/assembling/authority/AuthorityResolver.kt
```

Implemented `AuthorityResolver` + `DeterministicAuthorityResolver` with structured classification, conservative same-slot contradiction detection, explicit retrieval/error/ambiguity handling, deterministic AUTHORITY.* reasons, and BELIEF_AUTHORITY output provenance. No natural-language reparsing or retrieval-score truth heuristic exists.

### Checkpoint 3 — P0 resolver tests

```text
commit = 8657a3041121df03b16ab35408c4665afcb0e7c3
file = src/test/kotlin/matrix/assembling/authority/AuthorityResolverTest.kt
```

Coverage includes WORLD provenance, false WORLD self-grant, OBSERVATION, REPORT, INFERENCE, BELIEF, unrelated predicates, same-slot conflicts, opposite polarity, temporal change, historical unresolved identity, correction semantics, SUPERSEDED exclusion, ambiguity, unresolved evidence uniqueness protection, NO_MATCH vs UNAVAILABLE, unresolved source short-circuit, and read-only port surface.

### Diff / PR checkpoint

Verified diff from base to pre-PR continuity head contains exactly:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/AuthorityCandidateEvidence.kt
src/main/kotlin/matrix/assembling/authority/AuthorityResolver.kt
src/test/kotlin/matrix/assembling/authority/AuthorityResolverTest.kt
```

No orchestrator, BasicAdapters, MipBridge, Memory file or other repository changed.

PR #14:

`Implement deterministic AUTHORITY-1.0 resolver`

PR-open head before this documentation update:

`aa55032ae33cacfecd288c38b3dc5b0ecc422105`

Full repository CI is the merge gate. Merge remains forbidden until the exact final PR head is green.

## Explicitly NOT implemented / not authorized

```text
MemoryRepository dependency
Memory writes
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
PersistentConsolidation
BasicAuthorityResolver replacement
root AuthorityDecision migration
final MipBridge migration
orchestrator rewiring
retrieval engine
other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-resolver-v1
PR = #14
base = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
PR-open head = aa55032ae33cacfecd288c38b3dc5b0ecc422105
real AuthorityResolver = CODE + TESTS ADDED / PR OPEN / CI PENDING
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = NOT_STARTED
other repos = READ-ONLY
NEXT = INSPECT PR #14 CI; FIX ONLY TASK-INTRODUCED FAILURES; MERGE ONLY IF FINAL HEAD GREEN
```
