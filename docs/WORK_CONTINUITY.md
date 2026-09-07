# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v91`

## 0 — NEW CHAT / SESSION BOOTSTRAP — READ THIS FIRST

This file is the first operational source for every new Matrix Engine Supervisor GPT chat/session.

```text
NEW CHAT / NEW SESSION
→ READ THIS CONTINUITY FIRST
→ RESTORE EXACT STATE, PLANS, RULES AND NEXT STEP
→ READ ACTIVE WORKSTREAM CONTINUITY WHEN RELEVANT
→ VERIFY LIVE SOURCE-OF-TRUTH STATE
→ ONLY THEN SPEAK / PLAN / ACT
```

Alberto is not the project's memory system. Old chats/recollection are fallback only.

Permanent rules:
- `docs/MATRIX_ENGINE_SESSION_CONTINUITY_AND_SUPERVISION_RULES.md`
- `docs/SUPERVISOR_IDEA_AND_EXECUTION_TRANSPARENCY_RULES.md`
- `docs/MATRIX_ENGINE_WORK_METHOD.md`

## 1 — SOURCE-OF-TRUTH HIERARCHY

```text
1. current canonical continuity
2. active workstream continuity
3. canonical plan/contract/rule docs referenced here
4. live repository branch/HEAD/checkpoint/artifact evidence
5. archived continuities for history
6. old chats/recollection only as fallback
```

Newer verified repo evidence overrides stale prose; continuity must then be checkpointed.

## 2 — PRESERVED HISTORY

Previous complete continuity preserved at:

```text
docs/continuity_archive/ASSEMBLING_V90_213cdc6ac6f81bb70f33e20550be8d813a891d67.md
```

The full dependency-ordered Phase A-F roadmap remains preserved in v82. Nothing in v91 deletes or silently reorders confirmed work.

## 3 — ROLES / AUTHORITY

```text
OWNER = Alberto / final project authority
SUPERVISOR GPT = planner / integrator / reviewer / gate-acceptance authority
CHATGPT WORK = bounded executor
EXTERNAL AGENTS = consultants only
```

Work does not self-assign later work and does not self-accept its own gate.

## 4 — CURRENT STUDENT-5 STATE

Active workstream:

```text
repo = MATRIXNEO23/matrix-understanding-lab
branch = student5-path-b-v3
workstream continuity = docs/WORK_CONTINUITY_STUDENT_5.md
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
CP38 repair specification = COMPLETE AS DESIGN / VERIFIED
training = NOT STARTED
production = NOT APPROVED
```

Verified CP38:

```text
HEAD = eef2682538dc91a5827bdd3eaff0be202f95a79e
report = reports/STUDENT_5_TRAIN_REPAIR_SPECIFICATION_CP38.md
evidence = reports/evidence/student5-path-b-cp38-repair-spec/
```

CP38 changed only specification/report/evidence/continuity. TRAIN v1, DEV, Frozen, model weights, evaluator/decoder and previous artifacts remained unchanged.

## 5 — IMMUTABLE TRAIN BASELINE

```text
logicalId = student5-matrix-nlu-v3-train-v1
path = data/student5_v3/
rows = 3150
claims = 3990
sha256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
status = IMMUTABLE BASELINE
```

The approved future repaired dataset identity is:

```text
logicalId = student5-matrix-nlu-v3-train-v2
status = AUTHORIZED TO CREATE / NOT YET CREATED
```

V1 must remain byte-identical and recoverable.

## 6 — CP36 GATE-B BLOCKERS REMAIN OPEN

```text
1. authorized DEV V3 identity/path/SHA/provenance = MISSING / BLOCKED
2. V3 evaluator incomplete for required gate semantics
3. sourceSpan boundary defects can escape current evaluator
4. mention-table span/entity defects can escape evaluator scoring/rejection
5. forbidden downstream fields can escape evaluator scoring/rejection
6. full exact-claim-set semantics not implemented by current scorer
7. threshold/calibration still depends on legacy evaluator/decoder path
8. connected V3 IT/EN/ES per-family residual analysis = NOT DEMONSTRATED
9. TRAIN↔DEV independent separation = UNKNOWN because DEV V3 is unidentified
```

No training is authorized while these remain unresolved.

## 7 — OWNER + SUPERVISOR APPROVED CP38 SEMANTIC DECISIONS

The seven CP38 semantic decisions are now jointly approved and no longer proposal-only.

### D01 — present desire vs future desired action

```text
Present desire/state = CURRENT.
Future timing of the desired action belongs to temporal/object evidence or downstream goal semantics when representable.
FUTURE applies only when the desire/commitment proposition itself is future-scoped.
```

### D02 — source vs perspective in reports

```text
sourceReferent = linguistic attributor/source.
perspectiveReferent = actual viewpoint holder only when linguistically supported.
Never mechanically copy source → perspective.
Unresolved viewpoint must stay unresolved/UNKNOWN rather than guessed.
```

### D03 — BELIEF vs DIRECT

```text
BELIEF = overt think/believe/opinion/mental-stance wrapper.
DIRECT = plain assertion without such wrapper, even if subjective.
```

### D04 — malformed/reflexive requests

```text
Genuinely malformed rows = quarantine/deactivate in v2 with provenance preserved.
Do not guess participant roles.
Valid grammatical reflexives = annotate case-by-case and preserve as useful contrasts.
```

### D05 — Spanish metalinguistic negation

```text
Negation cue only when it scopes over the represented atomic proposition.
Metalinguistic/discourse “No” does not mechanically force NEGATIVE polarity.
Positive corrected proposition remains POSITIVE with CORRECT act and explicit scope semantics.
```

### D06 — genuine no-claim observations

```text
Allow zero-claim observations when no V3-supported proposition exists.
Use them as valid boundary-negative teaching.
Do not invent unresolved claims to avoid an empty claim set.
UNKNOWN/abstention is for an existing proposition with unresolved critical fields.
```

### D07 — legitimate role divergence

```text
Add only semantically natural cases where owner!=subject and/or source!=perspective.
Never force role inequality merely to balance counts.
Every role difference must be linguistically justified.
```

Supervisor technical judgment: these decisions directly address the CP37/CP38 pedagogy defects without changing the frozen V3 contract or inventing semantics.

## 8 — CURRENT SINGLE WORK ASSIGNMENT PREPARED

Owner authorized implementation of the approved conservative repair.

Repository prompt:

```text
prompts/WORK_STUDENT_5_PATH_B_V3_CREATE_REPAIRED_TRAIN_V2_ONLY.md
prompt commit = ff6ee962bcc7629ace9bcd69263ddbf497376df8
status = PREPARED_NOT_EXECUTED
```

Assignment:

```text
CREATE + VERIFY student5-matrix-nlu-v3-train-v2 ONLY
```

Allowed:
- read immutable TRAIN v1 and CP37/CP38 evidence;
- enumerate all selector groups before edits;
- create a distinct v2 dataset;
- correct confirmed defects using D01-D07;
- add targeted TRAIN-only teaching required by frozen V3;
- quarantine malformed rows with lineage;
- handle only verified redundancy/equivalence deterministically;
- produce manifests/checksums/census/audit evidence.

Hard forbidden:

```text
NO TRAIN v1 mutation
NO DEV read/create/migration/modification
NO Frozen read/evaluation/modification
NO training / optimizer / backprop / fine-tuning
NO model-weight changes
NO evaluator/decoder/threshold/calibration repair
NO quantization / ONNX work
NO Assembling integration
NO production promotion
NO automatic follow-on work
```

Required Work verdict:

```text
TRAIN_V2_REPAIR = PASS
or
TRAIN_V2_REPAIR = BLOCKED
```

PASS means dataset-only readiness for Supervisor review, **not authorization to train**.

## 9 — EXACT SUPERVISOR ACTION NEXT

```text
1. send Work only prompts/WORK_STUDENT_5_PATH_B_V3_CREATE_REPAIRED_TRAIN_V2_ONLY.md
2. when Work returns, read Student-5 continuity and verify branch HEAD
3. inspect v2 dataset bytes/manifests/checksums/lineage/audit
4. verify v1 byte-identical
5. verify D01-D07 applied consistently
6. verify DEV/Frozen untouched and training not run
7. accept/reject TRAIN_V2_REPAIR on evidence
8. if accepted, separately address CP36 DEV/evaluator/calibration blockers
9. do not authorize training until both repaired TRAIN and Gate-B readiness are Supervisor-accepted
10. checkpoint continuity before interruption
```

## 10 — ASSEMBLING / ENGINE STATE

```text
PR #23 = DRAFT / NOT MERGED
real raw-language end-to-end comprehension = NOT YET DEMONSTRATED
Memory runtime = NOT IMPLEMENTED / NOT INTEGRATED
full engine = NOT DEMONSTRATED
phone = BLOCKED
```

Canonical Memory plan remains `docs/MATRIX_MEMORY_REAL_CONSTRUCTION_PLAN.md` and is unchanged.

## 11 — MASTER DEPENDENCY ORDER

```text
Student-5 repaired TRAIN v2
→ Gate-B DEV/evaluator/calibration closure
→ controlled Student-5 training
→ Student-5 completion + Supervisor acceptance
→ real NLU → Understanding → Context/Retrieval → Authority proof
→ Belief/Authority + Memory
→ later cognitive owners
→ realization/GGUF/world loop
→ E2E
→ Android
→ Moto G56
```

Later roadmap items remain unchanged.

## 12 — PERMANENT SUPERVISOR RULES

```text
CONTINUITY FIRST
VERIFY BEFORE CLAIMS
OWNER IS NOT PROJECT MEMORY
CONFIRMED PLANS AUTO-PERSIST
IDEA != APPROVED PLAN
PRESERVE STABLE WORK
DEEP PRIOR-ART BEFORE REDESIGN
EXTERNAL AGENTS = CONSULTANTS
SUPERVISOR RETAINS PLANNING/ACCEPTANCE
WORK GETS ONE CURRENT BOUNDED ASSIGNMENT
NO SILENT STOP / NO SENTINEL-ONLY REPORT
NO OVERWRITE
REAL TEST TRACE REQUIRED
NOTED != PREPARED != EXECUTED != VERIFIED != ACCEPTED
```

## 13 — CONTINUITY SELF-SUFFICIENCY TEST

Fresh Supervisor must know active repo/branch/HEAD, accepted checkpoints, current prepared assignment, blockers, immutable artifacts/data, approved semantic decisions, exact next action, permanent rules and required evidence without asking Alberto to reconstruct history.

## 14 — CONTINUITY MAINTENANCE POLICY

Archive before material rewrite; preserve valid state; correct stale state only; add missing operational information; never silently delete useful history or change architecture/roadmap/gates/confirmed plans without joint decision.
