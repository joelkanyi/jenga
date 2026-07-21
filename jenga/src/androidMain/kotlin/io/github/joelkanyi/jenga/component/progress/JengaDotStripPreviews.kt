package io.github.joelkanyi.jenga.component.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaDotStripPreview() {
    JengaTheme { DotStripShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDotStripRtlPreview() {
    JengaTheme { RtlPreview { DotStripShowcase() } }
}

@Composable
private fun DotStripShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaDotStrip(filled = 3, total = 4)
        JengaDotStrip(filled = 1, total = 4)
        JengaDotStrip(
            filled = 3,
            total = 4,
            style = JengaDotStripStyle.Bars,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
