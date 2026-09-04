# Matrix Assembly Plan

Status: CANONICAL ASSEMBLING PLAN
Date: 2026-09-04
Global architecture source: `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`

## Goal

Preserve the modules already built while converging on one authoritative runtime direction.

The frame-based path is canonical:

```text
User / World observation
→ NLU / Understanding
→ Working Memory of the current turn
→ context retrieval
→ Coherence / Authority / Belief resolution
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ output validation
→ persistent consolidation
```

The older `contracts/pipeline/prompt` path remains a compatibility facade only. It must not evolve into a competing architecture.

## Fundamental separation

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

- Understanding extracts semantic evidence.
- Belief/Authority decide how that evidence is interpreted.
- Memory Admission decides durable persistence.
- Affective evaluates emotional consequences but does not own RelationshipState.
- Matrix decision logic owns behavioral choice.
- GGUF realizes language; it does not own truth, memory or decisions.

## Phase 1 — Ingress / Understanding

Input:
- raw user/world observation;
- language/session/speaker metadata.

Output:
- NLU predictions;
- `SemanticFrame`;
- `TypedClaim` candidates;
- provenance/confidence/source spans.

Hard rule:
Understanding must not authorize stable memory or create World Truth.

## Phase 2 — Working Memory and context read

Working Memory is temporary operational state for the current turn. It is not durable storage.

Read in parallel where possible:
- relevant long-term memories;
- RelationshipState snapshot;
- AffectiveState snapshot;
- World/perceived-state snapshot;
- recent active referents/context.

## Phase 3 — Contextual resolution

Run bounded resolution using semantic evidence plus context:
- referents/ownership/perspective;
- temporal interpretation;
- report/source handling;
- contradiction/conflict checks;
- consent/intimacy context when relevant;
- Coherence and Authority decisions.

Adult/intimacy is normal semantic content, not an automatic block and not a reason to forbid affective persistence by itself.

## Phase 4 — Internal appraisal and decision

Affective Engine:
- transient emotion/appraisal;
- mood/persistent affect proposals;
- no direct RelationshipState ownership.

Matrix decision layer:
- integrates beliefs, relationship, affect, goals/intentions and context;
- owns behavioral choice;
- eventually exposes an immutable `DecisionSnapshot`.

The current Assembling prototype does not yet implement the complete BDI-lite + Utility layer; missing pieces must be marked non-wired rather than simulated.

## Phase 5 — Response realization

```text
Decision/context package
→ SemanticFrameToPrompt
→ GGUF
→ draft reply
```

GGUF receives natural-language summaries, not unrestricted internal state.

## Phase 6 — Output validation

Target validator checks at least:
- negation;
- referents/target;
- temporal meaning;
- unsupported facts;
- contradiction with resolved decision/context;
- consent/intimacy decision when relevant.

Until implemented, this phase is `NON_CABLATO`; prompt instructions are not considered equivalent to semantic validation.

## Phase 7 — Persistent consolidation

Only after the turn has been understood and the output accepted:
- Memory Admission / Long-Term write;
- persistent affect update;
- RelationshipState update through its own owner/controller;
- causal trace / lifecycle records.

Current Assembling memory adapters remain non-persistent placeholders. A future real memory adapter must not turn the existing pre-response placeholder call into an uncontrolled durable write.

## Memory model

```text
WORKING MEMORY
- temporary current-turn/context state
- bounded / evictable
- no durable truth authority

LONG-TERM MEMORY
- EPISODIC
- SEMANTIC
- REFLECTION
- optional CORE priority subset
```

## Compatibility preservation

Do not delete healthy work solely to clean architecture.

- `MatrixTurnFrame` path = authoritative integration path.
- `contracts/pipeline/prompt` path = compatibility/testing facade.
- migrate callers progressively;
- remove compatibility path only when its tests/callers have been migrated.

## Immediate alignment tasks

1. remove stable-memory authority from Understanding;
2. use canonical NLU confidence-head names in gates;
3. make third-party/report handling reach Authority correctly;
4. keep Affective separate from Relationship ownership;
5. remove adult/intimacy-specific persistence penalty;
6. keep memory backend disabled until the real foundation is connected;
7. add end-to-end smoke tests around the canonical orchestrator;
8. later add explicit output validation and commit-phase ports.

## Change-control rule

Any approved component change must update, in the same workstream:
- canonical architecture/spec;
- module wiring;
- tests;
- continuity;
- conflicting documentation.
