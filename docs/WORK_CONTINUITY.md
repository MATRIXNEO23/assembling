# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active work branch: `functional-audit-understanding-authority`  
Continuity schema: `matrix.assembling.continuity.v75`

## 0 — NON-NEGOTIABLE WORK METHOD

This section is canonical and supersedes earlier interpretations of “complete” or “working”.

### Demonstration criterion

A module is **not** considered working merely because it compiles, DTOs/contracts exist, serialization passes, unit/contract tests pass, or a test with prebuilt structured fixtures is green.

Required closure sequence:

```text
1. REAL IMPLEMENTATION PRESENT
2. REAL INPUT -> REAL MODULE OUTPUT
3. VERIFY OUTPUT FIELD-BY-FIELD AGAINST EXPECTED BEHAVIOR
4. VERIFY OUTPUT REACHES THE NEXT REAL MODULE
5. NORMAL + AMBIGUOUS + ERROR CASES
6. STRESS / COVERAGE TESTS FOR SEMANTIC OR LEARNED MODULES
7. ERROR ANALYSIS WITH COUNTS/CATEGORIES
8. FIX BASED ON OBSERVED ERRORS
9. RETEST SAME SUITE
10. REGRESSION TESTS
11. ONLY THEN CLOSE THE MODULE CHECKPOINT
```

For NLU/Understanding specifically:

```text
raw text
-> actual runtime/model
-> actual structured output
-> compare expected semantics
-> stress IT/EN/ES
-> error analysis
-> fixes
-> retest/regression
-> then verify hand-off to Authority
```

Never invent metrics or infer semantic quality from fixture-only tests.

### No incomplete/hypothetical code

Forbidden:

```text
placeholder code
TODO used as implementation
unfinished functions
non-compiling snippets presented as solutions
invented future contracts/modules not grounded in the real runtime
```

### Owner workflow

The owner should normally only need to:

```text
1. Fetch/Pull repo
2. Run exact requested test
3. Return output
```

Repository edits, branches, scripts, fixes and checkpoints are prepared online.

### Approval rule

Before any **new substantive implementation, modification, build or test action**, state exactly what will be done and wait for explicit owner approval.

Continuity/checkpoint preservation is pre-authorized and does not require separate approval.

### Interruption rule — mandatory

If the owner interrupts work, before doing anything unrelated:

```text
1. Preserve valuable completed work online.
2. Record repo / branch / HEAD.
3. Record exact task and interruption point.
4. Record everything completed.
5. Record tests/results/errors discovered.
6. Record decisions/constraints.
7. Record artifacts/datasets/models/checksums.
8. Record EVERYTHING STILL TO DO in the interrupted task, in execution order.
9. Record tests/fixes/regressions still pending.
10. Record exact restart action and closure condition.
```

The continuity file must permit exact restart without reconstructing state from chat history.

### Chat + Work coordinated execution rule

The project should use **ChatGPT Work and this chat in coordination**, not as independent unsynchronized workers.

Canonical responsibilities:

```text
THIS CHAT
- supervisor / architectural control
- defines next authorized task
- audits results and evidence
- keeps module closure criterion consistent
- verifies GitHub state / commits / CI / tests
- updates WORK_CONTINUITY.md
- tracks what Work is doing and what remains

CHATGPT WORK
- executes substantial authorized repository work when launched by the owner in Work mode
- may inspect/edit/build/test using its cloud computer/browser/tools
- must operate only on the explicitly assigned task/repo/branch/scope
- must not independently change architecture, switch students/models, lower gates, or start unrelated tasks
```

Synchronization requirement:

```text
Before Work starts:
- record assigned objective
- repo/branch/start HEAD
- exact allowed scope
- forbidden scope
- expected tests/artifacts
- stop condition

While Work is active:
- this chat must treat its task as ACTIVE WORK, not duplicate the same implementation
- parallel work here must be non-conflicting supervision/audit/planning only
- any new information from Work must be incorporated into the current status

After every Work checkpoint/output:
- record resulting HEAD/branch/PR
- files changed
- tests/builds/benchmarks and results
- defects found
- unfinished sub-tasks
- exact next Work action
```

Important capability boundary:

```text
This chat cannot silently drive or observe a separate Work session in real time.
Work must be launched/continued in Work mode by the owner.
Once Work produces repository commits/PRs/files/results, this chat can inspect them through GitHub and synchronize continuity.
```

No task may be declared complete merely because Work reports completion; repository evidence and the project demonstration criterion still apply.

---

## 1 — OWNER-APPROVED MACRO EXECUTION ORDER

```text
A. Complete all essential engine modules with real implementations.
B. For EACH module: demonstrative tests -> error analysis -> fix -> retest -> regression.
C. Verify inter-module hand-offs and semantic field preservation.
D. Complete Memory Foundation + real integration.
E. Run complete desktop/JVM/end-to-end engine cycle.
F. Fix all P0 and blocking integration defects found end-to-end.
G. Repeat full regression until remaining issues are mainly quality/improvement, not missing/fake architecture.
H. Only then build/test on Moto G56.
I. Use phone testing for real performance, RAM/CPU, latency, thermal, stability and quality iteration.
```

Pre-phone target = coherent, integrated, demonstrably executable baseline; not perfection.

---

## 2 — CANONICAL REPOSITORY STATE

### Main

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
PR #22 = merged
purpose = explicit RetrievalResult claimId + contextSnapshotId binding
```

### Active branch

```text
branch = functional-audit-understanding-authority
HEAD before this continuity update = 6b05299dc703a23280b92a1659d479d76b9863ce
base = main 693622ce12f1db6b4bc44753bee756b551c741ea
status = active audit/fix branch; not yet canonical main
```

### Preserved WIP branch

```text
branch = backup-memorycandidate-wip
commit = 9e7413c
purpose = preserve experimental MemoryCandidate contract/test
status = BACKUP ONLY / NOT CANONICAL / DO NOT MERGE AUTOMATICALLY
```

---

## 3 — CLOSED HISTORICAL CHECKPOINTS — DO NOT REDO

### Understanding V3 code/contract checkpoint

```text
CP-U3 PR = #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Structural path:

```text
Matrix-NLU V3 runtime output
-> CanonicalUnderstandingV3Adapter
-> MipUnderstandingV3Observation
-> MipUnderstandingV3Claim[]
-> MatrixTurnFrame.canonicalUnderstandingV3
```

Correct current interpretation:

```text
CODE/CONTRACT/ADAPTER CHECKPOINT = COMPLETE
RAW-TEXT REAL COMPREHENSION IN ASSEMBLING = NOT YET DEMONSTRATED
```

### Python Authority Resolver P0

Repository `MATRIXNEO23/memoria`:

```text
branch = python-authority-p0-v1
final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR = memoria #1
pre-merge CI = 34020629980 SUCCESS
merge = b8cc7e2133868049550d3c63d78f69da8f830f20
continuity closure = cde91db20d97e0792b61db15144faf1430fd27bc
```

Closed P0s:

```text
hardcoded owner -> removed
regex/free-text property extraction -> removed from Authority path
actor-overlap/content-difference false conflict -> replaced with structured semantic contradiction rules
```

### Understanding V3 -> Authority original wiring

```text
branch = cp-a2-v3-authority-wiring
PR = #21
head = b7ea750665a563cbca673b4050a72adc21275a8e
CI = 34020878347 SUCCESS
merge = d3994e59008aac648576a252eac0d7c4e1028589
```

Originally a wiring/code checkpoint; not proof of raw-language understanding.

---

## 4 — RETRIEVAL BINDING — CONTRACT/WIRE FIX MERGED

Merged PR #22 added explicit binding:

```text
RetrievalQuery.claimId: MipField<String>
RetrievalQuery.contextSnapshotId: mandatory nonblank String
RetrievalResult.claimId: MipField<String>
RetrievalResult.contextSnapshotId: MipField<String>
RetrievalResult.requireBinding(...)
```

Wire rules:

```text
missing newly introduced semantic binding -> UNRESOLVED
malformed PRESENT -> error
missing mandatory RetrievalQuery.contextSnapshotId -> error
NO_MATCH is retrieval outcome, not identity state
```

Owner local evidence before merge:

```text
verify_evidence_wire.sh -> BUILD SUCCESSFUL
full gradle test -> BUILD SUCCESSFUL
clean test -> BUILD SUCCESSFUL
```

Merge:

```text
693622ce12f1db6b4bc44753bee756b551c741ea
```

---

## 5 — FUNCTIONAL AUDIT: UNDERSTANDING V3 -> AUTHORITY

### Defect demonstrated

Owner first ran:

```text
bash run_functional_audit.sh
```

Observed:

```text
existing structured single-claim checks = BUILD SUCCESSFUL
multi-claim explicit retrieval-binding test = FAILED
```

Root cause in real runtime:

```text
CanonicalUnderstandingV3AuthorityPort.retrievalForClaim(...)
old behavior:
1 claim + 1 result -> bind
otherwise -> UNRESOLVED
```

Thus contract green had not fixed runtime behavior.

### Runtime fix on active branch

Active branch changed the Authority port to consume explicit:

```text
RetrievalResult.claimId
RetrievalResult.contextSnapshotId
```

with fail-closed matching and no list-order/query-id guessing.

Permanent regression added:

```text
src/test/kotlin/matrix/assembling/authority/runtime/CanonicalUnderstandingV3AuthorityRetrievalBindingTest.kt
```

### Owner retest after fix

```text
bash run_functional_audit.sh
```

Result:

```text
[1/2] Existing real structured-path checks -> BUILD SUCCESSFUL
[2/2] Multi-claim explicit retrieval-binding check -> BUILD SUCCESSFUL

SINGLE-CLAIM STRUCTURED PATH: VERIFIED BY EXISTING REAL TESTS
MULTI-CLAIM RETRIEVAL BINDING: WORKS
```

Correct classification:

```text
Understanding V3 -> Authority STRUCTURED HAND-OFF = VERIFIED for tested single/multi-claim cases
Raw-text NLU comprehension = NOT PROVEN BY THIS TEST
Global MIP runtime = NOT YET PROVEN
```

---

## 6 — CURRENT NLU ARTIFACT — PRESERVE / DO NOT SUBSTITUTE

Current candidate remains Student-4 v2.2A mixed/head-protected.

```text
status = EXPERIMENTAL_TEST_CANDIDATE / NOT_PRODUCTION_APPROVED
ZIP bytes = 356134801
ZIP SHA-256 = 4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5
mixed INT8 ONNX = matrix-nlu-mixed-head-protected-int8.onnx
ONNX bytes = 149711344
ONNX SHA-256 = 738a4d052790367509d55487b649b71aaa029d839135693bb5be46f74d55ef70
quantization = dynamic INT8 encoder + protected Matrix heads FP32
```

Current decisions:

```text
DO NOT SWITCH NLU MODEL
DO NOT START PHONE TEST YET
DO NOT OPEN STUDENT-5 AS A SIDE TASK
```

Student-5 remains separately preserved and must not overwrite/replace Student-4 automatically.

---

## 7 — MEMORY FOUNDATION — CURRENT IMMEDIATE PRIORITY

Canonical semantic architecture:

```text
TypedClaim / canonical claim
-> Authority Resolver
-> Memory Admission
-> MemoryRepository
```

Hard invariants:

```text
GGUF/NLU never access persistence directly
Authority may read evidence but never write Memory
semantic change uses supersede() and preserves lineage
Memory Admission schema v3 uses explicit contradicts_memory_id
Authority owns semantic contradiction detection
Memory Admission must not infer contradiction from text difference/shared actors
contradiction != supersession
correction != automatic supersession
temporal change != contradiction by default
```

Pre/post response architecture:

```text
PRE-RESPONSE READ/ENRICH
-> retrieval/index probe
-> no durable write

PRE-RESPONSE EVALUATE/PROPOSE
-> MemoryPreflightPort
-> ephemeral candidate/proposal only

POST-VALIDATION COMMIT
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
-> atomic SAVE / SUPERSEDE / metadata operation
```

Current Assembling reality:

```text
NoPersistentMemoryAdmission = compatibility/no-backend path
PersistentConsolidationPort = interface only
real durable Kotlin/Room persistence in assembling = NOT IMPLEMENTED
MemoryCandidate experiment = backup branch only
```

Real Python Memory Foundation exists in `MATRIXNEO23/memoria` and must remain the semantic reference.

---

## 8 — MODULE VERDICTS UNDER STRICT CRITERION

```text
Understanding V3 contracts/adapter = CODE COMPLETE; REAL RAW-TEXT COMPREHENSION NOT YET DEMONSTRATED IN ASSEMBLING
Authority resolver = REAL STRUCTURED RESOLVER EXISTS; broader functional/stress closure still required
Understanding -> Authority = TESTED/PASS for current structured single + multi-claim cases
Retrieval contracts/wire = TESTED/PASS for implemented binding behavior
Retrieval complete runtime/index = NOT YET DEMONSTRATED
Memory Admission/Repository integration = NOT COMPLETE
Persistent consolidation = NOT IMPLEMENTED
Coherence = compatibility/basic path; final real-module proof pending
Affective = adapter/basic implementation; final demonstrative proof pending
Relationship = NOT WIRED
Intimacy/Consent = NOT WIRED
Goal/Decision = NOT WIRED
GGUF in assembling = Echo/fake smoke adapter; REAL GGUF PATH NOT COMPLETE
Output Validator = interface/partial; real path not complete
Reflection = future/not complete
Global MIP = NOT DEMONSTRATED
End-to-end engine = NOT YET READY FOR CLOSURE
Phone baseline = BLOCKED UNTIL ENGINE BASELINE CLOSURE
```

---

## 9 — WORK INTERRUPTED / EVERYTHING STILL TO DO

### Phase 1 — close Memory Foundation integration

```text
1. Audit actual Memory Foundation implementation in MATRIXNEO23/memoria against current canonical contracts.
2. Identify exact Android/Kotlin integration target; do not add fake Room to JVM-only module.
3. Implement real boundary canonical AuthorityResolution -> Memory Admission/Repository.
4. Preserve contradiction identity, provenance, owner/scope, confidence separation and lineage.
5. Implement/finish durable post-validation consolidation path.
6. Demonstrative Memory tests:
   - save
   - retrieve
   - reinforce/update metadata as allowed
   - supersede with lineage
   - explicit contradiction identity
   - rollback/atomic failure
   - deletion/lineage protection
   - ambiguous/unresolved fail-closed behavior
7. Test Authority -> Memory real hand-off field-by-field.
8. Fix observed defects.
9. Rerun Memory + Authority regressions.
```

Memory closure condition:

```text
A real claim/resolution can be admitted/rejected/superseded through actual repository behavior with semantics/lineage preserved and regression evidence.
```

### Phase 2 — close remaining essential modules one at a time

For each:

```text
implementation audit
-> real demonstrative test
-> edge/ambiguity/error cases
-> field preservation to next module
-> error analysis
-> fix
-> retest
-> regression
```

Known remaining essential areas include:

```text
Context/Retrieval
Coherence
Affective
Relationship
Intimacy/Consent where required by final engine design
Goal/Decision where required by final engine design
real GGUF adapter
Output Validator
Persistent Consolidation
```

Actual orchestrator dependency graph decides order; do not invent modules merely to fill a plan.

### Phase 3 — real NLU/Understanding proof

```text
1. Execute actual Student-4 runtime/model on raw phrases.
2. IT/EN/ES stress suite.
3. Cover negation, temporal, referents, reports, corrections, requests/goals, ownership, multi-claim, ambiguity and combined cases.
4. Produce measured error counts/categories.
5. Fix only observed problems.
6. Retest identical suite plus regression.
7. Verify resulting real claims through Authority/downstream modules.
```

### Phase 4 — complete end-to-end

Required proof path:

```text
raw user input
-> real NLU/Understanding
-> context/retrieval
-> Authority
-> Memory preflight/admission
-> real MemoryRepository / consolidation
-> affective/relationship/goal/decision modules as wired
-> prompt builder
-> real GGUF
-> output validator
-> post-validation persistent consolidation
-> final response + diagnostics
```

Required checks:

```text
no semantic field silently lost
no field invented without provenance
no illegal direct persistence path
no P0 runtime defect
blocking regressions green
causal/diagnostic trace explains decisions
```

Fix defects and repeat until baseline closure.

### Phase 5 — Moto G56

Only after Phases 1-4:

```text
build/install Android baseline
-> real Student-4 NLU
-> real engine cycle
-> real GGUF
-> physical-device tests
-> CPU/RAM/PSS/latency/thermal/stability
-> behavioral quality tests
-> iterative improvements
```

Goal = improvable baseline, not perfection.

---

## 10 — CHATGPT WORK TASK LEDGER

Current state at this checkpoint:

```text
Work session task = NONE CURRENTLY SYNCHRONIZED IN THIS CHAT
Work repo/branch/HEAD = UNKNOWN UNTIL OWNER PROVIDES OR WORK COMMITS ARE VISIBLE
Do not assume Work is idle or finished without evidence.
```

When Work is started/continued, populate this ledger immediately:

```text
WORK OBJECTIVE =
WORK REPOSITORY =
WORK BRANCH =
WORK START HEAD =
WORK CURRENT HEAD =
AUTHORIZED FILES/SCOPE =
FORBIDDEN SCOPE =
CURRENT OPERATION =
COMPLETED SUBTASKS =
TESTS/RESULTS =
DEFECTS FOUND =
ARTIFACTS/CHECKSUMS =
REMAINING SUBTASKS =
NEXT WORK ACTION =
STOP/CLOSURE CONDITION =
```

This ledger must be updated after every significant Work checkpoint so this chat can supervise without duplicating or conflicting work.

---

## 11 — OWNER LOCAL TEST ENVIRONMENT

```text
repo path = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

`run_functional_audit.sh` configures the known Java/Gradle path for current functional audit.

---

## 12 — EXACT RESTART POINT

```text
ACTIVE REPO = MATRIXNEO23/assembling
ACTIVE BRANCH = functional-audit-understanding-authority
HEAD BEFORE THIS CONTINUITY UPDATE = 6b05299dc703a23280b92a1659d479d76b9863ce
LAST OWNER-RUN TEST = bash run_functional_audit.sh
LAST RESULT = structured single-claim PASS + multi-claim retrieval binding PASS
MAIN = 693622ce12f1db6b4bc44753bee756b551c741ea

CURRENT MACRO OBJECTIVE = close engine modules before end-to-end and Moto G56
CURRENT IMMEDIATE PRIORITY = Memory Foundation real integration
PHONE TEST = NOT YET
STUDENT-5 SIDE WORK = STOPPED / DO NOT CONTINUE
MEMORYCANDIDATE BACKUP = PRESERVED / NOT CANONICAL
WORK COORDINATION = REQUIRED; no duplicated/conflicting task
```

### Exact next substantive action — requires owner approval

```text
Audit the actual Memory Foundation code and current integration boundary,
identify the first real missing implementation required for
AuthorityResolution -> Memory Admission -> MemoryRepository,
and prepare one complete implementation/test cycle for that concrete gap.
```

Before starting that substantive action, wait for explicit owner approval.
