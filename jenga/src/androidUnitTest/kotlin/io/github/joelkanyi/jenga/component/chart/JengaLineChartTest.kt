package io.github.joelkanyi.jenga.component.chart

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs
import kotlin.math.sqrt

@RunWith(RobolectricTestRunner::class)
class JengaLineChartTest {

    @get:Rule
    val compose = createComposeRule()

    // Six points on 72..86, as on the Atlas performance screen.
    private val entries = listOf(
        JengaLineChartEntry("Jun", 72f),
        JengaLineChartEntry("Jul", 75f),
        JengaLineChartEntry("Aug", 79f),
        JengaLineChartEntry("Sep", 81f),
        JengaLineChartEntry("Oct", 84f),
        JengaLineChartEntry("Nov", 86f),
    )

    // ---- Colors ------------------------------------------------------------

    @Test
    fun defaultColorsUseThemeTokens() {
        compose.setContent {
            JengaTheme {
                val c = JengaTheme.colors
                val colors = JengaLineChartDefaults.colors()
                assertEquals(c.brand, colors.line)
                assertEquals(c.brand, colors.point)
                assertEquals(c.surface, colors.pointFill)
                assertEquals(c.brand, colors.selectedPoint)
                assertEquals(c.border, colors.gridline)
                assertEquals(c.textSecondary, colors.label)
                assertEquals(c.textPrimary, colors.selectedLabel)
                assertEquals(c.textMuted, colors.targetLine)
                assertEquals(c.textMuted, colors.targetLabel)
                assertEquals(colors, colors.copy())
                assertEquals(Color.Red, colors.copy(gridline = Color.Red).gridline)
            }
        }
    }

    // ---- Scale -------------------------------------------------------------

    @Test
    fun theAutomaticRangeIsTheSmallestToTheLargestEntryNotZeroBased() {
        assertEquals(72f..86f, range(entriesMin = 72f, entriesMax = 86f))
        assertEquals(-12f..9f, range(entriesMin = -12f, entriesMax = 9f))
        assertEquals(0f..0f, range())
    }

    @Test
    fun minValueAndMaxValueFixTheEndsOfTheRange() {
        assertEquals(60f..90f, range(entriesMin = 72f, entriesMax = 86f, minValue = 60f, maxValue = 90f))
        assertEquals(60f..86f, range(entriesMin = 72f, entriesMax = 86f, minValue = 60f))
        assertEquals(72f..80f, range(entriesMin = 72f, entriesMax = 86f, maxValue = 80f))
        // A fixed end beyond every entry leaves no scale: every entry is clamped to it.
        assertEquals(95f..95f, range(entriesMin = 72f, entriesMax = 86f, minValue = 95f))
        assertEquals(50f..50f, range(entriesMin = 72f, entriesMax = 86f, maxValue = 50f))
    }

    @Test
    fun theTargetAlwaysExtendsTheRange() {
        assertEquals(72f..90f, range(entriesMin = 72f, entriesMax = 86f, target = 90f))
        assertEquals(60f..86f, range(entriesMin = 72f, entriesMax = 86f, target = 60f))
        assertEquals(72f..86f, range(entriesMin = 72f, entriesMax = 86f, target = 80f))
        assertEquals(70f..95f, range(entriesMin = 72f, entriesMax = 86f, minValue = 70f, maxValue = 90f, target = 95f))
        assertEquals(40f..40f, range(target = 40f))
    }

    @Test
    fun valuesMapLinearlyOntoTheRangeAndAreClamped() {
        assertEquals(0f, lineChartFraction(72f, 72f, 86f))
        assertEquals(1f, lineChartFraction(86f, 72f, 86f))
        assertEquals(0.5f, lineChartFraction(79f, 72f, 86f))
        assertEquals(0f, lineChartFraction(10f, 72f, 86f))
        assertEquals(1f, lineChartFraction(100f, 72f, 86f))
        assertEquals(0.25f, lineChartFraction(-5f, -10f, 10f))
    }

    @Test
    fun aZeroRangeGivesTheMidHeight() {
        assertEquals(0.5f, lineChartFraction(80f, 80f, 80f))
        assertEquals(0.5f, lineChartFraction(0f, 0f, 0f))
    }

    @Test
    fun withAZeroRangeValuesOutsideItGoToTheTopOrTheBottom() {
        // maxValue below every entry, and minValue above every entry.
        assertEquals(1f, lineChartFraction(86f, 50f, 50f))
        assertEquals(1f, lineChartFraction(72f, 50f, 50f))
        assertEquals(0f, lineChartFraction(72f, 95f, 95f))
        assertEquals(0f, lineChartFraction(86f, 95f, 95f))
        assertEquals(0.5f, lineChartFraction(80f, 80f, 80f))
    }

    @Test
    fun entriesAboveMaxValueAreDrawnAtTheTopOfThePlot() {
        // Every entry is above maxValue, so the scale is 50..50 and the target sits at mid-height.
        // The label is lifted above the last point, which is at the top inset of the plot.
        compose.setContent { JengaTheme { Chart(maxValue = 50f, target = JengaChartTarget(50f, WideLabel)) } }
        val label = labelBounds()
        val pointTop = pointBounds("Nov", fraction = 1f).top
        assertTrue("label bottom ${label.bottom} should be above the point top $pointTop", label.bottom <= pointTop)
        assertTrue("label bottom ${label.bottom} should be just above the point top $pointTop", pointTop - label.bottom <= 3.dp)
    }

    @Test
    fun entriesBelowMinValueAreDrawnAtTheBottomOfThePlot() {
        // Every entry is below minValue: the points are at the bottom, far under the label, which
        // stays on the target line at mid-height.
        compose.setContent { JengaTheme { Chart(minValue = 95f, target = JengaChartTarget(95f, WideLabel)) } }
        val label = labelBounds()
        val plotBottom = compose.onNodeWithContentDescription("Nov").getUnclippedBoundsInRoot().top + PlotHeight
        assertDpEquals(PlotHeight / 2 + 0.5.dp + 2.dp, plotBottom - label.bottom, tolerance = 1f)
    }

    // ---- Emphasis ----------------------------------------------------------

    @Test
    fun theEmphasisedPointIsTheSelectedOneOrElseTheLatest() {
        assertEquals(5, emphasisedPointIndex(selectedIndex = null, count = 6))
        assertEquals(2, emphasisedPointIndex(selectedIndex = 2, count = 6))
        assertEquals(0, emphasisedPointIndex(selectedIndex = 0, count = 6))
        assertEquals(0, emphasisedPointIndex(selectedIndex = null, count = 1))
        assertNull(emphasisedPointIndex(selectedIndex = null, count = 0))
        // A selection that is not a point emphasises nothing, rather than a second point.
        assertNull(emphasisedPointIndex(selectedIndex = 6, count = 6))
        assertNull(emphasisedPointIndex(selectedIndex = -1, count = 6))
    }

    // ---- Edge cases --------------------------------------------------------

    @Test
    fun aChartWithNoEntriesKeepsItsPlotAndItsTarget() {
        compose.setContent {
            JengaTheme {
                JengaLineChart(
                    entries = emptyList(),
                    pointContentDescription = { it.label },
                    modifier = Modifier.testTag("empty").width(320.dp),
                    plotHeight = 100.dp,
                )
                JengaLineChart(
                    entries = emptyList(),
                    pointContentDescription = { it.label },
                    modifier = Modifier.testTag("target").width(320.dp),
                    target = JengaChartTarget(10f, "Target"),
                    plotHeight = 100.dp,
                )
            }
        }
        assertDpEquals(100.dp, compose.onNodeWithTag("empty").getUnclippedBoundsInRoot().height)
        assertTrue(compose.onNodeWithTag("target").getUnclippedBoundsInRoot().height >= 100.dp)
        compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).assertExists()
    }

    @Test
    fun aChartWithOnePointComposesWithThePointCentred() {
        compose.setContent { JengaTheme { Chart(entries = entries.take(1)) } }
        val slot = compose.onNodeWithContentDescription("Jun").getUnclippedBoundsInRoot()
        assertDpEquals(0.dp, slot.left)
        assertDpEquals(320.dp, slot.width)
    }

    @Test
    fun equalAndNegativeValuesCompose() {
        compose.setContent {
            JengaTheme {
                Chart(entries = entries.map { JengaLineChartEntry(it.label, 80f) })
                Chart(
                    entries = listOf(JengaLineChartEntry("X", -12f), JengaLineChartEntry("Y", -3f), JengaLineChartEntry("Z", 4f)),
                    target = JengaChartTarget(0f),
                )
            }
        }
        compose.onNodeWithContentDescription("Nov").assertExists()
        compose.onNodeWithContentDescription("X").assertExists()
    }

    @Test
    fun aValueThatIsNotANumberIsRejected() = assertRejected(entries + JengaLineChartEntry("Bad", Float.NaN))

    @Test
    fun anInfiniteValueIsRejected() = assertRejected(entries + JengaLineChartEntry("Bad", Float.POSITIVE_INFINITY))

    @Test
    fun aNegativeInfiniteValueIsRejected() = assertRejected(entries + JengaLineChartEntry("Bad", Float.NEGATIVE_INFINITY))

    @Test
    fun anInfiniteTargetIsRejected() = assertRejected(entries, target = JengaChartTarget(Float.POSITIVE_INFINITY))

    @Test
    fun aMinValueAboveMaxValueIsRejected() = assertRejected(entries, minValue = 90f, maxValue = 60f)

    @Test
    fun aMinValueThatIsNotANumberIsRejected() = assertRejected(entries, minValue = Float.NaN)

    @Test
    fun anInfiniteMinValueIsRejected() = assertRejected(entries, minValue = Float.NEGATIVE_INFINITY)

    @Test
    fun aMaxValueThatIsNotANumberIsRejected() = assertRejected(entries, maxValue = Float.NaN)

    @Test
    fun anInfiniteMaxValueIsRejected() = assertRejected(entries, maxValue = Float.POSITIVE_INFINITY)

    @Test
    fun aNegativeGridlineCountIsRejected() = assertRejected(entries, gridlineCount = -1)

    private fun assertRejected(
        entries: List<JengaLineChartEntry>,
        minValue: Float? = null,
        maxValue: Float? = null,
        target: JengaChartTarget? = null,
        gridlineCount: Int = JengaLineChartDefaults.GridlineCount,
    ) {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setContent {
                JengaTheme {
                    Chart(
                        entries = entries,
                        minValue = minValue,
                        maxValue = maxValue,
                        target = target,
                        gridlineCount = gridlineCount,
                    )
                }
            }
            compose.waitForIdle()
        }
    }

    // ---- Semantics ---------------------------------------------------------

    @Test
    fun tappingAPointSelectsIt() {
        var tapped = -1
        compose.setContent { JengaTheme { Chart(selectedIndex = 2, onSelect = { tapped = it }) } }
        compose.onNodeWithContentDescription("Aug").assertIsSelected()
        compose.onNodeWithContentDescription("Nov").assertIsNotSelected()
        compose.onNodeWithContentDescription("Sep").performClick()
        assertEquals(3, tapped)
    }

    @Test
    fun theLatestPointIsNotAnnouncedAsSelectedWithoutASelection() {
        compose.setContent { JengaTheme { Chart(onSelect = {}) } }
        compose.onNodeWithContentDescription("Nov").assertIsNotSelected()
    }

    @Test
    fun aStaticChartHasDescribedPointsThatAreNotSelectable() {
        compose.setContent { JengaTheme { Chart() } }
        compose.onNodeWithContentDescription("Nov")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
    }

    @Test
    fun theDescriptionIsOnTheNodeTheModifierIsOnWithAndWithoutATarget() {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("plain"), contentDescription = "Customers paying on time")
                Chart(
                    modifier = Modifier.testTag("target"),
                    contentDescription = "Customers paying on time",
                    target = JengaChartTarget(80f, label = "Target", contentDescription = "Target 80%"),
                )
                Chart(modifier = Modifier.testTag("none"))
            }
        }
        compose.onNodeWithTag("plain").assertContentDescriptionEquals("Customers paying on time")
        compose.onNodeWithTag("target").assertContentDescriptionEquals("Customers paying on time, Target 80%")
        compose.onNodeWithTag("none").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        // The label on the line is not read a second time.
        compose.onNodeWithText("Target").assertDoesNotExist()
    }

    @Test
    fun anEmptyTargetLabelShowsNoTextAndTakesNoRoom() {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("none"), target = JengaChartTarget(90f))
                Chart(modifier = Modifier.testTag("empty"), target = JengaChartTarget(90f, label = ""))
            }
        }
        compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).assertDoesNotExist()
        assertDpEquals(
            compose.onNodeWithTag("none").getUnclippedBoundsInRoot().height,
            compose.onNodeWithTag("empty").getUnclippedBoundsInRoot().height,
        )
        compose.onNodeWithTag("empty").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }

    // ---- Layout ------------------------------------------------------------

    @Test
    fun everyPointOfSixOnA360dpChartIsAFullSizeTapTarget() {
        compose.setContent {
            JengaTheme {
                JengaLineChart(
                    entries = entries,
                    pointContentDescription = { it.label },
                    modifier = Modifier.width(360.dp),
                    onSelect = {},
                )
            }
        }
        for (entry in entries) {
            val slot = compose.onNodeWithContentDescription(entry.label).getUnclippedBoundsInRoot()
            assertTrue("${entry.label} is ${slot.width} wide", slot.width >= 48.dp)
            assertTrue("${entry.label} is ${slot.height} tall", slot.height >= 48.dp)
        }
    }

    @Test
    fun pointsShareTheWidthEquallyInOrder() {
        compose.setContent { JengaTheme { Chart() } }
        val first = compose.onNodeWithContentDescription("Jun").getUnclippedBoundsInRoot()
        val last = compose.onNodeWithContentDescription("Nov").getUnclippedBoundsInRoot()
        assertDpEquals(0.dp, first.left)
        assertDpEquals(320.dp, last.right)
        // 320dp between six: 53 or 54dp each.
        assertDpEquals(first.width, last.width, tolerance = 1f)
    }

    @Test
    fun theFirstPointIsAtTheRightInARightToLeftLayout() {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Chart(target = JengaChartTarget(90f, "Target"))
                }
            }
        }
        val first = compose.onNodeWithContentDescription("Jun").getUnclippedBoundsInRoot()
        val last = compose.onNodeWithContentDescription("Nov").getUnclippedBoundsInRoot()
        assertDpEquals(320.dp, first.right)
        assertDpEquals(0.dp, last.left)
        // The target's label moves to the other end with the latest point.
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertDpEquals(0.dp, label.left)
    }

    // ---- Target label ------------------------------------------------------

    @Test
    fun theLabelSitsOnTheLineWhenTheSeriesIsBelowIt() {
        compose.setContent { JengaTheme { Chart(target = JengaChartTarget(100f, "Target")) } }
        // The target (100) is the top of the scale, 93dp above the plot's bottom; the label is 2dp
        // above the 1dp line.
        val label = labelBounds()
        val plotBottom = compose.onNodeWithContentDescription("Nov").getUnclippedBoundsInRoot().top + PlotHeight
        assertDpEquals(PlotHeight - Inset + 0.5.dp + 2.dp, plotBottom - label.bottom, tolerance = 1f)
    }

    @Test
    fun theLabelIsClearOfTheEmphasisedPointWhenTheSeriesEndsAboveTheTarget() {
        compose.setContent { JengaTheme { Chart(target = JengaChartTarget(80f, WideLabel)) } }
        assertLabelClearOfPoints(low = 72f, high = 86f)
        // The latest point is the top of the scale: the label is lifted above it, not left on the line.
        val label = labelBounds()
        val lastPointTop = pointBounds("Nov", fraction = 1f).top
        assertTrue("label bottom ${label.bottom} should be above the point top $lastPointTop", label.bottom <= lastPointTop)
        assertTrue(lastPointTop - label.bottom <= 3.dp)
    }

    @Test
    fun theLabelIsClearOfThePointsRightToLeft() {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Chart(target = JengaChartTarget(80f, WideLabel))
                }
            }
        }
        assertLabelClearOfPoints(low = 72f, high = 86f)
    }

    @Test
    fun theLabelIsClearOfAPointSelectedUnderIt() {
        // The series ends below the target, but the selected last point is drawn larger.
        compose.setContent {
            JengaTheme { Chart(selectedIndex = 5, onSelect = {}, target = JengaChartTarget(87f, WideLabel)) }
        }
        assertLabelClearOfPoints(low = 72f, high = 87f)
    }

    @Test
    fun theLabelNeverLeavesTheChartWhenItIsLiftedAboveThePlot() {
        compose.setContent {
            JengaTheme { Chart(modifier = Modifier.testTag("chart"), target = JengaChartTarget(80f, WideLabel)) }
        }
        val chart = compose.onNodeWithTag("chart").getUnclippedBoundsInRoot()
        val label = labelBounds()
        assertTrue("label top ${label.top} is above the chart top ${chart.top}", label.top >= chart.top - 0.5.dp)
    }

    @Test
    fun aHeightThatFitsHoldsTheTargetLabel() = assertBoundedHeightHoldsTheLabel(240.dp)

    @Test
    fun aHeightThatIsTooSmallIsKeptAndStillHoldsTheTargetLabel() = assertBoundedHeightHoldsTheLabel(110.dp)

    // Unbounded, this chart is the 100dp plot, the target label lifted above the last point at
    // the plot's top, and the point labels below.
    private fun assertBoundedHeightHoldsTheLabel(height: Dp) {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("chart").height(height), target = JengaChartTarget(80f, WideLabel))
            }
        }
        val chart = compose.onNodeWithTag("chart").getUnclippedBoundsInRoot()
        val label = labelBounds()
        // The chart keeps to the height it was given and the label stays inside it.
        assertDpEquals(height, chart.height)
        assertDpEquals(0.dp, chart.top)
        assertTrue("label top ${label.top} is above the chart top", label.top >= chart.top - 0.5.dp)
        assertTrue(label.bottom <= chart.bottom)
    }

    @Test
    fun theSeriesUnderTheLabelIsMeasuredFromTheEndEdge() {
        // Four points 100px apart (at 50, 150, 250 and 350) with radius 5 and a line 2 thick.
        val heights = listOf(10f, 90f, 30f, 50f)
        fun top(labelWidth: Float) = seriesTopUnderTargetLabel(
            pointHeights = heights,
            pointRadius = 5f,
            lineHalfWidth = 1f,
            plotWidth = 400f,
            labelWidth = labelWidth,
        )
        // Nothing under a label that stops short of the last point's edge.
        assertEquals(0f, top(0f))
        assertEquals(0f, top(40f))
        // The last point: its top edge.
        assertEquals(55f, top(50f))
        // Halfway along the falling line from 30 to 50: the last point is still the highest.
        assertEquals(55f, top(100f))
        // Up to the third point; then the line rising to the second point comes under the label.
        assertEquals(55f, top(150f))
        val slope = (30f - 90f) / 100f
        assertEquals(60f + sqrt(1f + slope * slope), top(200f), 0.001f)
        // The second point itself, then everything.
        assertEquals(95f, top(250f))
        assertEquals(95f, top(400f))
        assertEquals(95f, top(1000f))
        // An unbounded width counts every point; no points, nothing.
        assertEquals(
            95f,
            seriesTopUnderTargetLabel(heights, pointRadius = 5f, lineHalfWidth = 1f, plotWidth = Float.POSITIVE_INFINITY, labelWidth = 40f),
        )
        assertEquals(0f, seriesTopUnderTargetLabel(emptyList(), pointRadius = 5f, lineHalfWidth = 1f, plotWidth = 400f, labelWidth = 40f))
    }

    // ---- Helpers -----------------------------------------------------------

    private val PlotHeight = 100.dp

    // Robolectric measures text at 1px a character: wide enough to reach over the last point,
    // whose slot is 53dp wide, as a real "Target" label does.
    private val WideLabel = "Target".repeat(7)

    // The default selected point radius: points keep this far from the plot's top and bottom.
    private val Inset = 7.dp

    private fun range(
        entriesMin: Float? = null,
        entriesMax: Float? = null,
        minValue: Float? = null,
        maxValue: Float? = null,
        target: Float? = null,
    ) = lineChartRange(entriesMin, entriesMax, minValue, maxValue, target)

    private fun labelBounds() = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()

    /** Bounds of the point of the entry labelled [label], drawn at the larger (emphasised) radius. */
    private fun pointBounds(label: String, fraction: Float): PointBounds {
        val slot = compose.onNodeWithContentDescription(label).getUnclippedBoundsInRoot()
        val centerX = (slot.left + slot.right) / 2
        val centerY = slot.top + PlotHeight - (Inset + (PlotHeight - Inset * 2) * fraction)
        return PointBounds(left = centerX - Inset, top = centerY - Inset, right = centerX + Inset, bottom = centerY + Inset)
    }

    private class PointBounds(val left: Dp, val top: Dp, val right: Dp, val bottom: Dp)

    /**
     * Asserts the target label does not intersect the bounds of any point, each taken at the
     * emphasised radius, on a scale of [low]..[high], and that it is over at least one of them.
     */
    private fun assertLabelClearOfPoints(low: Float, high: Float) {
        val label = labelBounds()
        var under = 0
        for (entry in entries) {
            val point = pointBounds(entry.label, lineChartFraction(entry.value, low, high))
            val overlapsHorizontally = point.left < label.right && label.left < point.right
            val overlapsVertically = point.top < label.bottom && label.top < point.bottom
            if (overlapsHorizontally) under++
            assertTrue(
                "label (${label.left}, ${label.top}, ${label.right}, ${label.bottom}) covers the point of ${entry.label}",
                !(overlapsHorizontally && overlapsVertically),
            )
        }
        assertTrue("the label should be over at least one point", under > 0)
    }

    @Composable
    private fun Chart(
        modifier: Modifier = Modifier,
        entries: List<JengaLineChartEntry> = this.entries,
        contentDescription: String? = null,
        selectedIndex: Int? = null,
        onSelect: ((Int) -> Unit)? = null,
        minValue: Float? = null,
        maxValue: Float? = null,
        target: JengaChartTarget? = null,
        gridlineCount: Int = JengaLineChartDefaults.GridlineCount,
    ) {
        JengaLineChart(
            entries = entries,
            pointContentDescription = { it.label },
            modifier = modifier.width(320.dp),
            contentDescription = contentDescription,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            minValue = minValue,
            maxValue = maxValue,
            target = target,
            gridlineCount = gridlineCount,
            plotHeight = PlotHeight,
        )
    }

    private fun assertDpEquals(expected: Dp, actual: Dp, tolerance: Float = 0.5f) {
        assertTrue("expected $expected but was $actual", abs(expected.value - actual.value) <= tolerance)
    }
}
