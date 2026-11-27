package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTooltipPreview() {
    JengaTheme { TooltipShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipRtlPreview() {
    JengaTheme { RtlPreview { TooltipShowcase() } }
}

@Composable
private fun TooltipShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTooltip(text = "Re-admit this attendee")
        JengaTooltip(text = "This ticket was already scanned at 14:02 by Gate B.")
    }
}

// ---- Pointer ---------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTooltipPointerEdgesPreview() {
    JengaTheme { PointerEdgesShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipPointerEdgesRtlPreview() {
    JengaTheme { RtlPreview { PointerEdgesShowcase() } }
}

@JengaBlockPreviews
@Composable
internal fun JengaTooltipPointerOffsetsPreview() {
    JengaTheme { PointerOffsetsShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipPointerOffsetsRtlPreview() {
    JengaTheme { RtlPreview { PointerOffsetsShowcase() } }
}

/** The look on Android 9 and older, where only the bubble casts the shadow. */
@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaTooltipPointerBubbleShadowPreview() {
    JengaTheme { PointerEdgesShowcase(shadowFollowsPointer = false) }
}

/** One centred pointer on each edge. */
@Composable
private fun PointerEdgesShowcase(shadowFollowsPointer: Boolean = true) {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaTooltipPointerEdge.entries.forEach { edge ->
            JengaTooltipBubble(
                text = "Pointer on ${edge.name.lowercase()}",
                colors = JengaTooltipDefaults.colors(),
                pointer = JengaTooltipPointer(edge = edge),
                shadowFollowsPointer = shadowFollowsPointer,
            )
        }
    }
}

/** Offsets along an edge: at the start (held off the corner), a set distance, centred, past the end. */
@Composable
private fun PointerOffsetsShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaTooltip(
            text = "Offset 0: held off the corner",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Top, offset = 0.dp),
        )
        JengaTooltip(
            text = "Offset 48",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Top, offset = 48.dp),
        )
        JengaTooltip(
            text = "No offset: centred",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Bottom),
        )
        JengaTooltip(
            text = "Offset past the end: held off the corner",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Bottom, offset = 600.dp),
        )
        JengaTooltip(
            text = "This ticket was already scanned at 14:02 by Gate B.",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Start, offset = 600.dp),
        )
        JengaTooltip(
            text = "This ticket was already scanned at 14:02 by Gate B.",
            pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.End, offset = 14.dp),
        )
    }
}

// ---- Placement -------------------------------------------------------------

/**
 * Where [JengaTooltipBox] puts its tooltip, drawn without a popup (popups do not show in screenshot
 * tests): each frame stands for a window with one host in it, and the bubble is placed by the same
 * function the popup uses. The hosts sit 16dp in from the frame's sides, closer than the pointer can
 * reach from the bubble's corner at the 16dp margin, so each bubble moves into the margin to keep the
 * tip on the icon's centre: under a host at the top start; under a host at the top end; flipped above
 * a host at the bottom end.
 */
@JengaBlockPreviews
@Composable
internal fun JengaTooltipBoxPlacementPreview() {
    JengaTheme { PlacementShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipBoxPlacementRtlPreview() {
    JengaTheme { RtlPreview { PlacementShowcase() } }
}

@Composable
private fun PlacementShowcase() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        PlacementFrame(Alignment.TopStart, "First payment before the device is unlocked.")
        PlacementFrame(Alignment.TopEnd, "On the customer's SMS receipt.")
        PlacementFrame(Alignment.BottomEnd, "Synced 2 minutes ago.")
    }
}

/** A stand-in window with a host at [hostAlignment] and its tooltip placed by [placeJengaTooltip]. */
@Composable
private fun PlacementFrame(hostAlignment: Alignment, text: String) {
    val colors = JengaTooltipDefaults.colors()
    val gap = JengaTheme.spacing.xs
    val margin = JengaTheme.spacing.lg
    val verticalMargin = JengaTheme.spacing.sm
    val bubbleShape = JengaTheme.shapes.sm
    val hostInset = JengaTheme.spacing.lg
    SubcomposeLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(1.dp, JengaTheme.colors.border, JengaTheme.shapes.sm),
    ) { constraints ->
        val window = IntSize(constraints.maxWidth, constraints.maxHeight)
        val inset = hostInset.roundToPx()
        val host = subcompose("host") {
            JengaIcon(
                imageVector = JengaTheme.icons.info,
                contentDescription = null,
                size = JengaTheme.sizing.iconSmall,
            )
        }.single().measure(Constraints())
        val hostPosition = hostAlignment.align(
            IntSize(host.width, host.height),
            IntSize(window.width - 2 * inset, window.height - 2 * inset),
            layoutDirection,
        ) + IntOffset(inset, inset)
        // The bubble's size does not depend on where its pointer is, so one with a default pointer measures it.
        val probe = subcompose("probe") {
            JengaTooltipBubble(text, colors, JengaTooltipPointer(), shadowFollowsPointer = true)
        }.single().measure(Constraints(maxWidth = window.width))
        val placement = placeJengaTooltip(
            anchorBounds = IntRect(hostPosition, IntSize(host.width, host.height)),
            windowSize = window,
            tooltipSize = IntSize(probe.width, probe.height),
            layoutDirection = layoutDirection,
            gap = gap.roundToPx(),
            margin = margin.roundToPx(),
            verticalMargin = verticalMargin.roundToPx(),
            pointerInset = jengaTooltipPointerInset(bubbleShape, IntSize(probe.width, probe.height), layoutDirection, this),
        )
        val pointer = JengaTooltipPointer(placement.edge, placement.pointerOffset.toDp())
        val bubble = subcompose("bubble") {
            JengaTooltipBubble(text, colors, pointer, shadowFollowsPointer = true)
        }.single().measure(Constraints(maxWidth = window.width))
        layout(window.width, window.height) {
            host.place(hostPosition)
            bubble.place(placement.position)
        }
    }
}
