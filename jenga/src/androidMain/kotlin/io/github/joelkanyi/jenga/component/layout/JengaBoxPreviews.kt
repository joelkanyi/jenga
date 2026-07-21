package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBoxPreview() {
    JengaTheme {
        JengaBox(
            modifier = Modifier.padding(JengaTheme.spacing.xl),
            padding = PaddingValues(JengaTheme.spacing.lg),
            background = JengaTheme.colors.brandSubtle,
            shape = JengaTheme.shapes.card,
        ) {
            JengaText("JengaBox — padding + background + shape, no Modifier chain", color = JengaTheme.colors.onBrandSubtle)
        }
    }
}
