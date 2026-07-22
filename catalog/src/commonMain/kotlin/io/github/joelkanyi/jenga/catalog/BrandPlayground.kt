package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.chip.JengaChip
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.layout.JengaWrap
import io.github.joelkanyi.jenga.component.list.JengaListItem
import io.github.joelkanyi.jenga.component.selection.JengaToggle
import io.github.joelkanyi.jenga.component.stat.JengaStatTile
import io.github.joelkanyi.jenga.component.stat.JengaStatTone
import io.github.joelkanyi.jenga.component.tabs.JengaSegmentedControl
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.foundation.brand.JengaContrast
import io.github.joelkanyi.jenga.foundation.brand.JengaCornerStyle
import io.github.joelkanyi.jenga.foundation.brand.JengaDensity
import io.github.joelkanyi.jenga.foundation.brand.jengaBrand
import io.github.joelkanyi.jenga.theme.JengaTheme

private class Seed(val name: String, val hex: String, val color: Color)

private val seeds = listOf(
    Seed("Indigo", "0xFF6C5CE7", Color(0xFF6C5CE7)),
    Seed("Emerald", "0xFF10B981", Color(0xFF10B981)),
    Seed("Rose", "0xFFF43F5E", Color(0xFFF43F5E)),
    Seed("Amber", "0xFFF59E0B", Color(0xFFF59E0B)),
    Seed("Sky", "0xFF0EA5E9", Color(0xFF0EA5E9)),
    Seed("Slate", "0xFF334155", Color(0xFF334155)),
)

private val corners = listOf(JengaCornerStyle.Rounded, JengaCornerStyle.Soft, JengaCornerStyle.Sharp)
private val densities = listOf(JengaDensity.Compact, JengaDensity.Comfortable, JengaDensity.Spacious)
private val contrasts = listOf(JengaContrast.Standard, JengaContrast.Medium, JengaContrast.High)

/**
 * The live brand surface: pick a seed, corner, density and contrast and the
 * preview re-themes instantly through a nested [JengaTheme]. One seed derives a
 * whole accessible light and dark scheme; the code below always matches the
 * current selection.
 */
@Composable
fun BrandPlayground(modifier: Modifier = Modifier) {
    var seedIndex by remember { mutableIntStateOf(0) }
    var cornerIndex by remember { mutableIntStateOf(0) }
    var densityIndex by remember { mutableIntStateOf(1) }
    var contrastIndex by remember { mutableIntStateOf(0) }
    var dark by remember { mutableStateOf(false) }

    val seed = seeds[seedIndex]
    val brand = jengaBrand(
        seed = seed.color,
        contrast = contrasts[contrastIndex],
        corner = corners[cornerIndex],
        density = densities[densityIndex],
    )

    JengaStack(modifier = modifier, space = JengaTheme.spacing.xl) {
        JengaText(
            "One seed color derives a whole accessible light and dark theme. Change any input and the preview re-themes live.",
            style = JengaTheme.typography.bodyMedium,
            color = JengaTheme.colors.textSecondary,
        )

        JengaStack(space = JengaTheme.spacing.md) {
            Control("Seed") {
                JengaWrap {
                    seeds.forEachIndexed { index, s ->
                        val selected = index == seedIndex
                        JengaBox(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { seedIndex = index },
                            background = s.color,
                            shape = JengaTheme.shapes.sm,
                            border = BorderStroke(
                                width = if (selected) 3.dp else 1.dp,
                                color = if (selected) JengaTheme.colors.textPrimary else JengaTheme.colors.border,
                            ),
                        ) {}
                    }
                }
            }
            Control("Corner") {
                JengaSegmentedControl(cornerIndex, listOf("Rounded", "Soft", "Sharp"), { cornerIndex = it })
            }
            Control("Density") {
                JengaSegmentedControl(densityIndex, listOf("Compact", "Comfort", "Spacious"), { densityIndex = it })
            }
            Control("Contrast") {
                JengaSegmentedControl(contrastIndex, listOf("Standard", "Medium", "High"), { contrastIndex = it })
            }
            Control("Appearance") {
                JengaInline {
                    JengaText(if (dark) "Dark" else "Light", modifier = Modifier.weight(1f))
                    JengaToggle(checked = dark, onCheckedChange = { dark = it })
                }
            }
        }

        JengaTheme(brand = brand, darkTheme = dark) {
            BrandPreviewCluster()
        }

        CatalogCode(
            """
            val brand = jengaBrand(
                seed = Color(${seed.hex}),
                contrast = JengaContrast.${contrasts[contrastIndex].name},
                corner = JengaCornerStyle.${corners[cornerIndex].name},
                density = JengaDensity.${densities[densityIndex].name},
            )
            JengaTheme(brand = brand, darkTheme = $dark) { App() }
            """.trimIndent(),
        )
    }
}

@Composable
private fun Control(label: String, content: @Composable () -> Unit) {
    JengaStack(space = JengaTheme.spacing.xs) {
        JengaText(label, style = JengaTheme.typography.caption, color = JengaTheme.colors.textMuted)
        content()
    }
}

/** A representative mini-screen that shows the derived theme across many tokens. */
@Composable
private fun BrandPreviewCluster() {
    JengaBox(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(JengaTheme.spacing.xl),
        background = JengaTheme.colors.background,
        shape = JengaTheme.shapes.cardLarge,
        border = BorderStroke(1.dp, JengaTheme.colors.border),
    ) {
        JengaStack(space = JengaTheme.spacing.lg) {
            JengaText("Checkout", style = JengaTheme.typography.headingSmall)
            JengaWrap {
                JengaBadge("Valid", tone = JengaBadgeTone.Success)
                JengaBadge("Fast", tone = JengaBadgeTone.Info)
                JengaChip("Popular", selected = true, onClick = {})
            }
            JengaInline {
                JengaStatTile(label = "Total", value = "1,284", unit = "KES", tone = JengaStatTone.Success)
                JengaStatTile(label = "Saved", value = "12%", tone = JengaStatTone.Neutral)
            }
            JengaCard {
                JengaListItem(headline = "Ada Lovelace", supporting = "VIP · Table 4", onClick = {})
            }
            JengaInline(space = JengaTheme.spacing.sm) {
                JengaButton("Pay now", {}, modifier = Modifier.weight(1f))
                JengaButton("Cancel", {}, variant = JengaButtonVariant.Outline, modifier = Modifier.weight(1f))
            }
        }
    }
}
