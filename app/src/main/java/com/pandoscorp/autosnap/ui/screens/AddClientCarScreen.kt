package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.ui.viewmodel.AddClientCarViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientCarScreen(
    navController: NavHostController,
    viewModel: AddClientCarViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val success by viewModel.success.collectAsState()

    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var engineVolume by remember { mutableStateOf("") }
    var horsePower by remember { mutableStateOf("") }

    LaunchedEffect(success) {
        if (success) {
            // Даем время на обновление данных
            delay(300)
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавить автомобиль") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                AlertError(message = errorMessage!!)
            }

            FormTextField(
                value = brand,
                onValueChange = { brand = it },
                label = "Марка*",
                placeholder = "Например: Toyota"
            )

            FormTextField(
                value = model,
                onValueChange = { model = it },
                label = "Модель*",
                placeholder = "Например: Camry"
            )

            FormTextField(
                value = year,
                onValueChange = { year = it },
                label = "Год выпуска",
                placeholder = "Например: 2020",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            FormTextField(
                value = engineVolume,
                onValueChange = { engineVolume = it },
                label = "Объем двигателя (л)",
                placeholder = "Например: 2.5",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            FormTextField(
                value = horsePower,
                onValueChange = { horsePower = it },
                label = "Мощность (л.с.)",
                placeholder = "Например: 180",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.addCar(
                        brand = brand,
                        model = model,
                        year = year,
                        engineVolume = engineVolume.toDoubleOrNull() ?: 0.0,
                        horsePower = horsePower
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && brand.isNotBlank() && model.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Добавить автомобиль")
                }
            }
        }
    }
}

@Composable
private fun AlertError(message: String) {
    AlertDialog(
        onDismissRequest = { /* */ },
        title = { Text("Ошибка") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = { /* */ }) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}