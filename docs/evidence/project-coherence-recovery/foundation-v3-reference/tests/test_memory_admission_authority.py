"""Test per validazione authority in Memory Admission.
Verifica che le gerarchie di authority siano rispettate.
"""
import pytest
from memory.database import DatabaseManager
from memory.repository import MemoryRepository
from memory.schema import SchemaManager
from memory.models import Authority, MemoryCategory, MemoryRecord
from memory.admission import MemoryAdmission
from memory.admission_models import AdmissionCriteria, AdmissionDecision


@pytest.fixture
def db_manager(tmp_path):
    db_path = tmp_path / "test_authority.db"
    dm = DatabaseManager(db_path)
    conn = dm.connect()
    SchemaManager.create_schema(conn)
    yield dm
    dm.close()


@pytest.fixture
def repo(db_manager):
    return MemoryRepository(db_manager.connect())


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
    }
    defaults.update(overrides)
    return MemoryRecord(**defaults)


def test_all_authorities_accepted_by_default(repo):
    """Test: tutte le authority standard sono accettate con criteri default."""
    admission = MemoryAdmission(repo)
    
    for authority in [
        Authority.WORLD_TRUTH,
        Authority.OBSERVATION,
        Authority.REPORT,
        Authority.BELIEF,
        Authority.INFERENCE,
    ]:
        record = create_test_record(authority=authority)
        result = admission.evaluate(record)
        assert result.decision == AdmissionDecision.SAVE, f"{authority} should be accepted"


def test_restrict_authorities(repo):
    """Test: criteri restrittivi limitano le authority accettate."""
    criteria = AdmissionCriteria(
        allowed_authorities={Authority.WORLD_TRUTH, Authority.OBSERVATION}
    )
    admission = MemoryAdmission(repo, criteria)
    
    # Authority permesse
    for authority in [Authority.WORLD_TRUTH, Authority.OBSERVATION]:
        record = create_test_record(authority=authority)
        result = admission.evaluate(record)
        assert result.decision == AdmissionDecision.SAVE
    
    # Authority non permesse
    for authority in [Authority.REPORT, Authority.BELIEF, Authority.INFERENCE]:
        record = create_test_record(authority=authority)
        result = admission.evaluate(record)
        assert result.decision == AdmissionDecision.REJECT


def test_authority_hierarchy_in_conflict(repo, db_manager):
    """Test: gerarchia authority determina chi vince in caso di conflitto."""
    admission = MemoryAdmission(repo)
    
    # Salva record con authority REPORT (rank 3)
    report_record = create_test_record(
        content="Report says X",
        actors=["Source"],
        authority=Authority.REPORT,
        confidence=0.85,
    )
    with db_manager.transaction():
        old_id = repo.save(report_record)
    
    # OBSERVATION (rank 4) dovrebbe supersedeare
    obs_record = create_test_record(
        content="Observation says Y",
        actors=["Source"],
        authority=Authority.OBSERVATION,
        confidence=0.85,
        contradicts_memory_id=old_id,
    )
    result = admission.evaluate(obs_record)
    assert result.decision == AdmissionDecision.SUPERSEDE
    
    # BELIEF (rank 1) dovrebbe essere rifiutato
    belief_record = create_test_record(
        content="Belief says Z",
        actors=["Source"],
        authority=Authority.BELIEF,
        confidence=0.95,
        contradicts_memory_id=old_id,
    )
    result = admission.evaluate(belief_record)
    assert result.decision == AdmissionDecision.REJECT


def test_world_truth_always_wins(repo, db_manager):
    """Test: WORLD_TRUTH supersedea sempre."""
    admission = MemoryAdmission(repo)
    
    # Salva record con OBSERVATION
    obs_record = create_test_record(
        content="Observation",
        actors=["Witness"],
        authority=Authority.OBSERVATION,
        confidence=0.99,
    )
    with db_manager.transaction():
        old_id = repo.save(obs_record)
    
    # WORLD_TRUTH dovrebbe supersedeare anche con confidence inferiore
    truth_record = create_test_record(
        content="Absolute truth",
        actors=["Witness"],
        authority=Authority.WORLD_TRUTH,
        confidence=0.90,
        contradicts_memory_id=old_id,
    )
    result = admission.evaluate(truth_record)
    assert result.decision == AdmissionDecision.SUPERSEDE