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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaNavigationBar]. Override via [JengaNavigationBarDefaults.colors]. */
@Immutable
public data class JengaNavigationBarColors(
    public val container: Color,
    public val divider: Color,
    public val selectedContent: Color,
    public val unselectedContent: Color,
    public val disabledContent: Color,
    /** Fill of the [JengaNavIndicator.Pill] behind a selected item's icon. */
    public val selectedIndicator: Color,
    /** Icon color inside the [JengaNavIndicator.Pill]. */
    public val selectedIndicatorContent: Color,
)

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
 * @param selected whether this item is the current destination.
 * @param onClick called when the item is tapped.
 * @param icon the item icon (inherits the resolved content color).
 * @param modifier the [Modifier] for this item.
 * @param label optional text under the icon.
 * @param enabled whether the item is interactive.
 * @param colors the color set; defaults to [JengaNavigationBarDefaults.colors].
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
) {
    val showPill = indicator == JengaNavIndicator.Pill && selected && enabled
    val contentColor = when {
        !enabled -> colors.disabledContent
        selected -> colors.selectedContent
        else -> colors.unselectedContent
    }
    val iconColor = if (showPill) colors.selectedIndicatorContent else contentColor
    Column(
        modifier = modifier
            .weight(1f)
            .selectable(selected = selected, enabled = enabled, role = Role.Tab, onClick = onClick)
            .padding(vertical = JengaTheme.spacing.sm),
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
}
