package io.github.joelkanyi.jenga.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Semantic tone of a [JengaBanner]. */
public enum class JengaBannerTone { Info, Success, Warning, Error }

/** Resolved colors for a [JengaBanner]. Override via [JengaBannerDefaults.colors]. */
@Immutable
public data class JengaBannerColors(
    public val container: Color,
    public val content: Color,
    public val icon: Color,
)

/** Defaults and token mappings for [JengaBanner]. */
public object JengaBannerDefaults {
    /** Themed colors per [tone]. */
    @Composable
    public fun colors(tone: JengaBannerTone): JengaBannerColors {
        val c = JengaTheme.colors
        return when (tone) {
            JengaBannerTone.Info -> JengaBannerColors(c.infoContainer, c.onInfoContainer, c.info)
            JengaBannerTone.Success -> JengaBannerColors(c.successContainer, c.onSuccessContainer, c.success)
            JengaBannerTone.Warning -> JengaBannerColors(c.warningContainer, c.onWarningContainer, c.warning)
            JengaBannerTone.Error -> JengaBannerColors(c.errorContainer, c.onErrorContainer, c.error)
        }
    }

    /** Default leading icon per [tone]. */
    @Composable
    public fun icon(tone: JengaBannerTone): ImageVector = when (tone) {
        JengaBannerTone.Info -> JengaIcons.Info
        JengaBannerTone.Success -> JengaIcons.Check
        JengaBannerTone.Warning -> JengaIcons.Info
        JengaBannerTone.Error -> JengaIcons.Close
    }
}

/**
 * An inline banner / alert: a tinted, full-width message with a leading status
 * icon, optional title, and optional trailing action.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerSample
 *
 * @param message the banner body text.
 * @param modifier the [Modifier] for this banner.
 * @param tone the semantic tone; see [JengaBannerTone].
 * @param title optional bold title above the message.
 * @param action optional trailing action slot (e.g. a ghost [io.github.joelkanyi.jenga.component.button.JengaButton]).
 * @param colors the color set; defaults to [JengaBannerDefaults.colors] for [tone].
 */
@Composable
public fun JengaBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: JengaBannerTone = JengaBannerTone.Info,
    title: String? = null,
    action: (@Composable () -> Unit)? = null,
    colors: JengaBannerColors = JengaBannerDefaults.colors(tone),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(JengaTheme.shapes.card)
            .background(colors.container)
            .padding(JengaTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        JengaIcon(
            imageVector = JengaBannerDefaults.icon(tone),
            contentDescription = null,
            tint = colors.icon,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
        ) {
            if (title != null) {
                JengaText(text = title, style = JengaTheme.typography.titleSmall, color = colors.content)
            }
            JengaText(text = message, style = JengaTheme.typography.bodySmall, color = colors.content)
        }
        if (action != null) {
            CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
                action()
            }
        }
    }
}
