# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-04T20:00+02:00
Repository: `MATRIXNEO23/assembling`
Working branch: `architecture-alignment-20260904`
Continuity schema: `matrix.assembling.continuity.v4`
Branch tip before this continuity update: `559e21d1a56fb1a41034f9f7ad2dc729414f4bb6`
Baseline main before alignment: `4399a47258d4e44a7be3a216e2fdede01b8c407e`
Latest previously verified main CI: `33877882671` — success

## Canonical sources

Global Matrix architecture:
`MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`

Assembling document order:
1. `docs/README.md` — document index/status;
2. `docs/MODULE_CONNECTIONS.md` — canonical module wiring;
3. `docs/ASSEMBLY_PLAN.md` — operational plan;
4. `docs/WORK_CONTINUITY.md` — restart/current state.

## Current objective

Preserve all healthy modules already built while eliminating architecture drift and competing authorities.

Canonical principle:

```text
UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ DECIDE ≠ RESPOND
```

Canonical runtime direction:

```text
User/World observation
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

Not all future stages are wired yet. Missing stages must be marked `NON_CABLATO` rather than simulated.

## Preserved modules

No module was discarded by this alignment.

Preserved authoritative frame path:
- `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt`
- `src/main/kotlin/matrix/assembling/IntegrationPorts.kt`
- `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt`
- `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt`
- adapters under `src/main/kotlin/matrix/assembling/adapters/`

Preserved compatibility/testing path:
- `src/main/kotlin/matrix/assembling/contracts/*`
- `src/main/kotlin/matrix/assembling/pipeline/*`
- `src/main/kotlin/matrix/assembling/prompt/*`

The compatibility path remains build/test material only and must not become a second independent architecture.

## Contradictions resolved in this workstream

### 1. Documentation drift / two architectural paths

Resolved by:
- adding `docs/README.md` document index;
- declaring the frame path authoritative;
- declaring `contracts/pipeline/prompt` compatibility-only;
- updating root `README.md`, `MODULE_CONNECTIONS.md`, `ASSEMBLY_PLAN.md`.

### 2. Understanding owned durable-memory admission

Old behavior:
`UnderstandingLabAdapter` set `SemanticFrame.stableMemoryAllowed` from dialogue act, source type, worldTruth and confidence.

New behavior:
- Understanding preserves semantic evidence and provenance;
- `stableMemoryAllowed` remains only as legacy compatibility field and is set `false` by the real Understanding adapter;
- durable memory authority is deferred to Coherence + Authority + Memory Admission;
- diagnostic tag: `understanding_lab.memory_authority=DEFERRED`.

Relevant branch commit:
`777a59f6b110eab0cbba0aefba1db0612b4d6e58`

### 3. Negation confidence key mismatch

Old BasicCoherenceGuard key:
`tokens.negation`

Canonical Matrix-NLU head:
`token.negation`

Fixed in BasicCoherenceGuard.

### 4. Third-party report path could never reach Authority correctly

Old BasicAuthorityResolver derived source mostly from Coherence state; BasicCoherenceGuard did not reliably produce report state.

New behavior:
- Coherence reads `TypedClaim.sourceType`;
- `THIRD_PARTY_REPORT` becomes `REPORT_ONLY`;
- Authority reads the actual claim `sourceType` and rejects direct authority while preserving the indirect source.

Relevant branch commit:
`1c150a2ee080391073492e8ddd40cc7653005f04`

### 5. Affective vs Relationship authority conflation

Global Affective contract already says RelationshipState is external.

Old assembly adapter could derive `relationshipSummary` from persistent affect fields.

New behavior:
- Affective no longer derives canonical RelationshipState;
- prompt compatibility field explicitly says RelationshipState is externally owned;
- diagnostic tag: `affective_lab.relationship_owner=EXTERNAL`;
- persistent affect is allowed only from an actually admitted event (`memory.stableWrite=true`), not from Understanding's legacy flag.

Relevant branch commit:
`5ebc7afa0ea691174ecf8f03893d67e94422d68d`

### 6. Adult/intimacy blanket persistence penalty

Old compatibility CoherenceGuard required:
`persistentAffectAllowed = stableMemoryAllowed && adultIntimacy == None`

New behavior:
`persistentAffectAllowed = stableMemoryAllowed`

Adult/intimacy itself is never a reason to suppress affect; unresolved semantics may still remain transient like any other domain.

Relevant branch commit:
`627fcbbf9dbac2fcda9ee31d3099e047ea769711`

### 7. Contract comments were misleading

`MatrixTurnFrame.kt` now explicitly states:
- `worldTruth` is observation/provenance metadata, not a persistence shortcut;
- `stableMemoryAllowed` is legacy compatibility only;
- `AffectiveState.relationshipSummary` is a prompt projection, not Relationship ownership.

Relevant branch commit:
`559e21d1a56fb1a41034f9f7ad2dc729414f4bb6`

## Tests updated / added

`src/test/kotlin/matrix/assembling/adapters/ComponentMappingCompatibilityTest.kt`

Current intended coverage:
1. resolved semantic fields are preserved while Understanding does not own Memory Admission;
2. third-party reports become `REPORT_ONLY` and cannot become direct authority;
3. `token.negation` canonical key is actually enforced;
4. no persistent affect without stable admitted memory;
5. persistent affect depends on admitted event, not `SemanticFrame.stableMemoryAllowed`.

Branch commit:
`9cac02ad7f2dbf14f978d5bb02575706e010b796`

## Global architecture alignment

A parallel alignment branch exists in:
`MATRIXNEO23/8.10.9evo3-solo-gpt`

Branch:
`architecture-alignment-20260904`

Canonical architecture update commit:
`3816df2f3b01f1fc0932729e34956c8065e12b64`

Global changes include:
- Working Memory/Working Context explicitly temporary;
- remove `WORKING` from persistent Long-Term layers;
- Long-Term = `EPISODIC`, `SEMANTIC`, `REFLECTION`, optional `CORE` priority subset;
- Memory Acquisition creates candidates only;
- durable write moved to controlled consolidation;
- Long-Term retrieval/read happens before contextual decision when useful;
- RelationshipState separated from AffectiveState;
- adult/intimacy made explicit first-class semantic domain;
- orchestrator reordered as ingress/read/resolve/appraise/decide/respond/validate/consolidate;
- ADR-013 added for Working-vs-Long-Term and final persistent commit;
- ADR-011 strengthened: component changes require canonical docs + wiring + tests + continuity updates in same workstream.

## Memory state

Real persistent Memory Foundation is still not connected in Assembling.

Hard rule remains:

```text
No real memory writes.
No fake persistence.
No fake memory IDs.
No hidden stable state.
```

Current adapters must keep:

```text
stableWrite = false
memoryIds = []
status = NO_MEMORY_BACKEND / PROVISIONAL_CLAIM / REJECTED
```

Future design distinction:

```text
WORKING MEMORY
= temporary current-turn/context state

LONG-TERM MEMORY
= EPISODIC + SEMANTIC + REFLECTION
  + optional CORE priority subset
```

A future real MemoryAdmission integration must not reuse the current pre-response placeholder call as an uncontrolled durable write. Durable commit belongs to the final consolidation phase.

## NLU model state

Student-4-v2.2A:
- status remains controlled runtime candidate / not production approved;
- mixed-head-protected runtime bundle stored via Git LFS in Assembling;
- full artifact SHA-256: `4998ce2f44dd8553d75f86b8d7975529f6a5f779de9107eef393648022d6ccb5`;
- Student-5 remains separate experimental work and does not block Assembling.

Do not lower NLU gates and do not touch Frozen without explicit authorization.

## Adult/intimacy canonical rule

Adult/intimacy is first-class semantic coverage, not a censorship taxonomy.

It must not be automatically:
- blocked by NLU;
- treated as low-value;
- excluded from affective persistence;
- excluded from memory solely because it is intimate.

Persistence/behavior depend on ordinary Matrix semantics: context, source, confidence, relationship, meaning, relevance and admission rules.

## Still not wired / not production-real

- real ONNX Student-4-v2.2A Android/runtime bridge;
- real Long-Term MemoryRepository adapter;
- explicit Working Memory/context retrieval layer in Assembling;
- canonical RelationshipState controller/port;
- BDI-lite + Utility decision layer;
- Output Semantic Validator;
- explicit Persistent Consolidation/atomic commit port;
- real llama.cpp/MLC GGUF adapter in this repo;
- Android application integration.

These are gaps, not competing authorities. Do not invent placeholders that pretend they are implemented.

## Next exact task after branch verification

1. open/verify CI for the architecture-alignment branch;
2. fix only actual compile/test failures;
3. merge Assembling alignment only when green;
4. merge global canonical architecture alignment;
5. update this continuity file on `main` with final merge HEAD(s);
6. then continue with the end-to-end `MatrixAssemblingOrchestrator` smoke test and explicit Working Context/read boundaries.

## Permanent project-management rule

When a component changes role, order, ownership or contract, the project update is incomplete until the same workstream updates:
- canonical architecture/spec;
- Assembling wiring/adapter;
- affected tests;
- continuity;
- any active document that would otherwise contradict the new decision.

Do not create additional parallel architecture documents when an existing canonical document can be updated.
