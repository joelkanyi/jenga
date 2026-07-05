# Releasing Jenga to Maven Central

Jenga publishes to Maven Central through the [Central Portal](https://central.sonatype.com)
using the [vanniktech maven-publish plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/).

Coordinates: `io.github.joelkanyi:jenga:<version>`

The published artifact is public, even though this source repository is private. Only the
built library, sources jar and javadoc jar leave the repo.

## One-time setup

### 1. Claim the namespace

On the Central Portal, verify ownership of the `io.github.joelkanyi` namespace (it is tied to
the GitHub account `joelkanyi`, so the portal will ask you to create a verification repo or
add a TXT record). This is done once.

### 2. Create a Central Portal user token

Central Portal -> Account -> Generate User Token. You get a username and password pair.

### 3. Create a GPG signing key

```bash
gpg --gen-key                       # if you don't already have one
gpg --list-secret-keys --keyid-format short   # note the KEY_ID
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>   # publish the public key
# export the secret key in-memory form for Gradle:
gpg --armor --export-secret-keys <KEY_ID>
```

### 4. Put the credentials in `~/.gradle/gradle.properties`

Never commit these. Keep them in your home Gradle file (or pass them as
`ORG_GRADLE_PROJECT_*` environment variables in CI).

```properties
mavenCentralUsername=<central portal token username>
mavenCentralPassword=<central portal token password>

signingInMemoryKey=<the armored secret key from step 3, newlines as \n>
signingInMemoryKeyPassword=<the key passphrase>
```

A commented template lives in `gradle/publish.properties.template`.

## Cutting a release

1. Bump `version` in `jenga/build.gradle.kts` to the release version (no `-SNAPSHOT`).
2. Make sure everything is green:

   ```bash
   ./gradlew :jenga:check
   ```

3. Publish and release:

   ```bash
   ./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
   ```

   This builds every target (Android, Desktop, iOS), signs them, uploads to the Central
   Portal and triggers the release. It takes a few minutes.

4. Tag the release and bump `version` to the next `-SNAPSHOT`.

To publish a snapshot instead (no release, consumable from the Central snapshots repo), use a
`-SNAPSHOT` version and run `./gradlew publishToMavenCentral`.

## Consuming the published artifact

```kotlin
// settings.gradle.kts of the consumer (e.g. pikapal, ticketfiti-app)
dependencyResolutionManagement {
    repositories { mavenCentral() }
}

// a KMP module's build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.joelkanyi:jenga:0.1.0")
        }
    }
}
```

For an Android-only consumer, the same coordinate resolves the Android variant automatically.
