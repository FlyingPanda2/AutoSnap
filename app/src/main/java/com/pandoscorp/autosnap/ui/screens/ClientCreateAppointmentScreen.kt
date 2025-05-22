package com.pandoscorp.autosnap.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.ui.components.*
import com.pandoscorp.autosnap.ui.viewmodel.ClientCreateAppointmentViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ClientsMainViewModel
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCreateAppointmentScreen(
    navController: NavHostController,
    viewModel: ClientCreateAppointmentViewModel,
    clientViewModel: ClientsMainViewModel
) {
    val selectedService by clientViewModel.selectedService.collectAsState()
    val cars by viewModel.cars.collectAsState()
    val services by viewModel.services.collectAsState()
    val selectedServices by viewModel.selectedServices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    val selectedCarId by viewModel.selectedCarId.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()

    LaunchedEffect(selectedService) {
        selectedService?.id?.let { serviceCenterId ->
            viewModel.loadServices(serviceCenterId)
        }
    }

    LaunchedEffect(success) {
        if (success) {
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("Новая запись")
                        Text(
                            selectedService?.username ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedService == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Автосервис не выбран", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (error != null) {
                    item {
                        ErrorMessage(message = error!!)
                    }
                }

                item {
                    Text(
                        text = "Выберите автомобиль",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cars.isEmpty()) {
                    item {
                        Text("У вас нет добавленных автомобилей")
                    }
                } else {
                    items(cars) { car ->
                        CarSelectionItem(
                            car = car,
                            isSelected = car.id == selectedCarId,
                            onSelect = { viewModel.selectedCarId.value = car.id }
                        )
                    }
                }

                item {
                    Text(
                        text = "Дата и время",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                item {
                    DatePickerField(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.selectedDate.value = it }
                    )
                }

                item {
                    TimePickerField(
                        selectedTime = selectedTime,
                        onTimeSelected = { viewModel.selectedTime.value = it }
                    )
                }

                item {
                    Text(
                        text = "Выберите услуги",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (services.isEmpty()) {
                    item {
                        Text("Автосервис не предоставляет услуг")
                    }
                } else {
                    items(services) { service ->
                        ServiceSelectionItem(
                            service = service,
                            isSelected = selectedServices.contains(service.id),
                            onToggle = { viewModel.toggleServiceSelection(service.id) }
                        )
                    }
                }

                item {
                    val totalPrice = remember(selectedServices) {
                        services.filter { selectedServices.contains(it.id) }.sumOf { it.price }
                    }

                    Text(
                        text = "Итого: $totalPrice ₽",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                item {
                    Button(
                        onClick = {
                            selectedService?.id?.let { serviceCenterId ->
                                viewModel.createAppointment(serviceCenterId)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        enabled = selectedCarId.isNotEmpty() &&
                                selectedDate.isNotEmpty() &&
                                selectedTime.isNotEmpty() &&
                                selectedServices.isNotEmpty()
                    ) {
                        Text("Записаться", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CarSelectionItem(
    car: Car,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${car.brand} ${car.model}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Год: ${car.year}, ${car.engineVolume} л",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ServiceSelectionItem(
    service: Service,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${service.price} ₽",
                    color = MaterialTheme.colorScheme.primary
                )
                if (service.description.isNotEmpty()) {
                    Text(
                        text = service.description,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val date = Date(it)
                            onDateSelected(dateFormatter.format(date))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("Дата записи") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.split(":")[0].toIntOrNull() ?: 9,
            initialMinute = selectedTime.split(":").getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        onTimeSelected(timeFormatter.format(calendar.time))
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    OutlinedTextField(
        value = selectedTime,
        onValueChange = {},
        label = { Text("Время записи") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(Icons.Default.Info, contentDescription = "Выбрать время")
            }
        }
    )
}