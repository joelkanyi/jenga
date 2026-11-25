package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.theme.JengaTheme

@JengaBlockPreviews
@Composable
internal fun JengaStepIndicatorPreview() {
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        ) {
            PreviewSteps("Details" to JengaStepState.Current, "Choices" to JengaStepState.Pending, "Review" to JengaStepState.Pending)
            PreviewSteps("Details" to JengaStepState.Done, "Choices" to JengaStepState.Current, "Review" to JengaStepState.Pending)
            PreviewSteps("Device" to JengaStepState.Locked, "Payment" to JengaStepState.Locked, "Verification" to JengaStepState.Current)
            PreviewSteps("Details" to JengaStepState.Done, "Choices" to JengaStepState.Done, "Review" to JengaStepState.Done)
        }
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaStepIndicatorLongLabelsPreview() {
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        ) {
            PreviewSteps(
                "Personal details" to JengaStepState.Done,
                "Next of kin" to JengaStepState.Done,
                "Documents" to JengaStepState.Current,
                "Payment plan" to JengaStepState.Pending,
                "Review and sign" to JengaStepState.Pending,
            )
            PreviewSteps(
                "Personal details" to JengaStepState.Done,
                "Next of kin" to JengaStepState.Current,
                "Documents" to JengaStepState.Pending,
                "Payment plan" to JengaStepState.Pending,
                "Review and sign" to JengaStepState.Pending,
                modifier = Modifier.width(240.dp),
            )
        }
    }
}

/**
 * The numbered variant in every state. The first two rows show the same steps as labelled then
 * numbered, so the nodes can be compared; the rest are 328dp wide, a 360dp phone with margins.
 */
@JengaBlockPreviews
@Composable
internal fun JengaStepIndicatorNumberedPreview() {
    val numbered = JengaStepIndicatorVariant.Numbered
    val phone = Modifier.width(328.dp)
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        ) {
            PreviewSteps(
                "Details" to JengaStepState.Done,
                "Product" to JengaStepState.Current,
                "Documents" to JengaStepState.Pending,
                "Sign" to JengaStepState.Pending,
                modifier = phone,
            )
            PreviewSteps(
                "Details" to JengaStepState.Done,
                "Product" to JengaStepState.Current,
                "Documents" to JengaStepState.Pending,
                "Sign" to JengaStepState.Pending,
                modifier = phone,
                variant = numbered,
            )
            PreviewSteps(
                "Details" to JengaStepState.Current,
                "Product" to JengaStepState.Pending,
                "Documents" to JengaStepState.Pending,
                "Sign" to JengaStepState.Pending,
                modifier = phone,
                variant = numbered,
            )
            PreviewSteps(
                "Details" to JengaStepState.Locked,
                "Product" to JengaStepState.Locked,
                "Documents" to JengaStepState.Locked,
                "Sign" to JengaStepState.Current,
                modifier = phone,
                variant = numbered,
            )
            PreviewSteps(
                "Details" to JengaStepState.Done,
                "Product" to JengaStepState.Done,
                "Documents" to JengaStepState.Done,
                "Sign" to JengaStepState.Done,
                modifier = phone,
                variant = numbered,
            )
            PreviewSteps(
                "Taarifa za mteja" to JengaStepState.Done,
                "Bidhaa na mpango" to JengaStepState.Done,
                "Nyaraka" to JengaStepState.Current,
                "Makubaliano" to JengaStepState.Pending,
                "Malipo ya awali" to JengaStepState.Pending,
                "Uthibitisho" to JengaStepState.Pending,
                modifier = phone,
                variant = numbered,
            )
            PreviewSteps(
                *Array(10) { index ->
                    "Q${index + 1}" to when {
                        index < 2 -> JengaStepState.Done
                        index == 2 -> JengaStepState.Current
                        else -> JengaStepState.Pending
                    }
                },
                modifier = Modifier.width(420.dp),
                variant = numbered,
            )
        }
    }
}

@Composable
private fun PreviewSteps(
    vararg steps: Pair<String, JengaStepState>,
    modifier: Modifier = Modifier,
    variant: JengaStepIndicatorVariant = JengaStepIndicatorVariant.Labelled,
) {
    JengaStepIndicator(
        steps = steps.map { (label, state) -> JengaStep(label, state) },
        stepContentDescription = { _, step -> step.label },
        modifier = modifier,
        variant = variant,
    )
}
