# Memory Integration Policy

Status: MEMORY FOUNDATION NOT YET CONNECTED  
Date: 2026-09-05  
Cross-module semantic authority: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## Decision

The assembling repository must not pretend that production Memory persistence exists.
Until a real Memory Foundation is connected, Assembling uses only non-persistent preflight adapters and rejects any durable result on the pre-response path.

Memory-specific implementation must conform to MIP shared semantics and must not redefine `subject`, `owner`, `perspective`, `source`, confidence, temporal meaning, contradiction or context locally.

## Canonical separation

Memory has three distinct integration roles:

```text
PRE-RESPONSE READ / ENRICH
- every normal turn performs a lightweight Memory INDEX_PROBE
- retrieve/hydrate full Long-Term records only when relevant candidates exist
- retrieval result is read-only Context evidence
- no durable write

PRE-RESPONSE EVALUATE / PROPOSE
- MemoryPreflightPort.evaluate
- create/evaluate ephemeral MemoryCandidate values
- MemoryCandidate != MemoryRecord
- never perform durable write

POST-VALIDATION COMMIT
- PersistentConsolidationPort
- Memory Admission durable decision
- MemoryRepository save/supersede/metadata operation
- atomic transaction / lifecycle trace
```

`MemoryAdmissionPort` remains only as a deprecated compatibility facade for adapters created before this separation became explicit. It must never be implemented by a durable writer.

## Always-on index-probe rule

Target MIP behavior:

```text
EVERY NORMAL TURN
→ INDEX_PROBE
```

This is required to determine whether relevant Memory exists even when the user does not explicitly ask "do you remember?".

It does **not** mean loading the complete Memory store every turn.

Target levels:

```text
LEVEL 1 INDEX_PROBE — always and lightweight
LEVEL 2 HYDRATE_AND_RERANK — only when relevant hits exist
LEVEL 3 DEEP_OR_MULTI_HOP — only for explicit complex retrieval purpose
```

`NO_MATCH` means the index was queried successfully and no relevant candidate was found.
`INDEX_UNAVAILABLE` / domain `UNAVAILABLE` means the query could not be performed.
These states must never be collapsed into the same empty result.

## Current hard rules

- no stable Memory writes before validation;
- no fake persistence;
- no fake Memory IDs;
- no direct GGUF-to-Memory access;
- no NLU/Understanding direct Memory access;
- no Affective persistent delta from an unadmitted event;
- before response/output validation: `stableWrite == false` and `memoryIds == []`;
- `MatrixAssemblingOrchestrator` rejects violations of this pre-response boundary;
- retrieved Memory may affect context/relevance but does not automatically become Belief or truth.

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

The provisional candidate may exist only in the current `MatrixTurnFrame`/future `TurnWorkspace`; this is current-turn state, not Long-Term storage.

## Memory semantic boundaries

MIP reserves semantic Memory kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

`REFLECTION` is a record kind that can be supported even before a Reflection module exists.

The following are **not** semantic Memory kinds:

```text
RELATIONSHIP
AFFECTIVE
GOAL
INTIMACY
```

If Core/Recall/Archival terminology is retained later, it is an access/retention tier, not semantic Memory identity.

## Contradiction and supersession

```text
CONTRADICTION != SUPERSESSION
```

Authority/Belief resolution identifies explicit contradiction identity. Memory Admission consumes that resolution and must not invent semantic conflict from text difference, shared actors or unrelated predicates.

Temporal change is not contradiction by default.

Durable semantic change uses `supersede()` and preserves lineage:

```text
revisionOf → lineage root
supersededBy → sequential successor
```

Metadata reinforcement/update does not rewrite semantic history.

## Future real integration

The additive contract `PersistentConsolidationPort` names the only future durable-write boundary, but no implementation is connected.

Logical flow:

```text
INPUT
→ Understanding
→ MatrixContextSnapshot
→ Memory INDEX_PROBE / optional hydrate+rerank
→ other context reads
→ Coherence / Authority / Belief resolution
→ MemoryPreflightPort
→ appraisal / decision
→ response generation
→ OutputValidatorPort
→ PersistentConsolidationPort
     └→ Memory Admission
          └→ MemoryRepository
```

The GGUF must never write Memory directly.
Understanding, Coherence, Authority and Affective must never bypass public Memory contracts.

## Diagnostics

The canonical `DiagnosticTrace` records separately:
- retrieval/index availability and reason codes when wired;
- preflight/admission decision;
- Memory result;
- Memory ID only when a real durable commit eventually exists;
- deterministic reason codes;
- first boundary divergence.

A placeholder result must explicitly expose that persistence is disabled.

## Production status

Current Assembling Memory is an integration placeholder only:

```text
MEMORY_PERSISTENCE_DISABLED
MEMORY_RETRIEVAL_NON_CABLATO
NOT_PRODUCTION_APPROVED
```
