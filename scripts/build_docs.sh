#!/bin/sh
set -eu

./gradlew :jenga:dokkaGenerateHtml
rm -rf docs/api
cp -r jenga/build/dokka/html docs/api
cp CHANGELOG.md docs/changelog.md

./gradlew :catalog:exportDocs

mkdocs build --strict
node scripts/highlight/highlight.mjs site
