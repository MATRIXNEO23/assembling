# Memory integration status

Status: `NOT_IMPLEMENTED_YET / PREFLIGHT_ONLY`  
Date: 2026-09-05  
Cross-module semantic authority: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

The assembling repository does not currently contain a real persistent Matrix Memory backend.
Memory is represented only by non-persistent preflight adapters so the rest of the canonical pipeline can be connected and tested without pretending that durable storage exists.

MIP-1.0 now defines the future universal Memory read/retrieval boundary and the distinction between ephemeral MemoryCandidate and durable MemoryRecord.

## Current truth

- real Long-Term persistence: `NON_CABLATO`;
- real Memory index probe: `NON_CABLATO`;
- real hydrate/rerank retrieval: `NON_CABLATO`;
- deep/multi-hop retrieval: `NON_CABLATO`;
- supersede lineage persistence: `NON_CABLATO`;
- atomic repository transaction: `NON_CABLATO`;
- authoritative pre-response API: `MemoryPreflightPort.evaluate`;
- future durable API: `PersistentConsolidationPort.consolidate`;
- current adapters never produce durable writes or real Memory IDs;
- `MatrixAssemblingOrchestrator` enforces that no durable Memory result may exist before response/output validation.

`MemoryAdmissionPort` remains deprecated compatibility only. It is not the contract for future persistent integration.

## Target read behavior defined, not implemented

MIP target:

```text
EVERY NORMAL TURN
→ lightweight Memory INDEX_PROBE
```

The probe determines whether relevant Memory exists even without an explicit recall phrase.

Only relevant hits advance to:

```text
HYDRATE_AND_RERANK
```

and only explicit complex purposes advance to:

```text
DEEP_OR_MULTI_HOP
```

This target is currently architecture-only. No fake index/retrieval result may be produced until a real Memory read backend exists.

## Current temporary behavior

`NoPersistentMemoryAdmission` may return:

```text
PROVISIONAL_CLAIM
REJECTED
```

`BasicMemoryAdmission` may return:

```text
NO_MEMORY_BACKEND
REJECTED
```

For all current preflight adapters:

```text
stableWrite = false
memoryIds = []
```

The provisional candidate exists only in the current turn/frame and is not Long-Term persistence.

## Semantic status distinctions

When retrieval is implemented, the boundary must distinguish at least:

```text
MATCHED
NO_MATCH
AMBIGUOUS
INDEX_UNAVAILABLE
ERROR
```

`NO_MATCH` is not `INDEX_UNAVAILABLE`.

Likewise, missing domain state is never represented by fabricated zero/default values.

## Memory semantic kinds

MIP reserves:

```text
EPISODIC
SEMANTIC
REFLECTION
```

Relationship, Affective, Goal and Intimacy/Consent remain separate canonical state domains and are not Memory kinds.

## Diagnostic coverage

Current `DiagnosticTrace` records:
- preflight/admission decision;
- Memory result;
- Memory backend status;
- reason codes;
- eventual `firstDivergence`;
- `memoryId` remains null while no real commit exists.

Future retrieval diagnostics should additionally expose query/result status, selected Memory refs, snapshot IDs and deterministic reason codes without private chain-of-thought.

The orchestrator records the output-validation boundary as:

```text
output.validation = NON_CABLATO
```

unless an `OutputValidatorPort` implementation is explicitly supplied.

## Required future architecture

Do not replace the current pre-response adapter with a durable writer.

Future Memory integration remains separated as:

```text
READ / ENRICH
→ always-on lightweight index probe
→ optional hydrate/rerank of relevant Long-Term context

EVALUATE / PROPOSE
→ MemoryPreflightPort / ephemeral MemoryCandidate evaluation

FINAL COMMIT
→ only after accepted output/action result
→ PersistentConsolidationPort
→ Memory Admission durable decision
→ public MemoryRepository API
→ atomic persistence / supersede lineage
```

GGUF, NLU, Understanding, Authority and Affective must never write directly to the repository.

Authority/Belief resolution identifies semantic contradiction; Memory Admission must not invent conflict from text difference.

## Integration status

Builds using the current adapter must remain marked:

```text
MEMORY_PERSISTENCE_DISABLED
MEMORY_RETRIEVAL_NON_CABLATO
NOT_PRODUCTION_APPROVED
```
