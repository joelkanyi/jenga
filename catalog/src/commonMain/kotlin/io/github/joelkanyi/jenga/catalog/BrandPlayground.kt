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

private class Seed(
    val name: String,
    val hex: String,
    val color: Color,
)

private val seeds = listOf(
    Seed(
        name = "Indigo",
        hex = "0xFF6C5CE7",
        color = Color(
            color = 0xFF6C5CE7,
        ),
    ),
    Seed(
        name = "Emerald",
        hex = "0xFF10B981",
        color = Color(
            color = 0xFF10B981,
        ),
    ),
    Seed(
        name = "Rose",
        hex = "0xFFF43F5E",
        color = Color(
            color = 0xFFF43F5E,
        ),
    ),
    Seed(
        name = "Amber",
        hex = "0xFFF59E0B",
        color = Color(
            color = 0xFFF59E0B,
        ),
    ),
    Seed(
        name = "Sky",
        hex = "0xFF0EA5E9",
        color = Color(
            color = 0xFF0EA5E9,
        ),
    ),
    Seed(
        name = "Slate",
        hex = "0xFF334155",
        color = Color(
            color = 0xFF334155,
        ),
    ),
)

private val corners = listOf(
    JengaCornerStyle.Rounded,
    JengaCornerStyle.Soft,
    JengaCornerStyle.Sharp,
)
private val densities = listOf(
    JengaDensity.Compact,
    JengaDensity.Comfortable,
    JengaDensity.Spacious,
)
private val contrasts = listOf(
    JengaContrast.Standard,
    JengaContrast.Medium,
    JengaContrast.High,
)

/**
 * The live brand surface: pick a seed, corner, density and contrast and the
 * preview re-themes instantly through a nested [JengaTheme]. One seed derives a
 * whole accessible light and dark scheme; the code below always matches the
 * current selection.
 */
@Composable
fun BrandPlayground(
    modifier: Modifier = Modifier,
) {
    var seedIndex by remember {
        mutableIntStateOf(
            value = 0,
        )
    }
    var cornerIndex by remember {
        mutableIntStateOf(
            value = 0,
        )
    }
    var densityIndex by remember {
        mutableIntStateOf(
            value = 1,
        )
    }
    var contrastIndex by remember {
        mutableIntStateOf(
            value = 0,
        )
    }
    var dark by remember {
        mutableStateOf(
            value = false,
        )
    }

    val seed = seeds[seedIndex]
    val brand = jengaBrand(
        seed = seed.color,
        contrast = contrasts[contrastIndex],
        corner = corners[cornerIndex],
        density = densities[densityIndex],
    )

    JengaStack(
        modifier = modifier,
        space = JengaTheme.spacing.xl,
        content = {
            JengaText(
                text = "One seed color derives a whole accessible light and dark theme. Change any input and the preview re-themes live.",
                style = JengaTheme.typography.bodyMedium,
                color = JengaTheme.colors.textSecondary,
            )

            JengaStack(
                space = JengaTheme.spacing.md,
                content = {
                    Control(
                        label = "Seed",
                        content = {
                            JengaWrap(
                                content = {
                                    seeds.forEachIndexed { index, s ->
                                        val selected = index == seedIndex
                                        JengaBox(
                                            modifier = Modifier
                                                .size(
                                                    size = 36.dp,
                                                )
                                                .clickable { seedIndex = index },
                                            background = s.color,
                                            shape = JengaTheme.shapes.sm,
                                            border = BorderStroke(
                                                width = if (selected) 3.dp else 1.dp,
                                                color = if (selected) JengaTheme.colors.textPrimary else JengaTheme.colors.border,
                                            ),
                                            content = {},
                                        )
                                    }
                                },
                            )
                        },
                    )
                    Control(
                        label = "Corner",
                        content = {
                            JengaSegmentedControl(
                                selectedIndex = cornerIndex,
                                segments = listOf(
                                    "Rounded",
                                    "Soft",
                                    "Sharp",
                                ),
                                onSelect = { cornerIndex = it },
                            )
                        },
                    )
                    Control(
                        label = "Density",
                        content = {
                            JengaSegmentedControl(
                                selectedIndex = densityIndex,
                                segments = listOf(
                                    "Compact",
                                    "Comfort",
                                    "Spacious",
                                ),
                                onSelect = { densityIndex = it },
                            )
                        },
                    )
                    Control(
                        label = "Contrast",
                        content = {
                            JengaSegmentedControl(
                                selectedIndex = contrastIndex,
                                segments = listOf(
                                    "Standard",
                                    "Medium",
                                    "High",
                                ),
                                onSelect = { contrastIndex = it },
                            )
                        },
                    )
                    Control(
                        label = "Appearance",
                        content = {
                            JengaInline(
                                content = {
                                    JengaText(
                                        text = if (dark) "Dark" else "Light",
                                        modifier = Modifier.weight(
                                            weight = 1f,
                                        ),
                                    )
                                    JengaToggle(
                                        checked = dark,
                                        onCheckedChange = { dark = it },
                                    )
                                },
                            )
                        },
                    )
                },
            )

            JengaTheme(
                brand = brand,
                darkTheme = dark,
                content = {
                    BrandPreviewCluster()
                },
            )

            CatalogCode(
                code = """
            val brand = jengaBrand(
                seed = Color(${seed.hex}),
                contrast = JengaContrast.${contrasts[contrastIndex].name},
                corner = JengaCornerStyle.${corners[cornerIndex].name},
                density = JengaDensity.${densities[densityIndex].name},
            )
            JengaTheme(brand = brand, darkTheme = $dark) { App() }
                """.trimIndent(),
            )
        },
    )
}

@Composable
private fun Control(
    label: String,
    content: @Composable () -> Unit,
) {
    JengaStack(
        space = JengaTheme.spacing.xs,
        content = {
            JengaText(
                text = label,
                style = JengaTheme.typography.caption,
                color = JengaTheme.colors.textMuted,
            )
            content()
        },
    )
}

/** A representative mini-screen that shows the derived theme across many tokens. */
@Composable
private fun BrandPreviewCluster() {
    JengaBox(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(
            all = JengaTheme.spacing.xl,
        ),
        background = JengaTheme.colors.background,
        shape = JengaTheme.shapes.cardLarge,
        border = BorderStroke(
            width = 1.dp,
            color = JengaTheme.colors.border,
        ),
        content = {
            JengaStack(
                space = JengaTheme.spacing.lg,
                content = {
                    JengaText(
                        text = "Checkout",
                        style = JengaTheme.typography.headingSmall,
                    )
                    JengaWrap(
                        content = {
                            JengaBadge(
                                text = "Valid",
                                tone = JengaBadgeTone.Success,
                            )
                            JengaBadge(
                                text = "Fast",
                                tone = JengaBadgeTone.Info,
                            )
                            JengaChip(
                                label = "Popular",
                                selected = true,
                                onClick = {},
                            )
                        },
                    )
                    JengaInline(
                        content = {
                            JengaStatTile(
                                label = "Total",
                                value = "1,284",
                                unit = "KES",
                                tone = JengaStatTone.Success,
                            )
                            JengaStatTile(
                                label = "Saved",
                                value = "12%",
                                tone = JengaStatTone.Neutral,
                            )
                        },
                    )
                    JengaCard(
                        content = {
                            JengaListItem(
                                headline = "Ada Lovelace",
                                supporting = "VIP · Table 4",
                                onClick = {},
                            )
                        },
                    )
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaButton(
                                text = "Pay now",
                                onClick = {},
                                modifier = Modifier.weight(
                                    weight = 1f,
                                ),
                            )
                            JengaButton(
                                text = "Cancel",
                                onClick = {},
                                variant = JengaButtonVariant.Outline,
                                modifier = Modifier.weight(
                                    weight = 1f,
                                ),
                            )
                        },
                    )
                },
            )
        },
    )
}
