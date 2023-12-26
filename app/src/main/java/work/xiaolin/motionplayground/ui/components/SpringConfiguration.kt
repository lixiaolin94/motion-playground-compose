package work.xiaolin.motionplayground.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.utils.computeFrequencyResponse
import work.xiaolin.motionplayground.utils.computeStiffness
import work.xiaolin.motionplayground.utils.round

@Composable
fun SpringConfiguration(
    dampingRatio: Float,
    stiffness: Float,
    onDampingRatioChange: (Float) -> Unit,
    onStiffnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical
) {
    var designFriendlyProperties by remember { mutableStateOf(true) }
    var bounce by remember { mutableFloatStateOf((1f - dampingRatio).round(3)) }
    var duration by remember { mutableFloatStateOf(computeFrequencyResponse(stiffness).round(3)) }

    val estimateDuration = estimateAnimationDurationMillis(
        stiffness = stiffness,
        dampingRatio = dampingRatio,
        initialVelocity = 0f,
        initialDisplacement = 1f,
        delta = Spring.DefaultDisplacementThreshold
    )

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        Text(
            text = "Spring Configuration",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_small)),
        )

        Row(
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_small))
                .padding(vertical = dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.design_friendly_properties))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = designFriendlyProperties,
                onCheckedChange = { designFriendlyProperties = it })
        }

        if (designFriendlyProperties) {
            InputSlider(
                text = stringResource(R.string.bounce),
                value = bounce,
                onValueChange = {
                    bounce = it.round(3)
                    onDampingRatioChange(1f - bounce)
                },
                valueRange = -1f..1f
            )

            InputSlider(
                text = stringResource(R.string.duration),
                value = duration,
                onValueChange = {
                    duration = it.round(3)
                    onStiffnessChange(computeStiffness(duration).round(3))
                },
                valueRange = 0f..3f
            )
        } else {
            InputSlider(
                text = stringResource(R.string.damping_ratio),
                value = dampingRatio,
                onValueChange = {
                    bounce = 1f - it.round(3)
                    onDampingRatioChange(it.round(3))
                },
                valueRange = 0f..2f
            )

            InputSlider(
                text = stringResource(R.string.stiffness),
                value = stiffness,
                onValueChange = {
                    duration = computeFrequencyResponse(it.round(3))
                    onStiffnessChange(it.round(3))
                },
                valueRange = 0f..1000f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
        ) {
            Text(text = stringResource(R.string.estimate_duration))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = if (dampingRatio == 0f) "Infinite" else "${estimateDuration / 1000f}s")
        }

        SpringGraph(springSpec = SpringSpec(dampingRatio, stiffness))
    }
}