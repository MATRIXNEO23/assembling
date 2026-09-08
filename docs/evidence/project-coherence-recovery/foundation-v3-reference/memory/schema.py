"""Creazione/migrazione schema e indici FTS5."""
import json
import sqlite3


class SchemaManager:
    SCHEMA_VERSION = 3

    @staticmethod
    def create_schema(conn: sqlite3.Connection) -> None:
        conn.execute("""
            CREATE TABLE IF NOT EXISTS memories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner TEXT NOT NULL,
                memory_type TEXT NOT NULL,
                category TEXT NOT NULL,
                content TEXT NOT NULL,
                summary TEXT NOT NULL,
                actors TEXT,
                entities TEXT,
                world_time INTEGER,
                real_time INTEGER NOT NULL,
                location_id TEXT,
                authority TEXT NOT NULL,
                provenance TEXT NOT NULL,
                source_event_id TEXT,
                confidence REAL NOT NULL CHECK(confidence >= 0.0 AND confidence <= 1.0),
                salience REAL NOT NULL DEFAULT 0.5 CHECK(salience >= 0.0 AND salience <= 1.0),
                emotional_weight REAL DEFAULT 0.0 CHECK(emotional_weight >= -1.0 AND emotional_weight <= 1.0),
                validity TEXT NOT NULL DEFAULT 'valid',
                superseded_by INTEGER REFERENCES memories(id),
                revision_of INTEGER REFERENCES memories(id),
                revision_count INTEGER DEFAULT 0,
                contradicts_memory_id INTEGER REFERENCES memories(id),
                links TEXT,
                goal_id TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                access_count INTEGER DEFAULT 0,
                last_accessed INTEGER
            )
        """)

        conn.execute("""
            CREATE TABLE IF NOT EXISTS memory_actors (
                memory_id INTEGER NOT NULL REFERENCES memories(id) ON DELETE CASCADE,
                actor_id TEXT NOT NULL,
                role TEXT,
                PRIMARY KEY (memory_id, actor_id)
            )
        """)
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memory_actors_actor ON memory_actors(actor_id)")

        conn.execute("""
            CREATE TABLE IF NOT EXISTS memory_entities (
                memory_id INTEGER NOT NULL REFERENCES memories(id) ON DELETE CASCADE,
                entity_id TEXT NOT NULL,
                entity_type TEXT,
                PRIMARY KEY (memory_id, entity_id)
            )
        """)
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memory_entities_entity ON memory_entities(entity_id)")

        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_owner_type_time ON memories(owner, category, world_time DESC)")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_validity ON memories(owner, validity)")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_goal ON memories(owner, goal_id) WHERE goal_id IS NOT NULL")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_revision_lineage ON memories(revision_of) WHERE revision_of IS NOT NULL")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_contradicts ON memories(contradicts_memory_id) WHERE contradicts_memory_id IS NOT NULL")

        conn.execute("CREATE VIRTUAL TABLE IF NOT EXISTS memories_fts USING fts5(content, summary, content='memories', content_rowid='id')")

        conn.execute("""
            CREATE TRIGGER IF NOT EXISTS memories_ai AFTER INSERT ON memories BEGIN
                INSERT INTO memories_fts(rowid, content, summary) VALUES (new.id, new.content, new.summary);
            END
        """)
        conn.execute("""
            CREATE TRIGGER IF NOT EXISTS memories_ad AFTER DELETE ON memories BEGIN
                INSERT INTO memories_fts(memories_fts, rowid, content, summary) VALUES('delete', old.id, old.content, old.summary);
            END
        """)
        conn.execute("""
            CREATE TRIGGER IF NOT EXISTS memories_au AFTER UPDATE ON memories BEGIN
                INSERT INTO memories_fts(memories_fts, rowid, content, summary) VALUES('delete', old.id, old.content, old.summary);
                INSERT INTO memories_fts(rowid, content, summary) VALUES (new.id, new.content, new.summary);
            END
        """)

        conn.execute("CREATE TABLE IF NOT EXISTS schema_metadata (key TEXT PRIMARY KEY, value TEXT NOT NULL)")
        conn.execute("INSERT OR REPLACE INTO schema_metadata (key, value) VALUES ('schema_version', ?)", (str(SchemaManager.SCHEMA_VERSION),))
        conn.commit()

    @staticmethod
    def get_schema_version(conn: sqlite3.Connection) -> int:
        try:
            row = conn.execute("SELECT value FROM schema_metadata WHERE key = 'schema_version'").fetchone()
            return int(row[0]) if row else 0
        except sqlite3.OperationalError:
            return 0

    @staticmethod
    def _column_exists(conn: sqlite3.Connection, table: str, column: str) -> bool:
        cursor = conn.execute(f"PRAGMA table_info({table})")
        columns = [row["name"] for row in cursor.fetchall()]
        return column in columns

    @staticmethod
    def migrate(conn: sqlite3.Connection) -> None:
        current_version = SchemaManager.get_schema_version(conn)
        if current_version == 0:
            SchemaManager.create_schema(conn)
            return
        if current_version == 1:
            SchemaManager._migrate_v1_to_v2(conn)
            current_version = 2
        if current_version == 2:
            SchemaManager._migrate_v2_to_v3(conn)

    @staticmethod
    def _migrate_v1_to_v2(conn: sqlite3.Connection) -> None:
        if not SchemaManager._column_exists(conn, "memories", "revision_of"):
            conn.execute("ALTER TABLE memories ADD COLUMN revision_of INTEGER REFERENCES memories(id)")
        if not SchemaManager._column_exists(conn, "memories", "revision_count"):
            conn.execute("ALTER TABLE memories ADD COLUMN revision_count INTEGER DEFAULT 0")

        conn.execute("""
            CREATE TABLE IF NOT EXISTS memory_actors (
                memory_id INTEGER NOT NULL REFERENCES memories(id) ON DELETE CASCADE,
                actor_id TEXT NOT NULL, role TEXT,
                PRIMARY KEY (memory_id, actor_id)
            )
        """)
        conn.execute("""
            CREATE TABLE IF NOT EXISTS memory_entities (
                memory_id INTEGER NOT NULL REFERENCES memories(id) ON DELETE CASCADE,
                entity_id TEXT NOT NULL, entity_type TEXT,
                PRIMARY KEY (memory_id, entity_id)
            )
        """)

        rows = conn.execute("SELECT id, actors, entities FROM memories").fetchall()
        for row in rows:
            memory_id = row["id"]
            actors = json.loads(row["actors"]) if row["actors"] else []
            entities = json.loads(row["entities"]) if row["entities"] else []
            for actor in actors:
                conn.execute("INSERT OR IGNORE INTO memory_actors (memory_id, actor_id, role) VALUES (?, ?, ?)", (memory_id, actor, None))
            for entity in entities:
                conn.execute("INSERT OR IGNORE INTO memory_entities (memory_id, entity_id, entity_type) VALUES (?, ?, ?)", (memory_id, entity, None))

        conn.execute("DROP TABLE IF EXISTS memories_fts")
        conn.execute("CREATE VIRTUAL TABLE memories_fts USING fts5(content, summary, content='memories', content_rowid='id')")
        conn.execute("INSERT INTO memories_fts(rowid, content, summary) SELECT id, content, summary FROM memories")

        conn.execute("DROP TRIGGER IF EXISTS memories_ai")
        conn.execute("""
            CREATE TRIGGER memories_ai AFTER INSERT ON memories BEGIN
                INSERT INTO memories_fts(rowid, content, summary) VALUES (new.id, new.content, new.summary);
            END
        """)
        conn.execute("DROP TRIGGER IF EXISTS memories_ad")
        conn.execute("""
            CREATE TRIGGER memories_ad AFTER DELETE ON memories BEGIN
                INSERT INTO memories_fts(memories_fts, rowid, content, summary) VALUES('delete', old.id, old.content, old.summary);
            END
        """)
        conn.execute("DROP TRIGGER IF EXISTS memories_au")
        conn.execute("""
            CREATE TRIGGER memories_au AFTER UPDATE ON memories BEGIN
                INSERT INTO memories_fts(memories_fts, rowid, content, summary) VALUES('delete', old.id, old.content, old.summary);
                INSERT INTO memories_fts(rowid, content, summary) VALUES (new.id, new.content, new.summary);
            END
        """)

        conn.execute("CREATE INDEX IF NOT EXISTS idx_memory_actors_actor ON memory_actors(actor_id)")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memory_entities_entity ON memory_entities(entity_id)")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_revision_lineage ON memories(revision_of) WHERE revision_of IS NOT NULL")

        conn.execute("INSERT OR REPLACE INTO schema_metadata (key, value) VALUES ('schema_version', '2')")
        conn.commit()

    @staticmethod
    def _migrate_v2_to_v3(conn: sqlite3.Connection) -> None:
        if not SchemaManager._column_exists(conn, "memories", "contradicts_memory_id"):
            conn.execute("ALTER TABLE memories ADD COLUMN contradicts_memory_id INTEGER REFERENCES memories(id)")
        conn.execute("CREATE INDEX IF NOT EXISTS idx_memories_contradicts ON memories(contradicts_memory_id) WHERE contradicts_memory_id IS NOT NULL")
        conn.execute("INSERT OR REPLACE INTO schema_metadata (key, value) VALUES ('schema_version', '3')")
        conn.commit()