# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:58+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v41`

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
AUTHORITY PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
Authority value types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
shared evidence CI = 33955886389 SUCCESS
pre-task main HEAD = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
pre-task CI = 33955971985 SUCCESS
```

Authority remains read-only over evidence, never Memory writer/admission owner. `BasicAuthorityResolver` is still placeholder-only.

## ACTIVE TASK — AUTHORITY RUNTIME DTO BINDING ONLY

```text
branch = authority-runtime-dtos-v1
base = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
task-start continuity = 6e76d3c5430f326cf419937c3baf3703e0b0ed4f
PR = #13
other repos modified = false
```

### Functional checkpoints

```text
9f5b227de80c5f276eb029b64c1c563c1fbed6cd
= AuthorityResolveRequest + AuthorityResolution DTOs

60176c46ea2599504d6ae6ce63a9044b5f25989f
= initial MipClaimV1 wire support

9265458cde29d021a71f7c944f181c7992cf33c9
= pre-CI semanticMarkers decoder fix

297491d62219a60cdaa3362d54b0fc632dc4eafb
= AuthorityContractWire primitive codec

9542ce6248abee5d95a1016d2c709ac5c2f418a9
= Authority DTO/wire/fail-closed tests
```

Files added by this task:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt
src/main/kotlin/matrix/assembling/authority/AuthorityContractWire.kt
src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt
src/test/kotlin/matrix/assembling/authority/AuthorityContractsTest.kt
```

Contract status:

- request reuses MipClaimV1 + MatrixContextSnapshot + explicit MipField<RetrievalResult> + ProvenanceRef;
- result uses frozen AUTHORITY-1.0 typed fields;
- no nullable contradiction collapse;
- COMPLETE requires resolved Authority classification/confidence and completed contradiction assessment;
- concrete/ambiguous/no-match contradiction states are structurally constrained;
- claim/provenance identity mismatches fail;
- wire codecs are serialization only and delegate shared payloads to existing MIP evidence codec;
- no legacy AuthorityDecision or MipBridge final migration.

Test coverage includes claim/request/result round-trips, explicit RetrievalResult(NO_MATCH), malformed MipFields, semantic markers, provenance mismatches, contradiction candidate identity, ambiguity cardinality, COMPLETE fail-closed states, reason namespaces, confidence range and unknown enum rejection.

### PR checkpoint

PR #13:

`Bind frozen Authority-1.0 runtime DTOs to MIP evidence`

PR base:

`8dc6643e73c3ce6e569173a4922ec3a01e77e0ff`

PR-open head:

`e2d9278dfb750177076fc67e3834679bdbcfe82d`

Verified diff:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/AuthorityContractWire.kt
src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt
src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt
src/test/kotlin/matrix/assembling/authority/AuthorityContractsTest.kt
```

No orchestrator, Memory, legacy compatibility path, NLU repo or other repository changed.

### Validation state

```text
PR #13 = OPEN
full regression CI = PENDING
merge = FORBIDDEN UNTIL FINAL PR HEAD GREEN
```

If CI fails, fix only task-introduced failures; do not weaken tests/contracts.

## Explicitly NOT IMPLEMENTED

```text
real AuthorityResolver
semantic contradiction algorithm
retrieval engine
Memory Kotlin/Room
Memory Admission wiring
PersistentConsolidation
root AuthorityDecision migration
MipBridge final Authority migration
orchestrator rewiring
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
branch = authority-runtime-dtos-v1
PR = #13
base = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
PR-open head = e2d9278dfb750177076fc67e3834679bdbcfe82d
Authority runtime DTO binding = CODE + TESTS ADDED / PR OPEN / CI PENDING
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = INSPECT PR #13 CI; FIX ONLY TASK-INTRODUCED FAILURES; MERGE ONLY IF FINAL HEAD GREEN
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, or shared MIP evidence contracts.