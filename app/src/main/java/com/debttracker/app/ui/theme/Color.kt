package com.debttracker.app.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Material 3 color schemes
// ---------------------------------------------------------------------------

val LightPrimary = Color(0xFF00696B)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFF9CF1F2)
val LightOnPrimaryContainer = Color(0xFF002021)
val LightSecondary = Color(0xFF4A635F)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCCE8E2)
val LightOnSecondaryContainer = Color(0xFF06201C)
val LightTertiary = Color(0xFF4B607C)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFD3E4FF)
val LightOnTertiaryContainer = Color(0xFF041C35)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)
val LightBackground = Color(0xFFFDFBF8)
val LightOnBackground = Color(0xFF191C1B)
val LightSurface = Color(0xFFFDFBF8)
val LightOnSurface = Color(0xFF191C1B)
val LightSurfaceVariant = Color(0xFFDBE5E1)
val LightOnSurfaceVariant = Color(0xFF3F4946)
val LightOutline = Color(0xFF6F7976)
val LightOutlineVariant = Color(0xFFBEC9C5)

val DarkPrimary = Color(0xFF80D4D6)
val DarkOnPrimary = Color(0xFF003738)
val DarkPrimaryContainer = Color(0xFF004F51)
val DarkOnPrimaryContainer = Color(0xFF9CF1F2)
val DarkSecondary = Color(0xFFB1CCC6)
val DarkOnSecondary = Color(0xFF1C3531)
val DarkSecondaryContainer = Color(0xFF334B47)
val DarkOnSecondaryContainer = Color(0xFFCCE8E2)
val DarkTertiary = Color(0xFFB4C8E8)
val DarkOnTertiary = Color(0xFF1C314C)
val DarkTertiaryContainer = Color(0xFF334863)
val DarkOnTertiaryContainer = Color(0xFFD3E4FF)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)
val DarkBackground = Color(0xFF191C1B)
val DarkOnBackground = Color(0xFFE1E3E0)
val DarkSurface = Color(0xFF191C1B)
val DarkOnSurface = Color(0xFFE1E3E0)
val DarkSurfaceVariant = Color(0xFF3F4946)
val DarkOnSurfaceVariant = Color(0xFFBEC9C5)
val DarkOutline = Color(0xFF89938F)
val DarkOutlineVariant = Color(0xFF3F4946)

// ---------------------------------------------------------------------------
// Extra semantic colors (money in / money out / settled)
// ---------------------------------------------------------------------------

data class ExtraColors(
    val positive: Color,
    val positiveContainer: Color,
    val onPositiveContainer: Color,
    val negative: Color,
    val negativeContainer: Color,
    val onNegativeContainer: Color,
    val settledContainer: Color,
    val onSettledContainer: Color
)

val LightExtraColors = ExtraColors(
    positive = Color(0xFF2E7D32),
    positiveContainer = Color(0xFFA5D6A7),
    onPositiveContainer = Color(0xFF0A3D0C),
    negative = Color(0xFFC62828),
    negativeContainer = Color(0xFFEF9A9A),
    onNegativeContainer = Color(0xFF410002),
    settledContainer = Color(0xFFE0E0E0),
    onSettledContainer = Color(0xFF424242)
)

val DarkExtraColors = ExtraColors(
    positive = Color(0xFF81C995),
    positiveContainer = Color(0xFF1B5E20),
    onPositiveContainer = Color(0xFFC8E6C9),
    negative = Color(0xFFEF9A9A),
    negativeContainer = Color(0xFF7F1D1D),
    onNegativeContainer = Color(0xFFFFCDD2),
    settledContainer = Color(0xFF3E4143),
    onSettledContainer = Color(0xFFC4C7C5)
)
