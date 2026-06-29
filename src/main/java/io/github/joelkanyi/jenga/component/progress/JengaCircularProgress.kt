package io.github.joelkanyi.jenga.component.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for the Jenga circular progress indicators. */
public object JengaCircularProgressDefaults {
    /** Default diameter. */
    public val Size: Dp = 40.dp

    /** Default ring stroke width. */
    public val StrokeWidth: Dp = 4.dp

    /** Duration of one full rotation of the indeterminate ring (ms). Loop
     *  animation, hence outside the discrete
     *  [io.github.joelkanyi.jenga.foundation.motion.JengaMotion] scale. */
    public const val IndeterminateRotationDurationMillis: Int = 1000

    /** Arc length (degrees) of the moving indeterminate indicator. */
    public const val IndeterminateSweepDegrees: Float = 270f

    /** Brand-colored indicator. */
    public val color: Color
        @Composable get() = JengaTheme.colors.brand

    /** The faint full-circle track behind the indicator. */
    public val trackColor: Color
        @Composable get() = JengaTheme.colors.surfaceSunk
}

/**
 * A determinate circular progress ring.
 *
 * @param progress the progress in `0f..1f` (coerced into range). Sweeps clockwise
 *   from 12 o'clock.
 * @param modifier the [Modifier] for this indicator.
 * @param size the ring diameter; defaults to [JengaCircularProgressDefaults.Size].
 * @param strokeWidth the ring thickness; defaults to [JengaCircularProgressDefaults.StrokeWidth].
 * @param color the indicator color; defaults to the brand color.
 * @param trackColor the background track color.
 */
@Composable
public fun JengaCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = JengaCircularProgressDefaults.Size,
    strokeWidth: Dp = JengaCircularProgressDefaults.StrokeWidth,
    color: Color = JengaCircularProgressDefaults.color,
    trackColor: Color = JengaCircularProgressDefaults.trackColor,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Canvas(modifier.size(size)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        drawRing(trackColor, startAngle = 0f, sweepAngle = 360f, stroke = stroke)
        drawRing(color, startAngle = -90f, sweepAngle = clamped * 360f, stroke = stroke)
    }
}

/**
 * An indeterminate circular spinner — a brand-colored arc rotating around the
 * track, for when progress can't be measured.
 *
 * @param modifier the [Modifier] for this indicator.
 * @param size the ring diameter; defaults to [JengaCircularProgressDefaults.Size].
 * @param strokeWidth the ring thickness; defaults to [JengaCircularProgressDefaults.StrokeWidth].
 * @param color the indicator color; defaults to the brand color.
 * @param trackColor the background track color.
 */
@Composable
public fun JengaCircularProgressIndeterminate(
    modifier: Modifier = Modifier,
    size: Dp = JengaCircularProgressDefaults.Size,
    strokeWidth: Dp = JengaCircularProgressDefaults.StrokeWidth,
    color: Color = JengaCircularProgressDefaults.color,
    trackColor: Color = JengaCircularProgressDefaults.trackColor,
) {
    val transition = rememberInfiniteTransition(label = "circular")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = JengaCircularProgressDefaults.IndeterminateRotationDurationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )
    Canvas(modifier.size(size)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        drawRing(trackColor, startAngle = 0f, sweepAngle = 360f, stroke = stroke)
        drawRing(
            color,
            startAngle = rotation - 90f,
            sweepAngle = JengaCircularProgressDefaults.IndeterminateSweepDegrees,
            stroke = stroke,
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRing(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    stroke: Stroke,
) {
    val inset = stroke.width / 2f
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = Size(size.width - stroke.width, size.height - stroke.width),
        style = stroke,
    )
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaCircularProgressPreview() {
    JengaTheme { CircularProgressShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaCircularProgressRtlPreview() {
    JengaTheme { RtlPreview { CircularProgressShowcase() } }
}

@Composable
private fun CircularProgressShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaCircularProgress(progress = 0.25f)
        JengaCircularProgress(progress = 0.7f)
        JengaCircularProgressIndeterminate()
    }
}
