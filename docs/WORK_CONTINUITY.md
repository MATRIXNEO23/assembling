# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:11+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v27`

## Mandatory continuity policy

This is the single canonical restart file for the active Assembling workstream. Update after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Work rules

```text
writable repository = MATRIXNEO23/assembling only
MIP = single cross-module semantic authority
MipBridge = single common interop bridge
new functional module = dedicated directory/package
parallel protocol/context/adapter family = forbidden
cosmetic mass refactor = forbidden
gate/test weakening = forbidden
```

Other repositories remain read-only until the owner explicitly switches active repository.

## Completed baseline

### Assembling cleanup

```text
cleanup start = ef433a3aed519b31efe9289a8df78ed974170510
PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR #8 CI = 33951029064 SUCCESS
post-merge CI = 33951548865 SUCCESS
PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final cleanup CI = 33951808519 SUCCESS
```

Inventory, contract map, incompatibility matrix, MipBridge audit, structural cleanup, legacy quarantine, round-trip tests and docs are complete. No files moved/renamed/deleted.

Canonical protocol: `docs/MATRIX_INTERMODULE_PROTOCOL.md` = MIP-1.0.  
Canonical bridge: `src/main/kotlin/matrix/assembling/mip/MipBridge.kt`.

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

Authority contradiction mapping remains fail-closed; Python arbitrary integer -> Kotlin Long remains range checked.

## AUTHORITY-1.0 freeze — COMPLETE

Canonical file: `docs/MIP_AUTHORITY_CONTRACT.md`.

```text
contract = MIP-1.0 / AUTHORITY-1.0
status = FROZEN
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
freeze continuity = 9210773030afc96f631d0e0c0a3a669bf6a6c2f5
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
```

Frozen boundary:

```text
TypedClaim -> Authority Resolver -> AuthorityResolution -> Memory Admission -> MemoryRepository
```

Authority never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE.

Frozen `EpistemicClass`:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Frozen `AuthorityResolutionStatus`:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

Authority, AuthorityResolutionConfidence, SourceReliability, BeliefConfidence and RetrievalRelevance remain separate concepts.

Concrete contradiction identity requires a real VALID same-slot candidate, same normalized predicate, compatible temporal scope, truly incompatible value/target/polarity and one uniquely safe target. Same actor/entity or different text alone never proves contradiction. Temporal change is not contradiction by default.

Historical Python Authority material remains oracle/compatibility evidence only; MIP owns semantics. Current root `AuthorityDecision` remains compatibility-only. `BasicAuthorityResolver` remains a conservative placeholder and does not perform semantic contradiction detection.

## Kotlin Authority contract types phase — COMPLETE

Branch used:

`authority-kotlin-contracts-v1`

Phase base:

`bf8ef4aadcc6a73e85e920968a926bf4b838a0fa`

PR:

`#11`

Merge:

`b87dadf376300587511a7dbce594b0fe88695798`

Post-merge main CI:

```text
run = 33954180260
Run tests = SUCCESS
job conclusion = SUCCESS
```

### Files introduced

```text
src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt
src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt
```

No MipBridge, root DTO, orchestrator, Memory, NLU, Affective or other-repository implementation was modified by this phase.

### Canonical Kotlin Authority types now present

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

Semantics:

- `EpistemicClass` exactly matches AUTHORITY-1.0;
- `AuthorityResolutionStatus` exactly matches AUTHORITY-1.0;
- `AuthorityResolutionConfidence` is normalized `[0,1]` and distinct from Authority class;
- `SourceReliability` is normalized `[0,1]` and distinct from resolver confidence;
- `MemoryRef` is an opaque nonblank identifier, not a numeric protocol type;
- frozen `AUTHORITY.*` reason codes are centralized and observable diagnostics only.

No resolver method/class, Memory access, contradiction algorithm or persistence operation exists in these types.

### Contract tests

Tests verify:

```text
exact EpistemicClass vocabulary
exact AuthorityResolutionStatus vocabulary
AuthorityResolutionConfidence [0,1]
NaN/out-of-range rejection
SourceReliability [0,1]
MemoryRef opaque/nonblank behavior
frozen AUTHORITY reason-code namespace/set
```

One new test initially had a counting error:

```text
expected frozen reason codes = 18
actual frozen AUTHORITY-1.0 codes = 19
```

First gate:

`33953927557` = FAILED, 53 tests / 1 failed new assertion.

Root cause was test-only. Frozen constants/runtime were not changed to force a pass.

Test-only fix:

`831229d9bb77728782ffb84e60a4cfc5f3567c55`

Correct expectation:

`18 -> 19`

Subsequent PR CI:

```text
33954038866 = SUCCESS
33954114182 = SUCCESS on final PR head
```

Post-merge main CI:

`33954180260 = SUCCESS`

No gate was lowered.

## Shared runtime dependencies — STILL NOT IMPLEMENTED

```text
MatrixContextSnapshot runtime
ContextEntry runtime
RetrievalQuery/Result runtime
ProvenanceRef runtime
```

These are universal MIP types and must NOT be redefined privately under `authority/`.

Because those shared types are not yet implemented, the following remain intentionally deferred:

```text
AuthorityResolveRequest runtime DTO
full AuthorityResolution runtime DTO
real AuthorityResolver
semantic contradiction algorithm
```

## Explicitly NOT IMPLEMENTED

```text
AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime
full AuthorityResolution runtime DTO
MatrixContextSnapshot
RetrievalQuery/Result
ProvenanceRef
MipBridge final Authority migration
root AuthorityDecision migration
Memory Kotlin/Room
PersistentConsolidation
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
```

Other repositories modified:

`false`

## Next architectural step

Before implementing the real Authority Resolver, implement/freeze the minimal shared MIP runtime evidence types required by AUTHORITY-1.0, without creating module-private duplicates:

```text
MatrixContextSnapshot / ContextEntry contract types
RetrievalResult/read-only evidence contract
ProvenanceRef runtime type
```

This should be a separate bounded task/checkpoint. It must remain contract/data-structure work first; no Memory retrieval engine or Authority business logic should be smuggled into those shared types.

Only after those shared contracts are available should Assembling add:

```text
AuthorityResolveRequest
AuthorityResolution
AuthorityResolver implementation
```

## STOP / exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
current integrated HEAD before this continuity finalization = b87dadf376300587511a7dbce594b0fe88695798
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = INTEGRATED / TESTED
post-merge CI = 33954180260 SUCCESS
real AuthorityResolver = NOT_STARTED
shared Context/Retrieval/Provenance runtime = NOT_IMPLEMENTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = shared MIP evidence contract types only
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze or Kotlin Authority value-type work.