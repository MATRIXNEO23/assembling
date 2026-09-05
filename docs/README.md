# Matrix Assembling — Document Index

Status: CANONICAL DOCUMENT INDEX  
Date: 2026-09-05

## Canonical sources

1. Global Matrix architecture:
   `MATRIXNEO23/8.10.9evo3-solo-gpt/ARCHITETTURA_MATRIX_ENGINE.md`
2. Universal cross-module semantic contract:
   `docs/MATRIX_INTERMODULE_PROTOCOL.md` (`MIP-1.0`)
3. Assembling module wiring:
   `docs/MODULE_CONNECTIONS.md`
4. Current implementation plan:
   `docs/ASSEMBLY_PLAN.md`
5. Current operational state / restart point:
   `docs/WORK_CONTINUITY.md`

## Document authority rule

`docs/MATRIX_INTERMODULE_PROTOCOL.md` is the canonical Assembling-owned definition of shared intermodule meanings and boundary semantics.

It defines terms shared across modules such as:
- `subject`, `target`, `owner`, `perspective`, `source`;
- entity resolution states;
- temporal/provenance models;
- confidence taxonomy;
- universal context format;
- retrieval query/result semantics;
- state ownership/proposals/events;
- contradiction vs supersession;
- Relationship vs Affective vs Intimacy/Consent separation.

Module repositories may implement or extend their own internal details, but their public Matrix boundary must conform to the applicable MIP version and must not redefine shared terms independently.

## MIP adapter implementation status

`src/main/kotlin/matrix/assembling/mip/MipBridge.kt` is the explicit adapter-only implementation candidate for translating existing native DTOs to/from MIP-1.0.

Its compatibility audit is:

`docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md`

Hard rules:
- MIP Bridge does not replace module-owned DTOs;
- no business logic belongs in MIP Bridge;
- mappings are explicit, never reflection/magic;
- lossy conversion fails explicitly rather than silently dropping data;
- the current orchestrator is not rewired to the bridge by this checkpoint.

## Document classes

### CANONICAL / CURRENT
- `README.md`
- `docs/README.md`
- `docs/MATRIX_INTERMODULE_PROTOCOL.md`
- `docs/MODULE_CONNECTIONS.md`
- `docs/ASSEMBLY_PLAN.md`
- `docs/MEMORY_INTEGRATION_POLICY.md`
- `docs/MEMORY_INTEGRATION_STATUS.md`
- `docs/WORK_CONTINUITY.md`

### AUDIT / EVIDENCE
- `docs/MIP_BRIDGE_COMPATIBILITY_AUDIT.md`
- `docs/COMPONENT_MAPPING_AUDIT_2026-09-04.md`
- `docs/IMPORTED_COMPONENTS.md`

### COMPATIBILITY PATH
The following code path is retained only to preserve existing work and tests while the frame-based path is canonical:
- `src/main/kotlin/matrix/assembling/contracts/*`
- `src/main/kotlin/matrix/assembling/pipeline/*`
- `src/main/kotlin/matrix/assembling/prompt/*`

It must not receive new independent architectural authority.

The current `MatrixTurnFrame`, `NluOutput`, `SemanticFrame`, `TypedClaim` and integration ports are implementation predecessors/compatibility surfaces. They are not allowed to override MIP semantics merely because they contain older nullable/string/boolean representations.

## Canonical repository-work rule

Unless the project owner explicitly instructs otherwise, work on **one repository at a time**.

Rules:
- do not modify a second repository while an active task is being executed in the current repository;
- cross-repository dependencies may be read/referenced when necessary, but no write is authorized outside the active repository;
- changing the active repository requires an explicit user instruction;
- if a project-wide decision affects other repositories, record the dependency/action needed in the current repository continuity/backlog and apply it only when that repository becomes the active work target.

Current temporary active repository for the universal-protocol clarification/bridge workstream: `MATRIXNEO23/assembling`.

This temporary architecture checkpoint does not authorize implementation work in `memoria`, NLU, Affective, Relationship or other repositories.

## Change-control rule

When a component changes role, order, ownership or contract, the same repository workstream must update all affected material inside the active repository:

1. the relevant local architecture/wiring specification;
2. affected adapters/contracts/code when implementation is actually authorized;
3. affected tests when code changes;
4. `docs/WORK_CONTINUITY.md`;
5. any active document in the same repository that would otherwise become contradictory.

If the decision also affects another repository, do not modify it automatically: record the required follow-up and wait until that repository is explicitly selected as the active repository.

A decision made only in chat is not considered integrated project state.

Do not create a second competing intermodule protocol. Future cross-module semantic clarification must update MIP rather than creating parallel definitions.