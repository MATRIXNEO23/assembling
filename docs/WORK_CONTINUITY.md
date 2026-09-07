# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v81`

## Complete preserved history

The previous complete continuity (`matrix.assembling.continuity.v80`) is preserved byte-for-byte at:

`docs/continuity_archive/ASSEMBLING_V80_e1c495b5f00530e4d3216785849496d709a4fdcc.md`

Previous continuity blob SHA: `e1c495b5f00530e4d3216785849496d709a4fdcc`.

Read that archive for the full historical detail. This v81 file is the current supervisor handoff and overrides older CURRENT/STOP labels where they conflict with the state below.

## Current supervisor state

```text
THIS CHAT = supervisor / architecture / audit / continuity
CHATGPT WORK = active executor for Student-5 Path B
Do not duplicate Work execution in this chat.
```

Owner reported Work started after launching the prepared assignment.

### Active NLU workstream

```text
repo = MATRIXNEO23/matrix-understanding-lab
Work branch = student5-path-b-v3
handoff checkpoint = aca5968bc26f8d4d4ef22327f0d225b9685e1474
assignment = prompts/WORK_STUDENT_5_PATH_B_V3_COMPLETION.md
authorization = OWNER_GIVEN
executor = WORK
supervisor = THIS_CHAT
TASK 2.3 = ACCEPTED / DO NOT REPEAT
current expected task = TASK 2.4 physical V3 BERT/ONNX closure
next on PASS = TASK 3 Path-B training according to assignment gates
```

The supervisor must only evaluate repository-visible checkpoints/results from Work. No claim that TASK 2.4 or training is complete is valid until real evidence appears in the repository.

## Student roles — current canonical decision

```text
Student-5 Path B = primary candidate for Matrix-NLU V3
Student-5 Path A = preserved untaught INT8 runtime probe / read-only
Student-5 pristine 40k = immutable training lineage base
Student-4 v2.2A = preserved comparative/regression baseline / read-only
```

The previous planned real Student-4-only integration proof is **superseded as the active workstream**, not deleted. Its evidence remains in v80. Do not spend further integration effort adapting Student-4 V2 into V3 while Student-5 Path B is being completed. Student-4 remains useful for regression/comparison.

## Student-5 accepted upstream checkpoint

TASK 2.3 accepted from:

```text
commit = 5e49a2d81ecf054a543e3e92f76260fafeca9cc6
verdict = TRAIN_V3_MIGRATION_PASS_WITH_NONBLOCKING_RISKS
TRAIN rows = 3150
claims = 3990
retention = 100%
safe tests = 56/56 PASS
V3 structural violations = 0
target builder = 3150/3150 PASS
invalid pointers = 0
forbidden V2 sentinels = 0
missing required temporal anchors = 0
DEV used = false
Frozen read = false
training executed = false
Path B started at that checkpoint = false
Student-4 changed = false
dataset SHA-256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
```

Known nonblocking TRAIN coverage risks carried into Path B:

```text
BELIEF = absent
COMMAND = absent
REPORT/source attribution = 8 IT claims only
advanced temporal relations = absent
adult withdrawal = 1 IT claim
repeated text instances = 1561
```

These risks must be addressed only through versioned TRAIN-only evidence/augmentation when justified. Never overwrite the canonical V3 TRAIN v1 artifact.

## Mandatory artifact policy

Canonical policy in `matrix-understanding-lab`:

`docs/MODEL_ARTIFACT_PERSISTENCE_POLICY.md`

Binding rules:

```text
never overwrite pristine, Path A, Student-4 or prior Path-B candidates
all meaningful candidates/checkpoints get unique identity + SHA-256 + provenance
binary artifacts must be persistent in repository-owned storage (Git/Git LFS/Release)
GitHub Actions artifact alone is not sufficient preservation
no unnamed latest.onnx replacement
no deletion/pruning without owner authorization
```

## Assembling status while Work runs

```text
repo = MATRIXNEO23/assembling
branch = foundation-closure-f1-f2
HEAD before this continuity checkpoint = b5dc89f26e3cd035fdb5f6106f0c56d0500d436e
PR #23 = DRAFT / NOT MERGED
F1/F2 structured wiring = technically verified for tested fixture scope
REAL language comprehension = NOT YET DEMONSTRATED
Memory = NOT IMPLEMENTED / NOT INTEGRATED
full engine = NOT DEMONSTRATED
phone = BLOCKED
```

No Assembling implementation is to be changed while Work is actively completing Student-5 unless an independent urgent defect requires owner authorization.

## What happens after Work returns results

Order of supervision:

```text
1. Verify Work branch/HEAD and continuity.
2. Verify TASK 2.4 physical evidence: real pristine load, real V3 forward, physical ONNX export/check/load/forward, 16-head contract/output shapes, parity, artifact/checksum persistence.
3. If TASK 2.4 failed, audit root cause and prepare only the necessary fix; do not train around a broken contract.
4. If TASK 2.4 passed and TASK 3 ran, audit every training candidate, DEV/error analysis, TRAIN-only repairs, capacity escalation and preserved checkpoints.
5. Verify final taught FP32 lock before accepting quantization.
6. Verify taught FP32 ONNX vs quantized parity, critical semantic families and IT/EN/ES/code-switch/adult slices.
7. Frozen may be used only once after the candidate/tuning path is locked; no Frozen-driven retraining.
8. Do NOT auto-promote or integrate Student-5 into Assembling merely because lab gates pass.
9. Only after Student-5 is genuinely demonstrated: build the real bridge `raw text -> actual Student-5 runtime -> Understanding V3 -> Context/Retrieval/Coherence -> Authority`.
10. Field-by-field expected vs actual, ambiguous/error cases, first divergence, regression and downstream handoff are mandatory before calling NLU/Understanding functional.
11. Then continue the engine dependency plan, with Memory Foundation as the next major missing durable-state component unless real integration evidence changes dependency order.
```

## Real-proof standard remains binding

```text
REAL INPUT
-> REAL MODULE
-> ACTUAL OUTPUT
-> EXPECTED VS ACTUAL FIELD-BY-FIELD
-> REAL NEXT-MODULE HANDOFF
-> NORMAL + AMBIGUOUS + ERROR CASES
-> ERROR ANALYSIS
-> FIX OBSERVED ROOT CAUSE
-> IDENTICAL RETEST
-> REGRESSION
-> ONLY THEN "WORKING/DEMONSTRATED"
```

Fixture/contract/compile/CI success alone is not functional proof.

## Engine architecture invariants

```text
TypedClaim -> Authority Resolver -> Memory Admission -> MemoryRepository
GGUF/NLU never access persistence directly
text interpreted once; downstream modules do not reparse raw user text
Context immutable/read-only
one owner per mutable state
unknown/unresolved/ambiguous/unavailable/no-match/error remain distinct
contradiction != supersession
Authority identifies semantic contradiction identity
Memory Admission never infers conflict from text difference/shared actor
semantic Memory changes use supersede() preserving lineage
no durable state writes before output validation
missing modules = NOT_WIRED/UNAVAILABLE, never fake defaults
```

Target pipeline remains the complete design recorded in:

`docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`

## Remaining major engine work after real V3 NLU proof

```text
Memory M1-M5
BeliefState final owner
Affective
Relationship
autonomous Intimacy/Consent state
Goal/Intention BDI-lite
Reflection
Opportunity
Decision/Utility
optional tactical BT/FSM
Interaction Manager / Cognitive LOD
Realization Context Supervisor
Prompt Builder
real GGUF adapter
Output Validator
Persistent Consolidation
World ActionIntent/ActionResult
complete Causal Trace
full E2E
Android integration
Moto G56
```

Dependency order may change only from real integration evidence, not convenience.

## Current guards

```text
WORK ACTIVE = Student-5 Path B
DO NOT duplicate Work task in supervisor chat
DO NOT merge PR #23 without explicit owner approval
DO NOT modify Student-4 baseline
DO NOT modify Student-5 Path A
DO NOT overwrite any version/artifact
DO NOT integrate into Assembling until Student-5 lab candidate is locked and genuinely demonstrated
DO NOT call fixture-only work functional
Memory remains NOT IMPLEMENTED
PHONE TEST remains BLOCKED
```

## Exact resume action for supervisor

When owner returns with a Work update, first inspect:

```text
MATRIXNEO23/matrix-understanding-lab
branch student5-path-b-v3
current HEAD
docs/WORK_CONTINUITY_STUDENT_5.md
new reports/artifact registry/releases/checksums
```

Then compare the evidence against `prompts/WORK_STUDENT_5_PATH_B_V3_COMPLETION.md`. Do not rely only on Work's prose summary.
