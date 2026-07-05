package io.github.joelkanyi.jenga.component.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTabsPreview() {
    JengaTheme { TabsShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTabsRtlPreview() {
    JengaTheme { RtlPreview { TabsShowcase() } }
}

@Composable
private fun TabsShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.surface)
            .padding(vertical = JengaTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaTabs(selectedIndex = 0, tabs = listOf("Upcoming", "Live", "Past"), onSelect = {})
        JengaTabs(selectedIndex = 1, tabs = listOf("Upcoming", "Live", "Past"), onSelect = {})
    }
}
