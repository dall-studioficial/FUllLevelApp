package dall.full.level.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ClinometerDialTest {
    @Test
    fun normalizesNeedleAngleAcrossFullTurns() {
        assertEquals(0f, normalizeNeedleAngle(0f), 0.001f)
        assertEquals(180f, normalizeNeedleAngle(180f), 0.001f)
        assertEquals(0f, normalizeNeedleAngle(360f), 0.001f)
        assertEquals(180f, normalizeNeedleAngle(-180f), 0.001f)
    }

    @Test
    fun preservesDirectionPastNinetyDegrees() {
        assertEquals(89f, normalizeNeedleAngle(89f), 0.001f)
        assertEquals(90f, normalizeNeedleAngle(90f), 0.001f)
        assertEquals(91f, normalizeNeedleAngle(91f), 0.001f)
        assertEquals(135f, normalizeNeedleAngle(135f), 0.001f)
        assertEquals(225f, normalizeNeedleAngle(225f), 0.001f)
    }

    @Test
    fun normalizesNegativeAnglesWithoutFoldingAtNinety() {
        assertEquals(45f, normalizeNeedleAngle(45f), 0.001f)
        assertEquals(315f, normalizeNeedleAngle(-45f), 0.001f)
    }
}
