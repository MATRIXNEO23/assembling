"""CRUD canonico dei ricordi."""
import json
import sqlite3
from dataclasses import replace
from datetime import datetime
from typing import Optional
from .models import Authority, MemoryCategory, MemoryRecord, ValidityStatus


class MemoryRepository:
    def __init__(self, conn: sqlite3.Connection):
        self.conn = conn

    def save(self, record: MemoryRecord) -> int:
        now = int(datetime.now().timestamp())
        cursor = self.conn.execute("""
            INSERT INTO memories (
                owner, memory_type, category, content, summary,
                actors, entities, world_time, real_time, location_id,
                authority, provenance, source_event_id, confidence,
                salience, emotional_weight, validity, revision_of,
                revision_count, contradicts_memory_id, links, goal_id,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            record.owner,
            record.memory_type,
            record.category.value,
            record.content,
            record.summary,
            json.dumps(record.actors),
            json.dumps(record.entities),
            record.world_time,
            record.real_time or now,
            record.location_id,
            record.authority.value,
            record.provenance,
            record.source_event_id,
            record.confidence,
            record.salience,
            record.emotional_weight,
            record.validity.value,
            record.revision_of,
            record.revision_count,
            record.contradicts_memory_id,
            json.dumps(record.links),
            record.goal_id,
            now,
            now,
        ))
        memory_id = cursor.lastrowid

        for actor in record.actors:
            self.conn.execute("INSERT OR IGNORE INTO memory_actors (memory_id, actor_id, role) VALUES (?, ?, ?)", (memory_id, actor, None))
        for entity in record.entities:
            self.conn.execute("INSERT OR IGNORE INTO memory_entities (memory_id, entity_id, entity_type) VALUES (?, ?, ?)", (memory_id, entity, None))
        return memory_id

    def get_by_id(self, memory_id: int) -> Optional[MemoryRecord]:
        row = self.conn.execute("SELECT * FROM memories WHERE id = ?", (memory_id,)).fetchone()
        if row is None:
            return None
        return self._row_to_record(row)

    def get_by_owner(self, owner: str, validity: ValidityStatus = ValidityStatus.VALID, limit: int = 100) -> list[MemoryRecord]:
        rows = self.conn.execute("""
            SELECT * FROM memories WHERE owner = ? AND validity = ?
            ORDER BY created_at DESC LIMIT ?
        """, (owner, validity.value, limit)).fetchall()
        return [self._row_to_record(row) for row in rows]

    def get_by_actor(self, actor_id: str, limit: int = 100) -> list[MemoryRecord]:
        rows = self.conn.execute("""
            SELECT m.* FROM memories m
            JOIN memory_actors ma ON m.id = ma.memory_id
            WHERE ma.actor_id = ? AND m.validity = 'valid'
            ORDER BY m.created_at DESC LIMIT ?
        """, (actor_id, limit)).fetchall()
        return [self._row_to_record(row) for row in rows]

    def update_metadata(self, memory_id: int, **kwargs) -> bool:
        allowed_fields = {"confidence", "salience", "emotional_weight", "links", "goal_id"}
        invalid_fields = set(kwargs.keys()) - allowed_fields
        if invalid_fields:
            raise ValueError(
                f"Cannot update fields {invalid_fields} directly. "
                f"Use supersede() for semantic changes. "
                f"Allowed fields: {allowed_fields}"
            )
        if not kwargs:
            return False

        set_clauses = []
        values = []
        for field, value in kwargs.items():
            set_clauses.append(f"{field} = ?")
            values.append(json.dumps(value) if field == "links" else value)
        set_clauses.append("updated_at = ?")
        values.append(int(datetime.now().timestamp()))
        values.append(memory_id)

        cursor = self.conn.execute(f"UPDATE memories SET {', '.join(set_clauses)} WHERE id = ?", values)
        return cursor.rowcount > 0

    def supersede(self, old_id: int, new_record: MemoryRecord) -> int:
        old_record = self.get_by_id(old_id)
        if old_record is None:
            raise ValueError(f"Cannot supersede non-existent memory_id: {old_id}")

        revision_of = old_record.revision_of if old_record.revision_of is not None else old_id
        revision_count = old_record.revision_count + 1

        record_with_lineage = replace(new_record, revision_of=revision_of, revision_count=revision_count)
        new_id = self.save(record_with_lineage)
        self.mark_superseded(old_id, new_id)
        return new_id

    def mark_superseded(self, old_id: int, new_id: int) -> bool:
        now = int(datetime.now().timestamp())
        cursor = self.conn.execute("""
            UPDATE memories SET validity = 'superseded', superseded_by = ?, updated_at = ? WHERE id = ?
        """, (new_id, now, old_id))
        return cursor.rowcount > 0

    def delete(self, memory_id: int) -> bool:
        cursor = self.conn.execute("DELETE FROM memories WHERE id = ?", (memory_id,))
        return cursor.rowcount > 0

    def increment_access_count(self, memory_id: int) -> None:
        now = int(datetime.now().timestamp())
        self.conn.execute("UPDATE memories SET access_count = access_count + 1, last_accessed = ? WHERE id = ?", (now, memory_id))

    def _row_to_record(self, row: sqlite3.Row) -> MemoryRecord:
        return MemoryRecord(
            id=row["id"],
            owner=row["owner"],
            memory_type=row["memory_type"],
            category=MemoryCategory(row["category"]),
            content=row["content"],
            summary=row["summary"],
            actors=json.loads(row["actors"]) if row["actors"] else [],
            entities=json.loads(row["entities"]) if row["entities"] else [],
            world_time=row["world_time"],
            real_time=row["real_time"],
            location_id=row["location_id"],
            authority=Authority(row["authority"]),
            provenance=row["provenance"],
            source_event_id=row["source_event_id"],
            confidence=row["confidence"],
            salience=row["salience"],
            emotional_weight=row["emotional_weight"],
            validity=ValidityStatus(row["validity"]),
            superseded_by=row["superseded_by"],
            revision_of=row["revision_of"],
            revision_count=row["revision_count"],
            contradicts_memory_id=row["contradicts_memory_id"],
            links=json.loads(row["links"]) if row["links"] else [],
            goal_id=row["goal_id"],
            created_at=row["created_at"],
            updated_at=row["updated_at"],
            access_count=row["access_count"],
            last_accessed=row["last_accessed"],
        )