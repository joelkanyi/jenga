package io.github.joelkanyi.jenga.component.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Semantic tone of a [JengaBanner]. */
public enum class JengaBannerTone { Info, Success, Warning, Error }

/** Where a [JengaBanner] puts its `action`. */
public enum class JengaBannerActionPosition {
    /** At the end of the row, beside the text; the default. Takes its width from the text. */
    End,

    /** Under the message, at the start of the text column, which keeps the whole width. */
    Below,
}

/** Resolved colors for a [JengaBanner]. Override via [JengaBannerDefaults.colors]. */
@Poko
@Immutable
public class JengaBannerColors(
    public val container: Color,
    public val content: Color,
    public val icon: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        icon: Color = this.icon,
    ): JengaBannerColors = JengaBannerColors(container, content, icon)
}

/** Defaults and token mappings for [JengaBanner]. */
public object JengaBannerDefaults {
    /** Themed colors per [tone]. */
    @Composable
    public fun colors(tone: JengaBannerTone): JengaBannerColors {
        val c = JengaTheme.colors
        return when (tone) {
            JengaBannerTone.Info -> JengaBannerColors(c.infoContainer, c.onInfoContainer, c.info)
            JengaBannerTone.Success -> JengaBannerColors(c.successContainer, c.onSuccessContainer, c.success)
            JengaBannerTone.Warning -> JengaBannerColors(c.warningContainer, c.onWarningContainer, c.warning)
            JengaBannerTone.Error -> JengaBannerColors(c.errorContainer, c.onErrorContainer, c.error)
        }
    }

    /** Default leading icon per [tone]. */
    @Composable
    public fun icon(tone: JengaBannerTone): ImageVector = when (tone) {
        JengaBannerTone.Info -> JengaTheme.icons.info
        JengaBannerTone.Success -> JengaTheme.icons.check
        JengaBannerTone.Warning -> JengaTheme.icons.info
        JengaBannerTone.Error -> JengaTheme.icons.close
    }
}

/**
 * An inline banner / alert: a tinted, full-width message with a leading status
 * icon, optional title, optional trailing action and optional close ✕.
 *
 * It does not manage its own visibility: to make it dismissible, pass [onDismiss] and stop
 * showing the banner from it.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerSample
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerDismissSample
 *
 * @param message the banner body text.
 * @param modifier the [Modifier] for this banner.
 * @param tone the semantic tone; see [JengaBannerTone].
 * @param title optional bold title above the message.
 * @param action optional action slot (e.g. a ghost [io.github.joelkanyi.jenga.component.button.JengaButton]
 *   or a [io.github.joelkanyi.jenga.component.link.JengaLink]); placed by [actionPosition].
 * @param colors the color set; defaults to [JengaBannerDefaults.colors] for [tone].
 * @param actionPosition where the [action] goes: [JengaBannerActionPosition.End] (the default)
 *   beside the text, or [JengaBannerActionPosition.Below] under the message. Beside the text, the
 *   action takes its width from the text column, so keep its label to one short word when the banner
 *   also has the close ✕, or place it below.
 * @param onDismiss called when the close ✕ is tapped. The ✕ is shown at the end, after the
 *   [action], level with the leading icon, in [JengaBannerColors.content], when this is set together
 *   with [dismissContentDescription]; hide the banner from it.
 * @param dismissContentDescription accessibility label for the close ✕ (e.g. "Dismiss"), in the
 *   user's language.
 */
@Composable
public fun JengaBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: JengaBannerTone = JengaBannerTone.Info,
    title: String? = null,
    action: (@Composable () -> Unit)? = null,
    colors: JengaBannerColors = JengaBannerDefaults.colors(tone),
    actionPosition: JengaBannerActionPosition = JengaBannerActionPosition.End,
    onDismiss: (() -> Unit)? = null,
    dismissContentDescription: String? = null,
) {
    val spacing = JengaTheme.spacing.md
    val dismissible = onDismiss != null && dismissContentDescription != null
    // Screen readers order siblings by where they are, and the action and the ✕ start level with
    // the first line, so they would be read before the message. With either of them, the banner
    // becomes one traversal group that reads all the text, then the action, then the ✕.
    val ordered = action != null || dismissible
    val themedAction: (@Composable () -> Unit)? = action?.let {
        {
            CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
                it()
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(JengaTheme.shapes.card)
            .background(colors.container)
            .padding(JengaTheme.spacing.lg)
            .then(if (ordered) Modifier.semantics { isTraversalGroup = true } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.Top,
    ) {
        JengaIcon(
            imageVector = JengaBannerDefaults.icon(tone),
            contentDescription = null,
            tint = colors.icon,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .then(if (ordered) Modifier.traversalGroup(BannerTextTraversalIndex) else Modifier),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
        ) {
            if (title != null) {
                JengaText(text = title, style = JengaTheme.typography.titleSmall, color = colors.content)
            }
            JengaText(text = message, style = JengaTheme.typography.bodySmall, color = colors.content)
            if (themedAction != null && actionPosition == JengaBannerActionPosition.Below) {
                Box(modifier = Modifier.padding(top = JengaTheme.spacing.sm)) {
                    themedAction()
                }
            }
        }
        if (themedAction != null && actionPosition == JengaBannerActionPosition.End) {
            // Laid out like the banner's own row, so an action made of several pieces looks the
            // same as when they were the row's direct children.
            Row(
                modifier = Modifier.traversalGroup(BannerActionTraversalIndex),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.Top,
            ) {
                themedAction()
            }
        }
        if (onDismiss != null && dismissContentDescription != null) {
            val iconSize = JengaTheme.sizing.iconMedium
            Box(
                modifier = Modifier
                    .traversalGroup(BannerDismissTraversalIndex)
                    .dismissTouchTarget(
                        touchTarget = JengaTheme.sizing.minTouchTarget,
                        iconSize = iconSize,
                        contentPadding = PaddingValues(JengaTheme.spacing.lg),
                        spacing = spacing,
                    )
                    .clip(JengaTheme.shapes.pill)
                    .clickable(role = Role.Button, onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                JengaIcon(
                    imageVector = JengaTheme.icons.close,
                    contentDescription = dismissContentDescription,
                    tint = colors.content,
                    size = iconSize,
                )
            }
        }
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: JengaBannerTone = JengaBannerTone.Info,
    title: String? = null,
    action: (@Composable () -> Unit)? = null,
    colors: JengaBannerColors = JengaBannerDefaults.colors(tone),
) {
    JengaBanner(
        message = message,
        modifier = modifier,
        tone = tone,
        title = title,
        action = action,
        colors = colors,
        actionPosition = JengaBannerActionPosition.End,
        onDismiss = null,
        dismissContentDescription = null,
    )
}

// Reading order inside a banner that has an action or a close ✕: text, action, ✕.
private const val BannerTextTraversalIndex = 0f
private const val BannerActionTraversalIndex = 1f
private const val BannerDismissTraversalIndex = 2f

/** Reads everything inside this node together, at [index] among the banner's parts. */
private fun Modifier.traversalGroup(index: Float): Modifier = semantics {
    isTraversalGroup = true
    traversalIndex = index
}

/**
 * Gives the close ✕ its full [touchTarget] without pushing the glyph away from the edge or adding
 * the target's height to the banner's: the target is measured at full size, but the part around
 * the [iconSize] glyph that fits in the row's [spacing] (at the start) and in [contentPadding] (top,
 * bottom and end) is not reported to the row. Top and bottom give up the same amount, the smaller
 * of the two paddings. With the default padding only the glyph is reported, so in the top-aligned
 * row it sits level with the leading icon and ends where the text would, a one-line banner is as
 * tall as it is without the ✕, and the whole target stays inside the banner, so none of it is
 * clipped away.
 *
 * Same contract as the helper of the same name in `JengaSnackbar`; the two should become one.
 */
private fun Modifier.dismissTouchTarget(
    touchTarget: Dp,
    iconSize: Dp,
    contentPadding: PaddingValues,
    spacing: Dp,
): Modifier = layout { measurable, _ ->
    val target = touchTarget.roundToPx()
    val inset = ((touchTarget - iconSize) / 2).coerceAtLeast(0.dp)
    val start = min(spacing, inset).roundToPx()
    val end = min(contentPadding.calculateEndPadding(layoutDirection), inset).roundToPx()
    val vertical = min(min(contentPadding.calculateTopPadding(), contentPadding.calculateBottomPadding()), inset).roundToPx()
    val placeable = measurable.measure(Constraints.fixed(target, target))
    layout(target - start - end, target - 2 * vertical) {
        placeable.placeRelative(-start, -vertical)
    }
}
