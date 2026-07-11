package io.github.joelkanyi.jenga

import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * The token lint (a build-time quality gate, like [JengaContrastTest]): Jenga's component
 * blocks must express colour only through the semantic tokens ([io.github.joelkanyi.jenga.theme.JengaTheme]
 * colours / [io.github.joelkanyi.jenga.foundation.color.JengaColors]), never a raw literal.
 * A raw `Color(0x…)`, an integer-channel `Color(…)`, or a named `Color.Red` in a component
 * defeats theming (light/dark, brand reseeding) and the WCAG contrast guarantees, so it fails
 * the build here with the offending file and line.
 *
 * Colour VALUES live only in the foundation palette; components read tokens. dp literals are
 * deliberately NOT banned here - a design system *defines* dimensions (a component's
 * `*Defaults` legitimately holds `4.dp`); the "no raw dp" rule is a *consumer* rule enforced
 * in the app, not on Jenga's own internals.
 */
class JengaTokenLintTest {

    // Raw colour forms: hex ARGB `Color(0x…)`, integer channels `Color(12, …)`, and the
    // named Compose colours. `Color.Unspecified` / `Color.Transparent` are sentinels, not
    // values, so they are allowed.
    private val rawColor = Regex(
        """Color\(\s*0x[0-9A-Fa-f]|Color\(\s*\d|Color\.(Red|Blue|Green|Yellow|Magenta|Cyan|Black|White|Gray|Grey|LightGray|DarkGray)\b""",
    )

    @Test
    fun componentsUseColourTokensNotRawLiterals() {
        val componentDir = File(sourceRoot(), "jenga/src/commonMain/kotlin/io/github/joelkanyi/jenga/component")
        require(componentDir.isDirectory) { "component source not found at $componentDir" }

        val offenders = mutableListOf<String>()
        componentDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                file.readLines().forEachIndexed { i, line ->
                    val code = line.substringBefore("//")
                    if (rawColor.containsMatchIn(code)) {
                        offenders += "${file.name}:${i + 1}  ${line.trim()}"
                    }
                }
            }

        if (offenders.isNotEmpty()) {
            fail(
                "Raw colour literal in a Jenga component. Colour must come from JengaTheme.colors / " +
                    "JengaColors tokens, never a literal (theming + WCAG contrast depend on it).\n" +
                    offenders.joinToString("\n"),
            )
        }
    }

    /** Walk up from the test working dir to the repo root (the one holding settings.gradle.kts). */
    private fun sourceRoot(): File {
        var dir: File? = File(System.getProperty("user.dir") ?: ".").absoluteFile
        while (dir != null) {
            if (File(dir, "settings.gradle.kts").isFile && File(dir, "jenga").isDirectory) return dir
            dir = dir.parentFile
        }
        error("repo root (settings.gradle.kts) not found from ${System.getProperty("user.dir")}")
    }
}
