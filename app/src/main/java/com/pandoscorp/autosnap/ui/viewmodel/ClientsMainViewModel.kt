package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.pandoscorp.autosnap.model.Appointment
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


    init {
        if (auth.currentUser != null) {
            loadCars()
            loadUpcomingAppointments()
            loadHistory()
        } else {
            Log.e("AuthError", "User not authenticated")
        }
    }

    private fun loadCars() {
        auth.currentUser?.uid?.let { userId ->
            database.getReference("clients/$userId/cars")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val carsList = mutableListOf<Car>()
                        snapshot.children.forEach { carSnapshot ->
                            try {
                                val car = carSnapshot.getValue(Car::class.java)
                                car?.let {
                                    carsList.add(it.copy(id = carSnapshot.key ?: ""))
                                    Log.d("CarDebug", "Loaded car: ${it.brand} ${it.model}")
                                }
                            } catch (e: Exception) {
                                Log.e("CarError", "Error parsing car data", e)
                            }
                        }
                        _cars.value = carsList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("CarError", "Failed to load cars: ${error.message}")
                    }
                })
        }
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