package io.github.joelkanyi.jenga.component.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaToggle]. Override via [JengaToggleDefaults.colors]. */
@Immutable
public data class JengaToggleColors(
    public val checkedTrack: Color,
    public val uncheckedTrack: Color,
    public val disabledTrack: Color,
    public val thumb: Color,
)

/** Defaults and token mappings for [JengaToggle]. */
public object JengaToggleDefaults {
    public val Width: Dp = 44.dp
    public val Height: Dp = 26.dp
    public val ThumbSize: Dp = 20.dp
    public val ThumbPadding: Dp = 3.dp

    /** Travel distance of the thumb between off and on. */
    public val ThumbTravel: Dp = Width - ThumbSize - ThumbPadding * 2

    /** Themed colors for the toggle. */
    @Composable
    public fun colors(): JengaToggleColors {
        val c = JengaTheme.colors
        return JengaToggleColors(
            checkedTrack = c.brand,
            uncheckedTrack = c.borderStrong,
            disabledTrack = c.surfaceDisabled,
            thumb = c.onBrand,
        )
    }
}

/**
 * An on/off switch.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaToggleSample
 *
 * @param checked whether the toggle is on.
 * @param onCheckedChange called with the new state; pass `null` for a read-only,
 *   externally-driven toggle.
 * @param modifier the [Modifier] for this toggle.
 * @param enabled whether the toggle is interactive.
 * @param colors the color set; defaults to [JengaToggleDefaults.colors].
 */
@Composable
public fun JengaToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: JengaToggleColors = JengaToggleDefaults.colors(),
) {
    val motion = JengaTheme.motion
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledTrack
            checked -> colors.checkedTrack
            else -> colors.uncheckedTrack
        },
        animationSpec = tween(motion.durationFast, easing = motion.standard),
        label = "trackColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) JengaToggleDefaults.ThumbTravel else 0.dp,
        animationSpec = tween(motion.durationFast, easing = motion.standard),
        label = "thumbOffset",
    )

    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(toggleModifier)
            .size(width = JengaToggleDefaults.Width, height = JengaToggleDefaults.Height)
            .clip(JengaTheme.shapes.pill)
            .background(trackColor)
            .padding(JengaToggleDefaults.ThumbPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.roundToPx(), 0) }
                .size(JengaToggleDefaults.ThumbSize)
                .clip(JengaTheme.shapes.pill)
                .background(colors.thumb),
        )
    }
}
