# MATRIX — recupero e coerenza dell’intero progetto

**Esito: esistono componenti recuperabili e testati; non emerge una ragione per rifare tutto. Il sistema completo non è ancora dimostrato funzionante.** Il recupero riguarda tutti i domini individuati nei documenti e negli alberi correnti, non soltanto CP47. Le lacune principali sono collegamenti mancanti, prove con scope limitato e documenti di epoche diverse trattati come stato corrente.

Questo assessment aggiorna la riconciliazione precedente: R1 V3→Authority→prompt è già risolto; Memoria e Affective hanno sorgenti esterne ora recuperate/testate; il backup storico contiene altri moduli da conservare. Non sono state cambiate implementazioni, dataset, modelli o decisioni D01–D07.

## Fonti fotografate

| Repository | Commit letto | Ruolo |
|---|---|---|
| [assembling](https://github.com/MATRIXNEO23/assembling/tree/283c88122bc1fd6fb2d90ae7efe81d07b84ffede) | `283c88122bc1fd6fb2d90ae7efe81d07b84ffede` | Engine attivo; unica destinazione di scrittura |
| [matrix-understanding-lab](https://github.com/MATRIXNEO23/matrix-understanding-lab/tree/23341d2db7fbe6bd42907a8a742540c4dab1a5a6) | `23341d2db7fbe6bd42907a8a742540c4dab1a5a6` | Sorgente specializzata, sola lettura |
| [memoria](https://github.com/MATRIXNEO23/memoria/tree/cde91db20d97e0792b61db15144faf1430fd27bc) | `cde91db20d97e0792b61db15144faf1430fd27bc` | Sorgente specializzata, sola lettura |
| [matrix-affective-lab](https://github.com/MATRIXNEO23/matrix-affective-lab/tree/b51f89c45aed123b290d30bc8ab0a9a0031cc8b9) | `b51f89c45aed123b290d30bc8ab0a9a0031cc8b9` | Sorgente specializzata, sola lettura |
| [8.10.9evo3-solo-gpt](https://github.com/MATRIXNEO23/8.10.9evo3-solo-gpt/tree/e97f75052afcc93d5b1e08b3ac881dba35633451) | `e97f75052afcc93d5b1e08b3ac881dba35633451` | Backup storico, sola lettura |

Sono stati riutilizzati i 97 file/prove della precedente riconciliazione e le prove R1. In questo approfondimento sono stati autenticati altri 115 blob Git (96 Memoria/Affective e 19 storici) e 16 file Foundation. L’albero storico ha 381 entry, non troncato. Non è una verifica di ogni riga di ogni repository né dei working tree mai pushati.

## Prove realmente eseguite

| Componente | Esito | Cosa dimostra e limite |
|---|---|---|
| Engine / MIP / Understanding / Authority / prompt | 159 test JVM PASS, prova R1 e CI già persistite | Confini software con fixture; non NLU/GGUF reali. Suite non ripetuta perché implementazione invariata |
| Authority Python | 15/15 PASS ora | Resolver strutturato reference; non equivalenza completa Kotlin/Python |
| Affective Lab | 141/141 PASS ora | Appraisal/stato e controlli della suite esistente; non bridge Android né storage durabile |
| Memoria Foundation v3 | 25/25 PASS ora | 16 file canonici identici; SQLite reale in file di test, admission, rollback, lineage, supersede e protezioni; non riavvio Android |

Non sommare questi risultati come percentuale di correttezza del prodotto. I 181 test Python appena eseguiti non sostituiscono la prova della catena Engine. I log e XML grezzi sono nel dossier. Il primo avvio Foundation ebbe un errore di percorso nel setup locale: documentato in execution.json; corretta la copia di recupero, non il codice.

## Registro di tutti i domini individuati

Le classificazioni valgono nello scope della colonna prova. **NON_CABLATO nell’Engine non significa inesistente nel progetto.** Il registro JSON allegato conserva per ogni voce repository, commit, percorsi, limite e azione raccomandata.

| ID | Modulo / dominio | Stato osservato | Prova / limite |
|---|---|---|---|
| M01 | MIP / confini semantici | VERIFIED_SCOPED | Suite JVM R1: 159 test complessivi; contratti/status/evidence verificati con fixture. Non prova conformità di ogni backend reale. |
| M02 | MatrixTurnFrame / diagnostica | VERIFIED_SCOPED | Carrier del turno e firstDivergence testati. Non sostituisce MIP e non decide semantica. |
| M03 | Understanding V3 | VERIFIED_SCOPED | Adapter e validazione V3 eseguiti in JVM. Input strutturati di prova; inferenza linguistica reale non dimostrata. |
| M04 | Authority Kotlin | VERIFIED_SCOPED_WITH_GAP | Risoluzione strutturata e confine V3→Authority→prompt testati. Retrieval multi-claim resta unresolved; port non consuma binding ora presente nel DTO. |
| M05 | Authority Python di riferimento | VERIFIED_SCOPED | 15 test originali passati ora, Python 3.12.13 / pytest 8.4.1. Non è il runtime Kotlin; non dimostra equivalenza integrale delle due implementazioni. |
| M06 | Coherence | PARTIAL_LEGACY_WIRING | Controlli legacy e fail-closed coperti dalla suite JVM. BasicCoherenceGuard legge typedClaims legacy; indice cita anche percorso compatibility. |
| M07 | Orchestratore Engine | IMPLEMENTED_NOT_FUNCTIONALLY_VERIFIED | Percorso esistente testato con NLU statico, GGUF echo e memoria disabilitata. Mancano stadi espliciti retrieval/context, decision e consolidation; catena reale non provata. |
| M08 | Context / workspace / snapshot | PARTIAL_NOT_WIRED | DTO e snapshot definiti; frame iniziale può essere fornito dal chiamante. handle(UserMessage) non collega un provider di contesto/retrieval. |
| M09 | Memory Foundation / SQLite / Admission | VERIFIED_REFERENCE_NOT_WIRED | Recuperati 16/16 file con SHA canonici; 25 test originali passati ora su SQLite in file temporanei. Copia esatta in questo dossier. Non è un backend Kotlin/Room; non collegato all’Engine. Suite non dimostra riavvio Android/process death. |
| M10 | Memory preflight | VERIFIED_SCOPED | Divieto di scritture persistenti prima dell’output verificato in JVM. Non è una memoria durabile né retrieval. |
| M11 | Retrieval / indice / reranking | NOT_WIRED_CURRENT_HISTORICAL_SOURCE_EXISTS | Sorgenti storiche lette e autenticate; MIP corrente definisce il confine. Nessuna equivalenza o integrazione con MIP attuale provata; non testato qui. |
| M12 | Consolidamento / persistenza Engine | NOT_WIRED_CURRENT | Port PersistentConsolidationPort presente; nessuna chiamata nel percorso corrente. Foundation reference ha transazioni/lineage testate. Non esiste prova di commit finale Engine→storage→riavvio. |
| M13 | Affective appraisal / emotions / mood / persistent affect | VERIFIED_LAB_NOT_ENGINE_RUNTIME | 141 test originali passati ora; cause, decay, replay, target isolation e scenari coperti dalla suite. Stato affettivo Python in memoria non significa storage persistente; nessun bridge reale Android verificato. |
| M14 | Affective adapter Kotlin | IMPLEMENTED_NOT_FUNCTIONALLY_VERIFIED | Adapter e snapshot presenti. Diff vendor/lab è soltanto newline finale; nessuna deriva algoritmica in quel file. Appraisal prototype.py e runtime cross-language non sono dimostrati dal solo vendor engine.py. |
| M15 | RelationshipState / controller relazionale | NOT_WIRED_CURRENT | Dominio separato esplicito; Affective non ne è proprietario. Grafo storico di entità/relazioni non equivale a controller relazionale canonico. |
| M16 | Intimità / consenso / confini | NOT_WIRED_CURRENT | Dominio separato riservato. Requisiti linguistici NLU preservati. Nessun resolver completo identificato nell’Engine esaminato; lessico NLU non è policy/controller. |
| M17 | Goal / intention / agent state | NOT_WIRED_CURRENT_HISTORICAL_SOURCE_EXISTS | Sorgente autentica: deduplica stato, filtra goal attivi, ordina priorità e lega intention a goal. Non è un decisore BDI/Utility completo; non integrato in Assembling. |
| M18 | Decision / comportamento | NOT_WIRED_CURRENT_PARTIAL_REFERENCE | Interfaccia e BehaviorDecision storici recuperati. Interfaccia/envelope non dimostrano algoritmo completo; orchestratore corrente non ha decision stage. |
| M19 | Reflection | HISTORICAL_IMPLEMENTATION_NOT_CURRENTLY_VERIFIED | Quattro sorgenti lette; test storici identificati nell’albero Git. Test storici non eseguiti qui; nessun collegamento al nuovo MIP provato. |
| M20 | World / perception / agent loop | HISTORICAL_REFERENCE_NOT_CURRENTLY_WIRED | Protocollo e loop recuperati; test WorldMatrixProtocol identificato. Non prova un World host reale né runtime multi-agent integrato nell’Engine attuale. |
| M21 | Entity identity / relationship graph | PARTIAL_CONTRACT_AND_HISTORICAL_REFERENCE | Interfaccia + InMemoryEntityRelationshipGraph recuperati; riferenti/candidati esistono nel MIP corrente. Non equivale a resolver universale di identità o RelationshipState controller. |
| M22 | Prompt realization | VERIFIED_SCOPED_R1_CLOSED | R1 chiuso: V3→Authority→prompt eseguito, 159 test complessivi e CI verde, readback precedente. Non dimostra qualità del testo GGUF né intera orchestrazione. |
| M23 | GGUF generazione reale | PORT_ONLY_IN_CURRENT_ENGINE | Port e EchoGgufAdapter testabili. Nessun backend modello reale eseguito nella catena corrente esaminata; eventuale host storico non validato qui. |
| M24 | Output validator | PORT_NOT_REAL_SEMANTIC_VALIDATION | Confine opzionale e NON_CABLATO esplicito quando assente. Esecuzione di un port non prova validazione semantica. |
| M25 | Android / applicazione host | NOT_VERIFIED_FOR_CURRENT_ENGINE | Albero storico contiene app Android; Assembling corrente è Kotlin/JVM. Nessuna build/device test dell’app storica o integrazione nuova eseguita qui. |
| M26 | Compatibility / vecchie pipeline | HISTORICAL_COMPATIBILITY_PRESERVE | Percorsi separati e test esistenti. Non sono autorità canonica per nuovi caller; evitare fallback impliciti V3→legacy. |
| M27 | Student-4 / baseline | FAILED_HISTORICAL_DEV_GATE_NOT_PRODUCTION_APPROVED | Stato storico conservato; autorizzazione R2 controlled-runtime distinta dal gate production. LFS pointer valido non dimostra payload riletto in questa verifica; nessuna nuova valutazione. |
| M28 | Student-5 / dieci checkpoint FP32 | ARTIFACT_BYTE_VERIFIED_SELECTION_PENDING | CP47 Release 384423046 / asset 549780835: readback precedente fresco, 10 model SHA e 58 checksum interni validi. Nessun vincitore; qualità/generalizzazione non provata da TRAIN loss. |
| M29 | TRAIN / target builder / G03 | PRESERVED_PREVIOUS_SCOPED_VERIFICATION | TRAIN f90ae775a44023c37bf0c3a5087d64746413cbec770ca36cf154d1d533544aa4; G03 PASS storico; 4355 target. Nessun nuovo audit dataset; correttezza target non prova predizioni del modello. |
| M30 | E5 candidato / import | ISOLATED_CANDIDATE_NOT_MATRIX_VALIDATED | Import e locator LFS documentati nella riconciliazione precedente. Non è automaticamente modello NLU con teste MATRIX; non inferenza validata. |
| M31 | Benchmark / valutazione / prove di regressione | PARTIAL_EVIDENCE | CP48 documenta selezione bloccata; Colab 2/8 è resoconto esplorativo non gate. Manca selezione Student-5 autorizzata recuperata; nessuna sostituzione con TRAIN/Frozen. |
| M32 | Piani / indici / continuità inter-repo | PARTIAL_INCONSISTENT_STATUS_LABELS | Esistono piani/versioni con scope diversi e COMPLETE storici; questo registro separa origine, test, integrazione. Lab ARCHITETTURA Python DESIGN_ONLY e vecchi README non descrivono il runtime attuale. |

## Coerenza e problemi concreti

**C01 — HIGH — OPEN_INTEGRATION_GAP**. La catena canonica completa non è dimostrata: stadi legacy e canonici convivono; retrieval/context, decision e consolidation non sono cablati nel root orchestrator. R1 prompt è già chiuso, non va rifatto.

Azione: Un checkpoint di verifica del turno canonico sui port esistenti, con trace e prime divergenze; nessuna sostituzione dei backend o invenzione di campi.

**C02 — MEDIUM — CONFIRMED_STATIC_CAPABILITY_GAP**. CanonicalUnderstandingV3AuthorityPort.retrievalForClaim supporta soltanto 1 claim/1 result. Il commento dice che RetrievalResult non ha claimId, ma il DTO corrente ha claimId/contextSnapshotId e validatori di binding.

Azione: Proteggere con regressione specifica prima di eventuale correzione; non forzare risoluzione quando il binding è davvero insufficiente.

**C03 — HIGH_IF_DECLARED_COMPLETE — MISSING_ENGINE_BACKENDS**. Test di port/echo/preflight non provano backend NLU/GGUF, memoria persistente o Android.

Azione: Non promuovere Engine a funzionante end-to-end; recuperare e verificare i backend pertinenti in checkpoint separati.

**C04 — MEDIUM — RECOVERY_CLOSED_IN_THIS_CHANGE**. Foundation v3 non era completa nella repo memoria: 16 sorgenti/test esistevano separati. Ora recuperati con checksum identici, 25 test passati e snapshot di riferimento persistito.

Azione: Usare questo locator e provenance; non riscrivere per assenza della copia locale.

**C05 — MEDIUM — DOCUMENTATION_SCOPE_CONFLICT**. ARCHITETTURA.md Lab è DESIGN_ONLY, include RELATIONSHIP/GOAL nelle decisioni Admission e sequenza Memory prima di risposta; MIP corrente mantiene domini separati e commit finale dopo output. Vecchi README propongono ancora task già eseguiti.

Azione: Trattare quei piani come storici/proposte, non runtime canonico né nuova autorizzazione. Nessuna decisione semantica riaperta.

**C06 — MEDIUM — HISTORICAL_REUSE_COMPATIBILITY_UNVERIFIED**. Il backup Android contiene implementazioni di reflection/retrieval/goal/graph: assenza in Assembling non significa inesistenza globale. Copiarle senza verifica introdurrebbe contratti e ownership storici.

Azione: Conservare commit/path e test originali; riuso selettivo solo dopo confronto col MIP corrente.

**C07 — MEDIUM — MODEL_EVALUATION_LIMITATION_ALREADY_KNOWN**. Student-5 recuperabile non è selezionato; Student-4 non supera automaticamente gate production; E5 importato non è validato MATRIX.

Azione: Nessun training/rimpiazzo automatico; non usare loss TRAIN o 2/8 ad hoc come gate.

**C08 — LOW — NON_DEFECT_OBSERVATION**. SHA vendor Affective diverso dal Lab esclusivamente per newline finale: nessuna differenza algoritmica in affective_engine.py.

Azione: Non correggere o sostituire il file per il solo hash diverso.

**C09 — MEDIUM — STATUS_RECONCILIATION**. COMPLETE storico, sorgente presente, unit test PASS e integrazione reale erano confusi. Coherence index legacy e vecchi next-step non bastano per stato runtime.

Azione: Consultare assessment corrente nell’indice esistente e continuità; niente indice canonico concorrente.

## Cosa conservare, cosa correggere, cosa dimostrare

- **Conservare:** contratti MIP/TurnFrame, adapter V3, Authority e fix R1; sorgenti e suite Affective; Foundation SQLite recuperata; checkpoint e dataset esistenti; backup storico di retrieval/reflection/agent state/graph/host. Nessuna cancellazione o riscrittura totale è giustificata dalle prove raccolte.
- **Correggere in modo mirato:** collegamenti canonici dove i consumatori leggono ancora legacy, binding retrieval che non usa i campi disponibili, stati/documenti che confondono implementazione e integrazione. Questo assessment non applica tali modifiche di codice.
- **Dimostrare:** un turno Engine reale tra backend, non solo fixture; poi persistenza finale/recupero dopo riavvio e target Android. I domini non cablati restano tali: non si simulano Relationship, Consent, Decision o World per ottenere una demo verde.
- **Riuso storico:** prima di implementare un modulo dichiarato assente, consultare i percorsi del backup. Il codice storico è recuperabile ma non promosso automaticamente al MIP corrente. Nessuna app storica è stata ricompilata o modificata qui.

## Recupero durabile, senza dipendenza dalla chat

Il punto d’ingresso rimane `docs/ENGINE_MODULE_INDEX.md` con `docs/WORK_CONTINUITY.md`; questo documento è un assessment collegato, non un indice concorrente. Il dossier è `docs/evidence/project-coherence-recovery/`:

- `module-assessment.json`: 32 domini, origine e prossima verifica circoscritta.
- `findings.json`, `assessment.json`: esito, problemi, scope e limiti.
- `external-source-register.json`: 115 locator Git con SHA blob/SHA-256/dimensioni.
- `historical-tree.json`: inventario del backup per recuperare anche file non letti semanticamente.
- `foundation-v3-reference/`: copia esatta dei 16 file Foundation/test, reference storico e istruzioni; **non importata nel runtime**.
- `foundation-provenance.json`: SHA canonici e identità originali dei file recuperati.
- `*-tests.xml`, `*-tests.log`, `execution.json`: risultati e ambiente effettivi.
- `affective-vendor-comparison.json`: differenza solo newline, nessuna correzione necessaria.
- `SHA256SUMS`: verificare dalla root Assembling con `sha256sum -c docs/evidence/project-coherence-recovery/SHA256SUMS`.

Per codice già in Git, recuperare repository+commit+path; per modelli usare Release/asset e checksum dell’indice proprietario, non artifact temporanei. Assenza locale non significa perdita. I file Foundation prima dispersi hanno ora anche un unico snapshot Git di riferimento, verificabile e ripristinabile.

## Punto di ripresa

Assessment e recupero conclusi; **nessun nuovo sviluppo avviato**. Primo checkpoint tecnico raccomandato: verificare il turno canonico V3 attraverso gli stadi correnti dell’orchestratore, con le suite e la diagnostica esistenti, per localizzare i consumatori legacy rimasti. Non rifare R1, Foundation, Affective, TRAIN o training. Il collegamento di nuovi backend e il recupero selettivo di moduli storici richiedono ciascuno un checkpoint verificabile, senza cambiare architettura implicitamente.

La raccomandazione tecnica è recupero e completamento mirato, non ripartenza da zero. Non equivale a certificazione production: la verifica funzionale complessiva resta aperta con lacune nominate e sorgenti conservate.
