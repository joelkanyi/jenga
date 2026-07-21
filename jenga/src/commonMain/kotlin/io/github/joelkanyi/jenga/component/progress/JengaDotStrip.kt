package io.github.joelkanyi.jenga.component.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.theme.JengaTheme

/** How a [JengaDotStrip] draws its segments. */
public enum class JengaDotStripStyle {
    /** Round dots (a discrete "N of M" count). */
    Dots,

    /** Rounded bars that stretch to fill the width (a segmented meter). */
    Bars,
}

/** Resolved colors for a [JengaDotStrip]. Override via [JengaDotStripDefaults.colors]. */
@Poko
@Immutable
public class JengaDotStripColors(
    public val filled: Color,
    public val empty: Color,
) {
    public fun copy(
        filled: Color = this.filled,
        empty: Color = this.empty,
    ): JengaDotStripColors = JengaDotStripColors(filled, empty)
}

/** Defaults and token mappings for [JengaDotStrip]. */
public object JengaDotStripDefaults {
    /** Dot diameter in the [JengaDotStripStyle.Dots] style. */
    public val DotSize: Dp = 8.dp

    /** Bar height in the [JengaDotStripStyle.Bars] style. */
    public val BarHeight: Dp = 4.dp

    /** Themed colors for filled and empty segments. */
    @Composable
    public fun colors(): JengaDotStripColors = JengaDotStripColors(
        filled = JengaTheme.colors.success,
        empty = JengaTheme.colors.borderStrong,
    )
}

/**
 * A compact segmented meter: [filled] of [total] segments, drawn as dots or
 * bars. A one-glance "how complete is this" indicator (balance groups, days
 * covered, steps done). Generic and token-driven.
 *
 * @param filled how many segments are filled (clamped to `0..total`).
 * @param total the number of segments.
 * @param modifier the [Modifier] for the strip.
 * @param style dots or stretch-to-fill bars.
 * @param colors the filled/empty segment colors; defaults to [JengaDotStripDefaults.colors].
 * @param contentDescription accessibility description of the whole meter.
 */
@Composable
public fun JengaDotStrip(
    filled: Int,
    total: Int,
    modifier: Modifier = Modifier,
    style: JengaDotStripStyle = JengaDotStripStyle.Dots,
    colors: JengaDotStripColors = JengaDotStripDefaults.colors(),
    contentDescription: String? = null,
) {
    val safeFilled = filled.coerceIn(0, total)
    Row(
        modifier = modifier.then(
            if (contentDescription != null) {
                Modifier.semantics { this.contentDescription = contentDescription }
            } else {
                Modifier
            },
        ),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
    ) {
        repeat(total) { i ->
            val color = if (i < safeFilled) colors.filled else colors.empty
            Segment(style = style, color = color)
        }
    }
}

@Composable
private fun RowScope.Segment(style: JengaDotStripStyle, color: Color) {
    val pill = RoundedCornerShape(percent = 50)
    when (style) {
        JengaDotStripStyle.Dots -> Box(
            Modifier
                .size(JengaDotStripDefaults.DotSize)
                .clip(pill)
                .background(color),
        )

        JengaDotStripStyle.Bars -> Box(
            Modifier
                .weight(1f)
                .height(JengaDotStripDefaults.BarHeight)
                .clip(pill)
                .background(color),
        )
    }
}
