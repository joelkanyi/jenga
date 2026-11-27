package io.github.joelkanyi.jenga.component.banner

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.joelkanyi.jenga.component.link.JengaLink
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode

// Native graphics: Compose needs a working Region to list the nodes it reports to accessibility
// services, which the screen-reader order tests read.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JengaBannerDismissTest {

    @get:Rule
    val compose = createComposeRule()

    private var dismissals = 0
    private var actions = 0

    /** A 328dp-wide banner (a 360dp phone with 16dp gutters) with a title, an action and the ✕. */
    private fun showBannerWithActionAndClose(layoutDirection: LayoutDirection = LayoutDirection.Ltr) {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    JengaBanner(
                        message = "KES 3,000 received.",
                        modifier = Modifier.width(328.dp).testTag("banner"),
                        tone = JengaBannerTone.Success,
                        title = "Deposit confirmed",
                        action = { JengaLink(text = "Receipt", onClick = { actions++ }) },
                        onDismiss = { dismissals++ },
                        dismissContentDescription = "Dismiss",
                    )
                }
            }
        }
    }

    private fun clickableNodes() = compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)).fetchSemanticsNodes()

    @Test
    fun tappingCloseCallsOnDismiss() {
        compose.setContent {
            JengaTheme {
                JengaBanner(message = "Deposit confirmed.", onDismiss = { dismissals++ }, dismissContentDescription = "Dismiss")
            }
        }
        compose.onNodeWithContentDescription("Dismiss").assertIsDisplayed().performClick()
        assertEquals(1, dismissals)
    }

    @Test
    fun aBannerHiddenFromOnDismissIsGone() {
        compose.setContent {
            JengaTheme {
                var visible by remember { mutableStateOf(true) }
                if (visible) {
                    JengaBanner(message = "Deposit confirmed.", onDismiss = { visible = false }, dismissContentDescription = "Dismiss")
                }
            }
        }
        compose.onNodeWithContentDescription("Dismiss").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Deposit confirmed.").assertDoesNotExist()
    }

    @Test
    fun closeIsOneButtonWithTheContentDescriptionAndAMinTouchTargetSquare() {
        var touchTarget = 0.dp
        compose.setContent {
            JengaTheme {
                touchTarget = JengaTheme.sizing.minTouchTarget
                JengaBanner(message = "Deposit confirmed.", onDismiss = { dismissals++ }, dismissContentDescription = "Funga")
            }
        }
        assertEquals(48.dp, touchTarget)
        val close = compose.onNodeWithContentDescription("Funga")
        close.assertHasClickAction()
        close.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        close.assertWidthIsEqualTo(touchTarget)
        close.assertHeightIsEqualTo(touchTarget)
        // The whole target is inside the banner, so none of it is clipped away.
        val node = close.fetchSemanticsNode()
        assertEquals(node.size.width, node.boundsInRoot.width.toInt())
        assertEquals(node.size.height, node.boundsInRoot.height.toInt())
        // The ✕ is the only thing in the banner that can be clicked or focused.
        assertEquals(1, clickableNodes().size)
    }

    @Test
    fun noCloseByDefault() {
        compose.setContent {
            JengaTheme {
                JengaBanner(message = "Plain", title = "Title")
            }
        }
        compose.onNodeWithText("Plain").assertIsDisplayed()
        assertTrue(clickableNodes().isEmpty())
    }

    @Test
    fun closeIsShownOnlyWithBothTheCallbackAndTheDescription() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaBanner(message = "Callback only", onDismiss = { dismissals++ })
                    JengaBanner(message = "Description only", dismissContentDescription = "Dismiss")
                }
            }
        }
        compose.onNodeWithText("Callback only").assertIsDisplayed()
        compose.onNodeWithContentDescription("Dismiss").assertDoesNotExist()
        assertTrue(clickableNodes().isEmpty())
    }

    @Test
    fun closeDoesNotChangeTheHeightOfAOneLineBanner() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaBanner(message = "Deposit confirmed.", modifier = Modifier.testTag("plain"))
                    JengaBanner(
                        message = "Deposit confirmed.",
                        modifier = Modifier.testTag("dismissible"),
                        onDismiss = {},
                        dismissContentDescription = "Dismiss",
                    )
                }
            }
        }
        val plain = compose.onNodeWithTag("plain").fetchSemanticsNode()
        val dismissible = compose.onNodeWithTag("dismissible").fetchSemanticsNode()
        assertEquals(plain.size.height, dismissible.size.height)
    }

    @Test
    fun closeGlyphIsAtTheTopEndCornerOfTheContent() {
        var padding = 0.dp
        compose.setContent {
            JengaTheme {
                padding = JengaTheme.spacing.lg
                JengaBanner(
                    message = "KES 3,000 received from 0712 345 678. The balance and the next due date have been updated.",
                    modifier = Modifier.width(328.dp).testTag("banner"),
                    title = "Deposit confirmed",
                    onDismiss = {},
                    dismissContentDescription = "Dismiss",
                )
            }
        }
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val paddingPx = with(compose.density) { padding.toPx() }
        // Level with the leading icon, which starts at the top padding, and ending where the text would.
        assertEquals(banner.top + paddingPx, glyph.top, 1f)
        assertEquals(banner.right - paddingPx, glyph.right, 1f)
    }

    @Test
    fun closeIsAfterTheActionAndBothWork() {
        showBannerWithActionAndClose()
        val close = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode()
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        val action = compose.onNodeWithText("Receipt").fetchSemanticsNode()
        val message = compose.onNodeWithText("KES 3,000 received.").fetchSemanticsNode()
        assertTrue(message.boundsInRoot.right <= action.boundsInRoot.left)
        assertTrue(action.boundsInRoot.right <= glyph.boundsInRoot.left)
        // Traversal order follows the layout: the ✕ is the last clickable node.
        assertEquals(close.id, clickableNodes().last().id)

        compose.onNodeWithText("Receipt").performClick()
        assertEquals(1, actions)
        assertEquals(0, dismissals)
        compose.onNodeWithContentDescription("Dismiss").performClick()
        assertEquals(1, dismissals)
        assertEquals(1, actions)
    }

    @Test
    fun actionBelowSitsUnderTheMessageAndLeavesTheCloseAtTheTopEnd() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaBanner(
                        message = "KES 3,000 received.",
                        modifier = Modifier.width(328.dp).testTag("below"),
                        title = "Deposit confirmed",
                        action = { JengaLink(text = "View receipt", onClick = { actions++ }) },
                        actionPosition = JengaBannerActionPosition.Below,
                        onDismiss = { dismissals++ },
                        dismissContentDescription = "Dismiss",
                    )
                    JengaBanner(
                        message = "No action.",
                        modifier = Modifier.width(328.dp).testTag("plain"),
                        title = "Deposit confirmed",
                        onDismiss = {},
                        dismissContentDescription = "Close",
                    )
                }
            }
        }
        val banner = compose.onNodeWithTag("below").fetchSemanticsNode().boundsInRoot
        val plain = compose.onNodeWithTag("plain").fetchSemanticsNode().boundsInRoot
        val message = compose.onNodeWithText("KES 3,000 received.").fetchSemanticsNode().boundsInRoot
        val action = compose.onNodeWithText("View receipt").fetchSemanticsNode().boundsInRoot
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val plainGlyph = compose.onNodeWithContentDescription("Close", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue(action.top >= message.bottom)
        assertEquals(message.left, action.left, 1f)
        assertTrue(action.bottom <= banner.bottom)
        // The ✕ is where it is without an action.
        assertEquals(plainGlyph.top - plain.top, glyph.top - banner.top, 1f)
        assertEquals(plainGlyph.right, glyph.right, 1f)

        compose.onNodeWithText("View receipt").performClick()
        compose.onNodeWithContentDescription("Dismiss").performClick()
        assertEquals(1, actions)
        assertEquals(1, dismissals)
    }

    @Test
    fun closeIsMirroredToTheLeftOfTheActionInRtl() {
        showBannerWithActionAndClose(LayoutDirection.Rtl)
        val close = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode()
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode()
        val action = compose.onNodeWithText("Receipt").fetchSemanticsNode()
        assertTrue(glyph.boundsInRoot.right <= action.boundsInRoot.left)
        assertEquals(close.size.width, close.boundsInRoot.width.toInt())
        assertEquals(close.size.height, close.boundsInRoot.height.toInt())
    }

    /**
     * Taps the ✕ [fromStart] x [fromTop] into its 48dp target and returns how many times `onDismiss`
     * ran. The row is only told about the 20dp glyph in the middle of the target, so a tap outside
     * that still has to land on the ✕, not on the action next to it.
     */
    private fun dismissalsAfterTappingClose(layoutDirection: LayoutDirection, fromStart: Dp, fromTop: Dp): Int {
        showBannerWithActionAndClose(layoutDirection)
        compose.onNodeWithContentDescription("Dismiss").performTouchInput {
            val x = if (layoutDirection == LayoutDirection.Ltr) fromStart.toPx() else width - fromStart.toPx()
            click(Offset(x, fromTop.toPx()))
        }
        compose.waitForIdle()
        assertEquals(0, actions)
        return dismissals
    }

    @Test
    fun tappingTheStartEdgeOfTheCloseTargetDismisses() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Ltr, fromStart = 3.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheEndEdgeOfTheCloseTargetDismisses() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Ltr, fromStart = 45.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheTopEdgeOfTheCloseTargetDismisses() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Ltr, fromStart = 24.dp, fromTop = 3.dp))
    }

    @Test
    fun tappingTheBottomEdgeOfTheCloseTargetDismisses() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Ltr, fromStart = 24.dp, fromTop = 45.dp))
    }

    @Test
    fun tappingTheStartEdgeOfTheCloseTargetDismissesInRtl() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Rtl, fromStart = 3.dp, fromTop = 24.dp))
    }

    @Test
    fun tappingTheEndEdgeOfTheCloseTargetDismissesInRtl() {
        assertEquals(1, dismissalsAfterTappingClose(LayoutDirection.Rtl, fromStart = 45.dp, fromTop = 24.dp))
    }

    // ---- Screen-reader order ----------------------------------------------------------------

    private fun showBannersForTraversal(position: JengaBannerActionPosition?) {
        compose.setContent {
            JengaTheme {
                JengaBanner(
                    message = TraversalMessage,
                    modifier = Modifier.width(300.dp).testTag("banner"),
                    title = TraversalTitle,
                    action = position?.let { { JengaLink(text = "Receipt", onClick = {}) } },
                    actionPosition = position ?: JengaBannerActionPosition.End,
                    onDismiss = {},
                    dismissContentDescription = "Dismiss",
                )
            }
        }
        compose.waitForIdle()
    }

    /**
     * The order in which an accessibility service is told to visit the banner's nodes: Compose
     * works it out from position, `isTraversalGroup` and `traversalIndex`, and gives every node
     * the id of the one that follows it (`traversalBefore`). This follows that chain from the
     * title, and names each node by its text or content description.
     */
    private fun accessibilityTraversalOrder(): List<String> {
        val nodes = compose.onAllNodes(SemanticsMatcher("any") { true }).fetchSemanticsNodes()
        val labels = nodes.associate { node ->
            val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
            val description = node.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()
            node.id to (text ?: description)
        }
        val view = (nodes.first().root as ViewRootForTest).view
        val provider = view.accessibilityNodeProvider
        val order = mutableListOf<String>()
        var id = nodes.first { it.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == TraversalTitle }.id
        while (id != -1 && order.size <= nodes.size) {
            labels[id]?.let { order += it }
            id = provider.createAccessibilityNodeInfo(id)?.extras?.getInt(TraversalBeforeKey, -1) ?: -1
        }
        return order
    }

    /** Makes Compose compute the accessibility traversal order, as it does when TalkBack is on. */
    private fun enableAccessibilityServices() {
        val manager = ApplicationProvider.getApplicationContext<Context>().getSystemService(AccessibilityManager::class.java)
        shadowOf(manager).setEnabled(true)
        shadowOf(manager).setTouchExplorationEnabled(true)
        shadowOf(manager).setEnabledAccessibilityServiceList(listOf(AccessibilityServiceInfo()))
    }

    @Test
    fun accessibilityOrderIsTitleMessageClose() {
        enableAccessibilityServices()
        showBannersForTraversal(position = null)
        assertEquals(listOf(TraversalTitle, TraversalMessage, "Dismiss"), accessibilityTraversalOrder())
    }

    @Test
    fun accessibilityOrderIsTitleMessageEndActionClose() {
        enableAccessibilityServices()
        showBannersForTraversal(JengaBannerActionPosition.End)
        assertEquals(listOf(TraversalTitle, TraversalMessage, "Receipt", "Dismiss"), accessibilityTraversalOrder())
    }

    @Test
    fun accessibilityOrderIsTitleMessageBelowActionClose() {
        enableAccessibilityServices()
        showBannersForTraversal(JengaBannerActionPosition.Below)
        assertEquals(listOf(TraversalTitle, TraversalMessage, "Receipt", "Dismiss"), accessibilityTraversalOrder())
    }

    @Test
    fun bannerWithActionAndCloseIsOneTraversalGroupOfTextThenActionThenClose() {
        showBannersForTraversal(JengaBannerActionPosition.End)
        val isGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)

        fun index(value: Float) = SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, value)
        // The banner is a group, so the indices below only order its own parts, not the screen's.
        compose.onNodeWithTag("banner").assert(isGroup)
        compose.onNode(isGroup and index(0f), useUnmergedTree = true).assert(hasAnyDescendant(hasText(TraversalMessage)))
        compose.onNode(isGroup and index(0f), useUnmergedTree = true).assert(hasAnyDescendant(hasText(TraversalTitle)))
        compose.onNode(isGroup and index(1f), useUnmergedTree = true).assert(hasAnyDescendant(hasText("Receipt")))
        compose.onNodeWithContentDescription("Dismiss").assert(isGroup and index(2f))
    }

    @Test
    fun plainBannerHasNoTraversalSemantics() {
        compose.setContent {
            JengaTheme {
                JengaBanner(message = "Plain", title = "Title")
            }
        }
        val groups = compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.IsTraversalGroup), useUnmergedTree = true)
        val indexed = compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.TraversalIndex), useUnmergedTree = true)
        assertTrue(groups.fetchSemanticsNodes().isEmpty())
        assertTrue(indexed.fetchSemanticsNodes().isEmpty())
    }

    private companion object {
        /** The key under which Compose reports a node's `traversalBefore` id for tests. */
        // Long enough to wrap: the message then starts below the 48dp ✕ target, which is when
        // ordering by position alone would read the action and the ✕ before the message.
        const val TraversalTitle = "Credit check runs after the deposit has been confirmed by the bank"
        const val TraversalMessage = "You can register the sale now; we will notify you when the result of the check is in."

        const val TraversalBeforeKey = "android.view.accessibility.extra.EXTRA_DATA_TEST_TRAVERSALBEFORE_VAL"
    }
}
