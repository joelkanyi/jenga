package io.github.joelkanyi.jenga.component.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.R
import io.github.joelkanyi.jenga.component.layout.JengaWrap
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/**
 * Renders an [ImageVector] icon, sized and tinted from Jenga tokens.
 *
 * Tint resolution: explicit [tint] → inherited [LocalJengaContentColor] → theme
 * [JengaTheme.colors]`.textPrimary`. Size defaults to
 * [JengaTheme.sizing]`.iconMedium`.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaIconSample
 *
 * @param imageVector the icon to draw (e.g. from [JengaIcons]).
 * @param contentDescription accessibility label; `null` marks the icon decorative.
 * @param modifier the [Modifier] for this icon.
 * @param tint the icon color; [Color.Unspecified] falls back to content color then theme.
 * @param size the icon's square size; defaults to the medium icon size token.
 */
@Composable
public fun JengaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = JengaTheme.sizing.iconMedium,
) {
    val resolvedTint = tint
        .takeOrElse { LocalJengaContentColor.current }
        .takeOrElse { JengaTheme.colors.textPrimary }
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(resolvedTint),
    )
}

/**
 * Jenga's curated icon set — a small, self-contained collection of line icons
 * shipped as vector drawables (no external icon dependency). Directional icons
 * are `autoMirrored`, so they flip in right-to-left layouts.
 *
 * Render with [JengaIcon]: `JengaIcon(JengaIcons.Check, contentDescription = null)`.
 */
public object JengaIcons {
    public val Check: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_check)
    public val Close: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_close)
    public val Add: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_add)
    public val Remove: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_remove)
    public val ChevronRight: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_chevron_right)
    public val ChevronLeft: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_chevron_left)
    public val ChevronDown: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_chevron_down)
    public val ChevronUp: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_chevron_up)
    public val ArrowBack: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_arrow_back)
    public val Search: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_search)
    public val Info: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_info)
    public val Mail: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_mail)
    public val Lock: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_lock)
    public val Eye: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_eye)
    public val EyeOff: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_eye_off)
    public val CheckCircle: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_check_circle)
    public val Flash: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_flash)
    public val FlashOff: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_flash_off)
    public val Keyboard: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_keyboard)
    public val History: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_history)
    public val Warning: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_warning)
    public val Swap: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_swap)
    public val Ban: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_ban)
    public val QrCode: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_qr_code)
    public val Calendar: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_calendar)
    public val Chart: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_chart)
    public val User: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.jenga_ic_user)
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaIconPreview() {
    JengaTheme { IconShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaIconRtlPreview() {
    JengaTheme { RtlPreview { IconShowcase() } }
}

@Composable
private fun IconShowcase() {
    JengaWrap(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        space = JengaTheme.spacing.md,
    ) {
        JengaIcon(JengaIcons.Check, contentDescription = null, tint = JengaTheme.colors.success)
        JengaIcon(JengaIcons.Close, contentDescription = null, tint = JengaTheme.colors.error)
        JengaIcon(JengaIcons.Add, contentDescription = null)
        JengaIcon(JengaIcons.Search, contentDescription = null)
        JengaIcon(JengaIcons.ChevronRight, contentDescription = null)
        JengaIcon(JengaIcons.ArrowBack, contentDescription = null, tint = JengaTheme.colors.brand)
        JengaIcon(JengaIcons.Mail, contentDescription = null)
        JengaIcon(JengaIcons.Lock, contentDescription = null)
        JengaIcon(JengaIcons.Eye, contentDescription = null)
        JengaIcon(JengaIcons.EyeOff, contentDescription = null)
        JengaIcon(JengaIcons.CheckCircle, contentDescription = null, tint = JengaTheme.colors.success)
        JengaIcon(JengaIcons.Flash, contentDescription = null)
        JengaIcon(JengaIcons.FlashOff, contentDescription = null)
        JengaIcon(JengaIcons.Keyboard, contentDescription = null)
        JengaIcon(JengaIcons.History, contentDescription = null)
        JengaIcon(JengaIcons.Warning, contentDescription = null, tint = JengaTheme.colors.warning)
        JengaIcon(JengaIcons.Swap, contentDescription = null)
        JengaIcon(JengaIcons.Ban, contentDescription = null, tint = JengaTheme.colors.error)
        JengaIcon(JengaIcons.QrCode, contentDescription = null)
        JengaIcon(JengaIcons.Calendar, contentDescription = null)
        JengaIcon(JengaIcons.Chart, contentDescription = null)
        JengaIcon(JengaIcons.User, contentDescription = null)
    }
}
