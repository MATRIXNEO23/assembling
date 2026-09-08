# Recovered Memory Foundation v3 reference

These 16 existing source/test files were recovered by exact SHA-256 from the original Library files, against `MATRIXNEO23/memoria` commit `cde91db20d97e0792b61db15144faf1430fd27bc`, `REFERENCE_FOUNDATION_V3.md`. No implementation was rewritten. This directory is a read-only recovery reference, NOT an Engine import or new canonical module.

Recover by checking out the Assembling commit that contains this file and verifying `../SHA256SUMS` from the repository root. Per-file original identities and locators: `../foundation-provenance.json`. Preserve the `memory/` and `tests/` layout. In an isolated environment with Python 3.12.13 and pytest 8.4.1, run `python -m pytest -q` from this directory. Existing tests use disposable SQLite databases, not product data. Result in this recovery: 25 passed. Android/Room integration and process-restart testing are not claimed.
