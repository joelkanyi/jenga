pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    // PREFER_SETTINGS (not FAIL_ON_PROJECT_REPOS): the Kotlin/Wasm + Kotlin/JS plugin
    // unconditionally adds a project-level Node.js distribution repo, which the strict
    // mode rejects. PREFER_SETTINGS tolerates it while still preferring the repositories
    // declared here, including the Node.js/Yarn Ivy repos below that actually serve the
    // toolchain binaries.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://androidx.dev/snapshots/builds/13508953/artifacts/repository")
        }
        // Kotlin/Wasm + Kotlin/JS toolchain: the Node.js and Yarn distributions are
        // downloaded as Ivy artifacts, served from these repositories.
        ivy {
            name = "Node.js Distributions"
            setUrl("https://nodejs.org/dist")
            patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
            metadataSources { artifact() }
            content { includeModule("org.nodejs", "node") }
        }
        ivy {
            name = "Yarn Distributions"
            setUrl("https://github.com/yarnpkg/yarn/releases/download")
            patternLayout { artifact("v[revision]/[artifact]-v[revision].[ext]") }
            metadataSources { artifact() }
            content { includeModule("com.yarnpkg", "yarn") }
        }
    }
}

rootProject.name = "jenga"
include(":jenga")
include(":catalog")
