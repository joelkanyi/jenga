package io.github.joelkanyi.jenga.component.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for a [JengaSegmentedControl]. Override via [JengaSegmentedControlDefaults.colors]. */
@Poko
@Immutable
public class JengaSegmentedControlColors(
    public val track: Color,
    public val selectedSegment: Color,
    public val selectedContent: Color,
    public val unselectedContent: Color,
) {
    public fun copy(
        track: Color = this.track,
        selectedSegment: Color = this.selectedSegment,
        selectedContent: Color = this.selectedContent,
        unselectedContent: Color = this.unselectedContent,
    ): JengaSegmentedControlColors = JengaSegmentedControlColors(track, selectedSegment, selectedContent, unselectedContent)
}

/** Defaults and token mappings for [JengaSegmentedControl]. */
public object JengaSegmentedControlDefaults {
    /** Padding inside the track, around the segments. */
    public val TrackPadding: Dp = 4.dp

    /** Minimum segment height. */
    public val SegmentMinHeight: Dp = 36.dp

    /** Default outer shape. */
    public val shape: Shape
        @Composable get() = JengaTheme.shapes.pill

    /** Themed colors. */
    @Composable
    public fun colors(): JengaSegmentedControlColors {
        val c = JengaTheme.colors
        return JengaSegmentedControlColors(
            track = c.surfaceSunk,
            selectedSegment = c.surface,
            selectedContent = c.textPrimary,
            unselectedContent = c.textMuted,
        )
    }
}

/**
 * A segmented control — a small set of mutually-exclusive options in a pill
 * track, with the selected segment raised.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSegmentedControlSample
 *
 * @param selectedIndex the selected segment index.
 * @param segments the segment labels.
 * @param onSelect called with the index of a tapped segment.
 * @param modifier the [Modifier] for the control.
 * @param shape the outer shape; defaults to [JengaSegmentedControlDefaults.shape].
 * @param colors the color set; defaults to [JengaSegmentedControlDefaults.colors].
 */
@Composable
public fun JengaSegmentedControl(
    selectedIndex: Int,
    segments: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = JengaSegmentedControlDefaults.shape,
    colors: JengaSegmentedControlColors = JengaSegmentedControlDefaults.colors(),
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(colors.track)
            .padding(JengaSegmentedControlDefaults.TrackPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(JengaSegmentedControlDefaults.TrackPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        segments.forEachIndexed { index, segment ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(if (selected) colors.selectedSegment else Color.Transparent)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    )
                    .defaultMinSize(minHeight = JengaSegmentedControlDefaults.SegmentMinHeight)
                    .padding(horizontal = JengaTheme.spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                JengaText(
                    text = segment,
                    style = JengaTheme.typography.titleSmall,
                    color = if (selected) colors.selectedContent else colors.unselectedContent,
                    maxLines = 1,
                )
            }
        }
    }
}
