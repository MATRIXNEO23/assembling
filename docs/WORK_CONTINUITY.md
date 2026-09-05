# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T12:40+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v53`

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

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 merge = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 merge = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 merge = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
real Authority resolver PR #14 merge = b7237542259d86c26632b2185d7e90691e82141f
resolver post-merge CI = 33957882144 SUCCESS
resolver continuity = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
resolver continuity CI = 33957996637 SUCCESS
```

Canonical `DeterministicAuthorityResolver` is implemented and P0-tested. It is still deliberately not orchestrator-wired. Memory writes/admission remain untouched.

## AUTHORITY COMPATIBILITY / MIP BRIDGE CHECKPOINT — COMPLETE / INTEGRATED / TESTED

Branch:

`authority-compatibility-v1`

PR:

`#15 — Add fail-closed canonical Authority compatibility projections`

Final tested PR head:

`3e56bdf6ede1ded7b5c1468239102d6b4c3c0a07`

PR final-head CI:

```text
33958231886 = SUCCESS
```

Merge SHA:

`736aee2ebcd977c89faab9e519ace0f2420f668d`

Post-merge main CI:

```text
33961173851 = SUCCESS
job = kotlin-tests
Run tests = SUCCESS
```

Integrated files:

```text
src/main/kotlin/matrix/assembling/mip/MipAuthorityCompatibility.kt
src/test/kotlin/matrix/assembling/mip/MipAuthorityCompatibilityTest.kt
```

Legacy `MipAuthorityResolutionV1` and root `AuthorityDecision` remain unchanged/quarantined because they cannot losslessly represent canonical AUTHORITY-1.0 fields.

Canonical compatibility projections now available:

```text
AuthorityResolution.toKotlinMemoryContradictionProjection()
AuthorityResolution.toPythonContradictionProjection()
```

Projection invariants:

- only `resolutionStatus == COMPLETE` may project;
- `PRESENT("42")` preserves exact decimal identity;
- Kotlin projection additionally requires exact in-range `Long`;
- historical Python projection accepts exact arbitrary-size `BigInteger`;
- `NOT_APPLICABLE` becomes native null / None;
- `PARTIAL`, `HOLD`, `UNAVAILABLE`, `ERROR` cannot masquerade as admission-compatible output;
- opaque MemoryRef such as `memory:42` fails closed;
- noncanonical decimal identity such as `001` fails closed rather than normalize to `1`;
- Python projection is explicitly contradiction-only, not a full historical AuthorityResolution mapping.

No orchestrator, MemoryRepository, Memory Admission, root AuthorityDecision, or other repository was modified in PR #15.

## Canonical Authority state now available

```text
AuthorityResolveRequest
AuthorityResolution
AuthorityCandidateEvidencePort (read-only)
DeterministicAuthorityResolver
canonical contradiction identity/status
Kotlin Memory contradiction projection
historical Python contradiction projection
```

Authority semantics already enforced:

```text
WORLD_TRUTH only trusted WORLD provenance
OBSERVATION only trusted perception provenance
REPORT distinct from observation
BELIEF distinct from report/truth
INFERENCE requires derivation evidence
same actor != contradiction
unrelated predicate != contradiction
temporal change != contradiction by default
only VALID candidate can be contradiction target
multiple plausible contradiction targets -> AMBIGUOUS/HOLD
unresolved evidence prevents concrete contradiction ID
correction != automatic contradiction/supersession
SourceReliability not fabricated from NLU confidence
```

## Still intentionally NOT wired

Current runtime still contains compatibility path:

```text
IntegrationPorts.AuthorityResolverPort -> MatrixTurnFrame
BasicAdapters.BasicAuthorityResolver -> root AuthorityDecision
MatrixAssemblingOrchestrator -> AuthorityResolverPort
```

Therefore:

```text
canonical DeterministicAuthorityResolver = IMPLEMENTED / TESTED
canonical runtime adapter into MatrixTurnFrame = NOT STARTED
orchestrator uses canonical resolver = false
legacy BasicAuthorityResolver = STILL PRESENT / COMPATIBILITY
root AuthorityDecision = STILL LEGACY / QUARANTINED
```

## NEXT ACTIVE CHECKPOINT — CANONICAL AUTHORITY RUNTIME ADAPTER ONLY

Next bounded task:

```text
CANONICAL AUTHORITY RUNTIME ADAPTER
```

Goal:

- add an adapter in the dedicated Authority/adapters boundary that can construct `AuthorityResolveRequest` from existing runtime data plus canonical MIP Context/Retrieval evidence;
- invoke `DeterministicAuthorityResolver`;
- expose canonical `AuthorityResolution` for diagnostics/future Memory seam;
- preserve fail-closed semantics when old `MatrixTurnFrame` / root `AuthorityDecision` cannot represent the full canonical result;
- do not replace `BasicAuthorityResolver` in the orchestrator yet;
- do not redesign `MatrixTurnFrame` yet;
- do not write Memory;
- do not implement Memory Admission;
- do not modify other repositories.

Before any orchestrator rewire, tests must prove the adapter works standalone and that no canonical Authority field is silently lost.

## Explicitly NOT implemented / not authorized yet

```text
orchestrator canonical Authority rewire
BasicAuthorityResolver removal/replacement
root AuthorityDecision redesign
Memory Admission SAVE/SUPERSEDE/REJECT/IGNORE
MemoryRepository implementation/dependency
PersistentConsolidation
end-to-end durable Memory write
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
main compatibility merge = 736aee2ebcd977c89faab9e519ace0f2420f668d
post-merge compatibility CI = 33961173851 SUCCESS
AUTHORITY-1.0 = FROZEN
Authority value types = COMPLETE / INTEGRATED / TESTED
shared MIP evidence contracts = COMPLETE / INTEGRATED / TESTED
Authority runtime DTO binding = COMPLETE / INTEGRATED / TESTED
real DeterministicAuthorityResolver = COMPLETE / INTEGRATED / P0 TESTED
canonical compatibility projections = COMPLETE / INTEGRATED / TESTED
canonical runtime adapter = NOT STARTED
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = CANONICAL AUTHORITY RUNTIME ADAPTER ONLY
```
