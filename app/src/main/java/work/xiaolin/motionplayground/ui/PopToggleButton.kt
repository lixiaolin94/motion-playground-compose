package work.xiaolin.motionplayground.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.ui.components.InputRangeSlider

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PopToggleButton(
    modifier: Modifier = Modifier,
    dampingRatio: Float = 0.8f,
    stiffness: Float = 160f
) {
    val rangeSliderState = remember {
        RangeSliderState(
            activeRangeStart = 1f,
            activeRangeEnd = 1.5f,
            valueRange = 0.2f..2f
        )
    }

    val hapticFeedback = LocalHapticFeedback.current

    var isPressed by remember { mutableStateOf(false) }
    var isActivated by remember { mutableStateOf(false) }
    var isToggled by remember { mutableStateOf(false) }

    val animationSpec = SpringSpec<Float>(dampingRatio, stiffness)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) rangeSliderState.activeRangeEnd else rangeSliderState.activeRangeStart,
        animationSpec = animationSpec,
        finishedListener = {
            if (isPressed) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                isActivated = true
                isToggled = !isToggled
            }
        },
        label = "scale"
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
        ) {
            Box(modifier = Modifier
                .size(64.dp)
                .scale(scale)
                .background(
                    color = if (isToggled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .align(Alignment.Center)
                .pointerInput(animationSpec) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            if (isActivated) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isActivated = false
                            }
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = if (isToggled) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isToggled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Text(
                text = stringResource(R.string.press),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 120.dp)
            )
        }

        InputRangeSlider(
            state = rangeSliderState,
            text = stringResource(R.string.scale_range)
        )
    }
}
