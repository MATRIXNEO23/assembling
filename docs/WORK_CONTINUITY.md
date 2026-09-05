# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T08:18+02:00  
Repository: `MATRIXNEO23/assembling`  
Branch: `main`  
Continuity schema: `matrix.assembling.continuity.v16`  
Integrated HEAD before this continuity commit: `dfd2da5ced902cf601530e65bb11c43e89dbd98a`  
PR `#7`: **MERGED** — explicit MIP bridge adapters and compatibility audit  
PR merge SHA: `dfd2da5ced902cf601530e65bb11c43e89dbd98a`  
Final post-merge main CI run: `33949320319` — `Matrix Assembling CI` — **SUCCESS**

## Canonical work rules

- work on one repository at a time unless the owner explicitly says otherwise;
- active repository for this architecture clarification/bridge checkpoint is `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- when a component changes, keep code, tests, active documents and continuity coherent in the same workstream;
- do not create parallel specifications when an active canonical document can be updated.

Owner scope for this checkpoint:

```text
Resolve universal cross-module ambiguity in Assembling first.
Create an adapter/universal language without modifying existing modules.
Do not use this as authorization to begin Memory or other deferred cognitive modules.
```

## Canonical universal protocol

Assembling owns:

```text
docs/MATRIX_INTERMODULE_PROTOCOL.md
version = MIP-1.0
```

Protocol creation commit:

```text
524eddd160fffae5425f06db2b7a44fe78abfb19
```

Original MIP documentation alignment:

```text
7f1f3b73555bb88e64a5a2f9e01cc5c0cf2846ac  docs index / MIP authority
df8803c81059e58eb77b92c33e8095ed5c871754  module wiring aligned to MIP
2c20e0b4dce5b061bc07dbee250586ffa4462990  assembly plan aligned to MIP
bc58f21eecf4a7d29930a451c9be0a015d8a3796  memory integration policy aligned
f25185e1529c55eeb91bf499fe3acb25635e8d71  memory integration status aligned
10cea71af56427b81aa15c7bfcdf3be38823a4ee  canonical MIP architecture checkpoint
```

## MIP Bridge — INTEGRATED / TESTED

Implementation:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

Audit:

```text
docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md
```

Tests:

```text
src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt
```

PR history:

```text
PR #7 = MERGED
branch = mip-bridge-v1
base = 10cea71af56427b81aa15c7bfcdf3be38823a4ee
final PR head = f7a1b9ff81c46072bbd9b8fa7ade0ab8232bd662
merge SHA = dfd2da5ced902cf601530e65bb11c43e89dbd98a
```

Bridge commits:

```text
4754854b106131c7e368641ece662f09ba4605f3  initial bridge
c50118f992b2c269191bbd157193a26eaed6695b  compile-safe explicit mappings
bfd50eefc1f9d30eb327061762f4dc3cdc69ed60  round-trip/incompatibility tests
d3e6c254baabf18795fa0a1d7b7dc6dfd2149bb9  field-level compatibility audit
a70e75d4ce82de125ceae1a6ab4bd7ee26d0a6b3  docs index alignment
f7a1b9ff81c46072bbd9b8fa7ade0ab8232bd662  tested bridge continuity checkpoint
```

CI evidence:

```text
PR run 33949127818 = SUCCESS
PR final-head run 33949261802 = SUCCESS
main post-merge run 33949320319 = SUCCESS
```

Hard result:

```text
existingModuleDTOsModified = false
orchestratorRewired = false
businessLogicAddedToBridge = false
reflectionMagicUsed = false
crossRepositoryWrites = false
roundTripTests = PASS
missingFieldFailClosed = PASS
lossyConversionFailClosed = PASS
KotlinLongOverflowFailClosed = PASS
fullExistingRegressionSuite = PASS
```

## Bridge mappings integrated

```text
MatrixNluClaim <-> MipClaimV1
Assembling TypedClaim <-> MipClaimV1
Assembling CoherenceDecision <-> MipCoherenceDecisionV1
Assembling AuthorityDecision <-> MipAuthorityResolutionV1
owner-provided Python AuthorityResolution contradiction field <-> MipAuthorityResolutionV1
MipAuthorityResolutionV1 -> Kotlin Memory contradictedMemoryId wire
Assembling MemoryAdmissionResult <-> MipMemoryResultV1
Assembling AffectiveState <-> MipAffectiveSnapshotV1
```

For the concrete cross-language incompatibility:

```text
Python reference field:
contradicts_memory_id: Optional[int]

MIP:
contradictedMemoryId: MipField<String>

Kotlin Memory boundary:
contradictedMemoryId: Long?
```

MIP treats the memory identifier as an opaque decimal string at the language-neutral boundary. This preserves Python integer identity and makes Kotlin `Long` range checking explicit. Overflow is an explicit adapter error, never truncation.

Current Assembling `AuthorityDecision` does not contain contradiction identity. Therefore:
- `fromAssemblingAuthorityDecision()` marks it `UNAVAILABLE`;
- conversion from canonical MIP back to current Assembling Authority fails if a contradiction ID is `PRESENT`;
- the bridge never silently discards that field.

## Canonical semantic invariants

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS

TypedClaim ≠ Belief
Belief ≠ Memory
Memory ≠ State
State ≠ Context
Relationship ≠ Affective
SexualInterest ≠ CurrentDesire
CurrentDesire ≠ Consent
Contradiction ≠ Supersession
InterpretationConfidence ≠ SourceReliability
SourceReliability ≠ Authority
Authority ≠ BeliefConfidence
BeliefConfidence ≠ RetrievalRelevance
```

Shared roles remain globally distinct:

```text
speaker
observer
source
subject
target
owner
perspective
```

Shared missing/resolution semantics remain distinct:

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

No public module boundary may redefine these terms privately.

## Context model — canonical design

```text
MatrixContextSnapshot
→ immutable/read-only
→ versioned by snapshotId / parentSnapshotId
→ typed ContextEntry values
→ explicit domain availability
```

Reserved domains:

```text
LINGUISTIC
WORLD
MEMORY
BELIEF
RELATIONSHIP
AFFECTIVE
INTIMACY
GOAL
SYSTEM
```

Missing/non-wired domains must never be represented using fake zero/default values.

Current `MatrixTurnFrame` remains an implementation precursor/compatibility surface; it has not been rewritten.

## Memory retrieval rule — canonical design

```text
EVERY NORMAL TURN
→ LIGHTWEIGHT MEMORY INDEX PROBE
```

Retrieval levels:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — only on relevant hits
LEVEL 3 DEEP_OR_MULTI_HOP — only for explicit complex retrieval purpose
```

Retrieval is universal, not Reflection-specific. Real retrieval remains `NON_CABLATO`.

## Relationship / Affective / Intimacy separation

```text
RELATIONSHIP
= relatively stable directional relationship state

AFFECTIVE
= current/short-lived emotional state

INTIMACY
= current contextual consent/boundary/coercion/roleplay state
```

Hard invariant:

```text
sexualInterest HIGH + currentDesire HIGH != consent GRANTED
```

Adult/intimacy remains first-class semantics, not automatic censorship, confidence penalty, Memory penalty or Affective penalty.

## Memory semantic direction

Semantic Memory kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

`REFLECTION` is a future-compatible record kind; no Reflection module is assumed to exist today.

Not Memory kinds:

```text
RELATIONSHIP
AFFECTIVE
GOAL
INTIMACY
```

Core/Recall/Archival, if used later, are access/retention tiers rather than semantic Memory identity.

Pre-response:

```text
MemoryCandidate
= ephemeral TurnWorkspace data
!= MemoryRecord
```

Durable Memory:

```text
VALIDATE
→ PersistentConsolidationPort
→ Memory Admission
→ MemoryRepository
```

## Contradiction / supersession

```text
CONTRADICTION != SUPERSESSION
```

Authority/Belief resolution identifies explicit semantic contradiction identity. Memory Admission consumes that decision and must not infer conflict merely from different text, shared actors or unrelated predicates.

Temporal change is not contradiction by default.

Semantic durable modification uses `supersede()` and preserves lineage.

## Memory Foundation migration policy — PRESERVED

```text
Python Memory Foundation v3
= frozen reference/oracle
        ↓ contract parity
Kotlin / Room Memory Foundation
= future production implementation
        ↓ fault injection + regression gates
PersistentConsolidationPort
= only allowed durable runtime integration boundary
```

Preserve:
- `revisionOf` points to lineage root;
- `supersededBy` is sequential revision chain;
- semantic changes use `supersede()`;
- contradiction identity is explicit from Authority/Belief resolution;
- Memory Admission owns `SAVE/SUPERSEDE/REJECT/IGNORE` after validation;
- atomic rollback and lineage protection require fault-injection tests;
- destructive migration is not an accepted normal fallback;
- Python v3 remains frozen oracle until Kotlin/Room parity is proven.

Exact Room entities/DAO/index/storage details remain to be designed/audited in the Memory repository against MIP.

## Residual MIP Bridge risks

### P0

1. Current Assembling `AuthorityDecision` lacks contradiction identity required for real memory-backed Authority/Admission. The bridge prevents silent loss but does not alter that existing DTO.
2. The complete frozen/reference Python `AuthorityResolution` source contract is not stored in Assembling. Only the owner-provided `contradicts_memory_id` field is cross-language-grounded by this checkpoint. Before production wiring, audit the actual reference source.

### P1

1. `TypedClaim` does not independently carry dialogue act or the explicit semantic-domain marker; those exist elsewhere in current frame/NLU state.
2. Native `null` / `UNKNOWN` / `NONE` representations still exist outside the MIP boundary.
3. Predicate/source/status vocabularies remain mostly stringly typed.
4. `worldTruth: Boolean` remains a compatibility representation in current NLU/TypedClaim types.
5. Deprecated `contracts/*` remains a duplicate vocabulary and must stay compatibility-only.
6. Primitive wire codec currently targets the highest-risk Authority seam; add more codecs only when a real cross-process boundary requires them.

### P2

1. Python snake_case vs Kotlin camelCase remains a native naming difference handled explicitly by adapters.
2. `MemoryAdmissionResult.status` remains a string.
3. Semantic marker registry should later be versioned with PredicateId registry.

## Canonical runtime direction

```text
Input
→ NLU / Understanding
→ TurnWorkspace / MatrixContextSnapshot
→ ENRICH
   ├─ Memory index probe — ALWAYS
   ├─ Relationship read when wired
   ├─ Affective read when wired
   ├─ World read when wired
   └─ other registered reads when wired
→ Coherence / Authority / Belief resolution
→ MemoryPreflightPort
→ Affective appraisal
→ Matrix decision layer
→ Prompt / GGUF
→ OutputValidatorPort
→ PersistentConsolidationPort
```

Logical stages may use parallel reads where dependencies allow. Missing phases remain explicitly `NON_CABLATO` / `NOT_WIRED`.

## Existing hardening preserved

Completed and still regression-tested:
- all NLU claims preserved;
- critical confidence fail-closed on every claim;
- unresolved subject remains unresolved;
- third-party reports do not become direct authority;
- multi-claim turns cannot masquerade as one direct authority;
- pre-response Memory is preflight only;
- pre-response durable Memory output is rejected;
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed authorization;
- `DiagnosticTrace.firstDivergence` is write-once;
- Prompt Builder is realization-only;
- legacy pipeline is compatibility-only/deprecated;
- Output Validator boundary is after GGUF;
- durable persistence boundary is `PersistentConsolidationPort`;
- adult/intimacy NLU marker remains first-class.

## Still NON_CABLATO / NOT IMPLEMENTED

- full formal `MatrixEnvelope<T>` runtime integration;
- full typed `TemporalRef` / `ProvenanceRef` runtime integration;
- typed confidence wrappers across existing native DTOs;
- Predicate registry implementation;
- explicit `TurnWorkspace` migration;
- runtime `MatrixContextSnapshot` / `ContextEntry`;
- read-only context ports;
- real always-on Memory index probe;
- real Long-Term hydrate/rerank retrieval;
- per-claim contextual Authority/Belief resolution;
- real BeliefState;
- real semantic `OutputValidatorPort` implementation;
- real `PersistentConsolidationPort` implementation;
- production Memory Foundation adapter and atomic commit;
- canonical RelationshipState controller;
- canonical Intimacy/Consent resolver;
- BDI-lite + Utility Decision layer;
- real llama.cpp/MLC GGUF bridge;
- Android integration;
- Reflection implementation.

## Current checkpoint / STOP boundary

MIP-1.0 plus the explicit MIP Bridge adapter layer are now integrated on `main` and passed the full post-merge CI suite.

Do NOT automatically start:

```text
orchestrator rewiring to MIP Bridge
full Kotlin MIP rewrite
Working Context implementation
Memory retrieval implementation
Memory persistence
Relationship
Affective redesign
Intimacy/Consent implementation
Decision/BDI
Reflection
```

## Next repository/work decision

The next architecture move remains owner-controlled.

If explicitly switching to `MATRIXNEO23/memoria`:
- treat its existing README as historical/approximate where it conflicts with MIP;
- design Memory Foundation against `MIP-1.0` + the integrated bridge semantics;
- do not modify Assembling while Memory is active;
- preserve the always-on lightweight index-probe requirement;
- preserve post-validation durable write boundary;
- audit the complete frozen Python `AuthorityResolution` contract before relying on fields beyond the owner-provided contradiction ID;
- do not implement Reflection merely because `REFLECTION` exists as a Memory kind.

## Exact restart rule

If returning to Assembling implementation later:

```text
DO NOT restart the architecture audit.
DO NOT redo completed P0/P1/P2 hardening.
DO NOT invent another context/protocol model.
DO NOT bypass MIP Bridge with ad-hoc cross-module field mapping.
Start from MIP-1.0 + merged PR #7 (`dfd2da5c...`) and migrate runtime incrementally only when explicitly authorized.
```