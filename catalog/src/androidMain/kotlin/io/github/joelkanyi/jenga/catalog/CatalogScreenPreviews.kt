package io.github.joelkanyi.jenga.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Catalog screenshot goldens (Roborazzi) --------------------------------

@Preview(name = "Catalog — Light", showBackground = true, heightDp = 3200)
@Composable
internal fun CatalogScreenLightPreview() {
    JengaTheme(darkTheme = false) {
        CatalogScreen(darkTheme = false, onToggleTheme = {})
    }
}

@Preview(name = "Catalog — Dark", showBackground = true, heightDp = 3200)
@Composable
internal fun CatalogScreenDarkPreview() {
    JengaTheme(darkTheme = true) {
        CatalogScreen(darkTheme = true, onToggleTheme = {})
    }
}
