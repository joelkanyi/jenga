package io.github.joelkanyi.jenga

import androidx.compose.ui.graphics.Color
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.foundation.color.jengaDarkColors
import io.github.joelkanyi.jenga.foundation.color.jengaLightColors
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Depth guardrails: the sibling of [JengaContrastTest] for *separation* rather than text
 * legibility. Born from a real report: cards were nearly the same colour as the page, and
 * their borders were too faint to read the card's edge. These are enforceable so it can
 * never regress:
 *
 * - A card [JengaColors.surface] must sit perceptibly off the [JengaColors.background].
 * - A [JengaColors.border] hairline must be visible against the surface it draws on.
 * - The dark elevation ramp must be monotonic (recessed darker, raised lighter), because
 *   in a dark theme depth reads as *lightness*, not shadow.
 */
class JengaSurfaceSeparationTest {

    // A card must lift off the page. Dark themes convey elevation as lightness, so the step
    // is larger; light themes lift a white card off a near-white page with a gentler step
    // (plus the border below), so the floor is lower.
    private val darkSurfaceLift = 1.15
    private val lightSurfaceLift = 1.05

    // A hairline border must be legible against the surface it sits on, both schemes.
    private val borderVisibility = 1.20

    @Test
    fun darkCardsLiftOffThePage() {
        val c = jengaDarkColors()
        atLeast(darkSurfaceLift, c.surface, c.background, "dark surface vs background")
        atLeast(borderVisibility, c.border, c.surface, "dark border on surface")
    }

    @Test
    fun lightCardsLiftOffThePage() {
        val c = jengaLightColors()
        atLeast(lightSurfaceLift, c.surface, c.background, "light surface vs background")
        atLeast(borderVisibility, c.border, c.surface, "light border on surface")
    }

    @Test
    fun theDarkElevationRampIsMonotonic() {
        val c = jengaDarkColors()
        // recessed < page-card < raised; depth is lightness in the dark.
        assertTrue("surfaceSunk must be no lighter than surface", luminance(c.surfaceSunk) <= luminance(c.surface))
        assertTrue("surface must be lighter than background", luminance(c.surface) > luminance(c.background))
        assertTrue("surfaceVariant (raised) must be lighter than surface", luminance(c.surfaceVariant) > luminance(c.surface))
        assertTrue("borderStrong must be at least as strong as border", luminance(c.borderStrong) >= luminance(c.border))
    }

    private fun atLeast(min: Double, a: Color, b: Color, label: String) {
        val ratio = contrast(a, b)
        assertTrue(
            "$label: separation ${"%.3f".format(ratio)}:1 is below ${"%.2f".format(min)}:1 (too faint)",
            ratio >= min,
        )
    }

    private fun contrast(a: Color, b: Color): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        return (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)
    }

    private fun luminance(color: Color): Double {
        fun channel(v: Float): Double {
            val d = v.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
    }
}
