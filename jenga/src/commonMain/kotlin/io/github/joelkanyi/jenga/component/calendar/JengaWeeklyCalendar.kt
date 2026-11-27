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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.Layout
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.divider.JengaDivider
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.math.roundToInt

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
}

/**
 * One week of days in a row: a title between previous-week and next-week buttons,
 * above seven selectable days, each a day name over its number in a circle. The
 * selected day is filled, and today has a ring.
 *
 * The calendar holds no state: the caller keeps [week] and [selectedDate], and
 * moves [week] by seven days in [onPreviousWeek] and [onNextWeek]. [week] can be
 * any date in the week to show (today, say): the calendar starts the row on the
 * [firstDayOfWeek] on or before it, so the caller never has to line it up. Every
 * visible string comes from the caller ([weekTitle], [dayOfWeekLabel] and the
 * descriptions), so it follows the app's locale.
 *
 * A day can show more than its number: [dayContent] draws your own content in each
 * day (a check on a paid day, a lock on a locked day), under the number or, with
 * [showDayNumber] off, in its place. [dayBackground] joins runs of days with a soft
 * band behind them, like a date range. The selected fill and the today ring are
 * drawn on top of both.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaWeeklyCalendarSample
 * @sample io.github.joelkanyi.jenga.samples.JengaWeeklyCalendarDayContentSample
 *
 * @param week any date in the week to show. The row starts on the [firstDayOfWeek]
 *   on or before it and shows seven days, so a Thursday shows the week around that
 *   Thursday. Adding or subtracting seven days moves to the next or previous week.
 * @param selectedDate the selected date, or null for none.
 * @param onSelectedDateChange called with a tapped date.
 * @param onPreviousWeek called when the previous-week button is tapped.
 * @param onNextWeek called when the next-week button is tapped.
 * @param weekTitle the title for a week, given its first and last day (e.g. "16 Nov – 22 Nov").
 * @param dayOfWeekLabel the label above a day (e.g. "Mon"); keep it short.
 * @param previousWeekContentDescription accessibility label for the previous-week button.
 * @param nextWeekContentDescription accessibility label for the next-week button.
 * @param modifier the [Modifier] for the calendar.
 * @param firstDayOfWeek the day each week starts on, and so the first day of the
 *   row; Monday by default. The calendar does not read the locale: to follow the
 *   locale's first day of the week (Sunday in the US, for example), pass it here.
 * @param dateRange the earliest and latest dates that can be picked; days outside it
 *   are disabled.
 * @param isSelectable whether a date can be picked.
 * @param today the date marked as today. The default is read once, when the calendar
 *   enters the composition; pass your own value to follow the clock, since a
 *   calendar can stay on screen past midnight.
 * @param dateContentDescription accessibility label for a day; the ISO date by
 *   default. Give the full date, and say so when it is today (e.g. "Tuesday 17
 *   November, today"). Screen readers add whether the day is selected. It is the
 *   only thing read for a day, so also say here what [dayContent] or
 *   [dayBackground] shows (e.g. "Sunday 22 November, locked").
 * @param previousWeekEnabled whether the previous-week button can be tapped. By
 *   default it is on when there is no [dateRange], or when [dateRange] starts before
 *   the first day shown.
 * @param nextWeekEnabled whether the next-week button can be tapped. By default it is
 *   on when there is no [dateRange], or when [dateRange] ends after the last day
 *   shown (six days after the first).
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
 *   height is not used. In the taller cell (a number with [dayContent] under it) its
 *   size is replaced by the theme's `bodySmall` size.
 * @param colors the color set; defaults to [JengaWeeklyCalendarDefaults.colors].
 * @param showDayNumber used only with [dayContent]. When true, the number stays and
 *   the content sits under it, in a taller rounded cell; the number is then drawn at
 *   the theme's `bodySmall` size, and every day keeps room for content, so the
 *   numbers line up. When false, the content takes the number's place in the circle,
 *   and a day whose content is empty still shows its number.
 * @param dayBackground the color of the band behind a day, or [Color.Unspecified]
 *   (or a fully transparent color) for none. Use a soft container color, and draw
 *   [dayContent] on it in the matching on-container color: `successContainer` with
 *   `onSuccessContainer`, `errorContainer` with `onErrorContainer`. Days next to
 *   each other that have a band are joined into one run, whatever their colors: the
 *   band is rounded only where a run starts and ends and at the two ends of the
 *   week, and two colors meet with no gap. A day on its own gets a band in the shape
 *   of its circle or cell. The lambda is not composable, so read theme colors
 *   outside it and capture them, as the sample does.
 * @param dayContent content drawn in each day's cell, or null for the number alone.
 *   Keep it small: about 20dp under the number, or inside the 34dp circle when
 *   [showDayNumber] is false. [LocalJengaContentColor] follows the day's state, so
 *   an untinted [JengaIcon] does too: the selected content color on the selected
 *   day, else the disabled content color on a disabled day, else the today color on
 *   today, else the content color. Content that sets its own color (a green check)
 *   keeps it on a disabled day and on today, and should drop it when its date is
 *   [selectedDate], or it will not show on the selected fill. The content should
 *   not be clickable and is not read by screen readers: the whole day is one tab,
 *   described by [dateContentDescription], and the content's semantics are cleared
 *   (a test tag in it is found only in the unmerged tree). With [showDayNumber]
 *   false, content wrapped in an animated visibility still takes up room while it
 *   leaves, so the number comes back only when the exit animation ends.
 */
@Composable
public fun JengaWeeklyCalendar(
    week: LocalDate,
    selectedDate: LocalDate?,
    onSelectedDateChange: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    weekTitle: (start: LocalDate, endInclusive: LocalDate) -> String,
    dayOfWeekLabel: (DayOfWeek) -> String,
    previousWeekContentDescription: String,
    nextWeekContentDescription: String,
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dateRange: ClosedRange<LocalDate>? = null,
    isSelectable: (LocalDate) -> Boolean = { true },
    today: LocalDate = remember { JengaCalendarDefaults.today() },
    dateContentDescription: (LocalDate) -> String = { it.toString() },
    previousWeekEnabled: Boolean = dateRange == null || firstDayOfWeekOn(week, firstDayOfWeek) > dateRange.start,
    nextWeekEnabled: Boolean = dateRange == null ||
        firstDayOfWeekOn(week, firstDayOfWeek).plus(DaysInWeek - 1, DateTimeUnit.DAY) < dateRange.endInclusive,
    showDivider: Boolean = true,
    isMarked: ((LocalDate) -> Boolean)? = null,
    contentPadding: PaddingValues = JengaWeeklyCalendarDefaults.ContentPadding,
    titleStyle: TextStyle = JengaWeeklyCalendarDefaults.titleStyle,
    dayOfWeekStyle: TextStyle = JengaWeeklyCalendarDefaults.dayOfWeekStyle,
    dayNumberStyle: TextStyle = JengaWeeklyCalendarDefaults.dayNumberStyle,
    colors: JengaWeeklyCalendarColors = JengaWeeklyCalendarDefaults.colors(),
    showDayNumber: Boolean = true,
    dayBackground: (LocalDate) -> Color = { Color.Unspecified },
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
) {
    val days = remember(week, firstDayOfWeek) {
        val first = firstDayOfWeekOn(week, firstDayOfWeek)
        List(DaysInWeek) { first.plus(it, DateTimeUnit.DAY) }
    }
    val bandColors = days.map(dayBackground)
    val bandEdges = weekBandEdges(bandColors)
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
                days.forEachIndexed { index, date ->
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
                            bandColor = bandColors[index],
                            bandEdges = bandEdges[index],
                            showDayNumber = showDayNumber,
                            dayContent = dayContent,
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

/**
 * One day column. [marked] is null when the calendar shows no markers, so no room is
 * kept for one. [bandEdges] is null when the day has no band.
 */
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
    bandColor: Color,
    bandEdges: WeekBandEdges?,
    showDayNumber: Boolean,
    dayContent: (@Composable (LocalDate) -> Unit)?,
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
            // The band is drawn on a wrapper as wide as the column, so the slices of
            // two neighbouring days touch; the face (circle or cell) sits on top of it.
            // By design it also covers the press indication over the face's height.
            val pill = dayContent != null && showDayNumber
            val faceShape = JengaTheme.shapes.pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .dayBand(bandColor, bandEdges, faceWidth = if (pill) DayPillWidth else DayCircleSize, faceShape),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    dayContent == null -> DayNumber(
                        date = date,
                        selected = selected,
                        isToday = isToday,
                        enabled = enabled,
                        style = dayNumberStyle,
                        colors = colors,
                        shape = faceShape,
                    )

                    showDayNumber -> DayNumberOverContent(
                        date = date,
                        selected = selected,
                        isToday = isToday,
                        enabled = enabled,
                        style = dayNumberStyle,
                        colors = colors,
                        shape = faceShape,
                        content = dayContent,
                    )

                    else -> DayContentOrNumber(
                        modifier = Modifier.dayCircle().dayState(selected, isToday, colors, faceShape),
                        dayContent = { DayContent(date, selected, isToday, enabled, colors, dayContent) },
                        dayNumber = { DayNumberText(date, selected, isToday, enabled, dayNumberStyle, colors) },
                    )
                }
            }
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
    shape: RoundedCornerShape,
) {
    Box(modifier = Modifier.dayCircle().dayState(selected, isToday, colors, shape), contentAlignment = Alignment.Center) {
        DayNumberText(date, selected, isToday, enabled, style, colors, Modifier.clearAndSetSemantics {})
    }
}

/** The taller cell of a day that shows its number and [content] under it. */
@Composable
private fun DayNumberOverContent(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    enabled: Boolean,
    style: TextStyle,
    colors: JengaWeeklyCalendarColors,
    shape: RoundedCornerShape,
    content: @Composable (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier.dayPill().dayState(selected, isToday, colors, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DayPillSpacing, Alignment.CenterVertically),
    ) {
        DayNumberText(
            date = date,
            selected = selected,
            isToday = isToday,
            enabled = enabled,
            style = style.copy(fontSize = JengaTheme.typography.bodySmall.fontSize),
            colors = colors,
            modifier = Modifier.clearAndSetSemantics {},
        )
        // The box is kept for a day with no content, so the numbers of the week line up.
        Box(modifier = Modifier.size(DayContentSize).clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
            DayContent(date, selected, isToday, enabled, colors, content)
        }
    }
}

/**
 * Shows [dayContent] centred, or [dayNumber] when [dayContent] emits nothing (or nothing
 * that takes up room). Which one is known only once [dayContent] is measured, so both
 * are composed and one is placed. Neither is read by screen readers.
 */
@Composable
internal fun DayContentOrNumber(
    dayContent: @Composable () -> Unit,
    dayNumber: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        contents = listOf(dayContent, dayNumber),
        modifier = modifier.clearAndSetSemantics {},
    ) { (contentMeasurables, numberMeasurables), constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val contentPlaceables = contentMeasurables.map { it.measure(loose) }
        val shown = if (contentPlaceables.any { it.width > 0 && it.height > 0 }) {
            contentPlaceables
        } else {
            numberMeasurables.map { it.measure(loose) }
        }
        val width = constraints.constrainWidth(shown.maxOfOrNull { it.width } ?: 0)
        val height = constraints.constrainHeight(shown.maxOfOrNull { it.height } ?: 0)
        layout(width, height) {
            shown.forEach { it.place((width - it.width) / 2, (height - it.height) / 2) }
        }
    }
}

/**
 * The caller's content for a day, given the content color of the day's state: the
 * same color as the day's number.
 */
@Composable
private fun DayContent(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    enabled: Boolean,
    colors: JengaWeeklyCalendarColors,
    content: @Composable (LocalDate) -> Unit,
) {
    val contentColor = when {
        selected -> colors.selectedContent
        !enabled -> colors.disabledContent
        isToday -> colors.todayContent
        else -> colors.content
    }
    CompositionLocalProvider(LocalJengaContentColor provides contentColor) { content(date) }
}

@Composable
private fun DayNumberText(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    enabled: Boolean,
    style: TextStyle,
    colors: JengaWeeklyCalendarColors,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier,
    )
}

/** The selected fill or the today ring of a day's circle or cell. */
private fun Modifier.dayState(
    selected: Boolean,
    isToday: Boolean,
    colors: JengaWeeklyCalendarColors,
    shape: RoundedCornerShape,
): Modifier = when {
    selected -> background(colors.selectedContainer, shape)
    isToday -> border(TodayBorderWidth, colors.todayBorder, shape)
    else -> this
}

/**
 * Sizes the day circle: [DayCircleSize], growing at half the rate of the font scale
 * (a larger number needs more room, but seven circles share the row), and never
 * wider than its column less the gap between columns.
 */
private fun Modifier.dayCircle(): Modifier = layout { measurable, constraints ->
    val side = dayFaceWidth(DayCircleSize, constraints.maxWidth)
    val placeable = measurable.measure(Constraints.fixed(side, side))
    layout(side, side) { placeable.place(0, 0) }
}

/**
 * Sizes the cell of a day that shows content under its number: [DayPillWidth] wide
 * and at least [DayPillMinHeight] tall, both growing like the day circle, and never
 * wider than its column less the gap between columns.
 */
private fun Modifier.dayPill(): Modifier = layout { measurable, constraints ->
    val width = dayFaceWidth(DayPillWidth, constraints.maxWidth)
    val minHeight = (DayPillMinHeight * fontGrowth()).roundToPx()
    val placeable = measurable.measure(Constraints(minWidth = width, maxWidth = width, minHeight = minHeight))
    layout(width, placeable.height) { placeable.place(0, 0) }
}

/** How much a day's circle or cell grows with the font scale: half as fast. */
private fun Density.fontGrowth(): Float = 1f + (fontScale - 1f).coerceAtLeast(0f) / 2f

/**
 * The width in pixels of a day's circle or cell of [base] width, in a column
 * [columnWidth] pixels wide ([Constraints.Infinity] when unbounded).
 */
private fun Density.dayFaceWidth(base: Dp, columnWidth: Int): Int {
    val wanted = (base * fontGrowth()).roundToPx()
    val available = if (columnWidth == Constraints.Infinity) wanted else columnWidth - ColumnGap.roundToPx()
    return minOf(wanted, available).coerceAtLeast(0)
}

/**
 * Where a day's slice of a band stops short of its column's edge, with rounded
 * corners: at the [start] of a run, at its [end], or both for a day on its own.
 */
internal data class WeekBandEdges(val start: Boolean, val end: Boolean)

/**
 * The band edges of each day of a week, from each day's band color in display
 * order: null for a day with no band ([Color.Unspecified] or fully transparent). A
 * run is a stretch of neighbouring days that have a band, whatever their colors; it
 * starts and ends where a neighbour has none, and at the first and last day of the
 * week.
 */
internal fun weekBandEdges(colors: List<Color>): List<WeekBandEdges?> {
    val banded = colors.map { it.isSpecified && it.alpha > 0f }
    return banded.mapIndexed { index, hasBand ->
        if (hasBand) {
            WeekBandEdges(
                start = index == 0 || !banded[index - 1],
                end = index == banded.lastIndex || !banded[index + 1],
            )
        } else {
            null
        }
    }
}

/**
 * Draws a day's slice of a band behind its face (the circle or cell, [faceWidth]
 * wide before font scaling and of [faceShape]), on a wrapper as wide as the day's
 * column and as tall as the face. The slice fills the column's width, so it meets
 * its neighbours' slices; at an edge of a run it stops at the face's edge and takes
 * the corners [faceShape] gives the face there.
 */
private fun Modifier.dayBand(
    color: Color,
    edges: WeekBandEdges?,
    faceWidth: Dp,
    faceShape: RoundedCornerShape,
): Modifier {
    if (edges == null) return this
    return drawBehind {
        val face = dayFaceWidth(faceWidth, size.width.roundToInt())
        // The same rounding as the centred face, so a rounded edge sits exactly on it.
        val faceLeft = ((size.width - face) / 2f).roundToInt().toFloat()
        val ltr = layoutDirection == LayoutDirection.Ltr
        val roundLeft = if (ltr) edges.start else edges.end
        val roundRight = if (ltr) edges.end else edges.start
        // The face's own corners, resolved for its size as its shape resolves them.
        val faceSize = Size(face.toFloat(), size.height)
        val largest = faceSize.minDimension / 2f
        fun corner(corner: CornerSize) = CornerRadius(corner.toPx(faceSize, this).coerceIn(0f, largest))
        val startCorners = corner(faceShape.topStart) to corner(faceShape.bottomStart)
        val endCorners = corner(faceShape.topEnd) to corner(faceShape.bottomEnd)
        val none = CornerRadius.Zero to CornerRadius.Zero
        val left = if (!roundLeft) {
            none
        } else if (ltr) {
            startCorners
        } else {
            endCorners
        }
        val right = if (!roundRight) {
            none
        } else if (ltr) {
            endCorners
        } else {
            startCorners
        }
        val slice = RoundRect(
            rect = Rect(
                left = if (roundLeft) faceLeft else 0f,
                top = 0f,
                right = if (roundRight) faceLeft + face else size.width,
                bottom = size.height,
            ),
            topLeft = left.first,
            topRight = right.first,
            bottomRight = right.second,
            bottomLeft = left.second,
        )
        drawPath(Path().apply { addRoundRect(slice) }, color)
    }
}

private const val DaysInWeek = 7

/** The [firstDayOfWeek] on or before [date]: the first day of the row that shows [date]. */
private fun firstDayOfWeekOn(date: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
    val daysIntoWeek = (date.dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber + DaysInWeek) % DaysInWeek
    return date.minus(daysIntoWeek, DateTimeUnit.DAY)
}

private val DayCircleSize: Dp = 34.dp

/** The cell of a day that shows content under its number. */
private val DayPillWidth: Dp = 36.dp

private val DayPillMinHeight: Dp = 54.dp

/** The gap between the number and the content in the cell. */
private val DayPillSpacing: Dp = 2.dp

/** The room kept for content under the number. */
private val DayContentSize: Dp = 20.dp

private val TodayBorderWidth: Dp = 1.5.dp

private val MarkerSize: Dp = 4.dp

/** The visible gap between two day columns (their pressed shapes and circles). */
private val ColumnGap: Dp = 4.dp
