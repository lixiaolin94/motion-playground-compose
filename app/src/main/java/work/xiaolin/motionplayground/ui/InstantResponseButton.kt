package work.xiaolin.motionplayground.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun InstantResponseButton(
    modifier: Modifier = Modifier,
    dampingRatio: Float = 0.8f,
    stiffness: Float = 160f
) {
    var count by remember { mutableStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    val estimateDuration = estimateAnimationDurationMillis(
        stiffness = stiffness,
        dampingRatio = 1f,
        initialVelocity = 0f,
        initialDisplacement = 1f / Spring.DefaultDisplacementThreshold,
        delta = 1f
    ).toInt()

    val color by animateColorAsState(
        targetValue = if (isPressed) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp)
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        },
        animationSpec = if (isPressed) tween(durationMillis = 0) else tween(
            durationMillis = estimateDuration,
            easing = EaseOut
        ),
        label = "color"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .size(64.dp)
            .background(
                color = color,
                shape = CircleShape
            )
            .align(Alignment.Center)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        count++
                    }
                )
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "Tap count: $count",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 120.dp)
        )
    }
}