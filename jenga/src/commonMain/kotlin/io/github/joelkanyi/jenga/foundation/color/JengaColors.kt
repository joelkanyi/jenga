package io.github.joelkanyi.jenga.foundation.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import dev.drewhamilton.poko.Poko

/**
 * **Semantic color layer**: colors named by *role*, not by hue.
 *
 * This is the public color contract of Jenga. Components read these roles
 * (e.g. [brand], [surface], [textPrimary]) and never the raw [JengaPalette].
 * Because roles are theme-independent in *name* but theme-dependent in *value*,
 * the same component renders correctly in light and dark simply by swapping the
 * [JengaColors] instance ([jengaLightColors] vs [jengaDarkColors]).
 *
 * Rebrand or white-label by passing a `copy(...)` of one of the factories to
 * `JengaTheme(colors = ...)`.
 *
 * Status roles follow a `content` / `container` / `onContainer` triad:
 * - `success`: the vivid accent (dots, icons, borders, emphasis)
 * - `successContainer`: a tinted background for banners/badges
 * - `onSuccessContainer`: readable text/icon color on that container
 *
 * @property isLight whether this palette targets a light background. Components
 *   may branch on this for effects that can't be expressed as a single token
 *   (e.g. choosing an elevation overlay strategy).
 */
@Poko
@Immutable
public class JengaColors(
    // Brand: primary accent (orange)
    public val brand: Color,
    public val onBrand: Color,
    public val brandSubtle: Color,
    public val onBrandSubtle: Color,
    // Ink: secondary dark action (navy)
    public val ink: Color,
    public val onInk: Color,
    // Backgrounds & surfaces
    public val background: Color,
    public val surface: Color,
    public val surfaceVariant: Color,
    public val surfaceSunk: Color,
    // Content (text / icons)
    public val textPrimary: Color,
    public val textSecondary: Color,
    public val textMuted: Color,
    public val textFaint: Color,
    // Lines
    public val border: Color,
    public val borderStrong: Color,
    // Disabled states
    public val contentDisabled: Color,
    public val surfaceDisabled: Color,
    public val borderDisabled: Color,
    // Inverse: high-contrast surfaces (snackbars, tooltips)
    public val inverseSurface: Color,
    public val inverseOnSurface: Color,
    // Status: success
    public val success: Color,
    public val successContainer: Color,
    public val onSuccessContainer: Color,
    // Status: warning
    public val warning: Color,
    public val warningContainer: Color,
    public val onWarningContainer: Color,
    // Status: error
    public val error: Color,
    public val errorContainer: Color,
    public val onErrorContainer: Color,
    // Status: info
    public val info: Color,
    public val infoContainer: Color,
    public val onInfoContainer: Color,
    // Media overlays: content layered over a camera feed / photo / video, whose
    // backdrop is unpredictable. These are an *always dark-context* set, identical
    // in light and dark so a scanner or image viewer stays legible either way.
    public val overlaySurface: Color,
    public val onOverlay: Color,
    public val onOverlayMuted: Color,
    // Utility
    public val focusRing: Color,
    public val scrim: Color,
    public val isLight: Boolean,
) {
    public fun copy(
        brand: Color = this.brand,
        onBrand: Color = this.onBrand,
        brandSubtle: Color = this.brandSubtle,
        onBrandSubtle: Color = this.onBrandSubtle,
        ink: Color = this.ink,
        onInk: Color = this.onInk,
        background: Color = this.background,
        surface: Color = this.surface,
        surfaceVariant: Color = this.surfaceVariant,
        surfaceSunk: Color = this.surfaceSunk,
        textPrimary: Color = this.textPrimary,
        textSecondary: Color = this.textSecondary,
        textMuted: Color = this.textMuted,
        textFaint: Color = this.textFaint,
        border: Color = this.border,
        borderStrong: Color = this.borderStrong,
        contentDisabled: Color = this.contentDisabled,
        surfaceDisabled: Color = this.surfaceDisabled,
        borderDisabled: Color = this.borderDisabled,
        inverseSurface: Color = this.inverseSurface,
        inverseOnSurface: Color = this.inverseOnSurface,
        success: Color = this.success,
        successContainer: Color = this.successContainer,
        onSuccessContainer: Color = this.onSuccessContainer,
        warning: Color = this.warning,
        warningContainer: Color = this.warningContainer,
        onWarningContainer: Color = this.onWarningContainer,
        error: Color = this.error,
        errorContainer: Color = this.errorContainer,
        onErrorContainer: Color = this.onErrorContainer,
        info: Color = this.info,
        infoContainer: Color = this.infoContainer,
        onInfoContainer: Color = this.onInfoContainer,
        overlaySurface: Color = this.overlaySurface,
        onOverlay: Color = this.onOverlay,
        onOverlayMuted: Color = this.onOverlayMuted,
        focusRing: Color = this.focusRing,
        scrim: Color = this.scrim,
        isLight: Boolean = this.isLight,
    ): JengaColors = JengaColors(
        brand,
        onBrand,
        brandSubtle,
        onBrandSubtle,
        ink,
        onInk,
        background,
        surface,
        surfaceVariant,
        surfaceSunk,
        textPrimary,
        textSecondary,
        textMuted,
        textFaint,
        border,
        borderStrong,
        contentDisabled,
        surfaceDisabled,
        borderDisabled,
        inverseSurface,
        inverseOnSurface,
        success,
        successContainer,
        onSuccessContainer,
        warning,
        warningContainer,
        onWarningContainer,
        error,
        errorContainer,
        onErrorContainer,
        info,
        infoContainer,
        onInfoContainer,
        overlaySurface,
        onOverlay,
        onOverlayMuted,
        focusRing,
        scrim,
        isLight,
    )
}

/** The default Ticketfiti **light** color scheme. */
public fun jengaLightColors(): JengaColors = JengaColors(
    brand = JengaPalette.Orange500,
    onBrand = JengaPalette.White,
    brandSubtle = JengaPalette.Orange50,
    onBrandSubtle = JengaPalette.Orange700,
    ink = JengaPalette.Ink900,
    onInk = JengaPalette.White,
    background = JengaPalette.Neutral50,
    surface = JengaPalette.White,
    surfaceVariant = JengaPalette.Neutral50,
    surfaceSunk = JengaPalette.Neutral100,
    textPrimary = JengaPalette.Neutral700,
    textSecondary = JengaPalette.Neutral600,
    textMuted = JengaPalette.Neutral500,
    textFaint = JengaPalette.Neutral400,
    border = JengaPalette.Neutral200,
    borderStrong = JengaPalette.Neutral300,
    contentDisabled = JengaPalette.Neutral400,
    surfaceDisabled = JengaPalette.Neutral100,
    borderDisabled = JengaPalette.Neutral200,
    inverseSurface = JengaPalette.Ink900,
    inverseOnSurface = JengaPalette.DarkTextPrimary,
    success = JengaPalette.Green500,
    successContainer = JengaPalette.GreenContainerLight,
    onSuccessContainer = JengaPalette.Green700,
    warning = JengaPalette.Amber500,
    warningContainer = JengaPalette.AmberContainerLight,
    onWarningContainer = JengaPalette.Amber700,
    error = JengaPalette.Red500,
    errorContainer = JengaPalette.RedContainerLight,
    onErrorContainer = JengaPalette.Red700,
    info = JengaPalette.Blue500,
    infoContainer = JengaPalette.BlueContainerLight,
    onInfoContainer = JengaPalette.Blue700,
    overlaySurface = JengaPalette.Black.copy(alpha = 0.55f),
    onOverlay = JengaPalette.White,
    onOverlayMuted = JengaPalette.White.copy(alpha = 0.72f),
    focusRing = JengaPalette.Orange500.copy(alpha = 0.22f),
    scrim = JengaPalette.Ink900.copy(alpha = 0.55f),
    isLight = true,
)

/** The default Ticketfiti **dark** color scheme. */
public fun jengaDarkColors(): JengaColors = JengaColors(
    brand = JengaPalette.Orange500,
    onBrand = JengaPalette.White,
    brandSubtle = JengaPalette.Orange500.copy(alpha = 0.16f),
    onBrandSubtle = JengaPalette.Orange300,
    ink = JengaPalette.White,
    onInk = JengaPalette.Ink900,
    background = JengaPalette.DarkBg,
    surface = JengaPalette.DarkSurface,
    surfaceVariant = JengaPalette.DarkSurfaceVariant,
    surfaceSunk = JengaPalette.DarkSurfaceSunk,
    textPrimary = JengaPalette.DarkTextPrimary,
    textSecondary = JengaPalette.DarkTextSecondary,
    textMuted = JengaPalette.DarkTextMuted,
    textFaint = JengaPalette.DarkTextFaint,
    border = JengaPalette.DarkBorder,
    borderStrong = JengaPalette.DarkBorderStrong,
    contentDisabled = JengaPalette.DarkTextFaint,
    surfaceDisabled = JengaPalette.DarkSurfaceSunk,
    borderDisabled = JengaPalette.DarkBorder,
    inverseSurface = JengaPalette.DarkTextPrimary,
    inverseOnSurface = JengaPalette.Ink900,
    success = JengaPalette.Green300,
    successContainer = JengaPalette.Green500.copy(alpha = 0.12f),
    onSuccessContainer = JengaPalette.Green300,
    warning = JengaPalette.Amber300,
    warningContainer = JengaPalette.Amber500.copy(alpha = 0.12f),
    onWarningContainer = JengaPalette.Amber300,
    error = JengaPalette.Red300,
    errorContainer = JengaPalette.Red500.copy(alpha = 0.12f),
    onErrorContainer = JengaPalette.Red300,
    info = JengaPalette.Blue300,
    infoContainer = JengaPalette.Blue500.copy(alpha = 0.14f),
    onInfoContainer = JengaPalette.Blue300,
    overlaySurface = JengaPalette.Black.copy(alpha = 0.55f),
    onOverlay = JengaPalette.White,
    onOverlayMuted = JengaPalette.White.copy(alpha = 0.72f),
    focusRing = JengaPalette.Orange400.copy(alpha = 0.30f),
    scrim = JengaPalette.Black.copy(alpha = 0.66f),
    isLight = false,
)

/**
 * Multi-brand convenience: returns a copy of this scheme rebranded to [brand].
 * The semantic structure (surfaces, text, lines, status) is untouched; only the
 * brand accent and its subtle/on pairs change.
 *
 * ```
 * JengaTheme(colors = jengaLightColors().withBrand(Color(0xFF6D28D9))) { … }
 * ```
 */
public fun JengaColors.withBrand(
    brand: Color,
    onBrand: Color = this.onBrand,
    brandSubtle: Color = this.brandSubtle,
    onBrandSubtle: Color = this.onBrandSubtle,
): JengaColors = copy(
    brand = brand,
    onBrand = onBrand,
    brandSubtle = brandSubtle,
    onBrandSubtle = onBrandSubtle,
)
