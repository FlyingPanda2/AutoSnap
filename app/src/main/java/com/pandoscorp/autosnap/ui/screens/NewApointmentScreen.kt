package com.pandoscorp.autosnap.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AppointmentSharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.getMonthName
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
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
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedServices by remember {
        derivedStateOf { sharedViewModel.selectedServices.toList() }
    }
    val totalPrice = remember(sharedViewModel.selectedServices) {
        sharedViewModel.selectedServices.sumOf { it.price }
    }

    val discountPercent by sharedViewModel.discountPercent.collectAsState()
    val finalPrice = remember(totalPrice, discountPercent) {
        totalPrice - (totalPrice * discountPercent / 100)
    }

    LaunchedEffect(selectedClient) {
        Log.d("NewAppointmentForm", "Обновлен выбранный клиент: ${selectedClient?.name ?: "null"}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Новая запись",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White),
                modifier = Modifier.shadow(elevation = 5.dp)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "ООО",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { navController.navigate("clients/selection") },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (selectedClient == null) {
                            Text(
                                text = "Выбрать клиента",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        } else {
                            Text(
                                text = selectedClient!!.name + " " + selectedClient!!.surname,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { navController.navigate(ScreenObject.ClientCarsScreen.route) },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (selectedCar == null) {
                            Text(
                                text = "Выбрать автомобиль",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        } else {
                            Text(
                                text = "${selectedCar!!.brand} ${selectedCar!!.model} (${selectedCar!!.year})",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(2.dp))

                if (selectedServices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 3.dp)
                            .background(Color.White)
                            .clickable { navController.navigate(ScreenObject.ServiceChooseScreen.route) },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Выбрать услуги",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Выбранные услуги:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        selectedServices.forEach { service ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { sharedViewModel.removeSelectedService(service.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить услугу",
                                        tint = Color.Red
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
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 3.dp)
                            .background(Color.White)
                            .clickable { navController.navigate(ScreenObject.ServiceChooseScreen.route) },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Добавить еще услугу",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable(onClick = {
                            sharedViewModel.setDateSelectionMode(true)
                            navController.navigate(ScreenObject.SheduleScreen.route)

                        }),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Дата",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Text(
                            text = "${selectedDate.selectedDate.day} ${getMonthName(selectedDate.selectedDate.month)} ${selectedDate.selectedDate.year}",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Выбрать дату",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                var showTimePicker by remember { mutableStateOf(false) }

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showTimePicker = false },
                        onTimeSelected = { time ->
                            sharedViewModel.setStartTime(time)
                        },
                        initialTime = sharedViewModel.startTime.collectAsState().value ?: LocalTime.now()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { showTimePicker = true },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Начало",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        val startTime by sharedViewModel.startTime.collectAsState()
                        Text(
                            text = startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Не выбрано",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Выбрать время",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Длительность",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        val totalDuration by sharedViewModel.totalDuration.collectAsState()
                        Text(
                            text = totalDuration,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Стоимость услуг",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Text(
                            text = "%,d ₽".format(totalPrice),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                var showDiscountDialog by remember { mutableStateOf(false) }

                if (showDiscountDialog) {
                    DiscountDialog(
                        onDismiss = { showDiscountDialog = false },
                        currentPercent = sharedViewModel.discountPercent.value,
                        onPercentChanged = { sharedViewModel.setDiscountPercent(it) }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { showDiscountDialog = true },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Скидка на услуги",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        val discountPercent by sharedViewModel.discountPercent.collectAsState()
                        Text(
                            text = "$discountPercent%",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Выбрать скидку",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Итоговая стоимость",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Text(
                            text = "%,d ₽".format(finalPrice),
                            fontSize = 16.sp,
                            color = Color.Green,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

            }

        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    initialTime: LocalTime = LocalTime.now()
) {
    val timeState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = {
            Column {
                TimePicker(state = timeState)
            }
        },
        confirmButton = {
            Button(onClick = {
                onTimeSelected(LocalTime.of(timeState.hour, timeState.minute))
                onDismiss()
            }) {
                Text("Выбрать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountDialog(
    onDismiss: () -> Unit,
    currentPercent: Int,
    onPercentChanged: (Int) -> Unit
) {
    var tempPercent by remember { mutableStateOf(currentPercent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Установите скидку") },
        text = {
            Column {
                // Вариант 1: Slider
                Slider(
                    value = tempPercent.toFloat(),
                    onValueChange = { tempPercent = it.toInt() },
                    valueRange = 0f..100f,
                    steps = 100,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Вариант 2: Текстовое поле с валидацией
                OutlinedTextField(
                    value = tempPercent.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { num ->
                            tempPercent = num.coerceIn(0, 100)
                        }
                    },
                    label = { Text("Процент скидки") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Скидка: $tempPercent%",
                    style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            Button(onClick = {
                onPercentChanged(tempPercent)
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