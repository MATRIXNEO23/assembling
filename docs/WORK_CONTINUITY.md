# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:56+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v40`

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
other repos modified = false
```

### Checkpoint 1 — Authority runtime DTOs

```text
commit = 9f5b227de80c5f276eb029b64c1c563c1fbed6cd
file = src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt
```

Added canonical contract-only `AuthorityResolveRequest` and `AuthorityResolution` using existing MIP claim/context/retrieval/provenance types and frozen Authority value types.

### Checkpoint 2 — MipClaimV1 wire support

```text
initial commit = 60176c46ea2599504d6ae6ce63a9044b5f25989f
self-review fix = 9265458cde29d021a71f7c944f181c7992cf33c9
file = src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt
```

Claim wire is implemented as extension functions on the existing MipEvidenceWire codec. It preserves entity roles, MipField states, confidence map, spans and semantic markers. A semanticMarkers decoding-key defect was found and fixed before CI.

### Checkpoint 3 — Authority DTO primitive wire codec

```text
commit = 297491d62219a60cdaa3362d54b0fc632dc4eafb
file = src/main/kotlin/matrix/assembling/authority/AuthorityContractWire.kt
```

Serialization only. Shared MIP payloads delegate to MipEvidenceWire; Authority-owned fields use explicit typed codecs. Invalid enums/types/wrappers fail closed. No legacy AuthorityDecision mapping or MipBridge migration.

### Checkpoint 4 — Authority DTO/wire/fail-closed tests

```text
commit = 9542ce6248abee5d95a1016d2c709ac5c2f418a9
file = src/test/kotlin/matrix/assembling/authority/AuthorityContractsTest.kt
```

Coverage added:

```text
MipClaimV1 round-trip preserves roles/spans/semantic markers
AuthorityResolveRequest round-trip preserves PRESENT RetrievalResult(NO_MATCH)
field-level retrieval NO_MATCH rejected
request provenance claim mismatch rejected
COMPLETE no-contradiction AuthorityResolution round-trip
legacy persistence-style fields absent from canonical Authority wire
concrete contradiction must be among candidate refs
AMBIGUOUS contradiction requires >=2 candidate refs
COMPLETE fails if authority unresolved
COMPLETE fails if resolver confidence unresolved
COMPLETE fails if contradiction assessment is NO_MATCH instead of completed PRESENT/NOT_APPLICABLE
Authority reason codes require AUTHORITY.* namespace + no duplicates/blanks
resolution provenance claim mismatch rejected
invalid confidence wire rejected
unknown resolution status rejected
malformed retrieval field rejected instead of collapsed
semantic marker PRESENT-with-null rejected
concrete contradiction MemoryRef round-trip preserves identity
```

### Validation state

```text
Authority DTO code = ADDED
MipClaimV1 wire = ADDED + SELF-REVIEW FIX
Authority DTO wire = ADDED
new tests = ADDED / NOT YET CI-VALIDATED
full regression CI = NEXT
```

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
base = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
last functional commit = 9542ce6248abee5d95a1016d2c709ac5c2f418a9
Authority runtime DTO binding = CODE + TESTS ADDED / CI PENDING
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = OPEN PR, RUN FULL CI, FIX ONLY TASK-INTRODUCED FAILURES, MERGE ONLY IF GREEN
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, or shared MIP evidence contracts.