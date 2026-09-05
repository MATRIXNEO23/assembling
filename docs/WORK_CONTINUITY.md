# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T09:42+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-kotlin-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v22`

## Mandatory continuity policy

This is the single canonical restart file for the active Assembling workstream. Update it after every significant checkpoint: task/branch start, architecture or contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before any STOP/session end.

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

### Cleanup

```text
start = ef433a3aed519b31efe9289a8df78ed974170510
PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR #8 CI = 33951029064 SUCCESS
post-merge CI = 33951548865 SUCCESS
PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final cleanup CI = 33951808519 SUCCESS
```

Inventory, contract mapping, incompatibility audit, MipBridge cleanup, legacy quarantine, round-trip tests and documentation are complete. No files were moved/renamed/deleted.

Compatibility-only legacy paths remain:

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

Authority contradiction seam is fail-closed. `PRESENT` carries concrete identity; `NOT_APPLICABLE` alone may map to native nullable absence when native null means “no contradiction”; uncertainty/error states may not silently collapse. Python arbitrary int -> Kotlin Long uses checked range conversion.

Current `MipAuthorityResolutionV1` remains transition/compatibility only.

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

Durable persistence still occurs only after VALIDATE through PersistentConsolidationPort. Authority never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE.

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

Confidence concepts remain independent. Contradiction identity requires a real VALID same-slot candidate, same normalized predicate, compatible temporal scope, real incompatible value/target/polarity and one uniquely safe target. Same actor/entity/text difference alone is never sufficient.

Historical Python Authority material is oracle/compatibility evidence only; no authoritative checksummed `authority_models.py` has been identified. MIP owns the contract.

Current root `AuthorityDecision` is compatibility-only. `BasicAuthorityResolver` is a placeholder and does not detect semantic contradictions.

## Current phase — Kotlin Authority contract types only

```text
branch = authority-kotlin-contracts-v1
phase start/main = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
main CI = 33952808037 SUCCESS
resolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = false
other repositories modified = false
```

Critical dependency:

```text
MatrixContextSnapshot runtime = NOT_IMPLEMENTED
RetrievalResult runtime = NOT_IMPLEMENTED
ProvenanceRef runtime = NOT_IMPLEMENTED
```

Therefore no Authority-private Context/Retrieval/Provenance substitutes may be invented, and real `AuthorityResolveRequest`/full `AuthorityResolution` runtime DTOs remain deferred until shared MIP runtime types exist.

### Checkpoint 0 — phase start

`a2bc196f6391f4e43f9ae37ef3ad2d9e49bf05af`

Recorded scope, dependencies and restart point.

### Checkpoint 1 — frozen Authority value types

`177cf9db9031f5416d01399318df01c788275f43`

Added:

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

Invariants:

- AuthorityResolutionConfidence and SourceReliability are normalized [0,1] values;
- MemoryRef is opaque/nonblank, not a numeric protocol type;
- AUTHORITY-1.0 reason codes are centralized;
- no resolver/business logic;
- no Memory access;
- no contradiction algorithm;
- no shared-type duplication;
- no DTO/bridge/orchestrator migration.

Continuity checkpoint after code: `8f399ec430c1f1ecbe518b5620bd03152f842b3c`.

### Checkpoint 2 — contract-only tests

`0f4c719d26b28f206f71bb797e84e8fa724acbf8`

Added:

`src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt`

Tests lock:

```text
EpistemicClass exact enum vocabulary
AuthorityResolutionStatus exact enum vocabulary
AuthorityResolutionConfidence [0,1] + NaN rejection
SourceReliability [0,1] + NaN rejection
MemoryRef opaque/nonblank behavior
AUTHORITY reason-code namespace + frozen-v1 set
```

No existing test was changed or weakened.

## Current next action

```text
open PR for authority-kotlin-contracts-v1
verify changed-file scope
run full repository CI/regression
if green: merge
update continuity with PR/CI/merge evidence
STOP before resolver/shared Context-Retrieval implementation
```

## Explicitly not implemented

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

## Required phase gates

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
last functional commit = 0f4c719d26b28f206f71bb797e84e8fa724acbf8
current action = open PR and run full CI
resolver = NOT_STARTED
context/retrieval/provenance runtime = NOT_IMPLEMENTED
other repos = READ-ONLY
```

Do not redo cleanup or AUTHORITY-1.0 freeze.