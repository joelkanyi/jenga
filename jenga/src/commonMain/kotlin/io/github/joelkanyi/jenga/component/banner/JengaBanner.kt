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
import io.github.joelkanyi.jenga.component.icon.JengaIcons
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

/** Visual weight of a [JengaBanner]. */
public enum class JengaBannerEmphasis {
    /** A 20dp tone icon level with the first line, and the title and message in the tone's content color. */
    Default,

    /**
     * The result of a check that decides what happens next (a credit decision, an ID match): a
     * 28dp result icon centred on the text, and the message in the primary text color under a
     * title in the tone's content color.
     */
    Prominent,
}

/**
 * Resolved colors for a [JengaBanner]. Override via [JengaBannerDefaults.colors].
 *
 * @property container the background.
 * @property content the title, the message of a [JengaBannerEmphasis.Default] banner, the action,
 *   the close ✕, and the leading icon of a [JengaBannerEmphasis.Prominent] banner.
 * @property icon the leading icon of a [JengaBannerEmphasis.Default] banner. A prominent banner
 *   draws its larger result icon in [content] instead, which stands out more from [container].
 * @property prominentMessage the message of a [JengaBannerEmphasis.Prominent] banner. Defaults to
 *   [content], so a hand-built set stays legible on its own [container]. It does not follow a
 *   later `copy(content = …)`, so set it together with [container] and [content] when customising.
 */
@Poko
@Immutable
public class JengaBannerColors(
    public val container: Color,
    public val content: Color,
    public val icon: Color,
    public val prominentMessage: Color = content,
) {
    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(
        container: Color,
        content: Color,
        icon: Color,
    ) : this(container, content, icon, prominentMessage = content)

    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        icon: Color = this.icon,
        prominentMessage: Color = this.prominentMessage,
    ): JengaBannerColors = JengaBannerColors(container, content, icon, prominentMessage)

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
        icon: Color = this.icon,
    ): JengaBannerColors = copy(container, content, icon, this.prominentMessage)
}

/** Defaults and token mappings for [JengaBanner]. */
public object JengaBannerDefaults {
    /** Themed colors per [tone]. */
    @Composable
    public fun colors(tone: JengaBannerTone): JengaBannerColors {
        val c = JengaTheme.colors
        return when (tone) {
            JengaBannerTone.Info -> JengaBannerColors(c.infoContainer, c.onInfoContainer, c.info, c.textPrimary)
            JengaBannerTone.Success -> JengaBannerColors(c.successContainer, c.onSuccessContainer, c.success, c.textPrimary)
            JengaBannerTone.Warning -> JengaBannerColors(c.warningContainer, c.onWarningContainer, c.warning, c.textPrimary)
            JengaBannerTone.Error -> JengaBannerColors(c.errorContainer, c.onErrorContainer, c.error, c.textPrimary)
        }
    }

    /** Default leading icon per [tone] of a [JengaBannerEmphasis.Default] banner. */
    @Composable
    public fun icon(tone: JengaBannerTone): ImageVector = when (tone) {
        JengaBannerTone.Info -> JengaTheme.icons.info
        JengaBannerTone.Success -> JengaTheme.icons.check
        JengaBannerTone.Warning -> JengaTheme.icons.info
        JengaBannerTone.Error -> JengaTheme.icons.close
    }

    /**
     * Default leading icon per [tone] and [emphasis]. A [JengaBannerEmphasis.Prominent] banner
     * shows the result of a check: [JengaIcons.BadgeCheck] for Success, [JengaIcons.AlertCircle]
     * for Warning, [JengaIcons.Ban] for Error, and the theme's info glyph for Info.
     */
    @Composable
    public fun icon(tone: JengaBannerTone, emphasis: JengaBannerEmphasis): ImageVector = when (emphasis) {
        JengaBannerEmphasis.Default -> icon(tone)
        JengaBannerEmphasis.Prominent -> when (tone) {
            JengaBannerTone.Info -> JengaTheme.icons.info
            JengaBannerTone.Success -> JengaIcons.BadgeCheck
            JengaBannerTone.Warning -> JengaIcons.AlertCircle
            JengaBannerTone.Error -> JengaIcons.Ban
        }
    }
}

/**
 * An inline banner / alert: a tinted, full-width message with a leading status
 * icon, optional title, optional trailing action and optional close ✕.
 *
 * It does not manage its own visibility: to make it dismissible, pass [onDismiss] and stop
 * showing the banner from it.
 *
 * For the result of a check at the top of a screen (credit approved, ID not matched), use
 * [JengaBannerEmphasis.Prominent] with a [title]: a larger result icon centred on the text, and
 * the message in the primary text color. For a result with an amount and a progress bar, use
 * [io.github.joelkanyi.jenga.component.verdict.JengaVerdictBar] instead.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerSample
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerDismissSample
 * @sample io.github.joelkanyi.jenga.samples.JengaBannerProminentSample
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
 *   also has the close ✕, or place it below. Beside the text, it starts level with the first line,
 *   or is centred on the text in a [JengaBannerEmphasis.Prominent] banner. Below, it starts where
 *   the text does, and the leading icon of a prominent banner stays centred on the title and
 *   message alone.
 * @param onDismiss called when the close ✕ is tapped. The ✕ is shown at the end, after the
 *   [action], in the top corner of the content (level with the leading icon of a
 *   [JengaBannerEmphasis.Default] banner), in [JengaBannerColors.content], when this is set
 *   together with [dismissContentDescription]; hide the banner from it.
 * @param dismissContentDescription accessibility label for the close ✕ (e.g. "Dismiss"), in the
 *   user's language.
 * @param emphasis the visual weight: [JengaBannerEmphasis.Default], or
 *   [JengaBannerEmphasis.Prominent] for the result of a check.
 * @param icon the leading icon; defaults to [JengaBannerDefaults.icon] for [tone] and [emphasis].
 *   It is tinted with [JengaBannerColors.icon], or with [JengaBannerColors.content] in a
 *   [JengaBannerEmphasis.Prominent] banner.
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
    emphasis: JengaBannerEmphasis = JengaBannerEmphasis.Default,
    icon: ImageVector = JengaBannerDefaults.icon(tone, emphasis),
) {
    val spacing = JengaTheme.spacing.md
    val prominent = emphasis == JengaBannerEmphasis.Prominent
    val leadingIconSize = if (prominent) ProminentIconSize else JengaTheme.sizing.iconMedium
    // A prominent banner centres its icon and an action beside the text on the text block; the
    // default one starts them level with the first line.
    val alignment = if (prominent) Alignment.CenterVertically else Alignment.Top
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
        Column(
            modifier = Modifier
                .weight(1f)
                .align(alignment)
                .then(if (ordered) Modifier.traversalGroup(BannerTextTraversalIndex) else Modifier),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
        ) {
            // The icon and the text are a row of their own, so that an action under the message
            // does not move the icon of a prominent banner off the centre of the text.
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = alignment,
            ) {
                JengaIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (prominent) colors.content else colors.icon,
                    size = leadingIconSize,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
                ) {
                    if (title != null) {
                        JengaText(text = title, style = JengaTheme.typography.titleSmall, color = colors.content)
                    }
                    JengaText(
                        text = message,
                        style = JengaTheme.typography.bodySmall,
                        color = if (prominent) colors.prominentMessage else colors.content,
                    )
                }
            }
            if (themedAction != null && actionPosition == JengaBannerActionPosition.Below) {
                Box(modifier = Modifier.padding(start = leadingIconSize + spacing, top = JengaTheme.spacing.sm)) {
                    themedAction()
                }
            }
        }
        if (themedAction != null && actionPosition == JengaBannerActionPosition.End) {
            // Laid out like the banner's own row, so an action made of several pieces looks the
            // same as when they were the row's direct children.
            Row(
                modifier = Modifier
                    .align(alignment)
                    .traversalGroup(BannerActionTraversalIndex),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = alignment,
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

/** The leading icon of a [JengaBannerEmphasis.Prominent] banner; larger than any icon size token. */
private val ProminentIconSize = 28.dp

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
 * row it sits in the top corner of the content, level with the leading icon of a default banner,
 * and ends where the text would, a one-line banner is as tall as it is without the ✕, and the
 * whole target stays inside the banner, so none of it is clipped away.
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
