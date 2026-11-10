package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme

val OverlayStageWidth = 560.dp

@Composable
fun SpecimenFrame(
    stage: CatalogStage,
    modifier: Modifier = Modifier,
    background: Color = JengaTheme.colors.background,
    content: @Composable () -> Unit,
) {
    val stageSize = when (stage) {
        CatalogStage.Inline -> Modifier.width(
            width = 400.dp,
        )
        CatalogStage.Overlay ->
            Modifier
                .width(
                    width = OverlayStageWidth,
                )
                .fillMaxHeight()
    }
    val alignment = when (stage) {
        CatalogStage.Inline -> Alignment.Center
        CatalogStage.Overlay -> Alignment.TopCenter
    }
    Box(
        modifier = modifier
            .then(
                other = stageSize,
            )
            .background(
                color = background,
            )
            .padding(
                all = 32.dp,
            ),
        contentAlignment = alignment,
        content = {
            content()
        },
    )
}
