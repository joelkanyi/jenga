package io.github.joelkanyi.jenga.catalog

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/** Desktop (JVM) entry point for the Jenga catalog. Run with `./gradlew :catalog:run`. */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 480.dp, height = 900.dp),
        title = "Jenga Catalog",
    ) {
        CatalogApp()
    }
}
