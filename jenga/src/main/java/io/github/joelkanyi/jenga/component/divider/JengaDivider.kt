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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
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

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaDividerPreview() {
    JengaTheme { DividerShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDividerRtlPreview() {
    JengaTheme { DividerShowcase() }
}

@Composable
private fun DividerShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaText("Above")
        JengaDivider()
        JengaText("Below")
    }
}
