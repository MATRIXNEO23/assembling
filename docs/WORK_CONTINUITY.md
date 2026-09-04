# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T21:00+02:00
Repository: `MATRIXNEO23/assembling`
Working branch: `p1-diagnostics-hardening-20260904`
Continuity schema: `matrix.assembling.continuity.v7`
Current integrated main baseline: `0a2f9191428e7cf8246eacb06ebd5256bf58fd53`
Open PR: `#3` — P1 hardening + DiagnosticTrace + canonical smoke tests

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

## P1 hardening — implemented on PR #3

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

## DiagnosticTrace — implemented

The existing `MatrixTurnFrame.diagnostics` was extended; no parallel diagnostic system was created.

Structured fields now include:
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

`reasoningChain` contains observable deterministic reason codes only, never private chain-of-thought.

`firstDivergence` is write-once:

```text
first divergence wins;
later errors remain visible but do not overwrite it.
```

Boundary snapshots are currently wired through:

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
- illegal pre-response stable memory result carries the exact divergence.

## Canonical end-to-end integration smoke test

File:
`src/test/kotlin/matrix/assembling/MatrixAssemblingOrchestratorIntegrationTest.kt`

Actual path tested:

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

For each smoke case the trace is checked through Affective and memory remains non-persistent.

Code-level P1 + DiagnosticTrace + smoke CI:
- run `33907887621` — SUCCESS.

## Memory documents aligned

`docs/MEMORY_INTEGRATION_POLICY.md` and `docs/MEMORY_INTEGRATION_STATUS.md` now explicitly separate:

```text
READ / RETRIEVAL before decision
EVALUATE / PROPOSE during contextual processing
FINAL DURABLE COMMIT only after output validation
```

A real future persistent adapter must not replace the current pre-response placeholder in place.

## Current hard boundaries

- NLU/Understanding do not write memory;
- Understanding does not own durable admission;
- missing critical confidence fails closed;
- unresolved subject remains unresolved;
- Authority does not write persistence;
- current memory adapters cannot produce durable writes;
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

## Next exact step after PR #3

1. run final CI on the documented PR head;
2. merge PR #3 only if green;
3. update `main` continuity with final merge HEAD if needed;
4. next architecture work should introduce explicit Working Context/read boundaries before any real memory backend is connected;
5. OutputValidator/PersistentConsolidation interfaces can follow as separate controlled workstreams.
