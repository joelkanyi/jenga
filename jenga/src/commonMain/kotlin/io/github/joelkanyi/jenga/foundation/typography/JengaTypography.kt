package io.github.joelkanyi.jenga.foundation.typography

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import dev.drewhamilton.poko.Poko

/**
 * **Typography token set** — the Jenga type scale.
 *
 * Sizes are fixed-point ports of the frontend's fluid (`clamp`) scale, tuned
 * for mobile. Note the negative [TextStyle.letterSpacing] that tightens as type
 * gets larger (a deliberate display-type refinement carried over from the web
 * brand). Read via `JengaTheme.typography`; override per-app with [jengaTypography].
 */
@Poko
@Immutable
public class JengaTypography(
    /** Hero / marketing headline. */
    public val display: TextStyle,
    /** Screen title (h1). */
    public val headingLarge: TextStyle,
    /** Section heading (h2). */
    public val headingMedium: TextStyle,
    /** Sub-section heading (h3). */
    public val headingSmall: TextStyle,
    /** Card / list title (h4). */
    public val titleLarge: TextStyle,
    /** Medium title / row heading. */
    public val titleMedium: TextStyle,
    /** Dense title / emphasized body. */
    public val titleSmall: TextStyle,
    /** Lead paragraph / prominent body. */
    public val bodyLarge: TextStyle,
    /** Default body text. */
    public val bodyMedium: TextStyle,
    /** Secondary / supporting body text. */
    public val bodySmall: TextStyle,
    /** Uppercase eyebrow / field label (use with letterSpacing as-is). */
    public val label: TextStyle,
    /** Smallest supporting text (timestamps, captions). */
    public val caption: TextStyle,
    /** Button / call-to-action label. */
    public val button: TextStyle,
) {
    public fun copy(
        display: TextStyle = this.display,
        headingLarge: TextStyle = this.headingLarge,
        headingMedium: TextStyle = this.headingMedium,
        headingSmall: TextStyle = this.headingSmall,
        titleLarge: TextStyle = this.titleLarge,
        titleMedium: TextStyle = this.titleMedium,
        titleSmall: TextStyle = this.titleSmall,
        bodyLarge: TextStyle = this.bodyLarge,
        bodyMedium: TextStyle = this.bodyMedium,
        bodySmall: TextStyle = this.bodySmall,
        label: TextStyle = this.label,
        caption: TextStyle = this.caption,
        button: TextStyle = this.button,
    ): JengaTypography = JengaTypography(
        display,
        headingLarge,
        headingMedium,
        headingSmall,
        titleLarge,
        titleMedium,
        titleSmall,
        bodyLarge,
        bodyMedium,
        bodySmall,
        label,
        caption,
        button,
    )
}

private val LineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

/**
 * Builds the default Jenga type scale.
 *
 * @param fontFamily swap to re-theme typography with a different typeface while
 *   keeping the scale. Defaults to [FontFamily.Default]; `JengaTheme` injects the
 *   Outfit brand family (see [rememberJengaFontFamily]) when none is supplied.
 */
public fun jengaTypography(fontFamily: FontFamily = FontFamily.Default): JengaTypography =
    JengaTypography(
        display = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.8).sp,
            lineHeightStyle = LineHeight,
        ),
        headingLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            letterSpacing = (-0.6).sp,
            lineHeightStyle = LineHeight,
        ),
        headingMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.4).sp,
            lineHeightStyle = LineHeight,
        ),
        headingSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp,
            lineHeightStyle = LineHeight,
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.1).sp,
            lineHeightStyle = LineHeight,
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        label = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.0.sp,
            lineHeightStyle = LineHeight,
        ),
        caption = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.sp,
            lineHeightStyle = LineHeight,
        ),
        button = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.2.sp,
            lineHeightStyle = LineHeight,
        ),
    )

/**
 * Recommended per-tier caps on the system font scale, mirroring PayPal's
 * `maxScaleFactor`. Larger type is capped tighter (it scales the layout more);
 * body/label allow the full range. Pass these to `JengaText(maxFontScale = …)`.
 */
public object JengaTypographyDefaults {
    /** Display type: cap at 125%. */
    public const val DisplayMaxFontScale: Float = 1.25f
    /** Headings: cap at 150%. */
    public const val HeadingMaxFontScale: Float = 1.5f
    /** Titles: cap at 150%. */
    public const val TitleMaxFontScale: Float = 1.5f
    /** Body text: allow up to 200%. */
    public const val BodyMaxFontScale: Float = 2.0f
    /** Labels/captions: allow up to 200%. */
    public const val LabelMaxFontScale: Float = 2.0f
}
