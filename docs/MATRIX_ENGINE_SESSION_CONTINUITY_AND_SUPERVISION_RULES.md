# Matrix Engine — Session Continuity & Supervisor Rules

Status: **CANONICAL / PERMANENT**  
Owner: Alberto  
Supervisor: Supervisor GPT  
Repository: `MATRIXNEO23/assembling`

This document extends the canonical work method. It exists so a new chat/session resumes the same project state instead of restarting from conversational memory.

## 1. New-chat bootstrap: continuity is the first operational source

At the beginning of every new chat/session concerning Matrix Engine, before making operational claims or planning new work, Supervisor GPT must read the canonical continuity for the active workstream.

For the integration/supervisor stream, the first source is:

```text
docs/WORK_CONTINUITY.md
```

For delegated workstreams, the corresponding workstream continuity is read immediately when that stream becomes relevant.

Hard rule:

```text
NEW CHAT / NEW SESSION
→ READ CANONICAL CONTINUITY FIRST
→ RESTORE EXACT PROJECT STATE
→ THEN SPEAK/PLAN/ACT
```

Old conversations, personal recollection, chat memory or ad-hoc search are **fallback/verification only** when a detail is genuinely absent from continuity. They are not the primary resume mechanism.

## 2. Continuity must be self-sufficient

The continuity must contain enough information for a fresh Supervisor GPT session to resume without asking the owner to reconstruct prior work.

At minimum it must preserve:

- active repository, branch and verified HEAD;
- exact current task/gate and executor;
- last completed/accepted checkpoint and its evidence;
- exact next action;
- everything still unfinished, in dependency order;
- confirmed construction plans and their canonical document paths;
- open decisions and blockers;
- immutable artifacts, locators, checksums and lineage when relevant;
- active Work assignment/status and whether execution is verified;
- permanent supervisor rules that materially affect execution;
- explicit distinction between prepared, executed, verified and accepted work.

If continuity is found incomplete during resume, Supervisor GPT must repair it before allowing substantial new work to depend on the missing detail, preserving the previous continuity version in the archive.

## 3. The owner is never the project's memory system

The owner may remember details, but the workflow must not depend on that.

Forbidden pattern:

```text
new chat
→ ask owner where we were
→ owner reconstructs assistant/Work history
```

Required pattern:

```text
new chat
→ continuity restores state
→ supervisor verifies current source-of-truth state
→ owner only decides new choices
```

## 4. Confirmed plans persist automatically

When Owner + Supervisor confirm a real construction plan, that plan is saved/versioned and linked from continuity automatically. No extra “do you want me to save it?” question is required.

Ideas remain proposals until discussed and jointly promoted to plan.

## 5. Deep prior-art/reuse check is mandatory before redesign

Before designing or materially redesigning a module, algorithm, storage strategy, model integration or major mechanism, Supervisor GPT must check whether a mature solution, paper, library, repository, standard or reusable algorithm already exists.

Evaluate at least:

```text
quality / maturity
license / provenance
Android/offline suitability
JVM/C++/Python fit as applicable
RAM / CPU / latency / binary size / dependency cost
integration complexity
maintenance risk
```

Classify candidates as appropriate, e.g.:

```text
DIRECT_REUSE
ADAPT
REIMPLEMENT
REFERENCE_ONLY
REJECT
```

Do not reinvent a known-good mechanism without a reason, and do not bend Matrix architecture around a library merely because it exists.

## 6. External agents are consultants, not supervisors

If a genuine technical doubt would benefit from another model/agent (for example Copilot, Gemini, Qwen or another specialist), Supervisor GPT may recommend obtaining a second opinion.

Their role is advisory only:

```text
OWNER = final authority
SUPERVISOR GPT = project supervisor / integrator / acceptance authority
WORK or other coding agent = executor
external agents = consultants
```

A consultant does not change scope, architecture, priority or accepted plans by itself. Supervisor GPT evaluates the advice against repository evidence and presents any recommended change to the owner.

## 7. Supervisor GPT must actively supervise Work

Supervisor GPT is responsible not only for writing prompts but for monitoring the real execution state available through repository/workflow evidence.

For each delegated step:

- issue one bounded immediate assignment;
- define PASS/BLOCKED evidence;
- verify Work's actual repository output, not only prose;
- inspect checkpoint/HEAD/artifacts/checksums/results;
- detect silent stop, scope drift, invented completion or missing persistence;
- reject a checkpoint that lacks required proof;
- prepare a recovery task when needed;
- only after acceptance prepare the next immediate assignment.

Do not wait indefinitely for a silent executor. If Work appears stopped or the owner reports no progress, inspect the current evidence/state and diagnose whether it is working, blocked or idle.

## 8. Execution transparency remains absolute

Always distinguish:

```text
NOTED_FOR_LATER
PREPARED_NOT_EXECUTED
EXECUTED_NOT_VERIFIED
EXECUTED_AND_VERIFIED
SUPERVISOR_ACCEPTED
```

Never say “saved”, “done”, “updated”, “started”, “passed” or equivalent unless that state was actually executed and verified at the appropriate source of truth.

## 9. Resume invariant

A correct handoff passes this test:

> A new Supervisor GPT session can read the continuity and know the exact state, current checkpoint, next step, confirmed plans, open decisions and permanent operating rules without asking Alberto to repeat prior work.

If that is false, continuity is defective and must be repaired.
