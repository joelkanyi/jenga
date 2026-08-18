package io.github.joelkanyi.jenga.component.image

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformAsyncImage(
    url: String,
    contentDescription: String?,
    fit: JengaImageFit,
) {
    // Placeholder-only on Web until an async image loader is wired.
}
