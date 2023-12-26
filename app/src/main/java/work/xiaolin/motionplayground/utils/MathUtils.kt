package work.xiaolin.motionplayground.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt

fun Float.round(decimals: Int): Float {
    val multiplier = 10f.pow(decimals)
    return (this * multiplier).roundToInt() / multiplier
}

fun normalize(start: Float, stop: Float, value: Float): Float {
    return (value - start) / (stop - start)
}

fun computeAngle(start: Offset = Offset.Zero, end: Offset): Float {
    val angle = Math.toDegrees(
        kotlin.math.atan2(
            (end.y - start.y).toDouble(),
            (end.x - start.x).toDouble()
        )
    ).toFloat()
    return if (angle < 0) angle + 360 else angle
}

fun computeNaturalFrequencyByStiffness(stiffness: Float): Float {
    return sqrt(stiffness)
}

fun computeNaturalFrequencyByFrequencyResponse(frequencyResponse: Float): Float {
    return (2 * PI / frequencyResponse).toFloat()
}

fun computeStiffness(frequencyResponse: Float): Float {
    return computeNaturalFrequencyByFrequencyResponse(frequencyResponse).pow(2)
}

fun computeFrequencyResponse(stiffness: Float): Float {
    return (2 * PI / computeNaturalFrequencyByStiffness(stiffness)).toFloat()
}

fun computeAngularFrequency(naturalFrequency: Float, dampingRatio: Float): Float {
    return naturalFrequency * sqrt(abs(1 - dampingRatio * dampingRatio))
}

fun computeSpringMaximum(stiffness: Float, dampingRatio: Float, spring: (Float) -> Float): Float {
    return when {
        dampingRatio == 0f -> 2f
        dampingRatio >= 1f -> 1f
        else -> {
            val frequencyResponse = computeFrequencyResponse(stiffness)
            val t = frequencyResponse / (2 * sqrt(1 - dampingRatio * dampingRatio))
            spring(t)
        }
    }
}

fun springSolver(dampingRatio: Float, stiffness: Float): (Float) -> Float {
    val from = 0
    val to = 1

    val w = computeNaturalFrequencyByStiffness(stiffness)
    val z = dampingRatio
    val v = 0
    val s = from - to
    val c = w * z
    val a = computeAngularFrequency(w, z)

    return when {
        z < 1 -> {
            val A = s
            val B = (v + c * s) / a
            { t -> to + exp(-c * t) * (A * cos(a * t) + B * sin(a * t)) }
        }

        z == 1f -> {
            val A = s
            val B = v + c * s
            { t -> to + exp(-c * t) * (A + B * t) }
        }

        else -> {
            val A = a * s
            val B = v + c * s
            { t -> to + (exp(-c * t) * (A * cosh(a * t) + B * sinh(a * t))) / a }
        }
    }
}

