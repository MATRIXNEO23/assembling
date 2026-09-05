# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T14:02+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `understanding-v3-contract-v1`  
Continuity schema: `matrix.assembling.continuity.v67`

## Mandatory policy

Canonical method: `docs/MATRIX_ENGINE_WORK_METHOD.md`  
Checkpoint roadmap: `docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

Hard rules remain:

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
AUTHORITY-1.0 = FROZEN
Authority value types / shared evidence / DTO / resolver / compatibility / runtime adapter = MERGED/GREEN
MatrixTurnFrame canonical Context/Retrieval/Authority slots = MERGED/GREEN
frame-slot merge = 566751798d5ea2dc93db5a01039715f785b04d00
frame-slot post-merge CI = 33962117105 SUCCESS
CP-U1 audit commit = fd23526be154863f840ef373bbb0635242d10ff5
CP-U1 closure = 8278ea0b94c6500b3afc511bf21230c9c51679b9
CP-U1 closure CI = 33964635851 SUCCESS
```

Other repositories remain READ-ONLY. Memory writes/admission remain untouched.

## CP-U1 — COMPLETE

Document: `docs/UNDERSTANDING_V3_LOSSLESS_AUDIT.md`

Verdict:

```text
CP-U1 = PASS
CURRENT MIP/RUNTIME LOSSLESS FOR MATRIX_NLU_CONTRACT_V3 = NO
CP-U2 REQUIRED = YES
```

P0 findings preserved:

```text
P0-U1-01 legacy NLU can carry worldTruth
P0-U1-02 independent sourceReferent lost
P0-U1-03 plural spans collapse
P0-U1-04 temporalAnchorRef absent
P0-U1-05 structuralStatus / interpretationStatus absent
P0-U1-06 candidate identity / alternatives absent
P0-U1-07 legacy source category synthesized from dialogueAct
```

## ACTIVE CHECKPOINT — CP-U2 UNDERSTANDING/MIP CONTRACT EXTENSION

Owner approval to proceed received in current conversation.

Branch:

`understanding-v3-contract-v1`

Base:

`8278ea0b94c6500b3afc511bf21230c9c51679b9`

### CP-U2 design direction

Do NOT mutate frozen `MATRIX_NLU_CONTRACT_V3`.
Do NOT force frozen V3 evidence into legacy `MipClaimV1`.
Do NOT remove legacy DTOs in this checkpoint.

Add a new additive/versioned MIP Understanding V3 profile that preserves the complete frozen linguistic evidence envelope.

Proposed profile identity:

```text
MIP-1.0 / UNDERSTANDING-V3-1.0
```

Canonical profile must preserve:

```text
upstream contract version + fingerprint
observationSourceId + MIP provenance
speaker / observer
mentions[]
referentCandidates[]
original claimIds
sourceSpan
subjectSpans[]
objectSpans[]
negationCueSpans[]
temporalEvidence[]
entityMentionIds[]
dialogueAct / predicate / five referent roles / polarity / temporalRelation / claimKind
per-field value + confidence + V3 fieldStatus + alternatives[]
confidenceByField
overallInterpretationConfidence
structuralStatus
interpretationStatus
claim diagnostics[]
temporal anchor identity
```

The new profile MUST NOT expose NLU-owned `worldTruth`, Memory Admission, Authority, BeliefConfidence, persistent consent/goal, RelationshipState, AffectiveState or behavior decision.

### Compatibility strategy

```text
MipClaimV1 = unchanged legacy/downstream compatibility claim
Understanding V3 profile = new canonical lossless linguistic evidence envelope
no automatic lossy V3 -> legacy conversion
any later projection to Authority input must be explicit, validated and owned by CP-U3/CP-A1
```

### Hard boundaries for CP-U2

```text
no matrix-understanding-lab writes
no Student-5 training changes
no Understanding runtime adapter implementation yet
no MatrixAssemblingOrchestrator rewire
no BasicAuthorityResolver changes
no Memory Admission
no MemoryRepository
no PersistentConsolidation
no APK
no Reflection
no other repo writes
```

### CP-U2 gates

```text
versioned contract doc
Kotlin value types with constructor invariants
source != perspective independently representable
plural evidence preserved
candidate IDs/alternatives preserved
V3 statuses preserved exactly
required temporal anchors structurally validated
original V3 claim IDs preserved
MIP provenance bound to observation
no authority/worldTruth field in canonical profile
legacy MipClaimV1 unchanged
unit/boundary regression tests
full Kotlin suite green
final-head CI green
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = understanding-v3-contract-v1
base = 8278ea0b94c6500b3afc511bf21230c9c51679b9
ACTIVE = CP-U2
last completed operation = CP-U1 closure CI SUCCESS
current operation = define/freeze additive Understanding V3 MIP profile before adapter code
CP-U3 = NOT STARTED
Authority orchestrator rewire = NOT STARTED
Memory = NOT STARTED
other repos = READ-ONLY
NEXT = write CP-U2 profile spec -> implement Kotlin contract types -> add invariant tests -> full CI
```
