# Jenga

A Kotlin Multiplatform and Compose Multiplatform design system. Brandable design tokens and
ready-made `Jenga*` components that render the same on Android, iOS, Desktop and the Web (Wasm), from one shared
`commonMain` codebase.

> *Jenga* is Swahili for "to build". You build screens out of Jenga components, not out of raw
> Compose or Material primitives.

<div class="jenga-cards jenga-cards--showcase" markdown>

[![Button](assets/components/button-light.png#only-light){ loading=lazy }![Button](assets/components/button-dark.png#only-dark){ loading=lazy }<strong>Button</strong>](components/button.md){ .jenga-card }
[![Text field](assets/components/text-field-light.png#only-light){ loading=lazy }![Text field](assets/components/text-field-dark.png#only-dark){ loading=lazy }<strong>Text field</strong>](components/text-field.md){ .jenga-card }
[![Status pill](assets/components/status-pill-light.png#only-light){ loading=lazy }![Status pill](assets/components/status-pill-dark.png#only-dark){ loading=lazy }<strong>Status pill</strong>](components/status-pill.md){ .jenga-card }

</div>

## Example

```kotlin
// Light and dark follow the system.
JengaTheme {
    JengaScaffold(topBar = { JengaTopAppBar(title = "Events") }) { padding ->
        JengaStack(Modifier.padding(padding)) {
            JengaText("Tonight", style = JengaTheme.typography.headingMedium)
            JengaButton("Scan a ticket", onClick = { })
        }
    }
}
```

Brand it from one seed, and every component re-themes in light and dark:

```kotlin
JengaTheme(brand = jengaBrand(seed = Color(0xFF6D28D9))) { App() }
```

## Accessibility

Accessibility is built in and enforced in CI:

- Interactive blocks reserve a 48dp touch target.
- Color pairs are contrast-tested to WCAG AA (4.5:1 for text, 3:1 for brand fills), including
  brand-derived schemes across a range of seeds.
- Inputs show a focus ring, and directional icons mirror for right-to-left layouts.
- Every block ships verified light, dark, large-font and RTL screenshots.

[Get started](get-started.md){ .md-button .md-button--primary } [Browse components](components/index.md){ .md-button }
