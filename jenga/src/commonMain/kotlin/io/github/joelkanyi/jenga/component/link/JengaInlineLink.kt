package io.github.joelkanyi.jenga.component.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * One link inside the sentence of a [JengaTextWithLinks].
 *
 * @property text the phrase to turn into a link, written exactly as it appears in the sentence.
 * @property enabled whether the link responds to taps and uses its enabled color. A disabled
 *   link is plain text to screen readers: it is read as part of the sentence, with no
 *   "disabled" announcement.
 * @property onClick invoked when the link is tapped; not called while disabled.
 */
@Poko
@Immutable
public class JengaInlineLink(
    public val text: String,
    public val enabled: Boolean = true,
    public val onClick: () -> Unit,
)

/**
 * A [JengaText] whose sentence contains one or more links: each link is in the link color and
 * underlined, wraps with the sentence, and is announced and activated as a link by screen
 * readers and keyboards.
 *
 * Pass the whole sentence as [text] and each link phrase in [links]. The phrase is looked up
 * in the sentence, so a translation can put the link wherever its word order needs it: keep
 * the sentence and each link phrase as separate string resources and translate both.
 *
 * Each entry in [links] claims the first occurrence of its phrase that an earlier entry has
 * not already claimed, matching case exactly. A phrase that does not occur in [text] adds no
 * link; the sentence still shows in full. This is a plain substring match that is not
 * word-aware: the phrase "here" also matches inside "There". When one phrase contains another
 * ("terms of the agreement" and "terms"), list the longer phrase first, or the shorter one
 * claims its words and the longer one adds no link.
 *
 * To build the text yourself (a link that is not a plain phrase, extra emphasis next to it),
 * use [appendJengaLink] in `buildAnnotatedString` and pass the result to [JengaText].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTextWithLinksSample
 *
 * @param text the whole sentence, link phrases included.
 * @param links the phrases of [text] to turn into links, each with its own click.
 * @param modifier the [Modifier] for this text.
 * @param color color of the text around the links; [Color.Unspecified] falls back to [style] then theme.
 * @param style the [TextStyle] of the sentence; the links keep its size and line height.
 * @param textAlign horizontal alignment of the text within its bounds.
 * @param overflow how visual overflow is handled.
 * @param softWrap whether text wraps at soft line breaks.
 * @param maxLines maximum number of lines; excess is truncated per [overflow].
 * @param minLines minimum number of lines to occupy.
 * @param maxFontScale optional cap on the system font scale for this text.
 * @param linkColors the link colors; defaults to [JengaLinkDefaults.colors].
 */
@Composable
public fun JengaTextWithLinks(
    text: String,
    links: List<JengaInlineLink>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = JengaTheme.typography.bodyMedium,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    maxFontScale: Float? = null,
    linkColors: JengaLinkColors = JengaLinkDefaults.colors(),
) {
    val linkStyles = JengaLinkDefaults.inlineStyles(colors = linkColors)
    // A disabled link keeps the link's weight and underline, in the disabled color.
    val disabledStyle = remember(linkStyles, linkColors) {
        (linkStyles.style ?: SpanStyle()).copy(color = linkColors.disabledContent)
    }

    // The annotated string is rebuilt only when the sentence, the phrases or the look change.
    // Clicks go through the latest list, so a caller passing fresh lambdas on every
    // recomposition does not make the text lay out again.
    val currentLinks by rememberUpdatedState(links)
    val phrases = links.map { InlinePhrase(it.text, it.enabled) }
    val annotated = remember(text, phrases, linkStyles, disabledStyle) {
        linkedText(
            text = text,
            phrases = phrases,
            linkStyles = linkStyles,
            disabledStyle = disabledStyle,
            onClick = { index -> currentLinks.getOrNull(index)?.onClick?.invoke() },
        )
    }

    JengaText(
        text = annotated,
        modifier = modifier,
        color = color,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        maxFontScale = maxFontScale,
    )
}

/**
 * Appends [text] as a link inside `buildAnnotatedString`, for a sentence shown with
 * [JengaText]. Read [styles] from [JengaLinkDefaults.inlineStyles] in the composable and
 * build the string inside `remember`, so the text is not laid out again on every
 * recomposition. A remembered string keeps the [onClick] it was built with, so read the
 * callback through `rememberUpdatedState` and call that from [onClick]; otherwise the link
 * keeps calling the lambda from the first composition.
 *
 * The link is a Compose [LinkAnnotation.Clickable], so screen readers announce it as a link
 * and a keyboard can focus and activate it. For a link that is not available, append plain
 * text instead.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaAppendLinkSample
 *
 * @param text the link label.
 * @param styles the link look, from [JengaLinkDefaults.inlineStyles].
 * @param onClick invoked when the link is tapped.
 */
public fun AnnotatedString.Builder.appendJengaLink(
    text: String,
    styles: TextLinkStyles,
    onClick: () -> Unit,
) {
    withLink(LinkAnnotation.Clickable(tag = text, styles = styles) { onClick() }) { append(text) }
}

/** The part of a [JengaInlineLink] that decides how the sentence is built. */
private data class InlinePhrase(val text: String, val enabled: Boolean)

/**
 * Builds [text] with each of [phrases] turned into a link (or, when disabled, into plain text
 * in [disabledStyle]). [onClick] receives the index of the tapped phrase.
 */
private fun linkedText(
    text: String,
    phrases: List<InlinePhrase>,
    linkStyles: TextLinkStyles,
    disabledStyle: SpanStyle,
    onClick: (index: Int) -> Unit,
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)
    val claimed = mutableListOf<IntRange>()
    phrases.forEachIndexed { index, phrase ->
        val range = text.firstUnclaimed(phrase.text, claimed) ?: return@forEachIndexed
        claimed += range
        val end = range.last + 1
        if (phrase.enabled) {
            builder.addLink(
                LinkAnnotation.Clickable(tag = phrase.text, styles = linkStyles) { onClick(index) },
                range.first,
                end,
            )
        } else {
            builder.addStyle(disabledStyle, range.first, end)
        }
    }
    return builder.toAnnotatedString()
}

/** The first occurrence of [phrase] that overlaps none of [claimed], or null. */
private fun String.firstUnclaimed(phrase: String, claimed: List<IntRange>): IntRange? {
    if (phrase.isEmpty()) return null
    var from = 0
    while (true) {
        val start = indexOf(phrase, from)
        if (start < 0) return null
        val range = start until start + phrase.length
        val overlap = claimed.firstOrNull { it.first <= range.last && range.first <= it.last }
        if (overlap == null) return range
        from = overlap.last + 1
    }
}
