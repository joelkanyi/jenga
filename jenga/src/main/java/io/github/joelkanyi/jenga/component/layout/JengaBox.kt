package io.github.joelkanyi.jenga.component.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * The primitive styled container — a `Box` that bundles the common
 * padding / background / shape / border into parameters, so feature code stops
 * writing `Modifier.clip().background().border().padding()` chains. (Polaris /
 * Chakra `Box`.)
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBoxSample
 *
 * @param modifier the [Modifier] for the box (applied outside the styling).
 * @param padding inner padding; defaults to none.
 * @param background fill color; [Color.Unspecified] means no fill.
 * @param shape the clip/background/border shape; defaults to a rectangle.
 * @param border optional border stroke.
 * @param contentAlignment alignment of the content within the box.
 * @param content the box content, in a [BoxScope].
 */
@Composable
public fun JengaBox(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    background: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .then(if (background.isSpecified) Modifier.background(background, shape) else Modifier)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .padding(padding),
        contentAlignment = contentAlignment,
        content = content,
    )
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBoxPreview() {
    JengaTheme {
        JengaBox(
            modifier = Modifier.padding(JengaTheme.spacing.xl),
            padding = PaddingValues(JengaTheme.spacing.lg),
            background = JengaTheme.colors.brandSubtle,
            shape = JengaTheme.shapes.card,
        ) {
            JengaText("JengaBox — padding + background + shape, no Modifier chain", color = JengaTheme.colors.onBrandSubtle)
        }
    }
}
