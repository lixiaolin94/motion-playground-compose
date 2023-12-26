package work.xiaolin.motionplayground.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.utils.computeAngle
import work.xiaolin.motionplayground.utils.computeFrequencyResponse
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Preview(showBackground = true)
@Composable
fun SpringDynamics(
    modifier: Modifier = Modifier,
    dampingRatio: Float = 0.8f,
    stiffness: Float = 160f
) {
    val springOriginColor = MaterialTheme.colorScheme.outline
    val springColor = MaterialTheme.colorScheme.outlineVariant
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val animationSpec = SpringSpec<Offset>(dampingRatio, stiffness)

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawRoundRect(
                color = springOriginColor,
                size = Size(width = 64.dp.toPx(), height = 20.dp.toPx()),
                topLeft = Offset(size.width / 2 - 32.dp.toPx(), size.height / 2 - 10.dp.toPx()),
                cornerRadius = CornerRadius(4f.dp.toPx())
            )

            drawCircle(
                color = springColor,
                radius = 4.dp.toPx(),
                center = Offset(size.width / 2, size.height / 2)
            )

            val springCount = 8
            val springAmplitude = 20.dp.toPx()
            val period = offset.value.getDistance() / springCount

            val path = Path().apply {
                moveTo(0f, 0f)
                for (x in 1..springCount * period.toInt()) {
                    val y = springAmplitude * sin((2 * PI.toFloat()) / period * x)
                    lineTo(x.toFloat(), y)
                }
            }

            withTransform({
                translate(
                    left = size.width / 2,
                    top = size.height / 2
                )
                rotate(
                    degrees = computeAngle(end = offset.value),
                    pivot = Offset(0f, 0f)
                )
            }) {
                drawPath(
                    path,
                    color = springColor,
                    style = Stroke(
                        width = (2f / computeFrequencyResponse(animationSpec.stiffness)).dp.toPx(),
                        cap = StrokeCap.Round
                    ),
                )
            }
        }

        Box(modifier = Modifier
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .shadow(
                elevation = 6.dp,
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .size(100.dp * (animationSpec.dampingRatio + 0.25f))
            .align(Alignment.Center)
            .pointerInput(animationSpec) {
                coroutineScope {
                    while (true) {
                        val velocityTracker = VelocityTracker()
                        offset.stop()
                        awaitPointerEventScope {
                            val pointerId = awaitFirstDown().id
                            drag(pointerId) {
                                launch {
                                    offset.snapTo(
                                        offset.value + it.positionChange()
                                    )
                                }
                                velocityTracker.addPointerInputChange(it)
                            }
                        }
                        val velocity = velocityTracker.calculateVelocity()
                        val velocityOffset = Offset(velocity.x, velocity.y)
                        launch {
                            offset.animateTo(
                                targetValue = Offset.Zero,
                                animationSpec = animationSpec,
                                initialVelocity = velocityOffset
                            )
                        }
                    }
                }
            }
        ) {
            Text(
                text = stringResource(R.string.drag),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
