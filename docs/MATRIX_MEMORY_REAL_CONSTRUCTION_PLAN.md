# Matrix Memory — Piano Reale di Costruzione

Status: **CANONICAL CONSTRUCTION PLAN / NOT YET IMPLEMENTED**  
Repository: `MATRIXNEO23/assembling`  
Owner: Alberto  
Supervisor: Supervisor GPT  
Date: 2026-09-07

## 0. Scopo

Questo documento preserva il piano reale confermato per costruire la Memory di Matrix Engine senza doverla reinventare a ogni ripresa.

Non sostituisce MIP, il design completo dell'Engine o la Memory Integration Policy: li rende eseguibili come piano di costruzione Memory.

Fonti canoniche già approvate da cui questo piano è consolidato:

- `docs/MATRIX_ENGINE_COMPLETE_REAL_DESIGN_2026-09-07.md`
- `docs/MATRIX_INTERMODULE_PROTOCOL.md`
- `docs/MEMORY_INTEGRATION_POLICY.md`
- `docs/MEMORY_INTEGRATION_STATUS.md`
- Memory Foundation Python storica con rollback/lineage/supersede
- classificazione memoria dell'architettura approvata: `WORKING / EPISODIC / SEMANTIC / REFLECTION` e tipi contenuto `fatto / preferenza / promessa / evento / correzione / giudizio`

Regola: questo piano è versionato e si estende solo con decisione esplicita; non viene riscritto a intuito.

---

# 1. Obiettivo reale della Memory

La Memory deve permettere a Matrix di:

```text
ricevere informazione strutturata
→ capire se è degna di memoria
→ distinguere fatto/evento/preferenza/promessa/correzione/giudizio
→ sapere di chi parla, chi la possiede e da quale prospettiva
→ collocarla nel tempo
→ conservare provenienza e affidabilità
→ decidere SAVE / SUPERSEDE / REJECT / IGNORE
→ persistere atomicamente
→ preservare storia e lineage
→ recuperare il ricordo giusto in turni successivi e dopo riavvio
→ distinguere current/history
→ non contaminare persone, NPC o domini diversi
```

La Memory non è il cervello intero e non decide da sola verità, emozioni, relazione, consenso o comportamento.

---

# 2. Separazioni obbligatorie

```text
TypedClaim != Belief
Belief != Memory
Memory != State
State != Context
Relationship != Affective
SexualInterest != CurrentDesire
CurrentDesire != Consent
Contradiction != Supersession
Correction != Supersession
Temporal change != Contradiction by default
InterpretationConfidence != SourceReliability
SourceReliability != Authority
Authority != BeliefConfidence
BeliefConfidence != RetrievalRelevance
```

Flusso semantico canonico:

```text
TypedClaim
→ Authority Resolver
→ Memory Preflight / MemoryCandidate
→ VALIDATE accepted output/action
→ Persistent Consolidation
→ Memory Admission
→ MemoryRepository
```

Authority identifica semanticamente una contraddizione e il suo target. Memory Admission non inventa conflitti dalla differenza testuale, dallo stesso actor o da similarità lessicale.

GGUF e NLU non scrivono mai direttamente nella persistenza.

---

# 3. Due tassonomie diverse: livello memoria e tipo/contenuto

## 3.1 Livello / kind del ricordo

La classificazione approvata distingue:

```text
WORKING
EPISODIC
SEMANTIC
REFLECTION
```

Significato:

- `WORKING`: operativo breve/turn-bounded; non è Long-Term Memory persistente canonica.
- `EPISODIC`: evento/esperienza collocata in tempo e contesto.
- `SEMANTIC`: fatto o conoscenza consolidata.
- `REFLECTION`: deduzione derivata da evidenze; deve conservare source evidence e passare Authority/Admission.

Nel contratto persistente MIP i memory kind canonici Long-Term sono `EPISODIC / SEMANTIC / REFLECTION`; `WORKING` resta stato operativo temporaneo.

## 3.2 Tipi di contenuto recuperati dall'architettura approvata

La classificazione contenuto verificata è:

```text
FATTO
PREFERENZA
PROMESSA / IMPEGNO
EVENTO
CORREZIONE
GIUDIZIO
```

Questi tipi determinano confronto, recupero, evoluzione e regole di ammissione. Non sostituiscono `memoryKind`.

Uno stesso evento può generare record distinti collegati dagli stessi `sourceIds`.

Esempio:

```text
Evento sorgente: Marco non si presenta all'appuntamento.

EPISODIC / EVENTO:
- Marco non è arrivato alle 20:00.

EPISODIC o SEMANTIC / esperienza-affective evidence:
- Luna ha provato delusione in quell'evento.

REFLECTION / GIUDIZIO:
- Luna valuta Marco meno affidabile, con confidence e prove.
```

Non si fondono in una singola frase ambigua.

## 3.3 Domini separati che possono avere storia in Memory

I seguenti sono owner di stato separati e **non sono memory kind**:

```text
RELATIONSHIP
AFFECTIVE
INTIMACY / CONSENT
GOAL / INTENTION
BELIEF
WORLD
```

La Memory può però conservare evidenze storiche riguardanti questi domini.

Esempi:

- storico relazione: eventi di vicinanza, fiducia, rottura, riconciliazione;
- storico affettivo: reazione/emozione causata da un evento, con causa e tempo;
- storico intimo: eventi, confini, rifiuto, consenso o withdrawal avvenuti nel passato;
- promessa/impegno: cosa è stato promesso, da chi, a chi, condizioni e scadenza;
- obiettivo storico: desiderio/goal dichiarato o perseguito in un certo periodo;
- correzione: evidenza che una precedente informazione era errata o superata.

Hard rule Intimacy:

```text
historical intimacy/consent memory
!= current desire
!= current boundary
!= current consent
```

Il consenso corrente appartiene a `INTIMACY` ed è action/context/time scoped e revocabile. Un ricordo di consenso passato non autorizza il presente.

---

# 4. MemoryRecord finale

Ogni record persistente deve essere strutturato, non una frase libera isolata.

Superficie minima:

```text
IDENTITY
- memoryId
- schemaVersion
- memoryKind
- contentType
- semanticDomain
- predicateId

SEMANTIC ROLES
- subjectRef
- targetRef
- ownerRef
- sourceRef
- perspectiveRef
- observerRef quando rilevante
- entityRefs[] / actorRefs[]

VALUE
- typedValue
- valueType
- canonicalSummary / rawEvidenceText quando utile
- polarity
- modality

TIME
- eventTime
- validFrom
- validTo
- observedAt
- recordedAt
- temporalReferenceId
- granularity

EPISTEMIC
- epistemicClass / authority
- sourceReliability
- interpretationConfidence provenance link
- admissionConfidence / belief evidence se pertinente

PROVENANCE
- eventId / observationId / claimId
- AuthorityResolutionId
- derivedFromIds[]
- quotedFromId quando presente

LIFECYCLE / LINEAGE
- lifecycleState / validity
- revisionOf
- supersededBy
- revisionCount
- contradictsMemoryId esplicito

RETRIEVAL / RETENTION
- salience / importance
- retentionTier
- reinforcement/evidenceCount
- accessCount
- lastAccessedAt
- optional embeddingVersion
- normalized indexed roles/predicate/time

PRIVACY / ACCESS
- agent namespace
- contact/entity scope
- privacy/access policy quando necessaria
```

Un record unico è la fonte; cataloghi/indici conservano riferimenti, non copie divergenti.

---

# 5. Memory Acquisition / Preflight

Responsabilità:

```text
canonical TypedClaim + AuthorityResolution + retrieval evidence
→ MemoryCandidate effimero
```

Deve valutare:

- memory-worthy o rumore;
- `memoryKind`;
- `contentType`;
- semanticDomain/predicate/value;
- ruoli/entity identity;
- tempo/validità;
- provenance;
- salience/retention proposte;
- contradiction target già risolto da Authority;
- confidence e reason codes.

Non deve:

- fare regex sul testo grezzo;
- reinventare subject/source/owner/perspective;
- scrivere DB;
- promuovere claim a truth.

`MemoryCandidate != MemoryRecord`.

---

# 6. Authority + Memory Admission

Authority risolve prima:

```text
WORLD_TRUTH / OBSERVATION / REPORT / INFERENCE / BELIEF
source/perspective/owner
source reliability
semantic contradiction identity
ambiguity/conflict status
```

Dopo VALIDATE, Memory Admission decide l'operazione persistente canonica:

```text
SAVE
SUPERSEDE
REJECT
IGNORE
```

Reinforcement è un aggiornamento metadata/evidence, non un overwrite semantico.

Regole:

- correzione != supersede automatico;
- temporal change != contradiction by default;
- ambiguità/unresolved fail closed;
- duplicate/idempotent event non crea duplicati;
- semantic change passa da `supersede()`;
- vecchia memoria resta nella storia con lineage;
- non si cancella fisicamente una memoria referenziata rompendo lineage.

Le vecchie etichette concettuali `NEW/UPDATE/REINFORCE/CONFLICT/SUPERSEDE` restano utili come lifecycle/diagnostic classification, ma la durable decision API canonica è `SAVE/SUPERSEDE/REJECT/IGNORE` più metadata reinforcement.

---

# 7. MemoryRepository reale

Target Android:

```text
Room + SQLite
```

Obblighi:

- transazioni atomiche;
- rollback reale su errore/cancellazione;
- FK/lineage protetto;
- SAVE/SUPERSEDE/metadata update;
- duplicate/idempotency guard;
- current/history query;
- restart/reopen persistence;
- migrazioni schema versionate;
- structured indexes;
- SQLite FTS5/BM25 per retrieval testuale;
- nessun `replaceAll` globale come strategia canonica.

Semantiche/test da portare dalla Python Memory Foundation:

- atomic rollback fault-injection;
- lineage root e chain;
- semantic update protection;
- protected delete;
- restart integrity;
- `contradicts_memory_id` esplicito.

---

# 8. Retrieval reale

Ogni turno normale esegue:

```text
LEVEL 1 — INDEX_PROBE
```

leggero e strutturato. Non significa caricare tutta la memoria.

Hard filters, dove applicabili:

- agent/contact namespace e privacy;
- subject/entity;
- predicate/domain/contentType;
- target/source/perspective;
- current vs history;
- temporal range;
- location/event/goal refs.

Stati distinti:

```text
MATCHED
NO_MATCH
AMBIGUOUS
INDEX_UNAVAILABLE
ERROR
```

`NO_MATCH != UNAVAILABLE`.

Solo se ci sono candidati:

```text
LEVEL 2 — HYDRATE_AND_RERANK
```

Ranking:

- structured exact matches prima;
- SQLite FTS5/BM25;
- eventuale embedding solo se benchmark mobile lo giustifica;
- recency in funzione dello scopo;
- salience/importance;
- source/record confidence;
- access/reinforcement signals;
- benchmark weighted fusion vs RRF;
- MMR opzionale per ridondanza.

Solo per richieste complesse/reflection:

```text
LEVEL 3 — DEEP_OR_MULTI_HOP
```

bounded, seguendo entity/lineage/source links; non ogni turno.

---

# 9. Persistent Consolidation

Prima della risposta:

```text
stableWrite = false
memoryIds = []
```

Dopo:

```text
Decision / response draft
→ OutputValidator
→ ACCEPT
→ PersistentConsolidation
→ owner-specific durable proposals
→ Memory Admission
→ MemoryRepository transaction
→ committed result + IDs
```

Se OutputValidator rifiuta, nessuna durable mutation causata dal draft rifiutato.

PersistentConsolidation coordina; non diventa owner della Memory o degli altri stati.

---

# 10. Reflection e Memory

Reflection usa eventi/memorie vere e produce una derivazione tracciata:

```text
retrieved evidence
→ Reflection
→ ReflectionCandidate
→ Authority
→ Memory Admission
→ eventuale REFLECTION MemoryRecord
```

Reflection:

- source evidence IDs obbligatori;
- confidence;
- temporal scope;
- reason codes;
- possibilità di abstain;
- mai write diretto.

Livelli futuri:

```text
MICRO
DECISION
MACRO
```

Reflection viene implementata dopo una Memory verificata, non prima.

---

# 11. Checkpoint costruttivi Memory

## M1 — Schema / contratti finali

Chiudere:

- MemoryRecord;
- MemoryCandidate;
- contentType + memoryKind + semanticDomain;
- query/result types;
- lifecycle/lineage;
- privacy/access;
- reason codes;
- MIP adapters.

Gate: schema completo, ownership chiara, fixture strutturali e incompatibilità fail-closed.

## M2 — Repository / Admission persistenti

Implementare Room/SQLite + Admission:

- SAVE;
- SUPERSEDE;
- REJECT;
- IGNORE;
- reinforcement metadata;
- atomic rollback;
- lineage;
- idempotency;
- restart.

Gate: test reali su DB, non mock-only.

## M3 — Retrieval

Implementare:

- INDEX_PROBE ogni turno;
- hydrate/rerank;
- current/history;
- structured filter;
- FTS/BM25;
- opzionali RRF/MMR/embedding solo dopo benchmark.

Gate: ricordo corretto vince; contaminazione cross-persona = 0 nei test deterministici; superseded non viene trattato come current.

## M4 — Persistent Consolidation

Integrare:

```text
accepted response/action
→ VALIDATE
→ consolidate
→ Admission
→ Repository
```

Gate: nessuna durable write pre-validation; rejected output non commit.

## M5 — E2E reale / freeze

Batteria minima:

```text
"Vivo a Milano"
→ salva il fatto corretto
→ nuovo turno/restart
→ "Dove vivo?"
→ recupera Milano

"Prima vivevo a Venezia, ora vivo a Milano"
→ storia temporale corretta
→ nessun falso conflitto

"Marco dice che Anna vive a Roma"
→ REPORT
→ non WORLD_TRUTH

"Mi ero sbagliato, vivo a Torino"
→ target corretto
→ SUPERSEDE corretto
→ lineage preservato
→ Torino current
→ precedente storico

forced repository failure
→ rollback atomico

restart/reopen
→ ricordi e lineage invariati
```

Ogni scenario usa il metodo canonico:

```text
REAL INPUT
→ real module
→ actual output
→ expected-vs-actual field diff
→ MIP handoff
→ next module output
→ first divergence
```

Solo dopo M1-M5 la Memory può essere chiamata dimostrata per lo scope testato.

---

# 12. Ordine rispetto al resto del progetto

Il piano Memory è confermato e preservato, ma **non autorizza a saltare il lavoro corrente**.

Ordine attuale:

```text
Student-5 / NLU V3 lock + supervisor acceptance
→ real NLU → Understanding → Authority proof
→ Belief/Authority finalization + Memory M1-M3
→ convergence
→ Persistent Consolidation
→ Memory M5 E2E
→ altri state owners / decision / realization
→ Android/device
```

Nuove idee Memory vengono valutate e inserite nel punto di dipendenza corretto; non sostituiscono automaticamente questo piano.

---

# 13. Regola di preservazione

Questo documento rappresenta un **piano costruttivo reale confermato**.

Regola vincolante:

```text
OWNER + SUPERVISOR CONFERMANO "SI FA COSI"
→ SALVA SUBITO IL PIANO NELLA REPOSITORY / CONTINUITY
→ NON CHIEDERE AL PROPRIETARIO SE VA SALVATO
→ NON AFFIDARSI ALLA MEMORIA DELLA CHAT
→ LE MODIFICHE FUTURE SONO VERSIONATE E MOTIVATE
```

Non dichiarare mai che un piano è stato salvato finché commit e readback non sono stati realmente verificati.
