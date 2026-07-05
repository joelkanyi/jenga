package io.github.joelkanyi.jenga.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import io.github.joelkanyi.jenga.foundation.color.JengaColors
import io.github.joelkanyi.jenga.foundation.color.JengaPalette
import io.github.joelkanyi.jenga.foundation.shape.JengaShapes
import io.github.joelkanyi.jenga.foundation.typography.JengaTypography

/**
 * Internal Material 3 bridge.
 *
 * Jenga's public API is fully custom, but we still render on top of Compose
 * Material 3 primitives (ripples, text-selection handles, and the occasional
 * reused M3 component). Those read from [MaterialTheme], so we map Jenga tokens
 * onto an M3 [ColorScheme]/[Typography]/[Shapes] and provide it *underneath*
 * Jenga's own CompositionLocals. This keeps M3 internals on-brand without ever
 * exposing M3 in Jenga's API. None of this is public.
 */
internal fun JengaColors.toMaterialColorScheme(): ColorScheme {
    val onError = if (isLight) JengaPalette.White else JengaPalette.Ink900
    return if (isLight) {
        lightColorScheme(
            primary = brand,
            onPrimary = onBrand,
            primaryContainer = brandSubtle,
            onPrimaryContainer = onBrandSubtle,
            secondary = ink,
            onSecondary = onInk,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceSunk,
            onSurfaceVariant = textSecondary,
            error = error,
            onError = onError,
            errorContainer = errorContainer,
            onErrorContainer = onErrorContainer,
            outline = borderStrong,
            outlineVariant = border,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            scrim = scrim,
        )
    } else {
        darkColorScheme(
            primary = brand,
            onPrimary = onBrand,
            primaryContainer = brandSubtle,
            onPrimaryContainer = onBrandSubtle,
            secondary = ink,
            onSecondary = onInk,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceSunk,
            onSurfaceVariant = textSecondary,
            error = error,
            onError = onError,
            errorContainer = errorContainer,
            onErrorContainer = onErrorContainer,
            outline = borderStrong,
            outlineVariant = border,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            scrim = scrim,
        )
    }
}

internal fun JengaTypography.toMaterialTypography(): Typography = Typography(
    displayLarge = display,
    displayMedium = headingLarge,
    displaySmall = headingMedium,
    headlineLarge = headingLarge,
    headlineMedium = headingMedium,
    headlineSmall = headingSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = button,
    labelMedium = label,
    labelSmall = caption,
)

internal fun JengaShapes.toMaterialShapes(): Shapes = Shapes(
    extraSmall = xs,
    small = sm,
    medium = md,
    large = lg,
    extraLarge = xl,
)
