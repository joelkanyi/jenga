package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.component.layout.JengaGrid
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.foundation.typography.withTabularFigures
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * The token gallery: principles, then color, typography and the spacing, shape,
 * elevation, sizing and motion scales, each rendered straight from [JengaTheme].
 */
@Composable
fun FoundationsSection(
    modifier: Modifier = Modifier,
) {
    JengaStack(
        modifier = modifier,
        space = JengaTheme.spacing.xxl,
        content = {
            PrinciplesSection()
            ColorsSection()
            TypographySection()
            ScaleSections()
        },
    )
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    JengaStack(
        space = JengaTheme.spacing.lg,
        content = {
            JengaText(
                text = title.uppercase(),
                style = JengaTheme.typography.label,
                color = JengaTheme.colors.brand,
            )
            JengaBox(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(
                    all = JengaTheme.spacing.xl,
                ),
                background = JengaTheme.colors.surface,
                shape = JengaTheme.shapes.card,
                border = BorderStroke(
                    width = 1.dp,
                    color = JengaTheme.colors.border,
                ),
                content = {
                    content()
                },
            )
        },
    )
}

@Composable
private fun PrinciplesSection() {
    Section(
        title = "Principles",
        content = {
            JengaStack(
                space = JengaTheme.spacing.lg,
                content = {
                    Principle(
                        title = "Tokens, never hardcoded values",
                        description = "All color/space/type come from JengaTheme.*.",
                    )
                    Principle(
                        title = "Blocks are stateless & data-driven",
                        description = "State is hoisted; SDUI-friendly.",
                    )
                    Principle(
                        title = "Override via Defaults, don't fork",
                        description = "Every block exposes a Defaults object + params.",
                    )
                    Principle(
                        title = "Accessible by default",
                        description = "48dp targets, focus rings, WCAG-tested contrast.",
                    )
                    Principle(
                        title = "Light & dark are first-class",
                        description = "Every block ships verified in both.",
                    )
                },
            )
        },
    )
}

@Composable
private fun Principle(
    title: String,
    description: String,
) {
    JengaStack(
        space = JengaTheme.spacing.xxs,
        content = {
            JengaText(
                text = title,
                style = JengaTheme.typography.titleSmall,
            )
            JengaText(
                text = description,
                style = JengaTheme.typography.bodySmall,
                color = JengaTheme.colors.textMuted,
            )
        },
    )
}

@Composable
private fun ColorsSection() {
    val c = JengaTheme.colors
    val swatches = listOf(
        "brand" to c.brand,
        "brandSubtle" to c.brandSubtle,
        "ink" to c.ink,
        "background" to c.background,
        "surface" to c.surface,
        "surfaceVariant" to c.surfaceVariant,
        "surfaceSunk" to c.surfaceSunk,
        "inverseSurface" to c.inverseSurface,
        "surfaceDisabled" to c.surfaceDisabled,
        "textPrimary" to c.textPrimary,
        "textSecondary" to c.textSecondary,
        "textMuted" to c.textMuted,
        "textFaint" to c.textFaint,
        "border" to c.border,
        "borderStrong" to c.borderStrong,
        "success" to c.success,
        "successContainer" to c.successContainer,
        "warning" to c.warning,
        "warningContainer" to c.warningContainer,
        "error" to c.error,
        "errorContainer" to c.errorContainer,
        "info" to c.info,
        "infoContainer" to c.infoContainer,
    )
    Section(
        title = "Color",
        content = {
            JengaGrid(
                columns = 2,
                verticalSpace = JengaTheme.spacing.md,
                content = {
                    swatches.forEach { (name, color) ->
                        Swatch(
                            name = name,
                            color = color,
                        )
                    }
                },
            )
        },
    )
}

@Composable
private fun Swatch(
    name: String,
    color: Color,
) {
    JengaInline(
        space = JengaTheme.spacing.sm,
        content = {
            JengaBox(
                modifier = Modifier.size(
                    size = 36.dp,
                ),
                background = color,
                shape = JengaTheme.shapes.sm,
                border = BorderStroke(
                    width = 1.dp,
                    color = JengaTheme.colors.border,
                ),
                content = {},
            )
            JengaText(
                text = name,
                style = JengaTheme.typography.bodySmall,
                color = JengaTheme.colors.textSecondary,
            )
        },
    )
}

@Composable
private fun TypographySection() {
    val t = JengaTheme.typography
    Section(
        title = "Typography",
        content = {
            JengaStack(
                space = JengaTheme.spacing.lg,
                content = {
                    TypeRow(
                        name = "display",
                        sample = {
                            JengaText(
                                text = "Jenga",
                                style = t.display,
                            )
                        },
                    )
                    TypeRow(
                        name = "headingLarge",
                        sample = {
                            JengaText(
                                text = "Heading large",
                                style = t.headingLarge,
                            )
                        },
                    )
                    TypeRow(
                        name = "headingMedium",
                        sample = {
                            JengaText(
                                text = "Heading medium",
                                style = t.headingMedium,
                            )
                        },
                    )
                    TypeRow(
                        name = "titleLarge",
                        sample = {
                            JengaText(
                                text = "Title large",
                                style = t.titleLarge,
                            )
                        },
                    )
                    TypeRow(
                        name = "bodyMedium",
                        sample = {
                            JengaText(
                                text = "Body: default text",
                                style = t.bodyMedium,
                            )
                        },
                    )
                    TypeRow(
                        name = "label",
                        sample = {
                            JengaText(
                                text = "LABEL",
                                style = t.label,
                            )
                        },
                    )
                    TypeRow(
                        name = "caption",
                        sample = {
                            JengaText(
                                text = "Caption",
                                style = t.caption,
                            )
                        },
                    )
                    TypeRow(
                        name = "mono",
                        sample = {
                            JengaText(
                                text = "SN-0042-7781",
                                style = t.mono.withTabularFigures(),
                            )
                        },
                    )
                },
            )
        },
    )
}

@Composable
private fun TypeRow(
    name: String,
    sample: @Composable () -> Unit,
) {
    JengaStack(
        space = JengaTheme.spacing.xxs,
        content = {
            JengaText(
                text = name,
                style = JengaTheme.typography.caption,
                color = JengaTheme.colors.textMuted,
            )
            sample()
        },
    )
}

@Composable
private fun ScaleSections() {
    val s = JengaTheme.spacing
    JengaStack(
        space = JengaTheme.spacing.xxl,
        content = {
            Section(
                title = "Spacing",
                content = {
                    JengaStack(
                        space = JengaTheme.spacing.sm,
                        content = {
                            listOf(
                                s.xs to "xs",
                                s.sm to "sm",
                                s.md to "md",
                                s.lg to "lg",
                                s.xl to "xl",
                                s.xxl to "xxl",
                            ).forEach { (value, label) ->
                                JengaInline(
                                    content = {
                                        JengaBox(
                                            modifier = Modifier.width(
                                                width = value,
                                            ).height(
                                                height = 16.dp,
                                            ),
                                            background = JengaTheme.colors.brand,
                                            shape = JengaTheme.shapes.xs,
                                            content = {},
                                        )
                                        JengaText(
                                            text = "$label (${value.value.toInt()})",
                                            style = JengaTheme.typography.bodySmall,
                                            color = JengaTheme.colors.textSecondary,
                                        )
                                    },
                                )
                            }
                        },
                    )
                },
            )
            Section(
                title = "Shape",
                content = {
                    JengaInline(
                        space = JengaTheme.spacing.lg,
                        content = {
                            ShapeSample(
                                name = "sm",
                                shape = JengaTheme.shapes.sm,
                            )
                            ShapeSample(
                                name = "control",
                                shape = JengaTheme.shapes.control,
                            )
                            ShapeSample(
                                name = "card",
                                shape = JengaTheme.shapes.card,
                            )
                            ShapeSample(
                                name = "pill",
                                shape = JengaTheme.shapes.pill,
                            )
                        },
                    )
                },
            )
            Section(
                title = "Elevation",
                content = {
                    JengaInline(
                        space = JengaTheme.spacing.xl,
                        content = {
                            ElevationSample(
                                name = "sm",
                                elevation = JengaTheme.elevation.sm,
                            )
                            ElevationSample(
                                name = "md",
                                elevation = JengaTheme.elevation.md,
                            )
                            ElevationSample(
                                name = "lg",
                                elevation = JengaTheme.elevation.lg,
                            )
                        },
                    )
                },
            )
            Section(
                title = "Sizing",
                content = {
                    val z = JengaTheme.sizing
                    JengaStack(
                        space = JengaTheme.spacing.sm,
                        content = {
                            listOf(
                                z.minTouchTarget to "minTouchTarget",
                                z.iconSmall to "iconSmall",
                                z.iconMedium to "iconMedium",
                                z.iconLarge to "iconLarge",
                                z.controlHeightSmall to "controlHeightSmall",
                                z.controlHeightMedium to "controlHeightMedium",
                                z.controlHeightLarge to "controlHeightLarge",
                                z.fieldHeight to "fieldHeight",
                            ).forEach { (value, label) ->
                                JengaInline(
                                    content = {
                                        JengaBox(
                                            modifier = Modifier.size(
                                                size = value,
                                            ),
                                            background = JengaTheme.colors.brandSubtle,
                                            shape = JengaTheme.shapes.xs,
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = JengaTheme.colors.border,
                                            ),
                                            content = {},
                                        )
                                        JengaText(
                                            text = "$label (${value.value.toInt()})",
                                            style = JengaTheme.typography.bodySmall,
                                            color = JengaTheme.colors.textSecondary,
                                        )
                                    },
                                )
                            }
                        },
                    )
                },
            )
            Section(
                title = "Motion",
                content = {
                    val m = JengaTheme.motion
                    JengaStack(
                        space = JengaTheme.spacing.md,
                        content = {
                            JengaText(
                                text = "Durations",
                                style = JengaTheme.typography.titleSmall,
                            )
                            listOf(
                                m.durationFast to "fast",
                                m.durationMedium to "medium",
                                m.durationSlow to "slow",
                                m.durationSlowest to "slowest",
                            ).forEach { (ms, label) ->
                                JengaInline(
                                    content = {
                                        JengaBox(
                                            modifier = Modifier.width(
                                                width = (ms / 4).dp,
                                            ).height(
                                                height = 12.dp,
                                            ),
                                            background = JengaTheme.colors.brand,
                                            shape = JengaTheme.shapes.xs,
                                            content = {},
                                        )
                                        JengaText(
                                            text = "$label (${ms}ms)",
                                            style = JengaTheme.typography.bodySmall,
                                            color = JengaTheme.colors.textSecondary,
                                        )
                                    },
                                )
                            }
                            JengaText(
                                text = "Easing",
                                style = JengaTheme.typography.titleSmall,
                            )
                            JengaText(
                                text = "emphasized · standard · linear",
                                style = JengaTheme.typography.bodySmall,
                                color = JengaTheme.colors.textMuted,
                            )
                        },
                    )
                },
            )
        },
    )
}

@Composable
private fun ShapeSample(
    name: String,
    shape: Shape,
) {
    JengaStack(
        space = JengaTheme.spacing.sm,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            JengaBox(
                modifier = Modifier.size(
                    size = 56.dp,
                ),
                background = JengaTheme.colors.surfaceSunk,
                shape = shape,
                border = BorderStroke(
                    width = 1.dp,
                    color = JengaTheme.colors.borderStrong,
                ),
                content = {},
            )
            JengaText(
                text = name,
                style = JengaTheme.typography.caption,
                color = JengaTheme.colors.textMuted,
            )
        },
    )
}

@Composable
private fun ElevationSample(
    name: String,
    elevation: Dp,
) {
    JengaStack(
        space = JengaTheme.spacing.sm,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Box(
                modifier = Modifier
                    .size(
                        size = 56.dp,
                    )
                    .shadow(
                        elevation = elevation,
                        shape = JengaTheme.shapes.md,
                    )
                    .clip(
                        shape = JengaTheme.shapes.md,
                    )
                    .background(
                        color = JengaTheme.colors.surface,
                    ),
            )
            JengaText(
                text = name,
                style = JengaTheme.typography.caption,
                color = JengaTheme.colors.textMuted,
            )
        },
    )
}
