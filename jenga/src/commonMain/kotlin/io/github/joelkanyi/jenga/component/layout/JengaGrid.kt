package io.github.joelkanyi.jenga.component.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A non-scrolling grid that lays children out in [columns] equal-width columns
 * with token gaps, the "stop nesting Rows of Columns" primitive. (Braid `Tiles`
 * / Polaris `InlineGrid` / Chakra `SimpleGrid`.)
 *
 * For large, scrolling data sets prefer a `LazyVerticalGrid`; this is for a
 * bounded set of UI blocks composed inline.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaGridSample
 *
 * @param columns number of equal columns (>= 1).
 * @param modifier the [Modifier] for the grid (must be width-bounded).
 * @param horizontalSpace gap between columns; defaults to `spacing.md`.
 * @param verticalSpace gap between rows; defaults to `spacing.md`.
 * @param content the grid cells; each becomes one cell, filling its column width.
 */
@Composable
public fun JengaGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    horizontalSpace: Dp = JengaTheme.spacing.md,
    verticalSpace: Dp = JengaTheme.spacing.md,
    content: @Composable () -> Unit,
) {
    require(columns >= 1) { "JengaGrid columns must be >= 1, was $columns" }
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val hGap = horizontalSpace.roundToPx()
        val vGap = verticalSpace.roundToPx()
        val totalGap = hGap * (columns - 1)
        val columnWidth = ((constraints.maxWidth - totalGap) / columns).coerceAtLeast(0)
        val cellConstraints = Constraints(minWidth = columnWidth, maxWidth = columnWidth)
        val placeables = measurables.map { it.measure(cellConstraints) }

        val rowCount = (placeables.size + columns - 1) / columns
        val rowHeights = IntArray(rowCount) { row ->
            var max = 0
            for (col in 0 until columns) {
                val index = row * columns + col
                if (index < placeables.size) max = maxOf(max, placeables[index].height)
            }
            max
        }
        val gapsHeight = if (rowCount > 1) vGap * (rowCount - 1) else 0
        val totalHeight = (rowHeights.sum() + gapsHeight)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width = constraints.maxWidth, height = totalHeight) {
            var y = 0
            for (row in 0 until rowCount) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < placeables.size) {
                        placeables[index].placeRelative(x = col * (columnWidth + hGap), y = y)
                    }
                }
                y += rowHeights[row] + vGap
            }
        }
    }
}
