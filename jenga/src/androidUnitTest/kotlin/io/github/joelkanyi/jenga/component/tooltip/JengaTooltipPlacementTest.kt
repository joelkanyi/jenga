package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** [placeJengaTooltip] on its own: plain numbers, no Compose. */
class JengaTooltipPlacementTest {

    private val window = IntSize(360, 640)
    private val tooltip = IntSize(200, 50)
    private val gap = 4
    private val margin = 16

    /** A 16 x 16 host whose top-left corner is at ([x], [y]). */
    private fun host(x: Int, y: Int) = IntRect(IntOffset(x, y), IntSize(16, 16))

    private fun place(
        anchor: IntRect,
        direction: LayoutDirection = LayoutDirection.Ltr,
        size: IntSize = tooltip,
        windowSize: IntSize = window,
        verticalMargin: Int = 0,
        pointerInset: Int = 0,
    ) = placeJengaTooltip(anchor, windowSize, size, direction, gap, margin, verticalMargin, pointerInset)

    /** Where the pointer's tip is in the window, from a placement's start-relative offset. */
    private fun tipX(p: JengaTooltipPlacement, direction: LayoutDirection, width: Int = tooltip.width) = when (direction) {
        LayoutDirection.Ltr -> p.position.x + p.pointerOffset
        LayoutDirection.Rtl -> p.position.x + width - p.pointerOffset
    }

    // An 8 corner plus half of a 10 base: the closest the pointer's centre comes to a side.
    private val inset = 13

    @Test
    fun goesBelowTheHostCentredOnItWithThePointerOnTop() {
        val p = place(host(172, 100))
        assertEquals(JengaTooltipPointerEdge.Top, p.edge)
        // Host centre 180, tooltip 200 wide: 80..280. Gap of 4 under the host's bottom (116).
        assertEquals(IntOffset(80, 120), p.position)
        assertEquals(100, p.pointerOffset)
    }

    @Test
    fun flipsAboveWhenThereIsNoRoomBelow() {
        // Host bottom 600: below would need 604 + 50 = 654 > 640.
        val p = place(host(172, 584))
        assertEquals(JengaTooltipPointerEdge.Bottom, p.edge)
        assertEquals(IntOffset(80, 584 - gap - 50), p.position)
        assertEquals(100, p.pointerOffset)
    }

    @Test
    fun staysBelowWhenItJustFits() {
        // Host bottom 586: 590 + 50 = 640, exactly the window's height.
        val p = place(host(172, 570))
        assertEquals(JengaTooltipPointerEdge.Top, p.edge)
        assertEquals(590, p.position.y)
    }

    @Test
    fun clampsInsideTheLeftSideAndStillAimsAtTheHost() {
        val p = place(host(8, 100)) // centre 16
        assertEquals(margin, p.position.x)
        assertEquals(16 - margin, p.pointerOffset)
    }

    @Test
    fun clampsInsideTheRightSideAndStillAimsAtTheHost() {
        val p = place(host(330, 100)) // centre 338
        assertEquals(360 - margin - 200, p.position.x) // 144
        assertEquals(338 - 144, p.pointerOffset)
    }

    @Test
    fun rightToLeftMeasuresThePointerFromTheRight() {
        val centred = place(host(172, 100), LayoutDirection.Rtl)
        assertEquals(IntOffset(80, 120), centred.position)
        assertEquals(100, centred.pointerOffset)

        // Near the right side, which is the start in RTL: same clamp, pointer near the start edge.
        val nearRight = place(host(330, 100), LayoutDirection.Rtl)
        assertEquals(144, nearRight.position.x)
        assertEquals(144 + 200 - 338, nearRight.pointerOffset)

        // Near the left side, the end in RTL.
        val nearLeft = place(host(8, 100), LayoutDirection.Rtl)
        assertEquals(margin, nearLeft.position.x)
        assertEquals(margin + 200 - 16, nearLeft.pointerOffset)
    }

    @Test
    fun aHostWiderThanTheTooltipCentresItAndThePointer() {
        val wide = IntRect(IntOffset(20, 100), IntSize(320, 48)) // centre 180
        val p = place(wide)
        assertEquals(IntOffset(80, 152), p.position)
        assertEquals(100, p.pointerOffset)
    }

    @Test
    fun aTooltipWiderThanTheWindowIsCentredInIt() {
        val p = place(host(172, 100), size = IntSize(340, 50)) // more than 360 - 2 * 16
        assertEquals(10, p.position.x)
        assertEquals(180 - 10, p.pointerOffset)
    }

    @Test
    fun withRoomOnNeitherSideItTakesTheRoomierSideAndStaysInTheWindow() {
        val small = IntSize(360, 120)
        val tall = IntSize(200, 80)
        // Host at 40..56: 36 above, 60 below. Below wins, moved up to end at the window's bottom.
        val lower = place(host(172, 40), size = tall, windowSize = small)
        assertEquals(JengaTooltipPointerEdge.Top, lower.edge)
        assertEquals(120 - 80, lower.position.y)
        // Host at 70..86: 66 above, 30 below. Above wins, moved down to start at the window's top.
        val upper = place(host(172, 70), size = tall, windowSize = small)
        assertEquals(JengaTooltipPointerEdge.Bottom, upper.edge)
        assertEquals(0, upper.position.y)
        // Taller than the window: pinned to the top.
        val taller = place(host(172, 40), size = IntSize(200, 200), windowSize = small)
        assertEquals(0, taller.position.y)
    }

    @Test
    fun aHostNearTheLeftSideMovesTheTooltipIntoTheMarginSoThePointerHitsItsCentre() {
        // Host centre 24: at the 16 margin the pointer could only reach 16 + 13 = 29.
        LayoutDirection.entries.forEach { direction ->
            val p = place(host(16, 100), direction, pointerInset = inset)
            assertEquals("$direction x", 24 - inset, p.position.x)
            assertEquals("$direction tip", 24, tipX(p, direction))
            assertTrue("$direction on screen", p.position.x >= 0 && p.position.x + tooltip.width <= window.width)
            assertTrue("$direction reachable", p.pointerOffset >= inset && p.pointerOffset <= tooltip.width - inset)
        }
    }

    @Test
    fun aHostNearTheRightSideMovesTheTooltipIntoTheMarginSoThePointerHitsItsCentre() {
        // Host centre 336, 24 from the right side.
        LayoutDirection.entries.forEach { direction ->
            val p = place(host(328, 100), direction, pointerInset = inset)
            assertEquals("$direction x", 336 + inset - tooltip.width, p.position.x)
            assertEquals("$direction tip", 336, tipX(p, direction))
            assertTrue("$direction on screen", p.position.x >= 0 && p.position.x + tooltip.width <= window.width)
            assertTrue("$direction reachable", p.pointerOffset >= inset && p.pointerOffset <= tooltip.width - inset)
        }
        assertEquals(tooltip.width - inset, place(host(328, 100), LayoutDirection.Ltr, pointerInset = inset).pointerOffset)
        assertEquals(inset, place(host(328, 100), LayoutDirection.Rtl, pointerInset = inset).pointerOffset)
    }

    @Test
    fun theMarginStillHoldsWhenThePointerCanReachTheHost() {
        // Host centre 40: at the margin the pointer reaches it (40 - 16 = 24 >= 13).
        val p = place(host(32, 100), pointerInset = inset)
        assertEquals(margin, p.position.x)
        assertEquals(40, tipX(p, LayoutDirection.Ltr))
    }

    @Test
    fun aHostPartlyOffScreenKeepsTheTooltipOnScreen() {
        val left = place(host(-8, 100), pointerInset = inset) // centre 0
        assertEquals(0, left.position.x)
        val right = place(host(352, 100), pointerInset = inset) // centre 360
        assertEquals(window.width - tooltip.width, right.position.x)
        // Off the bottom: above it, inside the window.
        val bottom = place(IntRect(IntOffset(172, 630), IntSize(16, 16)), pointerInset = inset)
        assertEquals(JengaTooltipPointerEdge.Bottom, bottom.edge)
        assertEquals(630 - gap - tooltip.height, bottom.position.y)
    }

    @Test
    fun withRoomOnNeitherSideItKeepsTheVerticalMargin() {
        val small = IntSize(360, 120)
        val tall = IntSize(200, 80)
        assertEquals(120 - 8 - 80, place(host(172, 40), size = tall, windowSize = small, verticalMargin = 8).position.y)
        assertEquals(8, place(host(172, 70), size = tall, windowSize = small, verticalMargin = 8).position.y)
        // Too tall for both margins: as far inside as it can be, here ending at the window's bottom.
        assertEquals(120 - 110, place(host(172, 40), size = IntSize(200, 110), windowSize = small, verticalMargin = 8).position.y)
    }

    @Test
    fun thePointerInsetIsTheCornerPlusHalfTheBase() {
        val inset = jengaTooltipPointerInset(RoundedCornerShape(8.dp), IntSize(200, 50), LayoutDirection.Ltr, Density(1f))
        assertEquals(13, inset)
        // Rounded up at fractional densities: 2.625 * (8 + 5) = 34.125.
        assertEquals(35, jengaTooltipPointerInset(RoundedCornerShape(8.dp), IntSize(525, 131), LayoutDirection.Ltr, Density(2.625f)))
    }
}
