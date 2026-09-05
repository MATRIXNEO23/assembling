# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:36+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v49`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update it after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
Authority runtime DTO post-merge CI = 33956522738 SUCCESS
pre-resolver continuity = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
pre-resolver continuity CI = 33957143955 SUCCESS
```

Hard rules remain: one writable repo (`assembling`); MIP is the single semantic protocol; no gate weakening; no cross-repo writes unless owner explicitly switches.

## REAL AUTHORITY RESOLVER — COMPLETE / INTEGRATED / TESTED

Branch:

`authority-resolver-v1`

PR:

`#14 — Implement deterministic AUTHORITY-1.0 resolver`

Final PR head:

`9134eb7e06da3ea24c37df9298915bf0fd7cf594`

Final PR-head CI:

```text
run = 33957816535
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

Merge SHA:

`b7237542259d86c26632b2185d7e90691e82141f`

Post-merge main CI:

```text
run = 33957882144
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

Integrated files:

```text
src/main/kotlin/matrix/assembling/authority/AuthorityCandidateEvidence.kt
src/main/kotlin/matrix/assembling/authority/AuthorityResolver.kt
src/test/kotlin/matrix/assembling/authority/AuthorityResolverTest.kt
```

`docs/WORK_CONTINUITY.md` was updated throughout the phase.

### Integrated read-only evidence boundary

```text
AuthorityCandidateEvidence
AuthorityCandidateEvidencePort.read(memoryRef, contextSnapshot)
```

The port exposes no save/update/delete/supersede/admission operation. The projection is explicitly not a MemoryRecord or persistence/admission DTO.

### Integrated resolver

```text
AuthorityResolver
DeterministicAuthorityResolver
```

Authority classification:

```text
trusted WORLD provenance + explicit WORLD_TRUTH -> WORLD_TRUTH
trusted PERCEPTION provenance -> OBSERVATION
explicit derived INFERENCE provenance -> INFERENCE
structured report/self-report -> REPORT
structured belief/hypothesis -> BELIEF
otherwise -> HOLD / unresolved
```

Hard safeguards:

- ordinary text or compatibility world-truth markers cannot self-grant WORLD_TRUTH;
- OBSERVATION requires trusted perception provenance;
- INFERENCE requires explicit derivation provenance;
- REPORT requires resolved source identity;
- BELIEF requires resolved perspective identity;
- non-assertive QUESTION/REQUEST/COMMAND stay HOLD;
- SourceReliability remains unavailable unless backed by a real provider;
- no free-text reparsing;
- no retrieval-score-as-truth heuristic.

### Integrated contradiction semantics

- only VALID candidate evidence can become active contradiction target;
- same resolved subject + normalized predicate + applicable owner/target scope required;
- REPORT source and BELIEF perspective scopes remain distinct;
- CURRENT/ATEMPORAL can compare directly;
- broad historical/reference relations require stable temporal identity or remain unresolved;
- temporal change is not contradiction by default;
- opposite polarity on same semantic value may contradict;
- different values contradict only for explicitly registered single-value predicates;
- unrelated predicates do not contradict merely because actors overlap;
- multiple concrete targets -> AMBIGUOUS/HOLD;
- unresolved candidate evidence prevents selecting a concrete target;
- correction prioritizes diagnostics but never bypasses semantic verification;
- SUPERSEDED/non-VALID candidate cannot become active target.

Initial normalized single-value predicates:

```text
matrix.location.live_at
matrix.identity.age
```

### P0 regression coverage

Tests include:

```text
trusted/fake WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
same actor + unrelated predicate
same-slot single-value contradiction
opposite-polarity contradiction
temporal change / historical ambiguity
correction verification
SUPERSEDED exclusion
multiple-target ambiguity
unresolved evidence uniqueness protection
NO_MATCH != UNAVAILABLE
unresolved REPORT source short-circuit
read-only evidence-port API surface
```

No existing gate/test was weakened.

## Still intentionally NOT wired

The canonical resolver is integrated in the repository but the old runtime compatibility path remains untouched:

```text
IntegrationPorts.AuthorityResolverPort -> MatrixTurnFrame
BasicAdapters.BasicAuthorityResolver -> legacy AuthorityDecision
MatrixAssemblingOrchestrator -> legacy AuthorityResolverPort
```

Therefore:

```text
canonical DeterministicAuthorityResolver = IMPLEMENTED / TESTED / NOT YET ORCHESTRATOR-WIRED
legacy BasicAuthorityResolver = STILL PRESENT / COMPATIBILITY PATH
root AuthorityDecision = STILL LEGACY
```

## Explicitly NOT implemented / not authorized in completed resolver task

```text
MemoryRepository dependency
Memory writes
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
PersistentConsolidation
BasicAuthorityResolver replacement
root AuthorityDecision migration
final MipBridge migration
orchestrator rewiring
retrieval engine
other repo writes
```

## Next bounded task

Do NOT jump directly to Memory or orchestrator rewiring.

Next controlled checkpoint:

```text
AUTHORITY COMPATIBILITY / MIP BRIDGE COMPLETION ONLY
```

Goals:

- audit canonical AuthorityResolution against existing `MipAuthorityResolutionV1`, Python reference seam, and legacy root AuthorityDecision;
- add only lossless/fail-closed compatibility mappings that are actually representable;
- preserve opaque contradiction identity and explicit uncertainty states;
- do not drop EpistemicClass/confidence/source reliability/ambiguity/reason-code semantics;
- keep historical Python format as reference/oracle, not contract owner;
- no orchestrator rewiring yet;
- no Memory writes/admission.

If the legacy DTO cannot carry canonical semantics, keep it quarantined rather than forcing a lossy mapping.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
main resolver merge = b7237542259d86c26632b2185d7e90691e82141f
post-merge resolver CI = 33957882144 SUCCESS
AUTHORITY-1.0 = FROZEN
Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTO binding = COMPLETE / INTEGRATED / TESTED
real DeterministicAuthorityResolver = COMPLETE / INTEGRATED / P0 TESTED
orchestrator uses canonical resolver = false
MipBridge final Authority migration = NOT_STARTED
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = AUTHORITY COMPATIBILITY / MIP BRIDGE COMPLETION ONLY
```
