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
    val demo: @Composable () -> Unit,
) {
    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim().lowercase()
        return name.lowercase().contains(q) ||
            group.lowercase().contains(q) ||
            keywords.lowercase().contains(q)
    }
}

/**
 * A single entry, shadcn-style: the component name with a Preview/Code toggle,
 * then one bordered frame that shows either the live demo (centered, with room to
 * breathe) or its copy-paste code.
 */
@Composable
fun CatalogEntryCard(entry: CatalogEntry, modifier: Modifier = Modifier) {
    var showCode by remember { mutableStateOf(false) }
    JengaStack(modifier = modifier, space = JengaTheme.spacing.md) {
        Row(verticalAlignment = Alignment.Top) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(JengaTheme.spacing.xxs),
            ) {
                JengaText(entry.name, style = JengaTheme.typography.titleMedium)
                JengaText(
                    entry.description,
                    style = JengaTheme.typography.bodySmall,
                    color = JengaTheme.colors.textMuted,
                )
            }
            PreviewCodeToggle(showCode = showCode, onChange = { showCode = it })
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(JengaTheme.shapes.card)
                .border(1.dp, JengaTheme.colors.border, JengaTheme.shapes.card),
        ) {
            if (showCode) {
                CatalogCode(entry.code, framed = false)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JengaTheme.colors.surface)
                        .defaultMinSize(minHeight = 168.dp)
                        .padding(JengaTheme.spacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    entry.demo()
                }
            }
        }
    }
}

@Composable
private fun PreviewCodeToggle(showCode: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .clip(JengaTheme.shapes.pill)
            .background(JengaTheme.colors.surfaceSunk)
            .padding(2.dp),
    ) {
        ToggleChip("Preview", selected = !showCode) { onChange(false) }
        ToggleChip("Code", selected = showCode) { onChange(true) }
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    JengaText(
        text = label,
        style = JengaTheme.typography.caption,
        color = if (selected) JengaTheme.colors.textPrimary else JengaTheme.colors.textMuted,
        modifier = Modifier
            .clip(JengaTheme.shapes.pill)
            .background(if (selected) JengaTheme.colors.surface else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = JengaTheme.spacing.md, vertical = JengaTheme.spacing.xs),
    )
}
