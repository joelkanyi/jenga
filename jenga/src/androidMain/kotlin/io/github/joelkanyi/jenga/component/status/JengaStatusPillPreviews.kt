package io.github.joelkanyi.jenga.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.progress.JengaCircularProgressIndeterminate
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStatusPillPreview() {
    JengaTheme { StatusPillShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStatusPillRtlPreview() {
    JengaTheme { RtlPreview { StatusPillShowcase() } }
}

@Composable
private fun StatusPillShowcase() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        JengaStatusPill(label = "Synced", tone = JengaBadgeTone.Success)
        JengaStatusPill(label = "3 pending", tone = JengaBadgeTone.Warning, loading = true)
        JengaStatusPill(label = "Offline", tone = JengaBadgeTone.Error)
        JengaStatusPill(label = "Offline mode", tone = JengaBadgeTone.Info)
    }
}
