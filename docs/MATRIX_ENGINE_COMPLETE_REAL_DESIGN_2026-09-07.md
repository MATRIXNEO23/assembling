# Matrix Engine — Complete Real Design + Architecture Reality Audit

Date: 2026-09-07  
Repository owner: `MATRIXNEO23/assembling`  
Design branch at start: `functional-audit-understanding-authority`  
Start HEAD: `9fc0c39dafb4482194a213255018be6d4befde6d`  
Status: **CANONICAL DESIGN BASELINE / NO PRODUCTION IMPLEMENTATION AUTHORIZED BY THIS DOCUMENT**

## 0. Purpose

This document exists to prevent Matrix Engine from being built by discovering fundamental module responsibilities only after implementation has already started.

The target is not to forbid future changes. The target is to know the complete real product architecture, ownership, data contracts, runtime order, persistence boundaries and major algorithms before implementing the remaining modules. Later changes must be driven by demonstrable defects, benchmarks or new owner-approved product requirements, not by avoidable architectural omissions.

The project method is:

```text
REAL PRODUCT REQUIREMENT
-> COMPLETE MODULE RESPONSIBILITY
-> INPUT/OUTPUT/OWNER CONTRACT
-> RESEARCH EXISTING ALGORITHMS/LIBRARIES
-> REUSE / ADAPT / REIMPLEMENT / REFERENCE / REJECT DECISION
-> IMPLEMENT
-> REAL DEMONSTRATIVE TEST
-> FIX OBSERVED DEFECTS
-> REGRESSION
```

A green compile, DTO test or fixture-only integration test never proves a cognitive module works.

---

# 1. Product-level invariants

## 1.1 Fundamental separation

```text
OBSERVE != UNDERSTAND != BELIEVE != REMEMBER != FEEL
!= RELATE != CONSENT != WANT != DECIDE != EXPRESS
```

Each mutable canonical state has one logical owner.

```text
WORLD objective state              -> WORLD / APPLICATION ADAPTER
linguistic interpretation          -> NLU + UNDERSTANDING
belief / epistemic commitment      -> BELIEF / AUTHORITY
long-term autobiographical memory  -> MEMORY
transient emotion + mood           -> AFFECTIVE
relationship state                 -> RELATIONSHIP
current intimacy / consent state   -> INTIMACY
active goals / intentions           -> GOAL
behavioral choice                  -> DECISION
language realization              -> GGUF
post-validation commit routing     -> PERSISTENT CONSOLIDATION
```

No downstream module reparses natural language to recreate linguistic information already emitted by Understanding.

## 1.2 Confidence values remain distinct

```text
interpretationConfidence
!= sourceReliability
!= authority / epistemicClass
!= beliefConfidence
!= retrievalRelevance
!= decisionConfidence
```

## 1.3 Missing states remain explicit

Use the MIP distinction:

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

A missing module must never emit a fake zero/default state.

## 1.4 Durable state ordering

No durable state write occurs before an output/action has passed validation.

```text
PRE-RESPONSE
READ -> ENRICH -> RESOLVE -> PROPOSE -> DECIDE -> REALIZE -> VALIDATE

POST-VALIDATION
COMMIT -> owner-specific durable operations
```

Memory, Relationship, Affective and Goal updates may be proposed before response generation, but the authoritative durable mutation occurs through their owner after validation.

## 1.5 World remains objective authority

Matrix may select an `ActionIntent`. It must not assume that the action happened.

```text
Matrix ActionIntent
-> World validation/execution
-> ActionResult
-> World Event
-> Perception/Observation
```

This preserves the difference between intention, attempted action and objectively completed result.

---

# 2. Complete normal-turn architecture

```text
WORLD / USER / NPC / SYSTEM EVENT
                |
                v
        [0] PERCEPTION GATEWAY
                |
                v
           Observation(s)
                |
                v
           [1] NLU RUNTIME
                |
                v
        [2] UNDERSTANDING V3
                |
                v
       canonical TypedClaim[]
                |
                v
     [3] TURN WORKSPACE / CONTEXT
                |
       +--------+---------+---------+---------+---------+---------+
       |        |         |         |         |         |         |
       v        v         v         v         v         v         v
     WORLD    MEMORY    BELIEF  RELATIONSHIP AFFECTIVE INTIMACY  GOAL
     READ     INDEX     READ       READ       READ      READ      READ
              PROBE
       |        |         |         |         |         |         |
       +--------+---------+---------+---------+---------+---------+
                |
                v
      MatrixContextSnapshot
                |
                v
       [4] COHERENCE VALIDATOR
                |
                v
     [5] AUTHORITY / BELIEF RESOLUTION
                |
       +--------+-------------------------------+
       |                                        |
       v                                        v
 [6] MEMORY PREFLIGHT                    BELIEF PROPOSALS
       |
       +-----------+-----------+-----------+-----------+
       |           |           |           |           |
       v           v           v           v           v
 [7] AFFECTIVE  [8] RELATION  [9] INTIMACY [10] GOAL [11] REFLECTION*
     APPRAISAL     EVAL          EVAL        EVAL      IF TRIGGERED
       |           |           |           |           |
       +-----------+-----------+-----------+-----------+
                |
                v
        StateProposal[] / evidence
                |
                v
      [12] OPPORTUNITY MANAGER
                |
                v
        candidate initiatives
                |
                v
       [13] DECISION / UTILITY
                |
                v
          DecisionSnapshot
         ReplyIntent/ActionIntent
                |
                v
       [14] EXECUTION PLANNER*
        BT/FSM only if needed
                |
                v
 [15] REALIZATION CONTEXT SUPERVISOR
                |
                v
        [16] PROMPT BUILDER
                |
                v
           [17] GGUF
                |
                v
       [18] OUTPUT VALIDATOR
                |
         ACCEPT / REJECT
                |
                v
 [19] PERSISTENT CONSOLIDATION
       |           |           |           |
       v           v           v           v
    MEMORY     AFFECTIVE  RELATIONSHIP    GOAL/...
   ADMISSION     OWNER       OWNER          OWNER
   REPOSITORY
                |
                v
      committed causal trace
```

`*` indicates event-driven/optional execution, not every-turn mandatory work.

Independent state reads during Context ENRICH should use bounded structured concurrency. Causally dependent decisions/writes remain ordered.

---

# 3. Module specifications and reality audit

## 3.0 Application / World Adapter

### Real responsibility

Own objective world facts and translate application-specific entities/events/actions into generic Matrix contracts. Neon Tides-specific game logic must remain outside Matrix Core.

### Input
- game/world state changes;
- time/location/resource changes;
- NPC/system events;
- Matrix `ActionIntent`.

### Output
- `MatrixEvent` / `WorldMatrixProtocol.Event`;
- per-agent `Observation` after visibility/perception filtering;
- read-only world Context entries;
- `ActionResult`.

### Must not
- create subjective beliefs;
- write Memory;
- decide how Luna feels;
- reinterpret user language.

### Existing assets
`8.10.9evo3-solo-gpt/.../core/WorldMatrixProtocol.kt` already defines Entity, Location, Time, Resource, Event, Observation, ActionIntent and ActionResult.

### Decision
**ADAPT / PORT.** Reconcile names/IDs with current MIP rather than replacing this work.

---

## 3.1 Perception Gateway

### Real responsibility
Normalize heterogeneous observable events while preserving objective source, identity, time and visibility. It answers **what reached this Matrix agent**, not what the event means.

### Input
User text, NPC text, world/system/time/sensor/knowledge events.

### Output
Typed observations containing stable event/source IDs, observer, source type, content/payload, event time and provenance.

### Existing assets
Old Android `PerceptionEnvelope.kt` already validates event identity/source and separates USER_TEXT from other perceptions.

### External reference
CloudEvents v1.0 provides a mature event-envelope pattern with required `id`, `source`, `type`, `specversion` and optional subject/time/data. Matrix should adapt the identity/provenance principles, not import an unnecessary cloud event runtime.

### Decision
**ADAPT old Matrix code + REFERENCE_ONLY CloudEvents.** Final gateway must support multiple simultaneous perceptions rather than reducing the turn to a single primary user string.

---

## 3.2 NLU Runtime

### Real responsibility
Convert raw natural language into learned linguistic evidence. This is the only stage that should depend on Matrix-NLU model inference.

### Input
Raw text + locale + speaker/observer identities.

### Output
`MATRIX_NLU_CONTRACT_V3`: claim-level dialogue act, predicate, referents, polarity, temporal relation/evidence, spans, confidence, alternatives, statuses and claim kind.

### Must not
- decide truth/Authority;
- persist Memory;
- set Relationship/Affective/Consent state;
- apply gameplay censorship/policy.

### Existing assets
Student-4 v2.2A mixed/head-protected ONNX remains the current owner-approved runtime candidate. `CanonicalUnderstandingV3Adapter` already defines the bridge boundary, but `assembling` has no concrete production `MatrixNluV3RuntimeBridge`.

### External implementation
ONNX Runtime Mobile has official Android Java/C/C++ support, CPU/XNNPACK and NNAPI execution providers. Official custom builds can reduce the Android runtime by compiling only operators needed by the target model.

### Decision
**DIRECT_REUSE ONNX Runtime Android (MIT).** Benchmark CPU/XNNPACK first for quantized Student-4, then NNAPI on Moto G56; accelerator selection is evidence-driven.

---

## 3.3 Understanding V3

### Real responsibility
Losslessly convert NLU V3 output into universal Matrix/MIP semantic claims. Understanding describes the language interpretation; it does not decide whether claims are true.

### Required output
For every claim preserve:
- claim identity;
- speaker/observer/source/subject/target/owner/perspective;
- canonical PredicateId;
- typed object/value evidence;
- dialogue act / claim kind / polarity / modality;
- temporal relation/anchor/evidence;
- spans;
- alternatives/statuses;
- field and overall interpretation confidence;
- provenance.

### Existing assets
`CanonicalUnderstandingV3Adapter.kt` is conceptually aligned: it validates the frozen envelope, never reparses text, preserves multi-claim evidence and emits canonical V3 observations.

### Legacy rejection
`UnderstandingLabAdapter` is compatibility-only and must not remain the final canonical path.

### Decision
**KEEP/PORT canonical V3 adapter; retire legacy path from authoritative orchestration after real runtime integration.**

---

## 3.4 TurnWorkspace + Context Assembler

### Real responsibility
Hold turn-bounded operational data and create immutable snapshots of canonical state for consumers. It does not own the canonical state it reads.

### Workspace contents
- observations;
- NLU evidence;
- TypedClaims;
- context snapshots;
- retrieval queries/results;
- Coherence/Authority results;
- state proposals;
- DecisionSnapshot;
- realization package;
- generated output and validation;
- diagnostics.

### Context domains
`WORLD`, `MEMORY`, `BELIEF`, `RELATIONSHIP`, `AFFECTIVE`, `INTIMACY`, `GOAL`, `LINGUISTIC`, `SYSTEM`.

### Runtime rule
Independent read-only providers are executed concurrently inside the turn's structured coroutine scope. Every result carries availability independently from content.

### Existing assets
- MIP `MatrixContextSnapshot` contract;
- old Android `MatrixEngineFramework.Stage<I,O>` / `StageResult` gives a useful small typed-stage skeleton;
- old `ContextPackageBuilder` provides useful budget-aware, non-destructive packaging patterns.

### External implementation
`kotlinx.coroutines` structured concurrency (Apache 2.0) supplies bounded parent-child lifetimes, cancellation and error propagation.

### Decision
**ADAPT typed stage framework; DIRECT_REUSE kotlinx.coroutines; BUILD canonical Context Assembler.** Do not port old monolithic orchestrator.

---

## 3.5 Coherence Validator

### Real responsibility
Check that the interpreted semantic structure is internally usable and sufficiently resolved for downstream reasoning.

### Checks
- structural V3 validity;
- required field status compatibility;
- critical confidence thresholds where justified;
- no malformed spans/IDs;
- cross-field consistency such as resolved role requiring resolved identity;
- claim-by-claim fail-closed handling;
- no silent UNKNOWN→fact conversion.

### Must not
- classify REPORT/BELIEF authority;
- find Memory contradictions;
- decide persistence;
- decide behavior.

### Current defect
`BasicCoherenceGuard` currently emits policy/epistemic outcomes such as `REPORT_ONLY`, `QUESTION_ONLY` and multi-claim transient behavior. Those responsibilities belong downstream.

### Decision
**FIX/REIMPLEMENT narrow validator using V3/MIP fields.** Preserve useful confidence/error diagnostics, remove Authority/persistence decisions.

---

## 3.6 Authority Resolver

### Real responsibility
Resolve epistemic class, source/perspective/owner requirements, source reliability when available and semantic contradiction identity against structured retrieved evidence.

### Input
TypedClaim + ContextSnapshot + claim-bound RetrievalResult + candidate evidence + provenance.

### Output
`AuthorityResolution`:
- claim/context/retrieval binding;
- `WORLD_TRUTH / OBSERVATION / REPORT / INFERENCE / BELIEF`;
- source reliability if known;
- resolution confidence;
- exact `contradictedMemoryRef` status;
- candidate refs;
- reason codes/ambiguity;
- provenance.

### Must not
- persist Memory;
- treat interpretation confidence as Authority;
- infer contradiction from actor overlap/text difference;
- treat temporal change as contradiction by default.

### Existing assets
`DeterministicAuthorityResolver` is structurally strong. The active audit branch also fixes explicit multi-claim retrieval binding by `claimId + contextSnapshotId`.

### Decision
**KEEP + finish canonical orchestration/testing.**

---

## 3.7 BeliefState Owner

### Why it exists
Memory and belief are not the same thing. Matrix can remember that somebody said something without currently believing it. Authority resolution is an evaluation event; BeliefState is Luna's current epistemic commitment.

### Real responsibility
Maintain current subjective beliefs with explicit support/counterevidence and confidence, separate from historical Memory.

### Belief record must include
- beliefId;
- subject/predicate/value/target/owner/perspective scope;
- current status: ACCEPTED / UNCERTAIN / DISPUTED / REJECTED / UNKNOWN;
- beliefConfidence;
- supporting evidence refs;
- counterevidence refs;
- authority/source reliability summaries;
- valid temporal scope;
- provenance;
- version/revision.

### Research reference
AGM belief revision is useful as **REFERENCE_ONLY** for minimal-change revision/contraction/expansion concepts. Matrix should use a pragmatic evidence-backed belief base rather than a fully deductively closed theorem system.

### Decision
**BUILD small belief-base owner, REFERENCE AGM/Jason source-aware beliefs.**

---

# 4. Memory subsystem — complete design

Memory is one logical owner but has distinct read, proposal and durable-write responsibilities. These are designed together so the store is able to retain and retrieve the information the real engine needs.

## 4.1 Memory semantic kind is not information category

Canonical semantic kinds:

```text
EPISODIC
SEMANTIC
REFLECTION
```

These answer *what kind of memory record is this?*

They do not replace semantic domain/predicate classification.

Examples:

```text
"Alberto vive a Milano"
kind=SEMANTIC
predicate=matrix.location.live_at

"Ieri Alberto è andato al club"
kind=EPISODIC
predicate=matrix.activity.visit

"Luna conclude che Alberto tende a evitare i club"
kind=REFLECTION
predicate=matrix.behavior.avoid
```

Relationship, Affective, Goal and Intimacy are separate **state owners**, but Memory may preserve historical evidence/events concerning those domains.

## 4.2 Final MemoryRecord information surface

The durable record must be general enough to support retrieval by semantic meaning, entity, source, time and provenance rather than only text similarity.

Required axes:

```text
IDENTITY
- memoryId
- schemaVersion
- memoryKind
- semanticDomain
- predicateId

SEMANTIC ROLES
- subjectRef
- targetRef
- ownerRef
- sourceRef
- perspectiveRef
- observerRef when relevant
- entityRefs[] / actorRefs[]

VALUE
- typedValue / scalar value
- valueType
- rawEvidenceText / canonicalSummary where useful
- polarity / modality

TIME
- eventTime
- validFrom
- validTo
- observedAt
- recordedAt
- temporalReferenceId / granularity

EPISTEMIC
- epistemicClass / authority
- sourceReliability
- interpretationConfidence provenance link
- admissionConfidence / belief evidence where appropriate

PROVENANCE
- source event/observation/claim IDs
- AuthorityResolution ID
- derivedFrom IDs
- quoted source

LIFECYCLE
- validity/current state
- revisionOf lineage root
- supersededBy sequential successor
- revisionCount
- contradictsMemoryId if explicit

RETRIEVAL METADATA
- salience/importance
- retention tier
- reinforcement/evidence count
- access count / last accessed
- optional embedding/version
- indexable normalized entity/predicate/time fields
```

The old Python Memory Foundation already provides valuable persistence/lineage/rollback foundations, but the final Kotlin schema must add the structured MIP semantic-role/predicate/value/time fields required for adequate retrieval.

## 4.3 Memory Retrieval Service

### Level 1 — INDEX_PROBE, every normal turn

Structured query built from current claims/context, not raw-language re-parsing.

Hard filters/index dimensions should include as applicable:
- agent/owner namespace;
- subject/entity IDs;
- predicate/domain;
- target/source/perspective;
- current-vs-history purpose;
- temporal ranges;
- location/goal/event refs.

The result explicitly differentiates `MATCHED`, `NO_MATCH`, `AMBIGUOUS`, `INDEX_UNAVAILABLE`, `ERROR`.

### Level 2 — HYDRATE_AND_RERANK

For candidate records only:
- SQLite FTS BM25 lexical retrieval over content/summary/value/entity labels;
- structured exact matches boosted before soft semantic similarity;
- optional embedding score if mobile benchmark justifies an embedding model;
- recency appropriate to query purpose;
- salience/importance;
- source/record confidence;
- access/reinforcement signals.

### Level 3 — DEEP_OR_MULTI_HOP

Only for explicit complex retrieval/reflection purposes. It may follow entity/lineage/source links within bounded limits. Do not run graph-style deep retrieval every turn.

### Ranking algorithms
- old Matrix hard-filter-first retrieval is a good architecture to ADAPT;
- replace manual token overlap with SQLite FTS/BM25;
- benchmark a calibrated convex/weighted fusion against RRF;
- optional MMR post-ranking to avoid returning redundant near-duplicate memories;
- never hard-code old HybridMemoryRanker weights without benchmark evidence.

### External references
- SQLite FTS5 built-in BM25: DIRECT_REUSE through SQLite/Room;
- RRF (Cormack et al. 2009): REIMPLEMENT tiny formula as benchmark candidate;
- MMR (Carbonell & Goldstein 1998): REIMPLEMENT tiny optional diversity reranker;
- Graphiti: REFERENCE_ONLY for temporal validity, provenance and entity-aware retrieval; its Neo4j/Python/LLM stack is unsuitable as an Android dependency;
- Mem0/LangMem/Letta: REFERENCE_ONLY for memory lifecycle/search API patterns, not embedded dependencies.

## 4.4 Memory Preflight / Acquisition

### Real responsibility
Transform canonical structured claim + AuthorityResolution + retrieval evidence into an ephemeral `MemoryCandidate`.

It decides proposal properties such as:
- whether the content is memory-worthy;
- EPISODIC/SEMANTIC/REFLECTION kind;
- semantic domain/predicate/value mapping;
- proposed retention/salience;
- explicit contradiction target carried from Authority;
- provenance.

It must not parse raw text with regex and must not write persistence.

### Legacy rejection
Old Android `MemoryAcquisitionClassifier.kt` directly regex-parses user text into name/family/age/work/location/preference/fear/goal. This violates the current “interpret language once” rule.

### Decision
**REJECT legacy parser as production code; retain its examples as regression fixtures. BUILD acquisition from canonical TypedClaims.**

## 4.5 Memory Admission

### Real responsibility
After accepted output/action validation, decide the durable operation from the proposed candidate and resolved evidence.

Canonical decisions:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Reinforcement is a metadata/evidence operation, not semantic overwrite.

### Rules
- only explicit contradiction identity can drive conflict/supersession;
- correction is evidence, not automatic supersession;
- higher authority may outrank lower authority according to policy;
- same authority can use reliability/confidence/recency where applicable;
- ambiguous/unresolved contradiction fails closed;
- duplicate/idempotent events do not create duplicate memories;
- semantic change uses `supersede()`.

## 4.6 MemoryRepository / Android persistence

### Required behavior
- real Room/SQLite storage;
- atomic SAVE/SUPERSEDE/metadata operations;
- FK-protected lineage;
- current/history queries;
- structured indexes + FTS;
- restart/reopen persistence;
- duplicate/idempotency handling;
- no physical delete that breaks referenced lineage;
- stable schema/version migrations when needed.

### Existing reference
Recovered Python Foundation supplies proven test semantics for WAL/FKs, rollback fault injection, root lineage, semantic update protection and restart integrity.

### External implementation
Android Room + SQLite is the selected production foundation. Room supplies transactional execution with rollback on exception/cancellation and supports FTS-backed entities; SQLite FTS5 supplies BM25.

### Decision
**DIRECT_REUSE Room/SQLite APIs; PORT semantics/tests from recovered Python; do not port old snapshot-wide `replaceAll` storage model.**

---

# 5. Affective subsystem

## 5.1 Affective Appraisal Owner

### Real responsibility
Compute Luna's transient emotional reaction and mood dynamics from validated/structured events and current state.

Owns:
- appraisal dimensions;
- transient emotion intensities;
- valence/arousal/dominance or equivalent compact state;
- mood baseline/drift;
- decay/regulation;
- emotion cause/evidence IDs;
- manifestation influence supplied to Decision.

Does not own:
- relationship trust/affection/attraction;
- current consent;
- Memory;
- behavioral choice.

### Existing assets
`vendor/matrix-affective-lab/src/affective_engine.py` and `AffectiveLabAdapter.kt` contain useful appraisal/decay/emotion/mood foundations. Relationship-like persistent fields inside the prototype must be moved out rather than becoming parallel Relationship authority.

### External references
- FAtiMA Toolkit (Apache 2.0): Emotional Appraisal, Emotional Decision Making, Social Importance; useful algorithm/source reference and code mining source where license/provenance permits;
- Gamygdala (MIT): lightweight event appraisal/emotion pattern;
- OCC/appraisal literature: conceptual reference.

### Decision
**ADAPT existing Matrix affective implementation; selectively reuse Apache/MIT ideas/code only after per-file provenance review.**

---

# 6. RelationshipState Owner

## Real responsibility
Maintain a directional, slowly evolving relationship state between entities. It reflects accumulated interaction history but is not itself a Memory record.

Suggested stable dimensions, to be validated against Neon Tides behavior needs:
- familiarity;
- trust;
- affinity/closeness;
- affection;
- attraction/sexual interest as a stable relational dimension;
- respect/admiration;
- resentment/aversion;
- intimacy/comfort history.

Each dimension must carry:
- subject and target;
- value;
- update evidence/source IDs;
- last update/version;
- bounded rate/change policy;
- confidence where appropriate.

Current desire and current consent are not RelationshipState.

### Inputs
Only accepted committed events/proposals, not unvalidated raw claims.

### Research
FAtiMA Social Importance and social-relational agent research provide patterns for event-driven relation updates.

### Decision
**BUILD small Matrix-native directional state owner; ADAPT relational appraisal concepts; do not store canonical relationship values in Affective or Memory.**

---

# 7. Intimacy / Consent Owner

## Real responsibility
Maintain current contextual interpretation of intimacy state, desire, boundaries and consent for the active interaction.

Must distinguish:

```text
stable attraction/sexual interest
!= current desire/arousal
!= current comfort/boundary
!= consent for a specific action/context
!= historical memory of prior interactions
```

Consent must be action/context/time scoped, revocable, and never inferred solely from affinity, attraction or previous consent.

### Input
Structured consent/boundary claims, current context, Relationship/Affective read-only snapshots, World/action context.

### Output
Current intimacy/consent snapshot + reason/evidence refs for Decision.

### Decision
**BUILD Matrix-native owner.** No large external framework is required; MIP semantics and explicit state machine are preferable.

---

# 8. Goal / Intention — BDI-lite

## Real responsibility
Represent what the agent currently wants to achieve and which intention it is committed to pursuing. Goals are state, not memories.

### Goal contract
- goalId;
- owner;
- target condition / desired state;
- source/provenance;
- priority/utility bias;
- status ACTIVE/SUSPENDED/ACHIEVED/ABANDONED/BLOCKED;
- temporal constraints;
- preconditions;
- success/failure criteria.

### Intention contract
- intentionId;
- goalId;
- selected course/action family;
- commitment strength/hysteresis;
- evidence and reason codes;
- current status.

### Existing assets
Old Android `AgentStateGoalIntentionStage.kt` correctly separates state/goals/intentions from Memory but only filters/sorts them; it is not a full BDI implementation.

### External reference
Jason is a mature AgentSpeak BDI interpreter (LGPL-3.0). It is useful as an architectural reference, but importing a full logic programming interpreter would add excessive complexity for the embedded Matrix use case.

### Decision
**ADAPT existing Matrix contracts; BUILD lightweight BDI state transitions; Jason REFERENCE_ONLY.**

---

# 9. Reflection

## Three real levels

### MICRO
Fast, event-driven, only when triggers justify it. Example: detect an emerging pattern after multiple relevant events.

### DECISION
Used when the current decision is ambiguous/high-impact and a bounded inference over evidence could materially change choice.

### MACRO
Delayed/idle/session-end consolidation of patterns, self-model and longer-term hypotheses.

### Real responsibility
Produce explicit derived `ReflectionCandidate` / Belief or StateProposal with:
- source evidence IDs;
- derived predicate/value;
- confidence;
- temporal scope;
- reason codes;
- ability to abstain.

Reflection never writes Memory or Relationship directly. A persistent reflection memory must pass Authority/Admission like other derived claims.

### Existing assets
Old ReflectionTrigger/Queue/Processor architecture and source-provenance pattern are useful. `ReflectionEngine.kt` itself currently concatenates groups of high-importance memories; that is not sufficient as final reflective inference.

### External reference
Generative Agents is REFERENCE_ONLY for memory/reflection/planning separation and importance-triggered reflection.

### Decision
**PORT trigger/queue/budget/provenance patterns; REIMPLEMENT actual structured inference.**

---

# 10. Opportunity Manager

## Real responsibility
Determine whether a possible initiative should become a candidate now. This is different from selecting the final behavior.

Examples:
- initiate a conversation;
- ask a follow-up question;
- invite someone;
- revisit a goal;
- respond to a world event without direct user input.

### Input
World/time events + Context + Goals + Relationship + Affective + recent interaction + cooldown/history.

### Output
`OpportunityCandidate[]` containing trigger/evidence, urgency, relevance, expiry, interruption cost and action family.

### Decision
**BUILD small Matrix-native module.** Avoid running the LLM merely to decide whether a candidate opportunity exists.

---

# 11. Decision / Utility Engine

## Real responsibility
Choose what Luna should do, not how to phrase it.

### Candidate generation
Sources:
- respond/reply candidates;
- active intentions;
- opportunity candidates;
- application/world-valid action families;
- abstain/do-nothing/clarify candidate.

### Utility features
Initial explainable features:

```text
+ personalityFit
+ goalProgress
+ emotionalFit
+ relationshipFit
+ contextualFit
+ memorySupport
+ novelty
- risk
- interruptionCost
- repetitionPenalty
```

Weights must be configured and benchmarked rather than treated as universal constants.

### Selection
- hard constraints/preconditions first;
- utility score second;
- hysteresis to avoid intention thrashing;
- controlled stochastic tie-break only when candidates are genuinely close;
- explicit abstain/clarify behavior when evidence is insufficient.

### DecisionSnapshot output
- decisionId;
- candidates and component scores;
- selected ReplyIntent/ActionIntent;
- confidence/margin;
- reason codes;
- evidence/source IDs;
- allowed facts;
- forbidden claims;
- approved pre-commit StateProposals;
- manifestation style constraints.

### External references
- BDI concepts: Jason REFERENCE_ONLY;
- Behavior Trees/FSM from gdx-ai are useful for tactical execution, not global cognition;
- no reason to import a large planning engine until benchmarks show BDI-lite + Utility insufficient.

### Decision
**BUILD Matrix-native explainable Utility engine.**

---

# 12. Behavior / Action Executor

## Real responsibility
Execute a selected multi-step behavior/action plan when a single ActionIntent is insufficient. It is downstream of Decision.

### Suitable implementation
Small Behavior Tree or FSM executor with explicit success/failure/running state and World action boundaries.

### External source
`gdx-ai` provides Java Behavior Trees/FSM under Apache 2.0 and can be mined/reused selectively after dependency-size analysis.

### Decision
**OPTIONAL ADAPT/DIRECT_REUSE selected gdx-ai code, or REIMPLEMENT tiny executor.** Do not make BT the cognitive decision owner.

---

# 13. Interaction Manager

## Real responsibility
Track multi-party interaction sessions independently from long-term relationship state.

Owns:
- current participants/interlocutors;
- turn ownership;
- conversation/session IDs;
- who can observe each event;
- joins/leaves/interruption;
- group-vs-private context;
- current attention target.

### Why needed
A multi-NPC game cannot safely infer source/target/perspective and private/shared context from a single chat string alone.

### Decision
**BUILD Matrix-native module.**

---

# 14. Cognitive LOD Scheduler

## Real responsibility
Bound expensive cognition according to significance without changing semantic truth.

Possible levels:

```text
LOD0 — dormant/basic event tracking
LOD1 — cheap perception/context/index updates
LOD2 — active conversational cognition
LOD3 — expensive reflection/deep retrieval/complex planning
```

Inputs include proximity/interaction relevance, active goals, relationship significance, recent changes, unresolved conflicts and explicit user interaction.

### External reference
Game-engine significance/update managers provide the appropriate pattern: significance controls update rate/cost, not semantics.

### Decision
**BUILD small Matrix-specific scheduler; external engines REFERENCE_ONLY.**

---

# 15. Context Supervisor / Realization Package

## Real responsibility
After Decision, construct and validate the minimal evidence package that the language model needs to realize the selected intent.

Checks:
- required evidence present;
- ownership/provenance valid;
- duplicate blocks removed;
- context budget respected;
- forbidden internal data excluded;
- selected facts correspond to DecisionSnapshot allowed facts;
- non-wired domains remain explicit.

### Existing assets
Old `ContextPackageBuilder.kt` and `ContextSupervisor.kt` provide reusable budget/dedup/knowledge-boundary patterns.

### Decision
**PORT/ADAPT.** Replace heuristic text blacklist as a primary semantic safeguard with typed package constraints; text checks may remain a secondary defense.

---

# 16. Prompt Builder

## Real responsibility
Deterministically serialize the selected decision and approved context into instructions for the GGUF backend.

Input should be `DecisionSnapshot + RealizationPackage`, not a mixture of legacy SemanticFrame/Authority/Affective states from which it has to choose behavior.

Must preserve:
- required polarity/negation;
- referents;
- time;
- source/report framing;
- consent/boundary conditions;
- allowed facts/forbidden claims;
- manifestation style.

### Existing asset
`SemanticFrameToPrompt.kt` correctly states `REALIZATION_ONLY`; its contract must be updated once DecisionSnapshot exists.

### Decision
**KEEP principle, ADAPT final input contract after Decision owner is implemented.**

---

# 17. GGUF / Backend Adapter

## Real responsibility
Natural-language realization only.

It may choose wording/style within DecisionSnapshot constraints. It does not own Memory, truth, Relationship, Consent or behavioral policy.

### Existing reality
`EchoGgufAdapter` is smoke-only. Historical application already has backend abstraction separate from core.

### Target
Real llama.cpp GGUF path first; MLC remains a replaceable backend if later re-enabled. Model loading/readiness, JNI and storage remain outside cognitive state ownership.

### Decision
**BUILD/PORT backend adapter around existing Android runtime rather than embedding cognitive logic into llama.cpp.**

---

# 18. Output Validator

## Real responsibility
Validate the generated response/action representation before any durable state commit.

### Required checks
- a response exists and satisfies basic structural limits;
- required negation/polarity is not inverted;
- source/report framing is not promoted to truth;
- unresolved information is not fabricated as certain;
- consent/boundaries are not broadened or reversed;
- referents/temporal meaning are not materially altered;
- generated factual statements are supported by DecisionSnapshot allowed facts or explicitly marked as uncertainty/opinion;
- claimed physical/game action is not represented as completed before World `ActionResult` when execution confirmation is required;
- no forbidden internal instructions/diagnostics leaked.

### Implementation strategy
1. deterministic structural checks;
2. lightweight re-analysis of generated text through NLU/Understanding where semantic preservation matters;
3. compare resulting claims to required/forbidden semantic constraints;
4. on failure: at most a bounded regeneration with stricter realization constraints or deterministic safe fallback;
5. no state commit on rejected output.

### Decision
**BUILD Matrix-native validator; reuse the same NLU runtime instead of introducing another linguistic model.**

---

# 19. Persistent Consolidation

## Real responsibility
After accepted output/action, coordinate durable owner-specific proposals. It is a transaction coordinator, not a universal state owner.

Input:
- accepted DecisionSnapshot;
- OutputValidation result;
- StateProposal[];
- ActionResult where required;
- causal/provenance context.

Routes:
- MemoryCandidate -> Memory Admission/Repository;
- AffectiveProposal -> Affective owner;
- RelationshipProposal -> Relationship owner;
- GoalProposal -> Goal owner;
- other registered state owners.

Each owner validates its own preconditions. Cross-owner operations require explicit transaction/compensation semantics; do not silently leave half-applied state.

### Decision
**BUILD after owner contracts are known; keep current `PersistentConsolidationPort` seam.**

---

# 20. Diagnostics + Causal Trace

## Real responsibility
Make the entire cognitive/action cycle observable without storing private chain-of-thought.

Every stage should emit:
- trace/correlation ID;
- stage/span ID;
- parent/causation IDs;
- source/destination;
- input/output identity summaries;
- selected entity/claim/memory/proposal IDs;
- deterministic reason codes;
- status;
- confidence relevant to that module;
- timing and bounded size metrics;
- first divergence.

### Existing assets
Old `CausalTrace.kt` is a strong structured base but its stage enum is too short for the final pipeline.

### External references
- OpenTelemetry span model: parent/child operation, attributes, links, events, timestamps, status;
- W3C Trace Context: trace-id/parent-id propagation concepts;
- W3C PROV-O: entity/activity/agent and derivation concepts.

### Decision
**PORT/ADAPT old Matrix trace; REFERENCE standards; do not import full telemetry stack initially.**

---

# 21. Knowledge Source Adapter — optional read-only domain

For future local documents/lore/reference knowledge, expose a controlled read-only provider through Context/Authority.

It may supply evidence; it may not write Belief/Memory directly. The source and reliability must remain explicit.

---

# 22. What is intentionally NOT a separate cognitive owner

## Routing
The historical empty `routing/` module should not become another semantic parser. Routing needed for retrieval/processing can be derived from canonical TypedClaims and Decision/Context requirements.

## Memory category modules
Identity, location, preferences, family, work, relationship events, goals and intimacy information are semantic domains/predicates—not separate memory engines.

## GGUF reasoning authority
The LLM is not the hidden owner of memory/relationship/decision logic.

## Supervisor as universal fixer
Supervisor validates assembled evidence/package; it must not silently rerun or correct upstream cognition.

---

# 23. Parallelism model

Logical pipeline does not imply everything is serial.

## Safe parallel batch after Understanding
Within one bounded coroutine scope:

```text
WorldContextProvider.read()
MemoryRetrieval.indexProbe()
BeliefState.read()
RelationshipState.read()
AffectiveState.read()
IntimacyState.read()
GoalState.read()
```

Then construct a single immutable ContextSnapshot.

## Potential parallel proposal batch after Authority
Where input dependencies permit:

```text
MemoryPreflight
Affective appraisal
Relationship evaluation
Intimacy evaluation
Goal evaluation
```

Reflection remains trigger-controlled.

## Sequential barriers

```text
Understanding before semantic consumers
Context snapshot before Authority
Authority before contradiction-dependent Memory proposal
Decision before realization
GGUF before OutputValidator
OutputValidator before durable consolidation
World ActionResult before treating physical action as accomplished
```

Use Kotlin structured concurrency so cancellation/failure remains turn-scoped and children cannot leak beyond the request lifecycle.

---

# 24. External reuse matrix

| Candidate | Matrix purpose | License/status | Decision | Reason |
|---|---|---|---|---|
| ONNX Runtime Mobile | Student-4 inference | MIT | DIRECT_REUSE | official Android runtime, CPU/XNNPACK/NNAPI, custom reduced-op build |
| Kotlin coroutines | parallel Context/state reads | Apache-2.0 | DIRECT_REUSE | structured lifecycle/cancellation, native Kotlin fit |
| Android Room | durable state/Memory DB | AndroidX | DIRECT_REUSE | typed Android persistence and transactions |
| SQLite FTS5/BM25 | lexical Memory retrieval | SQLite built-in | DIRECT_REUSE | compact on-device full-text ranking |
| CloudEvents spec | Perception/event envelope ideas | spec/reference | REFERENCE_ONLY | mature id/source/type/time/dedup semantics; no need for SDK |
| W3C PROV | provenance semantics | W3C Recommendation | REFERENCE_ONLY | derivation/source modeling |
| OpenTelemetry/W3C Trace Context | causal trace model | standards | REFERENCE_ONLY | trace/span/link/event structure; full SDK unnecessary initially |
| FAtiMA Toolkit | appraisal/social/decision ideas | Apache-2.0 | ADAPT | close conceptual fit; whole toolkit too large/architecturally different |
| Gamygdala | lightweight emotion appraisal | MIT | REFERENCE/ADAPT | useful event-to-emotion model; JS implementation not direct Android target |
| Jason | BDI | LGPL-3.0 | REFERENCE_ONLY | mature BDI semantics; full interpreter unnecessary/heavy |
| gdx-ai | BT/FSM tactical behavior | Apache-2.0 | ADAPT / selective reuse | Java, modular; only downstream executor pieces needed |
| RRF | hybrid rank fusion | published algorithm | REIMPLEMENT/benchmark | tiny formula; no dependency needed |
| MMR | diversity reranking | published algorithm | REIMPLEMENT/optional | useful for redundant memories; tiny formula |
| Graphiti | temporal/entity memory patterns | Apache-2.0, Python/Neo4j stack | REFERENCE_ONLY | excellent temporal/provenance ideas, unsuitable mobile dependency |
| Mem0 | memory extraction/search lifecycle | Apache-2.0 ecosystem | REFERENCE_ONLY | useful patterns, dependency stack inappropriate for embedded Android |
| LangMem | hot/background memory patterns | Python/LangGraph | REFERENCE_ONLY | lifecycle ideas; model/provider stack unsuitable offline core |
| Letta | stateful-agent/context memory patterns | Apache-2.0 platform | REFERENCE_ONLY | server/TS platform, not mobile runtime |
| Generative Agents | memory/reflection/planning concepts | research | REFERENCE_ONLY | strong architectural benchmark, not production mobile code |

Before copying any external source file or substantial algorithm implementation, record exact repository, commit/release, file, license and local modifications in the target repo.

---

# 25. Existing Matrix code reuse matrix

## KEEP / PORT / ADAPT
- `assembling` MIP contracts and explicit status/provenance semantics;
- `CanonicalUnderstandingV3Adapter`;
- `DeterministicAuthorityResolver` + active retrieval-binding fix;
- old Android `WorldMatrixProtocol`;
- old `PerceptionEnvelope` validation concepts;
- old `MatrixEngineFramework.Stage/StageResult/diagnostic` lightweight abstraction;
- old hard-filter-first retrieval architecture;
- old ContextPackage budget/non-destructive packaging;
- old ContextSupervisor ownership/dedup checks;
- old Reflection trigger/queue/provenance patterns;
- old Goal/Intention separation;
- old CausalTrace bounded structured diagnostics;
- recovered Python Memory transactional/lineage/admission tests.

## FIX / REIMPLEMENT
- Coherence: remove Authority/policy ownership;
- Affective: remove Relationship ownership;
- Memory reconciliation: consume Authority contradiction identity instead of text/slot heuristics;
- Memory schema: add canonical MIP semantic roles/predicate/typed value/temporal fields;
- retrieval rank weights: benchmark, do not hard-code legacy constants;
- Reflection inference: replace concatenation heuristic with structured evidence reasoning;
- orchestrator: canonical stages + parallel read groups + post-validation commit.

## REJECT AS FINAL IMPLEMENTATION
- regex-based `MemoryAcquisitionClassifier` natural-language parsing;
- old `MatrixEngineOrchestrator` ordering that writes Memory before output validation;
- snapshot-wide `replaceAll` persistence as durable production repository;
- `BasicAuthorityResolver` as canonical Authority;
- `BasicMemoryAdmission` / `NoPersistentMemoryAdmission` as production Memory;
- `EchoGgufAdapter` as real backend;
- legacy `UnderstandingLabAdapter` as canonical V3 path;
- fixed Relationship values inside Affective owner;
- Prompt Builder making behavioral decisions.

---

# 26. Development order designed to minimize reopening modules

The order is dependency-driven. The goal is not “never edit a module twice”; the goal is to avoid foreseeable redesign caused by building downstream modules before their real upstream contracts are known.

## Phase F — foundations

### F1 — MIP/runtime envelope + provenance + causal trace alignment
Close event/observation, IDs, TurnWorkspace, context availability and diagnostic correlation.

### F2 — canonical orchestrator skeleton
Install typed stage boundaries, explicit `NOT_WIRED`, bounded structured parallel Context reads, OutputValidator and PersistentConsolidation seams. No fake module output.

## Phase U — upstream cognition

### U1 — real Student-4 ONNX runtime
Raw text -> actual V3 output on JVM/Android-compatible runtime.

### U2 — Understanding V3 proof
IT/EN/ES real semantic suite, error analysis/fixes/regression.

### U3 — narrow Coherence
Structural validation only.

### U4 — Context/initial read providers
Real immutable snapshot, initially with available domains and explicit unavailable others.

### U5 — Authority + BeliefState
Claim-wise Authority; current epistemic state separate from Memory.

## Phase M — Memory as one complete workstream

### M1 — final MemoryRecord + query schema
Design against all required categories/roles/time/provenance/retrieval purposes.

### M2 — Room Repository + Admission
Port recovered atomic/lineage/rollback semantics.

### M3 — Retrieval
Structured indexes + FTS/BM25 + optional vector + calibrated hybrid + current/history.

### M4 — MemoryPreflight + post-validation Consolidation
No pre-response writes.

### M5 — Memory real functional closure
SAVE/recall/supersede/restart/rollback/history/multi-claim/ambiguity tests.

## Phase S — subjective state owners

### S1 — Affective
Appraisal/transient/mood only.

### S2 — Relationship
Directional persistent social state.

### S3 — Intimacy/Consent
Current contextual consent/boundary/desire state.

### S4 — Goal/Intention
BDI-lite state.

## Phase H — higher cognition

### H1 — Reflection
Micro/decision/macro with provenance and abstention.

### H2 — Opportunity Manager
Event-driven initiative candidates.

### H3 — Decision/Utility
Explainable DecisionSnapshot.

### H4 — optional BT/FSM executor
Only if multi-step action behavior requires it.

### H5 — Interaction Manager
Multi-party conversation/action sessions.

### H6 — Cognitive LOD
Significance-driven computation budgets.

## Phase R — realization and execution

### R1 — final Context Supervisor / RealizationPackage
### R2 — Prompt Builder consuming DecisionSnapshot
### R3 — real GGUF backend
### R4 — Output Validator
### R5 — Persistent Consolidation all owners
### R6 — ActionIntent -> World ActionResult cycle

## Phase E — complete E2E

Desktop/JVM controlled cycle first, then Android integration. Fix demonstrated cross-module defects only. Repeat full regression.

## Phase P — Moto G56

Only after automatic E2E baseline:
- real Student-4 runtime;
- real GGUF;
- complete state/memory loop;
- RAM/PSS/CPU/latency/thermal/stability;
- behavioral quality tests.

---

# 27. Mandatory module closure template

Every implementation work package must declare before code:

```text
MODULE
REAL PRODUCT RESPONSIBILITY
STATE OWNER
INPUT CONTRACT
OUTPUT CONTRACT
READ-ONLY DEPENDENCIES
FORBIDDEN RESPONSIBILITIES
EXTERNAL RESEARCH / REUSE DECISIONS
PERFORMANCE BUDGET
NORMAL CASES
AMBIGUOUS CASES
ERROR CASES
HAND-OFF TEST TO NEXT REAL MODULE
REGRESSION SET
CLOSURE CONDITION
```

Completion requires:

```text
real implementation present
real input -> real output
field-by-field expected/actual
real handoff to next module
ambiguity/error behavior
stress/coverage where relevant
measured error analysis
fix observed defects
same-suite retest
regression
```

---

# 28. Immediate implications before Memory implementation

The earlier plan to simply begin Room/Memory is superseded by this design dependency check.

Only a **bounded foundation closure** is required before Memory; we do not need to implement every future module first.

Required before M1 Memory schema is frozen:

```text
1. canonical TurnWorkspace / ContextSnapshot data path is authoritative;
2. real Understanding V3 claims are the upstream semantic source;
3. Coherence is narrowed to its actual job;
4. claim-bound RetrievalResult contract remains stable;
5. AuthorityResolution is the authoritative contradiction/epistemic input;
6. final state ownership map above is accepted so Memory does not absorb Relationship/Affective/Goal/Intimacy;
7. MemoryRecord/query design covers all information categories and retrieval purposes above.
```

Relationship/Decision/GGUF do NOT have to be implemented before Memory. Their contracts/ownership must merely be known so Memory is not designed to take their jobs.

---

# 29. Evidence and research sources

## Internal Matrix evidence
- `MATRIXNEO23/assembling/docs/MATRIX_INTERMODULE_PROTOCOL.md`
- `MATRIXNEO23/assembling/docs/MODULE_CONNECTIONS.md`
- `MATRIXNEO23/assembling/docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`
- `MATRIXNEO23/assembling/src/main/kotlin/matrix/assembling/understanding/v3/CanonicalUnderstandingV3Adapter.kt`
- `MATRIXNEO23/assembling/src/main/kotlin/matrix/assembling/authority/AuthorityResolver.kt`
- `MATRIXNEO23/assembling/src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt`
- `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`
- old Android MatrixEngine Core/Memory/Retrieval/Reflection/Behavior/Supervisor/Diagnostics source tree at `e97f75052afcc93d5b1e08b3ac881dba35633451`
- recovered Python Memory Foundation + tests from owner Library.

## Primary/external references consulted
- ONNX Runtime Mobile: https://onnxruntime.ai/docs/tutorials/mobile/
- ONNX Runtime custom builds: https://onnxruntime.ai/docs/build/custom.html
- ONNX Runtime NNAPI: https://onnxruntime.ai/docs/execution-providers/NNAPI-ExecutionProvider.html
- Android Room data/FTS: https://developer.android.com/training/data-storage/room/defining-data
- Room transactions: https://developer.android.com/reference/kotlin/androidx/room/RoomDatabase
- SQLite FTS5/BM25: https://www.sqlite.org/fts5.html
- Kotlin coroutines: https://kotlinlang.org/docs/coroutines-basics.html
- CloudEvents spec: https://github.com/cloudevents/spec/blob/main/cloudevents/spec.md
- W3C PROV-O: https://www.w3.org/TR/prov-o/
- OpenTelemetry Trace API: https://opentelemetry.io/docs/specs/otel/trace/api/
- W3C Trace Context: https://www.w3.org/TR/trace-context/
- FAtiMA Toolkit: https://github.com/GAIPS/FAtiMA-Toolkit
- Gamygdala: https://github.com/broekens/gamygdala
- Jason BDI: https://github.com/jason-lang/jason
- gdx-ai: https://github.com/libgdx/gdx-ai
- RRF: Cormack, Clarke, Buettcher, SIGIR 2009, DOI 10.1145/1571941.1572114
- MMR: Carbonell & Goldstein, SIGIR 1998, DOI 10.1145/290941.291025
- Bruch, Gai, Ingber, `An Analysis of Fusion Functions for Hybrid Retrieval`, arXiv:2210.11934
- AGM belief revision overview: https://plato.stanford.edu/entries/logic-belief-revision/
- Graphiti: https://github.com/getzep/graphiti
- Mem0: https://github.com/mem0ai/mem0
- LangMem: https://github.com/langchain-ai/langmem
- Letta: https://github.com/letta-ai/letta

---

# 30. Current supervisor verdict

The project does not need to be restarted. It already contains valuable contracts, algorithms and historical Android modules. But it must stop treating old wiring/checkpoint green results as proof that the complete final engine already exists.

The correct strategy is:

```text
PRESERVE good existing work
+ PORT reusable generic stages
+ FIX ownership/order violations
+ BUILD missing state owners
+ DIRECTLY REUSE mature Android/runtime primitives
+ REIMPLEMENT small algorithms where framework dependencies would be excessive
+ TEST each module against its real product responsibility
```

The next implementation authorization should therefore be a **Foundation Closure package**, not Memory code directly and not a broad rewrite of the engine.

Proposed bounded next package after owner review:

```text
F1/F2 ONLY
- canonical TurnWorkspace/Context orchestration skeleton
- integrate real V3->Context->claim-bound Retrieval->Authority path
- narrow Coherence responsibility
- preserve Affective/Relationship ownership boundary in contracts
- explicit NOT_WIRED slots for future owners
- no new Memory persistence
- no Relationship/Decision implementation yet
- demonstrative cross-module tests + regression
```

After F1/F2 is demonstrated, Memory can be implemented once against the final semantic/state boundaries defined here.
