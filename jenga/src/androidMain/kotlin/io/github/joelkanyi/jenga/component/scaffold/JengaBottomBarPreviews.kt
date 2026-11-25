package io.github.joelkanyi.jenga.component.scaffold

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.button.JengaIconButton
import io.github.joelkanyi.jenga.component.button.JengaIconButtonVariant
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaBottomBarPreview() {
    JengaTheme { BottomBarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBottomBarRtlPreview() {
    JengaTheme { RtlPreview { BottomBarShowcase() } }
}

@Composable
private fun BottomBarShowcase() {
    JengaBottomBar {
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.Search, contentDescription = "Search")
        }
        JengaButton(text = "Manual entry", onClick = {}, modifier = Modifier.weight(1f))
        JengaIconButton(onClick = {}, variant = JengaIconButtonVariant.Standard) {
            JengaIcon(JengaIcons.History, contentDescription = "History")
        }
    }
}

// ---- Adaptive actions: side by side when both labels fit, otherwise stacked ----

/** Light and dark: side by side. Large font: the same labels no longer fit their halves, so they stack. */
@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(name = "Dark", showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", showBackground = true, widthDp = 360, fontScale = 1.5f)
@Composable
internal fun JengaBottomBarActionsShortLabelsPreview() {
    JengaTheme { BottomBarActions(primary = "Record sale", secondary = "Save as draft") }
}

@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(name = "Dark", showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Tablet", showBackground = true, widthDp = 840)
@Composable
internal fun JengaBottomBarActionsLongLabelsPreview() {
    JengaTheme { BottomBarActions(primary = SwahiliPrimary, secondary = SwahiliSecondary) }
}

@Preview(name = "One long label", showBackground = true, widthDp = 360)
@Composable
internal fun JengaBottomBarActionsOneLongLabelPreview() {
    JengaTheme { BottomBarActions(primary = SwahiliPrimary, secondary = "Cancel") }
}

@Preview(name = "Single action", showBackground = true, widthDp = 360)
@Composable
internal fun JengaBottomBarActionsSinglePreview() {
    JengaTheme { BottomBarActions(primary = "Record sale", secondary = null) }
}

@Preview(name = "RTL side by side", showBackground = true, widthDp = 360)
@Composable
internal fun JengaBottomBarActionsRtlPreview() {
    JengaTheme { RtlPreview { BottomBarActions(primary = "Record sale", secondary = "Save as draft") } }
}

private const val SwahiliPrimary = "Sajili mauzo na utume ujumbe"
private const val SwahiliSecondary = "Hifadhi kama rasimu kwa sasa"

@Composable
private fun BottomBarActions(primary: String, secondary: String?) {
    JengaBottomBar(
        primaryAction = { JengaButton(text = primary, onClick = {}) },
        secondaryAction = secondary?.let {
            { JengaButton(text = it, onClick = {}, variant = JengaButtonVariant.Neutral) }
        },
    )
}
