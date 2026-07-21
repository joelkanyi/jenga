package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaSnackbarPreview() {
    JengaTheme { SnackbarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSnackbarRtlPreview() {
    JengaTheme { RtlPreview { SnackbarShowcase() } }
}

@Composable
private fun SnackbarShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaSnackbar(message = "Ticket checked in", tone = JengaSnackbarTone.Success)
        JengaSnackbar(message = "Already scanned", tone = JengaSnackbarTone.Error)
        JengaSnackbar(
            message = "Saved offline",
            tone = JengaSnackbarTone.Neutral,
            actionLabel = "Undo",
            onAction = {},
        )
    }
}
