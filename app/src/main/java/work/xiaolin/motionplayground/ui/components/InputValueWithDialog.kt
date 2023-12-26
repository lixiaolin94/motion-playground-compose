package work.xiaolin.motionplayground.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun InputValueWithDialog(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle = LocalTextStyle.current
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf(TextFieldValue(value.toString())) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            focusRequester.requestFocus()
            dialogInput = TextFieldValue(value.toString())
            dialogInput = dialogInput.copy(selection = TextRange(0, dialogInput.text.length))
        }
    }

    Text(
        text = value.toString(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        style = style,
        modifier = modifier
            .clickable {
                showDialog = true
            }
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                text?.let {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    )
                }
            },
            text = {
                OutlinedTextField(
                    value = dialogInput,
                    onValueChange = { dialogInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onValueChange(dialogInput.text.toFloatOrNull() ?: value)
                            dialogInput = TextFieldValue("")
                            showDialog = false
                        }
                    ),
                    modifier = Modifier.focusRequester(focusRequester)
                )
            },
            confirmButton = {
                Button(onClick = {
                    onValueChange(dialogInput.text.toFloatOrNull() ?: value)
                    dialogInput = TextFieldValue("")
                    showDialog = false
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    dialogInput = TextFieldValue("")
                    showDialog = false
                }) {
                    Text(text = "Cancel")
                }
            },
        )
    }
}