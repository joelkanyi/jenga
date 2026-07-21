# Jenga

A Kotlin Multiplatform + Compose Multiplatform design system. Brandable design tokens and
ready-made `Jenga*` blocks that render the same on **Android, Desktop (JVM) and iOS**, from one
shared `commonMain` codebase.

> *Jenga* is Swahili for "to build". You build screens out of Jenga blocks, not out of raw
> Compose or Material primitives.

[![CI](https://github.com/joelkanyi/jenga/actions/workflows/ci.yml/badge.svg)](https://github.com/joelkanyi/jenga/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joelkanyi/jenga.svg)](https://central.sonatype.com/artifact/io.github.joelkanyi/jenga)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20Desktop%20%7C%20iOS-brightgreen)

```kotlin
JengaTheme {                                  // light/dark follows the system
    JengaScaffold(topBar = { JengaTopAppBar(title = "Events") }) { padding ->
        JengaStack(Modifier.padding(padding)) {
            JengaText("Tonight", style = JengaTheme.typography.headingMedium)
            JengaButton("Scan a ticket", onClick = { })
        }
    }
}
```

Brand it in one line — a full, accessible light and dark theme from one seed:

```kotlin
JengaTheme(brand = jengaBrand(seed = Color(0xFF6D28D9))) { App() }
```

---

## Why Jenga

- **One UI, three platforms.** The whole API lives in `commonMain`, so Android, Desktop and iOS
  share the same components and tokens.
- **Tokens, not literals.** Every color, size, radius, type style, elevation and motion value
  comes from a named token. No stray `Color(0x…)` or `16.dp` in feature code.
- **Brandable.** One `jengaBrand(seed = …)` derives a full, accessible light and dark theme —
  colors, type, shape and density — and every block re-themes. Drop down to per-token control
  when you need it.
- **Material 3 stays internal.** It is bridged inside Jenga for ripple, text selection and a few
  primitives, and never appears in the public API.
- **Quality-gated.** Explicit public API (binary-compatibility-validator), screenshot goldens
  (Roborazzi), WCAG contrast tests, and Compose lint checks all run in CI.

## Supported targets

| Target | Status |
|--------|--------|
| Android (`minSdk 24`) | yes |
| Desktop / JVM | yes |
| iOS (`iosArm64`, `iosSimulatorArm64`) | yes |

The Intel iOS simulator slice (`iosX64`) is not shipped, because Compose Multiplatform no longer
publishes it.

## Installation

Jenga is published to Maven Central as `io.github.joelkanyi:jenga`.

**Kotlin Multiplatform** consumer:

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.joelkanyi:jenga:0.1.0")
        }
    }
}
```

**Android-only** consumer: the same coordinate resolves the Android variant automatically.

```kotlin
dependencies {
    implementation("io.github.joelkanyi:jenga:0.1.0")
}
```

Make sure `mavenCentral()` is in your repositories.

## Quick start

Wrap your UI once in `JengaTheme`, then read tokens and use blocks below it. You never touch
`MaterialTheme`.

```kotlin
import io.github.joelkanyi.jenga.theme.JengaTheme
import io.github.joelkanyi.jenga.component.scaffold.JengaScaffold
import io.github.joelkanyi.jenga.component.scaffold.JengaTopAppBar
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.component.button.JengaButton

@Composable
fun App() {
    JengaTheme {                              // light/dark follows the system by default
        JengaScaffold(topBar = { JengaTopAppBar(title = "Events") }) { padding ->
            JengaStack(modifier = Modifier.padding(padding)) {
                JengaText("Tonight", style = JengaTheme.typography.headingMedium)
                JengaButton(text = "Scan a ticket", onClick = { /* … */ })
            }
        }
    }
}
```

Read tokens anywhere in the tree:

```kotlin
JengaText(
    text = "12 checked in",
    color = JengaTheme.colors.textMuted,
    style = JengaTheme.typography.bodySmall,
)
JengaSpacer(JengaTheme.spacing.lg)
```

## Theming and branding

Jenga ships neutral. One brand definition themes everything — a coherent, accessible
light and dark scheme, plus type, shape and density:

```kotlin
import androidx.compose.ui.graphics.Color
import io.github.joelkanyi.jenga.foundation.brand.jengaBrand

JengaTheme(brand = jengaBrand(seed = Color(0xFF6D28D9))) {
    App() // every block is now on-brand, in light and dark
}
```

The seed drives the brand color family (contrast holds by construction); neutral
surfaces and status colors (success / warning / error / info) keep their tuned
defaults, so meaning never rotates with your hue. Add more brand intent as needed:

```kotlin
jengaBrand(
    seed = Color(0xFF6D28D9),
    displayFontFamily = rememberPoppins(),  // headings and titles
    bodyFontFamily = rememberInter(),        // body, labels, captions
    corner = JengaCornerStyle.Sharp,         // Rounded (default) | Soft | Sharp
    density = JengaDensity.Compact,          // Comfortable (default) | Compact | Spacious
    contrast = JengaContrast.High,           // Standard (default) | Medium | High
)
```

### Bring your own font

Jenga doesn't ship third-party fonts; you supply a `FontFamily`. Drop `.ttf` files in
your app's `composeResources/font/` and build the family (Android, iOS, desktop, wasm):

```kotlin
@Composable
fun rememberPoppins() = FontFamily(
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_semibold, FontWeight.SemiBold),
    Font(Res.font.poppins_bold, FontWeight.Bold),
)
```

Omit the font to keep Jenga's bundled **Outfit** family.

### Bring your own icons

You are not limited to Jenga's icons. `JengaIcon` takes any `ImageVector`, so draw your
own anywhere — load vector files from your app's `composeResources/drawable/` the same
way as fonts. To re-brand the glyphs Jenga's components draw (chevron, `+`/`−`, status
icons), pass a custom set:

```kotlin
JengaTheme(icons = rememberJengaIconSet(chevron = MyChevron, trash = MyTrash)) { App() }
```

### Full control

Override any single token off the derived theme, or hand-author both schemes as one
pair — the light/dark selection is handled for you:

```kotlin
// tweak a few roles off a base
JengaTheme(colors = jengaLightColors().copy(brand = Color(0xFFFF5A1F))) { App() }

// a complete custom light + dark scheme
JengaTheme(
    scheme = JengaScheme(
        light = jengaLightColors().copy(/* your roles */),
        dark = jengaDarkColors().copy(/* your roles */),
    ),
) { App() }
```

Swap the icon language too: `JengaTheme(icons = rememberJengaIconSet(chevron = MyChevron))`.
See [docs/theming.md](docs/theming.md) for the full ladder.

## Token API

Read these off `JengaTheme.*`; never inline the raw value.

| Token set | Access | Contents |
|-----------|--------|----------|
| Colors | `JengaTheme.colors` | `brand`, `surface`, `background`, `textPrimary/Secondary/Muted`, `border`, status roles (`success`/`warning`/`error`/`info` + containers), and more. Light/dark aware. |
| Typography | `JengaTheme.typography` | `display`, `heading{Large,Medium,Small}`, `title*`, `body*`, `label`, `caption`, `button`. |
| Spacing | `JengaTheme.spacing` | `none, xxs, xs, sm, md, lg, xl, xxl, xxxl`. |
| Shapes | `JengaTheme.shapes` | corner radii (`sm`, `md`, `lg`, `pill`, …). |
| Sizing | `JengaTheme.sizing` | `minTouchTarget`, icon sizes, control heights, field sizes. |
| Elevation | `JengaTheme.elevation` | the elevation ladder. |
| Motion | `JengaTheme.motion` | durations and easing. |

## Blocks

Read the source or the [catalog](catalog/) for exact parameters. Every block has a `*Defaults`
object and closed `enum` variants.

| Need | Blocks |
|------|--------|
| Text | `JengaText` |
| Buttons | `JengaButton`, `JengaIconButton`, `JengaFab` |
| Inputs | `JengaTextField`, `JengaSearchField`, `JengaSlider`, `JengaStepper` |
| Selection | `JengaToggle`, `JengaCheckbox`, `JengaRadioButton`, `JengaSegmentedControl` |
| Containers | `JengaCard`, `JengaListItem`, `JengaDivider`, `JengaExpandableRow`, `JengaSwipeToDismiss` |
| Status / tags | `JengaBadge`, `JengaChip`, `JengaBanner`, `JengaVerdictBar` |
| Scaffolding | `JengaScaffold`, `JengaTopAppBar`, `JengaNavigationBar`, `JengaTabs` |
| Overlays / feedback | `JengaDialog`, `JengaBottomSheet`, `JengaSnackbar`, `JengaTooltip`, `JengaDropdownMenu` |
| Refresh | `JengaPullToRefresh` |
| Media / identity | `JengaAvatar`, `JengaIcon` + `JengaIcons`, `JengaMediaHero`, `JengaImageShelf` |
| Progress | `JengaLinearProgress`, `JengaCircularProgress` (+ indeterminate), `JengaDotStrip`, shimmer |
| Empty / error | `JengaEmptyState`, `JengaErrorState` |
| Layout primitives | `JengaStack`, `JengaInline`, `JengaWrap`, `JengaGrid`, `JengaBox`, `JengaSpacer`, `JengaSection`, `JengaSectionHeader` |
| Patterns | `JengaTicketRow`, `JengaStatCard`, `JengaStatTile`, `JengaReactionBar` |

Prefer the layout primitives over hand-nesting `Row`/`Column` with manual spacing:

```kotlin
JengaStack(space = JengaTheme.spacing.md) {   // vertical, token-spaced
    JengaInline(space = JengaTheme.spacing.sm) { /* horizontal row */ }
}
```

## Icons

Jenga bundles its own line-icon set as Compose Resources vector drawables, so there is no
external icon dependency:

```kotlin
JengaIcon(JengaIcons.Search, contentDescription = "Search")
JengaIcon(JengaIcons.Check, contentDescription = null, tint = JengaTheme.colors.success)
```

## The catalog (sample app)

[`catalog/`](catalog/) is a Compose Multiplatform sample that renders every token and component,
running on all three platforms from one shared UI:

```bash
./gradlew :catalog:run            # desktop window
./gradlew :catalog:installDebug   # android device/emulator
# iOS: open catalog/iosApp/iosApp.xcodeproj in Xcode, pick an Apple-Silicon simulator, Run
```

See [catalog/README.md](catalog/README.md) for details.

## API docs

Generate browsable HTML API docs from the KDoc with Dokka:

```bash
./gradlew :jenga:dokkaGenerateHtml
open jenga/build/dokka/html/index.html
```

## Architecture

Three tiers, tokens to blocks to patterns (Atomic Design / Spotify Encore):

```
foundation/   primitive palette -> semantic JengaColors -> typography / spacing / shape / sizing / elevation / motion
theme/        JengaTheme (CompositionLocals) + internal Material 3 bridge
component/    the blocks, one package per family (button/, card/, textfield/, …)
pattern/      organisms composed from blocks (TicketRow, StatCard, SectionHeader)
```

- **Primitive** values (raw hex) live only in `foundation/color/JengaPalette.kt`, generated from
  `tokens/primitives.json` (run `./gradlew :jenga:generateJengaTokens`, never hand-edit the
  generated file).
- **Semantic** roles are the public color contract and flip for dark mode.
- **Component** tokens are each block's `*Defaults`, reading only semantic tokens.

## Quality gates

Kept green in CI:

- **Public API**: `./gradlew :jenga:apiCheck` (run `:jenga:apiDump` after intentional changes).
- **Screenshots**: Roborazzi goldens on the Android target (`:jenga:verifyRoborazziDebug`,
  record with `:jenga:recordRoborazziDebug`).
- **Contrast**: WCAG unit tests over semantic text/background pairs.
- **Lint**: Slack compose-lints plus a token-usage check that fails on raw color literals in
  component code.
- **Explicit API**: the module is `explicitApi()`, so every public declaration needs an explicit
  visibility and return type.

## Adding a component

1. Confirm it is actually missing (grep `component/`, check the catalog).
2. Add tokens first if needed (new color role in `JengaColors`, or a value in
   `tokens/primitives.json` then `generateJengaTokens`).
3. Build the block under `component/<family>/`, reading only `JengaTheme.*` tokens, with a
   `*Defaults` object and closed enum variants.
4. Add an `internal` `@JengaBlockPreviews` preview plus an RTL preview in `androidMain`, and
   record goldens.
5. Add it to the catalog.
6. Run `:jenga:apiDump`, then `:jenga:check`.

## Publishing

Releases go to Maven Central through the Central Portal. See [RELEASING.md](RELEASING.md).

## License

Apache 2.0. See [LICENSE](LICENSE).
