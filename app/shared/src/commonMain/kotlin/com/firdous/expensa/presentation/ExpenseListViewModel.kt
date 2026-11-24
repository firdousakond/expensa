package com.firdous.expensa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

class ExpenseListViewModel(
    private val trip: Trip,
    observeTripDetail: ObserveTripDetail,
    observeCategories: ObserveCategories,
    private val addExpense: AddExpense,
) : ViewModel() {
    private var idCounter = 0

    /** Sensible default for the add-expense form's date field. */
    val defaultExpenseDate: LocalDate get() = trip.startDate

    val uiState: StateFlow<ExpenseListUiState> =
        combine(observeTripDetail(trip.id), observeCategories()) { detail, categories ->
            ExpenseListUiState(
                expenses = detail?.expenses ?: emptyList(),
                summary = detail?.summary,
                categories = categories,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ExpenseListUiState(),
        )

    /** Assembles an expense from form input and validates+persists it via the use case. */
    suspend fun submitExpense(
        amountMinor: Long,
        categoryId: CategoryId,
        date: LocalDate,
        note: String?,
    ): ValidationResult {
        val expense =
            Expense(
                id = ExpenseId("e-${++idCounter}"),
                tripId = trip.id,
                amount = Money(amountMinor, trip.currency),
                categoryId = categoryId,
                date = date,
                note = note?.ifBlank { null },
            )
        return addExpense(trip, expense)
    }
}
