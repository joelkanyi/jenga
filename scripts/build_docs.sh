#!/bin/sh
set -eu

./gradlew :jenga:dokkaGenerateHtml
rm -rf docs/api
cp -r jenga/build/dokka/html docs/api
cp CHANGELOG.md docs/changelog.md

./gradlew :catalog:exportDocs

./gradlew :catalog:wasmJsBrowserDistribution
python3 scripts/preview_bundle.py catalog/build/dist/wasmJs/productionExecutable docs/preview scripts/preview-size-baseline.txt

mkdocs build --strict
node scripts/highlight/highlight.mjs site
