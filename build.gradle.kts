// Top-level build file for the standalone Jenga design system.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.binary.compatibility.validator)
}

// Public-API tracking for the published library. Only :jenga is validated;
// the catalog is a consumer. Run `./gradlew apiDump` after intentional API
// changes; CI runs `apiCheck`.
apiValidation {
    ignoredProjects += listOf("catalog")
}
