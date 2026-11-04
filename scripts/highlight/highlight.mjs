import { readFileSync, writeFileSync, readdirSync, statSync } from 'node:fs'
import { join } from 'node:path'
import { createHighlighter } from 'shiki'

const theme = (name, colors) => ({
  name,
  type: name.endsWith('dark') ? 'dark' : 'light',
  colors: { 'editor.foreground': colors.text, 'editor.background': '#00000000' },
  tokenColors: [
    { scope: ['comment'], settings: { foreground: colors.comment } },
    { scope: ['keyword', 'storage'], settings: { foreground: colors.keyword } },
    { scope: ['string'], settings: { foreground: colors.string } },
    { scope: ['constant.numeric', 'constant.language'], settings: { foreground: colors.number } },
    { scope: ['entity.name.type.annotation', 'meta.annotation'], settings: { foreground: colors.annotation } },
    { scope: ['entity.name.function.declaration'], settings: { foreground: colors.declaration } },
    { scope: ['entity.name.function.call'], settings: { foreground: colors.call } },
    { scope: ['keyword.operator', 'punctuation'], settings: { foreground: colors.text } },
  ],
})

const light = theme('jenga-light', {
  text: '#080808', comment: '#8C8C8C', keyword: '#0033B3', string: '#067D17', number: '#1750EB',
  annotation: '#9E880D', declaration: '#00627A', call: '#00627A',
})
const dark = theme('jenga-dark', {
  text: '#BCBEC4', comment: '#7A7E85', keyword: '#CF8E6D', string: '#6AAB73', number: '#2AACB8',
  annotation: '#B3AE60', declaration: '#56A8F5', call: '#56A8F5',
})

const languages = ['kotlin', 'bash', 'json', 'yaml', 'groovy']
const highlighter = await createHighlighter({ themes: [light, dark], langs: languages })

const unescape = (html) => html
  .replace(/<[^>]+>/g, '')
  .replaceAll('&lt;', '<').replaceAll('&gt;', '>').replaceAll('&quot;', '"').replaceAll('&#39;', "'").replaceAll('&amp;', '&')

const block = /<code class="language-([a-z]+)">([\s\S]*?)<\/code>/g

function highlightPage(html) {
  return html.replace(block, (whole, lang, body) => {
    if (!languages.includes(lang)) return whole
    const rendered = highlighter.codeToHtml(unescape(body), {
      lang,
      themes: { light: 'jenga-light', dark: 'jenga-dark' },
      defaultColor: false,
    })
    const inner = rendered.match(/<code>([\s\S]*)<\/code>/)[1]
    return `<code class="language-${lang} shiki">${inner}</code>`
  })
}

function htmlFiles(dir) {
  return readdirSync(dir).flatMap((name) => {
    const path = join(dir, name)
    if (name === 'api') return []
    if (statSync(path).isDirectory()) return htmlFiles(path)
    return name.endsWith('.html') ? [path] : []
  })
}

for (const file of htmlFiles(process.argv[2])) {
  const html = readFileSync(file, 'utf8')
  const highlighted = highlightPage(html)
  if (highlighted !== html) writeFileSync(file, highlighted)
}
