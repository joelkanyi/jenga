package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.action.JengaAction
import io.github.joelkanyi.jenga.component.avatar.JengaAvatar
import io.github.joelkanyi.jenga.component.avatar.JengaAvatarSize
import io.github.joelkanyi.jenga.component.badge.JengaBadge
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.banner.JengaBanner
import io.github.joelkanyi.jenga.component.banner.JengaBannerTone
import io.github.joelkanyi.jenga.component.banner.JengaInfoBar
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonSize
import io.github.joelkanyi.jenga.component.button.JengaButtonSizeStyle
import io.github.joelkanyi.jenga.component.button.JengaButtonStyle
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.button.JengaIconButtonVariant
import io.github.joelkanyi.jenga.component.calendar.JengaCalendar
import io.github.joelkanyi.jenga.component.calendar.JengaCalendarDefaults
import io.github.joelkanyi.jenga.component.calendar.JengaDatePicker
import io.github.joelkanyi.jenga.component.calendar.JengaDatePickerDialog
import io.github.joelkanyi.jenga.component.calendar.rememberJengaCalendarState
import io.github.joelkanyi.jenga.component.camera.JengaCaptureFrame
import io.github.joelkanyi.jenga.component.camera.JengaCaptureShape
import io.github.joelkanyi.jenga.component.camera.JengaShutterButton
import io.github.joelkanyi.jenga.component.card.JengaCard
import io.github.joelkanyi.jenga.component.card.JengaCardFooter
import io.github.joelkanyi.jenga.component.card.JengaCardVariant
import io.github.joelkanyi.jenga.component.chart.JengaBarChart
import io.github.joelkanyi.jenga.component.chart.JengaBarChartEntry
import io.github.joelkanyi.jenga.component.chip.JengaChip
import io.github.joelkanyi.jenga.component.divider.JengaDivider
import io.github.joelkanyi.jenga.component.expandable.JengaExpandableRow
import io.github.joelkanyi.jenga.component.fab.JengaExtendedFab
import io.github.joelkanyi.jenga.component.fab.JengaFab
import io.github.joelkanyi.jenga.component.feedback.JengaBottomSheet
import io.github.joelkanyi.jenga.component.feedback.JengaDialog
import io.github.joelkanyi.jenga.component.feedback.JengaListSheet
import io.github.joelkanyi.jenga.component.feedback.JengaSideSheet
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbar
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbarHost
import io.github.joelkanyi.jenga.component.feedback.JengaSnackbarTone
import io.github.joelkanyi.jenga.component.feedback.rememberJengaSheetState
import io.github.joelkanyi.jenga.component.feedback.rememberJengaSnackbarHostState
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIconTile
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.image.JengaImage
import io.github.joelkanyi.jenga.component.layout.JengaBox
import io.github.joelkanyi.jenga.component.layout.JengaFormLayout
import io.github.joelkanyi.jenga.component.layout.JengaGrid
import io.github.joelkanyi.jenga.component.layout.JengaInline
import io.github.joelkanyi.jenga.component.layout.JengaSection
import io.github.joelkanyi.jenga.component.layout.JengaSpacer
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.layout.JengaWrap
import io.github.joelkanyi.jenga.component.link.JengaLink
import io.github.joelkanyi.jenga.component.list.JengaCheckboxListItem
import io.github.joelkanyi.jenga.component.list.JengaKeyValueEmphasis
import io.github.joelkanyi.jenga.component.list.JengaKeyValueRow
import io.github.joelkanyi.jenga.component.list.JengaListItem
import io.github.joelkanyi.jenga.component.list.JengaRadioListItem
import io.github.joelkanyi.jenga.component.media.JengaImageViewer
import io.github.joelkanyi.jenga.component.media.JengaMediaHero
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenu
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenuDivider
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenuItem
import io.github.joelkanyi.jenga.component.menu.JengaDropdownMenuItemTone
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBar
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationBarItem
import io.github.joelkanyi.jenga.component.navigation.JengaNavigationRailItem
import io.github.joelkanyi.jenga.component.otp.JengaOtpCellStyle
import io.github.joelkanyi.jenga.component.otp.JengaOtpField
import io.github.joelkanyi.jenga.component.otp.JengaOtpResend
import io.github.joelkanyi.jenga.component.otp.JengaOtpResendDefaults
import io.github.joelkanyi.jenga.component.otp.JengaOtpVerification
import io.github.joelkanyi.jenga.component.otp.rememberJengaCountdown
import io.github.joelkanyi.jenga.component.picker.JengaDateOfBirthPicker
import io.github.joelkanyi.jenga.component.picker.JengaTimeInput
import io.github.joelkanyi.jenga.component.picker.JengaTimeInputStyle
import io.github.joelkanyi.jenga.component.picker.JengaTimePickerDialog
import io.github.joelkanyi.jenga.component.picker.JengaWheelPicker
import io.github.joelkanyi.jenga.component.progress.JengaCircularProgress
import io.github.joelkanyi.jenga.component.progress.JengaCircularProgressIndeterminate
import io.github.joelkanyi.jenga.component.progress.JengaDotStrip
import io.github.joelkanyi.jenga.component.progress.JengaLabelledProgress
import io.github.joelkanyi.jenga.component.progress.JengaLinearProgress
import io.github.joelkanyi.jenga.component.progress.JengaLinearProgressIndeterminate
import io.github.joelkanyi.jenga.component.progress.JengaProgressTone
import io.github.joelkanyi.jenga.component.progress.jengaShimmer
import io.github.joelkanyi.jenga.component.reaction.JengaReactionBar
import io.github.joelkanyi.jenga.component.refresh.JengaPullToRefresh
import io.github.joelkanyi.jenga.component.scaffold.JengaBottomBar
import io.github.joelkanyi.jenga.component.scaffold.JengaScaffold
import io.github.joelkanyi.jenga.component.scaffold.JengaTopAppBar
import io.github.joelkanyi.jenga.component.scanner.JengaScanFeedback
import io.github.joelkanyi.jenga.component.scanner.JengaScannerStatus
import io.github.joelkanyi.jenga.component.scanner.JengaScannerViewfinder
import io.github.joelkanyi.jenga.component.search.JengaSearchField
import io.github.joelkanyi.jenga.component.search.JengaSearchTrigger
import io.github.joelkanyi.jenga.component.selection.JengaCheckbox
import io.github.joelkanyi.jenga.component.selection.JengaRadioButton
import io.github.joelkanyi.jenga.component.selection.JengaToggle
import io.github.joelkanyi.jenga.component.shelf.JengaImageShelf
import io.github.joelkanyi.jenga.component.shelf.JengaShelfCard
import io.github.joelkanyi.jenga.component.shelf.JengaThumbnailStrip
import io.github.joelkanyi.jenga.component.slider.JengaSlider
import io.github.joelkanyi.jenga.component.stat.JengaStatTile
import io.github.joelkanyi.jenga.component.stat.JengaStatTone
import io.github.joelkanyi.jenga.component.state.JengaEmptyState
import io.github.joelkanyi.jenga.component.state.JengaErrorState
import io.github.joelkanyi.jenga.component.status.JengaStatusPill
import io.github.joelkanyi.jenga.component.stepindicator.JengaStep
import io.github.joelkanyi.jenga.component.stepindicator.JengaStepIndicator
import io.github.joelkanyi.jenga.component.stepindicator.JengaStepState
import io.github.joelkanyi.jenga.component.stepper.JengaStepper
import io.github.joelkanyi.jenga.component.swipe.JengaSwipeToDismiss
import io.github.joelkanyi.jenga.component.tabs.JengaSegmentedControl
import io.github.joelkanyi.jenga.component.tabs.JengaTabs
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.component.textfield.JengaSelectField
import io.github.joelkanyi.jenga.component.textfield.JengaTextArea
import io.github.joelkanyi.jenga.component.textfield.JengaTextField
import io.github.joelkanyi.jenga.component.textfield.JengaTextFieldStatus
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineItem
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineMarker
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineMarkerStyle
import io.github.joelkanyi.jenga.component.timeline.JengaTimelinePosition
import io.github.joelkanyi.jenga.component.timeline.JengaTimelineState
import io.github.joelkanyi.jenga.component.tooltip.JengaTooltip
import io.github.joelkanyi.jenga.component.verdict.JengaVerdictBar
import io.github.joelkanyi.jenga.component.verdict.JengaVerdictSublines
import io.github.joelkanyi.jenga.component.verdict.JengaVerdictTone
import io.github.joelkanyi.jenga.pattern.JengaSectionHeader
import io.github.joelkanyi.jenga.pattern.JengaStatCard
import io.github.joelkanyi.jenga.pattern.JengaTicketRow
import io.github.joelkanyi.jenga.theme.JengaComponentStyles
import io.github.joelkanyi.jenga.theme.JengaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Every catalog entry, in display order. Each pairs a live demo with the exact
 * call that produces it, and carries a group and keywords for the search index.
 * A plain list, not a composable: the demos are only invoked when rendered.
 */
fun componentEntries(): List<CatalogEntry> = listOf(
    CatalogEntry(
        name = "Button",
        group = "Actions",
        description = "Triggers an action or event, in seven variants, with a loading state.",
        keywords = "primary ink ghost outline brand outline danger loading",
        code = """
            JengaButton(
                text = "Primary",
                onClick = {},
            )
            JengaButton(
                text = "Outline",
                onClick = {},
                variant = JengaButtonVariant.Outline,
            )
            JengaButton(
                text = "Brand outline",
                onClick = {},
                variant = JengaButtonVariant.BrandOutline,
            )
            JengaButton(
                text = "Danger",
                onClick = {},
                variant = JengaButtonVariant.Danger,
            )
            JengaButton(
                text = "Save",
                onClick = {},
                loading = true,
                loadingText = "Saving",
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaButton(
                                text = "Primary",
                                onClick = {},
                                variant = JengaButtonVariant.Primary,
                            )
                            JengaButton(
                                text = "Ink",
                                onClick = {},
                                variant = JengaButtonVariant.Ink,
                            )
                            JengaButton(
                                text = "Ghost",
                                onClick = {},
                                variant = JengaButtonVariant.Ghost,
                            )
                        },
                    )
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaButton(
                                text = "Outline",
                                onClick = {},
                                variant = JengaButtonVariant.Outline,
                            )
                            JengaButton(
                                text = "Brand outline",
                                onClick = {},
                                variant = JengaButtonVariant.BrandOutline,
                            )
                        },
                    )
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaButton(
                                text = "Danger",
                                onClick = {},
                                variant = JengaButtonVariant.Danger,
                            )
                            JengaButton(
                                text = "Save",
                                onClick = {},
                                loading = true,
                                loadingText = "Saving",
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Icon button",
        group = "Actions",
        description = "A compact, icon-only action in several emphases.",
        keywords = "standard filled tonal overlay",
        code = """
            JengaIconButton(
                onClick = {},
                variant = JengaIconButtonVariant.Filled,
                content = {
                    JengaIcon(
                        imageVector = JengaIcons.Add,
                        contentDescription = "Add",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Standard,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = "Search",
                            )
                        },
                    )
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Filled,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Add,
                                contentDescription = "Add",
                            )
                        },
                    )
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Tonal,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Info,
                                contentDescription = "Info",
                            )
                        },
                    )
                    Box(
                        modifier = Modifier.background(
                            color = Color.Black,
                        ),
                        content = {
                            JengaIconButton(
                                onClick = {},
                                variant = JengaIconButtonVariant.Overlay,
                                content = {
                                    JengaIcon(
                                        imageVector = JengaIcons.Flash,
                                        contentDescription = "Torch",
                                    )
                                },
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Floating action button",
        group = "Actions",
        description = "A prominent, circular primary action.",
        keywords = "fab add",
        code = """
            JengaFab(
                onClick = {},
                content = {
                    JengaIcon(
                        imageVector = JengaIcons.Add,
                        contentDescription = "Add",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaFab(
                onClick = {},
                content = {
                    JengaIcon(
                        imageVector = JengaIcons.Add,
                        contentDescription = "Add",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Extended floating action button",
        group = "Actions",
        description = "A prominent pill with an icon and a label, when the action needs a word.",
        keywords = "fab extended label pill create",
        code = """
            JengaExtendedFab(
                label = "New ticket",
                onClick = {},
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.Add,
                        contentDescription = null,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaExtendedFab(
                label = "New ticket",
                onClick = {},
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.Add,
                        contentDescription = null,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Link",
        group = "Actions",
        description = "An inline text action, optionally with a leading icon.",
        keywords = "link text hyperlink inline anchor",
        code = """
            JengaLink(
                text = "Forgot password?",
                onClick = {},
            )
            JengaLink(
                text = "Call support",
                onClick = {},
                leadingIcon = {
                    JengaIcon(
                        imageVector = JengaIcons.Phone,
                        contentDescription = null,
                    )
                },
            )
            JengaLink(
                text = "Unavailable",
                onClick = {},
                enabled = false,
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaLink(
                        text = "Forgot password?",
                        onClick = {},
                    )
                    JengaLink(
                        text = "Call support",
                        onClick = {},
                        leadingIcon = {
                            JengaIcon(
                                imageVector = JengaIcons.Phone,
                                contentDescription = null,
                            )
                        },
                    )
                    JengaLink(
                        text = "Unavailable",
                        onClick = {},
                        enabled = false,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Text field",
        group = "Inputs",
        description = "A single-line input with label, placeholder and status.",
        keywords = "input email form",
        code = """
            var text by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                placeholder = "you@example.com",
            )
        """.trimIndent(),
        demo = {
            var text by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                placeholder = "you@example.com",
            )
        },
    ),
    CatalogEntry(
        name = "Text area",
        group = "Inputs",
        description = "A multi-line box for comments and notes. Grows as you type, then scrolls; optional character limit with a counter.",
        keywords = "textarea comment notes description multiline reason counter limit",
        code = """
            var notes by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaTextArea(
                value = notes,
                onValueChange = { notes = it },
                label = "Visit notes",
                placeholder = "What happened on the visit?",
                maxLength = 200,
            )
        """.trimIndent(),
        demo = {
            var notes by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaTextArea(
                value = notes,
                onValueChange = { notes = it },
                label = "Visit notes",
                placeholder = "What happened on the visit?",
                maxLength = 200,
            )
        },
    ),
    CatalogEntry(
        name = "OTP field",
        group = "Inputs",
        description = "A one-time-code input drawn as cells. Paste, SMS autofill and screen readers treat it as one field.",
        keywords = "otp code pin verification sms one-time passcode",
        code = """
            var code by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaOtpField(
                value = code,
                onValueChange = { code = it },
                contentDescription = "Verification code",
                onComplete = {
                    verify(
                        it,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var code by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaStack(
                space = JengaTheme.spacing.lg,
                content = {
                    JengaOtpField(
                        value = code,
                        onValueChange = { code = it },
                        contentDescription = "Verification code",
                    )
                    JengaOtpField(
                        value = code.take(
                            n = 4,
                        ),
                        onValueChange = { code = it },
                        contentDescription = "PIN",
                        length = 4,
                        masked = true,
                        style = JengaOtpCellStyle.Filled,
                    )
                    JengaOtpField(
                        value = code,
                        onValueChange = { code = it },
                        contentDescription = "Verification code",
                        style = JengaOtpCellStyle.Underline,
                        groupSize = 3,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "OTP resend",
        group = "Inputs",
        description = "A countdown that turns into a resend action. The caller owns the time.",
        keywords = "otp resend countdown timer code",
        code = """
            val countdown = rememberJengaCountdown(
                duration = 30.seconds,
            )
            val remainingText = JengaOtpResendDefaults.formatRemaining(
                remaining = countdown.remaining,
            )
            JengaOtpResend(
                remaining = countdown.remaining,
                onResend = { countdown.restart() },
                countdownText = "Resend code in ${'$'}remainingText",
                resendText = "Resend code",
            )
        """.trimIndent(),
        demo = {
            val countdown = rememberJengaCountdown(
                duration = 30.seconds,
            )
            val remainingText = JengaOtpResendDefaults.formatRemaining(
                remaining = countdown.remaining,
            )
            JengaOtpResend(
                remaining = countdown.remaining,
                onResend = { countdown.restart() },
                countdownText = "Resend code in $remainingText",
                resendText = "Resend code",
            )
        },
    ),
    CatalogEntry(
        name = "OTP verification",
        group = "Inputs",
        description = "Title, description, code field, verifying row, resend and actions in one block for screens, sheets and dialogs.",
        keywords = "otp verification code sheet dialog screen",
        code = """
            JengaOtpVerification(
                title = "Enter the code",
                description = AnnotatedString(
                    text = "We sent a 6-digit code to +254 712 345 678",
                ),
                field = {
                    JengaOtpField(
                        value = code,
                        onValueChange = { code = it },
                        contentDescription = "Verification code",
                        onComplete = {
                            verify(
                                it,
                            )
                        },
                    )
                },
                verifying = verifying,
                verifyingText = "Verifying…",
                resend = {
                    JengaOtpResend(
                        remaining = remaining,
                        onResend = {},
                        countdownText = "…",
                        resendText = "Resend code",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var code by remember {
                mutableStateOf(
                    value = "",
                )
            }
            var verifying by remember {
                mutableStateOf(
                    value = false,
                )
            }
            var status by remember {
                mutableStateOf(
                    value = JengaTextFieldStatus.Default,
                )
            }
            val countdown = rememberJengaCountdown(
                duration = 5.minutes,
            )
            LaunchedEffect(
                key1 = verifying,
            ) {
                if (verifying) {
                    delay(
                        timeMillis = 1200,
                    )
                    status = if (code == "123456") JengaTextFieldStatus.Success else JengaTextFieldStatus.Error
                    verifying = false
                }
            }
            JengaOtpVerification(
                title = "Enter the code",
                description = AnnotatedString(
                    text = "We sent a 6-digit code to +254 712 345 678. Try 123456.",
                ),
                contentPadding = PaddingValues(),
                field = {
                    JengaOtpField(
                        value = code,
                        onValueChange = {
                            code = it
                            status = JengaTextFieldStatus.Default
                        },
                        contentDescription = "Verification code",
                        onComplete = { verifying = true },
                        readOnly = verifying,
                        status = status,
                        supportingText = when (status) {
                            JengaTextFieldStatus.Error -> "That code is not right. Try again."
                            JengaTextFieldStatus.Success -> "Verified"
                            JengaTextFieldStatus.Default -> null
                        },
                    )
                },
                verifying = verifying,
                verifyingText = "Verifying…",
                resend = {
                    val remainingText = JengaOtpResendDefaults.formatRemaining(
                        remaining = countdown.remaining,
                    )
                    JengaOtpResend(
                        remaining = countdown.remaining,
                        onResend = { countdown.restart() },
                        countdownText = "Resend code in $remainingText",
                        resendText = "Resend code",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Search field",
        group = "Inputs",
        description = "A pill-shaped input with a search icon and clear button.",
        keywords = "search query filter",
        code = """
            var query by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search",
                clearContentDescription = "Clear search",
            )
        """.trimIndent(),
        demo = {
            var query by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search",
                clearContentDescription = "Clear search",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {},
                ),
            )
        },
    ),
    CatalogEntry(
        name = "Search trigger",
        group = "Inputs",
        description = "Looks like a search field and opens a search screen when tapped.",
        keywords = "search entry button scan",
        code = """
            JengaSearchTrigger(
                placeholder = "Search tickets",
                onClick = { /* open search */ },
                trailingContent = {
                    JengaIcon(
                        imageVector = JengaIcons.QrCode,
                        contentDescription = "Scan",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaSearchTrigger(
                placeholder = "Search tickets",
                onClick = {},
                trailingContent = {
                    JengaIcon(
                        imageVector = JengaIcons.QrCode,
                        contentDescription = "Scan",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Select field",
        group = "Inputs",
        description = "Shows a chosen value and opens a picker when tapped.",
        keywords = "select dropdown picker choose option",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            var fault by remember {
                mutableStateOf<String?>(
                    value = null,
                )
            }
            JengaSelectField(
                value = fault,
                onClick = { open = true },
                label = "Fault",
                placeholder = "Choose a fault",
            )
            if (open) {
                JengaListSheet(
                    onDismissRequest = { open = false },
                    title = "Fault",
                    content = {
                        items(
                            items = listOf(
                                "Battery not charging",
                                "Panel damaged",
                            ),
                        ) { option ->
                            JengaRadioListItem(
                                headline = option,
                                selected = option == fault,
                                onClick = {
                                    fault = option
                                    open = false
                                },
                            )
                        }
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            var fault by remember {
                mutableStateOf<String?>(
                    value = null,
                )
            }
            JengaSelectField(
                value = fault,
                onClick = { open = true },
                label = "Fault",
                placeholder = "Choose a fault",
            )
            if (open) {
                JengaListSheet(
                    onDismissRequest = { open = false },
                    title = "Fault",
                    content = {
                        items(
                            items = listOf(
                                "Battery not charging",
                                "Panel damaged",
                            ),
                        ) { option ->
                            JengaRadioListItem(
                                headline = option,
                                selected = option == fault,
                                onClick = {
                                    fault = option
                                    open = false
                                },
                            )
                        }
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Toggle",
        group = "Selection",
        description = "A switch for an on or off setting.",
        keywords = "switch on off",
        code = """
            var checked by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaToggle(
                checked = checked,
                onCheckedChange = { checked = it },
            )
        """.trimIndent(),
        demo = {
            var checked by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaToggle(
                checked = checked,
                onCheckedChange = { checked = it },
            )
        },
    ),
    CatalogEntry(
        name = "Checkbox",
        group = "Selection",
        description = "A box for a single on or off choice.",
        keywords = "check tick",
        code = """
            var checked by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
            )
        """.trimIndent(),
        demo = {
            var checked by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
            )
        },
    ),
    CatalogEntry(
        name = "Radio button",
        group = "Selection",
        description = "Selects one option from a set.",
        keywords = "radio option single",
        code = """
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaRadioButton(
                selected = selected == 0,
                onClick = { selected = 0 },
            )
            JengaRadioButton(
                selected = selected == 1,
                onClick = { selected = 1 },
            )
            JengaRadioButton(
                selected = selected == 2,
                onClick = { selected = 2 },
                size = 18.dp,
            )
        """.trimIndent(),
        demo = {
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaInline(
                content = {
                    JengaRadioButton(
                        selected = selected == 0,
                        onClick = { selected = 0 },
                    )
                    JengaRadioButton(
                        selected = selected == 1,
                        onClick = { selected = 1 },
                    )
                    JengaRadioButton(
                        selected = selected == 2,
                        onClick = { selected = 2 },
                        size = 18.dp,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Radio list item",
        group = "Selection",
        description = "A list row with a radio button, for picking one option.",
        keywords = "radio option single list row",
        code = """
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaRadioListItem(
                headline = "Repair",
                selected = selected == 0,
                onClick = { selected = 0 },
            )
            JengaRadioListItem(
                headline = "Replace",
                selected = selected == 1,
                onClick = { selected = 1 },
                trailingContent = {
                    JengaBadge(
                        text = "Free",
                        tone = JengaBadgeTone.Success,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaRadioListItem(
                        headline = "Repair",
                        selected = selected == 0,
                        onClick = { selected = 0 },
                    )
                    JengaRadioListItem(
                        headline = "Replace",
                        selected = selected == 1,
                        onClick = { selected = 1 },
                        trailingContent = {
                            JengaBadge(
                                text = "Free",
                                tone = JengaBadgeTone.Success,
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Checkbox list item",
        group = "Selection",
        description = "A list row with a checkbox, for picking several options.",
        keywords = "checkbox multi select list row",
        code = """
            var checked by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaCheckboxListItem(
                headline = "Battery",
                checked = checked,
                onCheckedChange = { checked = it },
            )
            JengaCheckboxListItem(
                headline = "Inverter",
                checked = false,
                onCheckedChange = {},
                supporting = "Saving",
                loading = true,
            )
        """.trimIndent(),
        demo = {
            var battery by remember {
                mutableStateOf(
                    value = true,
                )
            }
            var panel by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaCheckboxListItem(
                        headline = "Battery",
                        checked = battery,
                        onCheckedChange = { battery = it },
                    )
                    JengaCheckboxListItem(
                        headline = "Panel",
                        checked = panel,
                        onCheckedChange = { panel = it },
                    )
                    JengaCheckboxListItem(
                        headline = "Inverter",
                        checked = false,
                        onCheckedChange = {},
                        supporting = "Saving",
                        loading = true,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Chip",
        group = "Selection",
        description = "A compact, selectable filter or tag.",
        keywords = "filter tag",
        code = """
            var selected by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaChip(
                label = "Filter",
                selected = selected,
                onClick = { selected = !selected },
            )
        """.trimIndent(),
        demo = {
            var selected by remember {
                mutableStateOf(
                    value = true,
                )
            }
            JengaInline(
                content = {
                    JengaChip(
                        label = "Filter",
                        selected = selected,
                        onClick = { selected = !selected },
                    )
                    JengaChip(
                        label = "Off",
                        selected = false,
                        onClick = {},
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Segmented control",
        group = "Selection",
        description = "Switches between a few mutually exclusive options.",
        keywords = "segment toggle group",
        code = """
            var index by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaSegmentedControl(
                selectedIndex = index,
                segments = listOf(
                    "Day",
                    "Week",
                    "Month",
                ),
                onSelect = { index = it },
                selectedTextStyle = JengaTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        """.trimIndent(),
        demo = {
            var index by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaSegmentedControl(
                selectedIndex = index,
                segments = listOf(
                    "Day",
                    "Week",
                    "Month",
                ),
                onSelect = { index = it },
                selectedTextStyle = JengaTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        },
    ),
    CatalogEntry(
        name = "Card",
        group = "Containers",
        description = "A surface for grouped content, elevated or outlined, optionally disabled.",
        keywords = "elevated outlined surface disabled",
        code = """
            JengaCard(
                variant = JengaCardVariant.Elevated,
                content = {
                    JengaText(
                        text = "Elevated",
                    )
                },
            )
            JengaCard(
                variant = JengaCardVariant.Outlined,
                content = {
                    JengaText(
                        text = "Outlined",
                    )
                },
            )
            JengaCard(
                variant = JengaCardVariant.Outlined,
                onClick = {},
                enabled = false,
                content = {
                    JengaText(
                        text = "Disabled",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaCard(
                        variant = JengaCardVariant.Elevated,
                        content = {
                            JengaText(
                                text = "Elevated",
                            )
                        },
                    )
                    JengaCard(
                        variant = JengaCardVariant.Outlined,
                        content = {
                            JengaText(
                                text = "Outlined",
                            )
                        },
                    )
                    JengaCard(
                        variant = JengaCardVariant.Outlined,
                        onClick = {},
                        enabled = false,
                        content = {
                            JengaText(
                                text = "Disabled",
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "List item",
        group = "Containers",
        description = "A row with leading, headline, supporting and trailing slots.",
        keywords = "row leading trailing",
        code = """
            JengaListItem(
                headline = "Gate A",
                supporting = "Main entrance",
                leadingContent = {
                    JengaIcon(
                        imageVector = JengaIcons.Check,
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    JengaIcon(
                        imageVector = JengaIcons.ChevronRight,
                        contentDescription = null,
                    )
                },
                onClick = {},
            )
            JengaListItem(
                headline = "Gate B",
                supporting = "North wing",
                supportingContent = {
                    JengaBadge(
                        text = "Busy",
                        tone = JengaBadgeTone.Warning,
                    )
                },
                onClick = {},
            )
            JengaListItem(
                headline = "Gate C",
                supporting = "Closed",
                onClick = {},
                enabled = false,
            )
            JengaListItem(
                headline =
                buildAnnotatedString {
                    append(
                        text = "Gate D ",
                    )
                    withStyle(
                        style = SpanStyle(
                            color = JengaTheme.colors.textMuted,
                        ),
                    ) {
                        append(
                            text = "· 12 waiting",
                        )
                    }
                },
                supporting = "East wing, opens at noon",
                headlineStyle = JengaTheme.typography.titleSmall,
                onClick = {},
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaListItem(
                        headline = "Gate A",
                        supporting = "Main entrance",
                        leadingContent = {
                            JengaIcon(
                                imageVector = JengaIcons.Check,
                                contentDescription = null,
                                tint = JengaTheme.colors.success,
                            )
                        },
                        trailingContent = {
                            JengaIcon(
                                imageVector = JengaIcons.ChevronRight,
                                contentDescription = null,
                            )
                        },
                        onClick = {},
                    )
                    JengaListItem(
                        headline = "Gate B",
                        supporting = "North wing",
                        supportingContent = {
                            JengaBadge(
                                text = "Busy",
                                tone = JengaBadgeTone.Warning,
                            )
                        },
                        onClick = {},
                    )
                    JengaListItem(
                        headline = "Gate C",
                        supporting = "Closed",
                        onClick = {},
                        enabled = false,
                    )
                    JengaListItem(
                        headline = buildAnnotatedString {
                            append(
                                text = "Gate D ",
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = JengaTheme.colors.textMuted,
                                ),
                            ) {
                                append(
                                    text = "· 12 waiting",
                                )
                            }
                        },
                        supporting = "East wing, opens at noon",
                        headlineStyle = JengaTheme.typography.titleSmall,
                        onClick = {},
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Divider",
        group = "Containers",
        description = "A thin rule that separates content.",
        keywords = "separator line rule",
        code = "JengaDivider()",
        demo = {
            JengaDivider()
        },
    ),
    CatalogEntry(
        name = "Banner",
        group = "Feedback",
        description = "An inline, tinted message tied to a status.",
        keywords = "inline message info error",
        code = """
            JengaBanner(
                message = "Working offline; scans will sync later.",
                tone = JengaBannerTone.Info,
            )
            JengaBanner(
                message = "Already scanned.",
                tone = JengaBannerTone.Error,
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaBanner(
                        message = "Working offline; scans will sync later.",
                        tone = JengaBannerTone.Info,
                    )
                    JengaBanner(
                        message = "Already scanned.",
                        tone = JengaBannerTone.Error,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Empty state",
        group = "Feedback",
        description = "A centered message for a screen or list with nothing to show yet.",
        keywords = "empty blank nothing zero placeholder",
        code = """
            JengaEmptyState(
                title = "No tickets yet",
                description = "Tickets you scan will show up here.",
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.Ticket,
                        contentDescription = null,
                    )
                },
                actionLabel = "Scan a ticket",
                onAction = {},
                actionSize = JengaButtonSize.Small,
            )
        """.trimIndent(),
        demo = {
            JengaEmptyState(
                title = "No tickets yet",
                description = "Tickets you scan will show up here.",
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.Ticket,
                        contentDescription = null,
                    )
                },
                actionLabel = "Scan a ticket",
                onAction = {},
                actionSize = JengaButtonSize.Small,
            )
        },
    ),
    CatalogEntry(
        name = "Error state",
        group = "Feedback",
        description = "A centered failure message with a retry action.",
        keywords = "error failure retry offline",
        code = """
            JengaErrorState(
                title = "Couldn't load tickets",
                description = "Check your connection and try again.",
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.CloudOff,
                        contentDescription = null,
                    )
                },
                actionLabel = "Retry",
                onAction = {},
            )
        """.trimIndent(),
        demo = {
            JengaErrorState(
                title = "Couldn't load tickets",
                description = "Check your connection and try again.",
                icon = {
                    JengaIcon(
                        imageVector = JengaIcons.CloudOff,
                        contentDescription = null,
                    )
                },
                actionLabel = "Retry",
                onAction = {},
            )
        },
    ),
    CatalogEntry(
        name = "Pull to refresh",
        group = "Feedback",
        description = "Wraps scrollable content so a downward pull reloads it.",
        keywords = "refresh reload pull swipe spinner",
        code = """
            var refreshing by remember {
                mutableStateOf(
                    value = false,
                )
            }
            LaunchedEffect(
                key1 = refreshing,
            ) {
                if (refreshing) {
                    delay(
                        timeMillis = 1500,
                    )
                    refreshing = false
                }
            }
            JengaPullToRefresh(
                isRefreshing = refreshing,
                onRefresh = { refreshing = true },
                modifier = Modifier.height(
                    height = 200.dp,
                ),
                content = {
                    LazyColumn(
                        content = {
                            items(
                                items = listOf(
                                    "Gate A",
                                    "Gate B",
                                    "Gate C",
                                ),
                            ) { gate ->
                                JengaListItem(
                                    headline = gate,
                                    supporting = "Pull down to refresh",
                                )
                            }
                        },
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var refreshing by remember {
                mutableStateOf(
                    value = false,
                )
            }
            LaunchedEffect(
                key1 = refreshing,
            ) {
                if (refreshing) {
                    delay(
                        timeMillis = 1500,
                    )
                    refreshing = false
                }
            }
            JengaPullToRefresh(
                isRefreshing = refreshing,
                onRefresh = { refreshing = true },
                modifier = Modifier.fillMaxWidth().height(
                    height = 200.dp,
                ),
                content = {
                    LazyColumn(
                        content = {
                            items(
                                items = listOf(
                                    "Gate A",
                                    "Gate B",
                                    "Gate C",
                                ),
                            ) { gate ->
                                JengaListItem(
                                    headline = gate,
                                    supporting = "Pull down to refresh",
                                )
                            }
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Snackbar",
        group = "Feedback",
        description = "A brief message with an optional action.",
        keywords = "toast action undo",
        code = """
            JengaSnackbar(
                message = "Saved offline",
                tone = JengaSnackbarTone.Neutral,
                actionLabel = "Undo",
                onAction = {},
            )
        """.trimIndent(),
        demo = {
            JengaSnackbar(
                message = "Saved offline",
                tone = JengaSnackbarTone.Neutral,
                actionLabel = "Undo",
                onAction = {},
            )
        },
    ),
    CatalogEntry(
        name = "Tooltip",
        group = "Feedback",
        description = "A small label that explains an element.",
        keywords = "hint label",
        code = """
            JengaTooltip(
                text = "Re-admit this attendee",
            )
        """.trimIndent(),
        demo = {
            JengaTooltip(
                text = "Re-admit this attendee",
            )
        },
    ),
    CatalogEntry(
        name = "Dialog",
        group = "Feedback",
        description = "A modal that interrupts for a decision.",
        keywords = "alert modal confirm",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaButton(
                text = "Dialog",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaDialog(
                    onDismissRequest = { open = false },
                    title = "Reset device?",
                    text = "This clears the cached gate session on this device.",
                    confirmButton = {
                        JengaButton(
                            text = "Reset",
                            onClick = { open = false },
                            variant = JengaButtonVariant.Danger,
                        )
                    },
                    dismissButton = {
                        JengaButton(
                            text = "Cancel",
                            onClick = { open = false },
                            variant = JengaButtonVariant.Ghost,
                        )
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Dialog",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaDialog(
                    onDismissRequest = { open = false },
                    title = "Reset device?",
                    text = "This clears the cached gate session on this device.",
                    confirmButton = {
                        JengaButton(
                            text = "Reset",
                            onClick = { open = false },
                            variant = JengaButtonVariant.Danger,
                        )
                    },
                    dismissButton = {
                        JengaButton(
                            text = "Cancel",
                            onClick = { open = false },
                            variant = JengaButtonVariant.Ghost,
                        )
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Bottom sheet",
        group = "Feedback",
        description = "A panel that slides up from the bottom edge.",
        keywords = "sheet modal drawer",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaButton(
                text = "Bottom sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaBottomSheet(
                    onDismissRequest = { open = false },
                    content = {
                        JengaStack(
                            modifier = Modifier.padding(
                                all = JengaTheme.spacing.xl,
                            ),
                            content = {
                                JengaText(
                                    text = "Select a gate",
                                    style = JengaTheme.typography.titleLarge,
                                )
                            },
                        )
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Bottom sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaBottomSheet(
                    onDismissRequest = { open = false },
                    content = {
                        JengaStack(
                            modifier = Modifier.fillMaxWidth().padding(
                                all = JengaTheme.spacing.xl,
                            ),
                            content = {
                                JengaText(
                                    text = "Select a gate",
                                    style = JengaTheme.typography.titleLarge,
                                )
                                JengaText(
                                    text = "Choose which gate you're scanning at.",
                                    color = JengaTheme.colors.textMuted,
                                )
                            },
                        )
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "List sheet",
        group = "Feedback",
        description = "A bottom sheet with a title, a lazy list body and a pinned footer, for pickers and short forms.",
        keywords = "sheet modal picker select footer",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            var selected by remember {
                mutableStateOf(
                    value = "Gate A",
                )
            }
            JengaButton(
                text = "List sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaListSheet(
                    onDismissRequest = { open = false },
                    title = "Select a gate",
                    subtitle =
                    buildAnnotatedString {
                        append(
                            text = "Scanning at ",
                        )
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        ) {
                            append(
                                text = selected,
                            )
                        }
                    },
                    footer = {
                        JengaButton(
                            text = "Done",
                            onClick = { open = false },
                            modifier = Modifier.weight(
                                weight = 1f,
                            ),
                        )
                    },
                    content = {
                        items(
                            items = listOf(
                                "Gate A",
                                "Gate B",
                                "Gate C",
                            ),
                        ) { gate ->
                            JengaRadioListItem(
                                headline = gate,
                                selected = gate == selected,
                                onClick = { selected = gate },
                            )
                        }
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            var selected by remember {
                mutableStateOf(
                    value = "Gate A",
                )
            }
            JengaButton(
                text = "List sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaListSheet(
                    onDismissRequest = { open = false },
                    title = "Select a gate",
                    subtitle = buildAnnotatedString {
                        append(
                            text = "Scanning at ",
                        )
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        ) {
                            append(
                                text = selected,
                            )
                        }
                    },
                    footer = {
                        JengaButton(
                            text = "Done",
                            onClick = { open = false },
                            modifier = Modifier.weight(
                                weight = 1f,
                            ),
                        )
                    },
                    content = {
                        items(
                            items = listOf(
                                "Gate A",
                                "Gate B",
                                "Gate C",
                            ),
                        ) { gate ->
                            JengaRadioListItem(
                                headline = gate,
                                selected = gate == selected,
                                onClick = { selected = gate },
                            )
                        }
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Dropdown menu",
        group = "Feedback",
        description = "A list of actions anchored to a trigger.",
        keywords = "menu popup overflow",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            Box(
                content = {
                    JengaButton(
                        text = "Menu",
                        onClick = { open = true },
                        variant = JengaButtonVariant.Outline,
                    )
                    JengaDropdownMenu(
                        expanded = open,
                        onDismissRequest = { open = false },
                        content = {
                            JengaDropdownMenuItem(
                                text = "Re-enter",
                                onClick = { open = false },
                            )
                            JengaDropdownMenuItem(
                                text = "View history",
                                onClick = { open = false },
                            )
                            JengaDropdownMenuDivider()
                            JengaDropdownMenuItem(
                                text = "Revoke",
                                onClick = { open = false },
                                tone = JengaDropdownMenuItemTone.Danger,
                            )
                        },
                    )
                },
            )
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            Box(
                content = {
                    JengaButton(
                        text = "Menu",
                        onClick = { open = true },
                        variant = JengaButtonVariant.Outline,
                    )
                    JengaDropdownMenu(
                        expanded = open,
                        onDismissRequest = { open = false },
                        content = {
                            JengaDropdownMenuItem(
                                text = "Re-enter",
                                onClick = { open = false },
                            )
                            JengaDropdownMenuItem(
                                text = "View history",
                                onClick = { open = false },
                            )
                            JengaDropdownMenuDivider()
                            JengaDropdownMenuItem(
                                text = "Revoke",
                                onClick = { open = false },
                                tone = JengaDropdownMenuItemTone.Danger,
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Tabs",
        group = "Navigation",
        description = "Switches between views with an underline indicator.",
        keywords = "tab bar",
        code = """
            var tab by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaTabs(
                selectedIndex = tab,
                tabs = listOf(
                    "Upcoming",
                    "Live",
                    "Past",
                ),
                onSelect = { tab = it },
            )
        """.trimIndent(),
        demo = {
            var tab by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaTabs(
                selectedIndex = tab,
                tabs = listOf(
                    "Upcoming",
                    "Live",
                    "Past",
                ),
                onSelect = { tab = it },
            )
        },
    ),
    CatalogEntry(
        name = "Top app bar",
        group = "Navigation",
        description = "A screen title with optional navigation and actions.",
        keywords = "app bar toolbar header title back",
        code = """
            JengaTopAppBar(
                title = "Events",
                subtitle = "Tonight",
                navigationIcon = {
                    JengaIconButton(
                        onClick = {},
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.ArrowBack,
                                contentDescription = "Back",
                            )
                        },
                    )
                },
                actions = {
                    JengaIconButton(
                        onClick = {},
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = "Search",
                            )
                        },
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaTopAppBar(
                title = "Events",
                subtitle = "Tonight",
                navigationIcon = {
                    JengaIconButton(
                        onClick = {},
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.ArrowBack,
                                contentDescription = "Back",
                            )
                        },
                    )
                },
                actions = {
                    JengaIconButton(
                        onClick = {},
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = "Search",
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Navigation bar",
        group = "Navigation",
        description = "Top-level destinations along the bottom edge.",
        keywords = "bottom nav destinations",
        code = """
            JengaNavigationBar(
                content = {
                    JengaNavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = null,
                            )
                        },
                        label = "Events",
                    )
                    JengaNavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Check,
                                contentDescription = null,
                            )
                        },
                        label = "Scan",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaNavigationBar(
                content = {
                    JengaNavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = null,
                            )
                        },
                        label = "Events",
                    )
                    JengaNavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Check,
                                contentDescription = null,
                            )
                        },
                        label = "Scan",
                    )
                    JengaNavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Info,
                                contentDescription = null,
                            )
                        },
                        label = "Stats",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Bottom bar",
        group = "Navigation",
        description = "A footer row of actions for the current screen.",
        keywords = "action bar footer",
        code = """
            JengaBottomBar(
                content = {
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Standard,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = "Search",
                            )
                        },
                    )
                    JengaButton(
                        text = "Manual entry",
                        onClick = {},
                        modifier = Modifier.weight(
                            weight = 1f,
                        ),
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaBottomBar(
                content = {
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Standard,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.Search,
                                contentDescription = "Search",
                            )
                        },
                    )
                    JengaButton(
                        text = "Manual entry",
                        onClick = {},
                        modifier = Modifier.weight(
                            weight = 1f,
                        ),
                    )
                    JengaIconButton(
                        onClick = {},
                        variant = JengaIconButtonVariant.Standard,
                        content = {
                            JengaIcon(
                                imageVector = JengaIcons.History,
                                contentDescription = "History",
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Badge",
        group = "Data display",
        description = "A small status label in several tones.",
        keywords = "label status tone",
        code = """
            JengaBadge(
                text = "Valid",
                tone = JengaBadgeTone.Success,
            )
            JengaBadge(
                text = "Pending",
                tone = JengaBadgeTone.Warning,
            )
            JengaBadge(
                text = "Denied",
                tone = JengaBadgeTone.Error,
            )
        """.trimIndent(),
        demo = {
            JengaWrap(
                content = {
                    JengaBadge(
                        text = "Valid",
                        tone = JengaBadgeTone.Success,
                    )
                    JengaBadge(
                        text = "Pending",
                        tone = JengaBadgeTone.Warning,
                    )
                    JengaBadge(
                        text = "Denied",
                        tone = JengaBadgeTone.Error,
                    )
                    JengaBadge(
                        text = "Info",
                        tone = JengaBadgeTone.Info,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Icon tile",
        group = "Data display",
        description = "An icon on a tinted rounded square, in each tone.",
        keywords = "icon tile leading tone",
        code = """
            JengaIconTile(
                icon = JengaIcons.Wrench,
                contentDescription = null,
            )
            JengaIconTile(
                icon = JengaIcons.Package,
                contentDescription = null,
                tone = JengaBadgeTone.Brand,
            )
            JengaIconTile(
                icon = JengaIcons.XCircle,
                contentDescription = null,
                tone = JengaBadgeTone.Error,
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaIconTile(
                        icon = JengaIcons.Wrench,
                        contentDescription = null,
                    )
                    JengaIconTile(
                        icon = JengaIcons.Package,
                        contentDescription = null,
                        tone = JengaBadgeTone.Brand,
                    )
                    JengaIconTile(
                        icon = JengaIcons.CheckCircle,
                        contentDescription = null,
                        tone = JengaBadgeTone.Success,
                    )
                    JengaIconTile(
                        icon = JengaIcons.XCircle,
                        contentDescription = null,
                        tone = JengaBadgeTone.Error,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Key value row",
        group = "Data display",
        description = "A label with an end-aligned value, for detail and summary sections.",
        keywords = "detail summary total label value",
        code = """
            JengaKeyValueRow(
                label = "Serial number",
                value = "SN-0042-7781",
                valueStyle = JengaTheme.typography.mono,
            )
            JengaKeyValueRow(
                label = "Status",
                value = "Paid",
                valueTone = JengaBadgeTone.Success,
            )
            JengaDivider()
            JengaKeyValueRow(
                label = "Total",
                value = "KES 12,450",
                emphasis = JengaKeyValueEmphasis.Total,
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaKeyValueRow(
                        label = "Serial number",
                        value = "SN-0042-7781",
                        valueStyle = JengaTheme.typography.mono,
                    )
                    JengaKeyValueRow(
                        label = "Status",
                        value = "Paid",
                        valueTone = JengaBadgeTone.Success,
                    )
                    JengaDivider()
                    JengaKeyValueRow(
                        label = "Total",
                        value = "KES 12,450",
                        emphasis = JengaKeyValueEmphasis.Total,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Avatar",
        group = "Data display",
        description = "Initials or an image, in three sizes or a custom diameter.",
        keywords = "initials profile picture diameter",
        code = """
            JengaAvatar(
                name = "Joel Kanyi",
                size = JengaAvatarSize.Small,
            )
            JengaAvatar(
                name = "Ada Lovelace",
            )
            JengaAvatar(
                name = "Grace Hopper",
                size = JengaAvatarSize.Large,
            )
            JengaAvatar(
                name = "Alan Turing",
                diameter = 72.dp,
                textStyle = JengaTheme.typography.headingSmall,
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaAvatar(
                        name = "Joel Kanyi",
                        size = JengaAvatarSize.Small,
                    )
                    JengaAvatar(
                        name = "Ada Lovelace",
                    )
                    JengaAvatar(
                        name = "Grace Hopper",
                        size = JengaAvatarSize.Large,
                    )
                    JengaAvatar(
                        name = "Alan Turing",
                        diameter = 72.dp,
                        textStyle = JengaTheme.typography.headingSmall,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Progress",
        group = "Data display",
        description = "Determinate and indeterminate, linear and circular.",
        keywords = "linear circular loading indeterminate",
        code = """
            JengaLinearProgress(
                progress = 0.6f,
            )
            JengaLinearProgress(
                progress = 0.2f,
                tone = JengaProgressTone.Error,
            )
            JengaLinearProgressIndeterminate()
            JengaCircularProgress(
                progress = 0.7f,
            )
            JengaCircularProgressIndeterminate()
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.md,
                content = {
                    JengaLinearProgress(
                        progress = 0.6f,
                    )
                    JengaLinearProgress(
                        progress = 0.2f,
                        tone = JengaProgressTone.Error,
                    )
                    JengaLinearProgressIndeterminate()
                    JengaInline(
                        content = {
                            JengaCircularProgress(
                                progress = 0.7f,
                            )
                            JengaCircularProgressIndeterminate()
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Slider",
        group = "Data display",
        description = "Picks a value from a continuous range.",
        keywords = "range value drag",
        code = """
            var value by remember {
                mutableFloatStateOf(
                    value = 0.4f,
                )
            }
            JengaSlider(
                value = value,
                onValueChange = { value = it },
            )
        """.trimIndent(),
        demo = {
            var value by remember {
                mutableFloatStateOf(
                    value = 0.4f,
                )
            }
            JengaSlider(
                value = value,
                onValueChange = { value = it },
            )
        },
    ),
    CatalogEntry(
        name = "Shimmer",
        group = "Data display",
        description = "A loading placeholder that animates.",
        keywords = "skeleton placeholder loading",
        code = """
            Box(
                modifier = Modifier
                    .size(
                        width = 160.dp,
                        height = 16.dp,
                    )
                    .clip(
                        shape = JengaTheme.shapes.sm,
                    )
                    .jengaShimmer(),
            )
        """.trimIndent(),
        demo = {
            Box(
                modifier = Modifier.size(
                    width = 160.dp,
                    height = 16.dp,
                ).clip(
                    shape = JengaTheme.shapes.sm,
                ).jengaShimmer(),
            )
        },
    ),
    CatalogEntry(
        name = "Grid",
        group = "Layout",
        description = "Arranges children into equal columns.",
        keywords = "columns arrange",
        code = """
            JengaGrid(
                columns = 3,
                content = {
                    repeat(
                        times = 6,
                    ) {
                        JengaBox(
                            padding = PaddingValues(
                                all = JengaTheme.spacing.md,
                            ),
                            content = {
                                JengaText(
                                    text = "${'$'}it",
                                )
                            },
                        )
                    }
                },
            )
        """.trimIndent(),
        demo = {
            JengaGrid(
                columns = 3,
                content = {
                    repeat(
                        times = 6,
                    ) { i ->
                        JengaBox(
                            padding = PaddingValues(
                                all = JengaTheme.spacing.md,
                            ),
                            background = JengaTheme.colors.surfaceSunk,
                            shape = JengaTheme.shapes.md,
                            contentAlignment = androidx.compose.ui.Alignment.Center,
                            content = {
                                JengaText(
                                    text = "${i + 1}",
                                )
                            },
                        )
                    }
                },
            )
        },
    ),
    CatalogEntry(
        name = "Stack and inline",
        group = "Layout",
        description = "Vertical and horizontal layouts with token spacing, plus a one-off spacer.",
        keywords = "stack inline column row spacer gap spacing",
        code = """
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaText(
                        text = "Stacked first",
                    )
                    JengaText(
                        text = "Stacked second",
                    )
                    JengaSpacer(
                        size = JengaTheme.spacing.lg,
                    )
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaBadge(
                                text = "Inline",
                            )
                            JengaBadge(
                                text = "Row",
                            )
                        },
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaText(
                        text = "Stacked first",
                    )
                    JengaText(
                        text = "Stacked second",
                    )
                    JengaSpacer(
                        size = JengaTheme.spacing.lg,
                    )
                    JengaInline(
                        space = JengaTheme.spacing.sm,
                        content = {
                            JengaBadge(
                                text = "Inline",
                            )
                            JengaBadge(
                                text = "Row",
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Wrap",
        group = "Layout",
        description = "A row that wraps onto new lines when it runs out of width.",
        keywords = "flow wrap tags chips lines",
        code = """
            JengaWrap(
                content = {
                    listOf(
                        "Solar",
                        "Battery",
                        "Inverter",
                        "Lighting",
                        "Radio",
                        "Fan",
                    ).forEach {
                        JengaChip(
                            label = it,
                            selected = false,
                            onClick = {},
                        )
                    }
                },
            )
        """.trimIndent(),
        demo = {
            JengaWrap(
                content = {
                    listOf(
                        "Solar",
                        "Battery",
                        "Inverter",
                        "Lighting",
                        "Radio",
                        "Fan",
                    ).forEach {
                        JengaChip(
                            label = it,
                            selected = false,
                            onClick = {},
                        )
                    }
                },
            )
        },
    ),
    CatalogEntry(
        name = "Section",
        group = "Layout",
        description = "A titled group of content with an optional header action.",
        keywords = "section group header title block",
        code = """
            JengaSection(
                title = "Account",
                actionLabel = "Edit",
                onActionClick = {},
                content = {
                    JengaKeyValueRow(
                        label = "Name",
                        value = "Ada Lovelace",
                    )
                    JengaKeyValueRow(
                        label = "Plan",
                        value = "Monthly",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaSection(
                title = "Account",
                actionLabel = "Edit",
                onActionClick = {},
                content = {
                    JengaKeyValueRow(
                        label = "Name",
                        value = "Ada Lovelace",
                    )
                    JengaKeyValueRow(
                        label = "Plan",
                        value = "Monthly",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Scaffold",
        group = "Layout",
        description = "The screen frame: top bar, bottom bar, snackbar host and inset-aware content.",
        keywords = "scaffold screen frame layout top bar bottom bar",
        code = """
            JengaScaffold(
                modifier = Modifier.height(
                    height = 240.dp,
                ),
                topBar = {
                    JengaTopAppBar(
                        title = "Inbox",
                    )
                },
                bottomBar = {
                    JengaBottomBar(
                        content = {
                            JengaButton(
                                text = "Compose",
                                onClick = {},
                                modifier = Modifier.weight(
                                    weight = 1f,
                                ),
                            )
                        },
                    )
                },
                content = { padding ->
                    JengaText(
                        text = "Screen content",
                        modifier = Modifier.padding(
                            paddingValues = padding,
                        ).padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaScaffold(
                modifier = Modifier.fillMaxWidth().height(
                    height = 240.dp,
                ),
                topBar = {
                    JengaTopAppBar(
                        title = "Inbox",
                    )
                },
                bottomBar = {
                    JengaBottomBar(
                        content = {
                            JengaButton(
                                text = "Compose",
                                onClick = {},
                                modifier = Modifier.weight(
                                    weight = 1f,
                                ),
                            )
                        },
                    )
                },
                content = { padding ->
                    JengaText(
                        text = "Screen content",
                        modifier = Modifier.padding(
                            paddingValues = padding,
                        ).padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Section header",
        group = "Patterns",
        description = "A titled header with an optional action.",
        keywords = "title subtitle action",
        code = """
            JengaSectionHeader(
                title = "Recent scans",
                subtitle = "Last 24 hours",
                actionLabel = "See all",
                onActionClick = {},
            )
        """.trimIndent(),
        demo = {
            JengaSectionHeader(
                title = "Recent scans",
                subtitle = "Last 24 hours",
                actionLabel = "See all",
                onActionClick = {},
            )
        },
    ),
    CatalogEntry(
        name = "Stat card",
        group = "Patterns",
        description = "A metric with a trend indicator.",
        keywords = "metric trend",
        code = """
            JengaStatCard(
                label = "Checked in",
                value = "1,284",
                trendLabel = "+12%",
                trendTone = JengaBadgeTone.Success,
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaStatCard(
                        label = "Checked in",
                        value = "1,284",
                        trendLabel = "+12%",
                        trendTone = JengaBadgeTone.Success,
                    )
                    JengaStatCard(
                        label = "Denied",
                        value = "37",
                        trendLabel = "+3",
                        trendTone = JengaBadgeTone.Error,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Ticket row",
        group = "Patterns",
        description = "A named row with a status badge.",
        keywords = "list ticket status",
        code = """
            JengaTicketRow(
                attendeeName = "Joel Kanyi",
                detail = "VIP · TKT-2026-001",
                statusLabel = "Valid",
                statusTone = JengaBadgeTone.Success,
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaTicketRow(
                        attendeeName = "Joel Kanyi",
                        detail = "VIP · TKT-2026-001",
                        statusLabel = "Valid",
                        statusTone = JengaBadgeTone.Success,
                    )
                    JengaTicketRow(
                        attendeeName = "Ada Lovelace",
                        detail = "Regular · TKT-2026-114",
                        statusLabel = "Used",
                        statusTone = JengaBadgeTone.Neutral,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Status pill",
        group = "Patterns",
        description = "A compact status with an optional spinner.",
        keywords = "sync offline loading",
        code = """
            JengaStatusPill(
                label = "Synced",
                tone = JengaBadgeTone.Success,
            )
            JengaStatusPill(
                label = "3 pending",
                tone = JengaBadgeTone.Warning,
                loading = true,
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaStatusPill(
                        label = "Synced",
                        tone = JengaBadgeTone.Success,
                    )
                    JengaStatusPill(
                        label = "3 pending",
                        tone = JengaBadgeTone.Warning,
                        loading = true,
                    )
                    JengaStatusPill(
                        label = "Offline",
                        tone = JengaBadgeTone.Error,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Verdict bar",
        group = "Content blocks",
        description = "A headline amount with progress and sublines.",
        keywords = "amount budget progress",
        code = """
            JengaVerdictBar(
                amount = "KES 1,190",
                amountSuffix = "left",
                tone = JengaVerdictTone.Positive,
                label = "This month",
                progress = 0.62f,
                sublines = JengaVerdictSublines(
                    start = "KES 710 of 1,900 spent",
                    end = "8 of 11 priced",
                ),
                action = JengaAction(
                    label = "Change",
                    onClick = {},
                ),
            )
        """.trimIndent(),
        demo = {
            JengaVerdictBar(
                amount = "KES 1,190",
                amountSuffix = "left",
                tone = JengaVerdictTone.Positive,
                label = "This month",
                progress = 0.62f,
                sublines = JengaVerdictSublines(
                    start = "KES 710 of 1,900 spent",
                    end = "8 of 11 priced",
                ),
                action = JengaAction(
                    label = "Change",
                    onClick = {},
                ),
            )
        },
    ),
    CatalogEntry(
        name = "Stat tile",
        group = "Content blocks",
        description = "A labelled metric with a unit and tone.",
        keywords = "metric value unit",
        code = """
            JengaStatTile(
                label = "Revenue",
                value = "1,284",
                unit = "KES",
                tone = JengaStatTone.Success,
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                content = {
                    JengaStatTile(
                        label = "Revenue",
                        value = "1,284",
                        unit = "KES",
                        tone = JengaStatTone.Success,
                    )
                    JengaStatTile(
                        label = "Refunds",
                        value = "37",
                        tone = JengaStatTone.Error,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Stepper",
        group = "Content blocks",
        description = "Increments or decrements a bounded number.",
        keywords = "quantity increment decrement",
        code = """
            var value by remember {
                mutableIntStateOf(
                    value = 2,
                )
            }
            JengaStepper(
                value = value,
                onValueChange = { value = it },
                min = 1,
                max = 9,
            )
        """.trimIndent(),
        demo = {
            var value by remember {
                mutableIntStateOf(
                    value = 2,
                )
            }
            JengaStepper(
                value = value,
                onValueChange = { value = it },
                min = 1,
                max = 9,
            )
        },
    ),
    CatalogEntry(
        name = "Dot strip",
        group = "Content blocks",
        description = "Progress or paging shown as filled dots.",
        keywords = "progress steps pager",
        code = """
            JengaDotStrip(
                filled = 3,
                total = 5,
                contentDescription = "3 of 5 done",
            )
        """.trimIndent(),
        demo = {
            JengaDotStrip(
                filled = 3,
                total = 5,
                contentDescription = "3 of 5 done",
            )
        },
    ),
    CatalogEntry(
        name = "Media hero",
        group = "Content blocks",
        description = "A media header with a title and support line.",
        keywords = "header image title",
        code = """
            JengaMediaHero(
                title = "Night Market",
                support = "Fri · 7pm · Riverside",
            )
        """.trimIndent(),
        demo = {
            JengaMediaHero(
                title = "Night Market",
                support = "Fri · 7pm · Riverside",
            )
        },
    ),
    CatalogEntry(
        name = "Expandable row",
        group = "Content blocks",
        description = "A card whose header reveals more content.",
        keywords = "accordion collapse disclosure",
        code = """
            var expanded by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaExpandableRow(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                header = {
                    JengaText(
                        text = "Delivery details",
                        modifier = Modifier.weight(
                            weight = 1f,
                        ),
                    )
                },
                content = {
                    JengaText(
                        text = "Ships in 2 to 3 business days.",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var expanded by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaExpandableRow(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                header = {
                    JengaText(
                        text = "Delivery details",
                        modifier = Modifier.weight(
                            weight = 1f,
                        ),
                    )
                },
                content = {
                    JengaText(
                        text = "Ships in 2 to 3 business days. Free returns within 30 days.",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Reaction bar",
        group = "Content blocks",
        description = "Positive and negative feedback actions.",
        keywords = "thumbs helpful feedback",
        code = """
            JengaReactionBar(
                onPositive = {},
                onNegative = {},
                positiveContentDescription = "Helpful",
                negativeContentDescription = "Not helpful",
            )
        """.trimIndent(),
        demo = {
            JengaReactionBar(
                onPositive = {},
                onNegative = {},
                positiveContentDescription = "Helpful",
                negativeContentDescription = "Not helpful",
            )
        },
    ),
    CatalogEntry(
        name = "Swipe to dismiss",
        group = "Content blocks",
        description = "Swipe a row away to dismiss it.",
        keywords = "swipe delete gesture",
        code = """
            JengaSwipeToDismiss(
                onDismiss = {},
                content = {
                    JengaListItem(
                        headline = "Swipe me away",
                        supporting = "End to start to dismiss",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaSwipeToDismiss(
                onDismiss = {},
                content = {
                    JengaListItem(
                        headline = "Swipe me away",
                        supporting = "End to start to dismiss",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Image shelf",
        group = "Media",
        description = "A horizontal shelf of media cards.",
        keywords = "carousel horizontal cards",
        code = """
            JengaImageShelf(
                title = "Picked for tonight",
                action = JengaAction(
                    label = "See all",
                    onClick = {},
                ),
                content = {
                    JengaShelfCard(
                        title = "Grilled veg bowl",
                        meta = "25 min · ~540 kcal",
                    )
                    JengaShelfCard(
                        title = "Miso ramen",
                        meta = "30 min · ~610 kcal",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaImageShelf(
                title = "Picked for tonight",
                action = JengaAction(
                    label = "See all",
                    onClick = {},
                ),
                content = {
                    JengaShelfCard(
                        title = "Grilled veg bowl",
                        meta = "25 min · ~540 kcal",
                    )
                    JengaShelfCard(
                        title = "Miso ramen",
                        meta = "30 min · ~610 kcal",
                    )
                    JengaShelfCard(
                        title = "Poke bowl",
                        meta = "15 min · ~480 kcal",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Image",
        group = "Media",
        description = "A remote image, clipped to a shape, with a placeholder while it loads.",
        keywords = "image photo url remote load placeholder",
        code = """
            JengaImage(
                url = "https://example.com/photo.jpg",
                contentDescription = "Product photo",
                modifier =
                Modifier.size(
                    width = 160.dp,
                    height = 120.dp,
                ),
            )
        """.trimIndent(),
        demo = {
            JengaImage(
                url = "https://example.com/photo.jpg",
                contentDescription = "Product photo",
                modifier = Modifier.size(
                    width = 160.dp,
                    height = 120.dp,
                ),
            )
        },
    ),
    CatalogEntry(
        name = "Scanner viewfinder",
        group = "Scanner",
        description = "A camera framing overlay with status.",
        keywords = "qr camera scan",
        code = """
            Box(
                modifier = Modifier.fillMaxWidth().height(
                    height = 220.dp,
                ).clip(
                    shape = JengaTheme.shapes.lg,
                ).background(
                    color = Color.Black,
                ),
                content = {
                    JengaScannerViewfinder(
                        status = JengaScannerStatus.Scanning,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.md,
                content = {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(
                            height = 200.dp,
                        ).clip(
                            shape = JengaTheme.shapes.lg,
                        ).background(
                            color = Color.Black,
                        ),
                        content = {
                            JengaScannerViewfinder(
                                status = JengaScannerStatus.Scanning,
                            )
                        },
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth().height(
                            height = 200.dp,
                        ).clip(
                            shape = JengaTheme.shapes.lg,
                        ).background(
                            color = Color.Black,
                        ),
                        content = {
                            JengaScanFeedback(
                                status = JengaScannerStatus.Success,
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Labelled progress",
        group = "Feedback",
        description = "A progress bar with a label row above it.",
        keywords = "progress step percent bar",
        code = """
            JengaLabelledProgress(
                progress = 0.6f,
                label = AnnotatedString(
                    text = "Repair · step 3 of 5",
                ),
                valueLabel = "60%",
            )
        """.trimIndent(),
        demo = {
            JengaLabelledProgress(
                progress = 0.6f,
                label = AnnotatedString(
                    text = "Repair · step 3 of 5",
                ),
                valueLabel = "60%",
            )
        },
    ),
    CatalogEntry(
        name = "Info bar",
        group = "Actions",
        description = "A button-shaped bar that says why no action is available.",
        keywords = "read only closed disabled view",
        code = """
            JengaInfoBar(
                text = "View only",
                leadingIcon = {
                    JengaIcon(
                        imageVector = JengaIcons.Lock,
                        contentDescription = null,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaInfoBar(
                text = "View only",
                leadingIcon = {
                    JengaIcon(
                        imageVector = JengaIcons.Lock,
                        contentDescription = null,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Card footer",
        group = "Containers",
        description = "A quiet strip at the bottom of a card.",
        keywords = "card footer meta sub row",
        code = """
            JengaCard(
                variant = JengaCardVariant.Outlined,
                contentPadding = PaddingValues(),
                content = {
                    JengaText(
                        text = "Battery replacement",
                        modifier = Modifier.padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                    JengaCardFooter(
                        text = "Waiting for parts since Monday",
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaCard(
                variant = JengaCardVariant.Outlined,
                contentPadding = PaddingValues(),
                content = {
                    JengaText(
                        text = "Battery replacement",
                        modifier = Modifier.padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                    JengaCardFooter(
                        text = "Waiting for parts since Monday",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Navigation rail item",
        group = "Navigation",
        description = "A destination row for a side navigation list.",
        keywords = "rail side nav drawer landscape",
        code = """
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaNavigationRailItem(
                        label = "Tickets",
                        selected = selected == 0,
                        onClick = { selected = 0 },
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Ticket,
                                contentDescription = null,
                            )
                        },
                    )
                    JengaNavigationRailItem(
                        label = "Work orders",
                        selected = selected == 1,
                        onClick = { selected = 1 },
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Wrench,
                                contentDescription = null,
                            )
                        },
                        showDot = true,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var selected by remember {
                mutableIntStateOf(
                    value = 0,
                )
            }
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaNavigationRailItem(
                        label = "Tickets",
                        selected = selected == 0,
                        onClick = { selected = 0 },
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Ticket,
                                contentDescription = null,
                            )
                        },
                    )
                    JengaNavigationRailItem(
                        label = "Work orders",
                        selected = selected == 1,
                        onClick = { selected = 1 },
                        icon = {
                            JengaIcon(
                                imageVector = JengaIcons.Wrench,
                                contentDescription = null,
                            )
                        },
                        showDot = true,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Thumbnail strip",
        group = "Media",
        description = "A row of square thumbnails with an add tile.",
        keywords = "photos images attachments gallery add",
        code = """
            JengaThumbnailStrip(
                count = 3,
                onAdd = {},
                addContentDescription = "Add photo",
                thumbnail = {
                    JengaIcon(
                        imageVector = JengaIcons.Image,
                        contentDescription = null,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaThumbnailStrip(
                count = 3,
                onAdd = {},
                addContentDescription = "Add photo",
                thumbnail = {
                    JengaIcon(
                        imageVector = JengaIcons.Image,
                        contentDescription = null,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Timeline",
        group = "Data display",
        description = "Stages of a process, each marked done, current, stopped or to do.",
        keywords = "timeline stages steps history progress",
        code = """
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaTimelineItem(
                        state = JengaTimelineState.Done,
                        title = "Lead created",
                        position = JengaTimelinePosition.First,
                        subtitle = "12 Mar",
                    )
                    JengaTimelineItem(
                        state = JengaTimelineState.Current,
                        title = "Quotation",
                        position = JengaTimelinePosition.Middle,
                    )
                    JengaTimelineItem(
                        state = JengaTimelineState.ToDo,
                        title = "Installation",
                        position = JengaTimelinePosition.Last,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    JengaTimelineItem(
                        state = JengaTimelineState.Done,
                        title = "Lead created",
                        position = JengaTimelinePosition.First,
                        subtitle = "12 Mar",
                    )
                    JengaTimelineItem(
                        state = JengaTimelineState.Current,
                        title = "Quotation",
                        position = JengaTimelinePosition.Middle,
                    )
                    JengaTimelineItem(
                        state = JengaTimelineState.ToDo,
                        title = "Installation",
                        position = JengaTimelinePosition.Last,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Form layout",
        group = "Layout",
        description = "A form whose bottom bar stays above the keyboard.",
        keywords = "form keyboard ime submit fields",
        code = """
            var name by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaFormLayout(
                modifier = Modifier.height(
                    height = 240.dp,
                ),
                bottomBar = {
                    JengaButton(
                        text = "Save",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                },
                content = {
                    item {
                        JengaTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Customer name",
                        )
                    }
                },
            )
        """.trimIndent(),
        demo = {
            var name by remember {
                mutableStateOf(
                    value = "",
                )
            }
            JengaFormLayout(
                modifier = Modifier.height(
                    height = 240.dp,
                ),
                bottomBar = {
                    JengaButton(
                        text = "Save",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().padding(
                            all = JengaTheme.spacing.lg,
                        ),
                    )
                },
                content = {
                    item {
                        JengaTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Customer name",
                        )
                    }
                },
            )
        },
    ),
    CatalogEntry(
        name = "Side sheet",
        group = "Feedback",
        description = "A sheet that slides in from the end edge, for wide layouts.",
        keywords = "sheet side drawer landscape tablet",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaButton(
                text = "Side sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaSideSheet(
                    onDismissRequest = { open = false },
                    title = "Filters",
                    footer = {
                        JengaButton(
                            text = "Apply",
                            onClick = { open = false },
                            modifier = Modifier.weight(
                                weight = 1f,
                            ),
                        )
                    },
                    content = {
                        item {
                            JengaCheckboxListItem(
                                headline = "Open only",
                                checked = true,
                                onCheckedChange = {},
                            )
                        }
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Side sheet",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaSideSheet(
                    onDismissRequest = { open = false },
                    title = "Filters",
                    footer = {
                        JengaButton(
                            text = "Apply",
                            onClick = { open = false },
                            modifier = Modifier.weight(
                                weight = 1f,
                            ),
                        )
                    },
                    content = {
                        item {
                            JengaCheckboxListItem(
                                headline = "Open only",
                                checked = true,
                                onCheckedChange = {},
                            )
                        }
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Image viewer",
        group = "Media",
        description = "A full-screen viewer with zoom, for photos.",
        keywords = "photo image zoom fullscreen viewer",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaButton(
                text = "Image viewer",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaImageViewer(
                    onDismissRequest = { open = false },
                    title = "photo.jpg",
                    closeContentDescription = "Close",
                    content = {
                        JengaIcon(
                            imageVector = JengaIcons.Image,
                            contentDescription = null,
                        )
                    },
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Image viewer",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaImageViewer(
                    onDismissRequest = { open = false },
                    title = "photo.jpg",
                    closeContentDescription = "Close",
                    content = {
                        JengaIcon(
                            imageVector = JengaIcons.Image,
                            contentDescription = null,
                        )
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Image viewer with pages",
        group = "Media",
        description = "Swipe between several images, each zoomed on its own.",
        keywords = "viewer gallery pager swipe photos pages zoom",
        stage = CatalogStage.Overlay,
        code = """
            JengaImageViewer(
                pageCount = 3,
                onDismissRequest = { open = false },
                title = { page -> "Photo ${'$'}{page + 1} of 3" },
                closeContentDescription = "Close",
                bottomBar = { page, goToPage -> /* thumbnail strip calling goToPage(i) */ },
                pageContent = { page ->
                    JengaImage(
                        url = urls[page],
                        contentDescription = null,
                        fit = JengaImageFit.Contain,
                    )
                },
            )
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Open 3 photos",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaImageViewer(
                    pageCount = 3,
                    onDismissRequest = { open = false },
                    title = { page -> "Photo ${page + 1} of 3" },
                    closeContentDescription = "Close",
                    pageContent = { page ->
                        JengaText(
                            text = "Photo ${page + 1}",
                            color = JengaTheme.colors.onOverlay,
                        )
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Date picker",
        group = "Inputs",
        description = "A month calendar for picking one date.",
        keywords = "date calendar day picker",
        code = """
            var date by remember {
                mutableStateOf<LocalDate?>(
                    value = LocalDate(
                        year = 2026,
                        month = 3,
                        day = 18,
                    ),
                )
            }
            JengaDatePicker(
                selectedDate = date,
                onSelectedDateChange = { date = it },
                monthTitle = { yearMonth ->
                    val monthName = yearMonth.month.name.lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                    "${'$'}monthName ${'$'}{yearMonth.year}"
                },
                dayOfWeekLabel = {
                    it.name.take(
                        n = 2,
                    ).lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                },
                previousMonthContentDescription = "Previous month",
                nextMonthContentDescription = "Next month",
            )
        """.trimIndent(),
        demo = {
            var date by remember {
                mutableStateOf<LocalDate?>(
                    value = LocalDate(
                        year = 2026,
                        month = 3,
                        day = 18,
                    ),
                )
            }
            JengaDatePicker(
                selectedDate = date,
                onSelectedDateChange = { date = it },
                monthTitle = { yearMonth ->
                    val monthName = yearMonth.month.name.lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                    "$monthName ${yearMonth.year}"
                },
                dayOfWeekLabel = {
                    it.name.take(
                        n = 2,
                    ).lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                },
                previousMonthContentDescription = "Previous month",
                nextMonthContentDescription = "Next month",
            )
        },
    ),
    CatalogEntry(
        name = "Date picker dialog",
        group = "Inputs",
        description = "A date picker in a dialog; here limited to adults' birth dates, with a year grid behind the title.",
        keywords = "date calendar dialog modal range birth year",
        stage = CatalogStage.Overlay,
        code = """
            var open by remember {
                mutableStateOf(
                    value = false,
                )
            }
            JengaButton(
                text = "Pick a date",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaDatePickerDialog(
                    onDismissRequest = { open = false },
                    onConfirm = { open = false },
                    dateRange = LocalDate(
                        year = 1906,
                        month = 1,
                        day = 1,
                    )..LocalDate(
                        year = 2008,
                        month = 10,
                        day = 28,
                    ),
                    yearPickerLabel = { "Choose year, ${'$'}{it.year}" },
                    confirmLabel = "OK",
                    dismissLabel = "Cancel",
                    monthTitle = { yearMonth ->
                        val monthName = yearMonth.month.name.lowercase().replaceFirstChar(
                            transform = Char::uppercase,
                        )
                        "${'$'}monthName ${'$'}{yearMonth.year}"
                    },
                    dayOfWeekLabel = {
                        it.name.take(
                            n = 2,
                        ).lowercase().replaceFirstChar(
                            transform = Char::uppercase,
                        )
                    },
                    previousMonthContentDescription = "Previous month",
                    nextMonthContentDescription = "Next month",
                )
            }
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Pick a date",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaDatePickerDialog(
                    onDismissRequest = { open = false },
                    onConfirm = { open = false },
                    dateRange = LocalDate(
                        year = 1906,
                        month = 1,
                        day = 1,
                    )..LocalDate(
                        year = 2008,
                        month = 10,
                        day = 28,
                    ),
                    yearPickerLabel = { "Choose year, ${it.year}" },
                    confirmLabel = "OK",
                    dismissLabel = "Cancel",
                    monthTitle = { yearMonth ->
                        val monthName = yearMonth.month.name.lowercase().replaceFirstChar(
                            transform = Char::uppercase,
                        )
                        "$monthName ${yearMonth.year}"
                    },
                    dayOfWeekLabel = {
                        it.name.take(
                            n = 2,
                        ).lowercase().replaceFirstChar(
                            transform = Char::uppercase,
                        )
                    },
                    previousMonthContentDescription = "Previous month",
                    nextMonthContentDescription = "Next month",
                )
            }
        },
    ),
    CatalogEntry(
        name = "Calendar",
        group = "Inputs",
        description = "A paging month grid where every day cell is your own content.",
        keywords = "calendar month grid range custom",
        code = """
            val state =
                rememberJengaCalendarState(
                    startMonth = YearMonth(
                        year = 2026,
                        month = Month.JANUARY,
                    ),
                    endMonth = YearMonth(
                        year = 2026,
                        month = Month.DECEMBER,
                    ),
                )
            JengaCalendar(
                state = state,
                dayContent = { day ->
                    JengaCalendarDefaults.Day(
                        day = day,
                        selected = false,
                        enabled = true,
                        isToday = false,
                        onClick = {},
                    )
                },
            )
        """.trimIndent(),
        demo = {
            val state = rememberJengaCalendarState(
                startMonth = YearMonth(
                    year = 2026,
                    month = Month.JANUARY,
                ),
                endMonth = YearMonth(
                    year = 2026,
                    month = Month.DECEMBER,
                ),
            )
            JengaCalendar(
                state = state,
                dayContent = { day ->
                    JengaCalendarDefaults.Day(
                        day = day,
                        selected = false,
                        enabled = true,
                        isToday = false,
                        onClick = {},
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Wheel picker",
        group = "Inputs",
        description = "A scroll wheel for picking one option.",
        keywords = "wheel picker scroll spinner",
        code = """
            var index by remember {
                mutableIntStateOf(
                    value = 1,
                )
            }
            JengaWheelPicker(
                items = listOf(
                    "Morning",
                    "Afternoon",
                    "Evening",
                ),
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
                visibleItemCount = 3,
            )
        """.trimIndent(),
        demo = {
            var index by remember {
                mutableIntStateOf(
                    value = 1,
                )
            }
            JengaWheelPicker(
                items = listOf(
                    "Morning",
                    "Afternoon",
                    "Evening",
                ),
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
                visibleItemCount = 3,
            )
        },
    ),
    CatalogEntry(
        name = "Date of birth picker",
        group = "Inputs",
        description = "Day, month and year wheels for a date of birth.",
        keywords = "birthday dob date wheels age",
        code = """
            var birthday by remember {
                mutableStateOf(
                    value = LocalDate(
                        year = 1990,
                        month = 3,
                        day = 14,
                    ),
                )
            }
            JengaDateOfBirthPicker(
                value = birthday,
                onValueChange = { birthday = it },
                monthLabel = {
                    it.name.lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            var birthday by remember {
                mutableStateOf(
                    value = LocalDate(
                        year = 1990,
                        month = 3,
                        day = 14,
                    ),
                )
            }
            JengaDateOfBirthPicker(
                value = birthday,
                onValueChange = { birthday = it },
                monthLabel = {
                    it.name.lowercase().replaceFirstChar(
                        transform = Char::uppercase,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Bar chart",
        group = "Data display",
        description = "Vertical bars from a shared baseline, optionally selectable.",
        keywords = "chart bar graph data visualization month",
        code = """
            var selected by remember {
                mutableIntStateOf(
                    value = 1,
                )
            }
            val months =
                listOf(
                    JengaBarChartEntry(
                        label = "May",
                        value = 8200f,
                    ),
                    JengaBarChartEntry(
                        label = "Jun",
                        value = 12400f,
                    ),
                    JengaBarChartEntry(
                        label = "Jul",
                        value = 9100f,
                    ),
                )
            JengaBarChart(
                entries = months,
                barContentDescription = { "${'$'}{it.label}, ${'$'}{it.value}" },
                selectedIndex = selected,
                onSelect = { selected = it },
            )
        """.trimIndent(),
        demo = {
            var selected by remember {
                mutableIntStateOf(
                    value = 1,
                )
            }
            val months = listOf(
                JengaBarChartEntry(
                    label = "May",
                    value = 8200f,
                ),
                JengaBarChartEntry(
                    label = "Jun",
                    value = 12400f,
                ),
                JengaBarChartEntry(
                    label = "Jul",
                    value = 9100f,
                ),
            )
            JengaBarChart(
                entries = months,
                barContentDescription = { "${it.label}, ${it.value}" },
                selectedIndex = selected,
                onSelect = { selected = it },
            )
        },
    ),
    CatalogEntry(
        name = "Bottom sheet that asks before closing",
        group = "Feedback",
        description = "A sheet that refuses a drag, scrim tap or Back until the user confirms.",
        keywords = "sheet confirm dismiss discard unsaved signature",
        stage = CatalogStage.Overlay,
        code = """
            var askDiscard by remember {
                mutableStateOf(
                    value = false,
                )
            }
            var discard by remember {
                mutableStateOf(
                    value = false,
                )
            }
            val sheetState =
                rememberJengaSheetState(
                    confirmDismiss = { discard.also { if (!it) askDiscard = true } },
                )
            JengaBottomSheet(
                onDismissRequest = { open = false },
                sheetState = sheetState,
                gesturesEnabled = false,
                content = {
                    /* signature pad */
                },
            )
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            var askDiscard by remember {
                mutableStateOf(
                    value = false,
                )
            }
            var discard by remember {
                mutableStateOf(
                    value = false,
                )
            }
            val scope = rememberCoroutineScope()
            val sheetState = rememberJengaSheetState(
                confirmDismiss = {
                    if (!discard) askDiscard = true
                    discard
                },
            )
            JengaButton(
                text = "Sign",
                onClick = {
                    discard = false
                    open = true
                },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaBottomSheet(
                    onDismissRequest = { open = false },
                    sheetState = sheetState,
                    gesturesEnabled = false,
                    content = {
                        JengaStack(
                            modifier = Modifier.fillMaxWidth().padding(
                                all = JengaTheme.spacing.xl,
                            ),
                            content = {
                                JengaText(
                                    text = "Customer signature",
                                    style = JengaTheme.typography.titleLarge,
                                )
                                JengaText(
                                    text = "Drag, Back and the scrim ask first.",
                                    color = JengaTheme.colors.textMuted,
                                )
                            },
                        )
                    },
                )
            }
            if (askDiscard) {
                JengaDialog(
                    onDismissRequest = { askDiscard = false },
                    title = "Discard the signature?",
                    confirmButton = {
                        JengaButton(
                            text = "Discard",
                            onClick = {
                                askDiscard = false
                                discard = true
                                scope.launch { sheetState.hide() }
                            },
                        )
                    },
                    dismissButton = {
                        JengaButton(
                            text = "Keep",
                            onClick = { askDiscard = false },
                            variant = JengaButtonVariant.Ghost,
                        )
                    },
                )
            }
        },
    ),
    CatalogEntry(
        name = "Snackbar host",
        group = "Feedback",
        description = "Shows snackbars one at a time from a host state, usually in a scaffold.",
        keywords = "snackbar host toast message queue",
        code = """
            val host = rememberJengaSnackbarHostState()
            val scope = rememberCoroutineScope()
            JengaButton(
                text = "Save",
                onClick = {
                    scope.launch {
                        host.showSnackbar(
                            message = "Saved offline",
                        )
                    }
                },
            )
            JengaSnackbarHost(
                hostState = host,
            )
        """.trimIndent(),
        demo = {
            val host = rememberJengaSnackbarHostState()
            val scope = rememberCoroutineScope()
            JengaStack(
                space = JengaTheme.spacing.sm,
                content = {
                    JengaButton(
                        text = "Save",
                        onClick = {
                            scope.launch {
                                host.showSnackbar(
                                    message = "Saved offline",
                                )
                            }
                        },
                        variant = JengaButtonVariant.Outline,
                    )
                    JengaSnackbarHost(
                        hostState = host,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Timeline with your own markers",
        group = "Data display",
        description = "Timeline rows with any marker and any content; positions worked out from the list.",
        keywords = "timeline marker steps numbered history log slot",
        code = """
            val stages = listOf(
                "Deposit paid",
                "Installed",
                "Handover",
            )
            stages.forEachIndexed { index, stage ->
                JengaTimelineItem(
                    position = JengaTimelinePosition.of(
                        index = index,
                        count = stages.size,
                    ),
                    marker = {
                        JengaTimelineMarker(
                            color = JengaTheme.colors.brand,
                            content = {
                                JengaText(
                                    text = "${'$'}{index + 1}",
                                )
                            },
                        )
                    },
                    content = {
                        JengaText(
                            text = stage,
                        )
                    },
                )
            }
        """.trimIndent(),
        demo = {
            val stages = listOf(
                "Deposit paid",
                "Installed",
                "Handover",
            )
            JengaStack(
                space = JengaTheme.spacing.none,
                content = {
                    stages.forEachIndexed { index, stage ->
                        JengaTimelineItem(
                            position = JengaTimelinePosition.of(
                                index = index,
                                count = stages.size,
                            ),
                            marker = {
                                JengaTimelineMarker(
                                    color = JengaTheme.colors.brand,
                                    style = if (index == 2) JengaTimelineMarkerStyle.Ring else JengaTimelineMarkerStyle.Filled,
                                    content = {
                                        JengaText(
                                            text = "${index + 1}",
                                            style = JengaTheme.typography.caption,
                                        )
                                    },
                                )
                            },
                            content = {
                                JengaText(
                                    text = stage,
                                )
                            },
                        )
                    }
                },
            )
        },
    ),
    CatalogEntry(
        name = "Step indicator",
        group = "Navigation",
        description = "A row of steps for a multi-step flow: done, current, pending or locked.",
        keywords = "steps stepper progress wizard flow checkout",
        code = """
            val steps =
                listOf(
                    JengaStep(
                        label = "Details",
                        state = JengaStepState.Done,
                    ),
                    JengaStep(
                        label = "Choices",
                        state = JengaStepState.Current,
                    ),
                    JengaStep(
                        label = "Review",
                        state = JengaStepState.Pending,
                    ),
                )
            JengaStepIndicator(
                steps = steps,
                stepContentDescription = { index, step ->
                    "Step ${'$'}{index + 1} of 3, ${'$'}{step.label}"
                },
            )
        """.trimIndent(),
        demo = {
            val steps = listOf(
                JengaStep(
                    label = "Details",
                    state = JengaStepState.Done,
                ),
                JengaStep(
                    label = "Choices",
                    state = JengaStepState.Current,
                ),
                JengaStep(
                    label = "Review",
                    state = JengaStepState.Pending,
                ),
            )
            JengaStepIndicator(
                steps = steps,
                stepContentDescription = { index, step -> "Step ${index + 1} of 3, ${step.label}" },
            )
        },
    ),
    CatalogEntry(
        name = "Time input",
        group = "Inputs",
        description = "Hour and minute as scroll wheels or typed fields, with AM / PM.",
        keywords = "time clock hour minute am pm wheel picker",
        code = """
            var time by remember {
                mutableStateOf(
                    value = LocalTime(
                        hour = 9,
                        minute = 30,
                    ),
                )
            }
            JengaTimeInput(
                time = time,
                onTimeChange = { time = it },
                hourLabel = "Hour",
                minuteLabel = "Minute",
                amLabel = "AM",
                pmLabel = "PM",
            )
            JengaTimeInput(
                time = time,
                onTimeChange = { time = it },
                hourLabel = "Hour",
                minuteLabel = "Minute",
                amLabel = "AM",
                pmLabel = "PM",
                style = JengaTimeInputStyle.Fields,
            )
        """.trimIndent(),
        demo = {
            var time by remember {
                mutableStateOf(
                    value = LocalTime(
                        hour = 9,
                        minute = 30,
                    ),
                )
            }
            JengaStack(
                space = JengaTheme.spacing.lg,
                content = {
                    JengaTimeInput(
                        time = time,
                        onTimeChange = { time = it },
                        hourLabel = "Hour",
                        minuteLabel = "Minute",
                        amLabel = "AM",
                        pmLabel = "PM",
                    )
                    JengaTimeInput(
                        time = time,
                        onTimeChange = { time = it },
                        hourLabel = "Hour",
                        minuteLabel = "Minute",
                        amLabel = "AM",
                        pmLabel = "PM",
                        style = JengaTimeInputStyle.Fields,
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Time picker dialog",
        group = "Inputs",
        description = "A time input in a dialog with confirm and dismiss.",
        keywords = "time dialog modal clock",
        stage = CatalogStage.Overlay,
        code = """
            JengaTimePickerDialog(
                onDismissRequest = { open = false },
                onConfirm = { open = false },
                title = "Delivery time",
                confirmLabel = "OK",
                dismissLabel = "Cancel",
                initialTime = LocalTime(
                    hour = 14,
                    minute = 0,
                ),
                hourLabel = "Hour",
                minuteLabel = "Minute",
                amLabel = "AM",
                pmLabel = "PM",
            )
        """.trimIndent(),
        demo = { startOpen ->
            var open by remember {
                mutableStateOf(
                    value = startOpen,
                )
            }
            JengaButton(
                text = "Pick a time",
                onClick = { open = true },
                variant = JengaButtonVariant.Outline,
            )
            if (open) {
                JengaTimePickerDialog(
                    onDismissRequest = { open = false },
                    onConfirm = { open = false },
                    title = "Delivery time",
                    confirmLabel = "OK",
                    dismissLabel = "Cancel",
                    initialTime = LocalTime(
                        hour = 14,
                        minute = 0,
                    ),
                    hourLabel = "Hour",
                    minuteLabel = "Minute",
                    amLabel = "AM",
                    pmLabel = "PM",
                )
            }
        },
    ),
    CatalogEntry(
        name = "Capture frame",
        group = "Scanner",
        description = "A guide over a camera preview for a face, card or page, with hold progress.",
        keywords = "camera capture selfie id card document kyc frame oval",
        code = """
            Box(
                modifier = Modifier.fillMaxSize(),
                content = {
                    /* camera preview */
                    JengaCaptureFrame(
                        shape = JengaCaptureShape.Rectangle(
                            aspectRatio = 1.586f,
                        ),
                        modifier = Modifier.fillMaxSize(),
                        ready = ready,
                        holdProgress = progress,
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaInline(
                space = JengaTheme.spacing.sm,
                content = {
                    Box(
                        modifier = Modifier.size(
                            width = 120.dp,
                            height = 200.dp,
                        ).background(
                            color = Color(
                                color = 0xFF6B6F76,
                            ),
                        ),
                        content = {
                            JengaCaptureFrame(
                                shape = JengaCaptureShape.Oval,
                                modifier = Modifier.fillMaxSize(),
                                holdProgress = 0.6f,
                            )
                        },
                    )
                    Box(
                        modifier = Modifier.size(
                            width = 120.dp,
                            height = 200.dp,
                        ).background(
                            color = Color(
                                color = 0xFF6B6F76,
                            ),
                        ),
                        content = {
                            JengaCaptureFrame(
                                shape = JengaCaptureShape.Rectangle(
                                    aspectRatio = 1.586f,
                                ),
                                modifier = Modifier.fillMaxSize(),
                                ready = true,
                            )
                        },
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Shutter button",
        group = "Scanner",
        description = "The round take-photo button for a camera screen.",
        keywords = "camera shutter capture photo take",
        code = """
            JengaShutterButton(
                onClick = {},
                contentDescription = "Take photo",
            )
        """.trimIndent(),
        demo = {
            Box(
                modifier = Modifier.background(
                    color = Color.Black,
                ).padding(
                    all = JengaTheme.spacing.md,
                ),
                content = {
                    JengaShutterButton(
                        onClick = {},
                        contentDescription = "Take photo",
                    )
                },
            )
        },
    ),
    CatalogEntry(
        name = "Theme component styles",
        group = "Layout",
        description = "Set a component's look once in the theme; a value passed to a call still wins.",
        keywords = "theme style components shape default override brand",
        code = """
            JengaTheme(
                components =
                JengaComponentStyles(
                    button =
                    JengaButtonStyle(
                        medium = JengaButtonSizeStyle(
                            shape = RoundedCornerShape(
                                size = 4.dp,
                            ),
                        ),
                    ),
                ),
                content = {
                    JengaButton(
                        text = "Square corners",
                        onClick = {},
                    )
                },
            )
        """.trimIndent(),
        demo = {
            JengaTheme(
                colors = JengaTheme.colors,
                components = JengaComponentStyles(
                    button = JengaButtonStyle(
                        medium = JengaButtonSizeStyle(
                            shape = RoundedCornerShape(
                                size = 4.dp,
                            ),
                        ),
                    ),
                ),
                content = {
                    JengaButton(
                        text = "Square corners",
                        onClick = {},
                    )
                },
            )
        },
    ),
)
