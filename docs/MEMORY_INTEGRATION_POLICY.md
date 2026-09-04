# Memory Integration Policy

Status: MEMORY FOUNDATION NOT YET IMPLEMENTED
Date: 2026-09-04

## Decision

The assembling repository must not pretend that a production memory module exists.
Until `MATRIXNEO23/memoria` contains a real Memory Foundation, assembling may only use a safe placeholder memory adapter.

## Current rule

- No stable memory writes.
- No fake persistence.
- No direct GGUF-to-memory access.
- No Affective persistent deltas based on unadmitted memory.
- Memory output must explicitly say `NO_MEMORY_BACKEND` or `TRANSIENT_ONLY`.

## Temporary component

Use a `NoMemoryAdapter` during integration tests.

Expected behavior:

```text
input: MatrixTurnFrame
output:
  status = NO_MEMORY_BACKEND
  stableWrite = false
  memoryIds = []
  reason = Memory Foundation not yet connected
```

This allows the pipeline to be tested end-to-end without corrupting future persistence.

## Future replacement

When `MATRIXNEO23/memoria` is implemented, replace `NoMemoryAdapter` with a real adapter that connects to:

```text
MemoryObservation
MemoryClaim
MemoryRepository
MemoryAdmission
```

Required contract:

```text
NLU / Understanding
→ Coherence Guard
→ Authority Resolver
→ Memory Admission
→ MemoryRepository
```

The GGUF must never write memory directly.

## Integration order

1. Assemble NLU output into `MatrixTurnFrame`.
2. Run Coherence/Authority.
3. Use `NoMemoryAdapter` while memory is missing.
4. Let Affective Engine receive only transient-safe signals.
5. Build GGUF prompt with explicit `NO_MEMORY_BACKEND` status.
6. Replace adapter later with real Memory Foundation.

## Production status

This is not production memory. It is an integration placeholder only.
