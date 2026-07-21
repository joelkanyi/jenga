package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.theme.JengaTheme

@Preview(name = "Components — Light", showBackground = true, heightDp = 4250)
@Composable
internal fun ComponentsGalleryLightPreview() {
    JengaTheme(darkTheme = false) {
        JengaBox(
            padding = PaddingValues(JengaTheme.spacing.lg),
            background = JengaTheme.colors.background,
        ) { ComponentsGallery() }
    }
}

@Preview(name = "Components — Dark", showBackground = true, heightDp = 4250)
@Composable
internal fun ComponentsGalleryDarkPreview() {
    JengaTheme(darkTheme = true) {
        JengaBox(
            padding = PaddingValues(JengaTheme.spacing.lg),
            background = JengaTheme.colors.background,
        ) { ComponentsGallery() }
    }
}
