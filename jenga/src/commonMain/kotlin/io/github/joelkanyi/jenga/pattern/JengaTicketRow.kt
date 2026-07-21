package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.avatar.JengaAvatar
import io.github.joelkanyi.jenga.component.avatar.JengaAvatarSize
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.card.JengaCardVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * **Pattern (organism)**, a ready-made attendee/ticket row composed from jenga
 * blocks ([JengaCard] + [JengaAvatar] + [JengaText] + [JengaBadge]).
 *
 * Patterns live one layer above blocks (Foundations → Blocks → Patterns): they
 * encode a recurring *arrangement* of blocks so feature code doesn't re-assemble
 * it each time. Stateless and data-driven, like every jenga block.
 *
 * @param attendeeName attendee's full name (also drives the avatar initials).
 * @param detail secondary line, e.g. ticket type or order id.
 * @param statusLabel the status pill text (e.g. "Valid", "Used").
 * @param statusTone the status pill tone; see [JengaBadgeTone].
 * @param modifier the [Modifier] for this row.
 * @param onClick optional row click handler.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTicketRowSample
 */
@Composable
public fun JengaTicketRow(
    attendeeName: String,
    detail: String,
    statusLabel: String,
    statusTone: JengaBadgeTone,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    JengaCard(
        modifier = modifier,
        variant = JengaCardVariant.Outlined,
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(JengaTheme.spacing.lg),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
        ) {
            JengaAvatar(name = attendeeName, size = JengaAvatarSize.Medium)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
            ) {
                JengaText(text = attendeeName, style = JengaTheme.typography.titleSmall, maxLines = 1)
                JengaText(
                    text = detail,
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                    maxLines = 1,
                )
            }
            JengaBadge(text = statusLabel, tone = statusTone)
        }
    }
}
