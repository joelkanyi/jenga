package io.github.joelkanyi.jenga.component.banner

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.link.JengaLink
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

// Native graphics: the icon has no semantics node, so its size and position are read from the
// pixels the banner draws, and the screen-reader order tests need a working Region.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JengaBannerProminentTest {

    @get:Rule
    val compose = createComposeRule()

    /** An icon that fills its whole box, so the pixels in the icon color are the icon's bounds. */
    private val solidIcon: ImageVector = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(0f, 0f)
            lineTo(24f, 0f)
            lineTo(24f, 24f)
            lineTo(0f, 24f)
            close()
        }
    }.build()

    /**
     * Colors that tell the parts apart: only the icon of a default banner is drawn in
     * [JengaBannerColors.icon], and only the message of a prominent one in
     * [JengaBannerColors.prominentMessage].
     */
    private val markerColors = JengaBannerColors(
        container = Color.White,
        content = Color.Black,
        icon = Color.Red,
        prominentMessage = Color.Blue,
    )

    private fun Dp.px(): Float = with(compose.density) { toPx() }

    private fun boundsOfText(text: String): Rect = compose.onNodeWithText(text).fetchSemanticsNode().boundsInRoot

    /**
     * The pixels the node tagged [tag] draws, copied from the window (`captureToImage` waits for a
     * frame that never comes under Robolectric).
     */
    private fun capture(tag: String): Bitmap {
        compose.waitForIdle()
        val node = compose.onNodeWithTag(tag).fetchSemanticsNode()
        val view = (node.root as ViewRootForTest).view
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }.filterIsInstance<Activity>().first()
        val inWindow = IntArray(2).also { view.getLocationInWindow(it) }
        val bounds = node.boundsInRoot
        val source = android.graphics.Rect(
            inWindow[0] + bounds.left.toInt(),
            inWindow[1] + bounds.top.toInt(),
            inWindow[0] + bounds.right.toInt(),
            inWindow[1] + bounds.bottom.toInt(),
        )
        val bitmap = Bitmap.createBitmap(source.width(), source.height(), Bitmap.Config.ARGB_8888)
        var result = -1
        PixelCopy.request(activity.window, source, bitmap, { result = it }, Handler(Looper.getMainLooper()))
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(PixelCopy.SUCCESS, result)
        return bitmap
    }

    private fun closeTo(argb: Int, other: Int, tolerance: Int): Boolean = listOf(16, 8, 0).all { shift -> abs((argb shr shift and 0xFF) - (other shr shift and 0xFF)) <= tolerance }

    /**
     * The bounds, in the root, of the pixels the banner tagged [tag] draws in [color], give or take
     * [tolerance] a channel (text may have no pixel in exactly its color), looking only [within]
     * that part of the root when given.
     */
    private fun boundsOfColor(tag: String, color: Color, within: Rect? = null, tolerance: Int = 0): Rect {
        val origin = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.topLeft
        val pixels = capture(tag)
        val argb = color.toArgb()
        var left = Int.MAX_VALUE
        var top = Int.MAX_VALUE
        var right = -1
        var bottom = -1
        for (y in 0 until pixels.height) {
            for (x in 0 until pixels.width) {
                val inside = within == null || within.contains(Offset(origin.x + x, origin.y + y))
                if (inside && closeTo(pixels.getPixel(x, y), argb, tolerance)) {
                    left = minOf(left, x)
                    top = minOf(top, y)
                    right = maxOf(right, x)
                    bottom = maxOf(bottom, y)
                }
            }
        }
        assertTrue("nothing is drawn in $color", right >= 0)
        return Rect(left.toFloat(), top.toFloat(), right + 1f, bottom + 1f).translate(origin)
    }

    /**
     * The bounds of the icon of the prominent banner tagged [tag]: what it draws in the content
     * color between its start edge and its [text], which is in the same color.
     */
    private fun prominentIconBounds(tag: String, text: Rect, rtl: Boolean = false): Rect {
        val banner = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val beside = if (rtl) banner.copy(left = text.right) else banner.copy(right = text.left)
        return boundsOfColor(tag, markerColors.content, within = beside)
    }

    private fun pixelsOf(tag: String): List<Int> {
        val pixels = capture(tag)
        return IntArray(pixels.width * pixels.height).also { pixels.getPixels(it, 0, pixels.width, 0, 0, pixels.width, pixels.height) }.toList()
    }

    /**
     * How many pixels of the two same-sized banners clearly differ. The edges of a glyph are
     * anti-aliased slightly differently from one position to another, so a small difference in a
     * channel is not counted.
     */
    private fun differingPixels(tag: String, otherTag: String): Int {
        val pixels = pixelsOf(tag)
        val other = pixelsOf(otherTag)
        assertEquals(pixels.size, other.size)
        return pixels.indices.count { i -> !closeTo(pixels[i], other[i], AntiAliasingNoise) }
    }

    @Composable
    private fun MarkerBanner(
        tag: String,
        emphasis: JengaBannerEmphasis,
        title: String? = "Credit approved",
        message: String = "All products are available on a payment plan.",
        action: (@Composable () -> Unit)? = null,
        actionPosition: JengaBannerActionPosition = JengaBannerActionPosition.End,
        dismissContentDescription: String? = null,
    ) {
        JengaBanner(
            message = message,
            modifier = Modifier.width(328.dp).testTag(tag),
            tone = JengaBannerTone.Success,
            title = title,
            action = action,
            colors = markerColors,
            actionPosition = actionPosition,
            onDismiss = {},
            dismissContentDescription = dismissContentDescription,
            emphasis = emphasis,
            icon = solidIcon,
        )
    }

    // ---- Icon size and position -------------------------------------------------------------

    @Test
    fun prominentIconIs28dpAndCentredOnTheTextBlock() {
        compose.setContent {
            JengaTheme { MarkerBanner(tag = "banner", emphasis = JengaBannerEmphasis.Prominent) }
        }
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val title = boundsOfText("Credit approved")
        val message = boundsOfText("All products are available on a payment plan.")
        val icon = prominentIconBounds("banner", title)
        assertEquals(28.dp.px(), icon.width, 1f)
        assertEquals(28.dp.px(), icon.height, 1f)
        // 16dp padding, then the icon, then a 12dp gap to the text.
        assertEquals(banner.left + 16.dp.px(), icon.left, 1f)
        assertEquals(icon.right + 12.dp.px(), title.left, 1f)
        assertEquals(title.left, message.left, 1f)
        // The text is taller than the icon, so it sets the height: 16dp padding above and below.
        assertEquals(banner.top + 16.dp.px(), title.top, 1f)
        assertEquals(banner.bottom - 16.dp.px(), message.bottom, 1f)
        assertEquals((title.top + message.bottom) / 2, icon.center.y, 1f)
    }

    @Test
    fun aOneLineProminentBannerIsAsTallAsItsIconAndCentresTheText() {
        compose.setContent {
            JengaTheme { MarkerBanner(tag = "banner", emphasis = JengaBannerEmphasis.Prominent, title = null, message = "ID matched.") }
        }
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val message = boundsOfText("ID matched.")
        val icon = prominentIconBounds("banner", message)
        assertEquals((16 + 28 + 16).dp.px(), banner.height, 1f)
        assertEquals(banner.top + 16.dp.px(), icon.top, 1f)
        assertEquals(icon.center.y, message.center.y, 1f)
    }

    @Test
    fun prominentIconIsMirroredToTheRightInRtl() {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MarkerBanner(tag = "banner", emphasis = JengaBannerEmphasis.Prominent)
                }
            }
        }
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val title = boundsOfText("Credit approved")
        val icon = prominentIconBounds("banner", title, rtl = true)
        assertEquals(28.dp.px(), icon.width, 1f)
        assertEquals(banner.right - 16.dp.px(), icon.right, 1f)
        assertEquals(icon.left - 12.dp.px(), title.right, 1f)
        assertEquals((title.top + boundsOfText("All products are available on a payment plan.").bottom) / 2, icon.center.y, 1f)
    }

    @Test
    fun defaultIconIsStill20dpAtTheTopStart() {
        var iconMedium = 0.dp
        compose.setContent {
            JengaTheme {
                iconMedium = JengaTheme.sizing.iconMedium
                MarkerBanner(tag = "banner", emphasis = JengaBannerEmphasis.Default)
            }
        }
        assertEquals(20.dp, iconMedium)
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val icon = boundsOfColor("banner", markerColors.icon)
        val title = boundsOfText("Credit approved")
        assertEquals(20.dp.px(), icon.width, 1f)
        assertEquals(20.dp.px(), icon.height, 1f)
        assertEquals(banner.left + 16.dp.px(), icon.left, 1f)
        assertEquals(banner.top + 16.dp.px(), icon.top, 1f)
        assertEquals(icon.right + 12.dp.px(), title.left, 1f)
        assertEquals(banner.top + 16.dp.px(), title.top, 1f)
    }

    @Test
    fun withoutTheNewParametersTheBannerIsTheDefaultEmphasis() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaBanner(
                        message = "KES 3,000 received.",
                        modifier = Modifier.width(328.dp).testTag("implicit"),
                        tone = JengaBannerTone.Success,
                        title = "Deposit confirmed",
                    )
                    JengaBanner(
                        message = "KES 3,000 received.",
                        modifier = Modifier.width(328.dp).testTag("explicit"),
                        tone = JengaBannerTone.Success,
                        title = "Deposit confirmed",
                        emphasis = JengaBannerEmphasis.Default,
                        icon = JengaBannerDefaults.icon(JengaBannerTone.Success),
                    )
                }
            }
        }
        assertEquals(0, differingPixels("explicit", "implicit"))
    }

    // ---- Colors ---------------------------------------------------------------------------

    @Test
    fun prominentMessageUsesItsOwnColorAndTheTitleTheContentColor() {
        compose.setContent {
            JengaTheme {
                Column {
                    MarkerBanner(tag = "prominent", emphasis = JengaBannerEmphasis.Prominent)
                    MarkerBanner(tag = "default", emphasis = JengaBannerEmphasis.Default, title = "Deposit confirmed", message = "KES 3,000 received.")
                }
            }
        }
        val title = boundsOfText("Credit approved")
        val message = boundsOfText("All products are available on a payment plan.")
        val banner = compose.onNodeWithTag("prominent").fetchSemanticsNode().boundsInRoot
        // From the text's start edge on: the icon is in the content color too.
        val text = banner.copy(left = title.left)
        val content = boundsOfColor("prominent", markerColors.content, within = text, tolerance = TextColorTolerance)
        val prominentMessage = boundsOfColor("prominent", markerColors.prominentMessage, within = text, tolerance = TextColorTolerance)
        assertTrue(content.top >= title.top && content.bottom <= title.bottom)
        assertTrue(prominentMessage.top >= message.top && prominentMessage.bottom <= message.bottom)
        // The default emphasis draws nothing in the prominent message color.
        val blue = markerColors.prominentMessage.toArgb()
        assertFalse(pixelsOf("default").any { closeTo(it, blue, TextColorTolerance) })
    }

    @Test
    fun themedColorsPutThePrimaryTextColorOnTheProminentMessage() {
        var textPrimary = Color.Unspecified
        val messages = mutableMapOf<JengaBannerTone, Color>()
        compose.setContent {
            JengaTheme {
                textPrimary = JengaTheme.colors.textPrimary
                JengaBannerTone.entries.forEach { messages[it] = JengaBannerDefaults.colors(it).prominentMessage }
            }
        }
        compose.waitForIdle()
        assertEquals(JengaBannerTone.entries.toSet(), messages.keys)
        messages.values.forEach { assertEquals(textPrimary, it) }
    }

    @Test
    fun handBuiltColorsUseTheContentColorForTheProminentMessage() {
        val colors = JengaBannerColors(container = Color.White, content = Color.Black, icon = Color.Red)
        assertEquals(Color.Black, colors.prominentMessage)
        assertEquals(Color.Black, colors.copy(container = Color.Gray).prominentMessage)
        assertEquals(Color.Blue, colors.copy(prominentMessage = Color.Blue).prominentMessage)
    }

    // ---- Default and custom icons ------------------------------------------------------------

    @Test
    fun prominentDefaultIconsAreTheResultGlyphs() {
        val prominent = mutableMapOf<JengaBannerTone, ImageVector>()
        val default = mutableMapOf<JengaBannerTone, ImageVector>()
        val expected = mutableMapOf<JengaBannerTone, ImageVector>()
        val expectedDefault = mutableMapOf<JengaBannerTone, ImageVector>()
        compose.setContent {
            JengaTheme {
                JengaBannerTone.entries.forEach {
                    prominent[it] = JengaBannerDefaults.icon(it, JengaBannerEmphasis.Prominent)
                    default[it] = JengaBannerDefaults.icon(it, JengaBannerEmphasis.Default)
                    expectedDefault[it] = JengaBannerDefaults.icon(it)
                }
                expected[JengaBannerTone.Info] = JengaTheme.icons.info
                expected[JengaBannerTone.Success] = JengaIcons.BadgeCheck
                expected[JengaBannerTone.Warning] = JengaIcons.AlertCircle
                expected[JengaBannerTone.Error] = JengaIcons.Ban
            }
        }
        compose.waitForIdle()
        assertEquals(expected, prominent)
        assertEquals(expectedDefault, default)
        // Success, Warning and Error each get a glyph of their own, unlike the default emphasis.
        assertEquals(4, prominent.values.toSet().size)
        listOf(JengaBannerTone.Success, JengaBannerTone.Warning, JengaBannerTone.Error).forEach {
            assertNotEquals(default[it], prominent[it])
        }
    }

    @Test
    fun bannerDrawsTheDefaultIconForItsToneUnlessGivenOne() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaBanner(
                        message = "All products are available.",
                        modifier = Modifier.width(328.dp).testTag("implicit"),
                        tone = JengaBannerTone.Success,
                        title = "Credit approved",
                        emphasis = JengaBannerEmphasis.Prominent,
                    )
                    JengaBanner(
                        message = "All products are available.",
                        modifier = Modifier.width(328.dp).testTag("badgeCheck"),
                        tone = JengaBannerTone.Success,
                        title = "Credit approved",
                        emphasis = JengaBannerEmphasis.Prominent,
                        icon = JengaIcons.BadgeCheck,
                    )
                    JengaBanner(
                        message = "All products are available.",
                        modifier = Modifier.width(328.dp).testTag("custom"),
                        tone = JengaBannerTone.Success,
                        title = "Credit approved",
                        emphasis = JengaBannerEmphasis.Prominent,
                        icon = JengaIcons.ShieldCheck,
                    )
                    JengaBanner(
                        message = "All products are available.",
                        modifier = Modifier.width(328.dp).testTag("defaultEmphasis"),
                        tone = JengaBannerTone.Success,
                        title = "Credit approved",
                    )
                    JengaBanner(
                        message = "All products are available.",
                        modifier = Modifier.width(328.dp).testTag("defaultEmphasisCustom"),
                        tone = JengaBannerTone.Success,
                        title = "Credit approved",
                        icon = JengaIcons.ShieldCheck,
                    )
                }
            }
        }
        assertEquals(0, differingPixels("badgeCheck", "implicit"))
        assertTrue(differingPixels("custom", "implicit") > DifferentGlyph)
        // A caller's icon is drawn in the default emphasis too.
        assertTrue(differingPixels("defaultEmphasisCustom", "defaultEmphasis") > DifferentGlyph)
    }

    @Test
    fun prominentIconIsTintedWithTheContentColorAndTheDefaultOneWithTheIconColor() {
        compose.setContent {
            JengaTheme {
                Column {
                    MarkerBanner(tag = "prominent", emphasis = JengaBannerEmphasis.Prominent)
                    MarkerBanner(tag = "default", emphasis = JengaBannerEmphasis.Default, title = "Deposit confirmed", message = "KES 3,000 received.")
                }
            }
        }
        // The icon is black in itself. The default emphasis draws it in JengaBannerColors.icon...
        assertEquals(20.dp.px(), boundsOfColor("default", markerColors.icon).width, 1f)
        // ...and the prominent one in JengaBannerColors.content, with nothing in the icon color.
        val red = markerColors.icon.toArgb()
        assertFalse(pixelsOf("prominent").any { closeTo(it, red, TextColorTolerance) })
        assertEquals(28.dp.px(), prominentIconBounds("prominent", boundsOfText("Credit approved")).width, 1f)
    }

    // ---- Action and close ✕ -----------------------------------------------------------------

    @Test
    fun endActionIsCentredOnTheTextAndTheCloseStaysInTheTopCorner() {
        var actions = 0
        compose.setContent {
            JengaTheme {
                MarkerBanner(
                    tag = "banner",
                    emphasis = JengaBannerEmphasis.Prominent,
                    message = "All products.",
                    action = { JengaLink(text = "Why", onClick = { actions++ }) },
                    dismissContentDescription = "Dismiss",
                )
            }
        }
        val banner = compose.onNodeWithTag("banner").fetchSemanticsNode().boundsInRoot
        val title = boundsOfText("Credit approved")
        val message = boundsOfText("All products.")
        val icon = prominentIconBounds("banner", title)
        val action = boundsOfText("Why")
        val glyph = compose.onNodeWithContentDescription("Dismiss", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val textCentre = (title.top + message.bottom) / 2
        assertEquals(textCentre, icon.center.y, 1f)
        assertEquals(textCentre, action.center.y, 1f)
        assertTrue(message.right <= action.left)
        assertTrue(action.right <= glyph.left)
        assertEquals(banner.top + 16.dp.px(), glyph.top, 1f)
        assertEquals(banner.right - 16.dp.px(), glyph.right, 1f)

        compose.onNodeWithText("Why").performClick()
        assertEquals(1, actions)
    }

    @Test
    fun anEndActionTallerThanTheTextIsCentredOnItAndOnTheIcon() {
        compose.setContent {
            JengaTheme {
                Column {
                    MarkerBanner(
                        tag = "prominent",
                        emphasis = JengaBannerEmphasis.Prominent,
                        title = null,
                        message = "ID not matched.",
                        action = { JengaButton(text = "Retry", onClick = {}, size = JengaButtonSize.Small) },
                    )
                    MarkerBanner(
                        tag = "default",
                        emphasis = JengaBannerEmphasis.Default,
                        title = null,
                        message = "Sync failed.",
                        action = { JengaButton(text = "Again", onClick = {}, size = JengaButtonSize.Small) },
                    )
                }
            }
        }
        val banner = compose.onNodeWithTag("prominent").fetchSemanticsNode().boundsInRoot
        val message = boundsOfText("ID not matched.")
        val button = boundsOfText("Retry")
        val icon = prominentIconBounds("prominent", message)
        // The button (with its touch target) is taller than the 28dp icon and the one-line
        // message, and sets the height.
        assertTrue(button.height > 28.dp.px())
        assertTrue(banner.height > (16 + 28 + 16).dp.px())
        assertEquals(banner.center.y, button.center.y, 1f)
        assertEquals(button.center.y, message.center.y, 1f)
        assertEquals(button.center.y, icon.center.y, 1f)

        // The default emphasis keeps the text, the icon and the action level with the top.
        val defaultBanner = compose.onNodeWithTag("default").fetchSemanticsNode().boundsInRoot
        val top = defaultBanner.top + 16.dp.px()
        assertEquals(top, boundsOfText("Sync failed.").top, 1f)
        // The button's 48dp touch target starts at the top; the button is in the middle of it.
        assertEquals(top + 24.dp.px(), boundsOfText("Again").center.y, 1f)
        assertEquals(top, boundsOfColor("default", markerColors.icon).top, 1f)
    }

    @Test
    fun actionBelowStartsWhereTheTextDoesAndLeavesTheIconCentredOnTheText() {
        compose.setContent {
            JengaTheme {
                Column {
                    MarkerBanner(
                        tag = "below",
                        emphasis = JengaBannerEmphasis.Prominent,
                        action = { JengaLink(text = "See the reasons", onClick = {}) },
                        actionPosition = JengaBannerActionPosition.Below,
                    )
                    MarkerBanner(
                        tag = "defaultBelow",
                        emphasis = JengaBannerEmphasis.Default,
                        title = "Deposit confirmed",
                        message = "KES 3,000 received.",
                        action = { JengaLink(text = "View receipt", onClick = {}) },
                        actionPosition = JengaBannerActionPosition.Below,
                    )
                }
            }
        }
        val title = boundsOfText("Credit approved")
        val message = boundsOfText("All products are available on a payment plan.")
        val action = boundsOfText("See the reasons")
        val icon = prominentIconBounds("below", title)
        assertEquals((title.top + message.bottom) / 2, icon.center.y, 1f)
        assertEquals(message.left, action.left, 1f)
        assertTrue(action.top >= message.bottom)

        // The default emphasis keeps its action where #67 put it: 10dp under the message, at its start.
        val defaultMessage = boundsOfText("KES 3,000 received.")
        val defaultAction = boundsOfText("View receipt")
        assertEquals(defaultMessage.left, defaultAction.left, 1f)
        assertEquals(action.top - message.bottom, defaultAction.top - defaultMessage.bottom, 1f)
    }

    // ---- Screen-reader order ----------------------------------------------------------------

    private fun showProminentBannerForTraversal(position: JengaBannerActionPosition) {
        val manager = ApplicationProvider.getApplicationContext<Context>().getSystemService(AccessibilityManager::class.java)
        shadowOf(manager).setEnabled(true)
        shadowOf(manager).setTouchExplorationEnabled(true)
        shadowOf(manager).setEnabledAccessibilityServiceList(listOf(AccessibilityServiceInfo()))
        compose.setContent {
            JengaTheme {
                JengaBanner(
                    message = TraversalMessage,
                    modifier = Modifier.width(300.dp),
                    tone = JengaBannerTone.Warning,
                    title = TraversalTitle,
                    action = { JengaLink(text = "Why", onClick = {}) },
                    actionPosition = position,
                    onDismiss = {},
                    dismissContentDescription = "Dismiss",
                    emphasis = JengaBannerEmphasis.Prominent,
                )
            }
        }
        compose.waitForIdle()
    }

    /** The order an accessibility service visits the banner's nodes in; see `JengaBannerDismissTest`. */
    private fun accessibilityTraversalOrder(): List<String> {
        val nodes = compose.onAllNodes(SemanticsMatcher("any") { true }).fetchSemanticsNodes()
        val labels = nodes.associate { node ->
            val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
            val description = node.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()
            node.id to (text ?: description)
        }
        val provider = (nodes.first().root as ViewRootForTest).view.accessibilityNodeProvider
        val order = mutableListOf<String>()
        var id = nodes.first { it.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == TraversalTitle }.id
        while (id != -1 && order.size <= nodes.size) {
            labels[id]?.let { order += it }
            id = provider.createAccessibilityNodeInfo(id)?.extras?.getInt(TraversalBeforeKey, -1) ?: -1
        }
        return order
    }

    @Test
    fun accessibilityOrderOfAProminentBannerIsTitleMessageEndActionClose() {
        showProminentBannerForTraversal(JengaBannerActionPosition.End)
        // The ✕ is at the top, so by position alone it would be read before the message.
        val close = compose.onNodeWithContentDescription("Dismiss").fetchSemanticsNode().boundsInRoot
        assertTrue(close.top < boundsOfText(TraversalMessage).top)
        assertEquals(listOf(TraversalTitle, TraversalMessage, "Why", "Dismiss"), accessibilityTraversalOrder())
    }

    @Test
    fun accessibilityOrderOfAProminentBannerIsTitleMessageBelowActionClose() {
        showProminentBannerForTraversal(JengaBannerActionPosition.Below)
        assertEquals(listOf(TraversalTitle, TraversalMessage, "Why", "Dismiss"), accessibilityTraversalOrder())
    }

    private companion object {
        /** The most a channel differs between two renderings of one glyph at different positions. */
        const val AntiAliasingNoise = 48

        /** How far a channel of an anti-aliased text pixel may be from the text's color. */
        const val TextColorTolerance = 8

        /** Fewer pixels than any two of the glyphs used here differ by. */
        const val DifferentGlyph = 20

        const val TraversalTitle = "Approved for smaller systems and for accessories only"
        const val TraversalMessage = "Only products up to KES 20,000 are available on a payment plan; offer a cash sale for the rest."

        /** The key under which Compose reports a node's `traversalBefore` id for tests. */
        const val TraversalBeforeKey = "android.view.accessibility.extra.EXTRA_DATA_TEST_TRAVERSALBEFORE_VAL"
    }
}
