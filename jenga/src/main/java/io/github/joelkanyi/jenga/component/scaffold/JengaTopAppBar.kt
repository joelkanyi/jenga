package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaTopAppBar]. Override via [JengaTopAppBarDefaults.colors]. */
@Immutable
public data class JengaTopAppBarColors(
    public val container: Color,
    public val content: Color,
    public val divider: Color,
)

/** Defaults and token mappings for [JengaTopAppBar]. */
public object JengaTopAppBarDefaults {
    /** Minimum bar height (excluding the status-bar inset). */
    public val Height: Dp = 56.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaTopAppBarColors {
        val c = JengaTheme.colors
        return JengaTopAppBarColors(
            container = c.surface,
            content = c.textPrimary,
            divider = c.border,
        )
    }
}

/**
 * A top app bar with a title, optional navigation icon and trailing actions.
 *
 * Applies status-bar inset padding itself, so it sits correctly under the system
 * bar whether or not it is hosted in a [JengaScaffold].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTopAppBarSample
 *
 * @param title the bar title.
 * @param modifier the [Modifier] for this bar.
 * @param subtitle optional secondary line under the title (e.g. context/details).
 * @param navigationIcon optional leading icon (e.g. back); inherits content color.
 * @param actions trailing actions laid out in a [RowScope]; inherit content color.
 * @param colors the color set; defaults to [JengaTopAppBarDefaults.colors].
 */
@Composable
public fun JengaTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: JengaTopAppBarColors = JengaTopAppBarDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.container)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawRect(
                    color = colors.divider,
                    topLeft = Offset(0f, size.height - stroke),
                    size = Size(size.width, stroke),
                )
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .heightIn(min = JengaTopAppBarDefaults.Height)
            .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
            navigationIcon?.invoke()
            Column(modifier = Modifier.weight(1f)) {
                JengaText(
                    text = title,
                    style = JengaTheme.typography.titleLarge,
                    color = colors.content,
                    maxLines = 1,
                )
                if (subtitle != null) {
                    JengaText(
                        text = subtitle,
                        style = JengaTheme.typography.caption,
                        color = JengaTheme.colors.textMuted,
                        maxLines = 1,
                    )
                }
            }
            actions()
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTopAppBarPreview() {
    JengaTheme { JengaTopAppBar(title = "Sol Fest 2026", subtitle = "Gate A · Online") }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTopAppBarRtlPreview() {
    JengaTheme { RtlPreview { JengaTopAppBar(title = "Sol Fest 2026", subtitle = "Gate A · Online") } }
}
