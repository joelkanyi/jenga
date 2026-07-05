# Module Jenga

**Jenga** is a Kotlin Multiplatform + Compose Multiplatform design system: brandable design
tokens and ready-made `Jenga*` blocks that render identically on Android, Desktop (JVM) and
iOS. *Jenga* is Swahili for "to build" — you build screens out of Jenga blocks rather than raw
Compose or Material primitives.

The public API is entirely `JengaTheme.*` (tokens) and `Jenga*` (blocks). Material 3 is bridged
internally and never leaks into the surface.

### Getting started

Wrap your UI once in [io.github.joelkanyi.jenga.theme.JengaTheme], then read tokens and use
blocks below it:

```kotlin
JengaTheme {
    JengaScaffold(topBar = { JengaTopAppBar(title = "Events") }) { padding ->
        JengaStack(modifier = Modifier.padding(padding)) {
            JengaText("Tonight", style = JengaTheme.typography.headingMedium)
            JengaButton(text = "Scan a ticket", onClick = { })
        }
    }
}
```

The three layers are **tokens → blocks → patterns**: primitive palette to semantic roles to
each block's `*Defaults`, then organisms composed from blocks.

# Package io.github.joelkanyi.jenga.theme

The theme entry point. [JengaTheme] wraps content, exposes every token set
(`JengaTheme.colors / typography / spacing / shapes / sizing / elevation / motion`) through
CompositionLocals, and bridges to Material 3 internally. Brand and typeface are injected here.

# Package io.github.joelkanyi.jenga.foundation.color

The color system: the generated primitive palette, the semantic [JengaColors] roles
(`brand`, `surface`, `textPrimary`, `success`…) that flip for dark mode, and `withBrand` for
rebranding.

# Package io.github.joelkanyi.jenga.foundation.typography

The Outfit-based type scale ([JengaTypography]) and `rememberJengaFontFamily`. Swap the family
to re-theme typography while keeping the scale.

# Package io.github.joelkanyi.jenga.component.button

Buttons and button-like actions: `JengaButton`, `JengaIconButton`, `JengaFab`, with their
variants and `*Defaults`.

# Package io.github.joelkanyi.jenga.component.layout

Layout primitives — `JengaStack`, `JengaInline`, `JengaWrap`, `JengaGrid`, `JengaBox`,
`JengaSection`, `JengaSpacer` — that read spacing tokens so you never hand-roll gaps.

# Package io.github.joelkanyi.jenga.component.icon

`JengaIcon` and the bundled `JengaIcons` line-icon set, shipped as Compose Resources vector
drawables (no external icon dependency), tinted from tokens.

# Package io.github.joelkanyi.jenga.pattern

Ticketfiti-flavoured organisms composed from blocks: `JengaTicketRow`, `JengaStatCard`,
`JengaSectionHeader`.
