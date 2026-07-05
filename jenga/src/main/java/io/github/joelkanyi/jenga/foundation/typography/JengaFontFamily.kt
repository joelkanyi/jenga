package io.github.joelkanyi.jenga.foundation.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.joelkanyi.jenga.R

/**
 * The **Outfit** typeface — Ticketfiti's brand font, shared with the web
 * frontend. Bundled with Jenga so consumers get the font automatically.
 *
 * Weights map to the five shipped files: Normal(400), Medium(500),
 * SemiBold(600), Bold(700), ExtraBold(800).
 */
public val JengaFontFamily: FontFamily = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold),
    Font(R.font.outfit_extrabold, FontWeight.ExtraBold),
)
