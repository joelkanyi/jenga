package io.github.joelkanyi.jenga.component.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaListItem]. Override via [JengaListItemDefaults.colors]. */
@Immutable
public data class JengaListItemColors(
    public val container: Color,
    public val headline: Color,
    public val supporting: Color,
    public val leadingTrailing: Color,
)

/** Defaults and token mappings for [JengaListItem]. */
public object JengaListItemDefaults {
    /** Minimum row height. */
    public val MinHeight: Dp = 56.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaListItemColors {
        val c = JengaTheme.colors
        return JengaListItemColors(
            container = Color.Transparent,
            headline = c.textPrimary,
            supporting = c.textMuted,
            leadingTrailing = c.textMuted,
        )
    }
}

/**
 * A single row in a list: a headline, optional supporting line, and optional
 * leading/trailing slots (icons, avatars, controls).
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaListItemSample
 *
 * @param headline the primary text.
 * @param modifier the [Modifier] for this row.
 * @param supporting optional secondary text below the headline.
 * @param leadingContent optional start slot (inherits the leading/trailing color).
 * @param trailingContent optional end slot (inherits the leading/trailing color).
 * @param onClick optional click handler; makes the row focusable with a ripple.
 * @param colors the color set; defaults to [JengaListItemDefaults.colors].
 */
@Composable
public fun JengaListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    colors: JengaListItemColors = JengaListItemDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.container)
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .defaultMinSize(minHeight = JengaListItemDefaults.MinHeight)
            .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        if (leadingContent != null) {
            CompositionLocalProvider(LocalJengaContentColor provides colors.leadingTrailing) {
                leadingContent()
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            JengaText(text = headline, style = JengaTheme.typography.titleSmall, color = colors.headline, maxLines = 1)
            if (supporting != null) {
                JengaText(
                    text = supporting,
                    style = JengaTheme.typography.bodySmall,
                    color = colors.supporting,
                    maxLines = 1,
                )
            }
        }
        if (trailingContent != null) {
            CompositionLocalProvider(LocalJengaContentColor provides colors.leadingTrailing) {
                trailingContent()
            }
        }
    }
}
