package com.firdous.expensa

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.firdous.expensa.presentation.widget.ExpenseListScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        ExpenseListScreen(koinViewModel())
    }
}
