package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaStepIndicatorTest {

    @get:Rule
    val compose = createComposeRule()

    private val steps = listOf(
        JengaStep("Device", JengaStepState.Done),
        JengaStep("Payment", JengaStepState.Current),
        JengaStep("Verification", JengaStepState.Pending),
    )

    private fun show(onStepClick: ((Int) -> Unit)?, variant: JengaStepIndicatorVariant = JengaStepIndicatorVariant.Labelled) {
        compose.setContent {
            JengaTheme {
                JengaStepIndicator(
                    steps = steps,
                    stepContentDescription = { index, step -> "Step ${index + 1} of 3, ${step.label}" },
                    variant = variant,
                    onStepClick = onStepClick,
                )
            }
        }
    }

    @Test
    fun eachStepAnnouncesItsDescriptionAndOnlyTheCurrentOneIsSelected() {
        show(onStepClick = null)
        compose.onNodeWithContentDescription("Step 1 of 3, Device").assertIsNotSelected()
        compose.onNodeWithContentDescription("Step 2 of 3, Payment").assertIsSelected()
        compose.onNodeWithContentDescription("Step 3 of 3, Verification").assertIsNotSelected()
    }

    @Test
    fun withoutOnStepClickTheStepsAreNotTappable() {
        show(onStepClick = null)
        compose.onNodeWithContentDescription("Step 1 of 3, Device")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
    }

    @Test
    fun tappingAStepReportsItsIndex() {
        val taps = mutableListOf<Int>()
        show(onStepClick = { taps += it })
        compose.onNodeWithContentDescription("Step 3 of 3, Verification").performClick()
        compose.onNodeWithContentDescription("Step 1 of 3, Device").performClick()
        assertEquals(listOf(2, 0), taps)
    }

    @Test
    fun numberedStepsAnnounceTheSameDescriptionsAndSelection() {
        show(onStepClick = null, variant = JengaStepIndicatorVariant.Numbered)
        compose.onNodeWithContentDescription("Step 1 of 3, Device").assertIsNotSelected()
        compose.onNodeWithContentDescription("Step 2 of 3, Payment").assertIsSelected()
        compose.onNodeWithContentDescription("Step 3 of 3, Verification").assertIsNotSelected()
    }

    @Test
    fun numberedStepsDoNotExposeTheirNumbersOrLabelsAsText() {
        show(onStepClick = null, variant = JengaStepIndicatorVariant.Numbered)
        compose.onAllNodes(hasAnyText, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun tappingANumberedStepReportsItsIndex() {
        val taps = mutableListOf<Int>()
        show(onStepClick = { taps += it }, variant = JengaStepIndicatorVariant.Numbered)
        compose.onNodeWithContentDescription("Step 3 of 3, Verification").performClick()
        compose.onNodeWithContentDescription("Step 2 of 3, Payment").performClick()
        assertEquals(listOf(2, 1), taps)
    }

    @Test
    fun numberedStepsKeepTheBoundsOfLabelledSteps() {
        var variant by mutableStateOf(JengaStepIndicatorVariant.Labelled)
        compose.setContent {
            JengaTheme {
                JengaStepIndicator(
                    steps = steps,
                    stepContentDescription = { index, _ -> "Step ${index + 1}" },
                    variant = variant,
                )
            }
        }
        fun bounds() = (1..3).map { compose.onNodeWithContentDescription("Step $it").getUnclippedBoundsInRoot() }
        val labelled = bounds()
        variant = JengaStepIndicatorVariant.Numbered
        compose.waitForIdle()
        assertEquals(labelled, bounds())
    }

    @Test
    fun numberColorsDefaultToTheActiveAndLabelColors() {
        val colors = JengaStepIndicatorColors(
            active = Color.Red,
            onActive = Color.White,
            pending = Color.Gray,
            line = Color.LightGray,
            label = Color.DarkGray,
            currentLabel = Color.Black,
        )
        assertEquals(Color.Red, colors.currentNumber)
        assertEquals(Color.DarkGray, colors.pendingNumber)
    }

    @Test
    fun copyKeepsAndChangesTheNumberColors() {
        val colors = JengaStepIndicatorColors(
            active = Color.Red,
            onActive = Color.White,
            pending = Color.Gray,
            line = Color.LightGray,
            label = Color.DarkGray,
            currentLabel = Color.Black,
            currentNumber = Color.Blue,
            pendingNumber = Color.Green,
        )
        assertEquals(colors, colors.copy())
        assertEquals(Color.Blue, colors.copy(active = Color.Magenta).currentNumber)
        assertEquals(Color.Cyan, colors.copy(pendingNumber = Color.Cyan).pendingNumber)
    }

    private fun showTagged(variant: JengaStepIndicatorVariant, steps: () -> List<JengaStep> = { this.steps }, onStepClick: ((Int) -> Unit)? = null) {
        compose.setContent {
            JengaTheme {
                JengaStepIndicator(
                    steps = steps(),
                    stepContentDescription = { index, step -> "Step ${index + 1} of 3, ${step.label}" },
                    modifier = Modifier.testTag("indicator"),
                    variant = variant,
                    onStepClick = onStepClick,
                )
            }
        }
    }

    @Test
    fun compactRowIsOneNodeAndItsHaloTall() {
        showTagged(JengaStepIndicatorVariant.Compact)
        compose.onNodeWithTag("indicator").assertHeightIsEqualTo(JengaStepIndicatorDefaults.NodeSize + 3.dp * 2)
    }

    @Test
    fun compactRowIsShorterThanTheLabelledRow() {
        compose.setContent {
            JengaTheme {
                Column {
                    for (variant in listOf(JengaStepIndicatorVariant.Labelled, JengaStepIndicatorVariant.Compact)) {
                        JengaStepIndicator(
                            steps = steps,
                            stepContentDescription = { _, step -> step.label },
                            modifier = Modifier.testTag(variant.name),
                            variant = variant,
                        )
                    }
                }
            }
        }
        val labelled = compose.onNodeWithTag("Labelled").getUnclippedBoundsInRoot().height
        val compact = compose.onNodeWithTag("Compact").getUnclippedBoundsInRoot().height
        assertTrue("compact $compact, labelled $labelled", compact < labelled)
    }

    @Test
    fun compactRowKeepsItsHeightAsTheCurrentStepMoves() {
        var current by mutableIntStateOf(0)
        showTagged(
            variant = JengaStepIndicatorVariant.Compact,
            steps = {
                List(3) { index ->
                    JengaStep(
                        label = "Step $index",
                        state = when {
                            index < current -> JengaStepState.Done
                            index == current -> JengaStepState.Current
                            else -> JengaStepState.Pending
                        },
                    )
                }
            },
        )
        // 3 is past the last step: every step is done and no halo is drawn.
        for (step in 0..3) {
            current = step
            compose.waitForIdle()
            compose.onNodeWithTag("indicator").assertHeightIsEqualTo(JengaStepIndicatorDefaults.NodeSize + 3.dp * 2)
        }
    }

    @Test
    fun compactStepsAnnounceTheirDescriptionsAndReportTaps() {
        val taps = mutableListOf<Int>()
        showTagged(JengaStepIndicatorVariant.Compact, onStepClick = { taps += it })
        compose.onNodeWithContentDescription("Step 1 of 3, Device").assertIsNotSelected()
        compose.onNodeWithContentDescription("Step 2 of 3, Payment").assertIsSelected()
        compose.onNodeWithContentDescription("Step 3 of 3, Verification").performClick()
        compose.onNodeWithContentDescription("Step 1 of 3, Device").performClick()
        assertEquals(listOf(2, 0), taps)
    }

    @Test
    fun compactStepsDoNotExposeTheirNumbersOrLabelsAsText() {
        showTagged(JengaStepIndicatorVariant.Compact)
        compose.onAllNodes(hasAnyText, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun compactStepsAreOnlyAsTallAsTheNode() {
        showTagged(JengaStepIndicatorVariant.Compact)
        compose.onNodeWithContentDescription("Step 1 of 3, Device").assertHeightIsEqualTo(JengaStepIndicatorDefaults.NodeSize)
    }

    @Test
    fun compactStepsSitWhereLabelledStepsDo() {
        var variant by mutableStateOf(JengaStepIndicatorVariant.Labelled)
        compose.setContent {
            JengaTheme {
                JengaStepIndicator(
                    steps = steps,
                    stepContentDescription = { index, _ -> "Step ${index + 1}" },
                    variant = variant,
                )
            }
        }
        fun edges() = (1..3).map { compose.onNodeWithContentDescription("Step $it").getUnclippedBoundsInRoot().let { b -> b.left to b.right } }
        val labelled = edges()
        variant = JengaStepIndicatorVariant.Compact
        compose.waitForIdle()
        assertEquals(labelled, edges())
    }

    @Test
    fun haloColorDefaultsToTheActiveColorAndCopyKeepsIt() {
        val colors = JengaStepIndicatorColors(
            active = Color.Red,
            onActive = Color.White,
            pending = Color.Gray,
            line = Color.LightGray,
            label = Color.DarkGray,
            currentLabel = Color.Black,
        )
        assertEquals(Color.Red.copy(alpha = 0.2f), colors.currentHalo)
        assertEquals(colors, colors.copy())
        assertEquals(Color.Blue, colors.copy(currentHalo = Color.Blue).currentHalo)
        assertEquals(Color.Blue, colors.copy(currentHalo = Color.Blue).copy(active = Color.Green).currentHalo)
    }

    // Narrower than the 320dp test screen, so the row is not cut.
    private val rowWidth = 300.dp
    private var rowLabels by mutableStateOf(emptyList<String>())
    private var rowCurrent by mutableIntStateOf(0)

    private fun showRow(
        labels: List<String>,
        variant: JengaStepIndicatorVariant = JengaStepIndicatorVariant.Labelled,
        arrangement: JengaStepIndicatorArrangement = JengaStepIndicatorArrangement.EdgeToEdge,
        current: Int = 0,
        rtl: Boolean = false,
        onStepClick: ((Int) -> Unit)? = null,
    ) {
        rowLabels = labels
        rowCurrent = current
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    Box(modifier = Modifier.width(rowWidth)) {
                        JengaStepIndicator(
                            steps = rowLabels.mapIndexed { index, label ->
                                JengaStep(label, if (index == rowCurrent) JengaStepState.Current else JengaStepState.Pending)
                            },
                            stepContentDescription = { index, _ -> "Step ${index + 1}" },
                            modifier = Modifier.testTag("indicator"),
                            variant = variant,
                            arrangement = arrangement,
                            onStepClick = onStepClick,
                        )
                    }
                }
            }
        }
    }

    private fun assertEdgeToEdgeStepsAreSelectableAndTappable(variant: JengaStepIndicatorVariant) {
        val taps = mutableListOf<Int>()
        showRow(labels = longLabels.take(4), variant = variant, current = 1, onStepClick = { taps += it })
        compose.onNodeWithContentDescription("Step 1").assertIsNotSelected()
        compose.onNodeWithContentDescription("Step 2").assertIsSelected()
        compose.onNodeWithContentDescription("Step 4").assertIsNotSelected()
        compose.onNodeWithContentDescription("Step 4").performClick()
        compose.onNodeWithContentDescription("Step 1").performClick()
        compose.onNodeWithContentDescription("Step 3").performClick()
        assertEquals(listOf(3, 0, 2), taps)
    }

    @Test
    fun edgeToEdgeLabelledStepsExposeSelectionAndReportTaps() {
        assertEdgeToEdgeStepsAreSelectableAndTappable(JengaStepIndicatorVariant.Labelled)
    }

    @Test
    fun edgeToEdgeCompactStepsExposeSelectionAndReportTaps() {
        assertEdgeToEdgeStepsAreSelectableAndTappable(JengaStepIndicatorVariant.Compact)
    }

    @Test
    fun edgeToEdgeStepsAreNotTappableWithoutOnStepClick() {
        showRow(labels = longLabels.take(4))
        for (step in 1..4) {
            compose.onNodeWithContentDescription("Step $step").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        }
    }

    @Test
    fun edgeToEdgeStepsDoNotExposeTheirNumbersOrLabelsAsText() {
        showRow(labels = longLabels.take(4), variant = JengaStepIndicatorVariant.Numbered)
        compose.onAllNodes(hasAnyText, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun edgeToEdgeTakesTheLeastWidthInsideAHorizontalScroll() {
        compose.setContent {
            JengaTheme {
                Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    JengaStepIndicator(
                        steps = List(4) { JengaStep("Step", JengaStepState.Pending) },
                        stepContentDescription = { index, _ -> "Step ${index + 1}" },
                        modifier = Modifier.testTag("indicator"),
                        variant = JengaStepIndicatorVariant.Compact,
                        arrangement = JengaStepIndicatorArrangement.EdgeToEdge,
                    )
                }
            }
        }
        // Four nodes with the shortest line, 12dp, between each pair.
        val least = JengaStepIndicatorDefaults.NodeSize * 4 + 12.dp * 3
        compose.onNodeWithTag("indicator").assertWidthIsEqualTo(least)
        val bounds = stepBounds(4)
        assertDp(0.dp, bounds.first().left)
        assertDp(least, bounds.last().right)
    }

    @Test
    fun edgeToEdgeLaysOutSixStepsInARowTooNarrowForThem() {
        var variant by mutableStateOf(JengaStepIndicatorVariant.Compact)
        compose.setContent {
            JengaTheme {
                Box(modifier = Modifier.width(100.dp)) {
                    JengaStepIndicator(
                        steps = longLabels.map { JengaStep(it, JengaStepState.Pending) },
                        stepContentDescription = { index, _ -> "Step ${index + 1}" },
                        variant = variant,
                        arrangement = JengaStepIndicatorArrangement.EdgeToEdge,
                    )
                }
            }
        }
        for (shown in listOf(JengaStepIndicatorVariant.Compact, JengaStepIndicatorVariant.Labelled)) {
            variant = shown
            compose.waitForIdle()
            val bounds = stepBounds(6)
            assertDp(0.dp, bounds.first().left)
            assertDp(100.dp, bounds.last().right)
            // The nodes overlap but still run in order from the start edge.
            bounds.zipWithNext { left, right -> assertTrue("$shown: $left then $right", left.left <= right.left) }
        }
    }

    private fun stepBounds(count: Int) = (1..count).map { compose.onNodeWithContentDescription("Step $it").getUnclippedBoundsInRoot() }

    private fun assertDp(expected: Dp, actual: Dp) = assertEquals(expected.value, actual.value, 1f)

    private val longLabels = listOf("Taarifa za mteja", "Bidhaa na mpango", "Nyaraka", "Makubaliano", "Malipo ya awali", "Uthibitisho")

    @Test
    fun edgeToEdgePutsTheFirstNodeAtTheStartEdgeAndTheLastAtTheEndEdge() {
        // A compact step is exactly its node, so these are the nodes' own edges.
        showRow(labels = longLabels.take(4), variant = JengaStepIndicatorVariant.Compact)
        val bounds = stepBounds(4)
        assertDp(0.dp, bounds.first().left)
        assertDp(JengaStepIndicatorDefaults.NodeSize, bounds.first().right)
        assertDp(rowWidth - JengaStepIndicatorDefaults.NodeSize, bounds.last().left)
        assertDp(rowWidth, bounds.last().right)
        // The nodes between are evenly spaced.
        val pitch = (rowWidth - JengaStepIndicatorDefaults.NodeSize) / 3
        assertDp(pitch, bounds[1].left)
        assertDp(pitch * 2, bounds[2].left)
    }

    @Test
    fun edgeToEdgeIsMirroredInRtl() {
        showRow(labels = longLabels.take(4), variant = JengaStepIndicatorVariant.Compact, rtl = true)
        val bounds = stepBounds(4)
        assertDp(rowWidth, bounds.first().right)
        assertDp(rowWidth - JengaStepIndicatorDefaults.NodeSize, bounds.first().left)
        assertDp(0.dp, bounds.last().left)
        assertDp((rowWidth - JengaStepIndicatorDefaults.NodeSize) / 3 * 2, bounds[1].left)
    }

    @Test
    fun edgeToEdgeLabelledStepsReachBothEdges() {
        showRow(labels = longLabels.take(4))
        val bounds = stepBounds(4)
        assertDp(0.dp, bounds.first().left)
        assertDp(rowWidth, bounds.last().right)
    }

    @Test
    fun edgeToEdgeLabelledStepsReachBothEdgesInRtl() {
        showRow(labels = longLabels.take(4), rtl = true)
        val bounds = stepBounds(4)
        assertDp(rowWidth, bounds.first().right)
        assertDp(0.dp, bounds.last().left)
    }

    @Test
    fun edgeToEdgeStepsWithLongLabelsNeverOverlap() {
        showRow(labels = longLabels)
        for (count in listOf(2, 3, 4, 6)) {
            rowLabels = longLabels.take(count)
            compose.waitForIdle()
            val bounds = stepBounds(count)
            bounds.zipWithNext { left, right ->
                assertTrue("$count steps: $left overlaps $right", left.right <= right.left)
            }
        }
    }

    @Test
    fun edgeToEdgeLaysOutOneStepAtTheStartEdge() {
        showRow(labels = longLabels.take(1))
        val only = stepBounds(1).single()
        assertDp(0.dp, only.left)
        assertDp(rowWidth, only.right)
    }

    @Test
    fun edgeToEdgeLaysOutTwoStepsAtTheEdges() {
        showRow(labels = longLabels.take(2), variant = JengaStepIndicatorVariant.Compact)
        val bounds = stepBounds(2)
        assertDp(0.dp, bounds.first().left)
        assertDp(rowWidth, bounds.last().right)
    }

    @Test
    fun edgeToEdgeLaysOutNoSteps() {
        showRow(labels = emptyList())
        compose.onNodeWithTag("indicator").assertHeightIsEqualTo(0.dp)
    }

    @Test
    fun centeredStepsKeepTheirEqualColumns() {
        showRow(labels = longLabels.take(4), arrangement = JengaStepIndicatorArrangement.Centered)
        val bounds = stepBounds(4)
        bounds.forEach { assertDp(JengaStepIndicatorDefaults.ColumnWidth, it.right - it.left) }
        assertDp(0.dp, bounds.first().left)
        assertDp(rowWidth, bounds.last().right)
    }

    @Test
    fun edgeToEdgeCompactKeepsTheHaloSpaceAboveAndBelowAnEdgeNode() {
        showRow(labels = longLabels.take(4), variant = JengaStepIndicatorVariant.Compact)
        for (current in listOf(0, 3)) {
            rowCurrent = current
            compose.waitForIdle()
            compose.onNodeWithTag("indicator").assertHeightIsEqualTo(JengaStepIndicatorDefaults.NodeSize + 3.dp * 2)
            val step = compose.onNodeWithContentDescription("Step ${current + 1}").getUnclippedBoundsInRoot()
            assertDp(3.dp, step.top)
            assertDp(3.dp + JengaStepIndicatorDefaults.NodeSize, step.bottom)
        }
    }

    @Test
    fun edgeToEdgeLabelWidthLeavesAGapBetweenNeighbours() {
        // 4 steps: pitch 100, so a centered label may be 96 wide but an edge label only lets it be 73.
        assertEquals(73, edgeToEdgeLabelWidth(rowWidth = 328, stepCount = 4, nodeSize = 28, gap = 4))
        // 9 steps: pitch 37.5.
        assertEquals(31, edgeToEdgeLabelWidth(rowWidth = 328, stepCount = 9, nodeSize = 28, gap = 4))
        // Two labels share the row; one takes all of it.
        assertEquals(162, edgeToEdgeLabelWidth(rowWidth = 328, stepCount = 2, nodeSize = 28, gap = 4))
        assertEquals(328, edgeToEdgeLabelWidth(rowWidth = 328, stepCount = 1, nodeSize = 28, gap = 4))
        // Never narrower than the node, however tight the row.
        assertEquals(28, edgeToEdgeLabelWidth(rowWidth = 120, stepCount = 9, nodeSize = 28, gap = 4))
    }

    @Test
    fun edgeToEdgeLabelWidthKeepsEdgeAndCenteredLabelsApart() {
        for (count in 3..9) {
            val width = edgeToEdgeLabelWidth(rowWidth = 328, stepCount = count, nodeSize = 28, gap = 4)
            val pitch = (328 - 28f) / (count - 1)
            val secondLabelLeft = 14 + pitch - width / 2f
            assertTrue("$count steps: first label meets the second", width + 4 <= secondLabelLeft + 0.01f)
            assertTrue("$count steps: centered labels meet", width + 4 <= pitch + 0.01f)
        }
    }

    private val hasAnyText = SemanticsMatcher("has text") { SemanticsProperties.Text in it.config }
}
