# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T14:14+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `understanding-v3-contract-v1`  
Continuity schema: `matrix.assembling.continuity.v68`

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

## ACTIVE CHECKPOINT — CP-U2 UNDERSTANDING/MIP CONTRACT EXTENSION

Owner approval to proceed was received.

Branch:

`understanding-v3-contract-v1`

Base:

`8278ea0b94c6500b3afc511bf21230c9c51679b9`

PR:

`#18 — Add lossless MIP Understanding V3 profile`

### Contract/profile

Document:

`docs/MIP_UNDERSTANDING_V3_PROFILE.md`

Profile:

```text
MIP-1.0/UNDERSTANDING-V3-1.0
```

Profile doc commit:

`d1c55206653e58c48562dbcfc4aecb3a2484abc3`

### Kotlin contract types

File:

`src/main/kotlin/matrix/assembling/mip/MipUnderstandingV3Contracts.kt`

Commit:

`1d0f71049d409252962eddc749550b5ca31fef1a`

Implemented additive types preserving:

```text
upstream contract identity + SHA-256 fingerprint
observationSourceId
NLU observation provenance
speaker / observer
mentions[]
referentCandidates[]
original claim IDs
claim provenance
sourceSpan
subjectSpans[]
objectSpans[]
negationCueSpans[]
temporalEvidence[]
entityMentionIds[]
dialogueAct / predicate / five role fields / polarity / temporalRelation / claimKind
V3 value + confidence + fieldStatus + ranked alternatives
fieldStatusByField
confidenceByField
overallInterpretationConfidence
structuralStatus
interpretationStatus
diagnostics[]
temporal anchor identity
```

The canonical profile intentionally exposes no `worldTruth`, Authority, Memory Admission, BeliefConfidence, persistent consent/goal, RelationshipState, AffectiveState or behavior-decision ownership.

Legacy `MipClaimV1` is unchanged.

### Tests

File:

`src/test/kotlin/matrix/assembling/mip/MipUnderstandingV3ContractsTest.kt`

Commit:

`1ce78ee5bc3896bc66f7373768f0e6845f858c63`

Coverage includes:

```text
independent source vs perspective
plural negation evidence
original claim ID preservation
ambiguous UNKNOWN primary + ranked alternatives
invalid role candidate rejection
NONE/UNKNOWN role-status invariants
required temporal anchor
unknown temporal/claim anchor rejection
multi-claim claim-anchor identity
INVALID => ABSTAINED
observation/claim provenance binding
mention-candidate evidence consistency
non-overlapping / in-source plural spans
exact upstream contract/fingerprint validation
absence of forbidden worldTruth/Authority/Memory fields
legacy MipClaimV1 remains independently constructible
```

### Diff audit

Diff from CP-U2 base contains exactly:

```text
docs/MIP_UNDERSTANDING_V3_PROFILE.md
docs/WORK_CONTINUITY.md
src/main/kotlin/matrix/assembling/mip/MipUnderstandingV3Contracts.kt
src/test/kotlin/matrix/assembling/mip/MipUnderstandingV3ContractsTest.kt
```

No orchestrator, Understanding runtime adapter, Authority implementation, Memory component or other repository changed.

### CI evidence

PR head before this continuity update:

`1ce78ee5bc3896bc66f7373768f0e6845f858c63`

Full repository CI:

```text
run = 33965360537
job = kotlin-tests
Run tests = SUCCESS
job conclusion = SUCCESS
```

No task-introduced fix was required.

This continuity update creates a new final PR head. Merge remains forbidden until CI for that exact final head is green.

## CP-U2 current verdict

```text
PROFILE DESIGN = COMPLETE
KOTLIN CONTRACT TYPES = COMPLETE
INVARIANT TESTS = COMPLETE
FULL REGRESSION = GREEN
FINAL DOC HEAD CI = PENDING
CP-U2 = PASS PENDING FINAL-HEAD CI / MERGE / POST-MERGE CI
CP-U3 = NOT STARTED
```

## Hard boundaries still enforced

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

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = understanding-v3-contract-v1
PR = #18
base = 8278ea0b94c6500b3afc511bf21230c9c51679b9
last green functional/test head = 1ce78ee5bc3896bc66f7373768f0e6845f858c63
CI = 33965360537 SUCCESS
ACTIVE = CP-U2
current operation = verify final continuity-only PR head CI
CP-U3 = NOT STARTED
Authority orchestrator rewire = NOT STARTED
Memory = NOT STARTED
other repos = READ-ONLY
NEXT = final-head CI -> merge PR #18 if green -> post-merge main CI -> close CP-U2 -> start CP-U3 only after green main baseline
```
