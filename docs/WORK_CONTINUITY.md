# Work Continuity — Matrix Assembling

Latest operational state: read the current recovery entrypoint below; older dated checkpoints are retained as history.  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v74`

## Current recovery entrypoint — recover artifacts before repeating work

The owner reports repeated work caused by lost artifact continuity. The immediate requirement is recovery and durable identification, not re-execution.

- Active write repository remains `MATRIXNEO23/assembling`; model ownership remains with `MATRIXNEO23/matrix-understanding-lab`. No model integration is authorized by this registry link.
- Verified Engine code checkpoint R1: `994ba3f75d76d2f3d3dd60aea9da20546be99529`, PR #24; 159 JVM fixture tests, successful pre/post-merge CI and remote readback. Full Engine/model validation remains incomplete as documented.
- **Canonical model recovery registry:** [STUDENT5_MODULE_INDEX.md at verified Lab HEAD](https://github.com/MATRIXNEO23/matrix-understanding-lab/blob/23341d2db7fbe6bd42907a8a742540c4dab1a5a6/STUDENT5_MODULE_INDEX.md). Use its CP45/CP47 identities, per-epoch hashes and recovery instructions. Do not create a competing module index or relabel old artifacts.
- **CP47 recovered afresh:** Release `384423046`, asset `549780835`, `student5-matrix-nlu-v3-fp32-cp47-run1.tar.xz`, 93,830,912 bytes, SHA-256 `029bc292c11091da3aa5742ac85017ffdbc7dbb7ba7be6fadc5cc603f07ceafa`. Fresh remote bytes match; all 58 internal checksum entries and all 10 epoch model hashes match. These ten checkpoints do not need retraining to be recovered. Selection remains pending; this is an integrity/recovery check, not model loading or evaluation.
- Evidence: `docs/evidence/artifact-recovery-cp47/readback.json` and `SHA256SUMS`. The archive remains in its original durable Release; the newly downloaded local file is only a disposable verification copy.
- Pristine Release `383143636` / asset `545406840` and CP45 Release `384399324` / asset `549697344` are still listed remotely with the expected sizes and published hashes. This particular check verified their metadata, not a fresh download of both binaries. Do not conflate those levels of evidence.

Recovery rules for the next session:

1. Open this continuity and the existing canonical index first. Recover by repository + pinned commit/path or Release/asset ID + size/SHA, never chat memory alone.
2. A missing local file, expired Actions artifact, broken download or unknown locator is not proof that the canonical artifact is lost. Quarantine invalid local bytes; recover and authenticate the persisted original before considering any reconstruction.
3. Do not call a deliverable durably saved if its only copy is session storage, cache or a temporary Actions artifact. Important binaries need the approved Release/persistent route; code, reports and recovery metadata need a Git commit. For LFS, verify the payload as well as the pointer when claiming byte recovery.
4. Before closing work, persist identity, parent lineage, locator, sizes, checksums and recovery instructions; actually read back from the destination. Record metadata-only, byte-verified and functionally-tested states separately.
5. If recovery remains blocked, preserve the evidence and exact missing identity. Do not automatically rebuild datasets, retrain, recreate DEV/gold, select a model or start another module.

The historical execution order and COMPLETE labels below describe their original checkpoints. They are not automatic instructions to redo work or to treat the entire project as functionally validated.

## Owner-approved execution order

```text
1. IMPLEMENT UNDERSTANDING V3
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
4. IMPLEMENT MEMORY KOTLIN/ROOM
```

## 1 — Understanding V3 — COMPLETE / MERGED / GREEN

Do not redo CP-U1/U2/U3.

```text
CP-U3 PR = #19
final head = 649af878630e49eba2934b14dd45862fcfb8de5b
merge = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
post-merge CI = 33966306986 SUCCESS
```

Canonical path:

```text
Matrix-NLU V3 runtime output
-> CanonicalUnderstandingV3Adapter
-> MipUnderstandingV3Observation
-> MipUnderstandingV3Claim[] real canonical V3 TypedClaims
-> MatrixTurnFrame.canonicalUnderstandingV3
```

Legacy `TypedClaim`, `NluOutput` and `SemanticFrame` are not auto-populated from V3.

## 2 — Python Authority Resolver P0 — COMPLETE / MERGED / GREEN

The previously missing source was found on the existing repository branch:

```text
repo = MATRIXNEO23/memoria
branch = python-authority-p0-v1
final head = f28fd33bbf3297072ef4873514ae0a551ea4576b
PR = memoria #1
pre-merge GitHub CI = run 34020629980 / SUCCESS
merge = b8cc7e2133868049550d3c63d78f69da8f830f20
continuity closure = cde91db20d97e0792b61db15144faf1430fd27bc
```

Closed P0s:

```text
P0-PA-01 hardcoded owner "test_agent"
  -> structured claim.owner is passed to read-only MemoryEvidencePort

P0-PA-02 regex/free-text property extraction
  -> removed from Authority path; structured predicate/object/polarity/temporal semantics only

P0-PA-03 actor-overlap + content-difference false conflict
  -> contradiction now requires same semantic slot/scope, compatible temporal identity,
     VALID candidate, and incompatible structured value/polarity
```

Memory Foundation v3 boundary preserved:

```text
AuthorityResolution contradiction identity
-> explicit contradicts_memory_id / MemoryRef
-> Memory Admission
-> MemoryRepository
```

Authority does not write Memory. Authority and confidence remain separate concepts.

## 3 — Canonical Understanding V3 -> Authority — COMPLETE / MERGED / GREEN

A first experimental branch/PR (#20) was deliberately closed without merge when the Python P0 gate was discovered still open. It is historical/quarantined and must not be used as the canonical merge.

Canonical implementation:

```text
branch = cp-a2-v3-authority-wiring
PR = #21 — CP-A2: wire canonical Understanding V3 into Authority
head = b7ea750665a563cbca673b4050a72adc21275a8e
Matrix Assembling CI = run 34020878347 / run #156 / SUCCESS
merge = d3994e59008aac648576a252eac0d7c4e1028589
```

Integrated path:

```text
MatrixTurnFrame.canonicalUnderstandingV3
-> CanonicalUnderstandingV3AuthorityPort
-> authority-specific structural MipClaimV1 projection
-> DeterministicAuthorityResolver
-> AuthorityResolution[]
-> MatrixTurnFrame.canonicalAuthorityResolutions
```

Preserved:
- original V3 claim IDs and claim provenance;
- independent source/subject/target/owner/perspective identities;
- claimKind, dialogueAct, polarity and temporal anchor;
- object evidence from explicit V3 object span, not regex/free-text property parsing;
- fail-closed ambiguity/unresolved states;
- no legacy root TypedClaim dependency;
- no Memory writes.

Important current limitation deliberately preserved:

```text
Mip RetrievalResult has no explicit claimId binding.
```

Therefore the integration only binds retrieval when it is unambiguous (one claim / one result). Multi-claim retrieval remains UNRESOLVED instead of guessing by list order or parsing query IDs.

`MatrixTurnFrame` canonical Authority coverage validation now uses the real V3 claim IDs when `canonicalUnderstandingV3` is PRESENT; legacy `typedClaims` remain the fallback only for legacy paths.

## 4 — NEXT: Memory Kotlin/Room consuming AuthorityResolution

Current hard Memory architecture remains:

```text
PRE-RESPONSE READ / ENRICH
-> lightweight index probe / retrieval
-> no durable write

PRE-RESPONSE EVALUATE / PROPOSE
-> MemoryPreflightPort
-> ephemeral MemoryCandidate only

POST-VALIDATION COMMIT
-> PersistentConsolidationPort
-> Memory Admission
-> MemoryRepository
-> atomic SAVE / SUPERSEDE / metadata operation
```

Durable persistence must consume canonical `AuthorityResolution`, including explicit contradiction identity. Memory Admission must not rediscover semantic contradictions from raw text, actor overlap or unrelated predicates.

Before implementing Room, verify the Android-capable target/module. Current `assembling` build is JVM-only; do not add fake Room persistence to a non-Android module merely to satisfy the milestone. Preserve one-repository-at-a-time work and explicit provenance when moving the Memory implementation into its Android-capable integration target.

## Current status

```text
Understanding V3 = COMPLETE
Python Authority P0 = COMPLETE
Understanding -> Authority = COMPLETE
Memory Kotlin/Room = ACTIVE NEXT
Memory durable persistence = NOT YET IMPLEMENTED
```

## Exact restart point

```text
assembling main functional HEAD before this continuity commit = d3994e59008aac648576a252eac0d7c4e1028589
step 1 = CLOSED
step 2 = CLOSED
step 3 = CLOSED
NEXT = verify Android/Room target, then implement Memory Kotlin/Room consuming canonical AuthorityResolution
```


## Recovery checkpoint R1 — V3 → Authority → prompt (2026-09-08)

Repository attiva: `MATRIXNEO23/assembling`; base `8e94db9e692e387f8ef2b5d528f8dd3f1c1d8fcf`.
Autorizzazione utente: recuperare il lavoro, verificarne le prove, eseguire il necessario sul collegamento individuato. Una sola repository scrivibile.

La riconciliazione delle due repo è persistita in `MATRIXNEO23/matrix-understanding-lab` al commit `23341d2db7fbe6bd42907a8a742540c4dab1a5a6`, report `reports/MATRIX_PROJECT_RECONCILIATION_20260908.md`. Non è una validazione funzionale dei modelli.

Task corrente: riprodurre l'interruzione del prompt root quando riceve Understanding e Authority canonici V3; correggere esclusivamente quel confine e verificarne regressioni e perdita di campi. Nessuna modifica ai contratti congelati, nessun rewire dei moduli assenti.

La dicitura storica COMPLETE sopra va letta per il checkpoint allora testato, non come prova di Engine completo. L'orchestrator esistente continua a usare porte legacy per Coherence/Memory/Affective; l'inferenza NLU reale, GGUF reale, MemoryRepository persistente e test APK non sono dimostrati da questa suite JVM.

Gate corrente all'avvio: baseline JVM e regressione mirata. Risultati finali, evidenze e punto di ripresa sono registrati nel report R1 aggiunto a chiusura. Altri repository modificati in questo checkpoint: false.

### R1 local gate result

- Baseline: 150/150 tests pass, zero skipped.
- Reproduction: 6 tests, one expected failure `MatrixTurnFrame missing semantic frame`.
- Targeted correction: V3 rendering in root prompt; no legacy semantic projection, no frozen-contract change.
- After: 159/159 tests pass, zero skipped; independent Python JSON verification PASS.
- Evidence: `docs/RECOVERY_R1_V3_PROMPT_20260908.md` and `docs/evidence/recovery-r1-20260908/`.
- Current gate: persist branch/PR, final-head CI, merge only if green, post-merge CI and remote byte readback.
- Exact restart: complete R1 remote gates; do not treat this as full orchestrator/model/Memory/Android validation. Historical “Memory ACTIVE NEXT” is not authorization to fabricate a new backend during recovery.

### R1 remote source gate and recovery locator

- PR: https://github.com/MATRIXNEO23/assembling/pull/24
- Code/evidence source commit: `5f9495c1def64296fe724fb7933b44191e0b7092`.
- Source PR CI: run `34198222276`, job `101970687729`, PASS; `gradle test` executed, BUILD SUCCESSFUL in 42s.
- Remote recovery: all 19 files of source commit fetched again; UTF-8 bytes, SHA-256 and Git blob identities match.
- Source code is frozen after that successful CI; this closure adds only receipts/documentation.
- **Publication receipt:** the final body of PR #24 records final PR head, final-head CI, merge/main SHA, post-merge CI and final readback. It is the durable exact restart locator and avoids a self-referencing commit SHA inside a commit. If that receipt is missing, publication has not yet been confirmed.
- After the publication receipt is PASS: R1 software boundary is closed; all broader runtime limitations in `docs/RECOVERY_R1_V3_PROMPT_20260908.md` remain open. No next module/training stage has been started.
