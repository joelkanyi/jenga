package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaTooltip]. Override via [JengaTooltipDefaults.colors]. */
@Poko
@Immutable
public class JengaTooltipColors(
    public val container: Color,
    public val content: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
    ): JengaTooltipColors = JengaTooltipColors(container, content)
}

/** The edge of a [JengaTooltip]'s bubble its pointer sits on. */
public enum class JengaTooltipPointerEdge {
    /** The top edge: the tooltip sits under its anchor and points up at it. */
    Top,

    /** The bottom edge: the tooltip sits above its anchor and points down at it. */
    Bottom,

    /** The start edge: left in left-to-right layouts, right in right-to-left ones. */
    Start,

    /** The end edge: right in left-to-right layouts, left in right-to-left ones. */
    End,
}

/**
 * The small triangle that ties a [JengaTooltip] to what it explains. It is drawn in the tooltip's
 * container color and is part of the bubble's outline, so the shadow follows both as one shape.
 * On Android 9 (API 28) and older the bubble alone casts the shadow; the pointer has none.
 *
 * The pointer is 10dp wide and 5dp tall. It adds those 5dp to the tooltip's own size on [edge],
 * and its tip lies on the tooltip's bounds: place the tooltip against the anchor and the tip
 * touches it. The bubble keeps its padding and maximum width.
 *
 * @param edge the edge of the bubble the pointer sits on.
 * @param offset the distance from the start of [edge] to the pointer's centre: from the start side
 *   (left, or right in right-to-left layouts) for [JengaTooltipPointerEdge.Top] and
 *   [JengaTooltipPointerEdge.Bottom], from the top for [JengaTooltipPointerEdge.Start] and
 *   [JengaTooltipPointerEdge.End]. [Dp.Unspecified], the default, centres the pointer on the edge.
 *   The pointer stays on the straight part of the edge: an offset that would put it over a rounded
 *   corner or past the edge is moved to the nearest place that fits.
 */
@Poko
@Immutable
public class JengaTooltipPointer(
    public val edge: JengaTooltipPointerEdge = JengaTooltipPointerEdge.Top,
    public val offset: Dp = Dp.Unspecified,
) {
    public fun copy(
        edge: JengaTooltipPointerEdge = this.edge,
        offset: Dp = this.offset,
    ): JengaTooltipPointer = JengaTooltipPointer(edge, offset)
}

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
 * A small tooltip bubble (inverse surface). This is the tooltip *surface* only: it is drawn where
 * it is placed. To show it next to the element it explains, placed to stay on screen and pointing
 * at that element, use [JengaTooltipBox].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTooltipSample
 * @sample io.github.joelkanyi.jenga.samples.JengaTooltipPointerSample
 *
 * @param text the tooltip text.
 * @param modifier the [Modifier] for the bubble.
 * @param colors the color set; defaults to [JengaTooltipDefaults.colors].
 * @param pointer a triangle on one edge of the bubble that points at what the tooltip explains,
 *   or null (the default) for the bubble alone. It makes the tooltip 5dp larger on that edge;
 *   see [JengaTooltipPointer].
 */
@Composable
public fun JengaTooltip(
    text: String,
    modifier: Modifier = Modifier,
    colors: JengaTooltipColors = JengaTooltipDefaults.colors(),
    pointer: JengaTooltipPointer? = null,
) {
    JengaTooltipBubble(
        text = text,
        colors = colors,
        pointer = pointer,
        shadowFollowsPointer = elevationShadowFollowsConcaveOutlines,
        modifier = modifier,
    )
}

/**
 * The tooltip itself. [shadowFollowsPointer] is false where the platform casts no shadow for the joined outline
 * (Android 9 and older): there the bubble alone casts the shadow and the pointer is drawn over it.
 */
@Composable
internal fun JengaTooltipBubble(
    text: String,
    colors: JengaTooltipColors,
    pointer: JengaTooltipPointer?,
    shadowFollowsPointer: Boolean,
    modifier: Modifier = Modifier,
) {
    val bubbleShape = JengaTheme.shapes.sm
    val elevation = JengaTheme.elevation.md
    val surface = if (pointer == null) {
        Modifier
            .shadow(elevation, bubbleShape)
            .clip(bubbleShape)
            .background(colors.container)
    } else {
        val shape = remember(bubbleShape, pointer) { JengaTooltipPointerShape(bubbleShape, pointer) }
        val room = when (pointer.edge) {
            JengaTooltipPointerEdge.Top -> PaddingValues(top = TooltipPointerHeight)
            JengaTooltipPointerEdge.Bottom -> PaddingValues(bottom = TooltipPointerHeight)
            JengaTooltipPointerEdge.Start -> PaddingValues(start = TooltipPointerHeight)
            JengaTooltipPointerEdge.End -> PaddingValues(end = TooltipPointerHeight)
        }
        if (shadowFollowsPointer) {
            // Filled as a path instead of clipped: a path clip is not anti-aliased on Android.
            Modifier
                .shadow(elevation, shape, clip = false)
                .background(colors.container, shape)
                .padding(room)
        } else {
            Modifier
                .drawWithCache {
                    val path = shape.pointerPath(shape.geometry(size, layoutDirection, this), this)
                    onDrawWithContent {
                        drawContent()
                        drawPath(path, colors.container)
                    }
                }
                .padding(room)
                .shadow(elevation, bubbleShape)
                .clip(bubbleShape)
                .background(colors.container)
        }
    }
    JengaText(
        text = text,
        style = JengaTheme.typography.bodySmall,
        color = colors.content,
        modifier = modifier
            .then(surface)
            .widthIn(max = 240.dp)
            .padding(horizontal = JengaTheme.spacing.md, vertical = JengaTheme.spacing.sm),
    )
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaTooltip(
    text: String,
    modifier: Modifier = Modifier,
    colors: JengaTooltipColors = JengaTooltipDefaults.colors(),
) {
    JengaTooltip(text = text, modifier = modifier, colors = colors, pointer = null)
}
