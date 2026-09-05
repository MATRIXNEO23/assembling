# MIP Bridge — Repository Consolidation and Compatibility Audit

Status: **AUDIT PHASE COMPLETE / SAFE FIXES PENDING**  
Date: 2026-09-05  
Repository: `MATRIXNEO23/assembling`  
Branch: `assembling-mip-cleanup`  
Start HEAD: `ef433a3aed519b31efe9289a8df78ed974170510`  
Canonical semantic source: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## 1. Scope and non-goals

This audit consolidates the Assembling repository around one cross-module semantic authority:

```text
MIP-1.0
```

and one explicit interop layer:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

It does not create a second protocol, rewrite existing modules, change business logic, implement Memory/Relationship/Reflection/BDI, or rewire the orchestrator.

Hard boundary:

```text
NATIVE MODULE DTO
        ↓ explicit mapping only
      MIP BRIDGE
        ↓ explicit mapping only
NATIVE DESTINATION DTO
```

The bridge:
- does not decide truth;
- does not resolve authority;
- does not infer contradictions;
- does not perform Memory Admission;
- does not mutate Relationship/Affective state;
- does not re-parse natural language;
- does not use reflection/magic mapping;
- fails explicitly when a destination cannot represent a canonical value.

Other repositories remain read-only.

## 2. Canonical repository structure rule

Owner rule added for all future work:

```text
EVERY NEW FUNCTIONAL MODULE
MUST LIVE IN ITS OWN DEDICATED DIRECTORY / PACKAGE.
```

Examples:

```text
matrix/assembling/mip/
matrix/assembling/coherence/
matrix/assembling/adapters/
```

Future modules such as context, retrieval, diagnostics, relationship adapters, or decision adapters must not be added as unrelated files directly into the root `matrix/assembling` package.

Existing root runtime files are not moved merely for aesthetics. A move/rename is allowed only when it reduces a demonstrated ambiguity and the compatibility benefit is greater than the migration risk.

## 3. Repository inventory

### 3.1 Root/build/control

| File/path | Classification | Decision |
|---|---|---|
| `.github/workflows/ci.yml` | TEST / CI CONTROL | KEEP |
| `.gitattributes` | REPOSITORY CONTROL | KEEP |
| `.gitignore` | REPOSITORY CONTROL | KEEP |
| `build.gradle.kts` | BUILD CONTROL | KEEP |
| `settings.gradle.kts` | BUILD CONTROL | KEEP |
| `PROJECT_WORK_RULES.md` | DOCUMENTATION_CANONICAL | KEEP |
| `README.md` | DOCUMENTATION_CANONICAL | KEEP |

### 3.2 Canonical runtime root

| File | Classification | Decision |
|---|---|---|
| `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt` | CANONICAL_RUNTIME | KEEP IN PLACE |
| `src/main/kotlin/matrix/assembling/IntegrationPorts.kt` | CANONICAL_RUNTIME | KEEP IN PLACE |
| `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt` | CANONICAL_RUNTIME | KEEP IN PLACE |
| `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt` | CANONICAL_RUNTIME | KEEP IN PLACE |

These four files form the current frame-based runtime surface. They are existing runtime roots, not authorization to place future modules in the same directory.

### 3.3 Adapter layer

| File | Classification | Decision |
|---|---|---|
| `adapters/UnderstandingLabAdapter.kt` | MIP-ADJACENT ADAPTER / CANONICAL_RUNTIME SUPPORT | KEEP |
| `adapters/AffectiveLabAdapter.kt` | ADAPTER / CANONICAL_RUNTIME SUPPORT | KEEP |
| `adapters/NoPersistentMemoryAdmission.kt` | TEMPORARY CANONICAL_RUNTIME ADAPTER | KEEP |
| `adapters/BasicAdapters.kt` | TEST/PLACEHOLDER ADAPTER COLLECTION | KEEP / REVIEW LATER |

`BasicAdapters.kt` contains several independent placeholder adapters in one file. This is not a new-module violation retroactively, but future additions should not expand this file with unrelated modules. Split only when a concrete maintenance or ownership reason exists.

### 3.4 MIP

| File | Classification | Decision |
|---|---|---|
| `mip/MipBridge.kt` | MIP_ADAPTER | KEEP / AUDIT |
| `docs/MATRIX_INTERMODULE_PROTOCOL.md` | MIP_CANONICAL | KEEP |
| `docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md` | AUDIT_EVIDENCE / CURRENT AUDIT | KEEP / UPDATE |

### 3.5 Compatibility-only path

| File/path | Classification | Decision |
|---|---|---|
| `contracts/MatrixAssemblyContracts.kt` | COMPATIBILITY_ONLY | KEEP_COMPATIBILITY |
| `pipeline/MatrixAssemblyPipeline.kt` | COMPATIBILITY_ONLY / DEPRECATED | KEEP_COMPATIBILITY |
| `prompt/SemanticFrameToPrompt.kt` | COMPATIBILITY_ONLY | DEPRECATE EXPLICITLY |
| `coherence/CoherenceGuard.kt` | COMPATIBILITY_ONLY despite generic package name | DEPRECATE EXPLICITLY |

The `coherence` directory is structurally ambiguous because its name sounds canonical while its implementation consumes only `matrix.assembling.contracts.*`, i.e. the legacy vocabulary. It must remain compatibility-only until explicitly replaced or removed.

### 3.6 Tests

| File/path | Classification | Role |
|---|---|---|
| `ArchitectureBoundaryTest.kt` | TEST | current architectural boundary regressions |
| `DiagnosticTraceTest.kt` | TEST | trace invariants |
| `MatrixAssemblingOrchestratorIntegrationTest.kt` | TEST | current frame-based runtime integration |
| `P1BoundaryTest.kt` | TEST | hardening regressions |
| `PromptBoundaryTest.kt` | TEST | current prompt boundary |
| `adapters/ComponentMappingCompatibilityTest.kt` | TEST | adapter compatibility |
| `mip/MipBridgeTest.kt` | TEST | MIP round-trip/fail-closed interop |
| `pipeline/MatrixAssemblyPipelineTest.kt` | TEST / COMPATIBILITY | legacy path regression |

No existing test is deleted or weakened in this cleanup.

### 3.7 Documentation/evidence

| File | Classification | Decision |
|---|---|---|
| `docs/README.md` | DOCUMENTATION_CANONICAL | UPDATE if needed |
| `docs/ASSEMBLY_PLAN.md` | DOCUMENTATION_CANONICAL | UPDATE if needed |
| `docs/MODULE_CONNECTIONS.md` | DOCUMENTATION_CANONICAL | UPDATE if needed |
| `docs/MEMORY_INTEGRATION_POLICY.md` | DOCUMENTATION_CANONICAL | KEEP |
| `docs/MEMORY_INTEGRATION_STATUS.md` | DOCUMENTATION_CANONICAL | KEEP |
| `docs/WORK_CONTINUITY.md` | DOCUMENTATION_CANONICAL | UPDATE |
| `docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md` | AUDIT_EVIDENCE / HISTORICAL | KEEP |
| `docs/IMPORTED_COMPONENTS.md` | AUDIT_EVIDENCE | KEEP |
| `prompts/WORK_ASSEMBLING_M1_INTEGRATION.md` | AUDIT/HISTORICAL PROMPT | KEEP |

### 3.8 Model artifact

| Path | Classification | Decision |
|---|---|---|
| `models/matrix-nlu/matrix-nlu-student-4-v22a-mixed-head-protected-local-20260904T1440Z.zip` | MODEL_ARTIFACT / BASELINE | KEEP / DO NOT MODIFY |

## 4. Current authoritative runtime boundaries

The authoritative Assembling path is `MatrixTurnFrame` + ports.

| Producer / boundary | Native output | Consumer / boundary | Native input | MIP equivalent today |
|---|---|---|---|---|
| caller/perception | `UserMessage` | `NluPort` | `MatrixTurnFrame.input` | future envelope/observation, not wired |
| Matrix-NLU runtime bridge | `MatrixNluInterpretation` / `MatrixNluClaim[]` | `UnderstandingLabAdapter` | `MatrixNluClaim` | `MipClaimV1` adapter exists |
| NLU adapter | `NluOutput`, `TypedClaim[]` | Understanding | frame fields | `MipClaimV1` adapter exists for claim |
| Understanding | `SemanticFrame`, `TypedClaim[]` | Coherence | frame fields | claim mapping partial; `SemanticFrame` is runtime DTO, not MIP authority |
| Coherence | root `CoherenceDecision` | Authority | `coherenceDecision` | `MipCoherenceDecisionV1` |
| Authority | root `AuthorityDecision` | Memory preflight | `authorityDecision` | `MipAuthorityResolutionV1` |
| Memory preflight | `MemoryAdmissionResult` | Affective + boundary guard | `memoryResult` | `MipMemoryResultV1` |
| Affective | root `AffectiveState` | Prompt Builder | `affectiveState` | `MipAffectiveSnapshotV1` |
| Prompt Builder | `GgufPrompt` | GGUF | `prompt` | no demonstrated cross-language need |
| GGUF | `AssistantReply` | Output Validator | `reply` | no demonstrated cross-language need |
| Output Validator | updated `MatrixTurnFrame` | caller/future consolidation | updated frame | future MIP envelope/context integration |
| Persistent Consolidation | `NON_CABLATO` | durable owners | `NON_CABLATO` | design only |

Internal lab DTOs (`MatrixNluRequest`, `AffectiveRuntimeRequest`, `AffectiveImpulse`, `AffectiveRuntimeOutput`, `PersistentAffectSnapshot`) are implementation-facing and are not independent semantic authorities.

## 5. Duplicate concepts and quarantine status

### 5.1 SemanticFrame duplication

```text
matrix.assembling.SemanticFrame
vs
matrix.assembling.contracts.SemanticFrame
```

The root type is used by the authoritative frame runtime. The `contracts` type is legacy/compatibility only.

Decision:

```text
root SemanticFrame = CANONICAL_RUNTIME DTO
contracts.SemanticFrame = COMPATIBILITY_ONLY
MIP = semantic cross-module authority
```

Neither SemanticFrame type may redefine MIP semantics.

### 5.2 Coherence duplication

```text
matrix.assembling.CoherenceDecision        // current enum
matrix.assembling.contracts.CoherenceDecision // legacy data class
```

and:

```text
adapters.BasicCoherenceGuard               // current placeholder runtime
coherence.CoherenceGuard                   // compatibility pipeline implementation
```

The legacy `coherence.CoherenceGuard` uses `contracts.*` types and is therefore compatibility-only despite its generic path/name.

### 5.3 Prompt duplication

```text
matrix.assembling.SemanticFrameToPrompt
vs
matrix.assembling.prompt.SemanticFrameToPrompt
```

Root = authoritative runtime port implementation.  
`prompt/*` = compatibility-only pipeline translator.

### 5.4 Memory preflight duplicate placeholders

`NoPersistentMemoryAdmission` and `BasicMemoryAdmission` both implement non-durable preflight behavior with different compatibility statuses/reasons.

Evidence shows `MatrixAssemblingOrchestratorIntegrationTest` uses `NoPersistentMemoryAdmission` in the current integration path.

Decision for this cleanup:

```text
NoPersistentMemoryAdmission = current temporary integration adapter
BasicMemoryAdmission = KEEP / OBSOLETE_CANDIDATE, no deletion without complete caller proof
```

## 6. Field-level incompatibility matrix

### 6.1 NLU → TypedClaim / MIP

| Boundary | Native field A | Native type A | MIP field | Native field B | Native type B | Problem | Severity |
|---|---|---|---|---|---|---|---|
| NLU→Claim | claim identity | absent | `claimId` | `claimId` | `String` | generated outside NLU | P1 |
| NLU→Claim | `dialogueAct` | `String` | `dialogueAct` | absent in `TypedClaim` | — | lost in TypedClaim alone | P1 |
| NLU→Claim | `predicate` | `String` | `predicate` | `predicate` | `String` | open string registry | P1 |
| NLU→Claim | `polarity` | `String` | `polarity` | `polarity` | `String` | open string enum | P1 |
| NLU→Claim | `temporalRelation` | `String` | target `TemporalRef` concept | `temporalRelation` | `String` | current bridge collapses target temporal model to string | P1 |
| NLU→Claim | `subjectReferent` | `String` | `subject: EntityRef` + source token | absent | — | symbolic referent not native in TypedClaim | P1 |
| NLU→Claim | `targetReferent` | `String` | `target: EntityRef` | absent | — | same | P1 |
| NLU→Claim | `ownerReferent` | `String` | `owner: EntityRef` | absent | — | same | P0 if silently defaulted |
| NLU→Claim | `perspectiveReferent` | `String` | `perspective: EntityRef` | absent | — | same | P0 if silently defaulted |
| NLU→Claim | `subject` | `String?` | `EntityRef` | `subject` | `String` + `UNKNOWN` sentinel | null/sentinel mismatch | P1 |
| NLU→Claim | `target` | `String?` | `EntityRef` | `target` | `String?` | null meaning not explicit natively | P1 |
| NLU→Claim | `owner` | `String?` | `EntityRef` | `ownerId` | `String?` | naming + null semantics | P0/P1 |
| NLU→Claim | `perspective` | `String?` | `EntityRef` | `perspective` | `String?` | null semantics | P0/P1 |
| NLU→Claim | `objectValue` | `String?` | `MipField<String>` | `objectValue` | `String?` | MIP distinguishes absence states, native does not | P1 |
| NLU→Claim | `confidence` | `Double` | `interpretationConfidence` | map key `overall` | `Double?` | structural mismatch | P1 |
| NLU→Claim | `confidenceByHead` | `Map<String,Double>` | `confidenceByField` | `confidence` | `Map<String,Double>` | naming mismatch | P2 |
| NLU→Claim | five span lists | `List<Int>?` | `MipSpan` map | span map | `TextSpan?` | shape/type mismatch | P2 |
| NLU→Claim | `sourceType` | `String?` | `sourceType` | `String` | nullability mismatch | P1 |
| NLU→Claim | `worldTruth` | `Boolean` | `epistemicClass` concept | `Boolean` | legacy bool | semantic compression | P1/P0 if treated as authority |
| NLU→Claim | `adultOrIntimacy` | `Boolean?` | semantic marker | absent in `TypedClaim` | — | marker lost in TypedClaim alone | P1 |
| MIP conformance | entity status | current bridge uses general `MipFieldStatus.PRESENT` | MIP requires `EntityResolutionStatus.RESOLVED` | — | — | canonical vocabulary mismatch | **P1 SAFE_FIX** |
| MIP conformance | global status | bridge enum lacks `NO_MATCH`,`ERROR` | MIP defines both | — | — | bridge status vocabulary incomplete | **P1 SAFE_FIX** |

### 6.2 Authority → Memory

Known owner-provided seam:

```text
Python AuthorityResolution.contradicts_memory_id: Optional[int]
MIP contradictedMemoryId: explicit opaque ID
future Kotlin Memory contradictedMemoryId: Long?
```

| Property | Python | MIP | Kotlin Memory | Problem | Severity |
|---|---|---|---|---|---|
| field name | `contradicts_memory_id` | `contradictedMemoryId` | `contradictedMemoryId` | snake/camel naming | P2 |
| ID type | arbitrary `int` | decimal opaque `String` | `Long` | width mismatch | P0 if unchecked |
| no contradiction | `None` | `NOT_APPLICABLE` | `null` | exact semantic mapping needed | P0/P1 |
| unresolved/unavailable | not representable by `Optional[int]` alone | explicit status | not representable by `Long?` alone | current helper may collapse to null | **P0 SAFE_FIX** |
| overflow | unlimited | preserved | bounded signed 64-bit | explicit error required | P0, already guarded |

Current Assembling `AuthorityDecision` has:

```text
accepted
ownerResolved
sourceType
conflictStatus
reason
```

and lacks contradiction identity.

This remains a P0 integration gap. The bridge correctly refuses a PRESENT contradiction ID when converting to this DTO.

### 6.3 Memory preflight → Affective

| Native field | Type | MIP | Problem | Severity |
|---|---|---|---|---|
| `status` | `String` | `MipMemoryResultV1.status` | open string | P1/P2 |
| `memoryIds` | `List<String>` | same | compatible | — |
| `stableWrite` | `Boolean` | same | pre-response must always remain false | P0 invariant guarded by orchestrator |
| `reason` | `String` | same | diagnostic text not semantic authority | P2 |

Current public Affective boundary:

```text
relationshipSummary: String
affectiveSummary: String
persistentDeltaAllowed: Boolean
```

`relationshipSummary` is compatibility projection only and must never become RelationshipState authority.

## 7. MIP Bridge implementation audit

File:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

### PASS

- explicit mapping functions;
- no reflection/magic mapper;
- no business-policy decisions;
- explicit schema version;
- Python integer ID preserved as opaque decimal string;
- Kotlin Long overflow fails;
- current Assembling Authority refuses PRESENT contradiction ID rather than dropping it;
- primitive Authority wire map exists;
- existing round-trip tests cover major seams.

### SAFE FIX REQUIRED 1 — Entity resolution status vocabulary

MIP-1.0 defines:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

Current bridge reuses general `MipFieldStatus` and encodes resolved entities as `PRESENT`.

This is not business logic, but it is a canonical vocabulary mismatch.

Required safe fix:

```text
introduce MipEntityResolutionStatus
use RESOLVED for resolved identity
keep MipFieldStatus for field presence only
```

### SAFE FIX REQUIRED 2 — Complete explicit field statuses

MIP-1.0 general status vocabulary includes:

```text
NO_MATCH
ERROR
```

Current `MipFieldStatus` omits them.

Required safe fix: add the values so wire decoding cannot invent a second subset vocabulary.

### SAFE FIX REQUIRED 3 — no semantic collapse to nullable contradiction field

Current generic `presentOrNull()` allows:

```text
UNKNOWN
UNRESOLVED
UNAVAILABLE
```

to become native `null`.

For the contradiction seam, native `null` has one defined meaning:

```text
NO CONTRADICTION / NOT_APPLICABLE
```

Therefore converting `UNKNOWN`, `UNRESOLVED`, `UNAVAILABLE`, `AMBIGUOUS`, `CONFLICTED`, `NO_MATCH`, or `ERROR` to `Optional[int]`/`Long?` would be semantic loss.

Required safe fix:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> null/None
all other statuses -> MipContractException
```

### DEFERRED — Full MIP claim parity

`MipClaimV1` does not yet encode the complete conceptual MIP `TypedClaim` (`claimKind`, `modality`, full `TemporalRef`, full `ProvenanceRef`). Current native source DTOs also do not reliably provide them.

Do not invent values in this cleanup. Record as P1 and extend only when a real source/destination contract exists.

### DEFERRED — MatrixEnvelope integration

MIP requires an envelope at cross-module boundaries. The current bridge payloads are not yet wrapped in a runtime `MatrixEnvelope<T>` because orchestrator rewiring is explicitly outside this task.

Record as P1 integration work, not a cleanup blocker.

## 8. Legacy path disposition

### `contracts/MatrixAssemblyContracts.kt`

```text
KEEP_COMPATIBILITY
```

Reason: still used by the legacy pipeline/tests. Duplicate vocabulary is documented and quarantined.

### `pipeline/MatrixAssemblyPipeline.kt`

```text
KEEP_COMPATIBILITY / ALREADY DEPRECATED
```

Reason: compatibility regression test exists.

### `prompt/SemanticFrameToPrompt.kt`

```text
KEEP_COMPATIBILITY + ADD EXPLICIT DEPRECATION
```

Reason: same class name as canonical root prompt builder creates avoidable ambiguity.

### `coherence/CoherenceGuard.kt`

```text
KEEP_COMPATIBILITY + ADD EXPLICIT DEPRECATION
```

Reason: package/name appears canonical but implementation is exclusively based on legacy `contracts.*` DTOs.

No compatibility code is deleted in this task.

## 9. Structural cleanup decision

Preferred long-term conceptual layout:

```text
matrix/assembling/
    runtime/
    ports/
    adapters/
    mip/
    diagnostics/
    compatibility/
```

No mass move is authorized now.

Current safe policy:

```text
existing root runtime files = KEEP IN PLACE
existing module directories = KEEP
future module = dedicated directory/package mandatory
legacy paths = document/deprecate, do not move without caller migration proof
```

This removes ambiguity without generating package churn.

## 10. P0/P1/P2 after audit

### P0

1. Current root `AuthorityDecision` lacks contradiction identity required by future real memory-backed Authority/Admission. **REMAINING / DO NOT MODIFY IN THIS TASK.**
2. Full frozen/reference Python `AuthorityResolution` contract is not present in this repository; only the owner-provided contradiction field is cross-language grounded here. **REMAINING.**
3. Canonical contradiction status currently risks semantic collapse when non-`NOT_APPLICABLE` missing states are converted to nullable Python/Kotlin ID. **SAFE FIX IN THIS TASK.**

### P1

1. Bridge entity resolution vocabulary uses `PRESENT` instead of canonical `RESOLVED`. **SAFE FIX.**
2. Bridge general status vocabulary omits `NO_MATCH` and `ERROR`. **SAFE FIX.**
3. `TypedClaim` lacks dialogue act and explicit semantic marker. **REMAINING.**
4. Native null/sentinel values remain outside bridge. **REMAINING / ADAPTER CONTAINMENT.**
5. Predicate/dialogue/source/status values remain stringly typed. **REMAINING.**
6. `worldTruth: Boolean` remains compatibility representation. **REMAINING.**
7. Full `TemporalRef`, `ProvenanceRef`, `claimKind`, `modality`, `MatrixEnvelope<T>` not yet implemented at runtime boundary. **REMAINING / DEFERRED.**
8. Legacy duplicate vocabulary remains. **MITIGATE BY EXPLICIT DEPRECATION/QUARANTINE.**

### P2

1. Python snake_case vs Kotlin camelCase naming.
2. `MemoryAdmissionResult.status` stringly typed.
3. Semantic marker registry open string map.
4. Root runtime package is not yet split into `runtime/ports/diagnostics`, but moving it now has higher compatibility risk than benefit.

## 11. Safe-fix plan after audit checkpoint

Only the following changes are authorized by this audit:

```text
SAFE_ADAPTER
- separate EntityResolutionStatus from FieldStatus
- add NO_MATCH and ERROR to general MIP status enum
- strict contradiction nullable conversion
- add focused tests

SAFE_STRUCTURAL
- explicit @Deprecated annotations on legacy prompt/coherence classes
- no file moves/deletes

SAFE_DOCUMENTATION
- record dedicated-directory rule for every future module
- update repository map / continuity / canonical docs
```

No business logic, thresholds, runtime order, external module DTOs, Memory semantics, NLU semantics, Affective semantics, or orchestrator wiring may change.

## 12. Acceptance criteria

```text
MIP = one semantic cross-module authority
MipBridge = one interop adapter layer
MatrixTurnFrame = runtime frame, not competing protocol
future modules = dedicated directory/package
legacy path = explicitly quarantined/deprecated
silent semantic loss = blocked
existing tests = preserved
new strict conversion tests = PASS
full Gradle regression = PASS
CI = GREEN
other repositories modified = false
```

This task does not authorize Memory, Relationship, Reflection, BDI/Decision, real retrieval, Android integration, or orchestrator rewiring.