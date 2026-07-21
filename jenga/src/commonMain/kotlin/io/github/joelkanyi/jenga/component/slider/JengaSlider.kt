package io.github.joelkanyi.jenga.component.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlin.math.roundToInt

/** Resolved colors for a [JengaSlider]. Override via [JengaSliderDefaults.colors]. */
@Poko
@Immutable
public class JengaSliderColors(
    public val activeTrack: Color,
    public val inactiveTrack: Color,
    public val thumb: Color,
    public val disabledActiveTrack: Color,
    public val disabledInactiveTrack: Color,
    public val disabledThumb: Color,
) {
    public fun copy(
        activeTrack: Color = this.activeTrack,
        inactiveTrack: Color = this.inactiveTrack,
        thumb: Color = this.thumb,
        disabledActiveTrack: Color = this.disabledActiveTrack,
        disabledInactiveTrack: Color = this.disabledInactiveTrack,
        disabledThumb: Color = this.disabledThumb,
    ): JengaSliderColors = JengaSliderColors(
        activeTrack,
        inactiveTrack,
        thumb,
        disabledActiveTrack,
        disabledInactiveTrack,
        disabledThumb,
    )
}

/** Defaults and token mappings for [JengaSlider]. */
public object JengaSliderDefaults {
    public val TrackHeight: Dp = 6.dp
    public val ThumbSize: Dp = 20.dp

    /** Themed colors for all states. */
    @Composable
    public fun colors(): JengaSliderColors {
        val c = JengaTheme.colors
        return JengaSliderColors(
            activeTrack = c.brand,
            inactiveTrack = c.surfaceSunk,
            thumb = c.brand,
            disabledActiveTrack = c.borderStrong,
            disabledInactiveTrack = c.surfaceSunk,
            disabledThumb = c.contentDisabled,
        )
    }
}

/**
 * A horizontal slider for choosing a value in a range. Drag the thumb to change.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSliderSample
 *
 * @param value the current value (within [valueRange]).
 * @param onValueChange called with the new value as the thumb moves.
 * @param modifier the [Modifier] for this slider.
 * @param enabled whether the slider is interactive.
 * @param valueRange the inclusive value range; defaults to `0f..1f`.
 * @param colors the color set; defaults to [JengaSliderDefaults.colors].
 */
@Composable
public fun JengaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: JengaSliderColors = JengaSliderDefaults.colors(),
) {
    val span = (valueRange.endInclusive - valueRange.start).takeIf { it > 0f } ?: 1f
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)

    var widthPx by remember { mutableFloatStateOf(0f) }
    val thumbPx = with(LocalDensity.current) { JengaSliderDefaults.ThumbSize.toPx() }
    val currentValue by rememberUpdatedState(value)
    val currentOnChange by rememberUpdatedState(onValueChange)

    val dragState = rememberDraggableState { deltaPx ->
        val usable = widthPx - thumbPx
        if (usable > 0f) {
            val curFraction = ((currentValue - valueRange.start) / span).coerceIn(0f, 1f)
            val newFraction = (curFraction + deltaPx / usable).coerceIn(0f, 1f)
            currentOnChange(valueRange.start + newFraction * span)
        }
    }

    val activeTrack = if (enabled) colors.activeTrack else colors.disabledActiveTrack
    val inactiveTrack = if (enabled) colors.inactiveTrack else colors.disabledInactiveTrack
    val thumbColor = if (enabled) colors.thumb else colors.disabledThumb

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(JengaSliderDefaults.ThumbSize + JengaTheme.spacing.lg)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .progressSemantics(value, valueRange, steps = 0)
            .draggable(state = dragState, orientation = Orientation.Horizontal, enabled = enabled),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(JengaSliderDefaults.TrackHeight)
                .clip(JengaTheme.shapes.pill)
                .background(inactiveTrack),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(JengaSliderDefaults.TrackHeight)
                .clip(JengaTheme.shapes.pill)
                .background(activeTrack),
        )
        Box(
            modifier = Modifier
                .offset { IntOffset((fraction * (widthPx - thumbPx)).roundToInt(), 0) }
                .size(JengaSliderDefaults.ThumbSize)
                .clip(JengaTheme.shapes.pill)
                .background(thumbColor),
        )
    }
}
