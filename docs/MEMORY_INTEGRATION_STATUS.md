# Memory integration status

Status: `NOT_IMPLEMENTED_YET / PREFLIGHT_ONLY`

The assembling repository does not currently contain the real Matrix Long-Term
Memory system. The current memory boundary is a non-persistent preflight used to
keep the canonical pipeline testable without pretending that durable storage
exists.

## Current truth

- Real persistent memory: not connected.
- Real Memory Admission SAVE/SUPERSEDE: not connected.
- Real recall/retrieval: not connected.
- Supersede lineage and atomic repository transaction: not executed here.
- Current pre-response contract: `MemoryPreflightPort`.
- Future post-validation write contract: `PersistentConsolidationPort`.

## Current behavior

`NoPersistentMemoryAdmission` and `BasicMemoryAdmission` are compatibility-named
preflight adapters. They:

- return `PROVISIONAL_CLAIM`, `NO_MEMORY_BACKEND`, or `REJECTED`;
- always return `stableWrite=false`;
- always return an empty `memoryIds` list;
- never call a MemoryRepository;
- expose the decision in `DiagnosticTrace`;
- mark the actual durable-memory stage `NOT_EXECUTED`.

The orchestrator rejects any preflight result that attempts:

```text
stableWrite=true
or
memoryIds not empty
```

with:

```text
firstDivergence = MEMORY_PREFLIGHT.UNAUTHORIZED_STABLE_WRITE
```

## Required future replacement

After output validation, a real consolidation adapter must connect:

```text
resolved TypedClaim / AuthorityResolution
→ Memory Admission
→ MemoryRepository public API
→ atomic commit or rollback
```

It must support provenance, validity, conflicts and supersession lineage. It
must not turn the existing pre-response preflight into a write path.

## Integration rule

The current pipeline may proceed for wiring and smoke tests, but every build
using this state remains:

```text
MEMORY_PERSISTENCE_DISABLED
NOT_PRODUCTION_APPROVED
```
