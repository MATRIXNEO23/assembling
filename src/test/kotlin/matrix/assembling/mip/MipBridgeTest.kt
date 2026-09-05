package matrix.assembling.mip

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import matrix.assembling.AffectiveState
import matrix.assembling.AuthorityDecision
import matrix.assembling.CoherenceDecision
import matrix.assembling.MemoryAdmissionResult
import matrix.assembling.TextSpan
import matrix.assembling.TypedClaim
import matrix.assembling.adapters.MatrixNluClaim

class MipBridgeTest {

    @Test
    fun matrixNluClaimRoundTripPreservesNativeData() {
        val native = MatrixNluClaim(
            dialogueAct = "CORRECT",
            predicate = "consent.withdraw",
            polarity = "NEGATIVE",
            temporalRelation = "PRESENT",
            subjectReferent = "KNOWN_ENTITY",
            targetReferent = "SPEAKER",
            ownerReferent = "KNOWN_ENTITY",
            perspectiveReferent = "KNOWN_ENTITY",
            confidence = 0.97,
            confidenceByHead = mapOf("token.negation" to 0.99, "sequence.predicate" to 0.96),
            sourceSpan = listOf(0, 5),
            subjectSpan = listOf(0, 4),
            objectSpan = listOf(10, 18),
            negationSpan = listOf(6, 9),
            temporalSpan = listOf(19, 23),
            subject = "anna",
            target = "alberto",
            owner = "anna",
            perspective = "anna",
            objectValue = "consenso",
            sourceType = "USER_ASSERTION",
            worldTruth = false,
            adultOrIntimacy = true,
        )

        val canonical = MipBridge.fromMatrixNluClaim(
            native = native,
            claimId = "turn-1:claim:0",
            speakerId = "alberto",
            observerId = "luna",
        )
        val roundTrip = MipBridge.toMatrixNluClaim(canonical)

        assertEquals(native, roundTrip)
        assertEquals("alberto", canonical.speaker.entityId)
        assertEquals("luna", canonical.observer.entityId)
        assertEquals(MipEntityResolutionStatus.RESOLVED, canonical.speaker.resolutionStatus)
        assertEquals(MipEntityResolutionStatus.UNKNOWN, canonical.source.resolutionStatus)
    }

    @Test
    fun assemblingTypedClaimRoundTripPreservesNativeData() {
        val native = TypedClaim(
            claimId = "turn-2:claim:0",
            ownerId = "alberto",
            subject = "alberto",
            predicate = "preference.like",
            objectValue = "sushi",
            target = null,
            polarity = "POSITIVE",
            temporalRelation = "CURRENT",
            sourceType = "USER_ASSERTION",
            confidence = mapOf("overall" to 0.95, "token.negation" to 0.99),
            spans = mapOf("object" to TextSpan(10, 15)),
            perspective = "alberto",
            worldTruth = false,
        )

        val canonical = MipBridge.fromAssemblingTypedClaim(native, speakerId = "alberto", observerId = "luna")
        val roundTrip = MipBridge.toAssemblingTypedClaim(canonical)

        assertEquals(native, roundTrip)
        assertEquals(MipEntityResolutionStatus.NOT_APPLICABLE, canonical.target.resolutionStatus)
    }

    @Test
    fun entityResolutionIsDistinctFromGenericFieldPresence() {
        val entity = MipEntityRef(
            entityId = "anna",
            surfaceForm = "Anna",
            resolutionStatus = MipEntityResolutionStatus.RESOLVED,
        )

        assertEquals(MipEntityResolutionStatus.RESOLVED, entity.resolutionStatus)
        assertFailsWith<IllegalArgumentException> {
            MipEntityRef(entityId = "anna", resolutionStatus = MipEntityResolutionStatus.UNKNOWN)
        }
    }

    @Test
    fun fieldStatusVocabularyIncludesNoMatchAndErrorWithoutFakeValues() {
        val noMatch = MipField.noMatch<String>()
        val error = MipField.error<String>()

        assertEquals(MipFieldStatus.NO_MATCH, noMatch.status)
        assertEquals(MipFieldStatus.ERROR, error.status)
        assertNull(noMatch.value)
        assertNull(error.value)
    }

    @Test
    fun pythonAuthorityRoundTripPreservesContradictionId() {
        val native = PythonAuthorityResolutionWire(contradicts_memory_id = BigInteger("9223372036854775807"))

        val canonical = MipBridge.fromPythonAuthorityResolution(native)
        val roundTrip = MipBridge.toPythonAuthorityResolution(canonical)

        assertEquals(native, roundTrip)
        assertEquals("9223372036854775807", canonical.contradictedMemoryId.value)
    }

    @Test
    fun pythonAuthorityCanTranslateToKotlinMemoryDecisionWithoutFieldRenameLoss() {
        val python = PythonAuthorityResolutionWire(contradicts_memory_id = BigInteger("42"))

        val canonical = MipBridge.fromPythonAuthorityResolution(python)
        val kotlinMemory = MipBridge.toKotlinMemoryAuthorityDecision(canonical)

        assertEquals(42L, kotlinMemory.contradictedMemoryId)
    }

    @Test
    fun nullPythonContradictionRemainsKnownNoContradiction() {
        val canonical = MipBridge.fromPythonAuthorityResolution(PythonAuthorityResolutionWire(null))

        assertEquals(MipFieldStatus.NOT_APPLICABLE, canonical.contradictedMemoryId.status)
        assertNull(MipBridge.toKotlinMemoryAuthorityDecision(canonical).contradictedMemoryId)
        assertNull(MipBridge.toPythonAuthorityResolution(canonical).contradicts_memory_id)
    }

    @Test
    fun unresolvedContradictionCannotCollapseToKotlinNull() {
        val canonical = pythonProjectionCanonical(contradictedMemoryId = MipField.unresolved())

        assertFailsWith<MipContractException> {
            MipBridge.toKotlinMemoryAuthorityDecision(canonical)
        }
    }

    @Test
    fun unavailableContradictionCannotCollapseToPythonNone() {
        val canonical = pythonProjectionCanonical(contradictedMemoryId = MipField.unavailable())

        assertFailsWith<MipContractException> {
            MipBridge.toPythonAuthorityResolution(canonical)
        }
    }

    @Test
    fun pythonPartialProjectionRejectsDroppingOtherCanonicalAuthorityFields() {
        val canonical = MipAuthorityResolutionV1(
            accepted = MipField.present(true),
            ownerResolved = MipField.unavailable(),
            sourceType = MipField.unavailable(),
            conflictStatus = MipField.unavailable(),
            contradictedMemoryId = MipField.notApplicable(),
            reason = MipField.unavailable(),
        )

        assertFailsWith<MipContractException> {
            MipBridge.toPythonAuthorityResolution(canonical)
        }
    }

    @Test
    fun currentAssemblingAuthorityRoundTripWorksWhenContradictionFieldIsUnavailable() {
        val native = AuthorityDecision(
            accepted = true,
            ownerResolved = true,
            sourceType = "USER_ASSERTION",
            conflictStatus = "NONE",
            reason = "direct",
        )

        val canonical = MipBridge.fromAssemblingAuthorityDecision(native)
        val roundTrip = MipBridge.toAssemblingAuthorityDecision(canonical)

        assertEquals(native, roundTrip)
        assertEquals(MipFieldStatus.UNAVAILABLE, canonical.contradictedMemoryId.status)
    }

    @Test
    fun currentAssemblingAuthorityFailsClosedIfCanonicalContradictionWouldBeLost() {
        val canonical = MipAuthorityResolutionV1(
            accepted = MipField.present(true),
            ownerResolved = MipField.present(true),
            sourceType = MipField.present("USER_ASSERTION"),
            conflictStatus = MipField.present("CONTRADICTION"),
            contradictedMemoryId = MipField.present("7"),
            reason = MipField.present("explicit contradiction"),
        )

        assertFailsWith<MipContractException> {
            MipBridge.toAssemblingAuthorityDecision(canonical)
        }
    }

    @Test
    fun currentAssemblingAuthorityAlsoFailsClosedForKnownAbsenceItCannotRepresent() {
        val canonical = MipAuthorityResolutionV1(
            accepted = MipField.present(true),
            ownerResolved = MipField.present(true),
            sourceType = MipField.present("USER_ASSERTION"),
            conflictStatus = MipField.present("NONE"),
            contradictedMemoryId = MipField.notApplicable(),
            reason = MipField.present("known no contradiction"),
        )

        assertFailsWith<MipContractException> {
            MipBridge.toAssemblingAuthorityDecision(canonical)
        }
    }

    @Test
    fun kotlinLongOverflowFailsExplicitly() {
        val canonical = pythonProjectionCanonical(
            contradictedMemoryId = MipField.present("9223372036854775808")
        )

        assertFailsWith<MipContractException> {
            MipBridge.toKotlinMemoryAuthorityDecision(canonical)
        }
    }

    @Test
    fun authorityWireMapRoundTripIsPrimitiveAndLossless() {
        val canonical = MipAuthorityResolutionV1(
            accepted = MipField.present(false),
            ownerResolved = MipField.present(true),
            sourceType = MipField.present("REPORT"),
            conflictStatus = MipField.present("PENDING_REVIEW"),
            contradictedMemoryId = MipField.present("123"),
            reason = MipField.present("third party conflict"),
        )

        val wire = MipBridge.authorityToWireMap(canonical)
        val decoded = MipBridge.authorityFromWireMap(wire)

        assertEquals(canonical, decoded)
        assertEquals(MIP_SCHEMA_VERSION, wire["schemaVersion"])
        assertTrue(wire["accepted"] is Map<*, *>)
    }

    @Test
    fun missingWireFieldIsExplicitContractError() {
        val wire = mapOf<String, Any?>(
            "schemaVersion" to MIP_SCHEMA_VERSION,
            "accepted" to mapOf("status" to "PRESENT", "value" to true),
        )

        assertFailsWith<MipContractException> {
            MipBridge.authorityFromWireMap(wire)
        }
    }

    @Test
    fun nonPresentFieldWithValueIsRejected() {
        val wire = mapOf<String, Any?>(
            "schemaVersion" to MIP_SCHEMA_VERSION,
            "accepted" to mapOf("status" to "UNAVAILABLE", "value" to true),
            "ownerResolved" to mapOf("status" to "UNAVAILABLE", "value" to null),
            "sourceType" to mapOf("status" to "UNAVAILABLE", "value" to null),
            "conflictStatus" to mapOf("status" to "UNAVAILABLE", "value" to null),
            "contradictedMemoryId" to mapOf("status" to "NOT_APPLICABLE", "value" to null),
            "reason" to mapOf("status" to "UNAVAILABLE", "value" to null),
        )

        assertFailsWith<MipContractException> {
            MipBridge.authorityFromWireMap(wire)
        }
    }

    @Test
    fun memoryResultRoundTripPreservesBoundaryFlags() {
        val native = MemoryAdmissionResult(
            status = "NO_MEMORY_BACKEND",
            memoryIds = emptyList(),
            stableWrite = false,
            reason = "preflight only",
        )

        assertEquals(native, MipBridge.toAssemblingMemoryResult(MipBridge.fromAssemblingMemoryResult(native)))
        assertFalse(MipBridge.fromAssemblingMemoryResult(native).stableWrite)
    }

    @Test
    fun affectiveStateRoundTripDoesNotAcquireRelationshipAuthority() {
        val native = AffectiveState(
            relationshipSummary = "external relationship owner",
            affectiveSummary = "transient affect",
            persistentDeltaAllowed = false,
        )

        val canonical = MipBridge.fromAssemblingAffectiveState(native)
        val roundTrip = MipBridge.toAssemblingAffectiveState(canonical)

        assertEquals(native, roundTrip)
        assertFalse(roundTrip.persistentDeltaAllowed)
    }

    @Test
    fun coherenceEnumRoundTripIsExplicit() {
        CoherenceDecision.entries.forEach { native ->
            assertEquals(native, MipBridge.toAssemblingCoherenceDecision(MipBridge.fromAssemblingCoherenceDecision(native)))
        }
    }

    private fun pythonProjectionCanonical(
        contradictedMemoryId: MipField<String>,
    ): MipAuthorityResolutionV1 = MipAuthorityResolutionV1(
        accepted = MipField.unavailable(),
        ownerResolved = MipField.unavailable(),
        sourceType = MipField.unavailable(),
        conflictStatus = MipField.unavailable(),
        contradictedMemoryId = contradictedMemoryId,
        reason = MipField.unavailable(),
    )
}
