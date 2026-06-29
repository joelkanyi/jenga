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

/**
 * A modal dialog with an optional title and body, plus confirm/dismiss actions.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaDialogSample
 *
 * @param onDismissRequest called when the user dismisses (scrim tap or back).
 * @param confirmButton the primary action (e.g. a [JengaButton]).
 * @param modifier the [Modifier] for the dialog surface.
 * @param title optional title.
 * @param text optional body text.
 * @param dismissButton optional secondary action.
 */
@Composable
public fun JengaDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        JengaDialogSurface(
            confirmButton = confirmButton,
            modifier = modifier,
            title = title,
            text = text,
            dismissButton = dismissButton,
        )
    }
}

@Composable
private fun JengaDialogSurface(
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(JengaTheme.shapes.card)
            .background(JengaTheme.colors.surface)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        if (title != null) {
            JengaText(text = title, style = JengaTheme.typography.headingSmall)
        }
        if (text != null) {
            JengaText(
                text = text,
                style = JengaTheme.typography.bodyMedium,
                color = JengaTheme.colors.textMuted,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm, Alignment.End),
        ) {
            if (dismissButton != null) dismissButton()
            confirmButton()
        }
    }
}

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
