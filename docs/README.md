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

## Change-control rule

When a component changes role, order, ownership or contract, the same change set must update:

1. the canonical architecture/specification;
2. the Assembling wiring/contract;
3. affected tests;
4. `docs/WORK_CONTINUITY.md`;
5. any document that would otherwise become contradictory.

A decision made only in chat is not considered integrated project state.

Do not create a new parallel architecture document when an existing canonical document can be updated.
