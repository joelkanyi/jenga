package io.github.joelkanyi.jenga.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBarChartPreview() {
    JengaTheme { JengaBarChartShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBarChartRtlPreview() {
    JengaTheme { RtlPreview { JengaBarChartShowcase() } }
}

@Composable
private fun JengaBarChartShowcase() {
    val months = previewMonths
    Column(
        modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xl),
    ) {
        JengaBarChart(entries = months, barContentDescription = { "${it.label}, ${it.value}" })
        JengaBarChart(
            entries = months,
            barContentDescription = { "${it.label}, ${it.value}" },
            selectedIndex = 4,
            onSelect = {},
            plotHeight = 70.dp,
            minBarHeight = 4.dp,
            barMaxWidth = 34.dp,
            barSpacing = 10.dp,
            barShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp, bottomEnd = 3.dp),
            labelSpacing = 6.dp,
            contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp),
        )
        JengaBarChart(
            entries = months.take(3),
            barContentDescription = { "${it.label}, ${it.value}" },
            valueLabel = { entry, _ ->
                JengaText("${(entry.value / 1000).toInt()}k", style = JengaTheme.typography.caption)
            },
        )
    }
}

// ---- Target line -----------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBarChartTargetPreview() {
    JengaTheme { JengaBarChartTargetShowcase() }
}

// ---- Highlighted, empty and ringed bars ------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBarChartHighlightPreview() {
    JengaTheme { JengaBarChartHighlightShowcase() }
}

@JengaBlockPreviews
@Composable
internal fun JengaBarChartHighlightOptionsPreview() {
    JengaTheme { JengaBarChartHighlightOptionsShowcase() }
}

@JengaBlockPreviews
@Composable
internal fun JengaBarChartTargetScrollPreview() {
    JengaTheme {
        ChartShowcaseColumn {
            Captioned("Scrolling chart with a target") { ScrollingTargetChart() }
            Captioned("Scrolling chart with a target, highlightColors() and a ring on the first bar") {
                ScrollingTargetChart(selectedIndex = 0, colors = JengaBarChartDefaults.highlightColors())
            }
        }
    }
}

// ---- Target label next to tall bars ----------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBarChartTargetLabelPreview() {
    JengaTheme { JengaBarChartTargetLabelShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBarChartTargetLabelRtlPreview() {
    JengaTheme { RtlPreview { JengaBarChartTargetLabelShowcase() } }
}

@Composable
private fun JengaBarChartTargetLabelShowcase() {
    ChartShowcaseColumn {
        Captioned("Last bar above the target: the label sits above that bar") {
            FullWeekChart(sunday = 46f, selectedIndex = 6)
        }
        Captioned("Last bar exactly at the target, default colors(): the label stays on the line") {
            FullWeekChart(sunday = 40f, selectedIndex = null, colors = JengaBarChartDefaults.colors())
        }
        Captioned("Last bar exactly at the target, highlightColors(): the label clears the room kept for a ring") {
            FullWeekChart(sunday = 40f, selectedIndex = null)
        }
    }
}

// A week with data on every day; Sunday is today.
@Composable
private fun FullWeekChart(
    sunday: Float,
    selectedIndex: Int?,
    colors: JengaBarChartColors = JengaBarChartDefaults.highlightColors(),
) {
    JengaBarChart(
        entries = previewWeek.take(4) + listOf(
            JengaBarChartEntry("Fri", 36f),
            JengaBarChartEntry("Sat", 30f),
            JengaBarChartEntry("Sun", sunday),
        ),
        barContentDescription = { "${it.label}, ${it.value}" },
        selectedIndex = selectedIndex,
        onSelect = {},
        highlightedIndices = setOf(6),
        maxValue = 50f,
        target = JengaChartTarget(value = 40f, label = "Target"),
        colors = colors,
    )
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBarChartTargetRtlPreview() {
    JengaTheme {
        RtlPreview {
            ChartShowcaseColumn {
                Captioned("Target, highlight, empty days and a ring, right to left") { WeekChart(selectedIndex = 1) }
                Captioned("Scrolling chart with a target, right to left") { ScrollingTargetChart() }
            }
        }
    }
}

private val previewMonths = listOf(
    JengaBarChartEntry("May", 8200f),
    JengaBarChartEntry("Jun", 12400f),
    JengaBarChartEntry("Jul", 0f),
    JengaBarChartEntry("Aug", 9100f),
    JengaBarChartEntry("Sep", 15800f),
    JengaBarChartEntry("Oct", 6300f),
)

// Thousands collected on each day of a week. Thursday is today; Friday to Sunday have no data yet.
private val previewWeek = listOf(
    JengaBarChartEntry("Mon", 32f),
    JengaBarChartEntry("Tue", 41f),
    JengaBarChartEntry("Wed", 28f),
    JengaBarChartEntry("Thu", 48.2f),
    JengaBarChartEntry("Fri", 0f),
    JengaBarChartEntry("Sat", 0f),
    JengaBarChartEntry("Sun", 0f),
)

@Composable
private fun ChartShowcaseColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xl),
    ) {
        content()
    }
}

@Composable
private fun Captioned(caption: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md)) {
        JengaText(caption, style = JengaTheme.typography.caption, color = JengaTheme.colors.textMuted)
        content()
    }
}

@Composable
private fun WeekChart(
    selectedIndex: Int?,
    modifier: Modifier = Modifier,
    colors: JengaBarChartColors = JengaBarChartDefaults.highlightColors(),
    target: JengaChartTarget? = JengaChartTarget(value = 40f, label = "Target", contentDescription = "Target 40,000"),
) {
    JengaBarChart(
        entries = previewWeek,
        barContentDescription = { "${it.label}, ${it.value}" },
        modifier = modifier,
        contentDescription = "Collections this week",
        selectedIndex = selectedIndex,
        onSelect = {},
        highlightedIndices = setOf(3),
        emptyIndices = setOf(4, 5, 6),
        maxValue = 50f,
        target = target,
        colors = colors,
    )
}

@Composable
private fun ScrollingTargetChart(
    selectedIndex: Int? = null,
    colors: JengaBarChartColors = JengaBarChartDefaults.colors(),
) {
    JengaBarChart(
        entries = previewMonths + previewMonths,
        barContentDescription = { "${it.label}, ${it.value}" },
        selectedIndex = selectedIndex,
        onSelect = {},
        highlightedIndices = setOf(4),
        colors = colors,
        target = JengaChartTarget(value = 10000f, label = "Target"),
        arrangement = JengaBarChartArrangement.Scroll(slotWidth = 48.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
    )
}

@Composable
private fun JengaBarChartTargetShowcase() {
    ChartShowcaseColumn {
        Captioned("Target below the tallest bar, with a label") {
            JengaBarChart(
                entries = previewMonths,
                barContentDescription = { "${it.label}, ${it.value}" },
                target = JengaChartTarget(value = 10000f, label = "Target"),
            )
        }
        Captioned("Target above every bar: the bars scale down") {
            JengaBarChart(
                entries = previewMonths,
                barContentDescription = { "${it.label}, ${it.value}" },
                target = JengaChartTarget(value = 31600f, label = "Target"),
            )
        }
        Captioned("Target without a label, at the height of the fourth bar") {
            JengaBarChart(
                entries = previewMonths,
                barContentDescription = { "${it.label}, ${it.value}" },
                target = JengaChartTarget(value = 9100f),
            )
        }
        Captioned("Target with value labels and a selection") {
            JengaBarChart(
                entries = previewMonths.take(3),
                barContentDescription = { "${it.label}, ${it.value}" },
                selectedIndex = 1,
                onSelect = {},
                target = JengaChartTarget(value = 12400f, label = "Target"),
                valueLabel = { entry, _ ->
                    JengaText("${(entry.value / 1000).toInt()}k", style = JengaTheme.typography.caption)
                },
            )
        }
    }
}

@Composable
private fun JengaBarChartHighlightShowcase() {
    ChartShowcaseColumn {
        Captioned("highlightColors(): today highlighted and selected, days to come empty") {
            WeekChart(selectedIndex = 3)
        }
        Captioned("Ring on an ordinary bar; today stays highlighted") { WeekChart(selectedIndex = 1, target = null) }
        Captioned("Ring on an empty day") { WeekChart(selectedIndex = 5, target = null) }
        Captioned("No selection") { WeekChart(selectedIndex = null, target = null) }
    }
}

// The previews are captured at screen height, so each showcase holds at most four charts.
@Composable
private fun JengaBarChartHighlightOptionsShowcase() {
    ChartShowcaseColumn {
        Captioned("highlightColors(): ordinary bars in textMuted (meets 3:1 on the surface)") {
            WeekChart(selectedIndex = 1, target = null)
        }
        Captioned("For comparison: ordinary bars in textFaint (lighter, below 3:1 on the surface)") {
            val faint = JengaTheme.colors.textFaint
            WeekChart(
                selectedIndex = 1,
                colors = JengaBarChartDefaults.highlightColors().copy(
                    bar = faint,
                    selectedBar = faint,
                    unselectedBar = faint,
                ),
                target = null,
            )
        }
        Captioned("Default colors() with a highlight: no ring, the selection changes color") {
            WeekChart(selectedIndex = 1, colors = JengaBarChartDefaults.colors(), target = null)
        }
        Captioned("240dp wide") { WeekChart(selectedIndex = 3, modifier = Modifier.width(240.dp)) }
    }
}
