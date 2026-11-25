package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaWeeklyCalendarTest {

    @get:Rule
    val compose = createComposeRule()

    private val picked = mutableListOf<LocalDate>()
    private var previousTaps = 0
    private var nextTaps = 0

    private val monday = LocalDate(2026, 11, 16)

    private fun showWeek(
        weekStart: LocalDate = monday,
        selectedDate: LocalDate? = null,
        dateRange: ClosedRange<LocalDate>? = null,
        isSelectable: (LocalDate) -> Boolean = { true },
        previousWeekEnabled: Boolean? = null,
        nextWeekEnabled: Boolean? = null,
        dateContentDescription: (LocalDate) -> String = { it.toString() },
    ) {
        compose.setContent {
            JengaTheme {
                if (previousWeekEnabled == null || nextWeekEnabled == null) {
                    JengaWeeklyCalendar(
                        weekStart = weekStart,
                        selectedDate = selectedDate,
                        onSelectedDateChange = { picked += it },
                        onPreviousWeek = { previousTaps++ },
                        onNextWeek = { nextTaps++ },
                        weekTitle = { start, end -> "$start to $end" },
                        dayOfWeekLabel = { it.name.take(3) },
                        previousWeekContentDescription = "Previous week",
                        nextWeekContentDescription = "Next week",
                        dateRange = dateRange,
                        isSelectable = isSelectable,
                        today = LocalDate(2026, 11, 17),
                        dateContentDescription = dateContentDescription,
                    )
                } else {
                    JengaWeeklyCalendar(
                        weekStart = weekStart,
                        selectedDate = selectedDate,
                        onSelectedDateChange = { picked += it },
                        onPreviousWeek = { previousTaps++ },
                        onNextWeek = { nextTaps++ },
                        weekTitle = { start, end -> "$start to $end" },
                        dayOfWeekLabel = { it.name.take(3) },
                        previousWeekContentDescription = "Previous week",
                        nextWeekContentDescription = "Next week",
                        dateRange = dateRange,
                        isSelectable = isSelectable,
                        today = LocalDate(2026, 11, 17),
                        dateContentDescription = dateContentDescription,
                        previousWeekEnabled = previousWeekEnabled,
                        nextWeekEnabled = nextWeekEnabled,
                    )
                }
            }
        }
    }

    @Test
    fun showsTheSevenDaysFromTheWeekStart() {
        showWeek()
        (16..22).forEach { compose.onNodeWithContentDescription("2026-11-$it").assertExists() }
        compose.onNodeWithContentDescription("2026-11-15").assertDoesNotExist()
        compose.onNodeWithContentDescription("2026-11-23").assertDoesNotExist()
        compose.onNodeWithText("2026-11-16 to 2026-11-22").assertExists()
    }

    @Test
    fun aWeekCrossesTheYearBoundary() {
        showWeek(weekStart = LocalDate(2026, 12, 28))
        compose.onNodeWithContentDescription("2026-12-31").assertExists()
        compose.onNodeWithContentDescription("2027-01-03").assertExists()
        compose.onNodeWithText("2026-12-28 to 2027-01-03").assertExists()
    }

    @Test
    fun tappingADayReportsItsDate() {
        showWeek()
        compose.onNodeWithContentDescription("2026-11-19").performClick()
        assertEquals(listOf(LocalDate(2026, 11, 19)), picked)
    }

    @Test
    fun theSelectedDayIsASelectedTab() {
        showWeek(selectedDate = LocalDate(2026, 11, 18))
        val isTab = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
        compose.onNodeWithContentDescription("2026-11-18").assertIsSelected().assert(isTab)
        compose.onNodeWithContentDescription("2026-11-17").assertIsNotSelected().assert(isTab)
    }

    @Test
    fun daysOutsideTheRangeAreDisabledAndDoNotReport() {
        showWeek(dateRange = LocalDate(2026, 11, 17)..LocalDate(2026, 11, 19))
        compose.onNodeWithContentDescription("2026-11-16").assertIsNotEnabled().performClick()
        compose.onNodeWithContentDescription("2026-11-17").assertIsEnabled()
        compose.onNodeWithContentDescription("2026-11-19").assertIsEnabled()
        compose.onNodeWithContentDescription("2026-11-20").assertIsNotEnabled().performClick()
        assertEquals(emptyList<LocalDate>(), picked)
    }

    @Test
    fun daysThatAreNotSelectableAreDisabled() {
        showWeek(isSelectable = { it.dayOfWeek != DayOfWeek.SUNDAY })
        compose.onNodeWithContentDescription("2026-11-22").assertIsNotEnabled().performClick()
        compose.onNodeWithContentDescription("2026-11-21").assertIsEnabled()
        assertEquals(emptyList<LocalDate>(), picked)
    }

    @Test
    fun theWeekButtonsCallBack() {
        showWeek()
        compose.onNodeWithContentDescription("Previous week").assertIsEnabled().performClick()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled().performClick()
        compose.onNodeWithContentDescription("Next week").performClick()
        assertEquals(1, previousTaps)
        assertEquals(2, nextTaps)
    }

    @Test
    fun theWeekButtonsStopAtTheEndsOfTheRange() {
        showWeek(dateRange = LocalDate(2026, 11, 16)..LocalDate(2026, 11, 22))
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled().performClick()
        compose.onNodeWithContentDescription("Next week").assertIsNotEnabled().performClick()
        assertEquals(0, previousTaps)
        assertEquals(0, nextTaps)
    }

    @Test
    fun theWeekButtonsStayOnWhileTheRangeReachesOtherWeeks() {
        showWeek(dateRange = LocalDate(2026, 11, 15)..LocalDate(2026, 11, 23))
        compose.onNodeWithContentDescription("Previous week").assertIsEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
    }

    @Test
    fun theWeekButtonsCanBeTurnedOffWithoutARange() {
        showWeek(previousWeekEnabled = false, nextWeekEnabled = true)
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
        compose.onNodeWithContentDescription("2026-11-16").assertIsEnabled()
    }

    @Test
    fun theWeekButtonsCanStayOnInsideARange() {
        showWeek(
            dateRange = LocalDate(2026, 11, 16)..LocalDate(2026, 11, 22),
            previousWeekEnabled = true,
            nextWeekEnabled = true,
        )
        compose.onNodeWithContentDescription("Previous week").assertIsEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
    }

    @Test
    fun aDayIsAnnouncedWithTheCallersDescriptionOnly() {
        showWeek(dateContentDescription = { if (it.day == 17) "Tuesday 17 November, today" else "day ${it.day}" })
        compose.onNodeWithContentDescription("Tuesday 17 November, today").assertExists()
        compose.onNodeWithContentDescription("day 22").assertExists()
        // The day name and number are not read out a second time.
        compose.onNodeWithText("17").assertDoesNotExist()
        compose.onNodeWithText("TUE").assertDoesNotExist()
    }

    @Test
    fun theDividerCanBeHidden() {
        compose.setContent {
            JengaTheme {
                Column {
                    listOf(true, false).forEach { divider ->
                        JengaWeeklyCalendar(
                            weekStart = monday,
                            selectedDate = null,
                            onSelectedDateChange = {},
                            onPreviousWeek = {},
                            onNextWeek = {},
                            weekTitle = { _, _ -> "Week" },
                            dayOfWeekLabel = { it.name.take(3) },
                            previousWeekContentDescription = "Previous week",
                            nextWeekContentDescription = "Next week",
                            modifier = Modifier.testTag("divider-$divider"),
                            showDivider = divider,
                        )
                    }
                }
            }
        }
        val with = compose.onNodeWithTag("divider-true").getUnclippedBoundsInRoot()
        val without = compose.onNodeWithTag("divider-false").getUnclippedBoundsInRoot()
        assertEquals(1f, ((with.bottom - with.top) - (without.bottom - without.top)).value, 0.01f)
    }

    @Test
    fun aMarkerRowIsKeptForEveryDayOnceMarkersAreOn() {
        compose.setContent {
            JengaTheme {
                Column {
                    listOf<((LocalDate) -> Boolean)?>(null, { false }, { true }).forEachIndexed { index, marker ->
                        JengaWeeklyCalendar(
                            weekStart = monday,
                            selectedDate = null,
                            onSelectedDateChange = {},
                            onPreviousWeek = {},
                            onNextWeek = {},
                            weekTitle = { _, _ -> "Week" },
                            dayOfWeekLabel = { it.name.take(3) },
                            previousWeekContentDescription = "Previous week",
                            nextWeekContentDescription = "Next week",
                            modifier = Modifier.testTag("markers-$index"),
                            isMarked = marker,
                        )
                    }
                }
            }
        }
        val heights = (0..2).map { index ->
            compose.onNodeWithTag("markers-$index").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        }
        assertEquals(8f, (heights[1] - heights[0]).value, 0.01f)
        assertEquals(heights[1].value, heights[2].value, 0.01f)
    }

    @Test
    fun weekStartOfFindsTheFirstDayOfTheWeek() {
        val weekStartOf = JengaWeeklyCalendarDefaults::weekStartOf
        // Monday first: a Sunday belongs to the week that ends on it.
        assertEquals(monday, weekStartOf(LocalDate(2026, 11, 22), DayOfWeek.MONDAY))
        assertEquals(monday, weekStartOf(monday, DayOfWeek.MONDAY))
        assertEquals(monday, JengaWeeklyCalendarDefaults.weekStartOf(LocalDate(2026, 11, 19)))
        // Sunday first: a Sunday starts its own week.
        assertEquals(LocalDate(2026, 11, 22), weekStartOf(LocalDate(2026, 11, 22), DayOfWeek.SUNDAY))
        assertEquals(LocalDate(2026, 11, 15), weekStartOf(LocalDate(2026, 11, 21), DayOfWeek.SUNDAY))
        // Across a year boundary.
        assertEquals(LocalDate(2026, 12, 28), weekStartOf(LocalDate(2027, 1, 1), DayOfWeek.MONDAY))
        assertEquals(LocalDate(2026, 12, 27), weekStartOf(LocalDate(2027, 1, 2), DayOfWeek.SUNDAY))
    }

    @Test
    fun weekStartOfHoldsForEveryDayAndEveryFirstDayOfTheWeek() {
        // 16-22 November 2026 has one of each weekday.
        (16..22).map { LocalDate(2026, 11, it) }.forEach { date ->
            DayOfWeek.entries.forEach { firstDayOfWeek ->
                val start = JengaWeeklyCalendarDefaults.weekStartOf(date, firstDayOfWeek)
                val daysBack = start.daysUntil(date)
                assertEquals("$date, $firstDayOfWeek", firstDayOfWeek, start.dayOfWeek)
                assertTrue("$date, $firstDayOfWeek: $daysBack days back", daysBack in 0..6)
            }
        }
    }

    @Test
    fun aDisabledDayCanStillBeShownAsSelected() {
        showWeek(selectedDate = LocalDate(2026, 11, 21), dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 19))
        compose.onNodeWithContentDescription("2026-11-21").assertIsSelected().assertIsNotEnabled().performClick()
        assertEquals(emptyList<LocalDate>(), picked)
    }

    @Test
    fun aSelectionInAnotherWeekSelectsNoDay() {
        showWeek(selectedDate = LocalDate(2026, 11, 25))
        (16..22).forEach { compose.onNodeWithContentDescription("2026-11-$it").assertIsNotSelected() }
    }

    @Test
    fun aRangeInsideTheWeekTurnsBothWeekButtonsOff() {
        showWeek(dateRange = LocalDate(2026, 11, 18)..LocalDate(2026, 11, 20))
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsNotEnabled()
    }

    @Test
    fun aRangeThatStartsMidWeekAndEndsMidNextWeekLeavesOnlyNextOn() {
        showWeek(dateRange = LocalDate(2026, 11, 18)..LocalDate(2026, 11, 25))
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
    }

    @Test
    fun aRangeThatEndsMidWeekLeavesOnlyPreviousOn() {
        showWeek(dateRange = LocalDate(2026, 11, 11)..LocalDate(2026, 11, 18))
        compose.onNodeWithContentDescription("Previous week").assertIsEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsNotEnabled()
    }

    @Test
    fun todayIsNotSelectedUntilItIsPicked() {
        // The ring itself is checked by the screenshot tests; here, that being today
        // does not make a day selected or stop it being picked.
        showWeek(selectedDate = LocalDate(2026, 11, 18))
        compose.onNodeWithContentDescription("2026-11-17").assertIsNotSelected().assertIsEnabled().performClick()
        assertEquals(listOf(LocalDate(2026, 11, 17)), picked)
    }
}
