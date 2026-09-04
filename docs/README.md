# Matrix Assembling — Document Index

Status: CANONICAL DOCUMENT INDEX
Date: 2026-09-04

## Canonical sources

1. Global Matrix architecture:
   `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`
2. Assembling module wiring:
   `docs/MODULE_CONNECTIONS.md`
3. Current implementation plan:
   `docs/ASSEMBLY_PLAN.md`
4. Current operational state / restart point:
   `docs/WORK_CONTINUITY.md`

## Document classes

### CANONICAL / CURRENT
- `README.md`
- `docs/README.md`
- `docs/MODULE_CONNECTIONS.md`
- `docs/ASSEMBLY_PLAN.md`
- `docs/MEMORY_INTEGRATION_POLICY.md`
- `docs/MEMORY_INTEGRATION_STATUS.md`
- `docs/WORK_CONTINUITY.md`

### AUDIT / EVIDENCE
- `docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md`
- `docs/IMPORTED_COMPONENTS.md`

### COMPATIBILITY PATH
The following code path is retained only to preserve existing work and tests while the frame-based path is canonical:
- `src/main/kotlin/matrix/assembling/contracts/*`
- `src/main/kotlin/matrix/assembling/pipeline/*`
- `src/main/kotlin/matrix/assembling/prompt/*`

It must not receive new independent architectural authority.

## Canonical repository-work rule

Unless the project owner explicitly instructs otherwise, work on **one repository at a time**.

Rules:
- do not modify a second repository while an active task is being executed in the current repository;
- cross-repository dependencies may be read/referenced when necessary, but no write is authorized outside the active repository;
- changing the active repository requires an explicit user instruction;
- if a project-wide decision affects other repositories, record the dependency/action needed in the current repository continuity/backlog and apply it only when that repository becomes the active work target.

Current active repository for this workstream: `MATRIXNEO23/assembling`.

## Change-control rule

When a component changes role, order, ownership or contract, the same repository workstream must update all affected material inside the active repository:

1. the relevant local architecture/wiring specification;
2. affected adapters/contracts/code;
3. affected tests;
4. `docs/WORK_CONTINUITY.md`;
5. any active document in the same repository that would otherwise become contradictory.

If the decision also affects another repository, do not modify it automatically: record the required follow-up and wait until that repository is explicitly selected as the active repository.

A decision made only in chat is not considered integrated project state.

Do not create a new parallel architecture document when an existing canonical document can be updated.
