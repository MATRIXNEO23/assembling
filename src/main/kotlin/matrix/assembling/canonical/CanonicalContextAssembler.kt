package matrix.assembling.canonical

import java.time.Instant
import matrix.assembling.CanonicalContextPort
import matrix.assembling.DiagnosticSnapshot
import matrix.assembling.MatrixTurnFrame
import matrix.assembling.mip.ContextDomain
import matrix.assembling.mip.ContextDomainAvailability
import matrix.assembling.mip.ContextEntry
import matrix.assembling.mip.ContextScope
import matrix.assembling.mip.DomainAvailability
import matrix.assembling.mip.MatrixContextSnapshot
import matrix.assembling.mip.MipEntityRef
import matrix.assembling.mip.MipEntityResolutionStatus
import matrix.assembling.mip.MipField
import matrix.assembling.mip.MipUnderstandingV3FieldStatus
import matrix.assembling.mip.ModuleId
import matrix.assembling.mip.TypedContextValue

/**
 * Minimal real MIP Context assembler for the canonical V3 foundation path.
 * It carries linguistic claim identity/provenance and explicit availability for every reserved domain.
 */
class CanonicalContextAssembler(
    private val memoryAvailability: DomainAvailability = DomainAvailability.UNAVAILABLE,
) : CanonicalContextPort {

    override fun assemble(turn: MatrixTurnFrame): MatrixTurnFrame {
        val observation = turn.requireCanonicalUnderstandingV3()
        val snapshotId = "ctx:${turn.sessionId}:${turn.turnId}:v1"
        val createdAt = Instant.ofEpochMilli(turn.input.timestampMillis)
        val candidates = observation.referentCandidates.associateBy { it.candidateId }
        val mentions = observation.mentions.associateBy { it.mentionId }

        val entries = observation.claims.map { claim ->
            val subjectRefs = claim.subjectReferent
                .takeIf { it.fieldStatus == MipUnderstandingV3FieldStatus.RESOLVED }
                ?.value
                ?.let(candidates::get)
                ?.entityRef
                ?.let(::listOf)
                .orEmpty()
            val entityRefs = claim.entityMentionIds
                .mapNotNull(mentions::get)
                .map { it.entityRef }
                .distinct()

            ContextEntry(
                entryId = "ctx-entry:${claim.claimId}",
                domain = ContextDomain.LINGUISTIC,
                scope = ContextScope.TURN,
                key = "understanding.claim.ref",
                typedValue = TypedContextValue(
                    typeId = "MIP_UNDERSTANDING_V3_CLAIM_REF",
                    payload = claim.claimId,
                ),
                subjectRefs = subjectRefs,
                entityRefs = entityRefs,
                authority = MipField.notApplicable(),
                confidence = MipField.present(claim.overallInterpretationConfidence),
                provenance = claim.provenance,
                validity = MipField.present("TURN"),
                ownerModule = ModuleId.UNDERSTANDING,
                stateVersion = MipField.notApplicable(),
            )
        }

        val availability = ContextDomain.entries.map { domain ->
            when (domain) {
                ContextDomain.LINGUISTIC -> ContextDomainAvailability(
                    domain,
                    DomainAvailability.AVAILABLE,
                    listOf("CONTEXT.LINGUISTIC.AVAILABLE"),
                )
                ContextDomain.MEMORY -> ContextDomainAvailability(
                    domain,
                    memoryAvailability,
                    listOf(
                        if (memoryAvailability == DomainAvailability.AVAILABLE) {
                            "CONTEXT.MEMORY.AVAILABLE"
                        } else {
                            "CONTEXT.MEMORY.${memoryAvailability.name}"
                        }
                    ),
                )
                ContextDomain.SYSTEM -> ContextDomainAvailability(
                    domain,
                    DomainAvailability.AVAILABLE,
                    listOf("CONTEXT.SYSTEM.AVAILABLE"),
                )
                else -> ContextDomainAvailability(
                    domain,
                    DomainAvailability.NOT_WIRED,
                    listOf("CONTEXT.${domain.name}.NOT_WIRED"),
                )
            }
        }

        val snapshot = MatrixContextSnapshot(
            snapshotId = snapshotId,
            turnId = turn.turnId,
            sessionId = turn.sessionId,
            agentId = turn.input.observerId,
            createdAt = createdAt,
            entries = entries,
            domainAvailability = availability,
        )

        return turn.copy(
            contextSnapshot = MipField.present(snapshot),
            diagnostics = turn.diagnostics
                .context(
                    DiagnosticSnapshot(
                        module = "CONTEXT_ASSEMBLER",
                        input = "claims=${observation.claims.size}",
                        output = "snapshotId=$snapshotId; entries=${entries.size}",
                        decision = "IMMUTABLE_CONTEXT_BUILT",
                        status = "PASS",
                        reasonCodes = listOf("CONTEXT.SNAPSHOT.BUILT"),
                        metadata = mapOf(
                            "snapshotId" to snapshotId,
                            "memoryAvailability" to memoryAvailability.name,
                            "linguisticEntryCount" to entries.size.toString(),
                        ),
                    )
                )
                .reason("CONTEXT.SNAPSHOT.BUILT")
                .add("context.canonical.built")
                .tag("context.snapshot_id", snapshotId),
        )
    }
}
