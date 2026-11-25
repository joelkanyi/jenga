package io.github.joelkanyi.jenga.component.link

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getFirstLinkBounds
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaComponentStyles
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

// Native graphics: a link's tap area is laid out from the path of its text range, which the
// legacy Robolectric renderer reports as empty.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JengaInlineLinkTest {

    @get:Rule
    val compose = createComposeRule()

    private val sentence = "See the terms of the agreement and the privacy policy for details."

    private fun SemanticsNodeInteraction.text(): AnnotatedString = fetchSemanticsNode().config[SemanticsProperties.Text].single()

    private fun SemanticsNodeInteraction.links(): List<AnnotatedString.Range<LinkAnnotation>> = text().let { it.getLinkAnnotations(0, it.length) }

    private fun tagged(tag: String): (AnnotatedString.Range<LinkAnnotation>) -> Boolean = { (it.item as LinkAnnotation.Clickable).tag == tag }

    @Test
    fun tappingALinkCallsItsOwnCallbackOnly() {
        var terms = 0
        var privacy = 0
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(
                    text = sentence,
                    links = listOf(
                        JengaInlineLink("terms of the agreement") { terms++ },
                        JengaInlineLink("privacy policy") { privacy++ },
                    ),
                    modifier = Modifier.testTag("text"),
                )
            }
        }
        compose.onNodeWithTag("text").performFirstLinkClick(tagged("privacy policy"))
        compose.runOnIdle {
            assertEquals(0, terms)
            assertEquals(1, privacy)
        }
        compose.onNodeWithTag("text").performFirstLinkClick(tagged("terms of the agreement"))
        compose.runOnIdle {
            assertEquals(1, terms)
            assertEquals(1, privacy)
        }
    }

    @Test
    fun eachLinkIsExposedAsALinkOverItsPhrase() {
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(
                    text = sentence,
                    links = listOf(
                        JengaInlineLink("terms of the agreement") {},
                        JengaInlineLink("privacy policy") {},
                    ),
                    modifier = Modifier.testTag("text"),
                )
            }
        }
        val node = compose.onNodeWithTag("text")
        // The whole sentence is still one piece of text for a screen reader.
        assertEquals(sentence, node.text().text)
        // Each phrase carries a link annotation: this is what TalkBack announces as a link.
        val links = node.links().sortedBy { it.start }
        assertEquals(
            listOf("terms of the agreement", "privacy policy"),
            links.map { sentence.substring(it.start, it.end) },
        )
        // And each link is its own node with a click action, so it can be focused and activated.
        val clickable = node.fetchSemanticsNode().children
            .filter { it.config.getOrNull(SemanticsActions.OnClick) != null }
        assertEquals(2, clickable.size)
    }

    @Test
    fun textAroundTheLinksIsNotClickable() {
        var clicks = 0
        compose.setContent {
            JengaTheme {
                // The link is several lines below the first one, so a tap on the first line is
                // well outside the minimum touch target Compose gives every clickable.
                JengaTextWithLinks(
                    text = "Plain text comes first.\n\n\n\n\nThen the terms.",
                    links = listOf(JengaInlineLink("terms") { clicks++ }),
                    modifier = Modifier.testTag("text"),
                )
            }
        }
        val node = compose.onNodeWithTag("text")
        node.assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))

        val link = node.getFirstLinkBounds()
        assertNotNull(link)
        assertTrue("link bounds $link", link!!.top > 96f)
        node.performTouchInput { click(Offset(1f, 1f)) }
        compose.runOnIdle { assertEquals(0, clicks) }

        // A tap inside the link's own bounds does reach it.
        node.performTouchInput { click(link.center) }
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun disabledLinkIsPlainTextInTheDisabledColor() {
        var clicks = 0
        var disabledColor = Color.Unspecified
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(
                    text = "Reports are not available yet. Download report.",
                    links = listOf(JengaInlineLink("Download report", enabled = false) { clicks++ }),
                    modifier = Modifier.testTag("text"),
                )
                disabledColor = JengaLinkDefaults.colors().disabledContent
            }
        }
        val node = compose.onNodeWithTag("text")
        assertTrue(node.links().isEmpty())
        val span = node.text().spanStyles.single()
        assertEquals("Download report", node.text().text.substring(span.start, span.end))
        assertEquals(TextDecoration.Underline, span.item.textDecoration)
        assertEquals(disabledColor, span.item.color)
        node.performTouchInput { click(center) }
        compose.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun missingPhraseAddsNoLinkAndRepeatedPhraseTakesTheNextOccurrence() {
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(
                    text = "Open here or here.",
                    links = listOf(
                        JengaInlineLink("here") {},
                        JengaInlineLink("nowhere") {},
                        JengaInlineLink("here") {},
                    ),
                    modifier = Modifier.testTag("text"),
                )
            }
        }
        val links = compose.onNodeWithTag("text").links().sortedBy { it.start }
        assertEquals(listOf(5 to 9, 13 to 17), links.map { it.start to it.end })
    }

    @Test
    fun newClickLambdasAreUsedWithoutRebuildingTheText() {
        var generation by mutableIntStateOf(0)
        var seen = -1
        compose.setContent {
            JengaTheme {
                val current = generation
                JengaTextWithLinks(
                    text = "Read the terms.",
                    links = listOf(JengaInlineLink("terms") { seen = current }),
                    modifier = Modifier.testTag("text"),
                )
            }
        }
        val before = compose.onNodeWithTag("text").text()
        generation = 7
        compose.waitForIdle()
        val after = compose.onNodeWithTag("text").text()
        assertEquals(before, after)
        compose.onNodeWithTag("text").performFirstLinkClick()
        compose.runOnIdle { assertEquals(7, seen) }
    }

    @Test
    fun appendJengaLinkAddsAClickableLink() {
        var support = 0
        var guide = 0
        compose.setContent {
            JengaTheme {
                val styles = JengaLinkDefaults.inlineStyles()
                val text = remember(styles) {
                    buildAnnotatedString {
                        append("Need help? ")
                        appendJengaLink("Call support", styles) { support++ }
                        append(" or ")
                        appendJengaLink("read the guide", styles) { guide++ }
                        append(".")
                    }
                }
                JengaText(text = text, modifier = Modifier.testTag("text"))
            }
        }
        val node = compose.onNodeWithTag("text")
        assertEquals(2, node.links().size)
        node.performFirstLinkClick(tagged("read the guide"))
        compose.runOnIdle {
            assertEquals(0, support)
            assertEquals(1, guide)
        }
    }

    @Test
    fun appendJengaLinkCallsTheLatestCallbackThroughRememberUpdatedState() {
        var customer by mutableIntStateOf(1)
        var opened = -1
        compose.setContent {
            JengaTheme {
                val onClick = { opened = customer }
                val currentOnClick by rememberUpdatedState(onClick)
                val styles = JengaLinkDefaults.inlineStyles()
                val text = remember(styles) {
                    buildAnnotatedString {
                        append("Read the ")
                        appendJengaLink("terms", styles) { currentOnClick() }
                    }
                }
                JengaText(text = text, modifier = Modifier.testTag("text"))
            }
        }
        customer = 2
        compose.waitForIdle()
        compose.onNodeWithTag("text").performFirstLinkClick()
        compose.runOnIdle { assertEquals(2, opened) }
    }

    @Test
    fun whenOnePhraseContainsAnotherTheFirstListedClaimsTheWords() {
        val text = "Read the terms of the agreement."
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(
                    text = text,
                    links = listOf(
                        JengaInlineLink("terms of the agreement") {},
                        JengaInlineLink("terms") {},
                    ),
                    modifier = Modifier.testTag("longerFirst"),
                )
                JengaTextWithLinks(
                    text = text,
                    links = listOf(
                        JengaInlineLink("terms") {},
                        JengaInlineLink("terms of the agreement") {},
                    ),
                    modifier = Modifier.testTag("shorterFirst"),
                )
            }
        }
        // Longer phrase first: it is the link, and the shorter one has nothing left to claim.
        assertEquals(
            listOf("terms of the agreement"),
            compose.onNodeWithTag("longerFirst").links().map { text.substring(it.start, it.end) },
        )
        // Shorter phrase first: it claims its word, and the longer phrase adds no link.
        assertEquals(
            listOf("terms"),
            compose.onNodeWithTag("shorterFirst").links().map { text.substring(it.start, it.end) },
        )
    }

    @Test
    fun changingOrReorderingTheLinksRoutesClicksToTheRightCallbacks() {
        val clicked = mutableListOf<String>()
        val terms = JengaInlineLink("terms of the agreement") { clicked += "terms" }
        val privacy = JengaInlineLink("privacy policy") { clicked += "privacy" }
        val details = JengaInlineLink("details") { clicked += "details" }
        var links by mutableStateOf(listOf(terms, privacy))
        compose.setContent {
            JengaTheme {
                JengaTextWithLinks(text = sentence, links = links, modifier = Modifier.testTag("text"))
            }
        }
        val node = compose.onNodeWithTag("text")
        node.performFirstLinkClick(tagged("terms of the agreement"))
        compose.runOnIdle { assertEquals(listOf("terms"), clicked) }

        // Same links, other order.
        links = listOf(privacy, terms)
        compose.waitForIdle()
        node.performFirstLinkClick(tagged("terms of the agreement"))
        node.performFirstLinkClick(tagged("privacy policy"))
        compose.runOnIdle { assertEquals(listOf("terms", "terms", "privacy"), clicked) }

        // A different list: one link gone, one new, one kept at another index.
        links = listOf(details, privacy)
        compose.waitForIdle()
        assertEquals(
            listOf("privacy policy", "details"),
            node.links().sortedBy { it.start }.map { sentence.substring(it.start, it.end) },
        )
        node.performFirstLinkClick(tagged("details"))
        node.performFirstLinkClick(tagged("privacy policy"))
        compose.runOnIdle { assertEquals(listOf("terms", "terms", "privacy", "details", "privacy"), clicked) }
    }

    @Test
    fun inlineStylesUseTheLinkColorAnUnderlineAndTheSurroundingWeight() {
        compose.setContent {
            JengaTheme {
                val styles = JengaLinkDefaults.inlineStyles()
                assertEquals(JengaTheme.colors.brand, styles.style?.color)
                assertEquals(TextDecoration.Underline, styles.style?.textDecoration)
                assertNull(styles.style?.fontWeight)
                assertNotNull(styles.focusedStyle)
            }
        }
    }

    @Test
    fun inlineStylesFollowTheThemeLinkStyle() {
        compose.setContent {
            JengaTheme {
                val colors = JengaLinkColors(
                    content = JengaTheme.colors.info,
                    disabledContent = JengaTheme.colors.textFaint,
                )
                JengaTheme(
                    components = JengaComponentStyles(
                        link = JengaLinkStyle(
                            textStyle = TextStyle(fontWeight = FontWeight.Medium),
                            underline = false,
                            colors = colors,
                        ),
                    ),
                ) {
                    val styles = JengaLinkDefaults.inlineStyles()
                    assertEquals(colors.content, styles.style?.color)
                    assertEquals(FontWeight.Medium, styles.style?.fontWeight)
                    // The theme's underline switch is for the standalone link; inline stays underlined.
                    assertEquals(TextDecoration.Underline, styles.style?.textDecoration)
                    assertEquals(false, JengaLinkDefaults.underline)
                }
            }
        }
    }
}
