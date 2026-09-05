# Matrix Engine — Metodo Canonico di Sviluppo e Integrazione

Status: CANONICAL WORK METHOD  
Scope: tutti i moduli Matrix Engine  
Repository owner: `MATRIXNEO23`  

## 1. Regola fondamentale

```text
NON PASSARE AL MODULO SUCCESSIVO
FINCHÉ LA SUITE PREVISTA PER IL CHECKPOINT CORRENTE
NON È VERDE AL 100%
```

Un modulo non è considerato pronto perché "sembra funzionare" o perché passa test isolati. È pronto solo quando contratto, adapter, test atomici, test cross-modulo, diagnostica, regression suite e continuity risultano coerenti e verdi.

Seconda regola fondamentale:

```text
UN TEST VERDE NON BASTA
SE NON VERIFICA LA PROPRIETÀ GIUSTA
```

I test devono verificare invarianti semantiche e architetturali, non soltanto l'assenza di eccezioni.

## 2. Contratti espliciti e immutabili

Ogni modulo deve avere un contratto esplicito prima dell'integrazione.

Il contratto deve definire almeno:

- input;
- output;
- ownership dello stato;
- identità e provenance;
- vocabolari/enumerazioni;
- semantica di `UNKNOWN`, `UNRESOLVED`, `AMBIGUOUS`, `UNAVAILABLE`, `NO_MATCH`, `ERROR`;
- cardinalità singola/plurale;
- invarianti temporali;
- regole di compatibilità;
- cosa il modulo NON può fare.

MIP è la singola autorità semantica cross-modulo.

Un modulo downstream non deve rileggere testo libero per reinventare informazioni già risolte upstream.

Esempio:

```text
NLU risolve source/subject/target/owner/perspective
→ Understanding preserva e struttura
→ Authority usa la struttura
→ Memory non reinterpreta il testo
```

Un contratto congelato non si modifica durante un checkpoint di implementazione per far passare i test. Se il contratto risulta realmente insufficiente:

```text
STOP
→ CONTRACT CHANGE CHECKPOINT
→ nuova revisione esplicita
→ nuovi test
→ nuovo freeze
```

## 3. Ownership: un solo proprietario per stato canonico

Ogni stato mutabile canonico ha un solo owner.

Esempi:

```text
WORLD              → world truth/state
NLU/UNDERSTANDING  → interpretazione linguistica
BELIEF_AUTHORITY   → risoluzione epistemica/Authority
MEMORY             → Long-Term Memory
AFFECTIVE          → AffectiveState
RELATIONSHIP       → RelationshipState
INTIMACY           → consent/boundaries correnti
GOAL               → goal/intention state
DECISION           → scelta comportamentale
GGUF               → realizzazione linguistica
```

Nessun adapter o modulo ausiliario può creare un secondo owner concorrente.

## 4. Adapter layer obbligatorio ai confini

Quando due moduli usano formati diversi, la conversione deve passare da un adapter esplicito.

Un adapter deve:

- mappare campo per campo;
- validare input/output;
- preservare identità;
- preservare provenance;
- preservare stati di incertezza;
- fallire chiuso quando la destinazione non può rappresentare il dato;
- non contenere business logic del modulo;
- non reinterpretare linguaggio naturale;
- non scrivere stato che non possiede.

Regola:

```text
LOSSLESS MAPPING POSSIBILE
→ CONVERTI

LOSSLESS MAPPING IMPOSSIBILE
→ FAIL CLOSED / QUARANTENA LEGACY
```

Mai convertire approssimativamente soltanto per mantenere compatibilità.

## 5. Test atomici

Ogni componente deve avere test unitari sulle proprie invarianti.

Devono includere almeno:

- casi validi;
- casi invalidi;
- boundary values;
- stati mancanti;
- ambiguità;
- errori provider;
- cardinalità;
- identità/provenance;
- casi che in passato hanno causato bug.

Ogni bug corretto deve produrre un regression test specifico.

## 6. Test di integrazione cross-modulo

I test isolati non sono sufficienti.

Ogni confine importante deve essere testato come:

```text
PRODUCER
→ CONTRACT
→ ADAPTER
→ CONSUMER
```

Devono essere verificati almeno:

- round-trip quando semanticamente possibile;
- perdita di campi = zero;
- stati explicit unknown/unavailable preservati;
- multi-claim preservato;
- identità e provenance preservate;
- downstream non reinterpreta upstream;
- nessun owner concorrente;
- nessuna scrittura prematura.

## 7. Test end-to-end

Quando una catena è integrabile, serve un test E2E reale.

Esempio Memory:

```text
INPUT
→ NLU
→ Understanding
→ Context/Retrieval
→ Authority
→ Memory Admission
→ MemoryRepository
→ nuovo turno
→ Retrieval
→ ricordo corretto recuperato
```

Il test deve verificare sia ciò che viene scritto sia ciò che viene successivamente recuperato.

## 8. DiagnosticTrace end-to-end

Ogni passaggio significativo deve essere osservabile.

Traccia minima:

```text
INPUT
→ OBSERVATION
→ UNDERSTANDING
→ CONTEXT
→ RETRIEVAL
→ AUTHORITY
→ MEMORY ADMISSION
→ MEMORY RESULT
→ AFFECTIVE/RELATIONSHIP/GOAL quando presenti
→ DECISION
→ PROMPT
→ GGUF
→ OUTPUT VALIDATION
→ PERSISTENT CONSOLIDATION
```

DiagnosticTrace deve contenere fatti osservabili, non chain-of-thought privata.

Deve includere quando applicabile:

- reason codes;
- stato decisione;
- confidence;
- source reliability;
- provenance;
- candidate IDs;
- selected IDs;
- first divergence;
- before/after state diff;
- memory lifecycle;
- prompt inviato al GGUF;
- output validation result.

## 9. Stati espliciti: mai collassare il significato

Distinzioni obbligatorie:

```text
UNKNOWN != UNRESOLVED
UNRESOLVED != AMBIGUOUS
AMBIGUOUS != CONFLICTED
NO_MATCH != UNAVAILABLE
UNAVAILABLE != ERROR
NOT_APPLICABLE != UNKNOWN
```

Un `null`, lista vuota, `false` o zero non deve essere usato per nascondere differenze semantiche quando il contratto dispone di uno stato esplicito.

## 10. Fix specifici, non patch generiche

Workflow obbligatorio dei bug:

```text
BUG OSSERVATO
→ RIPRODUZIONE
→ CAUSA RADICE
→ CLASSIFICAZIONE P0/P1/P2
→ FIX MINIMO MIRATO
→ REGRESSION TEST
→ TEST CROSS-MODULO
→ FULL SUITE
→ CONTINUITY
```

Da evitare:

- regex linguistiche aggiunte per casi singoli;
- owner/source hardcoded;
- first-entity wins;
- recent-entity wins;
- falsi conflitti da stesso actor;
- lexical similarity usata come contradiction;
- confidence usata come truth;
- correzione usata come supersede automatico;
- abbassamento dei gate per ottenere PASS.

## 11. Gate di congelamento obbligatori

Ogni fase importante termina con un freeze/checkpoint.

Gate tipico:

```text
CONTRACT FROZEN
→ IMPLEMENTATION
→ UNIT TESTS 100%
→ CROSS-MODULE TESTS 100%
→ FULL REGRESSION 100%
→ DIFF AUDIT
→ CONTINUITY UPDATE
→ FINAL HEAD CI 100%
→ MERGE
→ POST-MERGE MAIN CI 100%
```

Se uno di questi gate fallisce:

```text
STOP
```

Non si inizia il modulo successivo.

## 12. Checklist pre-integrazione

Prima di collegare realmente un modulo all'orchestrator verificare:

```text
[ ] contratto congelato
[ ] input completo
[ ] output completo
[ ] ownership definita
[ ] cardinalità definita
[ ] provenance definita
[ ] UNKNOWN/UNAVAILABLE/AMBIGUOUS preservati
[ ] adapter lossless o fail-closed
[ ] nessun re-parsing linguistico downstream
[ ] nessuna scrittura fuori ownership
[ ] unit tests verdi
[ ] boundary tests verdi
[ ] cross-module tests verdi
[ ] regressione completa verde
[ ] DiagnosticTrace presente
[ ] continuity aggiornata
[ ] diff limitato al checkpoint autorizzato
[ ] altri repository non modificati
[ ] CI final-head verde
```

Solo dopo questa checklist il rewire è autorizzabile.

## 13. Continuità obbligatoria

`docs/WORK_CONTINUITY.md` deve essere aggiornato:

- all'avvio di ogni task/branch;
- dopo ogni commit/checkpoint significativo;
- dopo ogni test/benchmark importante;
- quando cambia contratto, dataset, modello o strategia;
- prima di operazioni rischiose;
- appena emerge un rischio di stop/perdita contesto;
- prima di terminare la sessione.

Deve contenere almeno:

- repo/branch/HEAD;
- base/freeze di riferimento;
- task corrente;
- gate completati;
- gate corrente;
- ultimo esperimento/test e risultato;
- artifact/checksum quando applicabile;
- rischi aperti;
- modifiche ad altri repo = true/false;
- exact restart point.

## 14. Una repository scrivibile alla volta

Regola:

```text
ONE ACTIVE WRITE REPOSITORY
```

Le altre repository possono essere lette per contratti, provenance e compatibilità, ma non modificate senza autorizzazione esplicita del proprietario.

Le repo storiche sono checkpoint/backup, non target normali di sviluppo.

## 15. Compatibilità legacy

Il legacy non deve governare il nuovo contratto.

Se un DTO vecchio non può rappresentare il nuovo contratto:

```text
KEEP / DEPRECATE / QUARANTINE
```

Non si impoverisce il nuovo modello per adattarlo al vecchio.

La migrazione avviene soltanto dopo che il percorso canonico è testato standalone.

## 16. Memory-specific golden rules

Per Memory valgono inoltre:

```text
TypedClaim != Belief
Belief != Memory
Contradiction != Supersession
Correction != Supersession
Temporal change != Contradiction by default
Retrieval relevance != Authority
Authority != BeliefConfidence
```

Pre-response:

```text
stableWrite = false
memoryIds = []
```

Durable write soltanto dopo:

```text
accepted output/action
→ VALIDATE
→ PersistentConsolidation
→ Memory Admission
→ MemoryRepository
```

## 17. Acceptance criterion del core cognitivo base

Il core non è considerato realmente funzionante finché non passa automaticamente e poi su APK una batteria del tipo:

```text
"Vivo a Milano"
→ salva il fatto corretto

nuovo turno / riavvio
"Dove vivo?"
→ recupera Milano

"Prima vivevo a Venezia, ora vivo a Milano"
→ temporalità corretta, nessun falso conflitto

"Marco dice che Anna vive a Roma"
→ REPORT, non WORLD_TRUTH

"Mi ero sbagliato, vivo a Torino"
→ target corretto
→ SUPERSEDE corretto
→ lineage preservato
→ Torino corrente
→ precedente memoria storica/superseded
```

Solo dopo i test automatici verdi si produce il primo APK diagnostico.

## 18. Sequenza di lavoro corrente

Ordine canonico verso il primo APK:

```text
NLU V3 / Student-5
→ Understanding canonico
→ Context/Retrieval
→ Authority canonica
→ Memory Admission
→ MemoryRepository
→ Persistent Consolidation
→ test automatici E2E
→ primo APK diagnostico
→ test reali su dispositivo
→ Reflection
```

Reflection viene dopo una Memory verificata, così non produce inferenze su ricordi corrotti o non affidabili.

## 19. Regole d'oro finali

```text
1. CONTRACT BEFORE CODE
2. ONE OWNER PER STATE
3. ADAPTER BEFORE DIRECT COUPLING
4. FAIL CLOSED BEFORE GUESSING
5. UNIT + CROSS-MODULE + E2E
6. DIAGNOSTIC TRACE EVERYWHERE
7. FIX THE CAUSE, NOT THE SYMPTOM
8. NEVER LOWER A GATE TO GET GREEN
9. ONE WRITE REPO AT A TIME
10. NO NEXT MODULE UNTIL CURRENT SUITE IS 100% GREEN
```
