package io.github.joelkanyi.jenga.component.stat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.progress.JengaDotStrip
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaStatTilePreview() {
    JengaTheme { StatTileShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatTileRtlPreview() {
    JengaTheme { RtlPreview { StatTileShowcase() } }
}

@Composable
private fun StatTileShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        JengaStatTile(
            label = "Balance",
            value = "3",
            unit = "/4 groups",
            tone = JengaStatTone.Success,
            icon = { JengaIcon(JengaIcons.Check, contentDescription = null, size = JengaTheme.sizing.iconSmall) },
            modifier = Modifier.weight(1f),
            viz = { JengaDotStrip(filled = 3, total = 4, style = io.github.joelkanyi.jenga.component.progress.JengaDotStripStyle.Bars, modifier = Modifier.fillMaxWidth()) },
        )
        JengaStatTile(
            label = "Calories",
            value = "≈640",
            unit = "kcal",
            tone = JengaStatTone.Brand,
            icon = { JengaIcon(JengaIcons.Sparkles, contentDescription = null, size = JengaTheme.sizing.iconSmall) },
            modifier = Modifier.weight(1f),
        )
        JengaStatTile(
            label = "Cost",
            value = "210",
            unit = "KES",
            tone = JengaStatTone.Info,
            icon = { JengaIcon(JengaIcons.Info, contentDescription = null, size = JengaTheme.sizing.iconSmall) },
            modifier = Modifier.weight(1f),
        )
    }
}
