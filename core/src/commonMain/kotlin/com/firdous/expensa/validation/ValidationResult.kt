package com.firdous.expensa.validation

import com.firdous.expensa.model.Currency
import kotlinx.datetime.LocalDate

sealed interface ValidationResult {
    data object Valid : ValidationResult

    data class Invalid(
        val errors: List<ValidationError>,
    ) : ValidationResult
}

sealed interface ValidationError {
    data object AmountNotPositive : ValidationError

    data object EndDateBeforeStart : ValidationError

    data class CurrencyMismatch(
        val expected: Currency,
        val actual: Currency,
    ) : ValidationError

    data class DateOutsideTrip(
        val date: LocalDate,
        val start: LocalDate,
        val end: LocalDate,
    ) : ValidationError
}
