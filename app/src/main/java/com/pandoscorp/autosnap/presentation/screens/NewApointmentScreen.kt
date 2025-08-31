package com.pandoscorp.autosnap.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.domain.model.Service
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AppointmentSharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.getMonthName
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentForm(
    navController: NavHostController,
    sharedViewModel: AppointmentSharedViewModel
) {
    val selectedClient by sharedViewModel.selectedClient.collectAsState()
    val selectedCar by sharedViewModel.selectedCar.collectAsState()
    val selectedDate by sharedViewModel.state.collectAsState()
    val context = LocalContext.current
    val selectedServices by remember { derivedStateOf { sharedViewModel.selectedServices.toList() } }
    val totalPrice = remember(sharedViewModel.selectedServices) { sharedViewModel.selectedServices.sumOf { it.price } }
    val discountPercent by sharedViewModel.discountPercent.collectAsState()
    val finalPrice = remember(totalPrice, discountPercent) { totalPrice - (totalPrice * discountPercent / 100) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Новая запись",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            sharedViewModel.saveAppointment(
                                onSuccess = {
                                    Toast.makeText(context, "Запись успешно сохранена", Toast.LENGTH_SHORT).show()
                                    sharedViewModel.setDateSelectionMode(false)
                                    navController.popBackStack(ScreenObject.SheduleScreen.route, inclusive = true)
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Сохранить",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Блок информации о сервисе
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Гаражный Мастер",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Блок выбора клиента
                item {
                    SelectionCard(
                        title = "Клиент",
                        value = selectedClient?.let { "${it.name} ${it.surname}" } ?: "Не выбран",
                        onClick = { navController.navigate("clients/selection") }
                    )
                }

                // Блок выбора автомобиля
                item {
                    SelectionCard(
                        title = "Автомобиль",
                        value = selectedCar?.let { "${it.brand} ${it.model} (${it.year})" } ?: "Не выбран",
                        onClick = { navController.navigate(ScreenObject.ClientCarsScreen.route) }
                    )
                }

                // Блок выбора услуг
                item {
                    if (selectedServices.isEmpty()) {
                        SelectionCard(
                            title = "Услуги",
                            value = "Не выбраны",
                            onClick = { navController.navigate(ScreenObject.ServiceChooseScreen.route) }
                        )
                    } else {
                        Column {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Выбранные услуги",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    selectedServices.forEach { service ->
                                        ServiceItem(
                                            service = service,
                                            onRemove = { sharedViewModel.removeSelectedService(service.id) }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            SelectionCard(
                                title = "Добавить услугу",
                                value = "",
                                onClick = { navController.navigate(ScreenObject.ServiceChooseScreen.route) }
                            )
                        }
                    }
                }

                // Блок выбора даты и времени
                item {
                    SelectionCard(
                        title = "Дата",
                        value = "${selectedDate.selectedDate.day} ${getMonthName(selectedDate.selectedDate.month)} ${selectedDate.selectedDate.year}",
                        onClick = {
                            sharedViewModel.setDateSelectionMode(true)
                            navController.navigate(ScreenObject.SheduleScreen.route)
                        }
                    )
                }

                item {
                    val startTime by sharedViewModel.startTime.collectAsState()

                    SelectionCard(
                        title = "Время начала",
                        value = startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Не выбрано",
                        onClick = { showTimePickerDialog = true }
                    )

                    if (showTimePickerDialog) {
                        TimePickerDialog(
                            onCancel = { showTimePickerDialog = false },
                            onConfirm = {
                                val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                sharedViewModel.setStartTime(selectedTime)
                                showTimePickerDialog = false
                            },
                            state = timePickerState
                        )
                    }
                }

                item {
                    val totalDuration by sharedViewModel.totalDuration.collectAsState()
                    InfoCard(
                        title = "Длительность",
                        value = totalDuration
                    )
                }

                // Блок стоимости
                item {
                    InfoCard(
                        title = "Стоимость услуг",
                        value = "%,d ₽".format(totalPrice)
                    )
                }

                item {
                    var showDiscountDialog by remember { mutableStateOf(false) }

                    SelectionCard(
                        title = "Скидка",
                        value = "$discountPercent%",
                        onClick = { showDiscountDialog = true }
                    )
                }

                item {
                    InfoCard(
                        title = "Итоговая стоимость",
                        value = "%,d ₽".format(finalPrice),
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

@Composable
private fun SelectionCard(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Sharp.KeyboardArrowRight,
                contentDescription = "Выбрать",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )
        }
    }
}

@Composable
private fun ServiceItem(
    service: Service,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Удалить услугу",
                tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${service.price} ₽ • ${service.duration} мин",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    state: TimePickerState
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Выберите время") },
        text = {
            Column {
                TimePicker(state = state)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun DiscountDialog(
    currentDiscount: Int,
    onDiscountChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var discount by remember { mutableStateOf(currentDiscount) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Установите скидку") },
        text = {
            Column {
                Slider(
                    value = discount.toFloat(),
                    onValueChange = { discount = it.toInt() },
                    valueRange = 0f..100f,
                    steps = 100
                )
                Text(
                    text = "$discount%",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDiscountChanged(discount)
                onDismiss()
            }) {
                Text("Применить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}