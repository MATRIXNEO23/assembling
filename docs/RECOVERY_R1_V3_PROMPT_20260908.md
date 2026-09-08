# Recovery R1 — confine Understanding V3 → Authority → prompt

Data: 2026-09-08. Repository attiva: `MATRIXNEO23/assembling`.
Base verificata: `8e94db9e692e387f8ef2b5d528f8dd3f1c1d8fcf`.
Branch: `recovery/r1-canonical-v3-prompt`.

## Risultato e confine della prova

**Il difetto del confine canonico V3 → prompt è riprodotto e corretto.** La suite JVM originale esegue 150 test verdi; con la regressione e le nuove prove esegue 159 test verdi, zero errori, zero skip. La verifica indipendente del JSON generato passa. La CI della PR sul commit di codice `5f9495c1def64296fe724fb7933b44191e0b7092` è PASS (run `34198222276`, `gradle test`, 42s). Il readback dei 19 file di quel commit è PASS, con SHA-256 e Git blob ricalcolati. La ricevuta conclusiva della [PR #24](https://github.com/MATRIXNEO23/assembling/pull/24) registra il commit finale, CI pre/post-merge e readback finale: consultarla per l’esito di pubblicazione, senza confondere il gate locale con quello remoto.

Questa è una prova funzionale software del percorso `MatrixNluV3RuntimeBridge` con output fixture → `CanonicalUnderstandingV3Adapter` → `CanonicalUnderstandingV3AuthorityPort` con resolver deterministico reale → `SemanticFrameToPrompt`. Non è inferenza con un modello NLU, una prova con GGUF, una verifica semantica di dataset, un giro completo di `MatrixAssemblingOrchestrator`, un test Memory durevole o un test Android.

## Difetto R1-01

- Severità: P1, integrazione bloccata sul percorso canonico.
- Prima: l'adapter costruisce `canonicalUnderstandingV3`; Authority costruisce `canonicalAuthorityResolutions`. Il prompt root richiede invece `semantic`, poi le decisioni legacy.
- Riproduzione: nuovo test `canonical V3 Authority output reaches prompt without legacy fields`; codice di produzione identico alla base. Sei test eseguiti, cinque verdi e uno fallito con `IllegalStateException: MatrixTurnFrame missing semantic frame`.
- Conseguenza: i test precedenti verdi non dimostravano che l'output V3 raggiungesse il prompt. Riempire i DTO legacy avrebbe perso informazione V3.
- Correzione: dispatch nel prompt root su V3 PRESENT verso un renderer interno dedicato; V3 ERROR fallisce con diagnostica e non torna al legacy. Il corpo legacy successivo al dispatch è byte-identico alla base.
- Stato: corretto e verificato nel confine software indicato. Nessuna promozione generale di Engine/modelli.

## Cosa viene preservato

Il renderer trasporta direttamente tutti i campi dell'osservazione e dei claim V3: identità, fingerprint, provenance, candidati e mention, tutti e cinque i referent, confidence, status, alternative ordinate, span plurali, negation cues, temporal evidence e anchor, dialogueAct, predicate, polarity, claimKind e diagnostica. Ogni risoluzione Authority è associata per claimId, anche se la lista delle risoluzioni cambia ordine.

Usa i codec già esistenti per Authority, provenance, context e retrieval. Non cambia MIP, Authority, NLU V3 o i vocabolari congelati. In particolare mantiene `RESOLVED`, `UNKNOWN`, `AMBIGUOUS`, `NOT_APPLICABLE` e i valori upstream, incluso `NONE`; non introduce una diversa enumerazione KNOWN. Non sceglie candidati, non interpreta parole chiave, non legge memoria, non deduce la polarità dai token.

Authority mancante fallisce chiuso. Claim zero restano zero; INVALID/ABSTAINED e HOLD non sono promossi. Memoria pre-response con `stableWrite=true` o ID persistenti viene rifiutata. Decision Layer e Memory preflight mancanti sono espliciti, senza stato inventato. Le istruzioni testuali al modello sono vincoli di realizzazione: la loro efficacia con un GGUF reale resta da misurare.

## Prove eseguite

| Esecuzione | Test | Failure | Error | Skip | Esito |
|---|---:|---:|---:|---:|---|
| Base, suite completa | 150 | 0 | 0 | 0 | PASS |
| Base + sola regressione, classe Authority V3 | 6 | 1 | 0 | 0 | Riproduzione confermata |
| Correzione, suite completa | 159 | 0 | 0 | 0 | PASS |
| Decoder JSON Python indipendente | — | — | — | — | PASS |

Le nove prove aggiunte verificano il passaggio senza legacy, il blocco su Authority assente, il blocco su scrittura prematura, il blocco su Understanding ERROR, il percorso adapter→Authority→prompt, ambiguità/UNKNOWN/NONE, binding multiclaim con ordine invertito, zero-claim/abstention, escaping e testo multilingue. Le fixture di trasporto non costituiscono gold semantico: il caso storico con due cue `non` serve a verificare cardinalità, non la capacità del modello di interpretarlo.

Ambiente locale: OpenJDK 17 RI build 35 autenticato, Gradle 8.10.2 autenticato, plugin Kotlin 2.0.21, JVM toolchain 17. Proxy e trust store di sistema sono override del solo ambiente locale; TLS non è disabilitato. La precedente CI registrava Gradle 9.7.1/Temurin17: la CI esistente viene eseguita anche sul cambiamento per verificare l'ambiente del progetto. Workflow e dipendenze non sono modificati. I tentativi iniziali senza compilatore/proxy/CA corretti non sono risultati di test del codice.

## Evidenze persistite

Directory: `docs/evidence/recovery-r1-20260908/`.

- `test-results.json`: XML JUnit completi di baseline, riproduzione e dopo la correzione, con conteggi.
- `baseline.log`, `red.log`, `after.log`: log delle tre esecuzioni.
- `environment.json`: toolchain, identità e override locali.
- `source-identities.json`, `source-diff.patch`: prima/dopo del codice e SHA-256.
- `v3-prompt.json`, `v3-prompt-escaped.json`: output effettivi delle fixture JVM.
- `verify_prompt.py`: decoder/verifica indipendente; eseguibile anche contro `build/diagnostics` dopo `gradle test`.
- `verification.json`: risultati circoscritti e immutabilità degli altri file originali.
- `ci-pr-source.json`, `ci-pr-source.log`, `remote-readback-source.json`: prova della CI e recupero remoto del commit di codice prima della sola chiusura documentale.
- `SHA256SUMS`: checksum delle evidenze e dei quattro file Kotlin modificati/aggiunti; percorsi relativi alla root repository.

## Limiti aperti: nessuna dichiarazione di Engine completo

| Area | Stato comprovato / prova ancora richiesta |
|---|---|
| Orchestrator completo | Il codice esistente chiama porte Coherence/Memory/Affective legacy. Questo checkpoint non lo ricabla; manca una composizione canonica completa verificata. |
| NLU reale / Student-5 | Nessuna inferenza qui. La selezione dei checkpoint e l'autenticazione del set di valutazione restano nel laboratorio, con il loro stato persistito. |
| GGUF reale | I test storici dell'orchestrator usano Echo; nessuna qualità generativa reale misurata qui. |
| Memory durevole | Il backend persistente e il percorso post-validazione/consolidation non sono cablati in questo checkpoint. Nessuna prova scrittura → riavvio → retrieval. |
| Decision, Relationship, Intimacy e validazione output | I domini/port dichiarati mancanti restano mancanti; il prompt non li sostituisce. |
| Android | Build corrente JVM; nessun APK o test su dispositivo eseguito. |

La riconciliazione generale di partenza è conservata in `MATRIXNEO23/matrix-understanding-lab`, commit `23341d2db7fbe6bd42907a8a742540c4dab1a5a6`, `reports/MATRIX_PROJECT_RECONCILIATION_20260908.md`.

Nessun modello/dataset modificato, nessun TRAIN/DEV/Frozen letto per questo checkpoint, nessun training, nessuna quantizzazione/ONNX, nessuna modifica a Student-4, nessuna rimozione del workflow E5 storico, nessuna scrittura al laboratorio.

Punto di ripresa dopo il gate remoto: questo confine software è verificato; definire il successivo checkpoint su un collegamento/runtime realmente disponibile, usando indice e contratti correnti. Non dichiarare risolti i limiti sopra e non iniziare automaticamente training, nuovi moduli o un pivot architetturale.
