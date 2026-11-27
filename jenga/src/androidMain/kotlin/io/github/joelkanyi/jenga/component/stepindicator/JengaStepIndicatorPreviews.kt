package io.github.joelkanyi.jenga.component.stepindicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
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

/**
 * The compact variant in every state, in rows 328dp wide (a 360dp phone with margins). With nine
 * steps the nodes reach the row's edges, so the halo of a first or last current step runs 3dp
 * past them into the padding.
 */
@JengaBlockPreviews
@Composable
internal fun JengaStepIndicatorCompactPreview() {
    val compact = JengaStepIndicatorVariant.Compact
    val phone = Modifier.width(328.dp)
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        ) {
            PreviewSteps(
                "Customer details" to JengaStepState.Current,
                "Product and plan" to JengaStepState.Pending,
                "Documents" to JengaStepState.Pending,
                "Agreement" to JengaStepState.Pending,
                modifier = phone,
                variant = compact,
            )
            Column(verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm)) {
                PreviewSteps(
                    "Taarifa za mteja" to JengaStepState.Done,
                    "Bidhaa na mpango" to JengaStepState.Current,
                    "Nyaraka" to JengaStepState.Pending,
                    "Makubaliano" to JengaStepState.Pending,
                    modifier = phone,
                    variant = compact,
                )
                JengaText(
                    text = "Hatua 2 kati ya 4 · Bidhaa na mpango",
                    style = JengaTheme.typography.caption,
                    color = JengaTheme.colors.textMuted,
                )
            }
            PreviewSteps(
                "Customer details" to JengaStepState.Locked,
                "Product and plan" to JengaStepState.Locked,
                "Documents" to JengaStepState.Locked,
                "Agreement" to JengaStepState.Current,
                modifier = phone,
                variant = compact,
            )
            PreviewSteps(
                "Customer details" to JengaStepState.Done,
                "Product and plan" to JengaStepState.Done,
                "Documents" to JengaStepState.Done,
                "Agreement" to JengaStepState.Done,
                modifier = phone,
                variant = compact,
            )
            PreviewSteps(
                *numberedSteps(count = 6, current = 2),
                modifier = phone,
                variant = compact,
            )
            PreviewSteps(
                *numberedSteps(count = 9, current = 0),
                modifier = phone,
                variant = compact,
            )
            PreviewSteps(
                *numberedSteps(count = 9, current = 8),
                modifier = phone,
                variant = compact,
            )
        }
    }
}

/** The edge-to-edge arrangement in every variant, in rows 328dp wide (a 360dp phone with margins). */
@JengaBlockPreviews
@Composable
internal fun JengaStepIndicatorEdgeToEdgePreview() {
    JengaTheme { EdgeToEdgeShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaStepIndicatorEdgeToEdgeRtlPreview() {
    JengaTheme {
        RtlPreview {
            val phone = Modifier.width(328.dp)
            val edgeToEdge = JengaStepIndicatorArrangement.EdgeToEdge
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
                    arrangement = edgeToEdge,
                )
                PreviewSteps(
                    "Taarifa za mteja" to JengaStepState.Done,
                    "Bidhaa na mpango" to JengaStepState.Current,
                    "Nyaraka" to JengaStepState.Pending,
                    "Makubaliano" to JengaStepState.Pending,
                    modifier = phone,
                    variant = JengaStepIndicatorVariant.Numbered,
                    arrangement = edgeToEdge,
                )
                PreviewSteps(
                    *numberedSteps(count = 4, current = 0),
                    modifier = phone,
                    variant = JengaStepIndicatorVariant.Compact,
                    arrangement = edgeToEdge,
                )
                PreviewSteps(
                    *numberedSteps(count = 4, current = 1),
                    modifier = phone,
                    variant = JengaStepIndicatorVariant.Compact,
                )
            }
        }
    }
}

@Composable
private fun EdgeToEdgeShowcase() {
    val phone = Modifier.width(328.dp)
    val edgeToEdge = JengaStepIndicatorArrangement.EdgeToEdge
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
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            "Taarifa za mteja" to JengaStepState.Done,
            "Bidhaa na mpango" to JengaStepState.Current,
            "Nyaraka" to JengaStepState.Pending,
            "Makubaliano" to JengaStepState.Pending,
            modifier = phone,
            variant = JengaStepIndicatorVariant.Numbered,
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            "Taarifa za mteja" to JengaStepState.Locked,
            "Bidhaa na mpango" to JengaStepState.Locked,
            "Nyaraka" to JengaStepState.Done,
            "Makubaliano" to JengaStepState.Current,
            "Malipo ya awali" to JengaStepState.Pending,
            "Uthibitisho" to JengaStepState.Pending,
            modifier = phone,
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            *numberedSteps(count = 4, current = 3),
            modifier = phone,
            variant = JengaStepIndicatorVariant.Compact,
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            *numberedSteps(count = 9, current = 0),
            modifier = phone,
            variant = JengaStepIndicatorVariant.Compact,
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            "Taarifa za mteja" to JengaStepState.Done,
            "Bidhaa na mpango" to JengaStepState.Current,
            modifier = phone,
            arrangement = edgeToEdge,
        )
        PreviewSteps(
            "Taarifa za mteja na bidhaa" to JengaStepState.Current,
            modifier = phone,
            variant = JengaStepIndicatorVariant.Numbered,
            arrangement = edgeToEdge,
        )
    }
}

private fun numberedSteps(count: Int, current: Int): Array<Pair<String, JengaStepState>> = Array(count) { index ->
    "Q${index + 1}" to when {
        index < current -> JengaStepState.Done
        index == current -> JengaStepState.Current
        else -> JengaStepState.Pending
    }
}

@Composable
private fun PreviewSteps(
    vararg steps: Pair<String, JengaStepState>,
    modifier: Modifier = Modifier,
    variant: JengaStepIndicatorVariant = JengaStepIndicatorVariant.Labelled,
    arrangement: JengaStepIndicatorArrangement = JengaStepIndicatorArrangement.Centered,
) {
    JengaStepIndicator(
        steps = steps.map { (label, state) -> JengaStep(label, state) },
        stepContentDescription = { _, step -> step.label },
        modifier = modifier,
        variant = variant,
        arrangement = arrangement,
    )
}
