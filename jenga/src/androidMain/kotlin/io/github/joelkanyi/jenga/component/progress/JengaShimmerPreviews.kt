package io.github.joelkanyi.jenga.component.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaShimmerPreview() {
    JengaTheme { ShimmerShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaShimmerRtlPreview() {
    JengaTheme { RtlPreview { ShimmerShowcase() } }
}

@Composable
private fun ShimmerShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        Box(Modifier.fillMaxWidth().height(20.dp).clip(JengaTheme.shapes.sm).jengaShimmer())
        Box(Modifier.fillMaxWidth(0.7f).height(20.dp).clip(JengaTheme.shapes.sm).jengaShimmer())
        Box(Modifier.fillMaxWidth(0.4f).height(20.dp).clip(JengaTheme.shapes.sm).jengaShimmer())
    }
}
