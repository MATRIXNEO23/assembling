# Memory Integration Policy

Status: MEMORY FOUNDATION NOT YET CONNECTED
Date: 2026-09-04

## Decision

The assembling repository must not pretend that production memory persistence exists.
Until a real Memory Foundation is connected, assembling uses only non-persistent adapters and must reject any durable result on the pre-response path.

## Canonical separation

Memory has two different integration roles:

```text
PRE-RESPONSE READ / EVALUATION
- retrieve relevant Long-Term context when available
- create/evaluate memory candidates
- never perform durable write

POST-VALIDATION CONSOLIDATION
- Memory Admission durable decision
- MemoryRepository write/supersede
- persistent state commit
- atomic transaction / lifecycle trace
```

The current `MemoryAdmissionPort` call in the prototype is compatibility/preflight only. It must not be replaced in place by a real persistent writer.

## Current hard rules

- no stable memory writes;
- no fake persistence;
- no fake memory IDs;
- no direct GGUF-to-memory access;
- no Affective persistent delta from an unadmitted event;
- before response/output validation: `stableWrite == false` and `memoryIds == []`;
- `MatrixAssemblingOrchestrator` rejects violations of this pre-response boundary.

Current placeholder statuses may include:

```text
NO_MEMORY_BACKEND
PROVISIONAL_CLAIM
REJECTED
```

They describe integration state only and are not durable persistence results.

## Temporary components

Allowed adapters:

```text
NoPersistentMemoryAdmission
BasicMemoryAdmission
```

Required behavior:

```text
stableWrite = false
memoryIds = []
```

`NoPersistentMemoryAdmission` may preserve a claim provisionally inside the current `MatrixTurnFrame`; this is Working/turn state, not Long-Term storage.

## Future real integration

A future Memory Foundation integration must expose separate boundaries for:

```text
Long-Term retrieval/read
MemoryCandidate / admission evaluation
final durable admission/commit
MemoryRepository public API
```

Logical flow:

```text
INPUT
→ Understanding
→ context/read retrieval
→ Coherence / Authority
→ appraisal / decision
→ response generation
→ output validation
→ Persistent Consolidation
     └→ Memory Admission
          └→ MemoryRepository
```

The GGUF must never write memory directly.
Understanding, Coherence, Authority and Affective must never bypass the public memory contracts.

## Diagnostics

The canonical `DiagnosticTrace` records separately:
- `admissionDecision`;
- `memoryResult`;
- `memoryId` when a real durable commit eventually exists;
- reason codes;
- first boundary divergence.

A placeholder memory result must explicitly expose that persistence is disabled.

## Production status

Current Assembling memory is an integration placeholder only:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
