package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A modal bottom sheet, themed with Jenga tokens.
 *
 * Control visibility by conditional composition — render this only while the
 * sheet should be shown:
 * ```
 * if (showSheet) {
 *     JengaBottomSheet(onDismissRequest = { showSheet = false }) { /* content */ }
 * }
 * ```
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaBottomSheetSample
 *
 * @param onDismissRequest called when the sheet is dismissed (drag down or scrim).
 * @param modifier the [Modifier] for the sheet.
 * @param content the sheet body, laid out in a [ColumnScope].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun JengaBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = JengaTheme.colors.surface,
        contentColor = JengaTheme.colors.textPrimary,
        scrimColor = JengaTheme.colors.scrim,
        dragHandle = { JengaDragHandle() },
        content = content,
    )
}

@Composable
private fun JengaDragHandle() {
    Box(
        modifier = Modifier.padding(vertical = JengaTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(JengaTheme.shapes.pill)
                .background(JengaTheme.colors.borderStrong),
        )
    }
}

// ---- Previews --------------------------------------------------------------
// The modal window can't be captured statically, so the previews render the
// sheet's visual surface (handle + content) inline.

@JengaBlockPreviews
@Composable
internal fun JengaBottomSheetPreview() {
    JengaTheme { BottomSheetSurfacePreview() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaBottomSheetRtlPreview() {
    JengaTheme { RtlPreview { BottomSheetSurfacePreview() } }
}

@Composable
private fun BottomSheetSurfacePreview() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.lg),
    ) {
        Column(
            modifier = Modifier
                .clip(JengaTheme.shapes.card)
                .background(JengaTheme.colors.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JengaDragHandle()
            Column(
                modifier = Modifier.padding(JengaTheme.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
            ) {
                JengaText(text = "Select a gate", style = JengaTheme.typography.titleLarge)
                JengaText(
                    text = "Choose which gate you're scanning at.",
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                )
            }
        }
    }
}
