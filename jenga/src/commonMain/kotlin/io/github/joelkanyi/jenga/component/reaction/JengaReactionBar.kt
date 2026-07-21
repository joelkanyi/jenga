package io.github.joelkanyi.jenga.component.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaReactionBar]'s buttons. Override via [JengaReactionBarDefaults.colors]. */
@Immutable
public data class JengaReactionBarColors(
    public val buttonContainer: Color,
    public val buttonContent: Color,
)

/** Defaults and token mappings for [JengaReactionBar]. */
public object JengaReactionBarDefaults {
    /** Diameter of each round reaction button (touch target expands to 48dp). */
    public val ButtonSize: Dp = 40.dp

    /** Themed tonal button colors. */
    @Composable
    public fun colors(): JengaReactionBarColors {
        val c = JengaTheme.colors
        return JengaReactionBarColors(
            buttonContainer = c.surfaceVariant,
            buttonContent = c.textSecondary,
        )
    }
}

/**
 * A per-item feedback row: a positive and a negative round tonal button, then an
 * optional trailing [action] (e.g. an "Add" button). Replaces walls of feedback
 * chips with two clear one-tap affordances. Generic — the icons and the action
 * are yours; defaults are thumbs up/down.
 *
 * @param onPositive called when the positive button is tapped.
 * @param onNegative called when the negative button is tapped.
 * @param modifier the [Modifier] for the row.
 * @param positiveContentDescription accessibility label for the positive button.
 * @param negativeContentDescription accessibility label for the negative button.
 * @param positiveIcon the positive button icon.
 * @param negativeIcon the negative button icon.
 * @param colors the button color set; defaults to [JengaReactionBarDefaults.colors].
 * @param action optional trailing content, pushed to the end of the row.
 */
@Composable
public fun JengaReactionBar(
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier,
    positiveContentDescription: String? = null,
    negativeContentDescription: String? = null,
    positiveIcon: ImageVector = JengaIcons.ThumbsUp,
    negativeIcon: ImageVector = JengaIcons.ThumbsDown,
    colors: JengaReactionBarColors = JengaReactionBarDefaults.colors(),
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundButton(positiveIcon, positiveContentDescription, colors, onPositive)
        RoundButton(negativeIcon, negativeContentDescription, colors, onNegative)
        if (action != null) {
            Box(Modifier.weight(1f))
            action()
        }
    }
}

@Composable
private fun RoundButton(
    icon: ImageVector,
    contentDescription: String?,
    colors: JengaReactionBarColors,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(JengaReactionBarDefaults.ButtonSize)
            .clip(shape)
            .background(colors.buttonContainer)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        JengaIcon(imageVector = icon, contentDescription = contentDescription, tint = colors.buttonContent)
    }
}
