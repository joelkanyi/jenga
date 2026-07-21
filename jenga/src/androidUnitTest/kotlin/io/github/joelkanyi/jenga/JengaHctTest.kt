package io.github.joelkanyi.jenga

import io.github.joelkanyi.jenga.foundation.color.hct.ColorUtils
import io.github.joelkanyi.jenga.foundation.color.hct.Hct
import io.github.joelkanyi.jenga.foundation.color.hct.TonalPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Locks the vendored HCT color math to known-correct values, so a transcription
 * error or a future re-sync that breaks the algorithm fails the build. Values
 * come from the reference implementation's own test vectors.
 */
class JengaHctTest {

    private val colors = listOf(
        0xFFFF0000.toInt(), // red
        0xFF00FF00.toInt(), // green
        0xFF0000FF.toInt(), // blue
        0xFFFFFFFF.toInt(), // white
        0xFF000000.toInt(), // black
        0xFF6D28D9.toInt(), // a brand purple
        0xFFF97316.toInt(), // a brand orange
        0xFF808080.toInt(), // mid grey
    )

    @Test
    fun `HCT round-trips sRGB colors exactly`() {
        for (argb in colors) {
            assertEquals(argb, Hct.fromInt(argb).toInt())
        }
    }

    @Test
    fun `tone anchors at 0 for black and 100 for white`() {
        assertEquals(0.0, Hct.fromInt(0xFF000000.toInt()).tone, 1.0)
        assertEquals(100.0, Hct.fromInt(0xFFFFFFFF.toInt()).tone, 1.0)
    }

    @Test
    fun `pure colors match reference hue, chroma and tone`() {
        val red = Hct.fromInt(0xFFFF0000.toInt())
        assertEquals(27.408, red.hue, 1.0)
        assertEquals(113.357, red.chroma, 1.0)
        assertEquals(53.237, red.tone, 1.0)

        val green = Hct.fromInt(0xFF00FF00.toInt())
        assertEquals(142.139, green.hue, 1.0)
        assertEquals(108.410, green.chroma, 1.0)
        assertEquals(87.737, green.tone, 1.0)

        val blue = Hct.fromInt(0xFF0000FF.toInt())
        assertEquals(282.788, blue.hue, 1.0)
        assertEquals(87.230, blue.chroma, 1.0)
        assertEquals(32.302, blue.tone, 1.0)
    }

    @Test
    fun `solver preserves the requested tone`() {
        for (tone in listOf(10.0, 30.0, 50.0, 70.0, 90.0)) {
            val hct = Hct.from(120.0, 40.0, tone)
            assertEquals(tone, hct.tone, 0.5)
        }
    }

    @Test
    fun `tonal palette samples the requested tone and rises in luminance`() {
        val palette = TonalPalette.fromInt(0xFF6D28D9.toInt())

        // tone(t) returns an ARGB whose measured HCT tone is ~t.
        assertEquals(0.0, Hct.fromInt(palette.tone(0)).tone, 1.0)
        assertEquals(40.0, Hct.fromInt(palette.tone(40)).tone, 1.0)
        assertEquals(100.0, Hct.fromInt(palette.tone(100)).tone, 1.0)

        // Higher tones are lighter.
        assertTrue(
            ColorUtils.lstarFromArgb(palette.tone(30)) < ColorUtils.lstarFromArgb(palette.tone(70)),
        )
    }
}
