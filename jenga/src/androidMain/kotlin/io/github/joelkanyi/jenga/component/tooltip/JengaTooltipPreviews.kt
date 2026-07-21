package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTooltipPreview() {
    JengaTheme { TooltipShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTooltipRtlPreview() {
    JengaTheme { RtlPreview { TooltipShowcase() } }
}

@Composable
private fun TooltipShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTooltip(text = "Re-admit this attendee")
        JengaTooltip(text = "This ticket was already scanned at 14:02 by Gate B.")
    }
}
