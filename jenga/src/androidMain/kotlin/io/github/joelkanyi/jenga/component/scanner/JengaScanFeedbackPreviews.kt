package io.github.joelkanyi.jenga.component.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaScanFeedbackPreview() {
    JengaTheme {
        Box(Modifier.size(320.dp).background(androidx.compose.ui.graphics.Color.Black)) {
            JengaScanFeedback(status = JengaScannerStatus.Success)
        }
    }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaScanFeedbackRtlPreview() {
    JengaTheme {
        RtlPreview {
            Box(Modifier.size(320.dp).background(androidx.compose.ui.graphics.Color.Black)) {
                JengaScanFeedback(status = JengaScannerStatus.Error)
            }
        }
    }
}
