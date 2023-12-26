package work.xiaolin.motionplayground.ui.components

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.utils.computeSpringMaximum
import work.xiaolin.motionplayground.utils.normalize
import work.xiaolin.motionplayground.utils.round
import work.xiaolin.motionplayground.utils.springSolver
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun SpringGraph(
    modifier: Modifier = Modifier,
    springSpec: SpringSpec<Float> = SpringSpec(0.25f, 100f)
) {
    val guideColor = MaterialTheme.colorScheme.outlineVariant
    val centerColor = MaterialTheme.colorScheme.onSurfaceVariant
    val maxColor = MaterialTheme.colorScheme.surfaceTint
    val curveColor = MaterialTheme.colorScheme.primary

    var maxValue by remember { mutableStateOf("") }
    var maxOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = dimensionResource(R.dimen.padding_medium))
    ) {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(2f)

        ) {
            val range = size.height * 0.5f
            val mapX = { x: Float -> normalize(0f, range, x) }
            val mapY = { y: Float -> lerp(range, 0f, y) }

            drawLine(
                start = Offset(0f, mapY(-1f)),
                end = Offset(size.width, mapY(-1f)),
                color = guideColor
            )

            drawLine(
                start = Offset(0f, mapY(0f)),
                end = Offset(size.width, mapY(0f)),
                color = centerColor
            )

            drawLine(
                start = Offset(0f, mapY(1f)),
                end = Offset(size.width, mapY(1f)),
                color = guideColor
            )

            val formula = springSolver(springSpec.dampingRatio, springSpec.stiffness)

            val max = computeSpringMaximum(springSpec.stiffness, springSpec.dampingRatio, formula)
            maxValue = "Max: ${(max * 100f).round(1)} %"

            val maxY = mapY(max - 1f)
            maxOffset = maxY

            drawLine(
                start = Offset(0f, maxY),
                end = Offset(size.width, maxY),
                color = maxColor,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            val path = Path().apply {
                moveTo(0f, mapY(-1f))
                for (x in 1..size.width.toInt()) {
                    val cx = mapX(x.toFloat())
                    val cy = formula(cx) - 1f
                    val y = mapY(cy)
                    lineTo(x.toFloat(), y)
                }
            }

            drawPath(
                path,
                color = curveColor,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Text(
            text = maxValue,
            color = maxColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = dimensionResource(R.dimen.padding_small))
                .offset { IntOffset(0, maxOffset.roundToInt()) }
        )
    }
}