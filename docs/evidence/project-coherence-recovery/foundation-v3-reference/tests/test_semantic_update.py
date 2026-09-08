"""Test 7 — Protezione dalle modifiche semantiche e consistenza del lineage."""
import pytest
from memory.models import ValidityStatus
from conftest import create_dummy_record


def test_semantic_update_protection_and_lineage(repo, db_manager):
    """
    Verifica che:
    1. update() non esista più come metodo del repository
    2. update_metadata() rifiuti campi semantici (content, summary, actors, entities)
    3. Le modifiche semantiche passino esclusivamente da supersede()
    4. Il record originale resti congelato (SUPERSEDED) con i valori originali
    5. JSON nella tabella memories e tabelle associative siano consistenti
    6. revision_of punti alla radice della famiglia (non al padre immediato)
    """
    # ── 1. Creazione del record originale A ──
    record_a = create_dummy_record(
        actors=["Alice"], entities=["ProjectX"], content="v1", summary="s1"
    )
    with db_manager.transaction():
        id_a = repo.save(record_a)

    # ── 2. update() non deve esistere ──
    assert not hasattr(repo, "update"), (
        "update() non deve esistere: le modifiche semantiche "
        "devono passare da supersede()"
    )

    # ── 3. update_metadata() rifiuta campi semantici ──
    for field in ("content", "summary", "actors", "entities"):
        with pytest.raises(ValueError, match="Cannot update fields"):
            repo.update_metadata(id_a, **{field: "hacked"})

    # ── 4. Evoluzione corretta tramite supersede() ──
    record_b = create_dummy_record(
        actors=["Alice", "Bob"],
        entities=["ProjectY"],
        content="v2",
        summary="s2",
    )
    with db_manager.transaction():
        id_b = repo.supersede(id_a, record_b)

    # ── 5. A è congelato e marcato SUPERSEDED ──
    state_a = repo.get_by_id(id_a)
    assert state_a.validity == ValidityStatus.SUPERSEDED
    assert state_a.superseded_by == id_b
    assert state_a.actors == ["Alice"]
    assert state_a.entities == ["ProjectX"]
    assert state_a.content == "v1"
    assert state_a.summary == "s1"

    # ── 6. B è VALID con i nuovi valori ──
    state_b = repo.get_by_id(id_b)
    assert state_b.validity == ValidityStatus.VALID
    assert state_b.actors == ["Alice", "Bob"]
    assert state_b.entities == ["ProjectY"]
    assert state_b.content == "v2"
    assert state_b.summary == "s2"

    # ── 7. Lineage: B.revision_of punta alla radice A ──
    assert state_b.revision_of == id_a
    assert state_b.revision_count == 1

    # ── 8. Consistenza JSON ↔ tabelle associative per B ──
    actors_from_table = sorted(
        row["actor_id"]
        for row in repo.conn.execute(
            "SELECT actor_id FROM memory_actors WHERE memory_id = ?",
            (id_b,),
        ).fetchall()
    )
    entities_from_table = sorted(
        row["entity_id"]
        for row in repo.conn.execute(
            "SELECT entity_id FROM memory_entities WHERE memory_id = ?",
            (id_b,),
        ).fetchall()
    )
    assert actors_from_table == sorted(state_b.actors)
    assert entities_from_table == sorted(state_b.entities)

    # ── 9. update_metadata() funziona per i campi consentiti ──
    with db_manager.transaction():
        repo.update_metadata(id_b, confidence=0.95, salience=0.8)
    updated_b = repo.get_by_id(id_b)
    assert updated_b.confidence == 0.95
    assert updated_b.salience == 0.8
    # I campi semantici non sono cambiati
    assert updated_b.content == "v2"
    assert updated_b.actors == ["Alice", "Bob"]