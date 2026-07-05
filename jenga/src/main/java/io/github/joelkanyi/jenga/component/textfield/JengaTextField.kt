package io.github.joelkanyi.jenga.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Validation status of a [JengaTextField], driving border and supporting-text color. */
public enum class JengaTextFieldStatus { Default, Error, Success }

/** Defaults and token mappings for [JengaTextField]. */
public object JengaTextFieldDefaults {
    /** Default field shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.control
}

/**
 * A single- or multi-line text input.
 *
 * Built on Compose Foundation's `BasicTextField`, so its look is governed
 * entirely by Jenga tokens. The border reflects focus and [status]; an optional
 * [supportingText] sits below and is colored to match the status.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTextFieldSample
 *
 * @param value the current text.
 * @param onValueChange called when the text changes.
 * @param modifier the [Modifier] for the whole field (label + box + support).
 * @param label optional label shown above the field.
 * @param placeholder optional hint shown when [value] is empty.
 * @param status validation status; see [JengaTextFieldStatus].
 * @param supportingText optional helper/error text shown below the field.
 * @param enabled whether the field is editable.
 * @param readOnly whether the field is read-only (focusable but not editable).
 * @param singleLine whether the field is constrained to one line.
 * @param leadingIcon optional icon at the start (inherits the content color).
 * @param trailingIcon optional icon at the end (inherits the content color).
 * @param visualTransformation transforms the displayed text (e.g. password).
 * @param keyboardOptions software-keyboard configuration.
 * @param keyboardActions IME action handlers.
 * @param shape the field shape; defaults to [JengaTextFieldDefaults.shape].
 */
@Composable
public fun JengaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    status: JengaTextFieldStatus = JengaTextFieldStatus.Default,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = JengaTextFieldDefaults.shape,
) {
    val c = JengaTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        !enabled -> c.borderDisabled
        status == JengaTextFieldStatus.Error -> c.error
        status == JengaTextFieldStatus.Success -> c.success
        focused -> c.brand
        else -> c.borderStrong
    }
    val borderWidth = if (focused || status != JengaTextFieldStatus.Default) 2.dp else 1.dp
    val container = if (enabled) c.surface else c.surfaceDisabled
    val contentColor = if (enabled) c.textPrimary else c.contentDisabled

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
    ) {
        if (label != null) {
            JengaText(
                text = label,
                style = JengaTheme.typography.bodySmall,
                color = c.textSecondary,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = JengaTheme.typography.bodyMedium.copy(color = contentColor),
            cursorBrush = SolidColor(c.brand),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        // Soft focus halo that hugs the field — Revolut's "color +
                        // shadow" ring rendered as a translucent fill (not a second
                        // hard stroke). 3dp is reserved always so focus doesn't shift layout.
                        .clip(shape)
                        .background(if (focused) c.focusRing else Color.Transparent)
                        .padding(3.dp)
                        .clip(shape)
                        .background(container)
                        .border(borderWidth, borderColor, shape)
                        .defaultMinSize(minHeight = JengaTheme.sizing.fieldHeight)
                        .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompositionLocalProvider(LocalJengaContentColor provides c.textMuted) {
                        leadingIcon?.invoke()
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            JengaText(
                                text = placeholder,
                                style = JengaTheme.typography.bodyMedium,
                                color = c.textFaint,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                    CompositionLocalProvider(LocalJengaContentColor provides c.textMuted) {
                        trailingIcon?.invoke()
                    }
                }
            },
        )

        if (supportingText != null) {
            val supportColor = when (status) {
                JengaTextFieldStatus.Error -> c.error
                JengaTextFieldStatus.Success -> c.success
                JengaTextFieldStatus.Default -> c.textMuted
            }
            JengaText(
                text = supportingText,
                style = JengaTheme.typography.caption,
                color = supportColor,
            )
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTextFieldPreview() {
    JengaTheme { TextFieldShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTextFieldRtlPreview() {
    JengaTheme { RtlPreview { TextFieldShowcase() } }
}

@Composable
private fun TextFieldShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaTextField(
            value = "",
            onValueChange = {},
            label = "Email",
            placeholder = "you@example.com",
        )
        JengaTextField(
            value = "TKT-2026-001",
            onValueChange = {},
            label = "Ticket code",
            status = JengaTextFieldStatus.Success,
            supportingText = "Valid ticket",
        )
        JengaTextField(
            value = "bad-code",
            onValueChange = {},
            label = "Ticket code",
            status = JengaTextFieldStatus.Error,
            supportingText = "Ticket not found",
        )
        JengaTextField(
            value = "",
            onValueChange = {},
            label = "Disabled",
            placeholder = "Unavailable",
            enabled = false,
        )
    }
}
