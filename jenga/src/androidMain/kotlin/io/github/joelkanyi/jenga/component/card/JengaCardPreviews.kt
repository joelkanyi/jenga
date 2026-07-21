package io.github.joelkanyi.jenga.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaCardPreview() {
    JengaTheme { CardShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaCardRtlPreview() {
    JengaTheme { RtlPreview { CardShowcase() } }
}

@Composable
private fun CardShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        listOf(
            JengaCardVariant.Elevated,
            JengaCardVariant.Outlined,
            JengaCardVariant.Filled,
        ).forEach { v ->
            JengaCard(variant = v) {
                JengaText(text = v.name, style = JengaTheme.typography.titleLarge)
                JengaText(
                    text = "Supporting text inside the card.",
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                )
            }
        }
    }
}
