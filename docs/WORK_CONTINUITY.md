# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T05:45+02:00  
Repository: `MATRIXNEO23/assembling`  
Branch: `main`  
Continuity schema: `matrix.assembling.continuity.v12`  
Current integrated HEAD before this continuity commit: `b4a93d26062ca9fe5f6f123110c77a84e5ecc97d`  
PR `#6`: MERGED — remaining integration boundary fixes  
Final main CI before documentation-only Memory policy note: `33942262278` — `Matrix Assembling CI` — SUCCESS

## Canonical work rules

- work on one repository at a time unless the owner explicitly says otherwise;
- active repository is `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- when a component changes, keep code, tests, active documents and continuity coherent in the same workstream;
- do not create parallel specifications when an active canonical document can be updated.

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

## Integrated hardening status

Completed and regression-tested:
- all NLU claims preserved;
- critical confidence fail-closed on every claim;
- unresolved subject remains `UNKNOWN`;
- third-party reports do not become direct authority;
- multi-claim turns cannot masquerade as one direct authority;
- pre-response memory is `MemoryPreflightPort`, never durable write;
- pre-response `stableWrite=true` / memory IDs are rejected;
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed upstream authorization;
- `DiagnosticTrace` is typed and keeps write-once `firstDivergence`;
- Prompt Builder is realization-only;
- legacy pipeline is compatibility-only/deprecated;
- optional `OutputValidatorPort` boundary is after GGUF;
- future durable persistence boundary is `PersistentConsolidationPort`;
- explicit adult/intimacy NLU marker supported with compatibility fallback.

Evidence:
- PR #4 merged as `a46919d925824e12d66d074a77c231aa2b4b7a1b`;
- PR #6 merged as `1c603ac94d62d0e79d14fd455481c7a487d89ea4`;
- final main continuity commit before Memory policy note: `0d5e7533f37a654865d1fe55f07b3c334bf1f1a2`;
- final main CI run `33942262278` — SUCCESS.

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

## DiagnosticTrace status

Canonical trace is `MatrixTurnFrame.diagnostics`.

Structured coverage includes:
- original input;
- observation/NLU;
- Understanding;
- Coherence reason codes;
- Authority;
- Memory preflight/admission snapshot;
- Memory result;
- Affective;
- `firstDivergence`;
- deterministic `reasoningChain` reason codes only.

No private chain-of-thought is stored.

## Model/artifact state

Student-4-v2.2A remains a controlled runtime candidate and is not production approved.

Mixed-head-protected artifact remains available through Git LFS.

Full artifact SHA-256:

```text
4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5
```

No Frozen access, retraining, quantization change or gate reduction occurred in the completed hardening workstream.

## Memory Foundation migration decisions — SAVED / IMPLEMENTATION DEFERRED

The following decisions are now part of the active Assembling plan, but implementation must not start until the owner resumes this backlog.

```text
Python Memory Foundation v3
= frozen reference/oracle
        ↓ contract parity
Kotlin / Room Memory Foundation
= future production implementation
        ↓ fault-injection + regression gates
PersistentConsolidationPort
= only allowed durable runtime integration boundary
```

Preserve these invariants:
- `revisionOf` points to the lineage root;
- `supersededBy` is the sequential revision chain;
- semantic changes use `supersede()`;
- contradiction identity is explicit from Authority resolution, not inferred from text difference;
- Authority identifies `contradictsMemoryId`; Memory Admission consumes it and owns SAVE/SUPERSEDE/REJECT/IGNORE;
- atomic rollback and lineage protection require fault-injection tests;
- Kotlin/Room must pass parity tests against the frozen Python oracle before integration;
- maintain a fast core test gate plus the full extended regression suite;
- if there is no real legacy Android database, the future Room implementation may begin directly from the current canonical schema instead of recreating historical migrations;
- destructive migration is not an accepted normal fallback for Luna persistent memory;
- prefer structured Matrix-NLU authority/source evidence over regex parsing when the structured fields exist.

Do NOT treat these implementation details as canonical yet; audit them before coding:
- exact Room entities and DAO signatures;
- exact memory-category enum;
- FTS design;
- JSON versus normalized actors/entities representation;
- transaction-manager/API shape;
- concrete self-FK/delete policy;
- timestamp units.

Source rationale came from external advice reviewed on 2026-09-05; only the validated architectural subset above was accepted into the project.

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
5. The future Kotlin/Room Memory Foundation contract still requires a dedicated implementation audit before coding.

## DEFERRED BACKLOG — DO NOT START YET

The owner explicitly requested that the following work be recorded and deferred because another task must be completed first.

When this architecture work is resumed, continue in this order unless the owner changes priority:

1. **Working Context / Working Memory**
   - introduce an explicit bounded current-turn context object/port;
   - keep it temporary and non-persistent;
   - preserve active referents, semantic results, selected retrieved context and current decision state.

2. **Read-only context boundaries**
   - define read-only ports for contextual snapshots;
   - Long-Term Memory retrieval;
   - RelationshipState snapshot;
   - Affective snapshot;
   - World/perceived-state snapshot;
   - allow parallel reads where dependencies permit.

3. **Bounded Long-Term retrieval contract**
   - connect only a read contract when the real dependency is ready;
   - no durable write through the read path;
   - retrieved memories feed contextual resolution, not direct GGUF authority.

4. **Per-claim contextual resolution**
   - resolve multi-claim turns claim-by-claim against context/memory;
   - preserve source/owner/perspective/temporal/provenance per claim;
   - no multi-claim persistence until this is implemented and tested.

5. **Real semantic Output Validator**
   - implement behind the existing `OutputValidatorPort`;
   - validate at least negation, referents, temporal meaning, unsupported facts and resolved consent/intimacy semantics where relevant;
   - failure must populate `DiagnosticTrace.firstDivergence` without overwriting an earlier divergence.

6. **Persistent Consolidation / Memory Foundation**
   - before coding Kotlin/Room, audit the concrete persistence contract against the frozen Python oracle and the saved migration decisions above;
   - connect real Memory Foundation only through `PersistentConsolidationPort`;
   - durable Memory Admission/Repository operations happen only after accepted output/action result;
   - preserve SAVE/SUPERSEDE semantics, lineage, contract parity and atomic rollback;
   - persistent affect and RelationshipState commits remain separately owned.

7. **Later cognitive/runtime modules**
   - canonical RelationshipState controller;
   - BDI-lite + Utility Decision layer;
   - real GGUF bridge;
   - Android integration.

Hard resume rule:

```text
DO NOT restart the architecture audit.
DO NOT redo completed P0/P1/P2 hardening.
Resume from Working Context / read-only context boundaries only when the owner explicitly returns to this backlog.
```

## Current pause state

Architecture backlog and Memory Foundation production-migration decisions recorded and intentionally paused by owner request. The next user task may be unrelated; do not automatically start any deferred item above.
