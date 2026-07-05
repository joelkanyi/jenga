package io.github.joelkanyi.jenga.foundation.elevation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * **Elevation token set** — resting shadow depths.
 *
 * Maps to the frontend's `--shadow-e1/e2/e3` ladder: [sm] for resting cards,
 * [md] for raised/hover, [lg] for overlays, [xl] for modals/sheets. Apply with
 * `Modifier.shadow(JengaTheme.elevation.md, shape)`. Read via
 * `JengaTheme.elevation`.
 */
@Immutable
public data class JengaElevation(
    public val none: Dp = 0.dp,
    public val sm: Dp = 1.dp,
    public val md: Dp = 4.dp,
    public val lg: Dp = 8.dp,
    public val xl: Dp = 16.dp,
)

/** The default Jenga elevation ladder. */
public fun jengaElevation(): JengaElevation = JengaElevation()
