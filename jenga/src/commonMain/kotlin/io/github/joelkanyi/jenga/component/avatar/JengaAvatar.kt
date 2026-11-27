package io.github.joelkanyi.jenga.component.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Size of a [JengaAvatar]. */
public enum class JengaAvatarSize { Small, Medium, Large }

/**
 * Resolved colors for a [JengaAvatar]. Override via [JengaAvatarDefaults.colors], or pick the grey
 * preset [JengaAvatarDefaults.neutralColors].
 *
 * @property container the circle's fill.
 * @property content the color of the initials, and the content color handed to the content slot.
 */
@Poko
@Immutable
public class JengaAvatarColors(
    public val container: Color,
    public val content: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
    ): JengaAvatarColors = JengaAvatarColors(container, content)
}

/**
 * Theme-wide metrics for one [JengaAvatarSize], part of a [JengaAvatarStyle]. A `null` value keeps
 * Jenga's default.
 *
 * @property diameter the avatar diameter.
 * @property textStyle the initials text style.
 */
@Poko
@Immutable
public class JengaAvatarSizeStyle(
    public val diameter: Dp? = null,
    public val textStyle: TextStyle? = null,
) {
    public fun copy(
        diameter: Dp? = this.diameter,
        textStyle: TextStyle? = this.textStyle,
    ): JengaAvatarSizeStyle = JengaAvatarSizeStyle(diameter, textStyle)
}

/**
 * Theme-wide style for every [JengaAvatar], set through
 * [io.github.joelkanyi.jenga.theme.JengaComponentStyles.avatar]. A `null` value keeps Jenga's
 * default; arguments passed to a call still win.
 *
 * @property small metrics for [JengaAvatarSize.Small].
 * @property medium metrics for [JengaAvatarSize.Medium].
 * @property large metrics for [JengaAvatarSize.Large].
 * @property colors the color set of every avatar, initials or content; e.g.
 *   [JengaAvatarDefaults.neutralColors] for grey avatars.
 */
@Poko
@Immutable
public class JengaAvatarStyle(
    public val small: JengaAvatarSizeStyle? = null,
    public val medium: JengaAvatarSizeStyle? = null,
    public val large: JengaAvatarSizeStyle? = null,
    public val colors: JengaAvatarColors? = null,
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(
        small: JengaAvatarSizeStyle? = null,
        medium: JengaAvatarSizeStyle? = null,
        large: JengaAvatarSizeStyle? = null,
    ) : this(small, medium, large, colors = null)

    public fun copy(
        small: JengaAvatarSizeStyle? = this.small,
        medium: JengaAvatarSizeStyle? = this.medium,
        large: JengaAvatarSizeStyle? = this.large,
        colors: JengaAvatarColors? = this.colors,
    ): JengaAvatarStyle = JengaAvatarStyle(small, medium, large, colors)

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(
        small: JengaAvatarSizeStyle? = this.small,
        medium: JengaAvatarSizeStyle? = this.medium,
        large: JengaAvatarSizeStyle? = this.large,
    ): JengaAvatarStyle = copy(small, medium, large, this.colors)

    internal fun size(size: JengaAvatarSize): JengaAvatarSizeStyle? = when (size) {
        JengaAvatarSize.Small -> small
        JengaAvatarSize.Medium -> medium
        JengaAvatarSize.Large -> large
    }
}

/**
 * Defaults for [JengaAvatar]. [textStyle], [colors] and [contentSlotColors] follow the theme's
 * [JengaAvatarStyle]; [diameter] is Jenga's built-in value, and [JengaAvatar] resolves the theme's
 * diameter over it.
 */
public object JengaAvatarDefaults {
    /**
     * Themed colors of an initials avatar: the theme's [JengaAvatarStyle.colors], else the brand
     * tint.
     */
    @Composable
    public fun colors(): JengaAvatarColors = JengaTheme.components.avatar?.colors ?: JengaAvatarColors(
        container = JengaTheme.colors.brandSubtle,
        content = JengaTheme.colors.onBrandSubtle,
    )

    /**
     * Grey colors, for an avatar that should not carry the brand color. Pass them to a call, or
     * set them once as the theme's [JengaAvatarStyle.colors]. The fill is the theme's
     * `surfaceVariant`, so it is meant for an avatar on a `surface` (a list row, a card). In the
     * default light scheme `surfaceVariant` equals `background`, so on the page background only
     * the initials show.
     */
    @Composable
    public fun neutralColors(): JengaAvatarColors = JengaAvatarColors(
        container = JengaTheme.colors.surfaceVariant,
        content = JengaTheme.colors.textSecondary,
    )

    /**
     * Themed colors of an avatar with a content slot: the theme's [JengaAvatarStyle.colors], else
     * a sunk surface behind the content and the content color already in effect.
     */
    @Composable
    public fun contentSlotColors(): JengaAvatarColors = JengaTheme.components.avatar?.colors ?: JengaAvatarColors(
        container = JengaTheme.colors.surfaceSunk,
        content = LocalJengaContentColor.current,
    )

    /** Diameter per [size]. */
    public fun diameter(size: JengaAvatarSize): Dp = when (size) {
        JengaAvatarSize.Small -> 28.dp
        JengaAvatarSize.Medium -> 40.dp
        JengaAvatarSize.Large -> 56.dp
    }

    /** Initials text style per [size]. */
    @Composable
    public fun textStyle(size: JengaAvatarSize): TextStyle = JengaTheme.components.avatar?.size(size)?.textStyle
        ?: JengaTheme.typography.titleSmall.copy(
            fontSize = when (size) {
                JengaAvatarSize.Small -> 11.sp
                JengaAvatarSize.Medium -> 15.sp
                JengaAvatarSize.Large -> 20.sp
            },
            fontWeight = FontWeight.Bold,
        )
}

/**
 * A circular avatar showing up to two initials derived from [name].
 *
 * For an image avatar, use the [content] slot overload.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaAvatarSample
 *
 * @param name the full name; initials are derived from its first two words.
 * @param modifier the [Modifier] for this avatar.
 * @param size the avatar size preset; see [JengaAvatarSize].
 * @param diameter the avatar diameter; defaults to the [size] preset.
 * @param textStyle the initials text style; defaults to the [size] preset.
 * @param colors the color set; defaults to [JengaAvatarDefaults.colors]. Pass
 *   [JengaAvatarDefaults.neutralColors] for a grey avatar.
 */
@Composable
public fun JengaAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
    diameter: Dp = JengaTheme.components.avatar?.size(size)?.diameter ?: JengaAvatarDefaults.diameter(size),
    textStyle: TextStyle = JengaAvatarDefaults.textStyle(size),
    colors: JengaAvatarColors = JengaAvatarDefaults.colors(),
) {
    Box(
        modifier = modifier
            .size(diameter)
            .clip(JengaTheme.shapes.pill)
            .background(colors.container),
        contentAlignment = Alignment.Center,
    ) {
        JengaText(
            text = initialsOf(name),
            style = textStyle,
            color = colors.content,
        )
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
    diameter: Dp = JengaTheme.components.avatar?.size(size)?.diameter ?: JengaAvatarDefaults.diameter(size),
    textStyle: TextStyle = JengaAvatarDefaults.textStyle(size),
) {
    JengaAvatar(
        name = name,
        modifier = modifier,
        size = size,
        diameter = diameter,
        textStyle = textStyle,
        colors = JengaAvatarDefaults.colors(),
    )
}

/**
 * A circular avatar with custom [content] (e.g. an image), clipped to a circle.
 *
 * @param modifier the [Modifier] for this avatar.
 * @param size the avatar size preset; see [JengaAvatarSize].
 * @param diameter the avatar diameter; defaults to the [size] preset.
 * @param colors the color set; defaults to [JengaAvatarDefaults.contentSlotColors].
 *   [JengaAvatarColors.container] fills the circle behind [content], and
 *   [JengaAvatarColors.content] is the content color [content] inherits (e.g. the tint of an icon).
 * @param content the avatar content, clipped to the circle (e.g. an `Image`).
 */
@Composable
public fun JengaAvatar(
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
    diameter: Dp = JengaTheme.components.avatar?.size(size)?.diameter ?: JengaAvatarDefaults.diameter(size),
    colors: JengaAvatarColors = JengaAvatarDefaults.contentSlotColors(),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(diameter)
            .clip(JengaTheme.shapes.pill)
            .background(colors.container),
        contentAlignment = Alignment.Center,
        content = {
            CompositionLocalProvider(
                LocalJengaContentColor provides colors.content,
                content = content,
            )
        },
    )
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaAvatar(
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
    diameter: Dp = JengaTheme.components.avatar?.size(size)?.diameter ?: JengaAvatarDefaults.diameter(size),
    content: @Composable () -> Unit,
) {
    JengaAvatar(
        modifier = modifier,
        size = size,
        diameter = diameter,
        colors = JengaAvatarDefaults.contentSlotColors(),
        content = content,
    )
}

private fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val initials = when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2)
        else -> "${parts.first().first()}${parts.last().first()}"
    }
    return initials.uppercase()
}
