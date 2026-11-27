package io.github.joelkanyi.jenga.component.tooltip

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JengaTooltipBoxTest {

    @get:Rule
    val compose = createComposeRule()

    private val tip = "First payment before the device is unlocked."

    private fun showHost() {
        compose.setContent {
            JengaTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    JengaTooltipBox(text = tip) {
                        JengaIcon(imageVector = JengaTheme.icons.info, contentDescription = "What is the deposit?")
                    }
                }
            }
        }
    }

    private fun openTooltip() {
        compose.onNodeWithContentDescription("What is the deposit?").performClick()
        compose.onNodeWithText(tip).assertIsDisplayed()
    }

    @Test
    fun hostTapShowsTheTooltipAndAnOutsideTapHidesIt() {
        showHost()
        compose.onNodeWithText(tip).assertDoesNotExist()

        openTooltip()
        compose.onNode(isPopup()).assertExists()

        // A tap outside the popup, delivered to the popup's window as the system would deliver it.
        Espresso.onView(isRoot()).perform(onPopupRoot { tapAt(it, -10f, -10f) })
        compose.waitForIdle()
        compose.onNodeWithText(tip).assertDoesNotExist()
    }

    @Test
    fun aTapOnTheTooltipHidesIt() {
        showHost()
        openTooltip()
        compose.onNodeWithText(tip).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(tip).assertDoesNotExist()
    }

    @Test
    fun aTapOnTheHostWhileOpenHidesIt() {
        showHost()
        openTooltip()
        // The popup has focus, so a tap on the host reaches the popup's window as a tap outside it:
        // it dismisses the tooltip and is not passed on to the host, which would show it again.
        Espresso.onView(isRoot()).perform(onPopupRoot { tapAt(it, -10f, -10f) })
        compose.waitForIdle()
        compose.onNodeWithText(tip).assertDoesNotExist()
        compose.onNodeWithContentDescription("What is the deposit?").assertIsDisplayed()
    }

    @Test
    fun backHidesTheTooltip() {
        showHost()
        openTooltip()

        Espresso.pressBack()
        compose.waitForIdle()
        compose.onNodeWithText(tip).assertDoesNotExist()
    }

    @Test
    fun theHostIsAButtonLabelledByItsContentAndTheTooltipIsAPaneAndALiveRegion() {
        showHost()
        // The host's content description is the button's label.
        compose.onNodeWithContentDescription("What is the deposit?")
            .assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        openTooltip()
        compose.onNodeWithText(tip)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, tip))
    }

    @Test
    fun theExpandedOverloadShowsAndHidesWithItsFlag() {
        var expanded by mutableStateOf(false)
        var dismissals = 0
        compose.setContent {
            JengaTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    JengaTooltipBox(text = tip, expanded = expanded, onDismissRequest = { dismissals++ }) {
                        Box(Modifier.size(24.dp).testTag("host"))
                    }
                }
            }
        }
        compose.onNodeWithText(tip).assertDoesNotExist()
        expanded = true
        compose.onNodeWithText(tip).assertIsDisplayed()
        Espresso.pressBack()
        compose.waitForIdle()
        // Back only asks; the flag decides.
        assertEquals(1, dismissals)
        compose.onNodeWithText(tip).assertIsDisplayed()
        expanded = false
        compose.onNodeWithText(tip).assertDoesNotExist()
    }

    @Test
    fun theBubbleSitsUnderTheHostOpaqueWithItsTipOnTheHostsCentre() {
        // A 16dp host 16dp from the left side: closer than the pointer can reach at the 16dp margin.
        var container = Color.Unspecified
        compose.setContent {
            JengaTheme {
                container = JengaTooltipDefaults.colors().container
                Box(Modifier.fillMaxSize()) {
                    JengaTooltipBox(
                        text = tip,
                        expanded = true,
                        onDismissRequest = {},
                        modifier = Modifier.padding(start = 16.dp, top = 100.dp),
                    ) {
                        Box(Modifier.size(16.dp).testTag("host"))
                    }
                }
            }
        }
        compose.waitForIdle()
        val density = compose.density
        val host = compose.onNodeWithTag("host").getUnclippedBoundsInRoot()
        val hostCenterX = with(density) { ((host.left + host.right) / 2).toPx() }
        val hostBottom = with(density) { host.bottom.toPx() }

        var popupX = Int.MIN_VALUE
        var popupY = Int.MIN_VALUE
        var image: Bitmap? = null
        Espresso.onView(isRoot()).perform(
            onPopupRoot { view ->
                val params = view.layoutParams as WindowManager.LayoutParams
                popupX = params.x
                popupY = params.y
                // The popup's window holds just the tooltip: draw it the way the window would.
                image = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also { view.draw(Canvas(it)) }
            },
        )

        // Under the host, the xs gap (4dp) between the host and the pointer's tip.
        assertEquals(hostBottom + with(density) { 4.dp.toPx() }, popupY.toFloat(), 1f)
        // On screen.
        assertTrue("popup x $popupX", popupX >= 0)

        val bitmap = image!!
        fun pixel(x: Int, y: Int) = Color(bitmap.getPixel(x, y))
        // Opaque: the bubble's fill at the middle of its left side (inside the padding).
        val side = pixel(3, bitmap.height / 2)
        assertTrue("bubble fill $side", side.alpha > 0.99f && side.isCloseTo(container))
        // The tip: the container-colored pixels on a row just under the pointer's top.
        val row = with(density) { 2.dp.toPx() }.toInt()
        val tipColumns = (0 until bitmap.width).filter { pixel(it, row).alpha > 0.5f && pixel(it, row).isCloseTo(container) }
        assertTrue("no pointer found on row $row", tipColumns.isNotEmpty())
        val tipX = popupX + (tipColumns.first() + tipColumns.last()) / 2f
        assertEquals("tip $tipX vs host centre $hostCenterX", hostCenterX, tipX, 1.5f)
    }

    private fun Color.isCloseTo(other: Color): Boolean = abs(red - other.red) < 0.05f && abs(green - other.green) < 0.05f && abs(blue - other.blue) < 0.05f

    private fun tapAt(view: View, x: Float, y: Float) {
        val now = SystemClock.uptimeMillis()
        listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP).forEach { action ->
            val event = MotionEvent.obtain(now, now, action, x, y, 0)
            view.dispatchTouchEvent(event)
            event.recycle()
        }
    }

    /** Runs [block] on the focused window's root, checking that it is the popup's. */
    private fun onPopupRoot(block: (View) -> Unit) = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()

        override fun getDescription(): String = "act on the popup's root"

        override fun perform(uiController: UiController, view: View) {
            check(view.javaClass.name.contains("Popup")) { "Expected the popup's window to have focus, got ${view.javaClass.name}" }
            block(view)
            uiController.loopMainThreadUntilIdle()
        }
    }
}
