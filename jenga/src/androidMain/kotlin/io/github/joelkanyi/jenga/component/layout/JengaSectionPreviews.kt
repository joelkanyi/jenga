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
internal fun JengaSectionPreview() {
    JengaTheme {
        JengaSection(
            modifier = Modifier
                .background(JengaTheme.colors.background)
                .padding(JengaTheme.spacing.lg),
            title = "Recent scans",
            subtitle = "Last 24 hours",
            actionLabel = "See all",
            onActionClick = {},
        ) {
            JengaText("Row one")
            JengaText("Row two")
            JengaText("Row three")
        }
    }
}
