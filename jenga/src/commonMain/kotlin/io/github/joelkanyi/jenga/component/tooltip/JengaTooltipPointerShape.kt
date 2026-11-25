package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlin.math.min
import kotlin.math.sqrt

/** The width of the pointer's base, along the bubble's edge. */
internal val TooltipPointerWidth = 10.dp

/** How far the pointer sticks out of the bubble, measured to the tip before rounding. */
internal val TooltipPointerHeight = 5.dp

/** How much the tip is rounded: the radius of the arc its curve stands in for. */
private val TooltipPointerTipRounding = 1.5.dp

/** The physical side of the bubble a pointer sits on, after Start and End are resolved. */
internal enum class JengaTooltipPointerSide { Left, Top, Right, Bottom }

/**
 * Where a pointer ends up inside a tooltip of a given size, in pixels.
 *
 * @property side the physical side of the bubble the pointer sits on.
 * @property bubble the bubble's bounds: the whole tooltip minus the pointer's height on [side].
 * @property center the pointer's centre along the edge: an x for [JengaTooltipPointerSide.Top] and
 *   [JengaTooltipPointerSide.Bottom], a y for the other two.
 * @property halfWidth half the pointer's base.
 * @property height how far the pointer sticks out of the bubble.
 */
internal class JengaTooltipPointerGeometry(
    val side: JengaTooltipPointerSide,
    val bubble: Rect,
    val center: Float,
    val halfWidth: Float,
    val height: Float,
)

/**
 * The outline of a tooltip with a pointer: [bubbleShape] and the pointer's triangle joined into
 * one path, so a fill, a shadow or a border follows both as a single shape.
 *
 * The shape covers the whole tooltip, pointer included; the bubble takes the size that is left
 * after the pointer's height is removed from the pointer's side.
 */
internal class JengaTooltipPointerShape(
    private val bubbleShape: Shape,
    private val pointer: JengaTooltipPointer,
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val geometry = geometry(size, layoutDirection, density)
        val bubbleOutline = bubbleShape.createOutline(geometry.bubble.size, layoutDirection, density)
        val bubblePath = Path().apply {
            addOutline(bubbleOutline)
            translate(geometry.bubble.topLeft)
        }
        return Outline.Generic(Path.combine(PathOperation.Union, bubblePath, pointerPath(geometry, density)))
    }

    /** Resolves the pointer's side, centre and size for a tooltip of [size]. */
    fun geometry(size: Size, layoutDirection: LayoutDirection, density: Density): JengaTooltipPointerGeometry {
        val rtl = layoutDirection == LayoutDirection.Rtl
        val side = when (pointer.edge) {
            JengaTooltipPointerEdge.Top -> JengaTooltipPointerSide.Top
            JengaTooltipPointerEdge.Bottom -> JengaTooltipPointerSide.Bottom
            JengaTooltipPointerEdge.Start -> if (rtl) JengaTooltipPointerSide.Right else JengaTooltipPointerSide.Left
            JengaTooltipPointerEdge.End -> if (rtl) JengaTooltipPointerSide.Left else JengaTooltipPointerSide.Right
        }
        val horizontalEdge = side == JengaTooltipPointerSide.Top || side == JengaTooltipPointerSide.Bottom
        // Rounded the same way as the padding that makes room for the pointer.
        val height = with(density) { TooltipPointerHeight.roundToPx() }.toFloat()
            .coerceIn(0f, if (horizontalEdge) size.height else size.width)
        val bubble = when (side) {
            JengaTooltipPointerSide.Top -> Rect(0f, height, size.width, size.height)
            JengaTooltipPointerSide.Bottom -> Rect(0f, 0f, size.width, size.height - height)
            JengaTooltipPointerSide.Left -> Rect(height, 0f, size.width, size.height)
            JengaTooltipPointerSide.Right -> Rect(0f, 0f, size.width - height, size.height)
        }
        val edgeStart = if (horizontalEdge) bubble.left else bubble.top
        val edgeEnd = if (horizontalEdge) bubble.right else bubble.bottom
        val halfWidth = min(with(density) { TooltipPointerWidth.toPx() } / 2f, (edgeEnd - edgeStart) / 2f).coerceAtLeast(0f)

        val (insetStart, insetEnd) = cornerInsets(side, bubble.size, layoutDirection, density)
        val requested = when {
            !pointer.offset.isSpecified -> (edgeStart + edgeEnd) / 2f
            horizontalEdge && rtl -> edgeEnd - with(density) { pointer.offset.toPx() }
            else -> edgeStart + with(density) { pointer.offset.toPx() }
        }
        // Keep the whole base on the straight part of the edge. When the straight part is shorter
        // than the base (a pill, say), centre the pointer on it.
        val lowest = edgeStart + insetStart + halfWidth
        val highest = edgeEnd - insetEnd - halfWidth
        val center = if (lowest <= highest) requested.coerceIn(lowest, highest) else (lowest + highest) / 2f
        return JengaTooltipPointerGeometry(side, bubble, center, halfWidth, height)
    }

    /** The pointer alone: the triangle with its rounded tip, reaching its own height into the bubble. */
    fun pointerPath(geometry: JengaTooltipPointerGeometry, density: Density): Path {
        val path = Path()
        val half = geometry.halfWidth
        val height = geometry.height
        if (half <= 0f || height <= 0f) return path

        // Unit vectors: `out` points away from the bubble, `along` runs along the edge.
        val out: Offset
        val along: Offset
        val base: Offset
        val bubble = geometry.bubble
        when (geometry.side) {
            JengaTooltipPointerSide.Top -> {
                out = Offset(0f, -1f)
                along = Offset(1f, 0f)
                base = Offset(geometry.center, bubble.top)
            }
            JengaTooltipPointerSide.Bottom -> {
                out = Offset(0f, 1f)
                along = Offset(1f, 0f)
                base = Offset(geometry.center, bubble.bottom)
            }
            JengaTooltipPointerSide.Left -> {
                out = Offset(-1f, 0f)
                along = Offset(0f, 1f)
                base = Offset(bubble.left, geometry.center)
            }
            JengaTooltipPointerSide.Right -> {
                out = Offset(1f, 0f)
                along = Offset(0f, 1f)
                base = Offset(bubble.right, geometry.center)
            }
        }
        val first = base - along * half
        val second = base + along * half
        val tip = base + out * height
        // The base reaches into the bubble by the pointer's height, so nothing shows between them
        // even where the bubble's corners curve away under the ends of the base.
        val depth = if (out.x == 0f) bubble.height else bubble.width
        val overlap = out * -min(height, depth)

        val sideLength = sqrt(half * half + height * height)
        // Distance from the tip to where an arc of that radius would touch each side.
        val rounding = min(with(density) { TooltipPointerTipRounding.toPx() } * height / half, sideLength / 2f)

        path.moveTo(first + overlap)
        path.lineTo(first)
        if (rounding > 0f) {
            path.lineTo(tip + (first - tip) * (rounding / sideLength))
            val end = tip + (second - tip) * (rounding / sideLength)
            path.quadraticTo(tip.x, tip.y, end.x, end.y)
        } else {
            path.lineTo(tip)
        }
        path.lineTo(second)
        path.lineTo(second + overlap)
        path.close()
        return path
    }

    /**
     * How much of the edge on [side] is taken by the corners at its two ends, in the order the
     * edge is walked (left to right, or top to bottom).
     */
    private fun cornerInsets(
        side: JengaTooltipPointerSide,
        bubbleSize: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Pair<Float, Float> {
        val outline = bubbleShape.createOutline(bubbleSize, layoutDirection, density)
        if (outline is Outline.Rounded) {
            val r = outline.roundRect
            return when (side) {
                JengaTooltipPointerSide.Top -> r.topLeftCornerRadius.x to r.topRightCornerRadius.x
                JengaTooltipPointerSide.Bottom -> r.bottomLeftCornerRadius.x to r.bottomRightCornerRadius.x
                JengaTooltipPointerSide.Left -> r.topLeftCornerRadius.y to r.bottomLeftCornerRadius.y
                JengaTooltipPointerSide.Right -> r.topRightCornerRadius.y to r.bottomRightCornerRadius.y
            }
        }
        // A rectangle, or a shape whose corners cannot be read: the whole edge is usable.
        return 0f to 0f
    }
}

private fun Path.moveTo(point: Offset) = moveTo(point.x, point.y)

private fun Path.lineTo(point: Offset) = lineTo(point.x, point.y)

/**
 * Whether this platform draws an elevation shadow for an outline that is not convex, which a
 * bubble joined to its pointer is. False on Android 9 (API 28) and older, where such an outline
 * gets no shadow at all.
 */
internal expect val elevationShadowFollowsConcaveOutlines: Boolean
