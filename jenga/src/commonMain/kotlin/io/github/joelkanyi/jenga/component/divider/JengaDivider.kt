package io.github.joelkanyi.jenga.component.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for [JengaDivider]. */
public object JengaDividerDefaults {
    /** Default line thickness. */
    public val Thickness: Dp = 1.dp

    /** Default line color. */
    public val color: Color
        @Composable get() = JengaTheme.colors.border
}

/**
 * A thin horizontal rule that separates content.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaDividerSample
 *
 * @param modifier the [Modifier] for this divider.
 * @param thickness the line thickness; defaults to [JengaDividerDefaults.Thickness].
 * @param color the line color; defaults to [JengaDividerDefaults.color].
 */
@Composable
public fun JengaDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = JengaDividerDefaults.Thickness,
    color: Color = JengaDividerDefaults.color,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color),
        content = {},
    )
}
