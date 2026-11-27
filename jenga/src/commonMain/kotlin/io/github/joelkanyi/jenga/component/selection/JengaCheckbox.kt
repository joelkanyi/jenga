package io.github.joelkanyi.jenga.component.selection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * Resolved colors for a [JengaCheckbox]. Override via [JengaCheckboxDefaults.colors].
 * The mixed state uses the checked colors: [checkedFill] and [checkedBorder] for the box, and
 * [checkmark] (or [disabledCheckmark]) for the dash.
 */
@Poko
@Immutable
public class JengaCheckboxColors(
    public val checkedFill: Color,
    public val uncheckedFill: Color,
    public val disabledFill: Color,
    public val checkedBorder: Color,
    public val uncheckedBorder: Color,
    public val disabledBorder: Color,
    public val checkmark: Color,
    public val disabledCheckmark: Color,
) {
    public fun copy(
        checkedFill: Color = this.checkedFill,
        uncheckedFill: Color = this.uncheckedFill,
        disabledFill: Color = this.disabledFill,
        checkedBorder: Color = this.checkedBorder,
        uncheckedBorder: Color = this.uncheckedBorder,
        disabledBorder: Color = this.disabledBorder,
        checkmark: Color = this.checkmark,
        disabledCheckmark: Color = this.disabledCheckmark,
    ): JengaCheckboxColors = JengaCheckboxColors(
        checkedFill,
        uncheckedFill,
        disabledFill,
        checkedBorder,
        uncheckedBorder,
        disabledBorder,
        checkmark,
        disabledCheckmark,
    )
}

/** Defaults and token mappings for [JengaCheckbox]. */
public object JengaCheckboxDefaults {
    public val Size: Dp = 22.dp

    /** Default box shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.xs

    /** Themed colors for all checkbox states. */
    @Composable
    public fun colors(): JengaCheckboxColors {
        val c = JengaTheme.colors
        return JengaCheckboxColors(
            checkedFill = c.brand,
            uncheckedFill = c.surface,
            disabledFill = c.surfaceDisabled,
            checkedBorder = c.brand,
            uncheckedBorder = c.borderStrong,
            disabledBorder = c.borderDisabled,
            checkmark = c.onBrand,
            disabledCheckmark = c.contentDisabled,
        )
    }
}

/**
 * A checkbox.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaCheckboxSample
 *
 * @param checked whether the box is checked.
 * @param onCheckedChange called with the new state; pass `null` for a read-only,
 *   externally-driven checkbox (e.g. inside a selectable row), which also drops the
 *   control's own 48dp touch area.
 * @param modifier the [Modifier] for this checkbox.
 * @param enabled whether the checkbox is interactive.
 * @param shape the box shape; defaults to [JengaCheckboxDefaults.shape].
 * @param colors the color set; defaults to [JengaCheckboxDefaults.colors].
 */
@Composable
public fun JengaCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = JengaCheckboxDefaults.shape,
    colors: JengaCheckboxColors = JengaCheckboxDefaults.colors(),
) {
    val toggleModifier = if (onCheckedChange != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
    } else {
        Modifier
    }
    CheckboxBox(
        state = ToggleableState(checked),
        modifier = modifier.then(toggleModifier),
        enabled = enabled,
        shape = shape,
        colors = colors,
    )
}

/**
 * A checkbox with three states: unchecked, checked or mixed. The mixed state
 * ([ToggleableState.Indeterminate]) is for a "Select all" box over a list in which only some
 * items are checked; it draws the checked box with a dash instead of the tick, and screen readers
 * announce it as partially checked.
 *
 * The checkbox does not pick the next state: [onClick] only reports the tap. The usual
 * "Select all" rule is mixed or unchecked to checked, and checked to unchecked.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaCheckboxMixedSample
 *
 * @param state the state shown: [ToggleableState.Off], [ToggleableState.On] or
 *   [ToggleableState.Indeterminate] (mixed).
 * @param onClick called when the box is tapped; pass `null` for a read-only,
 *   externally-driven checkbox (e.g. inside a selectable row), which also drops the
 *   control's own 48dp touch area.
 * @param modifier the [Modifier] for this checkbox.
 * @param enabled whether the checkbox is interactive.
 * @param shape the box shape; defaults to [JengaCheckboxDefaults.shape].
 * @param colors the color set; defaults to [JengaCheckboxDefaults.colors].
 */
@Composable
public fun JengaCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = JengaCheckboxDefaults.shape,
    colors: JengaCheckboxColors = JengaCheckboxDefaults.colors(),
) {
    val toggleModifier = if (onClick != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .triStateToggleable(
                state = state,
                enabled = enabled,
                role = Role.Checkbox,
                onClick = onClick,
            )
    } else {
        Modifier
    }
    CheckboxBox(
        state = state,
        modifier = modifier.then(toggleModifier),
        enabled = enabled,
        shape = shape,
        colors = colors,
    )
}

@Composable
private fun CheckboxBox(
    state: ToggleableState,
    enabled: Boolean,
    shape: Shape,
    colors: JengaCheckboxColors,
    modifier: Modifier = Modifier,
) {
    // Mixed shares the checked box; only the mark differs.
    val filled = state != ToggleableState.Off
    val fill = when {
        !enabled && filled -> colors.disabledFill
        filled -> colors.checkedFill
        else -> colors.uncheckedFill
    }
    val borderColor = when {
        !enabled -> colors.disabledBorder
        filled -> colors.checkedBorder
        else -> colors.uncheckedBorder
    }
    val markColor = if (enabled) colors.checkmark else colors.disabledCheckmark

    Canvas(
        modifier = modifier
            .size(JengaCheckboxDefaults.Size)
            .clip(shape)
            .background(fill)
            .border(1.5.dp, borderColor, shape)
            .padding(5.dp),
    ) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()
        when (state) {
            ToggleableState.On -> {
                // Draw a check mark: down-stroke then up-stroke.
                drawLine(
                    color = markColor,
                    start = Offset(0f, h * 0.55f),
                    end = Offset(w * 0.38f, h),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = markColor,
                    start = Offset(w * 0.38f, h),
                    end = Offset(w, h * 0.1f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }

            ToggleableState.Indeterminate -> {
                // A short dash on the box's center line, inset so its round caps stay inside the mark area.
                drawLine(
                    color = markColor,
                    start = Offset(stroke, h / 2f),
                    end = Offset(w - stroke, h / 2f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }

            ToggleableState.Off -> Unit
        }
    }
}
