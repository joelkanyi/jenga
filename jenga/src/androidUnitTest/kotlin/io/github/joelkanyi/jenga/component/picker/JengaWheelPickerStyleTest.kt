package io.github.joelkanyi.jenga.component.picker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import io.github.joelkanyi.jenga.theme.JengaComponentStyles
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaWheelPickerStyleTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun wheelDefaultsFollowTheThemeStyle() {
        val themed = JengaWheelPickerColors(selectedContainer = Color.Yellow, selectedContent = Color.Blue, content = Color.Gray)
        var resolved: JengaWheelPickerColors? = null
        compose.setContent {
            JengaTheme(components = JengaComponentStyles(wheelPicker = JengaWheelPickerStyle(colors = themed))) {
                resolved = JengaWheelPickerDefaults.colors()
            }
        }

        assertEquals(themed, resolved)
    }
}
