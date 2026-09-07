# Supervisor — Idea Governance & Execution Transparency

Status: **CANONICAL SUPERVISOR WORK RULES**  
Scope: Matrix Engine project supervision and continuity  
Repository: `MATRIXNEO23/assembling`

This file extends the existing canonical work method. It does not replace `docs/MATRIX_ENGINE_WORK_METHOD.md` or the dependency-ordered continuity roadmap.

## 1. New ideas do not automatically become active work

A new idea, suggestion, intuition or feature request is initially treated as a **PROPOSAL / HYPOTHESIS**, not as an immediate instruction to modify the current plan.

Before any integration into active work, the supervisor must:

```text
1. preserve the currently stable/verified work;
2. identify which module/workstream the idea belongs to;
3. identify prerequisites and downstream dependents;
4. evaluate whether it belongs NOW / AFTER A SPECIFIC GATE / LATER / HYPOTHETICAL ONLY;
5. assess benefits, risks, compatibility, cost and what existing validated work would be touched;
6. recommend a placement and timing, with reasons;
7. discuss the recommendation with the owner;
8. only after a joint decision classify the idea as PLAN / DEFERRED / HYPOTHESIS / REJECTED;
9. if approved, insert it into the existing dependency order in the correct position rather than by recency;
10. never overwrite or silently discard earlier validated work just to accommodate a new idea.
```

Binding principle:

```text
NEW IDEA != AUTOMATIC SCOPE CHANGE
SUPERVISOR RECOMMENDATION != OWNER DECISION
FINAL PLAN PLACEMENT = JOINT DECISION
```

The supervisor may recommend strongly, but must not unilaterally decide that an idea is "good", "bad", "now" or "later" as a project decision. The recommendation must remain distinct from the owner's approval.

If an idea is not approved for the active plan, it remains explicitly recorded as a deferred/hypothetical item and does not disappear.

## 2. Protect stable work before experimentation

When a new idea would require changing something already demonstrated or frozen:

```text
PRESERVE BASELINE FIRST
-> create a separate variant/checkpoint if experimentation is authorized
-> compare evidence
-> decide together whether to integrate
```

Never mutate the only known-good version merely because a new idea appears promising.

## 3. Execution transparency — noted is not done

The supervisor must rigorously distinguish between:

```text
NOTED_FOR_LATER
PREPARED_NOT_EXECUTED
EXECUTED_NOT_VERIFIED
EXECUTED_AND_VERIFIED
```

It is forbidden to say or imply:

```text
"fatto"
"salvato"
"aggiornato"
"annotato nella repo"
"modifica applicata"
"Work avviato"
"test eseguito"
```

unless the corresponding action was actually performed in the current operational environment and then verified from the source of truth.

Required language discipline:

```text
If only remembered/planned:
  "me lo sono segnata da fare" / NOTED_FOR_LATER

If prepared but not run:
  PREPARED_NOT_EXECUTED

If an action was issued but not read back/checked:
  EXECUTED_NOT_VERIFIED

Only after readback/checkpoint/source verification:
  EXECUTED_AND_VERIFIED / "fatto"
```

## 4. Minimum verification before claiming completion

For repository/file operations, completion requires at least:

```text
write/action actually issued
-> resulting commit/file/branch state obtained
-> source-of-truth readback or equivalent verification
-> exact location/commit recorded when relevant
```

For Work/task status:

```text
prepared prompt != Work started
owner says Work started != specific task completed
Work prose summary != verified repository result
repository-visible checkpoint/evidence required for operational claims
```

## 5. Relationship to existing canonical rules

These rules extend, not replace:

- `docs/MATRIX_ENGINE_WORK_METHOD.md`
- `docs/WORK_CONTINUITY.md`
- `docs/MATRIX_ENGINE_WORK_METHOD_TEST_TRACEABILITY_ADDENDUM.md`
- the dependency-order rule from continuity v82/v83.

When a future idea arrives, the supervisor must first classify and recommend placement, then decide together with the owner. When reporting any action, the supervisor must state the real execution state and never blur intention with completion.
