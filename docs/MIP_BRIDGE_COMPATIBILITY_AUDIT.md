# MIP Bridge — Repository Consolidation and Compatibility Audit

Status: **SAFE CLEANUP APPLIED / REGRESSION VALIDATION PENDING**  
Date: 2026-09-05  
Repository: `MATRIXNEO23/assembling`  
Branch: `assembling-mip-cleanup`  
Start HEAD: `ef433a3aed519b31efe9289a8df78ed974170510`  
Canonical semantic source: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## 1. Scope

This cleanup consolidates the Assembling repository around exactly one cross-module semantic authority and one interop layer:

```text
MIP-1.0
+
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

It does not create another protocol, change business logic, rewire the orchestrator, implement Memory/Relationship/Reflection/BDI, or modify other repositories.

Hard boundary:

```text
NATIVE MODULE DTO
        ↓ explicit mapping
      MIP BRIDGE
        ↓ explicit mapping
NATIVE DESTINATION DTO
```

The bridge does not decide truth, authority, contradiction, Memory Admission, affective meaning or natural-language interpretation.

## 2. Canonical repository-structure rule

Owner rule:

```text
EVERY NEW FUNCTIONAL MODULE
→ DEDICATED DIRECTORY / PACKAGE
```

Examples already consistent with the rule:

```text
matrix/assembling/mip/
matrix/assembling/adapters/
matrix/assembling/coherence/
```

Future modules such as context, retrieval, diagnostics or decision adapters must use their own directory/package when explicitly authorized.

Existing root runtime files are not moved for aesthetics. Move/rename requires demonstrated ambiguity reduction, compatibility evidence and green regression tests.

## 3. Repository map

### Canonical runtime

| Path | Role | Decision |
|---|---|---|
| `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt` | runtime frame + current root DTOs + diagnostics | KEEP |
| `src/main/kotlin/matrix/assembling/IntegrationPorts.kt` | authoritative runtime ports | KEEP |
| `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt` | authoritative turn orchestrator | KEEP |
| `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt` | authoritative frame-based prompt builder | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt` | NLU/Understanding adapter | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/AffectiveLabAdapter.kt` | affective lab adapter | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/NoPersistentMemoryAdmission.kt` | current non-durable Memory preflight adapter | KEEP |
| `src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt` | placeholder/test adapters | KEEP / REVIEW LATER |

### MIP

| Path | Role | Decision |
|---|---|---|
| `docs/MATRIX_INTERMODULE_PROTOCOL.md` | cross-module semantic authority | CANONICAL |
| `src/main/kotlin/matrix/assembling/mip/MipBridge.kt` | sole common interop adapter layer | CANONICAL ADAPTER |
| `src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt` | round-trip/fail-closed gate | KEEP |
| `docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md` | compatibility/consolidation audit | CURRENT AUDIT |

### Compatibility-only

| Path | Decision |
|---|---|
| `src/main/kotlin/matrix/assembling/contracts/MatrixAssemblyContracts.kt` | KEEP_COMPATIBILITY |
| `src/main/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipeline.kt` | KEEP_COMPATIBILITY / already deprecated |
| `src/main/kotlin/matrix/assembling/prompt/SemanticFrameToPrompt.kt` | KEEP_COMPATIBILITY / explicitly deprecated in this cleanup |
| `src/main/kotlin/matrix/assembling/coherence/CoherenceGuard.kt` | KEEP_COMPATIBILITY / explicitly deprecated in this cleanup |
| `src/test/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipelineTest.kt` | compatibility regression gate |

No compatibility file is deleted or moved in this checkpoint.

### Tests

All existing `*Test.kt` remain active regression gates:

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

### Documentation/evidence

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

Model baseline remains untouched:

`models/matrix-nlu/matrix-nlu-student-4-v22a-mixed-head-protected-local-20260904T1440Z.zip`

## 4. Boundary map

| Producer | Native output | MIP equivalent | Consumer | Loss characteristics |
|---|---|---|---|---|
| Matrix-NLU runtime | `MatrixNluInterpretation` / `MatrixNluClaim[]` | `MipClaimV1` | Understanding adapter | explicit mapping; native claimId absent |
| NLU adapter | `NluOutput`, `TypedClaim[]` | `MipClaimV1` | Understanding/Coherence | TypedClaim alone lacks dialogueAct/domain marker |
| Understanding | root `SemanticFrame`, `TypedClaim[]` | MIP claim concepts | Coherence | root SemanticFrame remains runtime DTO, not protocol |
| Coherence | root enum `CoherenceDecision` | `MipCoherenceDecisionV1` | Authority | exact symbolic round-trip |
| Authority | root `AuthorityDecision` | `MipAuthorityResolutionV1` | Memory preflight | current DTO lacks contradiction identity |
| Python Authority reference seam | `contradicts_memory_id: Optional[int]` | opaque `contradictedMemoryId` | Kotlin Memory seam | width/nullability conversion explicit |
| Memory preflight | `MemoryAdmissionResult` | `MipMemoryResultV1` | Affective/guard | pre-response stableWrite must remain false |
| Affective | root `AffectiveState` | `MipAffectiveSnapshotV1` | Prompt Builder | relationship summary is compatibility-only |
| Prompt Builder | `GgufPrompt` | no proven cross-language need | GGUF | codec deferred |
| GGUF | `AssistantReply` | no proven cross-language need | Output Validator | codec deferred |

`MatrixTurnFrame` is the operational per-turn container. It is not a competing semantic protocol.

## 5. Incompatibility matrix

### NLU / Understanding / Claim

| Semantic | Native source | MIP | Current destination | Problem | Severity |
|---|---|---|---|---|---|
| claim identity | absent in `MatrixNluClaim` | `claimId` | required in `TypedClaim` | generated externally | P1 |
| dialogue act | `String` | explicit field | absent in `TypedClaim` | lost if TypedClaim isolated | P1 |
| predicate | `String` | PredicateId concept | `String` | stringly/open registry | P1 |
| polarity | `String` | canonical semantic value | `String` | stringly/open registry | P1 |
| temporal | `String` | target `TemporalRef` | `String` | full temporal model not represented | P1 |
| resolved subject | `String?` | `EntityRef` | `String` + `UNKNOWN` sentinel | null/sentinel mismatch | P1 |
| target | `String?` | `EntityRef` | `String?` | native null ambiguous | P1 |
| owner | `String?` | `EntityRef` | `ownerId: String?` | naming/null semantics | P0/P1 |
| perspective | `String?` | `EntityRef` | `String?` | null semantics | P0/P1 |
| confidence overall | `Double` | `interpretationConfidence` | `confidence["overall"]` | structural mismatch | P1 |
| per-head confidence | map | `confidenceByField` | map | naming mismatch | P2 |
| spans | five `List<Int>?` | `MipSpan` map | `TextSpan` map | shape/type mismatch | P2 |
| source type | `String?` | explicit MIP field | `String` | nullability mismatch | P1 |
| world truth | `Boolean` | epistemic/source semantics | `Boolean` | semantic compression; must not become authority | P0/P1 |
| adult/intimacy | `Boolean?` | semantic marker | absent in `TypedClaim` | loss in isolated TypedClaim | P1 |

### Authority / Memory

Owner-provided real seam:

```text
Python: contradicts_memory_id: Optional[int]
MIP: contradictedMemoryId: explicit opaque decimal String ID
future Kotlin Memory: contradictedMemoryId: Long?
```

| Issue | Risk | Cleanup behavior |
|---|---|---|
| Python arbitrary int → Kotlin Long | overflow/corruption | explicit range validation; overflow throws |
| Python `None` / Kotlin `null` | could mean several states | only MIP `NOT_APPLICABLE` may become native absence |
| `UNRESOLVED`/`UNAVAILABLE`/`AMBIGUOUS`/etc. | silent semantic collapse | conversion now throws |
| current root `AuthorityDecision` lacks contradiction field | contradiction identity loss | conversion only round-trips when canonical field is `UNAVAILABLE`; otherwise throws |
| Python seam DTO only models contradiction field | dropping other canonical Authority fields | Python projection now requires all other canonical fields to be `UNAVAILABLE`; otherwise throws |

### Memory preflight / Affective

| Field | Current type | Risk | Status |
|---|---|---|---|
| `MemoryAdmissionResult.status` | `String` | open vocabulary | P1/P2 remaining |
| `stableWrite` | `Boolean` | pre-response persistence violation | guarded by orchestrator |
| `memoryIds` | `List<String>` | durable ID before response | guarded by orchestrator |
| `AffectiveState.relationshipSummary` | `String` | mistaken Relationship authority | documented compatibility-only |
| `persistentDeltaAllowed` | `Boolean` | persistence without admission | existing boundary tests guard |

## 6. Duplicate concepts

### SemanticFrame

```text
matrix.assembling.SemanticFrame
matrix.assembling.contracts.SemanticFrame
```

Decision:

```text
root SemanticFrame = current runtime DTO
contracts.SemanticFrame = compatibility-only
MIP = shared semantic authority
```

### Coherence

```text
matrix.assembling.CoherenceDecision
matrix.assembling.contracts.CoherenceDecision
adapters.BasicCoherenceGuard
coherence.CoherenceGuard
```

`coherence.CoherenceGuard` consumes only legacy `contracts.*`; it is explicitly deprecated compatibility-only despite the generic package name.

### Prompt

```text
matrix.assembling.SemanticFrameToPrompt
matrix.assembling.prompt.SemanticFrameToPrompt
```

Root implementation is the current frame runtime. `prompt.SemanticFrameToPrompt` is explicitly deprecated compatibility-only.

### Memory preflight placeholders

`NoPersistentMemoryAdmission` and `BasicMemoryAdmission` both provide non-durable preflight behavior. The current integration test uses `NoPersistentMemoryAdmission`.

Decision:

```text
NoPersistentMemoryAdmission = current temporary integration adapter
BasicMemoryAdmission = obsolete candidate / KEEP until complete caller proof
```

## 7. Safe fixes applied

### SAFE_ADAPTER

1. Added a distinct `MipEntityResolutionStatus`:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

This removes the previous ambiguity where generic field `PRESENT` was also used as entity-resolution state.

2. Completed `MipFieldStatus` with MIP-1.0 result states:

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

3. Hardened Authority contradiction conversion:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> null / None
all other statuses -> MipContractException
```

4. Hardened partial Python Authority projection so non-modeled canonical fields cannot be silently dropped.

### SAFE_STRUCTURAL

- `pipeline.MatrixAssemblyPipeline` remains deprecated compatibility-only.
- `prompt.SemanticFrameToPrompt` now has explicit `@Deprecated` compatibility annotation.
- `coherence.CoherenceGuard` now has explicit `@Deprecated` compatibility annotation.
- no files moved;
- no files renamed;
- no files deleted.

### SAFE_DOCUMENTATION

- dedicated-directory/package rule added to `PROJECT_WORK_RULES.md`;
- `docs/README.md` updated with MIP status vocabulary and compatibility paths;
- this audit updated as the single consolidation audit.

## 8. Tests added/strengthened

`MipBridgeTest` now additionally verifies:

- resolved entity state is `RESOLVED`, not generic `PRESENT`;
- invalid unresolved entity carrying `entityId` fails;
- `NO_MATCH` and `ERROR` exist without fake values;
- unresolved contradiction cannot collapse to Kotlin `null`;
- unavailable contradiction cannot collapse to Python `None`;
- Python partial projection cannot discard populated canonical Authority fields;
- current Assembling Authority cannot represent even a known `NOT_APPLICABLE` contradiction state without semantic loss.

Existing round-trip, overflow, wire-map, Memory, Affective and Coherence tests remain.

## 9. Deferred work — intentionally NOT implemented

The following remain outside cleanup scope:

- adding contradiction identity to root `AuthorityDecision`;
- implementing real contextual Authority/Belief resolution;
- full frozen/reference Python Authority contract adapter;
- complete MIP `TypedClaim` parity (`claimKind`, `modality`, `TemporalRef`, `ProvenanceRef`);
- full `MatrixEnvelope<T>` runtime integration;
- PredicateId registry implementation;
- `MatrixContextSnapshot` runtime;
- Memory index/retrieval;
- Kotlin/Room Memory Foundation;
- Relationship;
- Reflection;
- Decision/BDI;
- Android/GGUF production integration.

No field is invented to pretend these components already exist.

## 10. Residual risks

### P0 remaining

1. **Current root `AuthorityDecision` lacks contradiction identity.** Real memory-backed Authority/Admission cannot be wired through that DTO without an authorized richer boundary/adapter.
2. **Complete Python frozen/reference Authority contract is not available in this repository.** Only the owner-provided contradiction field is cross-language grounded here.
3. **Owner/perspective remain nullable/sentinel-rich in native DTOs.** Existing guards mitigate, but final MIP-native runtime types are still needed before production integration.
4. **`worldTruth: Boolean` remains in compatibility DTOs.** It must never be interpreted as self-granted Authority; MIP/World source remains authoritative.

### P1 remaining

1. `TypedClaim` lacks dialogue act and explicit semantic-domain marker.
2. Native `null`/`UNKNOWN`/`NONE` sentinels remain outside the bridge.
3. Predicate/dialogue/source/status values remain mostly stringly typed.
4. Full temporal/provenance/modality/claimKind parity is not yet represented in `MipClaimV1`.
5. Runtime does not yet carry all intermodule payloads in `MatrixEnvelope<T>`.
6. Legacy duplicate vocabulary remains, though now quarantined/deprecated.
7. Only the high-risk Authority seam currently has an explicit primitive wire-map codec.

### P2

1. Python snake_case vs Kotlin camelCase remains an adapter concern.
2. `MemoryAdmissionResult.status` remains a string.
3. semantic marker registry remains an open string map.
4. current root runtime package is not physically split into runtime/ports/diagnostics; moving it now has higher risk than benefit.

## 11. Acceptance criteria for this cleanup

```text
MIP = single cross-module semantic authority
MipBridge = single common interop adapter layer
MatrixTurnFrame = runtime frame, not competing protocol
future module = dedicated directory/package
legacy path = explicitly quarantined/deprecated
silent contradiction-state loss = blocked
business logic in bridge = false
existing module DTOs modified = false
other repositories modified = false
files moved = 0
files renamed = 0
files deleted = 0
new strict tests = added
full regression = REQUIRED
CI = REQUIRED
```

The cleanup is not complete until the full Gradle suite and CI are green.
