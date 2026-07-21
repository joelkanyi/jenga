package io.github.joelkanyi.jenga.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Visual style of a [JengaButton]. */
public enum class JengaButtonVariant {
    /** Primary call to action — filled brand orange. */
    Primary,

    /** High-emphasis alternative — filled ink (navy). */
    Ink,

    /** Low-emphasis, transparent — for tertiary actions. */
    Ghost,

    /** Secondary — filled neutral (surface-variant), no border, for the calm
     *  companion to a Primary action (e.g. an "Another"/"Skip" beside "Confirm"). */
    Neutral,

    /** Medium-emphasis — bordered on a surface fill. */
    Outline,

    /** Destructive action — bordered, error-colored. */
    Danger,
}

/** Size of a [JengaButton]; controls height, padding and label style. */
public enum class JengaButtonSize { Small, Medium, Large }

/**
 * Resolved colors for a [JengaButton] in a single state set. Obtain a themed
 * instance from [JengaButtonDefaults.colors] and `copy(...)` to override.
 */
@Poko
@androidx.compose.runtime.Immutable
public class JengaButtonColors(
    public val container: Color,
    public val content: Color,
    public val border: Color,
    public val disabledContainer: Color,
    public val disabledContent: Color,
    public val disabledBorder: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        border: Color = this.border,
        disabledContainer: Color = this.disabledContainer,
        disabledContent: Color = this.disabledContent,
        disabledBorder: Color = this.disabledBorder,
    ): JengaButtonColors = JengaButtonColors(container, content, border, disabledContainer, disabledContent, disabledBorder)
}

/** Default values and token mappings for [JengaButton]. Override any of these per call. */
public object JengaButtonDefaults {

    /** Border width used by bordered variants. */
    public val BorderWidth: Dp = 1.dp

    /** Diameter of the inline loading spinner. */
    public val SpinnerSize: Dp = 18.dp

    /** The default button shape (control radius). */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.control

    /** Minimum touch height per [size] (Medium meets the 48dp accessibility target). */
    public fun minHeight(size: JengaButtonSize): Dp = when (size) {
        JengaButtonSize.Small -> 36.dp
        JengaButtonSize.Medium -> 48.dp
        JengaButtonSize.Large -> 56.dp
    }

    /** Content padding per [size]. */
    public fun contentPadding(size: JengaButtonSize): PaddingValues = when (size) {
        JengaButtonSize.Small -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        JengaButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        JengaButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    }

    /** Label text style per [size]. */
    @Composable
    public fun textStyle(size: JengaButtonSize): TextStyle {
        val base = JengaTheme.typography.button
        return when (size) {
            JengaButtonSize.Small -> base.copy(fontSize = 14.sp, lineHeight = 18.sp)
            JengaButtonSize.Medium -> base
            JengaButtonSize.Large -> base.copy(fontSize = 17.sp, lineHeight = 22.sp)
        }
    }

    /** Themed colors for the given [variant]. */
    @Composable
    public fun colors(variant: JengaButtonVariant): JengaButtonColors {
        val c = JengaTheme.colors
        return when (variant) {
            JengaButtonVariant.Primary -> JengaButtonColors(
                container = c.brand,
                content = c.onBrand,
                border = Color.Transparent,
                disabledContainer = c.surfaceDisabled,
                disabledContent = c.contentDisabled,
                disabledBorder = Color.Transparent,
            )
            JengaButtonVariant.Ink -> JengaButtonColors(
                container = c.ink,
                content = c.onInk,
                border = Color.Transparent,
                disabledContainer = c.surfaceDisabled,
                disabledContent = c.contentDisabled,
                disabledBorder = Color.Transparent,
            )
            JengaButtonVariant.Ghost -> JengaButtonColors(
                container = Color.Transparent,
                content = c.textSecondary,
                border = Color.Transparent,
                disabledContainer = Color.Transparent,
                disabledContent = c.contentDisabled,
                disabledBorder = Color.Transparent,
            )
            JengaButtonVariant.Neutral -> JengaButtonColors(
                container = c.surfaceVariant,
                content = c.textPrimary,
                border = Color.Transparent,
                disabledContainer = c.surfaceDisabled,
                disabledContent = c.contentDisabled,
                disabledBorder = Color.Transparent,
            )
            JengaButtonVariant.Outline -> JengaButtonColors(
                container = c.surface,
                content = c.textPrimary,
                border = c.borderStrong,
                disabledContainer = c.surfaceDisabled,
                disabledContent = c.contentDisabled,
                disabledBorder = c.borderDisabled,
            )
            JengaButtonVariant.Danger -> JengaButtonColors(
                container = Color.Transparent,
                content = c.error,
                border = c.borderStrong,
                disabledContainer = Color.Transparent,
                disabledContent = c.contentDisabled,
                disabledBorder = c.borderDisabled,
            )
        }
    }
}

/**
 * A Jenga button — the primary way to trigger an action.
 *
 * This is the convenience overload that renders a text label with the
 * size-appropriate style. For custom content (e.g. icon + multi-style text),
 * use the slot overload below.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaButtonSample
 *
 * @param text the button label.
 * @param onClick called when the button is tapped (ignored while disabled/loading).
 * @param modifier the [Modifier] for this button.
 * @param variant the visual style; see [JengaButtonVariant].
 * @param size the button size; see [JengaButtonSize].
 * @param enabled whether the button is interactive.
 * @param loading when true, shows a spinner and blocks clicks.
 * @param leadingIcon optional icon before the label.
 * @param trailingIcon optional icon after the label.
 */
@Composable
public fun JengaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: JengaButtonVariant = JengaButtonVariant.Primary,
    size: JengaButtonSize = JengaButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    JengaButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    ) {
        JengaText(
            text = text,
            style = JengaButtonDefaults.textStyle(size),
            maxLines = 1,
        )
    }
}

/**
 * A Jenga button with a custom content slot.
 *
 * @param onClick called when the button is tapped (ignored while disabled/loading).
 * @param modifier the [Modifier] for this button.
 * @param variant the visual style; see [JengaButtonVariant].
 * @param size the button size; see [JengaButtonSize].
 * @param enabled whether the button is interactive.
 * @param loading when true, shows a spinner and blocks clicks.
 * @param leadingIcon optional icon before the content.
 * @param trailingIcon optional icon after the content.
 * @param shape the button shape; defaults to [JengaButtonDefaults.shape].
 * @param colors the color set; defaults to [JengaButtonDefaults.colors] for [variant].
 * @param contentPadding inner padding; defaults to [JengaButtonDefaults.contentPadding].
 * @param content the button content; rendered with the resolved content color.
 */
@Composable
public fun JengaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: JengaButtonVariant = JengaButtonVariant.Primary,
    size: JengaButtonSize = JengaButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    shape: Shape = JengaButtonDefaults.shape,
    colors: JengaButtonColors = JengaButtonDefaults.colors(variant),
    contentPadding: PaddingValues = JengaButtonDefaults.contentPadding(size),
    content: @Composable RowScope.() -> Unit,
) {
    val isEnabled = enabled && !loading
    val container = if (isEnabled) colors.container else colors.disabledContainer
    val contentColor = if (isEnabled) colors.content else colors.disabledContent
    val borderColor = if (isEnabled) colors.border else colors.disabledBorder

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(shape)
            .background(container)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(JengaButtonDefaults.BorderWidth, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(
                enabled = isEnabled,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = JengaButtonDefaults.minHeight(size))
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val rowScope = this
        CompositionLocalProvider(LocalJengaContentColor provides contentColor) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(JengaButtonDefaults.SpinnerSize),
                    color = contentColor,
                    strokeWidth = 2.dp,
                )
            } else {
                leadingIcon?.invoke()
                rowScope.content()
                trailingIcon?.invoke()
            }
        }
    }
}
