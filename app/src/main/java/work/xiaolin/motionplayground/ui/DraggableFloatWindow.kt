package work.xiaolin.motionplayground.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.ui.components.InputSlider
import work.xiaolin.motionplayground.utils.round
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun DraggableFloatWindow(
    modifier: Modifier = Modifier,
    dampingRatio: Float = 0.8f,
    stiffness: Float = 160f
) {
    val density = LocalDensity.current
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var windowSize by remember { mutableStateOf(Size.Zero) }

    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    var velocityResponseFactor by remember { mutableStateOf(1f) }
    var debugInfo by remember { mutableStateOf("") }

    val animationSpec = SpringSpec<Offset>(dampingRatio, stiffness)

    Box(modifier = modifier
        .fillMaxSize()
        .padding(dimensionResource(R.dimen.padding_medium))
        .onSizeChanged { containerSize = it.toSize() }
    ) {

        val paddingTop = (containerSize.height * 0.5f) / density.density
        Column(modifier = Modifier.padding(top = paddingTop.dp)) {
            InputSlider(
                value = velocityResponseFactor,
                onValueChange = { velocityResponseFactor = it.round(3) },
                text = stringResource(R.string.velocity_response_factor),
                valueRange = 0f..1f,
            )

            Text(
                text = debugInfo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)),
            )
        }

        Box(modifier = Modifier
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .onSizeChanged { windowSize = it.toSize() }
            .shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .size(
                width = dimensionResource(R.dimen.float_window_width),
                height = dimensionResource(R.dimen.float_window_height)
            )
            .align(Alignment.BottomEnd)
            .pointerInput(animationSpec) {
                val decay = splineBasedDecay<Offset>(this)

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
                        val velocityOffset = Offset(velocity.x, velocity.y) * velocityResponseFactor

                        val targetOffset = decay.calculateTargetValue(
                            typeConverter = Offset.VectorConverter,
                            initialValue = offset.value,
                            initialVelocity = velocityOffset
                        )

                        debugInfo =
                            "Gesture release velocity:\n  ├ x: ${velocity.x}\n  └ y: ${velocity.y}\n\nProjection offset target:\n  ├ x: ${targetOffset.x}\n  └ y: ${targetOffset.y}"

                        val distanceX = containerSize.width - windowSize.width
                        val targetValueX = if (targetOffset.x < -distanceX * 0.5) {
                            -distanceX
                        } else {
                            0f
                        }

                        val distanceY = containerSize.height - windowSize.height
                        val targetValueY = if (targetOffset.y < -distanceY) {
                            -distanceY
                        } else if (targetOffset.y > 0) {
                            0f
                        } else {
                            targetOffset.y
                        }

                        launch {
                            offset.animateTo(
                                targetValue = Offset(targetValueX, targetValueY),
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