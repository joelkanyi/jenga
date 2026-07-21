package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTopAppBarPreview() {
    JengaTheme { JengaTopAppBar(title = "Sol Fest 2026", subtitle = "Gate A · Online") }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTopAppBarRtlPreview() {
    JengaTheme { RtlPreview { JengaTopAppBar(title = "Sol Fest 2026", subtitle = "Gate A · Online") } }
}
