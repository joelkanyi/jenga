package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
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

    private val hasAnyText = SemanticsMatcher("has text") { SemanticsProperties.Text in it.config }
}
