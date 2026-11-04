# Jenga

A Kotlin Multiplatform and Compose Multiplatform design system. Brandable design tokens and
ready-made `Jenga*` blocks that render the same on Android, iOS, Desktop and the Web (Wasm), from one shared
`commonMain` codebase.

> *Jenga* is Swahili for "to build". You build screens out of Jenga blocks, not out of raw
> Compose or Material primitives.

[![CI](https://github.com/joelkanyi/jenga/actions/workflows/ci.yml/badge.svg)](https://github.com/joelkanyi/jenga/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joelkanyi/jenga.svg)](https://central.sonatype.com/artifact/io.github.joelkanyi/jenga)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-brightgreen)

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

## Install

Jenga is on Maven Central. Add `mavenCentral()` to your repositories, then the dependency.

Kotlin Multiplatform:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.joelkanyi:jenga:<version>")
        }
    }
}
```

Android only (the same coordinate resolves the Android variant automatically):

```kotlin
dependencies {
    implementation("io.github.joelkanyi:jenga:<version>")
}
```

## Documentation

Everything else lives on the site: **[joelkanyi.github.io/jenga](https://joelkanyi.github.io/jenga/)**

- [Get started](https://joelkanyi.github.io/jenga/get-started/): install, usage, tokens, running the catalog
- [Components](https://joelkanyi.github.io/jenga/components/): every block, with light and dark screenshots
- [Theming](https://joelkanyi.github.io/jenga/theming/): from one seed to a hand-authored scheme
- [API reference](https://joelkanyi.github.io/jenga/api/)
- Architecture and how to contribute: [jenga/README.md](jenga/README.md)
- Releasing to Maven Central: [RELEASING.md](RELEASING.md)

## License

Apache 2.0. See [LICENSE](LICENSE).
