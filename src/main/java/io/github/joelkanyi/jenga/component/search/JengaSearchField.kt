package io.github.joelkanyi.jenga.component.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.joelkanyi.jenga.component.icon.JengaIcon
import io.github.joelkanyi.jenga.component.icon.JengaIcons
import io.github.joelkanyi.jenga.component.textfield.JengaTextField
import io.github.joelkanyi.jenga.core.preview.JengaBlockPreviews
import io.github.joelkanyi.jenga.core.preview.RtlPreview
import io.github.joelkanyi.jenga.theme.JengaTheme

/**
 * A search input — a pill-shaped [JengaTextField] with a leading search icon and
 * a clear button that appears once there's text.
 *
 * @sample io.github.joelkanyi.jenga.samples.JengaSearchFieldSample
 *
 * @param value the current query.
 * @param onValueChange called when the query changes.
 * @param modifier the [Modifier] for this field.
 * @param placeholder hint shown when empty.
 * @param enabled whether the field is editable.
 * @param onClear called when the clear button is tapped; defaults to clearing the text.
 */
@Composable
public fun JengaSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    enabled: Boolean = true,
    onClear: (() -> Unit)? = null,
) {
    JengaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        singleLine = true,
        shape = JengaTheme.shapes.pill,
        leadingIcon = { JengaIcon(JengaIcons.Search, contentDescription = null) },
        trailingIcon = if (value.isNotEmpty()) {
            {
                JengaIcon(
                    imageVector = JengaIcons.Close,
                    contentDescription = "Clear search",
                    modifier = Modifier.clickable {
                        if (onClear != null) onClear() else onValueChange("")
                    },
                )
            }
        } else {
            null
        },
    )
}

// ---- Previews --------------------------------------------------------------

@JengaBlockPreviews
@Composable
internal fun JengaSearchFieldPreview() {
    JengaTheme { SearchShowcase() }
}

@Preview(name = "RTL", showBackground = true)
@Composable
internal fun JengaSearchFieldRtlPreview() {
    JengaTheme { RtlPreview { SearchShowcase() } }
}

@Composable
private fun SearchShowcase() {
    Column(
        modifier = Modifier
            .background(JengaTheme.colors.background)
            .padding(JengaTheme.spacing.xl),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(JengaTheme.spacing.md),
    ) {
        JengaSearchField(value = "", onValueChange = {})
        JengaSearchField(value = "Lovelace", onValueChange = {})
    }
}
