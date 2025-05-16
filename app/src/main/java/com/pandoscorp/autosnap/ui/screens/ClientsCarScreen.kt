package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.ui.viewmodel.SharedViewModel

// ui/screens/ClientCarsScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCarsForm(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onCarSelected: (Car) -> Unit = {}
) {
    val selectedClient by sharedViewModel.selectedClient.collectAsState()
    val cars = selectedClient?.cars ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор автомобиля") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(cars) { car ->
                CarItem(
                    car = car,
                    onSelect = {
                        onCarSelected(car)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun CarItem(car: Car, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onSelect),
        colors = CardColors(containerColor = Color.Gray, contentColor = Color.White, disabledContainerColor = Color.Gray, disabledContentColor = Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${car.brand} ${car.model}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Мощность " + car.horsePower + " л.с",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = "Год: ${car.year}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}