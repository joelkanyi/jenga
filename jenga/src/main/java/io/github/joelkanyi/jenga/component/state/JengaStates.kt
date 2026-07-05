package io.github.joelkanyi.jenga.component.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for the empty/error state blocks. Override via [JengaStateDefaults.colors]. */
@Immutable
public data class JengaStateColors(
    public val title: Color,
    public val description: Color,
)

/** Defaults and token mappings for [JengaEmptyState] / [JengaErrorState]. */
public object JengaStateDefaults {
    /** Themed colors. */
    @Composable
    public fun colors(): JengaStateColors = JengaStateColors(
        title = JengaTheme.colors.textPrimary,
        description = JengaTheme.colors.textMuted,
    )
}

/**
 * A centered empty-state placeholder: optional icon, title, message, and action.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaEmptyStateSample
 *
 * @param title the headline (e.g. "No scans yet").
 * @param modifier the [Modifier] for this state.
 * @param description optional supporting message.
 * @param icon optional icon/illustration slot above the title.
 * @param actionLabel optional action button label; shown with [onAction].
 * @param onAction called when the action is tapped.
 * @param actionVariant the action button's variant; defaults to Outline.
 * @param colors the color set; defaults to [JengaStateDefaults.colors].
 */
@Composable
public fun JengaEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Outline,
    colors: JengaStateColors = JengaStateDefaults.colors(),
) {
    StateLayout(
        title = title,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        actionVariant = actionVariant,
        onAction = onAction,
        colors = colors,
        modifier = modifier,
    )
}

/**
 * A centered error-state placeholder with a retry-style action.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaErrorStateSample
 *
 * @param title the headline (e.g. "Something went wrong").
 * @param modifier the [Modifier] for this state.
 * @param description optional supporting message.
 * @param icon optional icon/illustration slot above the title.
 * @param actionLabel optional action button label (e.g. "Retry"); shown with [onAction].
 * @param onAction called when the action is tapped.
 * @param actionVariant the action button's variant; defaults to Primary.
 * @param colors the color set; defaults to [JengaStateDefaults.colors].
 */
@Composable
public fun JengaErrorState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Primary,
    colors: JengaStateColors = JengaStateDefaults.colors(),
) {
    StateLayout(
        title = title,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        actionVariant = actionVariant,
        onAction = onAction,
        colors = colors,
        modifier = modifier,
    )
}

@Composable
private fun StateLayout(
    title: String,
    description: String?,
    icon: (@Composable () -> Unit)?,
    actionLabel: String?,
    actionVariant: JengaButtonVariant,
    onAction: (() -> Unit)?,
    colors: JengaStateColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(JengaTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        if (icon != null) icon()
        JengaText(
            text = title,
            style = JengaTheme.typography.titleLarge,
            color = colors.title,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            JengaText(
                text = description,
                style = JengaTheme.typography.bodySmall,
                color = colors.description,
                textAlign = TextAlign.Center,
            )
        }
        if (actionLabel != null && onAction != null) {
            JengaButton(text = actionLabel, onClick = onAction, variant = actionVariant)
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStatesPreview() {
    JengaTheme { StatesShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatesRtlPreview() {
    JengaTheme { RtlPreview { StatesShowcase() } }
}

@Composable
private fun StatesShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxl),
    ) {
        JengaEmptyState(
            title = "No scans yet",
            description = "Validated tickets will appear here as attendees check in.",
            actionLabel = "Scan a ticket",
            onAction = {},
        )
        JengaErrorState(
            title = "Couldn't load gates",
            description = "Check your connection and try again.",
            actionLabel = "Retry",
            onAction = {},
        )
    }
}
