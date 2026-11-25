package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaDialogLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private var confirms = 0
    private var dismisses = 0

    /** A 320dp dialog surface with the default 24dp padding, so its content is 272dp wide from x = 24dp. */
    private fun showSurface(
        actionsLayout: JengaDialogActionsLayout = JengaDialogActionsLayout.Row,
        withIcon: Boolean = false,
        iconAlignment: JengaDialogIconAlignment = JengaDialogIconAlignment.Start,
        withDismiss: Boolean = true,
    ) {
        compose.setContent {
            JengaTheme {
                JengaDialogSurface(
                    modifier = Modifier.width(DialogWidth),
                    title = "Cancel this sale?",
                    text = "This cannot be undone.",
                    icon = if (withIcon) {
                        { Box(Modifier.size(IconSize).testTag("icon")) }
                    } else {
                        null
                    },
                    iconAlignment = iconAlignment,
                    actionsLayout = actionsLayout,
                    confirmButton = {
                        JengaButton(
                            text = "Cancel sale",
                            onClick = { confirms++ },
                            modifier = Modifier.testTag("confirm"),
                            variant = JengaButtonVariant.Danger,
                        )
                    },
                    dismissButton = if (withDismiss) {
                        {
                            JengaButton(
                                text = "Keep sale",
                                onClick = { dismisses++ },
                                modifier = Modifier.testTag("dismiss"),
                                variant = JengaButtonVariant.Ghost,
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun bounds(tag: String) = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()

    private fun titleBounds() = compose.onNodeWithText("Cancel this sale?").getUnclippedBoundsInRoot()

    private fun assertDp(expected: Dp, actual: Dp) = assertEquals(expected.value, actual.value, 0.5f)

    @Test
    fun stackedPutsConfirmAboveDismissAndBothSpanTheContentWidth() {
        showSurface(actionsLayout = JengaDialogActionsLayout.Stacked)
        val confirm = bounds("confirm")
        val dismiss = bounds("dismiss")

        assertDp(Padding, confirm.left)
        assertDp(DialogWidth - Padding, confirm.right)
        assertDp(Padding, dismiss.left)
        assertDp(DialogWidth - Padding, dismiss.right)
        assertDp(8.dp, dismiss.top - confirm.bottom)
    }

    @Test
    fun stackedSingleActionSpansTheContentWidth() {
        showSurface(actionsLayout = JengaDialogActionsLayout.Stacked, withDismiss = false)
        val confirm = bounds("confirm")

        assertDp(Padding, confirm.left)
        assertDp(DialogWidth - Padding, confirm.right)
    }

    @Test
    fun rowKeepsDismissThenConfirmAtTheEndEdge() {
        showSurface()
        val confirm = bounds("confirm")
        val dismiss = bounds("dismiss")

        assertDp(dismiss.top, confirm.top)
        assertDp(DialogWidth - Padding, confirm.right)
        assertDp(8.dp, confirm.left - dismiss.right)
        assertTrue("the row's buttons keep their own width", dismiss.left > Padding)
    }

    @Test
    fun startIconSitsAtTheStartEdge12dpAboveTheTitle() {
        showSurface(withIcon = true)
        val icon = bounds("icon")
        val title = titleBounds()

        assertDp(Padding, icon.left)
        assertDp(Padding, icon.top)
        assertDp(12.dp, title.top - icon.bottom)
        assertDp(Padding, title.left)
    }

    @Test
    fun centeredIconIsCentredAndTheTitleSpansTheWidthUnderIt() {
        showSurface(withIcon = true, iconAlignment = JengaDialogIconAlignment.Center)
        val icon = bounds("icon")
        val title = titleBounds()

        assertDp((DialogWidth - IconSize) / 2, icon.left)
        assertDp(12.dp, title.top - icon.bottom)
        assertDp(Padding, title.left)
        assertDp(DialogWidth - Padding, title.right)
    }

    @Test
    fun withoutAnIconTheTitleStartsAtTheTopPadding() {
        showSurface(iconAlignment = JengaDialogIconAlignment.Center)
        val title = titleBounds()

        assertDp(Padding, title.top)
        assertDp(Padding, title.left)
        assertTrue("the title keeps its own width without an icon", title.right < DialogWidth - Padding)
    }

    @Test
    fun stackedButtonsFireTheirOwnCallbacks() {
        showSurface(actionsLayout = JengaDialogActionsLayout.Stacked)

        compose.onNodeWithTag("confirm").performClick()
        assertEquals(1, confirms)
        assertEquals(0, dismisses)

        compose.onNodeWithTag("dismiss").performClick()
        assertEquals(1, confirms)
        assertEquals(1, dismisses)
    }

    @Test
    fun rowButtonsFireTheirOwnCallbacks() {
        showSurface()

        compose.onNodeWithTag("dismiss").performClick()
        assertEquals(0, confirms)
        assertEquals(1, dismisses)

        compose.onNodeWithTag("confirm").performClick()
        assertEquals(1, confirms)
        assertEquals(1, dismisses)
    }

    @Test
    fun dialogShowsTheIconAndStackedActionsInItsWindow() {
        compose.setContent {
            JengaTheme {
                JengaDialog(
                    onDismissRequest = {},
                    title = "Cancel this sale?",
                    icon = { Box(Modifier.size(IconSize).testTag("icon")) },
                    actionsLayout = JengaDialogActionsLayout.Stacked,
                    confirmButton = {
                        JengaButton(text = "Cancel sale", onClick = { confirms++ }, modifier = Modifier.testTag("confirm"))
                    },
                    dismissButton = {
                        JengaButton(text = "Keep sale", onClick = { dismisses++ }, modifier = Modifier.testTag("dismiss"))
                    },
                )
            }
        }
        compose.waitForIdle()

        compose.onNodeWithTag("icon").assertIsDisplayed()
        val confirm = bounds("confirm")
        val dismiss = bounds("dismiss")
        assertTrue("confirm is above dismiss", confirm.bottom <= dismiss.top)
        assertDp(confirm.left, dismiss.left)
        assertDp(confirm.right, dismiss.right)

        compose.onNodeWithTag("confirm").performClick()
        assertEquals(1, confirms)
        assertEquals(0, dismisses)
    }

    private companion object {
        val DialogWidth = 320.dp
        val Padding = 24.dp
        val IconSize = 48.dp
    }
}
