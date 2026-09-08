"""Test per casi di supersede in Memory Admission.
Verifica che record con authority/confidence superiore sostituiscano quelli esistenti.
"""
import pytest
import time
from memory.database import DatabaseManager
from memory.repository import MemoryRepository
from memory.schema import SchemaManager
from memory.models import Authority, MemoryCategory, MemoryRecord, ValidityStatus
from memory.admission import MemoryAdmission
from memory.admission_models import AdmissionDecision


@pytest.fixture
def db_manager(tmp_path):
    db_path = tmp_path / "test_supersede.db"
    dm = DatabaseManager(db_path)
    conn = dm.connect()
    SchemaManager.create_schema(conn)
    yield dm
    dm.close()


@pytest.fixture
def repo(db_manager):
    return MemoryRepository(db_manager.connect())


@pytest.fixture
def admission(repo):
    return MemoryAdmission(repo)


def create_test_record(**overrides) -> MemoryRecord:
    defaults = {
        "owner": "test_agent",
        "memory_type": "episodic",
        "category": MemoryCategory.EPISODIC,
        "content": "Test content",
        "summary": "Test summary",
        "actors": ["Alice"],
        "entities": ["ProjectX"],
        "authority": Authority.OBSERVATION,
        "provenance": "test",
        "confidence": 0.8,
        "salience": 0.5,
        "emotional_weight": 0.0,
        "real_time": int(time.time()),
    }
    defaults.update(overrides)
    return MemoryRecord(**defaults)


def test_supersede_higher_authority(admission, db_manager, repo):
    """Test: record con authority superiore supersedea quello esistente."""
    # Salva record con authority BELIEF
    old_record = create_test_record(
        content="I think it will rain",
        actors=["Weather"],
        authority=Authority.BELIEF,
        confidence=0.7,
    )
    with db_manager.transaction():
        old_id = repo.save(old_record)
    
    # Nuovo record con authority OBSERVATION che contraddice esplicitamente
    new_record = create_test_record(
        content="It is raining",
        actors=["Weather"],
        authority=Authority.OBSERVATION,
        confidence=0.9,
        contradicts_memory_id=old_id,  # Contraddizione esplicita
    )
    result = admission.evaluate(new_record)
    
    assert result.decision == AdmissionDecision.SUPERSEDE
    assert result.superseded_id == old_id
    assert "higher authority" in result.reasoning
    
    # Esecuzione
    with db_manager.transaction():
        new_id = admission.execute(result)
    
    # Verifica che old sia SUPERSEDED
    old_state = repo.get_by_id(old_id)
    assert old_state.validity == ValidityStatus.SUPERSEDED
    assert old_state.superseded_by == new_id
    
    # Verifica che new sia VALID
    new_state = repo.get_by_id(new_id)
    assert new_state.validity == ValidityStatus.VALID
    assert new_state.content == "It is raining"


def test_supersede_higher_confidence(admission, db_manager, repo):
    """Test: record con confidence superiore supersedea (stessa authority)."""
    # Salva record con confidence 0.7
    old_record = create_test_record(
        content="Meeting at 3pm",
        actors=["Team"],
        authority=Authority.REPORT,
        confidence=0.7,
    )
    with db_manager.transaction():
        old_id = repo.save(old_record)
    
    # Nuovo record con confidence 0.95 (stessa authority)
    new_record = create_test_record(
        content="Meeting at 4pm",
        actors=["Team"],
        authority=Authority.REPORT,
        confidence=0.95,
        contradicts_memory_id=old_id,  # Contraddizione esplicita
    )
    result = admission.evaluate(new_record)
    
    assert result.decision == AdmissionDecision.SUPERSEDE
    assert result.superseded_id == old_id
    
    # Esecuzione
    with db_manager.transaction():
        new_id = admission.execute(result)
    
    # Verifica lineage
    new_state = repo.get_by_id(new_id)
    assert new_state.revision_of == old_id
    assert new_state.revision_count == 1


def test_supersede_more_recent(admission, db_manager, repo):
    """Test: record più recente supersedea (stessa authority e confidence)."""
    old_time = int(time.time()) - 3600
    new_time = int(time.time())
    
    # Salva record vecchio
    old_record = create_test_record(
        content="Price: $100",
        actors=["Product"],
        authority=Authority.REPORT,
        confidence=0.8,
        real_time=old_time,
    )
    with db_manager.transaction():
        old_id = repo.save(old_record)
    
    # Nuovo record più recente
    new_record = create_test_record(
        content="Price: $90",
        actors=["Product"],
        authority=Authority.REPORT,
        confidence=0.8,
        real_time=new_time,
        contradicts_memory_id=old_id,  # Contraddizione esplicita
    )
    result = admission.evaluate(new_record)
    
    assert result.decision == AdmissionDecision.SUPERSEDE
    assert result.superseded_id == old_id


def test_no_supersede_lower_authority(admission, db_manager, repo):
    """Test: record con authority inferiore NON supersedea."""
    # Salva record con authority OBSERVATION
    old_record = create_test_record(
        content="Direct observation",
        actors=["Witness"],
        authority=Authority.OBSERVATION,
        confidence=0.9,
    )
    with db_manager.transaction():
        old_id = repo.save(old_record)
    
    # Nuovo record con authority BELIEF (inferiore)
    new_record = create_test_record(
        content="I believe something else",
        actors=["Witness"],
        authority=Authority.BELIEF,
        confidence=0.95,
        contradicts_memory_id=old_id,  # Contraddizione esplicita
    )
    result = admission.evaluate(new_record)
    
    # Dovrebbe essere rifiutato, non supersedeare
    assert result.decision == AdmissionDecision.REJECT