# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T05:40+02:00  
Repository: `MATRIXNEO23/assembling`  
Branch: `main`  
Continuity schema: `matrix.assembling.continuity.v10`  
Current integrated HEAD before this continuity commit: `1c603ac94d62d0e79d14fd455481c7a487d89ea4`  
PR `#6`: MERGED — remaining integration boundary fixes  
Final PR CI: `33942176370` — `Matrix Assembling CI` — SUCCESS

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

## Integrated hardening history

### P0

Resolved:
- all NLU claims are preserved in `typedClaims`;
- critical confidence fails closed;
- illegal pre-response stable-memory output is rejected.

Evidence:
- `33906844505` — SUCCESS;
- `33907038217` — SUCCESS.

### P1 + DiagnosticTrace

Resolved:
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed upstream authorization;
- unresolved subject remains `UNKNOWN`;
- typed `DiagnosticTrace` with write-once `firstDivergence`;
- canonical end-to-end smoke tests through Affective.

Evidence:
- `33907887621` — SUCCESS;
- `33908146792` — SUCCESS.

### Prompt and compatibility cleanup

PR `#4` merged as:

```text
a46919d925824e12d66d074a77c231aa2b4b7a1b
```

Resolved:
- `SemanticFrameToPrompt` remains realization-only;
- legacy `MatrixAssemblyPipeline` is deprecated for new callers;
- prompt-boundary regression test added.

Evidence:
- `33908498023` — SUCCESS.

PR `#5` was closed without merge because it was based on stale `main` and overlapped newer hardening. No code from that divergent PR is authoritative.

## PR #6 — remaining fixes integrated

Merged to `main` as:

```text
1c603ac94d62d0e79d14fd455481c7a487d89ea4
```

### 1. Claim-wide confidence validation

`BasicCoherenceGuard` now validates critical confidence on every `TypedClaim`, including secondary claims.

Critical heads:

```text
token.negation
sequence.predicate
sequence.subjectReferent
sequence.targetReferent
```

Missing or low values fail closed. Diagnostics identify the exact claim/key, for example:

```text
claim[1].token.negation
```

No threshold was lowered.

### 2. Explicit multi-claim Authority state

Current bounded behavior:

```text
multiple claims
→ all claims preserved
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

Compatibility-only API:

```text
MemoryAdmissionPort
= deprecated facade
```

Future durable boundary:

```text
PersistentConsolidationPort
```

Existing fail-closed invariant:

```text
before output validation:
stableWrite == false
memoryIds == []
```

`NoPersistentMemoryAdmission` and `BasicMemoryAdmission` now implement `MemoryPreflightPort` directly. Their `admit()` methods remain deprecated helper methods only.

### 4. Output validation boundary

`OutputValidatorPort` is an optional post-GGUF boundary.

- supplied validator executes after GGUF generation;
- absent validator records `output.validation=NON_CABLATO`;
- no fake semantic validator was introduced;
- no persistent consolidation runs automatically.

### 5. Explicit adult/intimacy NLU marker

`MatrixNluClaim` and `NluOutput` now support:

```text
adultOrIntimacy: Boolean?
```

Understanding prefers this explicit semantic marker. The local keyword fallback remains only for older runtimes.

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

No competing architecture document was created.

## Test coverage

Current regression suite includes:
- primary and secondary claim critical-confidence failures;
- exact missing confidence source/key diagnostics;
- multi-claim preservation and explicit Authority hold;
- third-party report handling;
- unresolved-subject preservation;
- pre-response durable-write rejection through `MemoryPreflightPort`;
- Affective relationship and persistence boundaries;
- `DiagnosticTrace.firstDivergence` immutability;
- explicit NLU adult/intimacy marker;
- output validator execution after GGUF;
- absence of validator marked `NON_CABLATO`;
- prompt realization-only boundary;
- legacy compatibility tests.

Final PR head:

```text
6554690ecf11b53f76088b9d8f21e8a22eef9b7e
```

Final PR CI:

```text
run 33942176370
workflow Matrix Assembling CI
job kotlin-tests
conclusion SUCCESS
```

No test was weakened or rewritten to hide a real error.

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
3. Persistence remains disabled; SAVE/SUPERSEDE/rollback are not yet exercised end-to-end in Assembling.
4. The adult/intimacy keyword fallback remains until every runtime emits the explicit marker.

## Next exact work target

Continue only in `MATRIXNEO23/assembling`:

1. introduce explicit Working Context and read-only context ports;
2. add bounded Long-Term retrieval contract only when its dependency is ready;
3. implement per-claim contextual resolution before any multi-claim persistence;
4. implement a real semantic output validator as a separate controlled workstream;
5. connect real Memory Foundation only through `PersistentConsolidationPort`;
6. preserve all current regression and DiagnosticTrace invariants.
