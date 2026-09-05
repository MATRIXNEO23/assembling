# Matrix Engine — Canonical Checkpoint Roadmap

Status: **ACTIVE / BINDING**  
Repository owner for this roadmap: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Method dependency: `docs/MATRIX_ENGINE_WORK_METHOD.md`

## 0. Golden rules

1. **One checkpoint at a time.**
2. **Do not start the next checkpoint until the current checkpoint has a 100% green required suite.**
3. **If work must be interrupted, first commit/update `docs/WORK_CONTINUITY.md` with the exact restart point.**
4. **Contract before code.** Freeze or explicitly version every cross-module contract before implementation.
5. **Adapters validate; they do not guess.** Any non-lossless translation fails closed.
6. **Preserve UNKNOWN / UNRESOLVED / AMBIGUOUS / NOT_APPLICABLE / UNAVAILABLE / NO_MATCH distinctions.**
7. **Every functional fix gets a regression test.**
8. **Every module checkpoint requires unit + boundary + cross-module tests appropriate to its scope.**
9. **DiagnosticTrace must remain end-to-end observable with deterministic reason codes and first divergence.**
10. **No durable Memory write before accepted output/action validation.**
11. **One writable repository at a time unless the owner explicitly switches.**
12. **Never lower a gate to make a checkpoint pass.**

A checkpoint may end only as:

```text
PASS
PASS_WITH_NONBLOCKING_RISKS
BLOCKED
```

P0 failure => `BLOCKED`.  
A P1 may be non-blocking only when it cannot invalidate the semantics or safety of the next checkpoint and is explicitly recorded.

---

# PHASE U — UNDERSTANDING V3

## CP-U1 — V3 lossless contract audit — ACTIVE

Goal: prove whether frozen `MATRIX_NLU_CONTRACT_V3` can be transported into the canonical Matrix/MIP runtime without semantic loss.

Audit every V3 field and classify it exactly as:

```text
LOSSLESS
REPRESENTABLE_WITH_EXISTING_MIP_FIELD
MISSING_FROM_MIP_RUNTIME
LEGACY_ONLY_LOSS
NOT_APPLICABLE_DOWNSTREAM
```

Must cover at minimum:

```text
speaker / observer / source / subject / target / owner / perspective
dialogueAct / predicate / claimKind / polarity
subjectSpans[] / objectSpans[] / negationCueSpans[] / temporalEvidence[]
entityMentionIds / referentCandidates
fieldStatus / alternatives / confidenceByField
temporalRelation + temporalAnchorRef
structuralStatus / interpretationStatus / overallInterpretationConfidence
multi-claim identity and provenance
```

Gate:

- complete field-by-field audit;
- no silent loss accepted;
- no implementation shortcut;
- no `matrix-understanding-lab` writes from this checkpoint;
- continuity updated.

Exit:

- if existing MIP is lossless -> CP-U3;
- if MIP/runtime is insufficient -> CP-U2.

## CP-U2 — Understanding/MIP contract extension — CONDITIONAL

Run only if CP-U1 proves a frozen V3 field cannot be represented losslessly.

Goal: extend/version the Assembling/MIP boundary minimally and explicitly.

Gate:

- no duplicate semantic owner;
- no free-text re-parsing contract;
- source != perspective preserved;
- plural spans/evidence preserved;
- field statuses preserved;
- temporal anchor identity preserved;
- cross-version compatibility strategy explicit;
- full Kotlin regression green.

Exit: contract frozen -> CP-U3.

## CP-U3 — Canonical Understanding V3 adapter/runtime boundary

Goal:

```text
MatrixNluOutput V3
-> validated adapter
-> canonical MIP/Typed Claims
```

Hard rules:

- Understanding does not infer linguistic meaning again;
- no regex linguistic patch layer;
- no hardcoded owner/source/perspective;
- no first-claim-only behavior;
- invalid/abstained/ambiguous V3 statuses survive downstream;
- no World Truth, Memory or Authority decision emitted here.

Gate:

- unit tests for every V3 role/status;
- IT/EN/ES/code-switch fixtures where applicable;
- multi-claim fixtures;
- negation plural-span fixtures;
- temporal-anchor fixtures;
- report/belief/hypothesis/correction/request/goal fixtures;
- adult/intimacy semantic fixtures;
- full regression green.

## CP-U4 — Understanding cross-module freeze

Goal: prove the boundary works with the current Assembling runtime before Authority rewire.

Required cross-module path:

```text
V3 fixture/runtime output
-> Understanding adapter
-> MatrixTurnFrame
-> canonical claim representation
-> DiagnosticTrace
```

Gate:

- no field loss;
- no legacy auto-collapse;
- no cross-claim leakage;
- no guessed identity;
- DiagnosticTrace first divergence correct;
- full repository suite 100% green.

Exit: **UNDERSTANDING_V3_FROZEN_FOR_INTEGRATION**.

---

# PHASE A — CANONICAL AUTHORITY ORCHESTRATOR INTEGRATION

Prerequisite: CP-U4 PASS.

## CP-A1 — Canonical Authority runtime port/stage

Goal: add the dedicated canonical Authority stage without mutating legacy port semantics in place.

Path:

```text
canonical claims
+ MatrixContextSnapshot
+ RetrievalResult[]
-> CanonicalAuthorityRuntimeAdapter
-> DeterministicAuthorityResolver per claim
-> canonicalAuthorityResolutions[]
```

Gate:

- explicit claim-by-claim invocation;
- no implicit first claim;
- Context/Retrieval availability semantics preserved;
- no write to legacy `AuthorityDecision` as a lossy projection;
- Memory still read-only/preflight only;
- full regression green.

## CP-A2 — Authority orchestrator rewire + compatibility freeze

Goal: make canonical Authority the authoritative path in the orchestrator while quarantining legacy compatibility.

Gate:

- NLU/Understanding -> Context/Retrieval -> Authority integration tests;
- REPORT != WORLD_TRUTH;
- BELIEF != REPORT;
- INFERENCE requires derivation;
- temporal change != contradiction by default;
- same actor != contradiction;
- multiple plausible contradiction targets -> HOLD/AMBIGUOUS;
- canonical contradiction identity survives;
- DiagnosticTrace end-to-end;
- full suite 100% green.

Exit: **CANONICAL_AUTHORITY_ORCHESTRATOR_FROZEN**.

---

# PHASE M — MEMORY

Prerequisite: CP-A2 PASS.

Memory canonical flow:

```text
TypedClaim
-> AuthorityResolution
-> Memory Admission
-> MemoryRepository
```

Authority identifies semantic contradiction; Memory Admission does not infer contradiction from mere text difference/shared actors.

## CP-M1 — Memory Admission contract + adapter

Implement/verify canonical decisions:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Gate:

- explicit `contradicts_memory_id` / canonical contradiction identity consumed;
- no direct GGUF/NLU persistence access;
- correction is evidence, not automatic supersession;
- historical change not contradiction by default;
- low/unresolved authority fails closed;
- unit + Authority->Admission cross-module tests green.

## CP-M2 — MemoryRepository persistence + lineage

Goal: real durable persistence.

Gate:

- SAVE atomic;
- SUPERSEDE atomic;
- lineage preserved;
- old record retained as historical/superseded rather than silently overwritten;
- protected delete/lineage invariants;
- transaction rollback on failure;
- idempotency/duplicate behavior explicitly tested;
- restart/reopen persistence test green.

## CP-M3 — Retrieval integration

Goal: retrieve the right memory, not merely save the right memory.

Mandatory normal-turn behavior:

```text
EVERY TURN -> MEMORY INDEX PROBE
```

Then hydrate/rerank/deep retrieval only when needed.

Gate:

- relevant memory recovered;
- irrelevant memories rejected/deprioritized;
- `NO_MATCH != UNAVAILABLE`;
- superseded memory not treated as current state;
- history remains retrievable for history purpose;
- multi-claim retrieval remains separated;
- deterministic provenance and DiagnosticTrace present.

## CP-M4 — Persistent Consolidation after validation

Goal:

```text
response/action accepted
-> VALIDATE
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
```

Gate:

- pre-response `stableWrite == false`;
- pre-response `memoryIds == []`;
- rejected/invalid response produces no durable commit;
- accepted result commits exactly once;
- rollback atomicity verified;
- DiagnosticTrace records commit/no-commit reason.

## CP-M5 — Memory automatic E2E freeze

Mandatory automatic scenarios include at minimum:

```text
"Vivo a Milano" -> save current fact
new turn -> retrieve Milano

"Prima vivevo a Venezia, ora vivo a Milano"
-> temporal history preserved
-> no false simultaneous contradiction

"Marco dice che Anna vive a Roma"
-> REPORT preserved
-> not promoted to WORLD_TRUTH

"Mi ero sbagliato, vivo a Torino"
-> correct contradiction target
-> SUPERSEDE correct current memory
-> lineage preserved
-> Torino current, previous state historical

restart process/database
-> correct current memory still retrieved

forced persistence failure
-> atomic rollback / no partial state
```

Gate: all automatic E2E tests green, no P0 remaining.

Exit: **MEMORY_CORE_FROZEN_FOR_DEVICE_TEST**.

---

# PHASE E — FULL ENGINE AUTOMATIC INTEGRATION

Prerequisite: CP-M5 PASS.

## CP-E1 — Closed-loop automatic integration

Path:

```text
input
-> NLU runtime or controlled V3 fixture
-> Understanding
-> Context/Retrieval
-> Authority
-> Memory Admission
-> response path
-> validation
-> durable commit
-> later turn
-> retrieval
```

Gate:

- contract fingerprints/versions checked;
- adapters validated;
- cross-module state ownership respected;
- DiagnosticTrace complete from input to future retrieval;
- no persistence before validation;
- full regression 100% green.

---

# PHASE APK — FIRST DIAGNOSTIC APK

Prerequisite: CP-E1 PASS.

## CP-APK1 — Runtime loop closure with provisional INT8 encoder

Purpose: integration/runtime diagnostics only.

Allowed temporary model:

```text
Student-5 Path A ONNX INT8 runtime-probe backbone
```

It is NOT the final Matrix-NLU V3 and must not be promoted as such.

Semantic correctness tests continue to use canonical V3 fixtures until final Student-5 V3 is ready.

APK diagnostics must expose at least:

```text
INPUT
NLU/runtime status
claims
Understanding output
Context availability
Retrieval query/result
AuthorityResolution
contradiction target
Memory Admission decision
pre/post DB state
commit/no-commit
future retrieval
first divergence
CPU/RAM/latency
```

Gate: APK loads/runs, closed communication circuit works, no crash/serialization mismatch, diagnostics complete.

## CP-APK2 — Real device Memory tests

Target device tests verify:

- correct save;
- correct non-save;
- correct supersede;
- correct history;
- correct retrieval;
- restart persistence;
- multi-turn stability;
- no legacy/canonical field collapse.

Exit: **BASE_COGNITIVE_CORE_DEVICE_VALIDATED**.

---

# PHASE N — FINAL STUDENT-5 V3 MODEL SWAP

This phase is independently produced in `matrix-understanding-lab` under its own authorized task sequence.

When final Student-5 V3 becomes available:

## CP-N1 — Physical V3 load/export/quantization verification

- exact contract fingerprint;
- 16 heads present;
- physical ONNX export/load;
- quantized artifact checksum;
- latency/RAM/size measured;
- no architecture change to Assembling.

## CP-N2 — Swap provisional runtime for final Matrix-NLU V3

Gate:

- same canonical V3 adapter contract;
- no downstream redesign required;
- automatic E2E re-run;
- device regression re-run;
- semantic benchmark gates remain unchanged.

---

# PHASE R — REFLECTION

Prerequisite: Memory automatic + device behavior is trustworthy enough that Reflection is not reasoning over corrupted memories.

## CP-R1 — Reflection contract freeze

Canonical concept:

```text
Memory / episodes / state / patterns
-> Reflection analysis
-> structured ReflectionCandidate
-> Authority
-> Memory Admission
```

Reflection never writes Memory directly.

Contract must define provenance, confidence, evidence memory refs, temporal scope, pattern/inference type, ambiguity and diagnostics.

## CP-R2 — Reflection implementation

Gate:

- no free-form persistence path;
- all conclusions evidence-linked;
- weak conclusions can abstain;
- no ownership violation;
- unit suite green.

## CP-R3 — Reflection cross-module integration

Path:

```text
retrieved evidence
-> Reflection
-> Authority
-> Memory Admission
-> optional REFLECTION memory
```

Gate:

- false pattern resistance;
- contradictory evidence handling;
- confidence propagation;
- lineage/provenance preserved;
- no direct repository mutation;
- DiagnosticTrace complete;
- full suite green.

## CP-R4 — Reflection freeze

Exit only after automatic and device-observable behavior is acceptable.

---

# Mandatory checkpoint save record

At every checkpoint transition, `docs/WORK_CONTINUITY.md` must record:

```text
checkpoint ID
status
branch
base SHA
current/final HEAD
changed files
contract/spec version
completed tests and exact results
CI run ID + conclusion
known P0/P1/P2 risks
forbidden scopes untouched
next checkpoint
exact restart action
```

If interrupted mid-checkpoint, also record:

```text
last completed operation
operation currently in progress
last green commit
uncommitted/unverified work if any
what must NOT be repeated
```

# Current checkpoint

As of 2026-09-05:

```text
ACTIVE = CP-U1 — V3 LOSSLESS CONTRACT AUDIT
NEXT = determined by CP-U1 result:
  lossless -> CP-U3
  missing canonical representation -> CP-U2
```

No later checkpoint is authorized to preempt CP-U1 unless the owner explicitly changes priority after an exact continuity save.