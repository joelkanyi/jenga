package io.github.joelkanyi.jenga.component.link

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaInlineLinkPreview() {
    JengaTheme { InlineLinkShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaInlineLinkRtlPreview() {
    JengaTheme { RtlPreview { InlineLinkShowcase() } }
}

@Composable
private fun InlineLinkShowcase() {
    JengaStack(
        space = JengaTheme.spacing.lg,
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .width(300.dp)
            .padding(JengaTheme.spacing.xl),
    ) {
        // One link in the middle of a sentence.
        JengaTextWithLinks(
            text = "Read the terms of the agreement before you continue.",
            links = listOf(JengaInlineLink(text = "terms of the agreement", onClick = {})),
        )
        // A link at the end of a sentence, on secondary text.
        JengaTextWithLinks(
            text = "By continuing, the customer accepts the terms of the agreement.",
            links = listOf(JengaInlineLink(text = "terms of the agreement", onClick = {})),
            color = JengaTheme.colors.textSecondary,
            style = JengaTheme.typography.bodySmall,
        )
        // Two links in one sentence.
        JengaTextWithLinks(
            text = "See the terms of the agreement and the privacy policy for details.",
            links = listOf(
                JengaInlineLink(text = "terms of the agreement", onClick = {}),
                JengaInlineLink(text = "privacy policy", onClick = {}),
            ),
        )
        // A link long enough to wrap across two lines.
        JengaTextWithLinks(
            text = "Payment is due as set out in the customer financing and repayment schedule agreement.",
            links = listOf(
                JengaInlineLink(text = "customer financing and repayment schedule agreement", onClick = {}),
            ),
        )
        // A disabled link.
        JengaTextWithLinks(
            text = "Reports are not available yet. Download report.",
            links = listOf(JengaInlineLink(text = "Download report", enabled = false, onClick = {})),
        )
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaAppendLinkPreview() {
    JengaTheme {
        val linkStyles = JengaLinkDefaults.inlineStyles()
        val text = remember(linkStyles) {
            buildAnnotatedString {
                append("Need help? ")
                appendJengaLink(text = "Call support", styles = linkStyles, onClick = {})
                append(" or ")
                appendJengaLink(text = "read the guide", styles = linkStyles, onClick = {})
                append(".")
            }
        }
        JengaText(
            text = text,
            modifier = Modifier
                .background(JengaTheme.colors.background)
                .padding(JengaTheme.spacing.xl),
            style = JengaTheme.typography.titleMedium,
        )
    }
}
