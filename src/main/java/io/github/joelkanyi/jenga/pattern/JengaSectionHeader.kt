package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * **Pattern (organism)** — a section header: a title with an optional trailing
 * text action. Composed from [JengaText] + [JengaButton].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSectionHeaderSample
 *
 * @param title the section title.
 * @param modifier the [Modifier] for this header.
 * @param subtitle optional supporting line under the title.
 * @param actionLabel optional trailing action label.
 * @param onActionClick called when the action is tapped (shown with [actionLabel]).
 */
@Composable
public fun JengaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
        ) {
            JengaText(text = title, style = JengaTheme.typography.titleLarge, maxLines = 1)
            if (subtitle != null) {
                JengaText(
                    text = subtitle,
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                    maxLines = 1,
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            JengaButton(
                text = actionLabel,
                onClick = onActionClick,
                variant = JengaButtonVariant.Ghost,
                size = JengaButtonSize.Small,
            )
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaSectionHeaderPreview() {
    JengaTheme { SectionHeaderShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSectionHeaderRtlPreview() {
    JengaTheme { RtlPreview { SectionHeaderShowcase() } }
}

@Composable
private fun SectionHeaderShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
    ) {
        JengaSectionHeader(
            title = "Recent scans",
            subtitle = "Last 24 hours",
            actionLabel = "See all",
            onActionClick = {},
        )
        JengaSectionHeader(title = "Gates")
    }
}
