package io.github.joelkanyi.jenga.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

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
