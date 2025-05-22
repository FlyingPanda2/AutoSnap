package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientsMainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)

    private val _upcomingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val upcomingAppointments = _upcomingAppointments.asStateFlow()

    private val _history = MutableStateFlow<List<Appointment>>(emptyList())
    val history = _history.asStateFlow()

    private val _selectedService = MutableStateFlow<User?>(null)
    val selectedService = _selectedService.asStateFlow()

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars = _cars.asStateFlow()

    private fun loadCars() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val snapshot = database.getReference("clients/$userId/cars").get().await()

                val carsList = snapshot.children.mapNotNull { child ->
                    child.getValue(Car::class.java)?.copy(id = child.key ?: "")
                }

                _cars.value = carsList
                Log.d("CarsDebug", "Loaded ${carsList.size} cars")
            } catch (e: Exception) {
                Log.e("CarsError", "Error loading cars", e)
            }
        }
    }

    init {
        loadCars()
        loadUpcomingAppointments()
        loadHistory()
    }

    fun setSelectedService(service: User) {
        _selectedService.value = service
    }

    fun clearSelectedService() {
        _selectedService.value = null
    }

    private fun loadUpcomingAppointments() {
        database.getReference("appointments")
            .orderByChild("clientId")
            .equalTo(auth.currentUser?.uid ?: "")
            .limitToFirst(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointments = snapshot.children.mapNotNull { child ->
                    child.getValue(Appointment::class.java)?.copy(id = child.key ?: "")
                }
                _upcomingAppointments.value = appointments
            }
    }

    private fun loadHistory() {
        database.getReference("appointments")
            .orderByChild("clientId")
            .equalTo(auth.currentUser?.uid ?: "")
            .limitToLast(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointments = snapshot.children.mapNotNull { child ->
                    child.getValue(Appointment::class.java)?.copy(id = child.key ?: "")
                }
                _history.value = appointments
            }
    }
}