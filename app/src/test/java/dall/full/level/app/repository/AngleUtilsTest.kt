package dall.full.level.app.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class AngleUtilsTest {
    @Test
    fun unwrapsAcrossPositiveNinetyDegreesWithoutFlipping() {
        assertEquals(91f, unwrapAxisAngle(-89f, 90f), 0.001f)
    }

    @Test
    fun unwrapsAcrossNegativeNinetyDegreesWithoutFlipping() {
        assertEquals(-91f, unwrapAxisAngle(89f, -90f), 0.001f)
    }

    @Test
    fun keepsPortraitReferenceNearZero() {
        assertEquals(0f, unwrapAxisAngle(180f, 0f), 0.001f)
    }
}
