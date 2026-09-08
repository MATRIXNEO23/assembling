# Understanding candidate — jangedoo/multilingual-e5-small-pruned

Status: `ISOLATED_TEST_CANDIDATE` — not integrated, not production-approved.

Upstream: `jangedoo/multilingual-e5-small-pruned`
Pinned upstream revision: `696e4b64a2e33f6ffa036d24a666a16fd2280556`
Base model: `intfloat/multilingual-e5-small`

Purpose: evaluate this already-pruned multilingual encoder as an alternative Understanding/NLU backbone without modifying or replacing the current NLU.

Expected upstream snapshot files:
- `model.safetensors` (351,646,568 bytes; upstream LFS SHA-256 `e8de21ecee219f55eb9a29e3f0ba7a6167756b6109512d201895b3158e97ae3e`)
- `config.json`
- `tokenizer.json`
- `tokenizer_config.json`
- `pruning_tokenizer.py`
- `modules.json`
- `sentence_bert_config.json`
- `config_sentence_transformers.json`
- `1_Pooling/config.json`
- `README.md`

The custom tokenizer/remapping code is required. Loading is expected through SentenceTransformers with `trust_remote_code=True`.

The model binary is intentionally not represented by a fake Git-LFS pointer. Use `fetch_upstream.py` in this directory to materialize the complete pinned upstream snapshot before testing.

No existing canonical files, MIP contracts, Understanding implementation, Student models, or current NLU artifacts are replaced by this candidate.
