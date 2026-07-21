package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * **Pattern (organism)**, a KPI / stat card: an eyebrow label, a large value,
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
