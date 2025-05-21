package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()

    private val _todayAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val todayAppointments: StateFlow<List<Appointment>> = _todayAppointments

    private val _weekAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val weekAppointments: StateFlow<List<Appointment>> = _weekAppointments

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Загрузка данных параллельно
                launch { loadTodayAppointments(userId) }
                launch { loadWeekAppointments(userId) }
                launch { loadClients(userId) }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки: ${e.message}"
                Log.e("StatisticsVM", "Error loading statistics", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadTodayAppointments(userId: String) {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val snapshot = database.getReference("users/$userId/appointments")
                .orderByChild("date")
                .equalTo(today)
                .get()
                .await()

            _todayAppointments.value = snapshot.children.mapNotNull {
                it.getValue(Appointment::class.java)?.copy(id = it.key ?: "")
            }
        } catch (e: Exception) {
            throw Exception("Failed to load today's appointments: ${e.message}")
        }
    }

    private suspend fun loadWeekAppointments(userId: String) {
        try {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            }
            val startOfWeek = calendar.time

            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = calendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateStr = dateFormat.format(startOfWeek)
            val endDateStr = dateFormat.format(endOfWeek)

            val snapshot = database.getReference("users/$userId/appointments")
                .orderByChild("date")
                .startAt(startDateStr)
                .endAt(endDateStr)
                .get()
                .await()

            _weekAppointments.value = snapshot.children.mapNotNull {
                it.getValue(Appointment::class.java)?.copy(id = it.key ?: "")
            }
        } catch (e: Exception) {
            throw Exception("Failed to load week appointments: ${e.message}")
        }
    }

    private suspend fun loadClients(userId: String) {
        try {
            val snapshot = database.getReference("users/$userId/clients")
                .get()
                .await()

            _clients.value = snapshot.children.mapNotNull { clientSnapshot ->
                val clientId = clientSnapshot.key ?: return@mapNotNull null

                // Получаем все поля клиента вручную
                val clientData = clientSnapshot.value as? Map<*, *> ?: return@mapNotNull null

                Client(
                    id = clientId,
                    name = clientData["name"] as? String ?: "",
                    surname = clientData["surname"] as? String ?: "",
                    phone = clientData["phone"] as? String ?: "",
                    email = clientData["email"] as? String ?: "",
                    birthdate = clientData["birthdate"] as? String ?: "",
                    note = clientData["note"] as? String ?: "",
                    cars = emptyList()
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to load clients: ${e.message}")
        }
    }
}