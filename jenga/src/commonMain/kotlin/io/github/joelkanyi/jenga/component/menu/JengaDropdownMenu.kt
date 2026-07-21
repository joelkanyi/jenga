package io.github.joelkanyi.jenga.component.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/**
 * A dropdown menu anchored to its caller, themed with Jenga tokens. Place
 * [JengaDropdownMenuItem]s in [content]. Control visibility via [expanded].
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaDropdownMenuSample
 *
 * @param expanded whether the menu is shown.
 * @param onDismissRequest called when the menu should close (outside tap / back).
 * @param modifier the [Modifier] for the menu surface.
 * @param content the menu items, in a [ColumnScope].
 */
@Composable
public fun JengaDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = content,
    )
}

/**
 * A single item in a [JengaDropdownMenu].
 *
 * @param text the item label.
 * @param onClick called when the item is tapped.
 * @param modifier the [Modifier] for this item.
 * @param leadingIcon optional leading icon (inherits content color).
 * @param enabled whether the item is interactive.
 */
@Composable
public fun JengaDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val contentColor = if (enabled) JengaTheme.colors.textPrimary else JengaTheme.colors.contentDisabled
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        if (leadingIcon != null) {
            CompositionLocalProvider(LocalJengaContentColor provides contentColor) { leadingIcon() }
        }
        JengaText(text = text, color = contentColor, maxLines = 1)
    }
}
