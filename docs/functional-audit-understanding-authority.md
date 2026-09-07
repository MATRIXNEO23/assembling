# Functional Audit — Understanding V3 → Authority

Status: FIX CANDIDATE — awaiting full CI regression

## Evidence from local execution

Environment:
- Java 17.0.20.1
- Gradle 9.7.1

Observed before fix:
- single-claim structured Understanding V3 → Authority: PASS
- multi-claim explicit RetrievalResult binding: FAIL
- failure: claim-bound retrieval was not delivered to Authority

Root cause:
`CanonicalUnderstandingV3AuthorityPort` still used legacy logic that treated multi-claim retrieval as unresolved even after `RetrievalResult` gained explicit `claimId` and `contextSnapshotId` bindings.

Fix applied:
- select retrieval by explicit PRESENT `claimId`
- require matching PRESENT `contextSnapshotId`
- reject duplicate matching results fail-closed
- preserve the legacy single-claim/single-result fallback only for an unbound legacy result
- do not guess by list order or queryId parsing

Observed after fix:
- single-claim structured path: PASS
- multi-claim retrieval binding: PASS

Scope limitation:
This audit proves structured Understanding V3 → Authority transport and decision wiring. It does **not** prove raw-text NLU comprehension.
