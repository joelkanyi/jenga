package io.github.joelkanyi.jenga.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaNavigationBarPreview() {
    JengaTheme { NavBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaNavigationBarRtlPreview() {
    JengaTheme { RtlPreview { NavBarShowcase() } }
}

@Composable
private fun NavBarShowcase() {
    Column(
        modifier = Modifier.background(JengaTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        // Classic (underline/tint) indicator — the default.
        JengaNavigationBar {
            JengaNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
                label = "Events",
            )
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Check, contentDescription = null) },
                label = "Scan",
            )
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Info, contentDescription = null) },
                label = "Stats",
            )
        }
        // Material 3 pill active-indicator — opt-in per item.
        JengaNavigationBar {
            JengaNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Sun, contentDescription = null) },
                label = "Today",
                indicator = JengaNavIndicator.Pill,
            )
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
                label = "Discover",
                indicator = JengaNavIndicator.Pill,
            )
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Calendar, contentDescription = null) },
                label = "Plan",
                indicator = JengaNavIndicator.Pill,
            )
        }
    }
}
