package io.github.joelkanyi.jenga.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaNavigationRailItemPreview() {
    JengaTheme { JengaNavigationRailItemShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaNavigationRailItemRtlPreview() {
    JengaTheme { RtlPreview { JengaNavigationRailItemShowcase() } }
}

@JengaBlockPreviews
@Composable
internal fun JengaNavigationRailItemBadgePreview() {
    JengaTheme { JengaNavigationRailItemBadgeShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaNavigationRailItemBadgeRtlPreview() {
    JengaTheme { RtlPreview { JengaNavigationRailItemBadgeShowcase() } }
}

@Composable
private fun JengaNavigationRailItemBadgeShowcase() {
    Column(modifier = Modifier.width(300.dp).background(JengaTheme.colors.background).padding(JengaTheme.spacing.md)) {
        // A badge on the selected item, then 12, 99+ and 0 (hidden) on unselected ones.
        JengaNavigationRailItem(
            label = "Alerts",
            selected = true,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
            badgeCount = 3,
        )
        JengaNavigationRailItem(
            label = "Tickets",
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Ticket, contentDescription = null) },
            badgeCount = 12,
        )
        JengaNavigationRailItem(
            label = "Work orders",
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Wrench, contentDescription = null) },
            showDot = true,
            badgeCount = 100,
        )
        JengaNavigationRailItem(
            label = "Inventory",
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Package, contentDescription = null) },
            badgeCount = 0,
        )
    }
}

@Composable
private fun JengaNavigationRailItemShowcase() {
    Column(modifier = Modifier.width(300.dp).background(JengaTheme.colors.background).padding(JengaTheme.spacing.md)) {
        JengaNavigationRailItem(
            label = "Tickets",
            selected = true,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Ticket, contentDescription = null) },
        )
        JengaNavigationRailItem(
            label = "Work orders",
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Wrench, contentDescription = null) },
            showDot = true,
        )
        JengaNavigationRailItem(
            label = "Inventory",
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Package, contentDescription = null) },
            enabled = false,
        )
    }
}
