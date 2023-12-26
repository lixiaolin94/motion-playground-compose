package work.xiaolin.motionplayground.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.ui.components.InputRangeSlider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SpringAnimationPlayground(
    modifier: Modifier = Modifier,
    dampingRatio: Float = 0.8f,
    stiffness: Float = 160f
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current

    var inputHeight by remember { mutableStateOf(0.dp) }

    val animatePropertyOptions = listOf("Size", "Offset", "Scale", "Rotate")
    var animatePropertySelectedIndex by remember { mutableStateOf(0) }

    val animationSpec = SpringSpec<Float>(dampingRatio, stiffness)

    var sizeAnimation by remember { mutableStateOf(false) }
    val widthRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 120f,
            activeRangeEnd = 320f,
            valueRange = 40f..320f
        )
    }
    val width by animateFloatAsState(
        targetValue = if (sizeAnimation) widthRangeSliderState.activeRangeEnd else widthRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "width"
    )
    val heightRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 80f,
            activeRangeEnd = 240f,
            valueRange = 40f..480f
        )
    }
    val height by animateFloatAsState(
        targetValue = if (sizeAnimation) heightRangeSliderState.activeRangeEnd else heightRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "height"
    )

    var offsetAnimation by remember { mutableStateOf(false) }
    val xRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 0f,
            activeRangeEnd = 100f,
            valueRange = -200f..200f
        )
    }
    val x by animateFloatAsState(
        targetValue = if (offsetAnimation) xRangeSliderState.activeRangeEnd else xRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "x"
    )
    val yRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 0f,
            activeRangeEnd = 100f,
            valueRange = -200f..200f
        )
    }
    val y by animateFloatAsState(
        targetValue = if (offsetAnimation) yRangeSliderState.activeRangeEnd else yRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "y"
    )

    var scaleAnimation by remember { mutableStateOf(false) }
    val scaleRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 1f,
            activeRangeEnd = 1.5f,
            valueRange = 0.2f..2f
        )
    }
    val scale by animateFloatAsState(
        targetValue = if (scaleAnimation) scaleRangeSliderState.activeRangeEnd else scaleRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "scale"
    )

    var rotateAnimation by remember { mutableStateOf(false) }
    val rotateRangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 0f,
            activeRangeEnd = 90f,
            valueRange = -360f..360f
        )
    }
    val rotate by animateFloatAsState(
        targetValue = if (rotateAnimation) rotateRangeSliderState.activeRangeEnd else rotateRangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        label = "rotate"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTapGestures {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        when (animatePropertySelectedIndex) {
                            0 -> sizeAnimation = !sizeAnimation
                            1 -> offsetAnimation = !offsetAnimation
                            2 -> scaleAnimation = !scaleAnimation
                            3 -> rotateAnimation = !rotateAnimation
                        }
                    }
                }

        ) {
            Box(modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .offset {
                    IntOffset(
                        x.dp
                            .toPx()
                            .roundToInt(),
                        y.dp
                            .toPx()
                            .roundToInt()
                    )
                }
                .scale(scale)
                .rotate(rotate)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .align(Alignment.Center)
            ) {
                Text(
                    text = stringResource(R.string.tap),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            when (animatePropertySelectedIndex) {
                0 -> {
                    InputRangeSlider(
                        state = widthRangeSliderState,
                        text = "Width Range",
                        round = 0,
                        modifier = Modifier
                            .onSizeChanged {
                                inputHeight = with(density) { it.height.toDp() }
                            }
                    )
                    InputRangeSlider(
                        state = heightRangeSliderState,
                        text = "Height Range",
                        round = 0
                    )
                }

                1 -> {
                    InputRangeSlider(state = xRangeSliderState, text = "X Range", round = 0)
                    InputRangeSlider(state = yRangeSliderState, text = "Y Range", round = 0)
                }

                2 -> {
                    Spacer(modifier = Modifier.height(inputHeight))
                    InputRangeSlider(state = scaleRangeSliderState, text = "Scale Range")
                }

                3 -> {
                    Spacer(modifier = Modifier.height(inputHeight))
                    InputRangeSlider(
                        state = rotateRangeSliderState,
                        text = "Rotate Range",
                        round = 0
                    )
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                animatePropertyOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = animatePropertyOptions.size
                        ),
                        onClick = { animatePropertySelectedIndex = index },
                        selected = index == animatePropertySelectedIndex,
                        icon = { SegmentedButtonDefaults.Icon(false) }
                    ) {
                        Text(label)
                    }
                }
            }

        }
    }
}