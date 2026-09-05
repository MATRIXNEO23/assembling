# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:27+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v63`

## Mandatory continuity policy

Single canonical restart file. Update at every meaningful checkpoint/CI/architecture decision and before STOP.

Canonical work method:

`docs/MATRIX_ENGINE_WORK_METHOD.md`

Canonical checkpoint roadmap:

`docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

Hard rules now explicitly include:

```text
CONTRACT BEFORE CODE
ONE OWNER PER STATE
ADAPTER BEFORE DIRECT COUPLING
FAIL CLOSED BEFORE GUESSING
UNIT + CROSS-MODULE + E2E
DIAGNOSTIC TRACE EVERYWHERE
FIX CAUSE, NOT SYMPTOM
NEVER LOWER A GATE
ONE WRITE REPO AT A TIME
NO NEXT MODULE UNTIL CURRENT SUITE IS 100% GREEN
SAVE EXACT CONTINUITY BEFORE ANY PRIORITY SWITCH OR INTERRUPTION
```

## Completed Authority / MIP baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = MERGED/GREEN
shared MIP evidence PR #12 = MERGED/GREEN
Authority runtime DTO PR #13 = MERGED/GREEN
real Authority resolver PR #14 = MERGED/GREEN
Authority compatibility PR #15 = MERGED/GREEN
canonical Authority runtime adapter PR #16 = MERGED/GREEN
runtime frame canonical MIP slots PR #17 merge = 566751798d5ea2dc93db5a01039715f785b04d00
post-merge frame-slot CI = 33962117105 SUCCESS
frame-slot continuity commit = 4efcd9a1d275fa6cf2d5546ea0b0e76f51800897
frame-slot continuity CI = 33962208689 SUCCESS
```

Other repos = READ-ONLY. Memory writes/admission = NOT TOUCHED.

## Canonical work-method checkpoint

Document:

`docs/MATRIX_ENGINE_WORK_METHOD.md`

Commit:

`c0e49e91a6d89563a080e1aea28df1bc3d872dea`

The method is binding for future Matrix Engine work. In particular, a downstream integration checkpoint must not start while an upstream canonical semantic boundary is still incomplete.

## Canonical checkpoint roadmap — FROZEN FOR CURRENT EXECUTION ORDER

Document:

`docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

Roadmap commit:

`140cd6837dacc7e008e84b642291dfa86ae0072a`

Execution order:

```text
PHASE U — UNDERSTANDING V3
  CP-U1 V3 lossless contract audit                       ACTIVE
  CP-U2 Understanding/MIP contract extension             CONDITIONAL
  CP-U3 Canonical Understanding V3 adapter/runtime        LOCKED
  CP-U4 Understanding cross-module freeze                 LOCKED

PHASE A — AUTHORITY ORCHESTRATOR
  CP-A1 Canonical Authority runtime port/stage            LOCKED
  CP-A2 Authority orchestrator rewire + freeze            LOCKED

PHASE M — MEMORY
  CP-M1 Memory Admission contract + adapter               LOCKED
  CP-M2 MemoryRepository persistence + lineage            LOCKED
  CP-M3 Retrieval integration                             LOCKED
  CP-M4 Persistent Consolidation after validation         LOCKED
  CP-M5 Memory automatic E2E freeze                       LOCKED

PHASE E — FULL AUTOMATIC INTEGRATION
  CP-E1 Closed-loop automatic integration                 LOCKED

PHASE APK — FIRST DEVICE LOOP
  CP-APK1 Diagnostic APK + provisional INT8 runtime       LOCKED
  CP-APK2 Real device Memory tests                        LOCKED

PHASE N — FINAL STUDENT-5 V3 SWAP
  CP-N1 Physical V3 load/export/quantization verification EXTERNAL/DEPENDENT
  CP-N2 Swap provisional runtime for final Matrix-NLU V3  LOCKED

PHASE R — REFLECTION
  CP-R1 Reflection contract freeze                        LOCKED
  CP-R2 Reflection implementation                         LOCKED
  CP-R3 Reflection cross-module integration               LOCKED
  CP-R4 Reflection freeze                                 LOCKED
```

No LOCKED checkpoint may preempt the ACTIVE checkpoint unless the owner explicitly changes priority after an exact continuity save.

If interrupted mid-checkpoint, continuity MUST record:

```text
checkpoint ID
last completed operation
operation in progress
last green commit
current HEAD/branch
changed files
known risks
what must not be repeated
exact restart action
```

## Current architecture state

```text
Canonical Context slot in MatrixTurnFrame = READY
Canonical Retrieval results slot = READY / MULTI-RESULT
Canonical AuthorityResolution slot = READY / MULTI-CLAIM
CanonicalAuthorityRuntimeAdapter = READY
DeterministicAuthorityResolver = READY
legacy AuthorityDecision = COMPATIBILITY-ONLY
orchestrator uses canonical resolver = false
Memory Admission = NOT IMPLEMENTED HERE
MemoryRepository = NOT TOUCHED
```

## ACTIVE CHECKPOINT — CP-U1 UNDERSTANDING V3 LOSSLESS CONTRACT AUDIT

Owner priority is now explicit: finish Understanding before canonical Authority orchestrator rewire, then Memory immediately after Authority integration.

CP-U1 must compare every frozen V3 field against current `MipClaimV1` and Assembling runtime types and classify it as:

```text
LOSSLESS
REPRESENTABLE_WITH_EXISTING_MIP_FIELD
MISSING_FROM_MIP_RUNTIME
LEGACY_ONLY_LOSS
NOT_APPLICABLE_DOWNSTREAM
```

Mandatory audit coverage:

```text
speaker / observer / source / subject / target / owner / perspective
dialogueAct / predicate / claimKind / polarity
subjectSpans[] / objectSpans[] / negationCueSpans[] / temporalEvidence[]
entityMentionIds / referentCandidates
fieldStatus / alternatives / confidenceByField
temporalRelation + temporalAnchorRef
structuralStatus / interpretationStatus / overallInterpretationConfidence
multi-claim identity and provenance
```

Decision after CP-U1:

```text
if current MIP/runtime is lossless -> CP-U3
if frozen V3 semantics cannot be represented losslessly -> CP-U2 first
```

No implementation shortcut is allowed.

## Explicitly NOT authorized during CP-U1

```text
no matrix-understanding-lab writes
no Student-5 training changes
no orchestrator Authority rewire
no BasicAuthorityResolver replacement
no Memory Admission
no MemoryRepository write/dependency
no PersistentConsolidation
no APK work
no Reflection
no other repo writes
```

The existing Student-5 Path A ONNX INT8 runtime probe is reserved for CP-APK1/runtime loop closure only. It does not preempt CP-U1 and is not the final Matrix-NLU V3.

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
work method = docs/MATRIX_ENGINE_WORK_METHOD.md
checkpoint roadmap = docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md
roadmap commit = 140cd6837dacc7e008e84b642291dfa86ae0072a
frame-slot continuity CI = 33962208689 SUCCESS
AUTHORITY-1.0 = FROZEN
shared MIP evidence = COMPLETE / TESTED
DeterministicAuthorityResolver = COMPLETE / TESTED
CanonicalAuthorityRuntimeAdapter = COMPLETE / TESTED
MatrixTurnFrame canonical MIP slots = COMPLETE / TESTED
ACTIVE CHECKPOINT = CP-U1
Understanding V3 canonical boundary = AUDIT IN PROGRESS
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = COMPLETE CP-U1 LOSSLESS FIELD-BY-FIELD AUDIT; DO NOT START CP-U2/U3 UNTIL CP-U1 VERDICT
```
