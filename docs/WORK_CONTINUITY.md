# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:06+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v43`

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
AUTHORITY contract freeze PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
Authority value types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
shared evidence post-merge CI = 33955886389 SUCCESS
```

Authority remains read-only over evidence, never Memory writer/admission owner. Hard MIP distinctions and explicit status vocabulary remain unchanged.

## Authority runtime DTO binding — COMPLETE / INTEGRATED / TESTED

PR #13:

`Bind frozen Authority-1.0 runtime DTOs to MIP evidence`

Merge SHA:

`6841d916ba8a28a5bfc16ab4b0fa679e40c555fc`

Integrated files:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityContracts.kt
src/main/kotlin/matrix/assembling/authority/AuthorityContractWire.kt
src/main/kotlin/matrix/assembling/mip/MipClaimWire.kt
src/test/kotlin/matrix/assembling/authority/AuthorityContractsTest.kt
```

Integrated contracts:

```text
AuthorityResolveRequest
AuthorityResolution
MipClaimV1 primitive wire support
Authority primitive wire support
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

PR final-head CI:

```text
33956466243 = SUCCESS
```

Post-merge main CI:

```text
33956522738 = SUCCESS
```

No gate was weakened. No task-introduced post-merge failure required repair.

## NEXT ACTIVE TASK — REAL AUTHORITY RESOLVER

This is now the next bounded task.

Scope:

- implement real deterministic `AuthorityResolver` under `src/main/kotlin/matrix/assembling/authority/`;
- consume only `AuthorityResolveRequest` structured claim/context/retrieval evidence;
- classify `EpistemicClass` according to frozen AUTHORITY-1.0 rules;
- perform conservative semantic contradiction detection over read-only retrieved candidate evidence;
- output canonical `AuthorityResolution` with explicit contradiction identity/status;
- preserve `NO_MATCH != UNAVAILABLE != ERROR`;
- deterministic observable reason codes only; no hidden chain-of-thought dependency;
- add P0 tests for WORLD_TRUTH provenance, OBSERVATION, REPORT, INFERENCE, BELIEF, unrelated predicates, temporal change, same-slot contradiction, ambiguity, invalid/superseded targets, correction semantics, and no-write behavior.

Explicitly NOT authorized in this task:

```text
Memory writes
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
MemoryRepository dependency
PersistentConsolidation
root AuthorityDecision migration
final MipBridge migration
orchestrator rewiring
retrieval engine implementation
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
branch = main
main HEAD functional merge = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
post-merge CI = 33956522738 SUCCESS
AUTHORITY-1.0 = FROZEN
Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTO binding = COMPLETE / INTEGRATED / TESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = REAL AUTHORITY RESOLVER ONLY
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, Authority value types, shared MIP evidence contracts, or Authority DTO binding.