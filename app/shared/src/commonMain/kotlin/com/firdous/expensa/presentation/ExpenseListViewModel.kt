package com.firdous.expensa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.repository.TripRepository
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseListViewModel(
    observeTripDetail: ObserveTripDetail,
    observeCategories: ObserveCategories,
    private val addExpense: AddExpense,
    trips: TripRepository,
) : ViewModel() {
    // The trip currently shown — the first trip in storage, or null when none exists.
    private val currentTrip: StateFlow<Trip?> =
        trips
            .observeTrips()
            .map { it.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<ExpenseListUiState> =
        currentTrip
            .flatMapLatest { trip ->
                if (trip == null) {
                    flowOf(ExpenseListUiState())
                } else {
                    combine(observeTripDetail(trip.id), observeCategories()) { detail, categories ->
                        ExpenseListUiState(
                            trip = trip,
                            expenses = detail?.expenses ?: emptyList(),
                            summary = detail?.summary,
                            categories = categories,
                        )
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExpenseListUiState(),
            )

    @OptIn(ExperimentalUuidApi::class)
    suspend fun submitExpense(
        amountMinor: Long,
        categoryId: CategoryId,
        date: LocalDate,
        note: String?,
    ): ValidationResult {
        val trip = currentTrip.value ?: return ValidationResult.Invalid(emptyList())
        val expense =
            Expense(
                id = ExpenseId(Uuid.random().toString()),
                tripId = trip.id,
                amount = Money(amountMinor, trip.currency),
                categoryId = categoryId,
                date = date,
                note = note?.ifBlank { null },
            )
        return addExpense(trip, expense)
    }
}
