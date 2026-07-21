package io.github.joelkanyi.jenga.component.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaSwipeToDismissPreview() {
    JengaTheme { SwipeShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSwipeToDismissRtlPreview() {
    JengaTheme { RtlPreview { SwipeShowcase() } }
}

@Composable
private fun SwipeShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        // At rest the row shows its content; the error reveal panel appears on swipe.
        JengaSwipeToDismiss(onDismiss = {}, actionContentDescription = "Remove") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JengaTheme.colors.surface)
                    .padding(JengaTheme.spacing.md),
            ) {
                JengaText(text = "Sukuma wiki · 2 bunches", style = JengaTheme.typography.bodyMedium, color = JengaTheme.colors.textPrimary)
            }
        }
    }
}
