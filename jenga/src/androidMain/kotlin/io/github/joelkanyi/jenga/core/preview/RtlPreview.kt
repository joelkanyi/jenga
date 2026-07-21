package io.github.joelkanyi.jenga.core.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Renders [content] in a right-to-left layout direction, for RTL screenshot
 * previews. Used instead of `@Preview(locale = "ar")` because the preview
 * `locale` knob does not flip layout direction under the Roborazzi/scanner
 * harness; forcing [LocalLayoutDirection] does.
 */
@Composable
internal fun RtlPreview(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}
