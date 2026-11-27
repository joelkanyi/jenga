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

@JengaBlockPreviews
@Composable
internal fun JengaNavigationBarBadgePreview() {
    JengaTheme { NavBarBadgeShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaNavigationBarBadgeRtlPreview() {
    JengaTheme { RtlPreview { NavBarBadgeShowcase() } }
}

@Composable
private fun NavBarBadgeShowcase() {
    Column(
        modifier = Modifier.background(JengaTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        // Pill indicator: a badge on the selected item, then 12, 99+ and 0 (hidden) on unselected ones.
        BadgeBar(indicator = JengaNavIndicator.Pill, selectedCount = 3)
        // Classic indicator, same counts.
        BadgeBar(indicator = JengaNavIndicator.None, selectedCount = 3)
        // The same bar with no badge on the selected item, to compare icon and label positions.
        BadgeBar(indicator = JengaNavIndicator.Pill, selectedCount = 0)
        // Five items, 99+ on the last one: the badge stays inside the item, so the edge does not cut it.
        JengaNavigationBar {
            listOf(JengaIcons.Sun, JengaIcons.Search, JengaIcons.Calendar, JengaIcons.Ticket).forEachIndexed { index, itemIcon ->
                JengaNavigationBarItem(
                    selected = index == 0,
                    onClick = {},
                    icon = { JengaIcon(itemIcon, contentDescription = null) },
                    label = "Item ${index + 1}",
                    indicator = JengaNavIndicator.Pill,
                )
            }
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                label = "Alerts",
                indicator = JengaNavIndicator.Pill,
                badgeCount = 100,
            )
        }
    }
}

@Composable
private fun BadgeBar(indicator: JengaNavIndicator, selectedCount: Int) {
    JengaNavigationBar {
        JengaNavigationBarItem(
            selected = true,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
            label = "Alerts",
            indicator = indicator,
            badgeCount = selectedCount,
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Ticket, contentDescription = null) },
            label = "Tickets",
            indicator = indicator,
            badgeCount = 12,
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Package, contentDescription = null) },
            label = "Stock",
            indicator = indicator,
            badgeCount = 100,
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
            label = "Search",
            indicator = indicator,
            badgeCount = 0,
        )
    }
}

@Composable
private fun NavBarShowcase() {
    Column(
        modifier = Modifier.background(JengaTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        // Classic (underline/tint) indicator: the default.
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
        // Material 3 pill active-indicator: opt-in per item.
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
