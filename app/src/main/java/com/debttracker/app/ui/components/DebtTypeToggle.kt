package com.debttracker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debttracker.app.data.local.TransactionType
import com.debttracker.app.ui.theme.LocalExtraColors

/**
 * Two-way toggle: "They owe me" (CREDIT, money incoming, green)
 * vs "I owe them" (DEBT, money outgoing, red).
 */
@Composable
fun DebtTypeToggle(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit,
    creditLabel: String,
    debtLabel: String,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ToggleOption(
            text = creditLabel,
            icon = Icons.Default.ArrowDownward,
            selected = selected == TransactionType.CREDIT,
            selectedContainerColor = extraColors.positiveContainer,
            selectedContentColor = extraColors.onPositiveContainer,
            onClick = { onSelect(TransactionType.CREDIT) },
            modifier = Modifier.weight(1f)
        )
        ToggleOption(
            text = debtLabel,
            icon = Icons.Default.ArrowUpward,
            selected = selected == TransactionType.DEBT,
            selectedContainerColor = extraColors.negativeContainer,
            selectedContentColor = extraColors.onNegativeContainer,
            onClick = { onSelect(TransactionType.DEBT) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ToggleOption(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    selectedContainerColor: Color,
    selectedContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) selectedContainerColor else Color.Transparent,
        label = "containerColor"
    )
    val contentColor = if (selected) selectedContentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
            maxLines = 1
        )
    }
}
