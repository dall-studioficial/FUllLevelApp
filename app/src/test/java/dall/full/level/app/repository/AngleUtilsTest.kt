package dall.full.level.app.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class AngleUtilsTest {
    @Test
    fun preservesForwardRotationPastPositiveNinetyDegrees() {
        assertEquals(91f, unwrapAngle(91f, 90f), 0.001f)
    }

    @Test
    fun preservesReverseRotationPastNegativeNinetyDegrees() {
        assertEquals(-91f, unwrapAngle(-91f, -90f), 0.001f)
    }

    @Test
    fun unwrapsAcrossPositiveOneHundredEightyDegrees() {
        assertEquals(181f, unwrapAngle(-179f, 179f), 0.001f)
    }

    @Test
    fun unwrapsAcrossNegativeOneHundredEightyDegrees() {
        assertEquals(-181f, unwrapAngle(179f, -179f), 0.001f)
    }

    @Test
    fun keepsAnInitiallyInvertedPhoneAtOneHundredEightyDegrees() {
        assertEquals(180f, unwrapAngle(180f, 0f), 0.001f)
    }
}
