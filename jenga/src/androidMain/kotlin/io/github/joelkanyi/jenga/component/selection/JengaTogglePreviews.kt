package io.github.joelkanyi.jenga.component.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTogglePreview() {
    JengaTheme { ToggleShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaToggleRtlPreview() {
    JengaTheme { RtlPreview { ToggleShowcase() } }
}

@Composable
private fun ToggleShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaToggle(checked = true, onCheckedChange = {})
        JengaToggle(checked = false, onCheckedChange = {})
        JengaToggle(checked = true, onCheckedChange = {}, enabled = false)
        JengaToggle(checked = false, onCheckedChange = {}, enabled = false)
    }
}
