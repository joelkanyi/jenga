package io.github.joelkanyi.jenga.component.calendar

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.divider.JengaDivider
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** Resolved colors for a [JengaWeeklyCalendar]. Override via [JengaWeeklyCalendarDefaults.colors]. */
@Poko
@Immutable
public class JengaWeeklyCalendarColors(
    public val selectedContainer: Color,
    public val selectedContent: Color,
    public val todayBorder: Color,
    public val todayContent: Color,
    public val content: Color,
    public val disabledContent: Color,
    public val weekdayContent: Color,
    public val titleContent: Color,
    public val marker: Color,
) {
    public fun copy(
        selectedContainer: Color = this.selectedContainer,
        selectedContent: Color = this.selectedContent,
        todayBorder: Color = this.todayBorder,
        todayContent: Color = this.todayContent,
        content: Color = this.content,
        disabledContent: Color = this.disabledContent,
        weekdayContent: Color = this.weekdayContent,
        titleContent: Color = this.titleContent,
        marker: Color = this.marker,
    ): JengaWeeklyCalendarColors = JengaWeeklyCalendarColors(
        selectedContainer,
        selectedContent,
        todayBorder,
        todayContent,
        content,
        disabledContent,
        weekdayContent,
        titleContent,
        marker,
    )
}

/** Defaults and token mappings for [JengaWeeklyCalendar]. */
public object JengaWeeklyCalendarDefaults {
    /** Default padding around the week title and the days: none. */
    public val ContentPadding: PaddingValues = PaddingValues(0.dp)

    /** Default text style of the week title. */
    public val titleStyle: TextStyle
        @Composable get() = JengaTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

    /** Default text style of the day name above each day. */
    public val dayOfWeekStyle: TextStyle
        @Composable get() = JengaTheme.typography.caption.copy(fontWeight = FontWeight.Medium)

    /** Default text style of the day number. */
    public val dayNumberStyle: TextStyle
        @Composable get() = JengaTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

    /** Themed colors. */
    @Composable
    public fun colors(): JengaWeeklyCalendarColors {
        val c = JengaTheme.colors
        return JengaWeeklyCalendarColors(
            selectedContainer = c.brand,
            selectedContent = c.onBrand,
            todayBorder = c.brand,
            todayContent = c.brand,
            content = c.textPrimary,
            disabledContent = c.contentDisabled,
            weekdayContent = c.textSecondary,
            titleContent = c.textPrimary,
            marker = c.brand,
        )
    }

    /**
     * The first day of the week that contains [date], for a week starting on
     * [firstDayOfWeek]: the `weekStart` to give [JengaWeeklyCalendar]. A date that
     * falls on [firstDayOfWeek] is its own week start.
     */
    public fun weekStartOf(date: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
        val daysIntoWeek = (date.dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber + DaysInWeek) % DaysInWeek
        return date.minus(daysIntoWeek, DateTimeUnit.DAY)
    }
}

/**
 * One week of days in a row: a title between previous-week and next-week buttons,
 * above seven selectable days, each a day name over its number in a circle. The
 * selected day is filled, and today has a ring.
 *
 * The calendar holds no state: the caller keeps [weekStart] and [selectedDate],
 * and moves [weekStart] by seven days in [onPreviousWeek] and [onNextWeek]. Every
 * visible string comes from the caller ([weekTitle], [dayOfWeekLabel] and the
 * descriptions), so it follows the app's locale.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaWeeklyCalendarSample
 *
 * @param weekStart the first day shown; the calendar shows it and the six days after
 *   it. Use [JengaWeeklyCalendarDefaults.weekStartOf] to get it from any date.
 * @param selectedDate the selected date, or null for none.
 * @param onSelectedDateChange called with a tapped date.
 * @param onPreviousWeek called when the previous-week button is tapped.
 * @param onNextWeek called when the next-week button is tapped.
 * @param weekTitle the title for a week, given its first and last day (e.g. "16 Nov – 22 Nov").
 * @param dayOfWeekLabel the label above a day (e.g. "Mon"); keep it short.
 * @param previousWeekContentDescription accessibility label for the previous-week button.
 * @param nextWeekContentDescription accessibility label for the next-week button.
 * @param modifier the [Modifier] for the calendar.
 * @param dateRange the earliest and latest dates that can be picked; days outside it
 *   are disabled.
 * @param isSelectable whether a date can be picked.
 * @param today the date marked as today. The default is read once, when the calendar
 *   enters the composition; pass your own value to follow the clock, since a
 *   calendar can stay on screen past midnight.
 * @param dateContentDescription accessibility label for a day; the ISO date by
 *   default. Give the full date, and say so when it is today (e.g. "Tuesday 17
 *   November, today"). Screen readers add whether the day is selected.
 * @param previousWeekEnabled whether the previous-week button can be tapped. By
 *   default it is on when there is no [dateRange], or when [dateRange] starts before
 *   [weekStart].
 * @param nextWeekEnabled whether the next-week button can be tapped. By default it is
 *   on when there is no [dateRange], or when [dateRange] ends after the last day
 *   shown (six days after [weekStart]).
 * @param showDivider whether a divider is drawn directly under the days, across the
 *   full width (outside [contentPadding]); add bottom [contentPadding] for more
 *   space above it.
 * @param isMarked when set, a small dot is shown under each day it returns true for
 *   (e.g. a day that has visits), and every day keeps room for one. The dot is visual
 *   only, so say what it means in [dateContentDescription].
 * @param contentPadding padding around the title and the days.
 * @param titleStyle the text style of the week title. Like the two styles below, its
 *   size follows the theme's type tokens (here `bodyMedium`); pass a style where a
 *   design fixes the size.
 * @param dayOfWeekStyle the text style of the day name above each day; the theme's
 *   `caption` by default.
 * @param dayNumberStyle the text style of the day number; the theme's `bodyMedium` by
 *   default, so a theme whose `bodyMedium` is 14sp gives a 14sp number. Its line
 *   height is not used.
 * @param colors the color set; defaults to [JengaWeeklyCalendarDefaults.colors].
 */
@Composable
public fun JengaWeeklyCalendar(
    weekStart: LocalDate,
    selectedDate: LocalDate?,
    onSelectedDateChange: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    weekTitle: (start: LocalDate, endInclusive: LocalDate) -> String,
    dayOfWeekLabel: (DayOfWeek) -> String,
    previousWeekContentDescription: String,
    nextWeekContentDescription: String,
    modifier: Modifier = Modifier,
    dateRange: ClosedRange<LocalDate>? = null,
    isSelectable: (LocalDate) -> Boolean = { true },
    today: LocalDate = remember { JengaCalendarDefaults.today() },
    dateContentDescription: (LocalDate) -> String = { it.toString() },
    previousWeekEnabled: Boolean = dateRange == null || weekStart > dateRange.start,
    nextWeekEnabled: Boolean = dateRange == null || weekStart.plus(6, DateTimeUnit.DAY) < dateRange.endInclusive,
    showDivider: Boolean = true,
    isMarked: ((LocalDate) -> Boolean)? = null,
    contentPadding: PaddingValues = JengaWeeklyCalendarDefaults.ContentPadding,
    titleStyle: TextStyle = JengaWeeklyCalendarDefaults.titleStyle,
    dayOfWeekStyle: TextStyle = JengaWeeklyCalendarDefaults.dayOfWeekStyle,
    dayNumberStyle: TextStyle = JengaWeeklyCalendarDefaults.dayNumberStyle,
    colors: JengaWeeklyCalendarColors = JengaWeeklyCalendarDefaults.colors(),
) {
    val days = remember(weekStart) { List(DaysInWeek) { weekStart.plus(it, DateTimeUnit.DAY) } }
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
        ) {
            WeekHeader(
                title = weekTitle(days.first(), days.last()),
                titleStyle = titleStyle,
                titleColor = colors.titleContent,
                previousWeekEnabled = previousWeekEnabled,
                nextWeekEnabled = nextWeekEnabled,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                previousWeekContentDescription = previousWeekContentDescription,
                nextWeekContentDescription = nextWeekContentDescription,
            )
            // No spacing between the columns: each day keeps the gap inside its own
            // touch target, as an inset on either side of its pressed shape.
            Row(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                days.forEach { date ->
                    // Keyed by date, so a pressed or focused highlight does not move to
                    // the same column of another week.
                    key(date) {
                        WeekDay(
                            date = date,
                            label = dayOfWeekLabel(date.dayOfWeek),
                            contentDescription = dateContentDescription(date),
                            selected = date == selectedDate,
                            isToday = date == today,
                            enabled = (dateRange == null || date in dateRange) && isSelectable(date),
                            marked = isMarked?.invoke(date),
                            onClick = { onSelectedDateChange(date) },
                            dayOfWeekStyle = dayOfWeekStyle,
                            dayNumberStyle = dayNumberStyle,
                            colors = colors,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
        if (showDivider) JengaDivider()
    }
}

@Composable
private fun WeekHeader(
    title: String,
    titleStyle: TextStyle,
    titleColor: Color,
    previousWeekEnabled: Boolean,
    nextWeekEnabled: Boolean,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    previousWeekContentDescription: String,
    nextWeekContentDescription: String,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        JengaIconButton(onClick = onPreviousWeek, enabled = previousWeekEnabled) {
            JengaIcon(
                JengaTheme.icons.chevron,
                contentDescription = previousWeekContentDescription,
                modifier = Modifier.rotate(180f),
            )
        }
        JengaText(
            text = title,
            style = titleStyle,
            color = titleColor,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.weight(1f),
        )
        JengaIconButton(onClick = onNextWeek, enabled = nextWeekEnabled) {
            JengaIcon(JengaTheme.icons.chevron, contentDescription = nextWeekContentDescription)
        }
    }
}

/** One day column. [marked] is null when the calendar shows no markers, so no room is kept for one. */
@Composable
private fun WeekDay(
    date: LocalDate,
    label: String,
    contentDescription: String,
    selected: Boolean,
    isToday: Boolean,
    enabled: Boolean,
    marked: Boolean?,
    onClick: () -> Unit,
    dayOfWeekStyle: TextStyle,
    dayNumberStyle: TextStyle,
    colors: JengaWeeklyCalendarColors,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = ColumnGap / 2)
                .clip(JengaTheme.shapes.md)
                .indication(interactionSource, LocalIndication.current),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = JengaTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.xs),
        ) {
            JengaText(
                text = label,
                style = dayOfWeekStyle,
                color = if (enabled) colors.weekdayContent else colors.disabledContent,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = ColumnGap / 2).clearAndSetSemantics {},
            )
            DayNumber(
                date = date,
                selected = selected,
                isToday = isToday,
                enabled = enabled,
                style = dayNumberStyle,
                colors = colors,
            )
            if (marked != null) {
                Box(
                    modifier = Modifier
                        .size(MarkerSize)
                        .background(if (marked) colors.marker else Color.Transparent, JengaTheme.shapes.pill),
                )
            }
        }
    }
}

@Composable
private fun DayNumber(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    enabled: Boolean,
    style: TextStyle,
    colors: JengaWeeklyCalendarColors,
) {
    val shape = JengaTheme.shapes.pill
    val state = when {
        selected -> Modifier.background(colors.selectedContainer, shape)
        isToday -> Modifier.border(TodayBorderWidth, colors.todayBorder, shape)
        else -> Modifier
    }
    Box(modifier = Modifier.dayCircle().then(state), contentAlignment = Alignment.Center) {
        JengaText(
            text = date.day.toString(),
            style = style.copy(lineHeight = TextUnit.Unspecified),
            color = when {
                selected -> colors.selectedContent
                !enabled -> colors.disabledContent
                isToday -> colors.todayContent
                else -> colors.content
            },
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * Sizes the day circle: [DayCircleSize], growing at half the rate of the font scale
 * (a larger number needs more room, but seven circles share the row), and never
 * wider than its column less the gap between columns.
 */
private fun Modifier.dayCircle(): Modifier = layout { measurable, constraints ->
    val growth = 1f + (fontScale - 1f).coerceAtLeast(0f) / 2f
    val wanted = (DayCircleSize * growth).roundToPx()
    val available = if (constraints.hasBoundedWidth) constraints.maxWidth - ColumnGap.roundToPx() else wanted
    val side = minOf(wanted, available).coerceAtLeast(0)
    val placeable = measurable.measure(Constraints.fixed(side, side))
    layout(side, side) { placeable.place(0, 0) }
}

private const val DaysInWeek = 7

private val DayCircleSize: Dp = 34.dp

private val TodayBorderWidth: Dp = 1.5.dp

private val MarkerSize: Dp = 4.dp

/** The visible gap between two day columns (their pressed shapes and circles). */
private val ColumnGap: Dp = 4.dp
