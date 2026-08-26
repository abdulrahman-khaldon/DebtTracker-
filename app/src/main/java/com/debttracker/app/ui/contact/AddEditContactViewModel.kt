package com.debttracker.app.ui.contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.data.repository.DebtRepository
import com.debttracker.app.data.repository.SettingsRepository
import com.debttracker.app.model.AppLanguage
import com.debttracker.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val repository: DebtRepository,
    savedStateHandle: SavedStateHandle,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val contactId: Long = savedStateHandle.get<Long>(Screen.AddEditContact.ARG_CONTACT_ID)
        ?: Screen.AddEditContact.NEW_CONTACT

    data class UiState(
        val name: String = "",
        val phone: String = "",
        val isEdit: Boolean = false,
        val nameError: Boolean = false,
        val saved: Boolean = false,
        val arabicNumerals: Boolean = true,
        val locale: Locale = AppLanguage.ARABIC.locale
    )

    private val _uiState = MutableStateFlow(UiState(isEdit = contactId > 0L))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        if (contactId > 0L) {
            viewModelScope.launch {
                repository.getContact(contactId)?.let { contact ->
                    _uiState.update {
                        it.copy(name = contact.name, phone = contact.phone.orEmpty())
                    }
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.language.collect { language ->
                _uiState.update {
                    it.copy(
                        arabicNumerals = language == AppLanguage.ARABIC,
                        locale = language.locale
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        viewModelScope.launch {
            val phone = state.phone.trim().ifEmpty { null }
            if (contactId > 0L) {
                repository.getContact(contactId)?.let { existing ->
                    repository.updateContact(
                        existing.copy(name = state.name, phone = phone)
                    )
                }
            } else {
                repository.addContact(name = state.name, phone = phone)
            }
            _uiState.update { it.copy(saved = true) }
        }
    }
}
