# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T09:02+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active contract-freeze branch: `authority-contract-freeze-v1`  
Continuity schema: `matrix.assembling.continuity.v19`  
Authority-freeze start HEAD: `afc5cd7e535dc08d09455339a056c71ba5dc6ea2`

## Canonical work rules

- one repository at a time unless the owner explicitly says otherwise;
- no writes to other repositories without explicit authorization;
- historical repositories are backup/checkpoint sources;
- when a component changes, update code/tests/docs/continuity coherently in the active repo;
- do not create a second protocol or adapter family when MIP/MipBridge already own that boundary;
- every new functional module must live in a dedicated directory/package;
- existing root runtime files are not moved for cosmetic cleanup.

Current owner-authorized task:

```text
AUTHORITY CONTRACT FREEZE ONLY
```

Explicitly NOT authorized/implemented by this task:

```text
real Authority Resolver Kotlin
Authority runtime DTO migration
MipBridge migration to final Authority profile
MatrixContextSnapshot runtime
retrieval runtime
Memory Kotlin/Room
Memory Admission durable wiring
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
orchestrator rewiring
```

Other repositories modified:

```text
false
```

Other repositories / saved Python reference material may be read-only evidence only.

---

## Assembling cleanup baseline — COMPLETE

Cleanup start HEAD:

`ef433a3aed519b31efe9289a8df78ed974170510`

Cleanup PR #8:

```text
branch = assembling-mip-cleanup
final tested head = 2e51e1b51df101d0fdb25f9cb567201839fc07d6
merge SHA = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR CI = 33951029064 SUCCESS
post-merge main CI = 33951548865 SUCCESS
```

Documentation finalization PR #9:

```text
head = 13822b4965390325195876b5f297451b84fb8153
merge SHA = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final main CI = 33951808519 SUCCESS
```

Cleanup sequence completed:

```text
inventory                     COMPLETE
contract mapping              COMPLETE
incompatibility matrix        COMPLETE
MIP Bridge audit              COMPLETE
structural cleanup            COMPLETE
legacy-path classification    COMPLETE
round-trip / strict tests     PASS
documentation                 COMPLETE
```

Structural result:

```text
files moved = 0
files renamed = 0
files deleted = 0
mass package refactor = false
```

Canonical legacy quarantine:

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

Future module rule:

```text
NEW FUNCTIONAL MODULE
→ dedicated directory/package
```

---

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

No `MatrixInterop`, `CommonProtocol`, `UniversalClaim`, second Context model or parallel diagnostic protocol is authorized.

Core semantic invariants:

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

---

## MIP Bridge state

Canonical adapter:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

Canonical cleanup/compatibility audit:

`docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md`

Tests:

`src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt`

Current MIP general field status:

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

Entity resolution status is separate:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

Existing contradiction seam fail-closed mapping:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> null / None
UNKNOWN -> ERROR if native field cannot preserve it
UNRESOLVED -> ERROR
AMBIGUOUS -> ERROR
CONFLICTED -> ERROR
UNAVAILABLE -> ERROR
NO_MATCH -> ERROR when native null has only no-contradiction meaning
ERROR -> ERROR
```

Python arbitrary-size integer -> Kotlin `Long` requires explicit range validation; overflow is an error, never truncation.

---

# AUTHORITY CONTRACT FREEZE — CURRENT CHECKPOINT

Canonical Authority profile created:

```text
docs/MIP_AUTHORITY_CONTRACT.md
MIP-1.0 / AUTHORITY-1.0
Status = CANONICAL MIP AUTHORITY PROFILE / CONTRACT FROZEN
```

Profile creation commit:

`a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b`

Document index alignment commit:

`dc3407cb2324c4bf96542ff8ff5ad7c441b13489`

This profile is subordinate to `MATRIX_INTERMODULE_PROTOCOL.md`; it is not a second protocol.

## Frozen Authority boundary

```text
TypedClaim
→ Authority Resolver
→ AuthorityResolution
→ Memory Admission
→ MemoryRepository
```

Runtime durable path remains:

```text
pre-response
TypedClaim + read-only context/retrieval
→ AuthorityResolution
→ preflight / MemoryCandidate only

accepted output/action
→ VALIDATE
→ PersistentConsolidationPort
→ Memory Admission
→ MemoryRepository
```

Authority Resolver may read evidence but must never persist or mutate Memory.

Authority Resolver never owns:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Those remain Memory Admission decisions.

## Frozen EpistemicClass

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Rules:

- WORLD_TRUTH only from explicit trusted WORLD/Game provenance;
- user/NPC text cannot self-grant WORLD_TRUTH;
- OBSERVATION requires direct structured observation provenance;
- REPORT preserves attributed external source;
- INFERENCE requires explicit derived-from evidence;
- BELIEF represents opinion/supposition/belief semantics.

## Frozen confidence separation

```text
EpistemicClass
!= InterpretationConfidence
!= AuthorityResolutionConfidence
!= SourceReliability
!= BeliefConfidence
!= RetrievalRelevance
```

`AuthorityResolutionConfidence` means confidence that the Authority classification/resolution is correct. It does not change the EpistemicClass rank.

`SourceReliability` is supplied only when real evidence/provider exists; never derive it from NLU confidence.

`BeliefConfidence` remains Belief-state responsibility.

## Frozen AuthorityResolveRequest

Conceptual language-independent input:

```text
AuthorityResolveRequest
- requestId
- claim: TypedClaim
- contextSnapshot: MatrixContextSnapshot
- retrievalResult?: RetrievalResult
- provenance
```

Authority consumes structured semantics. It must not re-parse free text to reinvent subject/predicate/object/target/owner/perspective/polarity/time/source.

## Frozen AuthorityResolution

```text
AuthorityResolution
- resolutionId
- claimId
- contextSnapshotId
- retrievalQueryId?
- resolutionStatus: AuthorityResolutionStatus
- authority: MipField<EpistemicClass>
- authorityResolutionConfidence: MipField<Confidence>
- sourceReliability: MipField<Confidence>
- contradictedMemoryRef: MipField<MemoryRef>
- candidateMemoryRefs[]
- ambiguityReasons[]
- reasonCodes[]
- provenance
```

The full claim is not duplicated when immutable `claimId` + workspace/envelope already provide it.

## Frozen AuthorityResolutionStatus

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

`COMPLETE` means the requested Authority assessment completed, not “persist this claim”.

`PARTIAL` cannot masquerade as complete persistence authorization.

`HOLD` means unresolved semantic/entity/temporal/source ambiguity prevents safe resolution.

## Frozen contradictedMemoryRef semantics

```text
PRESENT(memoryRef)
= one concrete semantic contradiction target identified

NOT_APPLICABLE
= contradiction assessment completed and no target exists

UNKNOWN
= target may exist but genuinely unknown

UNRESOLVED
= assessment incomplete

AMBIGUOUS
= several plausible targets; no unique target

CONFLICTED
= contradiction-target evidence internally inconsistent

UNAVAILABLE
= required retrieval/context evidence unavailable

NO_MATCH
= retrieval succeeded and found no matching candidate

ERROR
= assessment failed
```

Hard native mapping rule:

```text
PRESENT -> concrete native ID
NOT_APPLICABLE -> nullable None/null
all non-representable uncertainty/error states -> FAIL CLOSED
```

A Python/Kotlin native `None/null` must never silently mean both “no contradiction” and “could not resolve contradiction”.

## Frozen semantic contradiction rules

A contradiction requires the same relevant semantic slot and real incompatibility.

Comparison considers where applicable:

```text
resolved subject identity
normalized PredicateId
owner/scope
perspective/source scope for subjective/reported claims
target/object/value
polarity
temporal validity/reference event
predicate-specific identity keys
```

Concrete durable contradiction target requires:

1. candidate memory exists;
2. candidate is VALID at the resolution snapshot;
3. semantic slot is the same;
4. normalized predicate/property is the same;
5. temporal scopes overlap / same event-state applies;
6. values/targets/polarity are mutually incompatible;
7. one unique target can be identified safely.

Forbidden heuristics by themselves:

```text
same actor
same entity mentioned
text differs
low lexical similarity
shared words
higher confidence
lower confidence
higher Authority
lower Authority
```

Examples:

```text
Albert lives in Venice
Albert loves coffee
→ NO CONTRADICTION
```

```text
Albert lives in Venice
Albert lives in Milan
→ POSSIBLE only if same semantic + temporal slot
```

```text
Yesterday Albert was in Venice
Today Albert is in Milan
→ NO CONTRADICTION by default
```

Explicit correction is strong candidate evidence but does not bypass same-slot/VALID/temporal verification.

Multiple plausible targets without a unique safe target:

```text
contradictedMemoryRef = AMBIGUOUS
resolutionStatus = HOLD or PARTIAL
```

## Frozen Python-reference compatibility policy

Read-only saved evidence confirms a historical Authority surface with at least:

```text
claim
authority
confidence
status
reasoning
contradicts_memory_id
candidate_memories
is_contradiction_detected
ambiguity_level
```

However no authoritative `authority_models.py` file with frozen filename/version/checksum is currently identified in the saved Library.

Therefore:

```text
Python reference = oracle / compatibility evidence
MIP Authority profile = canonical contract owner
```

Mapping constraints:

```text
Python authority -> MIP authority
Python confidence -> authorityResolutionConfidence ONLY after source audit confirms meaning
Python contradicts_memory_id -> contradictedMemoryRef
Python candidate_memories -> candidateMemoryRefs
Python reasoning -> legacy diagnostic text only
Python is_contradiction_detected -> redundant consistency check
Python ambiguity_level -> legacy diagnostic metadata; not canonical policy
```

If historical `is_contradiction_detected` disagrees with contradiction identity/status, adapter must fail.

Historical `contradicts_memory_id=None` maps to `NOT_APPLICABLE` only if the historical result proves the contradiction assessment completed successfully; unresolved/unavailable/error must remain explicit.

## Current Kotlin compatibility state

Current root:

```text
AuthorityDecision
- accepted
- ownerResolved
- sourceType
- conflictStatus
- reason
```

is **not** the canonical AuthorityResolution contract.

Current `MipAuthorityResolutionV1` in `MipBridge.kt` is a transition/compatibility projection, not the final implementation of AUTHORITY-1.0.

No runtime DTO or bridge migration was authorized in this freeze task.

Future real Authority module location is reserved:

```text
src/main/kotlin/matrix/assembling/authority/
```

Reserved future names:

```text
AuthorityResolveRequest
AuthorityResolution
AuthorityResolutionStatus
EpistemicClass
AuthorityResolver
```

## Required future tests before replacing BasicAuthorityResolver

At minimum:

1. trusted WORLD provenance -> WORLD_TRUTH;
2. direct structured evidence -> OBSERVATION;
3. third-party attributed source -> REPORT;
4. explicit derived evidence -> INFERENCE;
5. belief/supposition -> BELIEF;
6. Authority != AuthorityResolutionConfidence;
7. real same-slot contradiction -> concrete target;
8. same actor + unrelated predicate -> no contradiction;
9. same predicate + incompatible value + overlapping time -> contradiction;
10. different temporal scopes -> no false contradiction;
11. correction prioritizes but does not bypass verification;
12. NO_MATCH != UNAVAILABLE;
13. SUPERSEDED memory cannot be active contradiction target;
14. multiple ambiguous targets -> conservative HOLD/PARTIAL;
15. unresolved owner/subject/time -> conservative behavior;
16. resolver performs no writes;
17. Python mapping fails closed on semantic loss;
18. AuthorityResolution -> Memory Admission preserves contradiction identity;
19. all existing Assembling tests remain green;
20. Memory Foundation/Admission tests remain unchanged when that repository becomes active.

---

## Existing Memory boundary preserved

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

Durable direction:

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

Contradiction is identified upstream by Authority/Belief resolution. Semantic changes use `supersede()` and preserve lineage.

---

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

Retrieval levels remain:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — on relevant hits
LEVEL 3 DEEP_OR_MULTI_HOP — explicit complex need only
```

Real runtime context/retrieval remains `NON_CABLATO`.

---

## Still NON_CABLATO / NOT IMPLEMENTED

- real Authority Resolver;
- Kotlin runtime implementation of AUTHORITY-1.0;
- migration of current `AuthorityDecision`;
- migration of `MipAuthorityResolutionV1`;
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

## Current branch checkpoint

```text
branch = authority-contract-freeze-v1
start HEAD = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
contract file = docs/MIP_AUTHORITY_CONTRACT.md
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
index alignment = dc3407cb2324c4bf96542ff8ff5ad7c441b13489
code changed = false
runtime behavior changed = false
other repositories modified = false
```

## Exact next-action / STOP rule

This task ends after documentation validation/CI and owner review.

Do not automatically implement Authority Resolver.

```text
AUTHORITY CONTRACT = FROZEN
AUTHORITY RESOLVER = NOT IMPLEMENTED
MIP BRIDGE FINAL AUTHORITY MIGRATION = NOT STARTED
MEMORY = NOT MODIFIED
OTHER REPOSITORIES MODIFIED = false
NEXT = AWAIT OWNER REVIEW BEFORE AUTHORITY IMPLEMENTATION TASK
```
