package com.firdous.expensa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.Trip
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveTripDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val trip: Trip,
    observeTripDetail: ObserveTripDetail,
    private val addExpense: AddExpense,
) : ViewModel() {
    val uiState: StateFlow<ExpenseListUiState> =
        observeTripDetail(trip.id)
            .map { detail ->
                ExpenseListUiState(
                    expenses = detail?.expenses ?: emptyList(),
                    summary = detail?.summary,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExpenseListUiState(),
            )

    fun add(expense: Expense) {
        viewModelScope.launch {
            addExpense(trip, expense)
        }
    }
}
