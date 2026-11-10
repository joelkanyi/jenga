package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import io.github.joelkanyi.jenga.component.state.JengaEmptyState
import io.github.joelkanyi.jenga.theme.JengaTheme

@Composable
fun LivePreview(
    slug: String,
    darkTheme: Boolean,
    onHeight: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entry = remember(
        slug,
    ) {
        componentEntries().firstOrNull { it.slug == slug }
    }
    val density = LocalDensity.current
    JengaTheme(
        darkTheme = darkTheme,
        content = {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = JengaTheme.colors.background,
                    ),
                contentAlignment = Alignment.TopCenter,
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(
                                align = Alignment.Top,
                                unbounded = true,
                            )
                            .onSizeChanged {
                                onHeight(
                                    with(density) { it.height.toDp() },
                                )
                            },
                        contentAlignment = Alignment.TopCenter,
                        content = {
                            SpecimenFrame(
                                content = {
                                    if (entry == null) {
                                        JengaEmptyState(
                                            title = "Component not found",
                                            description = "There is no live preview called \"$slug\".",
                                        )
                                    } else {
                                        entry.demo()
                                    }
                                },
                            )
                        },
                    )
                },
            )
        },
    )
}
