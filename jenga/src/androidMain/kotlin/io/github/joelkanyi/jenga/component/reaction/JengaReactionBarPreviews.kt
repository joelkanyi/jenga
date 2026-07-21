package io.github.joelkanyi.jenga.component.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.card.JengaCardVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaReactionBarPreview() {
    JengaTheme { ReactionBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaReactionBarRtlPreview() {
    JengaTheme { RtlPreview { ReactionBarShowcase() } }
}

@Composable
private fun ReactionBarShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
    ) {
        JengaCard(variant = JengaCardVariant.Outlined) {
            JengaReactionBar(
                onPositive = {},
                onNegative = {},
                positiveContentDescription = "More like this",
                negativeContentDescription = "Not this",
                action = {
                    JengaText(
                        text = "Add to plan",
                        style = JengaTheme.typography.bodySmall,
                        color = JengaTheme.colors.textPrimary,
                        modifier = Modifier
                            .clip(JengaTheme.shapes.pill)
                            .border(1.5.dp, JengaTheme.colors.borderStrong, JengaTheme.shapes.pill)
                            .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.sm),
                    )
                },
            )
        }
    }
}
