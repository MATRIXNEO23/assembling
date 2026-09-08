# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v82`

## Complete preserved history

Previous complete continuities are preserved byte-for-byte:

- `docs/continuity_archive/ASSEMBLING_V80_e1c495b5f00530e4d3216785849496d709a4fdcc.md`
- `docs/continuity_archive/ASSEMBLING_V81_7638c5b4f09095dff973f7fb88ace98c12e995fb.md`

This v82 file is the current operational index. Historical detail remains authoritative for completed checkpoints, artifacts, defects and decisions, but older CURRENT/STOP labels are superseded by the state below.

---

# 0 — CANONICAL RULE FOR ORDERING WORK

The project backlog is **not a stack** and not a simple chronological list.

A new point must never be placed at the top or bottom merely because it is new. It is inserted where it belongs in the dependency graph.

For every new point, the supervisor must determine:

```text
1. WHAT it changes or requires.
2. WHICH module/workstream owns it.
3. WHICH existing items it depends on.
4. WHICH later items depend on it.
5. WHETHER it blocks current work or can wait.
6. WHETHER it can run in parallel without shared mutable state or duplicated work.
7. WHICH gate proves it complete.
8. WHICH existing point it modifies, replaces, splits or leaves unchanged.
```

Insertion rule:

```text
AFTER its last real prerequisite
BEFORE the first item that depends on it
IN PARALLEL only when there is no causal/write dependency
NEVER by recency alone
```

If a new point changes an existing item, the old item is not silently deleted. Mark the relationship explicitly:

```text
UNCHANGED
EXTENDED_BY <id>
BLOCKED_BY <id>
SUPERSEDED_BY <id>
SPLIT_INTO <ids>
MERGED_INTO <id>
```

Nothing disappears from the plan without an explicit disposition.

Status vocabulary:

```text
ACTIVE       = being executed now
READY        = prerequisites satisfied; may start when authorized
WAITING      = waiting for an external/current work result
BLOCKED      = prerequisite or defect prevents start/continuation
PARALLEL     = safe to execute alongside another listed item
LATER        = valid requirement whose prerequisites are not yet met
DONE         = completed with the required evidence
SUPERSEDED   = replaced by a newer explicit decision; history preserved
REFERENCE    = preserved baseline/comparison, not active implementation
```

Priority is therefore derived primarily from **dependency and blocker impact**, then risk, then value/cost. A newer idea does not automatically become higher priority.

---

# 1 — PERMANENT CROSS-CUTTING CONSTRAINTS

These are not queue items. They apply to every item below and therefore do not displace work in the execution order.

## 1.1 Functional proof

A module is not called working because it compiles or passes fixture/DTO tests.

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
-> ONLY THEN WORKING/DEMONSTRATED
```

## 1.2 No incomplete/hypothetical implementation

No placeholder solution, fake output, TODO-as-implementation or unfinished/non-compiling code may be presented as implementation.

## 1.3 Complete module design before implementation

Before substantial module implementation/redesign, fix responsibility, owner, state, inputs, outputs, dependencies, errors/ambiguity, diagnostics, mobile/performance constraints and downstream handoff.

## 1.4 Deep research/reuse

Before substantial implementation/redesign, research mature algorithms/libraries/reference code; verify quality, license, provenance and Android/offline cost; classify `DIRECT_REUSE / ADAPT / REIMPLEMENT / REFERENCE_ONLY / REJECT`.

## 1.5 Persistence/non-overwrite

Model/data/artifact versions are immutable once registered. Preserve meaningful checkpoints persistently with unique ID, provenance and SHA-256. Never overwrite pristine, Path A, Student-4 or prior Path-B candidates.

## 1.6 Ownership and persistence boundaries

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL
!= RELATE != CONSENT != WANT != DECIDE != EXPRESS
```

One canonical owner per mutable state. No durable writes before output/action validation. NLU/GGUF never access persistence directly. Downstream modules never reparse raw user language.

## 1.7 Chat/Work coordination

```text
THIS CHAT = supervisor / architecture / audit / evidence / continuity
CHATGPT WORK = substantial authorized execution
```

When Work is active, supervisor must not duplicate its implementation.

---

# 2 — CURRENT EXECUTION STATE

## 2.1 ACTIVE — Student-5 Path B in Work

```text
repo = MATRIXNEO23/matrix-understanding-lab
branch = student5-path-b-v3
handoff checkpoint = aca5968bc26f8d4d4ef22327f0d225b9685e1474
assignment = prompts/WORK_STUDENT_5_PATH_B_V3_COMPLETION.md
executor = WORK
supervisor = THIS_CHAT
TASK 2.3 = ACCEPTED / DO NOT REPEAT
current expected gate = TASK 2.4 physical V3 BERT/ONNX closure
```

Owner reported Work started. The supervisor does not claim progress beyond repository-visible evidence.

Student roles:

```text
Student-5 Path B = ACTIVE primary Matrix-NLU V3 candidate
Student-5 pristine 40k = immutable Path-B base
Student-5 Path A = REFERENCE untaught INT8 runtime probe
Student-4 v2.2A = REFERENCE comparative/regression baseline
```

Accepted TASK 2.3 evidence:

```text
commit = 5e49a2d81ecf054a543e3e92f76260fafeca9cc6
TRAIN rows = 3150
claims = 3990
safe tests = 56/56 PASS
V3 structural violations = 0
target builder = 3150/3150 PASS
DEV used = false
Frozen read = false
training executed = false at checkpoint
dataset SHA-256 = 1118a900026f48cfcc290c3f9dc52a1a019a6a1f4761661f326c33770523140e
```

Known TRAIN coverage risks remain visible: BELIEF/COMMAND absent; REPORT only 8 IT claims; advanced temporal relations absent; adult withdrawal one IT claim; inherited repeated surfaces.

## 2.2 WAITING — Assembling implementation

```text
repo = MATRIXNEO23/assembling
branch = foundation-closure-f1-f2
PR #23 = DRAFT / NOT MERGED
F1/F2 structured wiring = verified only for tested structured cases
real language comprehension = NOT YET DEMONSTRATED
Memory = NOT IMPLEMENTED / NOT INTEGRATED
full engine = NOT DEMONSTRATED
phone = BLOCKED
```

No unrelated Assembling implementation should run while Work is completing the active NLU dependency.

---

# 3 — MASTER EXECUTION ORDER

This is the canonical operational order. New work must be inserted into this structure according to section 0.

## PHASE A — COMPLETE AND LOCK REAL MATRIX-NLU V3

### A1 — TASK 2.4 physical Student-5 V3 model/ONNX gate
**Status: ACTIVE in Work**

Prerequisites: TASK 2.3 accepted; pristine 40k and V3 contract preserved.

Required proof:

```text
real pristine BERT/tokenizer load
real V3 16-head model construction
real forward
physical ONNX export
ONNX checker/load/forward
all expected V3 outputs including sourceReferent
PyTorch-vs-ONNX parity
persistent artifact/checksum/provenance
```

Failure here blocks all training; do not train around a broken runtime/export contract.

### A2 — Path-B Matrix-NLU V3 training/error-mining
**Status: WAITING on A1 PASS; same authorized Work workstream**

Start from pristine FP32, never Path A INT8.

Capacity escalation only as evidence requires:

```text
frozen backbone + V3 heads
-> stronger heads/adapters if needed
-> unfreeze 1-2 layers if needed
-> unfreeze 3-4 layers if needed
-> full fine-tuning only as last resort
```

Every meaningful candidate must be versioned/preserved before escalation.

### A3 — Taught FP32 candidate lock
**Status: WAITING on A2 quality gates**

Requires DEV evidence with per-language/per-family error analysis and no hidden critical systematic failure.

### A4 — Taught ONNX export + quantized candidate(s)
**Status: WAITING on A3**

Compare taught FP32 vs quantized on size/runtime and semantic parity. Critical heads/families may not regress merely for size.

### A5 — Final Frozen evaluation
**Status: WAITING on locked tuning path**

Frozen opened only after all tuning/candidate decisions are locked. No Frozen-driven retraining or threshold tuning.

### A6 — Supervisor acceptance of Student-5 lab result
**Status: WAITING on Work output**

Supervisor verifies branch/HEAD, reports, metrics, artifact registry, persistent binaries, checksums, non-overwrite guards and exact gate evidence. Work prose alone is insufficient.

**Phase-A exit condition:** Student-5 is a preserved, trained, physically runnable V3 candidate with evidence sufficient to attempt real engine integration. This does **not** yet mean NLU/Understanding is demonstrated inside Matrix Engine.

---

## PHASE B — REAL NLU -> UNDERSTANDING -> AUTHORITY PROOF

### B1 — Real Student-5 runtime bridge into canonical Understanding V3
**Status: WAITING on A6**

```text
raw text
-> actual Student-5 runtime/model
-> actual decoder
-> canonical Understanding V3 / TypedClaim[]
```

No fabricated fields, no fallback to Student-4 V2 semantics.

### B2 — Field-by-field real semantic evaluation
**Status: WAITING on B1**

Required IT/EN/ES plus code-switch/critical families, normal, ambiguous and malformed/error cases.

### B3 — Real downstream handoff
**Status: WAITING on B1/B2**

```text
Understanding V3
-> ContextSnapshot
-> Retrieval
-> narrow Coherence
-> Authority/Belief Resolution
```

Record first divergence, lost/changed/invented fields and fail-closed behavior.

### B4 — Fix observed integration defects + identical retest/regression
**Status: WAITING on B2/B3 findings**

No speculative rewrites. Fix only demonstrated defects and rerun identical cases plus regression.

### B5 — F1/F2 closure / PR #23 decision
**Status: WAITING on B4**

Only after the real path is proven should the structured F1/F2 branch be reconciled/merged or amended. PR #23 remains draft until explicit owner approval.

**Phase-B exit condition:** raw language has been demonstrated through the actual NLU/Understanding path into Authority with measured errors, fixes and regression. Only here may NLU/Understanding be called demonstrated for the proven scope.

---

## PHASE C — EPISTEMIC + DURABLE MEMORY FOUNDATION

The two early lanes below may proceed in parallel **after Phase B stabilizes the V3/Authority identities**, because they own separate state. They converge before durable cognitive writes.

### C1 — BeliefState / Authority final owner
**Status: LATER; parallel lane after Phase B**

Finalize belief state, epistemic classes, confidence/source reliability separation, unresolved/conflicted behavior and proposal/commit boundary.

### C2 — Memory M1-M3 foundation
**Status: LATER; parallel lane after Phase B**

```text
M1 final MemoryRecord/schema/query model
M2 Room/Repository/Admission atomic persistence + lineage/supersede
M3 retrieval/index: hard filters -> FTS/BM25 -> optional benchmarked vector/hybrid -> rerank
```

Preserve historical Memory guarantees: atomic rollback, metadata-only reinforcement, semantic changes through `supersede()`, explicit contradiction identity, protected lineage delete, restart persistence.

### C3 — Memory preflight + Belief/Authority convergence
**Status: WAITING on C1 + C2**

Canonical semantic flow remains:

```text
TypedClaim -> Authority Resolver -> Memory Admission -> MemoryRepository
```

Authority identifies contradiction identity; Memory Admission consumes it and never infers semantic conflict from text difference/shared actor.

### C4 — Post-validation Persistent Consolidation for Memory/Belief
**Status: WAITING on C3 + Output Validator contract**

Proposals may exist pre-response; durable owner mutations occur only after validation.

### C5 — Memory M5 real cross-turn/restart/rollback/history proof
**Status: WAITING on C3/C4**

Real persistence proof across turns/process reopen, rollback failure cases, lineage/history retrieval and supersession.

**Phase-C exit condition:** Authority/Belief + Memory ownership and durable lifecycle are demonstrated with real persistence semantics.

---

## PHASE D — PARALLEL COGNITIVE STATE OWNERS

After ContextSnapshot, Belief and Memory interfaces are stable, the following owners may be developed largely in parallel because each owns different mutable state. Integration remains ordered.

### D1 — Affective
**Status: LATER / PARALLEL**

Appraisal, transient emotion, mood, confidence/diagnostics; no ownership leakage into Relationship/Goal/Memory.

### D2 — Relationship
**Status: LATER / PARALLEL**

Directional relationship state/evidence; no raw-language reparse.

### D3 — Intimacy/Consent
**Status: LATER / PARALLEL**

Current consent/intimacy state with explicit temporal/current-state semantics, distinct from remembered historical evidence.

### D4 — Goal/Intention BDI-lite
**Status: LATER / PARALLEL**

Own active goals/intentions; evidence from Understanding/Belief/Memory/context, not raw parsing.

Each D-module requires its own real input/output/error/regression proof before integration.

### D5 — Reflection
**Status: WAITING on C + relevant D owners**

Event-driven/triggered; consumes structured state and memory, produces bounded reflection evidence/proposals; no direct persistence bypass.

### D6 — Opportunity Manager
**Status: WAITING on D1-D5 as required**

Produces candidate initiatives from current state/goals/context.

### D7 — Decision / Utility + manifestation policy
**Status: WAITING on D6**

Selects ReplyIntent/ActionIntent; confidence and reasons diagnostic; does not claim world action succeeded.

**Phase-D exit condition:** state owners and decision path are demonstrated independently and together without ownership violations.

---

## PHASE E — ORCHESTRATION, REALIZATION, VALIDATION AND WORLD LOOP

### E1 — Interaction Manager / Cognitive LOD
**Status: LATER**

Controls when expensive modules run, without changing semantic ownership or fabricating missing results.

### E2 — optional tactical Execution Planner BT/FSM
**Status: LATER / CONDITIONAL**

Use only if real multi-step action behavior requires it; not every-turn mandatory.

### E3 — Realization Context Supervisor
**Status: WAITING on decision/state context**

Builds the bounded realization package for language generation.

### E4 — Prompt Builder
**Status: WAITING on E3**

Realization-only. Must not redo Understanding, Authority, Memory reasoning or state ownership.

### E5 — real GGUF adapter
**Status: WAITING on E4**

Actual local backend integration with timing/errors/diagnostics.

### E6 — Output Validator
**Status: WAITING on E5**

ACCEPT/REJECT output/action; provides gate before durable state mutation.

### E7 — Persistent Consolidation complete routing
**Status: WAITING on E6 + owner repositories**

Commits approved proposals to Memory/Affective/Relationship/Goal/etc. through each canonical owner.

### E8 — World ActionIntent -> ActionResult loop
**Status: WAITING on Decision + World adapter**

Matrix intention is validated/executed by World; resulting objective event re-enters through Perception.

### E9 — Complete Causal Trace
**Status: built incrementally, closure here**

Trace event -> interpretation -> evidence/memory/state -> intention -> decision -> response/action -> validation -> committed state/result.

**Phase-E exit condition:** complete normal-turn architecture operates with correct realization, validation and durable commit ordering.

---

## PHASE F — SYSTEM E2E, ANDROID AND DEVICE

### F1 — Full automatic E2E regression
**Status: LATER**

Normal, ambiguous, error, multi-turn, restart, state interaction, action-result and persistence cases.

### F2 — Performance/Cognitive LOD benchmark
**Status: LATER**

CPU/RAM/latency/binary cost, module scheduling and mobile budget.

### F3 — Android integration
**Status: WAITING on F1/F2**

Port/integrate proven engine modules; no reintroduction of historical pre-validation Memory writes or regex semantic parsing.

### F4 — Moto G56 real device gate
**Status: BLOCKED until F3**

Measure actual load, RAM, latency, stability, app integration and end-to-end behavior.

**Phase-F exit condition:** installable mobile engine demonstrates the designed architecture under real device constraints.

---

# 4 — CURRENT NEXT-ACTION LOGIC

While Student-5 Work is active:

```text
DO NOT start Phase B/C/D/E/F implementation.
WAIT for repository-visible Work checkpoints.
SUPERVISOR may inspect/audit but must not duplicate execution.
```

When a Work update arrives:

```text
1. inspect matrix-understanding-lab/student5-path-b-v3 HEAD
2. read docs/WORK_CONTINUITY_STUDENT_5.md
3. inspect reports/artifact registry/persistent releases/checksums
4. verify against WORK_STUDENT_5_PATH_B_V3_COMPLETION.md
5. update the status of A1-A6
6. only move the active marker to Phase B when Phase-A exit condition is genuinely met
```

If Work reports a blocker, insert the blocker directly before the first blocked A-item and mark downstream A-items `BLOCKED_BY`; do not create a new top-level priority that hides the existing plan.

---

# 5 — HOW FUTURE NEW POINTS ARE ADDED

For every owner request/new discovery, update this document using this exact procedure:

```text
NEW POINT
-> identify owner/module
-> identify prerequisite IDs
-> identify dependents
-> classify blocker/requirement/improvement/diagnostic
-> determine serial vs parallel
-> insert in Phase A-F at dependency-correct position
-> update affected statuses/links
-> preserve old wording/history if superseded
-> record why it was inserted there
```

Examples:

- A new NLU export defect goes before A2 if it blocks training/runtime proof.
- A new Memory retrieval optimization goes inside C2 after basic repository/schema and before C3 if Memory Admission needs it; it does not jump ahead of active NLU work.
- A new Relationship feature goes in D2 unless it creates a prerequisite for Goal/Decision, in which case dependent D-items are marked accordingly.
- A new Android UI request stays in F3/F4 unless it proves an earlier engine contract must change.

This rule is binding specifically to prevent recent conversation topics from erasing or displacing earlier unfinished work.

---

# 6 — CURRENT GUARDS

```text
WORK ACTIVE = Student-5 Path B
DO NOT duplicate Work task in supervisor chat
DO NOT merge PR #23 without explicit owner approval
DO NOT modify Student-4 baseline
DO NOT modify Student-5 Path A or pristine
DO NOT overwrite any model/data/artifact version
DO NOT integrate Student-5 into Assembling before lab lock + supervisor acceptance
DO NOT call fixture-only work functional
Memory remains NOT IMPLEMENTED
PHONE TEST remains BLOCKED
```

Target architecture remains canonical in:

`docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`

This continuity controls **execution order**; the design document controls **module architecture/responsibility**.
