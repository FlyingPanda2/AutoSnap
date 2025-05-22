package com.pandoscorp.autosnap.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.ClientsMainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMainScreen(
    navController: NavHostController,
    viewModel: ClientsMainViewModel
) {
    val cars by viewModel.cars.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val history by viewModel.history.collectAsState()
    val selectedService by viewModel.selectedService.collectAsState()

    LaunchedEffect(cars) {
        Log.d("CarDebug", "Cars in UI: ${cars.size}")
        cars.forEach { car ->
            Log.d("CarDebug", "Car: ${car.brand} ${car.model} (ID: ${car.id})")
        }
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мои записи") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            if (cars.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { navController.navigate("addCar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Создать автомобиль")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Блок выбранного сервиса
            selectedService?.let { service ->
                item {
                    SelectedServiceCard(
                        service = service,
                        onEditClick = { navController.navigate(ScreenObject.AutoServiceChoose.route) },
                        onClearClick = { viewModel.clearSelectedService() },
                        navController = navController
                    )
                }
            }

            // ... остальной существующий код (статистика, автомобили, записи)
            item {
                StatisticCard(
                    stats = listOf(
                        StatItem(
                            "Записей",
                            upcomingAppointments.size.toString(),
                            Icons.Default.DateRange
                        ),
                        StatItem("Автомобилей", cars.size.toString(), Icons.Default.Build),
                        StatItem("Потрачено", calculateTotalSpent(history), Icons.Default.Star)
                    )
                )
            }

            // === Мои автомобили ===
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Мои автомобили",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (cars.isNotEmpty()) {
                        TextButton(
                            onClick = { navController.navigate(ScreenObject.AddClientCarScreen.route) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Добавить")
                        }
                    }
                }
            }

            if (cars.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.Build,
                        title = "Нет автомобилей",
                        description = "Добавьте свой автомобиль для записи в сервис",
                        buttonText = "Создать автомобиль",
                        onButtonClick = { navController.navigate(ScreenObject.AddClientCarScreen.route) }
                    )
                }
            } else {
                items(cars) { car ->
                    CarCard(
                        car = car,
                        navController = navController
                    )
                }
            }

            // === Ближайшие записи ===
            item {
                Text(
                    text = "Ближайшие записи",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (upcomingAppointments.isEmpty()) {
                item {
                    if (selectedService != null) {
                        selectedService?.let { service ->
                            SelectedServiceCard(
                                service = service,
                                onEditClick = { navController.navigate(ScreenObject.AutoServiceChoose.route) },
                                onClearClick = { viewModel.clearSelectedService() },
                                navController = navController
                            )
                        }
                    } else {
                        EmptyStateCard(
                            icon = Icons.Default.DateRange,
                            title = "Нет записей",
                            description = "Запишитесь в автосервис для обслуживания",
                            buttonText = "Выбрать автосервис",
                            onButtonClick = { navController.navigate(ScreenObject.AutoServiceChoose.route) }
                        )
                    }
                }
            } else {
                items(upcomingAppointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { navController.navigate("appointmentDetails/${appointment.id}") }
                    )
                }
            }

            // === История записей ===
            item {
                Text(
                    text = "История посещений",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        text = "Здесь будет отображаться история ваших посещений",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                items(history) { appointment ->
                    HistoryCard(appointment = appointment)
                }
            }
        }
    }
}


// Обновлённые модели данных:
data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector
)

@Composable
fun StatisticCard(stats: List<StatItem>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Статистика",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            stats.forEach { stat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stat.label)
                    Text(stat.value, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CarCard(car: Car, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Build, contentDescription = "Автомобиль")
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${car.brand} ${car.model}", fontSize = 18.sp)
                Text("Год: ${car.year}, ${car.engineVolume} л")
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("📅 ${appointment.date}", fontWeight = FontWeight.Bold)
                Text("${appointment.totalPrice} ₽", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("⏰ ${appointment.time}")
        }
    }
}

@Composable
fun HistoryCard(appointment: Appointment) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("📅 ${appointment.date}")
            Text("${appointment.totalPrice} ₽")
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun SelectedServiceCard(
    service: User,
    navController: NavHostController,
    onEditClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Текущий автосервис",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Записаться",
                    modifier = Modifier
                        .clickable { navController.navigate(ScreenObject.ClientCreateAppointmentScreen.route) },
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                    )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.username ?: "Автосервис",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Адрес",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = service.address ?: "Адрес не указан",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Телефон",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = service.phone ?: "Телефон не указан",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))



            TextButton(onClick = onEditClick) {
                Text("Сменить сервис")
            }
        }
    }
}

private fun calculateTotalSpent(appointments: List<Appointment>): String {
    val total = appointments.sumOf { it.totalPrice }
    return "$total ₽"
}