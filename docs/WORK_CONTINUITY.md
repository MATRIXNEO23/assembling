# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T10:12+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `mip-evidence-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v29`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update it after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Global work rules

```text
writable repository = MATRIXNEO23/assembling only unless owner explicitly switches
MIP = single cross-module semantic authority
MipBridge = single common interop bridge
new functional module = dedicated directory/package
parallel protocol/context/adapter family = forbidden
cosmetic mass refactor = forbidden
gate/test weakening = forbidden
```

Other repositories remain read-only.

## Completed Assembling cleanup

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

## Canonical MIP state

`docs/MATRIX_INTERMODULE_PROTOCOL.md` = `MIP-1.0`.

Canonical bridge:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

MIP field states:

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

Authority contradiction mapping is fail-closed. Python arbitrary integer -> Kotlin Long uses checked range conversion.

Hard distinctions remain:

```text
TypedClaim != Belief != Memory
Memory != State != Context
Relationship != Affective != Intimacy
SexualInterest != CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability != Authority != BeliefConfidence != RetrievalRelevance
```

Roles remain distinct: speaker, observer, source, subject, target, owner, perspective.

## AUTHORITY-1.0 contract freeze — COMPLETE

Canonical file:

`docs/MIP_AUTHORITY_CONTRACT.md`

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
TypedClaim
-> Authority Resolver
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Authority may read evidence but never writes Memory and never owns SAVE/SUPERSEDE/REJECT/IGNORE.

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

Authority, AuthorityResolutionConfidence, SourceReliability, BeliefConfidence and RetrievalRelevance remain separate.

Concrete contradiction identity requires:

1. candidate exists;
2. candidate is VALID;
3. same resolved semantic slot;
4. same normalized predicate;
5. compatible/overlapping temporal scope or same relevant event;
6. truly incompatible value/target/polarity;
7. one uniquely safe target.

Same actor/entity mention, different text, lexical similarity or confidence/authority rank alone never proves contradiction. Temporal change is not contradiction by default. Correction is strong candidate evidence but not automatic contradiction/supersession.

Historical Python Authority material remains oracle/compatibility evidence only. MIP owns the canonical contract. Current root `AuthorityDecision` remains compatibility-only. `BasicAuthorityResolver` remains a conservative placeholder and does not perform semantic contradiction detection.

## Kotlin Authority contract value types — COMPLETE / INTEGRATED

Phase branch:

`authority-kotlin-contracts-v1`

Phase base:

`bf8ef4aadcc6a73e85e920968a926bf4b838a0fa`

PR:

`#11`

Merge SHA:

`b87dadf376300587511a7dbce594b0fe88695798`

Files introduced:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityTypes.kt
src/test/kotlin/matrix/assembling/authority/AuthorityTypesTest.kt
```

Kotlin types now present:

```text
EpistemicClass
AuthorityResolutionStatus
AuthorityResolutionConfidence
SourceReliability
MemoryRef
AuthorityReasonCode
```

Properties:

- exact AUTHORITY-1.0 enum vocabulary;
- confidence wrappers normalized `[0,1]` and reject NaN/out-of-range values;
- MemoryRef is opaque/nonblank, not a numeric protocol type;
- frozen `AUTHORITY.*` reason codes centralized;
- no resolver/business logic;
- no Memory access;
- no semantic contradiction algorithm;
- no Context/Retrieval/Provenance duplicates;
- no MipBridge/root DTO/orchestrator migration.

### Test history

Initial new-test gate:

```text
CI 33953927557
compile = PASS
53 tests = 1 FAIL
failure = Authority reason-code count expected 18 but frozen contract contains 19
```

This was a test-only counting error. Contract/runtime constants were NOT changed to force a pass.

Test-only correction commit:

`831229d9bb77728782ffb84e60a4cfc5f3567c55`

Correction:

```text
expected reason-code count 18 -> 19
```

Subsequent green PR gates:

```text
33954038866 = SUCCESS
33954114182 = SUCCESS on final PR head
```

Post-merge main gate:

```text
33954180260 = SUCCESS
```

Continuity-finalization commit:

`05ff921d33e3f9c133ef7ea4fd9026c4966c67b7`

Previous continuity CI:

```text
33954352500 = SUCCESS
```

No gate was lowered and no pre-existing test was modified to hide a runtime failure.

## Shared MIP evidence types — ACTIVE

Task:

```text
SHARED MIP EVIDENCE CONTRACT TYPES ONLY
```

Branch:

`mip-evidence-contracts-v1`

Start HEAD:

`05ff921d33e3f9c133ef7ea4fd9026c4966c67b7`

Scope:

- implement shared MIP runtime contract types for `ProvenanceRef`;
- implement shared `MatrixContextSnapshot` / `ContextEntry` / domain availability types;
- implement shared `RetrievalQuery` / `RetrievalResult` contract types;
- preserve explicit NO_MATCH vs UNAVAILABLE vs ERROR semantics;
- add structural/fail-closed/wire-round-trip tests without introducing a new protocol or adapter family;
- keep all types in the existing shared `matrix.assembling.mip` package;
- no retrieval engine;
- no Memory implementation;
- no Authority resolver/business logic;
- no orchestrator rewiring;
- no final MipBridge Authority migration;
- no writes to other repositories.

Design rule:

```text
shared Context/Retrieval/Provenance contracts belong to MIP
NOT to authority/
NOT to memory/
NOT to a new parallel protocol
```

Because this task is structural only, these remain deferred:

```text
AuthorityResolveRequest runtime DTO
full AuthorityResolution runtime DTO
real AuthorityResolver
semantic contradiction algorithm
```

## Explicitly NOT IMPLEMENTED

```text
real AuthorityResolver
semantic contradiction algorithm
AuthorityResolveRequest runtime
full AuthorityResolution runtime DTO
retrieval engine
Memory Kotlin/Room
PersistentConsolidation
MipBridge final Authority migration
root AuthorityDecision migration
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
```

Other repositories modified:

`false`

## STOP / exact restart point

```text
repo = MATRIXNEO23/assembling
branch = mip-evidence-contracts-v1
base HEAD = 05ff921d33e3f9c133ef7ea4fd9026c4966c67b7
AUTHORITY-1.0 = FROZEN
Kotlin Authority value types = COMPLETE / INTEGRATED / TESTED
shared Context/Retrieval/Provenance runtime = TASK STARTED / NO CODE YET
real AuthorityResolver = NOT_STARTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
NEXT = IMPLEMENT MINIMAL SHARED MIP EVIDENCE TYPES + TESTS
```

Do not redo cleanup, MIP audit, AUTHORITY-1.0 freeze, or Kotlin Authority value-type work.