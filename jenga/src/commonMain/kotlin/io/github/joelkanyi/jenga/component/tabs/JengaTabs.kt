package io.github.joelkanyi.jenga.component.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Resolved colors for [JengaTabs]. Override via [JengaTabsDefaults.colors]. */
@Poko
@Immutable
public class JengaTabsColors(
    public val selectedContent: Color,
    public val unselectedContent: Color,
    public val indicator: Color,
    public val divider: Color,
) {
    public fun copy(
        selectedContent: Color = this.selectedContent,
        unselectedContent: Color = this.unselectedContent,
        indicator: Color = this.indicator,
        divider: Color = this.divider,
    ): JengaTabsColors = JengaTabsColors(selectedContent, unselectedContent, indicator, divider)
}

/** Defaults and token mappings for [JengaTabs]. */
public object JengaTabsDefaults {
    /** Thickness of the selected-tab indicator. */
    public val IndicatorThickness: Dp = 2.dp

    /** Themed colors. */
    @Composable
    public fun colors(): JengaTabsColors {
        val c = JengaTheme.colors
        return JengaTabsColors(
            selectedContent = c.brand,
            unselectedContent = c.textMuted,
            indicator = c.brand,
            divider = c.border,
        )
    }
}

/**
 * A row of text tabs with an underline indicator under the selected tab.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaTabsSample
 *
 * @param selectedIndex the index of the selected tab.
 * @param tabs the tab titles.
 * @param onSelect called with the index of a tapped tab.
 * @param modifier the [Modifier] for the tab row.
 * @param colors the color set; defaults to [JengaTabsDefaults.colors].
 */
@Composable
public fun JengaTabs(
    selectedIndex: Int,
    tabs: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: JengaTabsColors = JengaTabsDefaults.colors(),
) {
    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        verticalAlignment = Alignment.Top,
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                JengaText(
                    text = title,
                    style = JengaTheme.typography.titleSmall,
                    color = if (selected) colors.selectedContent else colors.unselectedContent,
                    maxLines = 1,
                    modifier = Modifier.padding(
                        horizontal = JengaTheme.spacing.md,
                        vertical = JengaTheme.spacing.md,
                    ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(JengaTabsDefaults.IndicatorThickness)
                        .background(if (selected) colors.indicator else colors.divider),
                )
            }
        }
    }
}
