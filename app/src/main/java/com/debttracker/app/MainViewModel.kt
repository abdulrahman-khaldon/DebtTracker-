package com.debttracker.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.data.repository.SettingsRepository
import com.debttracker.app.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    /** App-level settings (language + dark mode) applied at the activity root. */
    val uiState: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())
}
