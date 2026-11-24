package com.firdous.expensa.presentation

/**
 * Parses a user-entered amount (major units, e.g. "12.50") into minor units (1250).
 * Returns null for empty, non-numeric, negative, or over-precise (>2 decimals) input.
 */
fun parseAmountToMinor(text: String): Long? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null

    val parts = trimmed.split('.')
    if (parts.size > 2) return null

    val whole = parts[0]
    val frac = parts.getOrNull(1).orEmpty()
    if (frac.length > 2) return null
    if (whole.isEmpty() && frac.isEmpty()) return null

    val wholeNum = if (whole.isEmpty()) 0L else whole.toLongOrNull() ?: return null
    if (wholeNum < 0) return null

    val fracNum = frac.padEnd(2, '0').toLongOrNull() ?: return null
    return wholeNum * 100 + fracNum
}
