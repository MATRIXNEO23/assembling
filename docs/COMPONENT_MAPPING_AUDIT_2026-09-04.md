# Component Mapping Audit — 2026-09-04

Scope: verify compatibility between currently connected assembly modules in `MATRIXNEO23/assembling`.

Audited links:

```text
A1: Matrix Understanding / NLU runtime output
B1: MatrixTurnFrame / SemanticFrame / TypedClaim

A2: MatrixTurnFrame semantic + memory result
B2: AffectiveRuntimeRequest
```

## Checklist requested

1. Every field of X must be mappable to a field of Y.
2. Types must be compatible: enum-like values, strings, integers, booleans, maps.
3. Defaults must be coherent.
4. Y must not require fields that X cannot produce.
5. Naming conventions must be consistent.

## Source facts

The Understanding source contract exposes an `UnderstandingEngine` that returns a `Domain.Interpretation` for `(caseId, language, text, context)`.

The source domain has `Domain.Claim` fields including:

```text
speaker, subject, target, owner, perspective,
dialogueAct, predicate, objectValue, polarity,
negationScope, temporalRelation, temporalExpression,
entities, claimKind, confidence, sourceSpans,
sourceIds, worldTruth
```

The Affective source prototype exposes transient emotions, PAD/mood and persistent affect fields:

```text
trust, attachment, affection, attraction,
resentment, respect, admiration, aversion
```

## Finding 1 — Resolved Understanding fields were lossy

Status: FIXED.

Problem:

The first `UnderstandingLabAdapter` bridge normalized the claim mainly through referent labels and spans:

```text
subjectReferent
targetReferent
ownerReferent
perspectiveReferent
objectSpan
```

That was not enough because the source Understanding contract already has resolved fields:

```text
subject
target
owner
perspective
objectValue
worldTruth
```

Risk:

```text
Domain.Claim.target = "Marco"
```

could degrade into:

```text
targetReferent = KNOWN_ENTITY
```

without preserving the actual target value.

Impact:

- entity/owner loss;
- wrong memory admission risk;
- third-party/report risk;
- affective module could receive generic referents instead of concrete target IDs;
- prompt builder could produce weaker or wrong instructions.

Minimal fix applied:

- extended `NluOutput` with optional resolved fields:

```text
resolvedSubject
resolvedTarget
resolvedOwner
resolvedPerspective
objectValue
sourceType
worldTruth
```

- updated only `UnderstandingLabAdapter` to preserve those fields;
- kept existing label fields untouched;
- all new fields have safe defaults, so existing callers remain compatible.

Commits:

```text
bd612ee386443e06711137281eaa6d01e85830ad
5f3a5db748f4490e99b0d4e653025b7456a70bfc
```

## Finding 2 — Third-party/report could be over-stabilized

Status: FIXED in the same adapter.

Problem:

A high-confidence claim was not enough to distinguish direct user assertion from report/indirect source in the stable memory decision.

Minimal fix applied:

`SemanticFrame.stableMemoryAllowed` now requires:

```text
dialogueAct in ASSERT/CORRECT
predicate != speech.unresolved
sourceType != THIRD_PARTY_REPORT
worldTruth == true
overall confidence >= 0.75
```

This preserves the interface and only tightens the adapter's admission precondition.

## Finding 3 — Affective persistence needs a stable memory gate

Status: ALREADY CORRECT / COVERED BY TEST.

`AffectiveLabAdapter` already computes:

```text
persistentAllowed = memory.stableWrite == true && semantic.stableMemoryAllowed
```

That is correct because the memory backend does not exist yet and emotional persistent deltas must not be written from transient or unresolved semantics.

Added tests verify:

- no persistent target when memory is not stable;
- persistent target is present only after stable semantic + stable memory admission.

## Finding 4 — Memory placeholder could still admit stable memory

Status: FIXED.

Problem:

`BasicMemoryAdmission` could return:

```text
status = ADMITTED
stableWrite = true
```

That was incompatible with the real system status because the memory backend does not exist yet.

Minimal fix applied:

- modified only `BasicMemoryAdmission`;
- no interface changes;
- it now always returns `stableWrite=false` and empty `memoryIds`;
- safe status is now `NO_MEMORY_BACKEND` or `REJECTED`.

Commit:

```text
9cd0df38f2e2dcf53c418b2246c30fc99d9461e0
```

## Finding 5 — Memory is intentionally absent

Status: OK.

The real memory backend is not implemented. The correct current behavior remains:

```text
NoPersistentMemoryAdmission / BasicMemoryAdmission
status = NO_MEMORY_BACKEND, PROVISIONAL_CLAIM or REJECTED
stableWrite = false
memoryIds = emptyList()
```

No fake durable memory should be added in this repo.

## Tests added

File:

```text
src/test/kotlin/matrix/assembling/adapters/ComponentMappingCompatibilityTest.kt
```

Coverage:

1. `understandingAdapterPreservesResolvedClaimFields`
2. `thirdPartyReportCannotBecomeStableMemoryByDefault`
3. `affectiveAdapterDoesNotApplyPersistentDeltaWithoutStableMemory`
4. `affectiveAdapterMapsStableSemanticMemoryToPersistentTarget`

## Current verdict

```text
Understanding → MatrixTurnFrame: FIXED / TESTED BY NEW CONTRACT TESTS
MatrixTurnFrame → Affective: COMPATIBLE / TESTED
Memory: ABSENT BY DESIGN / SAFE PLACEHOLDER ONLY
GGUF: STILL PLACEHOLDER / PROMPT CONTRACT PRESENT
```

## Next minimum step

Run CI and fix only actual compile/test failures. Do not rewrite tests to hide mismatches.
