package io.github.joelkanyi.jenga.component.expandable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.progress.JengaDotStrip
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaExpandableRowPreview() {
    JengaTheme { ExpandableShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaExpandableRowRtlPreview() {
    JengaTheme { RtlPreview { ExpandableShowcase() } }
}

@Composable
private fun ExpandableShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaExpandableRow(
            expanded = true,
            onExpandedChange = {},
            showChevron = false,
            header = {
                JengaText(text = "Friday", style = JengaTheme.typography.titleMedium, color = JengaTheme.colors.textPrimary)
                Row(Modifier.weight(1f)) {}
                JengaText(text = "Balanced", style = JengaTheme.typography.bodySmall, color = JengaTheme.colors.success)
            },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                JengaText(text = "Supper · Ugali, sukuma & beef", style = JengaTheme.typography.bodyMedium, color = JengaTheme.colors.textSecondary)
            }
        }
        JengaExpandableRow(
            expanded = false,
            onExpandedChange = {},
            header = {
                Column(Modifier.weight(1f)) {
                    JengaText(text = "Sat 13", style = JengaTheme.typography.titleMedium, color = JengaTheme.colors.textPrimary)
                    JengaDotStrip(filled = 3, total = 4, modifier = Modifier.padding(top = JengaTheme.spacing.sm))
                }
            },
        ) {}
    }
}
