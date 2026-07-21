package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.background
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
internal fun JengaGridPreview() {
    JengaTheme { GridShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaGridRtlPreview() {
    JengaTheme { RtlPreview { GridShowcase() } }
}

@Composable
private fun GridShowcase() {
    JengaGrid(
        columns = 3,
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
    ) {
        repeat(7) { i ->
            JengaBox(
                padding = androidx.compose.foundation.layout.PaddingValues(JengaTheme.spacing.lg),
                background = JengaTheme.colors.brandSubtle,
                shape = JengaTheme.shapes.md,
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = Modifier,
            ) {
                JengaText("${i + 1}", color = JengaTheme.colors.onBrandSubtle)
            }
        }
    }
}
