package com.firdous.expensa

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.firdous.expensa.di.DatabaseSeeder
import com.firdous.expensa.ui.ExpenseListScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        val seeder = koinInject<DatabaseSeeder>()
        LaunchedEffect(Unit) { seeder.seed() }
        ExpenseListScreen(koinViewModel())
    }
}
