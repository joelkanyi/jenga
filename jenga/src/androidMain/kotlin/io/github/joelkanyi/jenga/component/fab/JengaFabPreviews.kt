package io.github.joelkanyi.jenga.component.fab

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
import androidx.compose.ui.draw.shadow
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
internal fun JengaFabPreview() {
    JengaTheme { FabShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaFabRtlPreview() {
    JengaTheme { RtlPreview { FabShowcase() } }
}

@Composable
private fun FabShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JengaFab(onClick = {}) { JengaIcon(JengaIcons.Add, contentDescription = "Add") }
        JengaExtendedFab(
            label = "Ask",
            onClick = {},
            icon = { JengaIcon(JengaIcons.MessageCircle, contentDescription = null) },
        )
    }
}
