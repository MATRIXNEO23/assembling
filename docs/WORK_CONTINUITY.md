# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:18+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `mip-evidence-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v30`

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

### Assembling cleanup

```text
cleanup start = ef433a3aed519b31efe9289a8df78ed974170510
PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR #8 CI = 33951029064 SUCCESS
post-merge CI = 33951548865 SUCCESS
PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final cleanup CI = 33951808519 SUCCESS
```

Inventory, contract map, incompatibility matrix, MipBridge audit, structural cleanup, legacy quarantine, round-trip tests and documentation are complete. No files were moved/renamed/deleted.

Compatibility-only legacy paths remain quarantined:

```text
contracts/MatrixAssemblyContracts.kt
pipeline/MatrixAssemblyPipeline.kt
prompt/SemanticFrameToPrompt.kt
coherence/CoherenceGuard.kt
```

### MIP-1.0

Canonical spec:

`docs/MATRIX_INTERMODULE_PROTOCOL.md`

Canonical bridge:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

Hard MIP distinctions remain:

```text
TypedClaim != Belief != Memory
Memory != State != Context
Relationship != Affective != Intimacy
SexualInterest != CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability != Authority != BeliefConfidence != RetrievalRelevance
```

MIP field states remain explicit:

```text
PRESENT
NOT_APPLICABLE
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
NO_MATCH
ERROR
```

### AUTHORITY-1.0 freeze

Canonical spec:

`docs/MIP_AUTHORITY_CONTRACT.md`

```text
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
status = FROZEN
```

Boundary:

```text
TypedClaim
-> Authority Resolver
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Authority may read evidence but never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE. `BasicAuthorityResolver` remains placeholder-only.

Frozen EpistemicClass:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Frozen AuthorityResolutionStatus:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

Semantic contradiction requires same resolved semantic slot + same normalized predicate + compatible time + truly incompatible value/target/polarity + one unique VALID target. Same actor/text difference/similarity/confidence/authority rank alone never proves contradiction. Temporal change is not contradiction by default.

### Kotlin Authority value types

```text
branch = authority-kotlin-contracts-v1
PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
post-merge CI = 33954180260 SUCCESS
continuity checkpoint = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
continuity CI = 33954352500 SUCCESS
```

Files:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt
src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt
```

Types integrated:

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

No resolver, Memory access, contradiction algorithm, Context/Retrieval duplicate, orchestrator migration or final MipBridge migration was introduced.

## ACTIVE TASK — SHARED MIP EVIDENCE CONTRACT TYPES ONLY

Branch:

`mip-evidence-contracts-v1`

Base/start HEAD:

`05ff921d33e3f9c133ef7ea4fd9026c4966c67b7`

Task-start continuity commit:

`9bc0545a392f0875f3ede6509b2b384b91f8a455`

### Code checkpoint 1 — shared contracts added

Commit:

`402b6611daa4b0a7804f176e99135752f555b684`

File added:

`src/main/kotlin/matrix/assembling/mip/MipEvidenceContracts.kt`

Shared types now present on active branch:

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

Implemented invariants:

- shared contracts live in existing `matrix.assembling.mip`, not Authority/Memory-private packages;
- Provenance optional semantic fields use `MipField` instead of ambiguous nullable meanings;
- all reserved Context domains must declare availability exactly once;
- a domain marked NOT_WIRED/UNAVAILABLE/ERROR cannot carry fake ContextEntry content;
- snapshot IDs and context entry IDs are validated; entry IDs are unique;
- parent snapshot cannot equal current snapshot;
- context confidence is finite and normalized `[0,1]` when PRESENT;
- Retrieval purposes match MIP-1.0 universal registry;
- `includeSuperseded=true` requires `includeHistorical=true`;
- maxSelected must be positive and cannot exceed maxCandidates;
- selected retrieval refs must be a subset of candidate refs;
- score identity is explicit (`RetrievalScore.ref`) to avoid positional ambiguity;
- retrieval relevance must be finite `[0,1]`;
- MATCHED requires candidates;
- NO_MATCH carries no candidate/selected refs or scores;
- AMBIGUOUS requires at least two candidates;
- INDEX_UNAVAILABLE/ERROR carry no fake candidates;
- `NO_MATCH != INDEX_UNAVAILABLE != ERROR` remains structurally enforced.

No engine/provider behavior was implemented.

### Current untested status

```text
contracts code = ADDED / NOT YET CI-VALIDATED
contract tests = NEXT
wire/round-trip tests = NEXT
existing regression suite = NOT YET RUN ON THIS CHECKPOINT
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

Other repositories modified:

`false`

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = mip-evidence-contracts-v1
base = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
last functional commit = 402b6611daa4b0a7804f176e99135752f555b684
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = CODE ADDED / UNTESTED
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = ADD STRUCTURAL + FAIL-CLOSED + WIRE ROUND-TRIP TESTS, THEN FULL CI
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, or Kotlin Authority value-type work.