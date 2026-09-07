# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active supervisor/design branch: `functional-audit-understanding-authority`  
Continuity schema: `matrix.assembling.continuity.v78`

## 0 — NON-NEGOTIABLE WORK METHOD

### Real-module criterion

A module is **not** working merely because it compiles, has DTO/contracts, serializes, or passes fixture-only/unit tests.

```text
REAL IMPLEMENTATION
-> REAL INPUT
-> REAL MODULE OUTPUT
-> FIELD-BY-FIELD EXPECTED/ACTUAL
-> REAL HANDOFF TO NEXT MODULE
-> NORMAL + AMBIGUOUS + ERROR CASES
-> STRESS/COVERAGE WHERE APPLICABLE
-> ERROR ANALYSIS
-> FIX OBSERVED CAUSES
-> RETEST SAME SUITE
-> REGRESSION
-> ONLY THEN CLOSE MODULE
```

NLU/Understanding proof requires real Student-4 runtime/model on raw text, expected semantic comparison, IT/EN/ES stress, measured errors, fixes, identical retest/regression and downstream proof. Never invent metrics.

### No incomplete/hypothetical implementation

Forbidden: placeholders, TODO-as-implementation, unfinished/non-compiling code, fake module output presented as real functionality.

### Owner workflow

```text
Fetch/Pull -> run exact requested test -> return output
```

Repository edits/scripts/branches/checkpoints are prepared online whenever possible.

### Approval rule

Before any **new substantive implementation, modification, build or test action**, state exactly what will be done and wait for explicit owner approval. Continuity/checkpoint preservation is pre-authorized.

### Mandatory interruption rule

If owner interrupts active work, before unrelated work preserve repo/branch/HEAD, exact interrupted operation, completed substeps, files/changes, tests/results, defects, decisions, artifacts/checksums, **everything still to do in order**, pending fixes/tests/regressions, exact restart step and closure condition.

### Complete-design-before-piecewise-build rule

Every module is designed according to what it must actually do in the final real engine. Before implementation define responsibility, state owner, input/output, read-only dependencies, forbidden responsibilities, ambiguity/error behavior, performance/mobile constraints and handoff.

Future changes are allowed when demonstrated tests, benchmarks, security/data-integrity issues or owner-approved product requirements justify them. Do not invent foreseeable architecture piecemeal.

### Deep Research / reuse rule

Before implementing/redesigning a module:

```text
research mature algorithms/libraries/reference implementations
verify task fit
verify license/provenance
verify Android/offline/Kotlin/JVM/C++ fit
assess RAM/CPU/latency/dependency/binary cost
classify DIRECT_REUSE / ADAPT / REIMPLEMENT / REFERENCE_ONLY / REJECT
copy/adapt only with license + exact provenance recorded
never bend Matrix architecture around a library
```

### Chat + Work coordination

```text
THIS CHAT = supervisor / complete architecture / audit / evidence / continuity
CHATGPT WORK = substantial authorized execution when owner launches Work mode
```

Before Work starts record objective, repo, branch, start HEAD, scope, forbidden scope, tests and stop condition. Do not duplicate active Work implementation here. After each Work checkpoint synchronize HEAD/files/tests/defects/artifacts/remaining work/next action. This chat cannot silently observe a separate Work session in real time.

---

## 1 — REPOSITORY / BRANCH STATE

### `MATRIXNEO23/assembling`

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
main includes PR #22 explicit RetrievalResult claimId/contextSnapshotId binding
active branch = functional-audit-understanding-authority
active branch HEAD before this continuity commit = 496364da773bbdc7d4bae19debb1c700ed42367f
backup branch = backup-memorycandidate-wip
backup MemoryCandidate commit = 9e7413c
backup status = BACKUP ONLY / NOT CANONICAL / DO NOT AUTO-MERGE
```

Design artifacts created in the current approved architecture/research phase:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
status = CANONICAL DESIGN BASELINE / no production implementation authorized by the document itself

WORK_CONTINUITY v77 synchronization
commit = e6aa83003eadf561e529eb0e6343a4efb2530c12

prompts/WORK_FOUNDATION_CLOSURE_F1_F2.md
commit = 496364da773bbdc7d4bae19debb1c700ed42367f
status = PREPARED ONLY; Work must not execute unless owner launches/authorizes F1/F2
```

### `MATRIXNEO23/memoria`

```text
main HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
branches = main, python-authority-p0-v1
python-authority-p0-v1 final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR #1 merge = b8cc7e2133868049550d3c63d78f69da8f830f20
```

Current `memoria/main` contains Authority-related source/models but not the complete historical Memory Foundation source/test suite. The full Foundation was recovered from the owner Library and must not be reinvented.

### Historical Android source/design

```text
repo = MATRIXNEO23/8.10.9evo3-solo-gpt
reference HEAD = e97f75052afcc93d5b1e08b3ac881dba35633451
role = historical Android Matrix Engine source + product architectural intent
rule = audit/port selectively; never restore old orchestrator blindly
```

---

## 2 — PRESERVED/CLOSED EVIDENCE

### Understanding V3 contract/adapter

```text
PR #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Correct interpretation: contract/adapter code checkpoint closed; raw-text real comprehension in assembling **NOT yet demonstrated**.

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

Wiring/code checkpoint only; not raw-language or final-orchestrator proof.

### Retrieval binding PR #22

Added explicit query/result `claimId` and `contextSnapshotId` binding. Owner tests before merge:

```text
verify_evidence_wire.sh = BUILD SUCCESSFUL
gradle test = BUILD SUCCESSFUL
gradle clean test = BUILD SUCCESSFUL
```

### Structured Understanding -> Authority demonstrated defect/fix

First owner run:

```text
bash run_functional_audit.sh
single-claim structured path = PASS
multi-claim retrieval binding = FAIL
```

Root cause: runtime retained legacy 1-claim/1-result-only binding. Active branch fix consumes explicit `claimId + contextSnapshotId`, fail-closed, with permanent regression.

Owner retest:

```text
single-claim structured path = BUILD SUCCESSFUL
multi-claim explicit retrieval binding = BUILD SUCCESSFUL
```

Correct verdict: tested structured V3→Authority handoff works; raw-text understanding and final engine do not yet have proof.

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

```text
DO NOT replace with Student-5 automatically
DO NOT start phone test before engine baseline closure
DO NOT open Student-5 as side work
```

Deep Research runtime selection:

```text
ONNX Runtime Mobile Android = DIRECT_REUSE
license = MIT
initial physical benchmark = CPU/XNNPACK for quantized Student-4, then NNAPI
custom reduced-operator Android build only if measured size/runtime tradeoff justifies it
```

---

## 4 — COMPLETE REAL ENGINE DESIGN

Canonical design is now written before remaining implementation:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
```

Fundamental separation:

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL
!= RELATE != CONSENT != WANT != DECIDE != EXPRESS
```

Canonical owners:

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

Normal turn target:

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

Independent reads use bounded structured concurrency; causal decision/write barriers remain ordered.

---

## 5 — MODULE REALITY / REUSE VERDICTS

### World / Application Adapter
Old `WorldMatrixProtocol` is useful and aligned. **ADAPT/PORT**, keep World objective authority.

### Perception
Old `PerceptionEnvelope` has useful identity/source/time/duplicate validation. CloudEvents is **REFERENCE_ONLY**. Final gateway supports multiple simultaneous observations, not single-user-text collapse.

### NLU
Student-4 exists, concrete production V3 runtime bridge absent. ONNX Runtime Android = **DIRECT_REUSE**.

### Understanding
`CanonicalUnderstandingV3Adapter` = **KEEP/PORT**. Legacy `UnderstandingLabAdapter` remains compatibility only. Real raw-language proof pending.

### TurnWorkspace / Context
MIP contracts exist. Old `MatrixEngineFramework.Stage/StageResult/StageDiagnostic` = useful pattern. `kotlinx.coroutines` = **DIRECT_REUSE**. Canonical Context Assembler = **BUILD**.

### Coherence
Current `BasicCoherenceGuard` overlaps Authority/policy. **FIX/REIMPLEMENT** as narrow structural/semantic validator only.

### Authority
`DeterministicAuthorityResolver` + active retrieval-binding fix = **KEEP** and wire canonically.

### BeliefState
Required separate owner; final implementation absent. AGM/Jason = **REFERENCE_ONLY**. **BUILD small evidence-backed belief base**.

### Memory
Durable Memory not integrated. Recovered Python Foundation gives transaction/lineage/admission test reference. Old Android regex acquisition = **REJECT production**. Old reconciliation = **ADAPT lifecycle only**. Hard-filter-first retrieval = **ADAPT**. Room/SQLite = **DIRECT_REUSE**.

### Affective
Existing Matrix appraisal/emotion/mood/decay is valuable; Relationship-like values mixed inside are ownership defect. FAtiMA = **ADAPT/reference**, Gamygdala = **REFERENCE/ADAPT**. Adapt Matrix code and remove Relationship ownership.

### Relationship
Final owner absent. **BUILD small directional persistent state owner**; use FAtiMA social dynamics as reference.

### Intimacy/Consent
Final owner absent. Must distinguish stable attraction, current desire, comfort/boundary, action-scoped consent and history. **BUILD Matrix-native**.

### Goal/Intention
Old separation useful but no full BDI. Jason LGPL = **REFERENCE_ONLY**. **ADAPT contracts + BUILD BDI-lite**.

### Reflection
Old trigger/queue/provenance patterns useful; concatenation heuristic insufficient. **PORT trigger/budget/provenance; REIMPLEMENT structured inference**.

### Opportunity
Required for proactive behavior, final implementation not verified. **BUILD**.

### Decision/Utility
Final owner absent. **BUILD Matrix-native explainable utility DecisionSnapshot**. gdx-ai BT/FSM belongs downstream execution only.

### Interaction Manager
Required for multi-party turn-taking/visibility/session state. **BUILD**.

### Cognitive LOD
Architecture defined, final implementation absent. **BUILD small significance/budget scheduler**.

### Context Supervisor / Realization Package
Old ContextPackageBuilder/ContextSupervisor contain useful budget/dedup/knowledge-boundary patterns. **PORT/ADAPT** after Decision.

### Prompt
REALIZATION_ONLY principle correct; current inputs legacy/incomplete. **KEEP principle, ADAPT contract after DecisionSnapshot**.

### GGUF
Echo adapter is fake/smoke. Real backend exists historically outside cognitive core. **PORT/BUILD adapter later**.

### Output Validator
Interface only. Plan = deterministic constraints + lightweight re-NLU/Understanding compare + bounded regenerate/fallback. **BUILD**.

### Persistent Consolidation
Interface only. **BUILD post-validation coordinator**, never universal state owner.

### Diagnostics
Old `CausalTrace` strong reuse candidate. OpenTelemetry/W3C Trace Context/W3C PROV = **REFERENCE_ONLY**. **PORT/ADAPT** to complete MIP stages.

---

## 6 — DEEP RESEARCH REUSE MATRIX

```text
ONNX Runtime Mobile       MIT            DIRECT_REUSE
kotlinx.coroutines        Apache-2.0     DIRECT_REUSE
Android Room              AndroidX       DIRECT_REUSE
SQLite FTS/BM25           SQLite         DIRECT_REUSE
CloudEvents               standard       REFERENCE_ONLY
W3C PROV                  Recommendation REFERENCE_ONLY
OpenTelemetry/TraceContext standards      REFERENCE_ONLY
FAtiMA Toolkit            Apache-2.0     ADAPT/reference
Gamygdala                 MIT            REFERENCE/ADAPT
Jason                     LGPL-3.0      REFERENCE_ONLY
gdx-ai                    Apache-2.0     ADAPT/selective reuse BT/FSM
RRF                       algorithm       REIMPLEMENT/benchmark
MMR                       algorithm       REIMPLEMENT/optional
Graphiti                  Apache-2.0     REFERENCE_ONLY; Python/graph stack too heavy
Mem0                      Apache-2.0 ecosystem REFERENCE_ONLY
LangMem                   Python/LangGraph REFERENCE_ONLY
Letta                     Apache-2.0 platform REFERENCE_ONLY
Generative Agents         research        REFERENCE_ONLY
AGM belief revision       research        REFERENCE_ONLY
```

Retrieval decision:
- benchmark calibrated weighted/convex fusion versus RRF;
- do not assume old fixed HybridMemoryRanker weights are correct;
- optional MMR post-rank to reduce redundant near-duplicates;
- hard structured filters before soft relevance.

Exact external file/commit/license/provenance must be recorded before any copy/adaptation.

---

## 7 — MEMORY FOUNDATION RECOVERY + FINAL MEMORY REQUIREMENTS

Memory status: **NOT IMPLEMENTED / NOT INTEGRATED**.

Recovered reference source:

```text
memory/__init__.py
memory/models.py
memory/schema.py
memory/database.py
memory/repository.py
memory/admission.py
memory/admission_models.py
```

Recovered reference tests include:

```text
tests/conftest.py
test_memory_admission.py
test_memory_admission_authority.py
test_memory_admission_supersede.py
test_atomic_rollback.py
test_lineage_protection.py
test_semantic_update.py
restart/reopen integrity requirement
```

Semantics to preserve:

```text
atomic transaction/rollback
SAVE + read
metadata-only update
semantic evolution only through supersede()
root revisionOf + sequential supersededBy
protected lineage delete
explicit contradicts_memory_id
Admission SAVE/SUPERSEDE/REJECT/IGNORE
restart persistence
```

### Final Memory classification

`EPISODIC / SEMANTIC / REFLECTION` are **memory kinds**, not all information categories.

Final MemoryRecord must also carry/index:

```text
semanticDomain + predicateId
subject / target / owner / source / perspective / observer when applicable
entity/actor refs
typed object/value + value type
polarity/modality
eventTime / validFrom / validTo / observedAt / recordedAt / temporal anchor
authority / epistemic class
source reliability
provenance event/observation/claim/AuthorityResolution/derived refs
validity/current/history
revisionOf/supersededBy/revisionCount/contradiction id
salience/importance/retention/reinforcement/access metadata
optional embedding/index version only if benchmarked
```

Relationship/Affective/Goal/Intimacy are separate current state owners; Memory may retain historical evidence about those domains.

Retrieval:

```text
LEVEL 1 INDEX_PROBE every normal turn
LEVEL 2 HYDRATE_AND_RERANK only relevant candidates
LEVEL 3 DEEP_OR_MULTI_HOP only explicit complex purpose
```

Hard structured filters -> Room/SQLite indexes + FTS/BM25 -> optional vector if benchmark justified -> calibrated hybrid rank -> current/history policy. `NO_MATCH != UNAVAILABLE != ERROR`.

---

## 8 — OLD ANDROID AUDIT: WHAT TO SAVE / WHAT TO REJECT

Useful historical assets:

```text
WorldMatrixProtocol
PerceptionEnvelope
MatrixEngineFramework Stage/StageResult/StageDiagnostic
WorkingMemoryStage concepts
HierarchicalMemoryIndex/Retrieval + SubjectOwnerGuard
HybridMemoryRanker as benchmark/reference only
ContextPackageBuilder
ContextSupervisor
Reflection trigger/queue/processor patterns
AgentStateGoalIntention separation
CausalTrace
MemoryPersistenceAdapter interface separation
```

Critical defects not to reintroduce:

### Old orchestrator
Performs Memory acquisition/reconciliation/persistence **before backend generation/output validation**.

```text
DO NOT port wholesale
DO NOT restore pre-response durable writes
PORT useful stages individually into new MIP ordering
```

### Old MemoryAcquisitionClassifier
Regex-parses raw text into categories. Final MemoryPreflight consumes canonical TypedClaims + AuthorityResolution.

```text
REJECT production parser
retain examples only as possible regression fixtures
```

### Old reconciliation
Uses local slot/text heuristics for update/conflict. Final conflict identity comes from Authority.

---

## 9 — DEVELOPMENT ORDER TO MINIMIZE FORESEEABLE REWORK

### Architecture/research baseline — COMPLETE FOR CURRENT PASS

```text
design doc = docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
production code changed = NO
build/test run during design phase = NO
```

### NEXT IMPLEMENTATION PACKAGE — Foundation Closure F1/F2

Prepared Work instruction:

```text
prompts/WORK_FOUNDATION_CLOSURE_F1_F2.md
commit = 496364da773bbdc7d4bae19debb1c700ed42367f
status = PREPARED / NOT AUTHORIZED TO EXECUTE YET
```

F1/F2 scope:

```text
F1
- canonical event/observation/workspace/provenance/causal-trace foundation

F2
- canonical orchestrator skeleton
- real V3 -> Context -> claim-bound Retrieval -> Authority path
- Coherence narrowed to structural semantic validation
- explicit NOT_WIRED future owner slots
- bounded parallel read architecture
- OutputValidator/PersistentConsolidation seams
- NO durable Memory implementation
- NO Relationship/Decision implementation yet
```

F1/F2 proof must include normal single claim, multi-claim, ambiguity, retrieval NO_MATCH, retrieval UNAVAILABLE, retrieval ERROR and context-binding mismatch; exact claim/context/retrieval/Authority IDs; no field loss/invention; first divergence/reason codes; full regression.

After F1/F2 demonstrated green:

### Memory ONE COMPLETE WORKSTREAM

```text
M1 final MemoryRecord/query schema
M2 Room Repository + Admission
M3 Retrieval/index/BM25/hybrid/history
M4 MemoryPreflight + post-validation Consolidation
M5 cross-turn/restart/rollback/history/multi-claim E2E closure
```

Then:

```text
Affective -> Relationship -> Intimacy/Consent -> Goal/Intention
-> Reflection -> Opportunity -> Decision/Utility -> optional BT/FSM
-> Interaction Manager -> Cognitive LOD
-> RealizationPackage/Supervisor -> final Prompt -> real GGUF
-> OutputValidator -> full Consolidation -> World ActionResult loop
-> complete automatic E2E/regression
-> Android integration
-> Moto G56
```

---

## 10 — REOPEN MODULES ONLY WITH EVIDENCE

A closed module is reopened only when:

```text
real integration test demonstrates a defect
benchmark shows a material performance issue
security/data-integrity issue is found
owner approves a product requirement change
```

Then perform one bounded fix + regression + close again.

---

## 11 — CHATGPT WORK LEDGER

Current verified state:

```text
ACTIVE WORK SESSION SYNCHRONIZED HERE = NO
WORK OBJECTIVE = NONE VERIFIED
WORK REPO/BRANCH/HEAD = UNKNOWN until Work reports or repository-visible changes appear
```

Prepared next Work package exists but is **not running**:

```text
file = prompts/WORK_FOUNDATION_CLOSURE_F1_F2.md
task = Foundation Closure F1/F2 ONLY
repo = MATRIXNEO23/assembling
forbidden = durable Memory, Relationship/Decision implementation, model switch, Student-5, phone build, gate lowering
stop = F1/F2 evidence produced; do not auto-start Memory
```

When owner launches Work with this assignment, this chat supervises/audits and does not duplicate its implementation.

---

## 12 — OWNER LOCAL ENVIRONMENT

```text
assembling = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

Do not ask owner to manually create/copy project files unless unavoidable.

---

## 13 — GUARDS

```text
PHONE TEST = BLOCKED until automatic integrated baseline + Memory/E2E closure
CURRENT NLU = Student-4 v2.2A
STUDENT-5 = preserved but side work stopped unless explicitly reauthorized
MEMORYCANDIDATE WIP = separate backup, not canonical
NO model switch as side task
```

---

## 14 — EXACT RESTART POINT / EVERYTHING STILL TO DO

Current completed supervisor phase:

```text
COMPLETE REAL ENGINE DESIGN + DEEP RESEARCH = CURRENT PASS COMPLETE
DESIGN FILE = docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
DESIGN COMMIT = 89e25bd8fb3c27d826559ced5b629333a4a330e6
WORK PACKAGE FILE = prompts/WORK_FOUNDATION_CLOSURE_F1_F2.md
WORK PACKAGE COMMIT = 496364da773bbdc7d4bae19debb1c700ed42367f
PRODUCTION IMPLEMENTATION IN THIS PHASE = NONE
TEST/BUILD IN THIS PHASE = NONE
```

Everything remaining in order:

```text
1. owner review/accept complete design baseline
2. owner explicitly authorizes Foundation Closure F1/F2 implementation
3. launch/assign Work F1/F2 or execute authorized package in one workstream
4. F1/F2 implementation
5. demonstrative F1/F2 tests
6. observed-defect analysis/fixes
7. identical retest + repository regression
8. supervisor module/path verdict
9. Memory M1-M5 as one complete workstream
10. close Affective/Relationship/Intimacy/Goal owners
11. close Reflection/Opportunity/Decision/Interaction/LOD
12. close realization/GGUF/Validator/Consolidation/World action loop
13. full automatic E2E + fix/regression cycle
14. Android integration baseline
15. Moto G56 physical performance/behavior iteration
```

F1/F2 closure condition:

```text
canonical Understanding V3 claims reach a real immutable ContextSnapshot;
claim-bound Retrieval evidence/status reaches narrow Coherence and canonical Authority;
normal/multi-claim/ambiguous/NO_MATCH/UNAVAILABLE/ERROR/context-mismatch are demonstrated;
no semantic field is silently lost/invented;
legacy basic path is not authoritative;
no durable pre-validation write exists;
repository regression is green.
```

Memory closure after F1/F2:

```text
real canonical claims/resolutions persist through Room Admission/Repository;
structured semantic/entity/time retrieval works across turns/restart;
SUPERSEDE lineage/rollback/current-vs-history are demonstrated;
state-owner boundaries are preserved;
Memory/Authority/Understanding regressions are green.
```

### Next substantive action — explicit approval required

```text
FOUNDATION CLOSURE F1/F2 ONLY
```
