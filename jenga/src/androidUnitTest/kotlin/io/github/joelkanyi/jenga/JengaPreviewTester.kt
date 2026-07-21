package io.github.joelkanyi.jenga

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.RoborazziOptions

/**
 * Custom Roborazzi preview tester that adds a small per-pixel color tolerance to
 * screenshot comparison.
 *
 * Robolectric's native rendering differs by a hair across operating systems
 * (measured: <= 2/255 per channel between macOS dev machines and the Linux CI
 * runner, pure anti-aliasing / alpha-blend rounding, visually identical). With
 * the default pixel-perfect comparison that makes CI fail on goldens recorded on
 * a different OS. A `SimpleImageComparator(maxDistance)` absorbs that sub-pixel
 * noise while still failing on genuine visual changes (which differ by far more),
 * and keeps `verify` working on both macOS and Linux against one set of goldens.
 *
 * Wired via `roborazzi { generateComposePreviewRobolectricTests { testerQualifiedClassName = ... } }`.
 */
class JengaPreviewTester :
    ComposePreviewTester<ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter>
    by AndroidComposePreviewTester(TolerantCapturer)

private object TolerantCapturer : AndroidComposePreviewTester.Capturer {
    private val delegate = AndroidComposePreviewTester.DefaultCapturer()
    private val compareOptions = RoborazziOptions.CompareOptions(
        changeThreshold = 0.01f,
        imageComparator = SimpleImageComparator(maxDistance = 0.02f),
    )

    override fun capture(parameter: AndroidComposePreviewTester.CaptureParameter) {
        delegate.capture(
            AndroidComposePreviewTester.CaptureParameter(
                parameter.preview,
                parameter.filePath,
                parameter.roborazziComposeOptions,
                parameter.roborazziOptions.copy(compareOptions = compareOptions),
            ),
        )
    }
}
