package com.debttracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import com.debttracker.app.util.Formatters
import kotlin.math.abs

/** Circular avatar showing the initials of [name] over a stable gradient. */
@Composable
fun Avatar(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val initials = remember(name) { Formatters.initials(name) }
    val gradientColors = remember(name) {
        val (start, end) = AvatarPalette[abs(name.hashCode()) % AvatarPalette.size]
        listOf(start, end)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (fontSize.isUnspecified) (size.value * 0.36f).sp else fontSize
        )
    }
}

private val AvatarPalette = listOf(
    Color(0xFF1565C0) to Color(0xFF1E88E5),
    Color(0xFF2E7D32) to Color(0xFF43A047),
    Color(0xFF6A1B9A) to Color(0xFF8E24AA),
    Color(0xFFC62828) to Color(0xFFE53935),
    Color(0xFF00838F) to Color(0xFF00ACC1),
    Color(0xFF4527A0) to Color(0xFF5E35B1),
    Color(0xFFAD1457) to Color(0xFFD81B60),
    Color(0xFFE65100) to Color(0xFFEF6C00),
    Color(0xFF37474F) to Color(0xFF546E7A),
    Color(0xFF00695C) to Color(0xFF00897B)
)
