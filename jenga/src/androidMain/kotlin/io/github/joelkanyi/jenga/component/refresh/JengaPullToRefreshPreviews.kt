package io.github.joelkanyi.jenga.component.refresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------
// The pull gesture and its indicator are interactive, so the static preview just
// renders the content surface (the indicator is hidden at rest).

@JengaBlockPreviews
@Composable
internal fun JengaPullToRefreshPreview() {
    JengaTheme { PullToRefreshShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaPullToRefreshRtlPreview() {
    JengaTheme { RtlPreview { PullToRefreshShowcase() } }
}

@Composable
private fun PullToRefreshShowcase() {
    JengaPullToRefresh(isRefreshing = false, onRefresh = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(JengaTheme.colors.background)
                .padding(JengaTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
        ) {
            JengaText("Pull down to refresh", style = JengaTheme.typography.titleMedium)
            JengaText(
                "Scans sync when you refresh.",
                style = JengaTheme.typography.bodySmall,
                color = JengaTheme.colors.textMuted,
            )
        }
    }
}
