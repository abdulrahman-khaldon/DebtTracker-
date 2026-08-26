package com.debttracker.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.debttracker.app.ui.theme.LocalExtraColors
import com.debttracker.app.util.Formatters

/**
 * Amount label colored by sign: positive (they owe me) green, negative (I owe) red,
 * zero neutral.
 */
@Composable
fun AmountText(
    amount: Double,
    currency: String,
    arabicNumerals: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    showSign: Boolean = false,
    color: Color? = null,
    fontWeight: FontWeight? = null
) {
    val extraColors = LocalExtraColors.current
    val resolvedColor = color ?: when {
        amount > 0.004 -> extraColors.positive
        amount < -0.004 -> extraColors.negative
        else -> MaterialTheme.colorScheme.onSurface
    }
    Text(
        text = Formatters.amountWithCurrency(
            value = amount,
            currency = currency,
            arabicNumerals = arabicNumerals,
            showSign = showSign
        ),
        style = style,
        color = resolvedColor,
        fontWeight = fontWeight ?: FontWeight.SemiBold,
        modifier = modifier
    )
}
