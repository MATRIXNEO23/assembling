"""Connessione SQLite e gestione transazioni.
Reference implementation Python per test e validazione.
Production Android: tradurre lo stesso contratto in Room/Kotlin.
Questo modulo NON è il persistence layer production.
CONFINE DI SICUREZZA:
Questo modulo è infrastruttura. NON deve essere chiamato direttamente
da GGUF o NLU. Il flusso corretto è:
TypedClaim → Authority Resolver → Memory Admission → MemoryRepository
"""
import sqlite3
from contextlib import contextmanager
from pathlib import Path
from typing import Generator


class DatabaseManager:
    """Gestore della connessione SQLite."""

    def __init__(self, db_path: str | Path):
        """Inizializza il gestore database.
        Args:
            db_path: Percorso al file SQLite.
        """
        self.db_path = Path(db_path)
        self._connection: sqlite3.Connection | None = None

    def connect(self) -> sqlite3.Connection:
        """Apre la connessione al database.
        Returns:
            Connessione SQLite attiva.
        """
        if self._connection is None:
            self._connection = sqlite3.connect(
                str(self.db_path),
                check_same_thread=False,
            )
            self._connection.row_factory = sqlite3.Row
            self._connection.execute("PRAGMA journal_mode=WAL")
            self._connection.execute("PRAGMA foreign_keys=ON")
        return self._connection

    def close(self) -> None:
        """Chiude la connessione al database."""
        if self._connection is not None:
            self._connection.close()
            self._connection = None

    @contextmanager
    def transaction(self) -> Generator[sqlite3.Connection, None, None]:
        """Context manager per transazioni atomiche.
        Esempio:
            with db.transaction() as conn:
                conn.execute("INSERT ...")
                conn.execute("UPDATE ...")
            # Commit automatico se nessun errore, rollback altrimenti.
        """
        conn = self.connect()
        try:
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise

    def __enter__(self) -> sqlite3.Connection:
        """Supporto per 'with' statement."""
        return self.connect()

    def __exit__(self, exc_type, exc_val, exc_tb) -> None:
        """Chiusura automatica."""
        self.close()