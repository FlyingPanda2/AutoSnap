package com.pandoscorp.autosnap.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.ui.components.*
import com.pandoscorp.autosnap.ui.viewmodel.ClientsMainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMainScreen(
    navController: NavHostController,
    viewModel: ClientsMainViewModel
) {
    val cars by viewModel.cars.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val history by viewModel.history.collectAsState()

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
            // === Статистика клиента ===
            item {
                StatisticCard(
                    stats = listOf(
                        StatItem("Записей", upcomingAppointments.size.toString(), Icons.Default.DateRange),
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
                            onClick = { navController.navigate("addCar") }
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
                        onButtonClick = { navController.navigate("addCar") }
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
                    EmptyStateCard(
                        icon = Icons.Default.DateRange,
                        title = "Нет записей",
                        description = "Запишитесь в автосервис для обслуживания",
                        buttonText = "Выбрать автосервис",
                        onButtonClick = { navController.navigate("selectService") }
                    )
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
            Text("Статистика", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

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
fun HeaderWithButton(title: String, buttonText: String, onButtonClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = onButtonClick) {
            Icon(Icons.Default.Add, contentDescription = "Добавить")
            Spacer(modifier = Modifier.width(4.dp))
            Text(buttonText)
        }
    }
}

@Composable
fun CarCard(car: Car, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("carDetails/${car.id}") }
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

private fun calculateTotalSpent(appointments: List<Appointment>): String {
    val total = appointments.sumOf { it.totalPrice }
    return "$total ₽"
}