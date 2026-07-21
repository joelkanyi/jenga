package io.github.joelkanyi.jenga.component.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaSliderPreview() {
    JengaTheme { SliderShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSliderRtlPreview() {
    JengaTheme { RtlPreview { SliderShowcase() } }
}

@Composable
private fun SliderShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        JengaSlider(value = 0.3f, onValueChange = {})
        JengaSlider(value = 0.7f, onValueChange = {})
        JengaSlider(value = 0.5f, onValueChange = {}, enabled = false)
    }
}
