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
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@RunWith(RobolectricTestRunner::class)
class JengaBarChartTest {

    @get:Rule
    val compose = createComposeRule()

    private val entries = listOf(
        JengaBarChartEntry("A", 50f),
        JengaBarChartEntry("B", 100f),
        JengaBarChartEntry("C", 0f),
        JengaBarChartEntry("D", 25f),
    )

    // ---- Colors ------------------------------------------------------------

    @Test
    fun defaultColorsKeepTheirTokensAndDrawNoRing() {
        compose.setContent {
            JengaTheme {
                val c = JengaTheme.colors
                val colors = JengaBarChartDefaults.colors()
                assertEquals(c.brand, colors.bar)
                assertEquals(c.brand, colors.selectedBar)
                assertEquals(c.borderStrong, colors.unselectedBar)
                assertEquals(c.textSecondary, colors.label)
                assertEquals(c.textPrimary, colors.selectedLabel)
                assertEquals(c.brand, colors.highlightedBar)
                assertEquals(Color.Transparent, colors.selectedOutline)
            }
        }
    }

    @Test
    fun highlightColorsMeetThreeToOneOnTheSurfaceInLightAndDark() {
        var light: Pair<JengaColors, JengaBarChartColors>? = null
        var dark: Pair<JengaColors, JengaBarChartColors>? = null
        compose.setContent {
            JengaTheme(darkTheme = false) { light = JengaTheme.colors to JengaBarChartDefaults.highlightColors() }
            JengaTheme(darkTheme = true) { dark = JengaTheme.colors to JengaBarChartDefaults.highlightColors() }
        }
        compose.waitForIdle()
        for ((scheme, colors) in listOf(light!!, dark!!)) {
            assertEquals(scheme.textMuted, colors.bar)
            assertEquals(scheme.textMuted, colors.selectedBar)
            assertEquals(scheme.textMuted, colors.unselectedBar)
            assertEquals(scheme.brand, colors.highlightedBar)
            assertEquals(scheme.borderStrong, colors.emptyBar)
            assertEquals(scheme.textPrimary, colors.selectedOutline)
            assertEquals(scheme.textMuted, colors.targetLine)
            assertEquals(scheme.textMuted, colors.targetLabel)
            listOf(
                "bar" to colors.bar,
                "highlightedBar" to colors.highlightedBar,
                "selectedOutline" to colors.selectedOutline,
                "targetLine" to colors.targetLine,
            ).forEach { (name, color) ->
                val ratio = contrast(color, scheme.surface)
                assertTrue("$name on surface is $ratio:1, below 3:1 (light = ${scheme.isLight})", ratio >= 3.0)
            }
            // The target label is text: AA 4.5:1.
            assertTrue(contrast(colors.targetLabel, scheme.surface) >= 4.5)
        }
    }

    @Test
    fun colorsBuiltWithoutTheNewSlotsFallBackToTheOldOnes() {
        val colors = JengaBarChartColors(
            bar = Color.Red,
            selectedBar = Color.Green,
            unselectedBar = Color.Blue,
            label = Color.Gray,
            selectedLabel = Color.Black,
        )
        assertEquals(Color.Red, colors.highlightedBar)
        assertEquals(Color.Blue, colors.emptyBar)
        assertEquals(Color.Transparent, colors.selectedOutline)
        assertEquals(Color.Gray, colors.targetLine)
        assertEquals(Color.Gray, colors.targetLabel)
        assertEquals(Color.Yellow, colors.copy(selectedOutline = Color.Yellow).selectedOutline)
        assertEquals(colors, colors.copy())
    }

    // ---- Scale -------------------------------------------------------------

    @Test
    fun scaleWithoutATargetIsMaxValueOrTheLargestEntry() {
        assertEquals(100f, chartScaleMax(entriesMax = 100f, maxValue = null, target = null))
        assertEquals(150f, chartScaleMax(entriesMax = 100f, maxValue = 150f, target = null))
        assertEquals(80f, chartScaleMax(entriesMax = 100f, maxValue = 80f, target = null))
        assertEquals(0f, chartScaleMax(entriesMax = null, maxValue = null, target = null))
    }

    @Test
    fun scaleGrowsToATargetAboveItAndIgnoresALowerOne() {
        assertEquals(100f, chartScaleMax(entriesMax = 100f, maxValue = null, target = 40f))
        assertEquals(200f, chartScaleMax(entriesMax = 100f, maxValue = null, target = 200f))
        assertEquals(200f, chartScaleMax(entriesMax = 100f, maxValue = 150f, target = 200f))
        assertEquals(150f, chartScaleMax(entriesMax = 100f, maxValue = 150f, target = 120f))
        assertEquals(40f, chartScaleMax(entriesMax = null, maxValue = null, target = 40f))
    }

    @Test
    fun aTargetBelowTheTallestBarLeavesBarHeightsAlone() {
        compose.setContent { JengaTheme { Chart(target = JengaChartTarget(60f, "Target")) } }
        // B is full height (100dp) and A half of it.
        assertDpEquals(50.dp, slotHeight("B") - slotHeight("A"))
        assertDpEquals(100.dp, slotHeight("B") - slotHeight("C"))
    }

    @Test
    fun aTargetAboveEveryBarScalesTheBarsDown() {
        compose.setContent { JengaTheme { Chart(target = JengaChartTarget(200f, "Target")) } }
        // The target (200) is now full height, so B (100) is 50dp and A (50) is 25dp.
        assertDpEquals(25.dp, slotHeight("B") - slotHeight("A"))
        assertDpEquals(50.dp, slotHeight("B") - slotHeight("C"))
    }

    @Test
    fun aTargetAboveMaxValueScalesTheBarsDown() {
        compose.setContent { JengaTheme { Chart(maxValue = 100f, target = JengaChartTarget(200f)) } }
        assertDpEquals(50.dp, slotHeight("B") - slotHeight("C"))
    }

    @Test
    fun theChartGrowsToHoldATargetAboveEveryBarAndItsLabel() {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("plain"))
                Chart(modifier = Modifier.testTag("target"), target = JengaChartTarget(200f, "Target"))
            }
        }
        // Without a target the chart hugs its tallest bar (100dp). With the target the tallest bar
        // is 50dp, the line is 100dp above the baseline and the label sits above the line.
        val plain = compose.onNodeWithTag("plain").getUnclippedBoundsInRoot().height
        val withTarget = compose.onNodeWithTag("target").getUnclippedBoundsInRoot().height
        assertTrue("chart with a target ($withTarget) should be taller than without ($plain)", withTarget > plain)
    }

    @Test
    fun anEmptyEntryIsAStubWhateverItsValue() {
        compose.setContent { JengaTheme { Chart(emptyIndices = setOf(1), emptyBarHeight = 6.dp) } }
        // B (100, the largest) is empty: a 6dp stub. A (50) is still half the plot: 50dp.
        assertDpEquals(44.dp, slotHeight("A") - slotHeight("B"))
        assertDpEquals(6.dp, slotHeight("B") - slotHeight("C"))
    }

    // ---- Semantics ---------------------------------------------------------

    @Test
    fun theChartDescriptionIncludesTheTarget() {
        compose.setContent {
            JengaTheme {
                Chart(
                    contentDescription = "Collections this week",
                    target = JengaChartTarget(60f, label = "Target", contentDescription = "Target 40,000"),
                )
            }
        }
        compose.onNodeWithContentDescription("Collections this week, Target 40,000").assertExists()
        // The label on the line is not read a second time.
        compose.onNodeWithText("Target").assertDoesNotExist()
        compose.onNodeWithContentDescription("B").assertExists()
    }

    @Test
    fun theTargetLabelIsItsDescriptionByDefault() {
        compose.setContent { JengaTheme { Chart(target = JengaChartTarget(60f, label = "Target")) } }
        compose.onNodeWithContentDescription("Target").assertExists()
    }

    @Test
    fun aChartWithoutDescriptionsHasNone() {
        assertNull(chartDescription(null, null))
        assertNull(chartDescription(null, JengaChartTarget(60f)))
        assertEquals("Sales", chartDescription("Sales", JengaChartTarget(60f)))
        compose.setContent { JengaTheme { Chart(modifier = Modifier.testTag("chart")) } }
        compose.onNodeWithTag("chart").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }

    @Test
    fun highlightDoesNotChangeWhichBarIsSelected() {
        compose.setContent {
            JengaTheme {
                Chart(
                    selectedIndex = 1,
                    onSelect = {},
                    highlightedIndices = setOf(3),
                    colors = JengaBarChartDefaults.highlightColors(),
                )
            }
        }
        compose.onNodeWithContentDescription("B").assertIsSelected()
        compose.onNodeWithContentDescription("D").assertIsNotSelected()
    }

    @Test
    fun anEmptyBarCanBeSelected() {
        var tapped = -1
        compose.setContent {
            JengaTheme {
                Chart(
                    onSelect = { tapped = it },
                    emptyIndices = setOf(2, 3),
                    target = JengaChartTarget(60f, "Target"),
                    colors = JengaBarChartDefaults.highlightColors(),
                )
            }
        }
        compose.onNodeWithContentDescription("D").performClick()
        assertEquals(3, tapped)
    }

    @Test
    fun aScrollingChartWithATargetComposes() {
        compose.setContent {
            JengaTheme {
                Chart(
                    contentDescription = "Sales",
                    target = JengaChartTarget(200f, "Target"),
                    arrangement = JengaBarChartArrangement.Scroll(slotWidth = 48.dp),
                )
            }
        }
        compose.onNodeWithContentDescription("Sales, Target").assertExists()
        assertDpEquals(25.dp, slotHeight("B") - slotHeight("A"))
    }

    @Test
    fun aChartWithNoEntriesAndATargetComposes() {
        compose.setContent {
            JengaTheme {
                JengaBarChart(
                    entries = emptyList(),
                    barContentDescription = { it.label },
                    modifier = Modifier.testTag("chart"),
                    target = JengaChartTarget(10f, "Target"),
                )
            }
        }
        compose.onNodeWithTag("chart").assertExists()
    }

    // ---- Target label ------------------------------------------------------

    @Test
    fun theLabelSitsAboveALastBarThatPassesTheTarget() {
        compose.setContent { JengaTheme { LabelChart(last = 90f) } }
        assertLabelClearOfBars()
        // The last bar is 90dp tall and the line 60dp above the baseline: the label is above the bar.
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        val lastBarTop = compose.onNodeWithContentDescription("E").getUnclippedBoundsInRoot().top
        assertTrue("label bottom ${label.bottom} should be above the bar top $lastBarTop", label.bottom <= lastBarTop)
        assertDpEquals(2.dp, lastBarTop - label.bottom)
    }

    @Test
    fun theLabelStaysOnTheLineWhenTheLastBarOnlyReachesIt() {
        compose.setContent {
            JengaTheme {
                LabelChart(last = 60f, modifier = Modifier.testTag("at"))
                LabelChart(last = 10f, modifier = Modifier.testTag("below"))
            }
        }
        // Same place as with a short last bar: the chart is the same height either way.
        assertDpEquals(
            compose.onNodeWithTag("below").getUnclippedBoundsInRoot().height,
            compose.onNodeWithTag("at").getUnclippedBoundsInRoot().height,
        )
    }

    @Test
    fun theLabelIsClearOfBarsWhenTheLastBarIsAtTheTarget() {
        compose.setContent { JengaTheme { LabelChart(last = 60f) } }
        assertLabelClearOfBars()
    }

    @Test
    fun theLabelIsClearOfBarsRightToLeft() {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) { LabelChart(last = 90f) }
            }
        }
        assertLabelClearOfBars()
        // The line ends at the left in RTL, over the last bar.
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertDpEquals(0.dp, label.left)
    }

    @Test
    fun theLabelIsClearOfBarsWithARingColor() {
        compose.setContent {
            JengaTheme { LabelChart(last = 90f, selectedIndex = 4, colors = JengaBarChartDefaults.highlightColors()) }
        }
        assertLabelClearOfBars()
    }

    @Test
    fun theLabelIsClearOfEveryBarInAScrollingChart() {
        compose.setContent {
            JengaTheme { LabelChart(last = 20f, arrangement = JengaBarChartArrangement.Scroll(slotWidth = 60.dp)) }
        }
        // The last slot is partly under the label. B (100) passes the target and is not under the
        // label, but could scroll under it.
        assertLabelClearOfBars()
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        val tallestTop = compose.onNodeWithContentDescription("B").getUnclippedBoundsInRoot().top
        assertTrue(label.bottom <= tallestTop)
    }

    @Test
    fun slotsUnderTheLabelAreCountedFromTheEnd() {
        // Five 56px slots with 10px between them: a slot starts every 66px from the end edge.
        assertEquals(1, slotsUnderTargetLabel(slotCount = 5, plotWidth = 320f, spacing = 10f, labelWidth = 40f))
        assertEquals(1, slotsUnderTargetLabel(slotCount = 5, plotWidth = 320f, spacing = 10f, labelWidth = 66f))
        assertEquals(2, slotsUnderTargetLabel(slotCount = 5, plotWidth = 320f, spacing = 10f, labelWidth = 67f))
        assertEquals(5, slotsUnderTargetLabel(slotCount = 5, plotWidth = 320f, spacing = 10f, labelWidth = 1000f))
        assertEquals(0, slotsUnderTargetLabel(slotCount = 0, plotWidth = 320f, spacing = 10f, labelWidth = 40f))
        assertEquals(0, slotsUnderTargetLabel(slotCount = 5, plotWidth = 320f, spacing = 10f, labelWidth = 0f))
    }

    @Test
    fun anEmptyTargetLabelShowsNoTextAndTakesNoRoom() {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("none"), target = JengaChartTarget(200f))
                Chart(modifier = Modifier.testTag("empty"), target = JengaChartTarget(200f, label = ""))
            }
        }
        compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).assertDoesNotExist()
        assertDpEquals(
            compose.onNodeWithTag("none").getUnclippedBoundsInRoot().height,
            compose.onNodeWithTag("empty").getUnclippedBoundsInRoot().height,
        )
        compose.onNodeWithTag("empty").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }

    // ---- Modifier and constraints ------------------------------------------

    @Test
    fun theDescriptionIsOnTheNodeTheModifierIsOnWithAndWithoutATarget() {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("plain"), contentDescription = "Sales")
                Chart(
                    modifier = Modifier.testTag("target"),
                    contentDescription = "Sales",
                    target = JengaChartTarget(60f, label = "Target"),
                )
            }
        }
        compose.onNodeWithTag("plain").assertContentDescriptionEquals("Sales")
        compose.onNodeWithTag("target").assertContentDescriptionEquals("Sales, Target")
    }

    @Test
    fun aHeightThatFitsHoldsTheTargetLabel() = assertBoundedHeightHoldsTheLabel(150.dp)

    @Test
    fun aHeightThatIsTooSmallIsKeptAndStillHoldsTheTargetLabel() = assertBoundedHeightHoldsTheLabel(120.dp)

    // Unbounded, this chart is about 141dp tall: 100dp to the line, the label above it and the
    // bar labels below.
    private fun assertBoundedHeightHoldsTheLabel(height: Dp) {
        compose.setContent {
            JengaTheme {
                Chart(modifier = Modifier.testTag("chart").height(height), target = JengaChartTarget(200f, "Target"))
            }
        }
        val chart = compose.onNodeWithTag("chart").getUnclippedBoundsInRoot()
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        // The chart keeps to the height it was given and the label stays inside it.
        assertDpEquals(height, chart.height)
        assertDpEquals(0.dp, chart.top)
        assertTrue("label top ${label.top} is above the chart top", label.top >= chart.top - 0.5.dp)
        assertTrue(label.bottom <= chart.bottom)
    }

    @Test
    fun aPlotShorterThanTheEmptyStubStillComposes() {
        compose.setContent {
            JengaTheme {
                JengaBarChart(
                    entries = entries,
                    barContentDescription = { it.label },
                    plotHeight = 2.dp,
                    emptyIndices = setOf(0),
                )
            }
        }
        // The stub is clamped to the plot: no taller than the full-height bar B.
        assertDpEquals(0.dp, slotHeight("B") - slotHeight("A"))
    }

    // ---- Helpers -----------------------------------------------------------

    /** Five bars on a 0..100 scale that is 100dp tall, with a labelled target at 60. */
    @Composable
    private fun LabelChart(
        last: Float,
        modifier: Modifier = Modifier,
        selectedIndex: Int? = null,
        arrangement: JengaBarChartArrangement = JengaBarChartArrangement.Fill,
        colors: JengaBarChartColors = JengaBarChartDefaults.colors(),
    ) {
        JengaBarChart(
            entries = entries + JengaBarChartEntry("E", last),
            barContentDescription = { it.label },
            modifier = modifier.width(320.dp),
            selectedIndex = selectedIndex,
            onSelect = {},
            maxValue = 100f,
            target = JengaChartTarget(60f, label = "Target"),
            arrangement = arrangement,
            plotHeight = 100.dp,
            colors = colors,
        )
    }

    /**
     * Asserts the target label is above every bar slot it overlaps horizontally. A slot starts at
     * the top of its bar (or of the room kept for the ring), so this is at least as strict as
     * comparing with the bars themselves.
     */
    private fun assertLabelClearOfBars() {
        val label = compose.onNodeWithTag(ChartTargetLabelTestTag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        var checked = 0
        for (name in listOf("A", "B", "C", "D", "E")) {
            val slot = compose.onNodeWithContentDescription(name).getUnclippedBoundsInRoot()
            val overlapsHorizontally = slot.left < label.right && label.left < slot.right
            if (overlapsHorizontally) {
                checked++
                assertTrue("label (bottom ${label.bottom}) overlaps bar $name (top ${slot.top})", label.bottom <= slot.top)
            }
        }
        assertTrue("the label should be over at least one bar slot", checked > 0)
    }

    @Composable
    private fun Chart(
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
        selectedIndex: Int? = null,
        onSelect: ((Int) -> Unit)? = null,
        highlightedIndices: Set<Int> = emptySet(),
        emptyIndices: Set<Int> = emptySet(),
        maxValue: Float? = null,
        target: JengaChartTarget? = null,
        arrangement: JengaBarChartArrangement = JengaBarChartArrangement.Fill,
        emptyBarHeight: Dp = JengaBarChartDefaults.EmptyBarHeight,
        colors: JengaBarChartColors = JengaBarChartDefaults.colors(),
    ) {
        JengaBarChart(
            entries = entries,
            barContentDescription = { it.label },
            modifier = modifier.width(320.dp),
            contentDescription = contentDescription,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            highlightedIndices = highlightedIndices,
            emptyIndices = emptyIndices,
            maxValue = maxValue,
            target = target,
            arrangement = arrangement,
            plotHeight = 100.dp,
            emptyBarHeight = emptyBarHeight,
            colors = colors,
        )
    }

    /** Height of a bar's slot: its bar plus its label, and every label here is the same height. */
    private fun slotHeight(description: String): Dp = compose.onNodeWithContentDescription(description).getUnclippedBoundsInRoot().height

    private fun assertDpEquals(expected: Dp, actual: Dp) {
        assertTrue("expected $expected but was $actual", abs(expected.value - actual.value) <= 0.5f)
    }

    private fun contrast(a: Color, b: Color): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        return (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)
    }

    private fun luminance(color: Color): Double {
        fun channel(v: Float): Double {
            val d = v.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
    }
}
