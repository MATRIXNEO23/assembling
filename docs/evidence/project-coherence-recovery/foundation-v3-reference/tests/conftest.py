"""Fixture condivise per la suite di test della Memory Foundation."""
import pytest
from memory.database import DatabaseManager
from memory.repository import MemoryRepository
from memory.schema import SchemaManager
from memory.models import Authority, MemoryCategory, MemoryRecord, ValidityStatus


def create_dummy_record(**overrides) -> MemoryRecord:
    """Helper per costruire un MemoryRecord valido con valori di default sensati.

    I valori di default coprono tutti i campi obbligatori richiesti dallo schema v2.
    Gli `overrides` permettono di sovrascrivere qualsiasi campo per test specifici.
    """
    defaults = {
        "owner": "test_agent",
        "memory_type": "episodic",
        "category": MemoryCategory.EPISODIC,
        "content": "Test content",
        "summary": "Test summary",
        "actors": ["Alice"],
        "entities": ["ProjectX"],
        "world_time": None,
        "real_time": 1_700_000_000,
        "location_id": None,
        "authority": Authority.OBSERVATION,
        "provenance": "test_suite",
        "source_event_id": None,
        "confidence": 0.8,
        "salience": 0.5,
        "emotional_weight": 0.0,
        "validity": ValidityStatus.VALID,
        "links": [],
        "goal_id": None,
    }
    defaults.update(overrides)
    return MemoryRecord(**defaults)


@pytest.fixture
def db_manager(tmp_path):
    """DatabaseManager con schema v2 inizializzato e PRAGMA foreign_keys=ON."""
    db_path = tmp_path / "test_memory.db"
    dm = DatabaseManager(db_path)
    # connect() abilita PRAGMA foreign_keys=ON e journal_mode=WAL
    conn = dm.connect()
    SchemaManager.create_schema(conn)
    yield dm
    dm.close()


@pytest.fixture
def repo(db_manager):
    """MemoryRepository collegato al DB di test.

    Usa db_manager.connect() perché DatabaseManager non espone .conn come proprietà.
    """
    return MemoryRepository(db_manager.connect())