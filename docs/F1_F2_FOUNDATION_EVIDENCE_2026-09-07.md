# F1/F2 Canonical Foundation — Demonstrative Evidence

Date: 2026-09-07  
Repository: `MATRIXNEO23/assembling`  
Branch: `foundation-closure-f1-f2`  
Evidence HEAD before this document: `5892dc478072e77e5412271f7e5638f9998413cc`  
Draft PR: `#23`  
Final verification run for this evidence: GitHub Actions `#164` / run `34079096730` — **SUCCESS**

## 1. Scope actually demonstrated

This checkpoint demonstrates the structured canonical foundation path **after a canonical Understanding V3 observation already exists**:

```text
canonical Understanding V3 claims
-> CanonicalContextAssembler
-> immutable MatrixContextSnapshot
-> CanonicalClaimRetrievalStage
-> exact queryId + claimId + contextSnapshotId binding
-> CanonicalCoherenceValidator per claim
-> CanonicalUnderstandingV3AuthorityPort
-> DeterministicAuthorityResolver
-> canonical AuthorityResolution per coherent claim
```

It does **not** demonstrate raw-text Student-4 execution, Memory persistence, final BeliefState, Affective/Relationship/Decision, GGUF, OutputValidator, PersistentConsolidation, Android performance, or phone behavior.

## 2. Runtime changes

Added canonical-only F1/F2 surfaces without replacing the legacy path in-place:

```text
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationContracts.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalContextAssembler.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalRetrievalStage.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalCoherenceValidator.kt
src/main/kotlin/matrix/assembling/canonical/CanonicalFoundationOrchestrator.kt
```

Updated:

```text
IntegrationPorts.kt
- CanonicalContextPort
- CanonicalRetrievalPort
- CanonicalCoherencePort

MatrixTurnFrame.kt
- canonicalCoherenceResults claim-wise slot
- exact claim coverage/context binding validation
- Context/Retrieval/Coherence diagnostic snapshots
```

The legacy `MatrixAssemblingOrchestrator` remains compatibility-only for this checkpoint and is not used as proof of canonical F1/F2 behavior.

## 3. Coherence responsibility after F2

Canonical Coherence is deliberately narrow.

It checks only semantic/structural stability represented by V3 itself:

```text
structuralStatus
interpretationStatus
dialogueAct field status
predicate field status
subjectReferent field status
polarity field status
temporalRelation field status
```

It does not:

```text
classify REPORT/BELIEF authority
identify Memory contradiction
decide Memory persistence
choose behavior
reparse natural language
```

No arbitrary numeric confidence threshold was introduced because no calibrated F1/F2 evidence justifies one. V3 explicit RESOLVED/UNKNOWN/AMBIGUOUS/ABSTAINED/INVALID states remain authoritative for this gate.

## 4. Context behavior demonstrated

`CanonicalContextAssembler` builds one immutable `MatrixContextSnapshot` tied to:

```text
turnId
sessionId
observer/agentId
snapshotId
claim provenance
```

Each canonical claim receives a LINGUISTIC context entry carrying the exact claim identity, provenance and confidence. The complete claim remains in `canonicalUnderstandingV3`; the Context entry is a reference, not a lossy replacement.

Reserved domains explicitly report availability. Missing future modules are not represented by fabricated state.

Default pre-Memory behavior:

```text
LINGUISTIC = AVAILABLE
SYSTEM = AVAILABLE
MEMORY = UNAVAILABLE
other future state owners = NOT_WIRED
```

When a real/test retrieval provider is explicitly supplied for a scenario, the Context Memory domain can be marked `AVAILABLE` without inventing Memory records.

## 5. Retrieval behavior demonstrated

One structured `RetrievalQuery` is created per canonical claim.

Binding identity:

```text
queryId
claimId
contextSnapshotId
```

The result is accepted only when all three belong to the expected query/claim/snapshot. List order and query-string parsing are not used as identity substitutes.

Structured query evidence is projected from V3 claim fields such as:

```text
subjectRefs
entityRefs
predicate
temporal relation/anchor
```

Free text is not reparsed to recover semantic identity.

Explicit outcomes remain distinct:

```text
NO_MATCH
INDEX_UNAVAILABLE
ERROR
```

`ERROR` also records deterministic first divergence on the exact claim:

```text
RETRIEVAL.RESULT_ERROR.<claimId>
```

A stale/wrong `contextSnapshotId` fails closed as:

```text
RETRIEVAL.BINDING_MISMATCH
```

before Coherence or Authority can consume that evidence.

## 6. Demonstrative scenarios

### Scenario A — normal single claim

Fixture claim:

```text
c0 = REPORT: Marco says Anna lives in Rome
```

Observed/asserted path:

```text
ContextSnapshot created
LINGUISTIC entry payload = c0
RetrievalStatus = NO_MATCH
RetrievalResult.claimId = c0
RetrievalResult.contextSnapshotId = current snapshot
Coherence c0 = PASS
AuthorityResolution.claimId = c0
AuthorityResolution.contextSnapshotId = current snapshot
AuthorityResolutionStatus = COMPLETE
EpistemicClass = REPORT
```

Verdict: **PASS**.

### Scenario B — multi-claim

Two distinct claim IDs:

```text
c0
c1
```

Observed/asserted:

```text
2 Context claim references
2 claim-bound RetrievalResults
2 independent Coherence results
2 independent AuthorityResolutions
all resolution contextSnapshotIds = current snapshot
no claim identity collapse
```

Verdict: **PASS**.

### Scenario C — ambiguous semantic claim

Subject referent is explicitly V3 `AMBIGUOUS`; interpretation status is `AMBIGUOUS`.

Observed/asserted:

```text
Coherence c0 = HOLD
reason includes COHERENCE.INTERPRETATION_AMBIGUOUS
reason includes COHERENCE.FIELD_AMBIGUOUS.subjectReferent
canonical Authority remains UNAVAILABLE / not executed
firstDivergence = COHERENCE.CLAIM_HOLD.c0
```

No subject/owner/source/perspective is guessed.

Verdict: **PASS / FAIL-CLOSED**.

### Scenario D — Memory retrieval unavailable

Observed/asserted:

```text
Context MEMORY = UNAVAILABLE
provider is not called
RetrievalStatus = INDEX_UNAVAILABLE
AuthorityResolutionStatus = UNAVAILABLE
```

It is not converted to `NO_MATCH`.

Verdict: **PASS / FAIL-CLOSED**.

### Scenario E — Retrieval ERROR

Observed/asserted:

```text
RetrievalStatus = ERROR
AuthorityResolutionStatus = ERROR
firstDivergence = RETRIEVAL.RESULT_ERROR.c0
```

It is not converted to `UNAVAILABLE` or `NO_MATCH`.

Verdict: **PASS / ERROR PRESERVED**.

### Scenario F — stale Context binding

Provider returns a result bound to `ctx:stale`.

Observed/asserted:

```text
MatrixBoundaryViolationException
firstDivergence = RETRIEVAL.BINDING_MISMATCH
Coherence not allowed to consume stale retrieval
Authority not allowed to consume stale retrieval
```

Verdict: **PASS / FAIL-CLOSED**.

## 7. Field preservation audit

### Semantic fields lost

```text
NONE demonstrated at the F1/F2 workspace boundary.
```

Reason: `canonicalUnderstandingV3` remains the complete lossless carrier. Context and Retrieval add derived references/query fields but do not replace the original claim.

The existing Authority V3 projection regression suite remains green and already checks structured propagation of:

```text
source
subject
owner
perspective
object span/value
claimKind
temporal anchor
claim identity/provenance
```

### Semantic fields changed

```text
NONE intentionally changed by F1/F2.
```

Context/Query/Resolution IDs are new operational identities, not mutations of semantic meaning.

### Semantic fields invented

```text
NONE.
```

System-generated operational metadata is expected and explicit:

```text
contextSnapshotId
context entry ID
retrieval queryId
Authority resolutionId
reason codes
diagnostic events
```

These values describe processing identity/traceability; they do not invent subject, owner, source, perspective, predicate, truth, Memory or state.

## 8. Regression evidence

### Run #162

HEAD `3c63757ec5f4b7c17b7322449422a7b09c698b2b`

```text
full Gradle test suite = SUCCESS
```

Covered initial canonical F1/F2 demonstrative suite.

### Run #163

HEAD `49204a97197e848156c345ae472ec6add3c884a6`

```text
BUILD SUCCESSFUL
compileKotlin = PASS
compileTestKotlin = PASS
test = PASS
full repository regression = PASS
```

Added explicit Retrieval ERROR first-divergence implementation.

### Run #164

HEAD `5892dc478072e77e5412271f7e5638f9998413cc`

```text
full repository regression = SUCCESS
```

Added permanent test asserting:

```text
RetrievalStatus.ERROR
-> firstDivergence = RETRIEVAL.RESULT_ERROR.c0
```

## 9. Remaining limitations — do not overclaim

F1/F2 does not yet prove:

```text
raw user text -> actual Student-4 ONNX -> canonical Understanding V3
real Memory INDEX_PROBE backend
real Memory record retrieval
real BeliefState persistence/revision
Memory Admission/Repository
Affective/Relationship/Intimacy/Goal/Decision
real GGUF
real Output Validator
Persistent Consolidation
complete engine E2E
Moto G56 runtime/performance
```

The Memory domain is intentionally unavailable by default until the real Memory read backend exists.

## 10. F1/F2 verdict

Under the project strict demonstration criterion:

```text
CANONICAL STRUCTURED FOUNDATION F1/F2 = PASS FOR TESTED SCOPE
RAW-LANGUAGE ENGINE = NOT YET DEMONSTRATED
MEMORY = NOT STARTED BY THIS CHECKPOINT
FULL ENGINE = NOT YET DEMONSTRATED
PHONE = NOT AUTHORIZED YET
```

F1/F2 stops here. The next workstream requires a new explicit owner authorization and must follow the complete real design rather than silently continuing into Memory.
