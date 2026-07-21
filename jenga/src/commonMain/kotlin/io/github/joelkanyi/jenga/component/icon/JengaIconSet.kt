package io.github.joelkanyi.jenga.component.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.drewhamilton.poko.Poko

/**
 * The semantic icons that Jenga components draw for themselves — the chevron on
 * an expandable row, the +/- on a stepper, the glyph a banner shows for each
 * tone, and so on. Provide a custom set via `JengaTheme(icons = ...)` (or
 * [rememberJengaIconSet]) to give every component your own iconography.
 *
 * This is distinct from [JengaIcons], the full vector catalogue you draw with
 * [JengaIcon] in your own UI; the set below defaults to those vectors.
 */
@Poko
@Immutable
public class JengaIconSet(
    /** Increment / add (e.g. the stepper's `+`). */
    public val add: ImageVector,
    /** Decrement / remove (e.g. the stepper's `−`). */
    public val remove: ImageVector,
    /** Success / affirmation check (e.g. a success banner). */
    public val check: ImageVector,
    /** Filled success mark (e.g. scan success feedback). */
    public val checkCircle: ImageVector,
    /** Disclosure chevron; auto-mirrors in RTL (e.g. an expandable row). */
    public val chevron: ImageVector,
    /** Dismiss / clear (e.g. a search field's clear button, an error banner). */
    public val close: ImageVector,
    /** Informational glyph (e.g. an info/warning banner). */
    public val info: ImageVector,
    /** Search / find (e.g. a search field's leading icon). */
    public val search: ImageVector,
    /** Positive reaction (e.g. a reaction bar's up vote). */
    public val thumbsUp: ImageVector,
    /** Negative reaction (e.g. a reaction bar's down vote). */
    public val thumbsDown: ImageVector,
    /** Destructive action (e.g. a swipe-to-dismiss reveal). */
    public val trash: ImageVector,
)

/**
 * Builds a [JengaIconSet], defaulting every role to Jenga's own [JengaIcons]
 * vectors. Override only the glyphs you want to re-brand:
 *
 * ```
 * JengaTheme(icons = rememberJengaIconSet(chevron = MyChevron)) { … }
 * ```
 */
@Composable
public fun rememberJengaIconSet(
    add: ImageVector = JengaIcons.Add,
    remove: ImageVector = JengaIcons.Remove,
    check: ImageVector = JengaIcons.Check,
    checkCircle: ImageVector = JengaIcons.CheckCircle,
    chevron: ImageVector = JengaIcons.ChevronRight,
    close: ImageVector = JengaIcons.Close,
    info: ImageVector = JengaIcons.Info,
    search: ImageVector = JengaIcons.Search,
    thumbsUp: ImageVector = JengaIcons.ThumbsUp,
    thumbsDown: ImageVector = JengaIcons.ThumbsDown,
    trash: ImageVector = JengaIcons.Trash,
): JengaIconSet = JengaIconSet(
    add = add,
    remove = remove,
    check = check,
    checkCircle = checkCircle,
    chevron = chevron,
    close = close,
    info = info,
    search = search,
    thumbsUp = thumbsUp,
    thumbsDown = thumbsDown,
    trash = trash,
)
