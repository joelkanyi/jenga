package io.github.joelkanyi.jenga.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * The opening surface: what Jenga is, the dependency to add, and the two ways to
 * turn it on (defaults, or a brand derived from one seed).
 */
@Composable
fun GetStartedSection(modifier: Modifier = Modifier) {
    JengaStack(modifier = modifier, space = JengaTheme.spacing.xxl) {
        JengaStack(space = JengaTheme.spacing.sm) {
            JengaText("Build once, rebrand from one seed", style = JengaTheme.typography.headingMedium)
            JengaText(
                "Jenga is a Compose Multiplatform design system. Every component reads from theme tokens, so a single brand seed derives a coherent, accessible light and dark theme across the whole set.",
                style = JengaTheme.typography.bodyLarge,
                color = JengaTheme.colors.textSecondary,
            )
        }

        Doc(
            "1. Add the dependency",
            "Available on Maven Central.",
            """
            dependencies {
                implementation("io.github.joelkanyi:jenga:0.3.0")
            }
            """.trimIndent(),
        )

        Doc(
            "2. Wrap your app",
            "JengaTheme provides the tokens every component reads from.",
            """
            JengaTheme {
                JengaButton("Get started", onClick = {})
            }
            """.trimIndent(),
        )

        Doc(
            "3. Make it yours",
            "Pass a brand to re-theme everything from one seed. See the Brand tab to try it live.",
            """
            val brand = jengaBrand(seed = Color(0xFF6C5CE7))

            JengaTheme(brand = brand) {
                App()
            }
            """.trimIndent(),
        )
    }
}

@Composable
private fun Doc(title: String, description: String, code: String) {
    JengaStack(space = JengaTheme.spacing.md) {
        JengaText(title, style = JengaTheme.typography.titleLarge)
        JengaText(description, style = JengaTheme.typography.bodyMedium, color = JengaTheme.colors.textMuted)
        CatalogCode(code)
    }
}
