# WORK — MATRIX ENGINE FOUNDATION CLOSURE F1/F2

Status: **PREPARED / DO NOT EXECUTE UNLESS OWNER LAUNCHES AND AUTHORIZES THIS TASK**  
Supervisor design source: `docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`  
Continuity source: `docs/WORK_CONTINUITY.md`  
Repository: `MATRIXNEO23/assembling`  
Expected start branch: `functional-audit-understanding-authority`

## Mission

Close only the foundational runtime path required before Memory implementation, without implementing Memory persistence or unrelated future modules.

The objective is to make the already-defined canonical semantic path real and authoritative:

```text
canonical Understanding V3
-> TurnWorkspace / immutable ContextSnapshot
-> claim-bound RetrievalResult status/evidence
-> narrow Coherence validation
-> canonical AuthorityResolution per claim
```

while installing an orchestration skeleton that can later accept the known state owners without redesigning the core.

## Read first

Mandatory:

```text
docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md
docs/MATRIX_INTERMODULE_PROTOCOL.md
docs/MODULE_CONNECTIONS.md
docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md
docs/WORK_CONTINUITY.md
```

Inspect existing real code before changing it, especially:

```text
src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt
src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt
src/main/kotlin/matrix/assembling/IntegrationPorts.kt
src/main/kotlin/matrix/assembling/understanding/v3/
src/main/kotlin/matrix/assembling/authority/
src/main/kotlin/matrix/assembling/mip/
src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt
```

Also inspect the historical Android reference only as a source of reusable patterns, not as a wholesale replacement:

```text
MATRIXNEO23/8.10.9evo3-solo-gpt@e97f75052afcc93d5b1e08b3ac881dba35633451
```

Relevant reusable historical assets include:
- `MatrixEngineFramework.Stage/StageResult/StageDiagnostic` pattern;
- `PerceptionEnvelope` identity/provenance validation;
- `WorldMatrixProtocol`;
- `CausalTrace` bounded structured trace.

Do NOT port the old Android `MatrixEngineOrchestrator` wholesale because it performs Memory persistence before output validation.

## F1 — Foundation contracts/runtime envelope

Close these foundations without unnecessary redesign:

1. Canonical turn/workspace can carry:
   - observations / Understanding V3;
   - ContextSnapshot;
   - claim-bound RetrievalResults;
   - Coherence results per canonical claim or a lossless aggregate that retains exact claim identity;
   - canonical AuthorityResolutions;
   - explicit availability for future domains.
2. Event/correlation/causation/provenance IDs remain stable and inspectable.
3. Diagnostic trace can represent at least the actual F1/F2 stages and first divergence.
4. Missing future owners are explicit `NOT_WIRED`/`UNAVAILABLE`, never fake zero/default data.
5. Foundation APIs must remain JVM-testable; do not introduce Android Room here.

Prefer additive migration over breaking already-tested MIP semantics.

## F2 — Canonical orchestration closure

Required authoritative path:

```text
canonical V3 claims
-> Context assembler/provider stage
-> Retrieval query/result per claim
-> narrow Coherence validator
-> CanonicalUnderstandingV3AuthorityPort
-> DeterministicAuthorityResolver
-> canonicalAuthorityResolutions
```

### Context

Implement the minimal real Context assembler/provider needed for this path. It must:
- emit immutable `MatrixContextSnapshot`;
- preserve turn/session/agent/snapshot identity;
- report domain availability explicitly;
- allow future independent World/Memory/Belief/Relationship/Affective/Intimacy/Goal reads without changing the contract.

Do not invent fake Memory records. Until a real Memory read backend exists, Memory domain/retrieval must expose correct unavailable/no-match semantics according to MIP.

### Retrieval

Use the current explicit `RetrievalQuery.claimId + contextSnapshotId` and `RetrievalResult.claimId + contextSnapshotId` contracts.

Requirements:
- one query/result identity per canonical claim where retrieval is attempted;
- no list-order binding;
- no parsing claim identity from query strings;
- `NO_MATCH` is a successful query outcome, not provider unavailability;
- `UNAVAILABLE`/`ERROR` remain explicit;
- multi-claim turns preserve separation.

No real durable Memory backend is authorized in F1/F2.

### Coherence

Replace/narrow the authoritative Coherence behavior so it only checks semantic/structural stability.

Allowed:
- V3 structural/interpretation status;
- required role/predicate/polarity/temporal field statuses;
- critical confidence thresholds when explicitly justified;
- malformed/missing identity checks;
- claim-wise fail-closed result;
- deterministic reason codes.

Forbidden:
- choosing REPORT/BELIEF authority;
- identifying Memory contradiction;
- deciding Memory persistence;
- treating QUESTION/REPORT merely as unsafe because of their epistemic type;
- choosing behavior.

Preserve legacy compatibility only when necessary for existing tests; it must not remain the authoritative canonical path.

### Authority

Use the existing canonical `DeterministicAuthorityResolver` and the active branch explicit retrieval-binding fix.

Do not:
- reparse natural language;
- change Authority semantics merely to make tests pass;
- infer contradiction from actor overlap/text difference;
- write Memory.

## Orchestration skeleton for future modules

The foundation should expose stable seams for the already-designed future runtime:

```text
Context ENRICH providers
Coherence
Authority/Belief
MemoryPreflight
Affective/Relationship/Intimacy/Goal proposal providers
Decision
Prompt/GGUF
OutputValidator
PersistentConsolidation
```

Only implemented F1/F2 modules execute now. Future modules must be `NOT_WIRED`/optional, not simulated.

Where several read-only Context providers are independent, design for bounded Kotlin structured concurrency. Do not add uncontrolled background/global tasks.

## Forbidden scope

Do NOT:

```text
implement Room/SQLite Memory persistence
recover/modify Python Memory Foundation in this task
implement final Memory Admission/Repository
implement RelationshipState
implement Intimacy/Consent owner
implement Goal/Decision/Behavior
implement Reflection/Opportunity/LOD
change Student-4 model
start Student-5
retrain/quantize NLU
implement phone APK
replace GGUF backend
lower test gates
use placeholder implementations as proof of completion
```

## Demonstrative tests required

Create tests that prove behavior, not only DTO construction.

At minimum:

### Normal single claim
```text
V3 canonical claim
-> ContextSnapshot
-> claim-bound retrieval status/result
-> Coherence PASS
-> AuthorityResolution for exact claimId/contextSnapshotId
```

### Multi-claim
Two distinct claims in one observation:
- two independent identities;
- Context shared only where correct;
- retrieval results remain bound to correct claim;
- Coherence result does not collapse claims;
- Authority returns exactly one resolution per claim;
- no cross-claim evidence leakage.

### Ambiguous semantic claim
- ambiguity preserved;
- Coherence/Authority fail closed at the correct boundary;
- no guessed subject/owner/source/perspective.

### Retrieval NO_MATCH
- provider/query succeeded;
- Authority receives NO_MATCH semantics;
- not converted to UNAVAILABLE.

### Retrieval UNAVAILABLE
- explicitly different from NO_MATCH;
- Authority status/reason codes reflect unavailable evidence.

### Retrieval ERROR
- deterministic error status and first divergence.

### Context binding mismatch
- fail closed;
- no stale retrieval/context evidence consumed.

### Regression
Run existing repository suite after new F1/F2 tests.

## Evidence report

For every demonstrative scenario output/record:

```text
input claim IDs
contextSnapshotId
retrieval query/result IDs and statuses
Coherence decision/status per affected claim
AuthorityResolution IDs/status/authority/contradiction state
fields lost = [] or exact list
fields changed = [] or exact justified list
fields invented = [] or exact defect
first divergence
reason codes
```

Do not invent percentages.

## Fix loop

If a test exposes a defect:

```text
demonstrate failure
-> identify actual root cause
-> fix smallest correct layer
-> add permanent regression
-> rerun identical scenario
-> rerun full relevant regression
```

Do not lower gates or redesign unrelated modules to achieve green.

## Closure condition

F1/F2 may be reported complete only when:

```text
canonical Understanding V3 claims reach a real immutable ContextSnapshot;
claim-bound Retrieval status/evidence reaches narrow Coherence and canonical Authority;
normal/multi-claim/ambiguous/NO_MATCH/UNAVAILABLE/ERROR/context-mismatch cases are demonstrated;
no semantic field is silently lost or invented;
legacy basic path is not authoritative for the canonical flow;
no pre-response durable write exists;
full repository regression is green;
continuity contains exact branch/HEAD/tests/defects/remaining work.
```

STOP after F1/F2 evidence. Do not automatically start Memory.

## Mandatory continuity

Before risky/large operations and after every significant commit/test/fix, update `docs/WORK_CONTINUITY.md` with:
- branch/HEAD;
- exact current operation;
- completed substeps;
- failures/defects;
- tests/results;
- files changed;
- remaining substeps in order;
- exact next action;
- closure status.

If interrupted, continuity preservation occurs before unrelated work.
