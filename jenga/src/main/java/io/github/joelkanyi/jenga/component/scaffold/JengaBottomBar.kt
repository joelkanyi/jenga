package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.button.JengaIconButtonVariant
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaBottomBar]. Override via [JengaBottomBarDefaults.colors]. */
@Immutable
public data class JengaBottomBarColors(
    public val container: Color,
    public val content: Color,
    public val divider: Color,
)

/** Defaults and token mappings for [JengaBottomBar]. */
public object JengaBottomBarDefaults {
    /** Minimum bar height (excluding the navigation-bar inset). */
    public val Height: Dp = 64.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaBottomBarColors {
        val c = JengaTheme.colors
        return JengaBottomBarColors(
            container = c.surface,
            content = c.textPrimary,
            divider = c.border,
        )
    }
}

/**
 * A bottom app bar — a surface pinned to the bottom of a [JengaScaffold] hosting a
 * row of actions. Applies the navigation-bar inset itself, so it sits correctly
 * above the system bar in an edge-to-edge app, and draws a hairline divider on top.
 *
 * Lay it out as the scaffold's `bottomBar` slot; the content is a [RowScope] so you
 * can `weight` the primary action.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBottomBarSample
 *
 * @param modifier the [Modifier] for this bar.
 * @param colors the color set; defaults to [JengaBottomBarDefaults.colors].
 * @param content the actions, laid out in a [RowScope]; inherit content color.
 */
@Composable
public fun JengaBottomBar(
    modifier: Modifier = Modifier,
    colors: JengaBottomBarColors = JengaBottomBarDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.container)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawRect(color = colors.divider, topLeft = Offset(0f, 0f), size = Size(size.width, stroke))
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .heightIn(min = JengaBottomBarDefaults.Height)
            .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
            content()
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBottomBarPreview() {
    JengaTheme { BottomBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBottomBarRtlPreview() {
    JengaTheme { RtlPreview { BottomBarShowcase() } }
}

@Composable
private fun BottomBarShowcase() {
    JengaBottomBar {
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.Search, contentDescription = "Search")
        }
        JengaButton(text = "Manual entry", onClick = {}, modifier = Modifier.weight(1f))
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.History, contentDescription = "History")
        }
    }
}
