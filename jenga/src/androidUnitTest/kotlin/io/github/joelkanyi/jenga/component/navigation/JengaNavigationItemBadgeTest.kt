package io.github.joelkanyi.jenga.component.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.badge.jengaBadgeCountText
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaNavigationItemBadgeTest {

    @get:Rule
    val compose = createComposeRule()

    /** The merged text of the item tagged [tag], as a screen reader gets it. */
    private fun texts(tag: String): List<String> = compose.onNodeWithTag(tag).fetchSemanticsNode().config
        .getOrNull(SemanticsProperties.Text).orEmpty().map { it.text }

    private fun descriptions(tag: String): List<String> = compose.onNodeWithTag(tag).fetchSemanticsNode().config
        .getOrNull(SemanticsProperties.ContentDescription).orEmpty()

    /** Bounds of the badge showing [text]: the first of the badge and the text node inside it. */
    private fun badgeBounds(text: String) = compose.onAllNodesWithText(text, useUnmergedTree = true).onFirst()
        .fetchSemanticsNode().boundsInRoot

    private fun showBar(content: @Composable RowScope.() -> Unit) {
        compose.setContent { JengaTheme { JengaNavigationBar(content = content) } }
    }

    @Test
    fun countTextHidesZeroAndCapsAt99() {
        assertNull(jengaBadgeCountText(0))
        assertNull(jengaBadgeCountText(-1))
        assertEquals("1", jengaBadgeCountText(1))
        assertEquals("99", jengaBadgeCountText(99))
        assertEquals("99+", jengaBadgeCountText(100))
    }

    @Test
    fun barItemReadsTheLabelThenTheBadgeDescription() {
        showBar {
            JengaNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                modifier = Modifier.testTag("item"),
                label = "Notifications",
                badgeCount = 3,
                badgeContentDescription = "3 new",
            )
        }
        assertEquals(listOf("Notifications", "3 new"), texts("item"))
        assertEquals(emptyList<String>(), descriptions("item"))
        compose.onNodeWithTag("item").assertIsSelected()
    }

    @Test
    fun barItemReadsTheBadgeTextWhenNoDescriptionIsGiven() {
        showBar {
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                modifier = Modifier.testTag("item"),
                label = "Notifications",
                badgeCount = 100,
            )
        }
        assertEquals(listOf("Notifications", "99+"), texts("item"))
    }

    @Test
    fun barItemWithoutALabelAddsTheBadgeToTheIconDescription() {
        showBar {
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = "Notifications") },
                modifier = Modifier.testTag("item"),
                badgeCount = 3,
                badgeContentDescription = "3 new",
            )
        }
        assertEquals(listOf("Notifications", "3 new"), descriptions("item"))
        assertEquals(emptyList<String>(), texts("item"))
    }

    @Test
    fun barItemWithAZeroCountHasNoBadge() {
        showBar {
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                modifier = Modifier.testTag("item"),
                label = "Notifications",
                badgeCount = 0,
                badgeContentDescription = "0 new",
            )
        }
        assertEquals(listOf("Notifications"), texts("item"))
    }

    @Test
    fun barBadgeDoesNotChangeTheItemOrLabelBounds() {
        showBar {
            JengaNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                modifier = Modifier.testTag("plain"),
                label = "Plain",
                indicator = JengaNavIndicator.Pill,
            )
            JengaNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                modifier = Modifier.testTag("badged"),
                label = "Badged",
                indicator = JengaNavIndicator.Pill,
                badgeCount = 100,
            )
        }
        val plain = compose.onNodeWithTag("plain").fetchSemanticsNode().boundsInRoot
        val badged = compose.onNodeWithTag("badged").fetchSemanticsNode().boundsInRoot
        assertEquals(plain.size, badged.size)
        assertEquals(plain.top, badged.top)
        val plainLabel = compose.onNodeWithText("Plain", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val badgedLabel = compose.onNodeWithText("Badged", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(plainLabel.top, badgedLabel.top)
        assertEquals(plainLabel.bottom, badgedLabel.bottom)
    }

    @Test
    fun barBadgeStaysInsideANarrowItem() {
        compose.setContent {
            JengaTheme {
                Box(modifier = Modifier.width(150.dp)) {
                    JengaNavigationBar {
                        repeat(4) {
                            JengaNavigationBarItem(
                                selected = false,
                                onClick = {},
                                icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
                            )
                        }
                        JengaNavigationBarItem(
                            selected = false,
                            onClick = {},
                            icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                            modifier = Modifier.testTag("last"),
                            label = "Alerts",
                            badgeCount = 100,
                        )
                    }
                }
            }
        }
        val item = compose.onNodeWithTag("last").fetchSemanticsNode().boundsInRoot
        val badge = badgeBounds("99+")
        val unclampedRight = item.center.x + with(compose.density) { 8.dp.toPx() } + badge.width
        assertTrue("the badge fits without the clamp, so this test proves nothing", unclampedRight > item.right)
        assertTrue("badge $badge ends after item $item", badge.right <= item.right)
        assertTrue("badge $badge starts before item $item", badge.left >= item.left)
    }

    @Test
    fun barItemWithALabelKeepsAnIconDescriptionApartFromTheLabelAndCount() {
        showBar {
            JengaNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { JengaIcon(JengaIcons.Bell, contentDescription = "Bell") },
                modifier = Modifier.testTag("item"),
                label = "Notifications",
                badgeCount = 3,
                badgeContentDescription = "3 new",
            )
        }
        assertEquals(listOf("Notifications", "3 new"), texts("item"))
        assertEquals(listOf("Bell"), descriptions("item"))
    }

    @Test
    fun railItemWithAnIconDescriptionKeepsItApartFromTheLabelAndCount() {
        compose.setContent {
            JengaTheme {
                JengaNavigationRailItem(
                    label = "Notifications",
                    selected = false,
                    onClick = {},
                    icon = { JengaIcon(JengaIcons.Bell, contentDescription = "Bell") },
                    modifier = Modifier.testTag("item"),
                    badgeCount = 3,
                    badgeContentDescription = "3 new",
                )
            }
        }
        assertEquals(listOf("Notifications", "3 new"), texts("item"))
        assertEquals(listOf("Bell"), descriptions("item"))
    }

    @Test
    fun railItemShowsTheDotAndTheBadgeTogether() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaNavigationRailItem(
                        label = "Orders",
                        selected = false,
                        onClick = {},
                        icon = { JengaIcon(JengaIcons.Wrench, contentDescription = null) },
                        modifier = Modifier.testTag("dotted"),
                        showDot = true,
                        badgeCount = 7,
                    )
                    JengaNavigationRailItem(
                        label = "Orders",
                        selected = false,
                        onClick = {},
                        icon = { JengaIcon(JengaIcons.Wrench, contentDescription = null) },
                        modifier = Modifier.testTag("plain"),
                        badgeCount = 8,
                    )
                }
            }
        }
        assertEquals(listOf("Orders", "7"), texts("dotted"))
        val dotted = badgeBounds("7")
        val plain = badgeBounds("8")
        // The dot and one gap sit between the label and the badge.
        val expected = with(compose.density) { (JengaNavigationRailItemDefaults.DotSize + 8.dp).toPx() }
        assertEquals(expected, dotted.left - plain.left, 1f)
    }

    @Test
    fun railItemReadsTheLabelThenTheBadgeDescription() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaNavigationRailItem(
                        label = "Notifications",
                        selected = true,
                        onClick = {},
                        icon = { JengaIcon(JengaIcons.Bell, contentDescription = null) },
                        modifier = Modifier.testTag("described"),
                        badgeCount = 3,
                        badgeContentDescription = "3 new",
                    )
                    JengaNavigationRailItem(
                        label = "Tickets",
                        selected = false,
                        onClick = {},
                        icon = { JengaIcon(JengaIcons.Ticket, contentDescription = null) },
                        modifier = Modifier.testTag("plainCount"),
                        badgeCount = 12,
                    )
                    JengaNavigationRailItem(
                        label = "Inventory",
                        selected = false,
                        onClick = {},
                        icon = { JengaIcon(JengaIcons.Package, contentDescription = null) },
                        modifier = Modifier.testTag("zero"),
                        badgeCount = 0,
                    )
                }
            }
        }
        assertEquals(listOf("Notifications", "3 new"), texts("described"))
        assertEquals(listOf("Tickets", "12"), texts("plainCount"))
        assertEquals(listOf("Inventory"), texts("zero"))
    }
}
