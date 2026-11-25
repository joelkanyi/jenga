package io.github.joelkanyi.jenga.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.icon.JengaIcons
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

@JengaBlockPreviews
@Composable
internal fun JengaBannerProminentPreview() {
    JengaTheme { BannerProminentShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBannerProminentRtlPreview() {
    JengaTheme { RtlPreview { BannerProminentShowcase() } }
}

@JengaBlockPreviews
@Composable
internal fun JengaBannerProminentActionsPreview() {
    JengaTheme { BannerProminentActionsShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBannerProminentActionsRtlPreview() {
    JengaTheme { RtlPreview { BannerProminentActionsShowcase() } }
}

/** A title that wraps to two lines on a 360dp-wide phone, alone and beside an action and the ✕. */
@Preview(name = "360dp", showBackground = true, widthDp = 360)
@Composable
internal fun JengaBannerProminentNarrowPreview() {
    JengaTheme { BannerProminentNarrowShowcase() }
}

/**
 * The result of a check: positive, caution and negative with their default icons, a caller's own
 * icon, and Info with a message only.
 */
@Composable
private fun BannerProminentShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(
            message = "All products are available on a payment plan.",
            tone = JengaBannerTone.Success,
            title = "Credit approved",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "Only products up to KES 20,000.",
            tone = JengaBannerTone.Warning,
            title = "Approved for smaller systems",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "Offer a cash sale, or try again in 30 days.",
            tone = JengaBannerTone.Error,
            title = "Credit not approved",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "The name on the ID matches the customer.",
            tone = JengaBannerTone.Success,
            title = "ID matched",
            emphasis = JengaBannerEmphasis.Prominent,
            icon = JengaIcons.ShieldCheck,
        )
        JengaBanner(
            message = "Credit check in progress.",
            tone = JengaBannerTone.Info,
            emphasis = JengaBannerEmphasis.Prominent,
        )
    }
}

/**
 * A prominent banner with an action beside the text, with the ✕, with both and the action under
 * the message, and with the ✕ on a message with no title.
 */
@Composable
private fun BannerProminentActionsShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(
            message = "Only products up to KES 20,000.",
            tone = JengaBannerTone.Warning,
            title = "Approved for smaller systems",
            action = { JengaLink(text = "Why", onClick = {}) },
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "All products are available.",
            tone = JengaBannerTone.Success,
            title = "Credit approved",
            onDismiss = {},
            dismissContentDescription = "Dismiss",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "Offer a cash sale, or try again in 30 days.",
            tone = JengaBannerTone.Error,
            title = "Credit not approved",
            action = { JengaLink(text = "See the reasons", onClick = {}) },
            actionPosition = JengaBannerActionPosition.Below,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "Phone name matched.",
            tone = JengaBannerTone.Success,
            onDismiss = {},
            dismissContentDescription = "Dismiss",
            emphasis = JengaBannerEmphasis.Prominent,
        )
    }
}

@Composable
private fun BannerProminentNarrowShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(
            message = "All products are available on a payment plan.",
            tone = JengaBannerTone.Success,
            title = "Credit approved for every product in the catalogue",
            emphasis = JengaBannerEmphasis.Prominent,
        )
        JengaBanner(
            message = "Only products up to KES 20,000.",
            tone = JengaBannerTone.Warning,
            title = "Approved for smaller systems and accessories only",
            action = { JengaLink(text = "Why", onClick = {}) },
            onDismiss = {},
            dismissContentDescription = "Dismiss",
            emphasis = JengaBannerEmphasis.Prominent,
        )
    }
}
