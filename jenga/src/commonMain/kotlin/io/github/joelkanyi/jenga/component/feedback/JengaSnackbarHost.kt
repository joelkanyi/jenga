package io.github.joelkanyi.jenga.component.feedback

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** The outcome of a snackbar shown via [JengaSnackbarHostState.showSnackbar]. */
public enum class JengaSnackbarResult {
    /** The snackbar was dismissed (timeout, swipe or its close ✕) without the action being used. */
    Dismissed,

    /** The user tapped the action. */
    ActionPerformed,
}

/**
 * How long a snackbar stays before it auto-dismisses.
 *
 * Unlike Material, which forces an *indefinite* duration the moment an action
 * label is present, so an "Undo" can sit on screen forever, Jenga defaults an
 * action snackbar to [Short] as well. Opt into [Indefinite] explicitly when a
 * message must persist until acted upon, and pair it with the close ✕: pass
 * `dismissContentDescription` to [JengaSnackbarHostState.showSnackbar].
 */
public enum class JengaSnackbarDuration {
    /** Brief, for a simple confirmation. */
    Short,

    /** Longer, the sensible default for a snackbar carrying an action (e.g. Undo). */
    Long,

    /** Stays until acted upon or explicitly dismissed. Use sparingly. */
    Indefinite,
}

private fun JengaSnackbarDuration.toMaterial(): SnackbarDuration = when (this) {
    JengaSnackbarDuration.Short -> SnackbarDuration.Short
    JengaSnackbarDuration.Long -> SnackbarDuration.Long
    JengaSnackbarDuration.Indefinite -> SnackbarDuration.Indefinite
}

/**
 * Drives a [JengaSnackbarHost]: enqueue transient messages with [showSnackbar].
 * Create one with [rememberJengaSnackbarHostState]. Wraps the Material queueing
 * machinery, keeping it out of Jenga's public API.
 */
@Stable
public class JengaSnackbarHostState internal constructor(
    internal val m3: SnackbarHostState,
) {
    /**
     * Shows a snackbar and suspends until it is dismissed or its action is tapped.
     * A second call while one is showing replaces the current snackbar.
     *
     * @param message the message to display.
     * @param tone the semantic tone (accent dot); see [JengaSnackbarTone].
     * @param actionLabel optional action label.
     * @param duration how long it stays; defaults to [JengaSnackbarDuration.Short]
     *   even with an action, so an Undo never sits on screen indefinitely. Pass
     *   [JengaSnackbarDuration.Long] for a comfortable Undo window, or
     *   [JengaSnackbarDuration.Indefinite] to keep it until acted upon.
     * @param leadingContent optional single small item before the message, in place of the tone dot: an
     *   icon that says what happened or a counter; see [JengaSnackbar]. It is composed inside the
     *   snackbar with the snackbar's content color, so pass a plain lambda such as
     *   `{ JengaIcon(JengaIcons.CheckCircle, contentDescription = null) }`.
     *   The lambda is captured when `showSnackbar` is called and composed inside the host, so
     *   composition locals from the call site do not apply and plain captured values are fixed, like
     *   [message]; only reads of `State` update while the snackbar is shown.
     * @param dismissContentDescription when set, the snackbar shows a close ✕ at the end, after the
     *   action, with this accessibility label (e.g. "Dismiss"), in the user's language. Tapping it
     *   dismisses the snackbar and returns [JengaSnackbarResult.Dismissed].
     * @return whether the action was performed or the snackbar was dismissed.
     */
    public suspend fun showSnackbar(
        message: String,
        tone: JengaSnackbarTone = JengaSnackbarTone.Neutral,
        actionLabel: String? = null,
        duration: JengaSnackbarDuration = JengaSnackbarDuration.Short,
        leadingContent: (@Composable () -> Unit)? = null,
        dismissContentDescription: String? = null,
    ): JengaSnackbarResult {
        // Tone is carried out-of-band so the host can render the right accent.
        currentTone = tone
        val result = m3.showSnackbar(
            JengaSnackbarVisuals(message, actionLabel, duration.toMaterial(), leadingContent, dismissContentDescription),
        )
        return when (result) {
            SnackbarResult.ActionPerformed -> JengaSnackbarResult.ActionPerformed
            SnackbarResult.Dismissed -> JengaSnackbarResult.Dismissed
        }
    }

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public suspend fun showSnackbar(
        message: String,
        tone: JengaSnackbarTone = JengaSnackbarTone.Neutral,
        actionLabel: String? = null,
        duration: JengaSnackbarDuration = JengaSnackbarDuration.Short,
    ): JengaSnackbarResult = showSnackbar(message, tone, actionLabel, duration, leadingContent = null, dismissContentDescription = null)

    /**
     * Shows a snackbar for exactly [durationMillis], or until its action is tapped,
     * and suspends until then.
     *
     * @param message the message to display.
     * @param durationMillis how long the snackbar stays, in milliseconds.
     * @param tone the semantic tone (accent dot); see [JengaSnackbarTone].
     * @param actionLabel optional action label.
     * @param leadingContent optional single small item before the message, in place of the tone dot: an
     *   icon that says what happened or a counter; see [JengaSnackbar]. It is composed inside the
     *   snackbar with the snackbar's content color, so pass a plain lambda such as
     *   `{ JengaIcon(JengaIcons.CheckCircle, contentDescription = null) }`.
     *   The lambda is captured when `showSnackbar` is called and composed inside the host, so
     *   composition locals from the call site do not apply and plain captured values are fixed, like
     *   [message]; only reads of `State` update while the snackbar is shown.
     * @param dismissContentDescription when set, the snackbar shows a close ✕ at the end, after the
     *   action, with this accessibility label (e.g. "Dismiss"), in the user's language. Tapping it
     *   dismisses the snackbar and returns [JengaSnackbarResult.Dismissed].
     * @return whether the action was performed or the snackbar was dismissed.
     */
    public suspend fun showSnackbar(
        message: String,
        durationMillis: Long,
        tone: JengaSnackbarTone = JengaSnackbarTone.Neutral,
        actionLabel: String? = null,
        leadingContent: (@Composable () -> Unit)? = null,
        dismissContentDescription: String? = null,
    ): JengaSnackbarResult = coroutineScope {
        currentTone = tone
        // Indefinite for Material; the timer below ends it after durationMillis.
        val visuals = JengaSnackbarVisuals(message, actionLabel, SnackbarDuration.Indefinite, leadingContent, dismissContentDescription)
        val timer = launch {
            val shown = snapshotFlow { m3.currentSnackbarData }.first { it?.visuals === visuals }
            delay(durationMillis)
            if (m3.currentSnackbarData === shown) shown?.dismiss()
        }
        try {
            when (m3.showSnackbar(visuals)) {
                SnackbarResult.ActionPerformed -> JengaSnackbarResult.ActionPerformed
                SnackbarResult.Dismissed -> JengaSnackbarResult.Dismissed
            }
        } finally {
            timer.cancel()
        }
    }

    @Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public suspend fun showSnackbar(
        message: String,
        durationMillis: Long,
        tone: JengaSnackbarTone = JengaSnackbarTone.Neutral,
        actionLabel: String? = null,
    ): JengaSnackbarResult = showSnackbar(message, durationMillis, tone, actionLabel, leadingContent = null, dismissContentDescription = null)

    internal var currentTone: JengaSnackbarTone = JengaSnackbarTone.Neutral
}

/** Remembers a [JengaSnackbarHostState] for a [JengaSnackbarHost]. */
@Composable
public fun rememberJengaSnackbarHostState(): JengaSnackbarHostState {
    val m3 = remember { SnackbarHostState() }
    return remember(m3) { JengaSnackbarHostState(m3) }
}

/**
 * Renders the snackbar queued on [hostState] as a [JengaSnackbar]. Place it in a
 * scaffold's `snackbarHost` slot (see `JengaScaffold`).
 *
 * @param hostState the state from [rememberJengaSnackbarHostState].
 * @param modifier the [Modifier] for the host.
 * @param snackbarPadding space around the snackbar inside the host.
 * @param shape the snackbar shape.
 * @param colors the snackbar color set.
 * @param contentPadding padding inside the snackbar.
 * @param messageStyle the message text style.
 * @param actionStyle the action label text style.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSnackbarHostSample
 */
@Composable
public fun JengaSnackbarHost(
    hostState: JengaSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbarPadding: PaddingValues = JengaSnackbarDefaults.hostPadding,
    shape: Shape = JengaSnackbarDefaults.shape,
    colors: JengaSnackbarColors = JengaSnackbarDefaults.colors(),
    contentPadding: PaddingValues = JengaSnackbarDefaults.contentPadding,
    messageStyle: TextStyle = JengaSnackbarDefaults.messageStyle,
    actionStyle: TextStyle = JengaSnackbarDefaults.actionStyle,
) {
    SnackbarHost(
        hostState = hostState.m3,
        modifier = modifier,
    ) { data ->
        val visuals = data.visuals as? JengaSnackbarVisuals
        JengaSnackbar(
            message = data.visuals.message,
            modifier = Modifier.padding(snackbarPadding),
            tone = hostState.currentTone,
            actionLabel = data.visuals.actionLabel,
            onAction = data.visuals.actionLabel?.let { { data.performAction() } },
            shape = shape,
            colors = colors,
            contentPadding = contentPadding,
            messageStyle = messageStyle,
            actionStyle = actionStyle,
            leadingContent = visuals?.leadingContent,
            onDismiss = { data.dismiss() },
            dismissContentDescription = visuals?.dismissContentDescription,
        )
    }
}

/**
 * What the host needs to draw one queued snackbar: Material's visuals plus the leading item and the
 * label of the close ✕.
 */
private class JengaSnackbarVisuals(
    override val message: String,
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    val leadingContent: (@Composable () -> Unit)?,
    val dismissContentDescription: String?,
) : SnackbarVisuals {
    override val withDismissAction: Boolean get() = dismissContentDescription != null
}
