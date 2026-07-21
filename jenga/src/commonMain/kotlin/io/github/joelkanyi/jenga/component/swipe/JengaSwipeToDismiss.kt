package io.github.joelkanyi.jenga.component.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Which way a [JengaSwipeToDismiss] row can be swiped to trigger its action. */
public enum class JengaSwipeDirection { StartToEnd, EndToStart, Both }

/** Resolved colors for a [JengaSwipeToDismiss] reveal panel. */
@Immutable
public data class JengaSwipeToDismissColors(
    /** The panel revealed behind the row as it swipes. */
    public val container: Color,
    /** The icon (and any content) on the panel. */
    public val content: Color,
)

/** Defaults and token mappings for [JengaSwipeToDismiss]. */
public object JengaSwipeToDismissDefaults {
    /**
     * Themed colors for the reveal panel: a destructive [error] band with a
     * contrasting glyph. The glyph uses the theme background (which flips with the
     * scheme) so it stays legible on the error color in both light and dark.
     */
    @Composable
    public fun colors(): JengaSwipeToDismissColors {
        val c = JengaTheme.colors
        return JengaSwipeToDismissColors(container = c.error, content = c.background)
    }
}

/**
 * A row wrapper that reveals an action panel as it is swiped and triggers
 * [onDismiss] once dragged past the threshold — the standard swipe-to-delete
 * gesture. Generic and token-driven: by default it shows a destructive error
 * panel with a [icon] glyph at the swiped end, but supply your own [background]
 * slot for a different action.
 *
 * The caller owns the list, so [onDismiss] should remove the item; the box does
 * not keep the row hidden itself. Reversible removals should pair this with a
 * snackbar Undo at the call site.
 *
 * @param onDismiss called once the row is swiped past the dismiss threshold.
 * @param modifier the [Modifier] for the whole row.
 * @param enabled whether the swipe gesture is active.
 * @param direction which way the row may be swiped.
 * @param icon the default panel glyph (ignored when [background] is supplied).
 * @param actionContentDescription accessibility label for the default action glyph.
 * @param colors the reveal-panel colors; defaults to [JengaSwipeToDismissDefaults.colors].
 * @param background an optional custom reveal panel; fills the row behind [content].
 * @param content the row content shown at rest.
 */
@Composable
public fun JengaSwipeToDismiss(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    direction: JengaSwipeDirection = JengaSwipeDirection.EndToStart,
    icon: ImageVector = JengaIcons.Trash,
    actionContentDescription: String? = null,
    colors: JengaSwipeToDismissColors = JengaSwipeToDismissDefaults.colors(),
    background: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDismiss()
                true
            } else {
                false
            }
        },
    )
    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        enableDismissFromStartToEnd = enabled && direction != JengaSwipeDirection.EndToStart,
        enableDismissFromEndToStart = enabled && direction != JengaSwipeDirection.StartToEnd,
        backgroundContent = {
            if (background != null) {
                background()
            } else {
                val alignment = when (state.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Arrangement.Start
                    else -> Arrangement.End
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.container)
                        .padding(horizontal = JengaTheme.spacing.lg),
                    horizontalArrangement = alignment,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    JengaIcon(icon, contentDescription = actionContentDescription, tint = colors.content)
                }
            }
        },
        content = { content() },
    )
}
