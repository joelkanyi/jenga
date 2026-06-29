package io.github.joelkanyi.jenga.component.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
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
 * @param containerColor the background color; defaults to [JengaScaffoldDefaults.containerColor].
 * @param contentColor the foreground color; defaults to [JengaScaffoldDefaults.contentColor].
 * @param content the screen body; receives inset padding to respect the bars.
 */
@Composable
public fun JengaScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    containerColor: Color = JengaScaffoldDefaults.containerColor,
    contentColor: Color = JengaScaffoldDefaults.contentColor,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content,
    )
}
