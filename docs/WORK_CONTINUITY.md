# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:41+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-compatibility-v1`  
Continuity schema: `matrix.assembling.continuity.v50`

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

Current legacy surfaces:

```text
MipAuthorityResolutionV1
- accepted
- ownerResolved
- sourceType
- conflictStatus
- contradictedMemoryId
- reason

root AuthorityDecision
- accepted
- ownerResolved
- sourceType
- conflictStatus
- reason
```

Canonical AUTHORITY-1.0 `AuthorityResolution` additionally owns/resolves:

```text
claim/context/retrieval identity
resolutionStatus
EpistemicClass
AuthorityResolutionConfidence
SourceReliability
explicit contradictedMemoryRef state
candidateMemoryRefs
ambiguityReasons
AUTHORITY reasonCodes
provenance
```

Therefore:

```text
canonical AuthorityResolution -> MipAuthorityResolutionV1
= NOT LOSSLESS

canonical AuthorityResolution -> root AuthorityDecision
= NOT LOSSLESS
```

No forced migration will be added. Legacy types remain compatibility/quarantine surfaces.

### Representable seams authorized in this task

1. Canonical `AuthorityResolution` -> existing `KotlinMemoryAuthorityDecisionWire`
   - only when `resolutionStatus == COMPLETE`;
   - PRESENT opaque MemoryRef converts to Kotlin Long only if exact decimal/in-range;
   - NOT_APPLICABLE converts to null;
   - all unresolved/ambiguous/unavailable/error states fail closed.

2. Canonical `AuthorityResolution` -> historical Python contradiction-only projection
   - explicitly named/treated as a projection, NOT a full Python AuthorityResolution mapping;
   - only COMPLETE resolutions;
   - PRESENT MemoryRef must be exact decimal BigInteger;
   - NOT_APPLICABLE -> None/null;
   - no claim/authority/confidence/reason field is silently claimed to have been mapped.

3. Existing legacy `MipAuthorityResolutionV1` mappings remain for compatibility tests only and will be marked/decribed as deprecated transition projections where safe.

### Hard boundaries

```text
no orchestrator rewiring
no BasicAuthorityResolver replacement
no root AuthorityDecision redesign
no Memory Admission implementation
no MemoryRepository dependency/write
no Python schema invention beyond the already-known contradiction-only wire
no other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-compatibility-v1
base = bd1f46751e220bbc570b44e5a80c5b56bc4dab0e
compatibility audit = COMPLETE
code changes = NOT STARTED
NEXT = ADD CANONICAL COMPLETE-ONLY CONTRADICTION PROJECTIONS + FAIL-CLOSED TESTS; UPDATE MIP BRIDGE AUDIT
```
