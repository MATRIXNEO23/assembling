# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v90`

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

Previous complete continuities remain preserved through:

```text
docs/continuity_archive/ASSEMBLING_V89_b5c90c5f4b0b7a32e09fe2a887a612b54506b1fa.md
```

The full dependency-ordered Phase A-F roadmap remains preserved in v82. Nothing in v90 deletes or silently reorders confirmed work.

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
CP38 repair specification = COMPLETE AS DESIGN / BLOCKED_NEEDS_SUPERVISOR_DECISION / VERIFIED
training = NOT STARTED
production = NOT APPROVED
```

Verified CP38:

```text
HEAD = eef2682538dc91a5827bdd3eaff0be202f95a79e
report = reports/STUDENT_5_TRAIN_REPAIR_SPECIFICATION_CP38.md
evidence = reports/evidence/student5-path-b-cp38-repair-spec/
```

CP38 changed only specification/report/evidence/continuity. TRAIN v1, DEV, Frozen, model weights, evaluator/decoder and previous artifacts remain unchanged. No training/remediation was executed.

## 5 — IMMUTABLE ARTIFACTS / DATA

TRAIN v1 baseline remains immutable:

```text
logicalId = student5-matrix-nlu-v3-train-v1
path = data/student5_v3/
rows = 3150
claims = 3990
sha256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
```

Future proposed identity only:

```text
student5-matrix-nlu-v3-train-v2
status = NOT CREATED
```

Pristine, Path A, Student-4 and prior Path-B artifacts remain immutable and recoverable per registry/persistence policy.

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

No training is authorized while these remain unresolved.

## 7 — CP37 / CP38 CURRICULUM STATUS

CP37 established:

```text
TRAIN_CURRICULUM = REPAIR_REQUIRED_BEFORE_TRAINING
```

CP38 translated that into a conservative repair design with 24 repair cards, 11 overlapping annotation groups and 7 unresolved semantic decisions. The specification is complete as a design artifact, but implementation is blocked on those decisions.

No Student-5 regression is claimed. Student-4 regressions are historical evidence and recurrence risk only.

## 8 — SEVEN OPEN SEMANTIC DECISIONS

Supervisor GPT recommendations below are **PROPOSED_ONLY / NOT OWNER-APPROVED YET**.

### D01 — present desire vs future desired action

Recommendation:

```text
Keep claim temporalRelation = CURRENT when the proposition is the person's present desire/state.
Represent the future timing of the desired action only in temporal/object evidence or downstream goal semantics when available.
Use FUTURE only when the desire/commitment proposition itself is explicitly future-scoped.
```

Reason: do not confuse time of wanting with time of the wanted action.

### D02 — perspective in reported propositions

Recommendation:

```text
sourceReferent = linguistic attributor/speaker of the report
perspectiveReferent = viewpoint holder only when wording genuinely frames the embedded proposition from that person's viewpoint
otherwise do not mechanically copy source → perspective; use the contract's unresolved/appropriate value semantics.
```

Reason: source != perspective is a deliberate V3 separation.

### D03 — BELIEF vs DIRECT for opinion wrappers

Recommendation:

```text
Use BELIEF when wording overtly encodes think/believe/opinion/mental stance as the evidential wrapper.
Use DIRECT for plain assertions, including subjective predicates, when no belief wrapper is linguistically expressed.
```

Reason: BELIEF labels wording/evidential presentation, not downstream BeliefState.

### D04 — malformed/reflexive requests

Recommendation:

```text
Do not guess participant roles on malformed examples.
Quarantine/deactivate genuinely malformed rows in future v2 with provenance preserved.
For grammatical reflexive requests, annotate target/subject/owner according to actual semantics case-by-case and retain them as useful contrasts.
```

Reason: malformed gold is worse than less data; valid reflexives are valuable teaching.

### D05 — Spanish metalinguistic negation

Recommendation:

```text
Annotate a negation cue only if the token scopes over the atomic proposition being represented.
If “No” is discourse/metalinguistic correction outside that proposition, do not force NEGATIVE polarity from the token.
Preserve positive polarity when the corrected proposition itself is positive; use CORRECT dialogue act and explicit scope evidence.
```

Reason: cue presence and composed polarity are independent in V3.

### D06 — genuine no-claim observations

Recommendation:

```text
Allow genuine zero-claim observations as boundary-negative training examples when the input contains no proposition supported by V3.
Do not manufacture an unresolved claim merely to avoid an empty claim set.
Use UNKNOWN/abstention only when a proposition exists but a critical field cannot be resolved.
```

Reason: the boundary head needs safe absence teaching; no-claim != unresolved claim.

### D07 — owner≠subject and source≠perspective coverage

Recommendation:

```text
Add only naturally valid constructions where these roles differ.
Never force artificial inequality just to balance labels.
Examples should include possession/goal/consent states owned by one entity while grammatical subject differs, and reports/quotations where attributor and viewpoint holder genuinely differ.
```

Reason: V3 needs independent role learning, but synthetic role distortion would poison semantics.

## 9 — SUPERVISOR RECOMMENDED REPAIR SCOPE

If Alberto approves the seven decisions above, Supervisor recommendation is to authorize a separate bounded implementation task that:

```text
1. preserves TRAIN v1 byte-for-byte;
2. creates NEW student5-matrix-nlu-v3-train-v2 only;
3. corrects confirmed annotation defects identified by CP37/CP38;
4. enumerates and verifies all 628 explicit-English-I cases before correcting them;
5. keeps the 404 valid implicit-subject cases unchanged;
6. individually reviews the 11 code-switch unresolved desires;
7. adds targeted IT/EN/ES/code-switch teaching for COMMAND, BELIEF, REPORT, REQUEST/CORRECT, advanced temporal relations, role uncertainty/ambiguity and legitimate role divergence;
8. broadens adult desire/request/consent/refusal/withdrawal/boundary contrasts without moderation bias;
9. removes/deactivates only verified semantic duplicates or malformed rows, preserving provenance aliases and meaningful contrasts;
10. produces changed/added/deactivated/unchanged manifests, per-file checksums, ordered dataset SHA and family/language/head census;
11. reruns structural/semantic curriculum audit on v2 before any training;
12. does not read DEV/Frozen and does not train.
```

This implementation scope is **NOT AUTHORIZED YET** pending Alberto's approval of the semantic decisions and scope.

## 10 — EXACT SUPERVISOR ACTION NEXT

```text
1. explain the seven decisions to Alberto in plain language;
2. obtain approve/change/reject disposition from Alberto;
3. persist the jointly confirmed decisions;
4. only then prepare one bounded Work assignment to create TRAIN v2;
5. audit TRAIN v2 before training;
6. separately repair/close CP36 DEV/evaluator/calibration blockers;
7. training remains prohibited until both repaired curriculum and Gate-B readiness are Supervisor-accepted.
```

There is currently **NO ACTIVE WORK ASSIGNMENT** after CP38.

## 11 — ASSEMBLING / ENGINE STATE

```text
PR #23 = DRAFT / NOT MERGED
real raw-language end-to-end comprehension = NOT YET DEMONSTRATED
Memory runtime = NOT IMPLEMENTED / NOT INTEGRATED
full engine = NOT DEMONSTRATED
phone = BLOCKED
```

Canonical Memory plan remains `docs/MATRIX_MEMORY_REAL_CONSTRUCTION_PLAN.md` and is unchanged.

## 12 — MASTER DEPENDENCY ORDER

```text
Student-5 curriculum repair + Gate-B closure + Student-5 completion
→ real NLU → Understanding → Context/Retrieval → Authority proof
→ Belief/Authority + Memory
→ later cognitive owners
→ realization/GGUF/world loop
→ E2E
→ Android
→ Moto G56
```

Later roadmap items remain unchanged.

## 13 — PERMANENT SUPERVISOR RULES

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

## 14 — CONTINUITY SELF-SUFFICIENCY TEST

Fresh Supervisor must know active repo/branch/HEAD, accepted checkpoint, no-active-assignment state, blockers, immutable artifacts/data, open decisions, exact next owner/supervisor action, permanent rules and required evidence without asking Alberto to reconstruct history.

## 15 — CONTINUITY MAINTENANCE POLICY

Archive before material rewrite; preserve valid state; correct stale state only; add missing operational information; never silently delete useful history or change architecture/roadmap/gates/confirmed plans without joint decision.