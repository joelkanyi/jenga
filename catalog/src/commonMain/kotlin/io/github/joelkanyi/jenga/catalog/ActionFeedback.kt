package io.github.joelkanyi.jenga.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbar
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.coroutines.delay

@Composable
fun ActionFeedback(
    modifier: Modifier = Modifier,
    content: @Composable (confirm: (message: String) -> Unit) -> Unit,
) {
    var message by remember {
        mutableStateOf<String?>(
            value = null,
        )
    }
    LaunchedEffect(
        message,
    ) {
        if (message != null) {
            delay(
                timeMillis = 2_000,
            )
            message = null
        }
    }
    JengaStack(
        modifier = modifier,
        space = JengaTheme.spacing.md,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            content { message = it }
            message?.let {
                JengaSnackbar(
                    message = it,
                )
            }
        },
    )
}
