# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:31+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-resolver-v1`  
Continuity schema: `matrix.assembling.continuity.v48`

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

### Functional checkpoints

```text
389872dd24bd485a4873d0d8de6ccea63171248a
= read-only AuthorityCandidateEvidence + AuthorityCandidateEvidencePort

0015c36020f196258d2936a5731bbf7bb2cf5022
= initial deterministic Authority resolver

88f5f4945fc265318abdcf96121d1340c45c5894
= pre-CI resolver factory/compile self-review correction

8657a3041121df03b16ab35408c4665afcb0e7c3
= P0 Authority resolver tests
```

Resolver semantics:

- trusted WORLD provenance + explicit WORLD_TRUTH -> WORLD_TRUTH;
- trusted PERCEPTION provenance -> OBSERVATION;
- explicit derived INFERENCE provenance -> INFERENCE;
- structured report/self-report -> REPORT;
- structured belief/hypothesis -> BELIEF;
- unresolved authority -> HOLD;
- SourceReliability remains unavailable unless a real provider exists;
- no natural-language reparsing or retrieval-score truth heuristic.

Contradiction semantics:

- only VALID candidate evidence can be a target;
- same resolved subject/predicate/owner/target scope required;
- REPORT source and BELIEF perspective scopes remain distinct;
- temporal changes are not contradictions by default;
- unresolved historical/reference identity stays unresolved;
- opposite polarity on same value can contradict;
- different values contradict only for explicitly registered single-value predicates;
- unrelated predicates never contradict solely because actors overlap;
- multiple concrete targets -> AMBIGUOUS/HOLD;
- unresolved evidence prevents choosing a concrete target;
- correction never bypasses semantic verification.

### P0 regression coverage

Tests cover trusted/fake WORLD_TRUTH, OBSERVATION, REPORT, INFERENCE, BELIEF, unrelated predicates, same-slot conflicts, opposite polarity, temporal change, unresolved historical identity, correction, SUPERSEDED exclusion, multi-target ambiguity, unresolved-evidence uniqueness protection, NO_MATCH vs UNAVAILABLE, unresolved-source short circuit, and read-only evidence-port API.

### PR / CI evidence

Verified task diff contains only:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/AuthorityCandidateEvidence.kt
src/main/kotlin/matrix/assembling/authority/AuthorityResolver.kt
src/test/kotlin/matrix/assembling/authority/AuthorityResolverTest.kt
```

PR #14:

`Implement deterministic AUTHORITY-1.0 resolver`

Final green tested head before this documentation-only update:

`08efb0bdca44303c0a8d052a50ba52cbf0ea3a8c`

Full regression CI:

```text
run = 33957745559
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

No test/gate was weakened. No task-introduced CI repair was needed after the pre-CI self-review correction.

This documentation update creates a new final PR head. Merge remains forbidden until CI for that exact head is also green.

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
last green tested head before doc update = 08efb0bdca44303c0a8d052a50ba52cbf0ea3a8c
CI = 33957745559 SUCCESS
real AuthorityResolver = IMPLEMENTED / P0 TESTED GREEN
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = NOT_STARTED
other repos = READ-ONLY
NEXT = VERIFY FINAL DOC-ONLY HEAD CI; MERGE PR #14 ONLY IF GREEN; THEN VERIFY MAIN CI + FINALIZE CONTINUITY
```
