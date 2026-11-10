package io.github.joelkanyi.jenga.catalog

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.IRect
import org.jetbrains.skia.Image
import java.io.File
import java.nio.ByteBuffer

fun main(
    args: Array<String>,
) {
    val docs = File(
        args.first(),
    )
    val pages = File(
        docs,
        "components",
    ).apply {
        deleteRecursively()
        mkdirs()
    }
    val images = File(
        docs,
        "assets/components",
    ).apply {
        deleteRecursively()
        mkdirs()
    }
    val entries = componentEntries()
    for (entry in entries) {
        val light = renderSpecimen(
            entry = entry,
            darkTheme = false,
        )
        File(
            images,
            "${entry.slug}-light.png",
        ).writeBytes(
            array = light,
        )
        File(
            images,
            "${entry.slug}-dark.png",
        ).writeBytes(
            array = renderSpecimen(
                entry = entry,
                darkTheme = true,
            ),
        )
        val displayWidth = pngWidth(
            png = light,
        ) / Scale.toInt()
        File(
            pages,
            "${entry.slug}.md",
        ).writeText(
            text = componentPage(
                entry = entry,
                displayWidth = displayWidth,
                apiPath = apiPath(
                    apiDir = File(
                        docs,
                        "api",
                    ),
                    entry = entry,
                ),
            ),
        )
    }
    File(
        pages,
        "index.md",
    ).writeText(
        text = indexPage(
            entries = entries,
        ),
    )
    File(
        pages,
        ".nav.yml",
    ).writeText(
        text = navFile(
            entries = entries,
        ),
    )
}

private val CatalogEntry.componentName: String?
    get() = Regex(
        pattern = """\b(Jenga[A-Z][A-Za-z]*)\s*[({]""",
    ).find(
        input = code,
    )?.groupValues?.get(
        index = 1,
    )

@OptIn(ExperimentalComposeUiApi::class)
private fun renderSpecimen(
    entry: CatalogEntry,
    darkTheme: Boolean,
): ByteArray {
    var contentHeight = MaxHeightPx
    val scene = ImageComposeScene(
        width = WidthPx,
        height = MaxHeightPx,
        density = Density(
            density = Scale,
        ),
        content = {
            JengaTheme(
                darkTheme = darkTheme,
                content = {
                    SpecimenFrame(
                        modifier = Modifier.onSizeChanged {
                            contentHeight = it.height
                        },
                        content = { entry.demo() },
                    )
                },
            )
        },
    )
    scene.render(
        nanoTime = 0,
    )
    val image = scene.render(
        nanoTime = SettleNanos,
    )
    scene.close()
    return image.trimToContent(
        height = minOf(
            contentHeight,
            MaxHeightPx,
        ),
    ).encode()
}

private fun Image.trimToContent(
    height: Int,
): Image {
    val full = Bitmap.makeFromImage(
        image = this,
    )
    val background = full.getColor(
        x = 0,
        y = 0,
    )
    var left = width
    var top = height
    var right = 0
    var bottom = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (full.getColor(
                    x = x,
                    y = y,
                ) == background
            ) {
                continue
            }
            left = minOf(
                left,
                x,
            )
            top = minOf(
                top,
                y,
            )
            right = maxOf(
                right,
                x,
            )
            bottom = maxOf(
                bottom,
                y,
            )
        }
    }
    if (right < left) return this
    val bounds = IRect.makeLTRB(
        l = maxOf(
            0,
            left - TrimMarginPx,
        ),
        t = maxOf(
            0,
            top - TrimMarginPx,
        ),
        r = minOf(
            width,
            right + TrimMarginPx,
        ),
        b = minOf(
            height,
            bottom + TrimMarginPx,
        ),
    )
    val cropped = Bitmap()
    full.extractSubset(
        dst = cropped,
        subset = bounds,
    )
    return Image.makeFromBitmap(
        bitmap = cropped,
    )
}

private fun Image.encode(): ByteArray = requireNotNull(
    value = encodeToData(
        format = EncodedImageFormat.PNG,
    ),
).bytes

private fun pngWidth(
    png: ByteArray,
): Int = ByteBuffer.wrap(
    png,
    PngWidthOffset,
    4,
).int

private fun apiPath(
    apiDir: File,
    entry: CatalogEntry,
): String? {
    val name = entry.componentName ?: return null
    val folder = "-" + name.replace(
        regex = Regex(
            pattern = "([A-Z])",
        ),
        transform = { "-" + it.value.lowercase() },
    ).removePrefix(
        prefix = "-",
    )
    val page = apiDir.walkTopDown().firstOrNull { it.isDirectory && it.name == folder } ?: return null
    return "../" + page.relativeTo(
        base = apiDir.parentFile,
    ).invariantSeparatorsPath + "/index.html"
}

private fun componentPage(
    entry: CatalogEntry,
    displayWidth: Int,
    apiPath: String?,
): String = buildString {
    appendLine(
        value = "# ${entry.name}",
    )
    appendLine()
    appendLine(
        value = entry.description,
    )
    appendLine()
    appendLine(
        value = "<figure class=\"jenga-specimen jenga-live\" data-component=\"${entry.slug}\" markdown>",
    )
    appendLine(
        value = "![${entry.name}](../assets/components/${entry.slug}-light.png#only-light){ width=\"$displayWidth\" }",
    )
    appendLine(
        value = "![${entry.name}](../assets/components/${entry.slug}-dark.png#only-dark){ width=\"$displayWidth\" }",
    )
    appendLine(
        value = "</figure>",
    )
    appendLine()
    appendLine(
        value = "```kotlin",
    )
    appendLine(
        value = entry.code.trim(),
    )
    appendLine(
        value = "```",
    )
    if (apiPath != null) {
        appendLine()
        appendLine(
            value = "[API reference for `${entry.componentName}`]($apiPath)",
        )
    }
}

private fun indexPage(
    entries: List<CatalogEntry>,
): String = buildString {
    appendLine(
        value = "# Components",
    )
    appendLine()
    appendLine(
        value = "Every Jenga component, with its code. Light and dark follow the site's theme.",
    )
    for ((group, members) in entries.groupBy { it.group }) {
        appendLine()
        appendLine(
            value = "## $group",
        )
        appendLine()
        appendLine(
            value = "<div class=\"jenga-cards\" markdown>",
        )
        appendLine()
        members.forEach {
            appendLine(
                value = card(
                    entry = it,
                ),
            )
        }
        appendLine()
        appendLine(
            value = "</div>",
        )
    }
}

private fun card(
    entry: CatalogEntry,
): String = "[![${entry.name}](../assets/components/${entry.slug}-light.png#only-light){ loading=lazy }" +
    "![${entry.name}](../assets/components/${entry.slug}-dark.png#only-dark){ loading=lazy }" +
    "<strong>${entry.name}</strong><span>${entry.description}</span>](${entry.slug}.md){ .jenga-card }"

private fun navFile(
    entries: List<CatalogEntry>,
): String = buildString {
    appendLine(
        value = "nav:",
    )
    appendLine(
        value = "  - index.md",
    )
    for ((group, members) in entries.groupBy { it.group }) {
        appendLine(
            value = "  - $group:",
        )
        members.forEach {
            appendLine(
                value = "      - ${it.slug}.md",
            )
        }
    }
}

private const val Scale = 2f
private const val WidthPx = 800
private const val MaxHeightPx = 2400
private const val TrimMarginPx = 48
private const val PngWidthOffset = 16
private const val SettleNanos = 1_000_000_000L
