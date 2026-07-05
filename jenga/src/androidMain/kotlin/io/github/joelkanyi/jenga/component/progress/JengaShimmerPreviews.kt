package io.github.joelkanyi.jenga.component.progress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaColors
import kotlinx.coroutines.launch

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
