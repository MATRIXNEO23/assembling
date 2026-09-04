# Memory integration status

Status: `NOT_IMPLEMENTED_YET / PORT_ONLY`

The assembling repository does not currently contain the real Matrix memory
system. Memory is represented only by an integration port and a no-persistent
adapter so the rest of the pipeline can be connected and smoke-tested without
pretending that durable memory exists.

## Current truth

- Real persistent memory: not implemented here yet.
- Memory Foundation repository: external work item, expected from
  `MATRIXNEO23/memoria` or a future imported module.
- Current assembling behavior: no stable database write, no real recall, no
  supersede lineage, no Room/SQLite persistence.
- Current memory adapter purpose: preserve the module boundary and prevent NLU,
  Affective Engine or GGUF from writing memory directly.

## Allowed temporary behavior

Until Memory Foundation exists, the assembling layer may use
`NoPersistentMemoryAdmission`:

- it returns `PROVISIONAL_CLAIM` or `REJECTED`;
- it never returns real memory IDs;
- it never writes stable memory;
- it allows Affective and Prompt Builder integration to be tested safely;
- it records diagnostics showing memory persistence is disabled.

## Required future replacement

The temporary adapter must later be replaced by a real memory component exposing
this behavior:

```text
MemoryAdmissionPort
→ validates CoherenceDecision + AuthorityDecision
→ writes only admitted claims
→ stores provenance
→ supports RAW / PROVISIONAL / ADMITTED / SUPERSEDED / REJECTED
→ supports validAt / invalidAt / supersedesId
→ supports recall/search for prompt context
→ never allows GGUF direct writes
```

## Integration rule

The assembling pipeline may proceed with placeholder memory for end-to-end
wiring, but any build using it must be marked:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```

This avoids confusing a smoke-test pipeline with a real cognitive memory system.
