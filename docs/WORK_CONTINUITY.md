# Work Continuity — Matrix Assembling

Last updated: 2026-09-06T10:08+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v74`

## Owner-approved execution order

```text
1. IMPLEMENT UNDERSTANDING V3
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
4. IMPLEMENT MEMORY KOTLIN/ROOM
```

## 1 — Understanding V3 — COMPLETE / MERGED / GREEN

Do not redo CP-U1/U2/U3.

```text
CP-U3 PR = #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Canonical path:

```text
Matrix-NLU V3 runtime output
-> CanonicalUnderstandingV3Adapter
-> MipUnderstandingV3Observation
-> MipUnderstandingV3Claim[] real canonical V3 TypedClaims
-> MatrixTurnFrame.canonicalUnderstandingV3
```

Legacy `TypedClaim`, `NluOutput` and `SemanticFrame` are not auto-populated from V3.

## 2 — Python Authority Resolver P0 — COMPLETE / MERGED / GREEN

The previously missing source was found on the existing repository branch:

```text
repo = MATRIXNEO23/memoria
branch = python-authority-p0-v1
final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR = memoria #1
pre-merge GitHub CI = run 34020629980 / SUCCESS
merge = b8cc7e2133868049550d3c63d78f69da8f830f20
continuity closure = cde91db20d97e0792b61db15144faf1430fd27bc
```

Closed P0s:

```text
P0-PA-01 hardcoded owner "test_agent"
  -> structured claim.owner is passed to read-only MemoryEvidencePort

P0-PA-02 regex/free-text property extraction
  -> removed from Authority path; structured predicate/object/polarity/temporal semantics only

P0-PA-03 actor-overlap + content-difference false conflict
  -> contradiction now requires same semantic slot/scope, compatible temporal identity,
     VALID candidate, and incompatible structured value/polarity
```

Memory Foundation v3 boundary preserved:

```text
AuthorityResolution contradiction identity
-> explicit contradicts_memory_id / MemoryRef
-> Memory Admission
-> MemoryRepository
```

Authority does not write Memory. Authority and confidence remain separate concepts.

## 3 — Canonical Understanding V3 -> Authority — COMPLETE / MERGED / GREEN

A first experimental branch/PR (#20) was deliberately closed without merge when the Python P0 gate was discovered still open. It is historical/quarantined and must not be used as the canonical merge.

Canonical implementation:

```text
branch = cp-a2-v3-authority-wiring
PR = #21 — CP-A2: wire canonical Understanding V3 into Authority
head = b7ea750665a563cbca673b4050a72adc21275a8e
Matrix Assembling CI = run 34020878347 / run #156 / SUCCESS
merge = d3994e59008aac648576a252eac0d7c4e1028589
```

Integrated path:

```text
MatrixTurnFrame.canonicalUnderstandingV3
-> CanonicalUnderstandingV3AuthorityPort
-> authority-specific structural MipClaimV1 projection
-> DeterministicAuthorityResolver
-> AuthorityResolution[]
-> MatrixTurnFrame.canonicalAuthorityResolutions
```

Preserved:
- original V3 claim IDs and claim provenance;
- independent source/subject/target/owner/perspective identities;
- claimKind, dialogueAct, polarity and temporal anchor;
- object evidence from explicit V3 object span, not regex/free-text property parsing;
- fail-closed ambiguity/unresolved states;
- no legacy root TypedClaim dependency;
- no Memory writes.

Important current limitation deliberately preserved:

```text
Mip RetrievalResult has no explicit claimId binding.
```

Therefore the integration only binds retrieval when it is unambiguous (one claim / one result). Multi-claim retrieval remains UNRESOLVED instead of guessing by list order or parsing query IDs.

`MatrixTurnFrame` canonical Authority coverage validation now uses the real V3 claim IDs when `canonicalUnderstandingV3` is PRESENT; legacy `typedClaims` remain the fallback only for legacy paths.

## 4 — NEXT: Memory Kotlin/Room consuming AuthorityResolution

Current hard Memory architecture remains:

```text
PRE-RESPONSE READ / ENRICH
-> lightweight index probe / retrieval
-> no durable write

PRE-RESPONSE EVALUATE / PROPOSE
-> MemoryPreflightPort
-> ephemeral MemoryCandidate only

POST-VALIDATION COMMIT
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
-> atomic SAVE / SUPERSEDE / metadata operation
```

Durable persistence must consume canonical `AuthorityResolution`, including explicit contradiction identity. Memory Admission must not rediscover semantic contradictions from raw text, actor overlap or unrelated predicates.

Before implementing Room, verify the Android-capable target/module. Current `assembling` build is JVM-only; do not add fake Room persistence to a non-Android module merely to satisfy the milestone. Preserve one-repository-at-a-time work and explicit provenance when moving the Memory implementation into its Android-capable integration target.

## Current status

```text
Understanding V3 = COMPLETE
Python Authority P0 = COMPLETE
Understanding -> Authority = COMPLETE
Memory Kotlin/Room = ACTIVE NEXT
Memory durable persistence = NOT YET IMPLEMENTED
```

## Exact restart point

```text
assembling main functional HEAD before this continuity commit = d3994e59008aac648576a252eac0d7c4e1028589
step 1 = CLOSED
step 2 = CLOSED
step 3 = CLOSED
NEXT = verify Android/Room target, then implement Memory Kotlin/Room consuming canonical AuthorityResolution
```
