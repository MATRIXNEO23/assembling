"""Test — Protezione cancellazione lineage tramite API pubblica repo.delete()."""
import sqlite3
import pytest
from memory.models import ValidityStatus
from conftest import create_dummy_record


def test_repo_delete_fails_on_lineage_chain(db_manager, repo):
    """
    Verifica che repo.delete() (l'API pubblica) sia bloccata dalle FK
    quando si tenta di cancellare un record che fa parte di una catena
    di revisione A → B → C.

    Questo test è complementare a test_lineage_protection.py:
    quello testa SQL diretto, questo testa l'API pubblica del repository.
    """
    # ── 1. Costruzione catena A → B → C ──
    with db_manager.transaction():
        id_a = repo.save(create_dummy_record(content="v1"))

    with db_manager.transaction():
        id_b = repo.supersede(id_a, create_dummy_record(content="v2"))

    with db_manager.transaction():
        id_c = repo.supersede(id_b, create_dummy_record(content="v3"))

    # ── 2. Tentativo di cancellare A via API pubblica ──
    # B.revision_of → A, quindi la FK deve bloccare il DELETE
    with pytest.raises(sqlite3.IntegrityError):
        repo.delete(id_a)
    repo.conn.rollback()

    # ── 3. Tentativo di cancellare B via API pubblica ──
    # A.superseded_by → B, quindi la FK deve bloccare il DELETE
    with pytest.raises(sqlite3.IntegrityError):
        repo.delete(id_b)
    repo.conn.rollback()

    # ── 4. Tentativo di cancellare C via API pubblica ──
    # B.superseded_by → C, quindi la FK deve bloccare il DELETE
    with pytest.raises(sqlite3.IntegrityError):
        repo.delete(id_c)
    repo.conn.rollback()

    # ── 5. Verifica che tutti i record siano ancora intatti ──
    assert repo.get_by_id(id_a).validity == ValidityStatus.SUPERSEDED
    assert repo.get_by_id(id_b).validity == ValidityStatus.SUPERSEDED
    assert repo.get_by_id(id_c).validity == ValidityStatus.VALID


def test_repo_delete_succeeds_on_isolated_record(db_manager, repo):
    """
    Verifica che repo.delete() funzioni correttamente su un record
    isolato (senza catena di revisione), per confermare che il blocco
    nei test precedenti è dovuto alle FK e non a un bug generale.
    """
    # ── 1. Creazione record isolato (nessuna revisione) ──
    with db_manager.transaction():
        id_iso = repo.save(create_dummy_record(content="isolated"))

    # ── 2. Cancellazione dentro transazione atomica ──
    with db_manager.transaction():
        result = repo.delete(id_iso)

    assert result is True

    # ── 3. Verifica che il record non esista più ──
    assert repo.get_by_id(id_iso) is None