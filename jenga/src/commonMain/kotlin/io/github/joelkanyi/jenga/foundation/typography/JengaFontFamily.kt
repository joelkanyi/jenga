package io.github.joelkanyi.jenga.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.joelkanyi.jenga.resources.Res
import io.github.joelkanyi.jenga.resources.outfit_bold
import io.github.joelkanyi.jenga.resources.outfit_extrabold
import io.github.joelkanyi.jenga.resources.outfit_medium
import io.github.joelkanyi.jenga.resources.outfit_regular
import io.github.joelkanyi.jenga.resources.outfit_semibold
import org.jetbrains.compose.resources.Font

/**
 * The **Outfit** typeface, Jenga's default brand font, bundled with the library
 * so consumers get it automatically.
 *
 * Loaded through Compose Resources, so it must be resolved inside composition.
 * [JengaTheme][io.github.joelkanyi.jenga.theme.JengaTheme] does this for you; call
 * it directly only when building a custom typography scale.
 *
 * Weights map to the five shipped files: Normal(400), Medium(500), SemiBold(600),
 * Bold(700), ExtraBold(800).
 */
@Composable
public fun rememberJengaFontFamily(): FontFamily = FontFamily(
    Font(Res.font.outfit_regular, FontWeight.Normal),
    Font(Res.font.outfit_medium, FontWeight.Medium),
    Font(Res.font.outfit_semibold, FontWeight.SemiBold),
    Font(Res.font.outfit_bold, FontWeight.Bold),
    Font(Res.font.outfit_extrabold, FontWeight.ExtraBold),
)
