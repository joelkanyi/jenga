package io.github.joelkanyi.jenga.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaMediaHeroPreview() {
    JengaTheme { MediaHeroShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaMediaHeroRtlPreview() {
    JengaTheme { RtlPreview { MediaHeroShowcase() } }
}

@Composable
private fun MediaHeroShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaMediaHero(
            title = "Featured dish",
            support = "A complete plate · ready in 35 min",
            badge = { Eyebrow("TONIGHT'S PICK") },
        )
    }
}

@Composable
private fun Eyebrow(text: String) {
    JengaText(
        text = text,
        style = JengaTheme.typography.label,
        color = JengaTheme.colors.onOverlay,
        modifier = Modifier
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.overlaySurface)
            .padding(horizontal = JengaTheme.spacing.md, vertical = JengaTheme.spacing.xs),
    )
}
