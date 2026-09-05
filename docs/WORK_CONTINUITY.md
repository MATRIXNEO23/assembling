# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:26+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `mip-evidence-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v32`

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

Commit:

`402b6611daa4b0a7804f176e99135752f555b684`

File:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt`

Types:

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

Key structural invariants:

- every reserved ContextDomain declares availability exactly once;
- unavailable/not-wired/error domains cannot carry fake entries;
- snapshot/entry IDs are validated and entry IDs unique;
- optional provenance states use MipField;
- normalized context/retrieval confidence;
- includeSuperseded requires includeHistorical;
- selected refs subset candidates;
- score identity is ref-bound, not positional;
- MATCHED/NO_MATCH/AMBIGUOUS/INDEX_UNAVAILABLE/ERROR have distinct fail-closed list rules.

### Checkpoint 2 — explicit primitive wire codec

Commit:

`b237e1e22b98f480eaf39e7fcb108715855592e4`

File:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt`

Codec:

- reflection-free primitive Map/List/String/Number/Boolean representation;
- Provenance, Context snapshot/entries, Retrieval query/result round-trip support;
- MipField statuses preserved;
- unknown enums, malformed fields, bad timestamps and wrong primitive types fail closed;
- ISO-8601 Instant timestamps;
- codec remains inside MIP package and is not a parallel bridge.

### Checkpoint 3 — structural/wire/fail-closed tests

Commit:

`43858f686ba383bc7fa79e3a40f811b021ef69e1`

File:

`src/test/kotlin/matrix/assembling/mip/MipEvidenceContractsTest.kt`

Coverage added:

```text
reserved ModuleId/Context vocabulary
exact RetrievalPurpose/Status vocabulary
Provenance wire round-trip with UNKNOWN/UNRESOLVED/NOT_APPLICABLE preservation
all-domain availability requirement
unavailable domain cannot carry fake ContextEntry
Context snapshot wire round-trip
context confidence finite [0,1]
RetrievalQuery wire round-trip
includeSuperseded requires includeHistorical
NO_MATCH != INDEX_UNAVAILABLE != ERROR across wire round-trip
invalid status/list combinations rejected
MATCHED score identity preserved
malformed PRESENT-with-null wire field rejected
unknown enum rejected
invalid retrieval relevance rejected
unknown selected ref rejected
```

### Validation state

```text
contracts code = ADDED
wire codec = ADDED
tests = ADDED / NOT YET CI-VALIDATED
full regression CI = NEXT
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
last functional commit = 43858f686ba383bc7fa79e3a40f811b021ef69e1
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = CODE + TESTS ADDED / UNVALIDATED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = OPEN PR, RUN FULL CI, FIX ONLY TASK-INTRODUCED FAILURES, THEN MERGE IF GREEN
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, or Kotlin Authority value-type work.