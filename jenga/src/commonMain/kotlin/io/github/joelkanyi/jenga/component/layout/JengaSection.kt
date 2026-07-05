package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.pattern.JengaSectionHeader
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A titled section: an optional header (title + supporting line + trailing
 * action, via [JengaSectionHeader]) above a token-spaced body. Use to structure
 * a screen into labelled blocks without re-plumbing Columns and headers.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSectionSample
 *
 * @param modifier the [Modifier] for the section.
 * @param title optional section title; when null no header is shown.
 * @param subtitle optional supporting line under the title.
 * @param actionLabel optional trailing action label.
 * @param onActionClick called when the action is tapped (shown with [actionLabel]).
 * @param space vertical gap between the header and body items; defaults to `spacing.md`.
 * @param content the section body, in a [ColumnScope].
 */
@Composable
public fun JengaSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    space: Dp = JengaTheme.spacing.md,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space),
    ) {
        if (title != null) {
            JengaSectionHeader(
                title = title,
                subtitle = subtitle,
                actionLabel = actionLabel,
                onActionClick = onActionClick,
            )
        }
        content()
    }
}
