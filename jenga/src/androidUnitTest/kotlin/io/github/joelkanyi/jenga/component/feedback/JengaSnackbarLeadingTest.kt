package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The snackbar's leading item. With the default theme the snackbar has 16dp of padding at the start
 * and 12dp between the items of its row, a tone dot is 8dp wide and an icon 20dp.
 */
@RunWith(RobolectricTestRunner::class)
class JengaSnackbarLeadingTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var hostState: JengaSnackbarHostState
    private lateinit var scope: CoroutineScope
    private var result: JengaSnackbarResult? = null

    private fun showHost(show: suspend JengaSnackbarHostState.() -> JengaSnackbarResult) {
        compose.setContent {
            JengaTheme {
                scope = rememberCoroutineScope()
                hostState = rememberJengaSnackbarHostState()
                JengaSnackbarHost(hostState = hostState, snackbarPadding = PaddingValues(0.dp))
            }
        }
        compose.waitForIdle()
        scope.launch { result = hostState.show() }
        compose.waitForIdle()
    }

    private fun setSnackbar(
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        content: @Composable () -> Unit,
    ) {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Box(Modifier.width(SnackbarWidth)) { content() }
                }
            }
        }
    }

    private fun Dp.px(): Float = with(compose.density) { toPx() }

    private val SemanticsNode.left: Float get() = positionInRoot.x
    private val SemanticsNode.right: Float get() = positionInRoot.x + size.width
    private val SemanticsNode.centreY: Float get() = positionInRoot.y + size.height / 2f

    private fun message(text: String) = compose.onNodeWithText(text).fetchSemanticsNode()
    private fun leading() = compose.onNodeWithContentDescription("Synced").fetchSemanticsNode()

    @Test
    fun leadingIsShownBeforeTheMessageWithA12dpGap() {
        setSnackbar {
            JengaSnackbar(
                message = "12 visits synced",
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
            )
        }
        compose.onNodeWithContentDescription("Synced").assertIsDisplayed()
        val leading = leading()
        val message = message("12 visits synced")
        assertEquals(16.dp.px(), leading.left, 1f)
        assertEquals(20.dp.px(), leading.size.width.toFloat(), 1f)
        assertEquals(20.dp.px(), leading.size.height.toFloat(), 1f)
        assertEquals(12.dp.px(), message.left - leading.right, 1f)
    }

    @Test
    fun leadingReplacesTheToneDot() {
        setSnackbar {
            JengaSnackbar(
                message = "Ticket checked in",
                tone = JengaSnackbarTone.Success,
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
            )
        }
        // Padding, the icon, one gap: a dot before or after the icon would push both 20dp further.
        assertEquals(16.dp.px(), leading().left, 1f)
        assertEquals((16 + 20 + 12).dp.px(), message("Ticket checked in").left, 1f)
    }

    @Test
    fun withoutLeadingTheMessageStartsAtThePaddingOrAfterTheToneDot() {
        setSnackbar {
            Column {
                JengaSnackbar(message = "Payment saved")
                JengaSnackbar(message = "Ticket checked in", tone = JengaSnackbarTone.Success)
            }
        }
        assertEquals(16.dp.px(), message("Payment saved").left, 1f)
        assertEquals((16 + 8 + 12).dp.px(), message("Ticket checked in").left, 1f)
    }

    @Test
    fun leadingIsMirroredInRtl() {
        setSnackbar(LayoutDirection.Rtl) {
            JengaSnackbar(
                message = "12 visits synced",
                actionLabel = "View",
                onAction = {},
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
                onDismiss = {},
                dismissContentDescription = "Dismiss",
            )
        }
        val leading = leading()
        val message = message("12 visits synced")
        assertEquals((SnackbarWidth - 16.dp).px(), leading.right, 1f)
        assertEquals(12.dp.px(), leading.left - message.right, 1f)
    }

    @Test
    fun orderIsLeadingMessageActionClose() {
        setSnackbar {
            JengaSnackbar(
                message = "Photos still uploading",
                actionLabel = "View",
                onAction = {},
                leadingContent = { JengaBadge(text = "3", modifier = Modifier.testTag("count")) },
                onDismiss = {},
                dismissContentDescription = "Dismiss",
            )
        }
        val leading = compose.onNodeWithTag("count").fetchSemanticsNode()
        val message = message("Photos still uploading")
        val action = compose.onNodeWithText("View").fetchSemanticsNode()
        val close = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        assertTrue(leading.right <= message.left)
        assertTrue(message.right <= action.left)
        assertTrue(action.right <= close.left)
    }

    @Test
    fun orderIsMirroredInRtl() {
        setSnackbar(LayoutDirection.Rtl) {
            JengaSnackbar(
                message = "Photos still uploading",
                actionLabel = "View",
                onAction = {},
                leadingContent = { JengaBadge(text = "3", modifier = Modifier.testTag("count")) },
                onDismiss = {},
                dismissContentDescription = "Dismiss",
            )
        }
        val leading = compose.onNodeWithTag("count").fetchSemanticsNode()
        val message = message("Photos still uploading")
        val action = compose.onNodeWithText("View").fetchSemanticsNode()
        val close = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        assertTrue(message.right <= leading.left)
        assertTrue(action.right <= message.left)
        assertTrue(close.right <= action.left)
    }

    @Test
    fun leadingIsCentredOnAOneLineAndATwoLineMessage() {
        setSnackbar {
            Column {
                JengaSnackbar(
                    message = "Short",
                    leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
                )
                JengaSnackbar(
                    message = "Sync failed for 2 sales. They're saved on this phone and will sync later.",
                    leadingContent = { Box(Modifier.size(20.dp).testTag("besideTwoLines")) },
                )
            }
        }
        assertEquals(message("Short").centreY, leading().centreY, 1f)
        val twoLines = message("Sync failed for 2 sales. They're saved on this phone and will sync later.")
        assertTrue(twoLines.size.height > 30.dp.px())
        assertEquals(twoLines.centreY, compose.onNodeWithTag("besideTwoLines").fetchSemanticsNode().centreY, 1f)
    }

    @Test
    fun anItemNarrowerThanAnIconIsCentredInAnIconWideSlot() {
        setSnackbar {
            JengaSnackbar(
                message = "Payment saved",
                leadingContent = { Box(Modifier.size(10.dp).testTag("small")) },
            )
        }
        val small = compose.onNodeWithTag("small").fetchSemanticsNode()
        assertEquals((16 + 5).dp.px(), small.left, 1f)
        assertEquals((16 + 20 + 12).dp.px(), message("Payment saved").left, 1f)
    }

    @Test
    fun leadingGetsTheSnackbarContentColor() {
        var defaultContent: Color? = null
        var customContent: Color? = null
        var expectedDefault: Color? = null
        setSnackbar {
            expectedDefault = JengaTheme.colors.inverseOnSurface
            Column {
                JengaSnackbar(message = "Default", leadingContent = { defaultContent = LocalJengaContentColor.current })
                JengaSnackbar(
                    message = "Custom",
                    colors = JengaSnackbarColors(container = Color.Black, content = Color.Magenta),
                    leadingContent = { customContent = LocalJengaContentColor.current },
                )
            }
        }
        compose.waitForIdle()
        assertEquals(expectedDefault, defaultContent)
        assertEquals(Color.Magenta, customContent)
    }

    @Test
    fun hostSnackbarShowsItsLeadingItem() {
        showHost {
            showSnackbar(
                message = "12 visits synced",
                duration = JengaSnackbarDuration.Indefinite,
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
            )
        }
        compose.onNodeWithContentDescription("Synced").assertIsDisplayed()
        assertTrue(leading().right <= message("12 visits synced").left)
        assertNull(result)
    }

    @Test
    fun hostSnackbarWithLeadingReplacesTheToneDotAndKeepsActionAndClose() {
        showHost {
            showSnackbar(
                message = "Photos still uploading",
                tone = JengaSnackbarTone.Success,
                actionLabel = "View",
                duration = JengaSnackbarDuration.Indefinite,
                leadingContent = { JengaBadge(text = "3", modifier = Modifier.testTag("count")) },
                dismissContentDescription = "Dismiss",
            )
        }
        val leading = compose.onNodeWithTag("count").fetchSemanticsNode()
        val message = message("Photos still uploading")
        val action = compose.onNodeWithText("View").fetchSemanticsNode()
        val close = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        // The slot is the wider of the badge and an icon, with the badge centred in it. It starts at
        // the padding (no dot before it) and the message one gap after it.
        val slotWidth = maxOf(leading.size.width.toFloat(), 20.dp.px())
        val slotStart = (leading.left + leading.right) / 2f - slotWidth / 2f
        assertEquals(16.dp.px(), slotStart, 1f)
        assertEquals(12.dp.px(), message.left - (slotStart + slotWidth), 1f)
        assertTrue(message.right <= action.left)
        assertTrue(action.right <= close.left)

        compose.onNodeWithText("View").performClick()
        compose.waitForIdle()
        assertEquals(JengaSnackbarResult.ActionPerformed, result)
    }

    @Test
    fun timedHostSnackbarShowsItsLeadingItem() {
        showHost {
            showSnackbar(
                message = "12 visits synced",
                durationMillis = 60_000,
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
            )
        }
        compose.onNodeWithContentDescription("Synced").assertIsDisplayed()
    }

    @Test
    fun hostSnackbarHasNoLeadingByDefault() {
        showHost {
            showSnackbar(message = "Payment saved", duration = JengaSnackbarDuration.Indefinite)
        }
        compose.onNodeWithContentDescription("Synced").assertDoesNotExist()
        // The message starts at the snackbar's padding: nothing is in front of it.
        assertEquals(16.dp.px(), message("Payment saved").left, 1f)
    }

    @Test
    fun aSecondHostSnackbarDoesNotKeepTheFirstOnesLeadingItem() {
        showHost {
            showSnackbar(
                message = "12 visits synced",
                duration = JengaSnackbarDuration.Indefinite,
                leadingContent = { JengaIcon(JengaIcons.CheckCircle, contentDescription = "Synced") },
            )
        }
        compose.onNodeWithContentDescription("Synced").assertIsDisplayed()

        hostState.m3.currentSnackbarData?.dismiss()
        scope.launch { hostState.showSnackbar(message = "Payment saved", duration = JengaSnackbarDuration.Indefinite) }
        compose.waitForIdle()

        compose.onNodeWithText("Payment saved").assertIsDisplayed()
        compose.onNodeWithContentDescription("Synced").assertDoesNotExist()
    }

    private companion object {
        val SnackbarWidth = 300.dp
    }
}
