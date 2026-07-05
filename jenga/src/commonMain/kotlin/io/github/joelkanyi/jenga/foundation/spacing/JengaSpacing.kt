package io.github.joelkanyi.jenga.foundation.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * **Spacing token set** — a 4dp-based scale for padding, gaps and insets.
 *
 * Named mechanically (by step, not purpose) per the Revolut/Style-Dictionary
 * approach: the scale stays predictable and consistent, while *semantic* usage
 * (screen gutter, card padding, stack gap) is documented in the catalog and
 * applied at the component layer. Read via `JengaTheme.spacing`.
 *
 * | token  | dp |
 * |--------|----|
 * | none   | 0  |
 * | xxs    | 2  |
 * | xs     | 4  |
 * | sm     | 8  |
 * | md     | 12 |
 * | lg     | 16 |
 * | xl     | 24 |
 * | xxl    | 32 |
 * | xxxl   | 48 |
 * | xxxxl  | 64 |
 */
@Immutable
public data class JengaSpacing(
    public val none: Dp = 0.dp,
    public val xxs: Dp = 2.dp,
    public val xs: Dp = 4.dp,
    public val sm: Dp = 8.dp,
    public val md: Dp = 12.dp,
    public val lg: Dp = 16.dp,
    public val xl: Dp = 24.dp,
    public val xxl: Dp = 32.dp,
    public val xxxl: Dp = 48.dp,
    public val xxxxl: Dp = 64.dp,
)

/** The default Jenga spacing scale. */
public fun jengaSpacing(): JengaSpacing = JengaSpacing()
