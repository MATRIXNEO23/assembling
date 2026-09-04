# Memory integration status

Status: `NOT_IMPLEMENTED_YET / PORT_ONLY`
Date: 2026-09-04

The assembling repository does not currently contain a real persistent Matrix memory backend.
Memory is represented only by non-persistent integration adapters so the rest of the canonical pipeline can be connected and tested without pretending that durable storage exists.

## Current truth

- real Long-Term persistence: `NON_CABLATO`;
- real recall/retrieval: `NON_CABLATO`;
- supersede lineage persistence: `NON_CABLATO`;
- atomic repository transaction: `NON_CABLATO`;
- current adapters never produce durable writes or real memory IDs;
- `MatrixAssemblingOrchestrator` enforces that no durable memory result may exist before response/output validation.

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

For all current adapters:

```text
stableWrite = false
memoryIds = []
```

The provisional claim exists only in the current turn/frame and is not Long-Term persistence.

## Diagnostic coverage

Current `DiagnosticTrace` records:
- `admissionDecision`;
- `memoryResult`;
- memory backend status;
- reason codes;
- eventual `firstDivergence`;
- `memoryId` remains null while no real commit exists.

## Required future architecture

Do not replace the current pre-response placeholder with a durable writer.

Future memory integration must separate:

```text
READ / RETRIEVAL
→ relevant Long-Term context before contextual resolution/decision

EVALUATE / PROPOSE
→ MemoryCandidate / Authority / admission evaluation

FINAL COMMIT
→ only after accepted output/action result
→ Memory Admission durable decision
→ public MemoryRepository API
→ atomic persistence / supersede lineage
```

Target write path:

```text
Output Validation
→ Persistent Consolidation
→ Memory Admission
→ MemoryRepository
```

GGUF, NLU, Understanding, Authority and Affective must never write directly to the repository.

## Integration status

Builds using the current adapter must remain marked:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
