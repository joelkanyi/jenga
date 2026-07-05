import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.dokka)
}

android {
    namespace = "io.github.joelkanyi.jenga"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    lint {
        warningsAsErrors = false
        // Conflicts with our setup: previews must be `internal` (not private) for
        // ComposablePreviewScanner, and a design system legitimately defines
        // CompositionLocals for its theme.
        disable += setOf(
            "ComposePreviewPublic",
            "ComposePreviewNaming",
            "ComposeCompositionLocalUsage",
        )
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                // Improves Roborazzi screenshot fidelity (Robolectric 4.12.2+).
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
            }
        }
    }
}

kotlin {
    // Public API of a published library must be explicit: every public/protected
    // declaration needs an explicit visibility modifier and return type.
    explicitApi()

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Auto-generate Roborazzi screenshot tests from every @Preview in the library.
// Goldens are committed under src/test/screenshots so CI can `verify` against them.
roborazzi {
    outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("io.github.joelkanyi.jenga")
        // Cross-OS-tolerant screenshot comparison (macOS dev vs Linux CI).
        testerQualifiedClassName = "io.github.joelkanyi.jenga.JengaPreviewTester"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    // Exposed as `api` because Jenga's public API surfaces Compose types
    // (Color, Modifier, Dp, TextStyle, @Composable, RowScope, BorderStroke …),
    // so consumers must receive these transitively.
    api(composeBom)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.text)
    api(libs.androidx.compose.foundation)

    // Material 3 is an internal implementation detail (the bridge for ripple,
    // text selection and any reused M3 primitive). Not part of Jenga's API.
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Machine-enforced Compose API conventions (param order, modifier rules,
    // naming) — Slack's compose-lints, run via `./gradlew :jenga:lint`.
    lintChecks(libs.compose.lint.checks)

    // Screenshot testing (JVM, no emulator) — runs under JDK 17 by pinning
    // Robolectric to SDK 35 via src/test/resources/robolectric.properties.
    testImplementation(composeBom)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.roborazzi.compose.preview.scanner.support)
    testImplementation(libs.composable.preview.scanner)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.tooling)
}

// ---- Design-token pipeline -------------------------------------------------
// `tokens/primitives.json` is the single source of truth (a Figma -> JSON export
// stand-in). This task regenerates the primitive layer (JengaPalette.kt) from it,
// committing the output (the Bolt "generate -> PR" model). Run:
//   ./gradlew :jenga:generateJengaTokens
tasks.register("generateJengaTokens") {
    group = "jenga"
    description = "Regenerates JengaPalette.kt from tokens/primitives.json."
    val tokensFile = layout.projectDirectory.file("tokens/primitives.json")
    val outFile = layout.projectDirectory.file(
        "src/main/java/io/github/joelkanyi/jenga/foundation/color/JengaPalette.kt",
    )
    inputs.file(tokensFile)
    outputs.file(outFile)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val root = groovy.json.JsonSlurper().parse(tokensFile.asFile) as Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val colors = root["color"] as Map<String, String>
        val out = buildString {
            appendLine("package io.github.joelkanyi.jenga.foundation.color")
            appendLine()
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine()
            appendLine("/**")
            appendLine(" * GENERATED FILE — DO NOT EDIT BY HAND.")
            appendLine(" *")
            appendLine(" * Regenerate with `./gradlew :jenga:generateJengaTokens` after editing")
            appendLine(" * `tokens/primitives.json` (the design-token source of truth). This is the")
            appendLine(" * **primitive** color layer — raw values with no meaning; the semantic layer")
            appendLine(" * [JengaColors] maps these onto roles.")
            appendLine(" */")
            appendLine("internal object JengaPalette {")
            colors.forEach { (name, hex) ->
                val argb = "0xFF" + hex.removePrefix("#").uppercase()
                appendLine("    val $name: Color = Color($argb)")
            }
            appendLine("}")
        }
        outFile.asFile.writeText(out)
        logger.lifecycle("generateJengaTokens: wrote ${colors.size} primitives to ${outFile.asFile.name}")
    }
}

// ---- Token-usage enforcement -----------------------------------------------
// Fails the build if component/pattern code hardcodes raw `Color(0x…)` literals
// instead of reading JengaTheme.colors tokens (the #1 design-system audit
// failure; Glovo enforces the same). Primitives legitimately live in
// foundation/, which is exempt. Wired into `check` and CI.
tasks.register("checkJengaTokenUsage") {
    group = "verification"
    description = "Fails if component/pattern code uses raw Color(0x…) literals instead of tokens."
    val srcRoot = layout.projectDirectory.dir("src/main/java/io/github/joelkanyi/jenga")
    inputs.dir(srcRoot)
    doLast {
        val rootDirFile = srcRoot.asFile
        val hex = Regex("""Color\(\s*0x""")
        val offenders = mutableListOf<String>()
        rootDirFile.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val rel = file.relativeTo(rootDirFile).path.replace('\\', '/')
                val inComponentLayer = rel.startsWith("component/") || rel.startsWith("pattern/")
                if (inComponentLayer) {
                    file.readLines().forEachIndexed { index, line ->
                        if (hex.containsMatchIn(line)) {
                            offenders += "${file.relativeTo(rootDir)}:${index + 1}: ${line.trim()}"
                        }
                    }
                }
            }
        if (offenders.isNotEmpty()) {
            throw GradleException(
                "Raw Color(0x…) literals in component/pattern code — use JengaTheme.colors tokens:\n" +
                    offenders.joinToString("\n"),
            )
        }
        logger.lifecycle("checkJengaTokenUsage: no raw color literals in components ✓")
    }
}

tasks.named("check").configure { dependsOn("checkJengaTokenUsage") }
