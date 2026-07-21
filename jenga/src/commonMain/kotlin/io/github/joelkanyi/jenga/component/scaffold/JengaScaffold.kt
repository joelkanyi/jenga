package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.joelkanyi.jenga.theme.JengaTheme

/** Defaults and token mappings for [JengaScaffold]. */
public object JengaScaffoldDefaults {
    /** Default background color. */
    public val containerColor: Color
        @Composable get() = JengaTheme.colors.background

    /** Default content (foreground) color. */
    public val contentColor: Color
        @Composable get() = JengaTheme.colors.textPrimary

    /**
     * Window insets the content is kept clear of: the system bars by default,
     * so the content lambda's [PaddingValues] already avoids the status and
     * navigation bars in an edge-to-edge app.
     *
     * Pass `WindowInsets(0, 0, 0, 0)` when an ancestor already consumed the
     * insets (e.g. a parent scaffold that owns them), or when the screen draws
     * full-bleed and handles insets on individual controls itself.
     */
    public val contentWindowInsets: WindowInsets
        @Composable get() = ScaffoldDefaults.contentWindowInsets
}

/**
 * The structural shell for a Jenga screen: a background, optional top/bottom
 * bars, and a content area inset to avoid them.
 *
 * Thin wrapper over the (internal) Material 3 [Scaffold] for correct inset and
 * bar handling, themed with Jenga tokens. Pass the provided [PaddingValues] to
 * your content's outermost scrollable/container.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaScaffoldSample
 *
 * @param modifier the [Modifier] for the scaffold.
 * @param topBar slot for a top app bar (e.g. [JengaTopAppBar]).
 * @param bottomBar slot for a bottom bar.
 * @param snackbarHost slot for a snackbar host (e.g. `JengaSnackbarHost`).
 * @param containerColor the background color; defaults to [JengaScaffoldDefaults.containerColor].
 * @param contentColor the foreground color; defaults to [JengaScaffoldDefaults.contentColor].
 * @param contentWindowInsets insets the content is kept clear of; defaults to the system
 *   bars. Use `WindowInsets(0, 0, 0, 0)` when an ancestor already owns the insets.
 * @param content the screen body; receives inset padding to respect the bars.
 */
@Composable
public fun JengaScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = JengaScaffoldDefaults.containerColor,
    contentColor: Color = JengaScaffoldDefaults.contentColor,
    contentWindowInsets: WindowInsets = JengaScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}
