package io.github.joelkanyi.jenga.component.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * The state of a scan, which drives the accent color of [JengaScannerViewfinder]
 * and [JengaScanFeedback].
 */
public enum class JengaScannerStatus {
    /** Actively looking for a code — brand accent, scan line animating. */
    Scanning,

    /** The last scan was admitted — success accent. */
    Success,

    /** The last scan needs attention (already used / duplicate) — warning accent. */
    Warning,

    /** The last scan was rejected — error accent. */
    Error,
}

/** Defaults and token mappings for [JengaScannerViewfinder]. */
public object JengaScannerViewfinderDefaults {
    /** The reticle's side length as a fraction of the smaller viewport edge. */
    public const val FrameSizeRatio: Float = 0.72f

    /** Length of each corner bracket arm. */
    public val BracketLength: Dp = 48.dp

    /** Thickness of the corner brackets. */
    public val BracketStroke: Dp = 6.dp

    /** Corner radius where each bracket turns. */
    public val BracketRadius: Dp = 18.dp

    /** Thickness of the sweeping scan line. */
    public val ScanLineThickness: Dp = 4.dp

    /** Duration of one top-to-bottom sweep of the scan line (ms). A continuous
     *  loop, hence outside the discrete
     *  [io.github.joelkanyi.jenga.foundation.motion.JengaMotion] scale. */
    public const val ScanLineDurationMillis: Int = 2000

    /** The dimming scrim drawn around the reticle. */
    public val scrimColor: Color
        @Composable get() = JengaTheme.colors.scrim

    /** The accent color for the given [status]. */
    @Composable
    public fun accentColor(status: JengaScannerStatus): Color = when (status) {
        JengaScannerStatus.Scanning -> JengaTheme.colors.brand
        JengaScannerStatus.Success -> JengaTheme.colors.success
        JengaScannerStatus.Warning -> JengaTheme.colors.warning
        JengaScannerStatus.Error -> JengaTheme.colors.error
    }
}

/**
 * A camera-scanner reticle: a dimming scrim with a clear square cut-out, four
 * rounded corner brackets, and a sweeping scan line. Lay it directly over a
 * camera preview.
 *
 * The accent color follows [status] — brand while [JengaScannerStatus.Scanning]
 * (the only state that animates the scan line), and the matching status accent
 * otherwise.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaScannerViewfinderSample
 *
 * @param modifier the [Modifier] for this overlay (typically `fillMaxSize`).
 * @param status the scan state driving the accent and scan-line animation.
 * @param frameSizeRatio reticle side length as a fraction of the smaller edge.
 */
@Composable
public fun JengaScannerViewfinder(
    modifier: Modifier = Modifier,
    status: JengaScannerStatus = JengaScannerStatus.Scanning,
    frameSizeRatio: Float = JengaScannerViewfinderDefaults.FrameSizeRatio,
) {
    val accent = JengaScannerViewfinderDefaults.accentColor(status)
    val scrim = JengaScannerViewfinderDefaults.scrimColor

    val sweep by rememberInfiniteTransition(label = "viewfinder").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = JengaScannerViewfinderDefaults.ScanLineDurationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sweep",
    )

    BoxWithConstraints(modifier.fillMaxSize()) {
        val frameSize = minOf(maxWidth, maxHeight) * frameSizeRatio
        val frameTop = (maxHeight - frameSize) / 2
        val frameLeft = (maxWidth - frameSize) / 2

        // Scrim drawn as four rects around the reticle — a robust cut-out that
        // doesn't rely on BlendMode.Clear (which needs an offscreen layer).
        Canvas(Modifier.fillMaxSize()) {
            val l = frameLeft.toPx()
            val t = frameTop.toPx()
            val s = frameSize.toPx()
            drawRect(scrim, topLeft = Offset(0f, 0f), size = Size(size.width, t))
            drawRect(scrim, topLeft = Offset(0f, t + s), size = Size(size.width, size.height - t - s))
            drawRect(scrim, topLeft = Offset(0f, t), size = Size(l, s))
            drawRect(scrim, topLeft = Offset(l + s, t), size = Size(size.width - l - s, s))
        }

        Box(
            modifier = Modifier
                .size(frameSize)
                .align(Alignment.Center),
        ) {
            CornerBrackets(color = accent)

            if (status == JengaScannerStatus.Scanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(JengaScannerViewfinderDefaults.ScanLineThickness)
                        .offset {
                            IntOffset(x = 0, y = (sweep * frameSize.toPx()).toInt())
                        }
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, accent, Color.Transparent),
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun CornerBrackets(color: Color) {
    val length = JengaScannerViewfinderDefaults.BracketLength
    val strokeWidth = JengaScannerViewfinderDefaults.BracketStroke
    val radius = JengaScannerViewfinderDefaults.BracketRadius
    Canvas(Modifier.fillMaxSize()) {
        val len = length.toPx()
        val rad = radius.toPx()
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        fun cornerPath(startX: Float, startY: Float, hx: Float, vy: Float): Path = Path().apply {
            moveTo(startX + hx * len, startY)
            lineTo(startX + hx * rad, startY)
            quadraticBezierTo(startX, startY, startX, startY + vy * rad)
            lineTo(startX, startY + vy * len)
        }

        drawPath(cornerPath(0f, 0f, 1f, 1f), color, style = stroke)
        drawPath(cornerPath(size.width, 0f, -1f, 1f), color, style = stroke)
        drawPath(cornerPath(0f, size.height, 1f, -1f), color, style = stroke)
        drawPath(cornerPath(size.width, size.height, -1f, -1f), color, style = stroke)
    }
}
