package io.github.joelkanyi.jenga.component.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.isSpecified
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/**
 * The canonical way to render text in Jenga.
 *
 * Defaults to [JengaTheme] tokens: [JengaTheme.typography]`.bodyMedium` and
 * [JengaTheme.colors]`.textPrimary`. Built on Compose Foundation's `BasicText`
 * (no Material dependency), so its look is governed entirely by Jenga tokens.
 *
 * Color resolution order: the explicit [color] param → the [style]'s color →
 * the inherited [LocalJengaContentColor] (e.g. set by a button) → the theme's
 * [JengaTheme.colors]`.textPrimary`.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTextSample
 *
 * @param text the string to display.
 * @param modifier the [Modifier] for this text.
 * @param color text color; [Color.Unspecified] falls back to [style] then theme.
 * @param style the [TextStyle]; defaults to body text from the theme.
 * @param textAlign horizontal alignment of the text within its bounds.
 * @param overflow how visual overflow is handled.
 * @param softWrap whether text wraps at soft line breaks.
 * @param maxLines maximum number of lines; excess is truncated per [overflow].
 * @param minLines minimum number of lines to occupy.
 * @param maxFontScale optional cap on the system font scale for this text (e.g.
 *   `1.25f` for display type). See [io.github.joelkanyi.jenga.foundation.typography.JengaTypographyDefaults].
 */
@Composable
public fun JengaText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = JengaTheme.typography.bodyMedium,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    maxFontScale: Float? = null,
) {
    val resolvedColor = color
        .takeOrElse { style.color }
        .takeOrElse { LocalJengaContentColor.current }
        .takeOrElse { JengaTheme.colors.textPrimary }

    val mergedStyle = style
        .merge(color = resolvedColor, textAlign = textAlign ?: TextAlign.Unspecified)
        .cappedTo(maxFontScale)

    BasicText(
        text = text,
        modifier = modifier,
        style = mergedStyle,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * [AnnotatedString] overload of [JengaText] for rich/multi-style text (links,
 * partial emphasis). Same color-resolution and [maxFontScale] rules as the
 * [String] overload.
 *
 * @param text the styled text to display.
 * @param modifier the [Modifier] for this text.
 * @param color base text color; [Color.Unspecified] falls back to [style] then theme.
 * @param style the [TextStyle]; defaults to body text from the theme.
 * @param textAlign horizontal alignment of the text within its bounds.
 * @param overflow how visual overflow is handled.
 * @param softWrap whether text wraps at soft line breaks.
 * @param maxLines maximum number of lines; excess is truncated per [overflow].
 * @param minLines minimum number of lines to occupy.
 * @param maxFontScale optional cap on the system font scale for this text.
 */
@Composable
public fun JengaText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = JengaTheme.typography.bodyMedium,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    maxFontScale: Float? = null,
) {
    val resolvedColor = color
        .takeOrElse { style.color }
        .takeOrElse { LocalJengaContentColor.current }
        .takeOrElse { JengaTheme.colors.textPrimary }

    val mergedStyle = style
        .merge(color = resolvedColor, textAlign = textAlign ?: TextAlign.Unspecified)
        .cappedTo(maxFontScale)

    BasicText(
        text = text,
        modifier = modifier,
        style = mergedStyle,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * Clamps the effective system font scale of this style to [maxFontScale] by
 * shrinking sp sizes proportionally when the user's scale exceeds the cap
 * (PayPal's `maxScaleFactor`). No-op when [maxFontScale] is null or not exceeded.
 */
@Composable
private fun TextStyle.cappedTo(maxFontScale: Float?): TextStyle {
    if (maxFontScale == null) return this
    val fontScale = LocalDensity.current.fontScale
    if (fontScale <= maxFontScale) return this
    val factor = maxFontScale / fontScale
    return copy(
        fontSize = if (fontSize.isSpecified) fontSize * factor else fontSize,
        lineHeight = if (lineHeight.isSpecified) lineHeight * factor else lineHeight,
    )
}
