package io.github.joelkanyi.jenga.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
internal fun JengaNavigationBarPreview() {
    JengaTheme { NavBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaNavigationBarRtlPreview() {
    JengaTheme { RtlPreview { NavBarShowcase() } }
}

@Composable
private fun NavBarShowcase() {
    JengaNavigationBar(modifier = Modifier.background(JengaTheme.colors.background)) {
        JengaNavigationBarItem(
            selected = true,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
            label = "Events",
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Check, contentDescription = null) },
            label = "Scan",
        )
        JengaNavigationBarItem(
            selected = false,
            onClick = {},
            icon = { JengaIcon(JengaIcons.Info, contentDescription = null) },
            label = "Stats",
        )
    }
}
