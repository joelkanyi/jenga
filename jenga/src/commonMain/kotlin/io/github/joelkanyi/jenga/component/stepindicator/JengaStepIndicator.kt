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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
     * @sample io.github.joelkanyi.jenga.samples.JengaStepIndicatorCompactSample
     */
    Compact,
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
 *   ([JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact]); defaults to [active]. It does not follow a later
 *   `copy(active = ...)`, so set the two together when customising.
 * @property pendingNumber the number inside a pending step's ring
 *   ([JengaStepIndicatorVariant.Numbered] and [JengaStepIndicatorVariant.Compact]); defaults to [label].
 * @property currentHalo the soft halo around the current step's ring
 *   ([JengaStepIndicatorVariant.Compact]); defaults to [active] at 20% opacity. Like
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
    onStepClick: ((index: Int) -> Unit)? = null,
    colors: JengaStepIndicatorColors = JengaStepIndicatorDefaults.colors(),
) {
    val compact = variant == JengaStepIndicatorVariant.Compact
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sharedWidth = columnWidth(rowWidth = maxWidth, stepCount = steps.size)
        // Without a label to shrink, a compact column never gets narrower than its node.
        val columnWidth = if (compact) maxOf(sharedWidth, JengaStepIndicatorDefaults.NodeSize) else sharedWidth
        // The halo's height is kept free above and below at all times, so a scrolling parent
        // cannot cut it and the row does not change height as the current step moves.
        val haloSpace = if (compact) Modifier.padding(vertical = HaloWidth) else Modifier
        Row(modifier = Modifier.fillMaxWidth().then(haloSpace), verticalAlignment = Alignment.Top) {
            steps.forEachIndexed { index, step ->
                StepColumn(
                    step = step,
                    width = columnWidth,
                    index = index,
                    number = if (variant == JengaStepIndicatorVariant.Labelled) null else index + 1,
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

@Composable
private fun StepColumn(
    step: JengaStep,
    width: Dp,
    index: Int,
    number: Int?,
    compact: Boolean,
    description: String,
    onClick: ((index: Int) -> Unit)?,
    colors: JengaStepIndicatorColors,
) {
    val tap = if (onClick == null) Modifier else Modifier.clickable(role = Role.Button) { onClick(index) }
    Column(
        modifier = Modifier
            .width(width)
            .then(tap)
            .semantics(mergeDescendants = true) {
                contentDescription = description
                selected = step.state == JengaStepState.Current
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StepNode(state = step.state, number = number, halo = compact, colors = colors)
        if (!compact) {
            JengaText(
                text = step.label,
                style = JengaTheme.typography.caption.copy(fontWeight = labelWeight(step.state)),
                color = labelColor(step.state, colors),
                textAlign = TextAlign.Center,
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
