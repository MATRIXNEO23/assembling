# WORK PROMPT — Matrix Assembling M1 Integration

Repo: `MATRIXNEO23/assembling`
Branch: `main`

## Objective

Build the first functional integration layer that connects NLU/Understanding, Coherence, Authority, Memory, Affective Engine, Prompt Builder and GGUF without merging their responsibilities.

This repository is the integration boundary, not the training lab.

## Existing files to read first

- `README.md`
- `docs/WORK_CONTINUITY.md`
- `docs/ASSEMBLING_PLAN.md`
- `docs/MODULE_CONNECTIONS.md`
- `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt`
- `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt`

## Hard rules

- Do not make GGUF responsible for truth, memory, consent, policy or stable state.
- Do not let NLU write memory directly.
- Do not let Affective Engine persist emotions from low-confidence claims.
- Do not turn adult/intimacy content into censorship labels.
- Do not use adult public raw datasets.
- Keep prompt instructions short and deterministic.
- Keep modules replaceable through ports/adapters.

## Target architecture

```text
UserMessage
  -> NluPort
  -> UnderstandingPort
  -> CoherenceGuardPort
  -> AuthorityResolverPort
  -> MemoryAdmissionPort
  -> AffectivePort
  -> SemanticFrameToPromptPort
  -> GgufPort
  -> AssistantReply
```

## M1 deliverables

1. Add minimal tests or examples proving the orchestrator order.
2. Add fake/stub adapters for all ports.
3. Add example scenario:
   - input: `Non voglio uscire con Marco`
   - semantic meaning: refusal / negative goal
   - memory: no stable write unless admitted
   - affective: no persistent penalty
   - prompt: tells GGUF to respect the refusal
4. Add adult/intimacy robustness scenario with censored placeholder:
   - input contains `[azione intima]`
   - semantic meaning: request/desire/consent/refusal/boundary or unresolved
   - prompt must not say block/censor/error automatically
5. Add `docs/PROMPT_BUILDER_CONTRACT.md` explaining how numeric/classes become GGUF text.
6. Update `docs/WORK_CONTINUITY.md` after each commit.

## Expected output

- Commit SHA
- List of files changed
- What works
- What remains stubbed
- Next step to connect real NLU artifact from `MATRIXNEO23/matrix-understanding-lab`

## Important current context

`MATRIXNEO23/matrix-understanding-lab` is training Student-4-v2.2A. That artifact is not yet final. Design adapters so the real NLU can be plugged in later without changing the rest of the flow.
