package io.github.joelkanyi.jenga.component.textfield

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaTextAreaTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun typingStopsAtTheLimitAndPasteIsCut() {
        var notes by mutableStateOf("abc")
        compose.setContent {
            JengaTheme { JengaTextArea(value = notes, onValueChange = { notes = it }, maxLength = 5) }
        }

        compose.onNodeWithText("abc").performTextReplacement("abcdefgh")
        assertEquals("abcde", notes)
        compose.onNodeWithText("5 / 5").assertExists()

        compose.onNodeWithText("abcde").performTextInput("x")
        assertEquals("abcde", notes)
    }

    @Test
    fun noCounterWithoutALimit() {
        var notes by mutableStateOf("")
        compose.setContent {
            JengaTheme { JengaTextArea(value = notes, onValueChange = { notes = it }, label = "Notes") }
        }

        compose.onNodeWithText("Notes").assertExists()
        compose.onNodeWithText("0 / ", substring = true).assertDoesNotExist()
    }
}
