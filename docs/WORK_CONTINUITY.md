# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:12+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-resolver-v1`  
Continuity schema: `matrix.assembling.continuity.v44`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update it after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Hard work rules

```text
writable repo = MATRIXNEO23/assembling only unless owner explicitly switches
MIP = single cross-module semantic authority
MipBridge = single common interop bridge
new functional module = dedicated directory/package
parallel protocol/context/adapter family = forbidden
gate/test weakening = forbidden
other repositories = read-only
```

## Completed baseline — DO NOT REDO

```text
cleanup PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
cleanup PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
MIP = docs/MATRIX_INTERMODULE_PROTOCOL.md / MIP-1.0
AUTHORITY-1.0 contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
Authority freeze PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
Authority value types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 merge = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
post-merge DTO CI = 33956522738 SUCCESS
pre-resolver continuity commit = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
pre-resolver continuity CI = 33957143955 SUCCESS
```

## ACTIVE TASK — REAL AUTHORITY RESOLVER ONLY

```text
branch = authority-resolver-v1
base = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
AUTHORITY-1.0 = FROZEN
Authority runtime DTOs = COMPLETE / INTEGRATED / TESTED
other repos modified = false
```

Scope:

- implement `AuthorityResolver` + deterministic production implementation under `matrix.assembling.authority`;
- classify WORLD_TRUTH / OBSERVATION / REPORT / INFERENCE / BELIEF from structured MIP evidence only;
- perform conservative contradiction detection against retrieved candidate evidence;
- output canonical `AuthorityResolution`;
- deterministic `AUTHORITY.*` reason codes only;
- no natural-language reparsing;
- no repository writes/admission decisions.

### Read-only candidate evidence decision

`RetrievalResult` intentionally contains candidate IDs/scores, not durable MemoryRecord content. AUTHORITY-1.0 section 13 permits a future read-only evidence port when needed. Therefore this task will add an Authority-owned **read-only projection port**, not a MemoryRepository dependency:

```text
AuthorityCandidateEvidencePort
read(candidate MemoryRef, MatrixContextSnapshot)
-> MipField<AuthorityCandidateEvidence>
```

Rules:

- port exposes reads only; no save/supersede/delete/update methods;
- `AuthorityCandidateEvidence` is a normalized read projection for contradiction comparison, NOT a second MemoryRecord model and NOT persistence state ownership;
- later integration may back the port from hydrated Context/Retrieval evidence or a read-only Memory adapter;
- universal MIP does not depend on a concrete repository implementation.

Candidate projection must preserve at least:

```text
memoryRef
validity status
subject
predicate
object/value
target
owner
perspective
source
polarity
temporal relation
temporal reference key when available
provenance
```

### Conservative contradiction policy

- candidate must be VALID;
- subject and normalized predicate must match;
- owner/scope and source/perspective are compared when semantically applicable;
- temporal scopes must be safely compatible;
- opposite polarity on the same semantic value/target may contradict;
- different values contradict only for explicitly registered single-value predicates;
- different text/shared actor/retrieval score never prove contradiction;
- CURRENT/ATEMPORAL can compare directly; historical/reference relations require enough temporal identity, otherwise assessment remains unresolved;
- multiple concrete contradiction targets -> AMBIGUOUS/HOLD;
- unresolved candidate evidence prevents claiming a unique contradiction;
- correction prioritizes evidence but never bypasses same-slot/VALID/temporal checks.

### Explicitly NOT authorized

```text
MemoryRepository dependency
Memory writes
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
PersistentConsolidation
root AuthorityDecision migration
BasicAuthorityResolver replacement in orchestrator
final MipBridge migration
orchestrator rewiring
retrieval engine
other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-resolver-v1
base = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
real AuthorityResolver = TASK STARTED / NO RESOLVER CODE YET
read-only candidate evidence port = DESIGN DECIDED / NOT YET CODED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = NOT_STARTED
other repos = READ-ONLY
NEXT = IMPLEMENT READ-ONLY EVIDENCE PROJECTION + RESOLVER + P0 TESTS
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, shared MIP evidence contracts, or Authority DTO binding.