# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T09:56+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-kotlin-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v23`

## Mandatory continuity policy

This is the single canonical restart file for the active Assembling workstream. Update after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Work rules

```text
writable repository = MATRIXNEO23/assembling only
MIP = single cross-module semantic authority
MipBridge = single common interop bridge
new functional module = dedicated directory/package
parallel protocol/context/adapter family = forbidden
cosmetic mass refactor = forbidden
gate/test weakening = forbidden
```

Other repositories are read-only until explicitly selected by the owner.

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

Inventory, contract mapping, incompatibility matrix, MipBridge audit, structural cleanup, legacy quarantine, round-trip tests and documentation are complete. No files moved/renamed/deleted.

Compatibility-only legacy paths:

```text
contracts/MatrixAssemblyContracts.kt
pipeline/MatrixAssemblyPipeline.kt
prompt/SemanticFrameToPrompt.kt
coherence/CoherenceGuard.kt
```

### MIP-1.0

Canonical file: `docs/MATRIX_INTERMODULE_PROTOCOL.md`.

Hard distinctions preserved:

```text
TypedClaim != Belief != Memory
Memory != State != Context
Relationship != Affective != Intimacy
SexualInterest != CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability != Authority != BeliefConfidence != RetrievalRelevance
```

Roles remain distinct: speaker, observer, source, subject, target, owner, perspective.

MIP field states:

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

### MipBridge

Canonical file: `src/main/kotlin/matrix/assembling/mip/MipBridge.kt`.

Authority contradiction seam remains fail-closed. Python arbitrary int -> Kotlin Long uses checked range conversion. Current `MipAuthorityResolutionV1` remains transition/compatibility only.

## AUTHORITY-1.0 contract freeze — COMPLETE

Canonical file: `docs/MIP_AUTHORITY_CONTRACT.md`.

```text
contract = MIP-1.0 / AUTHORITY-1.0
status = FROZEN
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
freeze continuity = 9210773030afc96f631d0e0c0a3a669bf6a6c2f5
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
```

Boundary:

```text
TypedClaim
-> Authority Resolver
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Durable persistence occurs only after VALIDATE through PersistentConsolidationPort. Authority never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE.

Frozen `EpistemicClass`:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Frozen `AuthorityResolutionStatus`:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

Confidence concepts remain independent. Concrete contradiction identity requires a real VALID same-slot candidate, same normalized predicate, compatible temporal scope, true incompatible value/target/polarity and one uniquely safe target. Shared actor/entity or text difference alone never proves contradiction.

Historical Python Authority material is oracle/compatibility evidence only. MIP owns the contract. Root `AuthorityDecision` is compatibility-only. `BasicAuthorityResolver` remains a placeholder and does not detect semantic contradictions.

## Current phase — Kotlin Authority contract types only

```text
branch = authority-kotlin-contracts-v1
phase start/main = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
phase-start main CI = 33952808037 SUCCESS
resolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = false
other repositories modified = false
```

Critical shared dependencies still absent:

```text
MatrixContextSnapshot runtime = NOT_IMPLEMENTED
RetrievalResult runtime = NOT_IMPLEMENTED
ProvenanceRef runtime = NOT_IMPLEMENTED
```

Therefore no Authority-private Context/Retrieval/Provenance substitutes are allowed. Real `AuthorityResolveRequest` and full `AuthorityResolution` runtime DTO remain deferred until shared MIP runtime types exist.

### Checkpoint 0 — phase start

Commit: `a2bc196f6391f4e43f9ae37ef3ad2d9e49bf05af`

Recorded scope, dependencies and exact restart point.

### Checkpoint 1 — frozen Authority value types

Functional commit: `177cf9db9031f5416d01399318df01c788275f43`

File added:

`src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt`

Contains only:

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

Invariants: confidence values [0,1], MemoryRef opaque/nonblank, AUTHORITY-1.0 reason-code constants centralized. No resolver, Memory access, contradiction algorithm, shared-type duplication, DTO migration, bridge migration or orchestrator change.

Continuity after checkpoint 1: `8f399ec430c1f1ecbe518b5620bd03152f842b3c`.

### Checkpoint 2 — contract tests

Functional commit: `0f4c719d26b28f206f71bb797e84e8fa724acbf8`

File added:

`src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt`

Tests lock exact EpistemicClass/status enums, confidence range/NaN rejection, SourceReliability range, MemoryRef opacity/nonblank behavior, and frozen AUTHORITY reason-code namespace. No existing test changed or weakened.

Continuity after checkpoint 2: `e0b0c6b8eb62a0f3ed2d44bb67e6db453a964c7e`.

### Checkpoint 3 — PR opened / scope verified

PR:

```text
PR #11
head branch = authority-kotlin-contracts-v1
base = main
PR head before this continuity commit = e0b0c6b8eb62a0f3ed2d44bb67e6db453a964c7e
```

Compare from phase base confirmed exactly three changed paths before this continuity update:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt
src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt
```

No MipBridge, root DTO, orchestrator, Memory, NLU, Affective, legacy runtime or other-repository file changed.

Current next action:

```text
run/inspect full PR CI
if failure: fix only contract-phase regression and checkpoint continuity
if green: checkpoint CI evidence, merge PR #11, verify post-merge main CI, checkpoint continuity, STOP before next phase
```

## Explicitly NOT IMPLEMENTED

```text
AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime
full AuthorityResolution runtime DTO requiring shared Context/Retrieval/Provenance
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

## Phase completion gates

- dedicated `authority/` package only;
- frozen enum/value semantics preserved;
- no business logic;
- no parallel shared types;
- contract tests PASS;
- all existing tests PASS;
- CI GREEN;
- other repositories modified = false;
- continuity updated after each checkpoint.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-kotlin-contracts-v1
base/main = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
PR = #11
last functional commit = 0f4c719d26b28f206f71bb797e84e8fa724acbf8
current action = inspect PR #11 full CI
resolver = NOT_STARTED
context/retrieval/provenance runtime = NOT_IMPLEMENTED
other repos = READ-ONLY
```

Do not redo cleanup or AUTHORITY-1.0 freeze.