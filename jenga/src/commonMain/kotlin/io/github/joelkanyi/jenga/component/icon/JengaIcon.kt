package io.github.joelkanyi.jenga.component.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.resources.Res
import io.github.joelkanyi.jenga.resources.jenga_ic_add
import io.github.joelkanyi.jenga.resources.jenga_ic_arrow_back
import io.github.joelkanyi.jenga.resources.jenga_ic_arrow_right
import io.github.joelkanyi.jenga.resources.jenga_ic_ban
import io.github.joelkanyi.jenga.resources.jenga_ic_bell
import io.github.joelkanyi.jenga.resources.jenga_ic_bulb
import io.github.joelkanyi.jenga.resources.jenga_ic_calendar
import io.github.joelkanyi.jenga.resources.jenga_ic_chart
import io.github.joelkanyi.jenga.resources.jenga_ic_check
import io.github.joelkanyi.jenga.resources.jenga_ic_check_circle
import io.github.joelkanyi.jenga.resources.jenga_ic_chevron_down
import io.github.joelkanyi.jenga.resources.jenga_ic_chevron_left
import io.github.joelkanyi.jenga.resources.jenga_ic_chevron_right
import io.github.joelkanyi.jenga.resources.jenga_ic_chevron_up
import io.github.joelkanyi.jenga.resources.jenga_ic_clock
import io.github.joelkanyi.jenga.resources.jenga_ic_close
import io.github.joelkanyi.jenga.resources.jenga_ic_cloud
import io.github.joelkanyi.jenga.resources.jenga_ic_cloud_off
import io.github.joelkanyi.jenga.resources.jenga_ic_database
import io.github.joelkanyi.jenga.resources.jenga_ic_eye
import io.github.joelkanyi.jenga.resources.jenga_ic_eye_off
import io.github.joelkanyi.jenga.resources.jenga_ic_flash
import io.github.joelkanyi.jenga.resources.jenga_ic_flash_off
import io.github.joelkanyi.jenga.resources.jenga_ic_heart
import io.github.joelkanyi.jenga.resources.jenga_ic_history
import io.github.joelkanyi.jenga.resources.jenga_ic_image
import io.github.joelkanyi.jenga.resources.jenga_ic_info
import io.github.joelkanyi.jenga.resources.jenga_ic_keyboard
import io.github.joelkanyi.jenga.resources.jenga_ic_lock
import io.github.joelkanyi.jenga.resources.jenga_ic_logout
import io.github.joelkanyi.jenga.resources.jenga_ic_mail
import io.github.joelkanyi.jenga.resources.jenga_ic_message_circle
import io.github.joelkanyi.jenga.resources.jenga_ic_qr_code
import io.github.joelkanyi.jenga.resources.jenga_ic_refresh
import io.github.joelkanyi.jenga.resources.jenga_ic_remove
import io.github.joelkanyi.jenga.resources.jenga_ic_search
import io.github.joelkanyi.jenga.resources.jenga_ic_settings
import io.github.joelkanyi.jenga.resources.jenga_ic_share
import io.github.joelkanyi.jenga.resources.jenga_ic_shield
import io.github.joelkanyi.jenga.resources.jenga_ic_shield_check
import io.github.joelkanyi.jenga.resources.jenga_ic_sliders
import io.github.joelkanyi.jenga.resources.jenga_ic_smartphone
import io.github.joelkanyi.jenga.resources.jenga_ic_sparkles
import io.github.joelkanyi.jenga.resources.jenga_ic_sun
import io.github.joelkanyi.jenga.resources.jenga_ic_swap
import io.github.joelkanyi.jenga.resources.jenga_ic_thumbs_down
import io.github.joelkanyi.jenga.resources.jenga_ic_thumbs_up
import io.github.joelkanyi.jenga.resources.jenga_ic_trash
import io.github.joelkanyi.jenga.resources.jenga_ic_user
import io.github.joelkanyi.jenga.resources.jenga_ic_vibrate
import io.github.joelkanyi.jenga.resources.jenga_ic_volume
import io.github.joelkanyi.jenga.resources.jenga_ic_warning
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor
import org.jetbrains.compose.resources.vectorResource

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
        @Composable get() = vectorResource(Res.drawable.jenga_ic_check)
    public val Close: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_close)
    public val Add: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_add)
    public val Remove: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_remove)
    public val ChevronRight: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_chevron_right)
    public val ChevronLeft: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_chevron_left)
    public val ChevronDown: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_chevron_down)
    public val ChevronUp: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_chevron_up)
    public val ArrowBack: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_arrow_back)
    public val Search: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_search)
    public val Info: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_info)
    public val Mail: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_mail)
    public val Lock: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_lock)
    public val Eye: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_eye)
    public val EyeOff: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_eye_off)
    public val CheckCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_check_circle)
    public val Flash: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_flash)
    public val FlashOff: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_flash_off)
    public val Keyboard: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_keyboard)
    public val History: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_history)
    public val Warning: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_warning)
    public val Swap: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_swap)
    public val Ban: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_ban)
    public val QrCode: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_qr_code)
    public val Calendar: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_calendar)
    public val Chart: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_chart)
    public val User: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_user)
    public val Volume: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_volume)
    public val Vibrate: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_vibrate)
    public val Sun: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_sun)
    public val Cloud: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_cloud)
    public val CloudOff: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_cloud_off)
    public val Refresh: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_refresh)
    public val Bell: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_bell)
    public val Trash: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_trash)
    public val Smartphone: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_smartphone)
    public val Database: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_database)
    public val Logout: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_logout)
    public val Shield: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_shield)
    public val Settings: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_settings)

    // --- Feedback, media & directional line icons (broadly reusable) ---
    public val ThumbsUp: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_thumbs_up)
    public val ThumbsDown: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_thumbs_down)
    public val Heart: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_heart)
    public val Share: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_share)
    public val Image: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_image)
    public val Clock: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_clock)
    public val ArrowRight: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_arrow_right)
    public val Sliders: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_sliders)
    public val MessageCircle: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_message_circle)
    public val ShieldCheck: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_shield_check)
    public val Sparkles: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_sparkles)
    public val Bulb: ImageVector
        @Composable get() = vectorResource(Res.drawable.jenga_ic_bulb)
}
