# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T09:35+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-kotlin-contracts-v1`  
Continuity schema: `matrix.assembling.continuity.v20`

## Continuity policy

This file is the canonical restart point for Assembling work and must be updated after every significant checkpoint, including:

- branch/task start;
- contract or architecture decision;
- code/test checkpoint;
- CI or benchmark result;
- strategy change;
- before risky/long operations;
- before any STOP or session end.

Do not create a parallel continuity file for the same workstream.

## Canonical work rules

- work on one repository at a time unless the owner explicitly authorizes otherwise;
- active write target is only `MATRIXNEO23/assembling`;
- other repositories may be read for evidence, never written without explicit authorization;
- historical repositories are backups/checkpoints;
- MIP is the only cross-module semantic authority;
- MipBridge is the only common interop bridge;
- every new functional module must live in a dedicated directory/package;
- existing root runtime files are not moved for cosmetic reasons;
- no gate/test may be weakened to obtain a pass.

## Current owner-authorized phase

```text
PHASE = AUTHORITY KOTLIN CONTRACT TYPES ONLY
BRANCH = authority-kotlin-contracts-v1
START HEAD = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
```

Allowed now:

- implement Kotlin types that directly realize already-frozen `AUTHORITY-1.0` semantics;
- create them only under `src/main/kotlin/matrix/assembling/authority/`;
- add contract-only tests under `src/test/kotlin/matrix/assembling/authority/`;
- update docs and continuity;
- run full regression/CI.

Not authorized in this phase:

```text
real AuthorityResolver implementation
semantic contradiction algorithm
Memory reads/writes
MatrixContextSnapshot runtime implementation
Retrieval runtime implementation
MipBridge final Authority migration
current root AuthorityDecision migration
orchestrator rewiring
Memory Kotlin/Room
PersistentConsolidation implementation
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
```

Other repositories modified = `false`.

## Main baseline before current phase

### Assembling cleanup

```text
cleanup start HEAD = ef433a3aed519b31efe9289a8df78ed974170510
PR #8 tested HEAD = 2e51e1b51df101d0fdb25f9cb567201839fc07d6
PR #8 merge = ff38d09f73a1eec8b2a72a24571b92f1954c729c
PR #8 CI = 33951029064 SUCCESS
post-merge CI = 33951548865 SUCCESS
PR #9 merge = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
final cleanup CI = 33951808519 SUCCESS
```

Cleanup completed:

```text
inventory = COMPLETE
contract mapping = COMPLETE
incompatibility matrix = COMPLETE
MIP Bridge audit = COMPLETE
structural cleanup = COMPLETE
legacy-path classification = COMPLETE
round-trip strict tests = PASS
documentation = COMPLETE
files moved = 0
files renamed = 0
files deleted = 0
```

Legacy compatibility quarantine:

```text
contracts/MatrixAssemblyContracts.kt = KEEP_COMPATIBILITY
pipeline/MatrixAssemblyPipeline.kt = KEEP_COMPATIBILITY / DEPRECATED
prompt/SemanticFrameToPrompt.kt = KEEP_COMPATIBILITY / DEPRECATED
coherence/CoherenceGuard.kt = KEEP_COMPATIBILITY / DEPRECATED
```

## Canonical MIP state

```text
docs/MATRIX_INTERMODULE_PROTOCOL.md
version = MIP-1.0
owner = assembling
```

Hard semantic invariants:

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL != RELATE != CONSENT != WANT != DECIDE != EXPRESS
TypedClaim != Belief
Belief != Memory
Memory != State
State != Context
Relationship != Affective
SexualInterest != CurrentDesire
CurrentDesire != Consent
Contradiction != Supersession
InterpretationConfidence != SourceReliability
SourceReliability != Authority
Authority != BeliefConfidence
BeliefConfidence != RetrievalRelevance
```

Shared role meanings remain distinct:

```text
speaker
observer
source
subject
target
owner
perspective
```

MIP general field states:

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

Entity resolution states:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

## MIP Bridge state

Canonical adapter:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

Canonical audit:

`docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md`

Authority contradiction seam already fail-closed:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> native null/None
UNKNOWN/UNRESOLVED/AMBIGUOUS/CONFLICTED/UNAVAILABLE/NO_MATCH/ERROR
-> fail when native representation cannot preserve meaning
```

Python arbitrary-size integer to Kotlin `Long` uses explicit range validation; no truncation.

Current `MipAuthorityResolutionV1` is a transition/compatibility projection, not final AUTHORITY-1.0.

## AUTHORITY-1.0 contract freeze — COMPLETE

Canonical file:

`docs/MIP_AUTHORITY_CONTRACT.md`

Status:

```text
CANONICAL MIP AUTHORITY PROFILE / CONTRACT FROZEN
MIP-1.0 / AUTHORITY-1.0
```

Freeze history:

```text
freeze start HEAD = afc5cd7e535dc08d09455339a056c71ba5dc6ea2
contract commit = a3c7bf9bb4cd01f8032fd32c4e3f4ce3dc293f9b
index alignment = dc3407cb2324c4bf96542ff8ff5ad7c441b13489
freeze continuity = 9210773030afc96f631d0e0c0a3a669bf6a6c2f5
PR #10 merge = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
post-merge CI = 33952808037 SUCCESS
```

No runtime/code file changed in the freeze PR.

### Frozen cognitive/persistence boundary

```text
TypedClaim
-> Authority Resolver
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Durable runtime rule remains:

```text
pre-response:
TypedClaim + read-only context/retrieval
-> AuthorityResolution
-> MemoryCandidate/preflight only

accepted output/action
-> VALIDATE
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
```

Authority never writes Memory and never owns `SAVE/SUPERSEDE/REJECT/IGNORE`.

### Frozen EpistemicClass

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Rules:

- WORLD_TRUTH requires explicit trusted WORLD/Game provenance;
- ordinary text cannot self-grant WORLD_TRUTH;
- OBSERVATION requires structured direct-observation provenance;
- REPORT preserves attributed external source;
- INFERENCE requires explicit derived-from evidence;
- BELIEF represents belief/opinion/supposition evidence.

### Frozen confidence separation

```text
EpistemicClass
!= InterpretationConfidence
!= AuthorityResolutionConfidence
!= SourceReliability
!= BeliefConfidence
!= RetrievalRelevance
```

### Frozen AuthorityResolveRequest conceptual contract

```text
AuthorityResolveRequest
- requestId
- claim: TypedClaim
- contextSnapshot: MatrixContextSnapshot
- retrievalResult?: RetrievalResult
- provenance
```

Important current dependency:

```text
MatrixContextSnapshot runtime = NOT_IMPLEMENTED
RetrievalResult runtime = NOT_IMPLEMENTED
ProvenanceRef runtime = NOT_IMPLEMENTED
```

Therefore this current Kotlin-contract phase must NOT invent private Authority-specific replacements for Context, Retrieval or Provenance.

### Frozen AuthorityResolution conceptual contract

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

AuthorityResolutionStatus:

```text
COMPLETE
PARTIAL
HOLD
UNAVAILABLE
ERROR
```

### Contradiction identity semantics

```text
PRESENT(memoryRef) = one concrete real contradiction target
NOT_APPLICABLE = assessment complete; no contradiction target
UNKNOWN = target may exist but genuinely unknown
UNRESOLVED = assessment incomplete
AMBIGUOUS = multiple plausible targets; no unique target
CONFLICTED = contradiction-target evidence internally inconsistent
UNAVAILABLE = required evidence provider unavailable
NO_MATCH = retrieval succeeded; no matching candidate
ERROR = assessment failed
```

Concrete contradiction target requires:

1. candidate exists;
2. candidate is VALID;
3. same resolved semantic slot;
4. same normalized predicate/property;
5. compatible/overlapping temporal scope or same relevant event;
6. mutually incompatible values/target/polarity;
7. one unique target can be safely identified.

Forbidden standalone contradiction heuristics:

```text
same actor
same entity mention
text differs
low lexical similarity
shared words
higher/lower confidence
higher/lower authority
```

Temporal change is not contradiction by default.

Correction is strong candidate evidence but does not automatically mean contradiction or supersession.

### Python reference policy

Recovered historical evidence contains at least:

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

No authoritative `authority_models.py` with frozen filename/version/checksum has been identified.

Therefore:

```text
Python = oracle / compatibility evidence
MIP AUTHORITY-1.0 = contract owner
```

Historical `contradicts_memory_id=None` maps to MIP `NOT_APPLICABLE` only when historical status proves contradiction assessment completed. Otherwise uncertainty/error state must remain explicit.

### Current Kotlin compatibility predecessor

Root `AuthorityDecision` currently contains:

```text
accepted
ownerResolved
sourceType
conflictStatus
reason
```

It is NOT the canonical AuthorityResolution and must not be enlarged ad hoc during this phase.

`BasicAuthorityResolver` remains:

```text
conservative placeholder/gate
!= real semantic Authority Resolver
```

It does not query Memory, retrieve candidates, detect semantic contradictions or populate contradiction identity.

## Current phase checkpoint 0 — STARTED

```text
phase = AUTHORITY_KOTLIN_CONTRACT_TYPES_ONLY
branch = authority-kotlin-contracts-v1
base/main HEAD = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
main CI = 33952808037 SUCCESS
resolver implementation = NOT_STARTED
orchestrator rewiring = false
MipBridge migration = NOT_STARTED
other repositories modified = false
```

Design decision for this phase:

- create Kotlin Authority-owned enums/value types that can be represented without inventing shared Context/Retrieval/Provenance implementations;
- do not create a fake `AuthorityResolveRequest` until canonical shared `MatrixContextSnapshot` and `RetrievalResult` runtime types exist;
- do not create an Authority-private Context, Retrieval or Provenance model;
- contract implementation location: `src/main/kotlin/matrix/assembling/authority/`;
- tests location: `src/test/kotlin/matrix/assembling/authority/`.

Planned first code checkpoint:

```text
EpistemicClass
AuthorityResolutionStatus
opaque MemoryRef/value semantics
normalized confidence value wrapper if it can remain MIP-compatible
AUTHORITY reason-code constants/validation
contract invariants only
```

No semantic resolver logic in this checkpoint.

## Required gates before phase completion

- new Authority contract types match `docs/MIP_AUTHORITY_CONTRACT.md`;
- no new protocol/context/retrieval family;
- no resolver business logic;
- no current DTO migration;
- no MipBridge migration;
- dedicated `authority/` package only;
- contract unit tests PASS;
- all existing Assembling tests PASS;
- CI GREEN;
- other repositories modified = false;
- continuity updated after each checkpoint.

## Exact restart point

If context/session is lost, resume from:

```text
repo = MATRIXNEO23/assembling
branch = authority-kotlin-contracts-v1
base = bf8ef4aadcc6a73e85e920968a926bf4b838a0fa
contract = docs/MIP_AUTHORITY_CONTRACT.md (AUTHORITY-1.0 frozen)
current task = implement contract-only Kotlin Authority value/enums under authority/
resolver = NOT_STARTED
context/retrieval runtime = NOT_IMPLEMENTED
MipBridge final Authority migration = NOT_STARTED
other repos = READ-ONLY
```

Do not redo cleanup or contract freeze. Continue from current phase checkpoint 0.