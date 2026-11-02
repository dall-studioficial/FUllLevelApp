package dall.full.level.app.repository

import kotlin.math.roundToInt

internal fun unwrapAxisAngle(angle: Float, reference: Float): Float {
    val equivalentTurns = ((reference - angle) / 180f).roundToInt()
    return angle + equivalentTurns * 180f
}
