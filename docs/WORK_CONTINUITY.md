# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `foundation-closure-f1-f2`  
Continuity schema: `matrix.assembling.continuity.v80`

## 0 — NON-NEGOTIABLE WORK METHOD

A module is **not** considered working because it compiles, has DTO/contracts, serializes, or passes fixture-only/unit tests.

Required proof:

```text
REAL IMPLEMENTATION
-> REAL INPUT
-> REAL MODULE OUTPUT
-> FIELD-BY-FIELD EXPECTED/ACTUAL
-> REAL HANDOFF TO NEXT REAL MODULE
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
-> actual Student-4 model/runtime
-> actual decoder output
-> expected semantic comparison
-> IT/EN/ES stress
-> measured errors
-> fixes
-> identical retest/regression
-> downstream handoff proof
```

Never invent metrics. Never call fixture/DTO/CI-only success a proof that language comprehension works.

### Complete-design rule

Each module is designed against its real product responsibility before implementation: owner, inputs, outputs, read-only dependencies, forbidden responsibilities, ambiguity/error semantics, mobile constraints, diagnostics and downstream handoff. Foreseeable architecture must not be invented piecemeal while coding.

### Deep Research / reuse rule

Before substantial implementation/redesign:

```text
research mature algorithms/libraries/reference code
verify technical fit
verify license/provenance
verify Android/offline/Kotlin/JVM/C++ compatibility
assess CPU/RAM/latency/binary/dependency cost
classify DIRECT_REUSE / ADAPT / REIMPLEMENT / REFERENCE_ONLY / REJECT
record exact source/version/license before copying/adapting code
```

Do not bend Matrix around a library.

### No incomplete/hypothetical code

Forbidden: placeholders, TODO-as-implementation, unfinished/non-compiling code, fake output presented as real functionality.

### Owner workflow

Owner normally only:

```text
Fetch/Pull -> run exact requested test -> return output
```

### Approval rule

Before a new substantive module/workstream, state exact scope and wait for explicit owner approval. Continuity/checkpoint preservation is pre-authorized.

### Mandatory interruption rule

If owner interrupts active work, before unrelated work preserve:

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

This chat cannot silently launch or observe a separate Work session. When Work is active, do not duplicate its implementation; synchronize repository-visible checkpoints/results.

---

## 1 — REPOSITORY / BRANCH STATE

### `MATRIXNEO23/assembling`

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
PR #22 = merged explicit RetrievalResult claimId/contextSnapshotId binding
active branch = foundation-closure-f1-f2
branch start = 2d9dc760c72b6f9239fe3680f0df50b2f33442c4
F1/F2 code-tested HEAD = 5892dc478072e77e5412271f7e5638f9998413cc
F1/F2 evidence commit = ea6ab032fd009d69258e7f641ff25b71cc6c2cbf
previous continuity commit = bb1e8b6e21bcdb0768a0e6fe0f8c5260069b989d
PR #23 = DRAFT / NOT MERGED
```

### Other preserved branches

```text
functional-audit-understanding-authority = previous structured audit/design branch
backup-memorycandidate-wip = backup only
MemoryCandidate backup commit = 9e7413c
```

### `MATRIXNEO23/memoria`

```text
main HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
python-authority-p0-v1 final = f28fd33bbf3297072ef4873514ae0a551ea4576b
Authority PR #1 merge = b8cc7e2133868049550d3c63d78f69da8f830f20
```

`memoria/main` does not yet contain the full recovered historical Memory Foundation.

### Historical Android reference

```text
repo = MATRIXNEO23/8.10.9evo3-solo-gpt
reference HEAD = e97f75052afcc93d5b1e08b3ac881dba35633451
rule = selective reuse only; never restore old orchestrator wholesale
```

---

## 2 — COMPLETE REAL DESIGN BASELINE

Canonical document:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
commit = 89e25bd8fb3c27d826559ced5b629333a4a330e6
status = canonical design baseline
```

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
text interpreted once
no downstream linguistic reparse
one owner per mutable canonical state
Context immutable/read-only
UNKNOWN != UNRESOLVED != AMBIGUOUS != UNAVAILABLE != NO_MATCH != ERROR
interpretationConfidence != sourceReliability != authority != beliefConfidence != retrievalRelevance
contradiction != supersession
no durable state write before validation
Authority identifies contradiction identity
Memory Admission consumes it; never invents contradiction from text/shared actors
semantic Memory change -> supersede()
missing modules -> NOT_WIRED/UNAVAILABLE, never fake zero/default
```

Target normal cycle:

```text
Event/Input
-> Perception
-> NLU
-> Understanding V3 / TypedClaim[]
-> TurnWorkspace
-> read-only Context ENRICH [World/Memory/Belief/Relationship/Affective/Intimacy/Goal]
-> MatrixContextSnapshot
-> narrow Coherence
-> Authority/Belief
-> MemoryPreflight + state proposals
-> optional Reflection
-> Opportunity
-> Goal/Intention + Decision/Utility
-> manifestation policy
-> Realization Context Supervisor
-> Prompt Builder
-> GGUF
-> Output Validator
-> Persistent Consolidation
-> owner-specific durable commits
-> ActionIntent/World ActionResult where applicable
-> causal trace close
```

---

## 3 — F1/F2 STRUCTURED FOUNDATION STATUS

Implementation files:

```text
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationContracts.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalContextAssembler.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalRetrievalStage.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalCoherenceValidator.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationOrchestrator.kt
```

Updated surfaces:

```text
IntegrationPorts.kt -> CanonicalContextPort / CanonicalRetrievalPort / CanonicalCoherencePort
MatrixTurnFrame.kt -> claim-wise canonicalCoherenceResults + exact identity/context coverage validation
```

Demonstrative structured tests:

```text
CanonicalFoundationOrchestratorTest.kt
CanonicalRetrievalErrorTraceTest.kt
```

Tested structured path:

```text
canonical Understanding V3 fixture
-> immutable ContextSnapshot
-> claim-bound Retrieval
-> claim-wise Coherence
-> CanonicalUnderstandingV3AuthorityPort
-> DeterministicAuthorityResolver
-> canonical AuthorityResolution
```

Passing structured cases:

```text
single claim
multi-claim separated
ambiguous claim -> HOLD
NO_MATCH
INDEX_UNAVAILABLE
ERROR with firstDivergence=RETRIEVAL.RESULT_ERROR.<claimId>
stale context binding -> fail closed
```

CI:

```text
#162 / 34078896297 = SUCCESS
#163 / 34078966250 = SUCCESS / BUILD SUCCESSFUL / full regression
#164 / 34079096730 = SUCCESS
#165 = SUCCESS after evidence doc
#166 / 34079306069 = SUCCESS on continuity HEAD bb1e8b6...
```

Strict verdict:

```text
STRUCTURED F1/F2 WIRING = VERIFIED FOR TESTED CASES
REAL LANGUAGE COMPREHENSION = NOT YET DEMONSTRATED
MEMORY = NOT IMPLEMENTED
FULL ENGINE = NOT DEMONSTRATED
PHONE = BLOCKED
```

Do not call F1/F2 a real functioning cognitive module until raw model input/output + downstream handoff are proven.

---

## 4 — CURRENT NLU ARTIFACT / REAL-RUNTIME EVIDENCE

Canonical Student-4 runtime candidate:

```text
variant = Student-4 v2.2A mixed/head-protected
status = EXPERIMENTAL_TEST_CANDIDATE / NOT_PRODUCTION_APPROVED
training run = 33860928806
training HEAD = 617aeca8a6abac4366eb13347fb88026307dc3b8
model-state SHA-256 = 446b6a58265500001efd350f81d75867227cbfd3c6da699ed9c9330257d16a9c
bundle artifact ID = 9938150338
bundle artifact digest SHA-256 = f2dfea052df525af741f8ddd98e279b5443645450d09f949970a6eac18edb987
```

Durable candidate ZIP in assembling/LFS metadata:

```text
file = matrix-nlu-student-4-v22a-mixed-head-protected-local-20260904T1440Z.zip
expected bytes = 356134801
expected SHA-256 = 4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5
repo LFS pointer itself = 134 bytes, not model bytes
```

Runtime ONNX:

```text
file = matrix-nlu-mixed-head-protected-int8.onnx
expected bytes = 149711344
expected SHA-256 = 738a4d052790367509d55487b649b71aaa029d839135693bb5be46f74d55ef70
opset = 17
fixed sequence length = 64
quantization = dynamic INT8 encoder + Matrix heads FP32
```

Protected heads:

```text
token.boundary/object/subject/negation/temporal/entity
sequence.dialogueAct/predicate/subjectReferent/targetReferent/ownerReferent/
perspectiveReferent/polarity/temporalRelation/claimKind
```

Known lab caveat: dev gate not production-approved; one Spanish parity probe changes dialogueAct after encoder quantization.

### Real artifact downloaded in current session

Immediately before the owner interruption, the original GitHub Actions artifact was downloaded directly:

```text
source repo = MATRIXNEO23/matrix-understanding-lab
artifact ID = 9938150338
local tool artifact name = student4-v22a-bundle.zip
mounted path supplied by runtime = /mnt/data/student4-v22a-bundle.zip
file id = file_00000000ed90820ab2745db26b33f427
```

**IMPORTANT: ZIP content listing, local bytes/checksum validation and inference had NOT yet been executed when interrupted.**

---

## 5 — CRITICAL DISCOVERY: STUDENT-4 V2.2A IS NOT V3

This was verified immediately before interruption by comparing the actual lab decoder/contracts.

### Student-4 v2.2A label contract (`matrix_nlu/labels.py`)

Sequence heads include:

```text
dialogueAct = ASSERT/CORRECT/QUESTION/REQUEST/HYPOTHESIS/UNKNOWN
predicate = same core predicate family
subjectReferent = SPEAKER/OBSERVER/KNOWN_ENTITY/RECENT_ENTITY/UNKNOWN
targetReferent = NONE/SELF/SPEAKER/OBSERVER/KNOWN_ENTITY/RECENT_ENTITY/UNKNOWN
ownerReferent = SUBJECT/SPEAKER/OBSERVER/KNOWN_ENTITY/RECENT_ENTITY/UNKNOWN
perspectiveReferent = SPEAKER/SUBJECT/OBSERVER/KNOWN_ENTITY/RECENT_ENTITY/UNKNOWN
polarity
temporalRelation = ATEMPORAL/CURRENT/PAST/FUTURE/UNKNOWN
claimKind = EXPLICIT/HYPOTHESIS
```

There is **no independent `sourceReferent` head** in Student-4 v2.2A.

### V3 contract (`matrix_nlu/contract_v3.py`)

V3 requires 10 sequence heads including independent:

```text
sourceReferent
```

and changes semantics/vocabularies:

```text
claimKind = DIRECT/REPORT/BELIEF/HYPOTHESIS/UNKNOWN
DialogueAct no longer uses HYPOTHESIS as an act
role values SELF/SUBJECT/KNOWN_ENTITY/RECENT_ENTITY are forbidden final V3 role values
V3 roles are candidate-table pointers + NONE/UNKNOWN
V3 temporal relations include BEFORE/AFTER/DURING/RECURRENT/AT_REFERENCE
```

### Binding decision

```text
DO NOT create a fake "complete Student-4 -> V3" adapter.
```

Such an adapter would necessarily invent at least source identity / claim-kind semantics / candidate-pointer semantics for some inputs.

Correct real-proof plan:

```text
A. run Student-4 v2.2A exactly with its audited ONNX decoder on raw text
B. record actual Student-4 output field-by-field
C. compare that output against expected semantics
D. define only lossless mappings into current canonical structures
E. stop/fail closed on V3 fields Student-4 cannot represent
F. decide from evidence whether a bounded compatibility layer is sufficient or whether the NLU contract/model must genuinely be extended
```

Do not switch to Student-5 automatically. Do not manufacture V3 data to make the pipeline green.

---

## 6 — LAB RUNTIME SOURCE VERIFIED

Actual Student-4 decoder source:

```text
MATRIXNEO23/matrix-understanding-lab@5e49a2d81ecf054a543e3e92f76260fafeca9cc6
matrix_nlu/inference.py
```

`OnnxMatrixNluRuntime` uses:

```text
AutoTokenizer from bundle/tokenizer, local_files_only
ONNX Runtime CPUExecutionProvider
fixed maxLength from training-result.json
output order from TOKEN_LABELS + SEQUENCE_LABELS + MASSIVE heads
same audited decoder as PyTorch path
BIO claim-boundary decoding
per-claim rerun
decoded sequence heads + token spans + confidence
context binding for legacy Student-4 referents
worldTruthUpdates = 0
```

The same `inference.py` and `labels.py` are already vendored in `assembling/vendor/matrix-understanding-lab/matrix_nlu/`.

V3 decoder source checked separately:

```text
matrix_nlu/inference_v3.py
matrix_nlu/contract_v3.py
```

V3 validates already-learned V3 outputs; it is not a legitimate way to invent missing Student-4 heads.

---

## 7 — CURRENT INTERRUPTED ACTIVITY / LAVORO INTERROTTO

Owner interruption: **"cosa cè dentro?"** while supervisor was about to inspect the downloaded Student-4 artifact.

Exact activity at interruption:

```text
REAL STUDENT-4 PROOF PREPARATION
```

Completed before interruption:

```text
1. owner authorized proceeding with real proof
2. PR #23 intentionally NOT merged
3. verified Student-4 artifact manifest/checksums
4. verified no concrete MatrixNluV3RuntimeBridge implementation exists in assembling
5. inspected exact CanonicalUnderstandingV3Adapter boundary
6. verified original Student-4 inference.py decoder and label vocabularies
7. verified V3 inference/contract separately
8. demonstrated Student-4 v2.2A != V3 and documented why a complete adapter would fabricate data
9. downloaded original Student-4 Actions artifact ID 9938150338 to /mnt/data/student4-v22a-bundle.zip
```

Not yet done when interrupted:

```text
1. list ZIP members and sizes
2. calculate local downloaded ZIP bytes + SHA-256
3. compare against authoritative artifact digest/manifest as applicable
4. inspect bundle layout: training-result.json, labels.json, tokenizer, ONNX/models, manifests/checksums
5. identify exact mixed INT8 ONNX location inside artifact or retrieve the separate mixed-quant artifact if this bundle does not contain it
6. verify Python runtime dependencies available
7. execute audited OnnxMatrixNluRuntime on real raw phrases
8. record raw outputs, decoded labels/spans/confidences
9. build expected-vs-actual IT/EN/ES demonstrative suite
10. characterize observed failures
11. design only lossless downstream compatibility mapping
12. verify downstream Context/Coherence/Authority only for representable information
13. fail closed for nonrepresentable V3 fields
14. permanent regression tests
15. full regression
16. update evidence + continuity
```

Exact restart action:

```text
inspect /mnt/data/student4-v22a-bundle.zip contents and checksum first
```

Closure condition for this real-proof workstream:

```text
real raw phrases execute through the actual authorized Student-4 ONNX artifact and audited decoder;
actual semantic outputs are compared field-by-field to expected results across IT/EN/ES;
all mismatches are measured and analyzed;
no V3 field is invented;
representable outputs have a proven lossless downstream route;
nonrepresentable outputs fail closed explicitly;
fix/retest/regression evidence is recorded before any claim that NLU/Understanding functions.
```

---

## 8 — MEMORY FOUNDATION — CURRENT STATUS

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

Recovered/reference tests:

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

Mandatory semantics:

```text
atomic transaction + rollback
SAVE/read
metadata-only reinforcement
semantic evolution only through supersede()
root revisionOf + sequential supersededBy
protected lineage delete
explicit contradiction identity
Admission SAVE/SUPERSEDE/REJECT/IGNORE
restart persistence
```

Final MemoryRecord must support retrieval across all useful information categories through separate axes:

```text
memory kind EPISODIC/SEMANTIC/REFLECTION
semanticDomain + predicateId
subject/target/owner/source/perspective/observer
entity/actor refs
typed object/value + type
polarity/modality
eventTime/validFrom/validTo/observedAt/recordedAt/anchor
authority/sourceReliability/provenance
current/history/lifecycle
lineage/contradiction
salience/importance/retention/reinforcement/access metadata
```

Relationship/Affective/Goal/Intimacy remain separate current-state owners; Memory may preserve historical evidence about those domains.

Retrieval target:

```text
INDEX_PROBE every normal turn
HYDRATE_AND_RERANK on relevant candidates
DEEP_OR_MULTI_HOP only for explicit complex purpose
hard filters -> Room/SQLite indexes -> FTS/BM25 -> optional vector if benchmarked -> calibrated hybrid/RRF -> optional MMR -> current/history policy
```

---

## 9 — DEEP RESEARCH / REUSE DECISIONS

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
Graphiti                  Apache-2.0     REFERENCE_ONLY
Mem0/LangMem/Letta        various         REFERENCE_ONLY
Generative Agents         research        REFERENCE_ONLY
AGM belief revision       research        REFERENCE_ONLY
```

Historical Android selective reuse candidates:

```text
WorldMatrixProtocol
PerceptionEnvelope validation
MatrixEngineFramework Stage pattern
WorkingMemory concepts
HierarchicalMemoryIndex/Retrieval + SubjectOwnerGuard
ContextPackageBuilder / ContextSupervisor
Reflection trigger/queue patterns
AgentStateGoalIntention separation
CausalTrace
MemoryPersistenceAdapter separation
```

Do not restore:

```text
old Android orchestrator wholesale (pre-validation Memory writes)
old regex MemoryAcquisitionClassifier as production parser
old local text/slot conflict authority
```

---

## 10 — REMAINING ENGINE WORK AFTER CURRENT REAL-NLU PROOF

After the real Student-4 proof is resolved, proceed according to actual dependency evidence. Major remaining workstreams already designed:

```text
Memory M1-M5 complete workstream
BeliefState final owner
Affective cleanup/appraisal/mood
Relationship directional owner
Intimacy/Consent
Goal/Intention BDI-lite
Reflection three levels
Opportunity
Decision/Utility + manifestation policy
optional BT/FSM tactical layer
Interaction Manager
Cognitive LOD
RealizationPackage / Context Supervisor
final Prompt Builder realization-only
real GGUF adapter
Output Validator
Persistent Consolidation
World ActionIntent/ActionResult loop
complete Causal Trace
full automatic E2E
Android integration
Moto G56
```

Phone remains blocked until Memory + module demonstrations + full E2E baseline.

---

## 11 — OWNER LOCAL ENVIRONMENT

```text
repo = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

Owner should not be asked to manually create/copy source files unless unavoidable.

---

## 12 — CURRENT GUARDS

```text
PR #23 DRAFT / do not merge without explicit owner approval
CURRENT NLU = Student-4 v2.2A
STUDENT-5 = preserved / stopped unless explicitly reauthorized
MemoryCandidate WIP = backup only
NO model switch as side task
NO fake Student-4->V3 semantic completion
NO durable pre-validation writes
PHONE TEST = BLOCKED
```
