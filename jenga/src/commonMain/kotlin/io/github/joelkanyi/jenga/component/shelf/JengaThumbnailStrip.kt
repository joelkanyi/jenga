package io.github.joelkanyi.jenga.component.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

/** Resolved colors for a [JengaThumbnailStrip]. Override via [JengaThumbnailStripDefaults.colors]. */
@Poko
@Immutable
public class JengaThumbnailStripColors(
    public val thumbnail: Color,
    public val addBorder: Color,
    public val addContent: Color,
) {
    public fun copy(
        thumbnail: Color = this.thumbnail,
        addBorder: Color = this.addBorder,
        addContent: Color = this.addContent,
    ): JengaThumbnailStripColors = JengaThumbnailStripColors(thumbnail, addBorder, addContent)
}

/**
 * Theme-wide style for every [JengaThumbnailStrip], set through
 * [io.github.joelkanyi.jenga.theme.JengaComponentStyles.thumbnailStrip]. A `null` value keeps Jenga's
 * default; arguments passed to a call still win.
 *
 * @property thumbnailSize the edge length of each thumbnail and the add tile.
 * @property spacing the gap between tiles.
 * @property contentPadding padding around the row.
 * @property shape the tile shape.
 * @property colors the color set.
 */
@Poko
@Immutable
public class JengaThumbnailStripStyle(
    public val thumbnailSize: Dp? = null,
    public val spacing: Dp? = null,
    public val contentPadding: PaddingValues? = null,
    public val shape: Shape? = null,
    public val colors: JengaThumbnailStripColors? = null,
) {
    public fun copy(
        thumbnailSize: Dp? = this.thumbnailSize,
        spacing: Dp? = this.spacing,
        contentPadding: PaddingValues? = this.contentPadding,
        shape: Shape? = this.shape,
        colors: JengaThumbnailStripColors? = this.colors,
    ): JengaThumbnailStripStyle = JengaThumbnailStripStyle(thumbnailSize, spacing, contentPadding, shape, colors)
}

/**
 * Defaults and token mappings for [JengaThumbnailStrip]. The composable members follow the theme's
 * [JengaThumbnailStripStyle]; the constants are Jenga's built-in values, and [JengaThumbnailStrip]
 * resolves the theme's values over them.
 */
public object JengaThumbnailStripDefaults {
    /** Edge length of each square thumbnail. */
    public val ThumbnailSize: Dp = 80.dp

    /** Width of the dashed border on the add tile. */
    public val AddBorderWidth: Dp = 1.5.dp

    /** Thumbnail and add-tile shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.components.thumbnailStrip?.shape ?: JengaTheme.shapes.md

    /** Gap between thumbnails. */
    public val spacing: Dp
        @Composable get() = JengaTheme.components.thumbnailStrip?.spacing ?: JengaTheme.spacing.sm

    /** Padding around the row. */
    public val contentPadding: PaddingValues
        @Composable get() = JengaTheme.components.thumbnailStrip?.contentPadding ?: PaddingValues(horizontal = JengaTheme.spacing.lg)

    /** Themed colors. */
    @Composable
    public fun colors(): JengaThumbnailStripColors {
        JengaTheme.components.thumbnailStrip?.colors?.let { return it }
        val c = JengaTheme.colors
        return JengaThumbnailStripColors(
            thumbnail = c.surfaceSunk,
            addBorder = c.borderStrong,
            addContent = c.brand,
        )
    }
}

/** How a [JengaThumbnailStrip] lays out its tiles. */
public enum class JengaThumbnailStripLayout {
    /** One row that scrolls sideways. */
    Row,

    /** Fills the width, then continues on the next line. */
    Wrap,
}

/**
 * Square thumbnails (e.g. photos attached to a record) with an optional dashed "add" tile at the
 * end, in one scrolling row or wrapped over several lines (see [layout]). Each [thumbnail] slot is
 * clipped to [shape] and sized to [thumbnailSize]; put an image and any tap handling there.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaThumbnailStripSample
 *
 * @param count the number of thumbnails.
 * @param modifier the [Modifier] for the strip.
 * @param onAdd called when the add tile is tapped; null hides the tile.
 * @param addContentDescription accessibility label for the add tile.
 * @param addIcon the icon inside the add tile; inherits the add color.
 * @param thumbnailSize the edge length of each thumbnail and the add tile; defaults to the theme's
 *   value, else [JengaThumbnailStripDefaults.ThumbnailSize].
 * @param spacing the gap between tiles, across and between lines.
 * @param contentPadding padding around the tiles.
 * @param shape the tile shape.
 * @param colors the color set; defaults to [JengaThumbnailStripDefaults.colors].
 * @param layout one scrolling row, or wrapped lines; see [JengaThumbnailStripLayout].
 * @param thumbnail the content of the thumbnail at an index.
 */
@Suppress("ktlint:compose:content-slot-reused")
@Composable
public fun JengaThumbnailStrip(
    count: Int,
    modifier: Modifier = Modifier,
    onAdd: (() -> Unit)? = null,
    addContentDescription: String? = null,
    addIcon: @Composable () -> Unit = { JengaIcon(JengaTheme.icons.add, contentDescription = null) },
    thumbnailSize: Dp = JengaTheme.components.thumbnailStrip?.thumbnailSize ?: JengaThumbnailStripDefaults.ThumbnailSize,
    spacing: Dp = JengaThumbnailStripDefaults.spacing,
    contentPadding: PaddingValues = JengaThumbnailStripDefaults.contentPadding,
    shape: Shape = JengaThumbnailStripDefaults.shape,
    colors: JengaThumbnailStripColors = JengaThumbnailStripDefaults.colors(),
    layout: JengaThumbnailStripLayout = JengaThumbnailStripLayout.Row,
    thumbnail: @Composable (index: Int) -> Unit,
) {
    val tile = TileLook(thumbnailSize, shape, colors)
    when (layout) {
        JengaThumbnailStripLayout.Row -> LazyRow(
            modifier = modifier,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(count) { index -> ThumbnailTile(tile) { thumbnail(index) } }
            if (onAdd != null) item { AddTile(onAdd, addContentDescription, addIcon, tile) }
        }
        JengaThumbnailStripLayout.Wrap -> FlowRow(
            modifier = modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            repeat(count) { index -> ThumbnailTile(tile) { thumbnail(index) } }
            if (onAdd != null) AddTile(onAdd, addContentDescription, addIcon, tile)
        }
    }
}

@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
public fun JengaThumbnailStrip(
    count: Int,
    modifier: Modifier = Modifier,
    onAdd: (() -> Unit)? = null,
    addContentDescription: String? = null,
    addIcon: @Composable () -> Unit = { JengaIcon(JengaTheme.icons.add, contentDescription = null) },
    thumbnailSize: Dp = JengaTheme.components.thumbnailStrip?.thumbnailSize ?: JengaThumbnailStripDefaults.ThumbnailSize,
    spacing: Dp = JengaThumbnailStripDefaults.spacing,
    contentPadding: PaddingValues = JengaThumbnailStripDefaults.contentPadding,
    shape: Shape = JengaThumbnailStripDefaults.shape,
    colors: JengaThumbnailStripColors = JengaThumbnailStripDefaults.colors(),
    thumbnail: @Composable (index: Int) -> Unit,
) {
    JengaThumbnailStrip(
        count = count,
        modifier = modifier,
        onAdd = onAdd,
        addContentDescription = addContentDescription,
        addIcon = addIcon,
        thumbnailSize = thumbnailSize,
        spacing = spacing,
        contentPadding = contentPadding,
        shape = shape,
        colors = colors,
        layout = JengaThumbnailStripLayout.Row,
        thumbnail = thumbnail,
    )
}

private class TileLook(val size: Dp, val shape: Shape, val colors: JengaThumbnailStripColors)

@Composable
private fun ThumbnailTile(look: TileLook, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(look.size)
            .clip(look.shape)
            .background(look.colors.thumbnail),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun AddTile(onAdd: () -> Unit, contentDescription: String?, icon: @Composable () -> Unit, look: TileLook) {
    Box(
        modifier = Modifier
            .size(look.size)
            .clip(look.shape)
            .drawBehind {
                val stroke = JengaThumbnailStripDefaults.AddBorderWidth.toPx()
                drawOutline(
                    outline = look.shape.createOutline(size, layoutDirection, this),
                    color = look.colors.addBorder,
                    style = Stroke(
                        width = stroke * 2,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                    ),
                )
            }
            .clickable(role = Role.Button, onClick = onAdd)
            .semantics { contentDescription?.let { this.contentDescription = it } },
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalJengaContentColor provides look.colors.addContent, content = icon)
    }
}
