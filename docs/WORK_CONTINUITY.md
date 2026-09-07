# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v89`

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

Permanent rules:

- `docs/MATRIX_ENGINE_SESSION_CONTINUITY_AND_SUPERVISION_RULES.md`
- `docs/SUPERVISOR_IDEA_AND_EXECUTION_TRANSPARENCY_RULES.md`
- `docs/MATRIX_ENGINE_WORK_METHOD.md`

---

## 1 — SOURCE-OF-TRUTH HIERARCHY

```text
1. current canonical continuity
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
- `docs/continuity_archive/ASSEMBLING_V88_551ae9277534761abe1c7a2f3795ac4d119ebae9.md`

The full dependency-ordered Phase A-F roadmap remains preserved in v82. Nothing in v89 deletes or silently reorders confirmed work.

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

Accepted/executed chain:

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

Verified CP37:

```text
HEAD = e81a2ae9e1d60c6af61a7d8d1baabed9171f3166
report = reports/STUDENT_5_TRAIN_CURRICULUM_AUDIT_CP37.md
evidence = reports/evidence/student5-path-b-cp37-train-audit/
```

CP37 changed only report/evidence/continuity. TRAIN, DEV, Frozen, model weights and prior artifacts remained unchanged.

Current Student-5 branch after Supervisor assignment preparation:

```text
HEAD = 6cd1fac17bdceca742a8bf2932e0872ee5907601
prompt = prompts/WORK_STUDENT_5_PATH_B_V3_TRAIN_REPAIR_SPECIFICATION_ONLY.md
prompt state = PREPARED_IN_REPOSITORY / OWNER-AUTHORIZED
execution result = NOT_VERIFIED / NO WORK RESULT YET
```

Do not infer Work progress from UI text or GitHub Actions. GitHub Actions and ChatGPT Work are separate execution surfaces.

---

## 5 — IMMUTABLE ARTIFACTS / DATA

Primary pristine base:

```text
releaseId = 383143636
assetId = 545406840
asset = student5-minilm-phase-a-pruned-40k.zip
archive sha256 = 7804bfb245b71df9b835fff7ee00f6ec887e019772ae34c82d269d821d587191
model sha256 = d6e45891d1e0ec4ed023caaeb17c0dd0ae80a93b8bf877d275310b7f2837efa2
```

Accepted untrained Gate-A Path-B reference:

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

TRAIN v1 baseline remains immutable:

```text
logicalId = student5-matrix-nlu-v3-train-v1
path = data/student5_v3/
rows = 3150
claims = 3990
sha256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
```

Student roles:

```text
Student-5 Path B = ACTIVE primary Matrix-NLU V3 candidate
Student-5 pristine 40k = immutable Path-B base
Student-5 Path A = REFERENCE untaught runtime probe / immutable
Student-4 v2.2A = REFERENCE comparative/regression baseline / immutable
```

Never overwrite any model/data/artifact/checkpoint version.

---

## 6 — CP36 GATE-B BLOCKERS REMAIN OPEN

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

## 7 — CP37 CURRICULUM VERDICT

Supervisor GPT accepted:

```text
TRAIN_CURRICULUM = REPAIR_REQUIRED_BEFORE_TRAINING
```

This does not revoke TASK 2.3 structural acceptance and does not prove Student-5 regression. It establishes that the current TRAIN should not be used for fitting as-is.

Observed issues requiring repair/specification include:

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
confirmed/adjudication-required annotation issues for temporal spans, role/viewpoint semantics, explicit-English subject spans and malformed multi-participant requests
```

Student-4 recurrence on negation, temporal, referents/report, correction/request, ownership/span and IT/ES is a plausible historical risk, not proven Student-5 damage or proven curriculum causation.

---

## 8 — CURRENT SINGLE WORK ASSIGNMENT

Owner + Supervisor approved the **design-only** next step.

```text
assignment = STUDENT-5 V3 TRAIN REPAIR SPECIFICATION ONLY
prompt = prompts/WORK_STUDENT_5_PATH_B_V3_TRAIN_REPAIR_SPECIFICATION_ONLY.md
starting Student-5 HEAD = e81a2ae9e1d60c6af61a7d8d1baabed9171f3166
prompt commit/current branch HEAD = 6cd1fac17bdceca742a8bf2932e0872ee5907601
status = PREPARED_NOT_EXECUTED / awaiting Work result
```

Purpose:

- translate CP37 evidence into an issue-by-issue conservative repair specification;
- separate confirmed annotation defects from sparse coverage and unresolved semantic-policy decisions;
- specify what existing valid data must be preserved;
- specify missing V3 teaching to add only in a future NEW dataset version;
- specify redundancy handling without blind deletion;
- propose future dataset lineage/versioning and acceptance checks;
- provide Student-4 recurrence safeguards;
- return unresolved semantic decisions to Supervisor/Owner instead of guessing.

Hard scope:

```text
NO TRAIN mutation
NO new TRAIN shards
NO augmentation execution
NO row relabel/delete execution
NO DEV read/create/migration
NO Frozen read
NO training / optimizer / backprop / fine-tuning
NO evaluator/decoder/threshold repair
NO quantization
NO Assembling integration
NO automatic follow-on work
```

Required verdict:

```text
REPAIR_SPECIFICATION = READY_FOR_SUPERVISOR_OWNER_REVIEW
or
REPAIR_SPECIFICATION = BLOCKED_NEEDS_SUPERVISOR_DECISION
```

Work must return a substantive report beginning:

```text
Supervisor GPT — Student-5 TRAIN Repair Specification Report
```

Sentinel-only completion is non-compliant.

---

## 9 — EXACT SUPERVISOR ACTION WHEN WORK RETURNS

```text
1. read Student-5 continuity and verify current branch HEAD
2. inspect the repair-spec report/evidence against the exact prompt scope
3. verify no TRAIN/DEV/Frozen/model/evaluator mutation occurred
4. separate confirmed fixes, recommendations and unresolved semantic-policy choices
5. explain the proposed repair to Alberto in plain language
6. decide together which repair items are approved
7. only after joint approval prepare one bounded task to create a NEW repaired TRAIN version
8. preserve TRAIN v1 immutably
9. separately resolve CP36 DEV/evaluator/calibration blockers before training
10. training remains prohibited until repaired curriculum + Gate-B readiness are Supervisor-accepted
11. checkpoint continuity before interruption
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

Canonical architecture sources:

- `docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`
- `docs/MATRIX_INTERMODULE_PROTOCOL.md`
- `docs/MATRIX_ENGINE_WORK_METHOD.md`

Canonical Memory plan:

```text
docs/MATRIX_MEMORY_REAL_CONSTRUCTION_PLAN.md
status = CANONICAL CONSTRUCTION PLAN / NOT YET IMPLEMENTED
```

---

## 11 — MASTER DEPENDENCY ORDER

```text
Student-5 curriculum repair + Gate-B closure + Matrix-NLU V3 completion + Supervisor acceptance
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

The curriculum repair is inserted before training by evidence-backed dependency; later roadmap items remain unchanged.

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

A fresh Supervisor GPT must know without asking Alberto to reconstruct history:

```text
[ ] active repo/workstream
[ ] relevant branch + latest verified checkpoint/HEAD
[ ] roles
[ ] last Supervisor-accepted result
[ ] current bounded assignment and exact prompt
[ ] execution state
[ ] explicit NOT-DONE items
[ ] blockers/open decisions
[ ] immutable artifacts/data and recovery pointers
[ ] confirmed construction plans
[ ] exact immediate Supervisor action
[ ] permanent rules
[ ] evidence required before next PASS claim
```

If any item is missing or ambiguous, continuity is defective and must be repaired before substantial new work relies on it.

---

## 14 — CONTINUITY MAINTENANCE POLICY

```text
1. archive current version before material rewrite
2. preserve still-valid decisions/rules/state
3. correct only stale/contradictory state
4. add genuinely missing operational information
5. mark historical/superseded items instead of silently deleting useful history
6. do not change architecture/roadmap/gates/confirmed plans for prose convenience
7. real project-decision changes remain proposal-only until Alberto + Supervisor decide together
```

This v89 update records the owner-authorized repair-specification-only assignment. It does not authorize dataset mutation, DEV/Frozen access, evaluator repair or training.
