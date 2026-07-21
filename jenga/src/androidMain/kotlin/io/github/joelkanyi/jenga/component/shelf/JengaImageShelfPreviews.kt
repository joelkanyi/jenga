package io.github.joelkanyi.jenga.component.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.action.JengaAction
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaImageShelfPreview() {
    JengaTheme { ShelfShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaImageShelfRtlPreview() {
    JengaTheme { RtlPreview { ShelfShowcase() } }
}

@Composable
private fun ShelfShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(vertical = JengaTheme.spacing.lg),
    ) {
        JengaImageShelf(
            title = "Picked for tonight",
            eyebrow = "BECAUSE YOU'RE COOKING LIGHT",
            action = JengaAction(label = "See all", onClick = {}),
        ) {
            JengaShelfCard(
                title = "Githeri & avocado",
                meta = "25 min · ≈540 kcal",
                overlayTag = { Tag("Lighter") },
            )
            JengaShelfCard(title = "Ndengu & chapati", meta = "40 min · KES 160", overlayTag = { Tag("Budget") })
            JengaShelfCard(title = "Mboga & ugali", meta = "30 min · ≈480 kcal")
        }
    }
}

@Composable
private fun Tag(text: String) {
    JengaText(
        text = text,
        style = JengaTheme.typography.caption,
        color = JengaTheme.colors.onOverlay,
        modifier = Modifier
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.overlaySurface)
            .padding(horizontal = JengaTheme.spacing.sm, vertical = JengaTheme.spacing.xxs),
    )
}
