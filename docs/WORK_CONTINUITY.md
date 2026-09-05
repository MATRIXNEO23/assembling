# Work Continuity — Matrix Assembling Lab

Last updated: 2026-09-05T08:50+02:00  
Repository: `MATRIXNEO23/assembling`  
Active cleanup branch: `assembling-mip-cleanup`  
Main baseline at cleanup start: `ef433a3aed519b31efe9289a8df78ed974170510`  
Continuity schema: `matrix.assembling.continuity.v17`  
PR `#7`: **MERGED** — explicit MIP bridge adapters and compatibility audit  
PR #7 merge SHA: `dfd2da5ced902cf601530e65bb11c43e89dbd98a`  
Last verified main CI before cleanup: `33949428699` — **SUCCESS**

## Canonical work rules

- work on one repository at a time unless the owner explicitly says otherwise;
- active repository for this cleanup is `MATRIXNEO23/assembling`;
- historical repositories are backup/checkpoint sources, not active targets;
- do not write other repositories without explicit authorization;
- when a component changes, keep code, tests, active documents and continuity coherent in the same workstream;
- do not create parallel specifications when an active canonical document can be updated;
- every new functional module must live in its own dedicated directory/package;
- existing root runtime files are not moved for cosmetic cleanup.

Owner scope for this checkpoint:

```text
ASSEMBLING STRUCTURE
+
MIP INTEROP CONSOLIDATION
+
AMBIGUITY REMOVAL
```

Explicitly not authorized:

```text
Memory Kotlin/Room
real retrieval
Relationship
Reflection
BDI/Decision
Intimacy/Consent resolver
Android integration
real GGUF bridge
orchestrator rewiring to MIP
```

Other repositories remain read-only.

## Canonical universal protocol

Assembling owns:

```text
docs/MATRIX_INTERMODULE_PROTOCOL.md
version = MIP-1.0
```

Protocol creation commit:

`524eddd160fffae5425f06db2b7a44fe78abfb19`

Original MIP documentation alignment:

```text
7f1f3b73555bb88e64a5a2f9e01cc5c0cf2846ac  docs index / MIP authority
df8803c81059e58eb77b92c33e8095ed5c871754  module wiring aligned to MIP
2c20e0b4dce5b061bc07dbee250586ffa4462990  assembly plan aligned to MIP
bc58f21eecf4a7d29930a451c9be0a015d8a3796  memory integration policy aligned
f25185e1529c55eeb91bf499fe3acb25635e8d71  memory integration status aligned
10cea71af56427b81aa15c7bfcdf3be38823a4ee  canonical MIP architecture checkpoint
```

## MIP Bridge — integrated baseline

Implementation:

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt`

Audit:

`docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md`

Tests:

`src/test/kotlin/matrix/assembling/mip/MipBridgeTest.kt`

PR history:

```text
PR #7 = MERGED
branch = mip-bridge-v1
base = 10cea71af56427b81aa15c7bfcdf3be38823a4ee
final PR head = f7a1b9ff81c46072bbd9b8fa7ade0ab8232bd662
merge SHA = dfd2da5ced902cf601530e65bb11c43e89dbd98a
```

CI evidence before cleanup:

```text
PR run 33949127818 = SUCCESS
PR final-head run 33949261802 = SUCCESS
main post-merge run 33949320319 = SUCCESS
main continuity run 33949428699 = SUCCESS
```

## Current cleanup workstream

Branch:

`assembling-mip-cleanup`

Start HEAD:

`ef433a3aed519b31efe9289a8df78ed974170510`

Audit-only checkpoint:

`24c98102c9a6863a09d2229fa5bed12719295295`

Safe changes applied after audit:

```text
b3c7503d5406c11a6ebeeca053387ec55bfc226d  MIP status separation + strict fail-closed mapping
f450dd362157766edeadcfb83ed8b5eb84572315  strict MIP bridge tests
791b0ad0aa81546f2822fa3835c7a4ebbde81efa  dedicated-directory rule
5c2501f30c21fcde9a0629dedd9c34b1cc30f211  explicit legacy coherence deprecation
e3b74f8a5c5844ffe0e74844342e30ac9a819e39  explicit legacy prompt deprecation
8dfb9634708d387dd1736029822dd4c1d911f603  docs index cleanup alignment
b4582b2a2bcd7af6a3bf8af9d2c9b3af85cab231  final cleanup compatibility audit
```

### Safe adapter fixes

MIP generic field status is now explicitly:

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

Entity resolution is now a separate vocabulary matching MIP-1.0:

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

The bridge no longer uses generic `PRESENT` to mean an entity is resolved.

Authority contradiction-ID conversion is now fail-closed:

```text
PRESENT -> concrete ID
NOT_APPLICABLE -> native null / None
UNKNOWN -> ERROR
UNRESOLVED -> ERROR
AMBIGUOUS -> ERROR
CONFLICTED -> ERROR
UNAVAILABLE -> ERROR
NO_MATCH -> ERROR
ERROR -> ERROR
```

This prevents `null` from ambiguously meaning both "no contradiction" and "resolution unavailable/unfinished".

The partial `PythonAuthorityResolutionWire` represents only the owner-provided contradiction field. Conversion from full canonical Authority to that projection now fails if any non-modeled Authority field contains information that would be discarded.

Current root `AuthorityDecision` has no contradiction-ID field. It may round-trip through MIP only when that canonical field is `UNAVAILABLE`; any other semantic state fails rather than being dropped.

### Safe structural cleanup

No files moved, renamed or deleted.

Explicit compatibility quarantine:

```text
contracts/MatrixAssemblyContracts.kt = KEEP_COMPATIBILITY
pipeline/MatrixAssemblyPipeline.kt = KEEP_COMPATIBILITY / DEPRECATED
prompt/SemanticFrameToPrompt.kt = KEEP_COMPATIBILITY / DEPRECATED
coherence/CoherenceGuard.kt = KEEP_COMPATIBILITY / DEPRECATED
```

The root/frame runtime remains authoritative.

### New module-directory rule

```text
EVERY NEW FUNCTIONAL MODULE
→ dedicated directory/package
```

No new module may be added as an unrelated root file under `matrix/assembling`.

Existing root runtime files remain in place unless a future audited move has a demonstrated compatibility benefit.

### Current cleanup validation status

At this continuity write:

```text
inventory = COMPLETE
safe fixes = APPLIED
existing module DTOs modified = false
orchestrator rewired = false
business logic added to bridge = false
other repositories modified = false
files moved = 0
files renamed = 0
files deleted = 0
full regression = PENDING
CI = PENDING
```

Next action inside this same cleanup task:

```text
open cleanup PR
run full Gradle/CI regression
fix only cleanup regressions if any
merge only if green
update continuity
STOP / AWAIT OWNER REVIEW
```

## Bridge mappings

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

Concrete cross-language seam:

```text
Python reference field:
contradicts_memory_id: Optional[int]

MIP:
contradictedMemoryId: MipField<String>

future Kotlin Memory boundary:
contradictedMemoryId: Long?
```

MIP treats the memory identifier as an opaque decimal string at the language-neutral boundary. Python integer identity is preserved and Kotlin `Long` range checking is explicit.

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

Current `MatrixTurnFrame` remains an implementation precursor/runtime surface; it has not been replaced.

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

## Memory Foundation migration policy — preserved

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

## Residual cleanup risks

### P0

1. Current root `AuthorityDecision` lacks contradiction identity required for real memory-backed Authority/Admission. Bridge blocks loss but does not modify the DTO.
2. Complete frozen/reference Python `AuthorityResolution` is not stored in Assembling; only the owner-provided contradiction field is cross-language-grounded here.
3. Owner/perspective still use nullable/sentinel-rich native DTOs; current guards mitigate but final MIP-native runtime types are not yet wired.
4. `worldTruth: Boolean` remains in native compatibility DTOs and must never self-grant Authority.

### P1

1. `TypedClaim` lacks dialogue act and explicit semantic-domain marker.
2. Native `null` / `UNKNOWN` / `NONE` remain outside MIP.
3. Predicate/source/status vocabularies remain mostly stringly typed.
4. Full temporal/provenance/modality/claimKind parity is deferred.
5. `MatrixEnvelope<T>` is not yet the runtime boundary wrapper.
6. legacy duplicate vocabulary remains, now explicitly quarantined/deprecated.
7. only Authority currently has an explicit primitive wire codec.

### P2

1. Python snake_case vs Kotlin camelCase remains adapter naming.
2. `MemoryAdmissionResult.status` remains a string.
3. semantic marker registry remains open string map.
4. root runtime is not physically split into runtime/ports/diagnostics; move risk currently exceeds benefit.

## Existing hardening preserved

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

## STOP boundary

Do NOT automatically start:

```text
orchestrator rewiring to MIP Bridge
full Kotlin MIP rewrite
Working Context implementation
Memory retrieval implementation
Memory persistence
Authority Resolver implementation in another repository
Relationship
Affective redesign
Intimacy/Consent implementation
Decision/BDI
Reflection
```

## Exact restart rule

If returning after this cleanup:

```text
DO NOT restart architecture audit.
DO NOT redo completed P0/P1/P2 hardening.
DO NOT invent another context/protocol model.
DO NOT bypass MIP Bridge with ad-hoc cross-module field mapping.
NEW MODULE -> dedicated directory/package.
Start from MIP-1.0 + cleaned MIP Bridge checkpoint.
```
