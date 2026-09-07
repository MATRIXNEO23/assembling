# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v79`

## 0 — NON-NEGOTIABLE WORK METHOD

### Real-module proof

A module is not considered working because it compiles, has contracts/DTOs, serializes, or passes fixture-only tests.

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
-> IDENTICAL RETEST
-> REGRESSION
-> ONLY THEN CLOSE
```

For NLU/Understanding specifically:

```text
raw text
-> actual Student-4 runtime/model
-> actual structured output
-> expected semantic comparison
-> IT/EN/ES stress
-> measured errors
-> fixes
-> identical retest/regression
-> downstream proof
```

Never invent metrics. Never call a module functional merely because structured fixtures passed.

### Complete-design-before-piecewise-build

Every module is designed according to its real job in the final Matrix Engine before implementation. Define responsibility, state ownership, exact inputs/outputs, read-only dependencies, forbidden responsibilities, ambiguity/error semantics, mobile constraints, diagnostics and downstream handoff.

Foreseeable architecture must not be invented incrementally while coding. Later changes are allowed when tests, benchmarks, integrity/security evidence or owner-approved product requirements justify them.

### Deep Research / reuse

Before implementing or redesigning a substantial module:

```text
research mature algorithms/libraries/reference code
verify task fit and current maintenance
verify license/provenance
verify Android/offline/Kotlin/JVM/C++ compatibility
assess CPU/RAM/latency/binary/dependency cost
classify DIRECT_REUSE / ADAPT / REIMPLEMENT / REFERENCE_ONLY / REJECT
record exact source/version/license before copying code
```

Do not bend Matrix architecture around an external library.

### No incomplete/hypothetical implementation

Forbidden: placeholders, TODO-as-implementation, unfinished/non-compiling code, fake module output presented as functionality.

### Owner workflow

Owner normally only:

```text
Fetch/Pull -> run exact requested test -> return output
```

Repository changes are prepared online whenever possible.

### Approval rule

Before a new substantive implementation/module/workstream, state exact scope and wait for explicit owner approval. Continuity/checkpoint preservation is pre-authorized.

### Mandatory interruption rule

If interrupted during active work, before unrelated work preserve:

```text
repo/branch/HEAD
exact interrupted operation
completed substeps/files
all tests/results/defects
architecture/research decisions
artifacts/models/datasets/checksums
EVERYTHING still to do in order
pending fixes/tests/regressions
exact restart action
closure condition
```

### Chat + Work coordination

```text
THIS CHAT = supervisor / architecture / audit / evidence / continuity
CHATGPT WORK = substantial authorized execution when owner launches Work mode
```

This chat cannot silently launch/observe a separate Work session. When Work is active, do not duplicate its implementation; synchronize repository-visible checkpoints/results.

---

## 1 — REPOSITORY STATE

### Main

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
PR #22 = merged retrieval claim/context binding
```

### Current F1/F2 branch

```text
branch = foundation-closure-f1-f2
branch base/start = 2d9dc760c72b6f9239fe3680f0df50b2f33442c4
code-tested HEAD = 5892dc478072e77e5412271f7e5638f9998413cc
evidence doc commit = ea6ab032fd009d69258e7f641ff25b71cc6c2cbf
HEAD before this continuity update = ea6ab032fd009d69258e7f641ff25b71cc6c2cbf
PR = #23 DRAFT
PR base = main
status = NOT MERGED / owner merge approval not given
```

### Previous audit branch preserved

```text
branch = functional-audit-understanding-authority
last documented head before F1/F2 branch = 2d9dc760c72b6f9239fe3680f0df50b2f33442c4
contains structured multi-claim retrieval-binding runtime fix and design/research baseline
```

### MemoryCandidate backup

```text
branch = backup-memorycandidate-wip
commit = 9e7413c
status = BACKUP ONLY / NOT CANONICAL / DO NOT AUTO-MERGE
```

### Memory reference repo

```text
repo = MATRIXNEO23/memoria
main HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
python Authority P0 branch final = f28fd33bbf3297072ef4873514ae0a551ea4576b
Authority PR #1 merge = b8cc7e2133868049550d3c63d78f69da8f830f20
```

`memoria/main` does not contain the full recovered historical Memory Foundation yet.

### Historical Android source

```text
repo = MATRIXNEO23/8.10.9evo3-solo-gpt
reference HEAD = e97f75052afcc93d5b1e08b3ac881dba35633451
role = source/reference for selective porting only
```

Do not restore the historical Android orchestrator wholesale.

---

## 2 — CANONICAL DESIGN BASELINE

Canonical complete design:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
status = CANONICAL DESIGN BASELINE
```

Prepared F1/F2 bounded task:

```text
prompts/WORK_FOUNDATION_CLOSURE_F1_F2.md
commit = 496364da773bbdc7d4bae19debb1c700ed42367f
```

F1/F2 owner authorization received in chat on 2026-09-07 after coherence/ambiguity/adaptation review.

Fundamental separation:

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL
!= RELATE != CONSENT != WANT != DECIDE != EXPRESS
```

Canonical state owners:

```text
WORLD objective state              -> WORLD / APPLICATION ADAPTER
linguistic interpretation          -> NLU + UNDERSTANDING
belief / epistemic commitment      -> BELIEF / AUTHORITY
long-term autobiographical memory  -> MEMORY
transient emotion + mood           -> AFFECTIVE
relationship state                 -> RELATIONSHIP
current intimacy / consent state   -> INTIMACY
active goals / intentions          -> GOAL
behavioral choice                  -> DECISION
language realization              -> GGUF
post-validation commit routing     -> PERSISTENT CONSOLIDATION
```

Hard invariants:

```text
TEXT interpreted once
no downstream linguistic reparse
one owner per canonical mutable state
Context is immutable/read-only
UNKNOWN != UNRESOLVED != AMBIGUOUS != UNAVAILABLE != NO_MATCH != ERROR
interpretationConfidence != sourceReliability != authority != beliefConfidence != retrievalRelevance
contradiction != supersession
no durable Memory/state writes before VALIDATE
Authority identifies contradiction identity
Memory Admission consumes it; never invents conflict from text/shared actors
semantic Memory change -> supersede()
missing modules -> NOT_WIRED/UNAVAILABLE, never fake zero/default state
```

Normal target cycle:

```text
World/User/NPC/System Event
-> Perception Gateway
-> NLU Runtime
-> Understanding V3 / TypedClaim[]
-> TurnWorkspace
-> parallel read-only Context ENRICH
   [World, Memory index, Belief, Relationship, Affective, Intimacy, Goal]
-> immutable MatrixContextSnapshot
-> narrow Coherence
-> Authority / Belief
-> MemoryPreflight + state evaluations/proposals
-> Reflection when triggered
-> Opportunity
-> Goal/Intention + Decision/Utility
-> manifestation policy where relevant
-> Realization Context Supervisor
-> Prompt Builder
-> GGUF
-> Output Validator
-> Persistent Consolidation
-> owner-specific durable commits
-> ActionIntent -> World ActionResult when applicable
-> causal trace close
```

---

## 3 — F1/F2 FOUNDATION CLOSURE — IMPLEMENTED AND TESTED

Detailed evidence:

```text
docs/F1_F2_FOUNDATION_EVIDENCE_2026-09-07.md
commit = ea6ab032fd009d69258e7f641ff25b71cc6c2cbf
```

### New canonical runtime files

```text
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationContracts.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalContextAssembler.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalRetrievalStage.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalCoherenceValidator.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationOrchestrator.kt
```

### Updated foundation surfaces

```text
src/main/kotlin/matrix/assembling/IntegrationPorts.kt
- CanonicalContextPort
- CanonicalRetrievalPort
- CanonicalCoherencePort

src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt
- canonicalCoherenceResults claim-wise slot
- exact claim coverage/context binding validation
- Context/Retrieval/Coherence diagnostics
```

### New demonstrative tests

```text
src/test/kotlin/matrix/assembling/canonical/CanonicalFoundationOrchestratorTest.kt
src/test/kotlin/matrix/assembling/canonical/CanonicalRetrievalErrorTraceTest.kt
```

### Canonical path now demonstrated for structured V3 input

```text
canonical Understanding V3
-> CanonicalContextAssembler
-> immutable MatrixContextSnapshot
-> claim-bound RetrievalQuery/Result
-> CanonicalCoherenceValidator
-> CanonicalUnderstandingV3AuthorityPort
-> DeterministicAuthorityResolver
-> canonical AuthorityResolution
```

### Context behavior

```text
LINGUISTIC = AVAILABLE
SYSTEM = AVAILABLE
MEMORY default pre-backend = UNAVAILABLE
future state domains = NOT_WIRED
```

Each claim has an exact LINGUISTIC context reference; the full canonical claim remains in `canonicalUnderstandingV3` and is not replaced by the reference.

### Retrieval behavior

One query/result per claim when applicable. Binding requires exact:

```text
queryId
claimId
contextSnapshotId
```

No list-order guessing. No claim-id parsing from query text. No natural-language reparse.

```text
NO_MATCH != INDEX_UNAVAILABLE != ERROR
```

Stale context binding fails as `RETRIEVAL.BINDING_MISMATCH`.

Retrieval ERROR records exact first divergence:

```text
RETRIEVAL.RESULT_ERROR.<claimId>
```

### Canonical Coherence after F2

Coherence only validates semantic/structural stability from V3:

```text
structuralStatus
interpretationStatus
dialogueAct field status
predicate field status
subjectReferent field status
polarity field status
temporalRelation field status
```

It does NOT classify REPORT/BELIEF authority, identify Memory contradiction, decide persistence, choose behavior or reparse text.

No arbitrary numeric confidence threshold was added because no calibrated F1/F2 evidence justifies one.

### Demonstrative cases — all passing

```text
A. normal single claim
B. multi-claim separated through Context/Retrieval/Coherence/Authority
C. ambiguous semantic claim -> Coherence HOLD -> Authority skipped
D. Memory/index unavailable -> Retrieval INDEX_UNAVAILABLE -> Authority UNAVAILABLE
E. Retrieval ERROR -> Authority ERROR + exact first divergence
F. stale context binding -> fail closed before Coherence/Authority
```

### Field audit

```text
semantic fields silently lost = NONE demonstrated
semantic fields intentionally changed = NONE
semantic fields invented = NONE
```

System-generated operational metadata is expected and traceable:

```text
contextSnapshotId
context entry ID
retrieval queryId
Authority resolutionId
reason codes
diagnostic events
```

Existing Authority V3 projection regressions remain green and preserve source/subject/owner/perspective/object/claimKind/temporal anchor/claim identity/provenance.

### CI evidence

```text
run #162 / 34078896297
HEAD = 3c63757ec5f4b7c17b7322449422a7b09c698b2b
full repo suite = SUCCESS

run #163 / 34078966250
HEAD = 49204a97197e848156c345ae472ec6add3c884a6
BUILD SUCCESSFUL
compileKotlin PASS
compileTestKotlin PASS
test PASS
full repository regression PASS

run #164 / 34079096730
HEAD = 5892dc478072e77e5412271f7e5638f9998413cc
full repo suite = SUCCESS
includes permanent assertion:
RetrievalStatus.ERROR -> firstDivergence=RETRIEVAL.RESULT_ERROR.c0
```

### Strict F1/F2 verdict

```text
CANONICAL STRUCTURED FOUNDATION F1/F2 = PASS FOR TESTED SCOPE
RAW-TEXT STUDENT-4 EXECUTION = NOT YET DEMONSTRATED IN ASSEMBLING
MEMORY PERSISTENCE = NOT IMPLEMENTED
FULL ENGINE = NOT YET DEMONSTRATED
PHONE = NOT AUTHORIZED YET
```

F1/F2 STOP condition reached. Do not automatically continue into Memory without owner authorization.

---

## 4 — PREVIOUS IMPORTANT EVIDENCE — PRESERVE / DO NOT REDO

### Understanding V3 contract/adapter

```text
PR #19
head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Correct interpretation: code/contract boundary closed; raw-text language comprehension not proven by this.

### Understanding V3 -> Authority wiring

```text
PR #21
head = b7ea750665a563cbca673b4050a72adc21275a8e
merge = d3994e59008aac648576a252eac0d7c4e1028589
CI = 34020878347 SUCCESS
```

### Retrieval contract/wire

PR #22 added explicit `claimId` + `contextSnapshotId` query/result binding and merged to main.

### Structured multi-claim runtime defect

Initial functional audit demonstrated multi-claim binding failure. Fix on predecessor branch changed `CanonicalUnderstandingV3AuthorityPort` to consume explicit binding fail-closed; owner rerun demonstrated single + multi structured path PASS. Permanent regression is preserved.

### Python Authority P0

Preserved rules:

```text
no hardcoded owner
no free-text property regex in Authority
same actor/shared entity/text difference != contradiction
structured subject/predicate/owner/target/source/perspective/time/polarity/value comparison
Authority never writes Memory
```

---

## 5 — CURRENT NLU ARTIFACT — PRESERVE

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

Protected heads:

```text
token.boundary/object/subject/negation/temporal/entity
sequence.dialogueAct/predicate/subjectReferent/targetReferent/ownerReferent/
perspectiveReferent/polarity/temporalRelation/claimKind
```

Known lab caveat: experimental candidate; dev gate was not production-approved. Do not silently replace with Student-5.

Deep Research runtime decision:

```text
ONNX Runtime Mobile Android = DIRECT_REUSE / MIT
initial device benchmark = CPU/XNNPACK, then NNAPI
reduced-operator build only if measured benefit justifies complexity
```

---

## 6 — MEMORY FOUNDATION — CURRENT STATUS AND REQUIRED COMPLETE WORKSTREAM

```text
MEMORY = NOT IMPLEMENTED / NOT INTEGRATED
```

Recovered historical Python reference from owner Library:

```text
memory/__init__.py
memory/models.py
memory/schema.py
memory/database.py
memory/repository.py
memory/admission.py
memory/admission_models.py
```

Recovered/reference tests include:

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

Mandatory semantics to preserve/adapt:

```text
atomic transaction + rollback
SAVE + read
metadata-only reinforcement/update
semantic evolution only through supersede()
revisionOf -> root
supersededBy -> sequential successor
protected lineage delete
explicit contradiction identity
Admission SAVE/SUPERSEDE/REJECT/IGNORE
restart persistence
```

### Final Memory must accept/retrieve all useful information categories

`EPISODIC / SEMANTIC / REFLECTION` are memory kinds, not the subject taxonomy.

Final MemoryRecord must preserve/index at least:

```text
semanticDomain + predicateId
subject / target / owner / source / perspective / observer as applicable
entity/actor refs
typed object/value + value type
polarity/modality
eventTime / validFrom / validTo / observedAt / recordedAt / temporal anchor
authority / epistemic class
sourceReliability
provenance event/observation/claim/AuthorityResolution/derived refs
validity/current/history
revisionOf/supersededBy/revisionCount/contradiction id
salience/importance/retention/reinforcement/access metadata
optional embedding/index version only if benchmarked
```

Memory may retain historical evidence about Relationship/Affective/Goal/Intimacy, but those current states are owned by their dedicated modules.

### Retrieval architecture

```text
LEVEL 1 INDEX_PROBE every normal turn
LEVEL 2 HYDRATE_AND_RERANK only relevant candidates
LEVEL 3 DEEP_OR_MULTI_HOP only explicit complex purpose
```

Target approach:

```text
hard structured filters
-> Room/SQLite indexed retrieval
-> FTS/BM25
-> optional vector only if benchmark useful
-> benchmark calibrated weighted fusion vs RRF
-> optional MMR for near-duplicate diversity
-> explicit current/history/superseded policy
```

`NO_MATCH != UNAVAILABLE != ERROR` remains mandatory.

### Memory next workstream — execute as one coherent package

After owner authorization:

```text
M1 — finalize MemoryRecord + query/index contract against complete real design
M2 — Room Repository + transactions + Admission
M3 — structured index/FTS/BM25/hybrid/current-history retrieval
M4 — MemoryPreflight + post-validation PersistentConsolidation binding
M5 — demonstrative cross-turn/restart/rollback/lineage/multi-claim/history E2E
```

Do not jump between Memory and unrelated modules while this workstream is open except for required upstream bug fixes demonstrated by tests.

---

## 7 — OLD ANDROID CODE: REUSE/REJECT DECISIONS

Selective reuse candidates:

```text
WorldMatrixProtocol                    ADAPT/PORT
PerceptionEnvelope validation          ADAPT/PORT
MatrixEngineFramework Stage pattern    ADAPT
WorkingMemory concepts                 ADAPT
HierarchicalMemoryIndex/Retrieval      ADAPT/benchmark
SubjectOwnerGuard                      ADAPT
HybridMemoryRanker                     REFERENCE/BENCHMARK, old fixed weights not canonical
ContextPackageBuilder                  ADAPT after Decision
ContextSupervisor                      ADAPT after Decision
Reflection trigger/queue patterns      ADAPT
AgentStateGoalIntention separation     ADAPT
CausalTrace                            ADAPT/PORT
MemoryPersistenceAdapter separation    ADAPT
```

Do not restore:

```text
old MatrixEngineOrchestrator wholesale
- defect: persisted Memory before output validation

old MemoryAcquisitionClassifier as production parser
- defect: reparses free text with regex

old reconciliation conflict authority
- defect: local text/slot heuristics instead of canonical Authority contradiction identity
```

---

## 8 — DEEP RESEARCH / REUSE MATRIX

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
gdx-ai                    Apache-2.0     ADAPT/selective BT/FSM
RRF                       algorithm       REIMPLEMENT/benchmark
MMR                       algorithm       REIMPLEMENT/optional
Graphiti                  Apache-2.0     REFERENCE_ONLY; stack too heavy for direct mobile use
Mem0                      ecosystem       REFERENCE_ONLY
LangMem                   Python          REFERENCE_ONLY
Letta                     platform        REFERENCE_ONLY
Generative Agents         research        REFERENCE_ONLY
AGM belief revision       research        REFERENCE_ONLY
```

Before copying/adapting external code, record exact repository/version/commit/license.

---

## 9 — ENGINE WORK REMAINING AFTER MEMORY

Known final modules/workstreams already designed; do not invent them ad hoc later:

```text
real Student-4 ONNX runtime + raw-language Understanding proof
BeliefState final owner
Affective cleanup/appraisal/current mood
Relationship directional persistent owner
Intimacy/Consent contextual owner
Goal/Intention BDI-lite
Reflection micro/decision/macro bounded
Opportunity Manager
Decision/Utility + manifestation policy
optional gdx-ai BT/FSM tactical layer only where useful
Interaction Manager
Cognitive LOD scheduler
RealizationPackage / Context Supervisor
final Prompt Builder = realization only
real GGUF adapter
Output Validator
Persistent Consolidation for all state owners
World ActionIntent/ActionResult loop
complete Causal Trace
```

Canonical order after Memory should respect actual dependencies and close each module with real demonstrative tests/fixes/regression.

---

## 10 — COMPLETE ENGINE E2E BEFORE PHONE

Required proof path eventually:

```text
real raw input/event
-> Perception
-> real Student-4 NLU
-> Understanding V3
-> Context/Memory retrieval
-> Coherence
-> Authority/Belief
-> Memory/state proposals
-> Affective/Relationship/Intimacy/Goal
-> Opportunity/Decision
-> Prompt/GGUF
-> OutputValidator
-> PersistentConsolidation
-> owner-specific durable state
-> later-turn retrieval/use
-> World result where applicable
-> complete diagnostics
```

Required properties:

```text
no illegal direct persistence
no silent semantic field loss
no invented canonical state without provenance
P0 runtime defects eliminated
blocking regressions green
first divergence + reason codes observable
```

Only then build/test the integrated baseline on Moto G56.

Phone goal = solid improvable baseline, then CPU/RAM/PSS/latency/thermal/stability/quality iteration.

---

## 11 — MODULE REOPEN POLICY

After a module workstream closes, reopen it only for:

```text
real integration test demonstrates a defect
benchmark demonstrates material performance problem
data-integrity/security issue
owner-approved product requirement change
```

Then apply one bounded fix + permanent regression + close again.

---

## 12 — CHATGPT WORK LEDGER

```text
ACTIVE SEPARATE WORK SESSION SYNCHRONIZED = NO
```

F1/F2 was executed directly through repository tools in this chat because this chat cannot launch a separate Work session itself. No claim is made that a separate Work session ran.

For the next heavy authorized workstream, Work may be launched by owner and given the canonical design/continuity/evidence files. This chat then supervises without duplicating active implementation.

---

## 13 — OWNER LOCAL ENVIRONMENT

```text
repo path = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

Owner should not be asked to create/copy source files manually unless unavoidable.

---

## 14 — GUARDS

```text
PR #23 = DRAFT / DO NOT MERGE WITHOUT OWNER APPROVAL
PHONE TEST = BLOCKED until Memory + remaining module demonstrations + full E2E baseline
CURRENT NLU = Student-4 v2.2A
STUDENT-5 = preserved / side work stopped unless explicitly reauthorized
MEMORYCANDIDATE WIP = backup only
NO model switch as side task
NO durable pre-validation write
```

---

## 15 — EXACT RESTART POINT / EVERYTHING STILL TO DO

### Completed current workstream

```text
COMPLETE REAL ENGINE DESIGN + DEEP RESEARCH = COMPLETE FOR CURRENT DESIGN PASS
F1/F2 CANONICAL STRUCTURED FOUNDATION = IMPLEMENTED + DEMONSTRATED + FULL REGRESSION GREEN
F1/F2 code-tested HEAD = 5892dc478072e77e5412271f7e5638f9998413cc
F1/F2 evidence = docs/F1_F2_FOUNDATION_EVIDENCE_2026-09-07.md
PR = #23 DRAFT
Memory = NOT STARTED
```

### Remaining work in order

```text
1. owner reviews F1/F2 verdict and decides whether/when PR #23 may merge
2. explicit owner authorization for next Memory M1-M5 workstream
3. Memory final schema/index contract against real retrieval needs
4. Memory Room Repository + Admission + atomic persistence
5. Memory retrieval/index/BM25/hybrid/current-history
6. MemoryPreflight + post-validation Consolidation
7. Memory cross-turn/restart/rollback/lineage/history/multi-claim tests
8. fix all observed Memory defects + regression
9. raw Student-4 ONNX execution/Understanding proof and measured IT/EN/ES semantic test closure as scheduled in dependency plan
10. close Belief/Affective/Relationship/Intimacy/Goal modules
11. close Reflection/Opportunity/Decision/Interaction/LOD
12. close realization/GGUF/Validator/full Consolidation/World result loop
13. complete automatic E2E + defect fixes/regression
14. Android integration baseline
15. Moto G56 physical testing and iterative improvement
```

### Next substantive action — explicit owner approval required

```text
MEMORY M1-M5 COMPLETE WORKSTREAM
```

Do not automatically start Memory or merge PR #23.
