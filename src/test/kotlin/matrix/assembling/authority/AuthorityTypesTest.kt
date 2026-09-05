package matrix.assembling.authority

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AuthorityTypesTest {

    @Test
    fun epistemicClassExactlyMatchesFrozenAuthority10Vocabulary() {
        assertEquals(
            listOf("WORLD_TRUTH", "OBSERVATION", "REPORT", "INFERENCE", "BELIEF"),
            EpistemicClass.entries.map { it.name },
        )
    }

    @Test
    fun resolutionStatusExactlyMatchesFrozenAuthority10Vocabulary() {
        assertEquals(
            listOf("COMPLETE", "PARTIAL", "HOLD", "UNAVAILABLE", "ERROR"),
            AuthorityResolutionStatus.entries.map { it.name },
        )
    }

    @Test
    fun authorityResolutionConfidenceAcceptsOnlyNormalizedFiniteRange() {
        assertEquals(0.0, AuthorityResolutionConfidence(0.0).value)
        assertEquals(0.5, AuthorityResolutionConfidence(0.5).value)
        assertEquals(1.0, AuthorityResolutionConfidence(1.0).value)

        assertFailsWith<IllegalArgumentException> { AuthorityResolutionConfidence(-0.0001) }
        assertFailsWith<IllegalArgumentException> { AuthorityResolutionConfidence(1.0001) }
        assertFailsWith<IllegalArgumentException> { AuthorityResolutionConfidence(Double.NaN) }
    }

    @Test
    fun sourceReliabilityIsIndependentNormalizedValue() {
        assertEquals(0.0, SourceReliability(0.0).value)
        assertEquals(0.75, SourceReliability(0.75).value)
        assertEquals(1.0, SourceReliability(1.0).value)

        assertFailsWith<IllegalArgumentException> { SourceReliability(-1.0) }
        assertFailsWith<IllegalArgumentException> { SourceReliability(2.0) }
        assertFailsWith<IllegalArgumentException> { SourceReliability(Double.NaN) }
    }

    @Test
    fun memoryRefIsOpaqueAndMustNotBeBlank() {
        assertEquals("42", MemoryRef("42").value)
        assertEquals("memory:alpha:v3", MemoryRef("memory:alpha:v3").value)

        assertFailsWith<IllegalArgumentException> { MemoryRef("") }
        assertFailsWith<IllegalArgumentException> { MemoryRef("   ") }
    }

    @Test
    fun frozenReasonCodesRemainInAuthorityNamespaceWithoutDuplicates() {
        assertEquals(19, AuthorityReasonCode.frozenV1.size)
        assertTrue(AuthorityReasonCode.frozenV1.all(AuthorityReasonCode::isAuthorityCode))
        assertTrue(AuthorityReasonCode.CONTRADICTION_IDENTIFIED in AuthorityReasonCode.frozenV1)
        assertTrue(AuthorityReasonCode.CONTRADICTION_NONE in AuthorityReasonCode.frozenV1)
        assertTrue(AuthorityReasonCode.RETRIEVAL_NO_MATCH in AuthorityReasonCode.frozenV1)
        assertTrue(AuthorityReasonCode.RETRIEVAL_UNAVAILABLE in AuthorityReasonCode.frozenV1)
    }
}
