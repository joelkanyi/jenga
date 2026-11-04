# Jenga

A Kotlin Multiplatform and Compose Multiplatform design system. Brandable design tokens and
ready-made `Jenga*` blocks that render the same on Android, iOS, Desktop and the Web (Wasm), from one shared
`commonMain` codebase.

> *Jenga* is Swahili for "to build". You build screens out of Jenga blocks, not out of raw
> Compose or Material primitives.

```kotlin
JengaTheme {                                  // light and dark follow the system
    JengaScaffold(topBar = { JengaTopAppBar(title = "Events") }) { padding ->
        JengaStack(Modifier.padding(padding)) {
            JengaText("Tonight", style = JengaTheme.typography.headingMedium)
            JengaButton("Scan a ticket", onClick = { })
        }
    }
}
```

Brand it from one seed, and every block re-themes in light and dark:

```kotlin
JengaTheme(brand = jengaBrand(seed = Color(0xFF6D28D9))) { App() }
```

[Get started](get-started.md){ .md-button .md-button--primary } [Browse components](components.md){ .md-button }
