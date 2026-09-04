# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T15:27+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v3`
Current HEAD before pause: `f5c86ba5913bf34aa4cbff7b75f44b32b9528e3a`
Latest verified CI run: `33877882671` — `Matrix Assembling CI` — `success`

## Stop reason

Session paused by user request: "dobbiamo fermarci un attimo".

This file is the canonical restart point for continuing the `assembling` work without losing state.

## Current objective

Build `MATRIXNEO23/assembling` as the integration layer that connects the independently developed Matrix/Luna components:

```text
Understanding / Matrix-NLU
→ canonical MatrixTurnFrame
→ Coherence / Authority
→ Memory admission placeholder
→ Affective Engine
→ SemanticFrameToPrompt
→ GGUF adapter
```

`assembling` must not become a training lab and must not hide missing components. It should expose clear contracts, adapters, tests and continuity documents.

## Source repositories

| Component | Repository | Status | Import strategy |
|---|---|---|---|
| Understanding / NLU | `MATRIXNEO23/matrix-understanding-lab` | Active lab; Student-4-v2.2A still experimental until evaluation/frozen authorization | Copy stable contracts/runtime-facing interfaces and adapt to `MatrixTurnFrame` |
| Affective Engine / sentimenti | `MATRIXNEO23/matrix-affective-lab` | Validated prototype, not Android production module yet | Copy prototype and wrap behind a bridge/adapter |
| Memory | Not implemented yet | Missing real backend | Use only `NoPersistentMemoryAdmission` / `NO_MEMORY_BACKEND` |
| GGUF | Exists in Android app, not in assembling | Real adapter not connected yet | Use placeholder/fake until llama.cpp/MLC bridge is imported |

## Current architectural decision

Use one canonical frame per turn:

```text
MatrixTurnFrame
```

Every module receives the same frame and returns an updated copy.

Do not connect modules directly like this:

```text
NLU → Memory
GGUF → Memory
Affective → Memory DB
GGUF → Affective persistent state
```

Allowed route:

```text
UserMessage
→ NluPort
→ UnderstandingPort
→ CoherenceGuardPort
→ AuthorityResolverPort
→ MemoryAdmissionPort
→ AffectivePort
→ SemanticFrameToPromptPort
→ GgufPort
→ AssistantReply
```

## Implemented core files

| File | Purpose | Status |
|---|---|---|
| `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt` | Canonical per-turn envelope; contains input, NLU, semantic, claims, coherence, authority, memory, affective, prompt, reply, diagnostics | Implemented |
| `src/main/kotlin/matrix/assembling/IntegrationPorts.kt` | Frame-based ports for all modules | Implemented |
| `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt` | Sequential frame pipeline orchestrator | Implemented |
| `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt` | Main frame-based prompt builder | Implemented |
| `src/main/kotlin/matrix/assembling/prompt/SemanticFrameToPrompt.kt` | Older/parallel contract-level prompt translator | Present; keep until duplicate-contract cleanup |
| `src/main/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipeline.kt` | Earlier directive/prompt pipeline using `contracts/*` types | Present; still tested |
| `src/main/kotlin/matrix/assembling/contracts/MatrixAssemblyContracts.kt` | Earlier contract model used by old prompt pipeline | Present; still tested |

Important: there are currently two prompt/contract paths:

```text
A. Frame path: MatrixTurnFrame.kt + IntegrationPorts.kt + MatrixAssemblingOrchestrator.kt + root SemanticFrameToPrompt.kt
B. Older contract path: contracts/* + pipeline/* + prompt/*
```

Do not delete either blindly. Next cleanup must identify whether B is still needed or should be migrated into A.

## Imported component snapshots

### Understanding / Matrix-NLU

Copied/adapted from `MATRIXNEO23/matrix-understanding-lab`:

```text
vendor/matrix-understanding-lab/core/src/main/java/com/matrix/p0/Domain.java
vendor/matrix-understanding-lab/core/src/main/java/com/matrix/p0/UnderstandingEngine.java
vendor/matrix-understanding-lab/matrix_nlu/labels.py
vendor/matrix-understanding-lab/matrix_nlu/inference.py
```

Source contract facts:

- `UnderstandingEngine.interpret(caseId, language, text, context)` returns an `Interpretation`.
- `Domain.Claim` has `speaker`, `subject`, `target`, `owner`, `perspective`, `dialogueAct`, `predicate`, `objectValue`, `polarity`, `negationScope`, `temporalRelation`, `temporalExpression`, `entities`, `claimKind`, `confidence`, `sourceSpans`, `sourceIds`, `worldTruth`.

Assembly adapter:

```text
src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt
```

Role:

```text
Matrix-NLU/runtime output
→ NluOutput
→ SemanticFrame
→ TypedClaim
→ MatrixTurnFrame
```

### Affective Engine

Copied/adapted from `MATRIXNEO23/matrix-affective-lab`:

```text
vendor/matrix-affective-lab/src/affective_engine.py
```

Source prototype facts:

- Has `AffectiveEngine`.
- Supports transient emotions, mood, PAD-like values.
- Persistent affect fields include: `trust`, `attachment`, `affection`, `attraction`, `resentment`, `respect`, `admiration`, `aversion`.

Assembly adapter:

```text
src/main/kotlin/matrix/assembling/adapters/AffectiveLabAdapter.kt
```

Role:

```text
SemanticFrame + MemoryAdmissionResult
→ AffectiveRuntimeRequest
→ AffectiveState
→ MatrixTurnFrame
```

Persistent affect is guarded:

```text
persistentAllowed = memory.stableWrite == true && semantic.stableMemoryAllowed
```

## Memory state

Memory backend is not implemented yet.

Current rule:

```text
No real memory writes.
No fake persistence.
No fake memory IDs.
No hidden stable state.
```

Allowed temporary adapters:

```text
src/main/kotlin/matrix/assembling/adapters/NoPersistentMemoryAdmission.kt
src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt::BasicMemoryAdmission
```

Current required behavior:

```text
status = NO_MEMORY_BACKEND / PROVISIONAL_CLAIM / REJECTED
memoryIds = emptyList()
stableWrite = false
```

`BasicMemoryAdmission` was fixed because it could previously return `ADMITTED`/`stableWrite=true`, which was wrong while no memory backend exists.

Fix commit:

```text
9cd0df38f2e2dcf53c418b2246c30fc99d9461e0
```

## Component mapping audit performed

User requested check:

```text
A produces X
B receives Y
verify mapping, types, defaults, missing required fields, naming consistency
apply minimum fix only
```

Audit file:

```text
docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md
```

Findings:

### Finding 1 — Understanding → MatrixTurnFrame was lossy

Problem:

The source Understanding contract already produces resolved fields:

```text
subject
target
owner
perspective
objectValue
worldTruth
```

The first `UnderstandingLabAdapter` mainly transported:

```text
subjectReferent
targetReferent
ownerReferent
perspectiveReferent
objectSpan
```

Risk:

```text
Domain.Claim.target = "Marco"
```

could degrade into:

```text
targetReferent = KNOWN_ENTITY
```

without preserving the actual target value.

Minimal fix applied:

- Extend `NluOutput` with optional resolved fields:

```text
resolvedSubject
resolvedTarget
resolvedOwner
resolvedPerspective
objectValue
sourceType
worldTruth
```

- Extend `TypedClaim` with:

```text
perspective
worldTruth
```

- Update only `UnderstandingLabAdapter` to preserve the fields.
- Keep existing labels/fields untouched.
- Use safe defaults for backwards compatibility.

Commits:

```text
bd612ee386443e06711137281eaa6d01e85830ad
5f3a5db748f4490e99b0d4e653025b7456a70bfc
```

### Finding 2 — Third-party/report could be over-stabilized

Problem:

High confidence alone was not enough to prevent an indirect report from being treated like stable truth.

Minimal fix applied in `UnderstandingLabAdapter`:

```text
stableMemoryAllowed requires:
- dialogueAct in ASSERT/CORRECT
- predicate != speech.unresolved
- sourceType != THIRD_PARTY_REPORT
- worldTruth == true
- overall confidence >= 0.75
```

### Finding 3 — Affective persistence gate is correct

`AffectiveLabAdapter` already blocks persistent emotional/relationship deltas unless both semantic and memory gates allow it.

### Finding 4 — Memory placeholder was too permissive

Problem:

`BasicMemoryAdmission` could return `ADMITTED`/`stableWrite=true` despite memory being absent.

Minimal fix:

- `BasicMemoryAdmission` now never returns stable writes.
- Returns `NO_MEMORY_BACKEND` or `REJECTED`.
- Does not create `memoryIds`.

Commit:

```text
9cd0df38f2e2dcf53c418b2246c30fc99d9461e0
```

Audit documentation commit:

```text
0fc7294e99dfc957feaa381651d4562d37be695c
```

Audit update commit:

```text
f5c86ba5913bf34aa4cbff7b75f44b32b9528e3a
```

## Tests added / active

Existing earlier prompt tests:

```text
src/test/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipelineTest.kt
```

New mapping compatibility tests:

```text
src/test/kotlin/matrix/assembling/adapters/ComponentMappingCompatibilityTest.kt
```

Coverage:

1. `understandingAdapterPreservesResolvedClaimFields`
2. `thirdPartyReportCannotBecomeStableMemoryByDefault`
3. `affectiveAdapterDoesNotApplyPersistentDeltaWithoutStableMemory`
4. `affectiveAdapterMapsStableSemanticMemoryToPersistentTarget`

Commit:

```text
e3417637c544505acc0733cd7a59c0b6db318e2b
```

## CI state before pause

Latest checked run:

```text
Run: 33877882671
Workflow: Matrix Assembling CI
Commit: f5c86ba5913bf34aa4cbff7b75f44b32b9528e3a
Status: completed
Conclusion: success
Updated: 2026-09-04T13:26:01Z
```

Therefore the repo is paused in a CI-green state.

## Current verified route

Conceptual full route now represented in code:

```text
UserMessage
→ MatrixTurnFrame
→ UnderstandingLabAdapter.analyze
→ UnderstandingLabAdapter.understand
→ CoherenceGuardPort.check
→ AuthorityResolverPort.resolve
→ MemoryAdmissionPort.admit
→ AffectiveLabAdapter.update
→ SemanticFrameToPromptPort.buildPrompt
→ GgufPort.generate
→ AssistantReply
```

Simulatable now:

```text
fake MatrixNluRuntimeBridge
+ fake AffectiveRuntimeBridge
+ NoPersistentMemoryAdmission
+ SemanticFrameToPrompt
+ EchoGgufAdapter
```

Not yet real:

```text
real ONNX Student-4-v2.2A runtime bridge
real GGUF llama.cpp/MLC adapter
real MemoryRepository adapter
real Android app integration
```

## Active constraints

- Do not lower gates.
- Do not hide bugs by rewriting tests.
- Apply minimum fix only.
- Do not invent memory backend.
- Do not write stable memory until a real Memory Foundation exists.
- Do not let GGUF decide truth, memory, consent, ownership or relationship state.
- GGUF receives natural-language prompt summaries, not raw numeric/internal fields.
- NLU is semantic sensor, not censor.
- Adult/intimacy handling is semantic robustness only.
- Frozen remains unread unless explicitly authorized later.
- No automatic production approval.

## Next exact restart task

Resume from this point:

```text
Task: create an end-to-end smoke test around MatrixAssemblingOrchestrator.
```

Test should instantiate:

```text
UnderstandingLabAdapter(fake MatrixNluRuntimeBridge)
BasicCoherenceGuard or current CoherenceGuardPort implementation
BasicAuthorityResolver
NoPersistentMemoryAdmission
AffectiveLabAdapter(fake AffectiveRuntimeBridge)
SemanticFrameToPrompt
EchoGgufAdapter
MatrixAssemblingOrchestrator
```

Required test cases:

1. Negative/refusal case:

```text
"Non voglio uscire con Marco"
```

Expected:

```text
polarity = NEGATIVE
no stable memory
prompt says not to invert negation/refusal
```

2. Third-party report:

```text
"Marco dice che Sara mi odia"
```

Expected:

```text
sourceType = THIRD_PARTY_REPORT
worldTruth = false
stableMemoryAllowed = false
memory stableWrite = false
```

3. Request to Luna:

```text
"Vieni con me al bar?"
```

Expected:

```text
dialogueAct = REQUEST
memory stableWrite = false
possible transient affect only
```

4. Stable direct assertion simulation:

```text
"Vivo a Padova"
```

Expected with current no-memory backend:

```text
semantic.stableMemoryAllowed may be true
memory.stableWrite must still be false
memory.status = NO_MEMORY_BACKEND
```

After this, check CI. If it fails, fix only the actual compatibility/compile bug. Do not modify tests to hide the mismatch.

## Restart command/context for Work

Use this exact intent when resuming:

```text
Continue in MATRIXNEO23/assembling from docs/WORK_CONTINUITY.md.
Do not restart architecture.
Do not add fake memory.
Implement the next exact restart task: end-to-end smoke test around MatrixAssemblingOrchestrator using fake runtime bridges and no persistent memory.
Then run CI and fix only real failures with minimum changes.
```
