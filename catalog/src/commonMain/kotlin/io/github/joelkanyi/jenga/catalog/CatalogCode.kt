package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A monospace code block with a copy action, styled from Jenga tokens. Long lines
 * scroll horizontally so the surrounding column never widens. Pass [framed] as
 * false to drop the rounded clip when the block sits inside another frame.
 */
@Composable
fun CatalogCode(code: String, modifier: Modifier = Modifier, framed: Boolean = true) {
    val clipboard = LocalClipboardManager.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (framed) Modifier.clip(JengaTheme.shapes.md) else Modifier)
            .background(JengaTheme.colors.surfaceSunk)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            JengaText(
                text = "kotlin",
                style = JengaTheme.typography.caption,
                color = JengaTheme.colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            JengaButton(
                text = "Copy",
                onClick = { clipboard.setText(AnnotatedString(code)) },
                variant = JengaButtonVariant.Ghost,
                size = JengaButtonSize.Small,
            )
        }
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
