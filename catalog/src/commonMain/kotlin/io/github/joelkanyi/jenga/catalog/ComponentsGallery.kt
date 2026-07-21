package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.action.JengaAction
import io.github.joelkanyi.jenga.component.avatar.JengaAvatar
import io.github.joelkanyi.jenga.component.avatar.JengaAvatarSize
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.expandable.JengaExpandableRow
import io.github.joelkanyi.jenga.component.media.JengaMediaHero
import io.github.joelkanyi.jenga.component.progress.JengaDotStrip
import io.github.joelkanyi.jenga.component.reaction.JengaReactionBar
import io.github.joelkanyi.jenga.component.shelf.JengaImageShelf
import io.github.joelkanyi.jenga.component.shelf.JengaShelfCard
import io.github.joelkanyi.jenga.component.stat.JengaStatTile
import io.github.joelkanyi.jenga.component.stat.JengaStatTone
import io.github.joelkanyi.jenga.component.stepper.JengaStepper
import io.github.joelkanyi.jenga.component.swipe.JengaSwipeToDismiss
import io.github.joelkanyi.jenga.component.verdict.JengaVerdictBar
import io.github.joelkanyi.jenga.component.verdict.JengaVerdictTone
import io.github.joelkanyi.jenga.component.banner.JengaBanner
import io.github.joelkanyi.jenga.component.banner.JengaBannerTone
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.button.JengaIconButtonVariant
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.card.JengaCardVariant
import io.github.joelkanyi.jenga.component.chip.JengaChip
import io.github.joelkanyi.jenga.component.divider.JengaDivider
import io.github.joelkanyi.jenga.component.fab.JengaFab
import io.github.joelkanyi.jenga.component.feedback.JengaBottomSheet
import io.github.joelkanyi.jenga.component.feedback.JengaDialog
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbar
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbarTone
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.component.layout.JengaGrid
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaSection
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.layout.JengaWrap
import io.github.joelkanyi.jenga.component.list.JengaListItem
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenu
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenuItem
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBar
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBarItem
import io.github.joelkanyi.jenga.component.progress.JengaCircularProgress
import io.github.joelkanyi.jenga.component.progress.JengaCircularProgressIndeterminate
import io.github.joelkanyi.jenga.component.progress.JengaLinearProgress
import io.github.joelkanyi.jenga.component.progress.JengaLinearProgressIndeterminate
import io.github.joelkanyi.jenga.component.progress.jengaShimmer
import io.github.joelkanyi.jenga.component.scaffold.JengaBottomBar
import io.github.joelkanyi.jenga.component.scanner.JengaScanFeedback
import io.github.joelkanyi.jenga.component.scanner.JengaScannerStatus
import io.github.joelkanyi.jenga.component.scanner.JengaScannerViewfinder
import io.github.joelkanyi.jenga.component.search.JengaSearchField
import io.github.joelkanyi.jenga.component.status.JengaStatusPill
import io.github.joelkanyi.jenga.component.selection.JengaCheckbox
import io.github.joelkanyi.jenga.component.selection.JengaRadioButton
import io.github.joelkanyi.jenga.component.selection.JengaToggle
import io.github.joelkanyi.jenga.component.slider.JengaSlider
import io.github.joelkanyi.jenga.component.tabs.JengaSegmentedControl
import io.github.joelkanyi.jenga.component.tabs.JengaTabs
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.component.textfield.JengaTextField
import io.github.joelkanyi.jenga.component.tooltip.JengaTooltip
import io.github.joelkanyi.jenga.pattern.JengaSectionHeader
import io.github.joelkanyi.jenga.pattern.JengaStatCard
import io.github.joelkanyi.jenga.pattern.JengaTicketRow
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A live gallery of every Jenga block, pattern and layout primitive — scroll to
 * browse them all, interact with them, and flip light/dark from the toolbar.
 */
@Composable
fun ComponentsGallery() {
    // Interactive state
    var toggle by remember { mutableStateOf(true) }
    var checkbox by remember { mutableStateOf(true) }
    var radio by remember { mutableIntStateOf(0) }
    var chip by remember { mutableStateOf(true) }
    var segment by remember { mutableIntStateOf(0) }
    var tab by remember { mutableIntStateOf(0) }
    var slider by remember { mutableFloatStateOf(0.4f) }
    var query by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(2) }
    var expanded by remember { mutableStateOf(false) }

    JengaStack(space = JengaTheme.spacing.xxl) {
        Group("Buttons") {
            JengaInline(space = JengaTheme.spacing.sm) {
                JengaButton("Primary", {}, variant = JengaButtonVariant.Primary)
                JengaButton("Ink", {}, variant = JengaButtonVariant.Ink)
                JengaButton("Ghost", {}, variant = JengaButtonVariant.Ghost)
            }
            JengaInline(space = JengaTheme.spacing.sm) {
                JengaButton("Outline", {}, variant = JengaButtonVariant.Outline)
                JengaButton("Danger", {}, variant = JengaButtonVariant.Danger)
                JengaButton("Loading", {}, loading = true)
            }
            JengaInline {
                JengaFab(onClick = {}) { JengaIcon(JengaIcons.Add, contentDescription = "Add") }
                JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
                    JengaIcon(JengaIcons.Search, contentDescription = "Search")
                }
                JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Filled) {
                    JengaIcon(JengaIcons.Add, contentDescription = "Add")
                }
                JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Tonal) {
                    JengaIcon(JengaIcons.Info, contentDescription = "Info")
                }
                Box(Modifier.background(androidx.compose.ui.graphics.Color.Black)) {
                    JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Overlay) {
                        JengaIcon(JengaIcons.Flash, contentDescription = "Torch")
                    }
                }
            }
        }

        Group("Text fields") {
            JengaTextField(field, { field = it }, label = "Email", placeholder = "you@example.com")
            JengaSearchField(query, { query = it })
        }

        Group("Selection") {
            JengaInline {
                JengaToggle(checked = toggle, onCheckedChange = { toggle = it })
                JengaCheckbox(checked = checkbox, onCheckedChange = { checkbox = it })
                JengaRadioButton(selected = radio == 0, onClick = { radio = 0 })
                JengaRadioButton(selected = radio == 1, onClick = { radio = 1 })
            }
            JengaInline {
                JengaChip("Filter", selected = chip, onClick = { chip = !chip })
                JengaChip("Off", selected = false, onClick = {})
            }
            JengaSegmentedControl(segment, listOf("Day", "Week", "Month"), { segment = it })
        }

        Group("Containers & list") {
            JengaInline {
                JengaCard(variant = JengaCardVariant.Elevated) { JengaText("Elevated") }
                JengaCard(variant = JengaCardVariant.Outlined) { JengaText("Outlined") }
            }
            JengaBox(
                padding = PaddingValues(JengaTheme.spacing.lg),
                background = JengaTheme.colors.brandSubtle,
                shape = JengaTheme.shapes.card,
            ) { JengaText("JengaBox", color = JengaTheme.colors.onBrandSubtle) }
            JengaListItem(
                headline = "Gate A",
                supporting = "Main entrance",
                leadingContent = { JengaIcon(JengaIcons.Check, contentDescription = null, tint = JengaTheme.colors.success) },
                trailingContent = { JengaIcon(JengaIcons.ChevronRight, contentDescription = null) },
                onClick = {},
            )
            JengaDivider()
        }

        Group("Feedback") {
            JengaBanner("Working offline — scans will sync later.", tone = JengaBannerTone.Info)
            JengaBanner("Already scanned.", tone = JengaBannerTone.Error)
            JengaSnackbar("Saved offline", tone = JengaSnackbarTone.Neutral, actionLabel = "Undo", onAction = {})
            JengaTooltip("Re-admit this attendee")
            JengaInline {
                JengaButton("Dialog", { showDialog = true }, variant = JengaButtonVariant.Outline)
                JengaButton("Bottom sheet", { showSheet = true }, variant = JengaButtonVariant.Outline)
                Box {
                    JengaButton("Menu", { menuOpen = true }, variant = JengaButtonVariant.Outline)
                    JengaDropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        JengaDropdownMenuItem("Re-enter", { menuOpen = false })
                        JengaDropdownMenuItem("View history", { menuOpen = false })
                    }
                }
            }
        }

        Group("Navigation") {
            JengaTabs(tab, listOf("Upcoming", "Live", "Past"), { tab = it })
            JengaNavigationBar {
                JengaNavigationBarItem(true, {}, { JengaIcon(JengaIcons.Search, contentDescription = null) }, label = "Events")
                JengaNavigationBarItem(false, {}, { JengaIcon(JengaIcons.Check, contentDescription = null) }, label = "Scan")
                JengaNavigationBarItem(false, {}, { JengaIcon(JengaIcons.Info, contentDescription = null) }, label = "Stats")
            }
            JengaBottomBar {
                JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
                    JengaIcon(JengaIcons.Search, contentDescription = "Search")
                }
                JengaButton("Manual entry", {}, modifier = Modifier.weight(1f))
                JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
                    JengaIcon(JengaIcons.History, contentDescription = "History")
                }
            }
        }

        Group("Data display") {
            JengaWrap {
                JengaBadge("Valid", tone = JengaBadgeTone.Success)
                JengaBadge("Pending", tone = JengaBadgeTone.Warning)
                JengaBadge("Denied", tone = JengaBadgeTone.Error)
                JengaBadge("Info", tone = JengaBadgeTone.Info)
            }
            JengaInline {
                JengaAvatar("Joel Kanyi", size = JengaAvatarSize.Small)
                JengaAvatar("Ada Lovelace")
                JengaAvatar("Grace Hopper", size = JengaAvatarSize.Large)
            }
            JengaLinearProgress(progress = 0.6f)
            JengaLinearProgressIndeterminate()
            JengaInline {
                JengaCircularProgress(progress = 0.7f)
                JengaCircularProgressIndeterminate()
            }
            JengaSlider(value = slider, onValueChange = { slider = it })
            Box(Modifier.size(width = 160.dp, height = 16.dp).clip(JengaTheme.shapes.sm).jengaShimmer())
            JengaWrap {
                listOf(
                    JengaIcons.Check, JengaIcons.Close, JengaIcons.Add, JengaIcons.Search,
                    JengaIcons.ChevronRight, JengaIcons.ChevronDown, JengaIcons.ArrowBack, JengaIcons.Info,
                    JengaIcons.Volume, JengaIcons.Vibrate, JengaIcons.Sun, JengaIcons.Cloud,
                    JengaIcons.CloudOff, JengaIcons.Refresh, JengaIcons.Bell, JengaIcons.Trash,
                    JengaIcons.Smartphone, JengaIcons.Database, JengaIcons.Logout, JengaIcons.Shield,
                    JengaIcons.Settings,
                ).forEach { JengaIcon(it, contentDescription = null) }
            }
        }

        Group("Layout primitives") {
            JengaText("JengaGrid (3 columns):", style = JengaTheme.typography.bodySmall, color = JengaTheme.colors.textMuted)
            JengaGrid(columns = 3) {
                repeat(6) { i ->
                    JengaBox(
                        padding = PaddingValues(JengaTheme.spacing.md),
                        background = JengaTheme.colors.surfaceSunk,
                        shape = JengaTheme.shapes.md,
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) { JengaText("${i + 1}") }
                }
            }
        }

        Group("Patterns") {
            JengaSectionHeader(title = "Recent scans", subtitle = "Last 24 hours", actionLabel = "See all", onActionClick = {})
            JengaInline {
                JengaStatCard("Checked in", "1,284", trendLabel = "+12%", trendTone = JengaBadgeTone.Success)
                JengaStatCard("Denied", "37", trendLabel = "+3", trendTone = JengaBadgeTone.Error)
            }
            JengaTicketRow("Joel Kanyi", "VIP · TKT-2026-001", "Valid", JengaBadgeTone.Success)
            JengaTicketRow("Ada Lovelace", "Regular · TKT-2026-114", "Used", JengaBadgeTone.Neutral)
            JengaInline {
                JengaStatusPill("Synced", tone = JengaBadgeTone.Success)
                JengaStatusPill("3 pending", tone = JengaBadgeTone.Warning, loading = true)
                JengaStatusPill("Offline", tone = JengaBadgeTone.Error)
            }
        }

        Group("Content blocks") {
            JengaVerdictBar(
                amount = "KES 1,190",
                amountSuffix = "left",
                tone = JengaVerdictTone.Positive,
                label = "This month",
                progress = 0.62f,
                sublineStart = "KES 710 of 1,900 spent",
                sublineEnd = "8 of 11 priced",
                action = JengaAction("Change") {},
            )
            JengaInline {
                JengaStatTile(label = "Revenue", value = "1,284", unit = "KES", tone = JengaStatTone.Success)
                JengaStatTile(label = "Refunds", value = "37", tone = JengaStatTone.Error)
            }
            JengaInline {
                JengaStepper(value = quantity, onValueChange = { quantity = it }, min = 1, max = 9)
                JengaDotStrip(filled = 3, total = 5, contentDescription = "3 of 5 done")
            }
            JengaMediaHero(title = "Night Market", support = "Fri · 7pm · Riverside")
            JengaExpandableRow(
                expanded = expanded,
                onToggle = { expanded = !expanded },
                header = { JengaText("Delivery details", modifier = Modifier.weight(1f)) },
            ) {
                JengaText("Ships in 2 to 3 business days. Free returns within 30 days.")
            }
            JengaReactionBar(
                onPositive = {},
                onNegative = {},
                positiveContentDescription = "Helpful",
                negativeContentDescription = "Not helpful",
            )
            JengaSwipeToDismiss(onDismiss = {}) {
                JengaListItem(headline = "Swipe me away", supporting = "End to start to dismiss")
            }
        }

        Group("Media shelf") {
            JengaImageShelf(title = "Picked for tonight", action = JengaAction("See all") {}) {
                JengaShelfCard(title = "Grilled veg bowl", meta = "25 min · ~540 kcal")
                JengaShelfCard(title = "Miso ramen", meta = "30 min · ~610 kcal")
                JengaShelfCard(title = "Poke bowl", meta = "15 min · ~480 kcal")
            }
        }

        Group("Scanner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(JengaTheme.shapes.lg)
                    .background(androidx.compose.ui.graphics.Color.Black),
            ) {
                JengaScannerViewfinder(status = JengaScannerStatus.Scanning)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(JengaTheme.shapes.lg)
                    .background(androidx.compose.ui.graphics.Color.Black),
            ) {
                JengaScanFeedback(status = JengaScannerStatus.Success)
            }
        }
    }

    if (showDialog) {
        JengaDialog(
            onDismissRequest = { showDialog = false },
            title = "Reset device?",
            text = "This clears the cached gate session on this device.",
            confirmButton = { JengaButton("Reset", { showDialog = false }, variant = JengaButtonVariant.Danger) },
            dismissButton = { JengaButton("Cancel", { showDialog = false }, variant = JengaButtonVariant.Ghost) },
        )
    }
    if (showSheet) {
        JengaBottomSheet(onDismissRequest = { showSheet = false }) {
            JengaStack(modifier = Modifier.padding(JengaTheme.spacing.xl)) {
                JengaText("Select a gate", style = JengaTheme.typography.titleLarge)
                JengaText("Choose which gate you're scanning at.", color = JengaTheme.colors.textMuted)
            }
        }
    }
}

@Composable
private fun Group(title: String, content: @Composable () -> Unit) {
    JengaSection(title = title) { content() }
}
