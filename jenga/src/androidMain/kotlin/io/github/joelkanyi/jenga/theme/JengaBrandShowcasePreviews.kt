package io.github.joelkanyi.jenga.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.banner.JengaBanner
import io.github.joelkanyi.jenga.component.banner.JengaBannerTone
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.chip.JengaChip
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.stepper.JengaStepper
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.foundation.brand.JengaCornerStyle
import io.github.joelkanyi.jenga.foundation.brand.JengaDensity
import io.github.joelkanyi.jenga.foundation.brand.jengaBrand

// Proves re-branding end to end: the same components under a custom brand (a
// purple seed, sharp corners, compact density), so the golden differs visibly
// from the default theme in colour, corner radius and spacing.
@JengaBlockPreviews
@Composable
internal fun JengaBrandShowcasePreview() {
    JengaTheme(
        brand = jengaBrand(
            seed = Color(0xFF6D28D9),
            corner = JengaCornerStyle.Sharp,
            density = JengaDensity.Compact,
        ),
    ) {
        BrandShowcase()
    }
}

@Composable
private fun BrandShowcase() {
    var quantity by remember { mutableIntStateOf(2) }
    var selected by remember { mutableStateOf(true) }
    JengaStack(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        space = JengaTheme.spacing.md,
    ) {
        JengaText("Acme", style = JengaTheme.typography.headingSmall)
        JengaInline(space = JengaTheme.spacing.sm) {
            JengaButton("Primary", {}, variant = JengaButtonVariant.Primary)
            JengaButton("Outline", {}, variant = JengaButtonVariant.Outline)
        }
        JengaInline(space = JengaTheme.spacing.sm) {
            JengaChip(label = "Selected", selected = selected, onClick = { selected = !selected })
            JengaBadge(text = "New", tone = JengaBadgeTone.Brand)
            JengaStepper(value = quantity, onValueChange = { quantity = it }, min = 1, max = 9)
        }
        JengaBanner(message = "Your brand flows through every block.", tone = JengaBannerTone.Info, title = "Themed")
        JengaCard {
            JengaText(
                "Sharp corners, compact spacing, purple accent.",
                modifier = Modifier.padding(JengaTheme.spacing.md),
            )
        }
    }
}
