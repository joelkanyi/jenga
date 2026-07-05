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
