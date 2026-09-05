# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:44+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-dtos-v1`  
Continuity schema: `matrix.assembling.continuity.v36`

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

Branch:

`authority-runtime-dtos-v1`

Base/start HEAD:

`8dc6643e73c3ce6e569173a4922ec3a01e77e0ff`

Pre-task CI:

`33955971985 = SUCCESS`

Task scope:

- implement canonical `AuthorityResolveRequest` using MIP claim + shared `MatrixContextSnapshot` + explicit retrieval evidence state + `ProvenanceRef`;
- implement canonical `AuthorityResolution` using frozen AUTHORITY-1.0 value types;
- preserve contradiction identity/status without nullable collapse;
- add explicit primitive wire round-trip support using existing MIP evidence codec rather than creating a second intermodule bridge;
- add structural and fail-closed tests;
- keep runtime DTOs in dedicated `matrix.assembling.authority` package;
- no resolver algorithm;
- no semantic contradiction detection;
- no Memory access/write/admission;
- no root `AuthorityDecision` migration;
- no final `MipBridge` migration;
- no orchestrator rewiring;
- no writes to other repositories.

Design decisions for this checkpoint:

```text
request claim type = existing canonical MIP runtime claim (`MipClaimV1`)
retrieval evidence absence/state = explicit `MipField<RetrievalResult>`, not nullable
retrieval NO_MATCH = PRESENT RetrievalResult(status=NO_MATCH), not field-level nullable absence
AuthorityResolution contradiction identity = MipField<MemoryRef>
candidate memories = opaque MemoryRef list
reason codes = AUTHORITY.* observable codes only
```

Expected `AuthorityResolveRequest` contract:

```text
requestId
claim: MipClaimV1
contextSnapshot: MatrixContextSnapshot
retrievalResult: MipField<RetrievalResult>
provenance: ProvenanceRef
```

Expected `AuthorityResolution` contract:

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

Fail-closed target invariants:

- IDs nonblank;
- request claim/context IDs must be coherent with request provenance when explicitly referenced;
- candidate refs unique;
- reason/ambiguity lists nonblank and duplicate-free;
- every reason code must use AUTHORITY.* namespace;
- concrete contradictedMemoryRef must be among candidateMemoryRefs;
- AMBIGUOUS contradiction state requires >=2 candidates;
- NO_MATCH contradiction state cannot carry candidates;
- COMPLETE requires authority + authorityResolutionConfidence PRESENT and contradiction assessment completed as PRESENT or NOT_APPLICABLE;
- no DTO state means persistence approval.

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

Other repositories modified:

`false`

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-runtime-dtos-v1
base = 8dc6643e73c3ce6e569173a4922ec3a01e77e0ff
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTO task = STARTED / NO DTO CODE YET
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = IMPLEMENT AUTHORITY DTOs + MIP CLAIM WIRE SUPPORT + CONTRACT TESTS
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Kotlin Authority value types, or shared MIP evidence contracts.