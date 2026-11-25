# Change Log

## Unreleased

 * New: `JengaCheckbox` and `JengaCheckboxListItem` have overloads that take `state` (`ToggleableState.Off`, `On` or `Indeterminate`) and `onClick`, for a "Select all" box. The mixed state (`Indeterminate`) draws the checked box with a dash in `JengaCheckboxColors.checkmark`, and screen readers announce it as partially checked; the caller picks the next state in `onClick`.
 * New: `JengaAvatar` takes `colors` (`JengaAvatarColors`: `container` and `content`). `JengaAvatarDefaults.colors()` is the theme's avatar colors, else the brand tint as before; `JengaAvatarDefaults.neutralColors()` is a grey avatar; and `JengaAvatarStyle.colors` on `JengaComponentStyles.avatar` sets the colors of every avatar once. The content-slot overload also takes `colors`, defaulting to `JengaAvatarDefaults.contentSlotColors()`.
 * New: `JengaDatePickerDialog` takes `title`, an optional line above the month (e.g. "Payment date") in the same style and spacing as `JengaTimePickerDialog`'s title.
 * New: `JengaImageViewer` (both the single image and the pages overloads) takes `closeButtonPosition`: `JengaImageViewerCloseButtonPosition.Start` (close, title, actions; the default) or `End` (title, actions, close, with the title starting at the leading edge at the start padding of a top bar title).
 * New: `JengaSnackbar` takes `onDismiss` and `dismissContentDescription` and shows a close ✕ at the end, after the action, in `JengaSnackbarColors.content`, with a 48dp touch target.
 * New: `JengaSnackbarHostState.showSnackbar` takes `dismissContentDescription`; the snackbar then shows the close ✕, and tapping it returns `JengaSnackbarResult.Dismissed`.
 * New: `JengaSnackbar` takes `leadingContent`, one small item before the message (a 20dp icon or a counter badge): it replaces the tone dot, is centred on the message and takes `JengaSnackbarColors.content` as its content color.
 * New: `JengaSnackbarHostState.showSnackbar` takes `leadingContent`, shown before the message of that snackbar.
 * New: `JengaBanner` takes `onDismiss` and `dismissContentDescription` and shows a close ✕ at the end, after the action, in `JengaBannerColors.content`, with a 48dp touch target.
 * New: `JengaBanner` takes `actionPosition`: `JengaBannerActionPosition.End` (beside the text, the default) or `Below` (under the message, so the text keeps the width when the banner also has the close ✕).
 * New: `JengaBanner` takes `emphasis`: `JengaBannerEmphasis.Default` or `Prominent`, for the result of a check (credit approved, ID not matched): a 28dp result icon in `JengaBannerColors.content` centred on the text, and the message in `JengaBannerColors.prominentMessage` (the primary text color by default).
 * New: `JengaBanner` takes `icon`, which replaces the tone's icon in either emphasis. `JengaBannerDefaults.icon(tone, emphasis)` gives the default: for a prominent banner, `JengaIcons.BadgeCheck` (Success), `AlertCircle` (Warning), `Ban` (Error).
 * New: `JengaIcons.BadgeCheck`.
 * New: `JengaDialog` takes `icon`, a slot above the title, separated from it by the dialog's `spacing.md` gap (12dp by default), and `iconAlignment`: `JengaDialogIconAlignment.Start` (the default) or `Center`, which also centres the title and text.
 * New: `JengaDialog` takes `actionsLayout`: `JengaDialogActionsLayout.Row` (one row at the end edge, the default) or `Stacked` (full-width buttons one above the other, the confirm button first, `actionSpacing` apart).
 * New: `JengaEmptyState` and `JengaErrorState` take a second action, `secondaryActionLabel` and `onSecondaryAction`, shown under the first; `secondaryActionVariant` defaults to Ghost.
 * New: `JengaEmptyState` and `JengaErrorState` take `layout`. `JengaStateLayout.FillScreen` fills the given bounds, centers the icon, title and description in the space above the actions (scrolling when they do not fit) and pins the actions at the bottom as full-width buttons with 16dp at the sides. It does not pad for system bars: pass the scaffold's content padding through `modifier`, or add `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`. `JengaStateLayout.Inline`, the default, is the layout as before.
 * New: `JengaBottomBar` has an overload taking `primaryAction` and an optional `secondaryAction`: two actions sit side by side (secondary at the start, primary at the end, equal widths) when both labels fit on one line in their half of the bar, otherwise they stack full width with the primary on top, `spacing.md` (12dp) apart. The `RowScope` overload is unchanged.
 * New: `JengaNavigationBarItem` takes `badgeCount` and shows it in a `JengaBadge` at the top end of the icon: hidden at 0, "99+" above 99, `JengaBadgeTone.Error` unless `badgeTone` is set. `badgeContentDescription` (e.g. "3 new") is what a screen reader says after the item's name.
 * New: `JengaNavigationRailItem` takes the same `badgeCount`, `badgeContentDescription` and `badgeTone`; the badge follows the label.
 * New: `JengaTextWithLinks` shows a sentence with links inside it: pass the sentence and a `JengaInlineLink` (phrase and `onClick`) for each link. Links are in the link color, underlined, wrap with the text, and are announced as links by screen readers.
 * New: `appendJengaLink` adds a link inside `buildAnnotatedString`, styled with `JengaLinkDefaults.inlineStyles()`, for text shown with `JengaText`.
 * New: `JengaTooltip` takes `pointer` (`JengaTooltipPointer`): a small triangle on the `Top`, `Bottom`, `Start` or `End` edge of the bubble that points at what the tooltip explains. `offset` puts its centre a distance from the start of that edge (centred when left out), and the shadow follows the bubble and the pointer as one shape; on Android 9 (API 28) and older the bubble alone casts the shadow. No pointer by default.
 * New: `JengaTooltipBox` shows a `JengaTooltip` next to its host (the element that launches it, such as an info icon after a field label) in a popup, its pointer aimed at the host's centre. The tooltip goes under the host, or above it when there is no room under it in the window, and moves sideways to stay inside the window, but never so far that the pointer misses the host's centre; also in right-to-left layouts. Pass `expanded` and `onDismissRequest` to show it from your own state, or leave them out and a tap on the host shows it and the next tap hides it. While it is open the popup takes focus (an open keyboard closes); a tap outside (which only dismisses it), a tap on the tooltip, or Back dismisses it. It is exposed to screen readers as a pane titled with its text and as a polite live region.
 * New: `JengaStepIndicator` takes `variant`: `JengaStepIndicatorVariant.Labelled` (the default, as before) or `Numbered`, which shows the step number inside the ring of the current and pending steps. `JengaStepIndicatorColors` gains `currentNumber` and `pendingNumber`.
 * New: `JengaStepIndicatorVariant.Compact` draws numbered steps and lines with no labels and no space kept for them, for long step names or many steps; the current step gets a soft halo in `JengaStepIndicatorColors.currentHalo`. Show the current step's name once under the row yourself.
 * New: `JengaStepIndicator` takes `arrangement`: `JengaStepIndicatorArrangement.Centered` (equal columns, the default, as before) or `EdgeToEdge`, which puts the first step at the start edge and the last at the end edge with the lines filling the space between; the first label is start-aligned, the last end-aligned and the others centered.

## Version 0.8.6

_2026-11-10_

 * New: `JengaReactionBar` takes `selected` (`JengaReaction.Positive`, `Negative` or null) and shows the chosen button in `JengaReactionBarColors.selectedContainer` and `selectedContent`; screen readers announce it as selected.

## Version 0.8.5

_2026-11-03_

 * New: `JengaTextArea`, a multi-line box for comments and notes: starts `minLines` tall, grows to `maxLines` then scrolls, text starts at the top, optional `maxLength` with a counter under the box.
 * New: `JengaComponentStyles.wheelPicker` (`JengaWheelPickerStyle`) sets the colors of every wheel with default colors: `JengaWheelPicker`, `JengaTimeInput`, `JengaTimePickerDialog` and `JengaDateOfBirthPicker`.
 * New: `JengaThumbnailStrip` takes `layout`: `JengaThumbnailStripLayout.Row` (one scrolling row, the default) or `Wrap` (fills the width, then continues on the next line).
 * New: `JengaChip` and `JengaChipStyle` take `iconSpacing`, the gap between the leading icon, the label and the trailing content.
 * New: `JengaImageShelf` takes `subtitle`, a line under the title.
 * New: `JengaSegmentedControlColors.selectedBorder` draws an outline around the selected segment; transparent by default.
 * New: `JengaIcons.CameraOff` and `JengaIcons.FlashAuto`.
 * Change: in 12-hour mode, scrolling the hour wheel of `JengaTimeInput` past 12 switches AM and PM, so 11 AM scrolled forward to 12 becomes 12 PM.
 * Fix: the header action of `JengaImageShelf` and the action of `JengaVerdictBar` no longer show a grey box when pressed or hovered.
 * Change: a multi-line `JengaTextField` (`singleLine = false`) now starts its text and placeholder at the top of the box instead of centring them.

## Version 0.8.4

_2026-10-28_

 * New: `rememberJengaSheetState` takes `confirmDismiss`, called before the sheet closes from a drag, a scrim tap, Back or `hide()`; returning false keeps it open. With it set, Back skips the system shrink animation so a refused close leaves the sheet in place.
 * New: `JengaBottomSheet` (state overload) takes `gesturesEnabled` to turn off dragging the sheet.
 * New: `JengaDatePicker` and `JengaDatePickerDialog` take `dateRange`; days outside it are disabled and the month buttons stop at its ends.
 * New: `JengaDatePicker` and `JengaDatePickerDialog` take `yearPickerLabel`; when set, tapping the month title opens a grid of years.
 * New: `JengaStepIndicator`, a horizontal row of steps (done, current, pending, locked) joined by lines, with `JengaStep`, `JengaStepState` and `JengaStepIndicatorColors`.
 * New: `JengaTimeInput` and `JengaTimePickerDialog` for entering a time with scroll wheels (hour and minute loop) or typed fields, in 12- or 24-hour mode.
 * New: `JengaCaptureFrame`, a capture guide over a camera preview (oval or rectangle at an aspect ratio) with a ready color and hold progress, and `JengaShutterButton`.
 * New: `JengaImageViewer` overload with pages: swipe between images, each zoomed on its own, with a bottom bar that can move to any page.
 * New: `JengaDropdownMenu` draws a 1dp outline in dark themes by default (`JengaDropdownMenuDefaults.border`), so the menu's edge shows on dark screens. Pass `border = null` for none.
 * New: `JengaDialog` (and the date and time picker dialogs) draws the same dark-theme outline (`JengaDialogDefaults.border`); `JengaDialog` takes `border` to change or remove it.
 * New: in dark themes, `JengaSideSheet` and the elevated `JengaCard` draw the same 1dp outline, since shadows and surface edges don't show on dark backgrounds.
 * Fix: a ring `JengaTimelineMarker` now gives its content the ring color by default, so a number inside it reads on light backgrounds.
 * Fix: `JengaBottomSheet` no longer calls `onDismissRequest` on Back when the sheet did not close.
 * Change: `JengaImageViewer` only takes one-finger drags while zoomed in, so a pager or list around it can scroll; pinch and double tap are unchanged.

## Version 0.8.3

_2026-10-26_

 * New: `JengaSearchField` takes `keyboardOptions` and `keyboardActions`.
 * New: `JengaTimelineItem` overload with `marker` and `content` slots and a per-item `lineColor`; `JengaTimelineMarker` (filled or ring, optional content); `JengaTimelinePosition.of(index, count)`.

## Version 0.8.2

_2026-10-15_

 * New: `JengaButton` (text overload) takes `contentPadding` and `textStyle`.
 * New: `JengaProgressTone.Error`, and `error` colors in `JengaProgressStyle`. A `when` over `JengaProgressTone` without an `else` branch needs the new case.
 * New: `JengaCard` takes `selected`, drawing a brand border and reporting the card as selected to accessibility services.
 * New: `JengaButtonVariant.BrandOutline`, a brand border and label on a surface fill, and `brandOutline` colors in `JengaButtonStyle`. A `when` over `JengaButtonVariant` without an `else` branch needs the new case.

## Version 0.8.1

_2026-10-14_

 * New: `JengaStatusPill` takes `leadingIcon`, shown in place of the dot.
 * New: `JengaButton` (text overload) takes `loadingText`, shown beside the spinner while loading.

## Version 0.8.0

_2026-10-13_

 * New: Theme-level component styles. `JengaTheme(components = JengaComponentStyles(...))` sets the look of 31 components for everything below it. A value passed to a call still wins, and an unset value keeps Jenga's default.
 * New: `JengaOtpField`, `JengaOtpResend`, `JengaOtpVerification` and `rememberJengaCountdown` for one-time-code entry.
 * New: `JengaDialog` takes `colors`.
 * New: `JengaButton` (text overload) takes `colors`.
 * New: `JengaRadioListItem` and `JengaCheckboxListItem` take `headlineMaxLines` and `supportingMaxLines`.
 * New: `JengaListSheet` takes `subtitleColor`, `JengaTopAppBar` takes `titleStyle`, `JengaKeyValueRow` takes `labelStyle`, `JengaDropdownMenu` takes `width` and `JengaTabs` takes `selectedTextStyle`.
 * Changed: `JengaTheme`, `JengaDialog`, `JengaButton`, `JengaListSheet`, `JengaTopAppBar`, `JengaKeyValueRow`, `JengaDropdownMenu`, `JengaTabs` and the selection list items gained parameters. Source compatible, but code compiled against 0.7.0 must be recompiled.

## Version 0.7.0

_2026-10-08_

 * New: `JengaBarChart`, a bar chart with fill or scroll layout, selection, value labels and per-call metrics, shapes, styles and colors.
 * New: `JengaListItem` takes `headlineStyle`, `supportingStyle`, `contentSpacing` and `verticalAlignment`, plus an `AnnotatedString` headline overload.
 * New: `JengaListItemColors` has disabled headline, supporting and leading/trailing colors.
 * New: `JengaRadioListItem` and `JengaCheckboxListItem` take `contentSpacing`, `trailingContent`, `headlineStyle`, `supportingStyle` and `colors`. The radio item takes `radioSize`; the checkbox item takes `checkboxColors` and `loading`.
 * New: `JengaEmptyState` and `JengaErrorState` take `titleStyle`, `descriptionStyle`, `contentPadding`, `spacing`, `actionSpacing` and `actionSize`.
 * New: `JengaCard` takes `borderWidth`, `enabled` and `disabledAlpha`.
 * New: `JengaRadioButton` takes `size`.
 * New: `JengaLink` takes `leadingIcon` and `contentPadding`.
 * New: `JengaExpandableRow` takes `shape`, `colors`, `headerPadding` and `contentPadding`.
 * New: `JengaSegmentedControl` takes `selectedTextStyle`.
 * New: `JengaListSheet` and `JengaSideSheet` have `AnnotatedString` subtitle overloads.
 * New: `JengaAvatar` takes `diameter` and `textStyle`, defaulting to the `size` preset.
 * Changed: `JengaRadioButton` and `JengaCheckbox` only reserve the 48dp touch area when they have their own `onClick`. Selection list rows are 56dp tall by default instead of about 72dp.


## Version 0.6.0

_2026-10-07_

 * New: `JengaCalendar`, `JengaDatePicker`, `JengaDatePickerDialog`, `JengaWheelPicker` and `JengaDateOfBirthPicker`. Dates use kotlinx-datetime `LocalDate`, now an API dependency.
 * New: `JengaSelectField` and `JengaSearchTrigger`.
 * New: `JengaInfoBar`, `JengaCardFooter`, `JengaNavigationRailItem`, `JengaThumbnailStrip`, `JengaTimelineItem`, `JengaFormLayout`, `JengaSideSheet` and `JengaImageViewer`.
 * New: `JengaLabelledProgress`.
 * New: Size, padding, style and color options on list rows, the top bar, section header, tabs, segmented control, chip, icon button, icon tile, menu, dialog, status pill, list sheet, FAB, verdict bar, stepper, snackbar and progress indicators.
 * Changed: `JengaBadge` shows its text as written instead of uppercase.
 * Fix: Start-aligned tab indicator position.
 * Fix: `JengaKeyValueRow` inside intrinsic-size layouts.
 * Fix: `JengaSelectField` chevron in right-to-left layouts.


## Version 0.5.0

_2026-10-07_

 * New: `JengaIconTile` and `JengaKeyValueRow`, with colors.
 * New: `JengaRadioListItem` and `JengaCheckboxListItem`.
 * New: `JengaListSheet`, a sheet with a title, subtitle, lazy body and pinned footer. `JengaBottomSheet` stays free-form.
 * New: `JengaListItem` takes `supportingContent`, `enabled` and max lines.
 * New: A mono typography role and `TextStyle.withTabularFigures`.
 * New: 25 line icons in `JengaIcons`.
 * Changed: `JengaSearchField` requires a placeholder and a clear-button label.
 * Fix: The send icon mirrors in right-to-left layouts.
 * Fix: `withTabularFigures` replaces an existing `tnum` setting.


## Version 0.4.1

_2026-10-07_

 * Changed: Android minSdk lowered to 23.
 * Changed: Coil pinned to 3.4.0.
 * Changed: Overlay surfaces use the colors from 0.3.0 again.


## Version 0.4.0

_2026-08-25_

 * New: Web (wasmJs) target.
 * New: `JengaLink`, a tappable text link.
 * Changed: Overlays have more contrast against the page background.


## Version 0.3.0

_2026-07-22_

 * New: `JengaBrand` derives a whole light and dark theme from one seed color.
 * New: A display and body typeface pair in the typography.
 * New: The component icon set is themeable.
 * New: Verdict, stat, stepper, media, shelf and other content blocks.
 * Changed: The dropdown menu container uses the surface color.
 * Fix: Expandable row content padding and the dark focus ring.


## Version 0.2.1

_2026-07-11_

Initial public release: the Jenga Compose Multiplatform design system for Android, iOS and Desktop.
