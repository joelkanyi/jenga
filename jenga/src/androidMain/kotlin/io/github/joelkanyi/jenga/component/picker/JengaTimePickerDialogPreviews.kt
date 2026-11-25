package io.github.joelkanyi.jenga.component.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.LocalTime

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaTimePickerDialogPreview() {
    JengaTheme { TimePickerDialogShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaTimePickerDialogRtlPreview() {
    JengaTheme { RtlPreview { TimePickerDialogShowcase() } }
}

@Composable
private fun TimePickerDialogShowcase() {
    Box(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        // Render the dialog card inline (the Dialog window itself can't be
        // captured in a static screenshot).
        JengaTimePickerDialogSurface(
            time = LocalTime(14, 30),
            onTimeChange = {},
            onDismissRequest = {},
            onConfirm = {},
            title = "Delivery time",
            confirmLabel = "OK",
            dismissLabel = "Cancel",
            hourLabel = "Hour",
            minuteLabel = "Minute",
            amLabel = "AM",
            pmLabel = "PM",
            style = JengaTimeInputStyle.Wheel,
            is24Hour = false,
            minuteStep = 1,
        )
    }
}
