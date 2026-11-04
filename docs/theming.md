# Theming

Jenga ships neutral and themes from a single brand definition. Customization is a
ladder: each rung adds one argument to the one below, and every argument defaults
to the value the brand would derive. Start at the top; drop down only as far as you
need.

## L0: zero config

```kotlin
JengaTheme(
    content = { App() },
)
```

Jenga's default tokens (neutral palette, bundled Outfit font, rounded shapes). Good
for prototypes and internal tools.

## L1: one seed

```kotlin
JengaTheme(
    brand = jengaBrand(
        seed = Color(
            color = 0xFF6D28D9,
        ),
    ),
    content = { App() },
)
```

The seed derives the brand color family for both light and dark, accessibly by
construction: the on-colors sit at the opposite end of the seed's tonal ramp, so
text on a brand fill always has contrast. Neutral surfaces and the semantic status
colors (success / warning / error / info) keep Jenga's tuned defaults; meaning does
not rotate with your hue.

`jengaBrand` takes a few more inputs, all optional:

```kotlin
jengaBrand(
    seed = Color(
        color = 0xFF6D28D9,
    ),
    // optional; drives the ink action color
    accent = Color(
        color = 0xFF0EA5E9,
    ),
    contrast = JengaContrast.High, // Standard (default) | Medium | High
    displayFontFamily = rememberPoppins(), // display, heading, title roles
    bodyFontFamily = rememberInter(), // body, label, caption, button roles
    corner = JengaCornerStyle.Sharp, // Rounded (default) | Soft | Sharp
    density = JengaDensity.Compact, // Comfortable (default) | Compact | Spacious
    icons = rememberJengaIconSet(
        chevron = MyChevron,
    ),
)
```

A single `fontFamily = …` sets both display and body at once.

### Fonts

Jenga does not ship third-party fonts; you supply a `FontFamily`. Put the `.ttf`
files under your app's `composeResources/font/` and build the family in a
`remember`-style function (works on Android, iOS, desktop and wasm):

```kotlin
import org.jetbrains.compose.resources.Font

@Composable
fun rememberPoppins() = FontFamily(
    Font(
        resource = Res.font.poppins_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.poppins_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resource = Res.font.poppins_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resource = Res.font.poppins_bold,
        weight = FontWeight.Bold,
    ),
)
```

Omit the font entirely to keep the bundled **Outfit** family. Android's downloadable
`GoogleFont` provider is Android-only and does not fit a multiplatform library.

### Icons

You are never limited to Jenga's icons. `JengaIcon` (and every component's icon slot)
takes any `ImageVector`, so draw your own in your UI:

```kotlin
JengaIcon(
    imageVector = MyIcons.Heart,
    contentDescription = "Favorite",
)
```

To load your own vector files on every platform, drop the SVG/XML drawables in your
app's `composeResources/drawable/` and build them the same way as fonts:

```kotlin
import org.jetbrains.compose.resources.vectorResource

JengaIcon(
    imageVector = vectorResource(
        resource = Res.drawable.my_heart,
    ),
    contentDescription = "Favorite",
)
```

Components draw their own glyphs (a stepper's `+`/`−`, an expandable row's chevron, a
banner's status icon) from a themeable set. Swap the whole language or single glyphs so
every component follows your iconography:

```kotlin
JengaTheme(
    icons = rememberJengaIconSet(
        chevron = MyChevron,
        trash = MyTrash,
    ),
    content = { App() },
)
```

Unset roles fall back to Jenga's own vectors. `JengaIcons` is the bundled catalogue you
can draw with, but it is optional; any `ImageVector` works.

## L2: seed plus a targeted override

Every derived token is also an override argument on `JengaTheme(brand = …)`:

```kotlin
JengaTheme(
    brand = jengaBrand(
        seed = Color(
            color = 0xFF6D28D9,
        ),
    ),
    shapes = jengaBrand(
        seed = Color(
            color = 0xFF6D28D9,
        ),
    ).shapes().copy(
        control = RoundedCornerShape(
            size = 4.dp,
        ),
    ),
    content = { App() },
)
```

## L3: full control

Override a few roles off a base, or hand-author both schemes as one pair. The
light/dark selection is handled for you.

```kotlin
// tweak a few roles off a base
JengaTheme(
    colors = jengaLightColors().copy(
        brand = Color(
            color = 0xFFFF5A1F,
        ),
    ),
    content = { App() },
)

// a complete custom light + dark scheme
JengaTheme(
    scheme = JengaScheme(
        light = jengaLightColors().copy(
            /* your roles */
        ),
        dark = jengaDarkColors().copy(
            /* your roles */
        ),
    ),
    content = { App() },
)
```

`JengaColors` has one role per semantic slot (`brand`, `surface`, `textPrimary`,
`border`, the status roles, and so on). Overriding via `copy(...)` keeps every role
you don't name.

## Component styles

Tokens set the palette, type and shapes. To change how a component looks everywhere,
for example a squarer button or a tighter card, set its style once in the theme
instead of passing the same arguments on every call:

```kotlin
JengaTheme(
    brand = jengaBrand(
        seed = Color(
            color = 0xFF6D28D9,
        ),
    ),
    components = JengaComponentStyles(
        button = JengaButtonStyle(
            medium = JengaButtonSizeStyle(
                shape = RoundedCornerShape(
                    size = 4.dp,
                ),
                minHeight = 44.dp,
            ),
        ),
        card = JengaCardStyle(
            contentPadding = PaddingValues(
                all = 12.dp,
            ),
        ),
    ),
    content = { App() },
)
```

The order is always: a value passed to a call wins, then the theme style, then
Jenga's default. Every field is optional, so an unset field keeps the default.

Components with a style: button, icon button, floating action button, link, text
field, search field, search trigger, list item, selection list item, key value row,
card, card footer, chip, segmented control, stepper, dialog, snackbar, list sheet,
dropdown menu, top app bar, tabs, navigation rail item, status pill, info bar,
avatar, icon tile, thumbnail strip, wheel picker (also the time input and date-of-birth
wheels), empty and error states, linear and circular
progress.

## Reading tokens

Inside a theme, read tokens off `JengaTheme.*`, never inline a raw value:

```kotlin
JengaText(
    color = JengaTheme.colors.textPrimary,
    style = JengaTheme.typography.bodyMedium,
)
Spacer(
    modifier = Modifier.height(
        height = JengaTheme.spacing.lg,
    ),
)
JengaIcon(
    imageVector = JengaTheme.icons.search,
    contentDescription = "Search",
)
```
