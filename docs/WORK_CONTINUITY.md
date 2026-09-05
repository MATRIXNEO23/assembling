# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:45+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-compatibility-v1`  
Continuity schema: `matrix.assembling.continuity.v51`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update after every meaningful checkpoint, CI result, architecture decision, before risky operations and before STOP/session end.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
real Authority resolver PR #14 = b7237542259d86c26632b2185d7e90691e82141f
resolver post-merge CI = 33957882144 SUCCESS
resolver continuity = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
resolver continuity CI = 33957996637 SUCCESS
```

Canonical `DeterministicAuthorityResolver` is implemented/P0-tested but deliberately not orchestrator-wired. Memory writes/admission remain untouched.

## ACTIVE TASK — AUTHORITY COMPATIBILITY / MIP BRIDGE COMPLETION ONLY

```text
branch = authority-compatibility-v1
base = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
other repos modified = false
```

### Compatibility audit decision

Legacy `MipAuthorityResolutionV1` and root `AuthorityDecision` cannot losslessly represent canonical `AuthorityResolution` because they lack EpistemicClass, resolver confidence, source reliability, full contradiction status, candidate identities, ambiguity, reason-code list and provenance. They remain compatibility/quarantine surfaces; no forced canonical conversion is authorized.

### Checkpoint 1 — canonical contradiction-only projections

```text
commit = 56e94328da2f57f895365911ad2ada711d7be462
file = src/main/kotlin/matrix/assembling/mip/MipAuthorityCompatibility.kt
```

Added only representable canonical projections:

```text
AuthorityResolution.toKotlinMemoryContradictionProjection()
AuthorityResolution.toPythonContradictionProjection()
```

Rules:

- `resolutionStatus` must be COMPLETE;
- contradiction PRESENT -> exact canonical decimal identity;
- Kotlin projection requires exact in-range Long;
- Python projection accepts arbitrary-size exact BigInteger;
- NOT_APPLICABLE -> native null/None;
- PARTIAL/HOLD/UNAVAILABLE/ERROR cannot masquerade as admission-compatible output;
- opaque nonnumeric MemoryRef fails closed;
- noncanonical decimal forms such as `001` fail closed rather than normalize identity to `1`;
- these functions project only contradiction identity and explicitly do NOT claim to map the full historical Python AuthorityResolution.

No `MipAuthorityResolutionV1` or root `AuthorityDecision` redesign/migration was introduced.

### Checkpoint 2 — compatibility gates

```text
commit = 95d2720c6cd800749071e263a3b46ebb48dd31e9
file = src/test/kotlin/matrix/assembling/mip/MipAuthorityCompatibilityTest.kt
```

Coverage:

```text
COMPLETE + no contradiction -> Kotlin null / Python None
COMPLETE + canonical decimal ID -> exact Long + BigInteger
opaque nonnumeric MemoryRef -> fail closed
Python arbitrary integer survives while Kotlin overflow fails closed
noncanonical decimal identity `001` -> fail closed
PARTIAL -> cannot project
HOLD/AMBIGUOUS -> cannot project
```

### Current validation state

```text
compatibility code = ADDED
compatibility tests = ADDED
full regression CI = NOT YET RUN
legacy Authority DTOs = UNCHANGED / QUARANTINED
orchestrator = UNCHANGED
Memory = UNCHANGED
```

### Hard boundaries

```text
no orchestrator rewiring
no BasicAuthorityResolver replacement
no root AuthorityDecision redesign
no Memory Admission implementation
no MemoryRepository dependency/write
no Python schema invention beyond known contradiction-only wire
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-compatibility-v1
base = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
last functional commit = 56e94328da2f57f895365911ad2ada711d7be462
last test commit = 95d2720c6cd800749071e263a3b46ebb48dd31e9
compatibility projections = CODE + TESTS ADDED / CI PENDING
legacy MipAuthorityResolutionV1 = QUARANTINED / UNCHANGED
orchestrator rewiring = NOT STARTED
other repos = READ-ONLY
NEXT = VERIFY DIFF; OPEN PR; FULL CI; MERGE ONLY IF FINAL HEAD GREEN
```
