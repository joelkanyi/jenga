package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme

@Composable
fun SpecimenFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .width(
                width = 400.dp,
            )
            .background(
                color = JengaTheme.colors.background,
            )
            .padding(
                all = 32.dp,
            ),
        contentAlignment = Alignment.Center,
        content = {
            content()
        },
    )
}
