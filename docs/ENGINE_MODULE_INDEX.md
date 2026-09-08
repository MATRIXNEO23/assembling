# MATRIX Engine — Canonical Module Index

Status: CANONICAL OPERATIONAL INDEX
Repository: `MATRIXNEO23/assembling`
Branch: `main`
Baseline HEAD when created: `693622ce12f1db6b4bc44753bee756b551c741ea`

## Mandatory use

This is the first lookup for every new Matrix Engine / Assembling session. Historical repositories such as `8.10.9evo3-solo-gpt` are backup/source references only and must not be treated as the active implementation.

Update this file whenever a module, adapter, protocol, model/runtime artifact, persistence route, integration status, or canonical locator changes. A task that changes Engine structure is not complete until this index and `docs/WORK_CONTINUITY.md` are updated.

## Semantic authority and wiring

- Intermodule semantic authority: `docs/MATRIX_INTERMODULE_PROTOCOL.md` (MIP-1.0)
- Current wiring authority: `docs/MODULE_CONNECTIONS.md`
- Imported component provenance: `docs/IMPORTED_COMPONENTS.md`
- Current continuity: `docs/WORK_CONTINUITY.md`
- Memory policy/status: `docs/MEMORY_INTEGRATION_POLICY.md`, `docs/MEMORY_INTEGRATION_STATUS.md`
- Authority contract: `docs/MIP_AUTHORITY_CONTRACT.md`
- Understanding V3 profile: `docs/MIP_UNDERSTANDING_V3_PROFILE.md`

## Current authoritative runtime spine

1. `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt` — canonical current-turn frame / diagnostic carrier.
2. `src/main/kotlin/matrix/assembling/IntegrationPorts.kt` — integration boundaries/ports.
3. `src/main/kotlin/matrix/assembling/MatrixAssemblingOrchestrator.kt` — current orchestration path.
4. `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt` — current root prompt realization path.

The older `contracts/*`, `pipeline/*`, `prompt/*` path is compatibility/testing only for new callers unless explicitly re-authorized by current docs.

## Implemented / present modules

### MIP / intermodule protocol bridge
Path: `src/main/kotlin/matrix/assembling/mip/`
Files include:
- `MipBridge.kt`
- `MipClaimWire.kt`
- `MipEvidenceContracts.kt`
- `MipEvidenceWire.kt`
- `MipUnderstandingV3Contracts.kt`
- `MipAuthorityCompatibility.kt`
Purpose: canonical cross-module semantic contracts, evidence binding and compatibility wiring.

### Understanding V3 integration
Path: `src/main/kotlin/matrix/assembling/understanding/v3/`
- `CanonicalUnderstandingV3Adapter.kt`
- `UnderstandingV3Validation.kt`
Adapter source also present at `src/main/kotlin/matrix/assembling/adapters/UnderstandingLabAdapter.kt`.
Vendor source snapshot:
- `vendor/matrix-understanding-lab/core/...`
- `vendor/matrix-understanding-lab/matrix_nlu/...`
Active lab of origin: `MATRIXNEO23/matrix-understanding-lab`.

### Coherence
Path: `src/main/kotlin/matrix/assembling/coherence/CoherenceGuard.kt`
Purpose: semantic invariant checks; fails closed on critical missing/sub-threshold evidence.

### Authority / belief-resolution boundary
Path: `src/main/kotlin/matrix/assembling/authority/`
Key files:
- `AuthorityResolver.kt`
- `AuthorityContracts.kt`
- `AuthorityTypes.kt`
- `AuthorityCandidateEvidence.kt`
- `AuthorityContractWire.kt`
Runtime adapters:
- `authority/runtime/CanonicalAuthorityRuntimeAdapter.kt`
- `authority/runtime/CanonicalUnderstandingV3AuthorityPort.kt`
Purpose: source/owner/perspective/direct-vs-indirect authority resolution. Does not own Memory writes.

### Affective Engine adapter
Path: `src/main/kotlin/matrix/assembling/adapters/AffectiveLabAdapter.kt`
Vendor prototype: `vendor/matrix-affective-lab/src/affective_engine.py`
Origin: `MATRIXNEO23/matrix-affective-lab`.
Purpose: current affective integration boundary. RelationshipState and consent remain separate authorities.

### Memory preflight compatibility
Path: `src/main/kotlin/matrix/assembling/adapters/NoPersistentMemoryAdmission.kt`
Current state: no real persistent Memory backend imported in this checkpoint; stable writes remain disabled where this fallback is active.
Authoritative status/policy: `docs/MEMORY_INTEGRATION_STATUS.md`, `docs/MEMORY_INTEGRATION_POLICY.md`.

### Basic integration adapters
Path: `src/main/kotlin/matrix/assembling/adapters/BasicAdapters.kt`
Purpose: current compatibility/test adapters around the assembly ports.

### Prompt realization
Authoritative current root: `src/main/kotlin/matrix/assembling/SemanticFrameToPrompt.kt`
Compatibility/deprecated-for-new-callers path: `src/main/kotlin/matrix/assembling/prompt/SemanticFrameToPrompt.kt`.
Prompt Builder is realization-only; it must not acquire decision, truth, memory, relationship, affective or consent authority.

### Diagnostic trace / workspace carrier
Primary surface: `MatrixTurnFrame.diagnostics` in `src/main/kotlin/matrix/assembling/MatrixTurnFrame.kt`.
Rule: one diagnostic path; no parallel trace system. `firstDivergence` is write-once; reason codes only, no private chain-of-thought.

### Compatibility assembly pipeline
Path: `src/main/kotlin/matrix/assembling/pipeline/MatrixAssemblyPipeline.kt`
Status: compatibility/testing path, not the preferred authoritative caller path.

### Legacy assembly contracts
Path: `src/main/kotlin/matrix/assembling/contracts/MatrixAssemblyContracts.kt`
Status: compatibility/testing; do not extend as a competing semantic authority to MIP.

## Connected/vendor artifacts

### Understanding/NLU vendor snapshot
`vendor/matrix-understanding-lab/`
Contains copied contract/runtime decoder material. Source lab remains authoritative for experiments and Student model artifacts.

### Affective vendor snapshot
`vendor/matrix-affective-lab/src/affective_engine.py`
Validated prototype snapshot for adapter integration.

### Current NLU runtime candidate placeholder
`vendor/matrix-understanding-lab/runtime-candidate/student-4-v2.2a/ASSEMBLING_ARTIFACT.json`
and `models/matrix-nlu/matrix-nlu-student-4-v22a-mixed-head-protected-local-20260904T1440Z.zip`.
Status must be read from the manifest/current docs before use; do not assume production approval.

## Reserved / not fully wired modules

These are canonical domains but must not be simulated when absent:

- Memory Foundation / durable Long-Term Memory owner — status from current memory docs.
- RelationshipState owner/controller — `NON_CABLATO` in current module wiring unless newer Assembling evidence supersedes it.
- Intimacy / Consent resolver — `NON_CABLATO` unless newer Assembling evidence supersedes it.
- Matrix Decision Layer / full BDI-lite + Utility — `NON_CABLATO` unless newer Assembling evidence supersedes it.
- Output Validator real semantic implementation — port boundary exists; real implementation may remain `NON_CABLATO`.
- Persistent Consolidation final durable stage — reserved; no connected implementation at this checkpoint unless newer evidence supersedes it.
- World/perceived-state owner — use explicit availability; no fake default state.

Always re-check `docs/MODULE_CONNECTIONS.md` and `docs/WORK_CONTINUITY.md` for the newest status before changing one of these.

## Tests / verification surfaces

Main test roots:
- `src/test/kotlin/matrix/assembling/`
- `src/test/kotlin/matrix/assembling/adapters/`
- `src/test/kotlin/matrix/assembling/authority/`
- `src/test/kotlin/matrix/assembling/mip/`
- `src/test/kotlin/matrix/assembling/understanding/v3/`

Notable tests include architecture boundaries, diagnostic trace, orchestrator integration, canonical slots, prompt boundary, authority, MIP bridge/evidence, and Understanding V3 adapter tests.

CI: `.github/workflows/ci.yml`.

## Runtime direction

Canonical logical direction from `docs/MODULE_CONNECTIONS.md`:

`Observation → NLU → Understanding → TurnWorkspace/Context → Memory retrieval/context enrich → Coherence → Authority/Belief resolution → Memory preflight → Affective appraisal → Matrix decision → Prompt → GGUF → Output validation → Persistent consolidation`

Only wired phases execute. Missing domains must be explicit `NOT_WIRED/NON_CABLATO`, never fabricated.

## Update checklist for every new Engine work item

Before declaring a task complete, update this index if any of the following changed:
- module/package/file locator;
- module status (`WIRED`, `PARTIAL`, `NON_CABLATO`, deprecated/compatibility);
- source repository or vendor snapshot;
- protocol/profile version;
- model/runtime artifact locator or SHA;
- integration port/adapter;
- persistence/recovery route;
- canonical HEAD/checkpoint;
- new tests/gates materially defining module readiness.

Also update `docs/WORK_CONTINUITY.md` with the exact resume point and next task.