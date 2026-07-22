package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A monospace code block styled from Jenga tokens. Long lines scroll horizontally
 * so the surrounding column never widens. Pass [framed] as false to drop the
 * rounded clip when the block sits inside another frame.
 */
@Composable
fun CatalogCode(code: String, modifier: Modifier = Modifier, framed: Boolean = true) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (framed) Modifier.clip(JengaTheme.shapes.md) else Modifier)
            .background(JengaTheme.colors.surfaceSunk)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        JengaText(
            text = "kotlin",
            style = JengaTheme.typography.caption,
            color = JengaTheme.colors.textMuted,
        )
        JengaText(
            text = code,
            style = JengaTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = JengaTheme.colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            softWrap = false,
        )
    }
}
