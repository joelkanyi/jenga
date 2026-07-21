package io.github.joelkanyi.jenga.component.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Size of a [JengaAvatar]. */
public enum class JengaAvatarSize { Small, Medium, Large }

/** Defaults for [JengaAvatar]. */
public object JengaAvatarDefaults {
    /** Diameter per [size]. */
    public fun diameter(size: JengaAvatarSize): Dp = when (size) {
        JengaAvatarSize.Small -> 28.dp
        JengaAvatarSize.Medium -> 40.dp
        JengaAvatarSize.Large -> 56.dp
    }
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
 * @param size the avatar size; see [JengaAvatarSize].
 */
@Composable
public fun JengaAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
) {
    val diameter = JengaAvatarDefaults.diameter(size)
    val fontSize = when (size) {
        JengaAvatarSize.Small -> 11.sp
        JengaAvatarSize.Medium -> 15.sp
        JengaAvatarSize.Large -> 20.sp
    }
    Box(
        modifier = modifier
            .size(diameter)
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.brandSubtle),
        contentAlignment = Alignment.Center,
    ) {
        JengaText(
            text = initialsOf(name),
            style = JengaTheme.typography.titleSmall.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            ),
            color = JengaTheme.colors.onBrandSubtle,
        )
    }
}

/**
 * A circular avatar with custom [content] (e.g. an image), clipped to a circle.
 *
 * @param modifier the [Modifier] for this avatar.
 * @param size the avatar size; see [JengaAvatarSize].
 * @param content the avatar content, clipped to the circle (e.g. an `Image`).
 */
@Composable
public fun JengaAvatar(
    modifier: Modifier = Modifier,
    size: JengaAvatarSize = JengaAvatarSize.Medium,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(JengaAvatarDefaults.diameter(size))
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.surfaceSunk),
        contentAlignment = Alignment.Center,
        content = { content() },
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
