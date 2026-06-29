@file:Suppress("unused", "UNUSED_PARAMETER")

package io.github.joelkanyi.jenga.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.joelkanyi.jenga.component.avatar.JengaAvatar
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.banner.JengaBanner
import io.github.joelkanyi.jenga.component.banner.JengaBannerTone
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.chip.JengaChip
import io.github.joelkanyi.jenga.component.fab.JengaFab
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenu
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenuItem
import io.github.joelkanyi.jenga.component.tooltip.JengaTooltip
import io.github.joelkanyi.jenga.component.divider.JengaDivider
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.component.layout.JengaGrid
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaSection
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.layout.JengaWrap
import io.github.joelkanyi.jenga.component.list.JengaListItem
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBar
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBarItem
import io.github.joelkanyi.jenga.component.search.JengaSearchField
import io.github.joelkanyi.jenga.component.selection.JengaRadioButton
import io.github.joelkanyi.jenga.component.slider.JengaSlider
import io.github.joelkanyi.jenga.component.tabs.JengaSegmentedControl
import io.github.joelkanyi.jenga.component.tabs.JengaTabs
import io.github.joelkanyi.jenga.component.feedback.JengaBottomSheet
import io.github.joelkanyi.jenga.component.feedback.JengaDialog
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbar
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbarTone
import io.github.joelkanyi.jenga.component.progress.JengaLinearProgress
import io.github.joelkanyi.jenga.component.scaffold.JengaScaffold
import io.github.joelkanyi.jenga.component.scaffold.JengaTopAppBar
import io.github.joelkanyi.jenga.component.selection.JengaCheckbox
import io.github.joelkanyi.jenga.component.selection.JengaToggle
import io.github.joelkanyi.jenga.component.state.JengaEmptyState
import io.github.joelkanyi.jenga.component.state.JengaErrorState
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.component.textfield.JengaTextField
import io.github.joelkanyi.jenga.pattern.JengaSectionHeader
import io.github.joelkanyi.jenga.pattern.JengaStatCard
import io.github.joelkanyi.jenga.pattern.JengaTicketRow
import io.github.joelkanyi.jenga.theme.JengaTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Compiled usage samples surfaced in API docs via `@sample`. They are kept
 * `internal` and compile with the library, so the examples in the KDoc are
 * always valid (Glovo's "samples-as-tests" pattern).
 */

@Composable
internal fun JengaTextSample() {
    JengaText(text = "Scan a ticket to begin")
}

@Composable
internal fun JengaButtonSample() {
    JengaButton(text = "Check in", onClick = { }, variant = JengaButtonVariant.Primary)
}

@Composable
internal fun JengaCardSample() {
    JengaCard {
        JengaText(text = "Gate A · Main entrance")
    }
}

@Composable
internal fun JengaBadgeSample() {
    JengaBadge(text = "Valid", tone = JengaBadgeTone.Success)
}

@Composable
internal fun JengaChipSample() {
    var selected by remember { mutableStateOf(false) }
    JengaChip(label = "Music", selected = selected, onClick = { selected = !selected })
}

@Composable
internal fun JengaTextFieldSample() {
    var value by remember { mutableStateOf("") }
    JengaTextField(
        value = value,
        onValueChange = { value = it },
        label = "Ticket code",
        placeholder = "TKT-…",
    )
}

@Composable
internal fun JengaToggleSample() {
    var on by remember { mutableStateOf(true) }
    JengaToggle(checked = on, onCheckedChange = { on = it })
}

@Composable
internal fun JengaCheckboxSample() {
    var checked by remember { mutableStateOf(false) }
    JengaCheckbox(checked = checked, onCheckedChange = { checked = it })
}

@Composable
internal fun JengaAvatarSample() {
    JengaAvatar(name = "Ada Lovelace")
}

@Composable
internal fun JengaSnackbarSample() {
    JengaSnackbar(
        message = "Saved offline",
        tone = JengaSnackbarTone.Neutral,
        actionLabel = "Undo",
        onAction = { },
    )
}

@Composable
internal fun JengaDialogSample() {
    var open by remember { mutableStateOf(true) }
    if (open) {
        JengaDialog(
            onDismissRequest = { open = false },
            title = "Reset device?",
            text = "This clears the cached gate session.",
            confirmButton = {
                JengaButton(text = "Reset", onClick = { open = false }, variant = JengaButtonVariant.Danger)
            },
            dismissButton = {
                JengaButton(text = "Cancel", onClick = { open = false }, variant = JengaButtonVariant.Ghost)
            },
        )
    }
}

@Composable
internal fun JengaBottomSheetSample() {
    var open by remember { mutableStateOf(true) }
    if (open) {
        JengaBottomSheet(onDismissRequest = { open = false }) {
            JengaText(text = "Select a gate")
        }
    }
}

@Composable
internal fun JengaTopAppBarSample() {
    JengaTopAppBar(title = "Events")
}

@Composable
internal fun JengaScaffoldSample() {
    JengaScaffold(
        topBar = { JengaTopAppBar(title = "Events") },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            JengaText(text = "Content")
        }
    }
}

@Composable
internal fun JengaEmptyStateSample() {
    JengaEmptyState(
        title = "No scans yet",
        description = "Validated tickets appear here.",
        actionLabel = "Scan a ticket",
        onAction = { },
    )
}

@Composable
internal fun JengaErrorStateSample() {
    JengaErrorState(
        title = "Couldn't load gates",
        actionLabel = "Retry",
        onAction = { },
    )
}

@Composable
internal fun JengaLinearProgressSample() {
    JengaLinearProgress(progress = 0.6f)
}

@Composable
internal fun JengaTicketRowSample() {
    JengaTicketRow(
        attendeeName = "Ada Lovelace",
        detail = "VIP · TKT-2026-001",
        statusLabel = "Valid",
        statusTone = JengaBadgeTone.Success,
    )
}

@Composable
internal fun JengaIconSample() {
    JengaIcon(JengaIcons.Check, contentDescription = "Valid")
}

@Composable
internal fun JengaDividerSample() {
    JengaDivider()
}

@Composable
internal fun JengaListItemSample() {
    JengaListItem(
        headline = "Gate A",
        supporting = "Main entrance",
        trailingContent = { JengaIcon(JengaIcons.ChevronRight, contentDescription = null) },
        onClick = { },
    )
}

@Composable
internal fun JengaNavigationBarSample() {
    JengaNavigationBar {
        JengaNavigationBarItem(
            selected = true,
            onClick = { },
            icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
            label = "Events",
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = { },
            icon = { JengaIcon(JengaIcons.Check, contentDescription = null) },
            label = "Scan",
        )
    }
}

@Composable
internal fun JengaTabsSample() {
    JengaTabs(selectedIndex = 0, tabs = listOf("Upcoming", "Past"), onSelect = { })
}

@Composable
internal fun JengaSegmentedControlSample() {
    JengaSegmentedControl(selectedIndex = 0, segments = listOf("Day", "Week"), onSelect = { })
}

@Composable
internal fun JengaRadioButtonSample() {
    var selected by remember { mutableStateOf(true) }
    JengaRadioButton(selected = selected, onClick = { selected = !selected })
}

@Composable
internal fun JengaSearchFieldSample() {
    var query by remember { mutableStateOf("") }
    JengaSearchField(value = query, onValueChange = { query = it })
}

@Composable
internal fun JengaSliderSample() {
    var value by remember { androidx.compose.runtime.mutableFloatStateOf(0.5f) }
    JengaSlider(value = value, onValueChange = { value = it })
}

@Composable
internal fun JengaFabSample() {
    JengaFab(onClick = { }) { JengaIcon(JengaIcons.Add, contentDescription = "Add") }
}

@Composable
internal fun JengaBannerSample() {
    JengaBanner(message = "Working offline — scans will sync later.", tone = JengaBannerTone.Info)
}

@Composable
internal fun JengaDropdownMenuSample() {
    var expanded by remember { mutableStateOf(false) }
    JengaDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        JengaDropdownMenuItem(text = "Re-enter", onClick = { expanded = false })
        JengaDropdownMenuItem(text = "View history", onClick = { expanded = false })
    }
}

@Composable
internal fun JengaTooltipSample() {
    JengaTooltip(text = "Re-admit this attendee")
}

@Composable
internal fun JengaStatCardSample() {
    JengaStatCard(label = "Checked in", value = "1,284", trendLabel = "+12%", trendTone = JengaBadgeTone.Success)
}

@Composable
internal fun JengaSectionHeaderSample() {
    JengaSectionHeader(title = "Recent scans", actionLabel = "See all", onActionClick = { })
}

@Composable
internal fun JengaStackSample() {
    JengaStack {
        JengaText("One")
        JengaText("Two")
        JengaText("Three")
    }
}

@Composable
internal fun JengaInlineSample() {
    JengaInline {
        JengaText("A")
        JengaText("B")
        JengaText("C")
    }
}

@Composable
internal fun JengaWrapSample() {
    JengaWrap {
        repeat(5) { JengaBadge(text = "tag${it + 1}") }
    }
}

@Composable
internal fun JengaBoxSample() {
    JengaBox(padding = PaddingValues(16.dp), background = JengaTheme.colors.brandSubtle) {
        JengaText("Boxed", color = JengaTheme.colors.onBrandSubtle)
    }
}

@Composable
internal fun JengaGridSample() {
    JengaGrid(columns = 2) {
        JengaText("1")
        JengaText("2")
        JengaText("3")
        JengaText("4")
    }
}

@Composable
internal fun JengaSectionSample() {
    JengaSection(title = "Recent scans", actionLabel = "See all", onActionClick = { }) {
        JengaText("Row one")
        JengaText("Row two")
    }
}
