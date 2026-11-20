package com.firdous.expensa.model

data class Money(
    val amountMinor: Long,
    val currency: Currency,
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) {
            "Cannot add different currencies: $currency vs ${other.currency}"
        }
        return Money(amountMinor + other.amountMinor, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) {
            "Cannot subtract different currencies: $currency vs ${other.currency}"
        }
        return Money(amountMinor - other.amountMinor, currency)
    }

    companion object {
        fun zero(currency: Currency) = Money(0, currency)
    }
}
