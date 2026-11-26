package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalLayoutDirection
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
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        week: LocalDate = monday,
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
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
                        week = week,
                        selectedDate = selectedDate,
                        onSelectedDateChange = { picked += it },
                        onPreviousWeek = { previousTaps++ },
                        onNextWeek = { nextTaps++ },
                        weekTitle = { start, end -> "$start to $end" },
                        dayOfWeekLabel = { it.name.take(3) },
                        previousWeekContentDescription = "Previous week",
                        nextWeekContentDescription = "Next week",
                        firstDayOfWeek = firstDayOfWeek,
                        dateRange = dateRange,
                        isSelectable = isSelectable,
                        today = LocalDate(2026, 11, 17),
                        dateContentDescription = dateContentDescription,
                    )
                } else {
                    JengaWeeklyCalendar(
                        week = week,
                        selectedDate = selectedDate,
                        onSelectedDateChange = { picked += it },
                        onPreviousWeek = { previousTaps++ },
                        onNextWeek = { nextTaps++ },
                        weekTitle = { start, end -> "$start to $end" },
                        dayOfWeekLabel = { it.name.take(3) },
                        previousWeekContentDescription = "Previous week",
                        nextWeekContentDescription = "Next week",
                        firstDayOfWeek = firstDayOfWeek,
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
        showWeek(week = LocalDate(2026, 12, 28))
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
                            week = monday,
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
                            week = monday,
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
    fun aMidWeekDateShowsItsMondayFirstWeek() {
        showWeek(week = LocalDate(2026, 11, 19))
        (16..22).forEach { compose.onNodeWithContentDescription("2026-11-$it").assertExists() }
        compose.onNodeWithContentDescription("2026-11-15").assertDoesNotExist()
        compose.onNodeWithContentDescription("2026-11-23").assertDoesNotExist()
        compose.onNodeWithText("2026-11-16 to 2026-11-22").assertExists()
    }

    @Test
    fun aSundayBelongsToTheMondayFirstWeekThatEndsOnIt() {
        showWeek(week = LocalDate(2026, 11, 22))
        compose.onNodeWithText("2026-11-16 to 2026-11-22").assertExists()
    }

    @Test
    fun sundayFirstShowsTheSundayFirstWeekContainingTheDate() {
        showWeek(week = LocalDate(2026, 11, 19), firstDayOfWeek = DayOfWeek.SUNDAY)
        (15..21).forEach { compose.onNodeWithContentDescription("2026-11-$it").assertExists() }
        compose.onNodeWithContentDescription("2026-11-22").assertDoesNotExist()
        compose.onNodeWithText("2026-11-15 to 2026-11-21").assertExists()
    }

    @Test
    fun aSundayStartsItsOwnSundayFirstWeek() {
        showWeek(week = LocalDate(2026, 11, 22), firstDayOfWeek = DayOfWeek.SUNDAY)
        compose.onNodeWithText("2026-11-22 to 2026-11-28").assertExists()
    }

    @Test
    fun aMidWeekDateAcrossTheYearBoundaryShowsItsWeek() {
        showWeek(week = LocalDate(2027, 1, 1))
        compose.onNodeWithText("2026-12-28 to 2027-01-03").assertExists()
    }

    @Test
    fun everyDateShowsTheWeekThatContainsItForEveryFirstDayOfTheWeek() {
        var week by mutableStateOf(monday)
        var firstDayOfWeek by mutableStateOf(DayOfWeek.MONDAY)
        var shown: Pair<LocalDate, LocalDate>? = null
        compose.setContent {
            JengaTheme {
                JengaWeeklyCalendar(
                    week = week,
                    selectedDate = null,
                    onSelectedDateChange = {},
                    onPreviousWeek = {},
                    onNextWeek = {},
                    weekTitle = { start, end ->
                        shown = start to end
                        "Week"
                    },
                    dayOfWeekLabel = { it.name.take(3) },
                    previousWeekContentDescription = "Previous week",
                    nextWeekContentDescription = "Next week",
                    firstDayOfWeek = firstDayOfWeek,
                )
            }
        }
        // 16-22 November 2026 has one of each weekday.
        (16..22).map { LocalDate(2026, 11, it) }.forEach { date ->
            DayOfWeek.entries.forEach { first ->
                week = date
                firstDayOfWeek = first
                compose.waitForIdle()
                val (start, end) = shown!!
                val daysBack = start.daysUntil(date)
                assertEquals("$date, $first", first, start.dayOfWeek)
                assertTrue("$date, $first: $daysBack days back", daysBack in 0..6)
                assertEquals("$date, $first", 6, start.daysUntil(end))
                compose.onNodeWithContentDescription(date.toString()).assertExists()
            }
        }
    }

    @Test
    fun theWeekButtonsStopAtTheEndsOfTheRangeForAMidWeekDate() {
        showWeek(week = LocalDate(2026, 11, 19), dateRange = LocalDate(2026, 11, 16)..LocalDate(2026, 11, 22))
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsNotEnabled()
    }

    @Test
    fun theWeekButtonsStayOnForAMidWeekDateWhileTheRangeReachesOtherWeeks() {
        showWeek(week = LocalDate(2026, 11, 19), dateRange = LocalDate(2026, 11, 15)..LocalDate(2026, 11, 23))
        compose.onNodeWithContentDescription("Previous week").assertIsEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
    }

    @Test
    fun theWeekButtonsFollowTheSundayFirstWeekOfAMidWeekDate() {
        // Sunday first, 19 November shows 15-21 November: the range starts on the
        // first day shown and ends a day after the last.
        showWeek(
            week = LocalDate(2026, 11, 19),
            firstDayOfWeek = DayOfWeek.SUNDAY,
            dateRange = LocalDate(2026, 11, 15)..LocalDate(2026, 11, 22),
        )
        compose.onNodeWithContentDescription("Previous week").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Next week").assertIsEnabled()
    }

    @Test
    fun inRightToLeftTheFirstDayOfAMidWeekDatesWeekIsOnTheRight() {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                JengaTheme {
                    JengaWeeklyCalendar(
                        week = LocalDate(2026, 11, 19),
                        selectedDate = null,
                        onSelectedDateChange = {},
                        onPreviousWeek = {},
                        onNextWeek = {},
                        weekTitle = { _, _ -> "Week" },
                        dayOfWeekLabel = { it.name.take(3) },
                        previousWeekContentDescription = "Previous week",
                        nextWeekContentDescription = "Next week",
                    )
                }
            }
        }
        val lefts = (16..22).map { compose.onNodeWithContentDescription("2026-11-$it").getUnclippedBoundsInRoot().left }
        assertEquals(lefts.sortedDescending(), lefts)
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

    // ---- Day content and bands ---------------------------------------------

    private val week = (16..22).map { LocalDate(2026, 11, it) }

    @Composable
    private fun Week(
        tag: String = "week",
        selectedDate: LocalDate? = null,
        dateRange: ClosedRange<LocalDate>? = null,
        dateContentDescription: (LocalDate) -> String = { it.toString() },
        showDayNumber: Boolean = true,
        dayBackground: (LocalDate) -> Color = { Color.Unspecified },
        dayContent: (@Composable (LocalDate) -> Unit)? = null,
    ) {
        JengaWeeklyCalendar(
            week = monday,
            selectedDate = selectedDate,
            onSelectedDateChange = { picked += it },
            onPreviousWeek = {},
            onNextWeek = {},
            weekTitle = { _, _ -> "Week" },
            dayOfWeekLabel = { it.name.take(3) },
            previousWeekContentDescription = "Previous week",
            nextWeekContentDescription = "Next week",
            modifier = Modifier.testTag(tag),
            dateRange = dateRange,
            today = LocalDate(2026, 11, 17),
            dateContentDescription = dateContentDescription,
            showDayNumber = showDayNumber,
            dayBackground = dayBackground,
            dayContent = dayContent,
        )
    }

    private fun heightOf(tag: String): Float = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot().let { (it.bottom - it.top).value }

    private fun assertTheDayContentIsComposedOncePerDate(showDayNumber: Boolean) {
        val composed = mutableListOf<LocalDate>()
        compose.setContent {
            JengaTheme { Week(showDayNumber = showDayNumber, dayContent = { composed += it }) }
        }
        compose.waitForIdle()
        assertEquals(week, composed.sorted())
    }

    @Test
    fun theDayContentIsComposedOnceForEachOfTheSevenDates() = assertTheDayContentIsComposedOncePerDate(showDayNumber = true)

    @Test
    fun theDayContentIsComposedOnceForEachOfTheSevenDatesWithTheNumberOff() = assertTheDayContentIsComposedOncePerDate(showDayNumber = false)

    @Test
    fun theDayContentGetsTheContentColorOfTheDaysState() {
        val seen = mutableMapOf<LocalDate, Color>()
        var selectedContent = Color.Unspecified
        var disabledContent = Color.Unspecified
        var todayContent = Color.Unspecified
        var content = Color.Unspecified
        compose.setContent {
            JengaTheme {
                val colors = JengaWeeklyCalendarDefaults.colors()
                selectedContent = colors.selectedContent
                disabledContent = colors.disabledContent
                todayContent = colors.todayContent
                content = colors.content
                Week(
                    selectedDate = LocalDate(2026, 11, 18),
                    dateRange = LocalDate(2026, 11, 2)..LocalDate(2026, 11, 20),
                    dayContent = { seen[it] = LocalJengaContentColor.current },
                )
            }
        }
        compose.waitForIdle()
        assertEquals(selectedContent, seen[LocalDate(2026, 11, 18)])
        assertEquals(disabledContent, seen[LocalDate(2026, 11, 21)])
        // Today, not selected: its content takes the color of its number.
        assertEquals(todayContent, seen[LocalDate(2026, 11, 17)])
        assertEquals(content, seen[LocalDate(2026, 11, 16)])
        assertEquals(4, setOf(selectedContent, disabledContent, todayContent, content).size)
    }

    private fun contentColorOfToday(selectedDate: LocalDate?, dateRange: ClosedRange<LocalDate>?): Pair<Color?, JengaWeeklyCalendarColors> {
        var seen: Color? = null
        lateinit var colors: JengaWeeklyCalendarColors
        compose.setContent {
            JengaTheme {
                colors = JengaWeeklyCalendarDefaults.colors()
                Week(
                    selectedDate = selectedDate,
                    dateRange = dateRange,
                    showDayNumber = false,
                    dayContent = { if (it.day == 17) seen = LocalJengaContentColor.current },
                )
            }
        }
        compose.waitForIdle()
        return seen to colors
    }

    @Test
    fun todaySelectedGivesTheSelectedContentColor() {
        val (seen, colors) = contentColorOfToday(selectedDate = LocalDate(2026, 11, 17), dateRange = null)
        assertEquals(colors.selectedContent, seen)
    }

    @Test
    fun todayDisabledGivesTheDisabledContentColor() {
        val (seen, colors) = contentColorOfToday(selectedDate = null, dateRange = LocalDate(2026, 11, 18)..LocalDate(2026, 11, 30))
        assertEquals(colors.disabledContent, seen)
    }

    @Test
    fun todayGivesTheTodayContentColorWithTheNumberOff() {
        val (seen, colors) = contentColorOfToday(selectedDate = LocalDate(2026, 11, 18), dateRange = null)
        assertEquals(colors.todayContent, seen)
    }

    @Test
    fun theSelectedDayGivesItsContentColorWithTheNumberOffToo() {
        val seen = mutableMapOf<LocalDate, Color>()
        var selectedContent = Color.Unspecified
        compose.setContent {
            JengaTheme {
                selectedContent = JengaWeeklyCalendarDefaults.colors().selectedContent
                Week(
                    selectedDate = LocalDate(2026, 11, 18),
                    showDayNumber = false,
                    dayContent = { seen[it] = LocalJengaContentColor.current },
                )
            }
        }
        compose.waitForIdle()
        assertEquals(selectedContent, seen[LocalDate(2026, 11, 18)])
    }

    private fun assertADayIsDescribedByTheCallerOnly(showDayNumber: Boolean) {
        compose.setContent {
            JengaTheme {
                Week(
                    dateContentDescription = { if (it.day == 22) "Sunday 22 November, locked" else "day ${it.day}" },
                    showDayNumber = showDayNumber,
                    dayContent = { date ->
                        if (date.day == 22) {
                            JengaIcon(JengaIcons.Lock, contentDescription = "lock icon")
                            JengaText(text = "locked text")
                        }
                    },
                )
            }
        }
        // The day is one node that carries the caller's words and nothing else: the
        // icon's description and the text in the slot are not merged into it.
        compose.onNodeWithContentDescription("Sunday 22 November, locked")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ContentDescription,
                    listOf("Sunday 22 November, locked"),
                ),
            )
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Text))
            .performClick()
        compose.onNodeWithContentDescription("lock icon").assertDoesNotExist()
        compose.onNodeWithText("locked text").assertDoesNotExist()
        compose.onNodeWithText("22").assertDoesNotExist()
        assertEquals(listOf(LocalDate(2026, 11, 22)), picked)
    }

    @Test
    fun aDayIsDescribedByTheCallerAndItsContentIsNotReadSeparately() = assertADayIsDescribedByTheCallerOnly(showDayNumber = true)

    @Test
    fun aDayIsDescribedByTheCallerAndItsContentIsNotReadSeparatelyWithTheNumberOff() = assertADayIsDescribedByTheCallerOnly(showDayNumber = false)

    @Test
    fun withTheNumberOffADayWithEmptyContentShowsItsNumberAndADayWithContentDoesNot() {
        // The cell clears the semantics of what it holds, so what it places is
        // recorded by the boxes themselves.
        val placed = mutableListOf<String>()

        @Composable
        fun Mark(name: String, modifier: Modifier = Modifier) = Box(modifier.onPlaced { placed += name })
        compose.setContent {
            Column {
                DayContentOrNumber(
                    dayContent = {},
                    dayNumber = { Mark("number of the empty day", Modifier.size(10.dp)) },
                    modifier = Modifier.size(34.dp),
                )
                // Content that takes up no room counts as empty.
                DayContentOrNumber(
                    dayContent = { Mark("content with no room") },
                    dayNumber = { Mark("number of the day with no room", Modifier.size(10.dp)) },
                    modifier = Modifier.size(34.dp),
                )
                DayContentOrNumber(
                    dayContent = { Mark("content", Modifier.size(20.dp)) },
                    dayNumber = { Mark("number of the day with content", Modifier.size(10.dp)) },
                    modifier = Modifier.size(34.dp),
                )
            }
        }
        compose.waitForIdle()
        assertEquals(
            listOf("content", "number of the day with no room", "number of the empty day"),
            placed.distinct().sorted(),
        )
    }

    @Test
    fun theNumberComesAndGoesAsTheContentDoes() {
        // The cell clears semantics from the merged tree only, so the tags are still
        // found in the unmerged one; a node that is not placed is not shown.
        fun isShown(tag: String) = compose.onAllNodesWithTag(tag, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .any { it.layoutInfo.isPlaced }
        var hasContent by mutableStateOf(false)
        compose.setContent {
            DayContentOrNumber(
                dayContent = { if (hasContent) Box(Modifier.size(20.dp).testTag("content")) },
                dayNumber = { Box(Modifier.size(10.dp).testTag("number")) },
                modifier = Modifier.size(34.dp),
            )
        }
        compose.waitForIdle()
        assertEquals(listOf(false, true), listOf(isShown("content"), isShown("number")))
        hasContent = true
        compose.waitForIdle()
        assertEquals(listOf(true, false), listOf(isShown("content"), isShown("number")))
        hasContent = false
        compose.waitForIdle()
        assertEquals(listOf(false, true), listOf(isShown("content"), isShown("number")))
    }

    @Test
    fun theStripKeepsItsHeightWithoutDayContent() {
        compose.setContent {
            JengaTheme {
                // Scrolls, so that no week is squeezed by the height of the test screen.
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Week(tag = "plain")
                    Week(tag = "number-off-no-content", showDayNumber = false)
                    Week(tag = "bands", dayBackground = { Color.Green })
                    Week(tag = "content-number-off", showDayNumber = false, dayContent = {})
                    Week(tag = "content", dayContent = {})
                    Week(tag = "content-on-one-day", dayContent = { if (it.day == 16) Box(Modifier.size(20.dp)) })
                }
            }
        }
        val plain = heightOf("plain")
        assertEquals(plain, heightOf("number-off-no-content"), 0.01f)
        assertEquals(plain, heightOf("bands"), 0.01f)
        assertEquals(plain, heightOf("content-number-off"), 0.01f)
        // With the number on, the circle gives way to the 54dp cell, on every day.
        // (At least: the test font is taller than the 54dp the cell allows for.)
        val withContent = heightOf("content")
        assertTrue("$withContent, $plain", withContent >= plain + 20f)
        assertEquals(withContent, heightOf("content-on-one-day"), 0.01f)
    }

    @Test
    fun aRunOfBandsStartsAndEndsWhereANeighbourHasNone() {
        val green = Color.Green
        val red = Color.Red
        val none = Color.Unspecified
        val start = WeekBandEdges(start = true, end = false)
        val inner = WeekBandEdges(start = false, end = false)
        val end = WeekBandEdges(start = false, end = true)
        val alone = WeekBandEdges(start = true, end = true)

        // Two colors side by side are one run: nothing is rounded where they meet.
        assertEquals(
            listOf(null, start, inner, inner, end, null, null),
            weekBandEdges(listOf(none, green, green, red, red, none, none)),
        )
        // A day on its own.
        assertEquals(
            listOf(null, null, alone, null, null, null, null),
            weekBandEdges(listOf(none, none, green, none, none, none, none)),
        )
        // A full week: the run ends at the week's edges.
        assertEquals(
            listOf(start, inner, inner, inner, inner, inner, end),
            weekBandEdges(listOf(green, green, red, red, green, red, green)),
        )
        // No colors.
        assertEquals(List(7) { null }, weekBandEdges(List(7) { none }))
        // Runs cut at both edges of the week, and a single day at each edge.
        assertEquals(
            listOf(start, end, null, null, null, start, end),
            weekBandEdges(listOf(green, red, none, none, none, red, red)),
        )
        assertEquals(
            listOf(alone, null, alone, null, alone, null, alone),
            weekBandEdges(listOf(green, none, red, none, green, none, red)),
        )
        // A translucent color, as the dark theme's containers are, is a band.
        val faintGreen = green.copy(alpha = 0.12f)
        val faintRed = red.copy(alpha = 0.12f)
        assertEquals(
            listOf(start, inner, end, null, alone, null, null),
            weekBandEdges(listOf(faintGreen, faintRed, faintRed, none, faintGreen.copy(alpha = 0.01f), none, none)),
        )
        // A fully transparent color is no band.
        assertNull(weekBandEdges(listOf(Color.Transparent))[0])
        assertEquals(listOf(alone, null, alone), weekBandEdges(listOf(green, Color.Transparent, red)))
    }
}
