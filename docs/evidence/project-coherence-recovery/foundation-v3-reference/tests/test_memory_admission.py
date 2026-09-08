"""Test base per Memory Admission.
Casi fondamentali: SAVE, IGNORE, validazione campi.
"""
import pytest
from memory.database import DatabaseManager
from memory.repository import MemoryRepository
from memory.schema import SchemaManager
from memory.models import Authority, MemoryCategory, MemoryRecord, ValidityStatus
from memory.admission import MemoryAdmission
from memory.admission_models import AdmissionCriteria, AdmissionDecision


@pytest.fixture
def db_manager(tmp_path):
    """DatabaseManager con schema v3 inizializzato."""
    db_path = tmp_path / "test_admission.db"
    dm = DatabaseManager(db_path)
    conn = dm.connect()
    SchemaManager.create_schema(conn)
    yield dm
    dm.close()


@pytest.fixture
def repo(db_manager):
    """MemoryRepository collegato al DB di test."""
    return MemoryRepository(db_manager.connect())


@pytest.fixture
def admission(repo):
    """MemoryAdmission con criteri di default."""
    return MemoryAdmission(repo)


def create_test_record(**overrides) -> MemoryRecord:
    """Helper per creare record di test."""
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
    }
    defaults.update(overrides)
    return MemoryRecord(**defaults)


def test_save_valid_record(admission, db_manager, repo):
    """Test: record valido viene salvato."""
    record = create_test_record()
    result = admission.evaluate(record)
    
    assert result.decision == AdmissionDecision.SAVE
    assert result.is_accepted()
    assert result.reasoning == "All checks passed"
    
    # Esecuzione
    with db_manager.transaction():
        memory_id = admission.execute(result)
    
    assert memory_id is not None
    saved = repo.get_by_id(memory_id)
    assert saved is not None
    assert saved.content == record.content


def test_ignore_low_salience(admission):
    """Test: record con salience troppo bassa viene ignorato."""
    record = create_test_record(salience=0.1)
    result = admission.evaluate(record)
    
    assert result.decision == AdmissionDecision.IGNORE
    assert result.is_ignored()
    assert "Salience" in result.reasoning


def test_reject_missing_owner(admission):
    """Test: record senza owner viene rifiutato."""
    record = create_test_record(owner="")
    result = admission.evaluate(record)
    
    assert result.decision == AdmissionDecision.REJECT
    assert result.is_rejected()
    assert "Missing required fields" in result.reasoning


def test_reject_missing_content(admission):
    """Test: record senza content viene rifiutato."""
    record = create_test_record(content="")
    result = admission.evaluate(record)
    
    assert result.decision == AdmissionDecision.REJECT
    assert result.is_rejected()


def test_ignore_duplicate(admission, db_manager, repo):
    """Test: duplicato viene ignorato."""
    # Salva primo record
    record1 = create_test_record(content="Unique content")
    with db_manager.transaction():
        repo.save(record1)
    
    # Valuta duplicato
    record2 = create_test_record(content="Unique content")
    result = admission.evaluate(record2)
    
    assert result.decision == AdmissionDecision.IGNORE
    assert "Duplicate" in result.reasoning


def test_custom_criteria(admission, repo):
    """Test: criteri personalizzati funzionano."""
    # Criteri più restrittivi
    strict_criteria = AdmissionCriteria(
        min_confidence=0.9,
        min_salience=0.7,
    )
    strict_admission = MemoryAdmission(repo, strict_criteria)
    
    # Record con confidence 0.8 dovrebbe essere rifiutato
    record = create_test_record(confidence=0.8)
    result = strict_admission.evaluate(record)
    
    assert result.decision == AdmissionDecision.REJECT
    assert "Confidence" in result.reasoning