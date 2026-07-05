package io.github.joelkanyi.jenga.component.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults for the Jenga progress bars. */
public object JengaProgressDefaults {
    /** Duration of one indeterminate sweep (ms). Loop animation, hence outside
     *  the discrete [io.github.joelkanyi.jenga.foundation.motion.JengaMotion] scale. */
    public const val IndeterminateDurationMillis: Int = 1100
}

/**
 * A determinate linear progress bar.
 *
 * @param progress the progress in `0f..1f` (coerced into range).
 * @param modifier the [Modifier] for this bar.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaLinearProgressSample
 */
@Composable
public fun JengaLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Track(modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(clamped)
                .clip(JengaTheme.shapes.pill)
                .background(JengaTheme.colors.brand),
        )
    }
}

/**
 * An indeterminate linear progress bar (a brand-colored bar sliding across the
 * track), for when progress can't be measured.
 *
 * @param modifier the [Modifier] for this bar.
 */
@Composable
public fun JengaLinearProgressIndeterminate(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "indeterminate")
    val fraction by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = JengaProgressDefaults.IndeterminateDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "offset",
    )
    Track(modifier) {
        val indicatorWidth = 0.4f
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(indicatorWidth)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val parentWidth = constraints.maxWidth
                    val x = (fraction * parentWidth).toInt()
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(x, 0)
                    }
                }
                .clip(JengaTheme.shapes.pill)
                .background(JengaTheme.colors.brand),
        )
    }
}

@Composable
private fun Track(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.surfaceSunk),
        content = { content() },
    )
}
