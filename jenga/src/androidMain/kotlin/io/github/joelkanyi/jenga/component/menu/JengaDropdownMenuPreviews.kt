package io.github.joelkanyi.jenga.component.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews --------------------------------------------------------------
// The real menu is a popup window; the preview renders the item surface inline.

@JengaBlockPreviews
@Composable
internal fun JengaDropdownMenuPreview() {
    JengaTheme { MenuSurfacePreview() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDropdownMenuRtlPreview() {
    JengaTheme {
        io.github.joelkanyi.jenga.core.preview.RtlPreview { MenuSurfacePreview() }
    }
}

@Composable
private fun MenuSurfacePreview() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
    ) {
        Column(
            modifier = Modifier
                .clip(JengaTheme.shapes.card)
                .background(JengaTheme.colors.surface),
        ) {
            JengaDropdownMenuItem(
                text = "Re-enter",
                onClick = {},
                leadingIcon = { JengaIcon(JengaIcons.Check, contentDescription = null) },
            )
            JengaDropdownMenuItem(
                text = "View history",
                onClick = {},
                leadingIcon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
            )
            JengaDropdownMenuItem(text = "Remove", onClick = {}, enabled = false)
        }
    }
}
