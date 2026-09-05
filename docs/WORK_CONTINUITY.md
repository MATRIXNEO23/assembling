# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T12:50+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-adapter-v1`  
Continuity schema: `matrix.assembling.continuity.v55`

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
resolver post-merge CI = 33957882144 SUCCESS
Authority compatibility PR #15 merge = 736aee2ebcd977c89faab9e519ace0f2420f668d
compatibility post-merge CI = 33961173851 SUCCESS
compatibility continuity commit = 70e761de23e9c162c2415054e8662881590b2753
compatibility continuity CI = 33961261672 SUCCESS
```

Canonical state already integrated:

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
other repos modified = false
```

### Checkpoint 1 — standalone runtime adapter

Commit:

`2e0b46636d95526491fd68e76e220523826388bf`

File:

`src/main/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapter.kt`

Added:

```text
CanonicalAuthorityRuntimeInput
LegacyAuthorityGap
LegacyAuthorityDecisionProjectionStatus
LegacyAuthorityRuntimeOutcome
CanonicalAuthorityRuntimeAdapter
```

Design:

- package is dedicated `matrix.assembling.authority.runtime`;
- adapter does NOT implement legacy `AuthorityResolverPort`;
- canonical path builds `AuthorityResolveRequest` and returns full `AuthorityResolution` unchanged;
- legacy path requires one explicitly selected `TypedClaim`; no implicit first-claim selection;
- legacy path uses existing `MipBridge.fromAssemblingTypedClaim` and surfaces missing semantics as typed compatibility gaps;
- root `AuthorityDecision` is never produced because it cannot preserve canonical AUTHORITY-1.0 losslessly;
- MatrixTurnFrame is never mutated;
- structural turn/session/claim/provenance mismatches return deterministic `AUTHORITY.RUNTIME.*` Blocked outcome;
- no Memory/admission/persistence method exists.

Expected legacy gaps surfaced explicitly:

```text
SOURCE_IDENTITY_NOT_REPRESENTED
DIALOGUE_ACT_NOT_REPRESENTED
CLAIM_KIND_NOT_REPRESENTED
OWNER_UNRESOLVED
PERSPECTIVE_UNRESOLVED
```

A legacy USER_ASSERTION can therefore legitimately reach canonical `AuthorityResolutionStatus.HOLD` because source identity is unknown; the adapter must not guess the source.

Trusted WORLD evidence may still resolve when independent trusted WORLD provenance supplies the required authority evidence; the missing root source field does not self-grant WORLD_TRUTH.

### Checkpoint 2 — standalone adapter gates

Commit:

`7dde24a59ff2f1bb218142bf89e15d3befec477e`

File:

`src/test/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapterTest.kt`

Coverage:

```text
canonical adapter output == direct resolver AuthorityResolution exactly
canonical REPORT with resolved source -> COMPLETE / REPORT
legacy USER_ASSERTION -> source remains UNKNOWN, canonical HOLD, no guessed source
legacy compatibility gaps expose source/dialogueAct/claimKind loss
trusted WORLD legacy input resolves only with trusted WORLD provenance
context turn mismatch -> BLOCKED before resolver
claim not explicitly present in frame -> BLOCKED
multi-claim frame requires explicit claim selection and preserves selected claim identity
MatrixTurnFrame.authorityDecision remains null/unmodified
adapter is not assignable to legacy AuthorityResolverPort
adapter exposes no save/admit/supersede/delete/update/consolidate/persist API
```

### Current validation state

```text
runtime adapter code = ADDED
runtime adapter tests = ADDED
full repository CI = NOT YET RUN
orchestrator = UNCHANGED
MatrixTurnFrame = UNCHANGED
BasicAuthorityResolver = UNCHANGED
root AuthorityDecision = UNCHANGED
Memory = UNCHANGED
```

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
base = 70e761de23e9c162c2415054e8662881590b2753
last functional commit = 2e0b46636d95526491fd68e76e220523826388bf
last test commit = 7dde24a59ff2f1bb218142bf89e15d3befec477e
canonical runtime adapter = CODE + TESTS ADDED / CI PENDING
orchestrator uses canonical resolver = false
legacy BasicAuthorityResolver = STILL PRESENT / COMPATIBILITY
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = VERIFY DIFF; OPEN PR; RUN FULL CI; FIX ONLY TASK-INTRODUCED FAILURES
```
