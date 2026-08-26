package com.debttracker.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {

    private val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    /** Formats a (positive) amount with grouping and up to two decimals. */
    fun amount(value: Double, arabicNumerals: Boolean): String {
        val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))
        val formatted = format.format(kotlin.math.abs(value))
        return if (arabicNumerals) toArabicDigits(formatted) else formatted
    }

    /** Formats an amount with a currency label, e.g. "1,500 ر.ي" or "-2,250.5 ر.ي". */
    fun amountWithCurrency(
        value: Double,
        currency: String,
        arabicNumerals: Boolean,
        showSign: Boolean = false
    ): String {
        val sign = when {
            value < 0 -> "-"
            showSign && value > 0 -> "+"
            else -> ""
        }
        return "$sign${amount(value, arabicNumerals)} $currency"
    }

    /** Long date like "15 يناير 2026" / "15 January 2026". */
    fun date(millis: Long, locale: Locale, arabicNumerals: Boolean): String {
        val formatted = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy", locale))
        return if (arabicNumerals) toArabicDigits(formatted) else formatted
    }

    /** First letter of the first two words, e.g. "أحمد علي" -> "أع". */
    fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val first = parts.getOrNull(0)?.firstOrNull()
        val second = parts.getOrNull(1)?.firstOrNull()
        return when {
            first == null -> "؟"
            second != null -> "$first$second"
            else -> first.toString()
        }
    }

    /**
     * Parses user amount input. Accepts Western or Arabic-Indic digits and
     * both "." and "," (and the Arabic decimal separator) as the decimal point.
     * Returns null for blank or non-positive values.
     */
    fun parseAmount(input: String): Double? {
        val normalized = normalizeDigits(input.trim())
        if (normalized.isEmpty()) return null
        return normalized.toDoubleOrNull()
            ?.takeIf { it.isFinite() && it > 0.0 }
    }

    fun toArabicDigits(text: String): String = buildString {
        for (character in text) {
            if (character in '0'..'9') {
                append(arabicDigits[character - '0'])
            } else {
                append(character)
            }
        }
    }

    fun normalizeDigits(input: String): String = buildString {
        for (character in input) {
            when {
                character in '٠'..'٩' -> append('0' + (character - '٠'))
                character in '۰'..'۹' -> append('0' + (character - '۰'))
                character == '٫' || character == ',' -> append('.')
                else -> append(character)
            }
        }
    }

    fun todayMillis(): Long =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    /** Converts a local-zone timestamp to the equivalent UTC-midnight timestamp (DatePicker input). */
    fun localMillisToUtcMidnight(millis: Long): Long =
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

    /** Converts a UTC-midnight timestamp (DatePicker output) to local-zone midnight. */
    fun utcMidnightToLocalMillis(utcMillis: Long): Long =
        Instant.ofEpochMilli(utcMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}
