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
