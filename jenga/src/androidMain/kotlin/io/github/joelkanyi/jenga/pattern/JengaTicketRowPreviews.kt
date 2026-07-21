package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTicketRowPreview() {
    JengaTheme { TicketRowShowcase() }
}

// Genuine RTL coverage: force the layout direction (the @Preview `locale` knob
// does not flip layout direction under the Roborazzi/scanner harness).
@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTicketRowRtlPreview() {
    JengaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TicketRowShowcase()
        }
    }
}

@Composable
private fun TicketRowShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTicketRow(
            attendeeName = "Joel Kanyi",
            detail = "VIP · TKT-2026-001",
            statusLabel = "Valid",
            statusTone = JengaBadgeTone.Success,
        )
        JengaTicketRow(
            attendeeName = "Ada Lovelace",
            detail = "Regular · TKT-2026-114",
            statusLabel = "Used",
            statusTone = JengaBadgeTone.Neutral,
        )
        JengaTicketRow(
            attendeeName = "Grace Hopper",
            detail = "Regular · TKT-2026-256",
            statusLabel = "Denied",
            statusTone = JengaBadgeTone.Error,
        )
    }
}
