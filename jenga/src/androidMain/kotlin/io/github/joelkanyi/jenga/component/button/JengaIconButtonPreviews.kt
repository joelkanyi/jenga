package io.github.joelkanyi.jenga.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaIconButtonPreview() {
    JengaTheme { IconButtonShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaIconButtonRtlPreview() {
    JengaTheme { RtlPreview { IconButtonShowcase() } }
}

@Composable
private fun IconButtonShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.Search, contentDescription = "Search")
        }
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Filled) {
            JengaIcon(JengaIcons.Add, contentDescription = "Add")
        }
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Tonal) {
            JengaIcon(JengaIcons.Info, contentDescription = "Info")
        }
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Overlay) {
            JengaIcon(JengaIcons.Flash, contentDescription = "Torch")
        }
        JengaIconButton(onClick = {}, enabled = false) {
            JengaIcon(JengaIcons.Close, contentDescription = "Close")
        }
    }
}
