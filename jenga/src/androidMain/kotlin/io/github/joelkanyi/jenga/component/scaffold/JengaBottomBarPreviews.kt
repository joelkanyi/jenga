package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.button.JengaIconButtonVariant
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBottomBarPreview() {
    JengaTheme { BottomBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBottomBarRtlPreview() {
    JengaTheme { RtlPreview { BottomBarShowcase() } }
}

@Composable
private fun BottomBarShowcase() {
    JengaBottomBar {
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.Search, contentDescription = "Search")
        }
        JengaButton(text = "Manual entry", onClick = {}, modifier = Modifier.weight(1f))
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.History, contentDescription = "History")
        }
    }
}
