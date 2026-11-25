package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaSnackbarDismissTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var hostState: JengaSnackbarHostState
    private lateinit var scope: CoroutineScope
    private var result: JengaSnackbarResult? = null

    private fun showHost(
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        show: suspend JengaSnackbarHostState.() -> JengaSnackbarResult,
    ) {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    scope = rememberCoroutineScope()
                    hostState = rememberJengaSnackbarHostState()
                    JengaSnackbarHost(hostState = hostState)
                }
            }
        }
        compose.waitForIdle()
        scope.launch { result = hostState.show() }
        compose.waitForIdle()
    }

    @Test
    fun tappingCloseOnAHostSnackbarReturnsDismissed() {
        showHost {
            showSnackbar(
                message = "Sync failed",
                duration = JengaSnackbarDuration.Indefinite,
                dismissContentDescription = "Dismiss",
            )
        }
        compose.onNodeWithText("Sync failed").assertIsDisplayed()
        assertNull(result)

        compose.onNodeWithContentDescription("Dismiss").performClick()
        compose.waitForIdle()

        assertEquals(JengaSnackbarResult.Dismissed, result)
        compose.onNodeWithText("Sync failed").assertDoesNotExist()
    }

    @Test
    fun tappingTheActionNextToCloseStillReturnsActionPerformed() {
        showHost {
            showSnackbar(
                message = "Couldn't send the prompt",
                actionLabel = "Retry",
                duration = JengaSnackbarDuration.Indefinite,
                dismissContentDescription = "Dismiss",
            )
        }
        compose.onNodeWithContentDescription("Dismiss").assertIsDisplayed()

        compose.onNodeWithText("Retry").performClick()
        compose.waitForIdle()

        assertEquals(JengaSnackbarResult.ActionPerformed, result)
    }

    @Test
    fun tappingCloseOnATimedSnackbarReturnsDismissed() {
        showHost {
            showSnackbar(message = "Sync failed", durationMillis = 60_000, dismissContentDescription = "Dismiss")
        }

        compose.onNodeWithContentDescription("Dismiss").performClick()
        compose.waitForIdle()

        assertEquals(JengaSnackbarResult.Dismissed, result)
    }

    @Test
    fun hostSnackbarHasNoCloseByDefault() {
        showHost {
            showSnackbar(message = "Removed", actionLabel = "Undo", duration = JengaSnackbarDuration.Indefinite)
        }
        compose.onNodeWithText("Removed").assertIsDisplayed()
        compose.onNodeWithText("Undo").assertHasClickAction()
        // Only the action can be clicked: there is no second button in the snackbar.
        assertEquals(1, compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)).fetchSemanticsNodes().size)
    }

    @Test
    fun closeHasTheContentDescriptionAClickActionAndA48dpTarget() {
        var dismissals = 0
        compose.setContent {
            JengaTheme {
                JengaSnackbar(
                    message = "Sync failed",
                    onDismiss = { dismissals++ },
                    dismissContentDescription = "Funga",
                )
            }
        }
        val close = compose.onNodeWithContentDescription("Funga")
        close.assertHasClickAction()
        close.assertWidthIsEqualTo(48.dp)
        close.assertHeightIsEqualTo(48.dp)
        // The whole target is inside the snackbar, so none of it is clipped away.
        val node = close.fetchSemanticsNode()
        assertEquals(node.size.width, node.boundsInRoot.width.toInt())
        assertEquals(node.size.height, node.boundsInRoot.height.toInt())

        close.performClick()
        assertEquals(1, dismissals)
    }

    @Test
    fun closeTargetStaysInsideASnackbarWithoutPadding() {
        compose.setContent {
            JengaTheme {
                JengaSnackbar(
                    message = "Sync failed",
                    contentPadding = PaddingValues(0.dp),
                    onDismiss = {},
                    dismissContentDescription = "Dismiss",
                )
            }
        }
        val node = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode()
        assertEquals(node.size.width, node.boundsInRoot.width.toInt())
        assertEquals(node.size.height, node.boundsInRoot.height.toInt())
    }

    @Test
    fun snackbarShowsCloseOnlyWithBothTheCallbackAndTheDescription() {
        compose.setContent {
            JengaTheme {
                JengaSnackbar(message = "Plain")
                JengaSnackbar(message = "Callback only", onDismiss = {})
                JengaSnackbar(message = "Description only", dismissContentDescription = "Dismiss")
            }
        }
        compose.onNodeWithContentDescription("Dismiss").assertDoesNotExist()
        assertTrue(compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)).fetchSemanticsNodes().isEmpty())
    }

    /**
     * Taps the close ✕ of a host snackbar [fromStart] x [fromTop] into its 48dp target and returns the
     * result. The row is only told about the middle of the target (12..34dp across in LTR, 14..36dp in
     * RTL, 12..36dp down), so a tap outside that still has to land on the ✕.
     */
    private fun resultOfTappingClose(layoutDirection: LayoutDirection, fromStart: Dp, fromTop: Dp): JengaSnackbarResult? {
        showHost(layoutDirection) {
            showSnackbar(
                message = "Sync failed",
                actionLabel = "Retry",
                duration = JengaSnackbarDuration.Indefinite,
                dismissContentDescription = "Dismiss",
            )
        }
        assertNull(result)
        compose.onNodeWithContentDescription("Dismiss").performTouchInput {
            val x = if (layoutDirection == LayoutDirection.Ltr) fromStart.toPx() else width - fromStart.toPx()
            click(Offset(x, fromTop.toPx()))
        }
        compose.waitForIdle()
        return result
    }

    @Test
    fun tappingTheStartEdgeOfTheCloseTargetDismisses() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Ltr, fromStart = 3.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheEndEdgeOfTheCloseTargetDismisses() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Ltr, fromStart = 45.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheTopEdgeOfTheCloseTargetDismisses() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Ltr, fromStart = 24.dp, fromTop = 3.dp))
    }

    @Test
    fun tappingTheBottomEdgeOfTheCloseTargetDismisses() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Ltr, fromStart = 24.dp, fromTop = 45.dp))
    }

    @Test
    fun tappingTheStartEdgeOfTheCloseTargetDismissesInRtl() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Rtl, fromStart = 3.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheEndEdgeOfTheCloseTargetDismissesInRtl() {
        assertEquals(JengaSnackbarResult.Dismissed, resultOfTappingClose(LayoutDirection.Rtl, fromStart = 45.dp, fromTop = 24.dp))
    }

    @Test
    fun closeIsMirroredToTheLeftOfTheActionInRtl() {
        showHost(LayoutDirection.Rtl) {
            showSnackbar(
                message = "Sync failed",
                actionLabel = "Retry",
                duration = JengaSnackbarDuration.Indefinite,
                dismissContentDescription = "Dismiss",
            )
        }
        val close = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode()
        val action = compose.onNodeWithText("Retry").fetchSemanticsNode()
        assertTrue(close.positionInRoot.x + close.size.width <= action.positionInRoot.x)
        assertEquals(close.size.width, close.boundsInRoot.width.toInt())
    }

    @Test
    fun closeStaysOnTheMessageCentreLineWithUnevenVerticalPadding() {
        compose.setContent {
            JengaTheme {
                JengaSnackbar(
                    message = "Sync failed",
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                    onDismiss = {},
                    dismissContentDescription = "Dismiss",
                )
            }
        }
        val message = compose.onNodeWithText("Sync failed").fetchSemanticsNode()
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        val messageCentre = message.positionInRoot.y + message.size.height / 2f
        val glyphCentre = glyph.positionInRoot.y + glyph.size.height / 2f
        assertEquals(messageCentre, glyphCentre, 1f)

        val target = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode()
        assertEquals(target.size.width, target.boundsInRoot.width.toInt())
        assertEquals(target.size.height, target.boundsInRoot.height.toInt())
    }
}
