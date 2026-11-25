package io.github.joelkanyi.jenga.component.chart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** One bar of a [JengaBarChart]: a [label] under the bar and a non-negative [value]. */
@Poko
@Immutable
public class JengaBarChartEntry(
    public val label: String,
    public val value: Float,
)

/** How a [JengaBarChart] lays its bars out. */
public sealed interface JengaBarChartArrangement {
    /** Bars share the chart width equally; for short series. */
    public data object Fill : JengaBarChartArrangement

    /** Each bar gets a fixed [slotWidth] and the chart scrolls horizontally; for long series. */
    @Poko
    public class Scroll(public val slotWidth: Dp) : JengaBarChartArrangement
}

/**
 * Resolved colors for a [JengaBarChart]. Override via [JengaBarChartDefaults.colors] or
 * [JengaBarChartDefaults.highlightColors].
 *
 * A bar is filled with the first of these that applies: [emptyBar] when it is one of the chart's
 * `emptyIndices`, [highlightedBar] when it is one of its `highlightedIndices`, then [bar] while
 * nothing is selected, [selectedBar] when it is the selected bar and [unselectedBar] otherwise.
 *
 * @property bar an ordinary bar while no bar is selected.
 * @property selectedBar the selected bar, unless it is highlighted or empty.
 * @property unselectedBar an ordinary bar while another bar is selected.
 * @property label the label under a bar.
 * @property selectedLabel the label under the selected bar.
 * @property highlightedBar a highlighted bar (e.g. today), whichever bar is selected.
 * @property emptyBar the stub drawn for an entry with no data yet. A low-contrast placeholder by
 *   design: it marks where a bar will be and carries no value, so it is not meant to meet 3:1.
 * @property selectedOutline a ring drawn around the selected bar, so the selection shows on a
 *   bar of any fill; transparent draws none.
 * @property targetLine the dashed target line.
 * @property targetLabel the label at the end of the target line.
 */
@Poko
@Immutable
public class JengaBarChartColors(
    public val bar: Color,
    public val selectedBar: Color,
    public val unselectedBar: Color,
    public val label: Color,
    public val selectedLabel: Color,
    public val highlightedBar: Color = bar,
    public val emptyBar: Color = unselectedBar,
    public val selectedOutline: Color = Color.Transparent,
    public val targetLine: Color = label,
    public val targetLabel: Color = label,
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(
        bar: Color,
        selectedBar: Color,
        unselectedBar: Color,
        label: Color,
        selectedLabel: Color,
    ) : this(
        bar = bar,
        selectedBar = selectedBar,
        unselectedBar = unselectedBar,
        label = label,
        selectedLabel = selectedLabel,
        highlightedBar = bar,
        emptyBar = unselectedBar,
        selectedOutline = Color.Transparent,
        targetLine = label,
        targetLabel = label,
    )

    public fun copy(
        bar: Color = this.bar,
        selectedBar: Color = this.selectedBar,
        unselectedBar: Color = this.unselectedBar,
        label: Color = this.label,
        selectedLabel: Color = this.selectedLabel,
        highlightedBar: Color = this.highlightedBar,
        emptyBar: Color = this.emptyBar,
        selectedOutline: Color = this.selectedOutline,
        targetLine: Color = this.targetLine,
        targetLabel: Color = this.targetLabel,
    ): JengaBarChartColors = JengaBarChartColors(
        bar = bar,
        selectedBar = selectedBar,
        unselectedBar = unselectedBar,
        label = label,
        selectedLabel = selectedLabel,
        highlightedBar = highlightedBar,
        emptyBar = emptyBar,
        selectedOutline = selectedOutline,
        targetLine = targetLine,
        targetLabel = targetLabel,
    )

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(
        bar: Color = this.bar,
        selectedBar: Color = this.selectedBar,
        unselectedBar: Color = this.unselectedBar,
        label: Color = this.label,
        selectedLabel: Color = this.selectedLabel,
    ): JengaBarChartColors = copy(
        bar = bar,
        selectedBar = selectedBar,
        unselectedBar = unselectedBar,
        label = label,
        selectedLabel = selectedLabel,
        highlightedBar = this.highlightedBar,
        emptyBar = this.emptyBar,
        selectedOutline = this.selectedOutline,
        targetLine = this.targetLine,
        targetLabel = this.targetLabel,
    )
}

/** Defaults and token mappings for [JengaBarChart]. */
public object JengaBarChartDefaults {
    /** Height of the tallest bar. */
    public val PlotHeight: Dp = 96.dp

    /** Height of a bar whose value is zero. */
    public val MinBarHeight: Dp = 0.dp

    /** Height of the stub drawn for an entry with no data yet. */
    public val EmptyBarHeight: Dp = 4.dp

    /** Widest a bar may be; the rest of its slot stays empty. */
    public val BarMaxWidth: Dp = 24.dp

    /** Default bar shape: a rounded tip and a square base. */
    public val barShape: Shape
        @Composable get() = JengaTheme.shapes.xs.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))

    /** Gap between bar slots. */
    public val barSpacing: Dp
        @Composable get() = JengaTheme.spacing.sm

    /** Gap between a bar and its label. */
    public val labelSpacing: Dp
        @Composable get() = JengaTheme.spacing.xs

    /** Padding around the chart. */
    public val contentPadding: PaddingValues
        @Composable get() = PaddingValues()

    /** Label text style. */
    public val labelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption

    /** Label text style under the selected bar. */
    public val selectedLabelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold)

    /** Text style of the label at the end of the target line. */
    public val targetLabelStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption

    /** How bar heights animate when the data changes. */
    public val animationSpec: AnimationSpec<Float>
        @Composable get() = tween(JengaTheme.motion.durationSlow, easing = JengaTheme.motion.standard)

    /**
     * Themed colors. With no selection every bar uses `bar`; once a bar is selected it uses
     * `selectedBar` and the others `unselectedBar`. The selection is shown by that change of
     * color, so there is no ring (`selectedOutline` is transparent).
     */
    @Composable
    public fun colors(): JengaBarChartColors {
        val c = JengaTheme.colors
        return JengaBarChartColors(
            bar = c.brand,
            selectedBar = c.brand,
            unselectedBar = c.borderStrong,
            label = c.textSecondary,
            selectedLabel = c.textPrimary,
            highlightedBar = c.brand,
            emptyBar = c.borderStrong,
            selectedOutline = Color.Transparent,
            targetLine = c.textMuted,
            targetLabel = c.textMuted,
        )
    }

    /**
     * Themed colors for a chart that highlights some of its bars (e.g. today): highlighted bars
     * are the brand color and ordinary bars a neutral, whichever bar is selected, and the
     * selected bar gets a ring around it instead of a change of color.
     *
     * Ordinary bars use `textMuted`, a neutral that meets 3:1 against `surface` in light and dark
     * themes, so each bar can be told from the card it sits on. Bars for entries with no data
     * yet (`emptyBar`) are a low-contrast placeholder by design.
     */
    @Composable
    public fun highlightColors(): JengaBarChartColors {
        val c = JengaTheme.colors
        return JengaBarChartColors(
            bar = c.textMuted,
            selectedBar = c.textMuted,
            unselectedBar = c.textMuted,
            label = c.textSecondary,
            selectedLabel = c.textPrimary,
            highlightedBar = c.brand,
            emptyBar = c.borderStrong,
            selectedOutline = c.textPrimary,
            targetLine = c.textMuted,
            targetLabel = c.textMuted,
        )
    }
}

/** Thickness of the ring around the selected bar. */
private val SelectedOutlineWidth: Dp = 2.dp

/** Gap between the selected bar and the ring around it. */
private val SelectedOutlineGap: Dp = 2.dp

/**
 * A vertical bar chart: one bar per entry, growing from a shared baseline, with its label
 * underneath. Bar heights are relative to [maxValue] and animate when the data changes.
 * With [onSelect] each bar's whole slot is a tap target and the chart reads as a group of
 * selectable bars; without it the chart is static. There are no axes; put the figure the
 * selection refers to next to the chart, or add a [valueLabel] at each bar's tip.
 *
 * A [target] draws a dashed line across the plot, behind the bars. [highlightedIndices] marks
 * bars (e.g. today) that keep their own color whichever bar is selected, and [emptyIndices]
 * marks entries with no data yet, drawn as a short stub. For a chart that uses them, pass
 * [JengaBarChartDefaults.highlightColors]: it shows the selected bar with a ring around it
 * rather than a change of color, so highlight and selection can be told apart.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBarChartSample
 * @sample io.github.joelkanyi.jenga.samples.JengaBarChartTargetSample
 *
 * @param entries the bars, in display order; values must not be negative.
 * @param barContentDescription accessibility label for a bar (e.g. "May, 12,000"). Say here
 *   what a highlighted or empty bar means (e.g. "Today, 48,200", "Friday, no data yet").
 * @param modifier the [Modifier] for the chart.
 * @param contentDescription accessibility description of the chart as a whole (e.g.
 *   "Collections this week"). The [target]'s description is added after it.
 * @param selectedIndex the selected bar, or null for none.
 * @param onSelect called with a tapped bar's index; null makes the chart static.
 * @param highlightedIndices the bars drawn in `highlightedBar`, independent of the selection.
 * @param emptyIndices the entries with no data yet: each is drawn as a stub [emptyBarHeight]
 *   tall in `emptyBar`, whatever its value, and can still be selected. Give such an entry the
 *   value 0: its value still counts toward the largest entry that sets the scale. An entry that
 *   is both empty and highlighted is drawn empty.
 * @param maxValue the value of a full-height bar; the largest entry by default.
 * @param target an optional target line. A target above [maxValue], or above every bar when
 *   [maxValue] is null, becomes the value of a full-height bar, so the line is always inside
 *   the plot. Its label never covers a bar; see [JengaChartTarget.label].
 * @param arrangement how bars are laid out; see [JengaBarChartArrangement].
 * @param plotHeight the height of a full-height bar.
 * @param minBarHeight the height of a zero-value bar.
 * @param emptyBarHeight the height of the stub for an entry in [emptyIndices]; at most [plotHeight].
 * @param barMaxWidth the widest a bar may be.
 * @param barSpacing the gap between bar slots.
 * @param barShape the bar shape.
 * @param labelSpacing the gap between a bar and its label.
 * @param contentPadding padding around the chart.
 * @param labelStyle the label text style.
 * @param selectedLabelStyle the label text style under the selected bar.
 * @param targetLabelStyle the text style of the [target]'s label.
 * @param colors the color set; defaults to [JengaBarChartDefaults.colors].
 * @param animationSpec how bar heights animate.
 * @param valueLabel optional content above each bar's tip, given the entry and whether it is
 *   selected. With a [target], the value label of a bar that ends near the target can sit on
 *   the line or under the target's label.
 * @param label the content under each bar, given the entry and whether it is selected.
 */
@Composable
public fun JengaBarChart(
    entries: List<JengaBarChartEntry>,
    barContentDescription: (JengaBarChartEntry) -> String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    selectedIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null,
    highlightedIndices: Set<Int> = emptySet(),
    emptyIndices: Set<Int> = emptySet(),
    maxValue: Float? = null,
    target: JengaChartTarget? = null,
    arrangement: JengaBarChartArrangement = JengaBarChartArrangement.Fill,
    plotHeight: Dp = JengaBarChartDefaults.PlotHeight,
    minBarHeight: Dp = JengaBarChartDefaults.MinBarHeight,
    emptyBarHeight: Dp = JengaBarChartDefaults.EmptyBarHeight,
    barMaxWidth: Dp = JengaBarChartDefaults.BarMaxWidth,
    barSpacing: Dp = JengaBarChartDefaults.barSpacing,
    barShape: Shape = JengaBarChartDefaults.barShape,
    labelSpacing: Dp = JengaBarChartDefaults.labelSpacing,
    contentPadding: PaddingValues = JengaBarChartDefaults.contentPadding,
    labelStyle: TextStyle = JengaBarChartDefaults.labelStyle,
    selectedLabelStyle: TextStyle = JengaBarChartDefaults.selectedLabelStyle,
    targetLabelStyle: TextStyle = JengaBarChartDefaults.targetLabelStyle,
    colors: JengaBarChartColors = JengaBarChartDefaults.colors(),
    animationSpec: AnimationSpec<Float> = JengaBarChartDefaults.animationSpec,
    valueLabel: (@Composable (entry: JengaBarChartEntry, selected: Boolean) -> Unit)? = null,
    label: @Composable (entry: JengaBarChartEntry, selected: Boolean) -> Unit = { entry, selected ->
        JengaText(
            text = entry.label,
            style = if (selected) selectedLabelStyle else labelStyle,
            color = if (selected) colors.selectedLabel else colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
) {
    require(entries.all { it.value >= 0f }) { "JengaBarChart values must not be negative" }
    require(minBarHeight <= plotHeight) { "minBarHeight must not exceed plotHeight" }
    require(target == null || target.value >= 0f) { "JengaBarChart target must not be negative" }
    val max = chartScaleMax(entries.maxOfOrNull { it.value }, maxValue, target?.value)
    val slot: @Composable (Int, JengaBarChartEntry, Modifier) -> Unit = { index, entry, slotModifier ->
        BarSlot(
            entry = entry,
            fraction = barFraction(entry.value, max),
            selected = index == selectedIndex,
            hasSelection = selectedIndex != null,
            highlighted = index in highlightedIndices,
            empty = index in emptyIndices,
            onClick = onSelect?.let { { it(index) } },
            contentDescription = barContentDescription(entry),
            plotHeight = plotHeight,
            minBarHeight = minBarHeight,
            emptyBarHeight = emptyBarHeight,
            barMaxWidth = barMaxWidth,
            barShape = barShape,
            labelSpacing = labelSpacing,
            colors = colors,
            animationSpec = animationSpec,
            valueLabel = valueLabel,
            label = label,
            modifier = slotModifier,
        )
    }
    // The room each bar takes up above the baseline once it has stopped animating; a ring color
    // keeps room for the ring above every bar.
    val outlineInset = selectedOutlineInset(colors)
    val barTops = entries.mapIndexed { index, entry ->
        outlineInset + barHeight(
            fraction = barFraction(entry.value, max),
            empty = index in emptyIndices,
            plotHeight = plotHeight,
            minBarHeight = minBarHeight,
            emptyBarHeight = emptyBarHeight,
        )
    }
    val description = chartDescription(contentDescription, target)
    val descriptionModifier = if (description != null) {
        Modifier.semantics { this.contentDescription = description }
    } else {
        Modifier
    }
    val chart: @Composable (Modifier) -> Unit = { chartModifier ->
        when (arrangement) {
            JengaBarChartArrangement.Fill -> Row(
                modifier = chartModifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(barSpacing),
                verticalAlignment = Alignment.Bottom,
            ) {
                entries.forEachIndexed { index, entry -> slot(index, entry, Modifier.weight(1f)) }
            }
            is JengaBarChartArrangement.Scroll -> LazyRow(
                modifier = chartModifier
                    .fillMaxWidth()
                    .selectableGroup(),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(barSpacing),
                verticalAlignment = Alignment.Bottom,
            ) {
                itemsIndexed(entries) { index, entry -> slot(index, entry, Modifier.width(arrangement.slotWidth)) }
            }
        }
    }
    // The description goes on the outermost node, with or without a target.
    if (target == null) {
        chart(modifier.then(descriptionModifier))
    } else {
        ChartTargetLayout(
            target = target,
            // The same mapping as a bar's height, so a bar at the target value just touches the line.
            lineHeight = minBarHeight + (plotHeight - minBarHeight) * barFraction(target.value, max),
            barTops = barTops,
            slotsShareWidth = arrangement == JengaBarChartArrangement.Fill,
            slotSpacing = barSpacing,
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

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaBarChart(
    entries: List<JengaBarChartEntry>,
    barContentDescription: (JengaBarChartEntry) -> String,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null,
    maxValue: Float? = null,
    arrangement: JengaBarChartArrangement = JengaBarChartArrangement.Fill,
    plotHeight: Dp = JengaBarChartDefaults.PlotHeight,
    minBarHeight: Dp = JengaBarChartDefaults.MinBarHeight,
    barMaxWidth: Dp = JengaBarChartDefaults.BarMaxWidth,
    barSpacing: Dp = JengaBarChartDefaults.barSpacing,
    barShape: Shape = JengaBarChartDefaults.barShape,
    labelSpacing: Dp = JengaBarChartDefaults.labelSpacing,
    contentPadding: PaddingValues = JengaBarChartDefaults.contentPadding,
    labelStyle: TextStyle = JengaBarChartDefaults.labelStyle,
    selectedLabelStyle: TextStyle = JengaBarChartDefaults.selectedLabelStyle,
    colors: JengaBarChartColors = JengaBarChartDefaults.colors(),
    animationSpec: AnimationSpec<Float> = JengaBarChartDefaults.animationSpec,
    valueLabel: (@Composable (entry: JengaBarChartEntry, selected: Boolean) -> Unit)? = null,
    label: @Composable (entry: JengaBarChartEntry, selected: Boolean) -> Unit = { entry, selected ->
        JengaText(
            text = entry.label,
            style = if (selected) selectedLabelStyle else labelStyle,
            color = if (selected) colors.selectedLabel else colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
) {
    JengaBarChart(
        entries = entries,
        barContentDescription = barContentDescription,
        modifier = modifier,
        contentDescription = null,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        highlightedIndices = emptySet(),
        emptyIndices = emptySet(),
        maxValue = maxValue,
        target = null,
        arrangement = arrangement,
        plotHeight = plotHeight,
        minBarHeight = minBarHeight,
        emptyBarHeight = JengaBarChartDefaults.EmptyBarHeight,
        barMaxWidth = barMaxWidth,
        barSpacing = barSpacing,
        barShape = barShape,
        labelSpacing = labelSpacing,
        contentPadding = contentPadding,
        labelStyle = labelStyle,
        selectedLabelStyle = selectedLabelStyle,
        targetLabelStyle = JengaBarChartDefaults.targetLabelStyle,
        colors = colors,
        animationSpec = animationSpec,
        valueLabel = valueLabel,
        label = label,
    )
}

/** How far up the plot a bar of [value] reaches, from 0 to 1, when [max] is a full-height bar. */
private fun barFraction(value: Float, max: Float): Float = if (max > 0f) (value / max).coerceIn(0f, 1f) else 0f

/** The height of a bar [fraction] of the way up the plot, or of the stub of an [empty] entry. */
private fun barHeight(fraction: Float, empty: Boolean, plotHeight: Dp, minBarHeight: Dp, emptyBarHeight: Dp): Dp = if (empty) {
    emptyBarHeight.coerceAtMost(plotHeight)
} else {
    minBarHeight + (plotHeight - minBarHeight) * fraction
}

/**
 * The room kept free at the top and sides of every bar for the ring around the selected one:
 * none unless [colors] has a ring color.
 */
private fun selectedOutlineInset(colors: JengaBarChartColors): Dp = if (colors.selectedOutline.alpha > 0f) SelectedOutlineGap + SelectedOutlineWidth else 0.dp

@Composable
private fun BarSlot(
    entry: JengaBarChartEntry,
    fraction: Float,
    selected: Boolean,
    hasSelection: Boolean,
    highlighted: Boolean,
    empty: Boolean,
    onClick: (() -> Unit)?,
    contentDescription: String,
    plotHeight: Dp,
    minBarHeight: Dp,
    emptyBarHeight: Dp,
    barMaxWidth: Dp,
    barShape: Shape,
    labelSpacing: Dp,
    colors: JengaBarChartColors,
    animationSpec: AnimationSpec<Float>,
    valueLabel: (@Composable (JengaBarChartEntry, Boolean) -> Unit)?,
    label: @Composable (JengaBarChartEntry, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(if (empty) 0f else fraction, animationSpec, label = "bar")
    val barColor = when {
        empty -> colors.emptyBar
        highlighted -> colors.highlightedBar
        !hasSelection -> colors.bar
        selected -> colors.selectedBar
        else -> colors.unselectedBar
    }
    val barHeight = barHeight(animated, empty, plotHeight, minBarHeight, emptyBarHeight)
    // With a ring color the bar keeps room for the ring at its top and sides, selected or not,
    // so the ring is never clipped and bars do not shift when the selection moves.
    val outlineInset = selectedOutlineInset(colors)
    val hasOutline = outlineInset > 0.dp
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
        valueLabel?.invoke(entry, selected)
        Box(
            modifier = Modifier
                .padding(start = outlineInset, top = outlineInset, end = outlineInset)
                .chartBaseline()
                .widthIn(max = barMaxWidth)
                .fillMaxWidth()
                .height(barHeight)
                .then(
                    if (hasOutline && selected) {
                        Modifier.selectedOutline(colors.selectedOutline, barShape)
                    } else {
                        Modifier
                    },
                )
                .clip(barShape)
                .background(barColor),
        )
        Box(modifier = Modifier.padding(top = labelSpacing)) {
            label(entry, selected)
        }
    }
}

/**
 * Draws a ring in [color] around this bar: [shape]'s outline, [SelectedOutlineGap] outside the
 * bar's top and sides. Its bottom edge is centred on the baseline, so the stroke reaches half
 * its width (1dp) below the bar, short of the label.
 */
private fun Modifier.selectedOutline(color: Color, shape: Shape): Modifier = drawBehind {
    val stroke = SelectedOutlineWidth.toPx()
    val outset = SelectedOutlineGap.toPx() + stroke / 2f
    val outline = shape.createOutline(
        size = Size(size.width + outset * 2f, size.height + outset),
        layoutDirection = layoutDirection,
        density = this,
    )
    translate(left = -outset, top = -outset) {
        drawOutline(outline = outline, color = color, style = Stroke(width = stroke))
    }
}
