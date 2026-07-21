package io.github.joelkanyi.jenga.component.progress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import io.github.joelkanyi.jenga.theme.LocalJengaColors
import kotlinx.coroutines.launch

/** Defaults for [jengaShimmer]. */
public object JengaShimmerDefaults {
    /** Duration of one shimmer sweep (ms). A loop animation, hence outside the
     *  discrete [io.github.joelkanyi.jenga.foundation.motion.JengaMotion] scale. */
    public const val DurationMillis: Int = 1400
}

/**
 * Paints an animated shimmer gradient over this element: the standard
 * skeleton-loading effect. Apply to a clipped, sized placeholder:
 * ```
 * Box(Modifier.size(120.dp, 16.dp).clip(JengaTheme.shapes.sm).jengaShimmer())
 * ```
 *
 * Implemented as a `Modifier.Node` (the recommended, performant API) that reads
 * Jenga colors at draw time, so it auto-themes without a composable factory.
 */
public fun Modifier.jengaShimmer(): Modifier = this then JengaShimmerElement

private data object JengaShimmerElement : ModifierNodeElement<JengaShimmerNode>() {
    override fun create(): JengaShimmerNode = JengaShimmerNode()
    override fun update(node: JengaShimmerNode) {}
    override fun InspectorInfo.inspectableProperties() {
        name = "jengaShimmer"
    }
}

private class JengaShimmerNode :
    Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {

    private val progress = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = JengaShimmerDefaults.DurationMillis),
                    repeatMode = RepeatMode.Restart,
                ),
            )
        }
    }

    override fun ContentDrawScope.draw() {
        val colors = currentValueOf(LocalJengaColors)
        val width = size.width
        val travel = width * 2f
        val start = -width + travel * progress.value
        val brush = Brush.linearGradient(
            colors = listOf(colors.surfaceSunk, colors.border, colors.surfaceSunk),
            start = Offset(start, 0f),
            end = Offset(start + width, 0f),
        )
        drawRect(brush)
        drawContent()
    }
}
