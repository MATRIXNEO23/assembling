# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:01+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v42`

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
9f5b227de80c5f276eb029b64c1c563c1fbed6cd = AuthorityResolveRequest + AuthorityResolution DTOs
60176c46ea2599504d6ae6ce63a9044b5f25989f = initial MipClaimV1 wire support
9265458cde29d021a71f7c944f181c7992cf33c9 = pre-CI semanticMarkers decoder fix
297491d62219a60cdaa3362d54b0fc632dc4eafb = AuthorityContractWire primitive codec
9542ce6248abee5d95a1016d2c709ac5c2f418a9 = Authority DTO/wire/fail-closed tests
```

Files added:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt
src/main/kotlin/matrix/assembling/authority/AuthorityContractWire.kt
src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt
src/test/kotlin/matrix/assembling/authority/AuthorityContractsTest.kt
```

Contract properties:

- request reuses MipClaimV1, MatrixContextSnapshot, explicit MipField<RetrievalResult>, ProvenanceRef;
- result uses frozen AUTHORITY-1.0 typed fields;
- COMPLETE does not mean persistence approval;
- contradiction identity/status cannot collapse to nullable absence;
- concrete/ambiguous/no-match contradiction states are structurally constrained;
- claim/provenance identity mismatch fails closed;
- shared payload serialization delegates to existing MIP evidence codec;
- no legacy AuthorityDecision or final MipBridge migration.

Test coverage includes claim/request/result round-trips, explicit RetrievalResult(NO_MATCH), malformed MipFields, semantic markers, provenance mismatches, contradiction target identity, ambiguity cardinality, COMPLETE fail-closed behavior, reason namespaces, confidence range and unknown-enum rejection.

### PR / CI evidence

PR #13:

`Bind frozen Authority-1.0 runtime DTOs to MIP evidence`

Final green tested PR head before this documentation update:

`bf8a8a3585d6668ff66d24be1fe7ce6bdf5414cf`

Full regression CI:

```text
run = 33956392311
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

No task-introduced CI repair was required. The only defect found in this task was the semanticMarkers decoder-key mistake detected and fixed during self-review before CI.

This continuity update is documentation-only and creates the final pre-merge head. Merge is forbidden until the CI for this exact final head is green.

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
last green tested head before doc update = bf8a8a3585d6668ff66d24be1fe7ce6bdf5414cf
CI = 33956392311 SUCCESS
Authority runtime DTO binding = IMPLEMENTED / TESTED GREEN
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = VERIFY FINAL DOC-ONLY HEAD CI; MERGE #13 ONLY IF GREEN; THEN POST-MERGE MAIN CI + FINALIZE CONTINUITY
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, or shared MIP evidence contracts.