# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T12:45+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-runtime-adapter-v1`  
Continuity schema: `matrix.assembling.continuity.v54`

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

Purpose:

- prove the canonical resolver can be invoked from runtime-facing inputs without modifying the orchestrator;
- construct `AuthorityResolveRequest` from canonical MIP claim/context/retrieval/provenance inputs;
- provide an explicit legacy-frame compatibility attempt that fails closed when old runtime data cannot supply required canonical semantics;
- return canonical `AuthorityResolution` intact;
- never write root `AuthorityDecision` as a lossy substitute;
- never write Memory or perform admission.

### Design decision — two adapter paths

1. Canonical path

```text
CanonicalAuthorityRuntimeInput
- requestId
- claim: MipClaimV1
- contextSnapshot: MatrixContextSnapshot
- retrievalResult: MipField<RetrievalResult>
- provenance: ProvenanceRef

-> AuthorityResolveRequest
-> DeterministicAuthorityResolver
-> AuthorityResolution
```

2. Legacy compatibility path

```text
MatrixTurnFrame + TypedClaim + canonical Context/Retrieval/Provenance
-> existing MipBridge.fromAssemblingTypedClaim(...)
-> inspect whether the resulting MipClaimV1 preserves enough semantics
-> RESOLVED only if safe
-> BLOCKED with deterministic reason codes when fields are unavailable/unresolved
```

Important expected legacy gaps:

```text
source identity may be UNKNOWN because root TypedClaim has no source EntityRef
dialogueAct is unavailable in root TypedClaim mapping
claimKind is absent
perspective/owner may be nullable
root AuthorityDecision cannot round-trip canonical EpistemicClass/confidence/provenance
```

Therefore old runtime compatibility is not assumed. The adapter must expose the gap rather than guessing source/perspective/act or silently dropping canonical output fields.

### Hard boundaries

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

### Planned tests

```text
canonical input -> resolver COMPLETE and full AuthorityResolution preserved
canonical REPORT with resolved source works
legacy USER_ASSERTION with unknown source -> BLOCKED/HOLD, no guessed source
trusted WORLD legacy projection may resolve only when provenance independently authorizes WORLD
multi-claim requires claim-explicit invocation, no implicit first-claim selection
legacy adapter never mutates MatrixTurnFrame.authorityDecision
legacy projection status names exact missing/unavailable fields
no Memory write API/dependency introduced
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-runtime-adapter-v1
base = 70e761de23e9c162c2415054e8662881590b2753
canonical runtime adapter = TASK STARTED / NO CODE YET
orchestrator uses canonical resolver = false
legacy BasicAuthorityResolver = STILL PRESENT / COMPATIBILITY
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = IMPLEMENT STANDALONE CANONICAL/LEGACY AUTHORITY RUNTIME ADAPTER + TESTS
```
