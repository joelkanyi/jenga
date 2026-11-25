package io.github.joelkanyi.jenga.component.tooltip

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JengaTooltipPointerTest {

    @get:Rule
    val compose = createComposeRule()

    // One pixel per dp keeps the numbers below readable: a 10 x 5 pointer on a bubble with 8 corners.
    private val density = Density(1f)
    private val bubbleShape = RoundedCornerShape(8.dp)
    private val size = Size(100f, 40f)

    private fun shape(
        edge: JengaTooltipPointerEdge,
        offset: Dp = Dp.Unspecified,
    ) = JengaTooltipPointerShape(bubbleShape, JengaTooltipPointer(edge = edge, offset = offset))

    private fun JengaTooltipPointerShape.path(direction: LayoutDirection = LayoutDirection.Ltr): Path = (createOutline(size, direction, density) as Outline.Generic).path

    private fun Path.covers(x: Float, y: Float): Boolean {
        val probe = Path().apply { addRect(Rect(Offset(x, y), 0.25f)) }
        return !Path.combine(PathOperation.Intersect, this, probe).isEmpty
    }

    @Test
    fun outlineCoversBubbleAndPointerOnEachEdge() {
        // A point just under the rounded tip, one at the pointer's middle, and the bubble's far side.
        val cases = mapOf(
            JengaTooltipPointerEdge.Top to Triple(Offset(50f, 1.5f), Offset(50f, 3f), Offset(50f, 39f)),
            JengaTooltipPointerEdge.Bottom to Triple(Offset(50f, 38.5f), Offset(50f, 37f), Offset(50f, 1f)),
            JengaTooltipPointerEdge.Start to Triple(Offset(1.5f, 20f), Offset(3f, 20f), Offset(99f, 20f)),
            JengaTooltipPointerEdge.End to Triple(Offset(98.5f, 20f), Offset(97f, 20f), Offset(1f, 20f)),
        )
        cases.forEach { (edge, points) ->
            val (nearTip, insidePointer, farSide) = points
            val shape = shape(edge)
            val path = shape.path()
            val bounds = path.getBounds()
            // The rounded tip stops a little short of the bounds the sharp one would touch.
            assertEquals("$edge left", 0f, bounds.left, 1f)
            assertEquals("$edge top", 0f, bounds.top, 1f)
            assertEquals("$edge right", 100f, bounds.right, 1f)
            assertEquals("$edge bottom", 40f, bounds.bottom, 1f)
            assertTrue("$edge reaches its tip", path.covers(nearTip.x, nearTip.y))
            assertTrue("$edge pointer is filled", path.covers(insidePointer.x, insidePointer.y))
            assertTrue("$edge bubble is filled", path.covers(farSide.x, farSide.y))
            // Beside the pointer, on the pointer's side, there is nothing: only the bubble is behind it.
            val beside = when (edge) {
                JengaTooltipPointerEdge.Top, JengaTooltipPointerEdge.Bottom -> Offset(20f, insidePointer.y)
                else -> Offset(insidePointer.x, 8f)
            }
            assertFalse("$edge has no fill beside the pointer", path.covers(beside.x, beside.y))
        }
    }

    @Test
    fun bubbleLosesOnlyThePointerHeightOnThePointerSide() {
        fun bubble(edge: JengaTooltipPointerEdge) = shape(edge).geometry(size, LayoutDirection.Ltr, density).bubble
        assertEquals(Rect(0f, 5f, 100f, 40f), bubble(JengaTooltipPointerEdge.Top))
        assertEquals(Rect(0f, 0f, 100f, 35f), bubble(JengaTooltipPointerEdge.Bottom))
        assertEquals(Rect(5f, 0f, 100f, 40f), bubble(JengaTooltipPointerEdge.Start))
        assertEquals(Rect(0f, 0f, 95f, 40f), bubble(JengaTooltipPointerEdge.End))
    }

    @Test
    fun offsetIsMeasuredToThePointerCentre() {
        fun center(edge: JengaTooltipPointerEdge, offset: Dp) = shape(edge, offset).geometry(size, LayoutDirection.Ltr, density).center
        assertEquals(30f, center(JengaTooltipPointerEdge.Top, 30.dp))
        assertEquals(30f, center(JengaTooltipPointerEdge.Bottom, 30.dp))
        assertEquals(20f, center(JengaTooltipPointerEdge.Start, 20.dp))
        assertEquals(50f, center(JengaTooltipPointerEdge.Top, Dp.Unspecified))
        assertEquals(20f, center(JengaTooltipPointerEdge.End, Dp.Unspecified))
    }

    @Test
    fun offsetIsClampedToTheStraightPartOfTheEdge() {
        fun center(edge: JengaTooltipPointerEdge, offset: Dp) = shape(edge, offset).geometry(size, LayoutDirection.Ltr, density).center
        // Corner radius 8 + half the 10 wide base = 13 from either end.
        assertEquals(13f, center(JengaTooltipPointerEdge.Top, 0.dp))
        assertEquals(13f, center(JengaTooltipPointerEdge.Top, (-40).dp))
        assertEquals(87f, center(JengaTooltipPointerEdge.Top, 500.dp))
        assertEquals(13f, center(JengaTooltipPointerEdge.Start, 0.dp))
        assertEquals(27f, center(JengaTooltipPointerEdge.Start, 500.dp))
    }

    @Test
    fun pointerIsCentredWhenTheStraightPartIsShorterThanItsBase() {
        val pill = JengaTooltipPointerShape(
            RoundedCornerShape(percent = 50),
            JengaTooltipPointer(edge = JengaTooltipPointerEdge.Start, offset = 0.dp),
        )
        assertEquals(20f, pill.geometry(size, LayoutDirection.Ltr, density).center)
    }

    @Test
    fun startAndEndSwapInRtl() {
        fun side(edge: JengaTooltipPointerEdge, direction: LayoutDirection) = shape(edge).geometry(size, direction, density).side
        assertEquals(JengaTooltipPointerSide.Left, side(JengaTooltipPointerEdge.Start, LayoutDirection.Ltr))
        assertEquals(JengaTooltipPointerSide.Right, side(JengaTooltipPointerEdge.Start, LayoutDirection.Rtl))
        assertEquals(JengaTooltipPointerSide.Right, side(JengaTooltipPointerEdge.End, LayoutDirection.Ltr))
        assertEquals(JengaTooltipPointerSide.Left, side(JengaTooltipPointerEdge.End, LayoutDirection.Rtl))

        val rtlStart = shape(JengaTooltipPointerEdge.Start).path(LayoutDirection.Rtl)
        assertTrue(rtlStart.covers(97f, 20f))
        assertTrue(rtlStart.covers(1f, 20f))
        assertFalse(rtlStart.covers(97f, 8f))
    }

    @Test
    fun offsetOnTopAndBottomIsMeasuredFromTheRightInRtl() {
        val geometry = shape(JengaTooltipPointerEdge.Top, 30.dp).geometry(size, LayoutDirection.Rtl, density)
        assertEquals(70f, geometry.center)
        assertEquals(JengaTooltipPointerSide.Top, geometry.side)
    }

    @Test
    fun clampingIsMirroredInRtl() {
        fun center(edge: JengaTooltipPointerEdge, offset: Dp) = shape(edge, offset).geometry(size, LayoutDirection.Rtl, density).center
        // Offsets on top and bottom run from the right, and stop 13 short of either corner.
        assertEquals(87f, center(JengaTooltipPointerEdge.Top, 0.dp))
        assertEquals(13f, center(JengaTooltipPointerEdge.Bottom, 500.dp))
        // Start and end still run from the top.
        assertEquals(13f, center(JengaTooltipPointerEdge.Start, 0.dp))
        assertEquals(27f, center(JengaTooltipPointerEdge.End, 500.dp))
    }

    @Test
    fun zeroSizeTooltipHasAnEmptyOutline() {
        JengaTooltipPointerEdge.entries.forEach { edge ->
            val shape = shape(edge, 30.dp)
            val geometry = shape.geometry(Size.Zero, LayoutDirection.Ltr, density)
            assertEquals("$edge bubble", Rect.Zero, geometry.bubble)
            assertEquals("$edge center", 0f, geometry.center)
            assertEquals("$edge half width", 0f, geometry.halfWidth)
            assertEquals("$edge height", 0f, geometry.height)
            assertTrue("$edge pointer", shape.pointerPath(geometry, density).isEmpty)
            val bounds = (shape.createOutline(Size.Zero, LayoutDirection.Ltr, density) as Outline.Generic).path.getBounds()
            assertEquals("$edge outline", Rect.Zero, bounds)
        }
    }

    @Test
    fun pointerDrawnOnItsOwnCoversTheTriangleAndReachesIntoTheBubble() {
        // What the Android 9 and older path draws over the bubble: one bound per edge.
        val cases = mapOf(
            JengaTooltipPointerEdge.Top to Rect(45f, 0f, 55f, 10f),
            JengaTooltipPointerEdge.Bottom to Rect(45f, 30f, 55f, 40f),
            JengaTooltipPointerEdge.Start to Rect(0f, 15f, 10f, 25f),
            JengaTooltipPointerEdge.End to Rect(90f, 15f, 100f, 25f),
        )
        cases.forEach { (edge, expected) ->
            val shape = shape(edge)
            val path = shape.pointerPath(shape.geometry(size, LayoutDirection.Ltr, density), density)
            val bounds = path.getBounds()
            assertEquals("$edge left", expected.left, bounds.left, 1f)
            assertEquals("$edge top", expected.top, bounds.top, 1f)
            assertEquals("$edge right", expected.right, bounds.right, 1f)
            assertEquals("$edge bottom", expected.bottom, bounds.bottom, 1f)
            assertTrue("$edge middle", path.covers(expected.center.x, expected.center.y))
        }
        val rtl = shape(JengaTooltipPointerEdge.Start, 0.dp)
        val rtlPath = rtl.pointerPath(rtl.geometry(size, LayoutDirection.Rtl, density), density)
        assertTrue(rtlPath.covers(97f, 13f))
    }

    @Test
    fun pointerMeetsAStronglyRoundedBubbleWithoutAGap() {
        // Three pixels per dp. An 18dp radius makes the short sides of a 36dp tall bubble half
        // circles, so the bubble curves away from both ends of a start or end pointer's base.
        val dense = Density(3f)
        val tall = Size(300f, 108f)
        fun path(edge: JengaTooltipPointerEdge, direction: LayoutDirection) = JengaTooltipPointerShape(RoundedCornerShape(18.dp), JengaTooltipPointer(edge = edge))
            .createOutline(tall, direction, dense)
            .let { (it as Outline.Generic).path }
        // Just inside the base's two ends, between the base line and where the curve has got to.
        val start = path(JengaTooltipPointerEdge.Start, LayoutDirection.Ltr)
        assertTrue(start.covers(16.6f, 40f))
        assertTrue(start.covers(16.6f, 68f))
        val end = path(JengaTooltipPointerEdge.End, LayoutDirection.Ltr)
        assertTrue(end.covers(283.4f, 40f))
        assertTrue(end.covers(283.4f, 68f))
        val rtlStart = path(JengaTooltipPointerEdge.Start, LayoutDirection.Rtl)
        assertTrue(rtlStart.covers(283.4f, 40f))
        // Beyond the base the bubble's curve is still the outline.
        assertFalse(start.covers(16.6f, 34f))
    }

    @Test
    fun withoutPointerTheTooltipKeepsItsSize() {
        compose.setContent {
            JengaTheme {
                JengaTooltip(text = "Tip", modifier = Modifier.testTag("plain"))
                JengaTooltip(
                    text = "Tip",
                    modifier = Modifier.testTag("top"),
                    pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Top),
                )
                JengaTooltip(
                    text = "Tip",
                    modifier = Modifier.testTag("end"),
                    pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.End),
                )
                JengaTooltip(
                    text = "Tip",
                    modifier = Modifier.testTag("bottom"),
                    pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Bottom),
                )
                JengaTooltip(
                    text = "Tip",
                    modifier = Modifier.testTag("start"),
                    pointer = JengaTooltipPointer(edge = JengaTooltipPointerEdge.Start),
                )
            }
        }
        val plain = compose.onNodeWithTag("plain").getUnclippedBoundsInRoot()
        compose.onNodeWithTag("top")
            .assertWidthIsEqualTo(plain.width)
            .assertHeightIsEqualTo(plain.height + 5.dp)
        compose.onNodeWithTag("end")
            .assertWidthIsEqualTo(plain.width + 5.dp)
            .assertHeightIsEqualTo(plain.height)
        compose.onNodeWithTag("bottom")
            .assertWidthIsEqualTo(plain.width)
            .assertHeightIsEqualTo(plain.height + 5.dp)
        compose.onNodeWithTag("start")
            .assertWidthIsEqualTo(plain.width + 5.dp)
            .assertHeightIsEqualTo(plain.height)
    }
}
