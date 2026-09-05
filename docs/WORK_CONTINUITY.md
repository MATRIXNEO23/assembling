# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T11:16+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Active branch: `authority-resolver-v1`  
Continuity schema: `matrix.assembling.continuity.v45`

## Mandatory continuity policy

This is the single canonical restart file for Assembling. Update it after every significant checkpoint: task/branch start, architecture/contract decision, code checkpoint, test/CI result, strategy change, before risky operations, and before every STOP/session end.

## Completed baseline — DO NOT REDO

```text
MIP = MIP-1.0
AUTHORITY-1.0 = FROZEN
Authority value types PR #11 = b87dadf376300587511a7dbce594b0fe88695798
shared MIP evidence PR #12 = 8f45a631b70c283169d058d98d1c880b5e37e554
Authority runtime DTO PR #13 = 6841d916ba8a28a5bfc16ab4b0fa679e40c555fc
post-merge DTO CI = 33956522738 SUCCESS
pre-resolver continuity = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
pre-resolver continuity CI = 33957143955 SUCCESS
```

Hard rules remain: MIP is the single cross-module semantic authority; other repos are read-only; no gate weakening; no orchestrator/Memory write changes in this task.

## ACTIVE TASK — REAL AUTHORITY RESOLVER ONLY

```text
branch = authority-resolver-v1
base = 3e413509ea60f4ea60ee2fe2382c6f37e892da6d
other repos modified = false
```

### Checkpoint 1 — read-only candidate evidence projection

Commit:

`389872dd24bd485a4873d0d8de6ccea63171248a`

File:

`src/main/kotlin/matrix/assembling/authority/AuthorityCandidateEvidence.kt`

Added:

```text
AuthorityCandidateEvidence
AuthorityCandidateEvidencePort
```

Properties:

- projection is explicitly NOT a MemoryRecord/persistence/admission DTO;
- port exposes only `read(memoryRef, contextSnapshot)`;
- no save/update/delete/supersede/admit operation exists;
- projection carries memory identity, validity, subject/predicate/object/target/owner/perspective/source, polarity, temporal relation/reference key and provenance;
- validity remains an observed `MipField<String>`; Authority does not own Memory lifecycle vocabulary;
- only `PRESENT("VALID")` may become an active contradiction target in resolver logic;
- resolved/unresolved entity semantics remain MIP entity semantics.

### Resolver policy to implement next

Authority classification precedence:

```text
trusted WORLD provenance + explicit WORLD_TRUTH -> WORLD_TRUTH
explicit PERCEPTION/OBSERVATION provenance -> OBSERVATION
explicit derived INFERENCE provenance -> INFERENCE
structured REPORT/source evidence -> REPORT
structured BELIEF/HYPOTHESIS evidence -> BELIEF
ordinary USER_ASSERTION/self-report -> REPORT
otherwise -> HOLD/UNRESOLVED
```

WORLD_TRUTH must never be granted from compatibility boolean/string alone without trusted WORLD provenance.

Contradiction remains conservative:

- candidate must be VALID;
- exact resolved subject + normalized predicate required;
- owner/source/perspective scope compared when applicable;
- different predicates are unrelated even with same actor;
- temporal change is not contradiction by default;
- broad historical/reference labels without stable temporal identity remain unresolved rather than guessed;
- opposite polarity on same semantic value may contradict;
- different values contradict only for registered single-value predicates;
- multiple concrete contradiction targets -> AMBIGUOUS/HOLD;
- any unresolved candidate that could affect uniqueness prevents selecting a concrete ID;
- correction adds candidate-priority diagnostics only; never automatic contradiction/supersession.

## Explicitly NOT authorized

```text
MemoryRepository dependency
Memory writes
Memory Admission decisions
PersistentConsolidation
BasicAuthorityResolver replacement in orchestrator
root AuthorityDecision migration
final MipBridge migration
orchestrator rewiring
retrieval engine
other repo writes
```

## Exact restart point

```text
repo = MATRIXNEO23/assembling
branch = authority-resolver-v1
last functional commit = 389872dd24bd485a4873d0d8de6ccea63171248a
read-only candidate evidence port = IMPLEMENTED / UNTESTED
real AuthorityResolver = NEXT / NOT YET CODED
MipBridge final Authority migration = NOT_STARTED
orchestrator rewiring = NOT_STARTED
other repos = READ-ONLY
NEXT = IMPLEMENT DETERMINISTIC AUTHORITY RESOLVER + P0 TESTS
```
