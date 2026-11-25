package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A [JengaTooltip] anchored to a host: wrap the element that launches the tooltip (an info icon
 * after a field label, say) in [content], and while [expanded] is true the tooltip shows in a popup
 * next to it, its pointer aimed at the host's centre.
 *
 * The tooltip goes under the host, pointing up at it; when there is not enough room under it in the
 * window it goes above, pointing down. It is centred on the host and moved sideways as needed to
 * keep the theme's `spacing.lg` from the sides of the window, except that the pointer's tip always
 * reaches the host's centre: for a host close to the window's side the tooltip moves into that
 * margin rather than aim beside the host. It never leaves the window. The theme's `spacing.xs`
 * separates the host's bounds from the pointer's tip. This works the same in right-to-left layouts.
 *
 * The host is [content]'s own bounds: padding added through [modifier] is not part of it. The gap
 * is measured from those bounds, so a host with a large touch area, such as a 48dp
 * [io.github.joelkanyi.jenga.component.button.JengaIconButton] around a 24dp icon, puts the tip about
 * 16dp below the icon itself.
 *
 * While the tooltip is open its popup takes focus, so an open soft keyboard closes. A tap outside
 * the tooltip (on the host too) only dismisses it and does not reach what is under it; a tap on the
 * tooltip or Back also calls [onDismissRequest]. The host keeps its own semantics; the tooltip is
 * exposed to screen readers as a pane titled with its text and as a polite live region.
 *
 * To show and hide the tooltip on taps of the host without holding the state yourself, use the
 * overload without [expanded].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTooltipBoxExpandedSample
 *
 * @param text the tooltip text.
 * @param expanded whether the tooltip is shown.
 * @param onDismissRequest called when the tooltip should close: a tap outside it or on it, or Back.
 * @param modifier the [Modifier] for the box around the host. The tooltip is placed against the
 *   host's bounds, inside any padding this adds.
 * @param colors the tooltip's color set; defaults to [JengaTooltipDefaults.colors].
 * @param content the host the tooltip explains and points at.
 */
@Composable
public fun JengaTooltipBox(
    text: String,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    colors: JengaTooltipColors = JengaTooltipDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        if (expanded) {
            JengaAnchoredTooltip(text = text, colors = colors, onDismissRequest = onDismissRequest)
        }
    }
}

/**
 * A [JengaTooltip] anchored to a host that shows it: a tap on [content] shows the tooltip, and the
 * next tap (on the host, on the tooltip or anywhere else) or Back hides it; that tap only hides it.
 * This is the Info tip pattern: an info icon after a field label that explains the field.
 *
 * The host becomes a button: it is at least 48dp square to be easy to tap, and its own content
 * description (the icon's, for example) becomes the button's label, so give the host one. The
 * tooltip is placed and aimed at [content] itself, not at the larger touch area; see the overload
 * with `expanded` for the placement rules. Do not make [content] clickable itself: it would take
 * the taps that show the tooltip. To show the tooltip from your own state, use that overload.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTooltipBoxSample
 *
 * @param text the tooltip text.
 * @param modifier the [Modifier] for the host's touch area.
 * @param colors the tooltip's color set; defaults to [JengaTooltipDefaults.colors].
 * @param content the host the tooltip explains and points at, e.g. a [io.github.joelkanyi.jenga.component.icon.JengaIcon]
 *   with a content description such as "What is the deposit?".
 */
@Composable
public fun JengaTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    colors: JengaTooltipColors = JengaTooltipDefaults.colors(),
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(JengaTheme.shapes.pill)
            .clickable(role = Role.Button) { expanded = !expanded },
        contentAlignment = Alignment.Center,
    ) {
        JengaTooltipBox(
            text = text,
            expanded = expanded,
            onDismissRequest = { expanded = false },
            colors = colors,
            content = content,
        )
    }
}

/**
 * The popup with the bubble. The position provider decides where the bubble goes and, from the same
 * computation, which edge the pointer is on and where along it; it hands that back through
 * [placement]. The bubble is composed with the pointer of the last placement it has seen and is
 * transparent while that differs from the latest one (on the first frame, before any placement, and
 * for the one frame after a flip or a sideways move of the host), so a pointer that aims at the
 * wrong place is never drawn.
 */
@Composable
private fun JengaAnchoredTooltip(
    text: String,
    colors: JengaTooltipColors,
    onDismissRequest: () -> Unit,
) {
    val density = LocalDensity.current
    val gap = with(density) { JengaTheme.spacing.xs.roundToPx() }
    val margin = with(density) { JengaTheme.spacing.lg.roundToPx() }
    val verticalMargin = with(density) { JengaTheme.spacing.sm.roundToPx() }
    val bubbleShape = JengaTheme.shapes.sm
    var placement by remember { mutableStateOf<JengaTooltipPlacement?>(null) }
    val provider = remember(gap, margin, verticalMargin, bubbleShape, density) {
        JengaTooltipPositionProvider(gap, margin, verticalMargin, bubbleShape, density) { placement = it }
    }
    val dismiss by rememberUpdatedState(onDismissRequest)
    Popup(
        popupPositionProvider = provider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        val placed = placement
        val pointer = JengaTooltipPointer(
            edge = placed?.edge ?: JengaTooltipPointerEdge.Top,
            offset = if (placed == null) Dp.Unspecified else with(density) { placed.pointerOffset.toDp() },
        )
        JengaTooltipBubble(
            text = text,
            colors = colors,
            pointer = pointer,
            shadowFollowsPointer = elevationShadowFollowsConcaveOutlines,
            modifier = Modifier
                .graphicsLayer {
                    // Read in the layer, so a new placement hides the bubble without waiting for recomposition.
                    val latest = placement
                    val aimed = placed != null &&
                        latest != null &&
                        latest.edge == placed.edge &&
                        latest.pointerOffset == placed.pointerOffset
                    alpha = if (aimed) 1f else 0f
                }
                .pointerInput(Unit) { detectTapGestures { dismiss() } }
                .semantics {
                    paneTitle = text
                    liveRegion = LiveRegionMode.Polite
                },
        )
    }
}
