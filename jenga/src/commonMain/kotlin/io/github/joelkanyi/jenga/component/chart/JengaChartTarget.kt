package io.github.joelkanyi.jenga.component.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A target for a chart: a dashed horizontal line across the plot at [value], on the same scale
 * as the data. The chart always scales to include it, so the line never leaves the plot.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBarChartTargetSample
 *
 * @property value where the line sits, in the same unit as the chart's entries.
 * @property label optional short text shown above the line's end, e.g. "Target"; null or empty
 *   shows none. It never covers a bar: where a bar under it is taller than the line, the label
 *   sits just above that bar instead (in a scrolling chart, above the tallest bar).
 * @property contentDescription what screen readers hear for the target, e.g. "Target 40,000";
 *   it is added to the chart's description. Defaults to [label]; null leaves the target out.
 */
@Poko
@Immutable
public class JengaChartTarget(
    public val value: Float,
    public val label: String? = null,
    public val contentDescription: String? = label,
)

/** Thickness of a chart's target line. */
internal val ChartTargetLineWidth: Dp = 1.dp

/** Length of each dash, and of each gap, of a chart's target line. */
private val ChartTargetDash: Dp = 4.dp

/** Gap between a chart's target line and the label above it. */
private val ChartTargetLabelGap: Dp = 2.dp

/** The y position that target heights are measured up from: the bottom of a bar chart's bars. */
internal val ChartBaseline: HorizontalAlignmentLine = HorizontalAlignmentLine(::max)

/** Reports the bottom edge of this element as the [ChartBaseline] of the chart it is in. */
internal fun Modifier.chartBaseline(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height, mapOf(ChartBaseline to placeable.height)) {
        placeable.place(0, 0)
    }
}

/**
 * The value drawn at full plot height: [maxValue], or the largest entry ([entriesMax]) without
 * one, raised to [target] when the target is higher. Without a target this is the chart's scale
 * as it was before targets existed.
 */
internal fun chartScaleMax(entriesMax: Float?, maxValue: Float?, target: Float?): Float {
    val max = maxValue ?: entriesMax ?: 0f
    return if (target != null && target > max) target else max
}

/** A chart's description for screen readers: its own [contentDescription], then the target's. */
internal fun chartDescription(contentDescription: String?, target: JengaChartTarget?): String? = listOfNotNull(contentDescription, target?.contentDescription)
    .filter { it.isNotEmpty() }
    .joinToString(", ")
    .ifEmpty { null }

/** Test tag of the label above a chart's target line; the label has no other semantics. */
internal const val ChartTargetLabelTestTag: String = "JengaChartTargetLabel"

/**
 * How many of a chart's last slots lie under a target label [labelWidth] wide at the end edge,
 * when [slotCount] equal slots share [plotWidth] with [spacing] between them. All values in px.
 */
internal fun slotsUnderTargetLabel(slotCount: Int, plotWidth: Float, spacing: Float, labelWidth: Float): Int {
    if (slotCount <= 0 || labelWidth <= 0f) return 0
    val pitch = (plotWidth + spacing) / slotCount
    if (pitch <= 0f) return slotCount
    // The slot j places from the end starts j pitches from the end edge.
    var count = 0
    while (count < slotCount && count * pitch < labelWidth) count++
    return count
}

/**
 * Lays [chart] out with a dashed target line behind it and the target's label above the line's
 * end. The line sits [lineHeight] above the chart's [ChartBaseline] (its bottom edge when the
 * chart reports none) and spans the chart's width inside [contentPadding].
 *
 * The label never covers a bar: when a bar under it is taller than the line, the label sits just
 * above that bar instead. [barTops] gives how far above the baseline each bar's slot is occupied
 * once its animation has settled, so the label does not move while bars animate. With
 * [slotsShareWidth] (bars fill the width) the bars under the label are those whose slot overlaps
 * it horizontally; otherwise (a scrolling chart) every bar counts, so the label stays put while
 * the bars scroll under it.
 *
 * The layout grows upward only as far as needed to keep the line and its label inside its
 * bounds; with a bounded height it gives the chart that much less room instead, and never
 * reports a height outside its constraints.
 *
 * [chart] must emit exactly one layout.
 */
@Composable
internal fun ChartTargetLayout(
    target: JengaChartTarget,
    lineHeight: Dp,
    barTops: List<Dp>,
    slotsShareWidth: Boolean,
    slotSpacing: Dp,
    contentPadding: PaddingValues,
    lineColor: Color,
    labelColor: Color,
    labelStyle: TextStyle,
    modifier: Modifier = Modifier,
    chart: @Composable () -> Unit,
) {
    val labelText = target.label?.takeIf { it.isNotEmpty() }
    Layout(
        content = {
            // First, so the chart draws over the line.
            Box(
                modifier = Modifier.drawBehind {
                    val dash = ChartTargetDash.toPx()
                    val y = size.height / 2f
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = size.height,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)),
                    )
                },
            )
            chart()
            if (labelText != null) {
                // The target is read out with the chart's description, not as a stray word.
                Box(modifier = Modifier.clearAndSetSemantics { testTag = ChartTargetLabelTestTag }) {
                    JengaText(
                        text = labelText,
                        style = labelStyle,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val isLtr = layoutDirection == LayoutDirection.Ltr
        val startPx = contentPadding.calculateStartPadding(layoutDirection).roundToPx()
        val endPx = contentPadding.calculateEndPadding(layoutDirection).roundToPx()
        val leftPx = if (isLtr) startPx else endPx
        val rightPx = if (isLtr) endPx else startPx
        val strokePx = ChartTargetLineWidth.roundToPx().coerceAtLeast(1)
        val gapPx = ChartTargetLabelGap.roundToPx()
        val lineHeightPx = lineHeight.toPx().roundToInt()

        // The chart fills the width it is given, so the label can be measured and placed first.
        val plotWidth = if (constraints.hasBoundedWidth) {
            (constraints.maxWidth - leftPx - rightPx).coerceAtLeast(0)
        } else {
            Constraints.Infinity
        }
        val labelPlaceable = measurables.getOrNull(2)?.measure(Constraints(maxWidth = plotWidth))

        // Heights above the baseline, in px, of the tallest bar and of the tallest bar under the label.
        val tallestBar = barTops.maxOfOrNull { it.toPx() }?.roundToInt() ?: 0
        val barUnderLabel = when {
            labelPlaceable == null -> 0
            slotsShareWidth && plotWidth != Constraints.Infinity -> {
                val under = slotsUnderTargetLabel(
                    slotCount = barTops.size,
                    plotWidth = plotWidth.toFloat(),
                    spacing = slotSpacing.toPx(),
                    labelWidth = labelPlaceable.width.toFloat(),
                )
                barTops.takeLast(under).maxOfOrNull { it.toPx() }?.roundToInt() ?: 0
            }
            else -> tallestBar
        }
        // Distances above the baseline: the line's top edge, the label's bottom, and the top of it all.
        val lineTopAbove = lineHeightPx + strokePx / 2
        val labelBottomAbove = max(lineTopAbove, barUnderLabel) + gapPx
        val topAbove = labelPlaceable?.let { labelBottomAbove + it.height } ?: lineTopAbove

        // With a bounded height the room above the bars comes out of the chart's own height.
        val reserved = (topAbove - tallestBar).coerceAtLeast(0)
        val chartConstraints = if (constraints.hasBoundedHeight) {
            constraints.copy(
                minHeight = (constraints.minHeight - reserved).coerceAtLeast(0),
                maxHeight = (constraints.maxHeight - reserved).coerceAtLeast(0),
            )
        } else {
            constraints
        }
        val chartPlaceable = measurables[1].measure(chartConstraints)
        val lineWidthPx = (chartPlaceable.width - leftPx - rightPx).coerceAtLeast(0)
        val linePlaceable = measurables[0].measure(Constraints.fixed(lineWidthPx, strokePx))

        val reported = chartPlaceable[ChartBaseline]
        val baseline = if (reported == AlignmentLine.Unspecified) chartPlaceable.height else reported
        val headroom = (topAbove - baseline).coerceAtLeast(0)
        val height = constraints.constrainHeight(chartPlaceable.height + headroom)
        // The chart keeps to the bottom, so any extra height is room above it. In a height too
        // small for everything, the line and label stay inside and the chart's bottom overflows.
        val chartY = (height - chartPlaceable.height).coerceAtLeast(headroom)
        val baselineY = chartY + baseline

        layout(chartPlaceable.width, height) {
            linePlaceable.place(leftPx, baselineY - lineTopAbove)
            chartPlaceable.place(0, chartY)
            labelPlaceable?.let { label ->
                val x = if (isLtr) chartPlaceable.width - rightPx - label.width else leftPx
                label.place(x, baselineY - labelBottomAbove - label.height)
            }
        }
    }
}
