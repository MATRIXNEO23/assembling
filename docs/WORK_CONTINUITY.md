# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:22+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `mip-evidence-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v31`

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

## Baseline already complete — DO NOT REDO

```text
Assembling cleanup PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
cleanup finalization PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
MIP = docs/MATRIX_INTERMODULE_PROTOCOL.md / MIP-1.0
AUTHORITY-1.0 contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
AUTHORITY freeze PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
Kotlin Authority types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
Authority post-merge CI = 33954180260 SUCCESS
pre-task main HEAD = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
pre-task continuity CI = 33954352500 SUCCESS
```

Hard semantic distinctions remain:

```text
TypedClaim != Belief != Memory
Memory != State != Context
Relationship != Affective != Intimacy
SexualInterest != CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability != Authority != BeliefConfidence != RetrievalRelevance
```

Authority remains read-only over evidence, never Memory writer/admission owner. `BasicAuthorityResolver` remains placeholder-only.

## ACTIVE TASK — SHARED MIP EVIDENCE CONTRACT TYPES ONLY

```text
branch = mip-evidence-contracts-v1
base = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
task-start continuity = 9bc0545a392f0875f3ede6509b2b384b91f8a455
other repos modified = false
```

### Checkpoint 1 — shared evidence contracts

Functional commit:

`402b6611daa4b0a7804f176e99135752f555b684`

File:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt`

Types added:

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

Key invariants:

- shared types live only in existing `matrix.assembling.mip`;
- every reserved ContextDomain declares availability exactly once;
- NOT_WIRED/UNAVAILABLE/ERROR domains cannot carry fake entries;
- snapshot/entry IDs are validated and entry IDs unique;
- parent snapshot cannot equal current snapshot;
- optional provenance semantics use `MipField`;
- context confidence and retrieval relevance are finite `[0,1]`;
- `includeSuperseded` requires `includeHistorical`;
- selected refs are subset of candidates;
- scores carry explicit ref identity, avoiding positional ambiguity;
- MATCHED requires candidate evidence;
- NO_MATCH carries no candidates/scores;
- AMBIGUOUS requires >=2 candidates;
- INDEX_UNAVAILABLE/ERROR carry no fake candidates;
- NO_MATCH, INDEX_UNAVAILABLE and ERROR cannot collapse.

### Checkpoint 2 — explicit primitive wire codec

Functional commit:

`b237e1e22b98f480eaf39e7fcb108715855592e4`

File:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt`

Purpose:

- reflection-free primitive Map/List/String/Number/Boolean wire projection;
- serialize/deserialize `ProvenanceRef`;
- serialize/deserialize `MatrixContextSnapshot` and nested ContextEntry/domain availability;
- serialize/deserialize `RetrievalQuery`;
- serialize/deserialize `RetrievalResult` and scores;
- preserve all `MipFieldStatus` states across wire boundaries;
- reject unsupported enum values, malformed typed fields, invalid timestamps and wrong primitive types;
- use ISO-8601 `Instant` for timestamps;
- this codec belongs to the MIP contract package and is NOT a second intermodule bridge.

### Current validation state

```text
contracts code = ADDED
wire codec = ADDED
new contract tests = NOT YET ADDED
full regression CI = NOT YET RUN ON CURRENT HEAD
```

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

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = mip-evidence-contracts-v1
base = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
last functional commit = b237e1e22b98f480eaf39e7fcb108715855592e4
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = CODE ADDED / UNTESTED
wire codec = CODE ADDED / UNTESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = ADD CONTRACT/WIRE/FAIL-CLOSED TESTS, THEN OPEN PR + FULL CI
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, or Kotlin Authority value-type work.