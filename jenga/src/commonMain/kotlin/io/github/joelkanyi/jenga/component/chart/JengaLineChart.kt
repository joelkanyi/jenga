package io.github.joelkanyi.jenga.component.chart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * One point of a [JengaLineChart]: a [label] under the point and its [value], which may be
 * negative but must be a finite number.
 */
@Poko
@Immutable
public class JengaLineChartEntry(
    public val label: String,
    public val value: Float,
)

/**
 * Resolved colors for a [JengaLineChart]. Override via [JengaLineChartDefaults.colors].
 *
 * @property line the line through the points.
 * @property point the outline of an ordinary point.
 * @property pointFill the inside of an ordinary point; the color of the surface the chart sits
 *   on makes the point look hollow.
 * @property selectedPoint the emphasised point (the selected one, or the latest when none is
 *   selected), which is filled.
 * @property gridline the horizontal gridlines.
 * @property label the label under a point.
 * @property selectedLabel the label under the selected point.
 * @property targetLine the dashed target line.
 * @property targetLabel the label at the end of the target line.
 */
@Poko
@Immutable
public class JengaLineChartColors(
    public val line: Color,
    public val point: Color,
    public val pointFill: Color,
    public val selectedPoint: Color,
    public val gridline: Color,
    public val label: Color,
    public val selectedLabel: Color,
    public val targetLine: Color,
    public val targetLabel: Color,
) {
    public fun copy(
        line: Color = this.line,
        point: Color = this.point,
        pointFill: Color = this.pointFill,
        selectedPoint: Color = this.selectedPoint,
        gridline: Color = this.gridline,
        label: Color = this.label,
        selectedLabel: Color = this.selectedLabel,
        targetLine: Color = this.targetLine,
        targetLabel: Color = this.targetLabel,
    ): JengaLineChartColors = JengaLineChartColors(
        line = line,
        point = point,
        pointFill = pointFill,
        selectedPoint = selectedPoint,
        gridline = gridline,
        label = label,
        selectedLabel = selectedLabel,
        targetLine = targetLine,
        targetLabel = targetLabel,
    )
}

/** Defaults and token mappings for [JengaLineChart]. */
public object JengaLineChartDefaults {
    /** Height of the plot: the area the line, points and gridlines are drawn in. */
    public val PlotHeight: Dp = 120.dp

    /** Thickness of the line. */
    public val LineWidth: Dp = 3.dp

    /** Radius of an ordinary point, to its outer edge. */
    public val PointRadius: Dp = 5.dp

    /** Radius of the emphasised point, to its outer edge. */
    public val SelectedPointRadius: Dp = 7.dp

    /** How many horizontal gridlines the plot has. */
    public const val GridlineCount: Int = 3

    /** Gap between the plot and the labels. */
    public val labelSpacing: Dp
        @Composable get() = JengaTheme.spacing.xs

    /** Padding around the chart. */
    public val contentPadding: PaddingValues
        @Composable get() = PaddingValues()

    /** Label text style. */
    public val labelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption

    /** Label text style under the selected point. */
    public val selectedLabelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold)

    /** Text style of the label at the end of the target line. */
    public val targetLabelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption

    /** How points move when the data changes. */
    public val animationSpec: AnimationSpec<Float>
        @Composable get() = tween(JengaTheme.motion.durationSlow, easing = JengaTheme.motion.standard)

    /**
     * Themed colors: a brand line with hollow points on `surface`, the emphasised point filled
     * with the brand color, and gridlines in `border`.
     */
    @Composable
    public fun colors(): JengaLineChartColors {
        val c = JengaTheme.colors
        return JengaLineChartColors(
            line = c.brand,
            point = c.brand,
            pointFill = c.surface,
            selectedPoint = c.brand,
            gridline = c.border,
            label = c.textSecondary,
            selectedLabel = c.textPrimary,
            targetLine = c.textMuted,
            targetLabel = c.textMuted,
        )
    }
}

/** Thickness of the outline of an ordinary point. */
private val PointStrokeWidth: Dp = 2.dp

/** Thickness of a gridline. */
private val GridlineWidth: Dp = 1.dp

/**
 * A line chart: one line through a point per entry, with each entry's label underneath and
 * light horizontal gridlines behind. One point is emphasised (drawn larger and filled): the
 * selected one, or the latest when none is selected. Points move when the data changes.
 * With [onSelect] each point's whole column is a tap target and the chart reads as a group of
 * selectable points; without it the chart is static. There are no axes, numbers or legend; put
 * the figure the chart refers to in text next to it.
 *
 * The points share the chart's width equally. With many points on a narrow chart a point's
 * column, and so its tap target, is narrower than 48dp, and long labels are cut short: keep the
 * series short (about 6 points on a phone) or the labels to a letter or two.
 *
 * The scale runs from the smallest to the largest value shown (the entries and the [target]),
 * not from zero, so a small change is visible; [minValue] and [maxValue] fix either end. This
 * automatic scale stretches the data to the full height of the plot, which exaggerates small
 * changes; pass [minValue] and [maxValue] for a fixed scale. When every value is the same the
 * line is flat at mid-height. In a right-to-left layout the first entry is at the right.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaLineChartSample
 *
 * @param entries the points, in display order; values may be negative but must be finite.
 * @param pointContentDescription accessibility label for a point (e.g. "November, 86%").
 * @param modifier the [Modifier] for the chart.
 * @param contentDescription accessibility description of the chart as a whole (e.g.
 *   "Customers paying on time"). The [target]'s description is added after it.
 * @param selectedIndex the selected point, or null for none; the latest point is then the
 *   emphasised one. An index that is not a point selects and emphasises none.
 * @param onSelect called with a tapped point's index; null makes the chart static.
 * @param minValue the value at the bottom of the plot; the smallest entry by default. Lower
 *   entries are drawn at the bottom.
 * @param maxValue the value at the top of the plot; the largest entry by default. Higher
 *   entries are drawn at the top.
 * @param target an optional target line, as on [JengaBarChart]. A target outside the scale
 *   extends it, whatever [minValue] and [maxValue] are, so the line is always inside the plot.
 *   Its label never covers the line or a point; see [JengaChartTarget.label].
 * @param gridlineCount how many evenly spaced horizontal gridlines to draw; 0 for none. They
 *   are decorative and stand for no particular value.
 * @param plotHeight the height of the plot. Point centres keep the larger of [pointRadius] and
 *   [selectedPointRadius] away from its top and bottom, so no point is clipped. The plot keeps
 *   this height whatever height the chart is given: in less room the labels overflow at the
 *   bottom.
 * @param lineWidth the thickness of the line.
 * @param pointRadius the radius of an ordinary point, to its outer edge; 0 draws none.
 * @param selectedPointRadius the radius of the emphasised point, to its outer edge.
 * @param labelSpacing the gap between the plot and the labels.
 * @param contentPadding padding around the chart.
 * @param labelStyle the label text style.
 * @param selectedLabelStyle the label text style under the selected point.
 * @param targetLabelStyle the text style of the [target]'s label.
 * @param colors the color set; defaults to [JengaLineChartDefaults.colors].
 * @param animationSpec how points move when the data changes.
 * @param label the content under each point, given the entry and whether it is selected.
 */
@Composable
public fun JengaLineChart(
    entries: List<JengaLineChartEntry>,
    pointContentDescription: (JengaLineChartEntry) -> String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    selectedIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null,
    minValue: Float? = null,
    maxValue: Float? = null,
    target: JengaChartTarget? = null,
    gridlineCount: Int = JengaLineChartDefaults.GridlineCount,
    plotHeight: Dp = JengaLineChartDefaults.PlotHeight,
    lineWidth: Dp = JengaLineChartDefaults.LineWidth,
    pointRadius: Dp = JengaLineChartDefaults.PointRadius,
    selectedPointRadius: Dp = JengaLineChartDefaults.SelectedPointRadius,
    labelSpacing: Dp = JengaLineChartDefaults.labelSpacing,
    contentPadding: PaddingValues = JengaLineChartDefaults.contentPadding,
    labelStyle: TextStyle = JengaLineChartDefaults.labelStyle,
    selectedLabelStyle: TextStyle = JengaLineChartDefaults.selectedLabelStyle,
    targetLabelStyle: TextStyle = JengaLineChartDefaults.targetLabelStyle,
    colors: JengaLineChartColors = JengaLineChartDefaults.colors(),
    animationSpec: AnimationSpec<Float> = JengaLineChartDefaults.animationSpec,
    label: @Composable (entry: JengaLineChartEntry, selected: Boolean) -> Unit = { entry, selected ->
        JengaText(
            text = entry.label,
            style = if (selected) selectedLabelStyle else labelStyle,
            color = if (selected) colors.selectedLabel else colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
) {
    require(entries.all { it.value.isFinite() }) { "JengaLineChart values must be finite" }
    require(minValue == null || minValue.isFinite()) { "minValue must be finite" }
    require(maxValue == null || maxValue.isFinite()) { "maxValue must be finite" }
    require(minValue == null || maxValue == null || minValue <= maxValue) { "minValue must not exceed maxValue" }
    require(target == null || target.value.isFinite()) { "JengaLineChart target must be finite" }
    require(gridlineCount >= 0) { "gridlineCount must not be negative" }

    val range = lineChartRange(
        entriesMin = entries.minOfOrNull { it.value },
        entriesMax = entries.maxOfOrNull { it.value },
        minValue = minValue,
        maxValue = maxValue,
        target = target?.value,
    )
    val fractions = entries.map { lineChartFraction(it.value, range.start, range.endInclusive) }
    val animated: List<State<Float>> = fractions.mapIndexed { index, fraction ->
        key(index) { animateFloatAsState(fraction, animationSpec, label = "point") }
    }
    val emphasised = emphasisedPointIndex(selectedIndex, entries.size)

    // Points and the line keep clear of the plot's top and bottom edges by the largest radius
    // drawn, so nothing is clipped; what is left of the plot is the scale.
    val largestRadius = max(max(pointRadius, selectedPointRadius), lineWidth / 2)
    val scaleHeight = (plotHeight - largestRadius * 2).coerceAtLeast(0.dp)
    val scaleBottom = (plotHeight - scaleHeight) / 2

    val description = chartDescription(contentDescription, target)
    val descriptionModifier = if (description != null) {
        Modifier.semantics { this.contentDescription = description }
    } else {
        Modifier
    }
    val chart: @Composable (Modifier) -> Unit = { chartModifier ->
        Row(
            modifier = chartModifier
                .fillMaxWidth()
                // The plot is never squeezed: in a height too small for it the labels overflow
                // at the bottom, and the drawing stays in step with the columns it is behind.
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .padding(contentPadding)
                .selectableGroup()
                // Behind the points' columns, and with no semantics of its own.
                .drawBehind {
                    // Whole pixels, as the columns' share of the plot is laid out.
                    val plot = plotHeight.roundToPx().toFloat()
                    drawGridlines(gridlineCount, plot, colors.gridline)
                    drawSeries(
                        fractions = animated,
                        emphasised = emphasised,
                        scaleBottomY = plot - scaleBottom.toPx(),
                        scaleHeight = scaleHeight.toPx(),
                        lineWidth = lineWidth.toPx(),
                        pointRadius = pointRadius.toPx(),
                        selectedPointRadius = selectedPointRadius.toPx(),
                        colors = colors,
                    )
                },
        ) {
            if (entries.isEmpty()) {
                // Keeps the plot's room, so the gridlines and the target still show.
                Spacer(modifier = Modifier.chartBaseline().weight(1f).height(plotHeight))
            }
            entries.forEachIndexed { index, entry ->
                PointSlot(
                    entry = entry,
                    selected = index == selectedIndex,
                    onClick = onSelect?.let { { it(index) } },
                    contentDescription = pointContentDescription(entry),
                    plotHeight = plotHeight,
                    labelSpacing = labelSpacing,
                    label = label,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
    // The description goes on the outermost node, with or without a target.
    if (target == null) {
        chart(modifier.then(descriptionModifier))
    } else {
        ChartTargetLayout(
            target = target,
            // The same mapping as a point's height, so a point at the target value sits on the line.
            lineHeight = scaleBottom + scaleHeight * lineChartFraction(target.value, range.start, range.endInclusive),
            chartTop = plotHeight,
            occupiedUnderLabel = seriesUnderTargetLabel(
                // Where the points settle, so the label does not move while they animate, and
                // the largest radius for each, so it does not move with the selection either.
                pointHeights = fractions.map { scaleBottom + scaleHeight * it },
                pointRadius = largestRadius,
                lineWidth = lineWidth,
            ),
            contentPadding = contentPadding,
            lineColor = colors.targetLine,
            labelColor = colors.targetLabel,
            labelStyle = targetLabelStyle,
            modifier = modifier.then(descriptionModifier),
        ) {
            chart(Modifier)
        }
    }
}

/**
 * The values at the bottom and the top of a line chart's plot: [minValue] and [maxValue], or
 * without them the smallest and largest entry ([entriesMin], [entriesMax]), then widened to
 * include [target]. With nothing to show the range is 0..0.
 */
internal fun lineChartRange(
    entriesMin: Float?,
    entriesMax: Float?,
    minValue: Float?,
    maxValue: Float?,
    target: Float?,
): ClosedFloatingPointRange<Float> {
    var low = minValue ?: entriesMin ?: target ?: 0f
    var high = maxValue ?: entriesMax ?: target ?: low
    // One fixed end beyond every entry: the entries are all clamped to it.
    if (low > high) {
        if (minValue != null) high = low else low = high
    }
    if (target != null) {
        low = minOf(low, target)
        high = maxOf(high, target)
    }
    return low..high
}

/**
 * How far up the scale [value] sits, from 0 ([min]) to 1 ([max]); values outside are clamped.
 * When [min] and [max] are equal there is no scale: a value equal to them sits at mid-height
 * (0.5), a higher one at the top and a lower one at the bottom.
 */
internal fun lineChartFraction(value: Float, min: Float, max: Float): Float {
    val span = max - min
    return when {
        span > 0f -> ((value - min) / span).coerceIn(0f, 1f)
        value > max -> 1f
        value < min -> 0f
        else -> 0.5f
    }
}

/**
 * The point drawn larger and filled among [count] points: the selected one, or the latest when
 * nothing is selected; null when there are no points or [selectedIndex] is not one of them.
 */
internal fun emphasisedPointIndex(selectedIndex: Int?, count: Int): Int? = (selectedIndex ?: (count - 1)).takeIf { it in 0 until count }

/**
 * How far above the baseline a line chart's line and points reach under a target label
 * [labelWidth] wide at the end edge of a plot [plotWidth] wide, or 0 when none of them is under
 * it. The points are [pointHeights] above the baseline (centres), [pointRadius] in radius, at
 * the centres of equal slots across the plot, and joined by a line [lineHalfWidth] thick each
 * side of its centre. All values in px; an infinite [plotWidth] counts every point.
 */
internal fun seriesTopUnderTargetLabel(
    pointHeights: List<Float>,
    pointRadius: Float,
    lineHalfWidth: Float,
    plotWidth: Float,
    labelWidth: Float,
): Float {
    val count = pointHeights.size
    if (count == 0 || labelWidth <= 0f) return 0f
    val clearance = maxOf(pointRadius, lineHalfWidth)
    val pitch = plotWidth / count
    if (!pitch.isFinite() || pitch <= 0f) return pointHeights.max() + clearance
    // Distances from the start edge: the label covers everything from labelStart to the end.
    val labelStart = plotWidth - labelWidth
    var top = 0f
    pointHeights.forEachIndexed { index, height ->
        val x = (index + 0.5f) * pitch
        if (x + clearance > labelStart) top = maxOf(top, height + clearance)
    }
    // Where the line enters the label's span, between two points.
    val firstX = 0.5f * pitch
    val lastX = (count - 0.5f) * pitch
    if (count > 1 && labelStart > firstX && labelStart < lastX) {
        val segment = floor((labelStart - firstX) / pitch).toInt().coerceIn(0, count - 2)
        val from = pointHeights[segment]
        val to = pointHeights[segment + 1]
        val slope = (to - from) / pitch
        val height = from + slope * (labelStart - (segment + 0.5f) * pitch)
        // A sloping line is thicker measured straight up than across.
        top = maxOf(top, height + lineHalfWidth * sqrt(1f + slope * slope))
    }
    return top
}

/**
 * [seriesTopUnderTargetLabel] for [ChartTargetLayout]: the label keeps [ChartTargetLabelGap]
 * clear of the line and points beside it, as it does of those under it.
 */
private fun seriesUnderTargetLabel(
    pointHeights: List<Dp>,
    pointRadius: Dp,
    lineWidth: Dp,
): Density.(labelWidth: Int, plotWidth: Int) -> Int = { labelWidth, plotWidth ->
    ceil(
        seriesTopUnderTargetLabel(
            pointHeights = pointHeights.map { it.toPx() },
            pointRadius = pointRadius.toPx(),
            lineHalfWidth = lineWidth.toPx() / 2f,
            plotWidth = if (plotWidth == Constraints.Infinity) Float.POSITIVE_INFINITY else plotWidth.toFloat(),
            labelWidth = labelWidth + ChartTargetLabelGap.toPx(),
        ),
    ).toInt()
}

/** Draws [count] evenly spaced horizontal lines across a plot [plotHeight] px tall, none at its edges. */
private fun DrawScope.drawGridlines(count: Int, plotHeight: Float, color: Color) {
    val stroke = GridlineWidth.toPx()
    for (line in 1..count) {
        // On whole pixels, so every gridline is equally crisp.
        val y = floor(plotHeight * line / (count + 1) - stroke / 2f) + stroke / 2f
        drawLine(color = color, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = stroke)
    }
}

/**
 * Draws the line and its points: point `i` at the centre of the `i`th of equal slots across the
 * width (from the right in a right-to-left layout), [fractions]`[i]` of [scaleHeight] above
 * [scaleBottomY]. The [emphasised] point is filled and drawn last, over its neighbours.
 */
private fun DrawScope.drawSeries(
    fractions: List<State<Float>>,
    emphasised: Int?,
    scaleBottomY: Float,
    scaleHeight: Float,
    lineWidth: Float,
    pointRadius: Float,
    selectedPointRadius: Float,
    colors: JengaLineChartColors,
) {
    val count = fractions.size
    if (count == 0) return
    val pitch = size.width / count
    val rtl = layoutDirection == LayoutDirection.Rtl
    val centers = List(count) { index ->
        val x = (index + 0.5f) * pitch
        Offset(if (rtl) size.width - x else x, scaleBottomY - scaleHeight * fractions[index].value)
    }
    if (count > 1 && lineWidth > 0f) {
        val path = Path()
        centers.forEachIndexed { index, center ->
            if (index == 0) path.moveTo(center.x, center.y) else path.lineTo(center.x, center.y)
        }
        drawPath(
            path = path,
            color = colors.line,
            style = Stroke(width = lineWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
    if (pointRadius > 0f) {
        val stroke = PointStrokeWidth.toPx().coerceAtMost(pointRadius)
        centers.forEachIndexed { index, center ->
            if (index != emphasised) {
                drawCircle(color = colors.pointFill, radius = pointRadius, center = center)
                drawCircle(
                    color = colors.point,
                    radius = pointRadius - stroke / 2f,
                    center = center,
                    style = Stroke(width = stroke),
                )
            }
        }
    }
    if (emphasised != null && selectedPointRadius > 0f) {
        drawCircle(color = colors.selectedPoint, radius = selectedPointRadius, center = centers[emphasised])
    }
}

/** One point's column: the room its share of the plot takes, then its label. The point itself is drawn by the chart. */
@Composable
private fun PointSlot(
    entry: JengaLineChartEntry,
    selected: Boolean,
    onClick: (() -> Unit)?,
    contentDescription: String,
    plotHeight: Dp,
    labelSpacing: Dp,
    label: @Composable (JengaLineChartEntry, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.selectable(selected = selected, role = Role.Tab, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.chartBaseline().fillMaxWidth().height(plotHeight))
        Box(modifier = Modifier.padding(top = labelSpacing)) {
            label(entry, selected)
        }
    }
}
