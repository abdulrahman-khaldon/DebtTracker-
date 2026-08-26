package com.debttracker.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.debttracker.app.R
import com.debttracker.app.data.local.ContactWithBalance
import com.debttracker.app.ui.components.AmountText
import com.debttracker.app.ui.components.Avatar
import com.debttracker.app.ui.components.ConfirmDialog
import com.debttracker.app.ui.components.EmptyState
import com.debttracker.app.ui.components.SwipeToRevealRow
import com.debttracker.app.ui.theme.LocalExtraColors
import com.debttracker.app.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onContactClick: (Long) -> Unit,
    onAddContact: () -> Unit,
    onEditContact: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currency = stringResource(R.string.currency_symbol)
    var contactToDelete by remember { mutableStateOf<ContactWithBalance?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_home),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddContact,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_contact)
                    )
                },
                text = { Text(stringResource(R.string.cd_add_contact)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = uiState.hasContacts,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SummaryCard(uiState = uiState, currency = currency)
            }

            Crossfade(
                targetState = uiState.contacts.isEmpty(),
                label = "homeContent"
            ) { isEmpty ->
                if (isEmpty) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))
                        EmptyState(
                            icon = Icons.Default.People,
                            title = stringResource(R.string.empty_contacts_title),
                            message = stringResource(R.string.empty_contacts_message),
                            actionLabel = stringResource(R.string.empty_contacts_action),
                            onAction = onAddContact
                        )
                    }
                } else {
                    ContactList(
                        uiState = uiState,
                        currency = currency,
                        onContactClick = onContactClick,
                        onEditContact = onEditContact,
                        onDeleteContact = { contactToDelete = it }
                    )
                }
            }
        }
    }

    contactToDelete?.let { contact ->
        ConfirmDialog(
            title = stringResource(R.string.delete_contact_title),
            message = stringResource(R.string.delete_contact_message, contact.name),
            confirmLabel = stringResource(R.string.action_delete),
            dismissLabel = stringResource(R.string.action_cancel),
            onConfirm = {
                viewModel.deleteContact(contact.id)
                contactToDelete = null
            },
            onDismiss = { contactToDelete = null }
        )
    }
}

@Composable
private fun ContactList(
    uiState: HomeViewModel.UiState,
    currency: String,
    onContactClick: (Long) -> Unit,
    onEditContact: (Long) -> Unit,
    onDeleteContact: (ContactWithBalance) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = uiState.contacts, key = { it.id }) { contact ->
            SwipeToRevealRow(
                onEdit = { onEditContact(contact.id) },
                onDelete = { onDeleteContact(contact) },
                editLabel = stringResource(R.string.action_edit),
                deleteLabel = stringResource(R.string.action_delete),
                modifier = Modifier.animateItem()
            ) {
                ContactCard(
                    contact = contact,
                    currency = currency,
                    arabicNumerals = uiState.arabicNumerals,
                    locale = uiState.locale,
                    onClick = { onContactClick(contact.id) }
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    uiState: HomeViewModel.UiState,
    currency: String
) {
    val extraColors = LocalExtraColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryItem(
                    title = stringResource(R.string.total_owed_to_me),
                    amount = uiState.totalOwedToMe,
                    icon = Icons.Default.ArrowDownward,
                    color = extraColors.positive,
                    currency = currency,
                    arabicNumerals = uiState.arabicNumerals,
                    modifier = Modifier.weight(1f)
                )
                SummaryItem(
                    title = stringResource(R.string.total_i_owe),
                    amount = uiState.totalIOwe,
                    icon = Icons.Default.ArrowUpward,
                    color = extraColors.negative,
                    currency = currency,
                    arabicNumerals = uiState.arabicNumerals,
                    modifier = Modifier.weight(1f)
                )
            }

            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )

            val animatedNet by animateFloatAsState(
                targetValue = uiState.net.toFloat(),
                animationSpec = tween(durationMillis = 600),
                label = "netBalance"
            )
            val netColor = when {
                animatedNet > 0.004f -> extraColors.positive
                animatedNet < -0.004f -> extraColors.negative
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.net_balance),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = Formatters.amountWithCurrency(
                        value = animatedNet.toDouble(),
                        currency = currency,
                        arabicNumerals = uiState.arabicNumerals,
                        showSign = true
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = netColor
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    currency: String,
    arabicNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = color.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = Formatters.amountWithCurrency(
                    value = amount,
                    currency = currency,
                    arabicNumerals = arabicNumerals
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactCard(
    contact: ContactWithBalance,
    currency: String,
    arabicNumerals: Boolean,
    locale: java.util.Locale,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Avatar(name = contact.name, size = 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contact.lastTransactionDate?.let { dateMillis ->
                        stringResource(
                            R.string.last_transaction_date,
                            Formatters.date(dateMillis, locale, arabicNumerals)
                        )
                    } ?: stringResource(R.string.no_transactions_yet),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AmountText(
                amount = contact.balance,
                currency = currency,
                arabicNumerals = arabicNumerals,
                showSign = true,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
