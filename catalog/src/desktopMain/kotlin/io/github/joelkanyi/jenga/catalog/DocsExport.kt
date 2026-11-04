package io.github.joelkanyi.jenga.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.jenga.theme.JengaTheme
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.IRect
import org.jetbrains.skia.Image
import java.io.File
import java.nio.ByteBuffer

fun main(args: Array<String>) {
    val docs = File(args.first())
    val pages = File(docs, "components").apply { deleteRecursively(); mkdirs() }
    val images = File(docs, "assets/components").apply { deleteRecursively(); mkdirs() }
    val entries = componentEntries()
    for (entry in entries) {
        val light = renderSpecimen(entry, darkTheme = false)
        File(images, "${entry.slug}-light.png").writeBytes(light)
        File(images, "${entry.slug}-dark.png").writeBytes(renderSpecimen(entry, darkTheme = true))
        val displayWidth = pngWidth(light) / Scale.toInt()
        File(pages, "${entry.slug}.md").writeText(componentPage(entry, displayWidth, apiPath(File(docs, "api"), entry)))
    }
    File(pages, "index.md").writeText(indexPage(entries))
    File(pages, ".nav.yml").writeText(navFile(entries))
}

private val CatalogEntry.slug: String
    get() = name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

private val CatalogEntry.componentName: String?
    get() = Regex("""\b(Jenga[A-Z][A-Za-z]*)\s*[({]""").find(code)?.groupValues?.get(1)

@OptIn(ExperimentalComposeUiApi::class)
private fun renderSpecimen(entry: CatalogEntry, darkTheme: Boolean): ByteArray {
    var contentHeight = MaxHeightPx
    val scene = ImageComposeScene(width = WidthPx, height = MaxHeightPx, density = Density(Scale)) {
        Specimen(darkTheme = darkTheme, onHeight = { contentHeight = it }) { entry.demo() }
    }
    scene.render(0)
    val image = scene.render(SettleNanos)
    scene.close()
    return image.trimToContent(minOf(contentHeight, MaxHeightPx)).encode()
}

@Composable
private fun Specimen(darkTheme: Boolean, onHeight: (Int) -> Unit, content: @Composable () -> Unit) {
    JengaTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .width(FrameWidth)
                .onSizeChanged { onHeight(it.height) }
                .background(JengaTheme.colors.background)
                .padding(FramePadding),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

private fun Image.trimToContent(height: Int): Image {
    val full = Bitmap.makeFromImage(this)
    val background = full.getColor(0, 0)
    var left = width
    var top = height
    var right = 0
    var bottom = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (full.getColor(x, y) == background) continue
            left = minOf(left, x)
            top = minOf(top, y)
            right = maxOf(right, x)
            bottom = maxOf(bottom, y)
        }
    }
    if (right < left) return this
    val bounds = IRect.makeLTRB(
        maxOf(0, left - TrimMarginPx),
        maxOf(0, top - TrimMarginPx),
        minOf(width, right + TrimMarginPx),
        minOf(height, bottom + TrimMarginPx),
    )
    val cropped = Bitmap()
    full.extractSubset(cropped, bounds)
    return Image.makeFromBitmap(cropped)
}

private fun Image.encode(): ByteArray = requireNotNull(encodeToData(EncodedImageFormat.PNG)).bytes

private fun pngWidth(png: ByteArray): Int = ByteBuffer.wrap(png, PngWidthOffset, 4).int

private fun apiPath(apiDir: File, entry: CatalogEntry): String? {
    val name = entry.componentName ?: return null
    val folder = "-" + name.replace(Regex("([A-Z])")) { "-" + it.value.lowercase() }.removePrefix("-")
    val page = apiDir.walkTopDown().firstOrNull { it.isDirectory && it.name == folder } ?: return null
    return "../" + page.relativeTo(apiDir.parentFile).invariantSeparatorsPath + "/index.html"
}

private fun componentPage(entry: CatalogEntry, displayWidth: Int, apiPath: String?): String = buildString {
    appendLine("# ${entry.name}")
    appendLine()
    appendLine(entry.description)
    appendLine()
    appendLine("<figure class=\"jenga-specimen\" markdown>")
    appendLine("![${entry.name}](../assets/components/${entry.slug}-light.png#only-light){ width=\"$displayWidth\" }")
    appendLine("![${entry.name}](../assets/components/${entry.slug}-dark.png#only-dark){ width=\"$displayWidth\" }")
    appendLine("</figure>")
    appendLine()
    appendLine("```kotlin")
    appendLine(entry.code.trim())
    appendLine("```")
    if (apiPath != null) {
        appendLine()
        appendLine("[API reference for `${entry.componentName}`]($apiPath)")
    }
}

private fun indexPage(entries: List<CatalogEntry>): String = buildString {
    appendLine("# Components")
    appendLine()
    appendLine("Every Jenga component, with its code. Light and dark follow the site's theme.")
    for ((group, members) in entries.groupBy { it.group }) {
        appendLine()
        appendLine("## $group")
        appendLine()
        appendLine("<div class=\"jenga-cards\" markdown>")
        appendLine()
        members.forEach { appendLine(card(it)) }
        appendLine()
        appendLine("</div>")
    }
}

private fun card(entry: CatalogEntry): String =
    "[![${entry.name}](../assets/components/${entry.slug}-light.png#only-light){ loading=lazy }" +
        "![${entry.name}](../assets/components/${entry.slug}-dark.png#only-dark){ loading=lazy }" +
        "<strong>${entry.name}</strong><span>${entry.description}</span>](${entry.slug}.md){ .jenga-card }"

private fun navFile(entries: List<CatalogEntry>): String = buildString {
    appendLine("nav:")
    appendLine("  - index.md")
    for ((group, members) in entries.groupBy { it.group }) {
        appendLine("  - $group:")
        members.forEach { appendLine("      - ${it.slug}.md") }
    }
}

private const val Scale = 2f
private val FrameWidth = 400.dp
private val FramePadding = 32.dp
private const val WidthPx = 800
private const val MaxHeightPx = 2400
private const val TrimMarginPx = 48
private const val PngWidthOffset = 16
private const val SettleNanos = 1_000_000_000L
