package io.github.joelkanyi.jenga.component.expandable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * An outlined card whose [header] row toggles an expandable [content] body — a
 * generic accordion (day/agenda cards, FAQ rows, grouped settings). The [header]
 * is laid out in a [RowScope]; the block appends an optional rotating chevron.
 * Domain-neutral and token-driven.
 *
 * @param expanded whether the [content] is currently shown.
 * @param onToggle called when the header is tapped.
 * @param modifier the [Modifier] for the card.
 * @param showChevron whether to append a chevron that rotates when expanded.
 * @param chevronContentDescription accessibility label for the chevron.
 * @param header the header content, laid out before the chevron.
 * @param content the expandable body, shown when [expanded].
 */
@Composable
public fun JengaExpandableRow(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    showChevron: Boolean = true,
    chevronContentDescription: String? = null,
    header: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val chevronRotation by animateFloatAsState(if (expanded) 90f else 0f, label = "chevron")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(JengaTheme.shapes.cardLarge)
            .background(JengaTheme.colors.surface)
            .border(1.dp, JengaTheme.colors.border, JengaTheme.shapes.cardLarge),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = JengaTheme.spacing.lg, vertical = JengaTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            header()
            if (showChevron) {
                JengaIcon(
                    imageVector = JengaIcons.ChevronRight,
                    contentDescription = chevronContentDescription,
                    tint = JengaTheme.colors.textFaint,
                    modifier = Modifier.rotate(chevronRotation),
                )
            }
        }
        if (expanded) {
            Column(Modifier.fillMaxWidth()) { content() }
        }
    }
}
