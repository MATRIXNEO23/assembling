# Regole canoniche di lavoro

Stato: CANONICO
Data: 2026-09-08

## Una repository alla volta

Salvo indicazione esplicita del proprietario, si lavora su una sola repository alla volta.

- Una sola repository è il target di lavoro attivo.
- Le altre repository possono essere lette solo quando serve verificare dipendenze o contesto.
- Non si scrive su altre repository senza istruzione esplicita del proprietario.
- Il cambio di repository attiva richiede istruzione esplicita.
- Se una modifica di un componente richiede aggiornamenti di codice, test, documentazione o continuità nella repository attiva, tali aggiornamenti vanno mantenuti coerenti nello stesso workstream.
- Non creare specifiche parallele quando esiste già un documento canonico aggiornabile.

## Sorgente operativa corrente: Assembling

Per il Matrix Engine corrente la repository attiva/canonica è `MATRIXNEO23/assembling` salvo esplicito cambio autorizzato dal proprietario.

Repository storiche come `8.10.9evo3`, `8.10.9evo3-solo-gpt` e predecessori sono backup/fonti storiche: non devono essere usate come implementazione corrente, né come base per un nuovo indice dei moduli, salvo recupero mirato esplicitamente richiesto.

## Indice canonico moduli Engine

Prima di iniziare o riprendere qualunque lavoro sul Matrix Engine leggere:

`docs/ENGINE_MODULE_INDEX.md`

L'indice deve permettere a una nuova sessione/Work/Supervisor di sapere autonomamente:
- quali moduli esistono;
- quali sono realmente cablati, parziali, compatibility/deprecated o `NON_CABLATO`;
- dove si trova il codice corrente;
- quali sono i documenti/contratti autorevoli;
- quali adapter/vendor/artifact appartengono al modulo;
- quali percorsi sono storici e non vanno usati come correnti.

Ogni task che modifica o aggiunge un modulo, adapter, protocollo, modello/runtime artifact, locator, persistence route, stato di wiring o gate di readiness deve aggiornare `docs/ENGINE_MODULE_INDEX.md` e `docs/WORK_CONTINUITY.md` prima di essere considerato completato.

## Nuovi moduli = directory/package dedicata

Ogni nuovo modulo funzionale introdotto nella repository deve vivere in una propria directory/package dedicata.

Regola:

```text
NEW FUNCTIONAL MODULE
→ dedicated directory/package
```

Esempi corretti:

```text
matrix/assembling/mip/
matrix/assembling/adapters/
matrix/assembling/coherence/
```

Per futuri moduli, usare analogamente directory/package dedicate (`context/`, `retrieval/`, `diagnostics/`, ecc.) quando e solo quando tali moduli vengono esplicitamente autorizzati.

Non aggiungere nuovi moduli come file scollegati direttamente nel root `matrix/assembling`.

Questa regola è prospettica: i file runtime root già esistenti non vengono spostati solo per estetica. Move/rename è consentito solo quando riduce un'ambiguità reale, mantiene compatibilità verificabile e supera i gate di regressione.

## Repository storiche = backup

Le repository/versioni precedenti o superate sono backup/checkpoint di recupero, non target di sviluppo normale.

- Restano integre come riferimento e via di ritorno.
- Si consultano o recuperano componenti da esse se la linea corrente arriva a un punto morto, introduce una regressione grave o serve confrontare una soluzione precedente valida.
- Non si riprende automaticamente un'intera vecchia repo: si recuperano solo i componenti/commit necessari con provenienza chiara.
- Non si modificano o cancellano le repo backup senza istruzione esplicita del proprietario.

Queste regole riguardano il metodo di lavoro e non modificano da sole l'architettura runtime.
