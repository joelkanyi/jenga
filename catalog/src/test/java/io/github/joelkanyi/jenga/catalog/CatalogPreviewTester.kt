package io.github.joelkanyi.jenga.catalog

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.RoborazziOptions

/**
 * Same cross-OS-tolerant screenshot comparator as the library's tester, applied
 * to the catalog's own preview goldens. See `io.github.joelkanyi.jenga.JengaPreviewTester`.
 */
class CatalogPreviewTester :
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
