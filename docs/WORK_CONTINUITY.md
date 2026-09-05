# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:00+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v57`

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

## CANONICAL AUTHORITY RUNTIME ADAPTER — COMPLETE / INTEGRATED / TESTED

Branch:

`authority-runtime-adapter-v1`

PR:

`#16 — Add standalone canonical Authority runtime adapter`

Functional commits:

```text
2e0b46636d95526491fd68e76e220523826388bf
= standalone canonical/legacy runtime adapter

7dde24a59ff2f1bb218142bf89e15d3befec477e
= standalone adapter regression gates
```

Final tested PR head:

`898b31f83168e0277042b67b0660d381ff7a09b0`

PR final-head CI:

```text
33961583602 = SUCCESS
```

Merge SHA:

`7a772412570237260130bd4062555c6449feaf46`

Post-merge main CI:

```text
33961639272 = SUCCESS
job = kotlin-tests
Run tests = SUCCESS
```

Integrated files:

```text
src/main/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapter.kt
src/test/kotlin/matrix/assembling/authority/runtime/CanonicalAuthorityRuntimeAdapterTest.kt
```

Integrated runtime adapter types:

```text
CanonicalAuthorityRuntimeInput
LegacyAuthorityGap
LegacyAuthorityDecisionProjectionStatus
LegacyAuthorityRuntimeOutcome
CanonicalAuthorityRuntimeAdapter
```

Properties now proven:

- canonical runtime adapter output equals direct `DeterministicAuthorityResolver` output exactly;
- canonical REPORT with resolved source can resolve COMPLETE/REPORT;
- legacy USER_ASSERTION preserves source as UNKNOWN and reaches canonical HOLD instead of guessing;
- trusted WORLD legacy evidence resolves only with independently trusted WORLD provenance;
- context turn/session/claim/provenance mismatches block before resolver;
- multi-claim turns require explicit claim selection; no implicit first-claim behavior;
- MatrixTurnFrame remains unmodified and `authorityDecision` remains untouched;
- adapter does not implement legacy `AuthorityResolverPort`;
- adapter exposes no persistence mutation API;
- no root `AuthorityDecision` is emitted because canonical AUTHORITY-1.0 cannot be represented there losslessly.

## Current runtime gap

The canonical adapter exists and is tested, but the current runtime frame still has no lossless slots for canonical shared evidence/output:

```text
MatrixContextSnapshot
RetrievalResult / explicit MipField state
AuthorityResolution
```

Current legacy frame still exposes:

```text
typedClaims: List<TypedClaim>
authorityDecision: AuthorityDecision?
```

This means the orchestrator cannot yet be safely rewired to canonical Authority without either dropping fields or inventing parallel hidden state.

## NEXT BOUNDED CHECKPOINT — RUNTIME FRAME MIP EVIDENCE SLOTS ONLY

Next safe task:

```text
RUNTIME FRAME MIP EVIDENCE SLOTS ONLY
```

Goal:

- add additive canonical runtime slots to `MatrixTurnFrame` for shared MIP context/retrieval/Authority output;
- use explicit `MipField` status semantics rather than ambiguous nullable values;
- preserve all legacy fields for compatibility;
- do not automatically synchronize or collapse canonical `AuthorityResolution` into legacy `AuthorityDecision`;
- add tests proving old callers remain source-compatible and canonical statuses survive copies/turn transitions;
- no orchestrator stage replacement yet;
- no BasicAuthorityResolver replacement yet;
- no Memory writes/admission.

Proposed additive fields to validate in that task:

```text
contextSnapshot: MipField<MatrixContextSnapshot>
retrievalResult: MipField<RetrievalResult>
canonicalAuthorityResolution: MipField<AuthorityResolution>
```

Default state must be explicit (normally `UNAVAILABLE`/`NOT_APPLICABLE` as semantically appropriate), never fake empty context or fake NO_MATCH.

Only after this checkpoint is green should the orchestrator canonical Authority rewire be attempted.

## Explicitly NOT implemented / not authorized yet

```text
orchestrator canonical Authority rewire
BasicAuthorityResolver removal/replacement
legacy AuthorityResolverPort removal
root AuthorityDecision redesign/removal
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
MemoryRepository implementation/dependency
PersistentConsolidation
end-to-end durable Memory write
other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
runtime adapter merge = 7a772412570237260130bd4062555c6449feaf46
runtime adapter post-merge CI = 33961639272 SUCCESS
AUTHORITY-1.0 = FROZEN
Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTO binding = COMPLETE / INTEGRATED / TESTED
real DeterministicAuthorityResolver = COMPLETE / INTEGRATED / P0 TESTED
canonical compatibility projections = COMPLETE / INTEGRATED / TESTED
canonical runtime adapter = COMPLETE / INTEGRATED / TESTED
orchestrator uses canonical resolver = false
MatrixTurnFrame canonical MIP slots = NOT STARTED
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = RUNTIME FRAME MIP EVIDENCE SLOTS ONLY
```
