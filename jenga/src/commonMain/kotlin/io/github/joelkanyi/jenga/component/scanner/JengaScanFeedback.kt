package io.github.joelkanyi.jenga.component.scanner

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for [JengaScanFeedback]. */
public object JengaScanFeedbackDefaults {
    /** Diameter of the result icon. */
    public val IconSize: Dp = 80.dp

    /** Thickness of the pulsing border. */
    public val BorderWidth: Dp = 6.dp

    /** Total duration of the pulse-and-fade animation (ms). */
    public const val PulseDurationMillis: Int = 600
}

/**
 * A full-screen scan-result indicator: an accent border and a centered status
 * icon that "pop" in, overlaid on a camera preview to confirm the outcome of a
 * scan at a glance.
 *
 * Render it conditionally; it plays its entrance once each time it enters
 * composition (key the call site on the scan so a new result re-pops), and stays
 * visible until the caller removes it (e.g. on navigation or after a delay).
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaScanFeedbackSample
 *
 * @param status the outcome to celebrate or warn about; drives the accent + icon.
 * @param modifier the [Modifier] for this overlay (typically `fillMaxSize`).
 */
@Composable
public fun JengaScanFeedback(
    status: JengaScannerStatus,
    modifier: Modifier = Modifier,
) {
    val accent = JengaScannerViewfinderDefaults.accentColor(status)
    val icons = JengaTheme.icons
    val icon = when (status) {
        JengaScannerStatus.Success -> icons.checkCircle
        JengaScannerStatus.Warning -> icons.info
        JengaScannerStatus.Error -> icons.close
        JengaScannerStatus.Scanning -> icons.search
    }

    val scale = remember { Animatable(0.85f) }
    val emphasized = JengaTheme.motion.emphasized
    val standard = JengaTheme.motion.standard

    LaunchedEffect(Unit) {
        val half = JengaScanFeedbackDefaults.PulseDurationMillis / 2
        scale.animateTo(1.1f, animationSpec = tween(half, easing = emphasized))
        scale.animateTo(1f, animationSpec = tween(half, easing = standard))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .border(
                    width = JengaScanFeedbackDefaults.BorderWidth,
                    color = accent,
                    shape = JengaTheme.shapes.xl,
                ),
        )
        JengaIcon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            size = JengaScanFeedbackDefaults.IconSize,
        )
    }
}
