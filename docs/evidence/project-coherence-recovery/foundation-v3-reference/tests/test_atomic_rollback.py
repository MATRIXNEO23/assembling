"""Test 1 — Rollback atomico reale con fault injection."""
import pytest
from memory.models import ValidityStatus
from conftest import create_dummy_record


def test_atomic_rollback_on_supersede_failure(db_manager, repo, monkeypatch):
    """
    Dimostra che supersede() è atomico quando eseguito dentro
    DatabaseManager.transaction().

    Scenario:
      - save(B) riesce (INSERT del nuovo record)
      - mark_superseded(A, B) fallisce (fault injection)
      - Il rollback di transaction() deve annullare l'INSERT di B
      - A deve rimanere VALID con superseded_by=None
    """
    # ── 1. Creazione e persistenza di A ──
    with db_manager.transaction():
        id_a = repo.save(create_dummy_record(content="original"))

    # ── 2. Fault injection: mark_superseded fallisce ──
    def failing_mark(*args, **kwargs):
        raise RuntimeError("Simulated DB failure during mark_superseded")

    monkeypatch.setattr(repo, "mark_superseded", failing_mark)

    # ── 3. Tentativo di supersede dentro una transazione atomica ──
    record_b = create_dummy_record(content="should_not_persist")

    with pytest.raises(RuntimeError, match="Simulated DB failure"):
        with db_manager.transaction():
            repo.supersede(id_a, record_b)

    # ── 4. Verifica rollback: A è ancora VALID, intatto ──
    state_a = repo.get_by_id(id_a)
    assert state_a is not None
    assert state_a.validity == ValidityStatus.VALID
    assert state_a.content == "original"
    assert state_a.superseded_by is None

    # ── 5. Verifica rollback: B non è stato persistito ──
    count = repo.conn.execute(
        "SELECT COUNT(*) FROM memories WHERE revision_of = ?",
        (id_a,),
    ).fetchone()[0]
    assert count == 0, "Il record B non deve esistere dopo il rollback"