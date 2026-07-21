package io.github.joelkanyi.jenga.foundation.shape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import dev.drewhamilton.poko.Poko

/**
 * **Shape token set**: corner radii for Jenga surfaces.
 *
 * Exposes both a mechanical scale ([xs] to [xl]) and semantic shapes
 * ([control], [card], [pill]) that map directly to the frontend's
 * `--radius-control` (10dp), `--radius-card` (16dp) and `--radius-pill` (full).
 * Read via `JengaTheme.shapes`.
 */
@Poko
@Immutable
public class JengaShapes(
    public val none: RoundedCornerShape = RoundedCornerShape(0.dp),
    public val xs: RoundedCornerShape = RoundedCornerShape(4.dp),
    public val sm: RoundedCornerShape = RoundedCornerShape(8.dp),
    public val md: RoundedCornerShape = RoundedCornerShape(12.dp),
    public val lg: RoundedCornerShape = RoundedCornerShape(16.dp),
    public val xl: RoundedCornerShape = RoundedCornerShape(24.dp),
    /** Buttons, inputs and other controls (10dp). */
    public val control: RoundedCornerShape = RoundedCornerShape(10.dp),
    /** Cards, sheets and panels (16dp). */
    public val card: RoundedCornerShape = RoundedCornerShape(16.dp),
    /**
     * Large cards, heroes and expressive panels (20dp). A softer, more
     * "M3-Expressive" corner than [card] for prominent, imagery-led surfaces;
     * opt in per-surface or theme-wide via `JengaTheme(shapes = …)`.
     */
    public val cardLarge: RoundedCornerShape = RoundedCornerShape(20.dp),
    /** Fully rounded: pills, chips, avatars, FABs. */
    public val pill: RoundedCornerShape = RoundedCornerShape(percent = 50),
) {
    public fun copy(
        none: RoundedCornerShape = this.none,
        xs: RoundedCornerShape = this.xs,
        sm: RoundedCornerShape = this.sm,
        md: RoundedCornerShape = this.md,
        lg: RoundedCornerShape = this.lg,
        xl: RoundedCornerShape = this.xl,
        control: RoundedCornerShape = this.control,
        card: RoundedCornerShape = this.card,
        cardLarge: RoundedCornerShape = this.cardLarge,
        pill: RoundedCornerShape = this.pill,
    ): JengaShapes = JengaShapes(none, xs, sm, md, lg, xl, control, card, cardLarge, pill)
}

/** The default Jenga shape set. */
public fun jengaShapes(): JengaShapes = JengaShapes()
