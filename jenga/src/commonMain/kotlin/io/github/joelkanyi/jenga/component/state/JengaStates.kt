package io.github.joelkanyi.jenga.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for the empty/error state blocks. Override via [JengaStateDefaults.colors]. */
@Poko
@Immutable
public class JengaStateColors(
    public val title: Color,
    public val description: Color,
) {
    public fun copy(
        title: Color = this.title,
        description: Color = this.description,
    ): JengaStateColors = JengaStateColors(title, description)
}

/** How a [JengaEmptyState] or [JengaErrorState] lays out its content and actions. */
public enum class JengaStateLayout {
    /**
     * As tall as its content, with the actions under the description at their own width. For a
     * section, a card or a sheet.
     */
    Inline,

    /**
     * Fills the bounds it is given: the icon, title and description are centered in the space above
     * the actions, and the actions are full-width buttons pinned at the bottom. For a state that
     * replaces the whole screen (no connection, update required, session expired).
     *
     * It does not pad for system bars: pass the scaffold's content padding through `modifier`, or
     * add `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`.
     *
     * It needs a bounded height. Inside a vertically scrolling parent there is no height to fill,
     * so it wraps its content (nothing is centered or pinned); give it a height there.
     */
    FillScreen,
}

/** Defaults and token mappings for [JengaEmptyState] / [JengaErrorState]. */
/**
 * Theme-wide style for every [JengaEmptyState] and [JengaErrorState], set through
 * [io.github.joelkanyi.jenga.theme.JengaComponentStyles.state]. A `null` value keeps Jenga's
 * default; arguments passed to a call still win.
 *
 * @property titleStyle the title text style.
 * @property descriptionStyle the description text style.
 * @property contentPadding padding around the state.
 * @property spacing the gap between the icon, title and description.
 * @property actionSpacing the gap above the action buttons; falls back to [spacing].
 * @property actionSize the size of the action buttons.
 * @property colors the color set.
 */
@Poko
@Immutable
public class JengaStateStyle(
    public val titleStyle: TextStyle? = null,
    public val descriptionStyle: TextStyle? = null,
    public val contentPadding: PaddingValues? = null,
    public val spacing: Dp? = null,
    public val actionSpacing: Dp? = null,
    public val actionSize: JengaButtonSize? = null,
    public val colors: JengaStateColors? = null,
) {
    public fun copy(
        titleStyle: TextStyle? = this.titleStyle,
        descriptionStyle: TextStyle? = this.descriptionStyle,
        contentPadding: PaddingValues? = this.contentPadding,
        spacing: Dp? = this.spacing,
        actionSpacing: Dp? = this.actionSpacing,
        actionSize: JengaButtonSize? = this.actionSize,
        colors: JengaStateColors? = this.colors,
    ): JengaStateStyle = JengaStateStyle(titleStyle, descriptionStyle, contentPadding, spacing, actionSpacing, actionSize, colors)
}

/** Defaults for [JengaEmptyState] and [JengaErrorState]. Each one follows the theme's [JengaStateStyle]. */
public object JengaStateDefaults {
    /** Title text style. */
    public val titleStyle: TextStyle
        @Composable get() = JengaTheme.components.state?.titleStyle ?: JengaTheme.typography.titleLarge

    /** Description text style. */
    public val descriptionStyle: TextStyle
        @Composable get() = JengaTheme.components.state?.descriptionStyle ?: JengaTheme.typography.bodySmall

    /** Padding around the state. */
    public val contentPadding: PaddingValues
        @Composable get() = JengaTheme.components.state?.contentPadding ?: PaddingValues(JengaTheme.spacing.xl)

    /** Gap between the icon, title and description. */
    public val spacing: Dp
        @Composable get() = JengaTheme.components.state?.spacing ?: JengaTheme.spacing.md

    /** Size of the action buttons. */
    public val actionSize: JengaButtonSize
        @Composable get() = JengaTheme.components.state?.actionSize ?: JengaButtonSize.Medium

    /** Themed colors. */
    @Composable
    public fun colors(): JengaStateColors = JengaTheme.components.state?.colors ?: JengaStateColors(
        title = JengaTheme.colors.textPrimary,
        description = JengaTheme.colors.textMuted,
    )
}

/**
 * A centered empty-state placeholder: optional icon, title, message, and up to two actions.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaEmptyStateSample
 *
 * @param title the headline (e.g. "No scans yet").
 * @param modifier the [Modifier] for this state.
 * @param description optional supporting message.
 * @param icon optional icon/illustration slot above the title.
 * @param actionLabel optional action button label; shown with [onAction].
 * @param onAction called when the action is tapped.
 * @param actionVariant the action button's variant; defaults to Outline.
 * @param colors the color set; defaults to [JengaStateDefaults.colors].
 * @param titleStyle the [title] text style.
 * @param descriptionStyle the [description] text style.
 * @param contentPadding padding around the state.
 * @param spacing the gap between the icon, title and description.
 * @param actionSpacing the gap above the action buttons; defaults to the theme's
 *   [JengaStateStyle.actionSpacing], else [spacing].
 * @param actionSize the size of the action buttons.
 * @param secondaryActionLabel optional label of a second, quieter action under the first (e.g.
 *   "Contact support"); shown with [onSecondaryAction]. It does not depend on the first action:
 *   given alone, it is the only button, in [secondaryActionVariant] and in the first one's place.
 * @param onSecondaryAction called when the second action is tapped.
 * @param secondaryActionVariant the second action button's variant; defaults to Ghost.
 * @param layout [JengaStateLayout.Inline] (the default) for a section or a sheet, or
 *   [JengaStateLayout.FillScreen] to fill the bounds given by [modifier]'s parent, center the icon,
 *   title and description in the space above the actions, and pin the actions at the bottom as
 *   full-width buttons with 16dp at the sides and below.
 *   In FillScreen [contentPadding] applies to the centered block only; when that block is taller
 *   than the space above the buttons it scrolls there, a hairline marks that more is below, and
 *   the buttons stay in view. FillScreen needs a bounded height: inside a vertically scrolling
 *   parent it wraps its content (nothing is centered or pinned), so give it a height there. The
 *   state does not pad for system bars: inside a
 *   [io.github.joelkanyi.jenga.component.scaffold.JengaScaffold] pass the content padding through
 *   [modifier], elsewhere add `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`. Button
 *   labels stay on one line, so keep them short.
 */
@Composable
public fun JengaEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Outline,
    colors: JengaStateColors = JengaStateDefaults.colors(),
    titleStyle: TextStyle = JengaStateDefaults.titleStyle,
    descriptionStyle: TextStyle = JengaStateDefaults.descriptionStyle,
    contentPadding: PaddingValues = JengaStateDefaults.contentPadding,
    spacing: Dp = JengaStateDefaults.spacing,
    actionSpacing: Dp = JengaTheme.components.state?.actionSpacing ?: spacing,
    actionSize: JengaButtonSize = JengaStateDefaults.actionSize,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionVariant: JengaButtonVariant = JengaButtonVariant.Ghost,
    layout: JengaStateLayout = JengaStateLayout.Inline,
) {
    StateLayout(
        title = title,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        actionVariant = actionVariant,
        onAction = onAction,
        colors = colors,
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle,
        contentPadding = contentPadding,
        spacing = spacing,
        actionSpacing = actionSpacing,
        actionSize = actionSize,
        secondaryActionLabel = secondaryActionLabel,
        secondaryActionVariant = secondaryActionVariant,
        onSecondaryAction = onSecondaryAction,
        layout = layout,
        modifier = modifier,
    )
}

/**
 * A centered error-state placeholder with a retry-style action and an optional second one.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaErrorStateSample
 * @sample io.github.joelkanyi.jenga.samples.JengaErrorStateFillScreenSample
 *
 * @param title the headline (e.g. "Something went wrong").
 * @param modifier the [Modifier] for this state.
 * @param description optional supporting message.
 * @param icon optional icon/illustration slot above the title.
 * @param actionLabel optional action button label (e.g. "Retry"); shown with [onAction].
 * @param onAction called when the action is tapped.
 * @param actionVariant the action button's variant; defaults to Primary.
 * @param colors the color set; defaults to [JengaStateDefaults.colors].
 * @param titleStyle the [title] text style.
 * @param descriptionStyle the [description] text style.
 * @param contentPadding padding around the state.
 * @param spacing the gap between the icon, title and description.
 * @param actionSpacing the gap above the action buttons; defaults to the theme's
 *   [JengaStateStyle.actionSpacing], else [spacing].
 * @param actionSize the size of the action buttons.
 * @param secondaryActionLabel optional label of a second, quieter action under the first (e.g.
 *   "Contact support"); shown with [onSecondaryAction]. It does not depend on the first action:
 *   given alone, it is the only button, in [secondaryActionVariant] and in the first one's place.
 * @param onSecondaryAction called when the second action is tapped.
 * @param secondaryActionVariant the second action button's variant; defaults to Ghost.
 * @param layout [JengaStateLayout.Inline] (the default) for a section or a sheet, or
 *   [JengaStateLayout.FillScreen] to fill the bounds given by [modifier]'s parent, center the icon,
 *   title and description in the space above the actions, and pin the actions at the bottom as
 *   full-width buttons with 16dp at the sides and below.
 *   In FillScreen [contentPadding] applies to the centered block only; when that block is taller
 *   than the space above the buttons it scrolls there, a hairline marks that more is below, and
 *   the buttons stay in view. FillScreen needs a bounded height: inside a vertically scrolling
 *   parent it wraps its content (nothing is centered or pinned), so give it a height there. The
 *   state does not pad for system bars: inside a
 *   [io.github.joelkanyi.jenga.component.scaffold.JengaScaffold] pass the content padding through
 *   [modifier], elsewhere add `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`. Button
 *   labels stay on one line, so keep them short.
 */
@Composable
public fun JengaErrorState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Primary,
    colors: JengaStateColors = JengaStateDefaults.colors(),
    titleStyle: TextStyle = JengaStateDefaults.titleStyle,
    descriptionStyle: TextStyle = JengaStateDefaults.descriptionStyle,
    contentPadding: PaddingValues = JengaStateDefaults.contentPadding,
    spacing: Dp = JengaStateDefaults.spacing,
    actionSpacing: Dp = JengaTheme.components.state?.actionSpacing ?: spacing,
    actionSize: JengaButtonSize = JengaStateDefaults.actionSize,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionVariant: JengaButtonVariant = JengaButtonVariant.Ghost,
    layout: JengaStateLayout = JengaStateLayout.Inline,
) {
    StateLayout(
        title = title,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        actionVariant = actionVariant,
        onAction = onAction,
        colors = colors,
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle,
        contentPadding = contentPadding,
        spacing = spacing,
        actionSpacing = actionSpacing,
        actionSize = actionSize,
        secondaryActionLabel = secondaryActionLabel,
        secondaryActionVariant = secondaryActionVariant,
        onSecondaryAction = onSecondaryAction,
        layout = layout,
        modifier = modifier,
    )
}

@Composable
private fun StateLayout(
    title: String,
    description: String?,
    icon: (@Composable () -> Unit)?,
    actionLabel: String?,
    actionVariant: JengaButtonVariant,
    onAction: (() -> Unit)?,
    colors: JengaStateColors,
    titleStyle: TextStyle,
    descriptionStyle: TextStyle,
    contentPadding: PaddingValues,
    spacing: Dp,
    actionSpacing: Dp,
    actionSize: JengaButtonSize,
    secondaryActionLabel: String?,
    secondaryActionVariant: JengaButtonVariant,
    onSecondaryAction: (() -> Unit)?,
    layout: JengaStateLayout,
    modifier: Modifier = Modifier,
) {
    val hasAction = actionLabel != null && onAction != null
    val hasSecondaryAction = secondaryActionLabel != null && onSecondaryAction != null
    val hasActions = hasAction || hasSecondaryAction
    val message: @Composable () -> Unit = {
        if (icon != null) icon()
        JengaText(
            text = title,
            style = titleStyle,
            color = colors.title,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            JengaText(
                text = description,
                style = descriptionStyle,
                color = colors.description,
                textAlign = TextAlign.Center,
            )
        }
    }
    val actions: @Composable (Modifier) -> Unit = { buttonModifier ->
        if (actionLabel != null && onAction != null) {
            JengaButton(
                text = actionLabel,
                onClick = onAction,
                modifier = buttonModifier,
                variant = actionVariant,
                size = actionSize,
            )
        }
        if (secondaryActionLabel != null && onSecondaryAction != null) {
            JengaButton(
                text = secondaryActionLabel,
                onClick = onSecondaryAction,
                modifier = buttonModifier,
                variant = secondaryActionVariant,
                size = actionSize,
            )
        }
    }
    when (layout) {
        JengaStateLayout.Inline -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            message()
            if (hasActions) {
                Column(
                    modifier = Modifier.padding(top = (actionSpacing - spacing).coerceAtLeast(0.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(actionGap),
                ) {
                    actions(Modifier)
                }
            }
        }

        JengaStateLayout.FillScreen -> FillScreenLayout(
            modifier = modifier.fillMaxSize(),
            message = {
                val scrollState = rememberScrollState()
                val cueColor = JengaTheme.colors.border
                Column(
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()
                            // More of the message is below: mark the edge it is cut at.
                            if (scrollState.canScrollForward) {
                                val thickness = ScrollCueThickness.toPx()
                                drawRect(
                                    color = cueColor,
                                    topLeft = Offset(0f, size.height - thickness),
                                    size = Size(size.width, thickness),
                                )
                            }
                        }
                        .verticalScroll(scrollState)
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
                ) {
                    message()
                }
            },
            actions = {
                if (hasActions) {
                    Column(
                        modifier = Modifier.padding(fillScreenActionsPadding),
                        verticalArrangement = Arrangement.spacedBy(actionGap),
                    ) {
                        actions(Modifier.fillMaxWidth())
                    }
                }
            },
        )
    }
}

/**
 * Padding around the pinned buttons of [JengaStateLayout.FillScreen]: 16dp at the sides and below,
 * and 8dp above, so a message that is cut off where its scroll area ends does not touch them.
 */
private val fillScreenActionsPadding: PaddingValues
    @Composable get() = PaddingValues(
        start = JengaTheme.spacing.lg,
        top = JengaTheme.spacing.sm,
        end = JengaTheme.spacing.lg,
        bottom = JengaTheme.spacing.lg,
    )

/** Gap between the first and the second action button. */
private val actionGap: Dp
    @Composable get() = JengaTheme.spacing.sm

/** Thickness of the line that marks more message below the scroll area in [JengaStateLayout.FillScreen]. */
private val ScrollCueThickness = 1.dp

/**
 * Pins [actions] at the bottom and gives [message] exactly the space above them, so the message can
 * center itself there and scroll when it is taller. With no height limit (inside a scrolling
 * parent) there is no "screen" to fill: the message takes its own height and the actions follow.
 */
@Composable
private fun FillScreenLayout(
    message: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        content = {
            Box(propagateMinConstraints = true) { message() }
            Box(propagateMinConstraints = true) { actions() }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val actionsPlaceable = measurables[1].measure(
            Constraints(minWidth = width, maxWidth = width, maxHeight = constraints.maxHeight),
        )
        val messageHeight = if (constraints.hasBoundedHeight) {
            (constraints.maxHeight - actionsPlaceable.height).coerceAtLeast(0)
        } else {
            maxOf(
                measurables[0].maxIntrinsicHeight(width),
                constraints.minHeight - actionsPlaceable.height,
            )
        }
        val messagePlaceable = measurables[0].measure(Constraints.fixed(width, messageHeight))
        layout(width, messageHeight + actionsPlaceable.height) {
            messagePlaceable.placeRelative(0, 0)
            actionsPlaceable.placeRelative(0, messageHeight)
        }
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Outline,
    colors: JengaStateColors = JengaStateDefaults.colors(),
    titleStyle: TextStyle = JengaStateDefaults.titleStyle,
    descriptionStyle: TextStyle = JengaStateDefaults.descriptionStyle,
    contentPadding: PaddingValues = JengaStateDefaults.contentPadding,
    spacing: Dp = JengaStateDefaults.spacing,
    actionSpacing: Dp = JengaTheme.components.state?.actionSpacing ?: spacing,
    actionSize: JengaButtonSize = JengaStateDefaults.actionSize,
) {
    JengaEmptyState(
        title = title,
        modifier = modifier,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        onAction = onAction,
        actionVariant = actionVariant,
        colors = colors,
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle,
        contentPadding = contentPadding,
        spacing = spacing,
        actionSpacing = actionSpacing,
        actionSize = actionSize,
        secondaryActionLabel = null,
        onSecondaryAction = null,
        secondaryActionVariant = JengaButtonVariant.Ghost,
        layout = JengaStateLayout.Inline,
    )
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaErrorState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionVariant: JengaButtonVariant = JengaButtonVariant.Primary,
    colors: JengaStateColors = JengaStateDefaults.colors(),
    titleStyle: TextStyle = JengaStateDefaults.titleStyle,
    descriptionStyle: TextStyle = JengaStateDefaults.descriptionStyle,
    contentPadding: PaddingValues = JengaStateDefaults.contentPadding,
    spacing: Dp = JengaStateDefaults.spacing,
    actionSpacing: Dp = JengaTheme.components.state?.actionSpacing ?: spacing,
    actionSize: JengaButtonSize = JengaStateDefaults.actionSize,
) {
    JengaErrorState(
        title = title,
        modifier = modifier,
        description = description,
        icon = icon,
        actionLabel = actionLabel,
        onAction = onAction,
        actionVariant = actionVariant,
        colors = colors,
        titleStyle = titleStyle,
        descriptionStyle = descriptionStyle,
        contentPadding = contentPadding,
        spacing = spacing,
        actionSpacing = actionSpacing,
        actionSize = actionSize,
        secondaryActionLabel = null,
        onSecondaryAction = null,
        secondaryActionVariant = JengaButtonVariant.Ghost,
        layout = JengaStateLayout.Inline,
    )
}
