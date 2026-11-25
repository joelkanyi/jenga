package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonDefaults
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Bar padding (`spacing.lg` each side) and the gap between the actions (`spacing.md`). */
private val BarPadding = 16.dp
private val Gap = 12.dp

private const val SwahiliPrimary = "Sajili mauzo na utume ujumbe"
private const val SwahiliSecondary = "Hifadhi kama rasimu kwa sasa"

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1000dp-h800dp-mdpi")
class JengaBottomBarActionsTest {

    @get:Rule
    val compose = createComposeRule()

    private val buttonHeight = JengaButtonDefaults.minHeight(JengaButtonSize.Medium)

    private fun bounds(tag: String): DpRect = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()

    private fun assertDp(expected: Dp, actual: Dp) = assertEquals(expected.value, actual.value, 0.5f)

    @Composable
    private fun Bar(
        width: Dp,
        primary: String,
        secondary: String?,
        onPrimary: () -> Unit = {},
        onSecondary: () -> Unit = {},
    ) {
        JengaBottomBar(
            modifier = Modifier.width(width).testTag("bar"),
            primaryAction = { JengaButton(text = primary, onClick = onPrimary, modifier = Modifier.testTag("primary")) },
            secondaryAction = secondary?.let {
                {
                    JengaButton(
                        text = it,
                        onClick = onSecondary,
                        modifier = Modifier.testTag("secondary"),
                        variant = JengaButtonVariant.Neutral,
                    )
                }
            },
        )
    }

    private fun assertSideBySide(barWidth: Dp) {
        val bar = bounds("bar")
        val primary = bounds("primary")
        val secondary = bounds("secondary")
        val half = (barWidth - BarPadding * 2 - Gap) / 2
        assertDp(bar.left + BarPadding, secondary.left)
        assertDp(bar.right - BarPadding, primary.right)
        assertDp(half, secondary.width)
        assertDp(half, primary.width)
        assertDp(Gap, primary.left - secondary.right)
        assertDp(secondary.top, primary.top)
        assertDp(secondary.height, primary.height)
        assertDp(buttonHeight, primary.height)
        assertDp(JengaBottomBarDefaults.Height, bar.height)
    }

    private fun assertStacked(barWidth: Dp) {
        val bar = bounds("bar")
        val primary = bounds("primary")
        val secondary = bounds("secondary")
        val full = barWidth - BarPadding * 2
        assertDp(full, primary.width)
        assertDp(full, secondary.width)
        assertDp(bar.left + BarPadding, primary.left)
        assertDp(bar.left + BarPadding, secondary.left)
        assertTrue("primary is above secondary", primary.top < secondary.top)
        assertDp(Gap, secondary.top - primary.bottom)
        assertDp(buttonHeight, primary.height)
        assertDp(buttonHeight, secondary.height)
        assertDp(buttonHeight * 2 + Gap + 16.dp, bar.height)
    }

    @Test
    fun shortLabelsSitSideBySideWithSecondaryAtStart() {
        compose.setContent { JengaTheme { Bar(360.dp, primary = "Save", secondary = "Cancel") } }
        assertSideBySide(360.dp)
        assertTrue(bounds("secondary").left < bounds("primary").left)
    }

    @Test
    fun longLabelsStackWithPrimaryOnTop() {
        compose.setContent { JengaTheme { Bar(360.dp, primary = SwahiliPrimary, secondary = SwahiliSecondary) } }
        assertStacked(360.dp)
    }

    @Test
    fun oneLongLabelIsEnoughToStack() {
        compose.setContent { JengaTheme { Bar(360.dp, primary = "Save", secondary = SwahiliSecondary) } }
        assertStacked(360.dp)
    }

    @Test
    fun sameLabelsStackWhenNarrowAndSitSideBySideWhenWide() {
        var width by mutableStateOf(360.dp)
        compose.setContent { JengaTheme { Bar(width, primary = SwahiliPrimary, secondary = SwahiliSecondary) } }
        assertStacked(360.dp)
        width = 840.dp
        compose.waitForIdle()
        assertSideBySide(840.dp)
        width = 360.dp
        compose.waitForIdle()
        assertStacked(360.dp)
    }

    /** The threshold is the button's own unconstrained width: one pixel less per half and the bar stacks. */
    @Test
    fun decisionFlipsExactlyAtTheButtonsNaturalWidth() {
        var width by mutableStateOf(1000.dp)
        compose.setContent {
            JengaTheme {
                Column {
                    JengaButton(
                        text = SwahiliPrimary,
                        onClick = {},
                        modifier = Modifier.wrapContentWidth(unbounded = true).testTag("natural-primary"),
                    )
                    JengaButton(
                        text = SwahiliSecondary,
                        onClick = {},
                        modifier = Modifier.wrapContentWidth(unbounded = true).testTag("natural-secondary"),
                    )
                    Bar(width, primary = SwahiliPrimary, secondary = SwahiliSecondary)
                }
            }
        }
        val natural = maxOf(bounds("natural-primary").width, bounds("natural-secondary").width)
        assertTrue("label is wider than its padding", natural > 100.dp)

        val justFits = natural * 2 + Gap + BarPadding * 2
        width = justFits
        compose.waitForIdle()
        assertSideBySide(justFits)
        assertDp(natural, bounds("primary").width)

        width = justFits - 2.dp
        compose.waitForIdle()
        assertStacked(justFits - 2.dp)
    }

    /** Fixed 100dp actions: (244 - 32 - 12) / 2 = 100 fits; at 243 each half is 99 and they stack. */
    @Test
    fun fitRuleUsesHalfTheInnerWidthMinusTheGap() {
        var width by mutableStateOf(244.dp)
        compose.setContent {
            JengaTheme {
                JengaBottomBar(
                    modifier = Modifier.width(width),
                    primaryAction = { Box(Modifier.width(100.dp).height(48.dp).testTag("primary")) },
                    secondaryAction = { Box(Modifier.width(100.dp).height(40.dp).testTag("secondary")) },
                )
            }
        }
        assertDp(bounds("secondary").top, bounds("primary").top)
        assertDp(48.dp, bounds("secondary").height)
        width = 243.dp
        compose.waitForIdle()
        assertTrue(bounds("primary").bottom <= bounds("secondary").top)
        assertDp(48.dp, bounds("primary").height)
        assertDp(40.dp, bounds("secondary").height)
        assertDp(Gap, bounds("secondary").top - bounds("primary").bottom)
    }

    @Test
    fun rtlMirrorsSideBySideOrder() {
        compose.setContent {
            JengaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Bar(360.dp, primary = "Save", secondary = "Cancel")
                }
            }
        }
        val bar = bounds("bar")
        val primary = bounds("primary")
        val secondary = bounds("secondary")
        assertDp(bar.left + BarPadding, primary.left)
        assertDp(bar.right - BarPadding, secondary.right)
        assertDp(Gap, secondary.left - primary.right)
        assertDp(primary.width, secondary.width)
    }

    @Test
    fun singleActionTakesFullWidth() {
        compose.setContent { JengaTheme { Bar(360.dp, primary = "Save", secondary = null) } }
        assertDp(360.dp - BarPadding * 2, bounds("primary").width)
        assertDp(buttonHeight, bounds("primary").height)
        assertDp(JengaBottomBarDefaults.Height, bounds("bar").height)
    }

    @Test
    fun emptySecondarySlotCountsAsNoSecondary() {
        compose.setContent {
            JengaTheme {
                JengaBottomBar(
                    modifier = Modifier.width(360.dp),
                    primaryAction = { JengaButton(text = "Save", onClick = {}, modifier = Modifier.testTag("primary")) },
                    secondaryAction = {},
                )
            }
        }
        assertDp(360.dp - BarPadding * 2, bounds("primary").width)
    }

    @Test
    fun callbacksFireForTheRightButtonInBothLayouts() {
        var primaryClicks = 0
        var secondaryClicks = 0
        var width by mutableStateOf(840.dp)
        compose.setContent {
            JengaTheme {
                Bar(width, SwahiliPrimary, SwahiliSecondary, onPrimary = { primaryClicks++ }, onSecondary = { secondaryClicks++ })
            }
        }
        compose.onNodeWithTag("primary").performClick()
        assertEquals(1 to 0, primaryClicks to secondaryClicks)
        compose.onNodeWithTag("secondary").performClick()
        assertEquals(1 to 1, primaryClicks to secondaryClicks)
        width = 360.dp
        compose.waitForIdle()
        compose.onNodeWithTag("secondary").performClick()
        assertEquals(1 to 2, primaryClicks to secondaryClicks)
        compose.onNodeWithTag("primary").performClick()
        assertEquals(2 to 2, primaryClicks to secondaryClicks)
    }

    @Test
    fun slotsAreComposedOnceAndSurviveTheLayoutChange() {
        var primaryCompositions = 0
        var secondaryCompositions = 0
        var entered = 0
        var disposed = 0
        var width by mutableStateOf(840.dp)
        // The width is read during measurement only, so nothing outside the bar recomposes the slots.
        val resizable = Modifier.layout { measurable, _ ->
            val placeable = measurable.measure(Constraints.fixedWidth(width.roundToPx()))
            layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        }
        compose.setContent {
            JengaTheme {
                JengaBottomBar(
                    modifier = resizable,
                    primaryAction = {
                        SideEffect { primaryCompositions++ }
                        DisposableEffect(Unit) {
                            entered++
                            onDispose { disposed++ }
                        }
                        JengaButton(text = SwahiliPrimary, onClick = {}, modifier = Modifier.testTag("primary"))
                    },
                    secondaryAction = {
                        SideEffect { secondaryCompositions++ }
                        DisposableEffect(Unit) {
                            entered++
                            onDispose { disposed++ }
                        }
                        JengaButton(text = SwahiliSecondary, onClick = {}, modifier = Modifier.testTag("secondary"))
                    },
                )
            }
        }
        compose.waitForIdle()
        assertDp(bounds("secondary").top, bounds("primary").top)
        width = 360.dp
        compose.waitForIdle()
        assertTrue(bounds("primary").bottom < bounds("secondary").top)
        assertEquals(1, primaryCompositions)
        assertEquals(1, secondaryCompositions)
        assertEquals(2, entered)
        assertEquals(0, disposed)
    }

    /**
     * Pins the documented behaviour: the decision follows the actions' current content. A loading
     * [JengaButton] without `loadingText` shows only a spinner, so the long label that made the bar
     * stack is gone and the actions move side by side; a `loadingText` about as long as the label
     * keeps them stacked. If [JengaButton] later keeps its width while loading, change this test.
     */
    @Test
    fun loadingWithoutLoadingTextFlipsTheLayoutAndLoadingTextKeepsIt() {
        var loading by mutableStateOf(false)
        var loadingText by mutableStateOf<String?>(null)
        compose.setContent {
            JengaTheme {
                JengaBottomBar(
                    modifier = Modifier.width(360.dp).testTag("bar"),
                    primaryAction = {
                        JengaButton(
                            text = SwahiliPrimary,
                            onClick = {},
                            modifier = Modifier.testTag("primary"),
                            loading = loading,
                            loadingText = loadingText,
                        )
                    },
                    secondaryAction = {
                        JengaButton(
                            text = "Cancel",
                            onClick = {},
                            modifier = Modifier.testTag("secondary"),
                            variant = JengaButtonVariant.Neutral,
                        )
                    },
                )
            }
        }
        assertStacked(360.dp)

        loading = true
        compose.waitForIdle()
        assertSideBySide(360.dp)

        loadingText = "Inasajili mauzo na ujumbe…"
        compose.waitForIdle()
        assertStacked(360.dp)

        loading = false
        compose.waitForIdle()
        assertStacked(360.dp)
    }

    /** In a horizontally scrolling parent there is no width to run out of: side by side at the wider natural width. */
    @Test
    fun unboundedWidthSitsSideBySideAtTheWiderNaturalWidth() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaButton(
                        text = SwahiliPrimary,
                        onClick = {},
                        modifier = Modifier.wrapContentWidth(unbounded = true).testTag("natural-primary"),
                    )
                    JengaButton(
                        text = "Cancel",
                        onClick = {},
                        modifier = Modifier.wrapContentWidth(unbounded = true).testTag("natural-secondary"),
                    )
                    Row(Modifier.width(360.dp).horizontalScroll(rememberScrollState())) {
                        JengaBottomBar(
                            modifier = Modifier.testTag("bar"),
                            primaryAction = {
                                JengaButton(text = SwahiliPrimary, onClick = {}, modifier = Modifier.testTag("primary"))
                            },
                            secondaryAction = {
                                JengaButton(text = "Cancel", onClick = {}, modifier = Modifier.testTag("secondary"))
                            },
                        )
                    }
                }
            }
        }
        val natural = maxOf(bounds("natural-primary").width, bounds("natural-secondary").width)
        assertTrue(bounds("natural-secondary").width < natural)
        val barWidth = natural * 2 + Gap + BarPadding * 2
        assertDp(barWidth, bounds("bar").width)
        assertSideBySide(barWidth)
        assertDp(natural, bounds("secondary").width)
    }

    @Test
    fun focusOrderFollowsTheVisualOrderOfEachLayout() {
        var width by mutableStateOf(244.dp)
        lateinit var focusManager: FocusManager
        compose.setContent {
            JengaTheme {
                focusManager = LocalFocusManager.current
                JengaBottomBar(
                    modifier = Modifier.width(width),
                    primaryAction = { Box(Modifier.width(100.dp).height(48.dp).testTag("primary").focusable()) },
                    secondaryAction = { Box(Modifier.width(100.dp).height(48.dp).testTag("secondary").focusable()) },
                )
            }
        }
        // Side by side: secondary (start) first, then primary.
        compose.runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        compose.onNodeWithTag("secondary").assertIsFocused()
        compose.runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        compose.onNodeWithTag("primary").assertIsFocused()

        // Stacked: primary (top) first, then secondary.
        compose.runOnIdle { focusManager.clearFocus(force = true) }
        width = 243.dp
        compose.waitForIdle()
        compose.runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        compose.onNodeWithTag("primary").assertIsFocused()
        compose.runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        compose.onNodeWithTag("secondary").assertIsFocused()
    }
}
