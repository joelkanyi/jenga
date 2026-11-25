package io.github.joelkanyi.jenga.component.state

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStatesPreview() {
    JengaTheme { StatesShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatesRtlPreview() {
    JengaTheme { RtlPreview { StatesShowcase() } }
}

@Composable
private fun StatesShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxl),
    ) {
        JengaEmptyState(
            title = "No scans yet",
            description = "Validated tickets will appear here as attendees check in.",
            actionLabel = "Scan a ticket",
            onAction = {},
        )
        JengaErrorState(
            title = "Couldn't load gates",
            description = "Check your connection and try again.",
            actionLabel = "Retry",
            onAction = {},
        )
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaEmptyStateStylesPreview() {
    JengaTheme {
        JengaEmptyState(
            title = "No work orders yet",
            modifier = Modifier.background(JengaTheme.colors.background),
            description = "New work orders show up here.",
            icon = { JengaIcon(JengaIcons.Package, contentDescription = null, size = 40.dp) },
            actionLabel = "Refresh",
            onAction = {},
            titleStyle = JengaTheme.typography.titleMedium,
            descriptionStyle = JengaTheme.typography.bodySmall,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 40.dp),
            spacing = 8.dp,
            actionSpacing = 10.dp,
            actionSize = JengaButtonSize.Small,
        )
    }
}

// ---- Second action ---------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStatesTwoActionsPreview() {
    JengaTheme { TwoActionsShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatesTwoActionsRtlPreview() {
    JengaTheme { RtlPreview { TwoActionsShowcase() } }
}

@Composable
private fun TwoActionsShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxl),
    ) {
        JengaEmptyState(
            title = "No scans yet",
            description = "Validated tickets will appear here as attendees check in.",
            actionLabel = "Scan a ticket",
            onAction = {},
            secondaryActionLabel = "Enter a code instead",
            onSecondaryAction = {},
        )
        JengaErrorState(
            title = "Couldn't load gates",
            description = "Check your connection and try again.",
            actionLabel = "Try again",
            onAction = {},
            secondaryActionLabel = "Contact support",
            onSecondaryAction = {},
        )
    }
}

// ---- Fill screen -----------------------------------------------------------

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Dark", showBackground = true, widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.5f)
@Composable
internal fun JengaErrorStateFillScreenPreview() {
    JengaTheme { NoConnectionFillScreen() }
}

@Preview(name = "RTL", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
internal fun JengaErrorStateFillScreenRtlPreview() {
    JengaTheme { RtlPreview { NoConnectionFillScreen() } }
}

/**
 * A landscape phone at a large font: the message is cut where its scroll area ends, a hairline marks
 * that more is below, and the buttons stay in view.
 */
@Preview(name = "Short large font", showBackground = true, widthDp = 640, heightDp = 306, fontScale = 1.5f)
@Composable
internal fun JengaErrorStateFillScreenOverflowPreview() {
    JengaTheme { NoConnectionFillScreen() }
}

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
internal fun JengaEmptyStateFillScreenOneActionPreview() {
    JengaTheme {
        JengaEmptyState(
            title = "No work orders yet",
            modifier = Modifier.background(JengaTheme.colors.background),
            description = "New work orders show up here.",
            icon = { JengaIcon(JengaIcons.Package, contentDescription = null, size = 40.dp) },
            actionLabel = "Refresh",
            onAction = {},
            layout = JengaStateLayout.FillScreen,
        )
    }
}

@Composable
private fun NoConnectionFillScreen() {
    JengaErrorState(
        title = "No internet connection",
        modifier = Modifier.background(JengaTheme.colors.background),
        description = "The app needs internet the first time you sign in. Check mobile data or Wi-Fi.",
        icon = { JengaIcon(JengaIcons.CloudOff, contentDescription = null, size = 40.dp) },
        actionLabel = "Try again",
        onAction = {},
        secondaryActionLabel = "Contact support",
        onSecondaryAction = {},
        layout = JengaStateLayout.FillScreen,
    )
}
