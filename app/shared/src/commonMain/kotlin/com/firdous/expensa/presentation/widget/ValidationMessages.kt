package com.firdous.expensa.presentation.widget

import com.firdous.expensa.validation.ValidationError
import expensa.app.shared.generated.resources.Res
import expensa.app.shared.generated.resources.error_amount_not_positive
import expensa.app.shared.generated.resources.error_currency_mismatch
import expensa.app.shared.generated.resources.error_date_outside_trip
import expensa.app.shared.generated.resources.error_end_date_before_start
import org.jetbrains.compose.resources.getString

/** Maps a domain validation error to a localized, user-facing message. */
suspend fun ValidationError.message(): String =
    when (this) {
        ValidationError.AmountNotPositive -> getString(Res.string.error_amount_not_positive)
        ValidationError.EndDateBeforeStart -> getString(Res.string.error_end_date_before_start)
        is ValidationError.CurrencyMismatch -> getString(Res.string.error_currency_mismatch, expected.value)
        is ValidationError.DateOutsideTrip ->
            getString(Res.string.error_date_outside_trip, start.toString(), end.toString())
    }
