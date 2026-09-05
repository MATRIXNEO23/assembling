# Memory Integration Policy

Status: CANONICAL — MEMORY FOUNDATION NOT YET CONNECTED  
Date: 2026-09-04

## Decision

`assembling` must not pretend that durable memory exists. The current memory step is a **pre-response preflight**, not Memory Admission and not a repository write.

## Canonical split

```text
PRE-RESPONSE
Coherence + Authority
→ MemoryPreflightPort.evaluate
→ PROVISIONAL_CLAIM / NO_MEMORY_BACKEND / REJECTED
→ stableWrite=false
→ memoryIds=[]

POST-OUTPUT-VALIDATION
PersistentConsolidationPort
→ real Memory Admission
→ MemoryRepository
→ optional durable write
```

The second path is not yet connected.

## Current hard rules

- No stable memory writes before output validation.
- `MemoryPreflightPort` must return `stableWrite=false` and no memory IDs.
- The orchestrator fails closed on any attempted pre-response durable write.
- No fake persistence or fake memory IDs.
- No direct NLU, Understanding, Affective or GGUF access to MemoryRepository.
- Affective persistent deltas cannot be accepted without an actually admitted event.
- Working Memory/current `MatrixTurnFrame` is temporary and is not Long-Term Memory.

## Current implementations

- `NoPersistentMemoryAdmission`: compatibility name, implements non-persistent preflight.
- `BasicMemoryAdmission`: compatibility name, implements non-persistent preflight.
- `MemoryAdmissionPort`: deprecated compatibility facade.
- `MemoryPreflightPort`: authoritative pre-response contract.
- `PersistentConsolidationPort`: reserved durable-write boundary; not wired.

Allowed preflight statuses:

```text
PROVISIONAL_CLAIM
NO_MEMORY_BACKEND
REJECTED
```

Required invariants:

```text
stableWrite = false
memoryIds = []
```

## Future real integration

After semantic output validation, a real consolidation implementation may execute:

```text
TypedClaim / resolved context
→ AuthorityResolution
→ Memory Admission decision
→ MemoryRepository public API
→ atomic commit / rollback
```

The durable implementation must preserve provenance, owner, perspective, validity, conflicts and supersession lineage. It must not reuse the current pre-response preflight as a write path.

## Diagnostic requirements

`DiagnosticTrace` must expose:

```text
MEMORY_ADMISSION = preflight decision
MEMORY = NOT_EXECUTED / NO_DURABLE_WRITE
firstDivergence = MEMORY_PREFLIGHT.UNAUTHORIZED_STABLE_WRITE
```

when a pre-response component attempts persistence.

## Production status

Any build using the current adapter remains:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
