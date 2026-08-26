package com.debttracker.app.ui.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debttracker.app.R
import com.debttracker.app.data.local.TransactionEntity
import com.debttracker.app.data.local.TransactionType
import com.debttracker.app.ui.components.AmountText
import com.debttracker.app.ui.components.Avatar
import com.debttracker.app.ui.components.ConfirmDialog
import com.debttracker.app.ui.components.DebtTypeToggle
import com.debttracker.app.ui.components.DatePickerField
import com.debttracker.app.ui.components.EmptyState
import com.debttracker.app.ui.theme.LocalExtraColors
import com.debttracker.app.util.Formatters
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    onBack: () -> Unit,
    onEditContact: (Long) -> Unit,
    viewModel: ContactDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currency = stringResource(R.string.currency_symbol)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(initialPage = 0) { 2 }

    var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    var showSettleDialog by remember { mutableStateOf(false) }

    val messageAdded = stringResource(R.string.message_transaction_added)
    val messageUpdated = stringResource(R.string.message_transaction_updated)
    val messageDeleted = stringResource(R.string.message_transaction_deleted)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.contact?.name.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditContact(viewModel.contactId) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_edit)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ContactHeader(
                uiState = uiState,
                currency = currency,
                onSettleAll = { showSettleDialog = true }
            )

            val tabTitles = listOf(
                stringResource(R.string.transactions_tab),
                stringResource(R.string.add_transaction_tab)
            )
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> HistoryTab(
                        uiState = uiState,
                        currency = currency,
                        onEdit = { transactionToEdit = it },
                        onDelete = { transactionToDelete = it }
                    )
                    else -> AddTransactionTab(
                        uiState = uiState,
                        onAdd = { type, amount, date, notes ->
                            viewModel.addTransaction(type, amount, date, notes)
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                                snackbarHostState.showSnackbar(messageAdded)
                            }
                        }
                    )
                }
            }
        }
    }

    val contactName = uiState.contact?.name.orEmpty()

    transactionToEdit?.let { transaction ->
        EditTransactionDialog(
            initial = transaction,
            uiState = uiState,
            onDismiss = { transactionToEdit = null },
            onSave = { updated ->
                viewModel.updateTransaction(updated)
                transactionToEdit = null
                scope.launch { snackbarHostState.showSnackbar(messageUpdated) }
            }
        )
    }

    transactionToDelete?.let { transaction ->
        ConfirmDialog(
            title = stringResource(R.string.delete_transaction_title),
            message = stringResource(R.string.delete_transaction_message),
            confirmLabel = stringResource(R.string.action_delete),
            dismissLabel = stringResource(R.string.action_cancel),
            onConfirm = {
                viewModel.deleteTransaction(transaction)
                transactionToDelete = null
                scope.launch { snackbarHostState.showSnackbar(messageDeleted) }
            },
            onDismiss = { transactionToDelete = null }
        )
    }

    if (showSettleDialog) {
        ConfirmDialog(
            title = stringResource(R.string.settle_all_title),
            message = stringResource(R.string.settle_all_message, contactName),
            confirmLabel = stringResource(R.string.ok),
            dismissLabel = stringResource(R.string.action_cancel),
            onConfirm = {
                viewModel.settleAll()
                showSettleDialog = false
            },
            onDismiss = { showSettleDialog = false }
        )
    }
}

@Composable
private fun ContactHeader(
    uiState: ContactDetailViewModel.UiState,
    currency: String,
    onSettleAll: () -> Unit
) {
    val contact = uiState.contact
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (contact != null) {
            Avatar(name = contact.name, size = 72.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            AmountText(
                amount = contact.balance,
                currency = currency,
                arabicNumerals = uiState.arabicNumerals,
                showSign = true,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = when {
                    contact.balance > 0.004 -> stringResource(R.string.owes_you)
                    contact.balance < -0.004 -> stringResource(R.string.you_owe)
                    else -> stringResource(R.string.nothing_between_you)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (!contact.phone.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            context.startActivity(
                                Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            )
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = stringResource(R.string.cd_call),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = contact.phone,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_phone),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedSettleButton(
                visible = contact.balance != 0.0 && uiState.transactions.any { !it.isSettled },
                onSettleAll = onSettleAll
            )
        }
    }
}

@Composable
private fun AnimatedSettleButton(
    visible: Boolean,
    onSettleAll: () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.expandVertically() +
            androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.shrinkVertically() +
            androidx.compose.animation.fadeOut()
    ) {
        FilledTonalButton(
            onClick = onSettleAll,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.settle_all))
        }
    }
}

@Composable
private fun HistoryTab(
    uiState: ContactDetailViewModel.UiState,
    currency: String,
    onEdit: (TransactionEntity) -> Unit,
    onDelete: (TransactionEntity) -> Unit
) {
    if (uiState.transactions.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            EmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = stringResource(R.string.empty_transactions_title),
                message = stringResource(R.string.empty_transactions_message)
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = uiState.transactions, key = { it.id }) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    currency = currency,
                    arabicNumerals = uiState.arabicNumerals,
                    locale = uiState.locale,
                    onClick = { onEdit(transaction) },
                    onLongClick = { onDelete(transaction) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionRow(
    transaction: TransactionEntity,
    currency: String,
    arabicNumerals: Boolean,
    locale: java.util.Locale,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current
    val isIncoming = transaction.type == TransactionType.CREDIT
    val iconColor = if (isIncoming) extraColors.positive else extraColors.negative
    val signedAmount = if (isIncoming) transaction.amount else -transaction.amount

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncoming) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                AmountText(
                    amount = signedAmount,
                    currency = currency,
                    arabicNumerals = arabicNumerals,
                    showSign = true,
                    style = MaterialTheme.typography.titleMedium
                )
                if (!transaction.notes.isNullOrBlank()) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = Formatters.date(transaction.date, locale, arabicNumerals),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (transaction.isSettled) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = extraColors.settledContainer
                ) {
                    Text(
                        text = stringResource(R.string.settled_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.onSettledContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTransactionTab(
    uiState: ContactDetailViewModel.UiState,
    onAdd: (TransactionType, Double, Long, String?) -> Unit
) {
    var isCredit by rememberSaveable { mutableStateOf(true) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var dateMillis by rememberSaveable { mutableStateOf(Formatters.todayMillis()) }
    var notes by rememberSaveable { mutableStateOf("") }
    var amountError by rememberSaveable { mutableStateOf(false) }

    val parsedAmount = Formatters.parseAmount(amountText)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DebtTypeToggle(
            selected = if (isCredit) TransactionType.CREDIT else TransactionType.DEBT,
            onSelect = { isCredit = it == TransactionType.CREDIT },
            creditLabel = stringResource(R.string.type_credit),
            debtLabel = stringResource(R.string.type_debt)
        )
        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                amountError = false
            },
            label = { Text(stringResource(R.string.field_amount)) },
            isError = amountError,
            supportingText = {
                if (amountError) {
                    Text(stringResource(R.string.error_invalid_amount))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        DatePickerField(
            label = stringResource(R.string.field_date),
            valueMillis = dateMillis,
            locale = uiState.locale,
            arabicNumerals = uiState.arabicNumerals,
            onDatePicked = { dateMillis = it }
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.field_notes)) },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (parsedAmount == null) {
                    amountError = true
                } else {
                    onAdd(
                        if (isCredit) TransactionType.CREDIT else TransactionType.DEBT,
                        parsedAmount,
                        dateMillis,
                        notes
                    )
                    amountText = ""
                    notes = ""
                    dateMillis = Formatters.todayMillis()
                    amountError = false
                }
            },
            enabled = parsedAmount != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.action_add),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun EditTransactionDialog(
    initial: TransactionEntity,
    uiState: ContactDetailViewModel.UiState,
    onDismiss: () -> Unit,
    onSave: (TransactionEntity) -> Unit
) {
    var type by remember(initial.id) { mutableStateOf(initial.type) }
    var amountText by remember(initial.id) {
        mutableStateOf(
            if (initial.amount == initial.amount.toLong().toDouble()) {
                initial.amount.toLong().toString()
            } else {
                initial.amount.toString()
            }
        )
    }
    var dateMillis by remember(initial.id) { mutableStateOf(initial.date) }
    var notes by remember(initial.id) { mutableStateOf(initial.notes.orEmpty()) }

    val parsedAmount = Formatters.parseAmount(amountText)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_transaction_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DebtTypeToggle(
                    selected = type,
                    onSelect = { type = it },
                    creditLabel = stringResource(R.string.type_credit),
                    debtLabel = stringResource(R.string.type_debt)
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(R.string.field_amount)) },
                    isError = amountText.isNotEmpty() && parsedAmount == null,
                    supportingText = {
                        if (amountText.isNotEmpty() && parsedAmount == null) {
                            Text(stringResource(R.string.error_invalid_amount))
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = stringResource(R.string.field_date),
                    valueMillis = dateMillis,
                    locale = uiState.locale,
                    arabicNumerals = uiState.arabicNumerals,
                    onDatePicked = { dateMillis = it }
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.field_notes)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = parsedAmount ?: return@TextButton
                    onSave(
                        initial.copy(
                            type = type,
                            amount = amount,
                            date = dateMillis,
                            notes = notes.trim().ifEmpty { null }
                        )
                    )
                },
                enabled = parsedAmount != null
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

