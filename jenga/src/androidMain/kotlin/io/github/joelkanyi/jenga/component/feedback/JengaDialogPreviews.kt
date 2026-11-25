package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.badge.JengaBadgeTone
import io.github.joelkanyi.jenga.component.button.JengaButton
import io.github.joelkanyi.jenga.component.button.JengaButtonVariant
import io.github.joelkanyi.jenga.component.icon.JengaIconTile
import io.github.joelkanyi.jenga.component.icon.JengaIconTileSize
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaDialogPreview() {
    JengaTheme { DialogShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDialogRtlPreview() {
    JengaTheme { RtlPreview { DialogShowcase() } }
}

/** Icon on top, stacked actions: the confirmation for money, sales and irreversible actions. */
@JengaBlockPreviews
@Composable
internal fun JengaDialogIconStackedPreview() {
    JengaTheme { ConfirmationShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaDialogIconStackedRtlPreview() {
    JengaTheme { RtlPreview { ConfirmationShowcase() } }
}

/** An icon with the default row of actions. */
@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaDialogIconRowPreview() {
    JengaTheme {
        DialogStage {
            JengaDialogSurface(
                modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
                icon = { DialogIcon(JengaIcons.Info, JengaBadgeTone.Info) },
                title = "Register this sale?",
                text = "The deposit prompt goes to 0712 345 678.",
                confirmButton = { JengaButton(text = "Register sale", onClick = {}) },
                dismissButton = {
                    JengaButton(text = "Go back", onClick = {}, variant = JengaButtonVariant.Ghost)
                },
            )
        }
    }
}

/** A centred icon: the title and text centre under it. */
@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaDialogIconCenterPreview() {
    JengaTheme {
        DialogStage {
            JengaDialogSurface(
                modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
                icon = { DialogIcon(JengaIcons.Info, JengaBadgeTone.Info) },
                iconAlignment = JengaDialogIconAlignment.Center,
                actionsLayout = JengaDialogActionsLayout.Stacked,
                title = "Register this sale?",
                text = "Jane Wanjiru, iMax 3000 on the 12-month plan. The deposit prompt goes to 0712 345 678.",
                confirmButton = { JengaButton(text = "Register sale", onClick = {}) },
                dismissButton = {
                    JengaButton(text = "Go back", onClick = {}, variant = JengaButtonVariant.Ghost)
                },
            )
        }
    }
}

/** Stacked actions without an icon. */
@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaDialogStackedPreview() {
    JengaTheme {
        DialogStage {
            JengaDialogSurface(
                modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
                actionsLayout = JengaDialogActionsLayout.Stacked,
                title = "Reset device?",
                text = "This clears the cached gate session on this device. You can sign in again anytime.",
                confirmButton = {
                    JengaButton(text = "Reset", onClick = {}, variant = JengaButtonVariant.Danger)
                },
                dismissButton = {
                    JengaButton(text = "Cancel", onClick = {}, variant = JengaButtonVariant.Ghost)
                },
            )
        }
    }
}

/** Long (Swahili) labels at a large font on a 360dp phone: the stacked layout gives each the whole width. */
@Preview(name = "Large font", showBackground = true, fontScale = 1.5f, widthDp = 360)
@Composable
internal fun JengaDialogStackedLongLabelsPreview() {
    JengaTheme {
        DialogStage {
            JengaDialogSurface(
                modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
                icon = { DialogIcon(JengaIcons.Warning, JengaBadgeTone.Error) },
                actionsLayout = JengaDialogActionsLayout.Stacked,
                title = "Ghairi mauzo haya?",
                text = "Mauzo na hati zake zitaondolewa. Hatua hii haiwezi kutenduliwa.",
                confirmButton = {
                    JengaButton(text = "Ghairi mauzo haya", onClick = {}, variant = JengaButtonVariant.Danger)
                },
                dismissButton = {
                    JengaButton(text = "Endelea na mauzo", onClick = {}, variant = JengaButtonVariant.Ghost)
                },
            )
        }
    }
}

/** A single stacked action, e.g. acknowledging a success. */
@Preview(name = "Light", showBackground = true)
@Composable
internal fun JengaDialogStackedSingleActionPreview() {
    JengaTheme {
        DialogStage {
            JengaDialogSurface(
                modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
                icon = { DialogIcon(JengaIcons.CheckCircle, JengaBadgeTone.Success) },
                actionsLayout = JengaDialogActionsLayout.Stacked,
                title = "Sale registered",
                text = "Account ACC-204518 is active.",
                confirmButton = { JengaButton(text = "Done", onClick = {}) },
            )
        }
    }
}

@Composable
private fun ConfirmationShowcase() {
    DialogStage {
        JengaDialogSurface(
            modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
            icon = { DialogIcon(JengaIcons.Warning, JengaBadgeTone.Error) },
            actionsLayout = JengaDialogActionsLayout.Stacked,
            title = "Cancel this sale?",
            text = "The sale and its documents will be removed. This cannot be undone.",
            confirmButton = {
                JengaButton(text = "Cancel sale", onClick = {}, variant = JengaButtonVariant.Danger)
            },
            dismissButton = {
                JengaButton(text = "Keep sale", onClick = {}, variant = JengaButtonVariant.Ghost)
            },
        )
    }
}

/** The 48dp tinted circle a confirmation shows above its title. */
@Composable
private fun DialogIcon(icon: ImageVector, tone: JengaBadgeTone) {
    JengaIconTile(
        icon = icon,
        contentDescription = null,
        tone = tone,
        size = JengaIconTileSize.Large,
        shape = JengaTheme.shapes.pill,
    )
}

@Composable
private fun DialogStage(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        content()
    }
}

@Composable
private fun DialogShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
    ) {
        // Render the dialog surface inline (the Dialog window itself can't be
        // captured in a static screenshot).
        JengaDialogSurface(
            modifier = Modifier.overlayOutline(JengaDialogDefaults.border, JengaDialogDefaults.shape),
            title = "Reset device?",
            text = "This clears the cached gate session on this device. You can sign in again anytime.",
            confirmButton = {
                JengaButton(text = "Reset", onClick = {}, variant = JengaButtonVariant.Danger)
            },
            dismissButton = {
                JengaButton(text = "Cancel", onClick = {}, variant = JengaButtonVariant.Ghost)
            },
        )
    }
}
