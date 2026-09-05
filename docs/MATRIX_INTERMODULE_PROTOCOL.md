# Matrix Intermodule Protocol (MIP)

Status: **CANONICAL ARCHITECTURAL PROTOCOL / IMPLEMENTATION MIGRATION PENDING**  
Version: `MIP-1.0`  
Date: 2026-09-05  
Owner repository: `MATRIXNEO23/assembling`

## 1. Purpose

MIP defines the universal typed language used by Matrix modules to exchange observations, semantic claims, contextual state, retrieval requests/results, proposals, events, provenance and diagnostics.

MIP is owned by Assembling because it is a cross-module contract. NLU, Memory, Affective, Relationship, World, Decision and future modules conform to MIP; none of those modules may redefine MIP privately.

The protocol is deliberately independent of modules that do not yet exist. Future Reflection, BDI, Relationship, Intimacy/Consent or other modules must adapt to MIP rather than introducing a new parallel context/protocol.

## 2. Fundamental separations

```text
OBSERVE ≠ UNDERSTAND ≠ BELIEVE ≠ REMEMBER ≠ FEEL ≠ RELATE ≠ CONSENT ≠ WANT ≠ DECIDE ≠ EXPRESS
```

Hard semantic invariants:

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

No module may silently absorb the authority or semantics of another module.

## 3. Universal execution principles

1. **Text is linguistically interpreted once.** NLU/Understanding produce structured semantic evidence. Downstream modules must not re-parse free text to reinvent subject, negation, predicate, time, owner or perspective.
2. **Every canonical state has exactly one logical owner.** Other modules receive read-only snapshots or submit typed proposals.
3. **Context is read-only.** A context snapshot is a temporary view, never the canonical state itself.
4. **Missing, unknown, unresolved, ambiguous and unavailable are different states.** They must never be collapsed into `null`, zero, empty string or empty list.
5. **Every intermodule datum has identity, provenance, time and traceability.**
6. **Missing modules are reported as unavailable, never simulated with fake values.**
7. **Logical stages do not require serial execution.** Independent reads may run in parallel when dependencies permit.
8. **No durable Memory write occurs before accepted output/action validation.** Pre-response memory work is read/evaluate/propose only.
9. **Adult/intimacy is an ordinary semantic domain.** It is not an automatic block, confidence penalty, persistence penalty or censorship label.

## 4. Universal logical stages

MIP uses the following stage vocabulary:

```text
OBSERVE
UNDERSTAND
ENRICH
RESOLVE
EVALUATE
DECIDE
REALIZE
VALIDATE
COMMIT
```

These are semantic phases, not a mandatory single-threaded pipeline.

Examples:
- Memory, Relationship, Affective and World snapshots may be read concurrently during `ENRICH`.
- Authority/Belief resolution occurs in `RESOLVE`.
- GGUF realization occurs in `REALIZE`.
- durable persistence occurs only in `COMMIT` after `VALIDATE`.

## 5. MatrixEnvelope<T>

Every intermodule message or payload is carried in an envelope equivalent to:

```text
MatrixEnvelope<T>
- schemaId
- schemaVersion
- messageId
- correlationId
- causationId?
- turnId
- sessionId
- agentId
- producer
- payloadType
- logicalStage
- observedAt?
- createdAt
- payload: T
- provenance
- traceRef
```

### 5.1 Identifier semantics

- `messageId`: unique identity of this exact envelope/payload emission.
- `correlationId`: identity shared by the complete causal workstream/turn.
- `causationId`: identity of the immediate event/message that caused this emission; absent only when the payload is a root event.
- `turnId`: current conversational/interaction turn identity.
- `sessionId`: interaction/session identity.
- `agentId`: Matrix agent whose subjective processing/state is involved.

Identifiers are opaque stable IDs. Human-readable names are not identifiers.

## 6. Module identifiers

`producer` and `ownerModule` use a versioned registry, not arbitrary strings.

Initial reserved identifiers:

```text
PERCEPTION
NLU
UNDERSTANDING
CONTEXT_ASSEMBLER
WORLD
BELIEF_AUTHORITY
MEMORY
AFFECTIVE
RELATIONSHIP
INTIMACY
GOAL
DECISION
PROMPT_BUILDER
GGUF
OUTPUT_VALIDATOR
PERSISTENT_CONSOLIDATION
SYSTEM
```

A reserved identifier may exist before the corresponding module is wired. This does not authorize fake outputs from that module.

Future Reflection, if implemented, receives a new registered identifier but uses the same MIP payloads and context format.

## 7. EntityRef — universal entity identity

Free strings such as `"Marco"` are not sufficient intermodule identity.

```text
EntityRef
- entityId?
- surfaceForm?
- entityType
- resolutionStatus
- confidence?
- candidateIds[]
```

### 7.1 EntityResolutionStatus

```text
RESOLVED
UNKNOWN
UNRESOLVED
AMBIGUOUS
CONFLICTED
NOT_APPLICABLE
```

Definitions:

- `RESOLVED`: one canonical entity identity has been selected.
- `UNKNOWN`: the entity is semantically expected but identity is not known.
- `UNRESOLVED`: evidence exists but is insufficient to select a canonical identity.
- `AMBIGUOUS`: two or more plausible identities remain.
- `CONFLICTED`: incompatible resolution evidence exists.
- `NOT_APPLICABLE`: the semantic field does not apply to this payload.

`UNKNOWN`, `UNRESOLVED` and `AMBIGUOUS` are not interchangeable.

When `resolutionStatus=RESOLVED`, `entityId` is mandatory. When unresolved/ambiguous, candidate IDs may be supplied but must not be treated as resolved identity.

## 8. Role semantics: speaker, observer, source, subject, target, owner, perspective

These names have exactly one meaning across Matrix:

- `speaker`: entity that physically/logically produced the utterance/message.
- `observer`: Matrix agent that perceived the observation.
- `source`: epistemic origin to which the information is attributed.
- `subject`: entity to which the predicate is attributed.
- `target`: entity/object toward which the predicate/action/state is directed.
- `owner`: entity that owns the internal state, preference, goal, belief, possession or experience represented by the predicate.
- `perspective`: epistemic viewpoint from which the claim is asserted/reported.

Example:

```text
Alberto says: "Marco told me that Anna loves Luca."

speaker     = ALBERTO
observer    = LUNA
source      = MARCO
subject     = ANNA
predicate   = matrix.affection.love
target      = LUCA
owner       = ANNA
perspective = MARCO
```

No downstream module may default unresolved `subject`, `owner` or `perspective` to `speaker` without explicit evidence.

## 9. PredicateId and semantic registry

Semantic predicates use a versioned identifier registry, not free-form phrases.

Canonical form:

```text
matrix.<domain>.<concept>[.<subconcept>]
```

Examples:

```text
matrix.preference.like
matrix.preference.dislike
matrix.relationship.trust
matrix.affection.love
matrix.affection.attraction
matrix.communication.message
matrix.goal.want
matrix.consent.grant
matrix.consent.refuse
matrix.consent.withdraw
matrix.location.live_at
```

Natural-language wording is evidence/source material, not the intermodule semantic key.

The registry is additive/versioned. Removing or changing the meaning of an existing PredicateId requires an explicit protocol-version migration.

## 10. TypedClaim — universal interpreted claim

`TypedClaim` represents what Understanding interpreted, not what Matrix has decided is true.

Conceptual contract:

```text
TypedClaim
- claimId
- speaker: EntityRef
- observer: EntityRef
- source: EntityRef
- subject: EntityRef
- target: EntityRef
- owner: EntityRef
- perspective: EntityRef
- predicate: PredicateId
- objectValue?
- dialogueAct
- claimKind
- polarity
- modality
- temporal: TemporalRef
- interpretationConfidence
- confidenceByField
- sourceSpans
- provenance
```

Hard invariant:

```text
TypedClaim != WorldTruth
TypedClaim != Belief
TypedClaim != MemoryRecord
```

Multiple claims from one observation remain separate `TypedClaim` values with separate identity, confidence, provenance and later resolution.

## 11. Explicit status instead of ambiguous null

Protocol boundaries must not use one nullable field to mean multiple semantic states.

MIP distinguishes:

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

Definitions:

- `NOT_APPLICABLE`: field/module does not semantically apply.
- `UNKNOWN`: a value is expected but genuinely unknown.
- `UNRESOLVED`: value may be derivable but resolution is incomplete.
- `AMBIGUOUS`: several plausible values remain.
- `CONFLICTED`: incompatible evidence exists.
- `UNAVAILABLE`: required provider/backend/module cannot supply the data.
- `NO_MATCH`: provider successfully queried and found no matching data.
- `ERROR`: provider attempted operation but failed.

`NO_MATCH` and `UNAVAILABLE` are especially forbidden from collapsing into the same empty result.

## 12. TemporalRef — universal temporal model

MIP separates when something was observed, when it occurred, when it was valid and when it was recorded.

```text
TemporalRef
- relation
- eventTime?
- validFrom?
- validTo?
- referenceEventId?
- granularity
- resolutionStatus
- confidence?
```

Envelope/provenance additionally carries `observedAt` and `createdAt/recordedAt` where appropriate.

### 12.1 TemporalRelation

Initial universal values:

```text
PAST
PRESENT
FUTURE
BEFORE
AFTER
DURING
INTERVAL
RECURRENT
AT_REFERENCE
UNKNOWN
UNRESOLVED
NOT_APPLICABLE
```

Historical truth is not contradiction by default.

Example:

```text
"I used to live in Rome; now I live in Milan."
```

may yield temporally distinct valid claims rather than two mutually exclusive current facts.

## 13. ProvenanceRef — universal provenance

Every derived semantic/state object must be traceable to its origins.

```text
ProvenanceRef
- originId
- originType
- originAgent?
- generatedBy
- derivedFromIds[]
- quotedFromId?
- revisionOfId?
- observationId?
- eventId?
- claimId?
- createdAt
```

Provenance is immutable historical evidence. A later state transition does not rewrite where earlier information came from.

## 14. Confidence taxonomy

All confidence-like values are normalized to `[0.0, 1.0]` when numeric, but their meanings are distinct types/fields.

### 14.1 InterpretationConfidence

How confident NLU/Understanding is that it interpreted the language correctly.

### 14.2 SourceReliability

How reliable the epistemic source is considered for this domain/context.

### 14.3 AuthorityLevel / EpistemicClass

Categorical authority/source class; not a probability.

Initial authority hierarchy retained for Memory/Belief work:

```text
WORLD_TRUTH
OBSERVATION
REPORT
INFERENCE
BELIEF
```

Additional origin qualifiers such as self-report or third-party report may refine provenance/sourceType without redefining the hierarchy.

Only the World/authorized world adapter may originate `WORLD_TRUTH`. NLU/Understanding must never promote ordinary text to World Truth.

### 14.4 BeliefConfidence

How strongly the agent currently accepts a resolved belief after evidence/authority handling.

### 14.5 RetrievalRelevance

How relevant a retrieved item is to the current retrieval query. It is not truth, reliability or belief.

Hard rule:

```text
interpretationConfidence
!= sourceReliability
!= authorityLevel
!= beliefConfidence
!= retrievalRelevance
```

## 15. State ownership

Every canonical mutable state has one logical owner.

Initial ownership map:

```text
WorldTruth / world state        -> WORLD
linguistic interpretation       -> NLU / UNDERSTANDING
belief/epistemic resolution     -> BELIEF_AUTHORITY
Long-Term Memory                -> MEMORY
AffectiveState                  -> AFFECTIVE
RelationshipState               -> RELATIONSHIP
current intimacy/consent state  -> INTIMACY
Goals/Intentions                -> GOAL / DECISION contract
behavioral choice               -> DECISION
language realization            -> GGUF
post-validation commit coord.   -> PERSISTENT_CONSOLIDATION
```

`PERSISTENT_CONSOLIDATION` coordinates durable commits but does not become owner of Memory, Relationship or Affective state.

## 16. MatrixContextSnapshot — one universal context format

Matrix does not define separate incompatible `ReflectionContext`, `DecisionContext`, `MemoryContext`, etc.

All consumers receive an immutable:

```text
MatrixContextSnapshot
- snapshotId
- parentSnapshotId?
- turnId
- sessionId
- agentId
- createdAt
- entries[]: ContextEntry
- domainAvailability[]
```

A module may consume only the entries/domains it needs.

### 16.1 ContextDomain

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

### 16.2 ContextScope

```text
TURN
CONVERSATION
EPISODE
SESSION
PERSISTENT
WORLD
```

### 16.3 ContextEntry

```text
ContextEntry
- entryId
- domain
- scope
- key
- typedValue
- subjectRefs[]
- entityRefs[]
- authority?
- confidence?
- provenance
- validity
- ownerModule
- stateVersion?
```

Context entries are immutable observations/snapshots of canonical state or retrieved evidence.

## 17. Domain availability

A missing module must not emit fake zero/default state.

Each context domain carries availability separately from content:

```text
AVAILABLE
NOT_WIRED
UNAVAILABLE
ERROR
```

Examples:

```text
RELATIONSHIP domain = NOT_WIRED
```

means no canonical Relationship controller is currently connected.

It must never be represented as:

```text
relationship.trust = 0
```

Similarly:

```text
memory retrieval status = NO_MATCH
```

means the index worked and found no relevant memory, while:

```text
memory domain = UNAVAILABLE
```

means retrieval could not be performed.

## 18. Context snapshot evolution

Snapshots are immutable and versioned within a turn.

Example:

```text
ContextSnapshot v1
  -> memory index probe
ContextSnapshot v2
  -> contextual/authority resolution
ContextSnapshot v3
```

Each new snapshot references `parentSnapshotId`.

Diagnostics must be able to identify exactly which snapshot a module consumed.

## 19. TurnWorkspace — ephemeral operational state

The current `MatrixTurnFrame` is the implementation precursor to a universal `TurnWorkspace` concept.

Conceptual contents:

```text
TurnWorkspace
- input/observation
- NLU evidence
- TypedClaims
- context snapshots
- retrieval queries/results
- coherence/authority resolutions
- preflight results
- state proposals
- decision/result
- prompt
- draft reply/action
- validation
- diagnostics
```

Properties:

```text
EPHEMERAL
TURN_BOUNDED
NON_PERSISTENT
```

The current Kotlin `MatrixTurnFrame`, `String` fields, nullable compatibility fields and `worldTruth` boolean remain implementation/compatibility details to migrate incrementally; they are not the final universal protocol definition.

## 20. StateProposal — universal cross-owner mutation request

A module that does not own a canonical state cannot mutate it. It may submit:

```text
StateProposal
- proposalId
- producer
- targetOwner
- operation
- targetKey
- proposedValue?
- delta?
- evidenceRefs[]
- reasonCodes[]
- confidence?
- preconditions[]
- provenance
```

The target owner validates/accepts/rejects the proposal according to its own contract.

No proposal is equivalent to a committed state change.

## 21. MatrixEvent — universal committed/observed event

Events use:

```text
MatrixEvent
- eventId
- eventType
- actorRefs[]
- targetRefs[]
- before?
- after?
- eventTime
- recordedAt
- causationId?
- correlationId
- provenance
- reasonCodes[]
```

Events may be used as sources for episodic memory, diagnostics and downstream perception without making the event itself a belief or memory automatically.

## 22. Retrieval is a universal service contract

Memory retrieval is not Reflection-specific. Future modules use the same retrieval protocol.

### 22.1 Always-on index probe

Every normal turn performs a lightweight Memory index query after structured semantics/current context are available:

```text
EVERY TURN
-> MEMORY INDEX PROBE
```

This is required because Matrix cannot know whether a relevant memory exists without querying the index.

This does **not** imply hydrating all memories every turn.

### 22.2 Retrieval levels

```text
LEVEL 1 — INDEX_PROBE
always; cheap candidate-ID lookup

LEVEL 2 — HYDRATE_AND_RERANK
only when relevant candidates exist

LEVEL 3 — DEEP_OR_MULTI_HOP
only when a consumer explicitly requests deeper history/pattern/causal retrieval
```

Future Reflection may request Level 3 but does not own a separate retrieval engine.

### 22.3 RetrievalPurpose

Universal purposes:

```text
ENRICH_TURN
VERIFY_CLAIM
CHECK_CONTRADICTION
FIND_HISTORY
EXPLICIT_RECALL
ANALYZE_PATTERN
EXPLAIN_STATE
SUPPORT_DECISION
```

The registry is extensible/versioned.

### 22.4 RetrievalQuery

```text
RetrievalQuery
- queryId
- purpose
- agentId
- subjectRefs[]
- entityRefs[]
- predicates[]
- temporalConstraint?
- relationshipTarget?
- goalRefs[]
- includeHistorical
- includeSuperseded
- maxCandidates
- maxSelected
- contextSnapshotId
```

Free text may be supplied as an auxiliary lexical/semantic-search signal but must not be the authoritative replacement for structured subject/predicate/entity/time fields.

### 22.5 RetrievalResult

```text
RetrievalResult
- queryId
- status
- candidateRefs[]
- selectedRefs[]
- scores[]
- reasonCodes[]
- indexVersion?
```

Statuses:

```text
MATCHED
NO_MATCH
AMBIGUOUS
INDEX_UNAVAILABLE
ERROR
```

An empty list may accompany `NO_MATCH` but may not replace the explicit status.

## 23. Retrieval ranking semantics

Ranking may combine bounded signals such as:

```text
semanticSimilarity
entityMatch
predicateMatch
temporalRelevance
recency
importance
salience
relationshipRelevance
affectiveRelevance
goalRelevance
conversationContinuity
authority/reliability filters
```

Relationship, affective or goal state may change **retrieval relevance only**.

They may not mutate the content, provenance, authority or historical validity of a MemoryRecord.

Example: anger may increase ranking of conflict episodes; it cannot rewrite those episodes.

## 24. Memory semantic categories vs access tiers

MIP reserves the semantic Memory kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

`REFLECTION` is a record kind that may be produced in the future; the absence of a Reflection module today does not require a protocol change later.

`CORE`, `RECALL`, `ARCHIVAL` are **not semantic memory kinds**. If used, they are access/retention tiers such as:

```text
PINNED
NORMAL
ARCHIVAL
```

RelationshipState, AffectiveState and GoalState are not Memory kinds.

## 25. MemoryCandidate and durable Memory boundary

Before response/action validation, a `MemoryCandidate` is ephemeral data inside the turn/workspace.

```text
MemoryCandidate != MemoryRecord
```

Pre-response stages may:
- retrieve;
- compare;
- evaluate;
- identify contradictions;
- propose candidate persistence.

They may not create durable memory IDs or durable writes.

Durable path:

```text
accepted reply/action result
-> VALIDATE
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
```

Current hard invariant remains:

```text
pre-response stableWrite == false
pre-response memoryIds == []
```

## 26. Memory decisions and lineage

Durable Memory Admission decisions remain:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Reinforcement of an unchanged semantic fact is metadata/evidence reinforcement, not an automatic semantic rewrite.

Hard lineage invariants:

```text
revisionOf -> lineage root
supersededBy -> sequential successor
semantic change -> supersede()
metadata-only change -> metadata update
```

## 27. Contradiction vs supersession

Definitions:

- `CONTRADICTION`: two pieces of evidence/claims/memories are semantically incompatible in the relevant subject/predicate/time/authority scope.
- `SUPERSESSION`: one durable Memory revision validly replaces an earlier semantic revision.

Contradiction does not automatically imply supersession.

Authority/Belief resolution identifies explicit contradiction identity (`contradictsMemoryId` or equivalent typed reference). Memory Admission consumes that resolution; it must not invent semantic contradiction from mere text difference or shared actors.

Temporal change is not contradiction by default.

## 28. Adult/intimacy universal semantics

MIP separates four concepts that must never be conflated:

### 28.1 Relationship stable dimension

Examples:

```text
matrix.relationship.attraction
matrix.relationship.sexualInterest
matrix.relationship.intimacy
matrix.relationship.sexualComfort
```

### 28.2 Current affective dimension

Examples:

```text
matrix.affective.desire
matrix.affective.arousal
matrix.affective.tension
```

### 28.3 Current intimacy/consent dimension

Examples:

```text
matrix.consent.status
matrix.consent.boundary
matrix.consent.coercion
matrix.intimacy.roleplayContext
```

### 28.4 Memory

Memory may preserve episodes, declared preferences, boundaries, corrections or prior intimate experiences as ordinary structured evidence/history.

Hard invariant:

```text
sexualInterest HIGH + currentDesire HIGH != consent GRANTED
```

Dominance/role language alone does not imply coercion. Consent/coercion resolution is contextual and separately owned.

Adult/intimacy semantic presence alone must not produce:

```text
block
censor
automatic confidence penalty
automatic persistence penalty
automatic affective penalty
```

All project-authored sexual training/test material remains adult-only.

## 29. Belief, Memory and World separation

World Truth, subjective Belief and Memory history must remain independently representable.

Examples:

```text
WORLD EVENT: door is locked
OBSERVATION: Luna sees the closed door
BELIEF: Luna believes the door is locked
MEMORY: Luna remembers seeing the door closed earlier
```

These may support each other but are not the same record/state.

A Memory record may correctly represent a report that the agent does not believe:

```text
"Marco said X"
```

Memory correctness therefore does not imply belief in `X`.

## 30. Diagnostic protocol

MIP reuses the single Assembling diagnostic path. No parallel diagnostic subsystem is authorized.

Every boundary should expose observable facts such as:

```text
producer
payloadType
inputRefs
outputRefs
snapshotId
status
reasonCodes
confidence fields
latency/metadata where available
```

`firstDivergence` remains write-once.

`reasonCodes` are deterministic/observable diagnostic codes only; private chain-of-thought is never stored.

## 31. Reason-code namespace

Reason codes use:

```text
<DOMAIN>.<CONDITION>[.<DETAIL>]
```

Examples:

```text
UNDERSTANDING.UNRESOLVED_SUBJECT
CONTEXT.DOMAIN_UNAVAILABLE.RELATIONSHIP
RETRIEVAL.NO_MATCH
RETRIEVAL.INDEX_UNAVAILABLE
AUTHORITY.OWNER_UNRESOLVED
MEMORY.PRE_RESPONSE_STABLE_WRITE
MEMORY.CONTRADICTION_IDENTIFIED
OUTPUT.UNSUPPORTED_FACT
```

Reason codes are additive/versioned and must not carry free-form hidden reasoning.

## 32. Protocol versioning and compatibility

- `MIP-1.0` defines semantics, not yet full Kotlin implementation parity.
- Breaking meaning/required-field changes require a major MIP version.
- Additive optional fields/enum extensions require compatibility review and a minor version when they affect consumers.
- Current `MatrixTurnFrame`, `NluOutput`, `SemanticFrame`, `TypedClaim` and integration ports are implementation predecessors/compatibility surfaces.
- Migration is incremental; do not rewrite working modules solely for aesthetic conformance.
- Compatibility adapters may exist, but they must not define a second semantic authority.

## 33. Current implementation gaps

The following MIP concepts are canonical design but not yet fully wired in Assembling:

```text
MatrixEnvelope<T> formal Kotlin type
EntityRef formal type
Predicate registry
TemporalRef formal type
ProvenanceRef formal type
confidence typed wrappers
MatrixContextSnapshot / ContextEntry
read-only context ports
always-on Memory index probe
RetrievalQuery / RetrievalResult
StateProposal
canonical Relationship controller
canonical Intimacy/Consent resolver
real BeliefState
real OutputValidator
real PersistentConsolidation
```

These gaps must remain explicit `NON_CABLATO` / `NOT_IMPLEMENTED`; do not simulate them.

## 34. Cross-repository conformance rule

Assembling owns MIP semantics.

Other repositories must eventually state which MIP version they implement and map their local types to it.

Examples:

```text
matrix-understanding-lab
-> produces NLU/TypedClaim evidence conforming to MIP

memoria
-> implements Memory/Retrieval/Admission payloads conforming to MIP

affective / relationship / future reflection
-> consume/produce MIP snapshots/events/proposals
```

A module repository may extend its internal data model, but its public boundary must not redefine shared terms such as `subject`, `owner`, `perspective`, `confidence`, `context`, `source`, `authority`, `unknown`, `contradiction` or `supersede`.

## 35. Non-negotiable invariants summary

```text
ONE SEMANTIC LANGUAGE.
ONE CONTEXT FORMAT.
ONE ENTITY REFERENCE MODEL.
ONE TEMPORAL MODEL.
ONE PROVENANCE MODEL.
ONE CONFIDENCE TAXONOMY.
ONE EVENT/TRACE LANGUAGE.

TEXT IS INTERPRETED ONCE.
NO DOWNSTREAM LINGUISTIC RE-PARSING.

EVERY CANONICAL STATE HAS ONE OWNER.
CONTEXT IS READ-ONLY.

MISSING != UNKNOWN != UNRESOLVED != AMBIGUOUS != UNAVAILABLE != NO_MATCH.

EVERY TURN QUERIES THE MEMORY INDEX.
INDEX PROBE != FULL MEMORY LOAD.

CLAIM != BELIEF != MEMORY != STATE != CONTEXT.
RELATIONSHIP != AFFECTIVE != INTIMACY.
SEXUAL INTEREST != CURRENT DESIRE != CONSENT.
CONTRADICTION != SUPERSESSION.

INTERPRETATION CONFIDENCE
!= SOURCE RELIABILITY
!= AUTHORITY
!= BELIEF CONFIDENCE
!= RETRIEVAL RELEVANCE.

NO DURABLE MEMORY WRITE BEFORE VALIDATION.
AUTHORITY IDENTIFIES CONTRADICTION.
MEMORY ADMISSION DOES NOT INVENT IT.
SEMANTIC CHANGE -> SUPERSEDE.

NO FAKE DATA FOR NON-WIRED MODULES.
FUTURE MODULES ADAPT TO MIP; MIP IS NOT REWRITTEN PER MODULE.
```

## 36. Next implementation checkpoint

This document is the canonical semantic specification. It does not authorize a large immediate rewrite.

Next Assembling implementation work, when explicitly authorized, should proceed incrementally:

```text
1. define the minimal typed MIP core primitives required by current boundaries;
2. add MatrixContextSnapshot/read-only context contract;
3. add always-on Memory index-probe read contract without persistence;
4. migrate current compatibility strings/nulls behind adapters;
5. preserve all existing P0/P1/P2 regression gates;
6. implement further domains only when their real module is ready.
```

Memory persistence, Relationship, Intimacy/Consent, Decision and future Reflection remain separate implementation workstreams.