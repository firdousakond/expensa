package com.firdous.expensa.ui

import com.firdous.expensa.model.Money
import kotlin.math.abs

/** Formats minor units as a major-unit string with the currency code, e.g. "EUR 12.50". */
fun Money.format(): String {
    val sign = if (amountMinor < 0) "-" else ""
    val absMinor = abs(amountMinor)
    val major = absMinor / 100
    val cents = (absMinor % 100).toString().padStart(2, '0')
    return "${currency.value} $sign$major.$cents"
}
