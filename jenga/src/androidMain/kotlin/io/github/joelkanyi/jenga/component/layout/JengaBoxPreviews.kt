package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
