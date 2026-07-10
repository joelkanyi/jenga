package io.github.joelkanyi.jenga.component.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import io.github.joelkanyi.jenga.theme.JengaTheme

/** How a [JengaImage] fills its bounds. */
public enum class JengaImageFit {
    /** Fill the bounds and crop the overflow (photos, hero images). */
    Cover,

    /** Fit entirely inside the bounds, letterboxing if the ratios differ. */
    Contain,
}

/** Defaults and token mappings for [JengaImage]. */
public object JengaImageDefaults {
    /** Default corner shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.card

    /** The fill shown while loading, or when there is no image to show. */
    public val placeholderColor: Color
        @Composable get() = JengaTheme.colors.surfaceVariant
}

/**
 * An async image loaded from a URL, clipped to a Jenga shape over a themed placeholder.
 *
 * Backed by Coil; the consuming app provides the network engine used to fetch it. When
 * [url] is null or blank only the placeholder shows, so content with no image (a dish
 * without a photo yet) degrades gracefully instead of breaking the layout.
 *
 * @param url the image URL, or null for none.
 * @param contentDescription accessibility description, or null if the image is decorative.
 * @param modifier the [Modifier] for this image; set a size on it.
 * @param shape the clip shape; defaults to [JengaImageDefaults.shape].
 * @param fit how the image fills its bounds; see [JengaImageFit].
 * @param placeholderColor the fill shown while loading or when there is no image.
 */
@Composable
public fun JengaImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = JengaImageDefaults.shape,
    fit: JengaImageFit = JengaImageFit.Cover,
    placeholderColor: Color = JengaImageDefaults.placeholderColor,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(placeholderColor),
    ) {
        if (!url.isNullOrBlank()) {
            PlatformAsyncImage(url, contentDescription, fit)
        }
    }
}

/**
 * Platform image loader, filling its parent [Box]. Android loads it with Coil; other
 * targets currently draw nothing (the [JengaImage] placeholder shows) until their
 * async-image toolchain is wired.
 */
@Composable
internal expect fun PlatformAsyncImage(
    url: String,
    contentDescription: String?,
    fit: JengaImageFit,
)
