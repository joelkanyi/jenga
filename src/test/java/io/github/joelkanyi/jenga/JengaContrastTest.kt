package io.github.joelkanyi.jenga

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.foundation.color.JengaPalette
import io.github.joelkanyi.jenga.foundation.color.jengaDarkColors
import io.github.joelkanyi.jenga.foundation.color.jengaLightColors
import io.github.joelkanyi.jenga.foundation.sizing.jengaSizing
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Accessibility guardrails: asserts the semantic color pairs meet WCAG 2.1
 * contrast in both light and dark, and that the minimum touch target is 48dp.
 * Pure JVM (no Android) — runs as a fast unit test and gates CI.
 *
 * - Normal text pairs must meet AA 4.5:1.
 * - Saturated brand fills (white-on-orange) are treated as large/UI text and
 *   must meet 3:1 (a documented brand trade-off).
 */
class JengaContrastTest {

    private val AA = 4.5
    private val AA_LARGE = 3.0

    @Test
    fun lightSchemeMeetsContrast() = assertScheme(jengaLightColors())

    @Test
    fun darkSchemeMeetsContrast() = assertScheme(jengaDarkColors())

    @Test
    fun minimumTouchTargetIs48dp() {
        assertTrue(
            "minTouchTarget must be >= 48dp for accessibility",
            jengaSizing().minTouchTarget.value >= 48f,
        )
    }

    private fun assertScheme(c: JengaColors) {
        // Body / normal text — AA 4.5:1
        atLeast(AA, c.textPrimary, c.background, "textPrimary on background")
        atLeast(AA, c.textPrimary, c.surface, "textPrimary on surface")
        atLeast(AA, c.textSecondary, c.surface, "textSecondary on surface")
        atLeast(AA, c.textMuted, c.surface, "textMuted on surface")

        // Status messaging text on its (possibly translucent) container — AA 4.5:1
        atLeast(AA, c.onSuccessContainer, c.successContainer.over(c.surface), "onSuccessContainer")
        atLeast(AA, c.onWarningContainer, c.warningContainer.over(c.surface), "onWarningContainer")
        atLeast(AA, c.onErrorContainer, c.errorContainer.over(c.surface), "onErrorContainer")
        atLeast(AA, c.onInfoContainer, c.infoContainer.over(c.surface), "onInfoContainer")
        atLeast(AA, c.onBrandSubtle, c.brandSubtle.over(c.surface), "onBrandSubtle")

        // High-contrast inverse surfaces (snackbars/tooltips) — AA 4.5:1
        atLeast(AA, c.inverseOnSurface, c.inverseSurface, "inverseOnSurface on inverseSurface")
        atLeast(AA, c.onInk, c.ink, "onInk on ink")

        // Media overlay: primary content on the (translucent) overlay surface, in
        // its worst-case darkest backdrop — AA 4.5:1. (onOverlayMuted is decorative
        // secondary text and intentionally not contrast-gated.)
        atLeast(AA, c.onOverlay, c.overlaySurface.over(JengaPalette.Black), "onOverlay on overlaySurface")

        // Saturated brand fill (e.g. white on orange) — large/UI 3:1
        atLeast(AA_LARGE, c.onBrand, c.brand, "onBrand on brand")
    }

    private fun Color.over(bg: Color): Color = compositeOver(bg)

    private fun atLeast(min: Double, fg: Color, bg: Color, label: String) {
        val ratio = contrast(fg, bg)
        assertTrue(
            "$label: contrast ${"%.2f".format(ratio)}:1 is below ${"%.1f".format(min)}:1",
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
