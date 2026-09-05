# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T14:22+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `understanding-v3-runtime-v1`  
Continuity schema: `matrix.assembling.continuity.v70`

## Owner-approved execution order

The owner explicitly corrected the integration order on 2026-09-05:

```text
1. IMPLEMENT UNDERSTANDING V3
   -> produce real canonical TypedClaim evidence
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
   -> consume real TypedClaims
4. IMPLEMENT MEMORY KOTLIN/ROOM
   -> consume AuthorityResolution
```

This order overrides the prior temporary controlled stop that put Python Authority verification before Understanding.

Reason: Authority must ultimately consume the canonical claims produced by Understanding; therefore the canonical Understanding runtime boundary must exist first.

## Hard rules

Canonical method: `docs/MATRIX_ENGINE_WORK_METHOD.md`  
Checkpoint roadmap: `docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

```text
CONTRACT BEFORE CODE
ONE OWNER PER STATE
ADAPTER BEFORE DIRECT COUPLING
FAIL CLOSED BEFORE GUESSING
UNIT + CROSS-MODULE + E2E
DIAGNOSTIC TRACE EVERYWHERE
NEVER LOWER A GATE
ONE WRITE REPO AT A TIME
NO NEXT MODULE UNTIL CURRENT REQUIRED SUITE IS GREEN
PIVOT ONLY AFTER OWNER DISCUSSION/APPROVAL
```

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 Kotlin = FROZEN
Kotlin DeterministicAuthorityResolver = IMPLEMENTED / TESTED / GREEN
MatrixTurnFrame canonical Context/Retrieval/Authority slots = MERGED/GREEN
CP-U1 Understanding V3 lossless audit = COMPLETE / PASS
CP-U1 closure CI = 33964635851 SUCCESS
CP-U2 Understanding V3 MIP profile = COMPLETE / MERGED / GREEN
CP-U2 profile = MIP-1.0/UNDERSTANDING-V3-1.0
CP-U2 merge = accb1e7ac47738bc5d658ca44808c220e16dad32
CP-U2 post-merge CI = 33965518114 SUCCESS
```

## ACTIVE CHECKPOINT — CP-U3 UNDERSTANDING V3 RUNTIME / REAL TYPED CLAIMS

Branch:

`understanding-v3-runtime-v1`

Base:

`c31c963bcf0cace44edd17e22ab732a974ef8f7a`

Goal:

```text
Matrix-NLU V3 runtime output
-> validated Understanding V3 adapter
-> MipUnderstandingV3Observation
-> real canonical claim list (MipUnderstandingV3Claim[])
-> MatrixTurnFrame canonical Understanding slot
-> DiagnosticTrace
```

The real canonical claims for this checkpoint are the complete `MipUnderstandingV3Claim` values carried by `MipUnderstandingV3Observation.claims`. Legacy root `TypedClaim` / `MipClaimV1` remain compatibility/projection surfaces and must not be treated as the lossless V3 owner.

CP-U3 must preserve:

```text
original V3 claimId
speaker / observer / source / subject / target / owner / perspective
claimKind / dialogueAct / predicate / polarity
plural subject/object/negation/temporal evidence
referent candidate identity
field status/confidence/alternatives
temporal anchor
structuralStatus / interpretationStatus
observation + claim provenance
multi-claim separation
```

Forbidden in canonical Understanding V3 output:

```text
worldTruth
authority
memoryAdmission
beliefConfidence
persistentConsent
persistentGoal
relationshipState
affectiveState
behaviorDecision
```

## Explicitly not in CP-U3

```text
no Python Authority Resolver modification yet
no Authority orchestrator integration yet
no Memory Kotlin/Room
no MemoryRepository
no PersistentConsolidation
no matrix-understanding-lab writes
no Student-5 training change
no other repository writes
```

## Subsequent owner-approved checkpoints

After CP-U3 is fully green:

```text
STEP 2 = Python Authority Resolver P0 fixes
STEP 3 = Understanding -> Authority integration using real canonical claims
STEP 4 = Memory Kotlin/Room consuming AuthorityResolution
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = understanding-v3-runtime-v1
ACTIVE = CP-U3
CP-U2 = COMPLETE / MERGED / GREEN
CP-U3 implementation = STARTING
Python Authority P0 fixes = DEFERRED UNTIL CP-U3 GREEN
Authority integration = NOT STARTED
Memory Kotlin/Room = NOT STARTED
other repos = READ-ONLY
NEXT = implement validated Understanding V3 runtime adapter + canonical frame slot + tests
```
