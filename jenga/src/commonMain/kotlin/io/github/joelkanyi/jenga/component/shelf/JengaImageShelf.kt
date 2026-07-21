package io.github.joelkanyi.jenga.component.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.action.JengaAction
import io.github.joelkanyi.jenga.component.image.JengaImage
import io.github.joelkanyi.jenga.component.image.JengaImageFit
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for [JengaImageShelf]/[JengaShelfCard]. */
public object JengaShelfDefaults {
    /** Default width of a [JengaShelfCard]. */
    public val CardWidth: Dp = 164.dp

    /** Default image aspect ratio of a [JengaShelfCard]. */
    public const val CardAspectRatio: Float = 4f / 3f

    /** Screen gutter the shelf content is inset by. */
    public val Gutter: Dp = 16.dp

    /** The image-slot clip shape. */
    public val cardShape: Shape
        @Composable get() = JengaTheme.shapes.cardLarge

    /** Themed fallback fill for a card with no image (never a grey box). */
    public val fallback: Brush
        @Composable get() = JengaTheme.colors.let {
            Brush.linearGradient(listOf(lerp(it.brand, it.surface, 0.35f), it.surfaceVariant))
        }
}

/**
 * A horizontal, edge-to-edge shelf: an optional [eyebrow] + [title] header with
 * an optional trailing action, over a horizontally scrolling row of [content]
 * (typically [JengaShelfCard]s). Content is inset by the screen [gutter] so
 * cards bleed to the edges as they scroll. Generic and domain-neutral.
 *
 * @param title the shelf title.
 * @param modifier the [Modifier] for the shelf.
 * @param eyebrow an optional small label above the title (e.g. a promo reason).
 * @param action optional trailing header action (label plus handler), e.g. "See all".
 * @param gutter horizontal inset applied to the header and the scroll content.
 * @param content the row items, laid out left-to-right in a scrolling row.
 */
@Composable
public fun JengaImageShelf(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    action: JengaAction? = null,
    gutter: Dp = JengaShelfDefaults.Gutter,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = gutter),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(Modifier.weight(1f)) {
                if (eyebrow != null) {
                    JengaText(
                        text = eyebrow,
                        style = JengaTheme.typography.label,
                        color = JengaTheme.colors.onBrandSubtle,
                        maxLines = 1,
                    )
                }
                JengaText(
                    text = title,
                    style = JengaTheme.typography.headingSmall,
                    color = JengaTheme.colors.textPrimary,
                    maxLines = 1,
                )
            }
            if (action != null) {
                JengaText(
                    text = action.label,
                    style = JengaTheme.typography.titleSmall,
                    color = JengaTheme.colors.brand,
                    maxLines = 1,
                    modifier = Modifier.clickable(onClick = action.onClick),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = gutter),
            horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
        ) {
            content()
        }
    }
}

/**
 * A single card in a [JengaImageShelf]: an image slot (cropped, with a themed
 * fallback and an optional [overlayTag]), a title, and an optional meta line.
 * Generic and token-driven.
 *
 * @param title the card title.
 * @param modifier the [Modifier] for the card.
 * @param imageUrl the image URL, or null/blank to show the [fallback].
 * @param meta an optional single meta line (e.g. "25 min · ≈540 kcal").
 * @param width the card width.
 * @param aspectRatio the image slot's width:height ratio.
 * @param onClick optional tap handler.
 * @param overlayTag optional small overlay in the image's top-start (e.g. a tag pill).
 * @param fallback drawn in the image slot when there is no image and no [fallbackContent].
 * @param fallbackContent an optional composable fallback (e.g. a branded motif) drawn in
 *   the image slot when there is no image; when non-null it replaces the [fallback] brush.
 */
@Composable
public fun JengaShelfCard(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    meta: String? = null,
    width: Dp = JengaShelfDefaults.CardWidth,
    aspectRatio: Float = JengaShelfDefaults.CardAspectRatio,
    onClick: (() -> Unit)? = null,
    overlayTag: (@Composable () -> Unit)? = null,
    fallback: Brush = JengaShelfDefaults.fallback,
    fallbackContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .width(width)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(JengaShelfDefaults.cardShape),
        ) {
            if (fallbackContent != null) fallbackContent() else Box(Modifier.fillMaxSize().background(fallback))
            JengaImage(
                url = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                shape = RectangleShape,
                fit = JengaImageFit.Cover,
                placeholderColor = Color.Transparent,
            )
            if (overlayTag != null) {
                Box(Modifier.align(Alignment.TopStart).padding(JengaTheme.spacing.sm)) {
                    overlayTag()
                }
            }
        }
        Column {
            JengaText(
                text = title,
                style = JengaTheme.typography.titleSmall,
                color = JengaTheme.colors.textPrimary,
                maxLines = 1,
            )
            if (meta != null) {
                JengaText(
                    text = meta,
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                    maxLines = 1,
                )
            }
        }
    }
}
