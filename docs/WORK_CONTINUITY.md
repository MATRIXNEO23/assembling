# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T12:56+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-adapter-v1`  
Continuity schema: `matrix.assembling.continuity.v56`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update after every meaningful checkpoint, CI result, architecture decision, before risky operations and before STOP/session end.

## Hard work rules

```text
writable repository = MATRIXNEO23/assembling only unless owner explicitly switches
MIP = single cross-module semantic authority
new functional module = dedicated package/directory
parallel competing protocol/adapter family = forbidden
gate/test weakening = forbidden
other repositories = read-only
```

## Completed Authority baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 merge = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
real Authority resolver PR #14 merge = b7237542259d86c26632b2185d7e90691e82141f
Authority compatibility PR #15 merge = 736aee2ebcd977c89faab9e519ace0f2420f668d
compatibility post-merge CI = 33961173851 SUCCESS
compatibility continuity commit = 70e761de23e9c162c2415054e8662881590b2753
compatibility continuity CI = 33961261672 SUCCESS
```

Canonical Authority pieces already integrated:

```text
AuthorityResolveRequest
AuthorityResolution
AuthorityCandidateEvidencePort (read-only)
DeterministicAuthorityResolver
canonical contradiction identity/status
Kotlin Memory contradiction projection
historical Python contradiction projection
```

Legacy `MipAuthorityResolutionV1` and root `AuthorityDecision` remain quarantined because they cannot losslessly represent canonical AUTHORITY-1.0.

## ACTIVE TASK — CANONICAL AUTHORITY RUNTIME ADAPTER ONLY

```text
branch = authority-runtime-adapter-v1
base = 70e761de23e9c162c2415054e8662881590b2753
PR = #16
other repos modified = false
```

### Checkpoint 1 — standalone runtime adapter

```text
commit = 2e0b46636d95526491fd68e76e220523826388bf
file = src/main/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapter.kt
```

Added canonical and legacy-facing standalone adapter types:

```text
CanonicalAuthorityRuntimeInput
LegacyAuthorityGap
LegacyAuthorityDecisionProjectionStatus
LegacyAuthorityRuntimeOutcome
CanonicalAuthorityRuntimeAdapter
```

Properties:

- dedicated package `matrix.assembling.authority.runtime`;
- adapter does NOT implement legacy `AuthorityResolverPort`;
- canonical path constructs `AuthorityResolveRequest` and returns full canonical `AuthorityResolution`;
- legacy path requires an explicitly selected `TypedClaim` and never chooses first claim implicitly;
- existing `MipBridge.fromAssemblingTypedClaim` is reused; no second bridge/protocol is created;
- missing legacy semantics are surfaced as typed compatibility gaps, never guessed;
- root `AuthorityDecision` is never produced because that conversion is inherently lossy;
- turn/session/claim/provenance structural mismatches block with deterministic `AUTHORITY.RUNTIME.*` reason codes;
- MatrixTurnFrame is immutable/unmodified;
- no persistence or Memory API exists.

Legacy gaps surfaced:

```text
SOURCE_IDENTITY_NOT_REPRESENTED
DIALOGUE_ACT_NOT_REPRESENTED
CLAIM_KIND_NOT_REPRESENTED
OWNER_UNRESOLVED
PERSPECTIVE_UNRESOLVED
```

### Checkpoint 2 — standalone runtime gates

```text
commit = 7dde24a59ff2f1bb218142bf89e15d3befec477e
file = src/test/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapterTest.kt
```

Coverage:

```text
canonical adapter output == direct resolver AuthorityResolution exactly
canonical REPORT with resolved source -> COMPLETE / REPORT
legacy USER_ASSERTION -> source UNKNOWN + canonical HOLD, never guessed
trusted WORLD legacy evidence resolves only with independent trusted WORLD provenance
context turn mismatch -> BLOCKED
claim not explicitly in frame -> BLOCKED
multi-claim invocation preserves explicitly selected claim identity
MatrixTurnFrame.authorityDecision remains null
adapter is not AuthorityResolverPort
adapter has no persistence mutation API
```

### Diff / PR / CI evidence

Verified diff from base contains exactly:

```text
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapter.kt
src/test/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapterTest.kt
```

PR #16:

`Add standalone canonical Authority runtime adapter`

Green tested PR head before this documentation update:

`0254367261adbc92e3215e4aacd7667000770763`

Full regression gate:

```text
run = 33961506698
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

No task-introduced CI fix was required.

This documentation update creates a new final PR head. Merge is forbidden until CI for that exact final head is also green.

## Hard boundaries still enforced

```text
no MatrixAssemblingOrchestrator modification
no IntegrationPorts.AuthorityResolverPort replacement
no BasicAuthorityResolver replacement/removal
no MatrixTurnFrame redesign
no root AuthorityDecision redesign/write
no MemoryRepository dependency/write
no Memory Admission implementation
no PersistentConsolidation
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-runtime-adapter-v1
PR = #16
base = 70e761de23e9c162c2415054e8662881590b2753
last green tested head before doc update = 0254367261adbc92e3215e4aacd7667000770763
CI = 33961506698 SUCCESS
canonical runtime adapter = IMPLEMENTED / TESTED GREEN / FINAL DOC HEAD CI PENDING
orchestrator uses canonical resolver = false
legacy BasicAuthorityResolver = STILL PRESENT / COMPATIBILITY
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = VERIFY FINAL DOC-ONLY HEAD CI; MERGE PR #16 ONLY IF GREEN; THEN VERIFY MAIN CI + FINALIZE CONTINUITY
```
