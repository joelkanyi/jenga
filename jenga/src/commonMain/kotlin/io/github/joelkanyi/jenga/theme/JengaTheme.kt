package io.github.joelkanyi.jenga.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.foundation.color.jengaDarkColors
import io.github.joelkanyi.jenga.foundation.color.jengaLightColors
import io.github.joelkanyi.jenga.foundation.elevation.JengaElevation
import io.github.joelkanyi.jenga.foundation.elevation.jengaElevation
import io.github.joelkanyi.jenga.foundation.motion.JengaMotion
import io.github.joelkanyi.jenga.foundation.motion.jengaMotion
import io.github.joelkanyi.jenga.foundation.shape.JengaShapes
import io.github.joelkanyi.jenga.foundation.shape.jengaShapes
import io.github.joelkanyi.jenga.foundation.sizing.JengaSizing
import io.github.joelkanyi.jenga.foundation.sizing.jengaSizing
import io.github.joelkanyi.jenga.foundation.spacing.JengaSpacing
import io.github.joelkanyi.jenga.foundation.spacing.jengaSpacing
import io.github.joelkanyi.jenga.foundation.typography.JengaTypography
import io.github.joelkanyi.jenga.foundation.typography.jengaTypography
import io.github.joelkanyi.jenga.foundation.typography.rememberJengaFontFamily

// staticCompositionLocalOf: the theme changes rarely (only on light/dark or brand
// swap), so we trade fine-grained invalidation for cheaper reads. Defaults keep
// components renderable in isolation (e.g. @Preview) without an explicit theme.
internal val LocalJengaColors = staticCompositionLocalOf { jengaLightColors() }
internal val LocalJengaTypography = staticCompositionLocalOf { jengaTypography() }
internal val LocalJengaSpacing = staticCompositionLocalOf { jengaSpacing() }
internal val LocalJengaShapes = staticCompositionLocalOf { jengaShapes() }
internal val LocalJengaSizing = staticCompositionLocalOf { jengaSizing() }
internal val LocalJengaElevation = staticCompositionLocalOf { jengaElevation() }
internal val LocalJengaMotion = staticCompositionLocalOf { jengaMotion() }

/**
 * The preferred content color (text, icons) for the current subtree, mirroring
 * Material's `LocalContentColor`. Components set this so their slot content
 * inherits the right color (e.g. text inside a primary button becomes `onBrand`).
 * [Color.Unspecified] means "fall back to the theme" — see [JengaText].
 */
public val LocalJengaContentColor: ProvidableCompositionLocal<Color> =
    compositionLocalOf { Color.Unspecified }

/**
 * Entry point to Jenga's design tokens, mirroring how `MaterialTheme` exposes
 * `colorScheme`/`typography`/`shapes`.
 *
 * Read tokens from any composable inside [JengaTheme]:
 * ```
 * Text(color = JengaTheme.colors.textPrimary, style = JengaTheme.typography.bodyMedium)
 * Spacer(Modifier.height(JengaTheme.spacing.lg))
 * ```
 */
public object JengaTheme {
    public val colors: JengaColors
        @Composable @ReadOnlyComposable get() = LocalJengaColors.current

    public val typography: JengaTypography
        @Composable @ReadOnlyComposable get() = LocalJengaTypography.current

    public val spacing: JengaSpacing
        @Composable @ReadOnlyComposable get() = LocalJengaSpacing.current

    public val shapes: JengaShapes
        @Composable @ReadOnlyComposable get() = LocalJengaShapes.current

    public val sizing: JengaSizing
        @Composable @ReadOnlyComposable get() = LocalJengaSizing.current

    public val elevation: JengaElevation
        @Composable @ReadOnlyComposable get() = LocalJengaElevation.current

    public val motion: JengaMotion
        @Composable @ReadOnlyComposable get() = LocalJengaMotion.current
}

/**
 * Provides the Jenga design system to [content].
 *
 * Wrap your app (or any subtree) once; everything below can then read tokens via
 * [JengaTheme] and use `Jenga*` components. Every parameter has a sensible
 * Ticketfiti default, so the simplest usage is just `JengaTheme { ... }`.
 *
 * Rebrand or white-label by supplying your own tokens, e.g.
 * `JengaTheme(colors = jengaLightColors().copy(brand = Color(0xFF6D28D9))) { ... }`.
 *
 * A Material 3 theme is provided internally (mapped from these tokens) so M3
 * primitives such as ripples and text selection stay on-brand — but it is an
 * implementation detail and not part of Jenga's API.
 *
 * @param darkTheme whether to use the dark scheme; follows the system by default.
 * @param colors semantic colors; defaults to the Ticketfiti light/dark scheme
 *   based on [darkTheme].
 * @param typography the type scale.
 * @param spacing the spacing scale.
 * @param shapes the corner-shape set.
 * @param sizing the sizing set (touch targets, icon/control sizes).
 * @param elevation the elevation ladder.
 * @param motion the motion tokens.
 * @param content the themed content.
 */
@Composable
public fun JengaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: JengaColors = if (darkTheme) jengaDarkColors() else jengaLightColors(),
    typography: JengaTypography? = null,
    spacing: JengaSpacing = jengaSpacing(),
    shapes: JengaShapes = jengaShapes(),
    sizing: JengaSizing = jengaSizing(),
    elevation: JengaElevation = jengaElevation(),
    motion: JengaMotion = jengaMotion(),
    content: @Composable () -> Unit,
) {
    // Resolve the default type scale inside composition so the Outfit brand font
    // (loaded via Compose Resources) can be attached; callers may still pass their
    // own fully-built typography.
    val resolvedTypography = typography ?: jengaTypography(rememberJengaFontFamily())
    CompositionLocalProvider(
        LocalJengaColors provides colors,
        LocalJengaTypography provides resolvedTypography,
        LocalJengaSpacing provides spacing,
        LocalJengaShapes provides shapes,
        LocalJengaSizing provides sizing,
        LocalJengaElevation provides elevation,
        LocalJengaMotion provides motion,
        LocalJengaContentColor provides colors.textPrimary,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = resolvedTypography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content,
        )
    }
}
