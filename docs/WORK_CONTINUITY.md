# Work Continuity — Matrix Assembling

Last updated: 2026-09-05T14:34+02:00  
Repository: `MATRIXNEO23/assembling`  
Canonical branch: `main`  
Continuity schema: `matrix.assembling.continuity.v72`

## Owner-approved execution order

```text
1. IMPLEMENT UNDERSTANDING V3
2. FIX P0 BUGS OF PYTHON AUTHORITY RESOLVER
3. INTEGRATE AUTHORITY RESOLVER WITH UNDERSTANDING
4. IMPLEMENT MEMORY KOTLIN/ROOM
```

## CP-U3 — COMPLETE / MERGED / GREEN

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

Initial CI failure `33966040483` was a local compile-scope bug in the SHA-256 validator and was fixed by `b53eefd84f12847ba000e96ca7342975b0187001`; no gate or architecture was weakened.

## Completed Understanding baseline — DO NOT REDO

```text
CP-U1 lossless audit = COMPLETE / PASS
CP-U2 MIP-1.0/UNDERSTANDING-V3-1.0 profile = COMPLETE / MERGED / GREEN
CP-U3 runtime + real canonical TypedClaims = COMPLETE / MERGED / GREEN
```

## Repository switch checkpoint

`assembling` is now frozen/read-only for the next owner-approved step.

No Authority integration or Memory code has been started after CP-U3.

## NEXT OWNER-APPROVED STEP — PYTHON AUTHORITY RESOLVER P0

Reference evidence recovered read-only from ChatGPT Library identifies the historical Python Authority P0 defects:

```text
P0-PA-01 owner hardcoded "test_agent" in candidate search
P0-PA-02 fragile property extraction via regex/free-text parsing
P0-PA-03 false conflict scoring from actor overlap + content difference
```

Canonical direction, now enabled by CP-U3:

```text
Python Authority Resolver should consume structured TypedClaim/MIP semantics
(subject/predicate/object-target/owner/perspective/polarity/temporal/claimKind/source/provenance)
and must not re-interpret natural-language text or infer contradiction from actor/content heuristics.
```

Before writing Python code, recover the exact source revision and canonical Memory Foundation v3 support files/tests from Library; preserve provenance/checksums; do not rebuild from scratch if the real source exists.

## Exact restart point

```text
ASSEMBLING HEAD = 089cb7169c5f511ffd5d27b8a1d5e887c4348b0c
ASSEMBLING POST-MERGE CI = 33966306986 SUCCESS
CP-U3 = COMPLETE / CLOSED
assembling writes = STOPPED for next step
NEXT = recover exact Python Authority Resolver source + canonical Memory Foundation v3 references, then fix only P0 defects with tests
AFTER = integrate Authority with real Understanding V3 claims
THEN = Memory Kotlin/Room
```
