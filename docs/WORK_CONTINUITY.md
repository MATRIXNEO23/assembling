# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:50+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v38`

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

Added canonical contract-only:

```text
AuthorityResolveRequest
AuthorityResolution
```

Request uses `MipClaimV1`, `MatrixContextSnapshot`, explicit `MipField<RetrievalResult>`, `ProvenanceRef`; no duplicate claim/context types.

Resolution uses frozen AUTHORITY-1.0 fields with typed EpistemicClass/confidence/reliability/MemoryRef and explicit MipField states.

Fail-closed invariants include claim provenance identity, unique candidates/reasons, AUTHORITY.* reason namespace, concrete contradiction included among candidates, ambiguous contradiction requires >=2 candidates, NO_MATCH cannot carry candidates, and COMPLETE requires resolved authority/confidence + completed contradiction assessment.

### Checkpoint 2 — MipClaimV1 primitive wire support

Initial claim-wire commit:

`60176c46ea2599504d6ae6ce63a9044b5f25989f`

Immediate pre-CI defect detected during self-review:

```text
semanticMarkers decoder attempted nonexistent nested key `valueField`
```

Fix commit:

`9265458cde29d021a71f7c944f181c7992cf33c9`

File:

`src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt`

Design:

- extension functions on existing `MipEvidenceWire`, NOT a new bridge object;
- `MipEvidenceWire.claimToWire(MipClaimV1)`;
- `MipEvidenceWire.claimFromWire(Map)`;
- round-trip covers all current MipClaimV1 fields: entity roles, object/dialogue/source/epistemic MipFields, confidence map, source spans and semantic markers;
- malformed field/status/enum/span/numeric structures fail closed;
- schemaVersion remains enforced by MipClaimV1 constructor;
- no native `TypedClaim` mapping change and no `MipBridge` migration.

The semanticMarkers defect was fixed before tests/CI and did not change contract semantics.

### Current validation state

```text
Authority DTO code = ADDED / UNTESTED
MipClaimV1 wire support = ADDED + SELF-REVIEW FIX / UNTESTED
Authority DTO wire codec = NEXT
contract/fail-closed tests = NEXT
full regression CI = NOT YET RUN
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
last functional/fix commit = 9265458cde29d021a71f7c944f181c7992cf33c9
Authority DTOs = ADDED / UNTESTED
MipClaimV1 wire = ADDED / FIXED / UNTESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = ADD AUTHORITY CONTRACT WIRE CODEC + TESTS, THEN FULL CI
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, or shared MIP evidence contracts.