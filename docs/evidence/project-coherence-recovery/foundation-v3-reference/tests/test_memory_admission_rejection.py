"""Test per casi di rifiuto in Memory Admission."""
import pytest
import time
from memory.database import DatabaseManager
from memory.repository import MemoryRepository
from memory.schema import SchemaManager
from memory.models import Authority, MemoryCategory, MemoryRecord
from memory.admission import MemoryAdmission
from memory.admission_models import AdmissionCriteria, AdmissionDecision, RejectionReason


@pytest.fixture
def db_manager(tmp_path):
    db_path = tmp_path / "test_rejection.db"
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


def test_reject_invalid_authority(admission):
    criteria = AdmissionCriteria(allowed_authorities={Authority.OBSERVATION, Authority.REPORT})
    restricted = MemoryAdmission(admission.repository, criteria)
    result = restricted.evaluate(create_test_record(authority=Authority.BELIEF))
    assert result.decision == AdmissionDecision.REJECT
    assert result.rejection_reason == RejectionReason.INVALID_AUTHORITY


def test_reject_low_confidence(admission):
    result = admission.evaluate(create_test_record(confidence=0.3))
    assert result.decision == AdmissionDecision.REJECT
    assert result.rejection_reason == RejectionReason.LOW_CONFIDENCE


def test_reject_high_emotional_weight(admission):
    result = admission.evaluate(create_test_record(emotional_weight=0.98))
    assert result.decision == AdmissionDecision.REJECT
    assert "exceeds threshold" in result.reasoning


def test_reject_expired_world_time(admission):
    limited = MemoryAdmission(admission.repository, AdmissionCriteria(max_age_seconds=3600))
    result = limited.evaluate(create_test_record(world_time=int(time.time()) - 7200))
    assert result.decision == AdmissionDecision.REJECT
    assert result.rejection_reason == RejectionReason.EXPIRED_WORLD_TIME


def test_reject_contradicts_higher_authority(admission, db_manager, repo):
    with db_manager.transaction():
        old_id = repo.save(create_test_record(
            content="Earth is round", actors=["Scientist"],
            authority=Authority.WORLD_TRUTH, confidence=0.95,
        ))
    result = admission.evaluate(create_test_record(
        content="Earth is flat", actors=["Scientist"],
        authority=Authority.BELIEF, confidence=0.7,
        contradicts_memory_id=old_id,
    ))
    assert result.decision == AdmissionDecision.REJECT
    assert result.rejection_reason == RejectionReason.CONTRADICTS_HIGHER_AUTHORITY


def test_no_false_conflicts(admission, db_manager, repo):
    with db_manager.transaction():
        repo.save(create_test_record(content="Albert lives in Venice", actors=["Albert"], confidence=0.9))
    result = admission.evaluate(create_test_record(
        content="Albert loves coffee", actors=["Albert"], confidence=0.9,
    ))
    assert result.decision == AdmissionDecision.SAVE