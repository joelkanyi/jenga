package io.github.joelkanyi.jenga.catalog

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * iOS entry point. The `iosApp` Xcode project embeds the `CatalogApp` framework and
 * hosts this view controller from SwiftUI (see iosApp/ContentView.swift).
 */
fun MainViewController(): UIViewController = ComposeUIViewController { CatalogApp() }
