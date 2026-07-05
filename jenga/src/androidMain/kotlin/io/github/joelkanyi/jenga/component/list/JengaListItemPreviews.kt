package io.github.joelkanyi.jenga.component.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaListItemPreview() {
    JengaTheme { ListItemShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaListItemRtlPreview() {
    JengaTheme { RtlPreview { ListItemShowcase() } }
}

@Composable
private fun ListItemShowcase() {
    Column(modifier = Modifier.background(JengaTheme.colors.surface)) {
        JengaListItem(
            headline = "Gate A",
            supporting = "Main entrance · 2 attendants",
            leadingContent = { JengaIcon(JengaIcons.Check, contentDescription = null, tint = JengaTheme.colors.success) },
            trailingContent = { JengaIcon(JengaIcons.ChevronRight, contentDescription = null) },
            onClick = {},
        )
        JengaListItem(
            headline = "Gate B",
            supporting = "North wing",
            trailingContent = { JengaIcon(JengaIcons.ChevronRight, contentDescription = null) },
            onClick = {},
        )
    }
}
