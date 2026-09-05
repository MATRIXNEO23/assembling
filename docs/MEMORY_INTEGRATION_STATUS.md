# Memory integration status

Status: `NOT_IMPLEMENTED_YET / PREFLIGHT_ONLY`  
Date: 2026-09-05

The assembling repository does not currently contain a real persistent Matrix memory backend.
Memory is represented only by non-persistent preflight adapters so the rest of the canonical pipeline can be connected and tested without pretending that durable storage exists.

## Current truth

- real Long-Term persistence: `NON_CABLATO`;
- real recall/retrieval: `NON_CABLATO`;
- supersede lineage persistence: `NON_CABLATO`;
- atomic repository transaction: `NON_CABLATO`;
- authoritative pre-response API: `MemoryPreflightPort.evaluate`;
- future durable API: `PersistentConsolidationPort.consolidate`;
- current adapters never produce durable writes or real memory IDs;
- `MatrixAssemblingOrchestrator` enforces that no durable memory result may exist before response/output validation.

`MemoryAdmissionPort` remains deprecated compatibility only. It is not the contract for future persistent integration.

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

The provisional claim exists only in the current turn/frame and is not Long-Term persistence.

## Diagnostic coverage

Current `DiagnosticTrace` records:
- preflight/admission decision;
- memory result;
- memory backend status;
- reason codes;
- eventual `firstDivergence`;
- `memoryId` remains null while no real commit exists.

The orchestrator records the output-validation boundary as:

```text
output.validation = NON_CABLATO
```

unless an `OutputValidatorPort` implementation is explicitly supplied.

## Required future architecture

Do not replace the current pre-response adapter with a durable writer.

Future memory integration remains separated as:

```text
READ / RETRIEVAL
→ relevant Long-Term context before contextual resolution/decision

EVALUATE / PROPOSE
→ MemoryPreflightPort / candidate evaluation

FINAL COMMIT
→ only after accepted output/action result
→ PersistentConsolidationPort
→ Memory Admission durable decision
→ public MemoryRepository API
→ atomic persistence / supersede lineage
```

GGUF, NLU, Understanding, Authority and Affective must never write directly to the repository.

## Integration status

Builds using the current adapter must remain marked:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
