# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:40+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `mip-evidence-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v34`

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

Authority remains read-only over evidence, never Memory writer/admission owner. `BasicAuthorityResolver` remains placeholder-only. Hard MIP distinctions and explicit status vocabulary remain unchanged.

## ACTIVE TASK — SHARED MIP EVIDENCE CONTRACT TYPES ONLY

```text
branch = mip-evidence-contracts-v1
base = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
PR = #12
task-start continuity = 9bc0545a392f0875f3ede6509b2b384b91f8a455
other repos modified = false
```

### Functional checkpoints

```text
402b6611daa4b0a7804f176e99135752f555b684
= add src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt

b237e1e22b98f480eaf39e7fcb108715855592e4
= add src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt

43858f686ba383bc7fa79e3a40f811b021ef69e1
= add src/test/kotlin/matrix/assembling/mip/MipEvidenceContractsTest.kt
```

Shared types:

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

Key invariants implemented:

- shared types live only in existing MIP package;
- all reserved domains declare availability exactly once;
- NOT_WIRED/UNAVAILABLE/ERROR domains cannot carry fake context entries;
- immutable snapshot lineage uses explicit IDs; parent cannot equal current snapshot;
- optional provenance states use MipField;
- normalized finite context/retrieval confidence;
- includeSuperseded requires includeHistorical;
- selected refs subset candidates;
- retrieval score identity is ref-bound;
- MATCHED requires evidence;
- NO_MATCH cannot carry candidate evidence;
- AMBIGUOUS requires >=2 candidates;
- INDEX_UNAVAILABLE/ERROR cannot carry fake candidates;
- NO_MATCH != INDEX_UNAVAILABLE != ERROR structurally and across wire round-trip.

Wire codec:

- reflection-free primitive maps/lists;
- Provenance, Context snapshot/entries, Retrieval query/result round trips;
- explicit MipField status preservation;
- malformed values/unknown enums/bad timestamps fail closed;
- ISO-8601 Instant timestamps;
- no second bridge/protocol introduced.

Test coverage includes vocabulary, round trips, domain availability, fake-content rejection, retrieval status distinctions, superseded/history constraints, malformed PRESENT fields, unknown enum rejection, normalized score validation and selected-ref integrity.

### PR / CI evidence

PR #12 opened from verified four-file diff only:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt
src/main/kotlin/matrix/assembling/mip/MipEvidenceWire.kt
src/test/kotlin/matrix/assembling/mip/MipEvidenceContractsTest.kt
```

Green full-suite gate on PR head `7857b268e2b2ce78ce40b3ba300733b9e793acea`:

```text
CI run = 33955741385
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

No task-introduced failure required repair.

This continuity update is documentation-only and creates the final pre-merge head. Merge remains forbidden until the CI for that final head is also green.

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
PR = #12
base = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
last green tested head before continuity update = 7857b268e2b2ce78ce40b3ba300733b9e793acea
CI = 33955741385 SUCCESS
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = IMPLEMENTED / TESTED GREEN
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = VERIFY FINAL DOC-ONLY PR HEAD CI; MERGE PR #12 ONLY IF GREEN; THEN VERIFY MAIN CI + FINALIZE CONTINUITY
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, or Kotlin Authority value-type work.