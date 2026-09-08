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

## 2. Source-of-truth hierarchy

When sources differ or a fresh session must reconstruct state, use this order:

```text
1. current canonical continuity
2. active workstream continuity
3. canonical plan/contract/rule documents referenced by continuity
4. live repository branch/HEAD/checkpoint/artifact evidence
5. archived continuities for historical reconstruction
6. old conversations/chat memory only as fallback for genuinely missing detail
```

A stale statement in continuity is not allowed to override newer verified repository evidence. When live evidence advances the state, continuity must be updated at the next supervisor checkpoint.

## 3. Continuity must be self-sufficient

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
- active Work assignment/status and whether execution is merely prepared, reported, verified or accepted;
- permanent supervisor rules that materially affect execution;
- explicit distinction between prepared, executed, verified and accepted work;
- the exact prompt/package path for any currently delegated bounded assignment.

If continuity is found incomplete during resume, Supervisor GPT must repair it before allowing substantial new work to depend on the missing detail, preserving the previous continuity version in the archive.

## 4. The owner is never the project's memory system

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

Manual owner inspection/copying should be requested only when an action truly cannot be completed or verified by available project tooling.

## 5. Confirmed plans persist automatically

When Owner + Supervisor confirm a real construction plan, that plan is saved/versioned and linked from continuity automatically. No extra “do you want me to save it?” question is required.

Ideas remain proposals until discussed and jointly promoted to plan.

## 6. Deep prior-art/reuse check is mandatory before redesign

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

## 7. External agents are consultants, not supervisors

If a genuine technical doubt would benefit from another model/agent (for example Copilot, Gemini, Qwen or another specialist), Supervisor GPT may recommend obtaining a second opinion.

Their role is advisory only:

```text
OWNER = final authority
SUPERVISOR GPT = project supervisor / integrator / acceptance authority
WORK or other coding agent = executor
external agents = consultants
```

A consultant does not change scope, architecture, priority or accepted plans by itself. Supervisor GPT evaluates the advice against repository evidence and presents any recommended change to the owner.

## 8. Supervisor GPT must actively supervise Work

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

GitHub Actions and ChatGPT Work are separate execution surfaces. Absence/failure of an Actions run is not proof that Work is idle, and Work's UI saying “processing” is not proof that repository work is progressing. Operational claims require the appropriate source-of-truth evidence.

## 9. Bounded delegation: Work gets the current task, not the roadmap

Supervisor GPT retains the project sequence and future assignments. Work receives only the current bounded objective plus the minimum prerequisite context required to execute it correctly.

Hard rule:

```text
SUPERVISOR KNOWS THE ROADMAP
WORK KNOWS THE CURRENT ASSIGNMENT
```

A Work prompt must not casually enumerate future tasks, automatic continuation or later workstreams. Future context is included only when it is strictly necessary to avoid corrupting the current step.

Within the assigned step, Work may resolve ordinary subproblems autonomously if they stay inside scope. It must not stop at every minor obstacle merely because the exact subcommand was not prewritten.

If the current step reaches PASS/BLOCKED, Work stops and reports. Supervisor GPT decides what comes next.

## 10. Work reporting contract

Every delegated assignment must end with a substantive operational report addressed to Supervisor GPT whenever the interface permits it.

The report must contain the required verdict, evidence, branch/HEAD and unresolved blockers defined by the assignment.

The following are not acceptable as the only completion response:

```text
::SKIP_COMPLETION::
SKIP_COMPLETION
DONE
COMPLETED
empty response
sentinel-only response
```

If an execution environment suppresses the user-visible report, the complete report must be persisted in the repository continuity/report evidence before termination so Supervisor GPT can audit it.

Work must never stop silently on a blocker. It reports what failed, what it tried, what remains and the minimum required intervention.

## 11. Execution transparency remains absolute

Always distinguish:

```text
NOTED_FOR_LATER
PREPARED_NOT_EXECUTED
EXECUTED_NOT_VERIFIED
EXECUTED_AND_VERIFIED
SUPERVISOR_ACCEPTED
```

Never say “saved”, “done”, “updated”, “started”, “passed” or equivalent unless that state was actually executed and verified at the appropriate source of truth.

## 12. Continuity maintenance is conservative

Improving continuity is not permission to rewrite the project.

Before a substantive continuity rewrite:

```text
1. preserve/archive the current continuity version;
2. identify what is still valid and keep it;
3. correct only stale/contradictory state;
4. add missing operational information;
5. mark historical/superseded items explicitly instead of silently deleting them;
6. do not change architecture, roadmap, accepted gates or confirmed construction plans merely to make the document cleaner;
7. if a proposed continuity edit would change a real project decision, present it to Alberto instead of applying it unilaterally.
```

After the edit, Supervisor GPT must be able to state separately:

```text
UNCHANGED / PRESERVED
CORRECTED AS STALE
ADDED FOR CONTINUITY
PROPOSED ONLY / NOT APPLIED
```

## 13. Continuity self-sufficiency test

A fresh Supervisor GPT session must be able to answer all of these from continuity + referenced canonical sources without asking Alberto to reconstruct history:

```text
1. What repository/workstream is active?
2. What branch and latest verified checkpoint/HEAD matter?
3. Who is owner, supervisor and executor?
4. What is the last Supervisor-accepted result?
5. What assignment is currently prepared/executing/awaiting verification?
6. What is explicitly NOT done yet?
7. What blockers/open decisions remain?
8. What artifacts/models/data are immutable and where are they recoverable?
9. What confirmed construction plans exist and where are they stored?
10. What exact immediate action must Supervisor GPT take next?
11. What permanent rules constrain that action?
12. What evidence must be checked before claiming the next PASS?
```

If any answer is unavailable or ambiguous, continuity is not self-sufficient and must be repaired before relying on it.

## 14. Resume invariant

A correct handoff passes this test:

> A new Supervisor GPT session can read the continuity and know the exact state, current checkpoint, next step, confirmed plans, open decisions and permanent operating rules without asking Alberto to repeat prior work.

If that is false, continuity is defective and must be repaired.
