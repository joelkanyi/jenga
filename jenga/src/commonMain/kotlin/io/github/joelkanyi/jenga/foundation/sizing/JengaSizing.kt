package io.github.joelkanyi.jenga.foundation.sizing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko

/**
 * **Sizing token set** — component dimensions, kept separate from [spacing][io.github.joelkanyi.jenga.foundation.spacing.JengaSpacing].
 *
 * Sizing (touch targets, icon sizes, control heights) is its own token category
 * rather than being conflated with spacing or hardcoded per component. Read via
 * `JengaTheme.sizing`.
 *
 * [minTouchTarget] is the accessibility minimum (48dp — Material/Lyft); Jenga's
 * interactive blocks expand their touch area to at least this even when drawn
 * smaller.
 */
@Poko
@Immutable
public class JengaSizing(
    public val minTouchTarget: Dp = 48.dp,
    public val iconSmall: Dp = 16.dp,
    public val iconMedium: Dp = 20.dp,
    public val iconLarge: Dp = 24.dp,
    public val controlHeightSmall: Dp = 36.dp,
    public val controlHeightMedium: Dp = 48.dp,
    public val controlHeightLarge: Dp = 56.dp,
    public val fieldHeight: Dp = 52.dp,
) {
    public fun copy(
        minTouchTarget: Dp = this.minTouchTarget,
        iconSmall: Dp = this.iconSmall,
        iconMedium: Dp = this.iconMedium,
        iconLarge: Dp = this.iconLarge,
        controlHeightSmall: Dp = this.controlHeightSmall,
        controlHeightMedium: Dp = this.controlHeightMedium,
        controlHeightLarge: Dp = this.controlHeightLarge,
        fieldHeight: Dp = this.fieldHeight,
    ): JengaSizing = JengaSizing(
        minTouchTarget,
        iconSmall,
        iconMedium,
        iconLarge,
        controlHeightSmall,
        controlHeightMedium,
        controlHeightLarge,
        fieldHeight,
    )
}

/** The default Jenga sizing set. */
public fun jengaSizing(): JengaSizing = JengaSizing()
