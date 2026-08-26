package com.debttracker.app.ui.contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debttracker.app.data.local.ContactWithBalance
import com.debttracker.app.data.local.TransactionEntity
import com.debttracker.app.data.local.TransactionType
import com.debttracker.app.data.repository.DebtRepository
import com.debttracker.app.data.repository.SettingsRepository
import com.debttracker.app.model.AppLanguage
import com.debttracker.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: DebtRepository,
    savedStateHandle: SavedStateHandle,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val contactId: Long = savedStateHandle.get<Long>(Screen.ContactDetail.ARG_CONTACT_ID)
        ?: error("contactId is required")

    data class UiState(
        val contact: ContactWithBalance? = null,
        val transactions: List<TransactionEntity> = emptyList(),
        val arabicNumerals: Boolean = true,
        val locale: Locale = AppLanguage.ARABIC.locale
    ) {
        val balance: Double get() = contact?.balance ?: 0.0
    }

    val uiState: StateFlow<UiState> = combine(
        repository.contactWithBalance(contactId),
        repository.transactionsForContact(contactId),
        settingsRepository.language
    ) { contact, transactions, language ->
        UiState(
            contact = contact,
            transactions = transactions,
            arabicNumerals = language == AppLanguage.ARABIC,
            locale = language.locale
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    fun addTransaction(type: TransactionType, amount: Double, date: Long, notes: String?) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    contactId = contactId,
                    amount = amount,
                    type = type,
                    date = date,
                    notes = notes?.trim()?.ifEmpty { null }
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun settleAll() {
        viewModelScope.launch {
            repository.settleAllForContact(contactId)
        }
    }
}
