"""Modelli per il modulo Memory Admission."""
from dataclasses import dataclass, field
from enum import Enum
from typing import Optional
from .models import Authority, MemoryRecord


class AdmissionDecision(Enum):
    SAVE = "save"
    SUPERSEDE = "supersede"
    REJECT = "reject"
    IGNORE = "ignore"


class RejectionReason(Enum):
    LOW_CONFIDENCE = "low_confidence"
    INVALID_AUTHORITY = "invalid_authority"
    DUPLICATE_CONTENT = "duplicate_content"
    CONTRADICTS_HIGHER_AUTHORITY = "contradicts_higher_authority"
    INVALID_CATEGORY = "invalid_category"
    MISSING_REQUIRED_FIELDS = "missing_required_fields"
    EXPIRED_WORLD_TIME = "expired_world_time"


@dataclass
class AdmissionCriteria:
    min_confidence: float = 0.6
    min_salience: float = 0.3
    max_emotional_weight: float = 0.95
    allowed_authorities: set[Authority] = field(default_factory=lambda: {
        Authority.WORLD_TRUTH,
        Authority.OBSERVATION,
        Authority.REPORT,
        Authority.BELIEF,
        Authority.INFERENCE,
    })
    duplicate_similarity_threshold: float = 0.95
    max_age_seconds: Optional[int] = None


@dataclass
class AdmissionResult:
    decision: AdmissionDecision
    record: Optional[MemoryRecord] = None
    superseded_id: Optional[int] = None
    rejection_reason: Optional[RejectionReason] = None
    confidence: float = 0.0
    reasoning: str = ""

    def is_accepted(self) -> bool:
        return self.decision in (AdmissionDecision.SAVE, AdmissionDecision.SUPERSEDE)

    def is_rejected(self) -> bool:
        return self.decision == AdmissionDecision.REJECT

    def is_ignored(self) -> bool:
        return self.decision == AdmissionDecision.IGNORE