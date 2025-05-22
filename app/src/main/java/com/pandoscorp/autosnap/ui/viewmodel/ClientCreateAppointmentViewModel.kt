package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ClientCreateAppointmentViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database("https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app")

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _selectedServices = MutableStateFlow<Set<String>>(emptySet())
    val selectedServices: StateFlow<Set<String>> = _selectedServices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    var selectedCarId = MutableStateFlow("")
    var selectedDate = MutableStateFlow("")
    var selectedTime = MutableStateFlow("")

    init {
        loadClientCars()
    }

    fun loadServices(serviceCenterId: String) {
        database.getReference("users/$serviceCenterId/services")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val servicesList = mutableListOf<Service>()
                    snapshot.children.forEach { serviceSnapshot ->
                        serviceSnapshot.getValue(Service::class.java)?.let { service ->
                            // Используем ключ snapshot'а как ID, если в объекте пустой ID
                            val serviceWithId = service.copy(id = serviceSnapshot.key ?: "")
                            servicesList.add(serviceWithId)
                        }
                    }
                    _services.value = servicesList
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Ошибка загрузки услуг: ${error.message}"
                }
            })
    }

    private fun loadClientCars() {
        auth.currentUser?.uid?.let { userId ->
            database.getReference("clients/$userId/cars")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val carsList = mutableListOf<Car>()
                        snapshot.children.forEach { carSnapshot ->
                            carSnapshot.getValue(Car::class.java)?.let {
                                carsList.add(it.copy(id = carSnapshot.key ?: ""))
                            }
                        }
                        _cars.value = carsList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _error.value = "Ошибка загрузки автомобилей: ${error.message}"
                    }
                })
        }
    }

    fun toggleServiceSelection(serviceId: String) {
        _selectedServices.value = if (_selectedServices.value.contains(serviceId)) {
            _selectedServices.value - serviceId
        } else {
            _selectedServices.value + serviceId
        }
    }

    fun createAppointment(serviceCenterId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val userId = auth.currentUser?.uid ?: throw Exception("Пользователь не авторизован")
                val appointmentRef = database.getReference("appointments").push()

                val appointment = Appointment(
                    id = appointmentRef.key ?: "",
                    clientId = userId,
                    carId = selectedCarId.value,
                    serviceIds = _selectedServices.value.toList(),
                    date = selectedDate.value,
                    time = selectedTime.value,
                    totalPrice = calculateTotalPrice(serviceCenterId),
                    discountPercent = 0,
                    createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    serviceCenterId = serviceCenterId
                )

                // Сначала сохраняем в общий список
                appointmentRef.setValue(appointment).await()

                // Затем в ветку клиента
                database.getReference("clients/$userId/appointments/${appointment.id}")
                    .setValue(appointment).await()

                _success.value = true

                // Очищаем выбранные значения
                resetState()

            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun calculateTotalPrice(serviceCenterId: String): Int {
        var total = 0
        _selectedServices.value.forEach { serviceId ->
            val snapshot = database.getReference("users/$serviceCenterId/services/$serviceId/price").get().await()
            total += (snapshot.getValue(Int::class.java) ?: 0)
        }
        return total
    }

    fun resetState() {
        _error.value = null
        _success.value = false
        _selectedServices.value = emptySet()
    }
}