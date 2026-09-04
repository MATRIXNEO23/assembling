# Matrix Assembling — Module Connections

Status: integration contract, not production approval.
Date: 2026-09-04

## Goal

Connect the already separated components without letting any single module take unsafe final authority.

Core rule:

```text
NLU reads language.
Understanding builds semantic frames.
Coherence checks stability.
Authority resolves source/conflict/owner.
Memory stores only admitted/provisional records.
Affective reacts only to safe events.
Prompt Builder translates internal state for GGUF.
GGUF writes natural language only.
```

## Canonical flow

```text
UserMessage
  ↓
NluPort
  ↓
UnderstandingPort
  ↓
CoherenceGuardPort
  ↓
AuthorityResolverPort
  ↓
MemoryAdmissionPort
  ↓
AffectivePort
  ↓
SemanticFrameToPromptPort
  ↓
GgufPort
  ↓
AssistantReply
```

## Module responsibilities

### 1. NLU

Input: raw user text.
Output: numeric/class predictions and confidence.

Allowed:
- dialogue act
- predicate
- polarity
- subject/object spans
- negation span
- temporal relation
- referent hints
- confidence values

Forbidden:
- final policy decisions
- stable memory writes
- affective changes
- censorship/blocking
- production approval

### 2. Understanding

Input: NLU output.
Output: `SemanticFrame` / `TypedClaimDraft`.

Allowed:
- normalize NLU predictions
- build subject/predicate/object frame
- keep original text
- mark uncertainty

Forbidden:
- writing memory directly
- deciding truth permanently

### 3. Coherence Guard

Input: semantic frame and confidence.
Output: `CoherenceDecision`.

Allowed decisions:
- `SAFE_TO_ADMIT`
- `SAFE_TRANSIENT_ONLY`
- `LOW_CONFIDENCE_HOLD`
- `REPORT_ONLY`
- `QUESTION_ONLY`
- `CONFLICT_REQUIRES_REVIEW`
- `REJECTED_UNSAFE`

Key rule: if negation, predicate, referents or temporal relation are uncertain, do not create stable memory.

### 4. Authority Resolver

Input: coherence-checked claim.
Output: authority/conflict decision.

Allowed:
- owner/source resolution
- same-property conflict detection
- correction/supersede proposal
- third-party/report separation

Forbidden:
- text-different-equals-conflict
- hardcoded owner

### 5. Memory Admission

Input: authority result.
Output: memory record lifecycle.

Allowed statuses:
- `RAW_OBSERVATION`
- `PROVISIONAL_CLAIM`
- `COHERENCE_CHECKED`
- `AUTHORITY_RESOLVED`
- `ADMITTED_MEMORY`
- `SUPERSEDED`
- `REJECTED`

Stable memory can only be written after coherence and authority are resolved.

### 6. Affective Engine

Input: safe semantic/memory events only.
Output: affective state deltas.

Allowed:
- transient emotion from uncertain input
- persistent affect from safe/admitted events

Forbidden:
- persistent trust/resentment changes from low-confidence claims

### 7. Prompt Builder

Input:
- original user text
- semantic summary
- memory summary
- relationship state
- affective state
- hard limits

Output: short GGUF prompt.

Rule: translate numeric/internal state into simple behavior instructions. Do not send raw numeric internals unless useful for diagnostics.

### 8. GGUF

Input: prepared prompt.
Output: natural language reply.

Allowed:
- expressive response
- style/personality realization

Forbidden:
- changing stable facts
- writing memory
- final truth resolution
- overriding semantic negation/consent/refusal decisions

## Minimal integration target

The first integration must prove this:

```text
"Non voglio uscire con Marco"
→ semantic frame: refusal / negative goal
→ no stable memory unless admitted
→ no affective persistent penalty
→ GGUF prompt says: respect the refusal
→ reply does not invert the negation
```

## Adult/intimacy robustness

Adult/intimacy text must not be treated as censorship by NLU.

Rule:

```text
adult/intimate terms
→ classify as request/desire/consent/refusal/boundary/unresolved
→ no automatic block
→ no stable memory without admission
```

If meaning is unclear, emit `speech.unresolved` or a low-confidence semantic frame, not a hard error.

## Integration status

- NLU v2.2A: training in progress in `MATRIXNEO23/matrix-understanding-lab`.
- Memory foundation: design saved in `MATRIXNEO23/memoria`.
- Affective prototype: validated separately in `MATRIXNEO23/matrix-affective-lab`.
- Assembling repo: this repository owns contracts, adapters and prompt-translation logic.
