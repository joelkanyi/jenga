package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineMarker
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineMarkerStyle
import io.github.joelkanyi.jenga.theme.JengaTheme

/** The state of one step in a [JengaStepIndicator]. */
public enum class JengaStepState {
    /** Finished: a filled circle with a check. */
    Done,

    /**
     * The step the user is on: a ring with a dot, or with the step number in
     * [JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact].
     */
    Current,

    /**
     * Not reached yet: an outline ring, with the step number in
     * [JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact].
     */
    Pending,

    /** Finished and no longer editable: a filled circle with a lock. */
    Locked,
}

/**
 * How a [JengaStepIndicator] draws its steps.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaStepIndicatorNumberedSample
 */
public enum class JengaStepIndicatorVariant {
    /** A label under each step; the current step shows a dot and a pending one an empty ring. */
    Labelled,

    /**
     * The same layout as [Labelled], with the step number (1, 2, 3...) inside the ring of the
     * current and pending steps. Done and locked steps keep the check and the lock.
     */
    Numbered,

    /**
     * Numbered steps and lines only: no label is drawn and no space is kept for one, for long
     * step names or many steps. The current step also gets a soft halo around its ring.
     *
     * Show the current step's name once under the row yourself, for example
     * "Step 2 of 4 · Product and plan". [JengaStep.label] is not drawn, but it is still passed
     * to `stepContentDescription` for screen readers.
     *
     * A step is only as tall as its circle (28dp), so a tappable step relies on the platform
     * widening small touch targets to its minimum size.
     *
     * The halo's height is always kept free above and below the row. Sideways it is not: when the
     * current step's circle touches the row's edge (the first or last step in
     * [JengaStepIndicatorArrangement.EdgeToEdge], or in a row too narrow for wider columns), the
     * halo reaches 3dp past that edge. Leave that much room beside the indicator, or do not clip
     * its parent.
     *
     * With [JengaStepIndicatorArrangement.Centered] the lines between circles shrink to short
     * stubs from about five steps on a phone and all but vanish at nine. Use
     * [JengaStepIndicatorArrangement.EdgeToEdge] for five steps or more.
     *
     * @sample io.github.joelkanyi.jenga.samples.JengaStepIndicatorCompactSample
     */
    Compact,
}

/**
 * Where a [JengaStepIndicator] puts its steps across the row. Both follow the layout direction.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaStepIndicatorEdgeToEdgeSample
 */
public enum class JengaStepIndicatorArrangement {
    /**
     * Each step gets an equal column, at most [JengaStepIndicatorDefaults.ColumnWidth] wide, with
     * its circle and label centered in it, so the first and last circles sit in from the row's edges.
     */
    Centered,

    /**
     * The first circle sits at the start edge of the row and the last at the end edge, with the
     * lines filling the space between. The first label starts under the first circle, the last
     * label ends under the last circle and the ones between are centered under theirs. Labels
     * share one width, as wide as fits without two neighbours meeting.
     *
     * In [JengaStepIndicatorVariant.Compact] the halo of a first or last current step reaches 3dp
     * past the row's edge; see there.
     */
    EdgeToEdge,
}

/** One step in a [JengaStepIndicator]: its [label] and [state]. */
@Poko
@Immutable
public class JengaStep(
    public val label: String,
    public val state: JengaStepState,
)

/**
 * Resolved colors for a [JengaStepIndicator]. Override via [JengaStepIndicatorDefaults.colors].
 *
 * @property active done and locked circles, the current ring, reached lines and the labels of
 *   done and locked steps.
 * @property onActive the check and the lock on a filled circle.
 * @property pending the ring of a pending step.
 * @property line a line that has not been reached.
 * @property label the label of a pending step.
 * @property currentLabel the label of the current step.
 * @property currentNumber the number inside the current step's ring
 *   ([JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact]); defaults
 *   to [active]. It does not follow a later `copy(active = ...)`, so set the two together when
 *   customising.
 * @property pendingNumber the number inside a pending step's ring
 *   ([JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact]); defaults
 *   to [label].
 * @property currentHalo the soft halo around the current step's ring
 *   ([JengaStepIndicatorVariant.Compact]). Built by hand it defaults to [active] at 20%
 *   opacity; [JengaStepIndicatorDefaults.colors] uses the theme's `focusRing` color. Like
 *   [currentNumber], it does not follow a later `copy(active = ...)`.
 */
@Poko
@Immutable
public class JengaStepIndicatorColors(
    public val active: Color,
    public val onActive: Color,
    public val pending: Color,
    public val line: Color,
    public val label: Color,
    public val currentLabel: Color,
    public val currentNumber: Color = active,
    public val pendingNumber: Color = label,
    public val currentHalo: Color = active.copy(alpha = 0.2f),
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(
        active: Color,
        onActive: Color,
        pending: Color,
        line: Color,
        label: Color,
        currentLabel: Color,
    ) : this(
        active,
        onActive,
        pending,
        line,
        label,
        currentLabel,
        currentNumber = active,
        pendingNumber = label,
        currentHalo = active.copy(alpha = 0.2f),
    )

    public fun copy(
        active: Color = this.active,
        onActive: Color = this.onActive,
        pending: Color = this.pending,
        line: Color = this.line,
        label: Color = this.label,
        currentLabel: Color = this.currentLabel,
        currentNumber: Color = this.currentNumber,
        pendingNumber: Color = this.pendingNumber,
        currentHalo: Color = this.currentHalo,
    ): JengaStepIndicatorColors = JengaStepIndicatorColors(
        active,
        onActive,
        pending,
        line,
        label,
        currentLabel,
        currentNumber,
        pendingNumber,
        currentHalo,
    )

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(
        active: Color = this.active,
        onActive: Color = this.onActive,
        pending: Color = this.pending,
        line: Color = this.line,
        label: Color = this.label,
        currentLabel: Color = this.currentLabel,
    ): JengaStepIndicatorColors = copy(active, onActive, pending, line, label, currentLabel, this.currentNumber, this.pendingNumber, this.currentHalo)
}

/** Defaults and token mappings for [JengaStepIndicator]. */
public object JengaStepIndicatorDefaults {
    /** The diameter of a step circle. */
    public val NodeSize: Dp = 28.dp

    /** The widest a step column gets; columns shrink equally when the row is narrower. */
    public val ColumnWidth: Dp = 60.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaStepIndicatorColors {
        val c = JengaTheme.colors
        return JengaStepIndicatorColors(
            active = c.brand,
            onActive = c.onBrand,
            pending = c.borderStrong,
            line = c.border,
            label = c.textMuted,
            currentLabel = c.textPrimary,
            currentNumber = c.brand,
            pendingNumber = c.textMuted,
            currentHalo = c.focusRing,
        )
    }
}

/**
 * A horizontal row of steps joined by lines, for multi-step flows such as sign-up,
 * verification or checkout. [variant] chooses what the step circles show and whether a
 * label is drawn under each step.
 *
 * A line is drawn in the active color once the step on its left is done and the
 * next one has been reached (done, current or locked).
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaStepIndicatorSample
 *
 * @param steps the steps, in order.
 * @param stepContentDescription accessibility label for a step, given its index
 *   (e.g. "Step 2 of 3, Payment, current step").
 * @param modifier the [Modifier] for the row.
 * @param variant how the steps are drawn; see [JengaStepIndicatorVariant]. The numbers of
 *   [JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact] are
 *   decoration: screen readers still read only [stepContentDescription].
 * @param arrangement where the steps sit across the row; see [JengaStepIndicatorArrangement].
 * @param onStepClick called with a step's index when it is tapped; null makes the
 *   steps not tappable. Which taps to act on is up to the caller.
 * @param colors the color set; defaults to [JengaStepIndicatorDefaults.colors].
 */
@Composable
public fun JengaStepIndicator(
    steps: List<JengaStep>,
    stepContentDescription: (index: Int, step: JengaStep) -> String,
    modifier: Modifier = Modifier,
    variant: JengaStepIndicatorVariant = JengaStepIndicatorVariant.Labelled,
    arrangement: JengaStepIndicatorArrangement = JengaStepIndicatorArrangement.Centered,
    onStepClick: ((index: Int) -> Unit)? = null,
    colors: JengaStepIndicatorColors = JengaStepIndicatorDefaults.colors(),
) {
    if (arrangement == JengaStepIndicatorArrangement.EdgeToEdge) {
        EdgeToEdgeSteps(steps, stepContentDescription, variant, onStepClick, colors, modifier)
        return
    }
    val compact = variant == JengaStepIndicatorVariant.Compact
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sharedWidth = columnWidth(rowWidth = maxWidth, stepCount = steps.size)
        // Without a label to shrink, a compact column never gets narrower than its node.
        val columnWidth = if (compact) maxOf(sharedWidth, JengaStepIndicatorDefaults.NodeSize) else sharedWidth
        Row(modifier = Modifier.fillMaxWidth().haloSpace(compact), verticalAlignment = Alignment.Top) {
            steps.forEachIndexed { index, step ->
                StepColumn(
                    step = step,
                    modifier = Modifier.width(columnWidth),
                    index = index,
                    number = stepNumber(variant, index),
                    compact = compact,
                    description = stepContentDescription(index, step),
                    onClick = onStepClick,
                    colors = colors,
                )
                val next = steps.getOrNull(index + 1)
                if (next != null) {
                    StepLine(reached = lineReached(step, next), colors = colors, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Lays the steps out for [JengaStepIndicatorArrangement.EdgeToEdge]: circles spread from edge to
 * edge, a line in each gap, and every step's circle and label in one cell that is the tap and
 * accessibility target.
 */
@Composable
private fun EdgeToEdgeSteps(
    steps: List<JengaStep>,
    stepContentDescription: (index: Int, step: JengaStep) -> String,
    variant: JengaStepIndicatorVariant,
    onStepClick: ((index: Int) -> Unit)?,
    colors: JengaStepIndicatorColors,
    modifier: Modifier = Modifier,
) {
    val compact = variant == JengaStepIndicatorVariant.Compact
    Layout(
        modifier = modifier.fillMaxWidth().haloSpace(compact),
        content = {
            // The lines come first, so the measure policy can tell them from the steps by position.
            for (index in 0 until steps.lastIndex) {
                StepLine(reached = lineReached(steps[index], steps[index + 1]), colors = colors)
            }
            steps.forEachIndexed { index, step ->
                StepColumn(
                    step = step,
                    index = index,
                    number = stepNumber(variant, index),
                    compact = compact,
                    description = stepContentDescription(index, step),
                    onClick = onStepClick,
                    colors = colors,
                    alignment = when {
                        index == 0 -> Alignment.Start
                        index == steps.lastIndex -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    },
                )
            }
        },
    ) { measurables, constraints ->
        // n steps come with n - 1 lines. Counting what was composed, not the list, keeps the two
        // in step even if the list is changed without a recomposition.
        val count = (measurables.size + 1) / 2
        val node = JengaStepIndicatorDefaults.NodeSize.roundToPx()
        // Without a width to fill (a horizontally scrolling parent), take the least that fits.
        val rowWidth = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            (node * count + MinLineWidth.roundToPx() * (count - 1).coerceAtLeast(0)).coerceAtLeast(constraints.minWidth)
        }
        // In a row narrower than one node the nodes pile up at the start instead of running backwards.
        val span = (rowWidth - node).coerceAtLeast(0)
        val nodeLefts = IntArray(count) { index -> if (count < 2) 0 else (span.toLong() * index / (count - 1)).toInt() }

        val lines = measurables.take((count - 1).coerceAtLeast(0)).mapIndexed { index, line ->
            val gap = (nodeLefts[index + 1] - nodeLefts[index] - node).coerceAtLeast(0)
            line.measure(Constraints.fixedWidth(gap))
        }

        val labelWidth = edgeToEdgeLabelWidth(rowWidth, count, node, LabelGap.roundToPx())
        // Keeping the spare width even puts a centered circle on a whole pixel in both directions.
        val cellWidth = if (compact) node else labelWidth - (labelWidth - node) % 2
        val cells = measurables.drop(lines.size).map { it.measure(Constraints.fixedWidth(cellWidth)) }

        val height = (cells.maxOfOrNull { it.height } ?: 0).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(rowWidth, height) {
            lines.forEachIndexed { index, line -> line.placeRelative(x = nodeLefts[index] + node, y = 0) }
            cells.forEachIndexed { index, cell ->
                val x = when {
                    index == 0 -> 0
                    index == count - 1 -> (rowWidth - cellWidth).coerceAtLeast(0)
                    else -> nodeLefts[index] - (cellWidth - node) / 2
                }
                cell.placeRelative(x = x, y = 0)
            }
        }
    }
}

/**
 * The width every label gets in [JengaStepIndicatorArrangement.EdgeToEdge], in pixels: the widest
 * at which a centered label cannot come within [gap] of the next centered one, nor of a first or
 * last label that grows inward from its edge. Never narrower than the node.
 */
internal fun edgeToEdgeLabelWidth(rowWidth: Int, stepCount: Int, nodeSize: Int, gap: Int): Int {
    val width = when {
        stepCount <= 1 -> rowWidth
        stepCount == 2 -> (rowWidth - gap) / 2
        else -> {
            val pitch = (rowWidth - nodeSize).toFloat() / (stepCount - 1)
            minOf(pitch - gap, (2 * pitch + nodeSize - 2 * gap) / 3).toInt()
        }
    }
    return width.coerceAtLeast(nodeSize)
}

private fun stepNumber(variant: JengaStepIndicatorVariant, index: Int): Int? = if (variant == JengaStepIndicatorVariant.Labelled) null else index + 1

/**
 * Keeps the halo's height free above and below a compact row at all times, so a scrolling parent
 * cannot cut it and the row does not change height as the current step moves.
 */
private fun Modifier.haloSpace(compact: Boolean): Modifier = if (compact) padding(vertical = HaloWidth) else this

@Composable
private fun StepColumn(
    step: JengaStep,
    index: Int,
    number: Int?,
    compact: Boolean,
    description: String,
    onClick: ((index: Int) -> Unit)?,
    colors: JengaStepIndicatorColors,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    val tap = if (onClick == null) Modifier else Modifier.clickable(role = Role.Button) { onClick(index) }
    Column(
        modifier = modifier
            .then(tap)
            .semantics(mergeDescendants = true) {
                contentDescription = description
                selected = step.state == JengaStepState.Current
            },
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StepNode(state = step.state, number = number, halo = compact, colors = colors)
        if (!compact) {
            JengaText(
                text = step.label,
                style = JengaTheme.typography.caption.copy(fontWeight = labelWeight(step.state)),
                color = labelColor(step.state, colors),
                textAlign = when (alignment) {
                    Alignment.Start -> TextAlign.Start
                    Alignment.End -> TextAlign.End
                    else -> TextAlign.Center
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
    }
}

@Composable
private fun StepNode(state: JengaStepState, number: Int?, halo: Boolean, colors: JengaStepIndicatorColors) {
    val size = JengaStepIndicatorDefaults.NodeSize
    when (state) {
        JengaStepState.Done -> JengaTimelineMarker(color = colors.active, size = size, contentColor = colors.onActive) {
            JengaIcon(JengaIcons.Check, contentDescription = null, size = size * 0.55f)
        }
        JengaStepState.Locked -> JengaTimelineMarker(color = colors.active, size = size, contentColor = colors.onActive) {
            JengaIcon(JengaIcons.Lock, contentDescription = null, size = size * 0.45f)
        }
        JengaStepState.Current -> JengaTimelineMarker(
            color = colors.active,
            modifier = if (halo) Modifier.halo(colors.currentHalo) else Modifier,
            style = JengaTimelineMarkerStyle.Ring,
            size = size,
            ringWidth = 2.dp,
        ) {
            if (number == null) {
                Box(modifier = Modifier.size(size * 0.4f).clip(JengaTheme.shapes.pill).background(colors.active))
            } else {
                StepNumber(number = number, color = colors.currentNumber)
            }
        }
        JengaStepState.Pending -> JengaTimelineMarker(
            color = colors.pending,
            style = JengaTimelineMarkerStyle.Ring,
            size = size,
            ringWidth = 1.5.dp,
            content = if (number == null) null else ({ StepNumber(number = number, color = colors.pendingNumber) }),
        )
    }
}

/** A band [HaloWidth] wide just outside the node's edge. It is drawn outside the node's bounds. */
private fun Modifier.halo(color: Color): Modifier = drawBehind {
    val width = HaloWidth.toPx()
    drawCircle(color = color, radius = (size.minDimension + width) / 2f, style = Stroke(width = width))
}

/**
 * The step number inside a ring. It follows the user's font size until it is half the node
 * tall, then stops growing, so one or two digits always fit the fixed-size node.
 */
@Composable
private fun StepNumber(number: Int, color: Color) {
    val caption = JengaTheme.typography.caption
    val largest = with(LocalDensity.current) { (JengaStepIndicatorDefaults.NodeSize * MaxNumberSize).toSp() }
    val semibold = caption.copy(fontWeight = FontWeight.SemiBold)
    val style = if (caption.fontSize.isSp && caption.fontSize > largest) {
        val shrink = largest.value / caption.fontSize.value
        semibold.copy(
            fontSize = largest,
            lineHeight = if (caption.lineHeight.isSp) caption.lineHeight * shrink else caption.lineHeight,
        )
    } else {
        semibold
    }
    JengaText(
        text = number.toString(),
        color = color,
        style = style,
        textAlign = TextAlign.Center,
        softWrap = false,
        maxLines = 1,
        modifier = Modifier.clearAndSetSemantics {},
    )
}

@Composable
private fun StepLine(reached: Boolean, colors: JengaStepIndicatorColors, modifier: Modifier = Modifier) {
    val color = if (reached) colors.active else colors.line
    val animatedColor by animateColorAsState(color, animationSpec = tween(durationMillis = 300), label = "stepLine")
    Box(modifier = modifier.height(JengaStepIndicatorDefaults.NodeSize), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .height(2.dp)
                .clip(JengaTheme.shapes.pill)
                .background(animatedColor),
        )
    }
}

private fun columnWidth(rowWidth: Dp, stepCount: Int): Dp {
    if (stepCount == 0) return JengaStepIndicatorDefaults.ColumnWidth
    val lineSpace = MinLineWidth * (stepCount - 1)
    return minOf(JengaStepIndicatorDefaults.ColumnWidth, (rowWidth - lineSpace) / stepCount)
}

private fun lineReached(left: JengaStep, right: JengaStep): Boolean = left.state == JengaStepState.Done && right.state != JengaStepState.Pending

private fun labelWeight(state: JengaStepState): FontWeight = when (state) {
    JengaStepState.Current -> FontWeight.Bold
    JengaStepState.Done -> FontWeight.SemiBold
    JengaStepState.Pending, JengaStepState.Locked -> FontWeight.Normal
}

private fun labelColor(state: JengaStepState, colors: JengaStepIndicatorColors): Color = when (state) {
    JengaStepState.Current -> colors.currentLabel
    JengaStepState.Done, JengaStepState.Locked -> colors.active
    JengaStepState.Pending -> colors.label
}

private val MinLineWidth = 12.dp

/** The least space between two neighbouring labels in [JengaStepIndicatorArrangement.EdgeToEdge]. */
private val LabelGap = 4.dp

/** The width of the halo around the current step in [JengaStepIndicatorVariant.Compact]. */
private val HaloWidth = 3.dp

/** The tallest a step number's font gets, as a fraction of the node size. */
private const val MaxNumberSize = 0.5f

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaStepIndicator(
    steps: List<JengaStep>,
    stepContentDescription: (index: Int, step: JengaStep) -> String,
    modifier: Modifier = Modifier,
    onStepClick: ((index: Int) -> Unit)? = null,
    colors: JengaStepIndicatorColors = JengaStepIndicatorDefaults.colors(),
) {
    JengaStepIndicator(
        steps = steps,
        stepContentDescription = stepContentDescription,
        modifier = modifier,
        variant = JengaStepIndicatorVariant.Labelled,
        onStepClick = onStepClick,
        colors = colors,
    )
}
