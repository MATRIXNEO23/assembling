# Product Feedback Ideas

Status: `DRAFT / NOT YET SENT`

Purpose: preserve product-feedback ideas that emerged during Matrix Engine work so they can later be refined and submitted without relying on chat memory.

These items are independent from the Matrix Engine execution backlog. They do not change module priorities or architecture.

## FEEDBACK-001 — Privacy-preserving reuse of generalizable methods

### Problem

A useful method may be discovered during one interaction, but the assistant should not need to retain or transfer a person's private conversation, identity or specific memories in order to benefit from the general lesson later.

### Requested capability

Support a safe mechanism that can extract and reuse **abstract, generalizable methods** from successful interactions across future sessions/users while keeping personal content separate.

Desired separation:

```text
conversation-specific facts / identity / private content
!=
abstract method / reusable procedure / general problem-solving pattern
```

Examples of what should be reusable when safe:

- a better debugging procedure;
- a more reliable project-continuity method;
- a general way to structure tests;
- a workflow that reduces repeated mistakes;
- an abstract algorithmic or organizational pattern.

What should not transfer:

- names, identities or personal details;
- private conversation content;
- user-specific memories;
- project secrets or credentials;
- any detail that would let another user infer the source person/session.

### Why it matters

It would let the system improve from successful problem-solving patterns without turning personal conversations into cross-user memory. The useful unit of learning is the **method**, not the person's data.

---

## FEEDBACK-002 — True concurrent voice conversation and operational work

### Problem

For long technical workflows, the user may want to keep talking while the assistant performs operational work: reading repositories, editing files, running tools, supervising Work, checking results or updating continuity.

Requiring voice to be stopped before operational work breaks continuity and creates unnecessary handoff friction. The user cannot naturally correct, add context or discuss ideas while the work is proceeding.

### Requested capability

Support **true concurrent voice + operational execution** so the assistant can continue a live conversation while also performing authorized tool/work actions.

Desired behavior:

```text
voice conversation remains active
+
operational task continues concurrently
+
user may interrupt/correct/add constraints in real time
+
assistant reconciles those updates with the active task safely
```

Useful requirements:

- keep voice full-duplex while tools/Work are running;
- allow the user to add or correct requirements without closing voice;
- clearly distinguish conversation from execution status;
- preserve task continuity when an interruption changes priorities;
- avoid duplicate execution when a Work session is already active;
- surface meaningful checkpoints without blocking the conversation.

### Why it matters

For collaborative technical work, the natural workflow is closer to working beside another person: the user keeps talking, reviewing and correcting while the assistant performs the task. Making voice and execution mutually exclusive forces the user to choose between collaboration and progress.

---

## Submission state

```text
FEEDBACK-001 = PRESERVED / NOT YET SUBMITTED
FEEDBACK-002 = PRESERVED / NOT YET SUBMITTED
```

Before submission, refine both into concise product-feedback messages while preserving the distinction between privacy-safe general method learning and concurrent voice/operational execution.
