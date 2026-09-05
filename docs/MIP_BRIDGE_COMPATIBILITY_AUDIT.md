# MIP Bridge — Compatibility Audit

Status: IMPLEMENTATION CANDIDATE / PR VALIDATION PENDING  
Date: 2026-09-05  
Repository: `MATRIXNEO23/assembling`  
Canonical semantic source: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)

## 1. Scope

This audit defines the adapter-only layer between existing Matrix module DTOs and MIP-1.0.

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

Existing modules are not modified by this workstream.

## 2. Current authoritative runtime boundaries

The authoritative Assembling path is `MatrixTurnFrame` + ports. Current boundary payloads are:

| Producer / boundary | Native output | Consumer / boundary | Native input |
|---|---|---|---|
| caller/perception | `UserMessage` | `NluPort` | `MatrixTurnFrame.input` |
| Matrix-NLU runtime bridge | `MatrixNluInterpretation` / `MatrixNluClaim[]` | `UnderstandingLabAdapter` | `MatrixNluClaim` |
| NLU adapter | `NluOutput`, `TypedClaim[]` | Understanding | `MatrixTurnFrame.nlu`, `typedClaims` |
| Understanding | `SemanticFrame`, `TypedClaim[]` | Coherence | `semantic`, `typedClaims` |
| Coherence | `CoherenceDecision` | Authority | `coherenceDecision` |
| Authority | `AuthorityDecision` | Memory preflight | `authorityDecision` |
| Memory preflight | `MemoryAdmissionResult` | Affective + orchestrator boundary guard | `memoryResult` |
| Affective | `AffectiveState` | Prompt Builder | `affectiveState` |
| Prompt Builder | `GgufPrompt` | GGUF | `prompt` |
| GGUF | `AssistantReply` | Output Validator | `reply` |
| Output Validator | updated `MatrixTurnFrame` | caller / future consolidation | updated frame |
| Persistent Consolidation | `NON_CABLATO` | durable owners | `NON_CABLATO` |

Internal lab bridge DTOs also exist:
- `MatrixNluRequest`;
- `AffectiveRuntimeRequest`;
- `AffectiveImpulse`;
- `AffectiveRuntimeOutput`;
- `PersistentAffectSnapshot`.

These are implementation-facing lab DTOs, not independent semantic authorities.

## 3. Compatibility path

`src/main/kotlin/matrix/assembling/contracts/*` remains deprecated compatibility-only.

It contains a second set of types:
- `contracts.SemanticFrame`;
- `DialogueAct`;
- `Predicate`;
- `Polarity`;
- `TemporalRelation`;
- `Referent`;
- `Confidence`;
- `AdultIntimacyMarker`;
- `contracts.CoherenceDecision`;
- `CoherenceStatus`;
- `RelationshipState`;
- `contracts.AffectiveState`;
- `FilteredMemorySummary`;
- `PromptDirective`.

MIP Bridge does not make these types canonical. Compatibility adapters may later map them explicitly if a real caller still requires them. No new runtime path may derive semantic authority from this deprecated contract family.

## 4. Field-level incompatibilities — NLU / Understanding

### 4.1 `MatrixNluClaim` → current `TypedClaim`

| Semantic | MatrixNluClaim | TypedClaim | Incompatibility |
|---|---|---|---|
| claim identity | absent | `claimId: String` | destination requires generated external ID |
| dialogue act | `dialogueAct: String` | absent | information lost in `TypedClaim` alone |
| predicate | `String` | `String` | compatible |
| polarity | `String` | `String` | compatible |
| temporal | `String` | `String` | compatible name/type; enum domain not enforced |
| subject referent | `subjectReferent: String` | absent | symbolic referent token lost after resolution |
| target referent | `targetReferent: String` | absent | same |
| owner referent | `ownerReferent: String` | absent | same |
| perspective referent | `perspectiveReferent: String` | absent | same |
| resolved subject | `subject: String?` | `subject: String` | nullability mismatch; `TypedClaim` uses `UNKNOWN` sentinel |
| resolved target | `target: String?` | `target: String?` | compatible nullable shape |
| resolved owner | `owner: String?` | `ownerId: String?` | name mismatch |
| resolved perspective | `perspective: String?` | `perspective: String?` | compatible |
| object | `objectValue: String?` | `objectValue: String?` | compatible nullable shape but null semantics not explicit |
| confidence overall | `confidence: Double` | map key `overall` | structural mismatch |
| confidence heads | `confidenceByHead` | `confidence` map | name mismatch |
| spans | five `List<Int>?` fields | `Map<String, TextSpan?>` | type/shape mismatch |
| source type | `sourceType: String?` | `sourceType: String` | nullability mismatch |
| world truth marker | `worldTruth: Boolean` | `worldTruth: Boolean` | compatible representation, but legacy semantic risk retained |
| adult/intimacy | `adultOrIntimacy: Boolean?` | absent | marker lost in `TypedClaim` alone |

MIP response:
- `MipClaimV1` preserves both resolved entity ID and source referent token (`surfaceForm`);
- absence/unresolved status is explicit;
- overall confidence and per-field confidence are separate;
- spans normalize to `MipSpan`;
- adult/intimacy is preserved through a generic semantic marker;
- `worldTruth` maps to an epistemic-class declaration only; it does not grant accepted authority.

## 5. Field-level incompatibilities — Authority / Memory

### 5.1 Owner-provided Python reference → Kotlin Memory boundary

Known incompatibility supplied by the project owner:

```text
Python AuthorityResolution
contradicts_memory_id: Optional[int]

Kotlin Memory AuthorityDecision
contradictedMemoryId: Long?
```

| Property | Python | Kotlin Memory | MIP canonical |
|---|---|---|---|
| name | `contradicts_memory_id` | `contradictedMemoryId` | `contradictedMemoryId` |
| numeric type | arbitrary-size Python `int` | signed 64-bit `Long` | opaque decimal `String` ID |
| nullable | `Optional` | nullable | explicit `MipField` status |
| no contradiction | `None` | `null` | `NOT_APPLICABLE` |
| overflow behavior | unlimited integer | overflow / impossible | explicit conversion error |

Why canonical ID is a string:
- MIP identifiers are opaque, not arithmetic values;
- JSON/JavaScript and cross-language integer widths differ;
- decimal string preserves Python integer identity exactly;
- destination adapters may explicitly validate `Long` representability.

No business logic is involved in this conversion.

### 5.2 Current Assembling `AuthorityDecision`

Current fields:

```text
accepted: Boolean
ownerResolved: Boolean
sourceType: String
conflictStatus: String
reason: String
```

Missing relative to Memory Foundation direction:

```text
contradictedMemoryId
```

This is a P0 representation gap for future contradiction-backed Memory Admission.

Bridge behavior:
- `fromAssemblingAuthorityDecision()` marks contradiction ID `UNAVAILABLE`;
- `toAssemblingAuthorityDecision()` succeeds only when the canonical contradiction ID is not present;
- if a canonical contradiction ID is present, conversion fails with `MipContractException` rather than silently dropping it;
- `toKotlinMemoryAuthorityDecision()` preserves the ID when representable as `Long`.

The bridge does **not** modify current `AuthorityDecision`.

## 6. Field-level incompatibilities — Coherence

Current authoritative `CoherenceDecision` is a Kotlin enum:

```text
SAFE_TO_ADMIT
SAFE_TRANSIENT_ONLY
LOW_CONFIDENCE_HOLD
REPORT_ONLY
QUESTION_ONLY
CONFLICT_REQUIRES_REVIEW
REJECTED_UNSAFE
```

Deprecated compatibility `CoherenceStatus` uses:

```text
SafeToUseForReply
TransientOnly
LowConfidence
QuestionOnly
ReportOnly
ConflictRequiresReview
RejectedUnsafe
```

Issues:
- different enum names/casing;
- compatibility contract also carries `stableMemoryAllowed` and `persistentAffectAllowed` booleans;
- authoritative enum is decision classification only;
- persistence authorization has moved to stricter downstream boundaries.

MIP Bridge maps the authoritative enum by exact stable symbolic name. It does not infer persistence booleans.

## 7. Field-level incompatibilities — Memory preflight

Current `MemoryAdmissionResult`:

```text
status: String
memoryIds: List<String>
stableWrite: Boolean
reason: String
```

Current pre-response invariant:

```text
stableWrite = false
memoryIds = []
```

MIP `MipMemoryResultV1` mirrors these boundary facts without changing their meaning.

Risk:
- `status` remains an open string rather than a versioned enum;
- this is acceptable for a compatibility bridge but should be formalized before durable Memory is wired.

The adapter does not turn preflight into persistence.

## 8. Field-level incompatibilities — Affective / Relationship

Current public `AffectiveState`:

```text
relationshipSummary: String
affectiveSummary: String
persistentDeltaAllowed: Boolean
```

`relationshipSummary` is explicitly compatibility-only. Canonical RelationshipState is externally owned.

Internal `AffectiveRuntimeOutput` also exposes:
- emotions map;
- valence;
- arousal;
- dominance;
- moodValence;
- persistent affect map;
- `persistentDeltaApplied`;
- compatibility relationship summary.

The current `AffectiveLabAdapter` contains actual appraisal/business mapping from semantic input to affective impulse. That logic must **not** be copied into MIP Bridge.

MIP Bridge therefore round-trips the public `AffectiveState` boundary only. Numeric appraisal mapping remains owned by the existing Affective adapter/module.

## 9. Prompt / GGUF / validation

`GgufPrompt(text)` and `AssistantReply(text, diagnosticTrace)` are already explicit boundary DTOs in the authoritative Assembling path.

No cross-language incompatibility requiring a new adapter was demonstrated in the current repository.

`OutputValidatorPort` and `PersistentConsolidationPort` operate on `MatrixTurnFrame`; the real implementations remain `NON_CABLATO`.

No speculative destination DTO is invented for them in this task.

## 10. Canonical bridge contract

The implementation lives in:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

### 10.1 Presence / missing semantics

```text
MipField<T>
- status
- value
```

Statuses:

```text
PRESENT
NOT_APPLICABLE
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
```

Hard invariant:

```text
PRESENT -> value required
other status -> value forbidden
```

This prevents one native `null` from silently representing multiple meanings.

### 10.2 Claim payload

`MipClaimV1` contains only evidence already required by MIP/current NLU boundaries:
- claim identity;
- speaker/observer/source/subject/target/owner/perspective references;
- predicate/object;
- dialogue act;
- polarity;
- temporal relation;
- source type;
- interpretation confidence;
- field confidence;
- spans;
- epistemic class declaration;
- semantic markers required to preserve existing first-class domains such as adult/intimacy.

The semantic marker map is justified by existing incompatible fields:

```text
NluOutput.adultOrIntimacy: Boolean?
MatrixNluClaim.adultOrIntimacy: Boolean?
legacy AdultIntimacyMarker: enum
```

It is generic rather than introducing an adult-only protocol special case.

### 10.3 Authority payload

`MipAuthorityResolutionV1` contains:
- accepted;
- ownerResolved;
- sourceType;
- conflictStatus;
- contradictedMemoryId;
- reason.

Each field has explicit presence status because the owner-provided Python example only guarantees the contradiction field, while current Assembling Authority guarantees the other five but lacks the contradiction ID.

### 10.4 Serialization

Canonical DTOs are plain Kotlin data classes with language-neutral field meanings.

For the cross-language Authority seam, MIP Bridge additionally provides an explicit primitive-only representation:

```text
authorityToWireMap()
authorityFromWireMap()
```

The resulting structure contains only:
- strings;
- booleans;
- null;
- nested maps.

It is directly representable as JSON object / Python dict / Kotlin map / Java map without reflection-based field discovery.

## 11. Explicit adapters implemented

```text
fromMatrixNluClaim()
toMatrixNluClaim()

fromAssemblingTypedClaim()
toAssemblingTypedClaim()

fromAssemblingCoherenceDecision()
toAssemblingCoherenceDecision()

fromAssemblingAuthorityDecision()
toAssemblingAuthorityDecision()

fromPythonAuthorityResolution()
toPythonAuthorityResolution()

toKotlinMemoryAuthorityDecision()

fromAssemblingMemoryResult()
toAssemblingMemoryResult()

fromAssemblingAffectiveState()
toAssemblingAffectiveState()
```

No reflection, bean-copy framework or implicit field-name mapper is used.

## 12. Tests

`src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt` covers:
- Matrix-NLU claim round-trip;
- current TypedClaim round-trip;
- Python Authority round-trip;
- Python contradiction ID → Kotlin Memory ID translation;
- `None/null` contradiction semantics;
- current Assembling Authority round-trip;
- fail-closed loss prevention when current Authority cannot represent contradiction ID;
- Kotlin Long overflow rejection;
- primitive wire-map round-trip;
- missing field rejection;
- illegal value-with-non-PRESENT-status rejection;
- MemoryResult round-trip;
- AffectiveState round-trip;
- Coherence enum round-trip.

Existing repository tests remain the regression gate for unchanged modules.

## 13. Residual risks

### P0

1. **Current Assembling AuthorityDecision lacks contradiction identity.**
   - MIP Bridge prevents silent loss, but real memory-backed Authority cannot be wired through this DTO without an adapter/destination contract that carries the ID.
   - Do not add the field to the existing module in this task; resolve during authorized contextual Authority integration.

2. **Real Python AuthorityResolution source contract is not stored in this repository.**
   - The only cross-language field guaranteed in this audit is the owner-provided `contradicts_memory_id: Optional[int]`.
   - Additional Python fields must be audited against the actual frozen/reference source before production wiring.

### P1

1. `TypedClaim` lacks dialogue act and explicit semantic-domain marker; information exists elsewhere in `MatrixTurnFrame`/`NluOutput`.
2. Native nulls/sentinel strings still exist outside MIP Bridge (`null`, `UNKNOWN`, `NONE`).
3. Current predicate/dialogue/source/status values are mostly open strings rather than versioned registries/enums.
4. `worldTruth: Boolean` remains a compatibility representation in NLU/TypedClaim even though MIP uses epistemic/source authority semantics.
5. Legacy `contracts/*` defines duplicate enum/type vocabularies and should remain quarantined as compatibility-only.
6. Only Authority currently has an explicit primitive wire-map codec in this implementation candidate; other MIP DTOs are data-class contracts and should receive wire codecs when a real cross-process boundary needs them.

### P2

1. Naming style differs between Python snake_case and Kotlin camelCase; explicit adapters make this harmless but generated schemas would improve tooling.
2. `MemoryAdmissionResult.status` remains stringly typed.
3. Semantic marker registry is currently an open string map and should eventually be versioned together with PredicateId registry.

## 14. Acceptance criteria

This adapter checkpoint is acceptable only if:

```text
existing modules modified = false
business logic added to MIP Bridge = false
round-trip tests = PASS
missing-field tests = PASS
lossy conversion tests = PASS/explicit error
overflow test = PASS/explicit error
existing Assembling regression suite = PASS
MIP-1.0 semantics preserved = true
other repositories modified = false
```

This task does not authorize:
- Memory implementation;
- Relationship implementation;
- Reflection implementation;
- BDI/Decision implementation;
- rewiring the orchestrator to MIP Bridge;
- modifying Matrix-NLU or Python Memory reference sources.
