package io.github.joelkanyi.jenga.component.image

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
internal actual fun PlatformAsyncImage(
    url: String,
    contentDescription: String?,
    fit: JengaImageFit,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = if (fit == JengaImageFit.Cover) ContentScale.Crop else ContentScale.Fit,
        modifier = Modifier.fillMaxSize(),
    )
}
