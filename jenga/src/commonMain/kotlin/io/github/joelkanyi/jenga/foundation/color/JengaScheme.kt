package io.github.joelkanyi.jenga.foundation.color

import androidx.compose.runtime.Immutable
import dev.drewhamilton.poko.Poko

/**
 * A light + dark [JengaColors] pair: a complete custom color theme as one object.
 * Pass it to `JengaTheme(scheme = ...)` and the right scheme is chosen for the
 * current `darkTheme`, so the caller never wires the selection themselves.
 *
 * ```
 * val brandColors = JengaScheme(
 *     light = jengaLightColors().copy(brand = BrandPurple),
 *     dark = jengaDarkColors().copy(brand = BrandPurpleLight),
 * )
 * JengaTheme(scheme = brandColors) { App() }
 * ```
 */
@Poko
@Immutable
public class JengaScheme(
    public val light: JengaColors,
    public val dark: JengaColors,
)
