package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.math.ceil
import kotlin.math.max

/**
 * Where an anchored tooltip goes, in window pixels.
 *
 * @property position the tooltip's top-left corner in the window.
 * @property edge the edge its pointer sits on: [JengaTooltipPointerEdge.Top] when the tooltip is
 *   below its host, [JengaTooltipPointerEdge.Bottom] when it is above.
 * @property pointerOffset the distance from the start of [edge] (the left, or the right in
 *   right-to-left layouts) to the host's horizontal centre: where the pointer's tip should be.
 */
internal data class JengaTooltipPlacement(
    val position: IntOffset,
    val edge: JengaTooltipPointerEdge,
    val pointerOffset: Int,
)

/**
 * Places a tooltip of [tooltipSize] (pointer included) next to a host at [anchorBounds], inside a
 * window of [windowSize].
 *
 * - Vertically it goes [gap] below the host, pointer on its top edge; when that would run past the
 *   bottom of the window it goes [gap] above the host instead, pointer on its bottom edge. When it
 *   fits on neither side it goes on the side with more room and is moved up or down until it keeps
 *   [verticalMargin] from the top and bottom of the window (or, if it is too tall for that, until
 *   it is as far inside the window as it can be), even if it then covers part of the host.
 * - Horizontally it is centred on the host and then moved, if needed, so it keeps [margin] from
 *   both sides of the window. A tooltip wider than the window minus both margins is centred in the
 *   window.
 * - The host wins over the margin: the pointer's centre can come no closer than [pointerInset] to
 *   the tooltip's sides (the bubble's rounded corner plus half the pointer's base), so when the
 *   host's centre is nearer the window's side than that, the tooltip moves into the margin until the
 *   pointer reaches the host's centre. It never leaves the window.
 * - The pointer is aimed at the host's horizontal centre, measured from the tooltip's start edge, so
 *   in right-to-left layouts it is measured from the right. The tooltip's position does not depend
 *   on the layout direction: the margins are the same on both sides.
 */
internal fun placeJengaTooltip(
    anchorBounds: IntRect,
    windowSize: IntSize,
    tooltipSize: IntSize,
    layoutDirection: LayoutDirection,
    gap: Int,
    margin: Int,
    verticalMargin: Int = 0,
    pointerInset: Int = 0,
): JengaTooltipPlacement {
    val width = tooltipSize.width
    val height = tooltipSize.height

    val below = anchorBounds.bottom + gap
    val above = anchorBounds.top - gap - height
    val fitsBelow = below + height <= windowSize.height
    val fitsAbove = above >= 0
    val placeBelow = when {
        fitsBelow -> true
        fitsAbove -> false
        // Neither fits: the side with more room.
        else -> windowSize.height - below >= anchorBounds.top - gap
    }
    val y = when {
        placeBelow && fitsBelow -> below
        !placeBelow && fitsAbove -> above
        else -> {
            val wanted = if (placeBelow) below else above
            val top = verticalMargin
            val bottom = windowSize.height - verticalMargin - height
            if (top <= bottom) {
                wanted.coerceIn(top, bottom)
            } else {
                wanted.coerceIn(0, (windowSize.height - height).coerceAtLeast(0))
            }
        }
    }

    val hostCenter = anchorBounds.left + anchorBounds.width / 2
    val lowest = margin
    val highest = windowSize.width - margin - width
    var x = if (lowest <= highest) (hostCenter - width / 2).coerceIn(lowest, highest) else (windowSize.width - width) / 2
    // Keep the pointer on the host: no further than the pointer can reach from the tooltip's sides.
    val reachLow = hostCenter + pointerInset - width
    val reachHigh = hostCenter - pointerInset
    if (reachLow <= reachHigh) x = x.coerceIn(reachLow, reachHigh)
    // But never off the window.
    val windowHigh = windowSize.width - width
    x = if (windowHigh >= 0) x.coerceIn(0, windowHigh) else windowHigh / 2

    val pointerOffset = when (layoutDirection) {
        LayoutDirection.Ltr -> hostCenter - x
        LayoutDirection.Rtl -> x + width - hostCenter
    }
    return JengaTooltipPlacement(
        position = IntOffset(x, y),
        edge = if (placeBelow) JengaTooltipPointerEdge.Top else JengaTooltipPointerEdge.Bottom,
        pointerOffset = pointerOffset,
    )
}

/**
 * The closest the pointer's centre can come to a side of a tooltip of [tooltipSize] with the bubble
 * [bubbleShape], in pixels: the largest corner radius of the bubble plus half the pointer's base,
 * resolved the way [JengaTooltipPointerShape] resolves them. Rounded up.
 */
internal fun jengaTooltipPointerInset(
    bubbleShape: Shape,
    tooltipSize: IntSize,
    layoutDirection: LayoutDirection,
    density: Density,
): Int {
    val pointerHeight = with(density) { TooltipPointerHeight.roundToPx() }
    val bubbleSize = Size(tooltipSize.width.toFloat(), (tooltipSize.height - pointerHeight).coerceAtLeast(0).toFloat())
    val outline = bubbleShape.createOutline(bubbleSize, layoutDirection, density)
    val corner = if (outline is Outline.Rounded) {
        val r = outline.roundRect
        max(max(r.topLeftCornerRadius.x, r.topRightCornerRadius.x), max(r.bottomLeftCornerRadius.x, r.bottomRightCornerRadius.x))
    } else {
        0f
    }
    val halfBase = with(density) { TooltipPointerWidth.toPx() } / 2f
    return ceil(corner + halfBase).toInt()
}

/**
 * Positions a [JengaTooltipBox]'s popup with [placeJengaTooltip] and reports each placement to
 * [onPlaced], so the bubble draws its pointer on the edge and at the offset this placement chose.
 */
internal class JengaTooltipPositionProvider(
    private val gap: Int,
    private val margin: Int,
    private val verticalMargin: Int,
    private val bubbleShape: Shape,
    private val density: Density,
    private val onPlaced: (JengaTooltipPlacement) -> Unit,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val placement = placeJengaTooltip(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            tooltipSize = popupContentSize,
            layoutDirection = layoutDirection,
            gap = gap,
            margin = margin,
            verticalMargin = verticalMargin,
            pointerInset = jengaTooltipPointerInset(bubbleShape, popupContentSize, layoutDirection, density),
        )
        onPlaced(placement)
        return placement.position
    }
}
