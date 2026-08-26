package com.debttracker.app.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.BuildConfig
import com.debttracker.app.data.csv.CsvExporter
import com.debttracker.app.data.repository.DebtRepository
import com.debttracker.app.data.repository.SettingsRepository
import com.debttracker.app.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvent {
    data object ExportSuccess : SettingsEvent
    data object ExportFailed : SettingsEvent
    data object DataCleared : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val debtRepository: DebtRepository,
    private val csvExporter: CsvExporter,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    data class UiState(
        val language: AppLanguage = AppLanguage.ARABIC,
        /** null = follow the system setting. */
        val darkModeOverride: Boolean? = null,
        val versionName: String = BuildConfig.VERSION_NAME
    )

    val uiState: StateFlow<UiState> = settingsRepository.settings
        .map { settings ->
            UiState(
                language = settings.language,
                darkModeOverride = settings.darkModeOverride
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState()
        )

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            debtRepository.clearAll()
            _events.emit(SettingsEvent.DataCleared)
        }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                appContext.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(csvExporter.build().toByteArray(Charsets.UTF_8))
                } ?: error("Could not open output stream for $uri")
            }.onSuccess {
                _events.emit(SettingsEvent.ExportSuccess)
            }.onFailure {
                _events.emit(SettingsEvent.ExportFailed)
            }
        }
    }
}
