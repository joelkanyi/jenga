package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A vertical stack with token-based spacing between children; use instead of a
 * hand-spaced `Column`. (Braid `Stack` / Polaris `BlockStack` / Chakra `VStack`.)
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaStackSample
 *
 * @param modifier the [Modifier] for the stack.
 * @param space vertical gap between children; defaults to `spacing.md`.
 * @param horizontalAlignment how children align horizontally.
 * @param content the children, in a [ColumnScope].
 */
@Composable
public fun JengaStack(
    modifier: Modifier = Modifier,
    space: Dp = JengaTheme.spacing.md,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space),
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

/**
 * A horizontal row with token-based spacing; use instead of a hand-spaced
 * `Row`. (Braid/Polaris `Inline` / Chakra `HStack`.) Does not wrap; see [JengaWrap].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaInlineSample
 *
 * @param modifier the [Modifier] for the row.
 * @param space horizontal gap between children; defaults to `spacing.md`.
 * @param verticalAlignment how children align vertically.
 * @param content the children, in a [RowScope].
 */
@Composable
public fun JengaInline(
    modifier: Modifier = Modifier,
    space: Dp = JengaTheme.spacing.md,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space),
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

/**
 * A horizontal layout that wraps onto new lines when it runs out of width, with
 * token spacing in both axes, for chips, tags and similar. (Braid `Inline` with
 * wrap / Chakra `Wrap`.)
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaWrapSample
 *
 * @param modifier the [Modifier] for the layout.
 * @param space horizontal gap between items; defaults to `spacing.sm`.
 * @param lineSpace vertical gap between wrapped lines; defaults to [space].
 * @param content the items, in a [FlowRowScope].
 */
@Composable
public fun JengaWrap(
    modifier: Modifier = Modifier,
    space: Dp = JengaTheme.spacing.sm,
    lineSpace: Dp = space,
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space),
        verticalArrangement = Arrangement.spacedBy(lineSpace),
        content = content,
    )
}

/**
 * A fixed-size empty gap from a spacing token. Inside a [JengaStack]/[JengaInline]
 * you rarely need this (their `space` handles gaps); use it for one-off spacing.
 *
 * @param modifier the [Modifier] for the spacer.
 * @param size the gap size; defaults to `spacing.md`.
 */
@Composable
public fun JengaSpacer(modifier: Modifier = Modifier, size: Dp = JengaTheme.spacing.md) {
    Spacer(modifier = modifier.size(size))
}
