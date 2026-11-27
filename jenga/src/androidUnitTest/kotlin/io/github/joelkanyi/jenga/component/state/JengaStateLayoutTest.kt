package io.github.joelkanyi.jenga.component.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaStateLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private fun bounds(text: String): DpRect = compose.onNodeWithText(text).getUnclippedBoundsInRoot()

    private fun assertDp(expected: Dp, actual: Dp) = assertEquals(expected.value, actual.value, 0.6f)

    /** A 300 x 400 "screen" (inside Robolectric's 320 x 470 window) holding one state. */
    @Composable
    private fun Screen(height: Dp = 400.dp, content: @Composable () -> Unit) {
        JengaTheme {
            Box(Modifier.size(300.dp, height).testTag("screen")) { content() }
        }
    }

    @Test
    fun fillScreenPinsFullWidthButtonsAtTheBottomPrimaryFirst() {
        compose.setContent {
            Screen {
                JengaErrorState(
                    title = "No internet connection",
                    description = "Check mobile data or Wi-Fi.",
                    actionLabel = "Try again",
                    onAction = {},
                    secondaryActionLabel = "Contact support",
                    onSecondaryAction = {},
                    layout = JengaStateLayout.FillScreen,
                )
            }
        }
        val primary = bounds("Try again")
        val secondary = bounds("Contact support")
        // Full width minus the 16dp side padding.
        assertDp(16.dp, primary.left)
        assertDp(268.dp, primary.width)
        assertDp(16.dp, secondary.left)
        assertDp(268.dp, secondary.width)
        // Secondary sits 16dp above the bottom edge, primary 8dp above it.
        assertDp(400.dp - 16.dp, secondary.bottom)
        assertDp(secondary.top - 8.dp, primary.bottom)
    }

    @Test
    fun fillScreenCentersTheMessageInTheSpaceAboveTheButtons() {
        compose.setContent {
            Screen {
                JengaErrorState(
                    title = "No internet connection",
                    description = "Check mobile data or Wi-Fi.",
                    actionLabel = "Try again",
                    onAction = {},
                    secondaryActionLabel = "Contact support",
                    onSecondaryAction = {},
                    layout = JengaStateLayout.FillScreen,
                )
            }
        }
        val title = bounds("No internet connection")
        val description = bounds("Check mobile data or Wi-Fi.")
        val buttonsTop = bounds("Try again").top
        // The block (title + description) has the same space above it as below it, up to the 8dp
        // padding above the buttons.
        assertDp(title.top, buttonsTop - 8.dp - description.bottom)
        assertTrue(description.bottom < buttonsTop)
        // And is centered horizontally.
        assertDp(150.dp, (title.left + title.right) / 2)
    }

    @Test
    fun fillScreenWithoutActionsCentersTheMessageInTheWholeSpace() {
        compose.setContent {
            Screen {
                JengaEmptyState(title = "Nothing here", layout = JengaStateLayout.FillScreen)
            }
        }
        val title = bounds("Nothing here")
        assertDp(title.top, 400.dp - title.bottom)
    }

    @Test
    fun fillScreenKeepsButtonsInViewAndScrollsATallMessage() {
        val long = (1..40).joinToString("\n") { "Line $it" }
        compose.setContent {
            Screen(height = 300.dp) {
                JengaErrorState(
                    title = "Something went wrong",
                    description = long,
                    actionLabel = "Try again",
                    onAction = {},
                    secondaryActionLabel = "Contact support",
                    onSecondaryAction = {},
                    layout = JengaStateLayout.FillScreen,
                )
            }
        }
        // The buttons are still pinned inside the 300dp screen.
        assertDp(300.dp - 16.dp, bounds("Contact support").bottom)
        compose.onNodeWithText("Try again").assertIsDisplayed()
        // The message starts at the top (not centered off-screen) and can be scrolled to its end.
        assertTrue(bounds("Something went wrong").top >= 0.dp)
        val buttonsTop = bounds("Try again").top
        assertTrue(bounds(long).bottom > buttonsTop)
        compose.onNode(hasScrollAction()).performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
        compose.waitForIdle()
        assertTrue(bounds(long).bottom <= buttonsTop - 8.dp)
        assertTrue(bounds("Something went wrong").top < 0.dp)
        compose.onNodeWithText("Try again").assertIsDisplayed()
        assertDp(300.dp - 16.dp, bounds("Contact support").bottom)
    }

    @Test
    fun fillScreenInsideAScrollingParentWrapsItsContent() {
        compose.setContent {
            JengaTheme {
                Column(Modifier.size(300.dp, 400.dp).verticalScroll(rememberScrollState())) {
                    JengaErrorState(
                        title = "No internet connection",
                        actionLabel = "Try again",
                        onAction = {},
                        layout = JengaStateLayout.FillScreen,
                    )
                }
            }
        }
        val title = bounds("No internet connection")
        val button = bounds("Try again")
        assertTrue(title.height > 0.dp)
        // contentPadding (24dp) below the title, 8dp above the button, then the button.
        assertDp(title.bottom + 24.dp + 8.dp, button.top)
    }

    @Test
    fun inlineKeepsTheFirstButtonWhereItWasAndPutsTheSecondUnderIt() {
        compose.setContent {
            Screen {
                Column {
                    JengaErrorState(
                        title = "One",
                        modifier = Modifier.testTag("one"),
                        actionLabel = "Retry one",
                        onAction = {},
                    )
                    JengaErrorState(
                        title = "Two",
                        modifier = Modifier.testTag("two"),
                        actionLabel = "Retry two",
                        onAction = {},
                        secondaryActionLabel = "Contact support",
                        onSecondaryAction = {},
                    )
                }
            }
        }
        val one = compose.onNodeWithTag("one").getUnclippedBoundsInRoot()
        val two = compose.onNodeWithTag("two").getUnclippedBoundsInRoot()
        val retryOne = bounds("Retry one")
        val retryTwo = bounds("Retry two")
        val support = bounds("Contact support")
        // Inline is as tall as its content: title + 12dp gap + button inside 24dp padding.
        assertDp(24.dp + bounds("One").height + 12.dp + retryOne.height + 24.dp, one.height)
        assertDp(one.bottom - 24.dp, retryOne.bottom)
        // The first button is at the same offset in both, hugging its label and centered.
        assertDp(retryOne.top - one.top, retryTwo.top - two.top)
        assertTrue(retryTwo.width < 200.dp)
        assertDp(150.dp, (retryTwo.left + retryTwo.right) / 2)
        // The second is 8dp under the first, centered, and the state grows by exactly that.
        assertDp(retryTwo.bottom + 8.dp, support.top)
        assertDp(150.dp, (support.left + support.right) / 2)
        assertDp(one.height + 8.dp + support.height, two.height)
    }

    @Test
    fun eachCallbackFiresForItsOwnButton() {
        var primary = 0
        var secondary = 0
        compose.setContent {
            Screen {
                Column {
                    JengaErrorState(
                        title = "Fill",
                        modifier = Modifier.size(300.dp, 190.dp),
                        actionLabel = "Try again",
                        onAction = { primary++ },
                        secondaryActionLabel = "Contact support",
                        onSecondaryAction = { secondary++ },
                        layout = JengaStateLayout.FillScreen,
                    )
                    JengaEmptyState(
                        title = "Inline",
                        actionLabel = "Scan",
                        onAction = { primary += 10 },
                        secondaryActionLabel = "Enter a code",
                        onSecondaryAction = { secondary += 10 },
                    )
                }
            }
        }
        compose.onNodeWithText("Try again").performClick()
        compose.runOnIdle { assertEquals(1 to 0, primary to secondary) }
        compose.onNodeWithText("Contact support").performClick()
        compose.runOnIdle { assertEquals(1 to 1, primary to secondary) }
        compose.onNodeWithText("Scan").performClick()
        compose.runOnIdle { assertEquals(11 to 1, primary to secondary) }
        compose.onNodeWithText("Enter a code").performClick()
        compose.runOnIdle { assertEquals(11 to 11, primary to secondary) }
    }

    @Test
    fun noSecondButtonByDefaultOrWithoutItsCallback() {
        compose.setContent {
            Screen {
                Column {
                    JengaErrorState(title = "A", actionLabel = "Retry", onAction = {})
                    JengaErrorState(title = "B", secondaryActionLabel = "Contact support")
                }
            }
        }
        compose.onNodeWithText("Retry").assertIsDisplayed()
        compose.onNodeWithText("Contact support").assertDoesNotExist()
    }

    @Test
    fun secondActionAloneIsTheOnlyButtonInTheFirstOnesPlace() {
        var taps = 0
        compose.setContent {
            Screen {
                Column {
                    JengaErrorState(
                        title = "Fill",
                        modifier = Modifier.size(300.dp, 200.dp),
                        secondaryActionLabel = "Contact support",
                        onSecondaryAction = { taps++ },
                        layout = JengaStateLayout.FillScreen,
                    )
                    JengaErrorState(
                        title = "Inline",
                        modifier = Modifier.testTag("inline"),
                        secondaryActionLabel = "Call us",
                        onSecondaryAction = { taps += 10 },
                    )
                }
            }
        }
        // Fill screen: full width, pinned 16dp above the bottom of its 200dp bounds.
        val pinned = bounds("Contact support")
        assertDp(16.dp, pinned.left)
        assertDp(268.dp, pinned.width)
        assertDp(200.dp - 16.dp, pinned.bottom)
        // Inline: centered at its own width, 12dp under the title, 24dp above the bottom edge.
        val inline = compose.onNodeWithTag("inline").getUnclippedBoundsInRoot()
        val call = bounds("Call us")
        assertTrue(call.width < 200.dp)
        assertDp(150.dp, (call.left + call.right) / 2)
        assertDp(bounds("Inline").bottom + 12.dp, call.top)
        assertDp(inline.bottom - 24.dp, call.bottom)
        compose.onNodeWithText("Contact support").performClick()
        compose.onNodeWithText("Call us").performClick()
        compose.runOnIdle { assertEquals(11, taps) }
    }

    @Test
    fun layoutDefaultsToInline() {
        compose.setContent {
            Screen {
                JengaErrorState(
                    title = "A",
                    contentPadding = PaddingValues(0.dp),
                    actionLabel = "Retry",
                    onAction = {},
                )
            }
        }
        // Inline by default: the state is as tall as its content, not the 400dp screen.
        assertTrue(bounds("Retry").bottom < 200.dp)
    }
}
