package work.xiaolin.motionplayground.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.utils.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRangeSlider(
    state: RangeSliderState,
    modifier: Modifier = Modifier,
    text: String?,
    round: Int = 3,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
    ) {
        text?.let {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)),
            )
        }
        RangeSlider(
            state = state,
            enabled = enabled
        )
        Row(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
        ) {
            InputValueWithDialog(
                value = state.activeRangeStart.round(round),
                onValueChange = { state.activeRangeStart = it },
                text = stringResource(R.string.from)
            )
            Spacer(modifier = Modifier.weight(1f))
            InputValueWithDialog(
                value = state.activeRangeEnd.round(round),
                onValueChange = { state.activeRangeEnd = it },
                text = stringResource(R.string.to)
            )
        }
    }
}