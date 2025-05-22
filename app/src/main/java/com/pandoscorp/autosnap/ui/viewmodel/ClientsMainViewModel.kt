package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientsMainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users/${auth.currentUser?.uid}")

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars = _cars.asStateFlow()

    private val _upcomingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val upcomingAppointments = _upcomingAppointments.asStateFlow()

    private val _history = MutableStateFlow<List<Appointment>>(emptyList())
    val history = _history.asStateFlow()

    init {
        loadCars()
        loadUpcomingAppointments()
        loadHistory()
    }

    private fun loadCars() {
        database.child("clients/${auth.currentUser?.uid}/cars")
            .get()
            .addOnSuccessListener { snapshot ->
                val cars = snapshot.children.mapNotNull { child ->
                    child.getValue(Car::class.java)?.copy(id = child.key ?: "")
                }
                _cars.value = cars
            }
    }

    private fun loadUpcomingAppointments() {
        database.child("appointments")
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
        database.child("appointments")
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