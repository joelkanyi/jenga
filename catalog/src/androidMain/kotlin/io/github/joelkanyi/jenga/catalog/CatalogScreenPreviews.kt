package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Catalog screenshot goldens (Roborazzi) --------------------------------

@Preview(name = "Catalog shell: Light", showBackground = true, heightDp = 1200)
@Composable
internal fun CatalogShellLightPreview() {
    JengaTheme(darkTheme = false) {
        CatalogScreen(darkTheme = false, onToggleTheme = {})
    }
}

@Preview(name = "Catalog shell: Dark", showBackground = true, heightDp = 1200)
@Composable
internal fun CatalogShellDarkPreview() {
    JengaTheme(darkTheme = true) {
        CatalogScreen(darkTheme = true, onToggleTheme = {})
    }
}

@Preview(name = "Brand: Light", showBackground = true)
@Composable
internal fun BrandPlaygroundLightPreview() {
    Surface(darkTheme = false) { BrandPlayground() }
}

@Preview(name = "Brand: Dark", showBackground = true)
@Composable
internal fun BrandPlaygroundDarkPreview() {
    Surface(darkTheme = true) { BrandPlayground() }
}

@Preview(name = "Foundations: Light", showBackground = true)
@Composable
internal fun FoundationsLightPreview() {
    Surface(darkTheme = false) { FoundationsSection() }
}

@Composable
private fun Surface(darkTheme: Boolean, content: @Composable () -> Unit) {
    JengaTheme(darkTheme = darkTheme) {
        JengaBox(
            padding = PaddingValues(JengaTheme.spacing.xl),
            background = JengaTheme.colors.background,
        ) { content() }
    }
}
