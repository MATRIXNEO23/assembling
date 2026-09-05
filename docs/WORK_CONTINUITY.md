# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T08:14+02:00  
Repository: `MATRIXNEO23/assembling`  
Branch: `main` with PR `#7` head `mip-bridge-v1` pending merge at this checkpoint  
Continuity schema: `matrix.assembling.continuity.v15`  
Current tested PR HEAD before this continuity commit: `a70e75d4ce82de125ceae1a6ab4bd7ee26d0a6b3`  
PR `#7`: OPEN — explicit MIP bridge adapters and compatibility audit  
PR CI run `33949127818`: `Matrix Assembling CI` — SUCCESS

## Canonical work rules

- work on one repository at a time unless the owner explicitly says otherwise;
- temporary active repository for this architecture clarification/bridge checkpoint is `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- when a component changes, keep code, tests, active documents and continuity coherent in the same workstream;
- do not create parallel specifications when an active canonical document can be updated.

Owner clarification for this checkpoint:

```text
Resolve the universal cross-module ambiguity in Assembling first.
Do not use this as authorization to begin Memory implementation or other deferred runtime modules.
After the protocol is clear, Memory can be designed against it in its own repository when explicitly selected.
```

## MIP Bridge implementation checkpoint — TESTED

The owner requested an explicit adapter layer between existing module formats without modifying the existing modules.

Implementation candidate:

```text
src/main/kotlin/matrix/assembling/mip/MipBridge.kt
```

Compatibility audit:

```text
docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md
```

Tests:

```text
src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt
```

PR:

```text
#7 Add explicit MIP bridge adapters and compatibility audit
base = main @ 10cea71af56427b81aa15c7bfcdf3be38823a4ee
head = mip-bridge-v1
```

Relevant branch commits before this continuity update:

```text
4754854b106131c7e368641ece662f09ba4605f3  initial bridge
c50118f992b2c269191bbd157193a26eaed6695b  compile-safe explicit mappings
bfd50eefc1f9d30eb327061762f4dc3cdc69ed60  round-trip/incompatibility tests
d3e6c254baabf18795fa0a1d7b7dc6dfd2149bb9  field-level compatibility audit
a70e75d4ce82de125ceae1a6ab4bd7ee26d0a6b3  docs index alignment
```

CI evidence:

```text
run = 33949127818
job = kotlin-tests
result = SUCCESS
Run tests = SUCCESS
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

### Bridge mappings implemented

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

For the owner-provided cross-language incompatibility:

```text
Python:
contradicts_memory_id: Optional[int]

MIP:
contradictedMemoryId: explicit MipField<String>

Kotlin Memory boundary:
contradictedMemoryId: Long?
```

MIP uses an opaque decimal string for this identifier so Python integer width is preserved; conversion to Kotlin `Long` is explicit and overflow fails closed.

Current Assembling `AuthorityDecision` does not contain contradiction identity. The bridge MUST throw instead of dropping a PRESENT canonical contradiction ID when converting back to that current DTO.

### Residual bridge risks

P0:
- current Assembling `AuthorityDecision` still lacks contradiction identity required for real memory-backed Authority/Admission;
- the complete frozen/reference Python `AuthorityResolution` source contract is not stored in this repository, so only the owner-provided `contradicts_memory_id` field is cross-language-grounded here.

P1:
- `TypedClaim` does not independently carry dialogue act or explicit semantic-domain marker;
- null/sentinel native formats remain outside the bridge;
- current predicate/source/status domains are still mostly stringly typed;
- `worldTruth: Boolean` remains a compatibility representation;
- legacy `contracts/*` remains a duplicate vocabulary but is quarantined as compatibility-only;
- primitive wire codec is implemented for the current high-risk Authority seam; additional cross-process codecs should be added only when a real boundary requires them.

P2:
- snake_case/camelCase naming differences remain native-format concerns handled explicitly by adapters;
- `MemoryAdmissionResult.status` is still a string;
- semantic marker registry needs future versioning with PredicateId registry.

This checkpoint does NOT authorize:
- changing existing module DTOs;
- rewiring `MatrixAssemblingOrchestrator` to MIP Bridge;
- implementing Memory;
- implementing Relationship;
- implementing Reflection;
- implementing BDI/Decision.

## Canonical universal protocol

Assembling owns the canonical Matrix Intermodule Protocol:

```text
docs/MATRIX_INTERMODULE_PROTOCOL.md
version = MIP-1.0
```

Protocol creation commit:

```text
524eddd160fffae5425f06db2b7a44fe78abfb19
```

Aligned documentation commits in the original MIP definition workstream:

```text
7f1f3b73555bb88e64a5a2f9e01cc5c0cf2846ac  docs index / MIP authority
df8803c81059e58eb77b92c33e8095ed5c871754  module wiring aligned to MIP
2c20e0b4dce5b061bc07dbee250586ffa4462990  assembly plan aligned to MIP
bc58f21eecf4a7d29930a451c9be0a015d8a3796  memory integration policy aligned
f25185e1529c55eeb91bf499fe3acb25635e8d71  memory integration status aligned
10cea71af56427b81aa15c7bfcdf3be38823a4ee  canonical MIP architecture checkpoint
```

No Kotlin runtime code was changed in the original architecture-definition checkpoint; the later PR #7 adds the isolated adapter layer described above without changing existing modules or orchestrator wiring.

## MIP-1.0 canonical principles

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS
```

```text
ONE SEMANTIC LANGUAGE
ONE CONTEXT FORMAT
ONE ENTITY REFERENCE MODEL
ONE TEMPORAL MODEL
ONE PROVENANCE MODEL
ONE CONFIDENCE TAXONOMY
ONE TRACE / REASON-CODE LANGUAGE
```

Hard semantic separations:

```text
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

## Shared term definitions now canonical

The following roles are globally distinct:

```text
speaker
observer
source
subject
target
owner
perspective
```

Unresolved-state semantics are also globally distinct:

```text
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
UNAVAILABLE
NO_MATCH
ERROR
NOT_APPLICABLE
```

No future module may redefine these terms privately at its public Matrix boundary.

## Context model now canonical at design level

Target universal context object:

```text
MatrixContextSnapshot
→ immutable/read-only
→ versioned by snapshotId/parentSnapshotId
→ contains typed ContextEntry values
→ domain availability is explicit
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

Missing/non-wired domain state must never be represented using fake zero/default values.

Current `MatrixTurnFrame` remains the implementation precursor / compatibility surface. It has not been rewritten.

## Memory retrieval rule now canonical at design level

Owner decision incorporated into MIP:

```text
EVERY NORMAL TURN
→ LIGHTWEIGHT MEMORY INDEX PROBE
```

Reason: Matrix cannot know whether relevant Memory exists without querying the index, even when the user does not explicitly say "do you remember?".

Target retrieval levels:

```text
LEVEL 1 INDEX_PROBE — always
LEVEL 2 HYDRATE_AND_RERANK — only on relevant hits
LEVEL 3 DEEP_OR_MULTI_HOP — only for explicit complex retrieval purpose
```

This rule is architecture-only today. Real retrieval remains `NON_CABLATO`.

Retrieval is universal and not Reflection-specific. Future Reflection will use the same `RetrievalQuery` / `RetrievalResult` protocol.

## Relationship / affective / intimacy separation

MIP reserves these as distinct state domains:

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

Dominance/role language alone does not imply coercion.
Adult/intimacy is first-class semantics and not automatic censorship, confidence penalty, Memory penalty or Affective penalty.

## Memory semantic clarification

MIP reserves semantic Memory kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

`REFLECTION` is a future-compatible Memory kind; no Reflection module is assumed to exist today.

The following are not Memory kinds:

```text
RELATIONSHIP
AFFECTIVE
GOAL
INTIMACY
```

If Core/Recall/Archival terminology is used later, it is an access/retention tier rather than semantic Memory identity.

Pre-response:

```text
MemoryCandidate
= ephemeral TurnWorkspace data
!= MemoryRecord
```

Durable Memory remains post-validation only:

```text
VALIDATE
→ PersistentConsolidationPort
→ Memory Admission
→ MemoryRepository
```

## Contradiction / supersession clarification

```text
CONTRADICTION != SUPERSESSION
```

Authority/Belief resolution identifies explicit semantic contradiction identity.
Memory Admission consumes that decision and must not infer conflict from mere text difference, shared actor or unrelated predicate.

Temporal change is not contradiction by default.

Durable semantic modification uses `supersede()` and preserves lineage.

## Canonical runtime direction

```text
Input
→ NLU / Understanding
→ TurnWorkspace / MatrixContextSnapshot
→ context ENRICH
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

Logical stages permit parallel reads where dependencies allow.

Missing target phases remain explicitly `NON_CABLATO` / `NOT_WIRED`.

## Integrated hardening status

Completed and regression-tested before the MIP bridge checkpoint:
- all NLU claims preserved;
- critical confidence fail-closed on every claim;
- unresolved subject remains `UNKNOWN`;
- third-party reports do not become direct authority;
- multi-claim turns cannot masquerade as one direct authority;
- pre-response Memory is `MemoryPreflightPort`, never durable write;
- pre-response `stableWrite=true` / Memory IDs are rejected;
- Affective cannot own RelationshipState;
- Affective persistence cannot exceed upstream authorization;
- `DiagnosticTrace` is typed and keeps write-once `firstDivergence`;
- Prompt Builder is realization-only;
- legacy pipeline is compatibility-only/deprecated;
- optional `OutputValidatorPort` boundary is after GGUF;
- future durable persistence boundary is `PersistentConsolidationPort`;
- explicit adult/intimacy NLU marker supported with compatibility fallback.

Evidence:
- PR #4 merged as `a46919d925824e12d66d074a77c231aa2b4b7a1b`;
- PR #6 merged as `1c603ac94d62d0e79d14fd455481c7a487d89ea4`;
- prior main CI run `33942262278` — SUCCESS;
- MIP Bridge PR CI run `33949127818` — SUCCESS.

No existing P0/P1/P2 gate was weakened or re-opened by MIP Bridge work.

## Memory Foundation migration decisions — PRESERVED

```text
Python Memory Foundation v3
= frozen reference/oracle
        ↓ contract parity
Kotlin / Room Memory Foundation
= future production implementation
        ↓ fault-injection + regression gates
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

## Still NON_CABLATO

- full formal Kotlin `MatrixEnvelope<T>` integration;
- full typed `TemporalRef` / `ProvenanceRef` integration;
- typed confidence wrappers across existing module DTOs;
- Predicate registry implementation;
- explicit `TurnWorkspace` migration;
- `MatrixContextSnapshot` / `ContextEntry` runtime types;
- read-only context ports;
- real always-on Memory index probe;
- real Long-Term hydrate/rerank retrieval;
- per-claim final contextual Authority/Belief resolution;
- real BeliefState;
- real semantic `OutputValidatorPort` implementation;
- real `PersistentConsolidationPort` implementation;
- real Memory Foundation adapter and atomic commit;
- canonical RelationshipState controller;
- canonical Intimacy/Consent resolver;
- BDI-lite + Utility Decision layer;
- real llama.cpp/MLC GGUF bridge;
- Android application integration;
- Reflection implementation.

## Current architecture checkpoint decision

MIP-1.0 is canonical. PR #7 adds an isolated tested bridge implementation candidate without changing existing module ownership or runtime order.

Do NOT start the following automatically:

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

After PR #7 is reviewed/merged, the next architecture choice remains owner-controlled.

If explicitly switching to `MATRIXNEO23/memoria`:
- treat the current Memory README as historical/approximate material, not authoritative where it conflicts with MIP;
- design Memory Foundation against `MIP-1.0` and the tested bridge semantics;
- do not modify Assembling while Memory is active;
- do not implement Reflection merely because `REFLECTION` exists as a Memory kind;
- preserve always-on lightweight index-probe requirement;
- preserve post-validation durable write boundary;
- audit the complete Python reference `AuthorityResolution` before relying on fields beyond the owner-provided contradiction ID.

## Exact restart rule

If returning to Assembling implementation later:

```text
DO NOT restart architecture audit.
DO NOT redo completed P0/P1/P2 hardening.
DO NOT invent another context/protocol model.
DO NOT bypass MIP Bridge with ad-hoc cross-module field mapping.
Start from MIP-1.0 + PR #7 bridge checkpoint and migrate runtime incrementally only when explicitly authorized.
```