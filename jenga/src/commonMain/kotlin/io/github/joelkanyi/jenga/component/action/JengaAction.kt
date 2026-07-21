package io.github.joelkanyi.jenga.component.action

import androidx.compose.runtime.Immutable
import dev.drewhamilton.poko.Poko

/**
 * A labeled tap action: the text to show and the handler to run, bundled so a
 * label can never be rendered without something to tap. Blocks that expose an
 * optional trailing action take a nullable [JengaAction] rather than a separate
 * label and handler pair.
 */
@Poko
@Immutable
public class JengaAction(
    public val label: String,
    public val onClick: () -> Unit,
) {
    public fun copy(
        label: String = this.label,
        onClick: () -> Unit = this.onClick,
    ): JengaAction = JengaAction(label, onClick)
}
