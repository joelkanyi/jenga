package io.github.joelkanyi.jenga.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.theme.LocalJengaContentColor

// ---- Previews (scanned by Roborazzi to generate screenshot tests) ----------

// Previews are `internal` (not `private`) so ComposablePreviewScanner picks them
// up — by default it skips private previews. They stay out of the public API.
@JengaBlockPreviews
@Composable
internal fun JengaButtonPreview() {
    JengaTheme { ButtonShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaButtonRtlPreview() {
    JengaTheme { RtlPreview { ButtonShowcase() } }
}

@Composable
private fun ButtonShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaButton(text = "Primary", onClick = {}, variant = JengaButtonVariant.Primary)
        JengaButton(text = "Ink", onClick = {}, variant = JengaButtonVariant.Ink)
        JengaButton(text = "Ghost", onClick = {}, variant = JengaButtonVariant.Ghost)
        JengaButton(text = "Outline", onClick = {}, variant = JengaButtonVariant.Outline)
        JengaButton(text = "Danger", onClick = {}, variant = JengaButtonVariant.Danger)
        Row(horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm)) {
            JengaButton(text = "Small", onClick = {}, size = JengaButtonSize.Small)
            JengaButton(text = "Large", onClick = {}, size = JengaButtonSize.Large)
        }
        JengaButton(text = "Disabled", onClick = {}, enabled = false)
        JengaButton(text = "Loading", onClick = {}, loading = true)
    }
}
