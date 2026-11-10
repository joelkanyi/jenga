package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.component.layout.JengaStack
import io.github.joelkanyi.jenga.component.text.JengaText
import io.github.joelkanyi.jenga.theme.JengaTheme

enum class CatalogStage {
    Inline,
    Overlay,
}

/**
 * One catalog entry: a named component paired with the code that produces it.
 * [demo] renders the live component; [code] is the copy-paste call that matches it.
 * [description] is the one-line summary under the name; [group] and [keywords]
 * drive the search index.
 */
@Immutable
class CatalogEntry(
    val name: String,
    val group: String,
    val description: String,
    val code: String,
    val keywords: String = "",
    val stage: CatalogStage = CatalogStage.Inline,
    val demo: @Composable (startOpen: Boolean) -> Unit,
) {
    val slug: String
        get() = name.lowercase().replace(
            regex = Regex(
                pattern = "[^a-z0-9]+",
            ),
            replacement = "-",
        ).trim(
            '-',
        )

    fun matches(
        query: String,
    ): Boolean {
        if (query.isBlank()) return true
        val q = query.trim().lowercase()
        return name.lowercase().contains(
            other = q,
        ) ||
            group.lowercase().contains(
                other = q,
            ) ||
            keywords.lowercase().contains(
                other = q,
            )
    }
}

/**
 * A single entry: the component name with a Preview/Code toggle,
 * then one bordered frame that shows either the live demo (centered, with room to
 * breathe) or its copy-paste code.
 */
@Composable
fun CatalogEntryCard(
    entry: CatalogEntry,
    modifier: Modifier = Modifier,
) {
    var showCode by remember {
        mutableStateOf(
            value = false,
        )
    }
    JengaStack(
        modifier = modifier,
        space = JengaTheme.spacing.md,
        content = {
            Row(
                verticalAlignment = Alignment.Top,
                content = {
                    Column(
                        modifier = Modifier.weight(
                            weight = 1f,
                        ),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            space = JengaTheme.spacing.xxs,
                        ),
                        content = {
                            JengaText(
                                text = entry.name,
                                style = JengaTheme.typography.titleMedium,
                            )
                            JengaText(
                                text = entry.description,
                                style = JengaTheme.typography.bodySmall,
                                color = JengaTheme.colors.textMuted,
                            )
                        },
                    )
                    PreviewCodeToggle(
                        showCode = showCode,
                        onChange = { showCode = it },
                    )
                },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        shape = JengaTheme.shapes.card,
                    )
                    .border(
                        width = 1.dp,
                        color = JengaTheme.colors.border,
                        shape = JengaTheme.shapes.card,
                    ),
                content = {
                    if (showCode) {
                        CatalogCode(
                            code = entry.code,
                            framed = false,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = JengaTheme.colors.surface,
                                )
                                .defaultMinSize(
                                    minHeight = 168.dp,
                                )
                                .padding(
                                    all = JengaTheme.spacing.xl,
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                entry.demo(false)
                            },
                        )
                    }
                },
            )
        },
    )
}

@Composable
private fun PreviewCodeToggle(
    showCode: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(
                shape = JengaTheme.shapes.pill,
            )
            .background(
                color = JengaTheme.colors.surfaceSunk,
            )
            .padding(
                all = 2.dp,
            ),
        content = {
            ToggleChip(
                label = "Preview",
                selected = !showCode,
                onClick = {
                    onChange(
                        false,
                    )
                },
            )
            ToggleChip(
                label = "Code",
                selected = showCode,
                onClick = {
                    onChange(
                        true,
                    )
                },
            )
        },
    )
}

@Composable
private fun ToggleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    JengaText(
        text = label,
        style = JengaTheme.typography.caption,
        color = if (selected) JengaTheme.colors.textPrimary else JengaTheme.colors.textMuted,
        modifier = Modifier
            .clip(
                shape = JengaTheme.shapes.pill,
            )
            .background(
                color = if (selected) JengaTheme.colors.surface else Color.Transparent,
            )
            .clickable { onClick() }
            .padding(
                horizontal = JengaTheme.spacing.md,
                vertical = JengaTheme.spacing.xs,
            ),
    )
}
