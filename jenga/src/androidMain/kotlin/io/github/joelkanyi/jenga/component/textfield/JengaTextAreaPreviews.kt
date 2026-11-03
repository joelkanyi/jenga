package io.github.joelkanyi.jenga.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaTextAreaPreview() {
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.background).padding(JengaTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        ) {
            JengaTextArea(
                value = "",
                onValueChange = {},
                label = "Visit notes",
                placeholder = "What happened on the visit?",
                maxLength = 512,
            )
            JengaTextArea(
                value = "Customer was out. Spoke to the neighbour, who said they will be back on Friday afternoon.",
                onValueChange = {},
                label = "Visit notes",
                maxLength = 512,
            )
            JengaTextArea(
                value = "Too short",
                onValueChange = {},
                label = "Reason",
                status = JengaTextFieldStatus.Error,
                supportingText = "Say why the order was cancelled",
                maxLength = 9,
            )
        }
    }
}
