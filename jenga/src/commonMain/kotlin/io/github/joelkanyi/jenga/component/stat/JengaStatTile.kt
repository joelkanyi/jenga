package io.github.joelkanyi.jenga.component.stat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/**
 * The semantic tone of a [JengaStatTile]; drives its fill and text colors from
 * theme tokens (never hardcoded), so a tile looks native to whatever app themes
 * Jenga.
 */
public enum class JengaStatTone { Neutral, Brand, Success, Warning, Error, Info }

/** Resolved colors for a [JengaStatTile]. Override via [JengaStatTileDefaults.colors]. */
@Poko
@Immutable
public class JengaStatTileColors(
    public val fill: Brush,
    public val label: Color,
    public val value: Color,
    public val unit: Color,
) {
    public fun copy(
        fill: Brush = this.fill,
        label: Color = this.label,
        value: Color = this.value,
        unit: Color = this.unit,
    ): JengaStatTileColors = JengaStatTileColors(
        fill,
        label,
        value,
        unit,
    )
}

/** Defaults and token mappings for [JengaStatTile]. */
public object JengaStatTileDefaults {
    /**
     * Themed colors for [tone]. On a light scheme the tile is a flat status
     * container with darker on-container text; on a dark scheme it is a
     * saturated two-stop gradient derived from the tone color with light text.
     */
    @Composable
    public fun colors(tone: JengaStatTone): JengaStatTileColors {
        val c = JengaTheme.colors
        if (tone == JengaStatTone.Neutral) {
            return JengaStatTileColors(
                fill = SolidColor(c.surfaceVariant),
                label = c.textSecondary,
                value = c.textPrimary,
                unit = c.textMuted,
            )
        }
        val content = when (tone) {
            JengaStatTone.Brand -> c.brand
            JengaStatTone.Success -> c.success
            JengaStatTone.Warning -> c.warning
            JengaStatTone.Error -> c.error
            JengaStatTone.Info -> c.info
            JengaStatTone.Neutral -> c.textSecondary
        }
        val container = when (tone) {
            JengaStatTone.Brand -> c.brandSubtle
            JengaStatTone.Success -> c.successContainer
            JengaStatTone.Warning -> c.warningContainer
            JengaStatTone.Error -> c.errorContainer
            JengaStatTone.Info -> c.infoContainer
            JengaStatTone.Neutral -> c.surfaceVariant
        }
        val onContainer = when (tone) {
            JengaStatTone.Brand -> c.onBrandSubtle
            JengaStatTone.Success -> c.onSuccessContainer
            JengaStatTone.Warning -> c.onWarningContainer
            JengaStatTone.Error -> c.onErrorContainer
            JengaStatTone.Info -> c.onInfoContainer
            JengaStatTone.Neutral -> c.textSecondary
        }
        return if (c.isLight) {
            JengaStatTileColors(
                fill = SolidColor(container),
                label = content,
                value = onContainer,
                unit = content,
            )
        } else {
            // Deepen the tone into a rich gradient; text stays bright. The dark
            // anchor is the theme's own scrim colour (opaque), not a raw black.
            val ink = c.scrim.copy(alpha = 1f)
            val top = lerp(content, ink, 0.55f)
            val bottom = lerp(content, ink, 0.74f)
            JengaStatTileColors(
                fill = Brush.linearGradient(listOf(top, bottom)),
                label = lerp(content, c.onOverlay, 0.25f),
                value = c.onOverlay,
                unit = lerp(content, c.onOverlay, 0.25f),
            )
        }
    }
}

/**
 * A one-glance metric tile: an icon + label, a big value with an optional unit,
 * and an optional micro-visualization ([viz], e.g. a [io.github.joelkanyi.jenga.component.progress.JengaDotStrip]
 * or a sub-label). Fills its layout slot (use it in an equal-weight row). Generic
 * and token-driven; pick a [tone] to color it.
 *
 * @param label the metric name (e.g. "Balance").
 * @param value the metric value (e.g. "3", "≈640", "210").
 * @param modifier the [Modifier] for this tile; give it width (e.g. `Modifier.weight(1f)`).
 * @param unit an optional trailing unit (e.g. "/4 groups", "kcal", "KES").
 * @param tone the semantic tone driving the fill and text colors.
 * @param icon an optional leading icon (inherits the label color).
 * @param viz optional micro-visualization rendered under the value.
 * @param colors the color set; defaults to [JengaStatTileDefaults.colors] for [tone].
 */
@Composable
public fun JengaStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    tone: JengaStatTone = JengaStatTone.Neutral,
    icon: (@Composable () -> Unit)? = null,
    viz: (@Composable ColumnScope.() -> Unit)? = null,
    colors: JengaStatTileColors = JengaStatTileDefaults.colors(tone),
) {
    Column(
        modifier = modifier
            .clip(JengaTheme.shapes.cardLarge)
            .background(colors.fill)
            .padding(JengaTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                CompositionLocalProvider(LocalJengaContentColor provides colors.label) {
                    icon()
                }
            }
            JengaText(
                text = label,
                style = JengaTheme.typography.label,
                color = colors.label,
                maxLines = 1,
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            JengaText(
                text = value,
                style = JengaTheme.typography.headingSmall,
                color = colors.value,
                maxLines = 1,
            )
            if (unit != null) {
                JengaText(
                    text = " $unit",
                    style = JengaTheme.typography.bodySmall,
                    color = colors.unit,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = JengaTheme.spacing.xxs),
                )
            }
        }
        if (viz != null) viz()
    }
}
