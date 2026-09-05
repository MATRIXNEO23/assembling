# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T14:30+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `understanding-v3-runtime-v1`  
Continuity schema: `matrix.assembling.continuity.v71`

## Owner-approved execution order

```text
1. IMPLEMENT UNDERSTANDING V3
   -> produce real canonical TypedClaim evidence
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
   -> consume real TypedClaims
4. IMPLEMENT MEMORY KOTLIN/ROOM
   -> consume AuthorityResolution
```

This order is binding until the owner changes it explicitly.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 Kotlin = FROZEN
Kotlin DeterministicAuthorityResolver = IMPLEMENTED / TESTED / GREEN
MatrixTurnFrame canonical Context/Retrieval/Authority slots = MERGED/GREEN
CP-U1 Understanding V3 lossless audit = COMPLETE / PASS
CP-U1 closure CI = 33964635851 SUCCESS
CP-U2 Understanding V3 profile = COMPLETE / MERGED / GREEN
CP-U2 profile = MIP-1.0/UNDERSTANDING-V3-1.0
CP-U2 merge = accb1e7ac47738bc5d658ca44808c220e16dad32
CP-U2 post-merge CI = 33965518114 SUCCESS
```

## CP-U3 — UNDERSTANDING V3 RUNTIME / REAL TYPED CLAIMS

Branch:

`understanding-v3-runtime-v1`

Base:

`c31c963bcf0cace44edd17e22ab732a974ef8f7a`

PR:

`#19 — Implement canonical Understanding V3 runtime and real TypedClaims`

### Functional implementation

Commit:

`87a80f5d111d7465601c70bc033bbb8ee17c5e5d`

File:

`src/main/kotlin/matrix/assembling/understanding/v3/CanonicalUnderstandingV3Adapter.kt`

Implemented:

```text
MatrixNluV3Request
MatrixNluV3Alternative<T>
MatrixNluV3Field<T>
MatrixNluV3Mention
MatrixNluV3ReferentCandidate
MatrixNluV3TemporalEvidence
MatrixNluV3TemporalRelationValue
MatrixNluV3Claim
MatrixNluV3Output
MatrixNluV3RuntimeBridge
CanonicalUnderstandingV3Config
CanonicalUnderstandingV3Adapter
```

Canonical path:

```text
Matrix-NLU V3 runtime output
-> envelope/fingerprint/input/speaker/observer validation
-> lossless MipUnderstandingV3Observation
-> MipUnderstandingV3Claim[] REAL CANONICAL TYPED CLAIMS
-> DiagnosticTrace
```

No free-text reparsing is performed.

### MatrixTurnFrame canonical Understanding slot

Commit:

`bf45eddf6e0d1cf3d1e54b463834b250168083e8`

Added as the final constructor field to preserve all historical positional parameter order:

```text
canonicalUnderstandingV3: MipField<MipUnderstandingV3Observation>
```

Helpers:

```text
requireCanonicalUnderstandingV3()
requireCanonicalTypedClaimsV3()
```

The complete `MipUnderstandingV3Observation` remains in the turn frame so candidate tables, plural spans, ambiguity alternatives, temporal anchors, claim statuses and provenance are not lost.

Legacy `typedClaims`, `NluOutput` and `SemanticFrame` are NOT auto-populated from the V3 path.

### Tests

Commit:

`b8000e3e75453165580267ed8f4319392411a8ee`

File:

`src/test/kotlin/matrix/assembling/understanding/v3/CanonicalUnderstandingV3AdapterTest.kt`

Coverage:

```text
real canonical V3 TypedClaim creation
source != perspective preservation
plural negation spans
claimKind preservation
temporal anchor preservation
original claimId preservation
multi-claim + cross-claim anchor
AMBIGUOUS + ranked alternatives survive
INVALID/ABSTAINED survive
empty observation remains explicit PRESENT/HOLD
contract fingerprint mismatch fail-closed
input/speaker/observer mismatch fail-closed
runtime exception -> deterministic first divergence
forbidden truth/Authority/Memory/state ownership fields absent
legacy MatrixTurnFrame remains source-compatible
legacy typedClaims remain untouched
```

### CI failure and targeted fix

Initial PR head:

`b8000e3e75453165580267ed8f4319392411a8ee`

Initial CI:

`33966040483 = FAILURE`

Cause was task-introduced and local:

```text
CanonicalUnderstandingV3Config referenced SHA256 validator outside adapter companion scope
```

No architecture/test/gate change was made.

Targeted fix commit:

`b53eefd84f12847ba000e96ca7342975b0187001`

Added:

`src/main/kotlin/matrix/assembling/understanding/v3/UnderstandingV3Validation.kt`

### Green gate

Current functional/test head:

`b53eefd84f12847ba000e96ca7342975b0187001`

Full repository CI:

```text
run = 33966131534
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

## CP-U3 current verdict

```text
CANONICAL V3 RUNTIME DTO = COMPLETE
VALIDATED ADAPTER = COMPLETE
REAL CANONICAL V3 TYPED CLAIMS = COMPLETE
MATRIXTURNFRAME V3 SLOT = COMPLETE
DIAGNOSTICTRACE = COMPLETE FOR THIS BOUNDARY
FULL REGRESSION = GREEN
CP-U3 = PASS PENDING FINAL CONTINUITY-HEAD CI / MERGE / POST-MERGE CI
```

## Hard boundaries preserved

```text
Python Authority Resolver = NOT MODIFIED YET
Authority integration with Understanding = NOT STARTED
Memory Kotlin/Room = NOT STARTED
MemoryRepository/PersistentConsolidation = NOT STARTED
matrix-understanding-lab = NOT MODIFIED
Student-5 training = NOT MODIFIED
other repositories = READ-ONLY
```

## Next owner-approved step after CP-U3 closure

```text
STEP 2 = FIX PYTHON AUTHORITY RESOLVER P0 BUGS
```

Only after that:

```text
STEP 3 = INTEGRATE AUTHORITY WITH REAL UNDERSTANDING V3 CLAIMS
STEP 4 = IMPLEMENT MEMORY KOTLIN/ROOM
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = understanding-v3-runtime-v1
PR = #19
last green functional/test head = b53eefd84f12847ba000e96ca7342975b0187001
CI = 33966131534 SUCCESS
current operation = verify CI for this final continuity-only head
CP-U3 = PASS PENDING FINAL-HEAD CI / MERGE / POST-MERGE CI
NEXT AFTER GREEN MERGE = switch repository/workstream only after exact continuity save; Python Authority Resolver P0 fixes
```
