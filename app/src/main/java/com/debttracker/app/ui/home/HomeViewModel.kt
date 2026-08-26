package com.debttracker.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.data.local.ContactWithBalance
import com.debttracker.app.data.repository.DebtRepository
import com.debttracker.app.data.repository.SettingsRepository
import com.debttracker.app.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DebtRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    data class UiState(
        val contacts: List<ContactWithBalance> = emptyList(),
        val totalOwedToMe: Double = 0.0,
        val totalIOwe: Double = 0.0,
        val arabicNumerals: Boolean = true,
        val locale: Locale = AppLanguage.ARABIC.locale
    ) {
        val net: Double get() = totalOwedToMe - totalIOwe
        val hasContacts: Boolean get() = contacts.isNotEmpty()
    }

    val uiState: StateFlow<UiState> = combine(
        repository.contactsWithBalance,
        repository.globalTotals,
        settingsRepository.language
    ) { contacts, totals, language ->
        UiState(
            contacts = contacts,
            totalOwedToMe = totals.totalOwedToMe,
            totalIOwe = totals.totalIOwe,
            arabicNumerals = language == AppLanguage.ARABIC,
            locale = language.locale
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
        }
    }
}
