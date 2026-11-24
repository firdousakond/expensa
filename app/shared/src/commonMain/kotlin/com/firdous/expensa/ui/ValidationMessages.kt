package com.firdous.expensa.ui

import com.firdous.expensa.validation.ValidationError

/** Maps a domain validation error to a user-facing message. */
fun ValidationError.message(): String =
    when (this) {
        ValidationError.AmountNotPositive -> "Amount must be greater than zero"
        ValidationError.EndDateBeforeStart -> "End date can't be before start date"
        is ValidationError.CurrencyMismatch -> "Currency must match the trip (${expected.value})"
        is ValidationError.DateOutsideTrip -> "Date must be between $start and $end"
    }
