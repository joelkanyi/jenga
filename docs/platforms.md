# Platforms

| Android | iOS | Desktop (JVM) | Web |
|---------|-----|---------------|-----|
| `minSdk 23` | `iosArm64`, `iosSimulatorArm64` | yes | `wasmJs` |

The whole public API lives in `commonMain`, so every platform shares the same components and
tokens. The Intel iOS simulator slice (`iosX64`) is not shipped, because Compose Multiplatform
no longer publishes it.

`JengaImage` loads images from a URL on Android only (through Coil 3, using your app's image
loader). On iOS, Desktop and Web it shows its placeholder for now.
