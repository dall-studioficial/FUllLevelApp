package dall.full.level.app.repository

import kotlin.math.roundToInt

internal fun unwrapAngle(angle: Float, reference: Float): Float {
    val equivalentTurns = ((reference - angle) / 360f).roundToInt()
    return angle + equivalentTurns * 360f
}
