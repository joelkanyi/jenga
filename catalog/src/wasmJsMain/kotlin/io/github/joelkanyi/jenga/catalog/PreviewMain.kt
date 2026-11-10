package io.github.joelkanyi.jenga.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.MessageEvent
import org.w3c.dom.url.URLSearchParams
import kotlin.math.ceil

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val query = URLSearchParams(
        init = window.location.search.toJsString(),
    )
    val slug = query.get(
        name = "component",
    ).orEmpty()
    var darkTheme by mutableStateOf(
        value = query.get(
            name = "theme",
        ) == "dark",
    )
    window.addEventListener(
        type = "message",
        callback = { event ->
            val message = event as MessageEvent
            val text = (message.data as? JsString)?.toString()
            if (message.origin == window.location.origin) {
                when (text) {
                    "jenga-theme:dark" -> darkTheme = true
                    "jenga-theme:light" -> darkTheme = false
                }
            }
        },
    )
    ComposeViewport(
        viewportContainer = document.body!!,
        content = {
            LivePreview(
                slug = slug,
                darkTheme = darkTheme,
                onHeight = { height ->
                    window.parent.postMessage(
                        message = "jenga-height:${ceil(height.value).toInt()}".toJsString(),
                        targetOrigin = window.location.origin,
                    )
                },
            )
        },
    )
}
