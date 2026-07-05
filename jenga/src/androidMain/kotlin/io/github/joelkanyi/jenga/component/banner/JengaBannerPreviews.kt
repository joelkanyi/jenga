package io.github.joelkanyi.jenga.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBannerPreview() {
    JengaTheme { BannerShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBannerRtlPreview() {
    JengaTheme { RtlPreview { BannerShowcase() } }
}

@Composable
private fun BannerShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaBanner(message = "Working offline — scans will sync later.", tone = JengaBannerTone.Info)
        JengaBanner(message = "All tickets validated.", tone = JengaBannerTone.Success, title = "Done")
        JengaBanner(message = "Low battery on this device.", tone = JengaBannerTone.Warning)
        JengaBanner(message = "Couldn't reach the server.", tone = JengaBannerTone.Error)
    }
}
