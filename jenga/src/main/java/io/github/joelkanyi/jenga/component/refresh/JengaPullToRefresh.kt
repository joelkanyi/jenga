package io.github.joelkanyi.jenga.component.refresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for [JengaPullToRefresh]. */
public object JengaPullToRefreshDefaults {
    /** The refresh indicator's container (surface) color. */
    public val containerColor: Color
        @Composable get() = JengaTheme.colors.surface

    /** The refresh indicator's spinner color. */
    public val indicatorColor: Color
        @Composable get() = JengaTheme.colors.brand
}

/**
 * Wraps scrollable content with pull-to-refresh. Drag down past the top to
 * trigger [onRefresh]; show the spinner by setting [isRefreshing] true while the
 * refresh runs, then false when it completes.
 *
 * The [content] must be vertically scrollable (a `LazyColumn`, or a `Column` with
 * `verticalScroll`) for the gesture to be reachable.
 *
 * ```
 * JengaPullToRefresh(isRefreshing = state.isRefreshing, onRefresh = { onEvent(Refresh) }) {
 *     LazyColumn(Modifier.fillMaxSize()) { /* items */ }
 * }
 * ```
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaPullToRefreshSample
 *
 * @param isRefreshing whether a refresh is currently in progress (drives the spinner).
 * @param onRefresh called when the user pulls to refresh.
 * @param modifier the [Modifier] for the container.
 * @param content the scrollable content, laid out in a [BoxScope].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun JengaPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = JengaPullToRefreshDefaults.containerColor,
                color = JengaPullToRefreshDefaults.indicatorColor,
            )
        },
        content = content,
    )
}

// ---- Previews --------------------------------------------------------------
// The pull gesture and its indicator are interactive, so the static preview just
// renders the content surface (the indicator is hidden at rest).

@JengaBlockPreviews
@Composable
internal fun JengaPullToRefreshPreview() {
    JengaTheme { PullToRefreshShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaPullToRefreshRtlPreview() {
    JengaTheme { RtlPreview { PullToRefreshShowcase() } }
}

@Composable
private fun PullToRefreshShowcase() {
    JengaPullToRefresh(isRefreshing = false, onRefresh = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(JengaTheme.colors.background)
                .padding(JengaTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.sm),
        ) {
            JengaText("Pull down to refresh", style = JengaTheme.typography.titleMedium)
            JengaText(
                "Scans sync when you refresh.",
                style = JengaTheme.typography.bodySmall,
                color = JengaTheme.colors.textMuted,
            )
        }
    }
}
