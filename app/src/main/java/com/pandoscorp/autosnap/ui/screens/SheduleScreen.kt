package com.pandoscorp.autosnap.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AppointmentSharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.CalendarView
import com.pandoscorp.autosnap.ui.viewmodel.getMonthName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheduleForm(
    navController: NavHostController,
    viewModel: AppointmentSharedViewModel,
) {
    val state by viewModel.state.collectAsState()

    val isDateSelectionMode by viewModel.isDateSelectionMode.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val appointments by viewModel.appointments.collectAsState()

    LaunchedEffect(state.selectedDate) {
        Log.d("qwerty223", "это стейт ${state.selectedDate}")
        viewModel.loadAppointmentsForDate(state.selectedDate)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Записи",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
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
            ) {
                CalendarView(
                    state = state,
                    onDateSelected = { date ->
                        viewModel.selectDate(date)
                        viewModel.loadAppointmentsForDate(date)
                    },
                    onMonthChanged = { month ->
                        viewModel.changeMonth(month)
                    },
                    onYearChanged = { year ->
                        viewModel.changeYear(year)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выбрано: ${state.selectedDate.day} ${getMonthName(state.selectedDate.month)} ${state.selectedDate.year}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .shadow(elevation = 5.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!isDateSelectionMode) {
                    Button(
                        onClick = { navController.navigate(ScreenObject.NewAppointmentScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonColors(containerColor = Color.DarkGray, contentColor = Color.White, disabledContentColor = Color.DarkGray, disabledContainerColor = Color.DarkGray)
                    ) {
                        Text("Создать запись")
                    }
                }

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    if (appointments.isEmpty()) {
                        item {
                            Text(
                                text = "На выбранную дату записей нет",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                viewModel = viewModel,
                                navController = navController
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        })
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    viewModel: AppointmentSharedViewModel,
    navController: NavHostController
) {
    var client by remember { mutableStateOf<Client?>(null) }
    var car by remember { mutableStateOf<Car?>(null) }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appointment) {
        client = viewModel.getClientById(appointment.clientId)
        car = viewModel.getCarById(appointment.clientId, appointment.carId)
        loading = false
        services = viewModel.getServicesByIds(appointment.serviceIds)
    }

    // Внешний Box для выхода за пределы Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardColors(containerColor = Color.Gray, contentColor = Color.White, disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (loading) {
                        CircularProgressIndicator()
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = appointment.time,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${appointment.totalPrice} ₽",
                                color = Color.Green,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                client?.let {
                                    Text(
                                        text = "${it.name} ${it.surname}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = it.phone,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } ?: Text("Клиент не найден")

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "${car?.brand} ${car?.model} ${car?.year}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Column {
                                if (services.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    services.forEach { service ->
                                        Text(
                                            text = "- ${service.name}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                } else {
                                    Text("Нет услуг")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Крестик теперь вне Card и не будет обрезан
        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Удалить запись",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление записи") },
            text = { Text("Вы уверены, что хотите удалить эту запись?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAppointment(
                            appointment.id,
                            onSuccess = { showDeleteDialog = false },
                            onError = { /* Обработка ошибки */ }
                        )
                    }
                ) {
                    Text("Удалить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}