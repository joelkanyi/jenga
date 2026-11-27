package io.github.joelkanyi.jenga.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// The previews are captured at screen height, so each showcase holds at most four charts, and
// the one that is also captured at a large font scale holds three.

@JengaBlockPreviews
@Composable
internal fun JengaLineChartPreview() {
    JengaTheme { JengaLineChartShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaLineChartRtlPreview() {
    JengaTheme {
        RtlPreview {
            LineChartShowcaseColumn {
                LineChartCaptioned("Right to left: the first month is at the right, the target's label at the left") {
                    OnTimeChart(target = JengaChartTarget(value = 80f, label = "Target"))
                }
                LineChartCaptioned("Right to left, with a selected point and a target above the line") {
                    OnTimeChart(selectedIndex = 1, target = JengaChartTarget(value = 90f, label = "Target"))
                }
            }
        }
    }
}

// ---- Edge cases ------------------------------------------------------------

@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaLineChartEdgeCasesPreview() {
    JengaTheme {
        LineChartShowcaseColumn {
            LineChartCaptioned("No gridlines, and a target above the line") {
                OnTimeChart(target = JengaChartTarget(value = 90f, label = "Target"), gridlineCount = 0)
            }
            LineChartCaptioned("One point: centred, no line") {
                OnTimeChart(entries = previewOnTime.takeLast(1))
            }
            LineChartCaptioned("Equal values: a flat line at mid-height") {
                OnTimeChart(entries = previewOnTime.map { JengaLineChartEntry(it.label, 80f) })
            }
            LineChartCaptioned("No entries: the gridlines and the target only") {
                OnTimeChart(entries = emptyList(), target = JengaChartTarget(value = 80f, label = "Target"))
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaLineChartDensePreview() {
    JengaTheme {
        LineChartShowcaseColumn {
            LineChartCaptioned("Negative values, with a target at zero and no label") {
                OnTimeChart(
                    entries = listOf(-12f, -4f, 6f, -8f, 3f, 9f).mapIndexed { index, change ->
                        JengaLineChartEntry(previewOnTime[index].label, change)
                    },
                    target = JengaChartTarget(value = 0f),
                )
            }
            LineChartCaptioned("12 points, the fourth selected") {
                OnTimeChart(
                    entries = listOf(64f, 61f, 66f, 70f, 68f, 71f, 72f, 75f, 79f, 81f, 84f, 86f).mapIndexed { index, value ->
                        JengaLineChartEntry("JFMAMJJASOND"[index].toString(), value)
                    },
                    selectedIndex = 3,
                )
            }
            LineChartCaptioned("A fixed scale: minValue 60 and maxValue 90") {
                OnTimeChart(minValue = 60f, maxValue = 90f)
            }
            LineChartCaptioned("240dp wide, with a target the line ends above") {
                OnTimeChart(
                    modifier = Modifier.width(240.dp),
                    target = JengaChartTarget(value = 80f, label = "Target"),
                )
            }
        }
    }
}

@Composable
private fun JengaLineChartShowcase() {
    LineChartShowcaseColumn {
        LineChartCaptioned("Six months, 72 to 86: the latest point is emphasised") { OnTimeChart() }
        LineChartCaptioned("A selected point that is not the latest") { OnTimeChart(selectedIndex = 2) }
        LineChartCaptioned("The line rises through the target: the label sits above the last points") {
            OnTimeChart(target = JengaChartTarget(value = 80f, label = "Target"))
        }
    }
}

// Customers paying on time, in percent, over six months.
private val previewOnTime = listOf(
    JengaLineChartEntry("Jun", 72f),
    JengaLineChartEntry("Jul", 75f),
    JengaLineChartEntry("Aug", 79f),
    JengaLineChartEntry("Sep", 81f),
    JengaLineChartEntry("Oct", 84f),
    JengaLineChartEntry("Nov", 86f),
)

@Composable
private fun OnTimeChart(
    modifier: Modifier = Modifier,
    entries: List<JengaLineChartEntry> = previewOnTime,
    selectedIndex: Int? = null,
    minValue: Float? = null,
    maxValue: Float? = null,
    target: JengaChartTarget? = null,
    gridlineCount: Int = JengaLineChartDefaults.GridlineCount,
) {
    JengaLineChart(
        entries = entries,
        pointContentDescription = { "${it.label}, ${it.value.toInt()}%" },
        modifier = modifier,
        contentDescription = "Customers paying on time",
        selectedIndex = selectedIndex,
        onSelect = {},
        minValue = minValue,
        maxValue = maxValue,
        target = target,
        gridlineCount = gridlineCount,
    )
}

@Composable
private fun LineChartShowcaseColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xl),
    ) {
        content()
    }
}

@Composable
private fun LineChartCaptioned(caption: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md)) {
        JengaText(caption, style = JengaTheme.typography.caption, color = JengaTheme.colors.textMuted)
        content()
    }
}
