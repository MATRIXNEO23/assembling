"""Modulo Memory Admission."""
import time
from typing import Optional
from .models import Authority, MemoryRecord, ValidityStatus
from .repository import MemoryRepository
from .admission_models import (
    AdmissionCriteria,
    AdmissionDecision,
    AdmissionResult,
    RejectionReason,
)


class MemoryAdmission:
    AUTHORITY_RANK = {
        Authority.WORLD_TRUTH: 5,
        Authority.OBSERVATION: 4,
        Authority.REPORT: 3,
        Authority.INFERENCE: 2,
        Authority.BELIEF: 1,
    }

    def __init__(self, repository: MemoryRepository, criteria: Optional[AdmissionCriteria] = None):
        self.repository = repository
        self.criteria = criteria or AdmissionCriteria()

    def evaluate(self, record: MemoryRecord) -> AdmissionResult:
        if not self._validate_required_fields(record):
            return AdmissionResult(
                decision=AdmissionDecision.REJECT,
                rejection_reason=RejectionReason.MISSING_REQUIRED_FIELDS,
                reasoning="Missing required fields: owner, content, or category",
            )

        if record.authority not in self.criteria.allowed_authorities:
            return AdmissionResult(
                decision=AdmissionDecision.REJECT,
                rejection_reason=RejectionReason.INVALID_AUTHORITY,
                reasoning=f"Authority {record.authority.value} not allowed",
            )

        if record.confidence < self.criteria.min_confidence:
            return AdmissionResult(
                decision=AdmissionDecision.REJECT,
                rejection_reason=RejectionReason.LOW_CONFIDENCE,
                confidence=record.confidence,
                reasoning=f"Confidence {record.confidence} below threshold {self.criteria.min_confidence}",
            )

        if record.salience < self.criteria.min_salience:
            return AdmissionResult(
                decision=AdmissionDecision.IGNORE,
                reasoning=f"Salience {record.salience} below threshold {self.criteria.min_salience}",
            )

        if abs(record.emotional_weight) > self.criteria.max_emotional_weight:
            return AdmissionResult(
                decision=AdmissionDecision.REJECT,
                rejection_reason=RejectionReason.INVALID_CATEGORY,
                reasoning=f"Emotional weight {record.emotional_weight} exceeds threshold {self.criteria.max_emotional_weight}",
            )

        duplicate = self._find_duplicate(record)
        if duplicate is not None:
            return AdmissionResult(
                decision=AdmissionDecision.IGNORE,
                reasoning=f"Duplicate of memory_id {duplicate.id}",
            )

        conflict = self._find_conflict(record)
        if conflict is not None:
            if self._should_supersede(record, conflict):
                return AdmissionResult(
                    decision=AdmissionDecision.SUPERSEDE,
                    record=record,
                    superseded_id=conflict.id,
                    reasoning=f"Supersedes memory_id {conflict.id} with higher authority/confidence",
                )
            else:
                return AdmissionResult(
                    decision=AdmissionDecision.REJECT,
                    rejection_reason=RejectionReason.CONTRADICTS_HIGHER_AUTHORITY,
                    reasoning=f"Contradicts memory_id {conflict.id} with higher authority/confidence",
                )

        if self.criteria.max_age_seconds is not None and not self._is_within_age_limit(record):
            return AdmissionResult(
                decision=AdmissionDecision.REJECT,
                rejection_reason=RejectionReason.EXPIRED_WORLD_TIME,
                reasoning=f"World time {record.world_time} exceeds max age {self.criteria.max_age_seconds}s",
            )

        return AdmissionResult(
            decision=AdmissionDecision.SAVE,
            record=record,
            confidence=record.confidence,
            reasoning="All checks passed",
        )

    def execute(self, result: AdmissionResult) -> Optional[int]:
        if result.decision == AdmissionDecision.SAVE:
            if result.record is None:
                raise ValueError("SAVE decision requires record")
            return self.repository.save(result.record)
        elif result.decision == AdmissionDecision.SUPERSEDE:
            if result.record is None or result.superseded_id is None:
                raise ValueError("SUPERSEDE decision requires record and superseded_id")
            return self.repository.supersede(result.superseded_id, result.record)
        elif result.decision in (AdmissionDecision.REJECT, AdmissionDecision.IGNORE):
            return None
        else:
            raise ValueError(f"Unknown decision: {result.decision}")

    def _validate_required_fields(self, record: MemoryRecord) -> bool:
        return bool(record.owner and record.content and record.category)

    def _find_duplicate(self, record: MemoryRecord) -> Optional[MemoryRecord]:
        candidates = self.repository.get_by_owner(record.owner, validity=ValidityStatus.VALID, limit=20)
        for candidate in candidates:
            if self._calculate_content_similarity(record.content, candidate.content) >= self.criteria.duplicate_similarity_threshold:
                return candidate
        return None

    def _calculate_content_similarity(self, text1: str, text2: str) -> float:
        if text1 == text2:
            return 1.0
        words1 = set(text1.lower().split())
        words2 = set(text2.lower().split())
        if not words1 or not words2:
            return 0.0
        return len(words1 & words2) / len(words1 | words2)

    def _find_conflict(self, record: MemoryRecord) -> Optional[MemoryRecord]:
        if record.contradicts_memory_id is None:
            return None
        contradicted = self.repository.get_by_id(record.contradicts_memory_id)
        if contradicted is None:
            return None
        if contradicted.validity != ValidityStatus.VALID:
            return None
        return contradicted

    def _should_supersede(self, new_record: MemoryRecord, existing: MemoryRecord) -> bool:
        new_rank = self.AUTHORITY_RANK.get(new_record.authority, 0)
        existing_rank = self.AUTHORITY_RANK.get(existing.authority, 0)
        if new_rank > existing_rank:
            return True
        if new_rank == existing_rank and new_record.confidence > existing.confidence:
            return True
        if new_rank == existing_rank and new_record.confidence == existing.confidence and new_record.real_time > existing.real_time:
            return True
        return False

    def _is_within_age_limit(self, record: MemoryRecord) -> bool:
        if self.criteria.max_age_seconds is None or record.world_time is None:
            return True
        return (int(time.time()) - record.world_time) <= self.criteria.max_age_seconds