package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * **Pattern (organism)** — a KPI / stat card: an eyebrow label, a large value,
 * and an optional trend badge. Composed from [JengaCard] + [JengaText] + [JengaBadge].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaStatCardSample
 *
 * @param label the metric name (rendered as an uppercase eyebrow).
 * @param value the metric value.
 * @param modifier the [Modifier] for this card.
 * @param trendLabel optional trend pill text (e.g. "+12%").
 * @param trendTone the trend pill tone; see [JengaBadgeTone].
 * @param onClick optional click handler.
 */
@Composable
public fun JengaStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trendLabel: String? = null,
    trendTone: JengaBadgeTone = JengaBadgeTone.Neutral,
    onClick: (() -> Unit)? = null,
) {
    JengaCard(modifier = modifier, onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm)) {
            JengaText(
                text = label.uppercase(),
                style = JengaTheme.typography.label,
                color = JengaTheme.colors.textMuted,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
            ) {
                JengaText(text = value, style = JengaTheme.typography.headingMedium)
                if (trendLabel != null) {
                    JengaBadge(text = trendLabel, tone = trendTone)
                }
            }
        }
    }
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStatCardPreview() {
    JengaTheme { StatCardShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatCardRtlPreview() {
    JengaTheme { RtlPreview { StatCardShowcase() } }
}

@Composable
private fun StatCardShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaStatCard(label = "Checked in", value = "1,284", trendLabel = "+12%", trendTone = JengaBadgeTone.Success)
        JengaStatCard(label = "Denied", value = "37", trendLabel = "+3", trendTone = JengaBadgeTone.Error)
    }
}
