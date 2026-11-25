package io.github.joelkanyi.jenga.component.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaComponentStyles
import io.github.joelkanyi.jenga.theme.JengaTheme

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaAvatarPreview() {
    JengaTheme { AvatarShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaAvatarRtlPreview() {
    JengaTheme { RtlPreview { AvatarShowcase() } }
}

@Composable
private fun AvatarShowcase() {
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JengaAvatar(name = "Joel Kanyi", size = JengaAvatarSize.Small)
        JengaAvatar(name = "Joel Kanyi", size = JengaAvatarSize.Medium)
        JengaAvatar(name = "Ada Lovelace", size = JengaAvatarSize.Large)
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaAvatarCustomSizePreview() {
    JengaTheme {
        Row(
            modifier = Modifier.background(JengaTheme.colors.background).padding(JengaTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            JengaAvatar(
                name = "Peter Kamau",
                diameter = 42.dp,
                textStyle = JengaTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            )
            JengaAvatar(
                name = "Grace Wanjiru",
                diameter = 36.dp,
                textStyle = JengaTheme.typography.caption,
            )
        }
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaAvatarNeutralColorsPreview() {
    JengaTheme { AvatarNeutralShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaAvatarNeutralColorsRtlPreview() {
    JengaTheme { RtlPreview { AvatarNeutralShowcase() } }
}

@Composable
private fun AvatarNeutralShowcase() {
    // On a surface, as in a list row or card: in the light scheme the neutral fill matches the
    // page background.
    Row(
        modifier = Modifier
            .background(JengaTheme.colors.surface)
            .padding(JengaTheme.spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JengaAvatar(
            name = "Jane Wanjiru",
            size = JengaAvatarSize.Small,
            colors = JengaAvatarDefaults.neutralColors(),
        )
        JengaAvatar(
            name = "Jane Wanjiru",
            size = JengaAvatarSize.Medium,
            colors = JengaAvatarDefaults.neutralColors(),
        )
        JengaAvatar(
            name = "Jane Wanjiru",
            size = JengaAvatarSize.Large,
            colors = JengaAvatarDefaults.neutralColors(),
        )
        // No name yet: an icon in the content slot takes the same colors.
        JengaAvatar(colors = JengaAvatarDefaults.neutralColors()) {
            JengaIcon(imageVector = JengaIcons.User, contentDescription = null)
        }
    }
}

@JengaBlockPreviews
@Composable
internal fun JengaAvatarCustomColorsPreview() {
    JengaTheme {
        Column(
            modifier = Modifier.background(JengaTheme.colors.surface).padding(JengaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
        ) {
            // Colors passed to each call.
            Row(
                horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                JengaAvatar(
                    name = "Peter Kamau",
                    colors = JengaAvatarColors(
                        container = JengaTheme.colors.infoContainer,
                        content = JengaTheme.colors.onInfoContainer,
                    ),
                )
                JengaAvatar(
                    name = "Grace Wanjiru",
                    colors = JengaAvatarDefaults.colors().copy(
                        container = JengaTheme.colors.surfaceSunk,
                    ),
                )
                JengaAvatar(
                    colors = JengaAvatarColors(
                        container = JengaTheme.colors.ink,
                        content = JengaTheme.colors.onInk,
                    ),
                ) {
                    JengaIcon(imageVector = JengaIcons.User, contentDescription = null)
                }
            }
            // Colors set once on the theme: initials, content slot, then a call that overrides them.
            JengaTheme(
                colors = JengaTheme.colors,
                components = JengaComponentStyles(
                    avatar = JengaAvatarStyle(colors = JengaAvatarDefaults.neutralColors()),
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(JengaTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    JengaAvatar(name = "Amina Otieno")
                    JengaAvatar {
                        JengaIcon(imageVector = JengaIcons.User, contentDescription = null)
                    }
                    JengaAvatar(
                        name = "Joel Kanyi",
                        colors = JengaAvatarColors(
                            container = JengaTheme.colors.brandSubtle,
                            content = JengaTheme.colors.onBrandSubtle,
                        ),
                    )
                }
            }
        }
    }
}
