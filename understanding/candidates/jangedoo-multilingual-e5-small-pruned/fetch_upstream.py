#!/usr/bin/env python3
"""Materialize the complete pinned Hugging Face snapshot for this isolated candidate."""

from pathlib import Path
from huggingface_hub import snapshot_download

REPO_ID = "jangedoo/multilingual-e5-small-pruned"
REVISION = "696e4b64a2e33f6ffa036d24a666a16fd2280556"
HERE = Path(__file__).resolve().parent
DEST = HERE / "upstream_snapshot"

snapshot_download(
    repo_id=REPO_ID,
    revision=REVISION,
    local_dir=str(DEST),
)

print(f"Materialized pinned candidate at: {DEST}")
