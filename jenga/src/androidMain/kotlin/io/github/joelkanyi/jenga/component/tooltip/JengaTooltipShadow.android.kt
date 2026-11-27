package io.github.joelkanyi.jenga.component.tooltip

import android.os.Build

// Up to Android 9 an outline built from a path casts a shadow only when the path is convex.
internal actual val elevationShadowFollowsConcaveOutlines: Boolean = Build.VERSION.SDK_INT > Build.VERSION_CODES.P
