# Matrix Engine — Addendum canonico di tracciabilità dei test

Status: CANONICAL WORK METHOD ADDENDUM  
Scope: tutti i test atomici, cross-modulo, E2E e diagnostici di Matrix Engine  
Extends: `docs/MATRIX_ENGINE_WORK_METHOD.md` §§5, 6, 7, 8, 12, 17  
Does not supersede: existing canonical work method, MIP, architecture, continuity or module ownership rules.

## 1. Scopo

Questo addendum precisa **come deve essere dimostrato** il comportamento di ogni modulo e di ogni confine MIP.

Non basta sapere che un test è verde o che un DTO è valido. Per ogni caso significativo deve essere possibile ricostruire, con dati osservabili:

```text
COSA È ENTRATO
-> COSA HA RICEVUTO DAVVERO IL MODULO
-> COSA HA PRODOTTO DAVVERO IL MODULO
-> COME L'OUTPUT È STATO RAPPRESENTATO NEL CONTRATTO MIP
-> QUALI CAMPI SONO STATI CONSEGNATI AL MODULO SUCCESSIVO
-> COSA HA RICEVUTO DAVVERO IL MODULO SUCCESSIVO
-> COSA HA PRODOTTO DAVVERO IL MODULO SUCCESSIVO
```

Obiettivo: quando il ciclo completo sarà integrato, il primo punto di divergenza deve essere localizzabile senza indovinare.

## 2. Regola fondamentale per ogni test reale

Per ogni modulo o passaggio cross-modulo devono essere registrati almeno:

```text
caseId / turnId / correlationId
implementation identity (repo/branch/HEAD/model/artifact quando applicabile)
real input
expected semantic result
actual module input
actual module output
MIP payload/envelope emitted
fields consumed by downstream
actual downstream input
actual downstream output quando il consumer è disponibile
field-by-field expected vs actual
lostFields[]
changedFields[]
inventedFields[]
firstDivergence
reasonCodes[]
confidence/status/provenance quando applicabili
verdict
```

Un campo non presente non può essere nascosto dentro un semplice `null` se MIP prevede uno stato esplicito.

## 3. Formato minimo di prova di un singolo modulo

```text
INPUT REALE
  "Marco dice che Anna vive a Roma"

MODULO REALE
  NLU / Understanding @ exact version

ATTESO
  source = Marco
  subject = Anna
  predicate = matrix.location.live_at
  object = Roma
  claimKind = REPORT

OTTENUTO
  <output reale decodificato>

DIFF CAMPO-PER-CAMPO
  source: expected Marco / actual ...
  subject: expected Anna / actual ...
  ...

VERDETTO
  PASS / PARTIAL / FAIL
```

Il valore concreto sopra è solo un esempio di formato. Nessun risultato va dichiarato senza aver eseguito il modulo reale.

## 4. Formato minimo di prova cross-modulo

Ogni confine importante deve essere dimostrato come:

```text
PRODUCER OUTPUT REALE
-> MIP CONTRACT / ENVELOPE REALE
-> ADAPTER REALE, se necessario
-> CONSUMER INPUT REALE
-> CONSUMER OUTPUT REALE
```

Verifiche obbligatorie:

```text
producer fields emitted
MIP fields represented
adapter mapping field-by-field
consumer fields consumed
identity/correlation/causation preserved
provenance preserved
uncertainty/status preserved
no field silently dropped
no field silently changed
no field invented
no downstream linguistic reparse
```

Se il consumer non è ancora implementato, la prova si ferma esplicitamente al confine disponibile e il verdict non può essere `DEMONSTRATED END-TO-END`.

## 5. MIP come lingua comune osservabile

MIP è la lingua tipizzata comune tra i moduli. Nei test va verificato **di fatto**, non solo concettualmente.

Per ogni handoff MIP significativo registrare:

```text
schemaId / schemaVersion
messageId
correlationId
causationId se applicabile
turnId / sessionId / agentId
producer
payloadType
logicalStage
payload reale
provenance
traceRef
```

Per payload semantici come `TypedClaim`, verificare campo per campo almeno le identità/ruoli, predicate, object/value, dialogueAct, claimKind, polarity, temporalità, confidence e provenance disponibili nel contratto corrente.

## 6. Traccia cumulativa del ciclo

Quando più moduli sono integrati, ogni test deve accumulare una singola catena osservabile, non report separati impossibili da correlare.

Esempio di struttura:

```text
RAW INPUT
-> Observation
-> NLU output
-> Understanding / TypedClaim[]
-> ContextSnapshot
-> Retrieval query/result
-> Coherence
-> Authority/Belief
-> MemoryPreflight / state proposals
-> Affective / Relationship / Intimacy / Goal quando presenti
-> Decision
-> Realization package
-> Prompt
-> GGUF output
-> OutputValidation
-> PersistentConsolidation
-> committed owner state
-> eventual World ActionResult
```

Ogni nodo deve avere identità e riferimento al nodo causale precedente sufficiente a trovare il primo punto che ha alterato il significato.

## 7. First-divergence obbligatorio

Quando un test fallisce, il report deve indicare il **primo passaggio osservabile** in cui expected e actual divergono.

Formato:

```text
firstDivergence = <STAGE>.<FIELD_OR_REASON>
upstreamCorrect = true/false
expected = ...
actual = ...
nextStagesAffected = [...]
rootCauseStatus = IDENTIFIED / NOT_YET_IDENTIFIED
```

Non correggere un modulo downstream se la prima divergenza è upstream.

## 8. Casi obbligatori

Per ogni capacità/modulo, compatibilmente con la sua responsabilità:

```text
normal case
boundary case
ambiguous/unresolved case
missing/unavailable case
provider/runtime error case
multi-item/multi-claim case
known regression case
```

Per NLU/Understanding aggiungere IT/EN/ES, code-switch e le famiglie critiche definite nei gate del candidato corrente.

## 9. Fix e retest

Dopo un difetto osservato:

```text
preserve failing input + trace
identify first divergence
identify root cause
apply minimal owner-correct fix
rerun IDENTICAL failing case
rerun neighboring cases
rerun regression suite
compare before/after trace
```

Non è sufficiente ottenere un output finale corretto se il significato è stato corrotto e poi compensato artificialmente da un modulo successivo.

## 10. Presentazione al proprietario

Quando il proprietario deve valutare un risultato, mostrare prima la prova comprensibile:

```text
frase/evento dato
-> cosa ha capito/prodotto il modulo
-> quali campi MIP sono usciti
-> cosa ha ricevuto il modulo successivo
-> cosa è uscito dopo
-> eventuale primo errore
```

Metriche aggregate, CI e percentuali vengono dopo e non sostituiscono questa prova concreta.

Non mostrare file/artifact da scaricare se il proprietario non deve usarli direttamente.

## 11. Criterio di chiusura

Un modulo può essere dichiarato `WORKING/DEMONSTRATED` solo per lo scope realmente provato quando:

```text
real input captured
real implementation executed
actual output captured
expected-vs-actual field comparison complete
MIP handoff verified where downstream exists
normal + ambiguity/error cases covered
first divergence available for failures
observed root causes fixed
identical retest passed
regression passed
trace/provenance sufficient to diagnose the cycle later
```

Fixture/DTO/compile/CI-only evidence remains `STRUCTURAL/TECHNICAL VERIFICATION`, non prova funzionale.

## 12. Relazione con le regole esistenti

Questo addendum:

- **estende** il metodo canonico esistente;
- non cambia ownership o architettura;
- non modifica MIP;
- non abbassa o sostituisce gate esistenti;
- non cambia l'ordine operativo della continuity;
- non autorizza nuovi workstream;
- rende più precisa e verificabile la prova richiesta dalle regole già esistenti.

In caso di conflitto, le invarianti architetturali e i contratti canonici restano autorità; questo addendum governa il formato e la tracciabilità delle prove.
