package io.github.joelkanyi.jenga.foundation.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Immutable
import dev.drewhamilton.poko.Poko

/**
 * **Motion token set**: animation durations and easing curves.
 *
 * Durations are milliseconds, ported from the frontend's transition timings
 * (`.12s`/`.16s`/`.22s`/`.4s`). [emphasized] is the brand's signature curve
 * (`cubic-bezier(0.2, 0.8, 0.2, 1)`) used for entrances and prominent moves;
 * [standard] suits small state changes. Read via `JengaTheme.motion`.
 */
@Poko
@Immutable
public class JengaMotion(
    /** Micro-interactions: hovers, small toggles (~120ms). */
    public val durationFast: Int = 120,
    /** Default UI transitions (~180ms). */
    public val durationMedium: Int = 180,
    /** Larger moves: sheets, expands (~240ms). */
    public val durationSlow: Int = 240,
    /** Deliberate, attention-drawing moves: progress fills (~400ms). */
    public val durationSlowest: Int = 400,
    /** Brand entrance/emphasis curve. */
    public val emphasized: Easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f),
    /** General-purpose curve for small state changes. */
    public val standard: Easing = FastOutSlowInEasing,
    /** Constant-rate curve for looping/indeterminate motion. */
    public val linear: Easing = LinearEasing,
) {
    public fun copy(
        durationFast: Int = this.durationFast,
        durationMedium: Int = this.durationMedium,
        durationSlow: Int = this.durationSlow,
        durationSlowest: Int = this.durationSlowest,
        emphasized: Easing = this.emphasized,
        standard: Easing = this.standard,
        linear: Easing = this.linear,
    ): JengaMotion = JengaMotion(
        durationFast,
        durationMedium,
        durationSlow,
        durationSlowest,
        emphasized,
        standard,
        linear,
    )
}

/** The default Jenga motion set. */
public fun jengaMotion(): JengaMotion = JengaMotion()
