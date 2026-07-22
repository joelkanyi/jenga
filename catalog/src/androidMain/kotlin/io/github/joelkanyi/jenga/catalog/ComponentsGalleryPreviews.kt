package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.theme.JengaTheme

@Preview(name = "Components: Light", showBackground = true, heightDp = 4250)
@Composable
internal fun ComponentsGalleryLightPreview() {
    JengaTheme(darkTheme = false) {
        Box(Modifier.fillMaxSize().background(JengaTheme.colors.background)) {
            ComponentsGallery()
        }
    }
}

@Preview(name = "Components: Dark", showBackground = true, heightDp = 4250)
@Composable
internal fun ComponentsGalleryDarkPreview() {
    JengaTheme(darkTheme = true) {
        Box(Modifier.fillMaxSize().background(JengaTheme.colors.background)) {
            ComponentsGallery()
        }
    }
}
