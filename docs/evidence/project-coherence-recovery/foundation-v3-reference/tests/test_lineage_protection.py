"""Test — Protezione della cancellazione del lineage via Foreign Keys (SQL diretto)."""
import sqlite3
import pytest
from memory.models import ValidityStatus
from conftest import create_dummy_record


def test_lineage_deletion_protection_via_foreign_keys(db_manager, repo):
    """
    Verifica che la catena A → B → C sia protetta dalle cancellazioni
    fisiche tramite Foreign Keys (PRAGMA foreign_keys=ON).

    Struttura reale dopo A → B → C:
      A: revision_of=NULL,  superseded_by=B
      B: revision_of=A,     superseded_by=C
      C: revision_of=A,     superseded_by=NULL

    Vincoli FK attivi:
      - Cancellare A fallisce perché B.revision_of → A
      - Cancellare B fallisce perché A.superseded_by → B
      - Cancellare C fallisce perché B.superseded_by → C
    """
    # ── 1. Costruzione catena A → B → C ──
    with db_manager.transaction():
        id_a = repo.save(create_dummy_record(content="v1"))

    with db_manager.transaction():
        id_b = repo.supersede(id_a, create_dummy_record(content="v2"))

    with db_manager.transaction():
        id_c = repo.supersede(id_b, create_dummy_record(content="v3"))

    # ── 2. Verifica struttura lineage conforme al contratto ──
    a = repo.get_by_id(id_a)
    b = repo.get_by_id(id_b)
    c = repo.get_by_id(id_c)

    assert a.validity == ValidityStatus.SUPERSEDED
    assert a.superseded_by == id_b
    assert a.revision_of is None
    assert a.revision_count == 0

    assert b.validity == ValidityStatus.SUPERSEDED
    assert b.superseded_by == id_c
    assert b.revision_of == id_a
    assert b.revision_count == 1

    assert c.validity == ValidityStatus.VALID
    assert c.superseded_by is None
    assert c.revision_of == id_a
    assert c.revision_count == 2

    # ── 3. Cancellare A fallisce: B.revision_of → A ──
    with pytest.raises(sqlite3.IntegrityError):
        repo.conn.execute("DELETE FROM memories WHERE id = ?", (id_a,))
    repo.conn.rollback()

    # ── 4. Cancellare B fallisce: A.superseded_by → B ──
    with pytest.raises(sqlite3.IntegrityError):
        repo.conn.execute("DELETE FROM memories WHERE id = ?", (id_b,))
    repo.conn.rollback()

    # ── 5. Cancellare C fallisce: B.superseded_by → C ──
    with pytest.raises(sqlite3.IntegrityError):
        repo.conn.execute("DELETE FROM memories WHERE id = ?", (id_c,))
    repo.conn.rollback()

    # ── 6. Integrità finale: tutto intatto ──
    assert repo.get_by_id(id_a).validity == ValidityStatus.SUPERSEDED
    assert repo.get_by_id(id_b).validity == ValidityStatus.SUPERSEDED
    assert repo.get_by_id(id_c).validity == ValidityStatus.VALID