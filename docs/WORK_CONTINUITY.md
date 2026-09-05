# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T09:02+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v18`  
Cleanup start HEAD: `ef433a3aed519b31efe9289a8df78ed974170510`  
Cleanup PR: `#8`  
Cleanup tested HEAD: `2e51e1b51df101d0fdb25f9cb567201839fc07d6`  
Cleanup merge SHA: `ff38d09f73a1eec8b2a72a24571b92f1954c729c`  
PR CI run: `33951029064` — SUCCESS  
Post-merge main CI run: `33951548865` — SUCCESS

## Canonical work rules

- one repository at a time unless the owner explicitly says otherwise;
- no writes to other repositories without explicit authorization;
- historical repositories are backup/checkpoint sources;
- when a component changes, update code/tests/docs/continuity coherently in the active repo;
- do not create parallel specifications when an existing canonical document can be updated;
- every new functional module must live in a dedicated directory/package;
- existing root runtime files are not moved for cosmetic cleanup.

Current owner scope completed:

```text
ASSEMBLING STRUCTURE
+
MIP INTEROP CONSOLIDATION
+
AMBIGUITY REMOVAL
```

Explicitly NOT implemented by this checkpoint:

```text
Authority Resolver real implementation
Memory Kotlin/Room
real retrieval
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
orchestrator rewiring to MIP
```

Other repositories modified:

```text
false
```

## Canonical MIP state

Assembling owns:

```text
docs/MATRIX_INTERMODULE_PROTOCOL.md
version = MIP-1.0
```

Hard rule:

```text
MIP = the single cross-module semantic authority
```

No `MatrixInterop`, `CommonProtocol`, `UniversalClaim`, second Context model or parallel diagnostic protocol was introduced.

Shared semantic invariants remain:

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS

TypedClaim ≠ Belief
Belief ≠ Memory
Memory ≠ State
State ≠ Context
Relationship ≠ Affective
SexualInterest ≠ CurrentDesire
CurrentDesire ≠ Consent
Contradiction ≠ Supersession
InterpretationConfidence ≠ SourceReliability
SourceReliability ≠ Authority
Authority ≠ BeliefConfidence
BeliefConfidence ≠ RetrievalRelevance
```

Shared roles remain globally distinct:

```text
speaker
observer
source
subject
target
owner
perspective
```

## MIP Bridge state

Canonical adapter:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

Canonical audit:

```text
docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md
```

Tests:

```text
src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt
```

PR #7 history preserved:

```text
PR #7 = MERGED
merge SHA = dfd2da5ced902cf601530e65bb11c43e89dbd98a
```

Cleanup PR #8:

```text
branch = assembling-mip-cleanup
base = ef433a3aed519b31efe9289a8df78ed974170510
final tested head = 2e51e1b51df101d0fdb25f9cb567201839fc07d6
merge SHA = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR CI = 33951029064 SUCCESS
post-merge main CI = 33951548865 SUCCESS
```

## Cleanup sequence completed

The required sequence was completed in order:

```text
1. inventory                     COMPLETE
2. contract mapping              COMPLETE
3. incompatibility matrix        COMPLETE
4. MIP Bridge audit              COMPLETE
5. structural cleanup            COMPLETE
6. legacy-path classification    COMPLETE
7. round-trip / strict tests     PASS
8. documentation                 COMPLETE
9. STOP                          ACTIVE
```

## Repository structure result

Canonical runtime root remains:

```text
src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt
src/main/kotlin/matrix/assembling/IntegrationPorts.kt
src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt
src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt
```

Existing module directories remain:

```text
src/main/kotlin/matrix/assembling/adapters/
src/main/kotlin/matrix/assembling/mip/
src/main/kotlin/matrix/assembling/coherence/
```

Future rule:

```text
NEW FUNCTIONAL MODULE
→ dedicated directory/package
```

Cleanup structural result:

```text
files moved = 0
files renamed = 0
files deleted = 0
mass package refactor = false
```

## Legacy compatibility quarantine

```text
contracts/MatrixAssemblyContracts.kt
= KEEP_COMPATIBILITY

pipeline/MatrixAssemblyPipeline.kt
= KEEP_COMPATIBILITY / DEPRECATED

prompt/SemanticFrameToPrompt.kt
= KEEP_COMPATIBILITY / DEPRECATED

coherence/CoherenceGuard.kt
= KEEP_COMPATIBILITY / DEPRECATED
```

The root/frame-based runtime remains authoritative.

## MIP ambiguity fixes completed

### General field status

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

### Entity resolution status

Separate from field presence:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

Resolved EntityRef no longer uses generic `PRESENT` as its resolution state.

### Authority contradiction seam

Canonical cross-language seam remains:

```text
Python reference:
contradicts_memory_id: Optional[int]

MIP:
contradictedMemoryId: explicit opaque decimal String ID

future Kotlin Memory boundary:
contradictedMemoryId: Long?
```

Fail-closed conversion:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> null / None
UNKNOWN -> ERROR
UNRESOLVED -> ERROR
AMBIGUOUS -> ERROR
CONFLICTED -> ERROR
UNAVAILABLE -> ERROR
NO_MATCH -> ERROR
ERROR -> ERROR
```

Python arbitrary-size integer → Kotlin `Long` requires explicit range validation. Overflow is an error, never truncation.

Partial Python Authority projection cannot silently discard populated canonical Authority fields.

Current root `AuthorityDecision` cannot represent contradiction identity and is NOT a final Authority contract.

## Authority Resolver boundary preserved

Canonical persistence-side architectural boundary remains:

```text
TypedClaim
→ Authority Resolver
→ Memory Admission
→ MemoryRepository
```

No Authority Resolver was implemented in this cleanup.

Current:

```text
BasicAuthorityResolver
= conservative placeholder / gate
≠ real semantic Authority Resolver
```

It does not:
- query Memory;
- retrieve candidate memories;
- compare normalized subject/predicate/value/time;
- detect semantic contradiction;
- populate contradiction identity;
- perform final per-claim Authority/Belief resolution.

Authority and confidence remain separate concepts.

## Memory boundary preserved

Pre-response durable write remains forbidden:

```text
MemoryPreflightPort
= read/evaluate/propose only
```

Hard guard:

```text
stableWrite == false
memoryIds == []
```

Durable direction remains:

```text
VALIDATE
→ PersistentConsolidationPort
→ Memory Admission
→ MemoryRepository
```

Memory Admission decisions remain:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Contradiction is identified upstream by Authority/Belief resolution; Memory Admission consumes explicit contradiction identity and does not invent semantic conflict from different text/shared actors.

Semantic change uses `supersede()` and preserves lineage.

## Context / retrieval direction preserved

Canonical context target:

```text
MatrixContextSnapshot
→ immutable/read-only
→ versioned
→ typed ContextEntry values
→ explicit domain availability
```

Every normal turn eventually performs:

```text
LIGHTWEIGHT MEMORY INDEX PROBE
```

Retrieval levels:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — on relevant hits
LEVEL 3 DEEP_OR_MULTI_HOP — explicit complex need only
```

Real retrieval remains `NON_CABLATO`.

## Round-trip / strict tests

`MipBridgeTest` now covers:
- Matrix-NLU claim round-trip;
- TypedClaim round-trip;
- Python Authority contradiction round-trip;
- Python → MIP → Kotlin seam;
- no-contradiction mapping;
- Assembling Authority representable round-trip;
- contradiction data-loss rejection;
- Kotlin Long overflow;
- primitive wire-map round-trip;
- missing field rejection;
- illegal status/value rejection;
- MemoryResult round-trip;
- AffectiveState round-trip;
- Coherence enum round-trip;
- explicit `RESOLVED` entity status;
- invalid unresolved EntityRef with ID rejection;
- `NO_MATCH` and `ERROR` status support;
- unresolved/unavailable contradiction cannot collapse to nullable native ID;
- partial Python projection cannot discard populated canonical fields.

All existing test files remain active and were not weakened or removed.

## Regression evidence

```text
PR #8 CI run 33951029064
Run tests = SUCCESS
job = SUCCESS

main post-merge run 33951548865
Run tests = SUCCESS
job = SUCCESS
```

## Residual risks

### P0 remaining

1. Root `AuthorityDecision` lacks contradiction identity and is not sufficient for a real memory-backed Authority integration.
2. Complete frozen/reference Python `AuthorityResolution` is not stored in Assembling; only the confirmed contradiction seam is grounded here.
3. Owner/perspective remain nullable/sentinel-rich in native DTOs.
4. `worldTruth: Boolean` remains compatibility-only and must never self-grant Authority.
5. `BasicAuthorityResolver` does not perform semantic contradiction detection.

### P1 remaining

1. `TypedClaim` lacks dialogue act and explicit semantic-domain marker.
2. Native `null` / `UNKNOWN` / `NONE` remain outside MIP.
3. Predicate/dialogue/source/status remain mostly stringly typed.
4. Full `TemporalRef`, `ProvenanceRef`, modality and claimKind parity is deferred.
5. `MatrixEnvelope<T>` is not yet the runtime wrapper for all boundaries.
6. Legacy duplicate vocabulary remains, now quarantined/deprecated.
7. Only the high-risk Authority seam has an explicit primitive wire codec.

### P2

1. Python snake_case vs Kotlin camelCase is adapter-local.
2. `MemoryAdmissionResult.status` remains a string.
3. Semantic marker registry remains an open string map.
4. Root runtime is not physically split into `runtime/ports/diagnostics`; current move risk exceeds benefit.

## Still NON_CABLATO / NOT IMPLEMENTED

- real Authority Resolver;
- full formal `MatrixEnvelope<T>` runtime integration;
- full typed `TemporalRef` / `ProvenanceRef` runtime integration;
- typed confidence wrappers across native DTOs;
- Predicate registry implementation;
- `TurnWorkspace` migration;
- runtime `MatrixContextSnapshot` / `ContextEntry`;
- read-only context ports;
- real memory index/retrieval;
- real BeliefState;
- real OutputValidator implementation;
- real PersistentConsolidation implementation;
- Kotlin/Room Memory Foundation;
- Relationship controller;
- Intimacy/Consent resolver;
- BDI-lite/Decision;
- real GGUF bridge;
- Android integration;
- Reflection.

## Exact restart rule

```text
DO NOT restart the architecture audit.
DO NOT redo completed cleanup/hardening.
DO NOT invent another protocol/context/adapter family.
DO NOT bypass MipBridge with ad-hoc mappings.
NEW MODULE -> dedicated directory/package.
```

If the next active repository becomes `MATRIXNEO23/memoria`, explicitly switch repository first and treat its current README as historical where it conflicts with MIP-1.0.

## STOP

```text
ASSEMBLING CLEANUP = COMPLETE
AUTHORITY RESOLVER = NOT IMPLEMENTED
OTHER REPOSITORIES MODIFIED = false
NEXT = AWAIT OWNER REVIEW
```
