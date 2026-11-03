package io.github.joelkanyi.jenga.component.picker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaTimeInputTest {

    @get:Rule
    val compose = createComposeRule()

    private val twelveHour = TwelveHourClock(is24Hour = false)

    @Test
    fun midnightAndNoonShowAsTwelve() {
        assertEquals(12, twelveHour.shownHour(LocalTime(0, 15)))
        assertEquals(12, twelveHour.shownHour(LocalTime(12, 15)))
        assertEquals(1, twelveHour.shownHour(LocalTime(13, 0)))
    }

    @Test
    fun pickingAnHourKeepsMorningOrAfternoon() {
        assertEquals(LocalTime(0, 30), twelveHour.withHour(LocalTime(9, 30), 12))
        assertEquals(LocalTime(12, 30), twelveHour.withHour(LocalTime(21, 30), 12))
        assertEquals(LocalTime(19, 30), twelveHour.withHour(LocalTime(21, 30), 7))
    }

    @Test
    fun switchingAmPmMovesTwelveHours() {
        assertEquals(LocalTime(21, 30), twelveHour.withPm(LocalTime(9, 30), pm = true))
        assertEquals(LocalTime(0, 5), twelveHour.withPm(LocalTime(12, 5), pm = false))
        assertEquals(LocalTime(9, 30), twelveHour.withPm(LocalTime(9, 30), pm = false))
    }

    @Test
    fun scrollingTheHourPastTwelveFlipsAmAndPm() {
        assertEquals(LocalTime(12, 30), twelveHour.afterHourScroll(LocalTime(11, 30), shownHour = 12, laps = 1))
        assertEquals(LocalTime(11, 15), twelveHour.afterHourScroll(LocalTime(12, 15), shownHour = 11, laps = -1))
        assertEquals(LocalTime(1, 0), twelveHour.afterHourScroll(LocalTime(22, 0), shownHour = 1, laps = 1))
        assertEquals(LocalTime(3, 0), twelveHour.afterHourScroll(LocalTime(10, 0), shownHour = 3, laps = 0))
        assertEquals(LocalTime(10, 0), twelveHour.afterHourScroll(LocalTime(10, 0), shownHour = 10, laps = 2))
    }

    @Test
    fun twentyFourHourWheelNeverFlips() {
        val clock = TwelveHourClock(is24Hour = true)
        assertEquals(LocalTime(1, 0), clock.afterHourScroll(LocalTime(23, 0), shownHour = 1, laps = 1))
    }

    @Test
    fun hourWheelWrapsBetweenElevenAndTwelve() {
        assertEquals(listOf(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), twelveHour.wheelHours)
        val rows = WheelRows(optionCount = 12, looping = true)
        val elevenRow = rows.firstRow(11)
        assertEquals(1, rows.lapsBetween(elevenRow, elevenRow + 1))
        assertEquals(-1, rows.lapsBetween(elevenRow + 1, elevenRow))
        assertEquals(0, rows.lapsBetween(elevenRow - 5, elevenRow))
        assertEquals(0, WheelRows(optionCount = 12, looping = false).lapsBetween(0, 11))
    }

    @Test
    fun twentyFourHourClockUsesTheHourAsIs() {
        val clock = TwelveHourClock(is24Hour = true)
        assertEquals(0, clock.shownHour(LocalTime(0, 0)))
        assertEquals(LocalTime(23, 10), clock.withHour(LocalTime(9, 10), 23))
    }

    @Test
    fun fieldsRefuseValuesPastTheRange() {
        assertTrue(fitsField("12", 1..12))
        assertTrue(fitsField("0", 1..12))
        assertTrue(fitsField("", 1..12))
        assertFalse(fitsField("13", 1..12))
        assertFalse(fitsField("123", 0..59))
        assertFalse(fitsField("6a", 0..59))
        assertTrue(fitsField("59", 0..59))
    }

    @Test
    fun loopingWheelGoesTheShortWayRound() {
        val rows = WheelRows(optionCount = 12, looping = true)
        val row = rows.firstRow(11)
        assertEquals(11, rows.optionAt(row))
        assertEquals(row + 1, rows.rowNear(0, row))
        assertEquals(row - 2, rows.rowNear(9, row))
    }

    @Test
    fun plainWheelRowsAreTheOptions() {
        val rows = WheelRows(optionCount = 5, looping = false)
        assertEquals(5, rows.count)
        assertEquals(3, rows.rowNear(3, 0))
        assertEquals(2, rows.firstRow(2))
    }

    @Test
    fun typingAnHourAndTappingPmGivesTheAfternoonTime() {
        var time by mutableStateOf(LocalTime(9, 30))
        compose.setContent {
            JengaTheme {
                JengaTimeInput(
                    time = time,
                    onTimeChange = { time = it },
                    hourLabel = "Hour",
                    minuteLabel = "Minute",
                    amLabel = "AM",
                    pmLabel = "PM",
                    style = JengaTimeInputStyle.Fields,
                )
            }
        }
        compose.onNodeWithContentDescription("Hour").performTextReplacement("13")
        compose.onNodeWithContentDescription("Hour").assert(hasText("09"))
        assertEquals(LocalTime(9, 30), time)
        compose.onNodeWithContentDescription("Hour").performTextReplacement("7")
        assertEquals(LocalTime(7, 30), time)
        compose.onNodeWithText("PM").performClick()
        assertEquals(LocalTime(19, 30), time)
    }
}
