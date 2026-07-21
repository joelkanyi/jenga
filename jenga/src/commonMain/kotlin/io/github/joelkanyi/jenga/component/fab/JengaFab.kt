package io.github.joelkanyi.jenga.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaFab]. Override via [JengaFabDefaults.colors]. */
@Poko
@Immutable
public class JengaFabColors(
    public val container: Color,
    public val content: Color,
) {
    public fun copy(
        container: Color = this.container,
        content: Color = this.content,
    ): JengaFabColors = JengaFabColors(container, content)
}

/** Defaults and token mappings for [JengaFab]. */
public object JengaFabDefaults {
    public val Size: Dp = 56.dp

    /** Height of an extended (labelled) FAB. */
    public val ExtendedHeight: Dp = 52.dp

    /** Default FAB shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.lg

    /** Extended-FAB shape: a full pill, so the label reads as one confident tap target. */
    public val extendedShape: Shape = RoundedCornerShape(percent = 50)

    /** Themed colors. */
    @Composable
    public fun colors(): JengaFabColors = JengaFabColors(container = JengaTheme.colors.brand, content = JengaTheme.colors.onBrand)
}

/**
 * A floating action button: a prominent, elevated action with an icon.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaFabSample
 *
 * @param onClick called when tapped.
 * @param modifier the [Modifier] for this FAB.
 * @param shape the FAB shape; defaults to [JengaFabDefaults.shape].
 * @param colors the color set; defaults to [JengaFabDefaults.colors].
 * @param content the FAB content (typically a single icon); inherits content color.
 */
@Composable
public fun JengaFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = JengaFabDefaults.shape,
    colors: JengaFabColors = JengaFabDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .shadow(JengaTheme.elevation.lg, shape)
            .clip(shape)
            .background(colors.container)
            .clickable(role = Role.Button, onClick = onClick)
            .size(JengaFabDefaults.Size),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
            content()
        }
    }
}

/**
 * An extended floating action button: a prominent, elevated pill carrying an
 * optional leading [icon] and a text [label]. Use it when the primary action
 * needs a word, not just a glyph. Same elevation language as [JengaFab]; the
 * [colors] default to brand and can be overridden with any pair (e.g. a neutral
 * inverse) so it reads correctly on any surface.
 *
 * @param label the action text.
 * @param onClick called when tapped.
 * @param modifier the [Modifier] for this FAB.
 * @param icon an optional leading icon; inherits the content color.
 * @param shape the FAB shape; defaults to a full pill ([JengaFabDefaults.extendedShape]).
 * @param colors the color set; defaults to [JengaFabDefaults.colors].
 */
@Composable
public fun JengaExtendedFab(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    shape: Shape = JengaFabDefaults.extendedShape,
    colors: JengaFabColors = JengaFabDefaults.colors(),
) {
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .shadow(JengaTheme.elevation.lg, shape)
            .clip(shape)
            .background(colors.container)
            .clickable(role = Role.Button, onClick = onClick)
            .height(JengaFabDefaults.ExtendedHeight)
            .padding(horizontal = JengaTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides colors.content) {
            if (icon != null) icon()
            JengaText(text = label, style = JengaTheme.typography.titleSmall, color = colors.content)
        }
    }
}
