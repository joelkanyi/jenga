#!/bin/sh
set -eu

./gradlew :jenga:dokkaGenerateHtml

rm -rf docs/api docs/assets/previews
cp -r jenga/build/dokka/html docs/api
cp CHANGELOG.md docs/changelog.md

mkdir -p docs/assets/previews
for file in jenga/src/androidUnitTest/screenshots/*PreviewsKt.*Preview.Light_WITH_BACKGROUND.png; do
    name=${file##*PreviewsKt.}
    cp "$file" "docs/assets/previews/${name%.Light_WITH_BACKGROUND.png}.png"
done
for file in jenga/src/androidUnitTest/screenshots/*PreviewsKt.*Preview.Dark_WITH_BACKGROUND_NIGHT.png; do
    name=${file##*PreviewsKt.}
    cp "$file" "docs/assets/previews/${name%.Dark_WITH_BACKGROUND_NIGHT.png}-dark.png"
done

mkdocs "$@"
