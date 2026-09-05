# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T09:39+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-kotlin-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v21`

## Mandatory continuity policy

This is the single canonical restart file for the active Assembling workstream.
Update it after every significant checkpoint, including branch/task start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.
Do not create a parallel continuity file for the same workstream.

## Work rules

- only `MATRIXNEO23/assembling` is writable in this workstream;
- other repositories are read-only unless the owner explicitly switches active repository;
- MIP is the only cross-module semantic authority;
- MipBridge is the only common interop bridge;
- no parallel protocol/context/adapter family;
- every new functional module must use its own directory/package;
- no cosmetic mass moves;
- no lowered gates/tests.

## Completed baseline

### MIP cleanup

```text
cleanup start = ef433a3aed519b31efe9289a8df78ed974170510
PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR #8 CI = 33951029064 SUCCESS
post-merge CI = 33951548865 SUCCESS
PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final cleanup CI = 33951808519 SUCCESS
```

Completed: inventory, contract map, incompatibility matrix, MipBridge audit, structural cleanup, legacy quarantine, round-trip tests, documentation.
No files moved/renamed/deleted.

Legacy compatibility-only paths:

```text
contracts/MatrixAssemblyContracts.kt
pipeline/MatrixAssemblyPipeline.kt
prompt/SemanticFrameToPrompt.kt
coherence/CoherenceGuard.kt
```

### Canonical MIP

`docs/MATRIX_INTERMODULE_PROTOCOL.md` = `MIP-1.0`.

Hard invariants include:

```text
TypedClaim != Belief != Memory
Memory != State != Context
Relationship != Affective != Intimacy
SexualInterest != CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability != Authority != BeliefConfidence != RetrievalRelevance
```

Roles remain distinct: `speaker`, `observer`, `source`, `subject`, `target`, `owner`, `perspective`.

General MIP field states:

```text
PRESENT
NOT_APPLICABLE
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
NO_MATCH
ERROR
```

### MIP Bridge

Canonical implementation:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

Authority contradiction seam is fail-closed:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> native null/None
all uncertainty/error states -> fail if native type cannot preserve them
```

Python arbitrary-size int -> Kotlin `Long` uses explicit range validation.
Current `MipAuthorityResolutionV1` remains transition/compatibility only.

## AUTHORITY-1.0 freeze — COMPLETE

Canonical file:

`docs/MIP_AUTHORITY_CONTRACT.md`

```text
Status = CANONICAL MIP AUTHORITY PROFILE / CONTRACT FROZEN
Version = MIP-1.0 / AUTHORITY-1.0
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
freeze continuity = 9210773030afc96f631d0e0c0a3a669bf6a6c2f5
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
```

Frozen boundary:

```text
TypedClaim
-> Authority Resolver
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Durable writes remain only after `VALIDATE -> PersistentConsolidationPort -> Memory Admission -> MemoryRepository`.
Authority never writes Memory and never owns `SAVE/SUPERSEDE/REJECT/IGNORE`.

Frozen `EpistemicClass`:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Frozen Authority confidence separation:

```text
EpistemicClass
!= InterpretationConfidence
!= AuthorityResolutionConfidence
!= SourceReliability
!= BeliefConfidence
!= RetrievalRelevance
```

Frozen conceptual input:

```text
AuthorityResolveRequest
- requestId
- claim: TypedClaim
- contextSnapshot: MatrixContextSnapshot
- retrievalResult?: RetrievalResult
- provenance
```

Frozen conceptual output:

```text
AuthorityResolution
- resolutionId
- claimId
- contextSnapshotId
- retrievalQueryId?
- resolutionStatus
- authority
- authorityResolutionConfidence
- sourceReliability
- contradictedMemoryRef
- candidateMemoryRefs[]
- ambiguityReasons[]
- reasonCodes[]
- provenance
```

`AuthorityResolutionStatus`:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

Contradiction target states preserve the full MIP status vocabulary. Concrete contradiction requires a VALID candidate, same resolved semantic slot/predicate, compatible temporal scope, real incompatible value/target/polarity, and one uniquely safe target. Shared actor/entity or different text alone never proves contradiction.

Python historical Authority material is oracle/compatibility evidence only; no authoritative checksummed `authority_models.py` has been identified. MIP owns the contract.

Current root `AuthorityDecision` is a compatibility predecessor only. `BasicAuthorityResolver` is a conservative placeholder and does not perform semantic contradiction detection.

## Current phase — Kotlin Authority contract types only

```text
phase = AUTHORITY_KOTLIN_CONTRACT_TYPES_ONLY
branch = authority-kotlin-contracts-v1
phase start/base = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
main CI at phase start = 33952808037 SUCCESS
resolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = false
other repositories modified = false
```

Important dependency rule:

```text
MatrixContextSnapshot runtime = NOT_IMPLEMENTED
RetrievalResult runtime = NOT_IMPLEMENTED
ProvenanceRef runtime = NOT_IMPLEMENTED
```

Therefore this phase must not invent Authority-private Context, Retrieval or Provenance substitutes. A real `AuthorityResolveRequest` is deferred until those shared MIP runtime types exist.

### Checkpoint 0 — phase start

Commit:

`a2bc196f6391f4e43f9ae37ef3ad2d9e49bf05af`

Recorded active branch, frozen contract, scope, dependencies and exact restart point.

### Checkpoint 1 — Authority value types

Commit:

`177cf9db9031f5416d01399318df01c788275f43`

Added only:

`src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt`

Implemented contract-only types:

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

Properties:

- confidence wrappers require `[0.0, 1.0]`;
- `MemoryRef` is opaque/nonblank and not numerically typed;
- frozen `AUTHORITY.*` reason codes are centralized;
- no resolver method/class;
- no Memory access;
- no contradiction algorithm;
- no Context/Retrieval/Provenance substitute;
- no current DTO or MipBridge migration;
- no orchestrator change;
- no other repository change.

Current next action:

```text
add contract-only tests for AuthorityTypes.kt
run focused/full regression
update continuity
```

## Still explicitly NOT IMPLEMENTED

```text
real AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime
AuthorityResolution full runtime DTO requiring shared Provenance/Context/ Retrieval
MatrixContextSnapshot runtime
RetrievalQuery/Result runtime
ProvenanceRef runtime
MipBridge final Authority migration
root AuthorityDecision migration
Memory Kotlin/Room
PersistentConsolidation
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
```

## Required gates for this phase

- code remains under dedicated `authority/` package;
- exact frozen enums/vocabulary preserved;
- no business logic;
- no parallel shared types;
- contract unit tests PASS;
- complete existing Assembling regression PASS;
- CI GREEN;
- other repositories modified = false;
- continuity updated after every checkpoint.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-kotlin-contracts-v1
base/main = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
last functional commit = 177cf9db9031f5416d01399318df01c788275f43
last continuity commit = THIS_COMMIT
contract = docs/MIP_AUTHORITY_CONTRACT.md AUTHORITY-1.0 FROZEN
current action = write tests for AuthorityTypes.kt
resolver = NOT_STARTED
context/retrieval/provenance runtime = NOT_IMPLEMENTED
other repos = READ-ONLY
```

Do not redo cleanup or contract freeze.