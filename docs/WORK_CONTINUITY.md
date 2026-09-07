# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor/design branch: `functional-audit-understanding-authority`  
Continuity schema: `matrix.assembling.continuity.v77`

## 0 — NON-NEGOTIABLE WORK METHOD

### Real-module criterion

A module is **not** considered working because it compiles, has DTO/contracts, serializes correctly, or passes fixture-only/unit tests.

Required closure sequence:

```text
REAL IMPLEMENTATION
-> REAL INPUT
-> REAL MODULE OUTPUT
-> FIELD-BY-FIELD EXPECTED/ACTUAL CHECK
-> REAL HAND-OFF TO NEXT MODULE
-> NORMAL + AMBIGUOUS + ERROR CASES
-> STRESS/COVERAGE WHERE APPLICABLE
-> ERROR ANALYSIS
-> FIX OBSERVED CAUSES
-> RETEST SAME SUITE
-> REGRESSION
-> ONLY THEN MODULE CHECKPOINT MAY CLOSE
```

For NLU/Understanding:

```text
raw text
-> actual Student-4 runtime/model
-> actual structured output
-> expected semantic comparison
-> IT/EN/ES stress
-> measured errors
-> fixes
-> identical retest + regression
-> downstream proof
```

Never invent metrics. Fixture-only tests do not prove linguistic understanding.

### No hypothetical/incomplete code

Forbidden:

```text
placeholder code
TODO as implementation
unfinished/non-compiling implementation
fake modules/outputs presented as real functionality
```

### Owner workflow

Owner should normally only need to:

```text
Fetch/Pull
-> run exact requested test
-> return output
```

Repository edits/scripts/branches/checkpoints are prepared online.

### Approval rule

Before any **new substantive implementation, modification, build or test action**, state exactly what will be done and wait for explicit owner approval.

Continuity/checkpoint preservation is pre-authorized.

### Mandatory interruption rule

If owner interrupts active work, before unrelated work:

```text
1. preserve valuable work online
2. record repo/branch/HEAD
3. record exact interruption point
4. record completed work
5. record tests/results/defects
6. record decisions/constraints/artifacts/checksums
7. record EVERYTHING STILL TO DO in order
8. record pending tests/fixes/regressions
9. record exact restart action
10. record closure condition
```

### Complete-design-before-piecewise-build rule

Every module must be designed according to what it must **actually do in the real Matrix Engine**. Do not discover obvious fundamental responsibilities only after downstream implementation has started.

For every module define before implementation:

```text
real product responsibility
state owner
input/output contracts
read-only dependencies
forbidden responsibilities
normal/ambiguous/error behavior
performance/mobile constraints
handoff to next real module
```

Future changes remain allowed when evidence from real tests/benchmarks justifies them. The rule is not “never touch a module twice”; it is “do not build blindly and invent the architecture piecemeal.”

### Deep Research / reuse rule

Before implementing or redesigning a module:

```text
1. research mature algorithms/libraries/reference implementations
2. verify task fit first
3. verify license/provenance
4. verify Android/offline/Kotlin/JVM/C++ compatibility
5. estimate RAM/CPU/latency/dependency/binary cost
6. classify candidate:
   DIRECT_REUSE / ADAPT / REIMPLEMENT / REFERENCE_ONLY / REJECT
7. copy/adapt external code only when license permits and exact provenance is recorded
8. do not bend Matrix architecture around a library
```

### Chat + Work coordination

```text
THIS CHAT = supervisor / complete architecture / audit / evidence / continuity
CHATGPT WORK = substantial authorized execution when owner launches Work mode
```

Before Work starts record objective, repo, branch, start HEAD, allowed/forbidden scope, required tests and stop condition. While Work is active this chat must not duplicate its implementation. After every Work checkpoint record current HEAD, files changed, tests, defects, artifacts, remaining work and next action.

This chat cannot silently observe a separate Work session in real time. Repository-visible Work output must be inspected and synchronized before acceptance.

---

## 1 — CANONICAL REPOSITORY STATE

### `MATRIXNEO23/assembling`

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
main includes PR #22 explicit RetrievalResult claimId/contextSnapshotId binding
active branch = functional-audit-understanding-authority
active branch HEAD before design-document commit = 9fc0c39dafb4482194a213255018be6d4befde6d
complete-design commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
backup branch = backup-memorycandidate-wip
backup MemoryCandidate commit = 9e7413c
backup status = BACKUP ONLY / NOT CANONICAL / DO NOT AUTO-MERGE
```

Canonical complete-design document:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
status = CANONICAL DESIGN BASELINE / no production implementation authorized merely by document
```

### `MATRIXNEO23/memoria`

```text
main HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
branches = main, python-authority-p0-v1
python-authority-p0-v1 final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR #1 merge = b8cc7e2133868049550d3c63d78f69da8f830f20
```

Current `memoria/main` has Authority + models, but not the complete historical Memory Foundation source/test suite. The complete Foundation was recovered from the owner Library and must not be reinvented.

### Historical Android design/source repository

```text
repo = MATRIXNEO23/8.10.9evo3-solo-gpt
reference HEAD = e97f75052afcc93d5b1e08b3ac881dba35633451
role = historical Android Matrix Engine code and approved architectural intent; read/audit/port selectively, do not blindly restore old orchestrator
```

---

## 2 — PRESERVED/CLOSED CHECKPOINT EVIDENCE

### Understanding V3 code/contract

```text
PR #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Interpretation:

```text
contract/adapter code checkpoint = CLOSED
raw-text real comprehension in assembling = NOT YET DEMONSTRATED
```

### Python Authority P0

Closed defects:

```text
hardcoded owner removed
regex/free-text property extraction removed from Authority path
actor-overlap/content-difference false contradiction removed
structured semantic contradiction rules preserved
```

### Understanding V3 -> Authority wiring

```text
PR #21
head = b7ea750665a563cbca673b4050a72adc21275a8e
merge = d3994e59008aac648576a252eac0d7c4e1028589
CI = 34020878347 SUCCESS
```

This was a wiring/code checkpoint, not proof of raw-language understanding or final orchestrator closure.

### Retrieval binding

PR #22 merged to main added:

```text
RetrievalQuery.claimId
RetrievalQuery.contextSnapshotId
RetrievalResult.claimId
RetrievalResult.contextSnapshotId
explicit binding validation
```

Owner tests before merge:

```text
verify_evidence_wire.sh = BUILD SUCCESSFUL
gradle test = BUILD SUCCESSFUL
gradle clean test = BUILD SUCCESSFUL
```

### Structured Understanding -> Authority defect/fix

First owner run:

```text
bash run_functional_audit.sh
single-claim structured path = PASS
multi-claim retrieval binding = FAIL
```

Root cause: runtime still used legacy 1-claim/1-result-only binding.

Active branch fix consumes explicit `claimId + contextSnapshotId`, fail-closed, no list-order/query-id guessing. Permanent regression added.

Owner retest:

```text
single-claim structured path = BUILD SUCCESSFUL
multi-claim explicit retrieval binding = BUILD SUCCESSFUL
```

Correct verdict:

```text
structured Understanding V3 -> Authority handoff = VERIFIED for tested single/multi-claim cases
raw-text understanding = NOT PROVEN
final engine orchestration = NOT PROVEN
```

---

## 3 — CURRENT NLU ARTIFACT — PRESERVE

```text
Student-4 v2.2A mixed/head-protected
status = EXPERIMENTAL_TEST_CANDIDATE / NOT_PRODUCTION_APPROVED
ZIP bytes = 356134801
ZIP SHA-256 = 4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5
ONNX = matrix-nlu-mixed-head-protected-int8.onnx
ONNX bytes = 149711344
ONNX SHA-256 = 738a4d052790367509d55487b649b71aaa029d839135693bb5be46f74d55ef70
quantization = dynamic INT8 encoder + protected Matrix heads FP32
```

Rules:

```text
DO NOT replace with Student-5 automatically
DO NOT start phone test before engine baseline closure
DO NOT open Student-5 as parallel side work
```

Deep Research runtime decision:

```text
ONNX Runtime Mobile Android = DIRECT_REUSE
license = MIT
initial benchmark path = CPU/XNNPACK for quantized model, then NNAPI on physical Moto G56
custom reduced-operator Android build may be used later if size benefit is demonstrated
```

---

## 4 — COMPLETE REAL ENGINE DESIGN — CURRENT CANONICAL BASELINE

The design is no longer being invented module-by-module. The complete intended cognitive/action system is documented before remaining implementation.

Canonical separation:

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL
!= RELATE != CONSENT != WANT != DECIDE != EXPRESS
```

State owners:

```text
WORLD objective state              -> WORLD / APPLICATION ADAPTER
linguistic interpretation          -> NLU + UNDERSTANDING
belief / epistemic commitment      -> BELIEF / AUTHORITY
long-term autobiographical memory  -> MEMORY
transient emotion + mood           -> AFFECTIVE
relationship state                 -> RELATIONSHIP
current intimacy / consent state   -> INTIMACY
active goals / intentions           -> GOAL
behavioral choice                  -> DECISION
language realization              -> GGUF
post-validation commit routing     -> PERSISTENT CONSOLIDATION
```

Normal real turn:

```text
World/User/NPC/System Event
-> Perception Gateway
-> NLU Runtime
-> Understanding V3 / TypedClaim[]
-> TurnWorkspace
-> parallel read-only ENRICH:
   World + Memory index + Belief + Relationship + Affective + Intimacy + Goal
-> MatrixContextSnapshot
-> Coherence Validator
-> Authority / Belief resolution
-> MemoryPreflight + state evaluations/proposals
-> optional Reflection when triggered
-> Opportunity Manager
-> Decision / Utility
-> optional BT/FSM tactical executor
-> Realization Context Supervisor
-> Prompt Builder
-> GGUF
-> Output Validator
-> Persistent Consolidation
-> owner-specific durable commits
-> ActionIntent -> World ActionResult when applicable
-> causal trace close
```

Independent Context/state reads should use bounded Kotlin structured concurrency. Writes/decisions with causal dependencies remain ordered.

Complete specification is in `docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`.

---

## 5 — CURRENT MODULE REALITY / REUSE VERDICTS

### Application/World Adapter

```text
old WorldMatrixProtocol = useful and aligned
verdict = ADAPT/PORT into MIP, keep World objective authority
```

### Perception

```text
old PerceptionEnvelope = useful event/source/time/duplicate validation
CloudEvents = REFERENCE_ONLY for event envelope identity/dedup semantics
verdict = ADAPT old Matrix code, support multiple simultaneous perceptions
```

### NLU

```text
Student-4 exists
assembling concrete production MatrixNluV3RuntimeBridge = absent
ONNX Runtime Android = DIRECT_REUSE
verdict = runtime integration still required
```

### Understanding

```text
CanonicalUnderstandingV3Adapter = KEEP/PORT
legacy UnderstandingLabAdapter = compatibility only
raw-language real proof pending
```

### TurnWorkspace / Context

```text
MIP contracts exist
old MatrixEngineFramework Stage/StageResult/diagnostics = useful lightweight skeleton
kotlinx.coroutines = DIRECT_REUSE for structured parallel reads
canonical Context Assembler = BUILD
```

### Coherence

```text
current BasicCoherenceGuard overlaps Authority/policy
verdict = FIX/REIMPLEMENT narrow structural semantic validator
must not decide Authority/Memory/behavior
```

### Authority

```text
DeterministicAuthorityResolver = KEEP
active retrieval-binding fix = KEEP
must become real canonical orchestrated path
```

### BeliefState

```text
separate from Memory required by MIP/product architecture
current final owner implementation = missing
AGM/Jason = REFERENCE_ONLY
verdict = BUILD small evidence-backed belief-base owner
```

### Memory

```text
real durable Memory = NOT IMPLEMENTED/INTEGRATED
old Python Foundation recovered and valuable for transaction/lineage/admission tests
old Android MemoryAcquisition regex parser = REJECT as production acquisition
old reconciliation = ADAPT lifecycle only; remove heuristic contradiction inference
old retrieval hard-filter-first architecture = ADAPT
old StructuredMemoryStore = TEST DOUBLE/REFERENCE only
Room/SQLite = DIRECT_REUSE for production Android
```

### Affective

```text
existing Matrix affective prototype has useful appraisal/emotion/mood/decay logic
relationship-like values currently mixed inside = ownership defect
FAtiMA Apache-2.0 = ADAPT/reference
Gamygdala MIT = REFERENCE/ADAPT
verdict = ADAPT Matrix affective, remove Relationship ownership
```

### Relationship

```text
final canonical owner = missing
FAtiMA Social Importance / social dynamics = reference
verdict = BUILD small directional persistent RelationshipState owner
```

### Intimacy/Consent

```text
final owner = missing
must distinguish stable attraction vs current desire vs boundaries vs action-scoped consent vs history
verdict = BUILD Matrix-native owner
```

### Goal/Intention

```text
old AgentStateGoalIntention separation = useful
no full BDI logic
Jason LGPL = REFERENCE_ONLY
verdict = ADAPT contracts + BUILD BDI-lite
```

### Reflection

```text
old trigger/queue/provenance patterns useful
old ReflectionEngine concatenation heuristic insufficient
verdict = PORT trigger/budget/provenance; REIMPLEMENT structured inference
```

### Opportunity Manager

```text
architecturally required for proactive behavior
no verified final implementation found
verdict = BUILD Matrix-native
```

### Decision/Utility

```text
final owner absent
initial explainable factors already defined by project
Jason = reference only
gdx-ai BT/FSM is downstream execution aid, not global decision
verdict = BUILD Matrix-native explainable utility DecisionSnapshot
```

### Interaction Manager

```text
required for multi-party sessions/turn-taking/visibility
no verified final implementation found
verdict = BUILD
```

### Cognitive LOD

```text
architecture defined, final implementation absent
verdict = BUILD small significance/budget scheduler
```

### Context Supervisor / Realization Package

```text
old ContextPackageBuilder + ContextSupervisor contain useful budget/dedup/knowledge-boundary patterns
verdict = PORT/ADAPT after DecisionSnapshot
```

### Prompt Builder

```text
REALIZATION_ONLY principle correct
current inputs legacy/incomplete; no DecisionSnapshot
verdict = KEEP principle, ADAPT final contract later
```

### GGUF

```text
assembling EchoGgufAdapter = fake/smoke only
real llama.cpp backend exists historically outside cognitive core
verdict = PORT/build adapter after Decision/Prompt contracts close
```

### Output Validator

```text
interface exists, real validator absent
planned strategy = deterministic semantic constraints + lightweight re-NLU/Understanding compare + bounded regenerate/fallback
verdict = BUILD
```

### Persistent Consolidation

```text
interface exists, implementation absent
verdict = BUILD post-validation coordinator; never state owner
```

### Diagnostics/Causal Trace

```text
old CausalTrace structured/bounded implementation = strong reuse candidate
OpenTelemetry / W3C Trace Context / W3C PROV = REFERENCE_ONLY standards
verdict = PORT/ADAPT old trace to complete MIP stages
```

---

## 6 — DEEP RESEARCH REUSE MATRIX

```text
ONNX Runtime Mobile       MIT          DIRECT_REUSE
kotlinx.coroutines        Apache-2.0   DIRECT_REUSE
Android Room              AndroidX     DIRECT_REUSE
SQLite FTS/BM25           SQLite       DIRECT_REUSE
CloudEvents               standard     REFERENCE_ONLY
W3C PROV                  Recommendation REFERENCE_ONLY
OpenTelemetry/TraceContext standards    REFERENCE_ONLY
FAtiMA Toolkit            Apache-2.0   ADAPT/reference
Gamygdala                 MIT          REFERENCE/ADAPT
Jason                     LGPL-3.0    REFERENCE_ONLY
gdx-ai                    Apache-2.0   ADAPT/selective reuse for BT/FSM
RRF                       algorithm     REIMPLEMENT/benchmark
MMR                       algorithm     REIMPLEMENT/optional
Graphiti                  Apache-2.0   REFERENCE_ONLY, too heavy Python/graph stack
Mem0                      Apache-2.0 ecosystem REFERENCE_ONLY
LangMem                   Python/LangGraph REFERENCE_ONLY
Letta                     Apache-2.0 platform REFERENCE_ONLY
Generative Agents         research      REFERENCE_ONLY
AGM belief revision       research      REFERENCE_ONLY
```

Important retrieval finding:
- RRF is a simple strong baseline;
- newer hybrid retrieval analysis reports parameter sensitivity and that tuned convex combination can outperform RRF;
- therefore Matrix will benchmark calibrated weighted/convex fusion vs RRF instead of assuming one fixed formula;
- MMR is optional final diversity reranking for near-duplicate memory results.

No external code is copied merely because it exists. Exact file/commit/license/provenance must be recorded before any copy/adaptation.

---

## 7 — MEMORY FOUNDATION RECOVERY / FINAL MEMORY REQUIREMENTS

Memory is **not** implemented yet.

### Recovered Python reference files

```text
memory/__init__.py
memory/models.py
memory/schema.py
memory/database.py
memory/repository.py
memory/admission.py
memory/admission_models.py
```

Recovered tests include:

```text
tests/conftest.py
test_memory_admission.py
test_memory_admission_authority.py
test_memory_admission_supersede.py
test_atomic_rollback.py
test_lineage_protection.py
test_semantic_update.py
restart/reopen integrity requirement from historical suite
```

Reference behaviors that must survive Kotlin/Room port:

```text
atomic transaction/rollback
SAVE
read by ID/owner/actor
metadata-only update
semantic changes only via supersede()
root lineage revisionOf
sequential supersededBy
protected lineage delete
explicit contradicts_memory_id
Admission SAVE/SUPERSEDE/REJECT/IGNORE
restart persistence
```

### Critical design correction

`EPISODIC / SEMANTIC / REFLECTION` are memory **kinds**, not sufficient content categories.

Final `MemoryRecord` must also carry/index structured semantic dimensions:

```text
semanticDomain + predicateId
subject / target / owner / source / perspective / observer where relevant
entity/actor refs
typed object/value + value type
polarity/modality
eventTime/validFrom/validTo/observedAt/recordedAt/temporal anchor
authority/epistemic class
source reliability
provenance: event/observation/claim/AuthorityResolution/derived refs
validity/current/history
revisionOf/supersededBy/revisionCount/contradiction id
salience/importance/retention/reinforcement/access metadata
optional embedding/index version if later benchmarked
```

Relationship/Affective/Goal/Intimacy remain separate state owners; Memory may remember historical evidence about them but does not own their current state.

### Retrieval levels

```text
LEVEL 1 INDEX_PROBE = every normal turn, lightweight structured lookup
LEVEL 2 HYDRATE_AND_RERANK = only relevant candidates
LEVEL 3 DEEP_OR_MULTI_HOP = explicit complex purpose only
```

Hard structured filters first; Room/SQLite indexes + FTS/BM25; optional vector only if benchmark justifies; calibrated hybrid rank; current/history distinction; explicit NO_MATCH != UNAVAILABLE/ERROR.

---

## 8 — IMPORTANT OLD-ANDROID AUDIT FINDINGS

Historical source has significantly more reusable code than `assembling` alone.

### Valuable assets

```text
WorldMatrixProtocol
PerceptionEnvelope
MatrixEngineFramework Stage/StageResult/StageDiagnostic
WorkingMemoryStage concepts
HierarchicalMemoryIndex/Retrieval + SubjectOwnerGuard architecture
HybridMemoryRanker as benchmark/reference only
ContextPackageBuilder
ContextSupervisor
ReflectionTrigger/Queue/Processor patterns
AgentStateGoalIntention separation
CausalTrace
MemoryPersistenceAdapter interface separation
```

### Critical defect: old orchestrator ordering

Old `MatrixEngineOrchestrator.kt` performs Memory acquisition/reconciliation/persistence **before backend generation/output validation**.

Therefore:

```text
DO NOT port old orchestrator wholesale
DO NOT restore pre-response durable writes
PORT useful stages selectively into the new MIP order
```

### Critical defect: old acquisition

Old `MemoryAcquisitionClassifier.kt` regex-parses raw language into identity/family/age/work/location/preference/fear/goal memories.

Final rule:

```text
REJECT production regex acquisition
retain examples as possible regression fixtures
MemoryPreflight consumes canonical TypedClaim + AuthorityResolution instead
```

### Critical defect: old reconciliation

Old conflict/update logic uses local slot/text heuristics. Final Memory must consume explicit Authority contradiction identity, not rediscover semantic conflict.

---

## 9 — DEVELOPMENT ORDER TO MINIMIZE REOPENING

### CURRENT PHASE A — complete architecture/research baseline

```text
STATUS = COMPLETE DESIGN DOCUMENT CREATED
ARTIFACT = docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
COMMIT = 89e25bd8fb3c27d826559ced5b629333a4a330e6
PRODUCTION CODE CHANGED = NO
BUILDS/TESTS RUN IN THIS PHASE = NO
```

### NEXT PACKAGE — Foundation Closure F1/F2

This is the bounded prerequisite before Memory implementation.

```text
F1
- align canonical event/observation/workspace/provenance/causal trace foundations

F2
- canonical orchestrator skeleton
- real V3 -> Context -> claim-bound Retrieval -> Authority path
- narrow Coherence to its actual responsibility
- explicit NOT_WIRED slots for future state owners
- bounded parallel read architecture
- OutputValidator/PersistentConsolidation seams preserved
- no durable Memory persistence
- no Relationship/Decision implementation yet
```

Required proof for F1/F2:

```text
real/controlled canonical V3 input
-> Context snapshot
-> per-claim retrieval binding/status
-> Coherence structural result
-> AuthorityResolution
-> field-by-field preservation
-> normal/ambiguous/unavailable/error/multi-claim cases
-> no legacy first-claim path
-> no pre-response durable write
-> regression green
```

After F1/F2 passes:

### MEMORY ONE COMPLETE WORKSTREAM

```text
M1 final MemoryRecord/query schema
M2 Room Repository + Admission
M3 Retrieval/index/BM25/hybrid/history
M4 MemoryPreflight + post-validation Consolidation
M5 real cross-turn/restart/rollback E2E Memory closure
```

Then state owners:

```text
Affective
Relationship
Intimacy/Consent
Goal/Intention
```

Then higher cognition:

```text
Reflection
Opportunity
Decision/Utility
optional BT/FSM executor
Interaction Manager
Cognitive LOD
```

Then realization/execution:

```text
Context Supervisor/RealizationPackage
Prompt Builder final
real GGUF
Output Validator
Persistent Consolidation all owners
ActionIntent -> World ActionResult
```

Then full automatic E2E/regression, finally Moto G56.

---

## 10 — MODULES MUST NOT BE REOPENED WITHOUT EVIDENCE

Once a module closes its real demonstrative suite, do not reopen it merely because a downstream implementation is being added.

Reopen only when:

```text
real integration test demonstrates a defect
benchmark demonstrates a material performance problem
canonical product requirement changes with owner approval
security/data-integrity issue is discovered
```

When reopened: one bounded fix, regression, close again.

---

## 11 — CHATGPT WORK LEDGER

Current verified state:

```text
WORK OBJECTIVE = NONE CURRENTLY SYNCHRONIZED FROM AN ACTIVE WORK SESSION
WORK REPOSITORY = unknown until Work session reports/commits become visible
WORK BRANCH = unknown
WORK START HEAD = unknown
WORK CURRENT HEAD = unknown
CURRENT OPERATION = none verified in this chat
```

Do not assume Work is idle/finished without evidence.

### Recommended next Work assignment after owner approval

```text
TASK = FOUNDATION CLOSURE F1/F2 ONLY
REPO = MATRIXNEO23/assembling
START POINT = current approved design branch/head after continuity checkpoint
ALLOWED = orchestration/context/coherence/foundation contracts, tests, diagnostics, continuity needed for F1/F2
FORBIDDEN = Memory durable implementation, Relationship/Decision implementation, changing Student-4 model, Student-5, phone build, lowering gates
EXPECTED = canonical typed path + demonstrative tests + regressions + exact defect report
STOP = F1/F2 evidence produced; no Memory implementation automatically
```

This chat supervises and audits Work evidence, and must not duplicate Work's active implementation.

---

## 12 — OWNER LOCAL ENVIRONMENT

```text
assembling path = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

Owner should not be asked to create/copy project files manually unless unavoidable.

---

## 13 — PHONE / STUDENT / MODEL GUARDS

```text
PHONE TEST = BLOCKED until complete automatic integrated baseline and Memory/E2E closure
CURRENT NLU = Student-4 v2.2A
STUDENT-5 = preserved, side work stopped unless explicitly reauthorized
MEMORYCANDIDATE WIP = preserved separately, not canonical
NO MODEL SWITCH as side task
```

---

## 14 — EXACT RESTART POINT / EVERYTHING STILL TO DO

```text
ACTIVE SUPERVISOR TASK = COMPLETE REAL ENGINE DESIGN + DEEP RESEARCH
STATUS = DESIGN BASELINE CREATED
DESIGN FILE = docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
DESIGN COMMIT = 89e25bd8fb3c27d826559ced5b629333a4a330e6
PRODUCTION IMPLEMENTATION DURING DESIGN PHASE = NONE
TEST/BUILD DURING DESIGN PHASE = NONE
```

### Remaining before implementation

```text
1. owner reviews/accepts the complete design baseline
2. if accepted, prepare/assign Foundation Closure F1/F2 to Work or execute in this chat only under explicit implementation approval
3. F1/F2 implementation
4. demonstrative F1/F2 test suite
5. error analysis/fixes
6. repeat identical tests + full regression
7. supervisor verdict
8. only then start Memory M1-M5 as one complete workstream
```

### F1/F2 closure condition

```text
canonical Understanding V3 claims reach a real immutable ContextSnapshot,
claim-bound Retrieval statuses/evidence reach narrow Coherence + canonical Authority,
normal/multi-claim/ambiguous/unavailable/error cases are demonstrated,
no semantic field is silently lost or invented,
legacy basic path is not authoritative,
no durable write occurs pre-validation,
and repository regression is green.
```

### Memory closure after F1/F2

```text
real canonical claims/resolutions can be persisted through Room Admission/Repository,
retrieved correctly by structured semantic/time/entity queries across turns/restart,
SUPERSEDE lineage and rollback are demonstrated,
current/history are correct,
no state-owner boundaries are violated,
and Memory/Authority/Understanding regressions are green.
```

### Final project sequence

```text
F1/F2 foundations
-> Memory complete
-> Affective/Relationship/Intimacy/Goal owners
-> Reflection/Opportunity/Decision/Interaction/LOD
-> realization/GGUF/Validator/Consolidation/World action loop
-> full desktop/JVM automatic E2E + fixes/regression
-> Android integration baseline
-> Moto G56 physical testing and improvement
```

### Next substantive action

**Requires explicit owner approval because it changes/tests production code:**

```text
FOUNDATION CLOSURE F1/F2 ONLY
```
