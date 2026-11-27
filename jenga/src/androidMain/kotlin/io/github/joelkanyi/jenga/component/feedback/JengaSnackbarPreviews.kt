package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
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

@JengaBlockPreviews
@Composable
internal fun JengaSnackbarEndPreview() {
    JengaTheme { SnackbarEndShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSnackbarEndRtlPreview() {
    JengaTheme { RtlPreview { SnackbarEndShowcase() } }
}

/** The four ends of a snackbar: none, action, close, action + close; then two lines and a tone. */
@Composable
private fun SnackbarEndShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaSnackbar(message = "Payment saved")
        JengaSnackbar(message = "Sale registered", actionLabel = "Undo", onAction = {})
        JengaSnackbar(message = "Sync failed for 2 sales", onDismiss = {}, dismissContentDescription = "Dismiss")
        JengaSnackbar(
            message = "Couldn't send the prompt",
            actionLabel = "Retry",
            onAction = {},
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaSnackbar(
            message = "Sync failed for 2 sales. They're saved on this phone and will sync later.",
            actionLabel = "Retry",
            onAction = {},
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaSnackbar(
            message = "Upload failed",
            tone = JengaSnackbarTone.Error,
            actionLabel = "Retry",
            onAction = {},
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaSnackbarLeadingPreview() {
    JengaTheme { SnackbarLeadingShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSnackbarLeadingRtlPreview() {
    JengaTheme { RtlPreview { SnackbarLeadingShowcase() } }
}

/**
 * The leading item: an icon; a counter with an action; an icon with an action and a close; an icon
 * and a counter beside two lines; then a tone with a leading item (no dot) above the same tone
 * without one (dot).
 */
@Composable
private fun SnackbarLeadingShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaSnackbar(
            message = "12 visits synced",
            leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = null) },
        )
        JengaSnackbar(
            message = "Photos still uploading",
            actionLabel = "View",
            onAction = {},
            leadingContent = { JengaBadge(text = "3") },
        )
        JengaSnackbar(
            message = "Couldn't send the prompt",
            actionLabel = "Retry",
            onAction = {},
            leadingContent = { JengaIcon(JengaIcons.Warning, contentDescription = null) },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaSnackbar(
            message = "Sync failed for 2 sales. They're saved on this phone and will sync later.",
            leadingContent = { JengaIcon(JengaIcons.CloudOff, contentDescription = null) },
        )
        JengaSnackbar(
            message = "Photos are still uploading. Keep the app open until they finish.",
            actionLabel = "View",
            onAction = {},
            leadingContent = { JengaBadge(text = "12") },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaSnackbar(
            message = "Ticket checked in",
            tone = JengaSnackbarTone.Success,
            leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = null) },
        )
        JengaSnackbar(message = "Ticket checked in", tone = JengaSnackbarTone.Success)
    }
}
