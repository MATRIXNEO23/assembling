# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T20:12+02:00
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Continuity schema: `matrix.assembling.continuity.v5`
Current integrated HEAD before this continuity update: `3e34a111134045a70f49de291ae3ffb313440c4b`
Architecture-alignment PR: `#1` — MERGED
Verified PR CI: `33903986381` — `Matrix Assembling CI` — SUCCESS

## Canonical work rules

Repository-local canonical workflow file:
`PROJECT_WORK_RULES.md`

Hard rules:
- one repository at a time unless the owner explicitly says otherwise;
- current active repository is `MATRIXNEO23/assembling`;
- other repositories may be read for dependency/context checks but must not be written without explicit authorization;
- historical/older repositories are backup/checkpoint sources, not normal development targets;
- do not delete or rewrite backup repositories;
- when a component changes in the active repository, update affected code, tests, local canonical docs and continuity in the same workstream.

## Canonical document order inside Assembling

1. `README.md` — repository purpose and authoritative path;
2. `docs/README.md` — document index and status;
3. `docs/MODULE_CONNECTIONS.md` — canonical module wiring;
4. `docs/ASSEMBLY_PLAN.md` — current implementation plan;
5. `docs/WORK_CONTINUITY.md` — exact operational restart point;
6. audit/evidence documents only for supporting history.

The old `contracts/pipeline/prompt` path remains compatibility/testing code. It is not a second architectural authority.

## Canonical principle

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

## Current canonical direction in Assembling

```text
User / World observation
→ NLU / Understanding
→ Working Memory / Semantic Draft
→ context read
→ Coherence / Authority / Belief resolution
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF realization
→ output validation
→ persistent consolidation
```

Not every target phase is implemented yet. Missing stages must be marked `NON_CABLATO`; do not simulate them as real authority.

## Alignment completed and merged

Merged through PR #1 into `main`.

Resolved contradictions:

1. **Two competing pipeline authorities**
   - frame path is authoritative;
   - `contracts/*`, `pipeline/*`, `prompt/*` retained only as compatibility/testing path.

2. **Understanding owned memory admission**
   - `UnderstandingLabAdapter` now preserves semantic evidence/provenance only;
   - `SemanticFrame.stableMemoryAllowed` remains legacy compatibility only and the real adapter sets it `false`;
   - diagnostic: `understanding_lab.memory_authority=DEFERRED`.

3. **Wrong negation confidence key**
   - fixed `tokens.negation` → canonical `token.negation`.

4. **Third-party/report authority path**
   - Coherence reads actual `TypedClaim.sourceType`;
   - `THIRD_PARTY_REPORT` becomes `REPORT_ONLY`;
   - Authority preserves indirect source and does not treat it as direct authority.

5. **Affective vs Relationship ownership**
   - Affective no longer derives canonical RelationshipState;
   - Relationship ownership is external;
   - diagnostic: `affective_lab.relationship_owner=EXTERNAL`.

6. **Adult/intimacy persistence penalty**
   - removed blanket exclusion from persistent affect;
   - adult/intimacy is semantic context, not a censor/persistence penalty;
   - unresolved meaning may still stay transient under normal semantic rules.

7. **Contract ambiguity**
   - `MatrixTurnFrame` comments now explicitly state authority boundaries for `worldTruth`, `stableMemoryAllowed`, and relationship projection.

## Tests locked by the alignment

`src/test/kotlin/matrix/assembling/adapters/ComponentMappingCompatibilityTest.kt`

Coverage now includes:
- resolved semantic fields preserved without Understanding owning memory admission;
- third-party reports cannot become direct authority;
- canonical `token.negation` confidence key is enforced;
- no persistent affect without admitted memory;
- persistent affect depends on the admitted event, not the legacy Understanding flag.

The first CI attempt exposed one real test fixture compile error (`target` missing); it was fixed without weakening the test. Final PR CI is green.

## Memory model currently used for planning

```text
WORKING MEMORY
= temporary current-turn/context state used to understand/respond

LONG-TERM MEMORY
= persistent catalogued information
```

Current Assembling does **not** have a real persistent Memory Foundation wired.

Hard runtime rule remains:

```text
stableWrite = false
memoryIds = []
status = NO_MEMORY_BACKEND / PROVISIONAL_CLAIM / REJECTED
```

Do not convert the current pre-response memory placeholder call into a durable write. When real persistence is connected, read/retrieval and final durable commit must be separate responsibilities.

## Model state

Student-4-v2.2A:
- controlled runtime candidate;
- not production approved;
- mixed-head-protected artifact is present in Assembling through Git LFS;
- full artifact SHA-256: `4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5`.

Student-5 remains a separate experiment and does not block Assembling.

No Frozen access or gate reduction is authorized here.

## Adult/intimacy rule

Adult/intimacy is first-class semantic coverage.

It must not be automatically:
- blocked by NLU;
- treated as low-value;
- excluded from affective persistence;
- excluded from memory solely because it is intimate.

Meaning, context, source, confidence, relationship and normal admission rules govern downstream handling.

## Still NON_CABLATO / not production-real

- real ONNX Student-4-v2.2A runtime bridge;
- explicit Working Memory/context read layer;
- real Long-Term MemoryRepository adapter;
- canonical RelationshipState controller/port;
- BDI-lite + Utility decision layer;
- Output Semantic Validator;
- explicit Persistent Consolidation / atomic commit port;
- real llama.cpp/MLC GGUF adapter in this repo;
- Android app integration.

These are implementation gaps, not permission to create competing authorities.

## Next exact work target

Continue **only in `MATRIXNEO23/assembling`**.

Next task:

```text
Implement the canonical end-to-end MatrixAssemblingOrchestrator smoke test
and introduce explicit Working Context / read boundaries without creating
fake persistence or a second pipeline.
```

Required smoke cases:
1. negation/refusal;
2. third-party report;
3. request;
4. direct assertion with `NO_MEMORY_BACKEND`;
5. adult/intimacy semantic case proving no automatic block/persistence penalty.

After changes:
- run CI;
- fix only actual failures;
- update this continuity file before ending the workstream.
