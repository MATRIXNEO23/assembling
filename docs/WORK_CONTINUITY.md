# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:00+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-kotlin-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v24`

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

Other repositories remain read-only.

## Stable completed baseline

```text
cleanup start = ef433a3aed519b31efe9289a8df78ed974170510
cleanup PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
cleanup PR CI = 33951029064 SUCCESS
cleanup post-merge CI = 33951548865 SUCCESS
cleanup docs PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
cleanup final CI = 33951808519 SUCCESS
```

MIP cleanup/inventory/contract-map/legacy quarantine/round-trip gates are complete. No files were moved/renamed/deleted.

Canonical protocol: `docs/MATRIX_INTERMODULE_PROTOCOL.md` = MIP-1.0.
Canonical bridge: `src/main/kotlin/matrix/assembling/mip/MipBridge.kt`.

MIP keeps explicit field states:

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

Authority contradiction mapping remains fail-closed; Python arbitrary integer -> Kotlin Long remains range checked.

## AUTHORITY-1.0 freeze — COMPLETE

Canonical file: `docs/MIP_AUTHORITY_CONTRACT.md`.

```text
contract = MIP-1.0 / AUTHORITY-1.0
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
freeze continuity = 9210773030afc96f631d0e0c0a3a669bf6a6c2f5
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
```

Frozen boundary:

```text
TypedClaim -> Authority Resolver -> AuthorityResolution -> Memory Admission -> MemoryRepository
```

Authority never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE.

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

Authority/confidence/source reliability/belief confidence/retrieval relevance remain separate concepts.
Concrete contradiction identity requires a VALID same-slot candidate, same normalized predicate, compatible temporal scope, actual incompatible value/target/polarity and one uniquely safe target. Shared actor/entity/text difference alone is insufficient.

Historical Python Authority material is compatibility/oracle evidence only; MIP owns semantics. Current root `AuthorityDecision` is compatibility-only. `BasicAuthorityResolver` remains a placeholder without semantic contradiction detection.

## Current phase — Kotlin Authority contract types only

```text
branch = authority-kotlin-contracts-v1
phase base/main = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
resolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = false
other repositories modified = false
```

Shared runtime dependencies intentionally remain absent:

```text
MatrixContextSnapshot = NOT_IMPLEMENTED
RetrievalResult = NOT_IMPLEMENTED
ProvenanceRef = NOT_IMPLEMENTED
```

No Authority-private replacements may be invented. `AuthorityResolveRequest` and full AuthorityResolution runtime DTO remain deferred until shared MIP types exist.

### Checkpoint 0 — phase start

`a2bc196f6391f4e43f9ae37ef3ad2d9e49bf05af`

### Checkpoint 1 — Authority value types

Functional commit: `177cf9db9031f5416d01399318df01c788275f43`

Added `src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt` with only:

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

No resolver/business logic/Memory access/contradiction algorithm/shared-type duplication/DTO migration/bridge migration/orchestrator change.

Continuity checkpoint: `8f399ec430c1f1ecbe518b5620bd03152f842b3c`.

### Checkpoint 2 — contract tests

Functional commit: `0f4c719d26b28f206f71bb797e84e8fa724acbf8`

Added `src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt`.

Tests cover exact enum vocabularies, confidence range + NaN rejection, SourceReliability range, opaque/nonblank MemoryRef and frozen AUTHORITY reason-code namespace.

Continuity checkpoint: `e0b0c6b8eb62a0f3ed2d44bb67e6db453a964c7e`.

### Checkpoint 3 — PR opened / scope verified

PR #11 opened against main.

PR head before continuity update: `e0b0c6b8eb62a0f3ed2d44bb67e6db453a964c7e`.

Changed paths verified:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt
src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt
```

No unrelated runtime or other repository file changed.

Continuity commit: `d1f329b8e06cf622b9f727a448c91f3ec9b968c7`.

### Checkpoint 4 — first full CI gate FAILED

CI run:

`33953927557`

Result at `Run tests`:

```text
53 tests completed
1 failed
AuthorityTypesTest > frozenReasonCodesRemainInAuthorityNamespaceWithoutDuplicates()
line = AuthorityTypesTest.kt:59
```

Compilation succeeded. Existing deprecation warnings are pre-existing compatibility warnings.

Root cause identified from the new test/contract constants:

```text
frozen AUTHORITY-1.0 reason codes actually present = 19
new test expected = 18
```

Count breakdown:

```text
5 RESOLVED
4 unresolved-role/time
2 RETRIEVAL
5 CONTRADICTION
1 CORRECTION
1 HOLD
1 ERROR
= 19
```

Therefore:

```text
contract implementation = not disproven
new test assertion = incorrect
fix scope = test-only expected count 18 -> 19
no production/runtime change required
no gate lowering allowed
```

Do NOT merge PR #11 in failed state.

## Immediate next action

```text
change only AuthorityTypesTest expected frozenV1 size from 18 to 19
commit fix
update continuity
rerun full CI
merge only if all tests green
```

## Explicitly NOT IMPLEMENTED

```text
AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime
full AuthorityResolution runtime DTO
MatrixContextSnapshot
RetrievalQuery/Result
ProvenanceRef
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

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-kotlin-contracts-v1
PR = #11
phase base = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
last CI = 33953927557 FAILED due new-test count assertion only
next action = fix AuthorityTypesTest expected 18 -> 19
resolver = NOT_STARTED
other repos = READ-ONLY
```

Do not redo cleanup or AUTHORITY-1.0 freeze.