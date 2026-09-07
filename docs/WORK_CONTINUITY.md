# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v88`

## 0 — NEW CHAT / SESSION BOOTSTRAP — READ THIS FIRST

This file is the **first operational source** for every new Matrix Engine Supervisor GPT chat/session.

```text
NEW CHAT / NEW SESSION
→ READ THIS CONTINUITY FIRST
→ RESTORE EXACT STATE, PLANS, RULES AND NEXT STEP
→ READ ACTIVE WORKSTREAM CONTINUITY WHEN RELEVANT
→ VERIFY LIVE SOURCE-OF-TRUTH STATE
→ ONLY THEN SPEAK / PLAN / ACT
```

Alberto is not the project's memory system. Old conversations/chat memory are fallback only for genuinely missing detail.

Permanent session/supervision rules:

```text
docs/MATRIX_ENGINE_SESSION_CONTINUITY_AND_SUPERVISION_RULES.md
```

Idea governance / execution-transparency rules:

```text
docs/SUPERVISOR_IDEA_AND_EXECUTION_TRANSPARENCY_RULES.md
```

---

## 1 — SOURCE-OF-TRUTH HIERARCHY

Use this order on resume or when two statements conflict:

```text
1. this current canonical continuity
2. active workstream continuity
3. canonical plan / contract / rule docs referenced here
4. live repository branch / HEAD / checkpoint / artifact evidence
5. archived continuities for history
6. old chats / recollection only as fallback
```

A stale continuity statement never overrides newer verified repository evidence; update continuity at the next Supervisor checkpoint.

---

## 2 — PRESERVED HISTORY

Previous complete continuities remain preserved:

- `docs/continuity_archive/ASSEMBLING_V80_e1c495b5f00530e4d3216785849496d709a4fdcc.md`
- `docs/continuity_archive/ASSEMBLING_V81_7638c5b4f09095dff973f7fb88ace98c12e995fb.md`
- `docs/continuity_archive/ASSEMBLING_V82_ff92e9dc6839dd6d3da7b06af0977658174e82ed.md`
- `docs/continuity_archive/ASSEMBLING_V83_08ca1612b899dc7ecfddb72873ef07e00ffca177.md`
- `docs/continuity_archive/ASSEMBLING_V84_07b6ee1c544706f8fb34d9cd422eb7855955fcc7.md`
- `docs/continuity_archive/ASSEMBLING_V85_5d504b5b45aaa56e0692149e268c8081c28ee098.md`
- `docs/continuity_archive/ASSEMBLING_V86_40029e96581577d3e15290b67c42e81b0c052649.md`
- `docs/continuity_archive/ASSEMBLING_V87_d077d0f1b713687f6681c8fccf003886819ce9a8.md`

The full dependency-ordered Phase A-F roadmap remains preserved in v82. Nothing in v88 deletes or silently reorders confirmed work.

---

## 3 — ROLES / AUTHORITY

```text
OWNER = Alberto / final project authority
SUPERVISOR GPT = planner / integrator / reviewer / gate-acceptance authority
CHATGPT WORK = bounded executor
EXTERNAL AGENTS (Copilot / Gemini / Qwen / others) = consultants only
```

Work does not self-assign later work and does not self-accept its own gate.

---

## 4 — CURRENT STUDENT-5 STATE

Active workstream:

```text
repo = MATRIXNEO23/matrix-understanding-lab
branch = student5-path-b-v3
workstream continuity = docs/WORK_CONTINUITY_STUDENT_5.md
artifact registry = docs/STUDENT_ARTIFACT_REGISTRY.md
```

Latest accepted/executed chain:

```text
TASK 2.3 = ACCEPTED / DO NOT REPEAT
A1 pristine acquisition = PASS
A2 real BERT/V3 forward = PASS
A3 physical ONNX/check/load/forward/parity = PASS
A4 durable publication = PASS / SUPERVISOR ACCEPTED
CP36 Gate-B readiness = BLOCKED / VERIFIED
CP37 TRAIN curriculum audit = REPAIR_REQUIRED_BEFORE_TRAINING / SUPERVISOR ACCEPTED
training = NOT STARTED
production = NOT APPROVED
```

Verified CP37 checkpoint:

```text
HEAD = e81a2ae9e1d60c6af61a7d8d1baabed9171f3166
report = reports/STUDENT_5_TRAIN_CURRICULUM_AUDIT_CP37.md
evidence = reports/evidence/student5-path-b-cp37-train-audit/
```

CP37 changed only report/evidence/continuity. TRAIN, DEV, Frozen, model weights and prior artifacts were not modified; no training/remediation/evaluator repair was executed.

Do not infer Work progress merely from UI text or GitHub Actions. GitHub Actions and ChatGPT Work are separate execution surfaces.

---

## 5 — IMMUTABLE STUDENT-5 ARTIFACTS / RECOVERY

Primary pristine base remains immutable:

```text
releaseId = 383143636
assetId = 545406840
asset = student5-minilm-phase-a-pruned-40k.zip
archive sha256 = 7804bfb245b71df9b835fff7ee00f6ec887e019772ae34c82d269d821d587191
model sha256 = d6e45891d1e0ec4ed023caaeb17c0dd0ae80a93b8bf877d275310b7f2837efa2
```

Accepted untrained Gate-A Path-B runtime reference:

```text
releaseId = 383886129
assetId = 548329150
asset = student5-path-b-v3-untrained-gate-a-cp35-20260907.zip
bytes = 176837978
sha256 = dfe20ae4cfa49656f557872f6ba2afeabef6caea06390f943ae8cd0945a37d71
fresh recovery = PASS
ZIP integrity = PASS
internal SHA256SUMS = 17/17 PASS
```

Student roles:

```text
Student-5 Path B = ACTIVE primary Matrix-NLU V3 candidate
Student-5 pristine 40k = immutable Path-B base
Student-5 Path A = REFERENCE untaught runtime probe / immutable
Student-4 v2.2A = REFERENCE comparative/regression baseline / immutable
```

Never overwrite any model/data/artifact/checkpoint version. Full recovery details remain in Student-5 continuity and artifact registry.

---

## 6 — CP36 GATE-B READINESS BLOCKERS REMAIN OPEN

Verified TRAIN identity:

```text
logicalId = student5-matrix-nlu-v3-train-v1
path = data/student5_v3/
rows = 3150
claims = 3990
sha256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
DEV copied into TRAIN = false
Frozen read = false
```

Gate-B remains blocked because:

```text
1. authorized DEV V3 identity/path/SHA/provenance = MISSING / BLOCKED
2. V3 evaluator incomplete for required gate semantics
3. sourceSpan boundary defects can escape current evaluator
4. mention-table span/entity defects can escape current evaluator
5. forbidden downstream fields can escape evaluator scoring/rejection
6. full exact-claim-set semantics not implemented by current scorer
7. threshold/calibration still depends on legacy evaluator/decoder path
8. connected V3 IT/EN/ES per-family residual analysis = NOT DEMONSTRATED
9. TRAIN↔DEV independent separation = UNKNOWN because DEV V3 is unidentified
```

No training is authorized while these blockers remain unresolved.

---

## 7 — CP37 CURRICULUM VERDICT / OBSERVED DEFECTS

Supervisor GPT accepts the CP37 evidence-backed verdict:

```text
TRAIN_CURRICULUM = REPAIR_REQUIRED_BEFORE_TRAINING
```

This does **not** revoke TASK 2.3 structural acceptance and does **not** prove Student-5 regression. It establishes that training now would teach an incomplete/biased V3 curriculum and therefore is not authorized.

Observed curriculum problems requiring disposition include:

```text
COMMAND = zero support
BELIEF = zero support
advanced temporal relations = zero support
PAST = extremely sparse / language-skewed
REPORT = extremely sparse and IT-only
perspective always speaker
owner always equals subject
no UNKNOWN/AMBIGUOUS role teaching
no meaningful multi-context-entity choice
code-switch concentrated in unresolved/desire-like templates
adult desire/refusal/withdrawal coverage uneven and language-skewed
substantial exact/normalized repetition/template concentration
specific annotation defects/inconsistencies found for temporal spans, role/viewpoint semantics, explicit-English subject spans and malformed multi-participant requests
```

Student-4 historical regressions make recurrence on negation, temporal, referents/report, correction/request, ownership/span and IT/ES a **plausible risk**, not a demonstrated Student-5 regression or proven curriculum-causation claim.

The immutable `student5-matrix-nlu-v3-train-v1` remains preserved unchanged as the audit baseline.

---

## 8 — CURRENT DECISION POINT / NO ACTIVE FOLLOW-ON EXECUTION

There is currently no authorized remediation or training execution.

Supervisor recommendation:

```text
NEXT = TRAIN REPAIR SPECIFICATION ONLY
```

Purpose of the proposed next bounded task:

- convert CP37 findings into a precise repair specification;
- define which existing rows are annotation fixes vs preserved valid examples;
- define missing semantic/role/language coverage to add in a NEW TRAIN version;
- define redundancy handling without deleting meaningful contrasts;
- preserve V1 immutably;
- define acceptance checks for the repaired TRAIN before any training;
- do not create/modify TRAIN yet;
- do not read DEV or Frozen;
- do not repair evaluator/calibration in the same task;
- do not train.

This recommendation is **PROPOSED_ONLY / NOT YET ASSIGNED**. Alberto + Supervisor must decide before any TRAIN mutation or remediation execution.

---

## 9 — EXACT SUPERVISOR ACTION NEXT

```text
1. tell Alberto CP37 is verified and accepted
2. explain plainly: current TRAIN should not be used for training as-is
3. recommend a bounded TRAIN repair-specification-only task
4. obtain joint decision before any TRAIN mutation
5. if approved, prepare only that specification task for Work
6. after specification review/acceptance, decide separately whether to create a NEW repaired TRAIN version
7. Gate-B DEV/evaluator/calibration blockers remain a separate later bounded workstream
8. training remains prohibited until repaired curriculum and Gate-B readiness are both Supervisor-accepted
```

---

## 10 — ASSEMBLING / ENGINE STATE

```text
repo = MATRIXNEO23/assembling
branch = foundation-closure-f1-f2
PR #23 = DRAFT / NOT MERGED
F1/F2 structured wiring = verified only for tested structured cases
real raw-language end-to-end comprehension = NOT YET DEMONSTRATED
Memory runtime = NOT IMPLEMENTED / NOT INTEGRATED
full engine = NOT DEMONSTRATED
phone = BLOCKED
```

Canonical architecture/design sources:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
docs/MATRIX_INTERMODULE_PROTOCOL.md
docs/MATRIX_ENGINE_WORK_METHOD.md
```

Canonical Memory construction plan:

```text
docs/MATRIX_MEMORY_REAL_CONSTRUCTION_PLAN.md
status = CANONICAL CONSTRUCTION PLAN / NOT YET IMPLEMENTED
```

Memory implementation later follows M1→M5 from that plan. Do not redesign it from recollection.

---

## 11 — MASTER DEPENDENCY ORDER

```text
Student-5 / Matrix-NLU V3 completion + Supervisor acceptance
→ real NLU → Understanding → Context/Retrieval → Authority proof
→ Belief/Authority finalization + Memory M1-M3
→ Memory/Belief convergence
→ Persistent Consolidation
→ Memory M5 E2E
→ Affective / Relationship / Intimacy / Goal / Reflection / Decision
→ realization / GGUF / validation / world loop
→ full automatic E2E
→ Android
→ Moto G56
```

Nothing in CP37 changes the later roadmap; it inserts required curriculum repair before Student-5 training because that dependency is now evidence-backed.

---

## 12 — PERMANENT SUPERVISOR RULES

```text
CONTINUITY FIRST ON EVERY NEW CHAT
VERIFY LIVE SOURCE OF TRUTH BEFORE OPERATIONAL CLAIMS
OWNER IS NOT THE PROJECT MEMORY SYSTEM
CONFIRMED CONSTRUCTION PLAN → AUTO-PERSIST + CONTINUITY LINK
IDEA/HYPOTHESIS != CONFIRMED PLAN
PRESERVE VALID/STABLE WORK BEFORE EXPERIMENTATION
DEEP PRIOR-ART CHECK BEFORE REDESIGN
EXTERNAL AGENTS = CONSULTANTS ONLY
SUPERVISOR GPT RETAINS PLANNING + ACCEPTANCE RESPONSIBILITY
WORK EXECUTES; WORK DOES NOT SELF-ASSIGN NEXT TASK
WORK GETS CURRENT ASSIGNMENT, NOT FUTURE ROADMAP
ACTIVELY MONITOR WORK STATE + RESULTS
ONE BOUNDED IMMEDIATE ASSIGNMENT AT A TIME
ORDINARY SUBPROBLEMS INSIDE SCOPE MAY BE SOLVED AUTONOMOUSLY BY WORK
WORK MUST REPORT PASS/BLOCKED; NO SILENT STOP
NO SENTINEL-ONLY WORK REPORTS
DO NOT OVERWRITE MODEL/DATA/ARTIFACT VERSIONS
DO NOT MERGE PR #23 WITHOUT OWNER APPROVAL
EVERY REAL TEST = INPUT → REAL MODULE → OUTPUT → MIP → HANDOFF → NEXT OUTPUT
RECORD LOST / CHANGED / INVENTED FIELDS + FIRST DIVERGENCE
NOTED != PREPARED != EXECUTED != VERIFIED != SUPERVISOR_ACCEPTED
CONTINUITY MAINTENANCE IS CONSERVATIVE: ARCHIVE, PRESERVE, CORRECT STALE, ADD MISSING
```

---

## 13 — CONTINUITY SELF-SUFFICIENCY TEST

A fresh Supervisor GPT must be able to answer from this continuity + referenced canonical sources, without asking Alberto to reconstruct history:

```text
[ ] active repo/workstream
[ ] relevant branch + latest verified checkpoint/HEAD
[ ] owner / supervisor / executor roles
[ ] last Supervisor-accepted result
[ ] current bounded assignment and exact prompt, or explicit NO ACTIVE ASSIGNMENT
[ ] execution status: prepared/executed/verified/accepted
[ ] explicit NOT-DONE items
[ ] blockers/open decisions
[ ] immutable artifacts and recovery pointers
[ ] confirmed construction plans and canonical paths
[ ] exact immediate Supervisor action
[ ] permanent rules constraining that action
[ ] evidence required before next PASS claim
```

If any item is missing or ambiguous, continuity is defective and must be repaired before substantial new work relies on it.

---

## 14 — CONTINUITY MAINTENANCE POLICY

When improving this file:

```text
1. archive the current version first when rewriting materially
2. preserve all still-valid decisions/rules/state
3. correct only stale or contradictory state
4. add genuinely missing operational information
5. mark superseded/historical items rather than silently deleting useful history
6. do not change architecture/roadmap/gates/confirmed plans merely to simplify prose
7. any real project-decision change is PROPOSAL ONLY until Alberto + Supervisor decide together
```

This v88 update records verified CP37 and the resulting decision point. It does not authorize TRAIN mutation, DEV/Frozen access, evaluator repair or training.
