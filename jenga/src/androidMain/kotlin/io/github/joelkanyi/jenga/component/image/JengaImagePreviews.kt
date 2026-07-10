package io.github.joelkanyi.jenga.component.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------
// Coil does not fetch over the network under Robolectric, so the goldens capture the
// themed placeholder (which is exactly the no-image / loading state).

@JengaBlockPreviews
@Composable
internal fun JengaImagePreview() {
    JengaTheme { ImageShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaImageRtlPreview() {
    JengaTheme { RtlPreview { ImageShowcase() } }
}

@Composable
private fun ImageShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        JengaImage(
            url = null,
            contentDescription = null,
            modifier = Modifier.width(220.dp).height(140.dp),
        )
        JengaImage(
            url = null,
            contentDescription = null,
            shape = JengaTheme.shapes.pill,
            modifier = Modifier.width(96.dp).height(96.dp),
        )
    }
}
