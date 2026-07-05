package io.github.joelkanyi.jenga.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaChipPreview() {
    JengaTheme { ChipShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaChipRtlPreview() {
    JengaTheme { RtlPreview { ChipShowcase() } }
}

@Composable
private fun ChipShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
    ) {
        JengaChip(label = "All", selected = true, onClick = {})
        JengaChip(label = "Music", selected = false, onClick = {})
        JengaChip(label = "Sports", selected = false, onClick = {})
        JengaChip(label = "Off", selected = false, onClick = {}, enabled = false)
    }
}
