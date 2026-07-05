package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * Root of the catalog. Owns the light/dark toggle state and wraps the gallery in
 * [JengaTheme] so the whole catalog re-themes instantly when toggled.
 */
@Composable
fun CatalogApp() {
    var darkTheme by remember { mutableStateOf(false) }
    val systemDark = isSystemInDarkTheme()
    // Seed from the system once, then let the in-app toggle take over.
    var initialized by remember { mutableStateOf(false) }
    if (!initialized) {
        darkTheme = systemDark
        initialized = true
    }

    JengaTheme(darkTheme = darkTheme) {
        CatalogScreen(
            darkTheme = darkTheme,
            onToggleTheme = { darkTheme = !darkTheme },
        )
    }
}
