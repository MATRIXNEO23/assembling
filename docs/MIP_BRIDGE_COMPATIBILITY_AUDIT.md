# MIP Bridge — Repository Consolidation and Compatibility Audit

Status: **CLEANUP COMPLETE / REGRESSION GREEN**  
Date: 2026-09-05  
Repository: `MATRIXNEO23/assembling`  
Canonical semantic source: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)  
Cleanup start HEAD: `ef433a3aed519b31efe9289a8df78ed974170510`  
Cleanup PR: `#8`  
Cleanup PR tested HEAD: `2e51e1b51df101d0fdb25f9cb567201839fc07d6`  
Cleanup merge SHA: `ff38d09f73a1eec8b2a72a24571b92f1954c729c`  
PR CI: `33951029064` — SUCCESS  
Post-merge main CI: `33951548865` — SUCCESS

## 1. Scope

This cleanup consolidates Assembling around exactly one cross-module semantic authority and one common interop layer:

```text
MIP-1.0
+
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

It does not create a second protocol, rewrite module business logic, rewire the orchestrator, implement Authority Resolver, Memory, Relationship, Reflection, BDI/Decision, retrieval, Android integration or production GGUF integration.

Other repositories were not modified.

## 2. Canonical repository-structure rule

```text
EVERY NEW FUNCTIONAL MODULE
→ DEDICATED DIRECTORY / PACKAGE
```

Examples already consistent:

```text
matrix/assembling/mip/
matrix/assembling/adapters/
matrix/assembling/coherence/
```

Future modules such as `context/`, `retrieval/`, `diagnostics/` or decision adapters must use a dedicated directory/package when explicitly authorized.

Existing root runtime files remain in place. Move/rename is not justified by aesthetics alone.

## 3. Repository map

### CANONICAL_RUNTIME

| Path | Role | Decision |
|---|---|---|
| `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt` | per-turn runtime frame + current DTOs + diagnostics | KEEP |
| `src/main/kotlin/matrix/assembling/IntegrationPorts.kt` | runtime ports | KEEP |
| `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt` | turn orchestrator | KEEP |
| `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt` | frame-based prompt builder | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt` | NLU/Understanding adapter | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/AffectiveLabAdapter.kt` | Affective adapter | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/NoPersistentMemoryAdmission.kt` | non-durable Memory preflight | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt` | conservative placeholders/test adapters | KEEP / REVIEW LATER |

### MIP_CANONICAL / MIP_ADAPTER

| Path | Role | Decision |
|---|---|---|
| `docs/MATRIX_INTERMODULE_PROTOCOL.md` | single cross-module semantic authority | CANONICAL |
| `src/main/kotlin/matrix/assembling/mip/MipBridge.kt` | single common interop adapter layer | CANONICAL ADAPTER |
| `src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt` | round-trip/fail-closed gate | KEEP |
| `docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md` | canonical cleanup/compatibility audit | CURRENT |

### COMPATIBILITY_ONLY

| Path | Decision |
|---|---|
| `src/main/kotlin/matrix/assembling/contracts/MatrixAssemblyContracts.kt` | KEEP_COMPATIBILITY |
| `src/main/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipeline.kt` | KEEP_COMPATIBILITY / DEPRECATED |
| `src/main/kotlin/matrix/assembling/prompt/SemanticFrameToPrompt.kt` | KEEP_COMPATIBILITY / DEPRECATED |
| `src/main/kotlin/matrix/assembling/coherence/CoherenceGuard.kt` | KEEP_COMPATIBILITY / DEPRECATED |
| `src/test/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipelineTest.kt` | compatibility regression gate |

No compatibility file was deleted, renamed or moved.

### TEST

All existing `*Test.kt` remain active:

```text
ArchitectureBoundaryTest.kt
DiagnosticTraceTest.kt
MatrixAssemblingOrchestratorIntegrationTest.kt
P1BoundaryTest.kt
PromptBoundaryTest.kt
adapters/ComponentMappingCompatibilityTest.kt
mip/MipBridgeTest.kt
pipeline/MatrixAssemblyPipelineTest.kt
```

### DOCUMENTATION / EVIDENCE

Canonical/current:

```text
docs/README.md
docs/MATRIX_INTERMODULE_PROTOCOL.md
docs/MODULE_CONNECTIONS.md
docs/ASSEMBLY_PLAN.md
docs/MEMORY_INTEGRATION_POLICY.md
docs/MEMORY_INTEGRATION_STATUS.md
docs/WORK_CONTINUITY.md
```

Historical/evidence:

```text
docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md
docs/IMPORTED_COMPONENTS.md
prompts/WORK_ASSEMBLING_M1_INTEGRATION.md
```

Model baseline remains untouched.

## 4. Boundary map

| Producer | Native output | MIP equivalent | Consumer | Status |
|---|---|---|---|---|
| Matrix-NLU runtime | `MatrixNluInterpretation` / `MatrixNluClaim[]` | `MipClaimV1` | Understanding | explicit mapping |
| NLU adapter | `NluOutput`, `TypedClaim[]` | `MipClaimV1` | Understanding/Coherence | partial native representation |
| Understanding | root `SemanticFrame`, `TypedClaim[]` | MIP claim concepts | Coherence | runtime DTO, not protocol authority |
| Coherence | root `CoherenceDecision` | `MipCoherenceDecisionV1` | Authority | exact symbolic round-trip |
| Authority placeholder | root `AuthorityDecision` | `MipAuthorityResolutionV1` | Memory preflight | lacks contradiction identity |
| Python Authority reference seam | `contradicts_memory_id: Optional[int]` | opaque `contradictedMemoryId` | future Kotlin Memory seam | explicit width/null handling |
| Memory preflight | `MemoryAdmissionResult` | `MipMemoryResultV1` | Affective/guard | pre-response non-durable only |
| Affective | root `AffectiveState` | `MipAffectiveSnapshotV1` | Prompt Builder | Relationship summary compatibility-only |
| Prompt Builder | `GgufPrompt` | codec deferred | GGUF | no demonstrated interop need |
| GGUF | `AssistantReply` | codec deferred | Output Validator | no demonstrated interop need |

`MatrixTurnFrame` is the operational turn container, not a competing semantic protocol.

## 5. Incompatibility matrix

### NLU / Understanding / Claim

| Semantic | Native source | MIP | Current destination | Problem | Severity |
|---|---|---|---|---|---|
| claim identity | absent in `MatrixNluClaim` | `claimId` | required in `TypedClaim` | generated externally | P1 |
| dialogue act | `String` | explicit | absent in `TypedClaim` | isolated TypedClaim loses field | P1 |
| predicate | `String` | PredicateId concept | `String` | open registry | P1 |
| polarity | `String` | canonical value | `String` | open registry | P1 |
| temporal | `String` | `TemporalRef` target | `String` | incomplete temporal model | P1 |
| subject | `String?` | `EntityRef` | `String` + `UNKNOWN` | null/sentinel mismatch | P1 |
| target | `String?` | `EntityRef` | `String?` | ambiguous native null | P1 |
| owner | `String?` | `EntityRef` | `ownerId: String?` | naming/null semantics | P0/P1 |
| perspective | `String?` | `EntityRef` | `String?` | null semantics | P0/P1 |
| confidence | `Double` + map | typed confidence taxonomy | map | structural/semantic mismatch | P1 |
| spans | list pairs | `MipSpan` map | `TextSpan` map | shape mismatch | P2 |
| source type | `String?` | explicit field | `String` | nullability | P1 |
| world truth | `Boolean` | epistemic/source semantics | `Boolean` | must not self-grant authority | P0/P1 |
| adult/intimacy | `Boolean?` | semantic marker | absent in isolated `TypedClaim` | potential loss | P1 |

### Authority / Memory

Canonical seam:

```text
Python: contradicts_memory_id: Optional[int]
MIP: contradictedMemoryId: explicit opaque decimal String ID
future Kotlin Memory: contradictedMemoryId: Long?
```

| Issue | Risk | Cleanup behavior |
|---|---|---|
| arbitrary Python int → Kotlin Long | overflow | explicit range check, overflow throws |
| native `None/null` | ambiguous absence | only `NOT_APPLICABLE` may become absence |
| `UNKNOWN/UNRESOLVED/AMBIGUOUS/CONFLICTED/UNAVAILABLE/NO_MATCH/ERROR` | silent collapse | conversion throws |
| root `AuthorityDecision` lacks contradiction field | identity loss | conversion refuses non-`UNAVAILABLE` state |
| partial Python Authority projection | dropping other canonical fields | projection requires other fields `UNAVAILABLE` |

### Memory preflight / Affective

| Field | Risk | Status |
|---|---|---|
| `MemoryAdmissionResult.status: String` | open vocabulary | P1/P2 remaining |
| `stableWrite` | pre-response persistence | orchestrator guard |
| `memoryIds` | durable ID pre-response | orchestrator guard |
| `AffectiveState.relationshipSummary` | accidental Relationship ownership | compatibility-only |
| `persistentDeltaAllowed` | persistence without admission | regression-guarded |

## 6. Duplicate concepts and disposition

```text
root SemanticFrame = current runtime DTO
contracts.SemanticFrame = COMPATIBILITY_ONLY
MIP = shared semantic authority
```

```text
root CoherenceDecision = current runtime enum
contracts.CoherenceDecision = COMPATIBILITY_ONLY
adapters.BasicCoherenceGuard = current placeholder
coherence.CoherenceGuard = COMPATIBILITY_ONLY / DEPRECATED
```

```text
root SemanticFrameToPrompt = current runtime prompt builder
prompt.SemanticFrameToPrompt = COMPATIBILITY_ONLY / DEPRECATED
```

```text
NoPersistentMemoryAdmission = current temporary integration preflight
BasicMemoryAdmission = obsolete candidate / KEEP until caller proof
```

## 7. MIP Bridge audit result

PASS:
- explicit mappings only;
- no reflection/magic;
- no business logic;
- schema version explicit;
- Python memory ID preserved as opaque decimal string;
- Kotlin Long overflow fails closed;
- lossy contradiction conversions fail closed;
- partial Python projection cannot silently discard populated canonical fields.

Fixes applied:

### Entity resolution

`MipEntityResolutionStatus` is distinct from field presence:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

### General field status

```text
PRESENT
NOT_APPLICABLE
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
NO_MATCH
ERROR
```

### Contradiction nullable conversion

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> null / None
all other states -> MipContractException
```

No Authority Resolver was implemented. `BasicAuthorityResolver` remains a conservative placeholder and does not perform semantic contradiction detection.

## 8. Structural cleanup

Result:

```text
FILES MOVED = 0
FILES RENAMED = 0
FILES DELETED = 0
```

No mass package refactor was performed. Existing runtime root files remain stable. New functional modules are now required to use dedicated directories/packages.

## 9. Legacy path

```text
contracts/* = KEEP_COMPATIBILITY
pipeline/* = KEEP_COMPATIBILITY / DEPRECATED
prompt/* = KEEP_COMPATIBILITY / DEPRECATED
coherence/CoherenceGuard.kt = KEEP_COMPATIBILITY / DEPRECATED
```

No legacy code was deleted without caller proof.

## 10. Round-trip / incompatibility tests

`MipBridgeTest` covers and preserves:
- Matrix-NLU claim round-trip;
- current TypedClaim round-trip;
- Python Authority contradiction round-trip;
- Python → MIP → Kotlin contradiction seam;
- nullable no-contradiction mapping;
- Assembling Authority round-trip when representable;
- fail-closed contradiction loss;
- Kotlin Long overflow;
- primitive wire-map round-trip;
- missing fields;
- illegal status/value combinations;
- MemoryResult round-trip;
- AffectiveState round-trip;
- Coherence enum round-trip;
- `RESOLVED` entity semantics;
- `NO_MATCH` and `ERROR` explicit states;
- unresolved/unavailable contradiction cannot collapse to native null;
- partial Python projection cannot discard canonical data.

Existing tests were not weakened or removed.

## 11. Regression / CI evidence

```text
cleanup PR #8
head = 2e51e1b51df101d0fdb25f9cb567201839fc07d6
CI run = 33951029064
result = SUCCESS

merge SHA = ff38d09f73a1eec8b2a72a24571b92f1954c729c
post-merge main CI run = 33951548865
Run tests = SUCCESS
job result = SUCCESS
```

## 12. Residual risks

### P0 remaining

1. Root `AuthorityDecision` lacks contradiction identity; it is not the final Authority contract.
2. Complete frozen/reference Python `AuthorityResolution` is not stored in Assembling; only the confirmed contradiction seam is cross-language grounded here.
3. Owner/perspective remain nullable/sentinel-rich in native DTOs.
4. `worldTruth: Boolean` remains a compatibility representation and must never self-grant Authority.
5. `BasicAuthorityResolver` does not detect semantic contradictions; real Authority work is explicitly deferred.

### P1 remaining

1. `TypedClaim` lacks dialogue act and explicit semantic-domain marker.
2. Native `null` / `UNKNOWN` / `NONE` remain outside MIP.
3. Predicate/dialogue/source/status remain mostly stringly typed.
4. Full `TemporalRef`, `ProvenanceRef`, modality and claimKind parity is deferred.
5. Runtime does not yet wrap all boundaries in `MatrixEnvelope<T>`.
6. Legacy duplicate vocabulary remains, but is quarantined/deprecated.
7. Only the high-risk Authority seam has an explicit primitive wire codec.

### P2

1. Python snake_case vs Kotlin camelCase is an adapter concern.
2. `MemoryAdmissionResult.status` remains a string.
3. Semantic marker registry remains an open string map.
4. Root runtime is not physically split into runtime/ports/diagnostics because current migration risk exceeds cleanup benefit.

## 13. Acceptance result

```text
MIP = single cross-module semantic authority        PASS
MipBridge = single common interop adapter layer     PASS
MatrixTurnFrame = runtime frame only                PASS
future module = dedicated directory/package         PASS
legacy path = quarantined/deprecated                 PASS
silent contradiction-state loss = blocked           PASS
business logic added to bridge = false              PASS
existing module DTOs modified = false               PASS
other repositories modified = false                 PASS
files moved = 0                                      PASS
files renamed = 0                                    PASS
files deleted = 0                                    PASS
round-trip / strict tests = PASS                     PASS
full regression = PASS                               PASS
CI = GREEN                                            PASS
```

## 14. STOP

Cleanup complete.

Do not automatically start:
- Authority Resolver implementation;
- Memory implementation/retrieval/persistence;
- Relationship;
- Reflection;
- Decision/BDI;
- Intimacy/Consent resolver;
- Android integration;
- orchestrator rewiring.

```text
NEXT = AWAIT OWNER REVIEW
```
