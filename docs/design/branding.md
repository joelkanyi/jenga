# Design: JengaBrand, brand a whole theme from one definition

Status: proposed (design-first, pre-implementation). Written before implementation.

## 1. Problem

A consumer who finds jenga and wants to make it theirs must today re-specify the
token set by hand: `jengaLightColors().copy(...)` across ~39 color roles, then
again for `jengaDarkColors()`, plus typography, spacing, shapes, and there is no
single object that says "this is my brand." Fonts only support one family; icons
are hardcoded in components and cannot be rebranded at all.

Goal: **one brand definition → a coherent, accessible light + dark theme**, with a
gentle slope from zero-config to full control.

## 2. Non-goals

- **No consumer-side KSP / codegen.** Consumer theming stays plain Kotlin values;
  any codegen belongs on the author side only.
  A consumer processor adds a Gradle plugin + slower builds to save ~15 lines and
  kills call-site autocomplete/KDoc.
- **No top-level configuration DSL.** A brand seed is a flat value set; a
  `jengaBrand { colors{} type{} }` block fails the tree-shaped-construction test
  and re-imports the enumeration the seed exists to remove. Use a value type +
  factory with named defaults.
- **The seed does not drive semantic status colors.** success / warning / error /
  info and media overlays must not rotate with the brand hue (red stays red). They
  remain fixed, individually-overridable defaults.

## 3. The layered API

Four tiers, each reachable by adding one named argument to the tier below, each
argument defaulting to the brand-derived value: a continuum, not four doors.

```kotlin
JengaTheme { App() }                                             // L0 zero-config
JengaTheme(brand = jengaBrand(seed = Purple)) { App() }          // L1 one seed
JengaTheme(brand = jengaBrand(seed = Purple),                    // L2 seed + override
           shapes = jengaBrand(seed = Purple).shapes()
               .copy(button = CutCornerShape(8.dp))) { App() }
JengaTheme(colors = jengaLightColors().copy(brand = Purple)) { } // L3 full manual (exists)
```

## 4. Core abstractions

### 4.1 `JengaBrand` (value type + factory)

```kotlin
@Poko
@Immutable
public class JengaBrand internal constructor(
    // Color
    public val seed: Color,                 // required: primary brand accent
    public val accent: Color?,              // optional: drives ink/onInk; derived if null
    public val neutral: Color?,             // optional: surface/text tint hint; null = seed hue @ low chroma (see depth note)
    public val contrast: JengaContrast,     // Standard | Medium | High: widens tonal deltas
    // Type: display + body, not one family
    public val displayFontFamily: FontFamily?,  // headings; null = jenga default (Outfit)
    public val bodyFontFamily: FontFamily?,      // body/text; null = jenga default
    // Shape + density
    public val corner: JengaCornerStyle,    // Rounded | Soft | Sharp: drives JengaShapes
    public val density: JengaDensity,       // Compact | Comfortable | Spacious: drives spacing + sizing
    // Icons
    public val icons: JengaIconSet,         // themeable semantic icon set
)

public fun jengaBrand(
    seed: Color,
    accent: Color? = null,
    neutral: Color? = null,
    contrast: JengaContrast = JengaContrast.Standard,
    fontFamily: FontFamily? = null,                 // convenience: sets both display + body
    displayFontFamily: FontFamily? = fontFamily,
    bodyFontFamily: FontFamily? = fontFamily,
    corner: JengaCornerStyle = JengaCornerStyle.Rounded,
    density: JengaDensity = JengaDensity.Comfortable,
    icons: JengaIconSet = JengaIconSet.Default,
): JengaBrand
```

Derivation is expressed as pure functions of the brand (testable, deterministic):

```kotlin
public fun JengaBrand.lightColors(): JengaColors
public fun JengaBrand.darkColors(): JengaColors
public fun JengaBrand.typography(): JengaTypography
public fun JengaBrand.shapes(): JengaShapes
public fun JengaBrand.spacing(): JengaSpacing
public fun JengaBrand.sizing(): JengaSizing
```

### 4.2 Color derivation: hybrid HCT (decision: HCT brand family, fixed neutrals)

Vendor a minimal HCT subset into `commonMain` (pure Kotlin math, Apache-2.0,
license header, own the core, and validate it in tests against a reference
implementation's known values). Only what is needed:

```
foundation/color/hct/   (internal)
  Hct.kt        : HCT <-> ARGB (CAM16 + L*)
  Cam16.kt
  ColorUtils.kt : sRGB <-> XYZ, luminance
  TonalPalette.kt : tone(0..100) off a fixed hue+chroma
```

Pipeline (hybrid scope):

```
seed:Color ──► HCT(seed) ──► brandPalette = TonalPalette(hue, chroma≈36)
                             accentPalette = TonalPalette(accent ?: derived, chroma≈16)

for isDark in {false, true}:
  brand         = brandPalette.tone(light 40 / dark 80)
  onBrand       = brandPalette.tone(light 100 / dark 20)
  brandSubtle   = brandPalette.tone(light 90 / dark 30)
  onBrandSubtle = brandPalette.tone(light 10 / dark 90)
  focusRing     = brand @ alpha
  ink / onInk   = accentPalette.tone(...)
  ── everything else (neutrals, status, overlays) = jenga's existing
     jengaLightColors()/jengaDarkColors() defaults, verbatim ──
  (a subtle neutral tint toward `neutral` may be layered later; out of scope v1)
```

Accessibility is **structural**: onBrand/onBrandSubtle are pulled from the
opposite end of the brand ramp from their background, so contrast holds by
construction; `contrast` widens the delta.

### 4.3 Typography: display + body split

`JengaTypography` stays a 13-role value type. `jengaTypography` gains a second
family so headings and body can differ:

```kotlin
public fun jengaTypography(
    fontFamily: FontFamily = FontFamily.Default,          // back-compat: sets both
    displayFontFamily: FontFamily = fontFamily,           // display/heading/title*
    bodyFontFamily: FontFamily = fontFamily,              // body*/label/caption/button
): JengaTypography
```

`JengaBrand.typography()` maps display roles (display, heading*, title*) to
`displayFontFamily` and the rest to `bodyFontFamily`.

#### Bring your own font (adopter side)

jenga does not ship third-party fonts; font files are the consumer's assets
(licensing + size). jenga accepts a `FontFamily`, which is Compose's standard
currency, and stays out of font loading. The adopter supplies the family; the
`.ttf`-in-`composeResources` path is the one that works on Android + iOS +
desktop + wasm (Android's downloadable `GoogleFont` provider is Android-only and
does not fit a KMP library).

1. Drop the files in the **app's** resources (not jenga's):

```
yourApp/src/commonMain/composeResources/font/
    poppins_regular.ttf  poppins_medium.ttf  poppins_semibold.ttf  poppins_bold.ttf
    nunito_regular.ttf   nunito_bold.ttf
```

2. Build the `FontFamily`: Compose Resources `Font()` is `@Composable`, so wrap
   it in a `remember`-style function (the same shape as jenga's own
   `rememberJengaFontFamily()`):

```kotlin
import yourapp.generated.resources.Res
import yourapp.generated.resources.*
import org.jetbrains.compose.resources.Font

@Composable
fun rememberPoppins() = FontFamily(
    Font(Res.font.poppins_regular,  FontWeight.Normal),
    Font(Res.font.poppins_medium,   FontWeight.Medium),
    Font(Res.font.poppins_semibold, FontWeight.SemiBold),
    Font(Res.font.poppins_bold,     FontWeight.Bold),
)
```

3. Hand it to the brand, one face everywhere, or a display/body pair:

```kotlin
JengaTheme(brand = jengaBrand(seed = Color(0xFF6D28D9), fontFamily = rememberPoppins())) {
    App() // every Jenga component now uses Poppins
}

JengaTheme(
    brand = jengaBrand(
        seed = Color(0xFF6D28D9),
        displayFontFamily = rememberPoppins(), // headings / titles
        bodyFontFamily = rememberNunito(),     // body / labels / captions
    ),
) { App() }
```

Omitting `fontFamily` keeps jenga's bundled Outfit default (zero setup). If an
adopter prefers not to use Compose Resources, they can build the `FontFamily` any
other way (platform fonts, etc.) and still just pass it in.

### 4.4 Icons: themeable `JengaIconSet` (architecture change)

Today `JengaIcons` is a static catalog and components hardcode 11 semantic roles
(Add, Remove, Check, CheckCircle, ChevronRight, Close, Info, Search, ThumbsUp,
ThumbsDown, Trash) in their bodies, so icons cannot be rebranded. Introduce a
themeable semantic set, keeping `JengaIcons` as the default vector catalog.

```kotlin
@Poko
@Immutable
public class JengaIconSet(
    public val add: ImageVector,
    public val remove: ImageVector,
    public val check: ImageVector,
    public val checkCircle: ImageVector,
    public val chevron: ImageVector,      // was ChevronRight
    public val close: ImageVector,
    public val info: ImageVector,
    public val search: ImageVector,
    public val thumbsUp: ImageVector,
    public val thumbsDown: ImageVector,
    public val trash: ImageVector,
) {
    public companion object {
        public val Default: JengaIconSet  // maps to JengaIcons.* catalog vectors
    }
}

internal val LocalJengaIcons = staticCompositionLocalOf { JengaIconSet.Default }
// JengaTheme.icons: JengaIconSet   (new accessor, like colors/typography)
```

- `JengaIcons` (catalog of ~all vectors) stays public and unchanged; consumers
  keep using `JengaIcons.Search` in their own UI.
- `JengaIconSet` is the semantic mapping components read. All 11 hardcoded call
  sites route through `JengaTheme.icons.*`. Components that already expose an icon
  param (`JengaReactionBar`, `JengaSwipeToDismiss`) default it to the themed value.

### 4.5 `JengaTheme(brand = …)` overload + accessor

```kotlin
@Composable
public fun JengaTheme(
    brand: JengaBrand,
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: JengaColors = if (darkTheme) brand.darkColors() else brand.lightColors(),
    typography: JengaTypography = brand.typography(),
    spacing: JengaSpacing = brand.spacing(),
    shapes: JengaShapes = brand.shapes(),
    sizing: JengaSizing = brand.sizing(),
    elevation: JengaElevation = jengaElevation(),
    motion: JengaMotion = jengaMotion(),
    icons: JengaIconSet = brand.icons,
    content: @Composable () -> Unit,
)
```

`JengaTheme.icons` joins `colors`/`typography`/… as a `@ReadOnlyComposable`
accessor over `LocalJengaIcons`. The existing L3 overload also gains an `icons`
parameter (default `JengaIconSet.Default`).

### 4.6 Full control: `JengaScheme` pair + `copy()` (L3, first-class)

For a consumer with a complete token sheet (every role specified, nothing
derived), the seed is the wrong tool. The right tool is **`copy()` off a base**
for the per-scheme edits, plus a **light+dark pair type** so the whole custom
theme is one named object and the caller never re-implements the `darkTheme`
branch.

```kotlin
public class JengaScheme(
    public val light: JengaColors,
    public val dark: JengaColors,
)

@Composable
public fun JengaTheme(
    scheme: JengaScheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // typography/spacing/… keep their existing defaults or come from a brand
    content: @Composable () -> Unit,
)
```

Usage:

```kotlin
val AcmeColors = JengaScheme(
    light = jengaLightColors().copy(brand = AcmePurple,   surface = White,  textPrimary = Ink),
    dark  = jengaDarkColors().copy(brand = AcmePurpleLt, surface = Ink900, textPrimary = Cloud),
)
JengaTheme(scheme = AcmeColors) { App() }   // selection handled internally
```

Why not a 39-parameter `jengaColors(...)` factory: it reintroduces exactly the
problems a wide factory has: no named-only enforcement, positional fragility, and
default-value duplication, while `copy()` already gives named-only, binary-safe
overrides for free. Why not a `jengaColors { }` builder-DSL: `copy()` already
delivers the DSL's benefits without the ~500-line, seven-bundle builder cost or the
unfamiliarity tax (a prototype confirmed the DSL's only extra was a marginal
`shared { }` block). See §10.

`jengaLightColors()`/`jengaDarkColors()` remain the zero-arg default schemes;
`JengaColors`' constructor may become `internal` since `copy()` + the factories
cover every construction need. Full custom is inherently two palettes (light +
dark), expected when a consumer has a spec for both modes; `JengaScheme` makes
that pair one object, and `JengaBrand` removes the duplication for the common case.

## 5. Data flow

```
JengaBrand ──derive──► JengaColors(light/dark), JengaTypography, JengaShapes,
                       JengaSpacing, JengaSizing, JengaIconSet
     │
JengaTheme(brand) picks scheme by darkTheme, provides all via CompositionLocals
     │
JengaTheme.colors / .typography / .icons / …  ◄── components read here
```

## 6. Failure cases & validation

- **Adversarial seeds** (pale yellow, mid green, dark blue, near-black, near-white):
  derivation must still yield legible on-colors. Guaranteed structurally, verified
  by test.
- **Contrast** becomes a property test: extend `JengaContrastTest` to run the WCAG
  assertions over `jengaBrand(seed).lightColors()/darkColors()` for a set of seeds
  × {Standard, High}, not just the two fixed default schemes.
- **HCT correctness**: unit-test the vendored `Hct`/`TonalPalette` against known
  reference values.
- **Determinism**: `brand.lightColors()` is pure, same input, same output; snapshot
  a golden set.

## 7. Packaging

- HCT math: `foundation/color/hct/` (`internal`).
- `JengaBrand`, `JengaContrast`, `JengaCornerStyle`, `JengaDensity`: `foundation/brand/`.
- `JengaIconSet`, `LocalJengaIcons`: `component/icon/` next to `JengaIcons`.
- No new module; no new runtime dependency. `explicitApi()` + committed
  `.api` / `.klib.api` cover the new surface.

## 8. Phasing (implementation order)

1. Typography display+body split (small, isolated).
2. `JengaIconSet` + `LocalJengaIcons` + `JengaTheme.icons`; route the 11 call sites.
3. Vendor HCT subset + unit tests vs oracle.
4. `JengaBrand` value type + factory + derivation functions (hybrid color, corner→
   shapes, density→spacing/sizing).
5. `JengaTheme(brand = …)` overload + `icons` param on the L3 overload; apiDump.
6. Property-test contrast over seeds; Roborazzi a branded showcase.
7. README "brand it in three lines" + a `docs/theming.md` ladder (seed → partial → full).

## 9. Open questions

- Neutral tinting toward `neutral` seed: deferred to a later pass (v1 keeps jenga's
  tuned neutrals). Revisit if brands ask for tinted surfaces.
- Whether `corner`/`density` are enums (proposed) or free scales, enums first
  (hard to misuse), escape hatch is L3 `shapes =`/`spacing =`.

## 10. DSL: deliberately not used (and why)

A receiver-lambda DSL was evaluated across every jenga surface against the
established criteria for builders vs factories and Kotlin's type-safe-builder
guidance. Verdict: **no new DSL earns its place.** Recorded here so the decision
isn't silently re-litigated.

Rubric: a builder/DSL beats a factory only when one genuinely holds:
1. tree-shaped / nested construction (`html { body { } }`);
2. a wide, same-typed, *growing* value bag where named-only + binary-compat matter
   AND `copy()` doesn't already provide them;
3. removing repeated qualifiers across many lines;
4. collection / scope building (`LazyListScope { item { } }`).

Per-surface outcome:
- **JengaColors (39 roles, growing)**: the one surface where the builder argument
  bites (criterion 2). But `copy()` already delivers named-only, binary-safe
  overrides with zero new/unfamiliar API. A DSL adds surface for no net gain.
- **Other token bundles (Typography/Spacing/Shapes/Sizing/Icons/Elevation/Motion)**
  are flat, stable ("2D point" case). Factory + `copy()`.
- **Full theme composition**: not tree-shaped; a flat bag of sibling bundles, and
  `JengaTheme` must stay a `@Composable` mirroring `MaterialTheme`. No `jengaTheme { }`.
- **JengaBrand seed**: 8 flat mixed scalars. Factory with named defaults.
- **Components**: trailing `@Composable` content slots (not config); already correct.
- **Collections (nav bar, menus)**: already use `RowScope`/`ColumnScope` item
  scopes (the `LazyListScope` pattern). Tabs/segmented take `List<String>` (flat).

A compiling prototype (`jengaColors { }` + a light/dark `jengaScheme { }`) confirmed
it: single-scheme block ≈ `copy()`; the only extra was a marginal `shared { }`, at a
cost of ~130 lines of hand-maintained builder per bundle (~500+ across all seven)
plus `@DslMarker`. The prototype's real payoff was exposing that the actual missing
piece is a **light+dark pair type** (`JengaScheme`, §4.6), not a DSL, a plain,
familiar value type that solves the two-palette/selection pain the block syntax was
reaching for.

The one future opening: if `JengaTabs`/`JengaSegmentedControl` ever carry
heterogeneous per-tab content (icon + badge + slot), a `JengaTabsScope { tab { } }`
collection-DSL (criterion 4) would then earn its place. Not before.
