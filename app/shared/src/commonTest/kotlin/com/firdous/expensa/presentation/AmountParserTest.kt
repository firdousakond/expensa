package com.firdous.expensa.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AmountParserTest {
    @Test
    fun `parses whole and decimal amounts to minor units`() {
        assertEquals(1250L, parseAmountToMinor("12.50"))
        assertEquals(1250L, parseAmountToMinor("12.5"))
        assertEquals(1200L, parseAmountToMinor("12"))
        assertEquals(99L, parseAmountToMinor("0.99"))
        assertEquals(50L, parseAmountToMinor(".5"))
        assertEquals(1250L, parseAmountToMinor("  12.50  "))
    }

    @Test
    fun `rejects invalid input`() {
        assertNull(parseAmountToMinor(""))
        assertNull(parseAmountToMinor("abc"))
        assertNull(parseAmountToMinor("12.345")) // too many decimals
        assertNull(parseAmountToMinor("-5")) // negative
        assertNull(parseAmountToMinor("1.2.3"))
    }
}
