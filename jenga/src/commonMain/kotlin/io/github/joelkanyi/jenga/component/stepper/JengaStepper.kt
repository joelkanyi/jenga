package io.github.joelkanyi.jenga.component.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaStepper]. Override via [JengaStepperDefaults.colors]. */
@Poko
@Immutable
public class JengaStepperColors(
    public val track: Color,
    public val button: Color,
    public val buttonContent: Color,
    public val value: Color,
    public val disabledButton: Color,
    public val disabledContent: Color,
) {
    public fun copy(
        track: Color = this.track,
        button: Color = this.button,
        buttonContent: Color = this.buttonContent,
        value: Color = this.value,
        disabledButton: Color = this.disabledButton,
        disabledContent: Color = this.disabledContent,
    ): JengaStepperColors = JengaStepperColors(track, button, buttonContent, value, disabledButton, disabledContent)
}

/** Defaults and token mappings for [JengaStepper]. */
public object JengaStepperDefaults {
    /** The pill track shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.pill

    /** Diameter of each round +/- button (its touch target expands to 48dp). */
    public val ButtonSize: Dp = 38.dp

    /** Themed colors for the track, buttons and value. */
    @Composable
    public fun colors(): JengaStepperColors {
        val c = JengaTheme.colors
        return JengaStepperColors(
            track = c.surfaceVariant,
            button = c.surface,
            buttonContent = c.textPrimary,
            value = c.textPrimary,
            disabledButton = c.surfaceDisabled,
            disabledContent = c.contentDisabled,
        )
    }
}

/**
 * A compact numeric stepper — a pill track with round decrement/increment
 * buttons around a centered value. Replaces bare `−`/`+` glyph pairs and gives
 * both buttons real 48dp touch targets and content descriptions.
 *
 * Generic and domain-neutral: use it for servings, quantities, household size,
 * times, or any bounded integer. Colors and icons come from Jenga tokens.
 *
 * @param value the current value.
 * @param onValueChange called with the clamped new value when a button is tapped.
 * @param modifier the [Modifier] for this stepper.
 * @param enabled whether the whole control is interactive.
 * @param min the inclusive lower bound; the decrement button disables at it.
 * @param max the inclusive upper bound; the increment button disables at it.
 * @param step the increment/decrement amount.
 * @param decrementContentDescription accessibility label for the `−` button.
 * @param incrementContentDescription accessibility label for the `+` button.
 * @param valueLabel formats the centered value (e.g. to add a unit).
 * @param colors the color set; defaults to [JengaStepperDefaults.colors].
 */
@Composable
public fun JengaStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
    decrementContentDescription: String? = null,
    incrementContentDescription: String? = null,
    valueLabel: (Int) -> String = { it.toString() },
    colors: JengaStepperColors = JengaStepperDefaults.colors(),
) {
    val canDecrement = enabled && value - step >= min
    val canIncrement = enabled && value + step <= max
    Row(
        modifier = modifier
            .clip(JengaStepperDefaults.shape)
            .background(colors.track)
            .padding(JengaTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            icon = JengaIcons.Remove,
            contentDescription = decrementContentDescription,
            enabled = canDecrement,
            colors = colors,
            onClick = { onValueChange(value - step) },
        )
        JengaText(
            text = valueLabel(value),
            style = JengaTheme.typography.titleLarge,
            color = if (enabled) colors.value else colors.disabledContent,
            modifier = Modifier
                .defaultMinSize(minWidth = 34.dp)
                .padding(horizontal = JengaTheme.spacing.xs),
            maxLines = 1,
        )
        StepperButton(
            icon = JengaIcons.Add,
            contentDescription = incrementContentDescription,
            enabled = canIncrement,
            colors = colors,
            onClick = { onValueChange(value + step) },
        )
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    enabled: Boolean,
    colors: JengaStepperColors,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(JengaStepperDefaults.ButtonSize)
            .then(if (enabled) Modifier.shadow(JengaTheme.elevation.sm, shape) else Modifier)
            .clip(shape)
            .background(if (enabled) colors.button else colors.disabledButton)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        JengaIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) colors.buttonContent else colors.disabledContent,
            size = JengaTheme.sizing.iconMedium,
        )
    }
}
