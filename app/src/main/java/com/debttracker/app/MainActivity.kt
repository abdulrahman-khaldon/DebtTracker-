package com.debttracker.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debttracker.app.model.AppLanguage
import com.debttracker.app.model.AppSettings
import com.debttracker.app.ui.navigation.AppNavHost
import com.debttracker.app.ui.theme.DebtTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.uiState.collectAsStateWithLifecycle()
            DebtTrackerApp(settings = settings)
        }
    }
}

/**
 * Root composable: wraps the whole app in a locale-overridden context so the
 * in-app language switch (Arabic/English) applies instantly without restarting
 * the activity, and sets the layout direction to match the selected language.
 */
@Composable
private fun DebtTrackerApp(settings: AppSettings) {
    val activityContext = LocalContext.current
    val locale = settings.language.locale

    val localeContext = remember(locale) {
        val configuration = Configuration(activityContext.resources.configuration)
        configuration.setLocale(locale)
        activityContext.createConfigurationContext(configuration)
    }

    val darkTheme = settings.darkModeOverride ?: isSystemInDarkTheme()
    val layoutDirection = if (settings.language == AppLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalContext provides localeContext) {
        DebtTrackerTheme(darkTheme = darkTheme) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
