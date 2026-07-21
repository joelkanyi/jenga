package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaStacksPreview() {
    JengaTheme {
        JengaStack(
            modifier = Modifier
                .background(JengaTheme.colors.background)
                .padding(JengaTheme.spacing.xl),
            space = JengaTheme.spacing.md,
        ) {
            JengaText("JengaStack — vertical, token spacing", style = JengaTheme.typography.titleSmall)
            JengaInline {
                JengaText("Inline")
                JengaText("·")
                JengaText("token gap")
            }
            JengaWrap {
                repeat(6) { JengaText("tag${it + 1}", color = JengaTheme.colors.brand) }
            }
        }
    }
}
