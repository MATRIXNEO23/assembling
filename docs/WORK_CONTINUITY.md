# Work Continuity — Matrix Assembling

Last updated: 2026-09-06 (Europe/Rome)  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v73`

## Owner-approved execution order

```text
1. IMPLEMENT UNDERSTANDING V3
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
4. IMPLEMENT MEMORY KOTLIN/ROOM
```

No autonomous pivot, replacement of missing historical code, repository switch or gate reduction is authorized by this checkpoint. Preserve the method and roadmap in `docs/MATRIX_ENGINE_WORK_METHOD.md` and `docs/MATRIX_ENGINE_CHECKPOINT_ROADMAP.md`.

## CP-U3 — COMPLETE / MERGED / GREEN — DO NOT REDO

PR:
`#19 — Implement canonical Understanding V3 runtime and real TypedClaims`

Final PR head:
`649af878630e49eba2934b14dd45862fcfb8de5b`

Final PR CI:
`33966229551 = SUCCESS`

Merge SHA:
`089cb7169c5f511ffd5d27b8a1d5e887c4348b0c`

Post-merge main CI:
`33966306986 = SUCCESS`

Closure/continuity commit, observed main HEAD before this update:
`506e1e0f5a30fbe0c374b14ba6b8a4960606a9b6`

The merged PR and successful post-merge job were checked again on 2026-09-06. No Understanding implementation was repeated.

Integrated canonical path:

```text
Matrix-NLU V3 runtime output
-> CanonicalUnderstandingV3Adapter
-> MipUnderstandingV3Observation
-> MipUnderstandingV3Claim[] real canonical V3 TypedClaims
-> MatrixTurnFrame.canonicalUnderstandingV3
-> DiagnosticTrace
```

The V3 path preserves original claim IDs, independent source/subject/target/owner/perspective roles, claimKind, plural evidence spans, candidate identity, ambiguity alternatives, temporal anchors, structural/interpretation status and provenance. It does not auto-populate lossy legacy `TypedClaim`, `NluOutput` or `SemanticFrame` fields.

This is implemented processing of structured NLU output, not evidence that a final trained Student-5 V3 model or full device conversation chain is already ready.

Initial CI failure `33966040483` was a compile-scope bug in the SHA-256 validator and was fixed by `b53eefd84f12847ba000e96ca7342975b0187001`; no gate or architecture was weakened.

## Completed Understanding baseline

```text
CP-U1 lossless audit = COMPLETE / PASS
CP-U2 MIP-1.0/UNDERSTANDING-V3-1.0 profile = COMPLETE / MERGED / GREEN
CP-U3 runtime + real canonical TypedClaims = COMPLETE / MERGED / GREEN
```

## Active step — Python Authority P0 source recovery

Historical reported P0 defects:

```text
P0-PA-01 owner hardcoded "test_agent" in candidate search
P0-PA-02 fragile property extraction via regex/free-text parsing
P0-PA-03 false conflict scoring from actor overlap + content difference
```

The audit is evidence of reported defects, not the complete source needed to reproduce and patch them. These three Python P0 defects are NOT marked fixed.

The approved direction remains structured TypedClaim/MIP semantics; do not re-interpret natural-language content or use actor/content heuristics as contradiction proof.

### Recovery actually completed on 2026-09-06

Library files recovered as original bytes:

| Archive | Library file ID | SHA-256 |
|---|---|---|
| `matrix_memory_v3_green_checkpoint.zip` | `file_0000000011b8820a8e0eef2c600a7977` | `3cca9a045972d5bfa23f90fb541f125e6a1024e44655080dd60ac43de527ad94` |
| `memory_foundation_admission_v3.zip` | `file_000000000470820a902fc92cf015f61e` | `9aefdc4dc9bf3d388109653cc2281d54e3cab02ff38d0721bdd55f1e2581d04f` |
| `qwen_authority_resolver_input_files.zip` | `file_000000000004820aa19df83bb19e4fd5` | `4e90764bcca5ca7495140d52134b4f9b4636b99036a9641e16c221494b511765` |

All three ZIP integrity checks passed.

Selected recovery baseline: `matrix_memory_v3_green_checkpoint.zip`.

Recovered unchanged:

```text
memory/__init__.py
memory/models.py
memory/schema.py
memory/database.py
memory/repository.py
memory/admission_models.py
memory/admission.py
tests/conftest.py
tests/test_atomic_rollback.py
tests/test_delete_protection.py
tests/test_lineage_protection.py
tests/test_semantic_update.py
tests/test_memory_admission.py
tests/test_memory_admission_authority.py
tests/test_memory_admission_rejection.py
tests/test_memory_admission_supersede.py
```

All 16 Python source files match the Qwen input archive byte-for-byte. The older Foundation/Admission archive differs, after path normalization, only by a test docstring saying schema v2 rather than v3. No executable/test assertion difference was found in that comparison. Original cache/bytecode entries were excluded from the runnable recovery directory.

Verified recovered behavior: schema version 3 contains `contradicts_memory_id`; Admission's conflict lookup consumes that explicit ID and checks the selected record is VALID. This does not promote the historical reference ontology or every historical implementation choice to the current MIP contract.

### Tests actually executed on recovered source

```text
cwd = recovered_reference/
command = PYTHONDONTWRITEBYTECODE=1 python -m pytest -q -p no:cacheprovider --junitxml=../foundation_v3_regression.xml
initial execution = 2026-09-06T07:47:55Z
Python = 3.13.5
pytest = 9.0.2
SQLite = 3.46.1
platform = Linux x86_64
PASS = 25
FAIL = 0
ERROR = 0
SKIP = 0
source/test checksums before vs after = ALL UNCHANGED
reproducibility script rerun = 25 PASS
```

Scope: original Memory Foundation + Admission tests ONLY. No Python Authority Resolver test has run because its complete source/tests were not recovered. No training or DEV/Frozen data access occurred.

### Missing Authority source — current blocker

Not recovered from the searched Library/conversation material or any of the three archives:

```text
memory/authority_resolver.py
memory/authority_models.py
original tests/test_authority_resolver*.py
```

Refined searches and the Library Python/archive inventory were checked. The related files found are an audit (`fff.txt`), prompts, previous summaries and the pre-code verification report, not complete executable Authority source.

`MATRIXNEO23/memoria/main` was read-only inspected again: its complete tree (`truncated=false`, SHA `712d84df59aa2e0ce153f6287e4b214c789dc49b`) contains only `README.md` and `PROJECT_WORK_RULES.md`. No Python source was imported or written there.

Do not reconstruct an alleged original resolver from audit snippets and call it a minimal fix. Do not mark the reported P0 defects CLOSED without patch and test evidence.

## Recovered meaning of gates A/B/C

Source: `MATRIX_AUTHORITY_RESOLVER_PRECODE_VERIFICATION.txt`, Library file `file_00000000d2a082439462343a59e384c8`.

```text
GATE A = Foundation/Admission v3 source recovery + provenance/checksums
GATE B = Authority MIP contract + read-only evidence + temporal/contradiction invariants
GATE C = Authority test specification + Admission integration/regression requirements
GATE D = implementation
```

Current evidence:

```text
GATE A Foundation/Admission recovery = RECOVERED_AND_REGRESSION_PASS
Python Authority original source recovery = SOURCE_MISSING
GATE B/C Python closure = NOT DECLARED BY THIS CHECKPOINT
Python Authority P0 fixes = NOT EXECUTED
```

Existing Kotlin Authority contracts/tests are reusable evidence, but Kotlin CI success alone is not proof that the original Python resolver has been fixed or its gates completed.

## Recovery artifacts created in this conversation

```text
MATRIX_AUTHORITY_SOURCE_RECOVERY_2026-09-06.zip
bytes = 29328
SHA256 = 7fa7def99183dd9769cc3b167224ddda59493467bc26fee48dcd7aa6008c4c28
contents = 16 original Python files, checksums, source manifest, report, unchanged-test log/JUnit XML, comparison diff, verification/rerun script

MATRIX_AUTHORITY_RECOVERY_REPORT_2026-09-06.txt
SHA256 = 5eb3708581a3ad19c7950edaaf5f18a7b7944e09e63f554271a691f8d58abe02
```

Working directory in this session: `/mnt/data/authority_recovery/`. Container paths alone are not durable cross-session locators; recover original archives using the Library IDs/hashes above or the delivered conversation ZIP. Verify checksums before reuse.

## Scope and stop state

```text
functional assembling changes this recovery checkpoint = NONE
assembling mutation = THIS CONTINUITY DOCUMENT ONLY
other repository writes = false
Library originals modified = false
Foundation/Admission source or existing tests modified = false
Python Authority P0 code changes = NOT EXECUTED (source missing)
Understanding/Authority integration = NOT STARTED
Memory Kotlin/Room = NOT STARTED
Student-5 / matrix-understanding-lab changes = NONE
training / DEV / Frozen access = NONE
```

The functional Assembling baseline remains frozen. This mandatory continuity save is documentation, not authorization to start a different module/repository or bypass step 2.

## Exact restart point

```text
repo for continuity = MATRIXNEO23/assembling
branch = main
HEAD before this documentation commit = 506e1e0f5a30fbe0c374b14ba6b8a4960606a9b6
functional CP-U3 merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
CP-U3 POST-MERGE CI = 33966306986 SUCCESS (reverified)
current documentation commit CI = NOT YET OBSERVED AT WRITE TIME
CP-U3 = COMPLETE / DO NOT REDO
Python Foundation/Admission recovery = COMPLETE / 25 TESTS PASS
ACTIVE = PYTHON AUTHORITY P0 / ORIGINAL SOURCE BLOCKER
NEXT = obtain exact complete authority_resolver.py, authority_models.py and original Authority tests; preserve hashes; reproduce P0; apply approved minimal fixes and rerun regressions
AFTER = integrate Authority with real Understanding V3 claims
THEN = Memory Kotlin/Room
```
