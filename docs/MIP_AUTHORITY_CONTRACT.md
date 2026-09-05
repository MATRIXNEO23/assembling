# MIP-1.0 Authority Contract

Status: **CANONICAL MIP AUTHORITY PROFILE / CONTRACT FROZEN**  
Version: `MIP-1.0 / AUTHORITY-1.0`  
Date: 2026-09-05  
Owner repository: `MATRIXNEO23/assembling`  
Parent protocol: `docs/MATRIX_INTERMODULE_PROTOCOL.md`

## 1. Scope and authority

This document freezes the Authority-resolution profile of MIP-1.0. It is **not a second protocol**. All definitions below are subordinate to, and must be interpreted with, `docs/MATRIX_INTERMODULE_PROTOCOL.md`.

This freeze exists before implementation so Python reference code, future Kotlin production code, Memory Admission and the MIP Bridge cannot evolve incompatible Authority meanings.

This task does **not** implement an Authority Resolver, Memory, retrieval, `MatrixContextSnapshot`, Kotlin/Room persistence or orchestrator rewiring.

## 2. Non-negotiable boundary

The cognitive/persistence boundary remains:

```text
TypedClaim
→ Authority Resolver
→ AuthorityResolution
→ Memory Admission
→ MemoryRepository
```

For the runtime durable path, MIP's post-validation rule also remains mandatory:

```text
pre-response
TypedClaim + read-only context/retrieval
→ Authority Resolution
→ MemoryCandidate / preflight only

accepted output/action
→ VALIDATE
→ PersistentConsolidationPort
→ Memory Admission
→ MemoryRepository
```

Therefore:

- Authority Resolver may read evidence but never write Memory;
- Authority Resolver never calls `save()`, `supersede()`, `delete()` or metadata-write APIs;
- Authority Resolver never decides `SAVE`, `SUPERSEDE`, `REJECT` or `IGNORE`;
- Memory Admission consumes Authority output and owns those persistence decisions;
- Memory Admission must not invent semantic contradiction from shared actors, text difference or lexical dissimilarity.

## 3. Authority is not confidence

The following remain distinct protocol concepts:

```text
EpistemicClass
!= InterpretationConfidence
!= AuthorityResolutionConfidence
!= SourceReliability
!= BeliefConfidence
!= RetrievalRelevance
```

### 3.1 EpistemicClass

Canonical values:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

`EpistemicClass` is categorical. It is not a probability.

### 3.2 AuthorityResolutionConfidence

A normalized `[0.0, 1.0]` confidence describing how certain the Authority Resolver is that its **Authority classification/resolution** is correct.

It does not increase or decrease the semantic rank of an `EpistemicClass`.

### 3.3 SourceReliability

A normalized `[0.0, 1.0]` value only when a real source-reliability provider/evidence exists. If no such evidence exists, the field is `UNAVAILABLE` or `UNKNOWN` as appropriate; it must not be fabricated from interpretation confidence.

### 3.4 BeliefConfidence

Belongs to Belief-state resolution, not Authority classification. It must not be silently produced by Authority Resolution merely because an Authority class is known.

## 4. Authority classification rules

### WORLD_TRUTH

`WORLD_TRUTH` may originate only from explicit canonical World/Game state provenance produced by `WORLD` or another explicitly authorized World adapter.

Hard rule:

```text
ordinary user/NPC text
!= WORLD_TRUTH
```

A `worldTruth: Boolean` compatibility field from an older DTO cannot self-grant `WORLD_TRUTH` without trusted provenance.

### OBSERVATION

Direct perceptual/experiential evidence attributable to the Matrix observer or an explicitly modeled observation source.

It must be supported by provenance. It must not be inferred merely because a speaker states something confidently.

### REPORT

Information attributed to another source/person/system.

Example:

```text
Alice says Bob lives in Rome
→ REPORT
```

The observer hearing the report does not thereby become the direct observer of the reported fact.

### INFERENCE

A conclusion explicitly derived from other structured evidence. The derived-from evidence/provenance must be traceable.

Authority Resolver does not turn arbitrary unsupported text into `INFERENCE` simply because it can reason about it.

### BELIEF

Opinion, supposition, belief or subjective proposition represented as such by the structured claim/provenance.

Example:

```text
I think Bob is angry
→ BELIEF
```

unless stronger explicit structured provenance says otherwise.

## 5. Canonical input — AuthorityResolveRequest

Conceptual language-independent contract:

```text
AuthorityResolveRequest
- requestId
- claim: TypedClaim
- contextSnapshot: MatrixContextSnapshot
- retrievalResult?: RetrievalResult
- provenance
```

### 5.1 Required semantics

`claim` is the MIP `TypedClaim` produced by Understanding. Authority must consume structured semantics and must not re-parse natural language to reinvent:

```text
subject
predicate
object/target
owner
perspective
polarity
temporal relation
source
claim kind/modality
```

`contextSnapshot` is read-only.

`retrievalResult`, when available, is the universal MIP retrieval result. For contradiction checking its purpose is normally `CHECK_CONTRADICTION` or a compatible general turn-enrichment result containing the required candidate evidence.

### 5.2 Missing context/retrieval

Missing evidence must be explicit:

```text
NO_MATCH
!= UNAVAILABLE
!= ERROR
!= AMBIGUOUS
```

A successful retrieval with no relevant memories is `NO_MATCH`.

A failed/non-wired Memory provider is not equivalent to “there is no contradiction”.

## 6. Canonical output — AuthorityResolution

The frozen semantic output is:

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

The original claim is referenced by immutable `claimId`; the full claim must not be duplicated merely for transport if it is already present in the turn/workspace/envelope.

## 7. AuthorityResolutionStatus

Canonical overall values:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

Definitions:

- `COMPLETE`: required Authority classification is resolved and all required assessments for the current purpose are complete. `contradictedMemoryRef` is either `PRESENT` or `NOT_APPLICABLE`.
- `PARTIAL`: Authority classification may be resolved, but some requested supporting assessment is unresolved/unavailable. A `PARTIAL` result must not masquerade as a complete persistence authorization.
- `HOLD`: semantic/entity/temporal/source ambiguity prevents safe Authority resolution.
- `UNAVAILABLE`: a required provider/context dependency is unavailable.
- `ERROR`: Authority resolution attempted and failed.

`COMPLETE` does not mean “persist this claim”. Memory Admission remains a separate stage.

## 8. ContradictedMemoryRef semantics

`contradictedMemoryRef` is the canonical language-neutral form of the Python v3 `contradicts_memory_id` seam.

Its `MipField` state has exact meaning:

```text
PRESENT(memoryRef)
= one real semantic contradiction target was identified

NOT_APPLICABLE
= contradiction assessment completed and no contradiction target exists

UNKNOWN
= a target may exist but is genuinely unknown

UNRESOLVED
= evidence exists but contradiction resolution is incomplete

AMBIGUOUS
= multiple plausible incompatible targets remain and no unique target can be selected

CONFLICTED
= evidence about the contradiction target is internally inconsistent

UNAVAILABLE
= required retrieval/context provider could not supply the evidence

NO_MATCH
= retrieval completed and no candidate memory matched the contradiction query

ERROR
= contradiction assessment failed
```

### 8.1 Native null/None mapping

For a nullable native contradiction-ID field:

```text
PRESENT -> concrete native ID
NOT_APPLICABLE -> null / None
```

All other MIP states must fail closed when the native field cannot preserve them.

`NO_MATCH` may eventually be mapped to an explicit native result status, but must not silently become `None` when `None` means only “no contradiction”.

## 9. Semantic contradiction rules

A contradiction exists only when two propositions are semantically incompatible in the same relevant semantic slot.

The comparison must consider, where applicable:

```text
resolved subject identity
normalized PredicateId
owner/scope
perspective/source scope for subjective or reported claims
target/object/value
polarity
temporal validity/reference event
predicate-specific identity keys
```

### 9.1 Required positive evidence

A durable contradiction target requires:

1. the candidate memory exists;
2. the candidate is `VALID` at the resolution snapshot;
3. subject/semantic slot compatibility is established;
4. the predicate/property is the same normalized semantic predicate;
5. temporal scopes overlap or refer to the same relevant event/state;
6. values, targets or polarity are mutually incompatible;
7. ambiguity is low enough to identify one concrete target.

If any critical semantic or temporal identity is unresolved, Authority must **not** set a concrete contradiction ID.

### 9.2 Forbidden contradiction heuristics

The following are insufficient by themselves:

```text
same actor
same entity mentioned
text differs
low lexical similarity
shared words
higher/lower confidence
higher/lower Authority class
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
→ POSSIBLE CONTRADICTION only if the same semantic/temporal slot is established
```

```text
Yesterday Albert was in Venice
Today Albert is in Milan
→ NO CONTRADICTION by default
```

```text
Meeting X is at 15:00
Meeting X is at 16:00
→ POSSIBLE CONTRADICTION if both refer to the same meeting/version/time scope
```

## 10. Correction semantics

An explicit correction is strong evidence for candidate prioritization, but it does not automatically create a contradiction or a supersession.

```text
“No, I was wrong, Meeting X is at 16:00”
```

may prioritize the earlier Meeting X time claim, but Authority must still verify:

- same semantic slot;
- target memory exists;
- target is `VALID`;
- temporal scope is compatible;
- incompatibility is real.

Authority identifies contradiction. Memory Admission decides whether that contradiction results in `SUPERSEDE`, `REJECT`, `SAVE` or `IGNORE`.

## 11. Multiple candidates and ambiguity

If several candidate memories are related but Authority cannot safely select one unique contradiction target:

```text
contradictedMemoryRef = AMBIGUOUS
resolutionStatus = HOLD or PARTIAL
```

Candidate IDs remain visible in `candidateMemoryRefs[]` for diagnostics/evidence.

Authority must never choose a candidate merely because it has the highest retrieval similarity score.

## 12. Candidate memory rules

`candidateMemoryRefs[]` records which candidate memories were actually considered.

The candidate list is diagnostic/evidence identity, not a persistence instruction.

Historical or `SUPERSEDED` memories may be inspected for history when retrieval purpose allows, but they cannot become the active contradiction target passed to Memory Admission as if they were `VALID`.

## 13. No direct repository write dependency

Preferred production boundary:

```text
Authority Resolver
← TypedClaim
← MatrixContextSnapshot
← RetrievalResult / read-only evidence
```

A Python reference implementation may use existing public read-only repository APIs as an oracle implementation detail, but the universal MIP contract must not require a concrete persistence implementation.

A future read port, if needed, must be explicitly read-only.

## 14. Diagnostics

Authority diagnostics contain observable facts, not hidden chain-of-thought.

Required diagnostic surface:

```text
resolutionId
claimId
contextSnapshotId
retrievalQueryId?
resolutionStatus
authority field status/value
authorityResolutionConfidence
sourceReliability field status/value
candidateMemoryRefs
contradictedMemoryRef status/value
ambiguityReasons
reasonCodes
```

Reason-code namespace uses `AUTHORITY.*`.

Initial required reason codes include:

```text
AUTHORITY.RESOLVED.WORLD_TRUTH
AUTHORITY.RESOLVED.OBSERVATION
AUTHORITY.RESOLVED.REPORT
AUTHORITY.RESOLVED.INFERENCE
AUTHORITY.RESOLVED.BELIEF
AUTHORITY.OWNER_UNRESOLVED
AUTHORITY.SUBJECT_UNRESOLVED
AUTHORITY.SOURCE_UNRESOLVED
AUTHORITY.TEMPORAL_UNRESOLVED
AUTHORITY.RETRIEVAL.NO_MATCH
AUTHORITY.RETRIEVAL.UNAVAILABLE
AUTHORITY.CONTRADICTION.IDENTIFIED
AUTHORITY.CONTRADICTION.NONE
AUTHORITY.CONTRADICTION.AMBIGUOUS
AUTHORITY.CONTRADICTION.TEMPORAL_MISMATCH
AUTHORITY.CONTRADICTION.UNRELATED_PREDICATE
AUTHORITY.CORRECTION.CANDIDATE
AUTHORITY.HOLD.AMBIGUOUS
AUTHORITY.ERROR
```

These codes are additive/versioned; they must not encode private reasoning text.

## 15. Compatibility with the historical Python reference

Recovered/saved reference evidence confirms a historical `AuthorityResolution` surface containing at least:

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

The exact canonical `authority_models.py` source artifact is **not currently identified in the saved Library by an authoritative filename/version/checksum**. Therefore the Python format is a reference/oracle input to compatibility work, not the owner of MIP semantics.

### 15.1 Mapping rules

| Historical Python | MIP Authority profile | Rule |
|---|---|---|
| `claim` | `claimId` + existing `TypedClaim` | do not duplicate as a second semantic claim model |
| `authority` | `authority` | explicit enum mapping only |
| `confidence` | `authorityResolutionConfidence` | only if source audit confirms it measures resolver confidence; otherwise STOP |
| `status` | `resolutionStatus` | explicit mapping table required; no free string passthrough |
| `reasoning` | diagnostic legacy text | never canonical decision logic / never chain-of-thought dependency |
| `contradicts_memory_id` | `contradictedMemoryRef` | integer identity becomes opaque MemoryRef |
| `candidate_memories` | `candidateMemoryRefs[]` | identity mapping; do not duplicate entire records unnecessarily |
| `is_contradiction_detected` | redundant consistency check | must agree with contradiction field; mismatch = adapter error |
| `ambiguity_level` | `ambiguityReasons[]` / field status | legacy numeric/string metadata may be retained diagnostically, not canonical policy |

### 15.2 `None` safety

Historical Python `contradicts_memory_id=None` may map to MIP `NOT_APPLICABLE` **only when the historical resolution status proves contradiction assessment completed successfully**.

If historical status means unresolved, ambiguous, unavailable or error, `None` must map to the corresponding explicit MIP state instead.

## 16. Compatibility with current Kotlin Assembling types

Current root `AuthorityDecision` is a compatibility/runtime predecessor:

```text
accepted
ownerResolved
sourceType
conflictStatus
reason
```

It is **not** the frozen canonical Authority contract.

Field interpretation:

- `accepted`: compatibility gate output; not equivalent to Memory Admission and absent from canonical `AuthorityResolution`;
- `ownerResolved`: precondition/diagnostic fact, not Authority class;
- `sourceType`: compatibility projection of claim provenance/source classification;
- `conflictStatus`: too weak to preserve canonical contradiction identity/status;
- `reason`: diagnostic compatibility text, not canonical reason-code authority.

A complete canonical `AuthorityResolution` cannot be losslessly converted to current `AuthorityDecision` when it carries information the DTO cannot represent.

The bridge must continue to fail closed rather than drop those fields.

## 17. Compatibility with current `MipAuthorityResolutionV1`

The current Kotlin `MipAuthorityResolutionV1` in `MipBridge.kt` is a **transition/compatibility projection**, not the final implementation of this frozen profile.

Current fields:

```text
accepted
ownerResolved
sourceType
conflictStatus
contradictedMemoryId
reason
```

Future migration must map it incrementally to the frozen `AuthorityResolution` profile. This freeze does not authorize changing `MipBridge.kt` or runtime DTOs.

No second bridge or protocol may be created during migration.

## 18. Future Kotlin implementation location

When implementation is explicitly authorized, the functional Authority module must obey the repository rule:

```text
src/main/kotlin/matrix/assembling/authority/
```

Recommended future contract/type names:

```text
AuthorityResolveRequest
AuthorityResolution
AuthorityResolutionStatus
EpistemicClass
AuthorityResolver
```

These names are reserved by this freeze to avoid another parallel Authority vocabulary.

The implementation must not be placed as unrelated new files in the root `matrix/assembling` package.

## 19. Mandatory future test gates

Before a real Authority Resolver may replace `BasicAuthorityResolver`, tests must prove at least:

1. WORLD_TRUTH only from trusted World provenance;
2. OBSERVATION from direct structured observation evidence;
3. REPORT preserves attributed external source;
4. INFERENCE requires derived evidence;
5. BELIEF remains distinct;
6. Authority class is independent from AuthorityResolutionConfidence;
7. real same-slot contradiction identified;
8. same actor + unrelated predicate produces no contradiction;
9. same predicate + incompatible value with overlapping time produces contradiction;
10. different valid temporal scopes produce no false contradiction;
11. explicit correction prioritizes but does not bypass semantic verification;
12. no candidate / `NO_MATCH` distinguished from `UNAVAILABLE`;
13. `SUPERSEDED` memory is not active contradiction target;
14. multiple ambiguous candidates fail conservatively;
15. unresolved subject/owner/time fail conservatively;
16. resolver performs no writes;
17. Python-reference compatibility mapping fails closed on semantic loss;
18. AuthorityResolution → Memory Admission integration preserves contradiction identity;
19. existing Assembling regressions remain green;
20. Memory Foundation/Admission reference tests remain unchanged when that repository becomes active.

## 20. Freeze invariants

```text
MIP OWNS AUTHORITY SEMANTICS.
PYTHON REFERENCE IS AN ORACLE, NOT THE UNIVERSAL CONTRACT.
CURRENT AuthorityDecision IS COMPATIBILITY-ONLY.
CURRENT MipAuthorityResolutionV1 IS A TRANSITION PROJECTION.

AUTHORITY != CONFIDENCE.
AUTHORITY != BELIEF CONFIDENCE.
AUTHORITY != MEMORY ADMISSION.
CONTRADICTION != SUPERSESSION.

AUTHORITY MAY READ EVIDENCE.
AUTHORITY MUST NOT WRITE MEMORY.

SAME ACTOR != CONTRADICTION.
TEXT DIFFERENCE != CONTRADICTION.
TEMPORAL CHANGE != CONTRADICTION BY DEFAULT.

CONCRETE CONTRADICTION TARGET MUST BE VALID AND SEMANTICALLY UNIQUE.
AMBIGUITY -> NO CONCRETE CONTRADICTION ID.

TypedClaim -> Authority Resolver -> AuthorityResolution -> Memory Admission -> MemoryRepository
MUST NOT BE BYPASSED.
```

## 21. STOP boundary

This contract freeze authorizes no implementation beyond documentation.

Do not automatically start:

```text
Authority Resolver Kotlin
Authority runtime DTO migration
MipBridge migration
MatrixContextSnapshot runtime
retrieval runtime
Memory Foundation Kotlin/Room
Memory Admission wiring
orchestrator rewiring
Relationship
Reflection
BDI/Decision
```

Next action requires owner authorization after review of this frozen contract.
