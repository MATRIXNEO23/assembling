# Work Continuity — Matrix Assembling

Last updated: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active work branch: `functional-audit-understanding-authority`  
Continuity schema: `matrix.assembling.continuity.v76`

## 0 — NON-NEGOTIABLE WORK METHOD

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

For NLU/Understanding specifically:

```text
raw text
-> actual Student-4 runtime/model
-> actual structured output
-> expected semantic comparison
-> IT/EN/ES stress
-> measured errors
-> fixes
-> identical retest + regression
-> downstream Authority proof
```

Never invent metrics. Never treat fixture-only tests as proof of linguistic understanding.

Forbidden:

```text
placeholder code
TODO as implementation
unfinished/non-compiling implementation
invented future modules/contracts not grounded in real code
```

Owner workflow target:

```text
owner normally only Fetch/Pull -> run exact requested test -> return output
```

Repository edits/scripts/branches/checkpoints are prepared online.

### Approval rule

Before any **new substantive implementation, modification, build or test action**, state exactly what will be done and wait for explicit owner approval.

Continuity/checkpoint preservation is pre-authorized.

### Mandatory interruption rule

If the owner interrupts work, before unrelated work:

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

### Chat + Work coordination

```text
THIS CHAT = supervisor / architecture / audit / evidence / continuity
CHATGPT WORK = substantial authorized execution when owner launches Work mode
```

Before Work starts record objective, repo, branch, start HEAD, allowed/forbidden scope, required tests and stop condition. While Work is active this chat must not duplicate its implementation. After every Work checkpoint record current HEAD, files changed, tests, defects, artifacts, remaining work and next action.

This chat cannot silently observe a separate Work session in real time. Repository-visible Work output must be inspected and synchronized before acceptance.

---

## 1 — OWNER-APPROVED MACRO ORDER

```text
A. Complete essential engine modules with real implementations.
B. For each module: demonstrative test -> error analysis -> fix -> retest -> regression.
C. Verify real inter-module hand-offs and semantic field preservation.
D. Complete Memory Foundation + real integration.
E. Run complete desktop/JVM end-to-end cycle.
F. Fix P0/blocking integration defects and repeat regression.
G. Reach an integrated improvable baseline; perfection is not required.
H. Only then build/test on Moto G56.
I. Phone phase measures real RAM/CPU/PSS/latency/thermal/stability/behavior and drives later quality improvements.
```

---

## 2 — REPOSITORY / BRANCH STATE

### `MATRIXNEO23/assembling`

```text
main HEAD = 693622ce12f1db6b4bc44753bee756b551c741ea
main includes PR #22 explicit RetrievalResult claimId/contextSnapshotId binding
active branch = functional-audit-understanding-authority
active branch HEAD before this checkpoint = cf35f26ed47976dc42c57cdd014fcd06b1dac7bd
backup branch = backup-memorycandidate-wip
backup MemoryCandidate commit = 9e7413c
backup status = BACKUP ONLY / NOT CANONICAL / DO NOT AUTO-MERGE
```

### `MATRIXNEO23/memoria`

```text
main HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
branches = main, python-authority-p0-v1 only
python-authority-p0-v1 final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR #1 merged as b8cc7e2133868049550d3c63d78f69da8f830f20
```

Important discovery on 2026-09-07:

```text
memoria/main currently contains:
- memory/models.py
- memory/authority_models.py
- memory/authority_resolver.py
- tests/test_authority_resolver_p0.py

memoria/main DOES NOT currently contain the full historical Memory Foundation files:
- memory/schema.py
- memory/database.py
- memory/repository.py
- memory/admission.py
- memory/admission_models.py
- corresponding Memory Foundation tests
```

The full Memory Foundation was recovered from the user's ChatGPT Library and therefore MUST NOT be re-invented from scratch.

---

## 3 — CLOSED / PRESERVED CHECKPOINTS

### Understanding V3 code/contract checkpoint

```text
PR #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Interpretation:

```text
contract/adapter code checkpoint = CLOSED
raw-text real comprehension = NOT YET DEMONSTRATED IN ASSEMBLING
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

This was a wiring/code checkpoint, not proof of raw-language understanding.

### Retrieval binding

PR #22 merged to main:

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

### Structured Understanding -> Authority functional defect/fix

Owner first ran:

```text
bash run_functional_audit.sh
```

Observed:

```text
single-claim structured path = PASS
multi-claim retrieval binding = FAIL
```

Root cause:

```text
CanonicalUnderstandingV3AuthorityPort still used legacy 1-claim/1-result-only binding and returned UNRESOLVED for multi-claim turns.
```

Active branch fix now consumes explicit RetrievalResult `claimId` + `contextSnapshotId`, fail-closed, no list-order/query-id guessing.

Permanent regression added:

```text
src/test/kotlin/matrix/assembling/authority/runtime/CanonicalUnderstandingV3AuthorityRetrievalBindingTest.kt
```

Owner retest:

```text
single-claim structured path = BUILD SUCCESSFUL
multi-claim explicit retrieval binding = BUILD SUCCESSFUL
```

Correct verdict:

```text
structured hand-off Understanding V3 -> Authority = VERIFIED for tested single/multi-claim cases
raw-text understanding = NOT PROVEN
Global MIP = NOT PROVEN
```

---

## 4 — CURRENT NLU ARTIFACT — PRESERVE

Do not switch model as a side task.

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

---

## 5 — MEMORY FOUNDATION: CURRENT REAL STATUS

Memory has **NOT been implemented/integrated yet** in the current engine. This must not be described as complete.

### Canonical architecture

```text
canonical TypedClaim
-> Authority Resolver
-> Memory Admission
-> MemoryRepository
```

Hard invariants:

```text
GGUF/NLU never access persistence directly
Authority may read evidence but never write Memory
Authority owns semantic contradiction detection
Memory Admission consumes explicit contradicts_memory_id
Memory Admission must not infer contradiction from shared actors/text difference
semantic changes use supersede()
lineage is preserved
contradiction != supersession
correction != automatic supersession
temporal change != contradiction by default
confidence != authority
```

Pre/post-response split:

```text
PRE-RESPONSE READ/ENRICH -> retrieval/index -> no durable write
PRE-RESPONSE PROPOSE -> MemoryPreflightPort -> ephemeral only
POST-VALIDATION COMMIT -> PersistentConsolidationPort -> Memory Admission -> MemoryRepository -> atomic durable operation
```

Current `assembling` reality:

```text
NoPersistentMemoryAdmission = temporary no-backend preflight
PersistentConsolidationPort = interface only
real durable MemoryRepository = NOT INTEGRATED
real Retrieval backend = NOT INTEGRATED
MemoryCandidate WIP = backup branch only
```

---

## 6 — RECOVERED CANONICAL MEMORY FOUNDATION FROM USER LIBRARY

Recovered source files:

```text
memory/__init__.py
memory/models.py
memory/schema.py
memory/database.py
memory/repository.py
memory/admission.py
memory/admission_models.py
```

Recovered behavior/invariants from the real reference:

### Schema v3

```text
memories table includes:
owner, memory_type, category, content, summary, actors, entities,
world_time, real_time, location_id, authority, provenance,
source_event_id, confidence, salience, emotional_weight,
validity, superseded_by, revision_of, revision_count,
contradicts_memory_id, links, goal_id, timestamps/access metadata

normalized memory_actors and memory_entities tables
foreign keys enabled
FTS5 content+summary
indexes for owner/type/time, validity, goal, revision lineage, contradiction id
```

### Database contract

```text
SQLite reference only; Android production must translate contract to Room/Kotlin
PRAGMA journal_mode=WAL
PRAGMA foreign_keys=ON
transaction() commits on success, rolls back on exception
```

### Repository contract

```text
save
get_by_id
get_by_owner
get_by_actor
metadata-only update
supersede
mark superseded
lineage rooted at original revision
semantic fields cannot be directly updated
```

`update_metadata()` allows only metadata such as confidence/salience/emotional_weight/links/goal_id. Semantic changes (`content`, `summary`, `actors`, `entities`) must go through `supersede()`.

### Admission contract

```text
AdmissionDecision = SAVE / SUPERSEDE / REJECT / IGNORE
explicit contradicts_memory_id drives conflict lookup
no contradicts_memory_id => Admission does not invent a conflict
higher Authority outranks lower Authority
same Authority may use confidence/recency for supersede decision
```

---

## 7 — RECOVERED MEMORY FOUNDATION TEST EVIDENCE / REQUIRED PORTING REGRESSIONS

Recovered canonical tests include at least:

```text
tests/conftest.py
test_memory_admission.py
test_memory_admission_authority.py
test_memory_admission_supersede.py
test_atomic_rollback.py
test_lineage_protection.py
test_semantic_update.py
```

Required behaviors demonstrated by those reference tests and therefore mandatory in Kotlin/Room port:

### Atomic rollback with fault injection

```text
save(new revision) succeeds
mark_superseded(old,new) is forced to fail
transaction rollback must remove new revision
old record remains VALID with superseded_by=null
```

### Lineage/delete protection

```text
A -> B -> C
A revision_of=null, superseded_by=B
B revision_of=A, superseded_by=C
C revision_of=A, superseded_by=null
revision_count 0/1/2
foreign keys prevent physical deletion that would break lineage
```

### Semantic update protection

```text
repository has no generic semantic update path
update_metadata rejects content/summary/actors/entities
semantic evolution occurs only through supersede()
old record remains frozen/superseded
new record contains changed semantics
normalized actor/entity tables remain consistent
```

### Admission / Authority behavior

```text
SAVE for valid accepted records
REJECT for invalid/missing/low-confidence according to criteria
IGNORE for duplicate/low-salience cases
SUPERSEDE only with explicit contradiction identity and authority/confidence/recency rules
lower-authority evidence must not supersede higher-authority memory
WORLD_TRUTH outranks lower classes
```

### Persistence/restart requirement

Historical test suite explicitly included database restart/reopen integrity. This remains a mandatory porting test even if the exact standalone file has not yet been isolated from Library search.

---

## 8 — CURRENT INTERRUPTION POINT

The owner interrupted while the supervisor was auditing Memory. No Memory implementation was started.

Completed before interruption:

```text
1. verified `memoria/main` does not contain full Memory Foundation
2. verified only `main` and `python-authority-p0-v1` branches exist
3. recovered canonical Memory Foundation source files from Library
4. recovered canonical Admission/rollback/lineage/semantic-update tests from Library
5. confirmed current `assembling` only has no-persistence preflight + future consolidation interface
6. confirmed experimental MemoryCandidate branch is not a substitute for real Memory Foundation
```

No new Memory production code, Room implementation, build, or Memory test was executed during this audit.

---

## 9 — EVERYTHING STILL TO DO FOR MEMORY, IN ORDER

### M0 — Preserve canonical reference online

```text
1. Select one exact recovered version of each canonical Memory Foundation source file.
2. Select one exact canonical copy of each required test; ignore duplicate Library copies.
3. Put the recovered Python reference + tests online in `MATRIXNEO23/memoria` on a dedicated branch.
4. Do not overwrite the already-fixed Authority Resolver.
5. Run the Python reference suite and record PASS/FAIL count.
6. Fix only genuine reference/recovery inconsistencies if found.
7. Update both Memory and Assembling continuity checkpoints.
```

Closure condition M0:

```text
full canonical Memory Foundation source + tests are durably online and reproducibly green as reference
```

### M1 — Freeze exact Kotlin/Room contract mapping

Map, do not redesign casually:

```text
MemoryRecord fields
schema v3 constraints/FKs/indexes
SAVE
SUPERSEDE
metadata update
lineage
protected delete
atomic transaction
explicit contradicts_memory_id
Admission SAVE/SUPERSEDE/REJECT/IGNORE
```

Identify the actual Android-capable target. `assembling` itself is currently JVM-only, therefore do not create fake Room merely to satisfy a checkbox.

### M2 — Implement real persistence/admission

```text
Room entities/DAO/database/transactions in the actual Android-capable integration target
Memory Admission consuming canonical claim + AuthorityResolution
AuthorityResolution contradiction identity mapped explicitly
no raw-text conflict inference
PersistentConsolidationPort implementation after validation
```

### M3 — Demonstrative Memory tests

At minimum:

```text
SAVE -> actual persisted row
READ -> exact record returned
metadata update -> only permitted metadata changes
SUPERSEDE -> old/new states + rooted lineage
explicit contradiction target preserved
lower authority cannot wrongly supersede higher authority
rollback fault injection -> no partial write
protected lineage deletion
restart/reopen -> correct current/history still present
idempotency/duplicate behavior
ambiguous/unresolved contradiction -> fail closed
```

### M4 — Real Authority -> Memory hand-off

Field-by-field verify:

```text
claimId
owner
subject/target/perspective/source where relevant
predicate/object/polarity/temporal scope
Authority class
Authority resolution confidence
source reliability if available
contradictedMemoryRef
candidate refs
provenance/context snapshot
```

No field may silently disappear or be invented.

### M5 — Retrieval

```text
index probe on normal turn
hydrate/rerank where needed
current vs historical distinction
superseded memory not treated as current
irrelevant memories must not win
historical lineage remains retrievable
NO_MATCH != INDEX_UNAVAILABLE/ERROR
```

### M6 — Persistent consolidation

```text
accepted response/action
-> Output Validation
-> Persistent Consolidation
-> Memory Admission
-> MemoryRepository
-> atomic durable operation
```

No durable write before output validation.

### M7 — Memory integration E2E

Required demonstrative scenarios:

```text
"Vivo a Milano" -> save -> later retrieval returns Milano
"Prima vivevo a Venezia, ora vivo a Milano" -> current/history correct
"Marco dice che Anna vive a Roma" -> REPORT, not WORLD_TRUTH
"Mi ero sbagliato, vivo a Torino" -> correct SUPERSEDE + lineage
restart -> correct memory still retrievable
forced persistence failure -> atomic rollback
```

Fix all observed defects, rerun same suite, then regress Authority/Understanding hand-offs.

Memory closure condition:

```text
real canonical claim + AuthorityResolution can be admitted/rejected/superseded through actual durable repository behavior; lineage, contradiction identity, persistence and rollback are demonstrated; retrieval can consume resulting state; regressions are green.
```

---

## 10 — REMAINING ENGINE WORK AFTER MEMORY

Do not jump here before Memory required suite closes.

For each actual module in dependency order:

```text
implementation audit -> real demonstrative test -> ambiguity/error -> hand-off -> error analysis -> fix -> retest -> regression
```

Known incomplete areas:

```text
Context/Retrieval runtime
Coherence final behavior
Affective final demonstrative proof
Relationship
Intimacy/Consent where required
Goal/Decision
real GGUF adapter
Output Validator
Persistent Consolidation
```

Raw NLU proof remains required using current Student-4, not a replacement model.

---

## 11 — COMPLETE ENGINE END-TO-END BEFORE PHONE

Required path:

```text
raw user input
-> real Student-4 NLU/Understanding
-> context/retrieval
-> Authority
-> Memory preflight/proposal
-> affective/relationship/goal/decision as wired
-> prompt builder
-> real GGUF
-> output validator
-> persistent consolidation
-> Memory Admission
-> MemoryRepository
-> future retrieval
-> final response + diagnostic trace
```

Required properties:

```text
no illegal direct persistence
no silent semantic field loss
no invented state without provenance
P0 runtime defects eliminated
blocking regressions green
diagnostics identify first divergence and reason codes
```

Only after this reaches an integrated improvable baseline does Moto G56 testing start.

---

## 12 — CHATGPT WORK LEDGER

```text
WORK OBJECTIVE = not currently synchronized from an active Work session
WORK REPOSITORY = unknown until Work session reports/commits are visible
WORK BRANCH = unknown
WORK START HEAD = unknown
WORK CURRENT HEAD = unknown
CURRENT OPERATION = none verified in this chat
```

When Work is launched for Memory, recommended assignment is **M0 recovery/preservation first**, not Room implementation yet.

Required Work task record before launch:

```text
OBJECTIVE = recover canonical Python Memory Foundation + tests into memoria without changing Authority semantics
REPO = MATRIXNEO23/memoria
START HEAD = cde91db20d97e0792b61db15144faf1430fd27bc
ALLOWED = recovered Memory Foundation source/tests + continuity + CI needed to run them
FORBIDDEN = changing NLU, assembling architecture, Authority semantic rules, Student-4/5, phone build
EXPECTED = canonical source online + complete test execution + report
STOP = after reference suite result and checkpoint; no Kotlin/Room yet
```

This chat should supervise that Work task and must not duplicate its implementation while active.

---

## 13 — OWNER LOCAL ENVIRONMENT

```text
assembling path = C:\Users\matri\Documents\GitHub\assembling
Java 17 = C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot
Gradle = $HOME/gradle/gradle-9.7.1
Git = 2.55.0.windows.5
```

Owner should not be asked to create/copy project files manually unless unavoidable.

---

## 14 — EXACT RESTART POINT

```text
ACTIVE SUPERVISOR TASK = Memory Foundation recovery/integration preparation
INTERRUPTION POINT = after canonical Memory source/tests were found in Library; before any implementation
MEMORY STATUS = NOT IMPLEMENTED / NOT INTEGRATED
NEXT REQUIRED TASK = M0 preserve full canonical Python Memory Foundation + tests online in `MATRIXNEO23/memoria`
NEXT IMPLEMENTATION AFTER M0 = map verified reference to actual Android Kotlin/Room target
PHONE TEST = BLOCKED UNTIL module demonstrations + fixes + full E2E baseline
CURRENT NLU = Student-4 v2.2A; DO NOT SUBSTITUTE
STUDENT-5 = preserved but side work stopped
MEMORYCANDIDATE WIP = preserved separately; not canonical
```

### Next substantive action — REQUIRES OWNER APPROVAL

```text
M0: recover one canonical copy of each Memory Foundation source/test from Library,
put them online on a dedicated `memoria` branch without changing Authority semantics,
run the complete Python reference test suite,
and report exact PASS/FAIL + remaining defects.
```
