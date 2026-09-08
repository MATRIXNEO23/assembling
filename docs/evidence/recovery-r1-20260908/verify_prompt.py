"""Validate captured JVM fixture output with Python's independent JSON parser.
Run here, or pass the build/diagnostics directory after `gradle test`.
This is contract transport verification, not a model quality benchmark.
"""
import json
from pathlib import Path
import sys
root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(__file__).parent
p = json.loads((root / 'v3-prompt.json').read_text())
c = p['claims'][0]
assert p['input'] == 'Marco dice che Anna non non vive a Roma dopo il 2024.'
assert p['nluContractFingerprintSha256'] == '7b0646e44243ad897760c0fcadbe141f1b8e88e3fd8d63a1789106571b9987b0'
assert len(p['claims']) == 1 and c['claimId'] == 'c0'
assert len(p['mentions']) == 3 and len(p['referentCandidates']) == 5
assert c['subjectReferent']['value'] == c['ownerReferent']['value'] == 'mention:m1'
assert c['sourceReferent']['value'] == 'mention:m0'
assert c['perspectiveReferent']['value'] == 'ctx:speaker'
assert c['targetReferent'] == {'value':'NONE','confidence':0.98,'fieldStatus':'NOT_APPLICABLE','alternatives':[]}
assert c['polarity']['value'] == 'NEGATIVE'
assert c['negationCueSpans'] == [{'start':20,'end':23},{'start':24,'end':27}]
assert [p['input'][s['start']:s['end']] for s in c['negationCueSpans']] == ['non','non']
assert c['temporalRelation']['value'] == {'relation':'AFTER','anchorRef':'temporal:t0'}
assert c['temporalEvidence'][0]['temporalId'] == 't0'
assert c['claimKind']['value'] == 'REPORT' and c['dialogueAct']['value'] == 'ASSERT'
assert c['authorityResolution']['claimId'] == c['claimId']
assert c['authorityResolution']['contextSnapshotId'] == p['contextSnapshot']['snapshotId']
assert c['provenance']['observationId']['value'] == p['observationSourceId']
assert c['provenance']['claimId']['value'] == c['claimId']
assert p['retrieval'] == {'status':'UNAVAILABLE','value':None}
assert p['memoryPreflight'] is None
assert all(d['availability'] == 'NOT_WIRED' for d in p['contextSnapshot']['domainAvailability'])
escaped = json.loads((root / 'v3-prompt-escaped.json').read_text())
assert escaped['claims'][0]['diagnostics'] == ['IT EN ES: sì, mañana, desire\n"istruzione"\\\t']
assert set(c) == {'claimId','provenance','sourceSpan','subjectSpans','objectSpans','negationCueSpans','temporalEvidence','entityMentionIds','dialogueAct','predicate','subjectReferent','targetReferent','ownerReferent','perspectiveReferent','sourceReferent','polarity','temporalRelation','claimKind','fieldStatusByField','confidenceByField','overallInterpretationConfidence','structuralStatus','interpretationStatus','diagnostics','authorityResolution'}
print('PASS: independent JSON decoding, role/evidence/identity preservation and escaping')
