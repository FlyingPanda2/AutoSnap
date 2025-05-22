package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.ui.viewmodel.AddServiceViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.pandoscorp.autosnap.R
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceForm(
    navController: NavHostController,
    userId: String,
    viewModel: AddServiceViewModel = viewModel()
) {
    var serviceName by remember { mutableStateOf("") }
    var serviceDuration by remember { mutableStateOf("") }
    var servicePrice by remember { mutableStateOf("") }
    var serviceDescription by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая услуга") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (serviceName.isNotBlank()) {
                                val service = Service(
                                    name = serviceName,
                                    duration = serviceDuration.toIntOrNull() ?: 0,
                                    price = servicePrice.toIntOrNull() ?: 0,
                                    description = serviceDescription
                                )
                                viewModel.addServiceToUser(service) { success ->
                                    if (success) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Название*") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = serviceDuration,
                    onValueChange = { serviceDuration = it.filter { c -> c.isDigit() } },
                    label = { Text("Длительность (минуты)") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = servicePrice,
                    onValueChange = { servicePrice = it.filter { c -> c.isDigit() } },
                    label = { Text("Стоимость (руб)") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = serviceDescription,
                    onValueChange = { serviceDescription = it },
                    label = { Text("Описание") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    maxLines = 3
                )
            }
        }
    )
}