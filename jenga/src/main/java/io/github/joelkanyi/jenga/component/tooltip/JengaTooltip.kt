package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaTooltip]. Override via [JengaTooltipDefaults.colors]. */
@Immutable
public data class JengaTooltipColors(
    public val container: Color,
    public val content: Color,
)

/** Defaults and token mappings for [JengaTooltip]. */
public object JengaTooltipDefaults {
    /** Themed colors (high-contrast inverse surface). */
    @Composable
    public fun colors(): JengaTooltipColors = JengaTooltipColors(
        container = JengaTheme.colors.inverseSurface,
        content = JengaTheme.colors.inverseOnSurface,
    )
}

/**
 * A small tooltip bubble (inverse surface). This is the tooltip *surface*; host
 * it over an anchor (e.g. in a `Popup`, or M3 `TooltipBox`) to show on long-press
 * or hover.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTooltipSample
 *
 * @param text the tooltip text.
 * @param modifier the [Modifier] for the bubble.
 * @param colors the color set; defaults to [JengaTooltipDefaults.colors].
 */
@Composable
public fun JengaTooltip(
    text: String,
    modifier: Modifier = Modifier,
    colors: JengaTooltipColors = JengaTooltipDefaults.colors(),
) {
    JengaText(
        text = text,
        style = JengaTheme.typography.bodySmall,
        color = colors.content,
        modifier = modifier
            .shadow(JengaTheme.elevation.md, JengaTheme.shapes.sm)
            .clip(JengaTheme.shapes.sm)
            .background(colors.container)
            .widthIn(max = 240.dp)
            .padding(horizontal = JengaTheme.spacing.md, vertical = JengaTheme.spacing.sm),
    )
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTooltipPreview() {
    JengaTheme { TooltipShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipRtlPreview() {
    JengaTheme { RtlPreview { TooltipShowcase() } }
}

@Composable
private fun TooltipShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTooltip(text = "Re-admit this attendee")
        JengaTooltip(text = "This ticket was already scanned at 14:02 by Gate B.")
    }
}
