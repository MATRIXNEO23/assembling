# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T05:35+02:00  
Repository: `MATRIXNEO23/assembling`  
Active work branch: `remaining-integration-boundaries-20260905`  
Continuity schema: `matrix.assembling.continuity.v9`  
Base main for this workstream: `a46919d925824e12d66d074a77c231aa2b4b7a1b`  
Branch tip before this continuity update: `7d718fc1b7941427e90daec50f55337b4a9cbf76`  
Pull request: `#6` — `Finish remaining integration boundary fixes`

## Canonical work rules

- work on one repository at a time unless the owner explicitly says otherwise;
- active repository is `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- when a component changes, keep code, tests, active documents and continuity coherent in the same workstream;
- do not create parallel specifications when an active canonical document can be updated.

No other repository was modified in this workstream.

## Canonical direction

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

```text
Input
→ NLU / Understanding
→ Working Context / context read
→ Coherence / Authority
→ MemoryPreflightPort
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF
→ OutputValidatorPort
→ PersistentConsolidationPort
```

Missing target phases remain explicitly `NON_CABLATO`.

## Prior integrated hardening preserved

### P0 — integrated before this workstream

- all NLU claims preserved in `typedClaims`;
- missing critical confidence fails closed;
- illegal pre-response stable-memory result is rejected;
- CI evidence: `33906844505`, `33907038217` — SUCCESS.

### P1 + DiagnosticTrace — integrated before this workstream

- Affective cannot own RelationshipState;
- Affective persistence cannot exceed upstream authorization;
- unresolved subject remains `UNKNOWN`;
- typed `DiagnosticTrace` with write-once `firstDivergence`;
- canonical end-to-end smoke tests through Affective;
- CI evidence: `33907887621`, `33908146792` — SUCCESS.

### Prompt/compatibility cleanup — integrated immediately before this branch

PR `#4` was merged to main as:

```text
a46919d925824e12d66d074a77c231aa2b4b7a1b
```

It keeps `SemanticFrameToPrompt` realization-only, deprecates the legacy pipeline for new callers and adds `PromptBoundaryTest`.

PR `#5` was closed without merge because it was based on stale main and overlapped newer hardening. No code from that divergent PR is authoritative.

## Current workstream — remaining minimal fixes

### 1. Claim-wide confidence validation

Before:
`BasicCoherenceGuard` inspected only primary `SemanticFrame.confidence`, so a secondary claim could lack or fail a critical head without being detected.

Now:
- every `TypedClaim` is checked;
- missing/low critical values in any claim fail closed;
- diagnostics identify exact claim/key, for example `claim[1].token.negation`;
- no threshold was lowered.

Critical heads:

```text
token.negation
sequence.predicate
sequence.subjectReferent
sequence.targetReferent
```

### 2. Explicit multi-claim Authority state

Before:
multiple claims were preserved, but Authority still exposed the first claim's source type.

Now:

```text
multiple claims
→ SAFE_TRANSIENT_ONLY
→ sourceType=MULTI_CLAIM
→ direct authority rejected
```

Per-claim final contextual Authority remains future work.

### 3. Memory preflight vs durable commit

Authoritative pre-response API:

```text
MemoryPreflightPort.evaluate
```

Compatibility:

```text
MemoryAdmissionPort
= deprecated facade only
```

Future durable boundary:

```text
PersistentConsolidationPort
```

The existing orchestrator fail-closed invariant remains:

```text
before output validation:
stableWrite == false
memoryIds == []
```

Current adapters `NoPersistentMemoryAdmission` and `BasicMemoryAdmission` now implement `MemoryPreflightPort` directly. Their old `admit()` methods remain only deprecated helper methods.

### 4. Output validation boundary

`OutputValidatorPort` is now an optional post-GGUF boundary.

- if supplied, it executes after GGUF generation;
- if absent, diagnostics record `output.validation=NON_CABLATO`;
- no fake validator was implemented;
- no persistent consolidation runs automatically.

### 5. Explicit adult/intimacy NLU marker

`MatrixNluClaim` and `NluOutput` now support:

```text
adultOrIntimacy: Boolean?
```

Understanding prefers this explicit semantic marker. The existing local keyword fallback remains only for older runtimes that do not supply the field.

Adult/intimacy remains semantic context, not censorship and not an automatic persistence penalty.

## Files changed in PR #6

Code/contracts:
- `src/main/kotlin/matrix/assembling/IntegrationPorts.kt`;
- `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt`;
- `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt`;
- `src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt`;
- `src/main/kotlin/matrix/assembling/adapters/NoPersistentMemoryAdmission.kt`;
- `src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt`.

Tests:
- `src/test/kotlin/matrix/assembling/ArchitectureBoundaryTest.kt`;
- `src/test/kotlin/matrix/assembling/MatrixAssemblingOrchestratorIntegrationTest.kt`.

Active documents updated in place:
- `docs/MEMORY_INTEGRATION_POLICY.md`;
- `docs/MEMORY_INTEGRATION_STATUS.md`;
- `docs/MODULE_CONNECTIONS.md`;
- `docs/ASSEMBLY_PLAN.md`;
- `docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md`;
- `docs/WORK_CONTINUITY.md`.

No new competing architecture document was created.

## Tests added/extended

- secondary claim with missing critical confidence fails closed;
- exact missing key is visible in diagnostics;
- multi-claim Authority exposes `MULTI_CLAIM` and cannot be directly accepted;
- pre-response durable result is rejected through `MemoryPreflightPort`;
- explicit NLU adult/intimacy marker reaches `SemanticFrame`;
- output validator sees an already-generated GGUF reply;
- absent validator remains explicitly `NON_CABLATO`;
- all earlier P0/P1/DiagnosticTrace/Prompt tests remain active.

## CI evidence

Current code and test head:

```text
72689f7eb28f6612dd6ae0be7432a3be484ed3fb
```

CI:

```text
run 33942024450
workflow Matrix Assembling CI
job kotlin-tests
conclusion SUCCESS
```

Later documentation-only branch runs were triggered after that green code/test result. Before merge, verify the latest PR head remains green.

## Current hard boundaries

- NLU/Understanding do not write memory;
- Understanding does not own durable admission;
- all NLU claims are preserved;
- every claim must carry valid critical confidence;
- unresolved subject remains unresolved;
- Authority does not write persistence;
- multi-claim turns cannot masquerade as one direct authority;
- pre-response memory is preflight only;
- durable memory requires future post-validation consolidation;
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed authorization;
- Prompt Builder remains realization-only;
- GGUF remains language realization only;
- adult/intimacy is first-class semantic context.

## Model/artifact state

Student-4-v2.2A remains a controlled runtime candidate and is not production approved.

Mixed-head-protected artifact remains available through Git LFS.

Full artifact SHA-256:

```text
4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5
```

No Frozen access, retraining, quantization change or gate reduction occurred in this workstream.

## Still NON_CABLATO

- explicit Working Context / context-read port;
- real Long-Term retrieval;
- per-claim final contextual Authority/Belief resolution;
- real semantic `OutputValidatorPort` implementation;
- real `PersistentConsolidationPort` implementation;
- real Memory Foundation adapter and atomic commit;
- canonical RelationshipState controller;
- BDI-lite + Utility Decision layer;
- real llama.cpp/MLC GGUF bridge;
- Android application integration.

## Residual risks

1. Multi-claim turns are preserved and safely held, but not yet resolved claim-by-claim against context/memory.
2. The output-validation port exists, but the real semantic validator is absent.
3. Persistence remains disabled; no end-to-end SAVE/SUPERSEDE/rollback can be tested in Assembling yet.
4. The adult/intimacy compatibility keyword fallback remains until every runtime emits the explicit marker.

## Next exact work target after PR #6 merge

Continue only in `MATRIXNEO23/assembling`:

1. introduce explicit Working Context and read-only context ports;
2. add bounded Long-Term retrieval contract only when its dependency is ready;
3. implement per-claim contextual resolution before any multi-claim persistence;
4. implement a real semantic output validator as a separate controlled workstream;
5. connect real Memory Foundation only through `PersistentConsolidationPort`;
6. preserve all current regression and DiagnosticTrace invariants.
