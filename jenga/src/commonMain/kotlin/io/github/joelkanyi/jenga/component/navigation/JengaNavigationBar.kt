package io.github.joelkanyi.jenga.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.badge.jengaBadgeCountText
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor
import kotlin.math.min

/** Resolved colors for a [JengaNavigationBar]. Override via [JengaNavigationBarDefaults.colors]. */
@Poko
@Immutable
public class JengaNavigationBarColors(
    public val container: Color,
    public val divider: Color,
    public val selectedContent: Color,
    public val unselectedContent: Color,
    public val disabledContent: Color,
    /** Fill of the [JengaNavIndicator.Pill] behind a selected item's icon. */
    public val selectedIndicator: Color,
    /** Icon color inside the [JengaNavIndicator.Pill]. */
    public val selectedIndicatorContent: Color,
) {
    public fun copy(
        container: Color = this.container,
        divider: Color = this.divider,
        selectedContent: Color = this.selectedContent,
        unselectedContent: Color = this.unselectedContent,
        disabledContent: Color = this.disabledContent,
        selectedIndicator: Color = this.selectedIndicator,
        selectedIndicatorContent: Color = this.selectedIndicatorContent,
    ): JengaNavigationBarColors = JengaNavigationBarColors(
        container,
        divider,
        selectedContent,
        unselectedContent,
        disabledContent,
        selectedIndicator,
        selectedIndicatorContent,
    )
}

/**
 * How a [JengaNavigationBarItem] marks the selected state.
 *
 * [None] tints the icon + label with the selected content color (the classic
 * bar). [Pill] additionally draws a rounded "pill" fill behind the icon, the
 * Material 3 active-indicator look. Defaults to [None] so existing bars are
 * unchanged; opt in per item.
 */
public enum class JengaNavIndicator { None, Pill }

/** Defaults and token mappings for [JengaNavigationBar]. */
public object JengaNavigationBarDefaults {
    /** Bar content height (excludes the navigation-bar inset). */
    public val Height: Dp = 64.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaNavigationBarColors {
        val c = JengaTheme.colors
        return JengaNavigationBarColors(
            container = c.surface,
            divider = c.border,
            selectedContent = c.brand,
            unselectedContent = c.textMuted,
            disabledContent = c.contentDisabled,
            selectedIndicator = c.brandSubtle,
            selectedIndicatorContent = c.onBrandSubtle,
        )
    }
}

/**
 * A bottom navigation bar. Place [JengaNavigationBarItem]s in [content]; each is
 * weighted equally. Applies the navigation-bar inset itself.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaNavigationBarSample
 *
 * @param modifier the [Modifier] for the bar.
 * @param colors the color set; defaults to [JengaNavigationBarDefaults.colors].
 * @param content the bar items, laid out in a [RowScope].
 */
@Composable
public fun JengaNavigationBar(
    modifier: Modifier = Modifier,
    colors: JengaNavigationBarColors = JengaNavigationBarDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.container)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawRect(
                    color = colors.divider,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, stroke),
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .heightIn(min = JengaNavigationBarDefaults.Height)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * A single item in a [JengaNavigationBar]: an icon, optional label, and selected
 * state. Must be used inside [JengaNavigationBar]'s content scope.
 *
 * With a [badgeCount] above zero, a [JengaBadge] with the count sits at the top end of the icon; it
 * takes no space, so the icon and label stay where they are. A badge too wide for a narrow item
 * moves toward the start to stay inside the item's width.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaNavigationBarBadgeSample
 *
 * @param selected whether this item is the current destination.
 * @param onClick called when the item is tapped.
 * @param icon the item icon (inherits the resolved content color).
 * @param modifier the [Modifier] for this item.
 * @param label optional text under the icon.
 * @param enabled whether the item is interactive.
 * @param indicator how the selected state is marked; see [JengaNavIndicator].
 * @param colors the color set; defaults to [JengaNavigationBarDefaults.colors].
 * @param badgeCount the count shown in a badge at the top end of the icon. Zero or less shows no
 *   badge; above 99 the badge reads "99+".
 * @param badgeContentDescription what a screen reader says for the badge, after the item's name
 *   (e.g. "3 new", so the item reads "Notifications, 3 new"). When null the badge text is read.
 *   When the item has a [label], the icon's `contentDescription` should be null so screen readers
 *   read the label and the count: those two are the item's text, and an icon description is kept
 *   apart from them as the item's content description.
 * @param badgeTone the badge's tone; see [JengaBadgeTone].
 */
@Composable
public fun RowScope.JengaNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    indicator: JengaNavIndicator = JengaNavIndicator.None,
    colors: JengaNavigationBarColors = JengaNavigationBarDefaults.colors(),
    badgeCount: Int = 0,
    badgeContentDescription: String? = null,
    badgeTone: JengaBadgeTone = JengaBadgeTone.Error,
) {
    val showPill = indicator == JengaNavIndicator.Pill && selected && enabled
    val contentColor = when {
        !enabled -> colors.disabledContent
        selected -> colors.selectedContent
        else -> colors.unselectedContent
    }
    val iconColor = if (showPill) colors.selectedIndicatorContent else contentColor
    val badgeText = jengaBadgeCountText(badgeCount)
    // The badge is a second slot placed over the column, so it never changes the item's size and
    // the pill's clip does not cut it. It comes after the label, so it is read after it.
    Layout(
        contents = listOf(
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
                ) {
                    Box(
                        modifier = if (showPill) {
                            Modifier
                                .clip(JengaTheme.shapes.pill)
                                .background(colors.selectedIndicator)
                                .padding(horizontal = 18.dp, vertical = 5.dp)
                        } else {
                            Modifier.padding(vertical = 5.dp)
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        CompositionLocalProvider(LocalJengaContentColor provides iconColor) {
                            icon()
                        }
                    }
                    if (label != null) {
                        JengaText(
                            text = label,
                            style = JengaTheme.typography.caption.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            ),
                            color = contentColor,
                            maxLines = 1,
                        )
                    }
                }
            },
            {
                if (badgeText != null) {
                    // A screen reader reads a content description instead of text, so the badge
                    // joins whichever of the two names the item: the label's text, or without a
                    // label the icon's content description.
                    JengaNavigationItemBadge(
                        text = badgeText,
                        description = badgeContentDescription ?: badgeText,
                        tone = badgeTone,
                        describeAsText = label != null,
                    )
                }
            },
        ),
        modifier = modifier
            .weight(1f)
            .selectable(selected = selected, enabled = enabled, role = Role.Tab, onClick = onClick)
            .padding(vertical = JengaTheme.spacing.sm),
    ) { (contentMeasurables, badgeMeasurables), constraints ->
        val content = contentMeasurables.first().measure(constraints)
        val badge = badgeMeasurables.firstOrNull()?.measure(Constraints())
        layout(content.width, content.height) {
            content.placeRelative(0, 0)
            // Kept inside the item's width, so a wide badge on a narrow edge item is not cut by
            // the screen edge; it then covers more of the icon instead.
            badge?.placeRelative(
                x = min(content.width / 2 + BadgeOffsetFromCenter.roundToPx(), content.width - badge.width).coerceAtLeast(0),
                y = BadgeOffsetFromTop.roundToPx(),
            )
        }
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun RowScope.JengaNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    indicator: JengaNavIndicator = JengaNavIndicator.None,
    colors: JengaNavigationBarColors = JengaNavigationBarDefaults.colors(),
) {
    JengaNavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        label = label,
        enabled = enabled,
        indicator = indicator,
        colors = colors,
        badgeCount = 0,
    )
}

/** Distance from the icon's centre to the badge's start edge. */
private val BadgeOffsetFromCenter = 8.dp

/** Distance from the top of the icon's pill to the top of the badge. */
private val BadgeOffsetFromTop = 4.dp

/**
 * The count badge of a navigation item. Its own text is replaced for screen readers by
 * [description], as text (read after a label) or, with [describeAsText] false, as a content
 * description (read after an icon's content description).
 */
@Composable
internal fun JengaNavigationItemBadge(
    text: String,
    description: String,
    tone: JengaBadgeTone,
    describeAsText: Boolean = true,
) {
    JengaBadge(
        text = text,
        modifier = Modifier.clearAndSetSemantics {
            if (describeAsText) this.text = AnnotatedString(description) else contentDescription = description
        },
        tone = tone,
    )
}
