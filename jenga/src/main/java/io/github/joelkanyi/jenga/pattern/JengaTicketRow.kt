package io.github.joelkanyi.jenga.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import io.github.joelkanyi.jenga.component.avatar.JengaAvatar
import io.github.joelkanyi.jenga.component.avatar.JengaAvatarSize
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.card.JengaCardVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * **Pattern (organism)** — a ready-made attendee/ticket row composed from jenga
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

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTicketRowPreview() {
    JengaTheme { TicketRowShowcase() }
}

// Genuine RTL coverage: force the layout direction (the @Preview `locale` knob
// does not flip layout direction under the Roborazzi/scanner harness).
@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTicketRowRtlPreview() {
    JengaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TicketRowShowcase()
        }
    }
}

@Composable
private fun TicketRowShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTicketRow(
            attendeeName = "Joel Kanyi",
            detail = "VIP · TKT-2026-001",
            statusLabel = "Valid",
            statusTone = JengaBadgeTone.Success,
        )
        JengaTicketRow(
            attendeeName = "Ada Lovelace",
            detail = "Regular · TKT-2026-114",
            statusLabel = "Used",
            statusTone = JengaBadgeTone.Neutral,
        )
        JengaTicketRow(
            attendeeName = "Grace Hopper",
            detail = "Regular · TKT-2026-256",
            statusLabel = "Denied",
            statusTone = JengaBadgeTone.Error,
        )
    }
}
