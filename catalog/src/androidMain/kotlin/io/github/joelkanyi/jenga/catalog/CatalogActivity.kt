package io.github.joelkanyi.jenga.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Host activity for the Jenga catalog: a browsable gallery of every Jenga
 * foundation token (and, in later phases, every component) rendered in all
 * states with a light/dark toggle.
 */
class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CatalogApp() }
    }
}
