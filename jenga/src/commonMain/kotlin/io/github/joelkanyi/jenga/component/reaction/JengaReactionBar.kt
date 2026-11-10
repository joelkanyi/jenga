package io.github.joelkanyi.jenga.component.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
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
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.theme.JengaTheme

/** The reaction a [JengaReactionBar] shows as chosen. */
public enum class JengaReaction {
    Positive,
    Negative,
}

/**
 * Resolved colors for a [JengaReactionBar]'s buttons. Override via [JengaReactionBarDefaults.colors].
 * The selected colors default to the unselected ones, so a hand-built set shows no selection until
 * they are given.
 */
@Poko
@Immutable
public class JengaReactionBarColors(
    public val buttonContainer: Color,
    public val buttonContent: Color,
    public val selectedContainer: Color = buttonContainer,
    public val selectedContent: Color = buttonContent,
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(
        buttonContainer: Color,
        buttonContent: Color,
    ) : this(buttonContainer, buttonContent, selectedContainer = buttonContainer, selectedContent = buttonContent)

    public fun copy(
        buttonContainer: Color = this.buttonContainer,
        buttonContent: Color = this.buttonContent,
        selectedContainer: Color = this.selectedContainer,
        selectedContent: Color = this.selectedContent,
    ): JengaReactionBarColors = JengaReactionBarColors(buttonContainer, buttonContent, selectedContainer, selectedContent)

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(
        buttonContainer: Color = this.buttonContainer,
        buttonContent: Color = this.buttonContent,
    ): JengaReactionBarColors = copy(buttonContainer, buttonContent, this.selectedContainer, this.selectedContent)
}

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
            selectedContainer = c.brandSubtle,
            selectedContent = c.brand,
        )
    }
}

/**
 * A per-item feedback row: a positive and a negative round tonal button, then an
 * optional trailing [action] (e.g. an "Add" button). Replaces walls of feedback
 * chips with two clear one-tap affordances. Generic: the icons and the action
 * are yours; defaults are thumbs up/down.
 *
 * @param onPositive called when the positive button is tapped.
 * @param onNegative called when the negative button is tapped.
 * @param modifier the [Modifier] for the row.
 * @param selected the reaction shown as chosen, or null for none. The bar does not change it
 *   itself: set it from [onPositive] and [onNegative].
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
    selected: JengaReaction? = null,
    positiveContentDescription: String? = null,
    negativeContentDescription: String? = null,
    positiveIcon: ImageVector = JengaTheme.icons.thumbsUp,
    negativeIcon: ImageVector = JengaTheme.icons.thumbsDown,
    colors: JengaReactionBarColors = JengaReactionBarDefaults.colors(),
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundButton(positiveIcon, positiveContentDescription, selected == JengaReaction.Positive, colors, onPositive)
        RoundButton(negativeIcon, negativeContentDescription, selected == JengaReaction.Negative, colors, onNegative)
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
    selected: Boolean,
    colors: JengaReactionBarColors,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(percent = 50)
    val container = if (selected) colors.selectedContainer else colors.buttonContainer
    val content = if (selected) colors.selectedContent else colors.buttonContent
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(JengaReactionBarDefaults.ButtonSize)
            .clip(shape)
            .background(container)
            .selectable(selected = selected, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        JengaIcon(imageVector = icon, contentDescription = contentDescription, tint = content)
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaReactionBar(
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier,
    positiveContentDescription: String? = null,
    negativeContentDescription: String? = null,
    positiveIcon: ImageVector = JengaTheme.icons.thumbsUp,
    negativeIcon: ImageVector = JengaTheme.icons.thumbsDown,
    colors: JengaReactionBarColors = JengaReactionBarDefaults.colors(),
    action: (@Composable () -> Unit)? = null,
) {
    JengaReactionBar(
        onPositive = onPositive,
        onNegative = onNegative,
        modifier = modifier,
        selected = null,
        positiveContentDescription = positiveContentDescription,
        negativeContentDescription = negativeContentDescription,
        positiveIcon = positiveIcon,
        negativeIcon = negativeIcon,
        colors = colors,
        action = action,
    )
}
