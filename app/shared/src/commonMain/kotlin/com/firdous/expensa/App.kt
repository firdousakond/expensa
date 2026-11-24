package com.firdous.expensa

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.firdous.expensa.di.AppContainer
import com.firdous.expensa.ui.ExpenseListScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val container = remember { AppContainer() }
        ExpenseListScreen(container.expenseListViewModel)
    }
}
