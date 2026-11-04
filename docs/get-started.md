# Get started

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

## Platforms

| Android | iOS | Desktop (JVM) | Web |
|---------|-----|---------------|-----|
| `minSdk 23` | `iosArm64`, `iosSimulatorArm64` | yes | `wasmJs` |

The whole public API lives in `commonMain`, so every platform shares the same components and
tokens. The Intel iOS simulator slice (`iosX64`) is not shipped, because Compose Multiplatform
no longer publishes it.

`JengaImage` loads images from a URL on Android only (through Coil 3, using your app's image
loader). On iOS, Desktop and Web it shows its placeholder for now.

## Usage

Wrap your UI once in `JengaTheme`, then read tokens off `JengaTheme.*` and compose `Jenga*`
blocks below it. You never touch `MaterialTheme`; it is bridged internally and never appears in
the public API.

```kotlin
JengaText(color = JengaTheme.colors.textMuted, style = JengaTheme.typography.bodySmall)
Spacer(Modifier.height(JengaTheme.spacing.lg))
```

Prefer the layout primitives over hand-nesting `Row`/`Column` with manual spacing:

```kotlin
JengaStack(space = JengaTheme.spacing.md) {   // vertical, token-spaced
    JengaInline(space = JengaTheme.spacing.sm) { /* horizontal row */ }
}
```

## Tokens

Read these off `JengaTheme.*`. Never inline a raw value.

| Token set | Access | Contents |
|-----------|--------|----------|
| Colors | `JengaTheme.colors` | `brand`, `surface`, `background`, `textPrimary/Secondary/Muted`, `border`, status roles (`success`/`warning`/`error`/`info` + containers), and more. Light and dark aware. |
| Typography | `JengaTheme.typography` | `display`, `heading{Large,Medium,Small}`, `title*`, `body*`, `label`, `caption`, `button`, `mono`. |
| Spacing | `JengaTheme.spacing` | `none, xxs, xs, sm, md, lg, xl, xxl, xxxl`. |
| Shapes | `JengaTheme.shapes` | corner radii (`sm`, `md`, `lg`, `pill`, and more). |
| Sizing | `JengaTheme.sizing` | `minTouchTarget`, icon sizes, control heights, field sizes. |
| Elevation | `JengaTheme.elevation` | the elevation ladder. |
| Motion | `JengaTheme.motion` | durations and easing. |

## See it running

The [catalog](https://github.com/joelkanyi/jenga/tree/main/catalog) is a sample app that renders every token and block on all three
platforms from one shared UI:

```bash
./gradlew :catalog:run            # desktop window
./gradlew :catalog:installDebug   # android device or emulator
# iOS: open catalog/iosApp/iosApp.xcodeproj in Xcode, pick an Apple-Silicon simulator, Run
```

See [catalog/README.md](https://github.com/joelkanyi/jenga/tree/main/catalog/README.md) for details.
