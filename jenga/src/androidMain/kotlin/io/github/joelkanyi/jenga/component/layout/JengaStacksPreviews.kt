package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
