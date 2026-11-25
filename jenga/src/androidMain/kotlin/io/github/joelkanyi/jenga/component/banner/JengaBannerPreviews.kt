package io.github.joelkanyi.jenga.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.link.JengaLink
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBannerPreview() {
    JengaTheme { BannerShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBannerRtlPreview() {
    JengaTheme { RtlPreview { BannerShowcase() } }
}

@Composable
private fun BannerShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(message = "Working offline; scans will sync later.", tone = JengaBannerTone.Info)
        JengaBanner(message = "All tickets validated.", tone = JengaBannerTone.Success, title = "Done")
        JengaBanner(message = "Low battery on this device.", tone = JengaBannerTone.Warning)
        JengaBanner(message = "Couldn't reach the server.", tone = JengaBannerTone.Error)
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaBannerDismissPreview() {
    JengaTheme { BannerDismissShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBannerDismissRtlPreview() {
    JengaTheme { RtlPreview { BannerDismissShowcase() } }
}

/** Action + ✕ on a 360dp-wide phone, the narrowest the text column gets. */
@Preview(name = "360dp", showBackground = true, widthDp = 360)
@Composable
internal fun JengaBannerDismissNarrowPreview() {
    JengaTheme { BannerDismissNarrowShowcase() }
}

/**
 * The close ✕: alone on Info and Success, after an action, on a banner with a title and a
 * message that wraps, and with the action under the message. Kept short enough to fit the
 * screen at the large font scale.
 */
@Composable
private fun BannerDismissShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(
            message = "Credit check runs after the deposit.",
            tone = JengaBannerTone.Info,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "Deposit confirmed.",
            tone = JengaBannerTone.Success,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "Deposit confirmed.",
            tone = JengaBannerTone.Success,
            action = { JengaLink(text = "Receipt", onClick = {}) },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "KES 3,000 received from 0712 345 678. The customer's balance and the next due date have been updated.",
            tone = JengaBannerTone.Success,
            title = "Deposit confirmed",
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "You can register the sale now; we'll notify you.",
            tone = JengaBannerTone.Info,
            title = "Credit check runs after the deposit",
            action = { JengaLink(text = "View details", onClick = {}) },
            actionPosition = JengaBannerActionPosition.Below,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
    }
}

/**
 * A two-word action beside the text leaves it about 15 characters a line; a one-word action reads
 * better; [JengaBannerActionPosition.Below] gives the text the whole width.
 */
@Composable
private fun BannerDismissNarrowShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(
            message = "You can register the sale now; we'll notify you.",
            tone = JengaBannerTone.Info,
            title = "Credit check runs after the deposit",
            action = { JengaLink(text = "View details", onClick = {}) },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "You can register the sale now; we'll notify you.",
            tone = JengaBannerTone.Info,
            title = "Credit check runs after the deposit",
            action = { JengaLink(text = "Details", onClick = {}) },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "You can register the sale now; we'll notify you.",
            tone = JengaBannerTone.Info,
            title = "Credit check runs after the deposit",
            action = { JengaLink(text = "View details", onClick = {}) },
            actionPosition = JengaBannerActionPosition.Below,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
        )
        JengaBanner(
            message = "KES 3,000 received from 0712 345 678.",
            tone = JengaBannerTone.Success,
            title = "Deposit confirmed",
            action = { JengaLink(text = "View receipt", onClick = {}) },
            actionPosition = JengaBannerActionPosition.Below,
        )
    }
}
