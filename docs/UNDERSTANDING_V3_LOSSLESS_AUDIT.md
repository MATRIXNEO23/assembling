# CP-U1 — Understanding V3 Lossless Contract Audit

Status: **COMPLETE / REQUIRES CP-U2 BEFORE IMPLEMENTATION**  
Repository: `MATRIXNEO23/assembling`  
Read-only source contract: `MATRIXNEO23/matrix-understanding-lab` / `MATRIX_NLU_CONTRACT_V3`  
Write scope: documentation only in `assembling`  

## 1. Objective

Determine whether frozen `MATRIX_NLU_CONTRACT_V3` can be transported through the current Assembling runtime and `MipClaimV1` without semantic loss.

No implementation change is authorized in CP-U1. This audit only classifies the current boundary and identifies exact contract gaps.

Classification vocabulary:

```text
LOSSLESS
REPRESENTABLE_WITH_EXISTING_MIP_FIELD
MISSING_FROM_MIP_RUNTIME
LEGACY_ONLY_LOSS
NOT_APPLICABLE_DOWNSTREAM
```

A field classified `REPRESENTABLE_WITH_EXISTING_MIP_FIELD` is transportable only when the current type actually preserves the frozen V3 semantics required for that field. If confidence/status/alternatives/identity are lost, the field is not considered globally lossless.

---

## 2. Frozen V3 ownership facts relevant to the audit

V3 is linguistic evidence only. It explicitly forbids NLU ownership of:

```text
worldTruth
memoryAdmission
authority
beliefConfidence
persistentConsent
persistentGoal
relationshipState
affectiveState
behaviorDecision
```

V3 independently represents:

```text
speaker
observer
source
subject
target
owner
perspective
```

`source != perspective` is a frozen design invariant.

V3 preserves plural evidence:

```text
subjectSpans[]
objectSpans[]
negationCueSpans[]
temporalEvidence[]
```

V3 preserves per-field:

```text
value
confidence
fieldStatus = RESOLVED | UNKNOWN | AMBIGUOUS | NOT_APPLICABLE
alternatives[]
```

V3 also preserves:

```text
claimKind
referent candidate IDs
temporal anchor identity
structuralStatus
interpretationStatus
overallInterpretationConfidence
multi-claim observation identity/provenance
```

---

## 3. Current Assembling surfaces audited

### 3.1 Legacy `MatrixNluClaim`

Current compatibility DTO contains:

```text
dialogueAct
predicate
polarity
temporalRelation
subjectReferent
targetReferent
ownerReferent
perspectiveReferent
confidence
confidenceByHead
sourceSpan?
subjectSpan?
objectSpan?
negationSpan?
temporalSpan?
subject?
target?
owner?
perspective?
objectValue?
sourceType?
worldTruth
adultOrIntimacy?
```

Critical V3 omissions include `sourceReferent`, `claimKind`, plural spans/evidence, candidate table, field statuses/alternatives, temporal anchor, structural/interpretation status and observation provenance.

### 3.2 Current `MipClaimV1`

Current canonical MIP claim contains:

```text
claimId
speaker
observer
source
subject
target
owner
perspective
predicate
objectValue
dialogueAct
polarity
temporalRelation
sourceType
interpretationConfidence
confidenceByField
sourceSpans: Map<String, MipSpan?>
epistemicClass
semanticMarkers
```

It has useful role/status primitives but does not yet model the complete frozen V3 evidence shape.

### 3.3 Current bridge behavior

`MipBridge.fromMatrixNluClaim(...)` currently:

```text
source = UNKNOWN unconditionally
subject/target/owner/perspective = projected from legacy fields
one span per named role only
worldTruth = mapped into epistemicClass WORLD_TRUTH
```

That bridge is therefore compatibility-only and cannot be the canonical V3 boundary.

---

## 4. Field-by-field audit

| V3 field / concept | Current representation | Classification | Loss / note |
|---|---|---|---|
| `contractVersion` | no explicit upstream NLU contract version in `MipClaimV1` | MISSING_FROM_MIP_RUNTIME | MIP schema version is not the NLU contract fingerprint/version. |
| observation input text | `MatrixTurnFrame.input.text` | LOSSLESS | Available at frame level; does not need duplication in every claim. |
| `observationSourceId` | no claim-level provenance field | MISSING_FROM_MIP_RUNTIME | Cannot prove exact observation origin from `MipClaimV1` alone. |
| `speakerRef` | `MipClaimV1.speaker` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Entity identity/status can be carried, but current legacy source may not provide full V3 candidate provenance. |
| `observerRef` | `MipClaimV1.observer` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Same limitation as speaker. |
| `mentions[]` | none | MISSING_FROM_MIP_RUNTIME | Mention IDs, types and spans are lost. |
| `referentCandidates[]` | none | MISSING_FROM_MIP_RUNTIME | Candidate IDs/kinds/resolution data lost. |
| `claimId` | `MipClaimV1.claimId` | LOSSLESS | Opaque string identity is preserved. |
| `sourceSpan` | `sourceSpans["source"]` | LOSSLESS | Singular claim source span is representable. |
| `subjectSpans[]` | `sourceSpans["subject"]` singular | LEGACY_ONLY_LOSS | V3 plural groups collapse to one span. |
| `objectSpans[]` | `sourceSpans["object"]` singular | LEGACY_ONLY_LOSS | V3 plural groups collapse to one span. |
| `negationCueSpans[]` | `sourceSpans["negation"]` singular | LEGACY_ONLY_LOSS | Double negation/negative concord evidence cannot survive losslessly. |
| `temporalEvidence[]` | `sourceSpans["temporal"]` singular | LEGACY_ONLY_LOSS | IDs, multiple spans and normalized evidence metadata are lost. |
| `entityMentionIds[]` | none | MISSING_FROM_MIP_RUNTIME | Claim-to-mention linkage lost. |
| `dialogueAct.value` | `dialogueAct: MipField<String>` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Value/status can be represented. |
| `dialogueAct.confidence` | `confidenceByField` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Numeric confidence can be carried by keyed map. |
| `dialogueAct.fieldStatus` | `MipField.status` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Status vocabulary is richer in MIP; V3 states can map without collapse. |
| `dialogueAct.alternatives[]` | none | MISSING_FROM_MIP_RUNTIME | Ranked ambiguity alternatives lost. |
| `predicate.value` | plain `String` | LEGACY_ONLY_LOSS | No native field status or alternatives; UNKNOWN/AMBIGUOUS cannot be represented as structured predicate state. |
| `predicate.confidence` | `confidenceByField` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Numeric confidence only. |
| `predicate.fieldStatus` / alternatives | none on predicate | MISSING_FROM_MIP_RUNTIME | Structured uncertainty lost. |
| `subjectReferent.value` | `MipEntityRef` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Resolved/unknown/not-applicable identity can be carried. |
| `subjectReferent.fieldStatus` | `MipEntityResolutionStatus` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | V3 RESOLVED/UNKNOWN/AMBIGUOUS/NOT_APPLICABLE maps conceptually. |
| `subjectReferent.candidateId` | not retained distinctly from entity ID | MISSING_FROM_MIP_RUNTIME | Mention candidate identity can be lost even when entity resolution is unresolved. |
| `subjectReferent.alternatives[]` | none | MISSING_FROM_MIP_RUNTIME | Ambiguity ranking lost. |
| `targetReferent` | `MipEntityRef` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Same partial support as subject. |
| `targetReferent.candidateId/alternatives` | none | MISSING_FROM_MIP_RUNTIME | Same loss. |
| `ownerReferent` | `MipEntityRef` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Same partial support as subject. |
| `ownerReferent.candidateId/alternatives` | none | MISSING_FROM_MIP_RUNTIME | Same loss. |
| `perspectiveReferent` | `MipEntityRef` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Role exists independently. |
| `perspectiveReferent.candidateId/alternatives` | none | MISSING_FROM_MIP_RUNTIME | Same loss. |
| `sourceReferent` | `MipClaimV1.source` exists | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | MIP has the role, but current `MatrixNluClaim` does not provide it. |
| V3 `sourceReferent` through current adapter | hardcoded `UNKNOWN` in `fromMatrixNluClaim` | LEGACY_ONLY_LOSS | Critical attribution identity is discarded. |
| `sourceReferent.candidateId/alternatives` | none | MISSING_FROM_MIP_RUNTIME | Candidate identity/ranking lost. |
| `polarity.value` | plain `String` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Value can be carried. |
| `polarity.confidence` | `confidenceByField` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Numeric confidence can be carried. |
| `polarity.fieldStatus/alternatives` | none on polarity | MISSING_FROM_MIP_RUNTIME | Structured uncertainty lost. |
| `temporalRelation.relation` | plain `String` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Relation value can be carried. |
| `temporalRelation.confidence` | `confidenceByField` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Numeric confidence can be carried. |
| `temporalRelation.fieldStatus` | none on relation | MISSING_FROM_MIP_RUNTIME | Structured state lost. |
| `temporalAnchorRef` | none | MISSING_FROM_MIP_RUNTIME | BEFORE/AFTER/DURING/AT_REFERENCE cannot preserve required anchor identity. |
| `claimKind.value` | no dedicated field; Authority already probes `semanticMarkers["CLAIM_KIND"]` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Chosen value could be carried as semantic marker. |
| `claimKind.confidence` | `confidenceByField` possible | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Numeric confidence possible. |
| `claimKind.fieldStatus` | marker uses `MipField` | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Status can be carried if standardized. |
| `claimKind.alternatives[]` | none | MISSING_FROM_MIP_RUNTIME | Ambiguity ranking lost. |
| `confidenceByField` | `confidenceByField` | LOSSLESS | Map of scalar confidences can be transported if keys remain exact. |
| `overallInterpretationConfidence` | `interpretationConfidence` | LOSSLESS | Numeric value is directly representable. |
| `fieldStatusByField` as a complete map | partial fields only | MISSING_FROM_MIP_RUNTIME | Many fields are plain String/span and cannot expose V3 status. |
| per-field `alternatives[]` | none | MISSING_FROM_MIP_RUNTIME | General V3 ambiguity evidence lost. |
| `structuralStatus` | none | MISSING_FROM_MIP_RUNTIME | VALID/INVALID does not survive canonical claim boundary. |
| `interpretationStatus` | none | MISSING_FROM_MIP_RUNTIME | RESOLVED/AMBIGUOUS/ABSTAINED does not survive. |
| V3 claim `diagnostics[]` | DiagnosticTrace exists at turn level, but no lossless V3 diagnostics payload/IDs in claim | MISSING_FROM_MIP_RUNTIME | Turn diagnostics do not reconstruct exact validator diagnostics per claim. |
| multi-claim list | `MatrixTurnFrame.typedClaims` / claim-wise MIP possible | REPRESENTABLE_WITH_EXISTING_MIP_FIELD | Cardinality can be preserved. |
| multi-claim original V3 claim IDs | current Understanding rewrites IDs as `${turnId}:claim:index` | LEGACY_ONLY_LOSS | Frozen decoder claim identity is not preserved as-is. |
| observation-level claim linkage/provenance | no canonical claim provenance object | MISSING_FROM_MIP_RUNTIME | Cannot trace each claim to exact observation/candidate table. |
| `worldTruth` | forbidden in V3; present in legacy `MatrixNluClaim` and mapped to `epistemicClass` | LEGACY_ONLY_LOSS | **P0 ownership drift**: NLU compatibility DTO can self-grant downstream epistemic authority. |
| `adultOrIntimacy` compatibility boolean | V3 treats domain semantics through ordinary predicates/evidence | LEGACY_ONLY_LOSS | Compatibility marker may remain legacy-only but must not become required V3 semantic ownership. |
| `sourceType` derived from dialogueAct fallback | legacy helper exists | LEGACY_ONLY_LOSS | V3 `claimKind`/`sourceReferent` must not be reconstructed heuristically from dialogue act. |
| `semanticSummary` free-text synthesis | legacy `SemanticFrame` | NOT_APPLICABLE_DOWNSTREAM | May remain diagnostic/prompt compatibility text, but cannot be canonical semantic authority. |

---

## 5. Critical loss findings

### P0-U1-01 — Legacy NLU can carry `worldTruth`

Frozen V3 explicitly forbids `worldTruth` in NLU output. Current `MatrixNluClaim` has:

```text
worldTruth: Boolean = false
```

and `MipBridge.fromMatrixNluClaim(...)` maps true to:

```text
epistemicClass = WORLD_TRUTH
```

This violates the frozen ownership boundary. V3 Understanding must not accept this as canonical NLU authority.

### P0-U1-02 — Independent `sourceReferent` is lost

Frozen V3 adds a dedicated source pointer head because `source != perspective`.

Current `MatrixNluClaim` has no `sourceReferent`/source entity. The current MIP bridge therefore writes:

```text
source = UNKNOWN
```

for every legacy Matrix NLU claim.

REPORT attribution cannot be canonical through this path.

### P0-U1-03 — Plural negation/temporal/object/subject evidence collapses

V3 requires all valid BIO groups to survive. Current runtime holds one nullable span for each category.

This loses double negation, negative concord, multiple objects/subjects and multiple temporal evidence spans.

### P0-U1-04 — Temporal anchor identity is absent

V3 requires an anchor for `BEFORE`, `AFTER`, `DURING`, `AT_REFERENCE`. Current `MipClaimV1` stores only a relation string.

Downstream cannot distinguish a valid anchored relation from an unanchored label without reparsing text, which is forbidden.

### P0-U1-05 — V3 abstention/structural validity does not survive

`structuralStatus` and `interpretationStatus` are absent from current `MipClaimV1`/root `TypedClaim`.

An `ABSTAINED` or `INVALID` claim could therefore be collapsed into an apparently ordinary claim unless a new canonical boundary preserves the status.

### P0-U1-06 — Ambiguity alternatives and candidate identity are absent

`MipEntityRef` can preserve entity-resolution state, but not the V3 candidate ID and ranked alternatives required to explain ambiguity and avoid guessed identity.

### P0-U1-07 — Current bridge synthesizes semantic source category from dialogue act

Current compatibility Understanding uses a fallback like:

```text
QUESTION/REQUEST -> TURN_INTENT
HYPOTHESIS -> HYPOTHESIS
else -> USER_ASSERTION
```

V3 separates `dialogueAct`, `claimKind` and `sourceReferent`. Canonical V3 must consume those fields directly rather than reconstructing them.

---

## 6. What is already reusable

The audit does **not** require discarding current work.

Reusable without semantic redesign:

```text
MipField / MipFieldStatus vocabulary
MipEntityRef / MipEntityResolutionStatus
MipSpan primitive
MatrixTurnFrame multi-claim/runtime MIP slots
DiagnosticTrace infrastructure
CanonicalAuthorityRuntimeAdapter
DeterministicAuthorityResolver
Authority reason-code system
MIP fail-closed philosophy
legacy adapters as quarantined compatibility paths
```

`MipClaimV1` itself can be extended/versioned rather than replaced blindly, provided CP-U2 preserves compatibility and one semantic owner.

---

## 7. CP-U1 verdict

```text
CP-U1 = PASS
AUDIT COMPLETE = YES
CURRENT MIP/RUNTIME LOSSLESS FOR MATRIX_NLU_CONTRACT_V3 = NO
CP-U3 DIRECT START = BLOCKED
CP-U2 REQUIRED = YES
```

Reason: the current runtime cannot represent frozen V3 semantics losslessly. The gap is structural, not a test-data or configuration issue.

At minimum CP-U2 must provide canonical representation for:

```text
upstream NLU contract identity/fingerprint
observation/claim provenance
mentions and referent candidate identity
plural subject/object/negation/temporal evidence
sourceReferent from V3
per-field status/confidence/alternatives where required
temporal anchor identity
claimKind canonical field/marker contract
structuralStatus
interpretationStatus
V3 claim diagnostics linkage
original V3 claim identity
```

and must prevent V3 NLU from supplying `worldTruth`/Authority ownership.

---

## 8. Stop condition / next action

Per owner rule, this audit does **not** autonomously start CP-U2.

```text
NEXT RECOMMENDED = CP-U2 — UNDERSTANDING/MIP CONTRACT EXTENSION
STATUS = AWAIT OWNER DISCUSSION/APPROVAL BEFORE CP-U2 IMPLEMENTATION
```

No orchestrator, Memory, `matrix-understanding-lab`, Student-5 training or other repository was modified by CP-U1.
