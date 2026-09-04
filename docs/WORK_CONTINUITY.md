# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T21:10+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v8`
Current integrated HEAD before this continuity commit: `b899740a0f9519f2e2b00ac9de2195d104dc8a26`
PR `#3`: MERGED — P1 hardening + DiagnosticTrace + canonical smoke tests
Final PR CI: `33908146792` — `Matrix Assembling CI` — SUCCESS

## Canonical work rules

- one repository at a time unless the owner explicitly says otherwise;
- active repository: `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- component changes must keep code, tests, active docs and continuity coherent in the same workstream.

## Canonical direction

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

```text
Input
→ NLU / Understanding
→ Working Context / context read
→ Coherence / Authority
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF
→ Output Validation
→ Persistent Consolidation
```

Missing target phases remain `NON_CABLATO`; they must not be simulated as real authority.

## P0 hardening — merged

PR `#2` merged to main as:
`0a2f9191428e7cf8246eacb06ebd5256bf58fd53`

Resolved:
1. all NLU claims preserved; no silent multi-claim loss;
2. critical confidence fail-closed;
3. pre-response durable memory results rejected.

Evidence:
- pre-fix CI `33906637932`: 12 tests, exactly 3 expected failures;
- post-fix CI `33906844505`: SUCCESS;
- final P0 CI `33907038217`: SUCCESS.

## P1 hardening — merged

PR `#3` merged to main as:
`b899740a0f9519f2e2b00ac9de2195d104dc8a26`

### P1-01 — Affective cannot own RelationshipState

Before:
Affective runtime could provide `relationshipSummary`, which the adapter passed downstream as relationship state.

Fix:
`AffectiveLabAdapter` ignores runtime relationship authority and always exposes the explicit external-owner compatibility summary.

Regression:
`P1BoundaryTest.affectiveRuntimeCannotProvideRelationshipAuthority`

### P1-02 — Persistent affect is fail-closed

Before:
Affective runtime could report `persistentDeltaApplied=true` even if upstream persistence was not authorized.

Fix:

```text
persistentApplied = output.persistentDeltaApplied && persistentAllowed
```

Unauthorized attempts are blocked and tagged:
`affective_lab.persistence_violation=BLOCKED`.

Structured divergence:
`AFFECTIVE.PERSISTENCE_WITHOUT_ADMISSION`.

Regression:
`P1BoundaryTest.affectivePersistenceIsClampedWhenAdmissionDidNotAuthorizeIt`

### P1-03 — unresolved subject must remain unresolved

Before:
unknown subject could silently fall back to speaker.

Fix:
- unresolved subject maps to literal `UNKNOWN`;
- Coherence returns `LOW_CONFIDENCE_HOLD`;
- diagnostic tag: `coherence.subject=UNRESOLVED`.

Regression:
`P1BoundaryTest.unresolvedSubjectDoesNotSilentlyBecomeSpeaker`

P1 pre-fix evidence:
- CI `33907204697`;
- 15 tests;
- exactly 3 expected failures matching P1-01/02/03.

P1 final evidence:
- code + DiagnosticTrace + smoke CI `33907887621` — SUCCESS;
- final documented PR head CI `33908146792` — SUCCESS.

No gate/threshold was lowered and no regression test was weakened to obtain PASS.

## DiagnosticTrace — integrated

The existing `MatrixTurnFrame.diagnostics` was extended; no parallel diagnostic system was created.

Structured fields:
- `inputOriginale`;
- `observation`;
- `understandingResult`;
- `authorityResolution`;
- `admissionDecision`;
- `memoryResult`;
- `memoryId`;
- `affectiveStimulus`;
- `firstDivergence`;
- `reasoningChain`;
- existing `events` and `tags`.

`DiagnosticSnapshot` records observable boundary data such as module, input/output summary, decision/status, reason codes, confidence and metadata.

`reasoningChain` contains deterministic diagnostic reason codes only, never private chain-of-thought.

`firstDivergence` is write-once:

```text
first divergence wins;
later violations remain visible but do not overwrite it.
```

Boundary snapshots are wired through:

```text
INPUT
→ NLU
→ Understanding
→ Coherence reason codes
→ Authority
→ Memory Admission placeholder
→ Memory result
→ Affective
```

Pre-response memory boundary failures carry the trace through `MatrixBoundaryViolationException`.

Diagnostic tests:
`src/test/kotlin/matrix/assembling/DiagnosticTraceTest.kt`

Coverage:
- first divergence is immutable;
- unauthorized persistent affect is blocked/traced;
- illegal pre-response stable memory result carries exact divergence.

## Canonical end-to-end integration smoke test

File:
`src/test/kotlin/matrix/assembling/MatrixAssemblingOrchestratorIntegrationTest.kt`

Actual tested path:

```text
UnderstandingLabAdapter(fake runtime)
→ BasicCoherenceGuard
→ BasicAuthorityResolver
→ NoPersistentMemoryAdmission
→ BasicAffectiveAdapter
→ SemanticFrameToPrompt
→ EchoGgufAdapter
→ MatrixAssemblingOrchestrator
```

Cases:
1. negation/refusal;
2. third-party report;
3. request;
4. direct assertion with persistence disabled;
5. adult/intimacy semantic signal without automatic block.

Each smoke case verifies the structured trace through Affective and confirms no durable memory write.

## Memory integration documents aligned

`docs/MEMORY_INTEGRATION_POLICY.md` and `docs/MEMORY_INTEGRATION_STATUS.md` now separate:

```text
READ / RETRIEVAL before decision
EVALUATE / PROPOSE during contextual processing
FINAL DURABLE COMMIT only after output validation
```

A real future persistent adapter must not replace the current pre-response placeholder in place.

`docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md` was refreshed in place and no competing audit/spec document was created.

## Current hard boundaries

- NLU/Understanding do not write memory;
- Understanding does not own durable admission;
- all NLU claims are preserved;
- missing critical confidence fails closed;
- unresolved subject remains unresolved;
- Authority does not write persistence;
- current memory adapters cannot produce durable writes;
- orchestrator rejects pre-response stable write/memory IDs;
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed upstream authorization;
- GGUF remains language realization only;
- adult/intimacy is semantic context, not automatic block/persistence penalty.

## Current model state

Student-4-v2.2A remains controlled runtime candidate / not production approved.
Mixed-head-protected artifact remains available through Git LFS.
Full artifact SHA-256:
`4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5`

No Frozen access or gate reduction is authorized.

## Still NON_CABLATO

- explicit Working Context / read port;
- real Long-Term retrieval;
- real Memory Foundation adapter and final atomic commit;
- canonical RelationshipState controller/port;
- BDI-lite + Utility Decision layer;
- Output Semantic Validator;
- Persistent Consolidation port;
- real llama.cpp/MLC GGUF bridge;
- Android application integration.

## Next exact work target

Continue only in `MATRIXNEO23/assembling`.

Next architectural checkpoint:
1. introduce explicit Working Context / context-read boundaries without persistence;
2. add Long-Term retrieval port only as a read contract when the dependency is ready;
3. keep real durable Memory Admission/Repository writes blocked until a post-validation `PersistentConsolidation` boundary exists;
4. add `OutputValidatorPort` and `PersistentConsolidationPort` as separate controlled workstreams when their contracts are ready;
5. preserve current P0/P1 regression suite and DiagnosticTrace invariants.
