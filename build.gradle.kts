@file:OptIn(kotlinx.validation.ExperimentalBCVApi::class)

// Top-level build file for the standalone Jenga design system.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.poko) apply false
    alias(libs.plugins.binary.compatibility.validator)
}

// Public-API tracking for the published library. Only :jenga is validated;
// the catalog is a consumer. Run `./gradlew apiDump` after intentional API
// changes; CI runs `apiCheck`.
apiValidation {
    ignoredProjects += listOf("catalog")
    // The library ships native (iOS) targets, so validate the KLib ABI too;
    // JVM `.api` dumps alone do not cover the native public surface.
    klib {
        enabled = true
    }
}
