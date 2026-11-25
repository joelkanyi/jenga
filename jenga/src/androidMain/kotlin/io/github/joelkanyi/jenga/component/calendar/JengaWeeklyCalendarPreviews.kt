package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaWeeklyCalendarPreview() {
    JengaTheme { WeeklyCalendarShowcase() }
}

@JengaBlockPreviews
@Composable
internal fun JengaWeeklyCalendarDisabledPreview() {
    JengaTheme { WeeklyCalendarDisabledShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaWeeklyCalendarRtlPreview() {
    JengaTheme {
        RtlPreview {
            Showcase {
                Case(label = "Selected, today ringed, markers") {
                    PreviewWeek(
                        selectedDate = LocalDate(2026, 11, 18),
                        isMarked = { it.day % 2 == 0 },
                        weekTitle = ::previewIsolatedWeekTitle,
                    )
                }
                Case(label = "Disabled from the 20th") {
                    PreviewWeek(
                        selectedDate = LocalDate(2026, 11, 18),
                        dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 19),
                        weekTitle = ::previewIsolatedWeekTitle,
                    )
                }
            }
        }
    }
}

@Preview(name = "Boundaries", showBackground = true)
@Composable
internal fun JengaWeeklyCalendarBoundariesPreview() {
    JengaTheme {
        Showcase {
            Case(label = "Across two months") {
                PreviewWeek(weekStart = LocalDate(2026, 11, 30), selectedDate = LocalDate(2026, 12, 1))
            }
            Case(label = "Across two years") {
                PreviewWeek(weekStart = LocalDate(2026, 12, 28), selectedDate = LocalDate(2027, 1, 1))
            }
            Case(label = "Sunday first, no divider") {
                PreviewWeek(
                    weekStart = JengaWeeklyCalendarDefaults.weekStartOf(Today, DayOfWeek.SUNDAY),
                    selectedDate = LocalDate(2026, 11, 19),
                    showDivider = false,
                )
            }
        }
    }
}

@Preview(name = "Narrow, large font", showBackground = true, widthDp = 320, fontScale = 1.5f)
@Composable
internal fun JengaWeeklyCalendarNarrowPreview() {
    JengaTheme { WeeklyCalendarNarrowShowcase() }
}

@Preview(name = "Narrow, largest font", showBackground = true, widthDp = 320, fontScale = 2f)
@Composable
internal fun JengaWeeklyCalendarNarrowLargestFontPreview() {
    JengaTheme { WeeklyCalendarNarrowShowcase() }
}

@Composable
private fun WeeklyCalendarNarrowShowcase() {
    Column(modifier = Modifier.background(JengaTheme.colors.background)) {
        PreviewWeek(
            selectedDate = LocalDate(2026, 11, 22),
            isMarked = { it.day % 2 == 0 },
            contentPadding = PaddingValues(horizontal = JengaTheme.spacing.lg),
        )
        PreviewWeek(
            weekStart = LocalDate(2026, 12, 28),
            selectedDate = LocalDate(2026, 12, 30),
            dateRange = LocalDate(2026, 12, 1)..LocalDate(2026, 12, 31),
        )
    }
}

@Composable
private fun WeeklyCalendarShowcase() {
    Showcase {
        Case(label = "A week, nothing selected") {
            PreviewWeek(selectedDate = null, today = LocalDate(2026, 11, 10))
        }
        Case(label = "Today selected") {
            PreviewWeek(selectedDate = Today)
        }
        Case(label = "Another day selected, today ringed") {
            PreviewWeek(selectedDate = LocalDate(2026, 11, 18))
        }
        Case(label = "Markers") {
            PreviewWeek(
                selectedDate = LocalDate(2026, 11, 16),
                isMarked = { it.day in setOf(16, 17, 19, 20) },
            )
        }
    }
}

@Composable
private fun WeeklyCalendarDisabledShowcase() {
    Showcase {
        Case(label = "Disabled from the 20th, next week off") {
            PreviewWeek(
                selectedDate = LocalDate(2026, 11, 18),
                dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 19),
            )
        }
        Case(label = "Disabled before the 18th: today disabled, previous week off") {
            PreviewWeek(
                selectedDate = LocalDate(2026, 11, 19),
                dateRange = LocalDate(2026, 11, 18)..LocalDate(2026, 12, 31),
            )
        }
        Case(label = "Selected day disabled (the 21st), today disabled (the 20th)") {
            PreviewWeek(
                selectedDate = LocalDate(2026, 11, 21),
                today = LocalDate(2026, 11, 20),
                dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 19),
            )
        }
        Case(label = "Weekend not selectable") {
            PreviewWeek(
                selectedDate = LocalDate(2026, 11, 18),
                isSelectable = { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY },
            )
        }
    }
}

@Composable
private fun Showcase(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
    ) {
        content()
    }
}

@Composable
private fun Case(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs)) {
        JengaText(text = label, style = JengaTheme.typography.caption, color = JengaTheme.colors.textMuted)
        content()
    }
}

@Composable
private fun PreviewWeek(
    selectedDate: LocalDate?,
    weekStart: LocalDate = LocalDate(2026, 11, 16),
    today: LocalDate = Today,
    dateRange: ClosedRange<LocalDate>? = null,
    isSelectable: (LocalDate) -> Boolean = { true },
    showDivider: Boolean = true,
    isMarked: ((LocalDate) -> Boolean)? = null,
    contentPadding: PaddingValues = JengaWeeklyCalendarDefaults.ContentPadding,
    weekTitle: (LocalDate, LocalDate) -> String = ::previewWeekTitle,
) {
    JengaWeeklyCalendar(
        weekStart = weekStart,
        selectedDate = selectedDate,
        onSelectedDateChange = {},
        onPreviousWeek = {},
        onNextWeek = {},
        weekTitle = weekTitle,
        dayOfWeekLabel = ::previewDayName,
        previousWeekContentDescription = "Previous week",
        nextWeekContentDescription = "Next week",
        dateRange = dateRange,
        isSelectable = isSelectable,
        today = today,
        showDivider = showDivider,
        isMarked = isMarked,
        contentPadding = contentPadding,
    )
}

private val Today = LocalDate(2026, 11, 17)

private fun previewWeekTitle(start: LocalDate, end: LocalDate): String = "${previewShortDate(start)} – ${previewShortDate(end)}"

/**
 * The preview title wrapped in a left-to-right isolate (U+2066 … U+2069), as an app
 * does for a Latin date range inside right-to-left text: without it the bidi
 * algorithm moves the leading day number to the end ("Nov – 22 Nov 16").
 */
private fun previewIsolatedWeekTitle(start: LocalDate, end: LocalDate): String = "\u2066${previewWeekTitle(start, end)}\u2069"

private fun previewShortDate(date: LocalDate): String = "${date.day} ${date.month.name.take(3).lowercase().replaceFirstChar(Char::uppercase)}"

private fun previewDayName(day: DayOfWeek): String = day.name.take(3).lowercase().replaceFirstChar(Char::uppercase)
