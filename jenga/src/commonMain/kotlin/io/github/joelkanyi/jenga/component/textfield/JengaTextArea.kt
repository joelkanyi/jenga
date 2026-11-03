package io.github.joelkanyi.jenga.component.textfield

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults for [JengaTextArea]. */
public object JengaTextAreaDefaults {
    /** Lines the box shows before anything is typed. */
    public const val MinLines: Int = 3

    /** Lines the box grows to before its text scrolls inside it. */
    public const val MaxLines: Int = 8
}

/**
 * A multi-line text box for comments, notes, reasons and descriptions. It starts [minLines] tall,
 * grows up to [maxLines] as the user types, then scrolls inside itself. Text and placeholder start
 * at the top. The Enter key adds a new line unless [keyboardOptions] sets an IME action.
 *
 * With a [maxLength], a counter ("120 / 512") sits under the box on the right; typing stops at the
 * limit, pasted text is cut to it, and the counter turns to the error color once the limit is reached.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTextAreaSample
 *
 * @param value the current text.
 * @param onValueChange called when the text changes.
 * @param modifier the [Modifier] for the whole field (label + box + support).
 * @param label optional label shown above the box.
 * @param placeholder optional hint shown when [value] is empty.
 * @param status validation status; see [JengaTextFieldStatus].
 * @param supportingText optional helper/error text shown below the box.
 * @param maxLength the most characters allowed; `null` means no limit and no counter.
 * @param enabled whether the box is editable.
 * @param readOnly whether the box is read-only (focusable but not editable).
 * @param minLines lines shown before anything is typed.
 * @param maxLines lines shown before the text scrolls inside the box.
 * @param keyboardOptions software-keyboard configuration.
 * @param keyboardActions IME action handlers.
 * @param shape the box shape; defaults to [JengaTextFieldDefaults.shape].
 * @param colors the color set; defaults to [JengaTextFieldDefaults.colors].
 * @param textStyle the input and placeholder text style.
 * @param contentPadding padding inside the box.
 * @param focusRingWidth width of the soft focus halo; `0.dp` removes it.
 */
@Composable
public fun JengaTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    status: JengaTextFieldStatus = JengaTextFieldStatus.Default,
    supportingText: String? = null,
    maxLength: Int? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    minLines: Int = JengaTextAreaDefaults.MinLines,
    maxLines: Int = JengaTextAreaDefaults.MaxLines,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = JengaTextFieldDefaults.shape,
    colors: JengaTextFieldColors = JengaTextFieldDefaults.colors(),
    textStyle: TextStyle = JengaTextFieldDefaults.textStyle,
    contentPadding: PaddingValues = JengaTextFieldDefaults.contentPadding(singleLine = false),
    focusRingWidth: Dp = JengaTheme.components.textField?.focusRingWidth ?: JengaTextFieldDefaults.FocusRingWidth,
) {
    val counter = maxLength?.let { TextFieldCounter(text = "${value.length} / $it", limitReached = value.length >= it) }
    JengaTextFieldLayout(
        value = value,
        onValueChange = { typed -> onValueChange(if (maxLength == null) typed else typed.take(maxLength)) },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        status = status,
        supportingText = supportingText,
        counter = counter,
        enabled = enabled,
        readOnly = readOnly,
        lines = TextFieldLines(minLines = minLines, maxLines = maxLines.coerceAtLeast(minLines)),
        leadingIcon = null,
        trailingIcon = null,
        visualTransformation = VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        look = TextFieldLook(shape, colors, textStyle, JengaTextFieldDefaults.minHeight(singleLine = false), contentPadding, focusRingWidth),
    )
}
