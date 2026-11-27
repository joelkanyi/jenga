package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaBottomBar]. Override via [JengaBottomBarDefaults.colors]. */
@Poko
@Immutable
public class JengaBottomBarColors(
    public val container: Color,
    public val content: Color,
    public val divider: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        divider: Color = this.divider,
    ): JengaBottomBarColors = JengaBottomBarColors(container, content, divider)
}

/** Defaults and token mappings for [JengaBottomBar]. */
public object JengaBottomBarDefaults {
    /** Minimum bar height (excluding the navigation-bar inset). */
    public val Height: Dp = 64.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaBottomBarColors {
        val c = JengaTheme.colors
        return JengaBottomBarColors(
            container = c.surface,
            content = c.textPrimary,
            divider = c.border,
        )
    }
}

/**
 * A bottom app bar: a surface pinned to the bottom of a [JengaScaffold] hosting a
 * row of actions. Applies the navigation-bar inset itself, so it sits correctly
 * above the system bar in an edge-to-edge app, and draws a hairline divider on top.
 *
 * Lay it out as the scaffold's `bottomBar` slot; the content is a [RowScope] so you
 * can `weight` the primary action. For one or two actions that should stack when a
 * label does not fit, use the overload taking `primaryAction` and `secondaryAction`.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBottomBarSample
 *
 * @param modifier the [Modifier] for this bar.
 * @param colors the color set; defaults to [JengaBottomBarDefaults.colors].
 * @param content the actions, laid out in a [RowScope]; inherit content color.
 */
@Composable
public fun JengaBottomBar(
    modifier: Modifier = Modifier,
    colors: JengaBottomBarColors = JengaBottomBarDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.bottomBarChrome(
            colors = colors,
            insets = WindowInsets.navigationBars,
            horizontalPadding = JengaTheme.spacing.lg,
            verticalPadding = JengaTheme.spacing.sm,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
            content()
        }
    }
}

/**
 * A bottom app bar for one or two actions that picks its own layout: the same surface, divider,
 * padding and navigation-bar inset as the [RowScope] overload, with the actions laid out by the bar.
 *
 * With two actions the bar decides, every time it is measured, between:
 *
 * - **Side by side**: [secondaryAction] at the start and [primaryAction] at the end, each given the
 *   same width (half the bar's inner width minus half the [JengaSpacing.md][io.github.joelkanyi.jenga.foundation.spacing.JengaSpacing.md]
 *   gap between them) and the same height.
 * - **Stacked**: [primaryAction] on top and [secondaryAction] under it, each as wide as the bar's
 *   inner width and at its own normal height, [JengaSpacing.md][io.github.joelkanyi.jenga.foundation.spacing.JengaSpacing.md]
 *   (12dp) apart.
 *
 * The fit rule: the actions sit side by side only when **both** fit on one line in their half of the
 * bar, i.e. when each action's natural single-line width (its max intrinsic width; for a
 * [JengaButton][io.github.joelkanyi.jenga.component.button.JengaButton] that is the unwrapped label
 * plus icons and content padding) is no wider than that half. Otherwise both stack. The rule is the
 * same on every width, font scale and orientation, so its outcome follows the space available: labels
 * that stack on a phone can sit side by side on a tablet or in landscape.
 *
 * The decision is made during measurement, so each slot is composed exactly once, keeps its state
 * when the layout changes, and there is no frame in which the wrong layout shows. Focus order follows
 * the visual order of the current layout: secondary then primary side by side, primary then secondary
 * stacked.
 *
 * The decision follows the actions' current content. A state that changes a label's width can
 * therefore change the layout: a [JengaButton][io.github.joelkanyi.jenga.component.button.JengaButton]
 * with `loading = true` and no `loadingText` swaps its label for a small spinner, so a long label that
 * made the bar stack no longer does and the actions move side by side until loading ends. Pass a
 * `loadingText` about as long as the label to keep the layout steady.
 *
 * With only [primaryAction] the action takes the full width. An absent action is a null
 * [secondaryAction] or a slot that emits nothing: leave it out with an `if`, not with a wrapper that
 * stays in the tree while showing nothing (e.g. a hidden `AnimatedVisibility`), which still counts as
 * an action and takes half the bar.
 *
 * Pass one button per slot and do not size its width (no `weight` or `fillMaxWidth` needed): the bar
 * sets it. A slot that emits several composables is treated as one action whose children are
 * overlaid on each other, each measured to the slot's size, so wrap them in a single layout instead.
 * A label longer than the full width of the bar is still shown on one line and cut by the button.
 *
 * The bar asks each action for its intrinsic width, so an action must support intrinsic measurement:
 * do not put `BoxWithConstraints`, a `SubcomposeLayout` or a lazy layout inside a slot, they throw
 * when asked. Do not give the bar a fixed height either: stacked actions need the extra height.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBottomBarActionsSample
 *
 * @param primaryAction the main action (e.g. a primary [JengaButton][io.github.joelkanyi.jenga.component.button.JengaButton]);
 *   at the end when side by side, on top when stacked.
 * @param modifier the [Modifier] for this bar.
 * @param secondaryAction the optional companion action; at the start when side by side, at the
 *   bottom when stacked.
 * @param colors the color set; defaults to [JengaBottomBarDefaults.colors].
 */
@Composable
public fun JengaBottomBar(
    primaryAction: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryAction: (@Composable () -> Unit)? = null,
    colors: JengaBottomBarColors = JengaBottomBarDefaults.colors(),
) {
    val spacing = JengaTheme.spacing.md
    val measurePolicy = remember(spacing) { BottomBarActionsMeasurePolicy(spacing) }
    val primary: @Composable () -> Unit = {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content, primaryAction)
    }
    val secondary: @Composable () -> Unit = {
        if (secondaryAction != null) {
            CompositionLocalProvider(LocalJengaContentColor provides colors.content, secondaryAction)
        }
    }
    Layout(
        contents = listOf(primary, secondary),
        modifier = modifier.bottomBarChrome(
            colors = colors,
            insets = WindowInsets.navigationBars,
            horizontalPadding = JengaTheme.spacing.lg,
            verticalPadding = JengaTheme.spacing.sm,
        ),
        measurePolicy = measurePolicy,
    )
}

/** The bar surface shared by both overloads: fill, top hairline, navigation-bar inset, min height, padding. */
private fun Modifier.bottomBarChrome(
    colors: JengaBottomBarColors,
    insets: WindowInsets,
    horizontalPadding: Dp,
    verticalPadding: Dp,
): Modifier = this
    .fillMaxWidth()
    .background(colors.container)
    .drawBehind {
        val stroke = 1.dp.toPx()
        drawRect(color = colors.divider, topLeft = Offset(0f, 0f), size = Size(size.width, stroke))
    }
    .windowInsetsPadding(insets)
    .heightIn(min = JengaBottomBarDefaults.Height)
    .padding(horizontal = horizontalPadding, vertical = verticalPadding)

/**
 * Lays out the primary (`measurables[0]`) and secondary (`measurables[1]`) actions of a
 * [JengaBottomBar]: side by side in equal halves when both fit on one line, otherwise stacked with
 * the primary on top. Children are placed in visual order so focus traversal follows it.
 */
private class BottomBarActionsMeasurePolicy(private val spacing: Dp) : MultiContentMeasurePolicy {

    override fun MeasureScope.measure(measurables: List<List<Measurable>>, constraints: Constraints): MeasureResult {
        val primary = measurables[0]
        val secondary = measurables[1]
        val gap = spacing.roundToPx()
        val bounded = constraints.hasBoundedWidth

        // One action (or none): full width at its normal height.
        if (primary.isEmpty() || secondary.isEmpty()) {
            val only = primary.ifEmpty { secondary }
            val width = if (bounded) constraints.maxWidth else maxOf(constraints.minWidth, only.naturalWidth())
            val placeables = only.measureAll(width, minHeight = 0, maxHeight = constraints.maxHeight)
            val contentHeight = placeables.maxOfOrNull { it.height } ?: 0
            val height = constraints.constrainHeight(contentHeight)
            return layout(width, height) {
                placeables.forEach { it.placeRelative(0, (height - it.height) / 2) }
            }
        }

        val primaryNatural = primary.naturalWidth()
        val secondaryNatural = secondary.naturalWidth()
        // In an unbounded container there is no width to run out of: size both to the wider label.
        val half = if (bounded) {
            ((constraints.maxWidth - gap) / 2).coerceAtLeast(0)
        } else {
            maxOf(primaryNatural, secondaryNatural, (constraints.minWidth - gap) / 2)
        }
        val width = if (bounded) constraints.maxWidth else half * 2 + gap
        val sideBySide = primaryNatural <= half && secondaryNatural <= half

        return if (sideBySide) {
            val rowHeight = (primary + secondary)
                .maxOf { it.maxIntrinsicHeight(half) }
                .coerceAtMost(constraints.maxHeight)
            val secondaryPlaceables = secondary.measureAll(half, minHeight = rowHeight, maxHeight = constraints.maxHeight)
            val primaryPlaceables = primary.measureAll(half, minHeight = rowHeight, maxHeight = constraints.maxHeight)
            val contentHeight = (secondaryPlaceables + primaryPlaceables).maxOf { it.height }
            val height = constraints.constrainHeight(contentHeight)
            layout(width, height) {
                secondaryPlaceables.forEach { it.placeRelative(0, (height - it.height) / 2) }
                primaryPlaceables.forEach { it.placeRelative(width - half, (height - it.height) / 2) }
            }
        } else {
            val primaryPlaceables = primary.measureAll(width, minHeight = 0, maxHeight = constraints.maxHeight)
            val secondaryPlaceables = secondary.measureAll(width, minHeight = 0, maxHeight = constraints.maxHeight)
            val primaryHeight = primaryPlaceables.maxOf { it.height }
            val secondaryHeight = secondaryPlaceables.maxOf { it.height }
            val contentHeight = primaryHeight + gap + secondaryHeight
            val height = constraints.constrainHeight(contentHeight)
            val top = ((height - contentHeight) / 2).coerceAtLeast(0)
            layout(width, height) {
                primaryPlaceables.forEach { it.placeRelative(0, top) }
                secondaryPlaceables.forEach { it.placeRelative(0, top + primaryHeight + gap) }
            }
        }
    }

    /** The widest single-line width any child of this slot asks for. */
    private fun List<Measurable>.naturalWidth(): Int = maxOfOrNull { it.maxIntrinsicWidth(Constraints.Infinity) } ?: 0

    private fun List<Measurable>.measureAll(width: Int, minHeight: Int, maxHeight: Int): List<Placeable> {
        val slot = Constraints(
            minWidth = width,
            maxWidth = width,
            minHeight = minHeight.coerceAtMost(maxHeight),
            maxHeight = maxHeight,
        )
        return map { it.measure(slot) }
    }
}
