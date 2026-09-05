# Memory Integration Policy

Status: MEMORY FOUNDATION NOT YET CONNECTED  
Date: 2026-09-05

## Decision

The assembling repository must not pretend that production memory persistence exists.
Until a real Memory Foundation is connected, assembling uses only non-persistent preflight adapters and rejects any durable result on the pre-response path.

## Canonical separation

Memory has two different integration roles:

```text
PRE-RESPONSE READ / EVALUATION
- MemoryPreflightPort.evaluate
- retrieve relevant Long-Term context when available
- create/evaluate memory candidates
- never perform durable write

POST-VALIDATION CONSOLIDATION
- PersistentConsolidationPort
- Memory Admission durable decision
- MemoryRepository write/supersede
- persistent state commit
- atomic transaction / lifecycle trace
```

`MemoryAdmissionPort` remains only as a deprecated compatibility facade for adapters created before this separation became explicit. It must never be implemented by a durable writer.

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
NoPersistentMemoryAdmission : MemoryPreflightPort
BasicMemoryAdmission : MemoryPreflightPort
```

Required behavior:

```text
stableWrite = false
memoryIds = []
```

The provisional claim may exist only in the current `MatrixTurnFrame`; this is Working/turn state, not Long-Term storage.

## Future real integration

The additive contract `PersistentConsolidationPort` now names the only future durable-write boundary, but no implementation is connected.

Logical flow:

```text
INPUT
→ Understanding
→ context/read retrieval
→ Coherence / Authority
→ MemoryPreflightPort
→ appraisal / decision
→ response generation
→ OutputValidatorPort
→ PersistentConsolidationPort
     └→ Memory Admission
          └→ MemoryRepository
```

The GGUF must never write memory directly.
Understanding, Coherence, Authority and Affective must never bypass public memory contracts.

## Diagnostics

The canonical `DiagnosticTrace` records separately:
- `admissionDecision` for the current preflight result;
- `memoryResult`;
- `memoryId` when a real durable commit eventually exists;
- deterministic reason codes;
- first boundary divergence.

A placeholder result must explicitly expose that persistence is disabled.

## Production status

Current Assembling memory is an integration placeholder only:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
