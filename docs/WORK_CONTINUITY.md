# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:47+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v37`

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
cosmetic mass refactor = forbidden
other repositories = read-only
```

## Completed baseline — DO NOT REDO

```text
Assembling cleanup PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
cleanup finalization PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
MIP = docs/MATRIX_INTERMODULE_PROTOCOL.md / MIP-1.0
AUTHORITY-1.0 contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
AUTHORITY freeze PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
Kotlin Authority types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
shared MIP evidence post-merge CI = 33955886389 SUCCESS
pre-task main HEAD = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
pre-task continuity CI = 33955971985 SUCCESS
```

Authority remains read-only over evidence, never Memory writer/admission owner. `BasicAuthorityResolver` remains placeholder-only. Hard MIP distinctions and explicit status vocabulary remain unchanged.

## Shared MIP evidence contracts — COMPLETE / INTEGRATED / TESTED

Integrated shared types:

```text
ModuleId
ProvenanceRef
ContextDomain
ContextScope
DomainAvailability
ContextDomainAvailability
TypedContextValue
ContextEntry
MatrixContextSnapshot
RetrievalPurpose
RetrievalStatus
RetrievalQuery
RetrievalScore
RetrievalResult
```

Shared wire codec:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt`

Hard evidence invariants include domain availability completeness, no fake context for unavailable domains, explicit MipField status preservation, retrieval identity-bound scores, and structural distinction between MATCHED / NO_MATCH / AMBIGUOUS / INDEX_UNAVAILABLE / ERROR.

## ACTIVE TASK — AUTHORITY RUNTIME DTO BINDING ONLY

```text
branch = authority-runtime-dtos-v1
base = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
pre-task CI = 33955971985 SUCCESS
task-start continuity = 6e76d3c5430f326cf419937c3baf3703e0b0ed4f
other repos modified = false
```

Task boundaries:

- canonical request/result DTO binding only;
- no resolver algorithm;
- no semantic contradiction detection;
- no Memory read/write/admission;
- no root AuthorityDecision migration;
- no final MipBridge migration;
- no orchestrator rewiring.

### Checkpoint 1 — canonical Authority runtime DTOs added

Functional commit:

`9f5b227de80c5f276eb029b64c1c563c1fbed6cd`

File:

`src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt`

Types added:

```text
AuthorityResolveRequest
AuthorityResolution
```

`AuthorityResolveRequest` fields:

```text
requestId
claim: MipClaimV1
contextSnapshot: MatrixContextSnapshot
retrievalResult: MipField<RetrievalResult>
provenance: ProvenanceRef
```

Request decisions/invariants:

- reuses existing MIP claim DTO; no second TypedClaim model;
- retrieval evidence is explicit `MipField`, never nullable;
- field-level `NO_MATCH` / `AMBIGUOUS` / `CONFLICTED` are rejected because those are result-level retrieval states when retrieval actually ran;
- allowed retrieval field states are PRESENT / NOT_APPLICABLE / UNKNOWN / UNRESOLVED / UNAVAILABLE / ERROR;
- `PRESENT RetrievalResult(status=NO_MATCH)` is the canonical successful-no-result representation;
- if request provenance explicitly contains claimId, it must equal `claim.claimId`.

`AuthorityResolution` fields:

```text
resolutionId
claimId
contextSnapshotId
retrievalQueryId: MipField<String>
resolutionStatus: AuthorityResolutionStatus
authority: MipField<EpistemicClass>
authorityResolutionConfidence: MipField<AuthorityResolutionConfidence>
sourceReliability: MipField<SourceReliability>
contradictedMemoryRef: MipField<MemoryRef>
candidateMemoryRefs[]
ambiguityReasons[]
reasonCodes[]
provenance
```

Resolution invariants:

- IDs nonblank;
- candidate MemoryRefs unique;
- ambiguity reasons and reason codes nonblank/duplicate-free;
- every reason code must be `AUTHORITY.*`;
- explicit provenance claimId must match resolution claimId;
- PRESENT contradictedMemoryRef must appear in candidateMemoryRefs;
- AMBIGUOUS contradictedMemoryRef requires at least two candidate refs;
- NO_MATCH contradictedMemoryRef cannot carry candidates;
- COMPLETE requires authority=PRESENT;
- COMPLETE requires authorityResolutionConfidence=PRESENT;
- COMPLETE requires contradiction assessment state PRESENT or NOT_APPLICABLE;
- COMPLETE is still not persistence/admission authorization.

### Current validation state

```text
DTO code = ADDED / NOT YET CI-VALIDATED
MipClaimV1 wire support = NEXT
Authority request/result wire codec = NEXT
contract/fail-closed tests = NEXT
full regression CI = NOT YET RUN ON CURRENT TASK
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
last functional commit = 9f5b227de80c5f276eb029b64c1c563c1fbed6cd
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTOs = CODE ADDED / UNTESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = EXTEND EXISTING MIP EVIDENCE WIRE WITH MipClaimV1 ROUND-TRIP, THEN AUTHORITY DTO WIRE + TESTS
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Kotlin Authority value types, or shared MIP evidence contracts.