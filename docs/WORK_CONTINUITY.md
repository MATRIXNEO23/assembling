# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:42+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v35`

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
Authority post-merge CI = 33954180260 SUCCESS
pre-evidence main HEAD = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
pre-evidence continuity CI = 33954352500 SUCCESS
```

Authority remains read-only over evidence, never Memory writer/admission owner. `BasicAuthorityResolver` remains placeholder-only. Hard MIP distinctions and explicit status vocabulary remain unchanged.

## Shared MIP evidence contracts — COMPLETE / INTEGRATED

Phase branch:

`mip-evidence-contracts-v1`

Phase base:

`05ff921d33e3f9c133ef7ea4fd9026c4966c67b7`

PR:

`#12`

Merge SHA:

`8f45a631b70c283169d058d98d1c880b5e37e554`

Files integrated:

```text
src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt
src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt
src/test/kotlin/matrix/assembling/mip/MipEvidenceContractsTest.kt
```

Types integrated:

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

Key invariants now enforced:

- every reserved ContextDomain declares availability exactly once;
- NOT_WIRED/UNAVAILABLE/ERROR domains cannot carry fake ContextEntry content;
- snapshot and entry IDs are explicit/validated; entry IDs unique;
- parent snapshot cannot equal current snapshot;
- optional provenance states use MipField instead of ambiguous null;
- context confidence and retrieval relevance are finite `[0,1]`;
- includeSuperseded requires includeHistorical;
- selected retrieval refs must be subset of candidates;
- score identity is explicitly ref-bound, not positional;
- MATCHED requires candidate evidence;
- NO_MATCH carries no candidate evidence;
- AMBIGUOUS requires at least two candidates;
- INDEX_UNAVAILABLE and ERROR cannot carry fake candidates;
- NO_MATCH != INDEX_UNAVAILABLE != ERROR is preserved structurally and on wire round-trip.

Wire support:

- reflection-free primitive Map/List/String/Number/Boolean representation;
- Provenance, Context snapshot/entries, Retrieval query/result round trips;
- all MipField states preserved;
- unknown enums, malformed PRESENT fields, bad timestamps and wrong primitive types fail closed;
- ISO-8601 Instant timestamps;
- no second bridge/protocol introduced.

Functional commits:

```text
402b6611daa4b0a7804f176e99135752f555b684 = shared contracts
b237e1e22b98f480eaf39e7fcb108715855592e4 = MIP evidence wire codec
43858f686ba383bc7fa79e3a40f811b021ef69e1 = contract/wire/fail-closed tests
```

PR CI evidence:

```text
33955741385 = SUCCESS
33955809007 = SUCCESS on final PR head
```

Post-merge main CI:

```text
33955886389 = SUCCESS
```

No task-introduced failure required repair. No existing gate/test was weakened.

## Explicitly NOT IMPLEMENTED

```text
retrieval engine
Memory Kotlin/Room
real AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime DTO
full AuthorityResolution runtime DTO
PersistentConsolidation
MipBridge final Authority migration
root AuthorityDecision migration
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

## Next bounded task

The shared evidence contracts required by AUTHORITY-1.0 now exist. The next safe task is deliberately narrower than implementing the resolver:

```text
AUTHORITY RUNTIME DTO BINDING ONLY
```

Scope:

- implement canonical `AuthorityResolveRequest` using existing MIP `TypedClaim`/claim identity, `MatrixContextSnapshot`, optional `RetrievalResult`, and `ProvenanceRef`;
- implement canonical `AuthorityResolution` DTO using frozen AUTHORITY-1.0 fields and shared MIP evidence types;
- preserve explicit contradiction field/status semantics end-to-end;
- add structural/wire/fail-closed tests;
- no AuthorityResolver algorithm;
- no semantic contradiction detection logic;
- no Memory writes/admission;
- no orchestrator rewiring;
- no final MipBridge migration yet.

Only after this DTO-binding checkpoint is green may the real resolver implementation begin.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
main functional merge = 8f45a631b70c283169d058d98d1c880b5e37e554
post-merge CI = 33955886389 SUCCESS
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = AUTHORITY RUNTIME DTO BINDING ONLY
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Kotlin Authority value-type work, or shared MIP evidence contract work.