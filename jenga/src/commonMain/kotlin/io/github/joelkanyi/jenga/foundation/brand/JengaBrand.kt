package io.github.joelkanyi.jenga.foundation.brand

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIconSet

/** How far apart the derived on-color and its background sit; higher widens the gap. */
public enum class JengaContrast { Standard, Medium, High }

/** The corner language of the derived shape set. */
public enum class JengaCornerStyle { Rounded, Soft, Sharp }

/** How tightly the derived spacing and control sizes pack. */
public enum class JengaDensity { Compact, Comfortable, Spacious }

/**
 * A single brand definition that derives a whole theme: a coherent, accessible
 * light and dark scheme plus type, shape and density, from a few inputs.
 *
 * Only the brand accent family and (optionally) the ink action color are derived
 * from color; neutral surfaces and semantic status colors keep Jenga's tuned
 * defaults so meaning does not rotate with the brand hue. Build one with
 * [jengaBrand] and pass it to `JengaTheme(brand = ...)`.
 */
@Poko
@Immutable
public class JengaBrand internal constructor(
    public val seed: Color,
    public val accent: Color?,
    public val contrast: JengaContrast,
    public val displayFontFamily: FontFamily?,
    public val bodyFontFamily: FontFamily?,
    public val corner: JengaCornerStyle,
    public val density: JengaDensity,
    public val icons: JengaIconSet?,
)

/**
 * Builds a [JengaBrand]. Only [seed] is required; everything else keeps a sensible
 * default so `jengaBrand(seed = brandColor)` yields a full themed light+dark scheme.
 *
 * @param seed the primary brand accent; the brand color family is derived from it.
 * @param accent an optional secondary action color; drives `ink`/`onInk` when set.
 * @param contrast how far apart derived on-colors sit from their backgrounds.
 * @param fontFamily a single face for the whole type scale, and the default for the
 *   two below. Null keeps Jenga's bundled default (resolved by `JengaTheme`).
 * @param displayFontFamily the face for display, heading and title roles.
 * @param bodyFontFamily the face for body, label, caption and button roles.
 * @param corner the corner language for the derived shapes.
 * @param density how tightly spacing and control sizes pack.
 * @param icons a custom icon set; null keeps Jenga's own (resolved by `JengaTheme`).
 */
public fun jengaBrand(
    seed: Color,
    accent: Color? = null,
    contrast: JengaContrast = JengaContrast.Standard,
    fontFamily: FontFamily? = null,
    displayFontFamily: FontFamily? = fontFamily,
    bodyFontFamily: FontFamily? = fontFamily,
    corner: JengaCornerStyle = JengaCornerStyle.Rounded,
    density: JengaDensity = JengaDensity.Comfortable,
    icons: JengaIconSet? = null,
): JengaBrand = JengaBrand(
    seed = seed,
    accent = accent,
    contrast = contrast,
    displayFontFamily = displayFontFamily,
    bodyFontFamily = bodyFontFamily,
    corner = corner,
    density = density,
    icons = icons,
)
