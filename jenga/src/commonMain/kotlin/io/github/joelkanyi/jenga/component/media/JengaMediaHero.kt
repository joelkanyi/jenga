package io.github.joelkanyi.jenga.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.image.JengaImage
import io.github.joelkanyi.jenga.component.image.JengaImageFit
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaMediaHero]. Override via [JengaMediaHeroDefaults.colors]. */
@Poko
@Immutable
public class JengaMediaHeroColors(
    /** The fallback fill shown when there is no image (never a grey box). */
    public val fallback: Brush,
    /** The bottom scrim that keeps title/support legible over any image. */
    public val scrim: Brush,
    public val title: Color,
    public val support: Color,
) {
    public fun copy(
        fallback: Brush = this.fallback,
        scrim: Brush = this.scrim,
        title: Color = this.title,
        support: Color = this.support,
    ): JengaMediaHeroColors = JengaMediaHeroColors(fallback, scrim, title, support)
}

/** Defaults and token mappings for [JengaMediaHero]. */
public object JengaMediaHeroDefaults {
    /** Default hero aspect ratio (16:10). */
    public const val AspectRatio: Float = 16f / 10f

    /** Default clip shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.cardLarge

    /** Themed colors: a brand-derived fallback, a bottom scrim, light text. */
    @Composable
    public fun colors(): JengaMediaHeroColors {
        val c = JengaTheme.colors
        val ink = c.scrim.copy(alpha = 1f)
        return JengaMediaHeroColors(
            fallback = Brush.linearGradient(
                listOf(c.brand, lerp(c.brand, ink, 0.62f)),
            ),
            scrim = Brush.verticalGradient(
                0.0f to Color.Transparent,
                0.55f to Color.Transparent,
                1.0f to c.scrim.copy(alpha = 0.86f),
            ),
            title = c.onOverlay,
            support = c.onOverlayMuted,
        )
    }
}

/**
 * A full-bleed media hero: an image (cropped, never stretched) under a bottom
 * scrim with a title and one support line, plus optional top-start [badge] and
 * top-end [action] overlays. When [imageUrl] is null/blank a themed [fallback]
 * shows instead of a grey box, so the layout never breaks.
 *
 * Generic and domain-neutral: supply your own [fallback] slot for a branded
 * placeholder motif, or your own [colors]/[titleStyle].
 *
 * @param title the hero title (over the scrim, bottom-start).
 * @param modifier the [Modifier] for this hero; it sizes to [aspectRatio].
 * @param imageUrl the image URL, or null/blank to show the [fallback].
 * @param support an optional single support line under the title.
 * @param aspectRatio width:height ratio of the hero.
 * @param onClick optional tap handler for the whole hero.
 * @param badge optional top-start overlay (e.g. a status pill).
 * @param action optional top-end overlay (e.g. an icon button).
 * @param titleStyle the title text style; defaults to the display style.
 * @param colors the color set; defaults to [JengaMediaHeroDefaults.colors].
 * @param overline an optional slot rendered just above the title (e.g. a provenance
 *   pill); inherits the title color.
 * @param fallback drawn behind the image (and shown alone when there is none);
 *   defaults to the themed [JengaMediaHeroColors.fallback] fill.
 */
@Composable
public fun JengaMediaHero(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    support: String? = null,
    aspectRatio: Float = JengaMediaHeroDefaults.AspectRatio,
    onClick: (() -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    titleStyle: TextStyle = JengaTheme.typography.display,
    colors: JengaMediaHeroColors = JengaMediaHeroDefaults.colors(),
    shape: Shape = JengaMediaHeroDefaults.shape,
    overline: (@Composable () -> Unit)? = null,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        if (fallback != null) fallback() else Box(Modifier.fillMaxSize().background(colors.fallback))
        JengaImage(
            url = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            shape = RectangleShape,
            fit = JengaImageFit.Cover,
            placeholderColor = Color.Transparent,
        )
        Box(Modifier.fillMaxSize().background(colors.scrim))
        if (badge != null || action != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(JengaTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (badge != null) badge()
                Box(Modifier.weight(1f))
                if (action != null) action()
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(JengaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
        ) {
            if (overline != null) {
                CompositionLocalProvider(LocalJengaContentColor provides colors.title) { overline() }
            }
            JengaText(text = title, style = titleStyle, color = colors.title)
            if (support != null) {
                JengaText(text = support, style = JengaTheme.typography.bodyMedium, color = colors.support)
            }
        }
    }
}
