package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
        showTagged(JengaStepIndicatorVariant.Labelled)
        val labelled = compose.onNodeWithTag("indicator").getUnclippedBoundsInRoot().height
        assertTrue("labelled row $labelled keeps room for labels", labelled > JengaStepIndicatorDefaults.NodeSize + 3.dp * 2)
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

    private val hasAnyText = SemanticsMatcher("has text") { SemanticsProperties.Text in it.config }
}
