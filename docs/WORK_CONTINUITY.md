# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T13:34+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v66`

## Mandatory continuity policy

Single canonical restart file. Update at every meaningful checkpoint/CI/architecture decision and before STOP.

Canonical work method:

`docs/MATRIX_ENGINE_WORK_METHOD.md`

Canonical checkpoint roadmap:

`docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

Hard rules:

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
SEMANTIC QUALITY IS MEASURED AND MAXIMIZED, NOT FROZEN AS ONE GLOBAL PERCENTAGE
PROPOSE PIVOTS WITH EVIDENCE; CHANGE DIRECTION ONLY AFTER OWNER APPROVAL
```

Owner-approved work-method pivot rule commit:

`d23d08159695045dcb1cd2c7ecbf7365ac365795`

Any change of method/module/architecture/model/strategy/priority must be discussed with the owner before execution. The assistant may identify and recommend alternatives but may not execute a pivot autonomously.

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

## Canonical checkpoint roadmap

Roadmap:

`docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`

Roadmap commit:

`140cd6837dacc7e008e84b642291dfa86ae0072a`

Execution order remains:

```text
PHASE U — UNDERSTANDING V3
  CP-U1 V3 lossless contract audit                       COMPLETE
  CP-U2 Understanding/MIP contract extension             RECOMMENDED / AWAIT OWNER APPROVAL
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

## CP-U1 — UNDERSTANDING V3 LOSSLESS CONTRACT AUDIT — COMPLETE

Audit document:

`docs/UNDERSTANDING_V3_LOSSLESS_AUDIT.md`

Audit commit:

`fd23526be154863f840ef373bbb0635242d10ff5`

Read-only source contract:

`MATRIXNEO23/matrix-understanding-lab` / `MATRIX_NLU_CONTRACT_V3`

No write was made to `matrix-understanding-lab`.

### CP-U1 verdict

```text
CP-U1 = PASS
AUDIT COMPLETE = YES
CURRENT MIP/RUNTIME LOSSLESS FOR MATRIX_NLU_CONTRACT_V3 = NO
CP-U3 DIRECT START = BLOCKED
CP-U2 REQUIRED = YES
```

The current boundary is structurally insufficient for frozen V3 semantics.

### Critical findings

```text
P0-U1-01 legacy MatrixNluClaim contains worldTruth and bridge can map it to WORLD_TRUTH
P0-U1-02 sourceReferent is absent from legacy MatrixNluClaim and current bridge writes source=UNKNOWN
P0-U1-03 plural subject/object/negation/temporal evidence collapses to one span
P0-U1-04 temporalAnchorRef has no canonical runtime representation
P0-U1-05 structuralStatus and interpretationStatus do not survive current claim boundary
P0-U1-06 candidate identity and ranked ambiguity alternatives are absent
P0-U1-07 legacy adapter derives source category from dialogueAct instead of consuming V3 claimKind/sourceReferent
```

Other missing/lossy V3 evidence includes:

```text
upstream NLU contract identity/fingerprint
observationSourceId / claim provenance
mentions[]
referentCandidates[]
entityMentionIds[]
per-field alternatives[]
complete fieldStatusByField
V3 validator diagnostics linkage
original V3 claim identity preservation
```

### Reusable current work

CP-U1 does NOT recommend discarding the current architecture. Reusable pieces include:

```text
MipField / MipFieldStatus
MipEntityRef / MipEntityResolutionStatus
MipSpan primitive
MatrixTurnFrame canonical multi-claim slots
DiagnosticTrace infrastructure
CanonicalAuthorityRuntimeAdapter
DeterministicAuthorityResolver
Authority reason-code system
legacy adapters as compatibility/quarantine paths
```

The structural gap can be addressed with a bounded contract extension/version rather than rebuilding the engine from zero.

## Current architecture state

```text
Canonical Context slot in MatrixTurnFrame = READY
Canonical Retrieval results slot = READY / MULTI-RESULT
Canonical AuthorityResolution slot = READY / MULTI-CLAIM
CanonicalAuthorityRuntimeAdapter = READY
DeterministicAuthorityResolver = READY
legacy AuthorityDecision = COMPATIBILITY-ONLY
Understanding V3 lossless audit = COMPLETE
Understanding V3 canonical runtime contract = INSUFFICIENT / REQUIRES CP-U2
orchestrator uses canonical resolver = false
Memory Admission = NOT IMPLEMENTED HERE
MemoryRepository = NOT TOUCHED
```

## STOP / OWNER DECISION REQUIRED BEFORE CP-U2

Per owner rule, CP-U2 is not started automatically.

Recommended next checkpoint:

```text
CP-U2 — UNDERSTANDING/MIP CONTRACT EXTENSION
```

Purpose of CP-U2 would be a minimal, versioned extension that preserves frozen V3 semantics without changing NLU ownership or rewriting already-working Authority/MatrixTurnFrame infrastructure.

Before CP-U2 implementation the owner must explicitly approve proceeding with that contract extension after reviewing the CP-U1 findings.

## Explicitly NOT started

```text
CP-U2 implementation = NOT STARTED
CP-U3 adapter = NOT STARTED
Authority orchestrator rewire = NOT STARTED
Memory Admission = NOT STARTED
MemoryRepository integration = NOT STARTED
PersistentConsolidation = NOT STARTED
APK work = NOT STARTED
Reflection = NOT STARTED
other repo writes = false
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = main
work method = docs/MATRIX_ENGINE_WORK_METHOD.md
work-method pivot-rule commit = d23d08159695045dcb1cd2c7ecbf7365ac365795
checkpoint roadmap = docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md
roadmap commit = 140cd6837dacc7e008e84b642291dfa86ae0072a
CP-U1 audit = docs/UNDERSTANDING_V3_LOSSLESS_AUDIT.md
CP-U1 audit commit = fd23526be154863f840ef373bbb0635242d10ff5
CP-U1 = COMPLETE / PASS
CP-U2 = RECOMMENDED / AWAIT OWNER APPROVAL
orchestrator uses canonical resolver = false
Memory writes/admission = NOT TOUCHED
other repos = READ-ONLY
NEXT = OWNER REVIEW OF CP-U1; DO NOT START CP-U2 UNTIL EXPLICIT APPROVAL
```
