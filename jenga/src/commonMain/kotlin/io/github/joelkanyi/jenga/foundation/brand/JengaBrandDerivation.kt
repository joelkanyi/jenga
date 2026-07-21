package io.github.joelkanyi.jenga.foundation.brand

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.foundation.color.hct.TonalPalette
import io.github.joelkanyi.jenga.foundation.color.jengaDarkColors
import io.github.joelkanyi.jenga.foundation.color.jengaLightColors
import io.github.joelkanyi.jenga.foundation.shape.JengaShapes
import io.github.joelkanyi.jenga.foundation.sizing.JengaSizing
import io.github.joelkanyi.jenga.foundation.sizing.jengaSizing
import io.github.joelkanyi.jenga.foundation.spacing.JengaSpacing
import io.github.joelkanyi.jenga.foundation.spacing.jengaSpacing
import io.github.joelkanyi.jenga.foundation.typography.JengaTypography
import io.github.joelkanyi.jenga.foundation.typography.jengaTypography

// The derived brand-family tones. On-colors sit at the opposite end of the tonal
// ramp from their background, so contrast holds by construction; the contrast
// level widens the gap for the subtle pair.
private fun subtleTone(contrast: JengaContrast, light: Boolean): Int = when (contrast) {
    JengaContrast.Standard -> if (light) 92 else 30
    JengaContrast.Medium -> if (light) 94 else 27
    JengaContrast.High -> if (light) 96 else 24
}

private fun onSubtleTone(contrast: JengaContrast, light: Boolean): Int = when (contrast) {
    JengaContrast.Standard -> if (light) 30 else 90
    JengaContrast.Medium -> if (light) 24 else 92
    JengaContrast.High -> if (light) 16 else 96
}

/** The derived light color scheme: brand family from [seed], neutrals/status kept. */
public fun JengaBrand.lightColors(): JengaColors {
    val brandPalette = TonalPalette.fromInt(seed.toArgb())
    var colors = jengaLightColors().copy(
        brand = Color(brandPalette.tone(40)),
        onBrand = Color(brandPalette.tone(100)),
        brandSubtle = Color(brandPalette.tone(subtleTone(contrast, light = true))),
        onBrandSubtle = Color(brandPalette.tone(onSubtleTone(contrast, light = true))),
        focusRing = Color(brandPalette.tone(40)).copy(alpha = 0.22f),
    )
    accent?.let {
        val accentPalette = TonalPalette.fromInt(it.toArgb())
        colors = colors.copy(ink = Color(accentPalette.tone(30)), onInk = Color(accentPalette.tone(100)))
    }
    return colors
}

/** The derived dark color scheme: brand family from [seed], neutrals/status kept. */
public fun JengaBrand.darkColors(): JengaColors {
    val brandPalette = TonalPalette.fromInt(seed.toArgb())
    var colors = jengaDarkColors().copy(
        brand = Color(brandPalette.tone(80)),
        onBrand = Color(brandPalette.tone(20)),
        brandSubtle = Color(brandPalette.tone(subtleTone(contrast, light = false))),
        onBrandSubtle = Color(brandPalette.tone(onSubtleTone(contrast, light = false))),
        focusRing = Color(brandPalette.tone(80)).copy(alpha = 0.30f),
    )
    accent?.let {
        val accentPalette = TonalPalette.fromInt(it.toArgb())
        colors = colors.copy(ink = Color(accentPalette.tone(80)), onInk = Color(accentPalette.tone(20)))
    }
    return colors
}

/**
 * The derived type scale. Unset families resolve to the system default here;
 * `JengaTheme(brand = ...)` injects Jenga's bundled brand font for unset families.
 */
public fun JengaBrand.typography(): JengaTypography = jengaTypography(
    displayFontFamily = displayFontFamily ?: FontFamily.Default,
    bodyFontFamily = bodyFontFamily ?: FontFamily.Default,
)

/** The derived shape set for the brand's [corner] language. */
public fun JengaBrand.shapes(): JengaShapes = when (corner) {
    JengaCornerStyle.Rounded -> JengaShapes()
    JengaCornerStyle.Sharp -> JengaShapes(
        none = RoundedCornerShape(0.dp),
        xs = RoundedCornerShape(2.dp),
        sm = RoundedCornerShape(3.dp),
        md = RoundedCornerShape(4.dp),
        lg = RoundedCornerShape(6.dp),
        xl = RoundedCornerShape(8.dp),
        control = RoundedCornerShape(4.dp),
        card = RoundedCornerShape(6.dp),
        cardLarge = RoundedCornerShape(8.dp),
        pill = RoundedCornerShape(percent = 50),
    )
    JengaCornerStyle.Soft -> JengaShapes(
        none = RoundedCornerShape(0.dp),
        xs = RoundedCornerShape(8.dp),
        sm = RoundedCornerShape(12.dp),
        md = RoundedCornerShape(18.dp),
        lg = RoundedCornerShape(24.dp),
        xl = RoundedCornerShape(32.dp),
        control = RoundedCornerShape(16.dp),
        card = RoundedCornerShape(24.dp),
        cardLarge = RoundedCornerShape(28.dp),
        pill = RoundedCornerShape(percent = 50),
    )
}

private fun JengaDensity.factor(): Float = when (this) {
    JengaDensity.Compact -> 0.85f
    JengaDensity.Comfortable -> 1f
    JengaDensity.Spacious -> 1.15f
}

/** The derived spacing scale for the brand's [density]. */
public fun JengaBrand.spacing(): JengaSpacing {
    val f = density.factor()
    if (f == 1f) return jengaSpacing()
    val base = jengaSpacing()
    return JengaSpacing(
        none = base.none,
        xxs = base.xxs * f,
        xs = base.xs * f,
        sm = base.sm * f,
        md = base.md * f,
        lg = base.lg * f,
        xl = base.xl * f,
        xxl = base.xxl * f,
        xxxl = base.xxxl * f,
        xxxxl = base.xxxxl * f,
    )
}

/** The derived sizing set for the brand's [density]; touch target and icons stay fixed. */
public fun JengaBrand.sizing(): JengaSizing {
    val f = density.factor()
    if (f == 1f) return jengaSizing()
    val base = jengaSizing()
    return JengaSizing(
        minTouchTarget = base.minTouchTarget,
        iconSmall = base.iconSmall,
        iconMedium = base.iconMedium,
        iconLarge = base.iconLarge,
        controlHeightSmall = base.controlHeightSmall * f,
        controlHeightMedium = base.controlHeightMedium * f,
        controlHeightLarge = base.controlHeightLarge * f,
        fieldHeight = base.fieldHeight * f,
    )
}
