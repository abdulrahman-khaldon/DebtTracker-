package com.debttracker.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormattersTest {

    @Test
    fun testInitialsGeneration() {
        assertEquals("أع", Formatters.initials("أحمد علي"))
        assertEquals("JD", Formatters.initials("John Doe"))
        assertEquals("A", Formatters.initials("Abdulrahman"))
        assertEquals("؟", Formatters.initials("   "))
    }

    @Test
    fun testArabicDigitsConversion() {
        assertEquals("١٢٣٤٥", Formatters.toArabicDigits("12345"))
        assertEquals("١٬٥٠٠.٥٠", Formatters.toArabicDigits("1,500.50"))
    }

    @Test
    fun testDigitsNormalization() {
        assertEquals("12345.50", Formatters.normalizeDigits("١٢٣٤٥٫٥٠"))
        assertEquals("100.25", Formatters.normalizeDigits("100,25"))
    }

    @Test
    fun testAmountParsing() {
        assertEquals(1500.5, Formatters.parseAmount("١٥٠٠٫٥")!!, 0.001)
        assertEquals(250.0, Formatters.parseAmount("250")!!, 0.001)
        assertNull(Formatters.parseAmount("-100"))
        assertNull(Formatters.parseAmount("abc"))
        assertNull(Formatters.parseAmount(""))
    }

    @Test
    fun testAmountFormatting() {
        assertEquals("1,500.5", Formatters.amount(1500.5, arabicNumerals = false))
        assertEquals("١٬٥٠٠.٥", Formatters.amount(1500.5, arabicNumerals = true))
    }
}
