package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** How a [JengaDialog] lays out its confirm and dismiss actions. */
public enum class JengaDialogActionsLayout {
    /** One row at the end edge: the dismiss action, then the confirm action. Each keeps its own width. */
    Row,

    /**
     * One above the other, each as wide as the dialog's content: the confirm action first, the
     * dismiss action below it. Gives long labels the whole width.
     *
     * A [JengaButton] text label stays on one line, so a label wider than the dialog is cut. For a
     * label that wraps, use the [JengaButton] overload with a content slot and put wrapping text
     * in it.
     *
     * Full width relies on the action honouring a minimum width, as [JengaButton] does. Content
     * that ignores it (e.g. a button inside the caller's own `Row`) keeps its own size.
     */
    Stacked,
}

/** Where a [JengaDialog] places its icon. The title and text align with the icon. */
public enum class JengaDialogIconAlignment {
    /** The icon sits at the start edge; the title and text stay start-aligned. */
    Start,

    /** The icon is centred; the title and text are centred under it. */
    Center,
}

/** Resolved colors for a [JengaDialog]. Override via [JengaDialogDefaults.colors]. */
@Poko
@Immutable
public class JengaDialogColors(
    public val container: Color,
    public val title: Color,
    public val text: Color,
) {
    public fun copy(
        container: Color = this.container,
        title: Color = this.title,
        text: Color = this.text,
    ): JengaDialogColors = JengaDialogColors(container, title, text)
}

/**
 * Theme-wide style for every [JengaDialog], set through
 * [io.github.joelkanyi.jenga.theme.JengaComponentStyles.dialog]. A `null` value keeps Jenga's
 * default; arguments passed to a [JengaDialog] call still win.
 *
 * @property shape the dialog shape.
 * @property contentPadding padding inside the dialog.
 * @property titleStyle the title text style.
 * @property textStyle the body text style.
 * @property actionSpacing the gap between the actions.
 * @property colors the color set.
 */
@Poko
@Immutable
public class JengaDialogStyle(
    public val shape: Shape? = null,
    public val contentPadding: PaddingValues? = null,
    public val titleStyle: TextStyle? = null,
    public val textStyle: TextStyle? = null,
    public val actionSpacing: Dp? = null,
    public val colors: JengaDialogColors? = null,
) {
    public fun copy(
        shape: Shape? = this.shape,
        contentPadding: PaddingValues? = this.contentPadding,
        titleStyle: TextStyle? = this.titleStyle,
        textStyle: TextStyle? = this.textStyle,
        actionSpacing: Dp? = this.actionSpacing,
        colors: JengaDialogColors? = this.colors,
    ): JengaDialogStyle = JengaDialogStyle(shape, contentPadding, titleStyle, textStyle, actionSpacing, colors)
}

/** Defaults for [JengaDialog]. Each one follows the theme's [JengaDialogStyle]. */
public object JengaDialogDefaults {
    /**
     * Default dialog outline: a hairline in the border color in dark themes, where a
     * dialog on a dark scrim has no visible edge, and none in light themes.
     */
    public val border: BorderStroke?
        @Composable get() = darkThemeHairline()

    /** Default dialog shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.components.dialog?.shape ?: JengaTheme.shapes.card

    /** Padding inside the dialog. */
    public val contentPadding: PaddingValues
        @Composable get() = JengaTheme.components.dialog?.contentPadding ?: PaddingValues(JengaTheme.spacing.xl)

    /** Title text style. */
    public val titleStyle: TextStyle
        @Composable get() = JengaTheme.components.dialog?.titleStyle ?: JengaTheme.typography.headingSmall

    /** Body text style. */
    public val textStyle: TextStyle
        @Composable get() = JengaTheme.components.dialog?.textStyle ?: JengaTheme.typography.bodyMedium

    /** Gap between the actions. */
    public val actionSpacing: Dp
        @Composable get() = JengaTheme.components.dialog?.actionSpacing ?: JengaTheme.spacing.sm

    /** Themed colors. */
    @Composable
    public fun colors(): JengaDialogColors = JengaTheme.components.dialog?.colors ?: JengaDialogColors(
        container = JengaTheme.colors.surface,
        title = JengaTheme.colors.textPrimary,
        text = JengaTheme.colors.textMuted,
    )
}

/**
 * A modal dialog with an optional icon, title and body, plus confirm/dismiss actions.
 * For a destructive confirmation, pass a danger [JengaButton] as [confirmButton].
 *
 * By default there is no icon and the actions sit in one row at the end edge. For a confirmation
 * that must be read before it is answered, pass an [icon] and
 * [JengaDialogActionsLayout.Stacked]: the icon sits above the title and the actions fill the
 * width, the confirm action first and the way out below it.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaDialogSample
 * @sample io.github.joelkanyi.jenga.samples.JengaDialogIconStackedSample
 *
 * @param onDismissRequest called when the user dismisses (scrim tap or back).
 * @param confirmButton the primary action (e.g. a [JengaButton]).
 * @param modifier the [Modifier] for the dialog surface.
 * @param title optional title.
 * @param text optional body text.
 * @param dismissButton optional secondary action.
 * @param shape the dialog shape.
 * @param contentPadding padding inside the dialog.
 * @param titleStyle the [title] text style.
 * @param textStyle the [text] text style.
 * @param actionSpacing gap between the actions, in either [actionsLayout].
 * @param colors the color set; defaults to [JengaDialogDefaults.colors].
 * @param border outline around the dialog; defaults to [JengaDialogDefaults.border]
 *   (a hairline in dark themes only). Pass null for none.
 * @param icon optional content above the [title], e.g. a tinted
 *   [io.github.joelkanyi.jenga.component.icon.JengaIconTile]. The dialog draws it as given and
 *   does not size or tint it.
 * @param iconAlignment where the [icon] sits; the [title] and [text] align with it. Has no effect
 *   without an [icon].
 * @param actionsLayout how the actions are laid out; see [JengaDialogActionsLayout].
 */
@Composable
public fun JengaDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = JengaDialogDefaults.shape,
    contentPadding: PaddingValues = JengaDialogDefaults.contentPadding,
    titleStyle: TextStyle = JengaDialogDefaults.titleStyle,
    textStyle: TextStyle = JengaDialogDefaults.textStyle,
    actionSpacing: Dp = JengaDialogDefaults.actionSpacing,
    colors: JengaDialogColors = JengaDialogDefaults.colors(),
    border: BorderStroke? = JengaDialogDefaults.border,
    icon: (@Composable () -> Unit)? = null,
    iconAlignment: JengaDialogIconAlignment = JengaDialogIconAlignment.Start,
    actionsLayout: JengaDialogActionsLayout = JengaDialogActionsLayout.Row,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        JengaDialogSurface(
            confirmButton = confirmButton,
            modifier = modifier.overlayOutline(border, shape),
            title = title,
            text = text,
            dismissButton = dismissButton,
            shape = shape,
            contentPadding = contentPadding,
            titleStyle = titleStyle,
            textStyle = textStyle,
            actionSpacing = actionSpacing,
            colors = colors,
            icon = icon,
            iconAlignment = iconAlignment,
            actionsLayout = actionsLayout,
        )
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = JengaDialogDefaults.shape,
    contentPadding: PaddingValues = JengaDialogDefaults.contentPadding,
    titleStyle: TextStyle = JengaDialogDefaults.titleStyle,
    textStyle: TextStyle = JengaDialogDefaults.textStyle,
    actionSpacing: Dp = JengaDialogDefaults.actionSpacing,
    colors: JengaDialogColors = JengaDialogDefaults.colors(),
    border: BorderStroke? = JengaDialogDefaults.border,
) {
    JengaDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        title = title,
        text = text,
        dismissButton = dismissButton,
        shape = shape,
        contentPadding = contentPadding,
        titleStyle = titleStyle,
        textStyle = textStyle,
        actionSpacing = actionSpacing,
        colors = colors,
        border = border,
        icon = null,
    )
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = JengaDialogDefaults.shape,
    contentPadding: PaddingValues = JengaDialogDefaults.contentPadding,
    titleStyle: TextStyle = JengaDialogDefaults.titleStyle,
    textStyle: TextStyle = JengaDialogDefaults.textStyle,
    actionSpacing: Dp = JengaDialogDefaults.actionSpacing,
    colors: JengaDialogColors = JengaDialogDefaults.colors(),
) {
    JengaDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        title = title,
        text = text,
        dismissButton = dismissButton,
        shape = shape,
        contentPadding = contentPadding,
        titleStyle = titleStyle,
        textStyle = textStyle,
        actionSpacing = actionSpacing,
        colors = colors,
        border = JengaDialogDefaults.border,
    )
}

// The two layouts call the action slots in their own visual order (row: dismiss, confirm; stacked:
// confirm, dismiss), so focus and reading order match what is drawn. Switching the layout while
// the dialog is open recomposes the action slots from scratch: state remembered inside a slot
// resets, hoisted state is unaffected.
@Suppress("ktlint:compose:content-slot-reused")
@Composable
internal fun JengaDialogSurface(
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = JengaDialogDefaults.shape,
    contentPadding: PaddingValues = JengaDialogDefaults.contentPadding,
    titleStyle: TextStyle = JengaDialogDefaults.titleStyle,
    textStyle: TextStyle = JengaDialogDefaults.textStyle,
    actionSpacing: Dp = JengaDialogDefaults.actionSpacing,
    colors: JengaDialogColors = JengaDialogDefaults.colors(),
    icon: (@Composable () -> Unit)? = null,
    iconAlignment: JengaDialogIconAlignment = JengaDialogIconAlignment.Start,
    actionsLayout: JengaDialogActionsLayout = JengaDialogActionsLayout.Row,
) {
    // The title and text follow the icon, so a centred icon never sits over start-aligned text.
    // Without an icon nothing changes: no alignment is passed and the text lays out as before.
    val centered = icon != null && iconAlignment == JengaDialogIconAlignment.Center
    val textModifier = if (centered) Modifier.fillMaxWidth() else Modifier
    val textAlign = if (centered) TextAlign.Center else null
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.container)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.align(if (centered) Alignment.CenterHorizontally else Alignment.Start),
            ) {
                icon()
            }
        }
        if (title != null) {
            JengaText(
                text = title,
                modifier = textModifier,
                style = titleStyle,
                color = colors.title,
                textAlign = textAlign,
            )
        }
        if (text != null) {
            JengaText(
                text = text,
                modifier = textModifier,
                style = textStyle,
                color = colors.text,
                textAlign = textAlign,
            )
        }
        when (actionsLayout) {
            JengaDialogActionsLayout.Row -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(actionSpacing, Alignment.End),
            ) {
                if (dismissButton != null) dismissButton()
                confirmButton()
            }

            JengaDialogActionsLayout.Stacked -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(actionSpacing),
            ) {
                // Each slot is measured at exactly the content width, so the caller's button
                // fills it without passing a modifier.
                Box(modifier = Modifier.fillMaxWidth(), propagateMinConstraints = true) {
                    confirmButton()
                }
                if (dismissButton != null) {
                    Box(modifier = Modifier.fillMaxWidth(), propagateMinConstraints = true) {
                        dismissButton()
                    }
                }
            }
        }
    }
}

internal fun Modifier.overlayOutline(border: BorderStroke?, shape: Shape): Modifier = if (border == null) this else border(border, shape)
