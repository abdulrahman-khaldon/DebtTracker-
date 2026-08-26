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
import androidx.compose.ui.platform.LocalConfiguration
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
 * Root composable: applies language, layout direction, and dark mode theme.
 */
@Composable
private fun DebtTrackerApp(settings: AppSettings) {
    val locale = settings.language.locale
    val currentConfig = LocalConfiguration.current

    val localeConfig = remember(locale, currentConfig) {
        Configuration(currentConfig).apply {
            setLocale(locale)
        }
    }

    val darkTheme = settings.darkModeOverride ?: isSystemInDarkTheme()
    val layoutDirection = if (settings.language == AppLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalConfiguration provides localeConfig,
        LocalLayoutDirection provides layoutDirection
    ) {
        DebtTrackerTheme(darkTheme = darkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost()
            }
        }
    }
}

