package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.foundation.shape.jengaShapes
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
                PreviewWeek(week = LocalDate(2026, 11, 30), selectedDate = LocalDate(2026, 12, 1))
            }
            Case(label = "Across two years") {
                PreviewWeek(week = LocalDate(2026, 12, 28), selectedDate = LocalDate(2027, 1, 1))
            }
            Case(label = "Sunday first, no divider") {
                PreviewWeek(
                    week = Today,
                    firstDayOfWeek = DayOfWeek.SUNDAY,
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

@JengaBlockPreviews
@Composable
internal fun JengaWeeklyCalendarDayContentPreview() {
    JengaTheme { WeeklyCalendarDayContentShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaWeeklyCalendarDayContentRtlPreview() {
    JengaTheme {
        RtlPreview {
            Showcase {
                Case(label = "Paid and locked under the number, the 19th selected") {
                    PaymentWeek(selectedDate = LocalDate(2026, 11, 19), weekTitle = ::previewIsolatedWeekTitle)
                }
                Case(label = "In place of the number, the 18th selected") {
                    PaymentWeek(
                        selectedDate = LocalDate(2026, 11, 18),
                        showDayNumber = false,
                        weekTitle = ::previewIsolatedWeekTitle,
                    )
                }
            }
        }
    }
}

/**
 * A theme whose pill shape has 8dp corners: the ends of a band take the same corners
 * as the cell and the circle, here under today's ring at the start of a run.
 */
@Preview(name = "Shape override", showBackground = true)
@Composable
internal fun JengaWeeklyCalendarDayContentShapePreview() {
    JengaTheme(shapes = jengaShapes().copy(pill = RoundedCornerShape(8.dp))) {
        Showcase {
            Case(label = "8dp corners, under the number; today (the 16th) starts a run") {
                PaymentWeek(selectedDate = null, today = LocalDate(2026, 11, 16))
            }
            Case(label = "8dp corners, in place of the number; today (the 22nd) on its own") {
                PaymentWeek(selectedDate = null, today = LocalDate(2026, 11, 22), showDayNumber = false)
            }
        }
    }
}

@Preview(name = "Narrow", showBackground = true, widthDp = 320)
@Composable
internal fun JengaWeeklyCalendarDayContentNarrowPreview() {
    JengaTheme { WeeklyCalendarDayContentNarrowShowcase() }
}

@Preview(name = "Narrow, large font", showBackground = true, widthDp = 320, fontScale = 1.5f)
@Composable
internal fun JengaWeeklyCalendarDayContentNarrowLargeFontPreview() {
    JengaTheme { WeeklyCalendarDayContentNarrowShowcase() }
}

@Composable
private fun WeeklyCalendarDayContentNarrowShowcase() {
    Column(modifier = Modifier.background(JengaTheme.colors.background)) {
        PaymentWeek(
            selectedDate = LocalDate(2026, 11, 19),
            contentPadding = PaddingValues(horizontal = JengaTheme.spacing.lg),
        )
        PaymentWeek(selectedDate = LocalDate(2026, 11, 18), showDayNumber = false)
    }
}

/**
 * A payment week: paid on the 16th to the 18th and on the 22nd, locked on the 19th
 * and 20th, nothing on the 21st. Today is the 17th.
 */
@Composable
private fun WeeklyCalendarDayContentShowcase() {
    Showcase {
        Case(label = "Paid and locked under the number, bands; today ringed, the 19th selected") {
            PaymentWeek(selectedDate = LocalDate(2026, 11, 19))
        }
        Case(label = "In place of the number, bands; the 21st has no content; the 18th selected") {
            PaymentWeek(selectedDate = LocalDate(2026, 11, 18), showDayNumber = false)
        }
        Case(label = "Content without bands, untinted; disabled from the 20th, the 16th selected") {
            PaymentWeek(
                selectedDate = LocalDate(2026, 11, 16),
                bands = false,
                tinted = false,
                dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 19),
            )
        }
        Case(label = "Bands without content, today selected") {
            PaymentWeek(selectedDate = Today, content = false)
        }
    }
}

private fun isPaid(date: LocalDate): Boolean = date.day in 16..18 || date.day == 22

private fun isLocked(date: LocalDate): Boolean = date.day in 19..20

@Composable
private fun PaymentWeek(
    selectedDate: LocalDate?,
    showDayNumber: Boolean = true,
    bands: Boolean = true,
    content: Boolean = true,
    tinted: Boolean = true,
    today: LocalDate = Today,
    dateRange: ClosedRange<LocalDate>? = null,
    contentPadding: PaddingValues = JengaWeeklyCalendarDefaults.ContentPadding,
    weekTitle: (LocalDate, LocalDate) -> String = ::previewWeekTitle,
) {
    val paidBand = JengaTheme.colors.successContainer
    val lockedBand = JengaTheme.colors.errorContainer
    val iconSize = if (showDayNumber) 20.dp else 22.dp
    val dayContent: (@Composable (LocalDate) -> Unit)? = if (content) {
        { date -> PaymentIcon(date = date, selected = date == selectedDate, tinted = tinted, size = iconSize) }
    } else {
        null
    }
    PreviewWeek(
        selectedDate = selectedDate,
        today = today,
        dateRange = dateRange,
        contentPadding = contentPadding,
        weekTitle = weekTitle,
        showDayNumber = showDayNumber,
        dayBackground = { date ->
            when {
                !bands -> Color.Unspecified
                isPaid(date) -> paidBand
                isLocked(date) -> lockedBand
                else -> Color.Unspecified
            }
        },
        dayContent = dayContent,
    )
}

/** A check on a paid day, a lock on a locked day, nothing on any other day. */
@Composable
private fun PaymentIcon(date: LocalDate, selected: Boolean, tinted: Boolean, size: Dp) {
    // On the selected day the icon keeps the content color the calendar provides.
    val useTone = tinted && !selected
    when {
        isPaid(date) -> JengaIcon(
            imageVector = JengaIcons.CheckCircle,
            contentDescription = null,
            tint = if (useTone) JengaTheme.colors.onSuccessContainer else Color.Unspecified,
            size = size,
        )

        isLocked(date) -> JengaIcon(
            imageVector = JengaIcons.Lock,
            contentDescription = null,
            tint = if (useTone) JengaTheme.colors.onErrorContainer else Color.Unspecified,
            size = size,
        )
    }
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
            week = LocalDate(2026, 12, 28),
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
    week: LocalDate = LocalDate(2026, 11, 16),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = Today,
    dateRange: ClosedRange<LocalDate>? = null,
    isSelectable: (LocalDate) -> Boolean = { true },
    showDivider: Boolean = true,
    isMarked: ((LocalDate) -> Boolean)? = null,
    contentPadding: PaddingValues = JengaWeeklyCalendarDefaults.ContentPadding,
    weekTitle: (LocalDate, LocalDate) -> String = ::previewWeekTitle,
    showDayNumber: Boolean = true,
    dayBackground: (LocalDate) -> Color = { Color.Unspecified },
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
) {
    JengaWeeklyCalendar(
        week = week,
        selectedDate = selectedDate,
        onSelectedDateChange = {},
        onPreviousWeek = {},
        onNextWeek = {},
        weekTitle = weekTitle,
        dayOfWeekLabel = ::previewDayName,
        previousWeekContentDescription = "Previous week",
        nextWeekContentDescription = "Next week",
        firstDayOfWeek = firstDayOfWeek,
        dateRange = dateRange,
        isSelectable = isSelectable,
        today = today,
        showDivider = showDivider,
        isMarked = isMarked,
        contentPadding = contentPadding,
        showDayNumber = showDayNumber,
        dayBackground = dayBackground,
        dayContent = dayContent,
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
