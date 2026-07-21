package io.github.joelkanyi.jenga.core.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview for jenga blocks — renders each showcase in light, dark and at a
 * large font scale, so Roborazzi captures all three from a single annotated
 * function, covering light/dark **and** sizes, not just one.
 *
 * Apply to an `internal` showcase that wraps content in `JengaTheme { ... }`
 * (no explicit `darkTheme`), so the "Dark" entry's night [Configuration] flips
 * `isSystemInDarkTheme()` and the theme follows.
 */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", showBackground = true, fontScale = 1.5f)
// Branded name kept deliberately; the Preview* prefix the rule wants would rename
// this internal annotation across every showcase file.
@Suppress("ktlint:compose:preview-annotation-naming")
internal annotation class JengaBlockPreviews
