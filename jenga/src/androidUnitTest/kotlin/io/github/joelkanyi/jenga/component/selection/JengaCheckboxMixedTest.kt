package io.github.joelkanyi.jenga.component.selection

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.joelkanyi.jenga.component.list.JengaCheckboxListItem
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JengaCheckboxMixedTest {

    @get:Rule
    val compose = createComposeRule()

    private fun hasState(state: ToggleableState) = SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, state)

    private val isCheckbox = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)

    @Test
    fun checkboxReportsEachOfTheThreeStates() {
        compose.setContent {
            JengaTheme {
                Column {
                    JengaCheckbox(state = ToggleableState.Off, onClick = {}, modifier = Modifier.testTag("off"))
                    JengaCheckbox(state = ToggleableState.On, onClick = {}, modifier = Modifier.testTag("on"))
                    JengaCheckbox(state = ToggleableState.Indeterminate, onClick = {}, modifier = Modifier.testTag("mixed"))
                }
            }
        }
        compose.onNodeWithTag("off").assert(hasState(ToggleableState.Off)).assert(isCheckbox)
        compose.onNodeWithTag("on").assert(hasState(ToggleableState.On)).assert(isCheckbox)
        compose.onNodeWithTag("mixed").assert(hasState(ToggleableState.Indeterminate)).assert(isCheckbox).assertHasClickAction()
    }

    @Test
    fun tappingAMixedCheckboxOnlyReportsTheTap() {
        var taps = 0
        compose.setContent {
            JengaTheme {
                JengaCheckbox(state = ToggleableState.Indeterminate, onClick = { taps++ }, modifier = Modifier.testTag("mixed"))
            }
        }
        compose.onNodeWithTag("mixed").performClick()
        assertEquals(1, taps)
        // The caller did not change the state, so the box stays mixed.
        compose.onNodeWithTag("mixed").assert(hasState(ToggleableState.Indeterminate))
    }

    @Test
    fun callerDecidesTheNextState() {
        var state by mutableStateOf(ToggleableState.Indeterminate)
        compose.setContent {
            JengaTheme {
                JengaCheckbox(
                    state = state,
                    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
                    modifier = Modifier.testTag("box"),
                )
            }
        }
        compose.onNodeWithTag("box").performClick()
        compose.onNodeWithTag("box").assert(hasState(ToggleableState.On))
        compose.onNodeWithTag("box").performClick()
        compose.onNodeWithTag("box").assert(hasState(ToggleableState.Off))
    }

    @Test
    fun disabledMixedCheckboxIgnoresTaps() {
        var taps = 0
        compose.setContent {
            JengaTheme {
                JengaCheckbox(
                    state = ToggleableState.Indeterminate,
                    onClick = { taps++ },
                    modifier = Modifier.testTag("mixed"),
                    enabled = false,
                )
            }
        }
        compose.onNodeWithTag("mixed").assertIsNotEnabled().assert(hasState(ToggleableState.Indeterminate)).performClick()
        assertEquals(0, taps)
    }

    @Test
    fun readOnlyCheckboxHasNoSemanticsOfItsOwn() {
        compose.setContent {
            JengaTheme {
                JengaCheckbox(state = ToggleableState.Indeterminate, onClick = null, modifier = Modifier.testTag("mixed"))
            }
        }
        compose.onNodeWithTag("mixed").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ToggleableState))
    }

    @Test
    fun booleanCheckboxStillReportsOnAndOffAndTheNewValue() {
        var reported: Boolean? = null
        compose.setContent {
            JengaTheme {
                Column {
                    JengaCheckbox(checked = true, onCheckedChange = { reported = it }, modifier = Modifier.testTag("on"))
                    JengaCheckbox(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("off"))
                }
            }
        }
        compose.onNodeWithTag("on").assert(hasState(ToggleableState.On)).assert(isCheckbox)
        compose.onNodeWithTag("off").assert(hasState(ToggleableState.Off)).assert(isCheckbox)
        compose.onNodeWithTag("on").performClick()
        assertEquals(false, reported)
    }

    @Test
    fun mixedListItemIsOneCheckboxNodeForTheWholeRow() {
        var taps = 0
        compose.setContent {
            JengaTheme {
                JengaCheckboxListItem(
                    headline = "Select all",
                    state = ToggleableState.Indeterminate,
                    onClick = { taps++ },
                    modifier = Modifier.testTag("row"),
                )
            }
        }
        compose.onNodeWithTag("row").assert(hasState(ToggleableState.Indeterminate)).assert(isCheckbox)
        // Tapping the label, not the box, reports the tap: the row is the target.
        compose.onNodeWithText("Select all").performClick()
        assertEquals(1, taps)
    }

    @Test
    fun loadingOrDisabledMixedListItemIgnoresTaps() {
        var taps = 0
        compose.setContent {
            JengaTheme {
                Column {
                    JengaCheckboxListItem(
                        headline = "Loading",
                        state = ToggleableState.Indeterminate,
                        onClick = { taps++ },
                        modifier = Modifier.testTag("loading"),
                        loading = true,
                    )
                    JengaCheckboxListItem(
                        headline = "Disabled",
                        state = ToggleableState.Indeterminate,
                        onClick = { taps++ },
                        modifier = Modifier.testTag("disabled"),
                        enabled = false,
                    )
                }
            }
        }
        compose.onNodeWithTag("loading").assertIsNotEnabled().performClick()
        compose.onNodeWithTag("disabled").assertIsNotEnabled().performClick()
        assertEquals(0, taps)
    }

    @Test
    fun booleanListItemStillReportsOnAndTheNewValue() {
        var reported: Boolean? = null
        compose.setContent {
            JengaTheme {
                JengaCheckboxListItem(
                    headline = "Battery",
                    checked = true,
                    onCheckedChange = { reported = it },
                    modifier = Modifier.testTag("row"),
                )
            }
        }
        compose.onNodeWithTag("row").assert(hasState(ToggleableState.On)).assert(isCheckbox)
        compose.onNodeWithText("Battery").performClick()
        assertEquals(false, reported)
    }
}
