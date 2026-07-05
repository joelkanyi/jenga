package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import androidx.compose.ui.window.Dialog
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaDialogPreview() {
    JengaTheme { DialogShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDialogRtlPreview() {
    JengaTheme { RtlPreview { DialogShowcase() } }
}

@Composable
private fun DialogShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        // Render the dialog surface inline (the Dialog window itself can't be
        // captured in a static screenshot).
        JengaDialogSurface(
            title = "Reset device?",
            text = "This clears the cached gate session on this device. You can sign in again anytime.",
            confirmButton = {
                JengaButton(text = "Reset", onClick = {}, variant = JengaButtonVariant.Danger)
            },
            dismissButton = {
                JengaButton(text = "Cancel", onClick = {}, variant = JengaButtonVariant.Ghost)
            },
        )
    }
}
