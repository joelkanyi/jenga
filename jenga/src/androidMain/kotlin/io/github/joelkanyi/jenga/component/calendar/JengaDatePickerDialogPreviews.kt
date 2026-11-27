package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaDatePickerDialogPreview() {
    JengaTheme { DatePickerDialogShowcase(title = null) }
}

@JengaBlockPreviews
@Composable
internal fun JengaDatePickerDialogTitlePreview() {
    JengaTheme { DatePickerDialogShowcase(title = "Payment date") }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDatePickerDialogTitleRtlPreview() {
    JengaTheme { RtlPreview { DatePickerDialogShowcase(title = "Payment date") } }
}

// A long title wraps. Dark adds nothing over the titled dark preview, so it is left out.
@Preview(name = "Light", showBackground = true)
@Preview(name = "Large font", showBackground = true, fontScale = 1.5f)
@Composable
internal fun JengaDatePickerDialogLongTitlePreview() {
    JengaTheme { DatePickerDialogShowcase(title = LONG_TITLE) }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDatePickerDialogLongTitleRtlPreview() {
    JengaTheme { RtlPreview { DatePickerDialogShowcase(title = LONG_TITLE) } }
}

@Composable
private fun DatePickerDialogShowcase(title: String?) {
    Box(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        // Render the dialog card inline (the Dialog window itself can't be
        // captured in a static screenshot).
        JengaDatePickerDialogSurface(
            selectedDate = LocalDate(2026, 11, 18),
            onSelectedDateChange = {},
            onDismissRequest = {},
            onConfirm = {},
            confirmLabel = "OK",
            dismissLabel = "Cancel",
            monthTitle = ::dialogPreviewMonthTitle,
            dayOfWeekLabel = ::dialogPreviewDayLabel,
            previousMonthContentDescription = "Previous month",
            nextMonthContentDescription = "Next month",
            isSelectable = { true },
            dateContentDescription = { it.toString() },
            colors = JengaCalendarDefaults.colors(),
            dateRange = null,
            title = title,
            yearPickerLabel = null,
            today = LocalDate(2026, 11, 17),
        )
    }
}

private const val LONG_TITLE = "First payment date for the twelve month solar home system payment plan"

private fun dialogPreviewMonthTitle(month: YearMonth): String = "${month.month.name.lowercase().replaceFirstChar(Char::uppercase)} ${month.year}"

private fun dialogPreviewDayLabel(day: DayOfWeek): String = day.name.take(2).lowercase().replaceFirstChar(Char::uppercase)
